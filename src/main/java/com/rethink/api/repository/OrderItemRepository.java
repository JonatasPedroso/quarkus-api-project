package com.rethink.api.repository;

import com.rethink.api.entity.OrderItem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepository<OrderItem> {
    
    public List<OrderItem> findByOrderId(Long orderId) {
        return find("order.id", orderId).list();
    }
    
    public List<OrderItem> findByProductId(Long productId) {
        return find("product.id", productId).list();
    }
    
    public void deleteByOrderId(Long orderId) {
        delete("order.id", orderId);
    }
}