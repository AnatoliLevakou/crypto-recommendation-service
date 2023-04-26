package com.xm.recommendation.repositories;

import com.xm.recommendation.models.ConfigurationEntity;
import org.springframework.data.repository.CrudRepository;

public interface ConfigurationRepository extends CrudRepository<ConfigurationEntity, Long> {

    ConfigurationEntity findByName(String name);

}
