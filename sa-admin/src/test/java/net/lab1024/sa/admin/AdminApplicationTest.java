package net.lab1024.sa.admin;

import jakarta.annotation.Resource;
import net.lab1024.sa.base.module.support.minio.utils.MinioUtils;
import net.lab1024.sa.base.module.support.securityprotect.service.SecurityPasswordService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AdminApplicationTest {

    @BeforeEach
    public void before() {
        System.out.println("----------------------- 测试开始 -----------------------");

    }

    @AfterEach
    public void after() {
        System.out.println("----------------------- 测试结束 -----------------------");
    }


    @Test
    public void test() {
        String password = SecurityPasswordService.getEncryptPwd("Qw020829@qazwsx");
        System.out.println(password);
    }
//
//    @Test
//    public void testMinioUpload() throws IOException {
//        File file = new File("D:\\1757383968846.png");
//        FileInputStream fis = new FileInputStream(file);
//        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/octet-stream", fis);
//        if(!minioUtils.existBucket("public")) {
//            minioUtils.makeBucket("public");
//        }
//        minioUtils.upload(multipartFile,"1757383968846.png");
//    }
//
//    @Test
//    public void testMinioDownload() {
//        minioUtils.download("1757383968846.png");
//    }

}