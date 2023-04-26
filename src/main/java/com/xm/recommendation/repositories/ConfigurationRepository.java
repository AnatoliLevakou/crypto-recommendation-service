package com.xm.recommendation.repositories;

import com.xm.recommendation.models.ConfigurationEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository return configuration from DB.
 */
public interface ConfigurationRepository extends CrudRepository<ConfigurationEntity, Long> {

    ConfigurationEntity findByName(String name);

}
