package com.jpfregossi.wenance.challenge.service;

import com.jpfregossi.wenance.challenge.model.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class QuoteTaskTest {
    @InjectMocks
    QuoteTask qT;

    @Mock
    WebClient wc;
    @Mock
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    WebClient.ResponseSpec responseSpec;


    @BeforeEach
    void beforeEach(){
        qT = new QuoteTask();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void btcUsdQuote() {
        Quote q = new Quote("11111.5", "BTC", "USD");

        Mockito.when(wc.get())
                .thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.retrieve())
                .thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToFlux(Quote.class))
                .thenReturn(Flux.just(new Quote("11111.50", "BTC", "USD")));

        StepVerifier.create(qT.btcUsdQuote())
                .expectNextCount(0)
                .expectNextMatches( n -> n.getPrice() == 11111.5D )
                .expectNoEvent(Duration.ofSeconds(9))
                .expectNextMatches( n -> n.getPrice() == 11111.5D )
                .expectNoEvent(Duration.ofSeconds(9))
                .expectNextMatches( n -> n.getPrice() == 11111.5D )
                .thenCancel()
                .verify();
    }
}