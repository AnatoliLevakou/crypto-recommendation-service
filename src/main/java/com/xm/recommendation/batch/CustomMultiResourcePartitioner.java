package com.xm.recommendation.batch;

import com.xm.recommendation.models.ConfigurationEntity;
import com.xm.recommendation.repositories.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomMultiResourcePartitioner extends MultiResourcePartitioner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMultiResourcePartitioner.class);
    private static final String SYMBOL_PATTERN = "^([^_]+)_";
    private static final String EXCLUSION_CONFIGURATION_KEY = "NOT_SUPPORTED_CRYPTO";

    private final Pattern pattern = Pattern.compile(SYMBOL_PATTERN);
    private final ConfigurationRepository configurationRepository;

    public CustomMultiResourcePartitioner(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void setResources(Resource[] resources) {
        super.setResources(this.filterResources(resources));
    }

    private Resource[] filterResources(Resource[] resources) {
        final ConfigurationEntity configuration = this.configurationRepository.findByName(EXCLUSION_CONFIGURATION_KEY);
        final List<Resource> result = new ArrayList<>();
        final List<String> exclusionList = configuration.getData();

        for (Resource resource : resources) {
            final Matcher matcher = pattern.matcher(resource.getFilename());
            if (matcher.find()) {
                final String symbol = matcher.group(1);
                if (!exclusionList.contains(symbol)) {
                    result.add(resource);
                } else {
                    LOGGER.debug("Symbol {} was excluded from import due to existing restrictions.", symbol);
                }
            }
        }

        return result.toArray(new Resource[0]);
    }
}
