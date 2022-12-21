package com.thoughtworks.db.migration.verify.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlExecutor {
    public static <T> List<T> fetchRows(Connection connection, String staticSql, Class<T> expectResultType) {
        try (var sourceStatement = connection.createStatement()) {
            var resultSet = sourceStatement.executeQuery(staticSql);
            return getObjectJsonArray(resultSet).toJavaList(expectResultType);
        } catch (SQLException e) {
            log.info("execute sql failed: [sql -> {}, message -> {}]", staticSql, e.getMessage());
        }
        return Collections.emptyList();
    }

    public static <T> Optional<T> fetchRow(Connection connection, String staticSql, Class<T> expectResultType) {
        return fetchRows(connection, staticSql, expectResultType).stream().findFirst();
    }

    public static Optional<String> fetchRow(Connection connection, String staticSql) {
        return fetchRows(connection, staticSql).stream().findFirst();
    }

    public static List<String> fetchRows(Connection connection, String staticSql) {
        try (var sourceStatement = connection.createStatement()) {
            return getStringArray(sourceStatement.executeQuery(staticSql)).stream().filter(Objects::nonNull).collect(Collectors.toList());
        } catch (SQLException e) {
            log.info("execute sql failed: [sql -> {}, message -> {}]", staticSql, e.getMessage());
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    private static List<String> getStringArray(ResultSet resultSet) {
        var result = new ArrayList<String>();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        return result;
    }

    private static JSONArray getObjectJsonArray(ResultSet resultSet) throws SQLException {
        var metaData = resultSet.getMetaData();
        var columnCount = metaData.getColumnCount();
        var columnNames = parseColumnNames(metaData, columnCount);

        var jsonArrayResult = new JSONArray();
        while (resultSet.next()) {
            var row = new JSONObject();
            for (String columnName : columnNames) {
                row.put(columnName, resultSet.getObject(columnName));
            }
            jsonArrayResult.add(row);
        }
        return jsonArrayResult;
    }

    private static List<String> parseColumnNames(ResultSetMetaData metaData, int columnCount) throws SQLException {
        var columnNames = new ArrayList<String>(columnCount);
        for (var columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
            columnNames.add(metaData.getColumnName(columnIndex + 1));
        }
        return columnNames;
    }

}
