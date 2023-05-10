package liveProject;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
public class ConsumerTest {
    //Headers

    Map<String, String> headers = new HashMap<>();

    //Resource Path
    String resourcePath = "/api/users";


    //Make a Contract
    @Pact(consumer = "UserConsumer", provider = "UserProvider")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        //Set the header
        headers.put("Content-Type","application/json");

        //Set the Request and Response body
        DslPart requestResponseBody = new PactDslJsonBody()
                .numberType("id",123)
                .stringType("firstName","Jyoti")
                .stringType("lastName","Ghosh")
                .stringType("email","jyoti@example.com");

        return builder.given("create user request")
                .uponReceiving("to create a user")
                .method("POST")
                .headers(headers)
                .path(resourcePath)
                .body(requestResponseBody)
                .willRespondWith()
                .status(201)
                .body(requestResponseBody)
                .toPact();
    }


    //Test with Provider Mock
    @Test
    @PactTestFor(providerName = "UserProvider", port = "8282")
    public void consumerTest() {
        // Mock url
        RestAssured.baseURI = "http://localhost:8282";
        // Create request specification
        RequestSpecification rq = RestAssured.given().headers(headers).when();

        // Create request body
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 123);
        map.put("firstName", "Jyoti");
        map.put("lastName", "Ghosh");
        map.put("email", "jyoti@example.com");

        // Send POST request
        Response response = rq.body(map).post(resourcePath);
        // Assertion
        assert (response.getStatusCode() == 201);


    }
}
