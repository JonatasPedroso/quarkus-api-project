package com.rethink.api.service;

import com.rethink.api.entity.Customer;
import com.rethink.api.entity.Order;
import com.rethink.api.entity.OrderItem;
import com.rethink.api.entity.Product;
import com.rethink.api.repository.OrderItemRepository;
import com.rethink.api.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderService {
    
    @Inject
    OrderRepository orderRepository;
    
    @Inject
    OrderItemRepository orderItemRepository;
    
    @Inject
    CustomerService customerService;
    
    @Inject
    ProductService productService;
    
    public List<Order> listAll() {
        return orderRepository.listAll();
    }
    
    public Order findById(Long id) {
        return orderRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado com ID: " + id));
    }
    
    public List<Order> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> findRecentOrders(int limit) {
        return orderRepository.findRecentOrders(limit);
    }
    
    public List<Order> findPendingOrders() {
        return orderRepository.findPendingOrders();
    }
    
    @Transactional
    public Order create(Order order, List<OrderItem> items) {
        Customer customer = customerService.findById(order.customer.id);
        order.customer = customer;
        
        if (items == null || items.isEmpty()) {
            throw new BadRequestException("Pedido deve ter pelo menos um item");
        }
        
        order.items.clear();
        for (OrderItem item : items) {
            Product product = productService.findById(item.product.id);
            
            if (product.quantity < item.quantity) {
                throw new BadRequestException("Estoque insuficiente para produto: " + product.name);
            }
            
            OrderItem orderItem = new OrderItem(product, item.quantity, product.price);
            order.addItem(orderItem);
            
            product.quantity -= item.quantity;
        }
        
        if (order.shippingAddress == null) {
            order.shippingAddress = customer.address;
            order.shippingCity = customer.city;
            order.shippingState = customer.state;
            order.shippingZipCode = customer.zipCode;
        }
        
        orderRepository.persist(order);
        return order;
    }
    
    @Transactional
    public Order updateStatus(Long id, Order.OrderStatus newStatus) {
        Order order = findById(id);
        
        validateStatusTransition(order.status, newStatus);
        
        order.status = newStatus;
        
        switch (newStatus) {
            case CONFIRMED:
                order.paymentDate = LocalDateTime.now();
                break;
            case SHIPPED:
                order.shippingDate = LocalDateTime.now();
                break;
            case DELIVERED:
                order.deliveryDate = LocalDateTime.now();
                break;
            case CANCELLED:
                for (OrderItem item : order.items) {
                    item.product.quantity += item.quantity;
                }
                break;
        }
        
        return order;
    }
    
    @Transactional
    public Order addItem(Long orderId, Long productId, Integer quantity) {
        Order order = findById(orderId);
        
        if (order.status != Order.OrderStatus.PENDING) {
            throw new BadRequestException("Só é possível adicionar itens a pedidos pendentes");
        }
        
        Product product = productService.findById(productId);
        
        OrderItem existingItem = order.items.stream()
                .filter(item -> item.product.id.equals(productId))
                .findFirst()
                .orElse(null);
        
        if (existingItem != null) {
            int additionalQty = quantity;
            if (product.quantity < additionalQty) {
                throw new BadRequestException("Estoque insuficiente");
            }
            existingItem.quantity += additionalQty;
            existingItem.calculateSubtotal();
            product.quantity -= additionalQty;
        } else {
            if (product.quantity < quantity) {
                throw new BadRequestException("Estoque insuficiente");
            }
            OrderItem newItem = new OrderItem(product, quantity, product.price);
            order.addItem(newItem);
            product.quantity -= quantity;
        }
        
        order.recalculateTotal();
        return order;
    }
    
    @Transactional
    public Order removeItem(Long orderId, Long itemId) {
        Order order = findById(orderId);
        
        if (order.status != Order.OrderStatus.PENDING) {
            throw new BadRequestException("Só é possível remover itens de pedidos pendentes");
        }
        
        OrderItem item = order.items.stream()
                .filter(i -> i.id.equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item não encontrado no pedido"));
        
        item.product.quantity += item.quantity;
        
        order.removeItem(item);
        orderItemRepository.delete(item);
        
        if (order.items.isEmpty()) {
            throw new BadRequestException("Pedido não pode ficar sem itens");
        }
        
        return order;
    }
    
    @Transactional
    public void delete(Long id) {
        Order order = findById(id);
        
        if (order.status != Order.OrderStatus.PENDING && order.status != Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Só é possível excluir pedidos pendentes ou cancelados");
        }
        
        if (order.status == Order.OrderStatus.PENDING) {
            for (OrderItem item : order.items) {
                item.product.quantity += item.quantity;
            }
        }
        
        orderRepository.delete(order);
    }
    
    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus newStatus) {
        boolean valid = switch (current) {
            case PENDING -> newStatus == Order.OrderStatus.CONFIRMED || newStatus == Order.OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == Order.OrderStatus.PROCESSING || newStatus == Order.OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == Order.OrderStatus.SHIPPED || newStatus == Order.OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == Order.OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
        
        if (!valid) {
            throw new BadRequestException(
                String.format("Transição inválida de status: %s para %s", current, newStatus)
            );
        }
    }
    
    public long countOrders() {
        return orderRepository.count();
    }
    
    public long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
}