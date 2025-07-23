package com.rethink.api.repository;

import com.rethink.api.entity.Customer;
import com.rethink.api.entity.Order;
import com.rethink.api.entity.OrderItem;
import com.rethink.api.entity.Product;
import jakarta.transaction.Transactional;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(RepositoryTestProfile.class)
public class OrderRepositoryTest extends BaseRepositoryTest {
    
    @Inject
    OrderRepository orderRepository;
    
    @Inject
    CustomerRepository customerRepository;
    
    @Inject
    ProductRepository productRepository;
    
    private Customer testCustomer1;
    private Customer testCustomer2;
    private Product testProduct;
    
    @BeforeEach
    @Transactional
    void setUp() {
        cleanDatabase();
        
        testCustomer1 = new Customer("Customer 1", "customer1@email.com", "(11) 98765-4321", "123.456.789-00");
        testCustomer2 = new Customer("Customer 2", "customer2@email.com", "(21) 98765-1234", "987.654.321-00");
        customerRepository.persist(testCustomer1);
        customerRepository.persist(testCustomer2);
        
        testProduct = new Product("Test Product", "Description", new BigDecimal("100.00"), 50);
        productRepository.persist(testProduct);
        
        Order order1 = new Order(testCustomer1);
        order1.status = Order.OrderStatus.DELIVERED;
        order1.totalAmount = new BigDecimal("200.00");
        order1.orderDate = LocalDateTime.now().minusDays(5);
        order1.paymentDate = LocalDateTime.now().minusDays(5);
        order1.shippingDate = LocalDateTime.now().minusDays(4);
        order1.deliveryDate = LocalDateTime.now().minusDays(2);
        
        Order order2 = new Order(testCustomer1);
        order2.status = Order.OrderStatus.PENDING;
        order2.totalAmount = new BigDecimal("100.00");
        order2.orderDate = LocalDateTime.now().minusDays(1);
        
        Order order3 = new Order(testCustomer2);
        order3.status = Order.OrderStatus.CONFIRMED;
        order3.totalAmount = new BigDecimal("300.00");
        order3.orderDate = LocalDateTime.now().minusHours(12);
        order3.paymentDate = LocalDateTime.now().minusHours(12);
        
        Order order4 = new Order(testCustomer2);
        order4.status = Order.OrderStatus.PENDING;
        order4.totalAmount = new BigDecimal("150.00");
        order4.orderDate = LocalDateTime.now();
        
        OrderItem item1 = new OrderItem(testProduct, 2, new BigDecimal("100.00"));
        order1.addItem(item1);
        
        OrderItem item2 = new OrderItem(testProduct, 1, new BigDecimal("100.00"));
        order2.addItem(item2);
        
        orderRepository.persist(order1);
        orderRepository.persist(order2);
        orderRepository.persist(order3);
        orderRepository.persist(order4);
        entityManager.flush();
        entityManager.clear();
    }
    
    @Test
    @Transactional
    void testFindByCustomerId() {
        List<Order> orders = orderRepository.findByCustomerId(testCustomer1.id);
        
        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o -> o.customer.id.equals(testCustomer1.id)));
    }
    
    @Test
    @Transactional
    void testFindByStatus() {
        List<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING);
        
        assertEquals(2, pendingOrders.size());
        assertTrue(pendingOrders.stream().allMatch(o -> o.status == Order.OrderStatus.PENDING));
    }
    
    @Test
    @Transactional
    void testFindByCustomerIdAndStatus() {
        List<Order> orders = orderRepository.findByCustomerIdAndStatus(testCustomer1.id, Order.OrderStatus.PENDING);
        
        assertEquals(1, orders.size());
        assertEquals(testCustomer1.id, orders.get(0).customer.id);
        assertEquals(Order.OrderStatus.PENDING, orders.get(0).status);
    }
    
    @Test
    @Transactional
    void testFindByDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);
        
        assertEquals(3, orders.size());
        assertTrue(orders.stream().allMatch(o -> 
            o.orderDate.isAfter(startDate.minusSeconds(1)) && 
            o.orderDate.isBefore(endDate.plusSeconds(1))
        ));
    }
    
    @Test
    @Transactional
    void testFindRecentOrders() {
        List<Order> recentOrders = orderRepository.findRecentOrders(2);
        
        assertEquals(2, recentOrders.size());
        assertTrue(recentOrders.get(0).orderDate.isAfter(recentOrders.get(1).orderDate));
    }
    
    @Test
    @Transactional
    void testFindPendingOrders() {
        List<Order> pendingOrders = orderRepository.findPendingOrders();
        
        assertEquals(2, pendingOrders.size());
        assertTrue(pendingOrders.stream().allMatch(o -> o.status == Order.OrderStatus.PENDING));
        assertTrue(pendingOrders.get(0).orderDate.isBefore(pendingOrders.get(1).orderDate));
    }
    
    @Test
    @Transactional
    void testFindOrdersToShip() {
        List<Order> ordersToShip = orderRepository.findOrdersToShip();
        
        assertEquals(1, ordersToShip.size());
        assertEquals(Order.OrderStatus.CONFIRMED, ordersToShip.get(0).status);
        assertNull(ordersToShip.get(0).shippingDate);
    }
    
    @Test
    @Transactional
    void testCountByStatus() {
        assertEquals(2L, orderRepository.countByStatus(Order.OrderStatus.PENDING));
        assertEquals(1L, orderRepository.countByStatus(Order.OrderStatus.CONFIRMED));
        assertEquals(1L, orderRepository.countByStatus(Order.OrderStatus.DELIVERED));
        assertEquals(0L, orderRepository.countByStatus(Order.OrderStatus.CANCELLED));
    }
    
    @Test
    @Transactional
    void testCountByCustomerId() {
        assertEquals(2L, orderRepository.countByCustomerId(testCustomer1.id));
        assertEquals(2L, orderRepository.countByCustomerId(testCustomer2.id));
    }
    
    @Test
    @Transactional
    void testFindOrdersWithItems() {
        List<Order> ordersWithItems = orderRepository.findOrdersWithItems();
        
        assertEquals(2, ordersWithItems.size());
        assertTrue(ordersWithItems.stream().allMatch(o -> !o.items.isEmpty()));
    }
    
    @Test
    @Transactional
    void testPersistAndFind() {
        Order newOrder = new Order(testCustomer1);
        newOrder.status = Order.OrderStatus.PROCESSING;
        newOrder.totalAmount = new BigDecimal("500.00");
        orderRepository.persist(newOrder);
        
        assertNotNull(newOrder.id);
        
        Order found = orderRepository.findById(newOrder.id);
        assertNotNull(found);
        assertEquals(Order.OrderStatus.PROCESSING, found.status);
        assertEquals(new BigDecimal("500.00"), found.totalAmount);
    }
    
    @Test
    @Transactional
    void testUpdate() {
        Order order = orderRepository.findByStatus(Order.OrderStatus.PENDING).get(0);
        order.status = Order.OrderStatus.CONFIRMED;
        order.paymentDate = LocalDateTime.now();
        
        orderRepository.persist(order);
        
        Order updated = orderRepository.findById(order.id);
        assertEquals(Order.OrderStatus.CONFIRMED, updated.status);
        assertNotNull(updated.paymentDate);
    }
    
    @Test
    @Transactional
    void testDelete() {
        Order order = orderRepository.findByStatus(Order.OrderStatus.PENDING).get(0);
        Long id = order.id;
        
        orderRepository.delete(order);
        
        Order deleted = orderRepository.findById(id);
        assertNull(deleted);
    }
    
    @Test
    @Transactional
    void testCount() {
        long count = orderRepository.count();
        
        assertEquals(4, count);
    }
    
    @Test
    @Transactional
    void testListAll() {
        List<Order> orders = orderRepository.listAll();
        
        assertEquals(4, orders.size());
    }
}