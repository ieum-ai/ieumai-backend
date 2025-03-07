package org.ieumai.ieumai_backend.service;

import jakarta.annotation.PostConstruct;
import org.ieumai.ieumai_backend.exception.EmailSendException;
import org.ieumai.ieumai_backend.exception.EmailTemplateException;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        name = "aws.ses.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class EmailService {
    private final AmazonSimpleEmailService amazonSES;
    private final ResourceLoader resourceLoader;
    private final Environment environment;
    private static final String PIN_VERIFICATION_TEMPLATE = "email/pin-verification.html";

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    @PostConstruct
    public void init() {
        try {
            // SES 연결 테스트
            amazonSES.listIdentities();
            log.info("AWS SES 연결 성공");
        } catch (Exception e) {
            log.error("AWS SES 연결 실패", e);
            // 개발 환경에서는 로깅만 하고 운영 환경에서는 애플리케이션 시작 차단
            if (!isDevEnvironment()) {
                throw new RuntimeException("AWS SES 연결에 실패했습니다", e);
            }
        }
    }

    private boolean isDevEnvironment() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.contains("dev") || profile.contains("local"));
    }

    public void sendPinEmail(String to, String pin) {
        try {
            String htmlTemplate = loadEmailTemplate(PIN_VERIFICATION_TEMPLATE);
            String htmlBody = htmlTemplate.replace("{pin}", pin);

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(to))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8")
                                            .withData(htmlBody)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8")
                                    .withData("로그인 인증 번호")))
                    .withSource(fromEmail);

            amazonSES.sendEmail(request);
            log.info("인증 이메일 발송 완료: {}", to);

        } catch (AmazonSimpleEmailServiceException e) {
            log.error("이메일 발송 실패: {}", to, e);
            throw new EmailSendException("이메일 발송에 실패했습니다");
        }
    }

    private String loadEmailTemplate(String templatePath) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + templatePath);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("이메일 템플릿 로드 실패: {}", templatePath, e);
            throw new EmailTemplateException("이메일 템플릿을 불러올 수 없습니다", e);
        }
    }
}