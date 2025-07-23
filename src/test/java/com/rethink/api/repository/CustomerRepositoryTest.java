package com.rethink.api.repository;

import com.rethink.api.entity.Customer;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class CustomerRepositoryTest {
    
    @Inject
    CustomerRepository customerRepository;
    
    @BeforeEach
    void setUp() {
        // Clean database before each test
        customerRepository.deleteAll();
        
        // Create test customers
        Customer customer1 = new Customer("João Silva", "joao@email.com", "(11) 98765-4321", "123.456.789-00");
        customer1.city = "São Paulo";
        customer1.state = "SP";
        customer1.createdAt = LocalDateTime.now().minusDays(5);
        
        Customer customer2 = new Customer("Maria Santos", "maria@email.com", "(21) 98765-1234", "987.654.321-00");
        customer2.city = "Rio de Janeiro";
        customer2.state = "RJ";
        customer2.createdAt = LocalDateTime.now().minusDays(2);
        
        Customer customer3 = new Customer("Pedro Oliveira", "pedro@email.com", "(31) 98765-5678", "456.789.123-00");
        customer3.city = "São Paulo";
        customer3.state = "SP";
        customer3.createdAt = LocalDateTime.now();
        
        customerRepository.persist(customer1);
        customerRepository.persist(customer2);
        customerRepository.persist(customer3);
    }
    
    @Test
    void testFindByEmail() {
        Optional<Customer> customer = customerRepository.findByEmail("joao@email.com");
        
        assertTrue(customer.isPresent());
        assertEquals("João Silva", customer.get().name);
        assertEquals("joao@email.com", customer.get().email);
    }
    
    @Test
    void testFindByEmailNotFound() {
        Optional<Customer> customer = customerRepository.findByEmail("notfound@email.com");
        
        assertFalse(customer.isPresent());
    }
    
    @Test
    void testFindByCpf() {
        Optional<Customer> customer = customerRepository.findByCpf("123.456.789-00");
        
        assertTrue(customer.isPresent());
        assertEquals("João Silva", customer.get().name);
        assertEquals("123.456.789-00", customer.get().cpf);
    }
    
    @Test
    void testFindByCpfNotFound() {
        Optional<Customer> customer = customerRepository.findByCpf("000.000.000-00");
        
        assertFalse(customer.isPresent());
    }
    
    @Test
    void testFindByNameContaining() {
        List<Customer> customers = customerRepository.findByNameContaining("Silva");
        
        assertEquals(1, customers.size());
        assertTrue(customers.get(0).name.toLowerCase().contains("silva"));
    }
    
    @Test
    void testFindByNameContainingCaseInsensitive() {
        List<Customer> customers = customerRepository.findByNameContaining("SANTOS");
        
        assertEquals(1, customers.size());
        assertEquals("Maria Santos", customers.get(0).name);
    }
    
    @Test
    void testFindByCity() {
        List<Customer> customers = customerRepository.findByCity("São Paulo");
        
        assertEquals(2, customers.size());
        assertTrue(customers.stream().allMatch(c -> c.city.equals("São Paulo")));
    }
    
    @Test
    void testFindByCityNotFound() {
        List<Customer> customers = customerRepository.findByCity("Brasília");
        
        assertTrue(customers.isEmpty());
    }
    
    @Test
    void testFindByState() {
        List<Customer> customers = customerRepository.findByState("SP");
        
        assertEquals(2, customers.size());
        assertTrue(customers.stream().allMatch(c -> c.state.equals("SP")));
    }
    
    @Test
    void testFindByStateNotFound() {
        List<Customer> customers = customerRepository.findByState("DF");
        
        assertTrue(customers.isEmpty());
    }
    
    @Test
    void testExistsByEmail() {
        assertTrue(customerRepository.existsByEmail("joao@email.com"));
        assertFalse(customerRepository.existsByEmail("notexist@email.com"));
    }
    
    @Test
    void testExistsByCpf() {
        assertTrue(customerRepository.existsByCpf("123.456.789-00"));
        assertFalse(customerRepository.existsByCpf("000.000.000-00"));
    }
    
    @Test
    void testFindRecentCustomers() {
        List<Customer> customers = customerRepository.findRecentCustomers(2);
        
        assertEquals(2, customers.size());
        assertEquals("Pedro Oliveira", customers.get(0).name); // Most recent
        assertEquals("Maria Santos", customers.get(1).name);  // Second most recent
    }
    
    @Test
    void testPersistAndFind() {
        Customer newCustomer = new Customer("Novo Cliente", "novo@email.com", "(41) 98765-9999", "111.222.333-44");
        newCustomer.city = "Curitiba";
        newCustomer.state = "PR";
        customerRepository.persist(newCustomer);
        
        assertNotNull(newCustomer.id);
        
        Customer found = customerRepository.findById(newCustomer.id);
        assertNotNull(found);
        assertEquals("Novo Cliente", found.name);
        assertEquals("novo@email.com", found.email);
    }
    
    @Test
    void testUpdate() {
        Customer customer = customerRepository.findByEmail("joao@email.com").get();
        customer.phone = "(11) 99999-9999";
        customer.address = "Nova Rua, 123";
        customer.updatedAt = LocalDateTime.now();
        
        customerRepository.persist(customer);
        
        Customer updated = customerRepository.findById(customer.id);
        assertEquals("(11) 99999-9999", updated.phone);
        assertEquals("Nova Rua, 123", updated.address);
        assertNotNull(updated.updatedAt);
    }
    
    @Test
    void testDelete() {
        Customer customer = customerRepository.findByEmail("joao@email.com").get();
        Long id = customer.id;
        
        customerRepository.delete(customer);
        
        Customer deleted = customerRepository.findById(id);
        assertNull(deleted);
    }
    
    @Test
    void testCount() {
        long count = customerRepository.count();
        
        assertEquals(3, count);
    }
    
    @Test
    void testListAll() {
        List<Customer> customers = customerRepository.listAll();
        
        assertEquals(3, customers.size());
    }
}