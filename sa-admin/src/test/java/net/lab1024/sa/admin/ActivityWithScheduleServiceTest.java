package net.lab1024.sa.admin;

import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.service.ActivityWithScheduleService;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleQueryForm;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.admin.module.business.funcampus.activityWithSchedule.domain.vo.ActivityWithScheduleVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
* author:akkkka114514
* create at 2025-11-06 15:55
*/
@ExtendWith(MockitoExtension.class)
public class ActivityWithScheduleServiceTest {
    
    @Mock
    private ActivityWithScheduleService activityWithScheduleService;
    
    @Test
    void testAddActivityWithSchedule() {
        // Given
        ActivityWithScheduleAddForm addForm = new ActivityWithScheduleAddForm();
        addForm.setTitle("Test Activity");
        addForm.setPosition("Test Position");
        addForm.setScoreCanGet(BigDecimal.valueOf(10));
        addForm.setEnrollNumLimit(100);
        addForm.setActivityOrganizerId(1L);
        addForm.setActivitySchoolId(1L);
        addForm.setEnrollStartTime(LocalDateTime.now());
        addForm.setEnrollEndTime(LocalDateTime.now().plusDays(1));
        addForm.setActivityStartTime(LocalDateTime.now().plusDays(2));
        addForm.setActivityEndTime(LocalDateTime.now().plusDays(3));
        addForm.setSigninStartTime(LocalDateTime.now().plusDays(2));
        addForm.setSigninEndTime(LocalDateTime.now().plusDays(2).plusHours(2));
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok("保存成功");
        when(activityWithScheduleService.addActivityWithSchedule(any(ActivityWithScheduleAddForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityWithScheduleService.addActivityWithSchedule(addForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("保存成功", result.getData());
        verify(activityWithScheduleService, times(1)).addActivityWithSchedule(addForm);
    }
    
    @Test
    void testDeleteActivityWithSchedule() {
        // Given
        Long activityId = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok("删除成功");
        when(activityWithScheduleService.deleteActivityWithSchedule(activityId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityWithScheduleService.deleteActivityWithSchedule(activityId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("删除成功", result.getData());
        verify(activityWithScheduleService, times(1)).deleteActivityWithSchedule(activityId);
    }
    
    @Test
    void testUpdateActivityWithSchedule() {
        // Given
        ActivityWithScheduleUpdateForm updateForm = new ActivityWithScheduleUpdateForm();
        updateForm.setId(1L);
        updateForm.setTitle("Updated Activity");
        updateForm.setPosition("Updated Position");
        updateForm.setScoreCanGet(BigDecimal.valueOf(20));
        updateForm.setEnrollNumLimit(200);
        updateForm.setActivityOrganizerId(1L);
        updateForm.setActivitySchoolId(1L);
        updateForm.setEnrollStartTime(LocalDateTime.now());
        updateForm.setEnrollEndTime(LocalDateTime.now().plusDays(1));
        updateForm.setActivityStartTime(LocalDateTime.now().plusDays(2));
        updateForm.setActivityEndTime(LocalDateTime.now().plusDays(3));
        updateForm.setSigninStartTime(LocalDateTime.now().plusDays(2));
        updateForm.setSigninEndTime(LocalDateTime.now().plusDays(2).plusHours(2));
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok("更新成功");
        when(activityWithScheduleService.updateActivityWithSchedule(any(ActivityWithScheduleUpdateForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityWithScheduleService.updateActivityWithSchedule(updateForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("更新成功", result.getData());
        verify(activityWithScheduleService, times(1)).updateActivityWithSchedule(updateForm);
    }
    
    @Test
    void testQueryActivityWithSchedule() {
        // Given
        ActivityWithScheduleQueryForm queryForm = new ActivityWithScheduleQueryForm();
        queryForm.setPageNum(1L);
        queryForm.setPageSize(10L);
        
        PageResult<ActivityWithScheduleVO> mockPageResult = new PageResult<>();
        ResponseDTO<PageResult<ActivityWithScheduleVO>> mockResponse = ResponseDTO.ok(mockPageResult);
        when(activityWithScheduleService.queryActivityWithSchedule(any(ActivityWithScheduleQueryForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<PageResult<ActivityWithScheduleVO>> result = activityWithScheduleService.queryActivityWithSchedule(queryForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertNotNull(result.getData());
        verify(activityWithScheduleService, times(1)).queryActivityWithSchedule(queryForm);
    }
    
    @Test
    void testBatchDelete() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(activityWithScheduleService.batchDelete(ids))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityWithScheduleService.batchDelete(ids);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(activityWithScheduleService, times(1)).batchDelete(ids);
    }
    
    @Test
    void testPublish() {
        // Given
        Long activityId = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok("功能未实现");
        when(activityWithScheduleService.publish(activityId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityWithScheduleService.publish(activityId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("功能未实现", result.getData());
        verify(activityWithScheduleService, times(1)).publish(activityId);
    }
    
    @Test
    void testCancelPublish() {
        // Given
        Long activityId = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok("功能未实现");
        when(activityWithScheduleService.cancelPublish(activityId))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = activityWithScheduleService.cancelPublish(activityId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        assertEquals("功能未实现", result.getData());
        verify(activityWithScheduleService, times(1)).cancelPublish(activityId);
    }
}