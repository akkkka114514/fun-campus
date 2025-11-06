package net.lab1024.sa.admin;

import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.service.ActivityEnrollmentService;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * author:akkkka114514
 * create at 2025-11-06 15:42
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ActivityEnrollmentServiceTest {
    
    @Mock
    private ActivityEnrollmentService activityEnrollmentService;
    
    @Test
    void testQueryPage() {
        // Given
        ActivityEnrollmentQueryForm queryForm = new ActivityEnrollmentQueryForm();
        queryForm.setPageNum(1L);
        queryForm.setPageSize(10L);
        
        PageResult<ActivityEnrollmentVO> mockPageResult = new PageResult<>();
        when(activityEnrollmentService.queryPage(any(ActivityEnrollmentQueryForm.class)))
            .thenReturn(mockPageResult);
        
        // When
        PageResult<ActivityEnrollmentVO> result = activityEnrollmentService.queryPage(queryForm);
        
        // Then
        assertNotNull(result);
        verify(activityEnrollmentService, times(1)).queryPage(queryForm);
    }
    
    @Test
    void testEnroll() {
        // Given
        Long activityId = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok("报名成功");
        when(activityEnrollmentService.enroll(activityId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityEnrollmentService.enroll(activityId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("报名成功", result.getData());
        verify(activityEnrollmentService, times(1)).enroll(activityId);
    }
    
    @Test
    void testCancelEnroll() {
        // Given
        Long activityId = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok("取消报名成功");
        when(activityEnrollmentService.cancelEnroll(activityId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityEnrollmentService.cancelEnroll(activityId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("取消报名成功", result.getData());
        verify(activityEnrollmentService, times(1)).cancelEnroll(activityId);
    }
    
    @Test
    void testSignIn() {
        // Given
        Long activityId = 1L;
        Long userId = 1L;
        ResponseDTO<Void> mockResponse = ResponseDTO.ok();
        when(activityEnrollmentService.signIn(activityId, userId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<Void> result = activityEnrollmentService.signIn(activityId, userId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(activityEnrollmentService, times(1)).signIn(activityId, userId);
    }
    
    @Test
    void testBackendOperateSignIn() {
        // Given
        Long activityId = 1L;
        Long userId = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok("后台操作签到成功");
        when(activityEnrollmentService.backendOperateSignIn(activityId, userId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityEnrollmentService.backendOperateSignIn(activityId, userId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("后台操作签到成功", result.getData());
        verify(activityEnrollmentService, times(1)).backendOperateSignIn(activityId, userId);
    }
    
    @Test
    void testBatchBackendOperateSignIn() {
        // Given
        Map<Long, Set<Long>> activityIdUserIdMap = new HashMap<>();
        Set<Long> userIds = new HashSet<>();
        userIds.add(1L);
        userIds.add(2L);
        activityIdUserIdMap.put(1L, userIds);
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok("批量后台操作签到成功");
        when(activityEnrollmentService.batchBackendOperateSignIn(activityIdUserIdMap))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityEnrollmentService.batchBackendOperateSignIn(activityIdUserIdMap);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("批量后台操作签到成功", result.getData());
        verify(activityEnrollmentService, times(1)).batchBackendOperateSignIn(activityIdUserIdMap);
    }
    
    @Test
    void testPassEnrollReview() {
        // Given
        Long activityId = 1L;
        Long userId = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok("通过审核成功");
        when(activityEnrollmentService.passEnrollReview(activityId, userId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityEnrollmentService.passEnrollReview(activityId, userId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("通过审核成功", result.getData());
        verify(activityEnrollmentService, times(1)).passEnrollReview(activityId, userId);
    }
}