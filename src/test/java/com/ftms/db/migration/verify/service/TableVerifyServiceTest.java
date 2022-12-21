package com.ftms.db.migration.verify.service;

import com.ftms.db.migration.verify.AbstractBaseTest;
import com.ftms.db.migration.verify.TableBuilder;
import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import com.thoughtworks.db.migration.verify.model.ColumnInfo;
import com.thoughtworks.db.migration.verify.model.TableInfo;
import com.ftms.db.migration.verify.model.TableInfoBuilder;
import com.thoughtworks.db.migration.verify.model.TableValidationResult;
import com.thoughtworks.db.migration.verify.service.TableVerifyService;
import com.thoughtworks.db.migration.verify.utils.CsvReportFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ftms.db.migration.verify.utils.CsvReportHelper.readReportContent;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TableVerifyServiceTest extends AbstractBaseTest {
    @InjectMocks
    private TableVerifyService tableVerifyService;

    @Test
    void should_success_generate_validation_report() {
        var request = new VerifyDatabaseRequest(getSourceDatasourceProperties(), getTargetDatasourceProperties());
        var executionId = "execution-id";

        tableVerifyService.verify(request, executionId);

        var file = new File(CsvReportFactory.generateReportFilePath(executionId));
        assertTrue(file.exists());
    }

    @Test
    void should_success_compare_all_rows_different_when_row_count_less_than_100() {
        var table = TableBuilder.withDefault("test_table").build();
        sourceDatabase.withTable(table).insertRows(() -> table.withRow("name", "source:" + UUID.randomUUID()), 10);
        targetDatabase.withTable(table).insertRows(() -> table.withRow("name", "target:" + UUID.randomUUID()), 10);

        tableVerifyService.verify(new VerifyDatabaseRequest(sourceDatabase.datasourceRequest(), targetDatabase.datasourceRequest()), "test-executeId");

        var reportContent = readReportContent("test-executeId");
        assertEquals(5, reportContent.rowCount());
        String actualResult = reportContent.read(5).content();
        assertTrue(actualResult.contains("test_table,FAILED,[ROW DIFF] id -> "), actualResult);
        assertEquals(10, actualResult.split("\n").length);
    }

    @Test
    void should_success_compare_100_rows_different_when_row_count_large_than_100() {
        var table = TableBuilder.withDefault("test_table").build();
        sourceDatabase.withTable(table).insertRows(() -> table.withRow("name", "source:" + UUID.randomUUID()), 200);
        targetDatabase.withTable(table).insertRows(() -> table.withRow("name", "target:" + UUID.randomUUID()), 200);

        tableVerifyService.verify(new VerifyDatabaseRequest(sourceDatabase.datasourceRequest(), targetDatabase.datasourceRequest()), "test-executeId");

        var reportContent = readReportContent("test-executeId");
        assertEquals(5, reportContent.rowCount());
        String actualResult = reportContent.read(5).content();
        assertTrue(actualResult.contains("test_table,FAILED,[ROW DIFF] id -> "), actualResult);
        assertEquals(100, actualResult.split("\n").length);
    }


    @Test
    void should_consider_as_correct_when_both_row_doesnt_exist() {
        TableBuilder.Table table = TableBuilder.withDefault("test_table").build();
        sourceDatabase.withTable(table);
        targetDatabase.withTable(table);

        tableVerifyService.verify(new VerifyDatabaseRequest(sourceDatabase.datasourceRequest(), targetDatabase.datasourceRequest()), "test-executeId");

        var reportContent = readReportContent("test-executeId");
        assertEquals(5, reportContent.rowCount());
        var reportRow = reportContent.read(5);
        assertEquals("test_table,SUCCESS,", reportRow.content());
    }

    @Test
    void should_skip_compare_row_when_table_has_no_primary_key() {
        TableBuilder.Table table = TableBuilder.withDefault("test_table").buildWithoutPrimaryKey();
        sourceDatabase.withTable(table);
        targetDatabase.withTable(table);

        tableVerifyService.verify(new VerifyDatabaseRequest(sourceDatabase.datasourceRequest(), targetDatabase.datasourceRequest()), "test-executeId");

        var reportContent = readReportContent("test-executeId");
        assertEquals(5, reportContent.rowCount());
        var reportRow = reportContent.read(5);
        assertEquals("test_table,FAILED,table has no primary key", reportRow.content());
    }
    @Test
    void should_skip_compare_row_when_primary_key_is_not_auto_incr() {
        TableBuilder.Table table = TableBuilder.withDefault("test_table").buildWithNonAutoIncrPrimaryKey();
        sourceDatabase.withTable(table).insertRows(() -> table.addRow("12212345678", "name"), 1);
        targetDatabase.withTable(table).insertRows(() -> table.addRow("12212345678", "name"), 1);

        tableVerifyService.verify(new VerifyDatabaseRequest(sourceDatabase.datasourceRequest(), targetDatabase.datasourceRequest()), "test-executeId");

        var reportContent = readReportContent("test-executeId");
        assertEquals(5, reportContent.rowCount());
        var reportRow = reportContent.read(5);
        assertEquals("test_table,FAILED,table primary key is not auto increment", reportRow.content());
    }

    @Test
    void should_success_generate_correct_csv_report() {
        var request = new VerifyDatabaseRequest(getSourceDatasourceProperties(), getTargetDatasourceProperties());
        var successValidationResult = new TableValidationResult("table-1", true, Collections.emptyList());
        var failedValidationResult = new TableValidationResult("table-2", false, List.of("target database not exist this table.", "row count not match."));
        var finalTableValidationResults = List.of(successValidationResult, failedValidationResult);
        var executionId = "execution-id";

        tableVerifyService.convertToReport(finalTableValidationResults, request, executionId);

        var reportContent = readReportContent(executionId);
        assertEquals(6, reportContent.rowCount());
        assertEquals("source-database," + getSourceDatasourceProperties().getJdbcUrl(), reportContent.read(1).content());
        assertEquals("target-database," + getTargetDatasourceProperties().getJdbcUrl(), reportContent.read(2).content());
        assertEquals("", reportContent.read(3).content());
        assertEquals("TableName,ValidationResult,ErrorMessage", reportContent.read(4).content());
        assertEquals("table-1,SUCCESS,", reportContent.read(5).content());
        assertEquals("table-2,FAILED,target database not exist this table.\nrow count not match.", reportContent.read(6).content());
    }

    @Test
    void should_success_generate_empty_report_with_empty_database() {
        var request = new VerifyDatabaseRequest(getSourceDatasourceProperties(), getTargetDatasourceProperties());
        List<TableValidationResult> finalTableValidationResults = Collections.emptyList();
        var executionId = "execution-id";

        tableVerifyService.convertToReport(finalTableValidationResults, request, executionId);

        var reportContent = readReportContent(executionId);
        assertEquals(4, reportContent.rowCount());
        assertEquals("source-database," + getSourceDatasourceProperties().getJdbcUrl(), reportContent.read(1).content());
        assertEquals("target-database," + getTargetDatasourceProperties().getJdbcUrl(), reportContent.read(2).content());
    }

    @Test
    void should_success_diff_table_base_Info() {
        sourceDatabase.withTable(TableBuilder.withDefault("table_name").engine("MyISAM").build());
        targetDatabase.withTable(TableBuilder.withDefault("table_name").engine("InnoDB").build());
        var request = new VerifyDatabaseRequest(sourceDatabase.datasourceRequest(), targetDatabase.datasourceRequest());

        tableVerifyService.verify(request, "executionId");

        var reportContent = readReportContent("executionId");

        assertEquals(5, reportContent.rowCount());
        assertEquals("table_name,FAILED,[engine] source -> MyISAM, target -> InnoDB", reportContent.read(5).content());
    }

    @Test
    void should_success_diff_table_column_info() {
        List<ColumnInfo> sourcesColumns = List.of(
                new ColumnInfo("id", "bigint(20)", "用户ID", true, true, "", true),
                new ColumnInfo("column_name", "varchar(32)", "任务名", true, false, "", false)
        );
        List<ColumnInfo> targetColumns = List.of(
                new ColumnInfo("id", "bigint(20)", "用户ID", true, true, "", true),
                new ColumnInfo("column_name", "varchar(32)", "任务名称", true, false, "", false)
        );
        TableInfo sourceTableValidationInfo = TableInfoBuilder.withDefault().columns(sourcesColumns).build();
        TableInfo targetTableValidationInfo = TableInfoBuilder.withDefault().columns(targetColumns).build();

        TableValidationResult validationResult = tableVerifyService.compareTableStructureBy(sourceTableValidationInfo, targetTableValidationInfo);
        assertFalse(validationResult.isSuccess());
        assertTrue(validationResult.getErrorMessages().contains("[column -> column_name -> comment] source -> 任务名, target -> 任务名称"));
    }

    @Test
    void should_success_find_out_different_table() {
        var executionId = "execution-id";
        sourceDatabase.withTable(TableBuilder.withDefault("sameTable").build());
        sourceDatabase.withTable(TableBuilder.withDefault("diffTable").build());
        targetDatabase.withTable(TableBuilder.withDefault("sameTable").build());


        var request = new VerifyDatabaseRequest(sourceDatabase.datasourceRequest(), targetDatabase.datasourceRequest());
        tableVerifyService.verify(request, executionId);

        var reportContent = readReportContent(executionId);

        assertEquals(6, reportContent.rowCount());
        assertEquals("diffTable,FAILED,target database not exist this table.", reportContent.read(5).content());
    }

    @Test
    void should_success_execute_verify_task() {
        var request = new VerifyDatabaseRequest(getSourceDatasourceProperties(), getTargetDatasourceProperties());
        var executionId = "execution-id";
        String tableName = "test_table";
        String anotherTableName = "another_table";
        sourceDatabase.withTable(TableBuilder.withDefault(tableName).build());
        targetDatabase.withTable(TableBuilder.withDefault(tableName).build());
        String insertSourceRowSql = "INSERT INTO test_table (name) VALUES ('test-source-coupon');";
        sourceDatabase.execute(insertSourceRowSql);
        targetDatabase.execute(insertSourceRowSql);
        sourceDatabase.withTable(TableBuilder.withDefault(anotherTableName).build());
        String insertAnotherSourceRowSql = "INSERT INTO another_table (name) VALUES ('test-source-coupon');";
        sourceDatabase.execute(insertAnotherSourceRowSql);

        tableVerifyService.verify(request, executionId);

        var reportContent = readReportContent(executionId);

        assertEquals(6, reportContent.rowCount());
        assertEquals("source-database," + getSourceDatasourceProperties().getJdbcUrl(), reportContent.read(1).content());
        assertEquals("target-database," + getTargetDatasourceProperties().getJdbcUrl(), reportContent.read(2).content());
        assertEquals("another_table,FAILED,target database not exist this table.", reportContent.read(5).content());
    }
}
