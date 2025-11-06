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
}