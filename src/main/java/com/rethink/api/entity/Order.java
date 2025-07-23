package com.rethink.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "customer_order")
public class Order extends PanacheEntity {
    
    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    public Customer customer;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    public OrderStatus status = OrderStatus.PENDING;
    
    @NotNull(message = "Total é obrigatório")
    @Positive(message = "Total deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal totalAmount;
    
    @Column(nullable = false)
    public LocalDateTime orderDate;
    
    @Column
    public LocalDateTime paymentDate;
    
    @Column
    public LocalDateTime shippingDate;
    
    @Column
    public LocalDateTime deliveryDate;
    
    @Column(length = 500)
    public String notes;
    
    @Column(length = 200)
    public String shippingAddress;
    
    @Column(length = 100)
    public String shippingCity;
    
    @Column(length = 2)
    public String shippingState;
    
    @Column(length = 9)
    public String shippingZipCode;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public List<OrderItem> items = new ArrayList<>();
    
    public Order() {
        this.orderDate = LocalDateTime.now();
    }
    
    public Order(Customer customer) {
        this();
        this.customer = customer;
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.order = this;
        recalculateTotal();
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        // Não definir item.order = null pois isso viola a validação
        // A exclusão do item será feita pelo OrderService
        recalculateTotal();
    }
    
    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(item -> item.unitPrice.multiply(BigDecimal.valueOf(item.quantity)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public enum OrderStatus {
        PENDING("Pendente"),
        CONFIRMED("Confirmado"),
        PROCESSING("Em Processamento"),
        SHIPPED("Enviado"),
        DELIVERED("Entregue"),
        CANCELLED("Cancelado");
        
        public final String description;
        
        OrderStatus(String description) {
            this.description = description;
        }
    }
}