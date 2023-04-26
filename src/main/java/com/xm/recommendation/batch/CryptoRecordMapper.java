package com.xm.recommendation.batch;

import com.xm.recommendation.models.CryptoRecord;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Custom line mapper to convert date.
 */
@Component
public class CryptoRecordMapper  implements FieldSetMapper<CryptoRecord> {
    private static final int TIMESTAMP_COLUMN_INDEX = 0;
    private static final int SYMBOL_COLUMN_INDEX = 1;
    private static final int PRICE_COLUMN_INDEX = 2;

    @Override
    public CryptoRecord mapFieldSet(FieldSet fieldSet) throws BindException {
        CryptoRecord record = new CryptoRecord();
        record.setPrice(fieldSet.readBigDecimal(PRICE_COLUMN_INDEX));
        record.setSymbol(fieldSet.readString(SYMBOL_COLUMN_INDEX));
        record.setTimestamp(readTimestamp(fieldSet));
        return record;
    }

    private LocalDateTime readTimestamp(FieldSet fieldSet) {
        long timestamp = fieldSet.readLong(TIMESTAMP_COLUMN_INDEX);
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC.normalized());
    }
}
