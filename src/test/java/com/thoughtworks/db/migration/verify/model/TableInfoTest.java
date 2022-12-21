package com.thoughtworks.db.migration.verify.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableInfoTest {
    @Test
    void should_success_skip_comparing_type_length_with_bigint() {
        ColumnInfo sourceColumnInfo = ColumnInfo.builder().type("bigint").build();
        ColumnInfo targetColumnInfo = ColumnInfo.builder().type("bigint(20)").build();
        TableInfo sourceTableInfo = TableInfo.builder().tableName("test_table").columns(List.of(sourceColumnInfo)).build();
        TableInfo targetTableInfo = TableInfo.builder().tableName("test_table").columns(List.of(targetColumnInfo)).build();

        ErrorMessages errorMessages = sourceTableInfo.compare(targetTableInfo);

        assertFalse(errorMessages.isNotEmpty());
    }

    @Test
    void should_success_comparing_type_length_with_char() {
        ColumnInfo sourceColumnInfo = ColumnInfo.builder().type("char(10)").build();
        ColumnInfo targetColumnInfo = ColumnInfo.builder().type("char(11)").build();
        TableInfo sourceTableInfo = TableInfo.builder().tableName("test_table").columns(List.of(sourceColumnInfo)).build();
        TableInfo targetTableInfo = TableInfo.builder().tableName("test_table").columns(List.of(targetColumnInfo)).build();

        ErrorMessages errorMessages = sourceTableInfo.compare(targetTableInfo);

        assertTrue(errorMessages.isNotEmpty());
        assertEquals("[column -> null -> type] source -> char(10), target -> char(11)",errorMessages.getValues().get(0));
    }
}