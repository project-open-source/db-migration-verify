package com.ftms.db.migration.verify.controller;

import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import com.thoughtworks.db.migration.verify.service.TableVerifyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

class VerifyControllerTest extends RepresentationBaseTest {
    @Autowired
    private TableVerifyService tableVerifyService;

    @Test
    void should_success_return_execution_id() {
        var request = new VerifyDatabaseRequest(getSourceDatasourceProperties(), getTargetDatasourceProperties());
        given()
                .body(request)
                .when()
                .post("/verify")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    void should_throw_exception_if_not_found_report_file() {
        given()
                .when()
                .get("/verify-report/not-exist-execution-id")
                .then()
                .statusCode(404)
                .body("code", equalTo("NOT_FOUND"))
                .body("message", equalTo("检测报告不存在或正在检测中，请稍后再试"));
    }

    @Test
    void should_throw_exception_when_request_with_empty_database_properties() {
        var emptyDatabaseProperties = new DatasourceRequest("", 3306, "", "", "");
        var request = new VerifyDatabaseRequest(emptyDatabaseProperties, getTargetDatasourceProperties());

        given()
                .body(request)
                .when()
                .post("/verify")
                .then()
                .statusCode(400)
                .body("code", equalTo("ILLEGAL_ARGUMENT"))
                .body("message", equalTo("参数不完整"));
    }

    @Test
    void should_success_download_report_file_after_validation() {
        var request = new VerifyDatabaseRequest(getSourceDatasourceProperties(), getTargetDatasourceProperties());
        var executionId = "execution-id";

        tableVerifyService.verify(request, executionId);

        given()
                .when()
                .get("/verify-report/" + executionId)
                .then()
                .statusCode(200)
                .header(CONTENT_DISPOSITION, format("attachment; filename=%s.csv", executionId))
                .contentType(equalTo(MediaType.APPLICATION_OCTET_STREAM.toString()))
                .body(notNullValue());
    }

}