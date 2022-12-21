package com.thoughtworks.db.migration.verify.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyDatabaseRequest {
    private DatasourceRequest sourceDatasource;
    private DatasourceRequest targetDatasource;
}
