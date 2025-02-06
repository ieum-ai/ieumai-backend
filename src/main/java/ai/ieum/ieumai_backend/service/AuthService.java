package ai.ieum.ieumai_backend.service;

import ai.ieum.ieumai_backend.domain.IpLimit;
import ai.ieum.ieumai_backend.domain.VerificationPin;
import ai.ieum.ieumai_backend.exception.EmailSendException;
import ai.ieum.ieumai_backend.exception.IpLimitException;
import ai.ieum.ieumai_backend.exception.PinVerificationException;
import ai.ieum.ieumai_backend.repository.IpLimitRepository;
import ai.ieum.ieumai_backend.repository.VerificationPinRepository;
import ai.ieum.ieumai_backend.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final VerificationPinRepository pinRepository;
    private final IpLimitRepository ipLimitRepository;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final int MAX_ATTEMPTS = 3;
    private static final int PIN_EXPIRY_MINUTES = 5;
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int BLOCK_DURATION_HOURS = 1;

    @Transactional
    public void requestPin(String email, String ip) {
        // IP 제한 확인
        checkIpLimit(ip);

        // 이전 PIN 삭제
        pinRepository.deleteByEmail(email);

        // 새 PIN 생성 및 저장
        String pin = generatePin();
        VerificationPin verificationPin = VerificationPin.builder()
                .email(email)
                .pin(pin)
                .attempts(0)
                .expiresAt(LocalDateTime.now().plusMinutes(PIN_EXPIRY_MINUTES))
                .build();

        verificationPin = pinRepository.save(verificationPin);

        // 이메일 발송
        try {
            emailService.sendPinEmail(email, pin);
            updateIpLimit(ip);
        } catch (Exception e) {
            pinRepository.delete(verificationPin);
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new EmailSendException("이메일 발송에 실패했습니다.");
        }
    }

    @Transactional
    public String verifyPin(String email, String pin) {
        VerificationPin verification = pinRepository.findByEmailAndExpiresAtGreaterThan(email, LocalDateTime.now())
                .orElseThrow(() -> new PinVerificationException("인증번호가 만료되었습니다."));

        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            pinRepository.delete(verification);
            throw new PinVerificationException("인증 시도 횟수를 초과했습니다. 새로운 인증번호를 요청해주세요.");
        }

        if (!verification.getPin().equals(pin)) {
            verification.incrementAttempts();
            pinRepository.save(verification);

            int remainingAttempts = verification.getRemainingAttempts();
            if (remainingAttempts <= 0) {
                pinRepository.delete(verification);
                throw new PinVerificationException("인증번호가 3회 틀렸습니다. 새로운 인증번호를 요청해주세요.");
            }

            throw new PinVerificationException(
                    String.format("잘못된 인증번호입니다. %d회 시도 기회가 남았습니다.", remainingAttempts));
        }

        // PIN 검증 성공 - PIN 삭제
        pinRepository.delete(verification);

        // JWT 토큰 생성
        return jwtTokenProvider.createToken(email);
    }

    private void checkIpLimit(String ip) {
        IpLimit ipLimit = ipLimitRepository.findByIp(ip)
                .orElse(IpLimit.builder()
                        .ip(ip)
                        .count(0)
                        .lastRequestAt(LocalDateTime.now())
                        .build());

        if (ipLimit.getBlockExpiresAt() != null &&
                ipLimit.getBlockExpiresAt().isAfter(LocalDateTime.now())) {
            LocalDateTime unblockTime = ipLimit.getBlockExpiresAt();
            throw new IpLimitException(String.format("너무 많은 요청이 있었습니다. %s까지 기다려주세요.",
                    unblockTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
        }

        // 마지막 요청으로부터 1시간이 지났다면 카운트 초기화
        if (ipLimit.getLastRequestAt().plusHours(1).isBefore(LocalDateTime.now())) {
            ipLimit.setCount(0);
            ipLimit.setBlockExpiresAt(null);
        }

        if (ipLimit.getCount() >= MAX_REQUESTS_PER_HOUR) {
            ipLimit.setBlockExpiresAt(LocalDateTime.now().plusHours(BLOCK_DURATION_HOURS));
            ipLimitRepository.save(ipLimit);
            throw new IpLimitException("5회 이상 요청하여 1시간 동안 차단되었습니다.");
        }
    }

    private void updateIpLimit(String ip) {
        IpLimit ipLimit = ipLimitRepository.findByIp(ip)
                .orElse(IpLimit.builder()
                        .ip(ip)
                        .count(0)
                        .lastRequestAt(LocalDateTime.now())
                        .build());

        if (ipLimit.getLastRequestAt().plusHours(1).isBefore(LocalDateTime.now())) {
            ipLimit.setCount(0);
        }

        ipLimit.setCount(ipLimit.getCount() + 1);
        ipLimit.setLastRequestAt(LocalDateTime.now());
        ipLimitRepository.save(ipLimit);
    }

    private String generatePin() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}

