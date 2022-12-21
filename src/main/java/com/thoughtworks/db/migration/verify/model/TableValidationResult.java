package com.thoughtworks.db.migration.verify.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TableValidationResult {
    private String tableName;
    private boolean success;
    private List<String> errorMessages;

    public static TableValidationResult generateBy(String tableName) {
        return new TableValidationResult(tableName, true, new ArrayList<>());
    }

    public String getErrorMessage() {
        return String.join("\n", errorMessages);
    }

    public void addErrorMessage(String errorMessage) {
        this.markFailed();
        this.errorMessages.add(errorMessage);
    }

    public void addErrorMessage(List<String> errorMessage) {
        this.markFailed();
        this.errorMessages.addAll(errorMessage);
    }


    private void markFailed() {
        this.success = false;
    }

    public void addErrorMessage(ErrorMessages errorMessages) {
        this.addErrorMessage(errorMessages.getValues());
    }
}
