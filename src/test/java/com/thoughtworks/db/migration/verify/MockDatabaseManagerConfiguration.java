package com.thoughtworks.db.migration.verify;

import com.thoughtworks.db.migration.verify.model.DatabaseManager;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockDatabaseManagerConfiguration {
    @Bean
    @Primary
    public DatabaseManager mockDatabaseManager() {
        return Mockito.mock(DatabaseManager.class);
    }
}
