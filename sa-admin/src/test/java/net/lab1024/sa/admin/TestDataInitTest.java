package net.lab1024.sa.admin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;

import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.domain.entity.CollegeInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.entity.OrganizationInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.manager.OrganizationInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.service.OrganizationInfoService;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.domain.form.BackendUserAddForm;
import net.lab1024.sa.admin.module.system.backendUser.service.BackendUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * author:akkkka114514
 * create at 2026-01-17 19:49
 */
@SpringBootTest
public class TestDataInitTest {
    @Resource
    private BackendUserService backendUserService;
    @Resource
    private CollegeInfoManager collegeInfoManager;
    @Resource
    private OrganizationInfoManager organizationInfoManager;

    @Test
    void addTestBackendUser() {
        Random random = new Random();
        for(int i=0;i<5000;i++) {
            int isMale = random.nextInt(0, 2);
            boolean isCollege = random.nextBoolean();
            BackendUserAddForm backendUserAddForm = new BackendUserAddForm();
            backendUserAddForm.setUsername("test"+i);
            backendUserAddForm.setGender(isMale);
            backendUserAddForm.setDisabledFlag(false);
            backendUserAddForm.setEmail("");
            backendUserAddForm.setRoleIdList(List.of(1L));

            if (isCollege) {
                QueryWrapper<CollegeInfoEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderBy(true, true, "RAND()").last("LIMIT 1");
                CollegeInfoEntity collegeInfoEntity = collegeInfoManager.list(queryWrapper).get(0);
                backendUserAddForm.setCollegeId(collegeInfoEntity.getId());
                backendUserAddForm.setSchoolId(collegeInfoEntity.getSchoolId());
            } else {
                QueryWrapper<OrganizationInfoEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderBy(true, true, "RAND()").last("LIMIT 1");
                OrganizationInfoEntity organizationInfoEntity = organizationInfoManager.list(queryWrapper).get(0);
                backendUserAddForm.setOrganizationId(organizationInfoEntity.getId());
                backendUserAddForm.setSchoolId(organizationInfoEntity.getSchoolId());
            }
            backendUserAddForm.setCanReview(true);

            backendUserService.addBackendUser(backendUserAddForm);

        }
    }

    @Test
    void addTestOrganizationInfo() {
        List<String> nameList = Lists.newArrayList("校团委","校学生会","校学生工作委员会","校青协","大学生新媒体中心","校职业发展团队");
        for(int i=1;i<=1000;i++){
            for (String s : nameList) {
                OrganizationInfoEntity organizationInfoEntity = new OrganizationInfoEntity();
                organizationInfoEntity.setSchoolId((long)i);
                organizationInfoEntity.setDeletedFlag(false);
                organizationInfoEntity.setCreateTime(LocalDateTime.now());
                organizationInfoEntity.setUpdateTime(LocalDateTime.now());
                organizationInfoEntity.setName(s);
                organizationInfoManager.save(organizationInfoEntity);
            }
        }
    }

    @Test
    void initOrganizationReviewer() {

    }
}
