package com.hotel.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.username=sa",
    "spring.datasource.password=sa",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
