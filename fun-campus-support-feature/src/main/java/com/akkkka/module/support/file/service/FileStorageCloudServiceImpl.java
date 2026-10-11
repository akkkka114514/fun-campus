package com.akkkka.module.support.file.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.akkkka.common.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.code.UserErrorCode;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.util.SmartStringUtil;
import com.akkkka.config.FileConfig;
import com.akkkka.constant.RedisKeyConst;
import com.akkkka.module.support.file.constant.FileFolderTypeEnum;
import com.akkkka.module.support.file.dao.FileDao;
import com.akkkka.module.support.file.domain.vo.FileDownloadVO;
import com.akkkka.module.support.file.domain.vo.FileMetadataVO;
import com.akkkka.module.support.file.domain.vo.FileUploadVO;
import com.akkkka.module.support.file.domain.vo.FileVO;
import com.akkkka.module.support.redis.RedisService;
import com.akkkka.module.support.securityprotect.service.Level3ProtectConfigService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 云计算 实现
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
public class FileStorageCloudServiceImpl implements IFileStorageService {

    /**
     * 自定义元数据 文件名称
     */
    private static final String USER_METADATA_FILE_NAME = "file-name";

    /**
     * 自定义元数据 文件格式
     */
    private static final String USER_METADATA_FILE_FORMAT = "file-format";

    /**
     * 自定义元数据 文件大小
     */
    private static final String USER_METADATA_FILE_SIZE = "file-size";

    /**
     * 预签名上传 URL 有效期
     */
    private static final Duration PRESIGN_URL_EXPIRE = Duration.ofHours(1);

    /**
     * 预签名直传允许的文件扩展名：与 SecurityFileService 的 MIME 白名单同口径
     * （中转上传由 Tika 嗅探内容防伪装；直传无法嗅探内容，先按扩展名做第一道拦截）
     */
    private static final Set<String> PRESIGN_ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "csv", "txt", "json", "zip", "7z",
            "mp3", "wav", "mp4", "mov", "avi");

    @Resource
    private S3Client s3Client;

    @Resource
    private S3Presigner s3Presigner;

    @Resource
    private FileConfig cloudConfig;

    @Resource
    private RedisService redisService;

    @Resource
    private FileDao fileDao;

    @Resource
    private Level3ProtectConfigService level3ProtectConfigService;

    @Override
    public ResponseDTO<FileUploadVO> upload(MultipartFile file, String path) {
        // 设置文件 key
        String originalFileName = file.getOriginalFilename();
        if (SmartStringUtil.isEmpty(originalFileName)) {
            return ResponseDTO.userErrorParam("上传文件名为空");
        }

        String fileType = FilenameUtils.getExtension(originalFileName);
        String uuid = IdUtil.fastSimpleUUID();
        String time = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_FORMATTER);
        String fileKey = path + uuid + "_" + time + "." + fileType;

        // 文件名称 URL 编码
        String urlEncoderFilename;
        urlEncoderFilename = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);
        Map<String, String> userMetadata = new HashMap<>(10);
        userMetadata.put(USER_METADATA_FILE_NAME, urlEncoderFilename);
        userMetadata.put(USER_METADATA_FILE_FORMAT, fileType);
        userMetadata.put(USER_METADATA_FILE_SIZE, String.valueOf(file.getSize()));

        // 根据文件路径获取并设置访问权限
        ObjectCannedACL acl = this.getACL(path);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(cloudConfig.getBucketName())
                .key(fileKey)
                .metadata(userMetadata)
                .contentLength(file.getSize())
                .contentType(this.getContentType(fileType))
                .contentEncoding(StandardCharsets.UTF_8.name())
                .contentDisposition("attachment;filename=" + urlEncoderFilename)
                .acl(acl)
                .build();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            log.error("文件上传-发生异常：", e);
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        // 返回上传结果
        FileUploadVO uploadVO = new FileUploadVO();
        uploadVO.setFileName(originalFileName);
        uploadVO.setFileType(fileType);
        // 根据 访问权限 返回不同的 URL
        String url = cloudConfig.getUrlPrefix() + fileKey;
        if (ObjectCannedACL.PRIVATE.equals(acl)) {
            // 获取临时访问的URL
            url = this.getFileUrl(fileKey).getData();
        }
        uploadVO.setFileUrl(url);
        uploadVO.setFileKey(fileKey);
        uploadVO.setFileSize(file.getSize());
        return ResponseDTO.ok(uploadVO);
    }

    /**
     * 获取文件url
     *
     * @param fileKey 文件key
     * @return url
     */
    @Override
    public ResponseDTO<String> getFileUrl(String fileKey) {
        if (StringUtils.isBlank(fileKey)) {
            return ResponseDTO.userErrorParam("文件不存在，key为空");
        }

        if (!fileKey.startsWith(FileFolderTypeEnum.FOLDER_PRIVATE)) {
            // 不是私有的 都公共读
            return ResponseDTO.ok(cloudConfig.getUrlPrefix() + fileKey);
        }

        // 如果是私有的，则规定时间内可以访问，超过规定时间，则连接失效

        String fileRedisKey = RedisKeyConst.Support.FILE_PRIVATE_VO + fileKey;
        FileVO fileVO = redisService.getObject(fileRedisKey, FileVO.class);
        if (fileVO == null) {
            fileVO = fileDao.getByFileKey(fileKey);
            if (fileVO == null) {
                return ResponseDTO.userErrorParam("文件不存在");
            }
            GetObjectRequest getUrlRequest = GetObjectRequest.builder().bucket(cloudConfig.getBucketName()).key(fileKey).build();
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofSeconds(cloudConfig.getPrivateUrlExpireSeconds())).getObjectRequest(getUrlRequest).build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            String url = presignedGetObjectRequest.url().toString();
            fileVO.setFileUrl(url);
            redisService.set(fileRedisKey, fileVO, cloudConfig.getPrivateUrlExpireSeconds() - 5);
        }

        return ResponseDTO.ok(fileVO.getFileUrl());
    }


    /**
     * 流式下载（名称为原文件）
     */
    @Override
    public ResponseDTO<FileDownloadVO> download(String key) {

        // 获取文件 meta
        HeadObjectRequest objectRequest = HeadObjectRequest.builder().bucket(this.cloudConfig.getBucketName()).key(key).build();
        HeadObjectResponse headObjectResponse = s3Client.headObject(objectRequest);
        Map<String, String> userMetadata = headObjectResponse.metadata();
        FileMetadataVO metadataDTO = null;
        if (MapUtils.isNotEmpty(userMetadata)) {
            metadataDTO = new FileMetadataVO();
            metadataDTO.setFileFormat(userMetadata.get(USER_METADATA_FILE_FORMAT));
            metadataDTO.setFileName(userMetadata.get(USER_METADATA_FILE_NAME));
            String fileSizeStr = userMetadata.get(USER_METADATA_FILE_SIZE);
            Long fileSize = StringUtils.isBlank(fileSizeStr) ? null : Long.valueOf(fileSizeStr);
            metadataDTO.setFileSize(fileSize);
        }
        if (metadataDTO == null) {
            // 直传文件没有自定义元数据：用对象 key 与 head 信息兜底，避免下载时空指针
            metadataDTO = new FileMetadataVO();
            metadataDTO.setFileName(StringUtils.substringAfterLast(key, "/"));
            metadataDTO.setFileFormat(FilenameUtils.getExtension(key));
            metadataDTO.setFileSize(headObjectResponse.contentLength());
        }

        // 流式拉取（延迟读取，不再整体载入内存），返回的流由调用方负责关闭
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(cloudConfig.getBucketName()).key(key).build();
        ResponseInputStream<GetObjectResponse> objectStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());

        FileDownloadVO fileDownloadVO = new FileDownloadVO();
        fileDownloadVO.setData(objectStream);
        fileDownloadVO.setMetadata(metadataDTO);
        return ResponseDTO.ok(fileDownloadVO);
    }

    /**
     * 根据文件夹路径 返回对应的访问权限
     *
     * @param fileKey 文件key
     * @return 权限
     */
    private ObjectCannedACL getACL(String fileKey) {
        // 公用读
        if (fileKey.contains(FileFolderTypeEnum.FOLDER_PUBLIC)) {
            return ObjectCannedACL.PUBLIC_READ;
        }
        // 其他默认私有读写
        return ObjectCannedACL.PRIVATE;
    }

    /**
     * 单个删除文件
     * 根据 file key 删除文件
     * ps：不能删除fileKey不为空的文件夹
     *
     * @param fileKey 文件or文件夹
     */
    @Override
    public ResponseDTO<String> delete(String fileKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(cloudConfig.getBucketName()).key(fileKey).build();
        s3Client.deleteObject(deleteObjectRequest);
        return ResponseDTO.ok();
    }

    /**
     * 生成预签名上传 URL
     * ps：上传目标桶固定为配置主桶，不由调用方指定，避免越权写入其他桶
     */
    public ResponseDTO<Map<String, Object>> generatePresignedUploadUrl(String originalFileName, String path) {
        try {
            // 构建文件 key（扩展名先做白名单拦截：直传无法嗅探文件内容，防止伪装扩展名上传可执行脚本等）
            String fileType = FilenameUtils.getExtension(originalFileName);
            if (SmartStringUtil.isEmpty(fileType) || !PRESIGN_ALLOWED_EXTENSIONS.contains(fileType.toLowerCase())) {
                return ResponseDTO.userErrorParam("不支持的文件类型：" + fileType);
            }
            fileType = fileType.toLowerCase();
            String uuid = IdUtil.fastSimpleUUID();
            String time = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_FORMATTER);
            String fileKey = path + uuid + "_" + time + "." + fileType;

            // 根据路径确定 ACL 权限
            ObjectCannedACL acl = this.getACL(path);

            // 设置上传策略
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(cloudConfig.getBucketName())
                    .key(fileKey)
                    .acl(acl)
                    .build();

            // 生成预签名上传 URL
            PutObjectPresignRequest presignRequest =
                    PutObjectPresignRequest.builder()
                            .signatureDuration(PRESIGN_URL_EXPIRE)
                            .putObjectRequest(putObjectRequest)
                            .build();

            PresignedPutObjectRequest presignedRequest =
                    s3Presigner.presignPutObject(presignRequest);
            log.debug("上传minio预签名request header：{}",
                    presignedRequest.httpRequest().headers().toString());

            // 准备返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("url", presignedRequest.url().toString());
            result.put("fileKey", fileKey);
            result.put("fileName", originalFileName);
            result.put("expiresAt", presignedRequest.expiration());
            result.put("acl", acl.toString());

            return ResponseDTO.ok(result);
        } catch (Exception e) {
            log.error("生成预签名上传URL失败", e);
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "生成预签名上传URL失败: " + e.getMessage());
        }
    }

    /**
     * 校验 fileKey 在存储侧真实存在且大小未超限
     * ps：固定校验配置主桶；大小上限与中转上传的 SecurityFileService 同口径
     */
    public void validateFileKey(String fileKey) {
        HeadObjectResponse response;
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(cloudConfig.getBucketName())
                    .key(fileKey)
                    .build();
            response = s3Client.headObject(request);
            // 如果能成功返回，说明对象存在
        } catch (NoSuchKeyException e) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "根据file key，文件不存在");
        } catch (Exception e) {
            // 其他错误：网络、权限、bucket 不存在等
            throw new RuntimeException("Error validating file key: " + e.getMessage(), e);
        }

        long maxSizeMb = level3ProtectConfigService.getMaxUploadFileSizeMb();
        if (maxSizeMb > 0 && response.contentLength() != null && response.contentLength() > maxSizeMb * 1024 * 1024) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "文件大小超过限制：" + maxSizeMb + " mb");
        }
    }
}
