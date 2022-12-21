package com.ftms.db.migration.verify;

import com.thoughtworks.db.migration.verify.controller.request.DatasourceRequest;
import com.thoughtworks.db.migration.verify.utils.DatabaseConnectionFactory;
import com.thoughtworks.db.migration.verify.utils.SqlExecutor;
import com.thoughtworks.db.migration.verify.utils.SqlGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;

import java.util.Objects;

import static java.lang.String.format;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractBaseTest {
    public static final String DROP_TABLE_SQL_TEMPLATE = "drop table %s";
    @Autowired(required = false)
    private DatabaseVerifyProperties localDatabaseProperties;
    @Autowired(required = false)
    @Qualifier("sourceMysqlContainer")
    private MySQLContainer<?> sourceMysqlContainer;
    @Autowired(required = false)
    @Qualifier("targetMysqlContainer")
    private MySQLContainer<?> targetMysqlContainer;
    private DatasourceRequest sourceDatasourceRequest;
    private DatasourceRequest targetDatasourceRequest;
    protected Database sourceDatabase;
    protected Database targetDatabase;

    public DatasourceRequest getSourceDatasourceProperties() {
        if (Objects.nonNull(sourceDatasourceRequest)) {
            return sourceDatasourceRequest;
        }

        synchronized (AbstractBaseTest.class) {
            if (Objects.isNull(localDatabaseProperties)) {
                this.sourceDatasourceRequest = new DatasourceRequest(sourceMysqlContainer.getHost(),
                        sourceMysqlContainer.getMappedPort(3306), sourceMysqlContainer.getDatabaseName(),
                        sourceMysqlContainer.getUsername(), sourceMysqlContainer.getPassword()
                );
            } else {
                this.sourceDatasourceRequest = new DatasourceRequest(localDatabaseProperties.host(),
                        localDatabaseProperties.port(), localDatabaseProperties.sourceDatabase(),
                        localDatabaseProperties.username(), localDatabaseProperties.password()
                );
            }
        }

        return sourceDatasourceRequest;
    }

    public DatasourceRequest getTargetDatasourceProperties() {
        if (Objects.nonNull(targetDatasourceRequest)) {
            return targetDatasourceRequest;
        }

        synchronized (AbstractBaseTest.class) {
            if (Objects.isNull(localDatabaseProperties)) {
                this.targetDatasourceRequest = new DatasourceRequest(targetMysqlContainer.getHost(),
                        targetMysqlContainer.getMappedPort(3306), targetMysqlContainer.getDatabaseName(),
                        targetMysqlContainer.getUsername(), targetMysqlContainer.getPassword()
                );
            } else {
                this.targetDatasourceRequest = new DatasourceRequest(localDatabaseProperties.host(),
                        localDatabaseProperties.port(), localDatabaseProperties.targetDatabase(),
                        localDatabaseProperties.username(), localDatabaseProperties.password()
                );
            }
        }

        return targetDatasourceRequest;
    }

    @BeforeEach
    void cleanDatabase() {
        sourceDatabase = Database.init(getSourceDatasourceProperties());
        targetDatabase = Database.init(getTargetDatasourceProperties());
        try (
                var sourceConnection = DatabaseConnectionFactory.acquireConnectionBy(getSourceDatasourceProperties());
                var targetConnection = DatabaseConnectionFactory.acquireConnectionBy(getTargetDatasourceProperties())
        ) {
            var sourceTableNames = SqlExecutor.fetchRows(sourceConnection, SqlGenerator.TABLE_QUERY);
            var targetTableNames = SqlExecutor.fetchRows(targetConnection, SqlGenerator.TABLE_QUERY);
            for (String tableName : sourceTableNames) {
                sourceConnection.createStatement().execute(format(DROP_TABLE_SQL_TEMPLATE, tableName));
            }
            for (String tableName : targetTableNames) {
                targetConnection.createStatement().execute(format(DROP_TABLE_SQL_TEMPLATE, tableName));
            }
        } catch (Exception ignored) {
        }
    }
}
