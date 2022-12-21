package com.thoughtworks.db.migration.verify.utils;

import com.thoughtworks.db.migration.verify.model.TableColumnSchema;
import com.thoughtworks.db.migration.verify.model.TableInfo;
import com.thoughtworks.db.migration.verify.model.TableSchema;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class DbParser {
    public DbParser() {
    }

    public static List<String> parseTableNames(Connection sourceConnection) {
        return SqlExecutor.fetchRows(sourceConnection, SqlGenerator.TABLE_QUERY);
    }

    public static List<TableInfo> parseTableInfos(Connection connection, String schema) {
        return parseTableNames(connection).stream().map(tableName -> {
            var columnSchemaList = SqlExecutor.fetchRows(connection, SqlGenerator.getDescCommandSql(tableName), TableColumnSchema.class);
            var tableSchema = SqlExecutor.fetchRow(connection, SqlGenerator.getInfomationSchemaSql(tableName, schema), TableSchema.class).orElseThrow();
            var validationInfo = TableInfo.generateBy(columnSchemaList, tableSchema);

            if (StringUtils.isNotBlank(validationInfo.getPrimaryKey())) {
                validationInfo.recordMaxId(SqlExecutor.fetchRow(connection, SqlGenerator.getQueryMaxIdSql(tableName, validationInfo.getPrimaryKey())).orElse(null));
                validationInfo.recordMinId(SqlExecutor.fetchRow(connection, SqlGenerator.getQueryMinIdSql(tableName, validationInfo.getPrimaryKey())).orElse(null));

            }
            return validationInfo;
        }).collect(Collectors.toList());
    }
}
