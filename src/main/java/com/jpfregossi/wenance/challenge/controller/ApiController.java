package com.jpfregossi.wenance.challenge.controller;

import com.jpfregossi.wenance.challenge.model.Response;
import com.jpfregossi.wenance.challenge.service.TimeseriesRepo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.sql.Timestamp;

@RestController
public class ApiController {
    @Autowired
    TimeseriesRepo repo;

    private final static Response errorResponse = new Response("error", 0D, "Parametros de entrada invalidos.");

    @GetMapping("/btc/{t1}")
    public Mono<Response> getQuote(@PathVariable String t1){
        long m = validateTimestamp(t1);

        if (m == 0) return Mono.just(errorResponse);
        else return repo.findQuoteByTimestamp(m);
    }

    @GetMapping("/btc/average/{t1}/{t2}")
    public Mono<Response[]> getAverage(@PathVariable("t1") String t1, @PathVariable("t2") String t2){
        long m1 = validateTimestamp(t1);
        long m2 = validateTimestamp(t2);

        if (m1 == 0 || m2 == 0) return Mono.just(new Response[]{errorResponse});
        return repo.findAverageByInterval( m1<m2 ? m1:m2 , m2>m1 ? m2:m1);
    }

    private static long validateTimestamp(String t){
        long l = 0;
        try {
            l = Timestamp.valueOf(t.replace('T',' ')).getTime();
        } catch (IllegalArgumentException e) {}

        return l;
    }
}
