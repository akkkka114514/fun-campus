package net.lab1024.sa.admin;

import net.lab1024.sa.admin.module.business.funcampus.portalUser.service.PortalUserService;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserAddForm;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.securityprotect.service.SecurityPasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PortalUserService 集成测试
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PortalUserServiceIntegrationTest {

    @Resource
    private PortalUserService portalUserService;

    @Test
    void testGeneratePortalUserTestData() {
        List<PortalUserAddForm> testDataList = generatePortalUserTestData();

        PortalUserAddForm addForm = new PortalUserAddForm();
        addForm.setGender(true);
        addForm.setSchoolName("清华大学");
        addForm.setCollegeName("人工智能与软件学院");
        addForm.setDeletedFlag(false);
        addForm.setDisableFlag(false);

        addForm.setUsername("akkkka114514");
        addForm.setPassword(SecurityPasswordService.getEncryptPwd("Qw020829@qazwsx"));

        portalUserService.add(addForm);
        for (PortalUserAddForm form : testDataList) {
            ResponseDTO<String> result = portalUserService.add(form);
            assertTrue(result.getOk(), "Failed to add user: " + form.getUsername());
        }
    }

    /**
     * 生成100个合理的PortalUser测试数据
     */
    private List<PortalUserAddForm> generatePortalUserTestData() {
        List<PortalUserAddForm> testDataList = new ArrayList<>();
        Random random = new Random();

        String[] schools = {
            "清华大学", "北京大学", "复旦大学", "上海交通大学", "浙江大学",
            "中国科学技术大学", "南京大学", "华中科技大学", "中山大学", "西安交通大学"
        };

        String[] colleges = {
            "计算机学院", "软件学院", "信息学院", "电子工程学院", "机械工程学院",
            "经济管理学院", "外国语学院", "法学院", "医学院", "艺术学院"
        };

        String[] usernames = {
            "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
            "郑一", "王二", "冯三", "陈四", "褚五", "卫六", "蒋七", "沈八"
        };

        for (int i = 1; i <= 100; i++) {
            PortalUserAddForm form = new PortalUserAddForm();


            // 设置用户名
            String username = usernames[random.nextInt(usernames.length)] + i;
            form.setUsername(username);

            // 设置密码
            form.setPassword(SecurityPasswordService.getEncryptPwd(new SecurityPasswordService().randomPassword()));

            // 设置性别 (true表示男，false表示女)
            form.setGender(random.nextBoolean());

            // 设置学校
            form.setSchoolName(schools[random.nextInt(schools.length)]);

            // 设置学院
            form.setCollegeName(colleges[random.nextInt(colleges.length)]);
            form.setDeletedFlag(false);
            form.setDisableFlag(false);
            testDataList.add(form);
        }

        return testDataList;
    }
}