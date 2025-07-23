package com.rethink.api.resource;

import com.rethink.api.dto.CreateOrderRequest;
import com.rethink.api.entity.Customer;
import com.rethink.api.entity.Order;
import com.rethink.api.entity.OrderItem;
import com.rethink.api.entity.Product;
import com.rethink.api.service.OrderService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Orders", description = "Gerenciamento de pedidos")
public class OrderResource {
    
    @Inject
    OrderService orderService;
    
    @GET
    @Operation(summary = "Listar todos os pedidos", description = "Retorna uma lista com todos os pedidos")
    @APIResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    public List<Order> list() {
        return orderService.listAll();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico pelo seu ID")
    @APIResponse(responseCode = "200", description = "Pedido encontrado")
    @APIResponse(responseCode = "404", description = "Pedido não encontrado")
    public Order getById(@Parameter(description = "ID do pedido") @PathParam("id") Long id) {
        return orderService.findById(id);
    }
    
    @GET
    @Path("/customer/{customerId}")
    @Operation(summary = "Listar pedidos por cliente", description = "Retorna todos os pedidos de um cliente")
    @APIResponse(responseCode = "200", description = "Lista de pedidos do cliente")
    public List<Order> getByCustomer(
            @Parameter(description = "ID do cliente") @PathParam("customerId") Long customerId) {
        return orderService.findByCustomerId(customerId);
    }
    
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Listar pedidos por status", description = "Retorna pedidos com um status específico")
    @APIResponse(responseCode = "200", description = "Lista de pedidos com o status")
    public List<Order> getByStatus(
            @Parameter(description = "Status do pedido") @PathParam("status") Order.OrderStatus status) {
        return orderService.findByStatus(status);
    }
    
    @GET
    @Path("/recent")
    @Operation(summary = "Listar pedidos recentes", description = "Retorna os pedidos mais recentes")
    @APIResponse(responseCode = "200", description = "Lista de pedidos recentes")
    public List<Order> getRecent(
            @Parameter(description = "Número de pedidos a retornar") 
            @QueryParam("limit") @DefaultValue("10") int limit) {
        return orderService.findRecentOrders(limit);
    }
    
    @GET
    @Path("/pending")
    @Operation(summary = "Listar pedidos pendentes", description = "Retorna todos os pedidos pendentes")
    @APIResponse(responseCode = "200", description = "Lista de pedidos pendentes")
    public List<Order> getPending() {
        return orderService.findPendingOrders();
    }
    
    @POST
    @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido com os itens especificados")
    @APIResponse(responseCode = "201", description = "Pedido criado com sucesso")
    @APIResponse(responseCode = "400", description = "Dados inválidos ou estoque insuficiente")
    public Response create(@Valid CreateOrderRequest request) {
        Order order = new Order();
        order.customer = new Customer();
        order.customer.id = request.customerId;
        order.notes = request.notes;
        order.shippingAddress = request.shippingAddress;
        order.shippingCity = request.shippingCity;
        order.shippingState = request.shippingState;
        order.shippingZipCode = request.shippingZipCode;
        
        List<OrderItem> items = new ArrayList<>();
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.items) {
            OrderItem item = new OrderItem();
            item.product = new Product();
            item.product.id = itemRequest.productId;
            item.quantity = itemRequest.quantity;
            items.add(item);
        }
        
        Order created = orderService.create(order, items);
        return Response.created(URI.create("/api/orders/" + created.id)).entity(created).build();
    }
    
    @PUT
    @Path("/{id}/status")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido")
    @APIResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @APIResponse(responseCode = "400", description = "Transição de status inválida")
    @APIResponse(responseCode = "404", description = "Pedido não encontrado")
    public Order updateStatus(
            @Parameter(description = "ID do pedido") @PathParam("id") Long id,
            @Parameter(description = "Novo status") @QueryParam("status") Order.OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
    
    @POST
    @Path("/{id}/items")
    @Operation(summary = "Adicionar item ao pedido", description = "Adiciona um novo item a um pedido pendente")
    @APIResponse(responseCode = "200", description = "Item adicionado com sucesso")
    @APIResponse(responseCode = "400", description = "Pedido não está pendente ou estoque insuficiente")
    @APIResponse(responseCode = "404", description = "Pedido ou produto não encontrado")
    public Order addItem(
            @Parameter(description = "ID do pedido") @PathParam("id") Long id,
            @Parameter(description = "ID do produto") @QueryParam("productId") Long productId,
            @Parameter(description = "Quantidade") @QueryParam("quantity") Integer quantity) {
        return orderService.addItem(id, productId, quantity);
    }
    
    @DELETE
    @Path("/{orderId}/items/{itemId}")
    @Operation(summary = "Remover item do pedido", description = "Remove um item de um pedido pendente")
    @APIResponse(responseCode = "200", description = "Item removido com sucesso")
    @APIResponse(responseCode = "400", description = "Pedido não está pendente ou é o último item")
    @APIResponse(responseCode = "404", description = "Pedido ou item não encontrado")
    public Order removeItem(
            @Parameter(description = "ID do pedido") @PathParam("orderId") Long orderId,
            @Parameter(description = "ID do item") @PathParam("itemId") Long itemId) {
        return orderService.removeItem(orderId, itemId);
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Excluir pedido", description = "Exclui um pedido pendente ou cancelado")
    @APIResponse(responseCode = "204", description = "Pedido excluído com sucesso")
    @APIResponse(responseCode = "400", description = "Pedido não pode ser excluído")
    @APIResponse(responseCode = "404", description = "Pedido não encontrado")
    public Response delete(@Parameter(description = "ID do pedido") @PathParam("id") Long id) {
        orderService.delete(id);
        return Response.noContent().build();
    }
    
    @GET
    @Path("/count")
    @Operation(summary = "Contar pedidos", description = "Retorna estatísticas de pedidos")
    @APIResponse(responseCode = "200", description = "Contagens retornadas com sucesso")
    public Response count() {
        OrderStats stats = new OrderStats();
        stats.total = orderService.countOrders();
        stats.pending = orderService.countByStatus(Order.OrderStatus.PENDING);
        stats.confirmed = orderService.countByStatus(Order.OrderStatus.CONFIRMED);
        stats.processing = orderService.countByStatus(Order.OrderStatus.PROCESSING);
        stats.shipped = orderService.countByStatus(Order.OrderStatus.SHIPPED);
        stats.delivered = orderService.countByStatus(Order.OrderStatus.DELIVERED);
        stats.cancelled = orderService.countByStatus(Order.OrderStatus.CANCELLED);
        return Response.ok(stats).build();
    }
    
    public static class OrderStats {
        public long total;
        public long pending;
        public long confirmed;
        public long processing;
        public long shipped;
        public long delivered;
        public long cancelled;
    }
}