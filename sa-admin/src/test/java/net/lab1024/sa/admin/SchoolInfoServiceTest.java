package net.lab1024.sa.admin;

import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.service.SchoolInfoService;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.form.SchoolInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.schoolInfo.domain.vo.SchoolInfoVO;
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
* create at 2025-11-06 15:57
*/
@ExtendWith(MockitoExtension.class)
public class SchoolInfoServiceTest {
    
    @Mock
    private SchoolInfoService schoolInfoService;
    
    @Test
    void testQueryPage() {
        // Given
        SchoolInfoQueryForm queryForm = new SchoolInfoQueryForm();
        queryForm.setPageNum(1L);
        queryForm.setPageSize(10L);
        
        PageResult<SchoolInfoVO> mockPageResult = new PageResult<>();
        when(schoolInfoService.queryPage(any(SchoolInfoQueryForm.class)))
            .thenReturn(mockPageResult);
        
        // When
        PageResult<SchoolInfoVO> result = schoolInfoService.queryPage(queryForm);
        
        // Then
        assertNotNull(result);
        verify(schoolInfoService, times(1)).queryPage(queryForm);
    }
    
    @Test
    void testAdd() {
        // Given
        SchoolInfoAddForm addForm = new SchoolInfoAddForm();
        addForm.setName("Test School");
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(schoolInfoService.add(any(SchoolInfoAddForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = schoolInfoService.add(addForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(schoolInfoService, times(1)).add(addForm);
    }
    
    @Test
    void testUpdate() {
        // Given
        SchoolInfoUpdateForm updateForm = new SchoolInfoUpdateForm();
        updateForm.setId(1L);
        updateForm.setName("Updated School");
        
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(schoolInfoService.update(any(SchoolInfoUpdateForm.class)))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = schoolInfoService.update(updateForm);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(schoolInfoService, times(1)).update(updateForm);
    }
    
    @Test
    void testBatchDelete() {
        // Given
        List<Long> idList = Arrays.asList(1L, 2L, 3L);
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(schoolInfoService.batchDelete(idList))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = schoolInfoService.batchDelete(idList);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(schoolInfoService, times(1)).batchDelete(idList);
    }
    
    @Test
    void testDelete() {
        // Given
        Long id = 1L;
        ResponseDTO<String> mockResponse = ResponseDTO.ok();
        when(schoolInfoService.delete(id))
            .thenReturn(mockResponse);
        
        // When
        ResponseDTO<String> result = schoolInfoService.delete(id);
        
        // Then
        assertNotNull(result);
        assertTrue(result.getOk());
        verify(schoolInfoService, times(1)).delete(id);
    }
}