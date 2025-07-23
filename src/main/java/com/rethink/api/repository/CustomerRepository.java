package com.rethink.api.repository;

import com.rethink.api.entity.Customer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
    
    public Optional<Customer> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    
    public Optional<Customer> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
    
    public List<Customer> findByNameContaining(String name) {
        return find("LOWER(name) like LOWER(?1)", "%" + name + "%").list();
    }
    
    public List<Customer> findByCity(String city) {
        return find("city", city).list();
    }
    
    public List<Customer> findByState(String state) {
        return find("state", state).list();
    }
    
    public boolean existsByEmail(String email) {
        return count("email = ?1", email) > 0;
    }
    
    public boolean existsByCpf(String cpf) {
        return count("cpf = ?1", cpf) > 0;
    }
    
    public List<Customer> findRecentCustomers(int limit) {
        return find("ORDER BY createdAt DESC").page(0, limit).list();
    }
}