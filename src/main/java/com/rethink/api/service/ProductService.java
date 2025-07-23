package com.rethink.api.service;

import com.rethink.api.entity.Product;
import com.rethink.api.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ProductService {
    
    @Inject
    ProductRepository productRepository;
    
    public List<Product> listAll() {
        return productRepository.listAll();
    }
    
    public Product findById(Long id) {
        return productRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado com ID: " + id));
    }
    
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContaining(name);
    }
    
    public List<Product> listAvailable() {
        return productRepository.findAvailableProducts();
    }
    
    @Transactional
    public Product create(Product product) {
        productRepository.persist(product);
        return product;
    }
    
    @Transactional
    public Product update(Long id, Product product) {
        Product entity = findById(id);
        entity.name = product.name;
        entity.description = product.description;
        entity.price = product.price;
        entity.quantity = product.quantity;
        return entity;
    }
    
    @Transactional
    public void delete(Long id) {
        Product entity = findById(id);
        productRepository.delete(entity);
    }
    
    public long countProducts() {
        return productRepository.count();
    }
    
    public long countAvailableProducts() {
        return productRepository.countAvailableProducts();
    }
}