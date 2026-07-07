package com.playground.helpdesk;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

public class TestContainersConfig {

    @ServiceConnection
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("helpdesk_db_test")
            .withUsername("test_user")
            .withPassword("test_password");
}