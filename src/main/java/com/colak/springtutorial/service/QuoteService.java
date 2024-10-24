package com.colak.springtutorial.service;

import com.colak.springtutorial.forismaticclient.ForismaticClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final ForismaticClient forismaticClient;

    public String getQuote() {
        return forismaticClient.getRandomQuote("getQuote", "json", "en");
    }
}

