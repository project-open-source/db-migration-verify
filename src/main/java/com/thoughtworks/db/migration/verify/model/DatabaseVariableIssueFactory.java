package com.thoughtworks.db.migration.verify.model;

import com.thoughtworks.db.migration.verify.configuration.DataSourceProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatabaseVariableIssueFactory {
    private final DataSourceProperties dataSourceProperties;

    public DatabaseVariableIssue createTargetVariableMissingIssue(DatabaseVariable databaseVariable) {
        var valuePair = new DatabaseVariableIssue.VariableValuePair(databaseVariable.getValue(), "VARIABLE_NOT_EXIST");
        return new DatabaseVariableIssue(databaseVariable.getName(), isConfirmed(databaseVariable.getName()), valuePair);
    }

    public DatabaseVariableIssue createSourceVariableMissingIssue(DatabaseVariable databaseVariable) {
        var valuePair = new DatabaseVariableIssue.VariableValuePair("VARIABLE_NOT_EXIST", databaseVariable.getValue());
        return new DatabaseVariableIssue(databaseVariable.getName(), isConfirmed(databaseVariable.getName()), valuePair);
    }

    public DatabaseVariableIssue createBy(DatabaseVariable sourceVariable, DatabaseVariable targetVariable) {
        var valuePair = new DatabaseVariableIssue.VariableValuePair(sourceVariable.getValue(), targetVariable.getValue());
        return new DatabaseVariableIssue(sourceVariable.getName(), isConfirmed(targetVariable.getName()), valuePair);
    }

    private boolean isConfirmed(String name) {
        return dataSourceProperties.getTargetConfirmedVariables().contains(name);
    }
}
