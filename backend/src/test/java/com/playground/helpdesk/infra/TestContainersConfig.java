package com.playground.helpdesk.infra;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;

public class TestContainersConfig {

    @ServiceConnection
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("helpdesk_db_test")
            .withUsername("test_user")
            .withPassword("test_password");

    public static final GenericContainer redisContainer = new GenericContainer("redis:7.2-alpine")
            .withExposedPorts(6379);

    public static final RabbitMQContainer rabbitContainer = new RabbitMQContainer("rabbitmq:3.13-management-alpine");
}