package com.xm.recommendation.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CryptoRecord {
    private LocalDateTime timestamp;
    private String symbol;
    private BigDecimal price;

    public CryptoRecord() {
    }

    public CryptoRecord(LocalDateTime timestamp, String symbol, BigDecimal price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
