package com.thoughtworks.db.migration.verify.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DatabaseValidationResult {
    private final List<String> errorMessage;

    public DatabaseValidationResult() {
        this.errorMessage = new ArrayList<>();
    }


}
