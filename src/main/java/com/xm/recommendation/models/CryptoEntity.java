package com.xm.recommendation.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity(name = "crypto")
public class CryptoEntity {
    @Id
    private BigInteger cryptoId;
    private LocalDateTime timestamp;
    private String symbol;
    private BigDecimal price;

    public BigInteger getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(BigInteger cryptoId) {
        this.cryptoId = cryptoId;
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
