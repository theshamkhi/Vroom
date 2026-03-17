package com.vroom.content;

import com.vroom.notification.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ContentApplicationTests {

    @MockBean
    EmailService emailService;

    @Test
    void contextLoads() {
    }
}