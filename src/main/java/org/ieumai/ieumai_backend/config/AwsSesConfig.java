package org.ieumai.ieumai_backend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AwsSesConfig {
    private static final Logger log = LoggerFactory.getLogger(AwsSesConfig.class);

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    @ConditionalOnProperty(
            name = "aws.ses.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            return AmazonSimpleEmailServiceClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();
        } catch (Exception e) {
            log.error("AWS SES 초기화 실패", e);
            throw new RuntimeException("AWS SES 초기화에 실패했습니다", e);
        }
    }
}