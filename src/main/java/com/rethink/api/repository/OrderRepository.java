package com.rethink.api.repository;

import com.rethink.api.entity.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {
    
    public List<Order> findByCustomerId(Long customerId) {
        return find("customer.id", customerId).list();
    }
    
    public List<Order> findByStatus(Order.OrderStatus status) {
        return find("status", status).list();
    }
    
    public List<Order> findByCustomerIdAndStatus(Long customerId, Order.OrderStatus status) {
        return find("customer.id = ?1 and status = ?2", customerId, status).list();
    }
    
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return find("orderDate between ?1 and ?2", startDate, endDate).list();
    }
    
    public List<Order> findRecentOrders(int limit) {
        return find("ORDER BY orderDate DESC").page(0, limit).list();
    }
    
    public List<Order> findPendingOrders() {
        return find("status = ?1 ORDER BY orderDate", Order.OrderStatus.PENDING).list();
    }
    
    public List<Order> findOrdersToShip() {
        return find("status = ?1 and shippingDate is null", Order.OrderStatus.CONFIRMED).list();
    }
    
    public Long countByStatus(Order.OrderStatus status) {
        return count("status", status);
    }
    
    public Long countByCustomerId(Long customerId) {
        return count("customer.id", customerId);
    }
    
    public List<Order> findOrdersWithItems() {
        return find("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.items IS NOT EMPTY").list();
    }
}