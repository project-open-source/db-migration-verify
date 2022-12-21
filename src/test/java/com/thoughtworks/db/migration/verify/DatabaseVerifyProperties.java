package com.thoughtworks.db.migration.verify;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConditionalOnProperty(
        value = "db-verify.custom-db-enable",
        havingValue = "true"
)
public class DatabaseVerifyProperties {
    @Value("${db-verify.custom-db-enable}")
    private boolean customDbEnable;
    @Value("${db-verify.host}")
    private String host;
    @Value("${db-verify.port}")
    private int port;
    @Value("${db-verify.source-database}")
    private String sourceDatabase;
    @Value("${db-verify.target-database}")
    private String targetDatabase;
    @Value("${db-verify.username}")
    private String username;
    @Value("${db-verify.password}")
    private String password;
}
