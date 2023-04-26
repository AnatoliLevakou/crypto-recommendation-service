package com.xm.recommendation.repositories;

import com.xm.recommendation.models.CryptoEntity;
import com.xm.recommendation.models.CryptoRecordDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CryptoRepository extends CrudRepository<CryptoEntity, Long> {

    boolean existsBySymbol(String symbol);

    /**
     * Calculate set of values for given symbol.
     * min_value
     * max_value
     * oldest_price
     * newest_price
     * normalized_value
     * @return Collection of Dao objects.
     */
    @Query(nativeQuery = true, value = "SELECT *, (maxPrice - minPrice) / minPrice AS normRange\n" +
            "FROM (SELECT DISTINCT (symbol),\n" +
            "yearMonth,\n" +
            "first_value(price) over (PARTITION BY SYMBOL,yearMonth ORDER BY yearMonth DESC) as oldestPrice,\n" +
            "last_value(price) over (PARTITION BY SYMBOL,yearMonth ORDER BY yearMonth DESC) as newestPrice,\n" +
            "min(price) OVER (PARTITION BY SYMBOL,yearMonth ORDER BY yearMonth DESC) AS minPrice,\n" +
            "max(price) OVER (PARTITION BY SYMBOL,yearMonth ORDER BY yearMonth DESC) AS maxPrice\n" +
            "FROM (SELECT symbol,\n" +
            "price,\n" +
            "timestamp,\n" +
            "FORMATDATETIME(timestamp, 'yyMM') as yearMonth\n" +
            "FROM CRYPTO) withYearMonth\n" +
            "ORDER BY yearMonth DESC) partitioned\n" +
            "ORDER BY normRange DESC;")
    List<CryptoRecordDao> findNormalizedData();

    /**
     * Calculates the best Crypto for given symbol based normalization
     * @param symbol Desired symbol for calculation
     * @return Dao representation
     */
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

    /**
     * Calculates the best Crypto for given date based on normalized value.
     *
     * @param date Given date
     * @return Dao representation
     */
    @Query(nativeQuery = true, value = "SELECT *, (maxPrice - minPrice) / minPrice AS normRange\n" +
            "FROM (SELECT DISTINCT (symbol),\n" +
            "valueDate,\n" +
            "first_value(price) over (PARTITION BY SYMBOL,valueDate ORDER BY valueDate DESC) as oldestPrice,\n" +
            "last_value(price) over (PARTITION BY SYMBOL,valueDate ORDER BY valueDate DESC) as newestPrice,\n" +
            "min(price) OVER (PARTITION BY SYMBOL,valueDate ORDER BY valueDate DESC) AS minPrice,\n" +
            "max(price) OVER (PARTITION BY SYMBOL,valueDate ORDER BY valueDate DESC) AS maxPrice\n" +
            "FROM (SELECT symbol,\n" +
            "price,\n" +
            "timestamp,\n" +
            "FORMATDATETIME(timestamp, 'yyMMdd') as valueDate\n" +
            "FROM CRYPTO) withYearMonth\n" +
            "WHERE valueDate = :date) partitioned\n" +
            "ORDER BY normRange DESC\n" +
            "LIMIT 1;")
    CryptoRecordDao findBestCryptoForDate(String date);
}
