package com.rethink.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import io.restassured.response.Response;

@QuarkusTest
public class OrderResourceTest {
    
    @Test
    public void testListAllEndpoint() {
        given()
            .when().get("/orders")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0));
    }
    
    @Test
    public void testGetByIdEndpoint() {
        given()
            .when().get("/orders/1")
            .then()
            .statusCode(200)
            .body("status", is("DELIVERED"))
            .body("totalAmount", is(4150.0f));
    }
    
    @Test
    public void testGetByIdNotFound() {
        given()
            .when().get("/orders/999")
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testGetByCustomerEndpoint() {
        given()
            .when().get("/orders/customer/1")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("[0].customer.id", is(1));
    }
    
    @Test
    public void testGetByStatusEndpoint() {
        given()
            .when().get("/orders/status/PENDING")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("[0].status", is("PENDING"));
    }
    
    @Test
    public void testGetRecentEndpoint() {
        given()
            .queryParam("limit", 3)
            .when().get("/orders/recent")
            .then()
            .statusCode(200)
            .body("$.size()", lessThanOrEqualTo(3));
    }
    
    @Test
    public void testGetPendingEndpoint() {
        given()
            .when().get("/orders/pending")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0));
    }
    
    @Test
    public void testCreateOrderEndpoint() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":1,\"items\":[{\"productId\":1,\"quantity\":1},{\"productId\":2,\"quantity\":2}],\"notes\":\"Teste de pedido\"}")
            .when().post("/orders")
            .then()
            .statusCode(201)
            .body("customer.id", is(1))
            .body("status", is("PENDING"))
            .body("totalAmount", notNullValue());
    }
    
    @Test
    public void testCreateOrderWithInvalidData() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":null,\"items\":[]}")
            .when().post("/orders")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testInvalidStatusTransition() {
        given()
            .queryParam("status", "PENDING")
            .when().put("/orders/1/status")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testAddItemToNonPendingOrder() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("productId", 1)
            .queryParam("quantity", 1)
            .when().post("/orders/1/items")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testDeleteEndpoint() {
        String location = given()
            .contentType(ContentType.JSON)
            .body("{\"customerId\":4,\"items\":[{\"productId\":7,\"quantity\":1}]}")
            .when().post("/orders")
            .then()
            .statusCode(201)
            .extract().header("Location");
        
        String id = location.substring(location.lastIndexOf("/") + 1);
        
        given()
            .when().delete("/orders/" + id)
            .then()
            .statusCode(204);
        
        given()
            .when().get("/orders/" + id)
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testDeleteNonPendingOrder() {
        given()
            .when().delete("/orders/1")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testCountEndpoint() {
        given()
            .when().get("/orders/count")
            .then()
            .statusCode(200)
            .body("total", greaterThan(0))
            .body("pending", greaterThanOrEqualTo(0))
            .body("delivered", greaterThanOrEqualTo(0));
    }
}