package com.colak.springtutorial.controller;

import com.colak.springtutorial.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    // http://localhost:8080/quote
    @GetMapping("/quote")
    public String getQuote() {
        return quoteService.getQuote();
    }
}

