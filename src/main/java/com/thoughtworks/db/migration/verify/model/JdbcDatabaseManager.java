package com.thoughtworks.db.migration.verify.model;

import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import com.thoughtworks.db.migration.verify.utils.DatabaseConnectionFactory;
import com.thoughtworks.db.migration.verify.utils.SqlExecutor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JdbcDatabaseManager implements DatabaseManager {
    @Override
    @SneakyThrows
    public <T> List<T> fetchRows(DatasourceRequest properties, String staticSql, Class<T> expectResultType) {
        try (var connection = DatabaseConnectionFactory.acquireConnectionBy(properties)) {
            return SqlExecutor.fetchRows(connection, staticSql, expectResultType);
        }
    }

    @Override
    @SneakyThrows
    public <T> Optional<T> fetchRow(DatasourceRequest properties, String staticSql, Class<T> expectResultType) {
        try (var connection = DatabaseConnectionFactory.acquireConnectionBy(properties)) {
            return SqlExecutor.fetchRow(connection, staticSql, expectResultType);
        }
    }

    @Override
    @SneakyThrows
    public Optional<String> fetchRow(DatasourceRequest properties, String staticSql) {
        try (var connection = DatabaseConnectionFactory.acquireConnectionBy(properties)) {
            return SqlExecutor.fetchRow(connection, staticSql);
        }
    }
}
