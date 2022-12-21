package com.thoughtworks.db.migration.verify.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ColumnInfoTest {
    @Test
    void should_success_convert_to_map() {
        ColumnInfo columnInfo = ColumnInfo.builder()
                                     .name("column_name")
                                     .autoIncrement(false)
                                     .defaultValue("default")
                                     .notNull(true)
                                     .primaryKey(false)
                                     .comment("comment")
                                     .type("char")
                                     .build();

        Map<String, Object> columnInfoMap = columnInfo.toMap();

        assertEquals(columnInfoMap.get("name"),"column_name");
        assertEquals(columnInfoMap.get("autoIncrement"),false);
        assertEquals(columnInfoMap.get("defaultValue"),"default");
        assertEquals(columnInfoMap.get("notNull"),true);
        assertEquals(columnInfoMap.get("comment"),"comment");
        assertEquals(columnInfoMap.get("type"),"char");
    }

    @Test
    void should_success_get_display_length() {
        ColumnInfo columnInfo = ColumnInfo.builder()
                                          .type("bigint(20)")
                                          .build();

        assertEquals("20", columnInfo.typeLength());
    }

    @Test
    void should_success_get_type_name() {
        ColumnInfo columnInfo = ColumnInfo.builder()
                                          .type("bigint(20)")
                                          .build();

        assertEquals("bigint", columnInfo.typeName());
    }
}