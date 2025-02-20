package org.ieumai.ieumai_backend.service;

import org.ieumai.ieumai_backend.exception.EmailSendException;
import org.ieumai.ieumai_backend.exception.EmailTemplateException;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final AmazonSimpleEmailService amazonSES;
    private final ResourceLoader resourceLoader;
    private static final String PIN_VERIFICATION_TEMPLATE = "email/pin-verification.html";

    @Value("${aws.ses.from-email}")
    private String fromEmail;

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
