package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.dao.PortalOrganizerUserDao;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.entity.PortalOrganizerUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserAddForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.vo.PortalOrganizerUserVO;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.manager.PortalOrganizerUserManager;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.file.constant.FileFolderTypeEnum;
import net.lab1024.sa.base.module.support.file.dao.FileDao;
import net.lab1024.sa.base.module.support.file.domain.entity.FileEntity;
import net.lab1024.sa.base.module.support.file.domain.vo.FileUploadVO;
import net.lab1024.sa.base.module.support.file.service.FileService;
import net.lab1024.sa.base.module.support.file.service.FileStorageCloudServiceImpl;
import net.lab1024.sa.base.module.support.file.service.IFileStorageService;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 组织账号运营者 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@Service
public class PortalOrganizerUserService {

    @Resource
    private PortalOrganizerUserDao portalOrganizerUserDao;
    @Resource
    private SchoolInfoManager schoolInfoManager;
    @Resource
    private FileService fileService;
    @Resource
    private PortalOrganizerUserManager portalOrganizerUserManager;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IFileStorageService fileStorageService;
    @Resource
    private FileDao fileDao;

    /**
     * 分页查询
     */
    public PageResult<PortalOrganizerUserVO> queryPage(PortalOrganizerUserQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PortalOrganizerUserVO> list = portalOrganizerUserDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<FileUploadVO> add(PortalOrganizerUserAddForm addForm) {
        //查看学校id是否错误
        if (schoolInfoManager.getById(addForm.getSchoolId()) == null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        //查看用户是否已存在
        LambdaQueryWrapper<PortalOrganizerUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PortalOrganizerUserEntity::getUsername, addForm.getUsername());
        if(portalOrganizerUserManager.getOne(queryWrapper)!=null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR,"用户名已存在");
        }
        PortalOrganizerUserEntity portalOrganizerUserEntity = SmartBeanUtil.copy(addForm, PortalOrganizerUserEntity.class);
        ResponseDTO<FileUploadVO> result = transactionTemplate.execute(transactionStatus -> {
            ResponseDTO<FileUploadVO> avatarUploadResponse = null;
            if(!addForm.getAvatar().isEmpty()&&addForm.getAvatar()!=null){
                //上传头像
                RequestUser requestUser = SmartRequestUtil.getRequestUser();
                avatarUploadResponse = fileService.fileUpload(
                        addForm.getAvatar(),
                        FileFolderTypeEnum.AVATAR.getValue(),
                        requestUser);
            }
            //插入数据
            if(portalOrganizerUserDao.insert(portalOrganizerUserEntity)==0){
                transactionStatus.setRollbackOnly();
            }
            if(avatarUploadResponse==null){
                transactionStatus.setRollbackOnly();
            }
            return avatarUploadResponse;
        });
        assert result != null;
        return ResponseDTO.ok(result.getData());
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<FileUploadVO> update(PortalOrganizerUserUpdateForm updateForm) {
        //查看学校id是否错误
        if(updateForm.getSchoolId()!=null){
            if (schoolInfoManager.getById(updateForm.getSchoolId()) == null){
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
            }
        }
        PortalOrganizerUserEntity portalOrganizerUserEntity = SmartBeanUtil.copy(updateForm, PortalOrganizerUserEntity.class);
        //查询用户是否存在
        if(portalOrganizerUserManager.getById(updateForm.getId()) == null){
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        ResponseDTO<FileUploadVO> result = null;
        if(updateForm.getAvatar()!=null) {
            //根据avatar file id查询数据库file记录
            FileEntity fileEntity = fileDao.selectById(portalOrganizerUserEntity.getAvatarFileId());

            result = transactionTemplate.execute(transactionStatus -> {
                //如果minio中和数据库中的记录存在，删除数据库file记录和minio文件
                if (portalOrganizerUserEntity.getAvatarFileId() != null) {
                    if (fileStorageService.delete(fileEntity.getFileKey()) == null
                            || fileDao.deleteById(fileEntity.getFileId()) == 0) {
                        transactionStatus.setRollbackOnly();
                    }
                }
                //上传新的头像
                ResponseDTO<FileUploadVO> avatarUploadResponse =
                        fileService.fileUpload(
                                updateForm.getAvatar(),
                                FileFolderTypeEnum.AVATAR.getValue(),
                                SmartRequestUtil.getRequestUser());

                if (avatarUploadResponse == null) {
                    transactionStatus.setRollbackOnly();
                }
                assert avatarUploadResponse != null;
                portalOrganizerUserEntity.setAvatarFileId(avatarUploadResponse.getData().getFileId());
                //插入portalOrganizerUserEntity
                if (portalOrganizerUserDao.updateById(portalOrganizerUserEntity) == 0) {
                    transactionStatus.setRollbackOnly();
                }
                return avatarUploadResponse;
            });
        }
        assert result != null;
        return ResponseDTO.ok(result.getData());
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        portalOrganizerUserDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        portalOrganizerUserDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    public ResponseDTO<String> batchDisable(List<Long> idList){
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        portalOrganizerUserDao.batchUpdateDisableFlag(idList, true);
        return ResponseDTO.ok();
    }



}
