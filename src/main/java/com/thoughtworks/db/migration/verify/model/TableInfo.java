package com.thoughtworks.db.migration.verify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {
    public static final String TYPE = "type";
    public static final String BIGINT = "bigint";
    public static final String INT = "int";
    private String tableName;
    private String primaryKey;
    private String collectionType;
    private String comment;
    private String engine;
    private List<ColumnInfo> columns;
    private String maxId;
    private String minId;
    private boolean validated;

    private Boolean primaryKeyAutoIncr;

    public static TableInfo generateBy(List<TableColumnSchema> tableColumnSchemas, TableSchema tableSchema) {
        var columns = tableColumnSchemas.stream().map(ColumnInfo::generateBy).collect(Collectors.toList());
        var primaryKeyColumnInfo = columns.stream().filter(ColumnInfo::isPrimaryKey).findFirst();
        var primaryKeyName = primaryKeyColumnInfo.map(ColumnInfo::getName).orElse(null);
        var primaryKeyAutoIncr = primaryKeyColumnInfo.map(ColumnInfo::isAutoIncrement).orElse(null);
        return TableInfo.builder()
                        .validated(false)
                        .tableName(tableSchema.getTableName())
                        .columns(columns)
                        .primaryKey(primaryKeyName)
                        .primaryKeyAutoIncr(primaryKeyAutoIncr)
                        .comment(tableSchema.getComment())
                        .engine(tableSchema.getEngine())
                        .collectionType(tableSchema.getCollation())
                        .build();
    }

    public void recordMaxId(String maxId) {
        this.maxId = maxId;
    }

    public void recordMinId(String minId) {
        this.minId = minId;
    }

    public void markValidated() {
        this.validated = true;
    }

    public boolean missingRows() {
        return Objects.isNull(minId) && Objects.isNull(maxId);
    }

    public ErrorMessages compare(TableInfo target) {
        var errorMessages = new ErrorMessages();
        if (ObjectUtils.notEqual(primaryKey, target.getPrimaryKey())) {
            errorMessages.add(String.format("[primaryKey] source -> %s, target -> %s", primaryKey, target.getPrimaryKey()));
        }
        if (ObjectUtils.notEqual(collectionType, target.getCollectionType())) {
            errorMessages.add(String.format("[collectionType] source -> %s, target -> %s", collectionType, target.getCollectionType()));
        }
        if (ObjectUtils.notEqual(comment, target.getComment())) {
            errorMessages.add(String.format("[comment] source -> %s, target -> %s", comment, target.getComment()));
        }
        if (ObjectUtils.notEqual(engine, target.getEngine())) {
            errorMessages.add(String.format("[engine] source -> %s, target -> %s", engine, target.getEngine()));
        }
        if (ObjectUtils.notEqual(maxId, target.getMaxId())) {
            errorMessages.add(String.format("[maxId] source -> %s, target -> %s", maxId, target.getMaxId()));
        }
        if (ObjectUtils.notEqual(minId, target.getMinId())) {
            errorMessages.add(String.format("[minId] source -> %s, target -> %s", minId, target.getMinId()));
        }
        errorMessages.addAll(compareColumns(target));
        return errorMessages;
    }

    private ErrorMessages compareColumns(TableInfo targets) {
        ErrorMessages errorMessages = new ErrorMessages();
        var sourceColumnInfoMap = generateColumnInfoMap();
        var targetColumnInfoMap = targets.generateColumnInfoMap();
        sourceColumnInfoMap.forEach((name, sourceInfo) -> {
            var targetInfo = targetColumnInfoMap.get(name);
            ErrorMessages columnFieldsErrorMessages = compareColumnFields(sourceInfo, targetInfo);
            errorMessages.addAll(columnFieldsErrorMessages);
        });
        return errorMessages;
    }

    private ErrorMessages compareColumnFields(ColumnInfo sourceInfo, ColumnInfo targetInfo) {
        ErrorMessages errorMessages = new ErrorMessages();
        var sourceColumnMap = sourceInfo.toMap();
        var targetColumnMap = targetInfo.toMap();
        sourceColumnMap.forEach((fieldName, fieldValue) -> {
            Object targetFieldValue = targetColumnMap.get(fieldName);
            if (TYPE.equals(fieldName) && isIntType(sourceInfo) && Objects.equals(sourceInfo.typeName(), targetInfo.typeName())) {
                return;
            }
            if (ObjectUtils.notEqual(fieldValue, targetFieldValue)) {
                errorMessages.add(String.format("[column -> %s -> %s] source -> %s, target -> %s"
                        , sourceInfo.getName(), fieldName, fieldValue, targetFieldValue));
            }
        });
        return errorMessages;
    }

    private boolean isIntType(ColumnInfo sourceInfo) {
        return BIGINT.equals(sourceInfo.typeName()) || INT.equals(sourceInfo.typeName());
    }

    private Map<String, ColumnInfo> generateColumnInfoMap() {
        return columns.stream().collect(Collectors.toMap(ColumnInfo::getName, Function.identity()));
    }
}
