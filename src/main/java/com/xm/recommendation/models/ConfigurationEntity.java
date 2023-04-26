package com.xm.recommendation.models;

import com.xm.recommendation.converters.ConfigurationDataAttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigInteger;
import java.util.List;

@Entity(name = "configuration")
public class ConfigurationEntity {
    @Id
    private BigInteger configurationId;
    private String name;
    private String description;
    @Convert(converter = ConfigurationDataAttributeConverter.class)
    private List<String> data;

    public ConfigurationEntity() {
    }

    public BigInteger getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(BigInteger configurationId) {
        this.configurationId = configurationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
