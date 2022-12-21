package com.thoughtworks.db.migration.verify.controller;


import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import com.thoughtworks.db.migration.verify.controller.response.DatabaseVerifyResponse;
import com.thoughtworks.db.migration.verify.service.DatabaseVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class VariablesVerifyController {
    private final DatabaseVerifyService databaseVerifyService;

    @PostMapping("/database-variables/verify")
    public DatabaseVerifyResponse verifyDatabaseVariable(@RequestBody VerifyDatabaseRequest request) {
        var variableIssues = databaseVerifyService.verify(request);
        return new DatabaseVerifyResponse(variableIssues, request.getSourceDatasource().getDatabase(), request.getTargetDatasource().getDatabase());
    }

}
