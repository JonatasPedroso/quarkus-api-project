package com.rethink.api.service;

import com.rethink.api.entity.Product;
import com.rethink.api.repository.ProductRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProductServiceTest {
    
    @Inject
    ProductService productService;
    
    @InjectMock
    ProductRepository productRepository;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", new BigDecimal("100.00"), 10);
        testProduct.id = 1L;
    }
    
    @Test
    void testListAll() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.listAll()).thenReturn(products);
        
        List<Product> result = productService.listAll();
        
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name);
        verify(productRepository, times(1)).listAll();
    }
    
    @Test
    void testFindById() {
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProduct));
        
        Product result = productService.findById(1L);
        
        assertNotNull(result);
        assertEquals("Test Product", result.name);
        verify(productRepository, times(1)).findByIdOptional(1L);
    }
    
    @Test
    void testFindByIdNotFound() {
        when(productRepository.findByIdOptional(999L)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            productService.findById(999L);
        });
    }
    
    @Test
    void testSearchByName() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContaining("Test")).thenReturn(products);
        
        List<Product> result = productService.searchByName("Test");
        
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name);
        verify(productRepository, times(1)).findByNameContaining("Test");
    }
    
    @Test
    void testCreate() {
        Product newProduct = new Product("New Product", "Description", new BigDecimal("200.00"), 5);
        doNothing().when(productRepository).persist(any(Product.class));
        
        Product result = productService.create(newProduct);
        
        assertEquals("New Product", result.name);
        verify(productRepository, times(1)).persist(newProduct);
    }
    
    @Test
    void testUpdate() {
        Product updateProduct = new Product("Updated Product", "Updated Description", 
                new BigDecimal("150.00"), 15);
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProduct));
        
        Product result = productService.update(1L, updateProduct);
        
        assertEquals("Updated Product", result.name);
        assertEquals("Updated Description", result.description);
        assertEquals(new BigDecimal("150.00"), result.price);
        assertEquals(15, result.quantity);
    }
    
    @Test
    void testDelete() {
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(any(Product.class));
        
        productService.delete(1L);
        
        verify(productRepository, times(1)).delete(testProduct);
    }
    
    @Test
    void testCountProducts() {
        when(productRepository.count()).thenReturn(5L);
        
        long count = productService.countProducts();
        
        assertEquals(5L, count);
        verify(productRepository, times(1)).count();
    }
    
    @Test
    void testCountAvailableProducts() {
        when(productRepository.countAvailableProducts()).thenReturn(3L);
        
        long count = productService.countAvailableProducts();
        
        assertEquals(3L, count);
        verify(productRepository, times(1)).countAvailableProducts();
    }
}