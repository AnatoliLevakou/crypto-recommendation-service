package com.xm.recommendation.converters;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ConfigurationDataAttributeConverterTest {

    @InjectMocks
    private ConfigurationDataAttributeConverter configurationDataAttributeConverter;

    @Test
    void whenCommaSeparatedValuesInDb_expectedArrayWillBeReturned() {
        // given
        final String dbValues = "ABC, QWE,XYZ";

        // when
        final List<String> converted = this.configurationDataAttributeConverter.convertToEntityAttribute(dbValues);

        // then
        assertThat(converted).contains("ABC", "QWE", "XYZ");
    }

    @Test
    void whenListOfValues_expectedConcatenatedStringGenerated() {
        // given
        final List<String> entityValues = Lists.newArrayList("ABC", "QWE", "XYZ");

        // when
        final String converted = this.configurationDataAttributeConverter.convertToDatabaseColumn(entityValues);

        // then
        assertThat(converted).isEqualTo("ABC,QWE,XYZ");
    }
}
