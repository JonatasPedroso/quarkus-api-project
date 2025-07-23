package com.rethink.api.repository;

import com.rethink.api.entity.Product;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(RepositoryTestProfile.class)
public class ProductRepositoryTest extends BaseRepositoryTest {
    
    @Inject
    ProductRepository productRepository;
    
    @BeforeEach
    @Transactional
    void setUp() {
        cleanDatabase();
        
        Product product1 = new Product("Test Product 1", "Description 1", new BigDecimal("100.00"), 10);
        Product product2 = new Product("Test Product 2", "Description 2", new BigDecimal("200.00"), 0);
        Product product3 = new Product("Another Product", "Description 3", new BigDecimal("150.00"), 5);
        
        productRepository.persist(product1);
        productRepository.persist(product2);
        productRepository.persist(product3);
        entityManager.flush();
        entityManager.clear();
    }
    
    @Test
    @Transactional
    void testFindByName() {
        List<Product> products = productRepository.findByName("Test Product 1");
        
        assertEquals(1, products.size());
        assertEquals("Test Product 1", products.get(0).name);
    }
    
    @Test
    @Transactional
    void testFindByNameNotFound() {
        List<Product> products = productRepository.findByName("Non Existent");
        
        assertTrue(products.isEmpty());
    }
    
    @Test
    @Transactional
    void testFindByNameContaining() {
        List<Product> products = productRepository.findByNameContaining("Test");
        
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(p -> p.name.toLowerCase().contains("test")));
    }
    
    @Test
    @Transactional
    void testFindByNameContainingCaseInsensitive() {
        List<Product> products = productRepository.findByNameContaining("PRODUCT");
        
        assertEquals(3, products.size());
    }
    
    @Test
    @Transactional
    void testFindAvailableProducts() {
        List<Product> products = productRepository.findAvailableProducts();
        
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(p -> p.quantity > 0));
    }
    
    @Test
    @Transactional
    void testFindProductsOrderByPriceAsc() {
        List<Product> products = productRepository.findProductsOrderByPriceAsc();
        
        assertEquals(3, products.size());
        assertEquals(new BigDecimal("100.00"), products.get(0).price);
        assertEquals(new BigDecimal("150.00"), products.get(1).price);
        assertEquals(new BigDecimal("200.00"), products.get(2).price);
    }
    
    @Test
    @Transactional
    void testCountAvailableProducts() {
        long count = productRepository.countAvailableProducts();
        
        assertEquals(2, count);
    }
    
    @Test
    @Transactional
    void testPersistAndFind() {
        Product newProduct = new Product("New Product", "New Description", new BigDecimal("300.00"), 20);
        productRepository.persist(newProduct);
        
        assertNotNull(newProduct.id);
        
        Product found = productRepository.findById(newProduct.id);
        assertNotNull(found);
        assertEquals("New Product", found.name);
        assertEquals(new BigDecimal("300.00"), found.price);
    }
    
    @Test
    @Transactional
    void testUpdate() {
        Product product = productRepository.findByName("Test Product 1").get(0);
        product.price = new BigDecimal("120.00");
        product.quantity = 15;
        
        productRepository.persist(product);
        
        Product updated = productRepository.findById(product.id);
        assertEquals(new BigDecimal("120.00"), updated.price);
        assertEquals(15, updated.quantity);
    }
    
    @Test
    @Transactional
    void testDelete() {
        Product product = productRepository.findByName("Test Product 1").get(0);
        Long id = product.id;
        
        productRepository.delete(product);
        
        Product deleted = productRepository.findById(id);
        assertNull(deleted);
    }
    
    @Test
    @Transactional
    void testCount() {
        long count = productRepository.count();
        
        assertEquals(3, count);
    }
    
    @Test
    @Transactional
    void testListAll() {
        List<Product> products = productRepository.listAll();
        
        assertEquals(3, products.size());
    }
}