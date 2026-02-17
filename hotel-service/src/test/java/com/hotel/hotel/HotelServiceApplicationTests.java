package com.hotel.hotel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource; // ✅ add this import

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.username=sa",
    "spring.datasource.password=sa",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class HotelServiceApplicationTests {

    @Test
    void contextLoads() {
        // test that Spring context loads successfully
    }
}
