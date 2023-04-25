package com.xm.recommendation.repositories;

import com.xm.recommendation.models.CryptoEntity;
import com.xm.recommendation.models.CryptoRecordDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CryptoRepository extends CrudRepository<CryptoEntity, Long> {

    boolean existsBySymbol(String symbol);

    @Query(nativeQuery = true, value = "SELECT *, (maxPrice - minPrice) / minPrice AS normRange\n" +
            "FROM (SELECT DISTINCT (symbol),\n" +
            "yearMonth,\n" +
            "first_value(price) over (PARTITION BY yearMonth ORDER BY yearMonth DESC) as oldestPrice,\n" +
            "last_value(price) over (PARTITION BY yearMonth ORDER BY yearMonth DESC) as newestPrice,\n" +
            "min(price) OVER (PARTITION BY yearMonth ORDER BY yearMonth DESC) AS minPrice,\n" +
            "max(price) OVER (PARTITION BY yearMonth ORDER BY yearMonth DESC) AS maxPrice\n" +
            "FROM (SELECT symbol,\n" +
            "price,\n" +
            "timestamp,\n" +
            "FORMATDATETIME(timestamp, 'yyMM') as yearMonth\n" +
            "FROM CRYPTO\n" +
            "WHERE SYMBOL = :symbol) withYearMonth) partitioned\n" +
            "ORDER BY normRange DESC")
    CryptoRecordDao findDataForSymbol(String symbol);
}
