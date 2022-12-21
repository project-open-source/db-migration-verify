package com.thoughtworks.db.migration.verify.utils;

import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseConnectionFactory {
    @SneakyThrows
    public static Connection acquireConnectionBy(DatasourceRequest datasourceRequest) {
        return DriverManager.getConnection(datasourceRequest.getJdbcUrl(),
                datasourceRequest.getUsername(), datasourceRequest.getPassword());
    }
}
