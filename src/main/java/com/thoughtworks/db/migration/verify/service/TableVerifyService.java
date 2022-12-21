package com.thoughtworks.db.migration.verify.service;

import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import com.thoughtworks.db.migration.verify.model.DatabaseValidationInfos;
import com.thoughtworks.db.migration.verify.model.ErrorMessages;
import com.thoughtworks.db.migration.verify.model.TableInfo;
import com.thoughtworks.db.migration.verify.model.TableValidationResult;
import com.thoughtworks.db.migration.verify.utils.CsvReportFactory;
import com.thoughtworks.db.migration.verify.utils.SqlExecutor;
import com.thoughtworks.db.migration.verify.utils.SqlGenerator;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableVerifyService {
    @SneakyThrows
    @Async
    public void verify(VerifyDatabaseRequest verifyDatabaseRequest, String executionId) {
        log.info("start to verify.");
        var databaseValidationInfos = DatabaseValidationInfos.generateBy(verifyDatabaseRequest.getSourceDatasource(), verifyDatabaseRequest.getTargetDatasource());
        var tableValidationResults = verifyTables(databaseValidationInfos);
        convertToReport(tableValidationResults, verifyDatabaseRequest, executionId);
    }


    private List<TableValidationResult> verifyTables(DatabaseValidationInfos databaseValidationInfos) {
        var targetMissingTables = compareDiffTableWith(databaseValidationInfos.getSourceTableInfos(), databaseValidationInfos.getTargetTableNames(), "target database not exist this table.");
        var sourceMissingTables = compareDiffTableWith(databaseValidationInfos.getTargetTableInfos(), databaseValidationInfos.getSourceTableNames(), "source database not exist this table.");

        var tableValidationResults = new ArrayList<TableValidationResult>();
        tableValidationResults.addAll(targetMissingTables);
        tableValidationResults.addAll(sourceMissingTables);

        databaseValidationInfos.getUnvalidatedSourceTableInfos()
                               .forEach(sourceTableInfo -> {
                                   log.info("comparing table {}", sourceTableInfo.getTableName());

                                   var targetTableInfo = databaseValidationInfos.findTargetTableInfoBy(sourceTableInfo.getTableName());
                                   var validationResult = compareTableStructureBy(sourceTableInfo, targetTableInfo);
                                   var errorMessages = compareTableRowsBy(databaseValidationInfos, sourceTableInfo.getTableName());

                                   if (errorMessages.isNotEmpty()) {
                                       validationResult.addErrorMessage(errorMessages);
                                   }

                                   tableValidationResults.add(validationResult);
                                   log.info("successfully compare table {}", sourceTableInfo.getTableName());
                               });

        return tableValidationResults;
    }

    private ErrorMessages compareTableRowsBy(DatabaseValidationInfos databaseValidationInfos, String tableName) {
        var errorMessages = new ErrorMessages();
        var tableInfo = databaseValidationInfos.findTargetTableInfoBy(tableName);
        var primaryKey = tableInfo.getPrimaryKey();
        var primaryKeyAutoIncr = tableInfo.getPrimaryKeyAutoIncr();

        if (Objects.isNull(primaryKey)) {
            errorMessages.add("table has no primary key");
            return errorMessages;
        }
        if (!primaryKeyAutoIncr) {
            errorMessages.add("table primary key is not auto increment");
            return errorMessages;
        }

        if (tableInfo.missingRows() || databaseValidationInfos.notEqualsRowCount(tableName)) {
            return errorMessages;
        }


        try (
                var sourceConnection = databaseValidationInfos.getSourceConnection();
                var targetConnection = databaseValidationInfos.getTargetConnection()
        ) {
            var maxId = Integer.parseInt(tableInfo.getMaxId());
            var minId = Integer.parseInt(tableInfo.getMinId());
            var rowCount = maxId - minId + 1;

            ThreadLocalRandom.current()
                             .ints(minId, maxId)
                             .limit(Math.min(rowCount, 100))
                             .forEach(randomId -> {
                                 var id = String.valueOf(randomId);
                                 var sourceRow = SqlExecutor.fetchRow(sourceConnection, SqlGenerator.getRowWithPrimaryKey(tableName, primaryKey, id), JSONObject.class);
                                 var targetRow = SqlExecutor.fetchRow(targetConnection, SqlGenerator.getRowWithPrimaryKey(tableName, primaryKey, id), JSONObject.class);

                                 if (ObjectUtils.notEqual(sourceRow.orElse(null), targetRow.orElse(null))) {
                                     errorMessages.add(format("[ROW DIFF] id -> %s", randomId));
                                 }
                             });
        } catch (Exception e) {
            log.error("compareTableRowsBy error: {}", e.getMessage());
            return errorMessages;
        }

        return errorMessages;
    }

    private List<TableValidationResult> compareDiffTableWith(List<TableInfo> sourceTableInfos, List<String> targetTableNames, String errorMessage) {
        return sourceTableInfos.stream()
                               .filter(tableInfo -> !targetTableNames.contains(tableInfo.getTableName()))
                               .map(diffTableInfo -> {
                                   var tableValidationResult = TableValidationResult.generateBy(diffTableInfo.getTableName());
                                   tableValidationResult.addErrorMessage(errorMessage);
                                   diffTableInfo.markValidated();
                                   return tableValidationResult;
                               })
                               .collect(Collectors.toList());
    }

    @SneakyThrows
    public void convertToReport(List<TableValidationResult> validationResults, VerifyDatabaseRequest request, String executionId) {
        var reportFile = CsvReportFactory.createReportFile(executionId);
        try (var writer = new CSVWriter(new FileWriter(reportFile))) {
            writer.writeNext(new String[]{"source-database", request.getSourceDatasource().getJdbcUrl()});
            writer.writeNext(new String[]{"target-database", request.getTargetDatasource().getJdbcUrl()});
            writer.writeNext(new String[]{});
            writer.writeNext(new String[]{"TableName", "ValidationResult", "ErrorMessage"});
            for (var validationResult : validationResults) {
                writer.writeNext(new String[]{validationResult.getTableName(), validationResult.isSuccess() ? "SUCCESS" : "FAILED", validationResult.getErrorMessage()});
            }

            log.info("successfully save file report with execution id: {}", executionId);
        }
    }

    public TableValidationResult compareTableStructureBy(TableInfo sourceTableInfo, TableInfo targetTableInfo) {
        TableValidationResult tableValidationResult = TableValidationResult.generateBy(sourceTableInfo.getTableName());
        ErrorMessages fieldCompareResult = sourceTableInfo.compare(targetTableInfo);
        if (fieldCompareResult.isNotEmpty()) {
            tableValidationResult.addErrorMessage(fieldCompareResult);
        }
        return tableValidationResult;
    }

}
