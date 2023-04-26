package com.xm.recommendation.models;

import java.math.BigDecimal;

public interface CryptoRecordDao {

    String getSymbol();

    BigDecimal getOldestPrice();

    BigDecimal getNewestPrice();

    BigDecimal getMinPrice();

    BigDecimal getMaxPrice();

    BigDecimal getNormRange();
}
