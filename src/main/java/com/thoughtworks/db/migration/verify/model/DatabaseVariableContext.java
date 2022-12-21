package com.thoughtworks.db.migration.verify.model;

import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
public class DatabaseVariableContext {
    private final List<DatabaseVariable> sourceDatabaseVariables;
    private final List<DatabaseVariable> targetDatabaseVariables;

    public DatabaseVariableContext(List<DatabaseVariable> sourceDatabaseVariables, List<DatabaseVariable> targetDatabaseVariables) {
        this.sourceDatabaseVariables = sourceDatabaseVariables;
        this.targetDatabaseVariables = targetDatabaseVariables;
    }

    public static DatabaseVariableContext generateBy(DatabaseManager databaseManager, DatasourceRequest sourceDatasourceRequest, DatasourceRequest targetDatasourceRequest) {
        var sourceDatabaseVariables = databaseManager.fetchRows(sourceDatasourceRequest, "show variables;", DatabaseVariable.class);
        var targetDatabaseVariables = databaseManager.fetchRows(targetDatasourceRequest, "show variables;", DatabaseVariable.class);
        return new DatabaseVariableContext(sourceDatabaseVariables, targetDatabaseVariables);
    }

    public boolean targetDatabaseVariablesContains(String variableName) {
        return targetDatabaseVariables.stream().anyMatch(targetVariable -> StringUtils.equals(targetVariable.getName(), variableName));
    }

    public DatabaseVariable findTargetVariableBy(String name) {
        return targetDatabaseVariables.stream().filter(databaseVariable -> StringUtils.equals(databaseVariable.getName(), name)).findFirst().orElseThrow();
    }

    public boolean sourceDatabaseVariablesContains(String variableName) {
        return sourceDatabaseVariables.stream().anyMatch(sourceVariable -> StringUtils.equals(sourceVariable.getName(), variableName));
    }
}
