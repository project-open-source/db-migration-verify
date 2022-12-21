package com.thoughtworks.db.migration.verify.model;

import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import com.thoughtworks.db.migration.verify.utils.DatabaseConnectionFactory;
import com.thoughtworks.db.migration.verify.utils.DbParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@AllArgsConstructor
public class DatabaseValidationInfos {
    private final DatasourceRequest sourceProperties;
    private final DatasourceRequest targetProperties;
    private final List<TableInfo> sourceTableInfos;
    private final List<TableInfo> targetTableInfos;
    private final List<String> sourceTableNames;
    private final List<String> targetTableNames;

    public DatabaseValidationInfos(DatasourceRequest sourceProperties, DatasourceRequest targetProperties) throws SQLException {
        this.sourceProperties = sourceProperties;
        this.targetProperties = targetProperties;

        try (var sourceConnection = DatabaseConnectionFactory.acquireConnectionBy(sourceProperties);
             var targetConnection = DatabaseConnectionFactory.acquireConnectionBy(targetProperties)
        ) {
            this.sourceTableInfos = DbParser.parseTableInfos(sourceConnection,sourceProperties.getDatabase());
            this.targetTableInfos = DbParser.parseTableInfos(targetConnection,targetProperties.getDatabase());
            this.sourceTableNames = this.sourceTableInfos.stream().map(TableInfo::getTableName).collect(Collectors.toList());
            this.targetTableNames = this.targetTableInfos.stream().map(TableInfo::getTableName).collect(Collectors.toList());

            log.info("successfully parse source & target table info.");
        }
    }

    @SneakyThrows
    public static DatabaseValidationInfos generateBy(DatasourceRequest sourceDatasourceRequest, DatasourceRequest targetDatasourceRequest) {
        return new DatabaseValidationInfos(sourceDatasourceRequest, targetDatasourceRequest);
    }

    public Connection getTargetConnection() {
        return DatabaseConnectionFactory.acquireConnectionBy(targetProperties);
    }

    public Connection getSourceConnection() {
        return DatabaseConnectionFactory.acquireConnectionBy(sourceProperties);
    }

    public List<TableInfo> getUnvalidatedSourceTableInfos() {
        return this.sourceTableInfos.stream().filter(tableInfo -> !tableInfo.isValidated()).collect(Collectors.toList());
    }

    public List<TableInfo> getUnvalidatedTargetTableInfos() {
        return this.targetTableInfos.stream().filter(tableInfo -> !tableInfo.isValidated()).collect(Collectors.toList());
    }

    public TableInfo findTargetTableInfoBy(String tableName) {
        return targetTableInfos.stream().filter(tableInfo -> StringUtils.equals(tableName, tableInfo.getTableName())).findFirst().orElseThrow();
    }

    public boolean notEqualsRowCount(String tableName) {
        var targetTableInfo = findTargetTableInfoBy(tableName);
        var sourceTableInfo = findSourceTableInfoBy(tableName);
        return ObjectUtils.notEqual(targetTableInfo.getMaxId(), sourceTableInfo.getMaxId()) || ObjectUtils.notEqual(targetTableInfo.getMinId(), sourceTableInfo.getMinId());
    }

    public TableInfo findSourceTableInfoBy(String tableName) {
        return sourceTableInfos.stream().filter(tableInfo -> StringUtils.equals(tableName, tableInfo.getTableName())).findFirst().orElseThrow();
    }
}
