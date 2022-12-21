package com.thoughtworks.db.migration.verify.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "db-verify")
@Getter
@Setter
public class DataSourceProperties {
    private List<String> targetConfirmedVariables;
}
