package com.thoughtworks.db.migration.verify;

import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import com.thoughtworks.db.migration.verify.utils.DatabaseConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.function.Supplier;

@AllArgsConstructor
@Slf4j
public class Database {
    @Getter
    private DatasourceRequest datasourceRequest;
    private Connection connection;

    public static Database init(DatasourceRequest properties) {
        return new Database(properties, DatabaseConnectionFactory.acquireConnectionBy(properties));
    }

    public void execute(String staticSql) {
        try (
                var statement = connection.createStatement()
        ) {
            statement.execute(staticSql);
        } catch (Exception exception) {
            log.error("execute error: {}", exception.getMessage());
        }
    }

    public Database withTable(TableBuilder.Table table) {
        try (
                var statement = connection.createStatement()
        ) {
            statement.execute(table.ddl());
        } catch (Exception exception) {
            log.error("execute error: {}", exception.getMessage());
        }
        return this;
    }

    public void insertRows(Supplier<String> insertSql, Integer insertCount) {
        for (int i = 0; i < insertCount; i++) {
            execute(insertSql.get());
        }
    }

    public void deleteRowById(String tableName, Integer id) {
        execute("DELETE FROM `" + tableName + "` WHERE id = " + id + ";");
    }

    public String getName() {
        return datasourceRequest.getDatabase();
    }
}
