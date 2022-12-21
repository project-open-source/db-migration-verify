package com.thoughtworks.db.migration.verify.controller;

import com.thoughtworks.db.migration.verify.configuration.ErrorResult;
import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import com.thoughtworks.db.migration.verify.service.TableVerifyService;
import com.thoughtworks.db.migration.verify.utils.CsvReportFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequiredArgsConstructor
public class TableVerifyController {
    private final TableVerifyService tableService;

    @PostMapping("/verify")
    public String verify(@RequestBody VerifyDatabaseRequest request) {
        if (!request.getSourceDatasource().isAllValidProperties() || !request
                .getTargetDatasource()
                .isAllValidProperties()) {
            throw new IllegalArgumentException("参数不完整");
        }
        var executionId = UUID.randomUUID().toString();
        tableService.verify(request, executionId);
        return executionId;
    }

    @GetMapping("/verify-report/{execution-id}")
    public ResponseEntity<Object> downloadVerifyReport(@PathVariable("execution-id") String executionId) throws FileNotFoundException {
        var reportFile = new File(CsvReportFactory.generateReportFilePath(executionId));

        if (!reportFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new ErrorResult("NOT_FOUND", "检测报告不存在或正在检测中，请稍后再试"));
        }

        var resource = new InputStreamResource(new FileInputStream(reportFile));

        return ResponseEntity.ok()
                             .header(CONTENT_DISPOSITION, format("attachment; filename=%s.csv", executionId))
                             .contentLength(reportFile.length())
                             .contentType(MediaType.APPLICATION_OCTET_STREAM)
                             .body(resource);
    }

}
