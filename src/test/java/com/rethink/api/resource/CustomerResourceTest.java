package com.rethink.api.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CustomerResourceTest {
    
    @Test
    public void testListAllEndpoint() {
        given()
            .when().get("/customers")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0));
    }
    
    @Test
    public void testGetByIdEndpoint() {
        given()
            .when().get("/customers/1")
            .then()
            .statusCode(200)
            .body("name", is("João Silva"))
            .body("email", is("joao.silva@email.com"));
    }
    
    @Test
    public void testGetByIdNotFound() {
        given()
            .when().get("/customers/999")
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testGetByEmailEndpoint() {
        given()
            .when().get("/customers/email/maria.santos@email.com")
            .then()
            .statusCode(200)
            .body("name", is("Maria Santos"));
    }
    
    @Test
    public void testGetByCpfEndpoint() {
        given()
            .when().get("/customers/cpf/123.456.789-00")
            .then()
            .statusCode(200)
            .body("name", is("João Silva"));
    }
    
    @Test
    public void testSearchEndpoint() {
        given()
            .queryParam("name", "Silva")
            .when().get("/customers/search")
            .then()
            .statusCode(200)
            .body("$.size()", is(1))
            .body("[0].name", containsString("Silva"));
    }
    
    @Test
    public void testGetByCityEndpoint() {
        given()
            .when().get("/customers/city/São Paulo")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("[0].city", is("São Paulo"));
    }
    
    @Test
    public void testGetByStateEndpoint() {
        given()
            .when().get("/customers/state/SP")
            .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("[0].state", is("SP"));
    }
    
    @Test
    public void testGetRecentEndpoint() {
        given()
            .queryParam("limit", 3)
            .when().get("/customers/recent")
            .then()
            .statusCode(200)
            .body("$.size()", lessThanOrEqualTo(3));
    }
    
    @Test
    public void testCreateEndpoint() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Teste Cliente\",\"email\":\"teste@email.com\",\"phone\":\"(11) 99999-9999\",\"cpf\":\"111.222.333-44\",\"address\":\"Rua Teste, 123\",\"city\":\"São Paulo\",\"state\":\"SP\",\"zipCode\":\"12345-678\"}")
            .when().post("/customers")
            .then()
            .statusCode(201)
            .body("name", is("Teste Cliente"))
            .body("email", is("teste@email.com"));
    }
    
    @Test
    public void testCreateWithInvalidData() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"\",\"email\":\"invalidemail\",\"phone\":\"\",\"cpf\":\"\"}")
            .when().post("/customers")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testCreateWithDuplicateEmail() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Outro Nome\",\"email\":\"joao.silva@email.com\",\"phone\":\"(11) 88888-8888\",\"cpf\":\"999.888.777-66\"}")
            .when().post("/customers")
            .then()
            .statusCode(400);
    }
    
    @Test
    public void testUpdateEndpoint() {
        String location = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Cliente Update\",\"email\":\"update@email.com\",\"phone\":\"(11) 77777-7777\",\"cpf\":\"555.666.777-88\"}")
            .when().post("/customers")
            .then()
            .statusCode(201)
            .extract().header("Location");
        
        String id = location.substring(location.lastIndexOf("/") + 1);
        
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Cliente Atualizado\",\"email\":\"update@email.com\",\"phone\":\"(11) 77777-7777\",\"cpf\":\"555.666.777-88\",\"address\":\"Nova Rua\"}")
            .when().put("/customers/" + id)
            .then()
            .statusCode(200)
            .body("name", is("Cliente Atualizado"))
            .body("address", is("Nova Rua"));
    }
    
    @Test
    public void testDeleteEndpoint() {
        String location = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Cliente Delete\",\"email\":\"delete@email.com\",\"phone\":\"(11) 66666-6666\",\"cpf\":\"444.555.666-77\"}")
            .when().post("/customers")
            .then()
            .statusCode(201)
            .extract().header("Location");
        
        String id = location.substring(location.lastIndexOf("/") + 1);
        
        given()
            .when().delete("/customers/" + id)
            .then()
            .statusCode(204);
        
        given()
            .when().get("/customers/" + id)
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testCountEndpoint() {
        given()
            .when().get("/customers/count")
            .then()
            .statusCode(200)
            .body("total", greaterThan(0));
    }
}