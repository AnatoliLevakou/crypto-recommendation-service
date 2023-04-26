package com.xm.recommendation.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides ability to split comma separated value from data db column
 */
@Converter
public class ConfigurationDataAttributeConverter implements AttributeConverter<List<String>, String> {
    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return String.join(SEPARATOR, attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(SEPARATOR)).map(String::trim).collect(Collectors.toList());
    }
}
