package com.xm.recommendation.models;

import java.math.BigDecimal;

public class CryptoRecordDto {
    private String symbol;
    private BigDecimal oldestPrice;
    private BigDecimal newestPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getOldestPrice() {
        return oldestPrice;
    }

    public void setOldestPrice(BigDecimal oldestPrice) {
        this.oldestPrice = oldestPrice;
    }

    public BigDecimal getNewestPrice() {
        return newestPrice;
    }

    public void setNewestPrice(BigDecimal newestPrice) {
        this.newestPrice = newestPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }
}
