package com.vroom.application;

import com.vroom.media.service.storage.VideoStorageService;
import com.vroom.notification.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

    @MockBean
    EmailService emailService;

    @MockBean
    VideoStorageService videoStorageService;

    @Test
    void contextLoads() {
    }

}
