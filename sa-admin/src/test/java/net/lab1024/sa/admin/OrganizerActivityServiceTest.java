package net.lab1024.sa.admin;

import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.service.OrganizerActivityService;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.form.OrganizerActivityQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizerActivity.domain.vo.OrganizerActivityVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
* author:akkkka114514
* create at 2025-11-06 15:56
*/
@ExtendWith(MockitoExtension.class)
public class OrganizerActivityServiceTest {
    
    @Mock
    private OrganizerActivityService organizerActivityService;
    
    @Test
    void testQueryPage() {
        // Given
        OrganizerActivityQueryForm queryForm = new OrganizerActivityQueryForm();
        queryForm.setPageNum(1L);
        queryForm.setPageSize(10L);
        
        PageResult<OrganizerActivityVO> mockPageResult = new PageResult<>();
        when(organizerActivityService.queryPage(any(OrganizerActivityQueryForm.class)))
            .thenReturn(mockPageResult);
        
        // When
        PageResult<OrganizerActivityVO> result = organizerActivityService.queryPage(queryForm);
        
        // Then
        assertNotNull(result);
        verify(organizerActivityService, times(1)).queryPage(queryForm);
    }
    
    @Test
    void testAdd() {
        // Given
        OrganizerActivityAddForm addForm = new OrganizerActivityAddForm();
        addForm.setOrganizerId(1L);
        addForm.setActivityId(1L);
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(organizerActivityService.add(any(OrganizerActivityAddForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = organizerActivityService.add(addForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(organizerActivityService, times(1)).add(addForm);
    }
    
    @Test
    void testUpdate() {
        // Given
        OrganizerActivityUpdateForm updateForm = new OrganizerActivityUpdateForm();
        updateForm.setId(1L);
        updateForm.setOrganizerId(1L);
        updateForm.setActivityId(1L);
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(organizerActivityService.update(any(OrganizerActivityUpdateForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = organizerActivityService.update(updateForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(organizerActivityService, times(1)).update(updateForm);
    }
    
    @Test
    void testBatchDelete() {
        // Given
        List<Long> idList = Arrays.asList(1L, 2L, 3L);
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(organizerActivityService.batchDelete(idList))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = organizerActivityService.batchDelete(idList);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(organizerActivityService, times(1)).batchDelete(idList);
    }
    
    @Test
    void testDelete() {
        // Given
        Long id = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(organizerActivityService.delete(id))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = organizerActivityService.delete(id);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(organizerActivityService, times(1)).delete(id);
    }
}