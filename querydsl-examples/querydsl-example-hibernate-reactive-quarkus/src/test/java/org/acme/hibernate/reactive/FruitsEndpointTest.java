package org.acme.hibernate.reactive;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FruitsEndpointTest {

  @Test
  public void testListAllFruits() {
    // List all, should have all 3 fruits the database has initially:
    given()
        .when()
        .get("/fruits/")
        .then()
        .statusCode(200)
        .body(containsString("Cherry"), containsString("Apple"), containsString("Banana"));

    // Update Cherry to Pineapple
    given()
        .when()
        .body("{\"name\" : \"Pineapple\"}")
        .contentType("application/json")
        .put("/fruits/1")
        .then()
        .statusCode(200)
        .body(containsString("\"id\":"), containsString("\"name\":\"Pineapple\""));

    // List all, Pineapple should've replaced Cherry:
    given()
        .when()
        .get("/fruits/")
        .then()
        .statusCode(200)
        .body(
            not(containsString("Cherry")),
            containsString("Pineapple"),
            containsString("Apple"),
            containsString("Banana"));

    // Delete Pineapple:
    given().when().delete("/fruits/1").then().statusCode(204);

    // List all, Pineapple should be missing now:
    given()
        .when()
        .get("/fruits/")
        .then()
        .statusCode(200)
        .body(not(containsString("Pineapple")), containsString("Apple"), containsString("Banana"));

    // Create the Pear:
    given()
        .when()
        .body("{\"name\" : \"Pear\"}")
        .contentType("application/json")
        .post("/fruits/")
        .then()
        .statusCode(201)
        .body(containsString("\"id\":"), containsString("\"name\":\"Pear\""));

    // List all, Pineapple should be still missing now:
    given()
        .when()
        .get("/fruits/")
        .then()
        .statusCode(200)
        .body(
            not(containsString("Pineapple")),
            containsString("Apple"),
            containsString("Banana"),
            containsString("Pear"));
  }
}
