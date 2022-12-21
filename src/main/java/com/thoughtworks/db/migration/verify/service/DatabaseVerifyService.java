package com.thoughtworks.db.migration.verify.service;

import com.thoughtworks.db.migration.verify.configuration.DataSourceProperties;
import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import com.thoughtworks.db.migration.verify.model.DatabaseManager;
import com.thoughtworks.db.migration.verify.model.DatabaseVariable;
import com.thoughtworks.db.migration.verify.model.DatabaseVariableContext;
import com.thoughtworks.db.migration.verify.model.DatabaseVariableIssue;
import com.thoughtworks.db.migration.verify.model.DatabaseVariableIssueFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DatabaseVerifyService {
    private final DatabaseManager databaseManager;
    private final DatabaseVariableIssueFactory databaseVariableIssueFactory;

    public DatabaseVerifyService(DatabaseManager databaseManager, DataSourceProperties dataSourceProperties) {
        this.databaseManager = databaseManager;
        databaseVariableIssueFactory = new DatabaseVariableIssueFactory(dataSourceProperties);
    }

    public List<DatabaseVariableIssue> verify(VerifyDatabaseRequest verifyDatabaseRequest) {
        var databaseVariableContext = DatabaseVariableContext.generateBy(databaseManager, verifyDatabaseRequest.getSourceDatasource(), verifyDatabaseRequest.getTargetDatasource());

        var missingVariableIssues = generateMissingVariableIssues(databaseVariableContext);
        var allVariableIssues = new ArrayList<>(missingVariableIssues);

        var sourceDatabaseVariables = databaseVariableContext.getSourceDatabaseVariables()
                                                             .stream()
                                                             .filter(variable -> !containsTargetVariable(missingVariableIssues, variable))
                                                             .collect(Collectors.toList());

        var differenceVariableIssues = generateDifferenceVariableIssues(databaseVariableContext, sourceDatabaseVariables);
        allVariableIssues.addAll(differenceVariableIssues);

        return allVariableIssues;
    }

    private boolean containsTargetVariable(List<DatabaseVariableIssue> missingVariableIssues, DatabaseVariable variable) {
        return missingVariableIssues
                .stream()
                .anyMatch(missingVariableIssue -> StringUtils.equals(variable.getName(), missingVariableIssue.getVariableName()));
    }

    private List<DatabaseVariableIssue> generateDifferenceVariableIssues(DatabaseVariableContext databaseVariableContext, List<DatabaseVariable> sourceDatabaseVariables) {
        return sourceDatabaseVariables.stream()
                                      .map(sourceVariable -> {
                                          var targetVariable = databaseVariableContext.findTargetVariableBy(sourceVariable.getName());
                                          return databaseVariableIssueFactory.createBy(sourceVariable, targetVariable);
                                      })
                                      .filter(issue -> !StringUtils.equals(issue
                                              .getVariableValue()
                                              .getSourceValue(), issue.getVariableValue().getTargetValue()))
                                      .collect(Collectors.toList());
    }

    private List<DatabaseVariableIssue> generateMissingVariableIssues(DatabaseVariableContext databaseVariableContext) {
        var sourceDatabaseVariables = databaseVariableContext.getSourceDatabaseVariables();
        var targetDatabaseVariables = databaseVariableContext.getTargetDatabaseVariables();

        var targetVariablesMissing = sourceDatabaseVariables.stream()
                                                            .filter(sourceDatabaseVariable -> !databaseVariableContext.targetDatabaseVariablesContains(sourceDatabaseVariable.getName()))
                                                            .collect(Collectors.toList());
        var sourceVariablesMissing = targetDatabaseVariables.stream()
                                                            .filter(targetDatabaseVariable -> !databaseVariableContext.sourceDatabaseVariablesContains(targetDatabaseVariable.getName()))
                                                            .collect(Collectors.toList());

        var missingVariableIssues = targetVariablesMissing.stream()
                                                          .map(databaseVariableIssueFactory::createTargetVariableMissingIssue)
                                                          .collect(Collectors.toCollection(ArrayList::new));
        var sourceVariableMissingIssues = sourceVariablesMissing.stream()
                                                                .map(databaseVariableIssueFactory::createSourceVariableMissingIssue)
                                                                .collect(Collectors.toList());
        missingVariableIssues.addAll(sourceVariableMissingIssues);
        return missingVariableIssues;
    }
}
