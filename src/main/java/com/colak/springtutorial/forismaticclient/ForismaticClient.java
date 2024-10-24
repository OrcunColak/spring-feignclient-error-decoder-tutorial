package com.colak.springtutorial.forismaticclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Define the Feign client for Forismatic API
@FeignClient(name = "forismaticClient", url = "${forismatic.api.url}")
public interface ForismaticClient {

    // Mapping for the getQuote method in Forismatic API
    @GetMapping(value = "/api/1.0/", produces = MediaType.APPLICATION_JSON_VALUE)
    String getRandomQuote(
            @RequestParam("method") String method,
            @RequestParam("format") String format,
            @RequestParam("lang") String lang
    );
}

