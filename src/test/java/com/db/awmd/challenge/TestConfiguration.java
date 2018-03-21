package com.db.awmd.challenge;

import com.db.awmd.challenge.service.NotificationService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfiguration {
    @Bean
    @Primary
    public NotificationService notificationService() {
        return Mockito.mock(NotificationService.class);
    }
}
