package org.ieumai.ieumai_backend;

import org.ieumai.ieumai_backend.config.jwt.JwtTokenProvider;
import org.ieumai.ieumai_backend.repository.VerificationPinRepository;
import org.ieumai.ieumai_backend.service.AuthService;
import org.ieumai.ieumai_backend.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private VerificationPinRepository verificationPinRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        // 이메일 서비스 모킹
        doNothing().when(emailService).sendPinEmail(anyString(), anyString());

        // JWT 토큰 프로바이더 모킹
        when(jwtTokenProvider.createToken(anyString())).thenReturn("mocked-jwt-token");
    }

    @Test
    public void testPinVerificationProcess() {
        // Given
        String testEmail = "test@example.com";
        String testIp = "127.0.0.1";

        // When: PIN 요청
        authService.requestPin(testEmail, testIp);

        // Then: PIN이 생성되었는지 확인
        assertThat(verificationPinRepository.findByEmailAndExpiresAtGreaterThan(testEmail, java.time.LocalDateTime.now()))
                .isPresent();
    }

    @Test
    public void testPinVerification() {
        // Given
        String testEmail = "test@example.com";
        String testIp = "127.0.0.1";

        // PIN 요청
        authService.requestPin(testEmail, testIp);

        // PIN 조회
        String pin = verificationPinRepository.findByEmailAndExpiresAtGreaterThan(testEmail, java.time.LocalDateTime.now())
                .map(verificationPin -> verificationPin.getPin())
                .orElseThrow(() -> new RuntimeException("PIN not found"));

        // When: PIN 검증
        String token = authService.verifyPin(testEmail, pin);

        // Then: JWT 토큰 생성 확인
        assertThat(token).isNotNull();
        assertThat(token.length()).isGreaterThan(0);
    }
}