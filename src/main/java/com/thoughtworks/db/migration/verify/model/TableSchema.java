package com.thoughtworks.db.migration.verify.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableSchema {
    @JsonAlias("TABLE_NAME")
    private String tableName;
    @JsonAlias("ENGINE")
    private String engine;
    @JsonAlias("TABLE_COLLATION")
    private String collation;
    @JsonAlias("TABLE_COMMENT")
    private String comment;
}
