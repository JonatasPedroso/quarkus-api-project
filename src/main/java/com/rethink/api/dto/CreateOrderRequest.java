package com.rethink.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class CreateOrderRequest {
    
    @NotNull(message = "ID do cliente é obrigatório")
    public Long customerId;
    
    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    public List<OrderItemRequest> items;
    
    public String notes;
    public String shippingAddress;
    public String shippingCity;
    public String shippingState;
    public String shippingZipCode;
    
    public static class OrderItemRequest {
        @NotNull(message = "ID do produto é obrigatório")
        public Long productId;
        
        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        public Integer quantity;
    }
}