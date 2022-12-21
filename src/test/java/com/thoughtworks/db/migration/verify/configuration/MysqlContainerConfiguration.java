package com.thoughtworks.db.migration.verify.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;

@Configuration
public class MysqlContainerConfiguration {
    @Bean(destroyMethod = "stop")
    @ConditionalOnProperty(
            value = "db-verify.custom-db-enable",
            havingValue = "false",
            matchIfMissing = true
    )
    public MySQLContainer<?> sourceMysqlContainer() {
        MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8").withDatabaseName("source-database");
        mysql.start();
        return mysql;
    }

    @Bean(destroyMethod = "stop")
    @ConditionalOnProperty(
            value = "db-verify.custom-db-enable",
            havingValue = "false",
            matchIfMissing = true
    )
    public MySQLContainer<?> targetMysqlContainer() {
        MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8").withDatabaseName("target-database");
        mysql.start();
        return mysql;
    }
}
