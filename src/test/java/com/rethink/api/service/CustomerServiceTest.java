package com.rethink.api.service;

import com.rethink.api.entity.Customer;
import com.rethink.api.entity.Order;
import com.rethink.api.repository.CustomerRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class CustomerServiceTest {
    
    @Inject
    CustomerService customerService;
    
    @InjectMock
    CustomerRepository customerRepository;
    
    private Customer testCustomer;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer("Test Customer", "test@email.com", "(11) 98765-4321", "123.456.789-00");
        testCustomer.id = 1L;
        testCustomer.address = "Test Address";
        testCustomer.city = "Test City";
        testCustomer.state = "SP";
        testCustomer.zipCode = "12345-678";
    }
    
    @Test
    void testListAll() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.listAll()).thenReturn(customers);
        
        List<Customer> result = customerService.listAll();
        
        assertEquals(1, result.size());
        assertEquals("Test Customer", result.get(0).name);
        verify(customerRepository, times(1)).listAll();
    }
    
    @Test
    void testFindById() {
        when(customerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testCustomer));
        
        Customer result = customerService.findById(1L);
        
        assertNotNull(result);
        assertEquals("Test Customer", result.name);
        verify(customerRepository, times(1)).findByIdOptional(1L);
    }
    
    @Test
    void testFindByIdNotFound() {
        when(customerRepository.findByIdOptional(999L)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            customerService.findById(999L);
        });
    }
    
    @Test
    void testFindByEmail() {
        when(customerRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testCustomer));
        
        Customer result = customerService.findByEmail("test@email.com");
        
        assertNotNull(result);
        assertEquals("test@email.com", result.email);
        verify(customerRepository, times(1)).findByEmail("test@email.com");
    }
    
    @Test
    void testFindByEmailNotFound() {
        when(customerRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            customerService.findByEmail("notfound@email.com");
        });
    }
    
    @Test
    void testFindByCpf() {
        when(customerRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(testCustomer));
        
        Customer result = customerService.findByCpf("123.456.789-00");
        
        assertNotNull(result);
        assertEquals("123.456.789-00", result.cpf);
        verify(customerRepository, times(1)).findByCpf("123.456.789-00");
    }
    
    @Test
    void testSearchByName() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findByNameContaining("Test")).thenReturn(customers);
        
        List<Customer> result = customerService.searchByName("Test");
        
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findByNameContaining("Test");
    }
    
    @Test
    void testFindByCity() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findByCity("Test City")).thenReturn(customers);
        
        List<Customer> result = customerService.findByCity("Test City");
        
        assertEquals(1, result.size());
        assertEquals("Test City", result.get(0).city);
        verify(customerRepository, times(1)).findByCity("Test City");
    }
    
    @Test
    void testFindByState() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findByState("SP")).thenReturn(customers);
        
        List<Customer> result = customerService.findByState("SP");
        
        assertEquals(1, result.size());
        assertEquals("SP", result.get(0).state);
        verify(customerRepository, times(1)).findByState("SP");
    }
    
    @Test
    void testFindRecentCustomers() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findRecentCustomers(5)).thenReturn(customers);
        
        List<Customer> result = customerService.findRecentCustomers(5);
        
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findRecentCustomers(5);
    }
    
    @Test
    void testCreate() {
        Customer newCustomer = new Customer("New Customer", "new@email.com", "(11) 98765-1234", "987.654.321-00");
        when(customerRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
        when(customerRepository.findByCpf("987.654.321-00")).thenReturn(Optional.empty());
        doNothing().when(customerRepository).persist(any(Customer.class));
        
        Customer result = customerService.create(newCustomer);
        
        assertEquals("New Customer", result.name);
        assertNotNull(result.createdAt);
        verify(customerRepository, times(1)).persist(newCustomer);
    }
    
    @Test
    void testCreateWithDuplicateEmail() {
        Customer newCustomer = new Customer("New Customer", "test@email.com", "(11) 98765-1234", "987.654.321-00");
        when(customerRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testCustomer));
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            customerService.create(newCustomer);
        });
        
        assertTrue(exception.getMessage().contains("Email já cadastrado"));
    }
    
    @Test
    void testCreateWithDuplicateCpf() {
        Customer newCustomer = new Customer("New Customer", "new@email.com", "(11) 98765-1234", "123.456.789-00");
        when(customerRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
        when(customerRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(testCustomer));
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            customerService.create(newCustomer);
        });
        
        assertTrue(exception.getMessage().contains("CPF já cadastrado"));
    }
    
    @Test
    void testUpdate() {
        Customer updateCustomer = new Customer("Updated Customer", "updated@email.com", "(11) 98765-9999", "123.456.789-00");
        updateCustomer.address = "New Address";
        
        when(customerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.findByEmail("updated@email.com")).thenReturn(Optional.empty());
        when(customerRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(testCustomer));
        
        Customer result = customerService.update(1L, updateCustomer);
        
        assertEquals("Updated Customer", result.name);
        assertEquals("updated@email.com", result.email);
        assertEquals("New Address", result.address);
        assertNotNull(result.updatedAt);
    }
    
    @Test
    void testUpdateWithDuplicateEmail() {
        Customer otherCustomer = new Customer("Other", "other@email.com", "(11) 11111-1111", "111.111.111-11");
        otherCustomer.id = 2L;
        
        Customer updateCustomer = new Customer("Updated", "other@email.com", "(11) 98765-9999", "123.456.789-00");
        
        when(customerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.findByEmail("other@email.com")).thenReturn(Optional.of(otherCustomer));
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            customerService.update(1L, updateCustomer);
        });
        
        assertTrue(exception.getMessage().contains("Email já cadastrado"));
    }
    
    @Test
    void testDelete() {
        when(customerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testCustomer));
        doNothing().when(customerRepository).delete(any(Customer.class));
        
        customerService.delete(1L);
        
        verify(customerRepository, times(1)).delete(testCustomer);
    }
    
    @Test
    void testDeleteCustomerWithOrders() {
        Order order = new Order(testCustomer);
        testCustomer.orders.add(order);
        
        when(customerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testCustomer));
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            customerService.delete(1L);
        });
        
        assertTrue(exception.getMessage().contains("Não é possível excluir cliente com pedidos vinculados"));
    }
    
    @Test
    void testCountCustomers() {
        when(customerRepository.count()).thenReturn(10L);
        
        long count = customerService.countCustomers();
        
        assertEquals(10L, count);
        verify(customerRepository, times(1)).count();
    }
}