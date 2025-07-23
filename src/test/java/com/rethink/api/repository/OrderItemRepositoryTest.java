package com.rethink.api.repository;

import com.rethink.api.entity.Customer;
import com.rethink.api.entity.Order;
import com.rethink.api.entity.OrderItem;
import com.rethink.api.entity.Product;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class OrderItemRepositoryTest {
    
    @Inject
    OrderItemRepository orderItemRepository;
    
    @Inject
    OrderRepository orderRepository;
    
    @Inject
    CustomerRepository customerRepository;
    
    @Inject
    ProductRepository productRepository;
    
    private Order testOrder1;
    private Order testOrder2;
    private Product testProduct1;
    private Product testProduct2;
    
    @BeforeEach
    void setUp() {
        // Clean database
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
        
        // Create test customer
        Customer customer = new Customer("Test Customer", "test@email.com", "(11) 98765-4321", "123.456.789-00");
        customerRepository.persist(customer);
        
        // Create test products
        testProduct1 = new Product("Product 1", "Description 1", new BigDecimal("100.00"), 50);
        testProduct2 = new Product("Product 2", "Description 2", new BigDecimal("200.00"), 30);
        productRepository.persist(testProduct1);
        productRepository.persist(testProduct2);
        
        // Create test orders
        testOrder1 = new Order(customer);
        testOrder1.status = Order.OrderStatus.PENDING;
        testOrder1.totalAmount = new BigDecimal("500.00");
        
        testOrder2 = new Order(customer);
        testOrder2.status = Order.OrderStatus.CONFIRMED;
        testOrder2.totalAmount = new BigDecimal("200.00");
        
        orderRepository.persist(testOrder1);
        orderRepository.persist(testOrder2);
        
        // Create test order items
        OrderItem item1 = new OrderItem(testProduct1, 2, new BigDecimal("100.00"));
        item1.order = testOrder1;
        
        OrderItem item2 = new OrderItem(testProduct2, 1, new BigDecimal("200.00"));
        item2.order = testOrder1;
        
        OrderItem item3 = new OrderItem(testProduct1, 1, new BigDecimal("100.00"));
        item3.order = testOrder1;
        
        OrderItem item4 = new OrderItem(testProduct2, 1, new BigDecimal("200.00"));
        item4.order = testOrder2;
        
        orderItemRepository.persist(item1);
        orderItemRepository.persist(item2);
        orderItemRepository.persist(item3);
        orderItemRepository.persist(item4);
    }
    
    @Test
    void testFindByOrderId() {
        List<OrderItem> items = orderItemRepository.findByOrderId(testOrder1.id);
        
        assertEquals(3, items.size());
        assertTrue(items.stream().allMatch(item -> item.order.id.equals(testOrder1.id)));
    }
    
    @Test
    void testFindByOrderIdEmpty() {
        // Create order without items
        Customer customer = new Customer("Another Customer", "another@email.com", "(21) 98765-1234", "987.654.321-00");
        customerRepository.persist(customer);
        
        Order emptyOrder = new Order(customer);
        emptyOrder.status = Order.OrderStatus.PENDING;
        emptyOrder.totalAmount = BigDecimal.ZERO;
        orderRepository.persist(emptyOrder);
        
        List<OrderItem> items = orderItemRepository.findByOrderId(emptyOrder.id);
        
        assertTrue(items.isEmpty());
    }
    
    @Test
    void testFindByProductId() {
        List<OrderItem> items = orderItemRepository.findByProductId(testProduct1.id);
        
        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(item -> item.product.id.equals(testProduct1.id)));
    }
    
    @Test
    void testFindByProductIdMultipleOrders() {
        List<OrderItem> items = orderItemRepository.findByProductId(testProduct2.id);
        
        assertEquals(2, items.size());
        // Check that items are from different orders
        assertNotEquals(items.get(0).order.id, items.get(1).order.id);
    }
    
    @Test
    void testDeleteByOrderId() {
        // Verify items exist
        List<OrderItem> itemsBefore = orderItemRepository.findByOrderId(testOrder1.id);
        assertEquals(3, itemsBefore.size());
        
        // Delete all items from order1
        orderItemRepository.deleteByOrderId(testOrder1.id);
        
        // Verify items were deleted
        List<OrderItem> itemsAfter = orderItemRepository.findByOrderId(testOrder1.id);
        assertTrue(itemsAfter.isEmpty());
        
        // Verify order2 items still exist
        List<OrderItem> order2Items = orderItemRepository.findByOrderId(testOrder2.id);
        assertEquals(1, order2Items.size());
    }
    
    @Test
    void testPersistAndFind() {
        Product newProduct = new Product("New Product", "New Description", new BigDecimal("150.00"), 20);
        productRepository.persist(newProduct);
        
        OrderItem newItem = new OrderItem(newProduct, 3, new BigDecimal("150.00"));
        newItem.order = testOrder2;
        orderItemRepository.persist(newItem);
        
        assertNotNull(newItem.id);
        assertEquals(new BigDecimal("450.00"), newItem.subtotal);
        
        OrderItem found = orderItemRepository.findById(newItem.id);
        assertNotNull(found);
        assertEquals(3, found.quantity);
        assertEquals(new BigDecimal("150.00"), found.unitPrice);
        assertEquals(new BigDecimal("450.00"), found.subtotal);
    }
    
    @Test
    void testUpdate() {
        OrderItem item = orderItemRepository.findByOrderId(testOrder1.id).get(0);
        item.quantity = 5;
        item.calculateSubtotal();
        
        orderItemRepository.persist(item);
        
        OrderItem updated = orderItemRepository.findById(item.id);
        assertEquals(5, updated.quantity);
        assertEquals(new BigDecimal("500.00"), updated.subtotal);
    }
    
    @Test
    void testDelete() {
        OrderItem item = orderItemRepository.findByOrderId(testOrder1.id).get(0);
        Long id = item.id;
        
        orderItemRepository.delete(item);
        
        OrderItem deleted = orderItemRepository.findById(id);
        assertNull(deleted);
    }
    
    @Test
    void testCount() {
        long count = orderItemRepository.count();
        
        assertEquals(4, count);
    }
    
    @Test
    void testListAll() {
        List<OrderItem> items = orderItemRepository.listAll();
        
        assertEquals(4, items.size());
    }
    
    @Test
    void testSubtotalCalculation() {
        OrderItem item = new OrderItem(testProduct1, 10, new BigDecimal("99.99"));
        
        assertEquals(new BigDecimal("999.90"), item.subtotal);
    }
}