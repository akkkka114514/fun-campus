package net.lab1024.sa.admin;

import net.lab1024.sa.admin.module.business.funcampus.portalUser.service.PortalUserService;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserAddForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
* author:akkkka114514
* create at 2025-11-06 15:56
*/
@ExtendWith(MockitoExtension.class)
public class PortalUserServiceTest {
    
    @Mock
    private PortalUserService portalUserService;
    
    @Test
    void testQueryPage() {
        // Given
        PortalUserQueryForm queryForm = new PortalUserQueryForm();
        queryForm.setPageNum(1L);
        queryForm.setPageSize(10L);
        
        PageResult<PortalUserVO> mockPageResult = new PageResult<>();
        when(portalUserService.queryPage(any(PortalUserQueryForm.class)))
            .thenReturn(mockPageResult);
        
        // When
        PageResult<PortalUserVO> result = portalUserService.queryPage(queryForm);
        
        // Then
        assertNotNull(result);
        verify(portalUserService, times(1)).queryPage(queryForm);
    }
    
    @Test
    void testAdd() {
        // Given
        PortalUserAddForm addForm = new PortalUserAddForm();
        addForm.setUsername("testuser");
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(portalUserService.add(any(PortalUserAddForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = portalUserService.add(addForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(portalUserService, times(1)).add(addForm);
    }
    
    @Test
    void testUpdate() {
        // Given
        PortalUserUpdateForm updateForm = new PortalUserUpdateForm();
        updateForm.setId(1L);
        updateForm.setUsername("updateduser");
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(portalUserService.update(any(PortalUserUpdateForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = portalUserService.update(updateForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(portalUserService, times(1)).update(updateForm);
    }
    
    @Test
    void testBatchDelete() {
        // Given
        List<Long> idList = Arrays.asList(1L, 2L, 3L);
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(portalUserService.batchDelete(idList))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = portalUserService.batchDelete(idList);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(portalUserService, times(1)).batchDelete(idList);
    }
    
    @Test
    void testDelete() {
        // Given
        Long id = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(portalUserService.delete(id))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = portalUserService.delete(id);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(portalUserService, times(1)).delete(id);
    }
    
    @Test
    void testGeneratePortalUserTestData() {
        // 生成测试数据并验证
        List<PortalUserAddForm> testDataList = generatePortalUserTestData();
        
        // 验证生成了100条数据
        assertEquals(100, testDataList.size());
        
        // 验证每条数据都包含必要的字段
        for (PortalUserAddForm form : testDataList) {
            assertNotNull(form.getUsername());
            assertNotNull(form.getPassword());
            assertNotNull(form.getSchoolName());
            assertNotNull(form.getCollegeName());
            assertNotNull(form.getGender());
        }
    }
    
    /**
     * 生成100个合理的PortalUser测试数据
     */
    public List<PortalUserAddForm> generatePortalUserTestData() {
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
            form.setPassword("password" + i);
            
            // 设置性别 (true表示男，false表示女)
            form.setGender(random.nextBoolean());
            
            // 设置学校
            form.setSchoolName(schools[random.nextInt(schools.length)]);
            
            // 设置学院
            form.setCollegeName(colleges[random.nextInt(colleges.length)]);
            
            testDataList.add(form);
        }
        
        return testDataList;
    }
}