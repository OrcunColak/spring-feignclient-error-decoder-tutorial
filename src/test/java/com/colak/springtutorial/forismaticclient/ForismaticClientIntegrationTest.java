package com.colak.springtutorial.forismaticclient;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@WireMockTest(httpPort = 8081)  // Specify the WireMock server port
@TestPropertySource(properties = "forismatic.api.url=http://localhost:8081")
public class ForismaticClientIntegrationTest {

    @Autowired
    private ForismaticClient forismaticClient;

    @Test
    public void testGetRandomQuote_Success() {
        // Mock the Forismatic API response
        WireMock.stubFor(WireMock.get(urlEqualTo("/api/1.0/?method=getQuote&format=json&lang=en"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"quoteText\":\"The best way to predict the future is to create it.\", \"quoteAuthor\":\"Peter Drucker\"}")));

        // Call the Feign client
        String quote = forismaticClient.getRandomQuote("getQuote", "json", "en");

        // Assert that the quote is returned and not null
        assertNotNull(quote);
    }

    @Test
    public void testGetRandomQuote_NotFound() {
        // Mock a 404 Not Found error response
        WireMock.stubFor(WireMock.get(urlEqualTo("/api/1.0/?method=getQuote&format=json&lang=en"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"error\":\"Some error message\"}")));

        // Verify that FeignErrorDecoder throws a ResponseStatusException with status 404
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            forismaticClient.getRandomQuote("getQuote", "json", "en");
        });

        // Assert that the exception status code is 404
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        // Assert that the exception message contains the expected error message
        assertEquals("Resource not found", exception.getReason());
    }

    @Test
    public void testGetRandomQuote_InternalServerError() {
        // Mock a 500 Internal Server Error response
        WireMock.stubFor(WireMock.get(urlEqualTo("/api/1.0/?method=getQuote&format=json&lang=en"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"error\":\"Some error message\"}")));

        // Verify that FeignErrorDecoder handles the 500 error
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            forismaticClient.getRandomQuote("getQuote", "json", "en");
        });

        // Assert that the exception status code is 500
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());

        // Assert that the exception message contains the expected error message
        assertEquals("Internal server error", exception.getReason());
    }
}
