package com.thoughtworks.db.migration.verify.controller.response;

import com.thoughtworks.db.migration.verify.model.DatabaseVariableIssue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DatabaseVerifyResponse {
    private final List<DatabaseVariableIssue> verifyResults;
    private final String sourceDatabase;
    private final String targetDatabase;
}
