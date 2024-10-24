package com.colak.springtutorial.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        if (response.body() == null) {
            return "No error message available.";
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder errorMessageBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorMessageBuilder.append(line);
            }
            return errorMessageBuilder.toString();
        } catch (IOException e) {
            return "Failed to read error message.";
        }
    }
}

