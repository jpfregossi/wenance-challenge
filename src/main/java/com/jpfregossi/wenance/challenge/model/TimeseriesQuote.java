package com.jpfregossi.wenance.challenge.model;

public class TimeseriesQuote {
    final double price;
    final long timestamp;

    public TimeseriesQuote(final double price, final long timestamp) {
        this.price = price;
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
