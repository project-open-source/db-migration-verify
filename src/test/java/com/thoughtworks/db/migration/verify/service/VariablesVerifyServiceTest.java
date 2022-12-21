package com.thoughtworks.db.migration.verify.service;

import com.thoughtworks.db.migration.verify.configuration.DataSourceProperties;
import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import com.thoughtworks.db.migration.verify.model.DatabaseManager;
import com.thoughtworks.db.migration.verify.model.DatabaseVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VariablesVerifyServiceTest {
    @Mock
    private DatabaseManager databaseManager;
    @Mock
    private DataSourceProperties dataSourceProperties;
    @InjectMocks
    private DatabaseVerifyService databaseVerifyService;
    private VerifyDatabaseRequest verifyDatabaseRequest;

    @BeforeEach
    void setUp() {
        var sourceDatasourceProperties = new DatasourceRequest("127.0.0.1", 3306, "sourceDatabase", "root", "password");
        var targetDatasourceProperties = new DatasourceRequest("127.0.0.1", 3306, "targetDatabase", "root", "password");
        verifyDatabaseRequest = new VerifyDatabaseRequest(sourceDatasourceProperties, targetDatasourceProperties);
    }

    @Test
    void should_success_compare_difference_variables_name_in_database() {
        var variableName = "character_set_results";
        var sourceDatabaseVariables = List.of(new DatabaseVariable(variableName, "utf8mb4"));
        var targetDatabaseVariables = List.of(new DatabaseVariable(variableName, "utf8mb3"));

        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getSourceDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(sourceDatabaseVariables);
        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getTargetDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(targetDatabaseVariables);

        var databaseVariableIssues = databaseVerifyService.verify(verifyDatabaseRequest);
        assertEquals(1, databaseVariableIssues.size());

        var databaseVariableIssue = databaseVariableIssues.get(0);
        assertEquals(variableName, databaseVariableIssue.getVariableName());
        assertEquals("utf8mb4", databaseVariableIssue.getVariableValue().getSourceValue());
        assertEquals("utf8mb3", databaseVariableIssue.getVariableValue().getTargetValue());
    }

    @Test
    void should_return_source_variables_when_target_not_exist_variables() {
        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getSourceDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(List.of(new DatabaseVariable("character_set_results", "utf8mb4")));
        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getTargetDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(List.of());

        var variableIssues = databaseVerifyService.verify(verifyDatabaseRequest);

        assertEquals(1, variableIssues.size());
        assertEquals("character_set_results", variableIssues.get(0).getVariableName());
        var variableValue = variableIssues.get(0).getVariableValue();
        assertEquals("utf8mb4", variableValue.getSourceValue());
        assertEquals("VARIABLE_NOT_EXIST", variableValue.getTargetValue());
    }

    @Test
    void should_return_target_variables_when_target_not_exist_variables() {
        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getSourceDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(List.of());
        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getTargetDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(List.of(new DatabaseVariable("character_set_results", "utf8mb4")));

        var variableIssues = databaseVerifyService.verify(verifyDatabaseRequest);

        assertEquals(1, variableIssues.size());
        assertEquals("character_set_results", variableIssues.get(0).getVariableName());
        var variableValue = variableIssues.get(0).getVariableValue();
        assertEquals("utf8mb4", variableValue.getTargetValue());
        assertEquals("VARIABLE_NOT_EXIST", variableValue.getSourceValue());
    }

    @Test
    void should_return_confirm_flag_when_check_variables() {
        when(dataSourceProperties.getTargetConfirmedVariables()).thenReturn(List.of("innodb_adaptive_hash_index"));
        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getSourceDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(List.of(new DatabaseVariable("innodb_adaptive_hash_index", "ON")));
        when(databaseManager.fetchRows(eq(verifyDatabaseRequest.getTargetDatasource()), anyString(), eq(DatabaseVariable.class)))
                .thenReturn(List.of(new DatabaseVariable("innodb_adaptive_hash_index", "OFF")));

        var variableIssues = databaseVerifyService.verify(verifyDatabaseRequest);

        assertEquals(1, variableIssues.size());
        assertEquals("innodb_adaptive_hash_index", variableIssues.get(0).getVariableName());
        assertTrue(variableIssues.get(0).isConfirmed());
    }
}