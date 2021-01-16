package com.jpfregossi.wenance.challenge.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiControllerTest {
    @Autowired
    ApiController aC;

    @Autowired
    private WebTestClient wtc;

    @Test
    void getQuoteValid() {
        wtc.get().uri("/btc/2021-01-15T22:10:14")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("btcQuote");
    }

    @Test
    void getAverageValid() {
        wtc.get().uri("/btc/average/2021-01-15T22:10:14/2021-01-15T22:15:14")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].code").isEqualTo("avg")
                .jsonPath("$[1].code").isEqualTo("diff");
    }

    @Test
    void getQuoteInvalid() {
        wtc.get().uri("/btc/202122:10:14")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("error");
    }

    @Test
    void getAverageInvalid() {
        wtc.get().uri("/btc/average/2021011522:10:14/20210115T221514")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].code").isEqualTo("error");
    }
}