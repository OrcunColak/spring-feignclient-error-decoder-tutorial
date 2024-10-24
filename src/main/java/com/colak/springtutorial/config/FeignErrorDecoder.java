package com.colak.springtutorial.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        // Log the error details for debugging
        log.error("Feign Error in method {}: status = {}, reason = {}", methodKey, status, response.reason());

        // Custom handling based on the status code
        return switch (status) {
            case NOT_FOUND -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
            case BAD_REQUEST -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
            case INTERNAL_SERVER_ERROR ->
                    new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
            default -> new Exception("General error occurred during Feign client call: " + response.reason());
        };
    }
}

