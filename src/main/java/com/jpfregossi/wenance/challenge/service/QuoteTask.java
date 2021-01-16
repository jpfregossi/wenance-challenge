package com.jpfregossi.wenance.challenge.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpfregossi.wenance.challenge.model.Quote;
import com.jpfregossi.wenance.challenge.model.TimeseriesQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Duration;

@Component
public class QuoteTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteTask.class);
    private WebClient wc;

    private static final String URLBTCUSD = "https://cex.io/api/last_price/BTC/USD";

    public QuoteTask() {
        this.wc = WebClient.builder()
                .baseUrl(URLBTCUSD)
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer ->{
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    configurer.customCodecs().decoder(new Jackson2JsonDecoder(mapper, MimeTypeUtils.parseMimeType("text/json")));
                }).build())
                .build();
    }

    public Flux<TimeseriesQuote> btcUsdQuote() {
        return getQoute()
                .map( this::toTimeseries )
                .map( m -> {
                    LOGGER.info("BTC/USD - " + (new Timestamp(m.getTimestamp())) + ": " + m.getPrice() );
                    return m;})
                .repeatWhen( l -> Flux.interval(Duration.ofSeconds(10)) );
    }

    public Flux<Quote> getQoute() {
        return wc
                .get()
                .retrieve()
                .bodyToFlux(Quote.class)
                .timeout(Duration.ofSeconds(9), Mono.empty());
    }

    public TimeseriesQuote toTimeseries(Quote q) {
        return new TimeseriesQuote(Double.parseDouble(q.getLprice()), System.currentTimeMillis());
    }
}
