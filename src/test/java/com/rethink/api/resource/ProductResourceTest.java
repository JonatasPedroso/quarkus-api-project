package com.rethink.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProductResourceTest {
    
    @Test
    public void testListAllEndpoint() {
        given()
            .when().get("/products")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0));
    }
    
    @Test
    public void testGetByIdEndpoint() {
        given()
            .when().get("/products/1")
            .then()
            .statusCode(200)
            .body("name", is("Notebook Dell"))
            .body("price", is(3500.0f));
    }
    
    @Test
    public void testGetByIdNotFound() {
        given()
            .when().get("/products/999")
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testCreateEndpoint() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Produto Teste\",\"description\":\"Descrição teste\",\"price\":100.00,\"quantity\":5}")
            .when().post("/products")
            .then()
            .statusCode(201)
            .body("name", is("Produto Teste"))
            .body("price", is(100.0f))
            .body("quantity", is(5));
    }
    
    @Test
    public void testCreateWithInvalidData() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"\",\"price\":-10,\"quantity\":-5}")
            .when().post("/products")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testUpdateEndpoint() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Notebook Dell Atualizado\",\"description\":\"Nova descrição\",\"price\":3000.00,\"quantity\":8}")
            .when().put("/products/1")
            .then()
            .statusCode(200)
            .body("name", is("Notebook Dell Atualizado"))
            .body("price", is(3000.0f));
    }
    
    @Test
    public void testDeleteEndpoint() {
        String location = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Produto Para Deletar\",\"price\":50.00,\"quantity\":1}")
            .when().post("/products")
            .then()
            .statusCode(201)
            .extract().header("Location");
        
        String id = location.substring(location.lastIndexOf("/") + 1);
        
        given()
            .when().delete("/products/" + id)
            .then()
            .statusCode(204);
        
        given()
            .when().get("/products/" + id)
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testSearchEndpoint() {
        given()
            .queryParam("name", "Mouse")
            .when().get("/products/search")
            .then()
            .statusCode(200)
            .body("$.size()", is(1))
            .body("[0].name", containsString("Mouse"));
    }
    
    @Test
    public void testAvailableEndpoint() {
        given()
            .when().get("/products/available")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("[0].quantity", greaterThan(0));
    }
    
    @Test
    public void testCountEndpoint() {
        given()
            .when().get("/products/count")
            .then()
            .statusCode(200)
            .body("total", greaterThan(0))
            .body("available", greaterThan(0));
    }
}