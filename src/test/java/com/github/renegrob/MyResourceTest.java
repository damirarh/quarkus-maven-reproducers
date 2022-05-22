package com.github.renegrob;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.renegrob.LogCorrelationIdAssertion.assertCorrelationIdLogged;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class MyResourceTest {

    private LogTestReader logReader;

    @BeforeEach
    void setUp() {
        logReader = new LogTestReader();
    }

    @Test
    public void testHelloEndpoint() {
        given()
                .header("X-Correlation-Id", "MY_CORRELATION_ID")
                .when().get("/v1/hello")

                .then()
                .statusCode(200)
                .body(is("Hello from RESTEasy Reactive"));
        assertCorrelationIdLogged(logReader);
    }

    @Test
    public void testBlockingEndpoint() {
        given()
                .when().get("/v1/worker-pool/blocking")
                .then()
                .statusCode(200);
        assertCorrelationIdLogged(logReader);
    }

    @Test
    public void testVertxBlocking() {
        given()
                .when().get("/v1/vertx/blocking")
                .then()
                .statusCode(200);
        assertCorrelationIdLogged(logReader);
    }

    @Test
    public void testVertxAnnotationBlocking() {
        given()
                .when().get("/v1/annotation/blocking")
                .then()
                .statusCode(200);
        assertCorrelationIdLogged(logReader);
    }
}
