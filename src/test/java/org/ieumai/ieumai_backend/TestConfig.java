package org.ieumai.ieumai_backend;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public AmazonS3 amazonS3() {
        return Mockito.mock(AmazonS3.class);
    }

    @Bean
    @Primary
    public AmazonSimpleEmailService amazonSES() {
        return Mockito.mock(AmazonSimpleEmailService.class);
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
}