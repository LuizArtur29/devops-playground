package com.playground.helpdesk;

import com.playground.helpdesk.infra.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportTestcontainers(TestContainersConfig.class)
class HelpDeskApplicationTests {

	@DynamicPropertySource
	static void registerRedisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", TestContainersConfig.redisContainer::getHost);
		registry.add("spring.data.redis.port", TestContainersConfig.redisContainer::getFirstMappedPort);

		registry.add("spring.rabbitmq.host", TestContainersConfig.rabbitContainer::getHost);
		registry.add("spring.rabbitmq.port", TestContainersConfig.rabbitContainer::getAmqpPort);
		registry.add("spring.rabbitmq.username", TestContainersConfig.rabbitContainer::getAdminUsername);
		registry.add("spring.rabbitmq.password", TestContainersConfig.rabbitContainer::getAdminPassword);
	}

	@Test
	void contextLoads() {
	}

}