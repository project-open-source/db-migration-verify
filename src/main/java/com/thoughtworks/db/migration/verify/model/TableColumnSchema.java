package com.thoughtworks.db.migration.verify.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnSchema {
    @JsonAlias("Field")
    private String field;
    @JsonAlias("Type")
    private String type;
    @JsonAlias("Null")
    private String nullable;
    @JsonAlias("Key")
    private String primaryKey;
    @JsonAlias("Default")
    private String defaultValue;
    @JsonAlias("Extra")
    private String extra;

    public boolean notNull() {
        return StringUtils.equals("NO", nullable);
    }

    public boolean isPrimaryKey() {
        return StringUtils.equals("PRI", primaryKey);
    }

    public boolean isAutoIncrement() {
        return StringUtils.equals("auto_increment", extra);
    }
}
