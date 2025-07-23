package com.rethink.api.service;

import com.rethink.api.entity.Customer;
import com.rethink.api.entity.Order;
import com.rethink.api.entity.OrderItem;
import com.rethink.api.entity.Product;
import com.rethink.api.repository.OrderItemRepository;
import com.rethink.api.repository.OrderRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class OrderServiceTest {
    
    @Inject
    OrderService orderService;
    
    @InjectMock
    OrderRepository orderRepository;
    
    @InjectMock
    OrderItemRepository orderItemRepository;
    
    @InjectMock
    CustomerService customerService;
    
    @InjectMock
    ProductService productService;
    
    private Order testOrder;
    private Customer testCustomer;
    private Product testProduct;
    private OrderItem testOrderItem;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer("Test Customer", "test@email.com", "(11) 98765-4321", "123.456.789-00");
        testCustomer.id = 1L;
        
        testProduct = new Product("Test Product", "Test Description", new BigDecimal("100.00"), 10);
        testProduct.id = 1L;
        
        testOrder = new Order(testCustomer);
        testOrder.id = 1L;
        testOrder.status = Order.OrderStatus.PENDING;
        
        testOrderItem = new OrderItem(testProduct, 2, new BigDecimal("100.00"));
        testOrderItem.id = 1L;
        testOrder.addItem(testOrderItem);
    }
    
    @Test
    void testListAll() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.listAll()).thenReturn(orders);
        
        List<Order> result = orderService.listAll();
        
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).listAll();
    }
    
    @Test
    void testFindById() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        Order result = orderService.findById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.id);
        verify(orderRepository, times(1)).findByIdOptional(1L);
    }
    
    @Test
    void testFindByIdNotFound() {
        when(orderRepository.findByIdOptional(999L)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            orderService.findById(999L);
        });
    }
    
    @Test
    void testFindByCustomerId() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(1L)).thenReturn(orders);
        
        List<Order> result = orderService.findByCustomerId(1L);
        
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByCustomerId(1L);
    }
    
    @Test
    void testFindByStatus() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(Order.OrderStatus.PENDING)).thenReturn(orders);
        
        List<Order> result = orderService.findByStatus(Order.OrderStatus.PENDING);
        
        assertEquals(1, result.size());
        assertEquals(Order.OrderStatus.PENDING, result.get(0).status);
        verify(orderRepository, times(1)).findByStatus(Order.OrderStatus.PENDING);
    }
    
    @Test
    void testFindRecentOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findRecentOrders(5)).thenReturn(orders);
        
        List<Order> result = orderService.findRecentOrders(5);
        
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findRecentOrders(5);
    }
    
    @Test
    void testFindPendingOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findPendingOrders()).thenReturn(orders);
        
        List<Order> result = orderService.findPendingOrders();
        
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findPendingOrders();
    }
    
    @Test
    void testCreate() {
        Order newOrder = new Order();
        newOrder.customer = testCustomer;
        
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.product = testProduct;
        item.quantity = 2;
        items.add(item);
        
        when(customerService.findById(1L)).thenReturn(testCustomer);
        when(productService.findById(1L)).thenReturn(testProduct);
        doNothing().when(orderRepository).persist(any(Order.class));
        
        Order result = orderService.create(newOrder, items);
        
        assertNotNull(result);
        assertEquals(1, result.items.size());
        assertEquals(new BigDecimal("200.00"), result.totalAmount);
        assertEquals(8, testProduct.quantity);
        verify(orderRepository, times(1)).persist(any(Order.class));
    }
    
    @Test
    void testCreateWithNoItems() {
        Order newOrder = new Order();
        newOrder.customer = testCustomer;
        
        when(customerService.findById(1L)).thenReturn(testCustomer);
        
        assertThrows(BadRequestException.class, () -> {
            orderService.create(newOrder, new ArrayList<>());
        });
    }
    
    @Test
    void testCreateWithInsufficientStock() {
        Order newOrder = new Order();
        newOrder.customer = testCustomer;
        
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.product = testProduct;
        item.quantity = 20;
        items.add(item);
        
        when(customerService.findById(1L)).thenReturn(testCustomer);
        when(productService.findById(1L)).thenReturn(testProduct);
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            orderService.create(newOrder, items);
        });
        
        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }
    
    @Test
    void testUpdateStatus() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        Order result = orderService.updateStatus(1L, Order.OrderStatus.CONFIRMED);
        
        assertEquals(Order.OrderStatus.CONFIRMED, result.status);
        assertNotNull(result.paymentDate);
    }
    
    @Test
    void testUpdateStatusInvalidTransition() {
        testOrder.status = Order.OrderStatus.DELIVERED;
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        assertThrows(BadRequestException.class, () -> {
            orderService.updateStatus(1L, Order.OrderStatus.PENDING);
        });
    }
    
    @Test
    void testUpdateStatusToCancelled() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        int originalStock = testProduct.quantity;
        Order result = orderService.updateStatus(1L, Order.OrderStatus.CANCELLED);
        
        assertEquals(Order.OrderStatus.CANCELLED, result.status);
        assertEquals(originalStock + 2, testProduct.quantity);
    }
    
    @Test
    void testAddItem() {
        Product newProduct = new Product("New Product", "Description", new BigDecimal("50.00"), 5);
        newProduct.id = 2L;
        
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        when(productService.findById(2L)).thenReturn(newProduct);
        
        Order result = orderService.addItem(1L, 2L, 3);
        
        assertEquals(2, result.items.size());
        assertEquals(2, newProduct.quantity);
        assertEquals(new BigDecimal("350.00"), result.totalAmount);
    }
    
    @Test
    void testAddItemToExistingProduct() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        when(productService.findById(1L)).thenReturn(testProduct);
        
        Order result = orderService.addItem(1L, 1L, 1);
        
        assertEquals(1, result.items.size());
        assertEquals(3, result.items.get(0).quantity);
        assertEquals(9, testProduct.quantity);
    }
    
    @Test
    void testAddItemToNonPendingOrder() {
        testOrder.status = Order.OrderStatus.CONFIRMED;
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        assertThrows(BadRequestException.class, () -> {
            orderService.addItem(1L, 2L, 1);
        });
    }
    
    @Test
    void testRemoveItem() {
        // Adicionar um segundo item ao pedido para que não fique vazio após remover um
        Product secondProduct = new Product("Second Product", "Description", new BigDecimal("50.00"), 5);
        secondProduct.id = 2L;
        OrderItem secondItem = new OrderItem(secondProduct, 1, new BigDecimal("50.00"));
        secondItem.id = 2L;
        testOrder.addItem(secondItem);
        
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderItemRepository).delete(any(OrderItem.class));
        
        int originalStock = testProduct.quantity;
        Order result = orderService.removeItem(1L, 1L);
        
        assertEquals(1, result.items.size());
        assertEquals(secondItem, result.items.get(0));
        assertEquals(originalStock + 2, testProduct.quantity);
        verify(orderItemRepository, times(1)).delete(testOrderItem);
    }
    
    @Test
    void testRemoveLastItemFails() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            orderService.removeItem(1L, 1L);
        });
        
        assertTrue(exception.getMessage().contains("Pedido não pode ficar sem itens"));
    }
    
    @Test
    void testRemoveItemFromNonPendingOrder() {
        testOrder.status = Order.OrderStatus.CONFIRMED;
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        assertThrows(BadRequestException.class, () -> {
            orderService.removeItem(1L, 1L);
        });
    }
    
    @Test
    void testDelete() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(any(Order.class));
        
        int originalStock = testProduct.quantity;
        orderService.delete(1L);
        
        assertEquals(originalStock + 2, testProduct.quantity);
        verify(orderRepository, times(1)).delete(testOrder);
    }
    
    @Test
    void testDeleteNonPendingOrder() {
        testOrder.status = Order.OrderStatus.CONFIRMED;
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrder));
        
        assertThrows(BadRequestException.class, () -> {
            orderService.delete(1L);
        });
    }
    
    @Test
    void testCountOrders() {
        when(orderRepository.count()).thenReturn(10L);
        
        long count = orderService.countOrders();
        
        assertEquals(10L, count);
        verify(orderRepository, times(1)).count();
    }
    
    @Test
    void testCountByStatus() {
        when(orderRepository.countByStatus(Order.OrderStatus.PENDING)).thenReturn(5L);
        
        long count = orderService.countByStatus(Order.OrderStatus.PENDING);
        
        assertEquals(5L, count);
        verify(orderRepository, times(1)).countByStatus(Order.OrderStatus.PENDING);
    }
}