package com.ftms.db.migration.verify;

import com.thoughtworks.db.migration.verify.utils.DatabaseConnectionFactory;
import com.thoughtworks.db.migration.verify.utils.DbParser;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DbParserTest extends AbstractBaseTest{
    @Test
    void should_success_parse_table_name() throws SQLException {
        try (Connection connection = DatabaseConnectionFactory.acquireConnectionBy(getSourceDatasourceProperties())) {
            sourceDatabase.withTable(TableBuilder.withDefault("table1").build());
            sourceDatabase.withTable(TableBuilder.withDefault("table2").build());

            var tableNameList = DbParser.parseTableNames(connection);

            assertEquals(2, tableNameList.size());
            assertTrue(tableNameList.contains("table1"));
            assertTrue(tableNameList.contains("table2"));
        }
    }

    @Test
    void should_success_parse_table_validation_infos() {
        var connection = DatabaseConnectionFactory.acquireConnectionBy(getSourceDatasourceProperties());
        sourceDatabase.withTable(TableBuilder.withDefault("table1").engine("MyISAM").collation("utf8mb4_bin").comment("comment").build())
                .execute("INSERT INTO table1 (name) VALUES ('test');");
        var tableInfos = DbParser.parseTableInfos(connection,"source-database");

        assertEquals(1, tableInfos.size());
        var currentTableInfo = tableInfos.get(0);
        assertEquals("table1", currentTableInfo.getTableName());
        assertEquals("id", currentTableInfo.getPrimaryKey());
        assertEquals("MyISAM", currentTableInfo.getEngine());
        assertEquals("utf8mb4_bin", currentTableInfo.getCollectionType());
        assertEquals("comment", currentTableInfo.getComment());
        assertEquals("1", currentTableInfo.getMaxId());
        assertEquals("1", currentTableInfo.getMinId());
        assertEquals(4, currentTableInfo.getColumns().size());
        var primaryKeyColumn = currentTableInfo.getColumns().get(0);
        assertEquals("id", primaryKeyColumn.getName());
        assertTrue(primaryKeyColumn.isPrimaryKey());
        var nameColumn = currentTableInfo.getColumns().get(1);
        assertTrue(nameColumn.isNotNull());
        assertEquals("name", nameColumn.getName());
    }

}