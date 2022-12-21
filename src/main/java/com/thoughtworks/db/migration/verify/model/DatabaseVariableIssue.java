package com.thoughtworks.db.migration.verify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseVariableIssue {
    private String variableName;
    private boolean confirmed;
    private VariableValuePair variableValue;

    @Getter
    @AllArgsConstructor
    public static class VariableValuePair {
        private String sourceValue;
        private String targetValue;
    }
}
