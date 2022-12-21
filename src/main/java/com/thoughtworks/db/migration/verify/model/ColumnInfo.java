package com.thoughtworks.db.migration.verify.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ColumnInfo {
    public static final Pattern DIGITAL_PATTERN = Pattern.compile("\\((\\d+)\\)");
    private String name;
    private String type;
    private String comment;
    private boolean notNull;
    private boolean primaryKey;
    private String defaultValue;
    private boolean autoIncrement;


    public static ColumnInfo generateBy(TableColumnSchema tableColumnSchema) {
        return ColumnInfo.builder()
                         .name(tableColumnSchema.getField())
                         .type(tableColumnSchema.getType())
                         .notNull(tableColumnSchema.notNull())
                         .primaryKey(tableColumnSchema.isPrimaryKey())
                         .defaultValue(tableColumnSchema.getDefaultValue())
                         .autoIncrement(tableColumnSchema.isAutoIncrement())
                         .build();
    }

    public Map<String, Object> toMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(this, Map.class);
    }

    public String typeLength() {
        Matcher m = DIGITAL_PATTERN.matcher(type);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public String typeName() {
        return type.replaceAll("\\((\\d+)\\)", "");
    }
}
