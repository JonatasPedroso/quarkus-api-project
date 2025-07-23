package com.rethink.api.repository;

import com.rethink.api.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    public List<Product> findByName(String name) {
        return find("name", name).list();
    }
    
    public List<Product> findByNameContaining(String name) {
        return find("LOWER(name) like LOWER(?1)", "%" + name + "%").list();
    }
    
    public List<Product> findAvailableProducts() {
        return find("quantity > 0").list();
    }
    
    public List<Product> findProductsOrderByPriceAsc() {
        return findAll().stream()
                .sorted((p1, p2) -> p1.price.compareTo(p2.price))
                .toList();
    }
    
    public long countAvailableProducts() {
        return count("quantity > 0");
    }
}