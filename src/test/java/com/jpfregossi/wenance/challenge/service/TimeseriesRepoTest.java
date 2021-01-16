package com.jpfregossi.wenance.challenge.service;

import com.jpfregossi.wenance.challenge.model.TimeseriesQuote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TimeseriesRepoTest {
    @InjectMocks
    TimeseriesRepo tR;

    @Mock
    private QuoteTask qt;

    List<TimeseriesQuote> ts;
    Random r = new Random();

    @BeforeEach
    void beforeEach(){
        tR = new TimeseriesRepo();
        ts = new LinkedList<TimeseriesQuote>();
        ts.add(new TimeseriesQuote(randQuote(), 1L));
        ts.add(new TimeseriesQuote(randQuote(), 2L));
        ts.add(new TimeseriesQuote(randQuote(), 3L));
        ts.add(new TimeseriesQuote(randQuote(), 4L));
        ts.add(new TimeseriesQuote(randQuote(), 5L));
        ts.add(new TimeseriesQuote(randQuote(), 6L));
        ts.add(new TimeseriesQuote(randQuote(), 7L));
        ts.add(new TimeseriesQuote(randQuote(), 8L));
        ts.add(new TimeseriesQuote(randQuote(), 9L));
        ts.add(new TimeseriesQuote(randQuote(), 10L));

        Mockito.when(qt.btcUsdQuote()).thenReturn(Flux.fromIterable(ts));
        tR.postConstruct();
    }

    @Test
    void findQuoteByTimestamp() {
        int indice = r.nextInt(9);
        StepVerifier.create(tR.findQuoteByTimestamp((long)indice+1))
                .expectNextMatches( r -> r.getValue() == ts.get(indice).getPrice())
                .verifyComplete();
    }

    @Test
    void findAverageByInterval() {
        int t1 = r.nextInt(4);
        int t2 = t1 + r.nextInt(9);

        double max = 0;
        for ( int i=0 ; i < 10 ; i++ ){
            if (ts.get(i).getPrice() > max) max = ts.get(i).getPrice();
        }

        double suma = 0;
        int cantidad = t2 - t1 + 1;
        for ( int i=t1 ; i <= t2 ; i++ ){
            suma += ts.get(i).getPrice();
        }

        double promedio = suma/cantidad;
        double diferencia = ((max-promedio)*100)/max;

        StepVerifier.create(tR.findAverageByInterval((long)t1+1,(long)t2+1))
                .expectNextMatches( r ->
                        Math.round(r[0].getValue()*100) == Math.round(promedio*100)
                                && Math.round(r[1].getValue()*100) == Math.round(diferencia*100) )
                .verifyComplete();
    }

    double randQuote(){
        int min = 360000;
        int max = 370000;
        Random r = new Random();
        return (double)(r.nextInt(max - min) + min)/10 ;
    }
}