package com.rethink.api.service;

import com.rethink.api.entity.Customer;
import com.rethink.api.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CustomerService {
    
    @Inject
    CustomerRepository customerRepository;
    
    public List<Customer> listAll() {
        return customerRepository.listAll();
    }
    
    public Customer findById(Long id) {
        return customerRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado com ID: " + id));
    }
    
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado com email: " + email));
    }
    
    public Customer findByCpf(String cpf) {
        return customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado com CPF: " + cpf));
    }
    
    public List<Customer> searchByName(String name) {
        return customerRepository.findByNameContaining(name);
    }
    
    public List<Customer> findByCity(String city) {
        return customerRepository.findByCity(city);
    }
    
    public List<Customer> findByState(String state) {
        return customerRepository.findByState(state);
    }
    
    public List<Customer> findRecentCustomers(int limit) {
        return customerRepository.findRecentCustomers(limit);
    }
    
    @Transactional
    public Customer create(Customer customer) {
        validateUniqueFields(customer, null);
        customer.createdAt = LocalDateTime.now();
        customerRepository.persist(customer);
        return customer;
    }
    
    @Transactional
    public Customer update(Long id, Customer customer) {
        Customer entity = findById(id);
        validateUniqueFields(customer, id);
        
        entity.name = customer.name;
        entity.email = customer.email;
        entity.phone = customer.phone;
        entity.cpf = customer.cpf;
        entity.address = customer.address;
        entity.city = customer.city;
        entity.state = customer.state;
        entity.zipCode = customer.zipCode;
        entity.updatedAt = LocalDateTime.now();
        
        return entity;
    }
    
    @Transactional
    public void delete(Long id) {
        Customer entity = findById(id);
        if (!entity.orders.isEmpty()) {
            throw new BadRequestException("Não é possível excluir cliente com pedidos vinculados");
        }
        customerRepository.delete(entity);
    }
    
    private void validateUniqueFields(Customer customer, Long excludeId) {
        var existingByEmail = customerRepository.findByEmail(customer.email);
        if (existingByEmail.isPresent() && !existingByEmail.get().id.equals(excludeId)) {
            throw new BadRequestException("Email já cadastrado: " + customer.email);
        }
        
        var existingByCpf = customerRepository.findByCpf(customer.cpf);
        if (existingByCpf.isPresent() && !existingByCpf.get().id.equals(excludeId)) {
            throw new BadRequestException("CPF já cadastrado: " + customer.cpf);
        }
    }
    
    public long countCustomers() {
        return customerRepository.count();
    }
}