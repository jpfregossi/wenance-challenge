package com.jpfregossi.wenance.challenge.service;

import com.jpfregossi.wenance.challenge.model.Response;
import com.jpfregossi.wenance.challenge.model.TimeseriesQuote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;

@Component
public class TimeseriesRepo {
    private List<TimeseriesQuote> btcUsdSeries = new LinkedList<TimeseriesQuote>();

    @Autowired
    private QuoteTask qt;

    @PostConstruct
    public void postConstruct() {
        qt.btcUsdQuote().subscribe(this::insertBtcUsdQuote);
    }

    private void insertBtcUsdQuote(TimeseriesQuote t) {
        this.btcUsdSeries.add(t);
    }

    /*
     * Método para obtener el precio del bitcoin en cierto timestamp.
     */
    public Mono<Response> findQuoteByTimestamp(Long t){
        TimeseriesQuote q = btcUsdSeries.stream()
                .filter( quote -> quote.getTimestamp() >= t )
                .findFirst()
                .orElse(new TimeseriesQuote(0,0));

        Response r = new Response("btcQuote", q.getPrice(), "Cotización BTC en " + (new Timestamp(q.getTimestamp())));

        return Mono.just(r);
    }

    /*
     * Método para conocer el promedio de valor entre dos timestamps así como la diferencia porcentual
     * entre ese valor promedio y el valor máximo almacenado para toda la serie temporal disponible.
     */
    public Mono<Response[]> findAverageByInterval(Long t1, Long t2){
        double average = btcUsdSeries.stream()
                .filter( quote -> quote.getTimestamp() >= t1 && quote.getTimestamp() <= t2 )
                .mapToDouble( quote -> quote.getPrice() )
                .summaryStatistics()
                .getAverage();

        double max = btcUsdSeries.stream()
                .mapToDouble( quote -> quote.getPrice() )
                .max()
                .orElse(0);

        double difPerc = 100 * ((max - average) / max);

        Response[] r = new Response[2];
            r[0] = new Response("avg", average, "Promedio entre timestamps.");
            r[1] = new Response("diff", difPerc, "Diferencia porcentual con Máximo de toda la serie.");

        return Mono.just(r);
    }
}
