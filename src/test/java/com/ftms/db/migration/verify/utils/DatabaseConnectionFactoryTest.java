package com.ftms.db.migration.verify.utils;

import com.ftms.db.migration.verify.AbstractBaseTest;
import com.thoughtworks.db.migration.verify.utils.DatabaseConnectionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConnectionFactoryTest extends AbstractBaseTest {

    @Test
    void should_success_return_correct_db_connection() {
        var connection = DatabaseConnectionFactory.acquireConnectionBy(getSourceDatasourceProperties());
        assertNotNull(connection);
    }
}