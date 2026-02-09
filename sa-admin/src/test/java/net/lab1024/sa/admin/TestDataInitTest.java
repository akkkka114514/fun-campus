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
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.manager.SchoolInfoManager;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import net.lab1024.sa.admin.module.business.funcampus.tribe.manager.TribeManager;
import net.lab1024.sa.admin.module.system.backendUser.domain.entity.BackendUserEntity;
import net.lab1024.sa.admin.module.system.backendUser.domain.form.BackendUserAddForm;
import net.lab1024.sa.admin.module.system.backendUser.service.BackendUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Resource
    private TribeManager tribeManager;
    @Resource
    private SchoolInfoManager schoolInfoManager;
    @Resource
    private TransactionTemplate transactionTemplate;

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
    void initTribe(){
        List<String> organizationList = Lists.newArrayList("校团委","校学生会","校学生工作委员会","校青协","大学生新媒体中心","校职业发展团队");
        List<String> gradeList=Lists.newArrayList("25级","24级","23级","22级");
        List<String> clubNames = List.of(
                "星辰创想社",
                "墨语文学社",
                "极光摄影协会",
                "知行公益社",
                "π研社",
                "青禾环保社",
                "回声戏剧社",
                "拾光手作工坊",
                "跃界街舞团",
                "思辨者联盟"
        );
        List<String> majorList = List.of(
                "计算机科学与技术",
                "软件工程",
                "人工智能",
                "数据科学与大数据技术",
                "电子信息工程",
                "通信工程",
                "自动化",
                "电气工程及其自动化",
                "机械设计制造及其自动化",
                "车辆工程",
                "土木工程",
                "建筑学",
                "城乡规划",
                "临床医学",
                "口腔医学",
                "药学",
                "护理学",
                "生物医学工程",
                "金融学",
                "会计学",
                "财务管理",
                "工商管理",
                "市场营销",
                "人力资源管理",
                "国际经济与贸易",
                "经济学",
                "法学",
                "知识产权",
                "社会工作",
                "政治学与行政学",
                "汉语言文学",
                "汉语国际教育",
                "英语",
                "日语",
                "新闻传播学",
                "广告学",
                "网络与新媒体",
                "历史学",
                "哲学",
                "数学与应用数学",
                "信息与计算科学",
                "物理学",
                "应用物理学",
                "化学",
                "应用化学",
                "生物科学",
                "生物技术",
                "心理学",
                "应用心理学",
                "教育学",
                "学前教育",
                "小学教育",
                "体育教育",
                "音乐表演",
                "音乐学",
                "舞蹈编导",
                "美术学",
                "视觉传达设计",
                "环境设计",
                "产品设计",
                "动画",
                "戏剧影视文学",
                "广播电视编导",
                "播音与主持艺术",
                "地理科学",
                "人文地理与城乡规划",
                "测绘工程",
                "遥感科学与技术",
                "地质工程",
                "采矿工程",
                "石油工程",
                "纺织工程",
                "轻化工程",
                "食品科学与工程",
                "食品质量与安全",
                "环境工程",
                "环境科学",
                "安全工程",
                "消防工程",
                "物流管理",
                "供应链管理",
                "电子商务",
                "旅游管理",
                "酒店管理",
                "会展经济与管理",
                "统计学",
                "应用统计学",
                "信息安全",
                "网络空间安全",
                "物联网工程",
                "智能科学与技术",
                "机器人工程",
                "智能制造工程",
                "新能源科学与工程",
                "储能科学与工程",
                "微电子科学与工程",
                "光电信息科学与工程",
                "材料科学与工程",
                "高分子材料与工程",
                "化学工程与工艺",
                "制药工程",
                "核工程与核技术",
                "航空航天工程",
                "飞行器设计与工程",
                "海洋工程与技术",
                "农业机械化及其自动化",
                "园艺",
                "植物保护",
                "动物科学",
                "动物医学",
                "林学",
                "园林",
                "中医学",
                "针灸推拿学",
                "中药学"
        );
        List<String> classNames=List.of("1班","2班","3班","4班","5班");
        Random rand = new Random();
        for(int i = 1; i <= 1000; i++) {
            for (String organization : organizationList) {
                Long randomNum = rand.nextLong(1157 - 159 + 1) + 159;
                TribeEntity tribeEntity = new TribeEntity();
                tribeEntity.setName(organization);
                tribeEntity.setCategoryId(8L);
                tribeEntity.setPresidentId(randomNum);
                tribeEntity.setBelongTo(1);
                tribeEntity.setDeletedFlag(false);
                tribeEntity.setCreateTime(LocalDateTime.now());
                tribeEntity.setUpdateTime(LocalDateTime.now());
                tribeEntity.setSchoolId((long) i);
                tribeManager.save(tribeEntity);
            }
            for(String club:clubNames){
                Long randomNum = rand.nextLong(1157 - 159 + 1) + 159;
                TribeEntity tribeEntity = new TribeEntity();
                tribeEntity.setName(club);
                tribeEntity.setCategoryId(5L);
                tribeEntity.setPresidentId(randomNum);
                tribeEntity.setBelongTo(1);
                tribeEntity.setDeletedFlag(false);
                tribeEntity.setCreateTime(LocalDateTime.now());
                tribeEntity.setUpdateTime(LocalDateTime.now());
                tribeEntity.setSchoolId((long) i);
                tribeManager.save(tribeEntity);
            }
            for(String major:majorList){
                Long randomNum = rand.nextLong(1157 - 159 + 1) + 159;
                for(String className:classNames) {
                    for(String grade:gradeList) {
                        TribeEntity tribeEntity = new TribeEntity();
                        tribeEntity.setName(grade+major+className);
                        tribeEntity.setCategoryId(5L);
                        tribeEntity.setPresidentId(randomNum);
                        tribeEntity.setBelongTo(2);
                        tribeEntity.setDeletedFlag(false);
                        tribeEntity.setCreateTime(LocalDateTime.now());
                        tribeEntity.setUpdateTime(LocalDateTime.now());
                        tribeEntity.setSchoolId((long) i);
                        tribeManager.save(tribeEntity);
                    }
                }
            }
        }
    }
    @Test
    void initTribe2() {
        Random rand = new Random();
        List<String> organizationList = Lists.newArrayList("校团委","校学生会","校学生工作委员会","校青协","大学生新媒体中心","校职业发展团队");
        List<String> gradeList=Lists.newArrayList("25级","24级","23级","22级");
        List<String> clubNames = List.of(
                "星辰创想社",
                "墨语文学社",
                "极光摄影协会",
                "知行公益社",
                "π研社",
                "青禾环保社",
                "回声戏剧社",
                "拾光手作工坊",
                "跃界街舞团",
                "思辨者联盟"
        );
        List<String> classNames=List.of("1班","2班","3班","4班","5班");
        List<String> majorList = List.of(
                "计算机科学与技术",
                "软件工程",
                "人工智能",
                "数据科学与大数据技术",
                "电子信息工程",
                "通信工程",
                "自动化",
                "电气工程及其自动化",
                "机械设计制造及其自动化",
                "车辆工程",
                "土木工程",
                "建筑学",
                "城乡规划",
                "临床医学",
                "口腔医学",
                "药学",
                "护理学",
                "生物医学工程",
                "金融学",
                "会计学",
                "财务管理",
                "工商管理",
                "市场营销",
                "人力资源管理",
                "国际经济与贸易",
                "经济学",
                "法学",
                "知识产权",
                "社会工作",
                "政治学与行政学",
                "汉语言文学",
                "汉语国际教育",
                "英语",
                "日语",
                "新闻传播学",
                "广告学",
                "网络与新媒体",
                "历史学",
                "哲学",
                "数学与应用数学",
                "信息与计算科学",
                "物理学",
                "应用物理学",
                "化学",
                "应用化学",
                "生物科学",
                "生物技术",
                "心理学",
                "应用心理学",
                "教育学",
                "学前教育",
                "小学教育",
                "体育教育",
                "音乐表演",
                "音乐学",
                "舞蹈编导",
                "美术学",
                "视觉传达设计",
                "环境设计",
                "产品设计",
                "动画",
                "戏剧影视文学",
                "广播电视编导",
                "播音与主持艺术",
                "地理科学",
                "人文地理与城乡规划",
                "测绘工程",
                "遥感科学与技术",
                "地质工程",
                "采矿工程",
                "石油工程",
                "纺织工程",
                "轻化工程",
                "食品科学与工程",
                "食品质量与安全",
                "环境工程",
                "环境科学",
                "安全工程",
                "消防工程",
                "物流管理",
                "供应链管理",
                "电子商务",
                "旅游管理",
                "酒店管理",
                "会展经济与管理",
                "统计学",
                "应用统计学",
                "信息安全",
                "网络空间安全",
                "物联网工程",
                "智能科学与技术",
                "机器人工程",
                "智能制造工程",
                "新能源科学与工程",
                "储能科学与工程",
                "微电子科学与工程",
                "光电信息科学与工程",
                "材料科学与工程",
                "高分子材料与工程",
                "化学工程与工艺",
                "制药工程",
                "核工程与核技术",
                "航空航天工程",
                "飞行器设计与工程",
                "海洋工程与技术",
                "农业机械化及其自动化",
                "园艺",
                "植物保护",
                "动物科学",
                "动物医学",
                "林学",
                "园林",
                "中医学",
                "针灸推拿学",
                "中药学"
        );
        List<TribeEntity> tribeEntityList = new ArrayList<>();
        transactionTemplate.executeWithoutResult(status -> {
                for (String organization : organizationList) {
                    Long randomNum = rand.nextLong(1157 - 159 + 1) + 159;
                    TribeEntity tribeEntity = new TribeEntity();
                    tribeEntity.setName(organization);
                    tribeEntity.setCategoryId(8L);
                    tribeEntity.setPresidentId(randomNum);
                    tribeEntity.setBelongTo(1);
                    tribeEntity.setDeletedFlag(false);
                    tribeEntity.setCreateTime(LocalDateTime.now());
                    tribeEntity.setUpdateTime(LocalDateTime.now());
                    tribeEntity.setSchoolId((long) 530);
                    tribeManager.save(tribeEntity);
                }
                for(String club:clubNames){
                    Long randomNum = rand.nextLong(1157 - 159 + 1) + 159;
                    TribeEntity tribeEntity = new TribeEntity();
                    tribeEntity.setName(club);
                    tribeEntity.setCategoryId(5L);
                    tribeEntity.setPresidentId(randomNum);
                    tribeEntity.setBelongTo(1);
                    tribeEntity.setDeletedFlag(false);
                    tribeEntity.setCreateTime(LocalDateTime.now());
                    tribeEntity.setUpdateTime(LocalDateTime.now());
                    tribeEntity.setSchoolId((long) 530);
                    tribeManager.save(tribeEntity);
                }


        });
    }
}
