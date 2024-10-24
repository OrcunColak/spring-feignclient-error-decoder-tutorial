package com.colak.springtutorial.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = getErrorMessage(response);
        return switch (response.status()) {
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
            case 500 -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String getErrorMessage(Response response) {
        Response.Body body = response.body();
        if (body == null) {
            return "No error message available.";
        }

        try {
            // Use Spring's StreamUtils to read the input stream
            return StreamUtils.copyToString(body.asInputStream(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "Failed to read error message.";
        }
    }
}

