package com.thoughtworks.db.migration.verify.model;

import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;

import java.util.List;
import java.util.Optional;

public interface DatabaseManager {

    <T> List<T> fetchRows(DatasourceRequest properties, String staticSql, Class<T> expectResultType);

    <T> Optional<T> fetchRow(DatasourceRequest properties, String staticSql, Class<T> expectResultType);

    Optional<String> fetchRow(DatasourceRequest properties, String staticSql);


}
