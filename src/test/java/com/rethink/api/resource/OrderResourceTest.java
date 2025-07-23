package com.rethink.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class OrderResourceTest {
    
    @Test
    public void testListAllEndpoint() {
        given()
            .when().get("/api/orders")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0));
    }
    
    @Test
    public void testGetByIdEndpoint() {
        given()
            .when().get("/api/orders/1")
            .then()
            .statusCode(200)
            .body("status", is("DELIVERED"))
            .body("totalAmount", is(4150.0f));
    }
    
    @Test
    public void testGetByIdNotFound() {
        given()
            .when().get("/api/orders/999")
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testGetByCustomerEndpoint() {
        given()
            .when().get("/api/orders/customer/1")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("[0].customer.id", is(1));
    }
    
    @Test
    public void testGetByStatusEndpoint() {
        given()
            .when().get("/api/orders/status/PENDING")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("[0].status", is("PENDING"));
    }
    
    @Test
    public void testGetRecentEndpoint() {
        given()
            .queryParam("limit", 3)
            .when().get("/api/orders/recent")
            .then()
            .statusCode(200)
            .body("$.size()", lessThanOrEqualTo(3));
    }
    
    @Test
    public void testGetPendingEndpoint() {
        given()
            .when().get("/api/orders/pending")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0));
    }
    
    @Test
    public void testCreateOrderEndpoint() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":1,\"items\":[{\"productId\":1,\"quantity\":1},{\"productId\":2,\"quantity\":2}],\"notes\":\"Teste de pedido\"}")
            .when().post("/api/orders")
            .then()
            .statusCode(201)
            .body("customer.id", is(1))
            .body("status", is("PENDING"))
            .body("items.size()", is(2));
    }
    
    @Test
    public void testCreateOrderWithInvalidData() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":null,\"items\":[]}")
            .when().post("/api/orders")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testUpdateStatusEndpoint() {
        // Criar um novo pedido para testar transição de status
        String location = given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":2,\"items\":[{\"productId\":3,\"quantity\":1}]}")
            .when().post("/api/orders")
            .then()
            .statusCode(201)
            .extract().header("Location");
        
        String id = location.substring(location.lastIndexOf("/") + 1);
        
        // Atualizar status para CONFIRMED
        given()
            .queryParam("status", "CONFIRMED")
            .when().put("/api/orders/" + id + "/status")
            .then()
            .statusCode(200)
            .body("status", is("CONFIRMED"));
    }
    
    @Test
    public void testInvalidStatusTransition() {
        // Tentar transição inválida (DELIVERED -> PENDING)
        given()
            .queryParam("status", "PENDING")
            .when().put("/api/orders/1/status")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testAddItemEndpoint() {
        // Usar o pedido 5 que está PENDING
        given()
            .queryParam("productId", 4)
            .queryParam("quantity", 1)
            .when().post("/api/orders/5/items")
            .then()
            .statusCode(200)
            .body("items.size()", greaterThan(1));
    }
    
    @Test
    public void testAddItemToNonPendingOrder() {
        // Tentar adicionar item a pedido não pendente
        given()
            .queryParam("productId", 1)
            .queryParam("quantity", 1)
            .when().post("/api/orders/1/items")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testRemoveItemEndpoint() {
        // Criar um pedido com múltiplos itens
        String location = given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":3,\"items\":[{\"productId\":5,\"quantity\":1},{\"productId\":6,\"quantity\":1}]}")
            .when().post("/api/orders")
            .then()
            .statusCode(201)
            .extract().header("Location");
        
        String orderId = location.substring(location.lastIndexOf("/") + 1);
        
        // Buscar o pedido para obter o ID do item
        Long itemId = given()
            .when().get("/api/orders/" + orderId)
            .then()
            .statusCode(200)
            .extract().path("items[0].id");
        
        // Remover um item
        given()
            .when().delete("/api/orders/" + orderId + "/items/" + itemId)
            .then()
            .statusCode(200)
            .body("items.size()", is(1));
    }
    
    @Test
    public void testDeleteEndpoint() {
        // Criar um pedido para deletar
        String location = given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":4,\"items\":[{\"productId\":7,\"quantity\":1}]}")
            .when().post("/api/orders")
            .then()
            .statusCode(201)
            .extract().header("Location");
        
        String id = location.substring(location.lastIndexOf("/") + 1);
        
        // Deletar o pedido (está PENDING, então pode ser deletado)
        given()
            .when().delete("/api/orders/" + id)
            .then()
            .statusCode(204);
        
        // Verificar que foi deletado
        given()
            .when().get("/api/orders/" + id)
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testDeleteNonPendingOrder() {
        // Tentar deletar pedido que não está PENDING
        given()
            .when().delete("/api/orders/1")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testCountEndpoint() {
        given()
            .when().get("/api/orders/count")
            .then()
            .statusCode(200)
            .body("total", greaterThan(0))
            .body("pending", greaterThanOrEqualTo(0))
            .body("delivered", greaterThanOrEqualTo(0));
    }
}