package com.ecommerce.service;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.constant.ErrorCode;
import com.ecommerce.model.dto.ProductCreateDTO;
import com.ecommerce.model.dto.ProductQueryDTO;
import com.ecommerce.model.dto.ProductUpdateDTO;
import com.ecommerce.model.entity.Product;
import com.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 商品服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductCreateDTO createDTO;
    private ProductUpdateDTO updateDTO;
    private ProductQueryDTO queryDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setDescription("这是一个测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStock(100);
        testProduct.setCategoryId(1L);
        testProduct.setImageUrl("http://example.com/image.jpg");
        testProduct.setStatus(Product.ProductStatus.ACTIVE.getValue());
        testProduct.setCreateTime(LocalDateTime.now());
        testProduct.setUpdateTime(LocalDateTime.now());

        createDTO = new ProductCreateDTO();
        createDTO.setName("新商品");
        createDTO.setDescription("新商品描述");
        createDTO.setPrice(new BigDecimal("199.99"));
        createDTO.setStock(50);
        createDTO.setCategoryId(2L);
        createDTO.setImageUrl("http://example.com/new-image.jpg");

        updateDTO = new ProductUpdateDTO();
        updateDTO.setName("更新商品");
        updateDTO.setDescription("更新商品描述");
        updateDTO.setPrice(new BigDecimal("299.99"));
        updateDTO.setStock(75);
        updateDTO.setCategoryId(3L);
        updateDTO.setImageUrl("http://example.com/updated-image.jpg");

        queryDTO = new ProductQueryDTO();
        queryDTO.setName("测试");
        queryDTO.setCategoryId(1L);
        queryDTO.setStatus(Product.ProductStatus.ACTIVE.getValue());
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);
    }

    @Test
    void testCreateProduct_Success() {
        // Given
        when(productMapper.insert(any(Product.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> productService.createProduct(createDTO));

        // Then
        verify(productMapper, times(1)).insert(any(Product.class));
    }

    @Test
    void testCreateProduct_NullDTO() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.createProduct(null));
        assertEquals(ErrorCode.PARAM_ERROR, exception.getErrorCode());
    }

    @Test
    void testGetProductDetail_Success() {
        // Given
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        // When
        Product result = productService.getProductDetail(1L);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetProductDetail_NotFound() {
        // Given
        when(productMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.getProductDetail(999L));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testGetProductDetail_InvalidId() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.getProductDetail(null));
        assertEquals(ErrorCode.PARAM_ERROR, exception.getErrorCode());

        exception = assertThrows(BusinessException.class, 
            () -> productService.getProductDetail(0L));
        assertEquals(ErrorCode.PARAM_ERROR, exception.getErrorCode());

        exception = assertThrows(BusinessException.class, 
            () -> productService.getProductDetail(-1L));
        assertEquals(ErrorCode.PARAM_ERROR, exception.getErrorCode());
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> productService.updateProduct(1L, updateDTO));

        // Then
        verify(productMapper, times(1)).selectById(1L);
        verify(productMapper, times(1)).updateById(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Given
        when(productMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.updateProduct(999L, updateDTO));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.deleteById(1L)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        // Then
        verify(productMapper, times(1)).selectById(1L);
        verify(productMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Given
        when(productMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.deleteProduct(999L));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testToggleProductStatus_Success() {
        // Given
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> productService.toggleProductStatus(1L));

        // Then
        verify(productMapper, times(1)).selectById(1L);
        verify(productMapper, times(1)).updateById(any(Product.class));
    }

    @Test
    void testToggleProductStatus_NotFound() {
        // Given
        when(productMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.toggleProductStatus(999L));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testSearchProducts_Success() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productMapper.selectByCondition(any(ProductQueryDTO.class))).thenReturn(products);

        // When
        PageResult<Product> result = productService.searchProducts(queryDTO);

        // Then
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertEquals(1, result.getRecords().size());
        verify(productMapper, times(1)).selectByCondition(any(ProductQueryDTO.class));
    }

    @Test
    void testGetLowStockProducts_Success() {
        // Given
        List<Product> lowStockProducts = Arrays.asList(testProduct);
        when(productMapper.selectLowStockProducts(anyInt())).thenReturn(lowStockProducts);

        // When
        List<Product> result = productService.getLowStockProducts(10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productMapper, times(1)).selectLowStockProducts(10);
    }

    @Test
    void testGetLowStockProducts_InvalidThreshold() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> productService.getLowStockProducts(-1));
        assertEquals(ErrorCode.PARAM_ERROR, exception.getErrorCode());
    }

    @Test
    void testProductStatusEnum() {
        // Test ProductStatus enum
        assertEquals(1, Product.ProductStatus.ACTIVE.getValue());
        assertEquals(0, Product.ProductStatus.INACTIVE.getValue());
        
        assertEquals(Product.ProductStatus.ACTIVE, Product.ProductStatus.fromValue(1));
        assertEquals(Product.ProductStatus.INACTIVE, Product.ProductStatus.fromValue(0));
        
        assertThrows(IllegalArgumentException.class, 
            () -> Product.ProductStatus.fromValue(999));
    }

    @Test
    void testProductToggleStatus() {
        // Test toggle from ACTIVE to INACTIVE
        testProduct.setStatus(Product.ProductStatus.ACTIVE.getValue());
        testProduct.toggleStatus();
        assertEquals(Product.ProductStatus.INACTIVE.getValue(), testProduct.getStatus());
        
        // Test toggle from INACTIVE to ACTIVE
        testProduct.toggleStatus();
        assertEquals(Product.ProductStatus.ACTIVE.getValue(), testProduct.getStatus());
    }

    @Test
    void testProductIsActive() {
        testProduct.setStatus(Product.ProductStatus.ACTIVE.getValue());
        assertTrue(testProduct.isActive());
        
        testProduct.setStatus(Product.ProductStatus.INACTIVE.getValue());
        assertFalse(testProduct.isActive());
    }

    @Test
    void testProductIsLowStock() {
        testProduct.setStock(5);
        assertTrue(testProduct.isLowStock(10));
        
        testProduct.setStock(15);
        assertFalse(testProduct.isLowStock(10));
        
        testProduct.setStock(10);
        assertFalse(testProduct.isLowStock(10)); // 等于阈值不算低库存
    }
}