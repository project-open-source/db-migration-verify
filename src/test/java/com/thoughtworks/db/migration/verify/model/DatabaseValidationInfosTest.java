package com.thoughtworks.db.migration.verify.model;

import com.thoughtworks.db.migration.verify.AbstractBaseTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseValidationInfosTest extends AbstractBaseTest {
    @Test
    void should_success_get_unvalidated_table_infos() {
        var sourceTableInfos = List.of(
                TableInfoBuilder.withDefault().tableName("source-validated").validated(true).build(),
                TableInfoBuilder.withDefault().tableName("source-unvalidated").validated(false).build()
        );

        var targetTableInfos = List.of(
                TableInfoBuilder.withDefault().tableName("target-validated").validated(true).build(),
                TableInfoBuilder.withDefault().tableName("target-unvalidated").validated(false).build()
        );
        var databaseValidationInfos = new DatabaseValidationInfos(null, null, sourceTableInfos, targetTableInfos, List.of(), List.of());

        assertEquals(1, databaseValidationInfos.getUnvalidatedSourceTableInfos().size());
        assertEquals(1, databaseValidationInfos.getUnvalidatedTargetTableInfos().size());
        assertEquals("source-unvalidated", databaseValidationInfos.getUnvalidatedSourceTableInfos().get(0).getTableName());
        assertEquals("target-unvalidated", databaseValidationInfos.getUnvalidatedTargetTableInfos().get(0).getTableName());
    }

    @Test
    void should_success_find_target_table_info_by_table_name() {
        var targetTableInfos = List.of(
                TableInfoBuilder.withDefault().tableName("target-table-1").build(),
                TableInfoBuilder.withDefault().tableName("target-table-2").build()
        );
        var databaseValidationInfos = new DatabaseValidationInfos(null, null, List.of(), targetTableInfos, List.of(), List.of());

        assertEquals(targetTableInfos.get(1), databaseValidationInfos.findTargetTableInfoBy("target-table-2"));
    }

    @Test
    void should_success_find_source_table_info_by_table_name() {
        var sourceTableInfos = List.of(
                TableInfoBuilder.withDefault().tableName("source-table-1").build(),
                TableInfoBuilder.withDefault().tableName("source-table-2").build()
        );
        var databaseValidationInfos = new DatabaseValidationInfos(null, null, sourceTableInfos, List.of(), List.of(), List.of());

        assertEquals(sourceTableInfos.get(1), databaseValidationInfos.findSourceTableInfoBy("source-table-2"));
    }

    @Test
    void should_return_false_if_not_equals_row_count() {
        var sourceTableInfos = List.of(
                TableInfoBuilder.withDefault().tableName("table-1").maxId("1").minId("1").build()
        );
        var targetTableInfos = List.of(
                TableInfoBuilder.withDefault().tableName("table-1").maxId(null).minId(null).build()
        );
        var databaseValidationInfos = new DatabaseValidationInfos(null, null, sourceTableInfos, targetTableInfos, List.of(), List.of());

        assertTrue(databaseValidationInfos.notEqualsRowCount("table-1"));
    }
}