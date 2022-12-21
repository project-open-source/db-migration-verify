package com.thoughtworks.db.migration.verify.model;

import lombok.Setter;

import java.util.List;

@Setter
public class TableInfoBuilder {
    private String tableName;
    private String primaryKey;
    private String collectionType;
    private String comment;
    private String engine;
    private List<ColumnInfo> columns;
    private String maxId;
    private String minId;
    private boolean validated;

    public TableInfoBuilder() {
        this.tableName = "table_name";
        this.primaryKey = "id";
        this.collectionType = "utf8mb4";
        this.engine = "InnoDB";
        this.comment = "测试表";
        this.columns = List.of(
                new ColumnInfo("id", "bigint(20)", "记录ID", true, true, "", true),
                new ColumnInfo("name", "varchar(32)", "任务名称", true, false, "", false)
        );
        this.maxId = "maxId";
        this.minId = "minId";
        this.validated = false;
    }

    public static TableInfoBuilder withDefault() {
        return new TableInfoBuilder();
    }

    public TableInfo build() {
        return TableInfo.builder()
                        .tableName(tableName)
                        .primaryKey(primaryKey)
                        .collectionType(collectionType)
                        .comment(comment)
                        .engine(engine)
                        .columns(columns)
                        .maxId(maxId)
                        .minId(minId)
                        .validated(validated)
                        .build();
    }
}
