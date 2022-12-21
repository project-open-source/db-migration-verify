package com.ftms.db.migration.verify.controller;

import com.thoughtworks.db.migration.verify.controller.request.VerifyDatabaseRequest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

class VariablesVerifyControllerTest extends RepresentationBaseTest {

    @Test
    void should_success_compare_and_return_difference_database_variables() {
        var request = new VerifyDatabaseRequest(getSourceDatasourceProperties(), getTargetDatasourceProperties());

        given()
                .body(request)
                .when()
                .post("/database-variables/verify")
                .then()
                .statusCode(200)
                .body("verifyResults.size()", equalTo(0))
                .body("sourceDatabase", equalTo(request.getSourceDatasource().getDatabase()))
                .body("targetDatabase", equalTo(request.getTargetDatasource().getDatabase()));
    }
}