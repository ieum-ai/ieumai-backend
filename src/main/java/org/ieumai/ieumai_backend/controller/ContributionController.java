package org.ieumai.ieumai_backend.controller;

import lombok.Getter;
import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.dto.ContributorProfile;
import org.ieumai.ieumai_backend.dto.VoiceFileDTO;
import org.ieumai.ieumai_backend.service.ContributionService;
import org.ieumai.ieumai_backend.service.ContributorService;
import org.ieumai.ieumai_backend.config.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/contributions")
@RequiredArgsConstructor
public class ContributionController {

    private final ContributorService contributorService;
    private final ContributionService contributionService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/profile")
    @Operation(summary = "기여자 프로필과 최근 기여 조회",
            description = "로그인한 기여자의 프로필 정보와 최근 기여한 음성 파일 3개를 조회합니다",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "프로필 및 기여 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ContributorProfile.class))),
            @ApiResponse(responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "기여자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<?> getProfileAndRecentContributions(@RequestHeader("Authorization") String token) {
        try {
            // 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token.replace("Bearer ", ""));

            // 기여자 프로필 조회
            ContributorProfile profile = contributorService.getContributorProfile(email);

            // 최근 기여 음성 파일 3개 조회
            List<VoiceFileDTO> recentContributions = contributionService.getRecentContributions(email, 3);

            // 응답 객체에 최근 기여 정보 추가
            return ResponseEntity.ok(new CommonResponse<>(
                    "기여자 프로필 및 최근 기여 조회 성공",
                    new ProfileWithContributions(profile, recentContributions)
            ));
        } catch (Exception e) {
            log.error("프로필 및 기여 조회 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(new CommonResponse<>("프로필 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @DeleteMapping("/voice/{voiceFileId}")
    @Operation(summary = "기여 음성 파일 삭제",
            description = "특정 음성 파일을 삭제합니다 (자신의 기여만 삭제 가능)",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "음성 파일 삭제 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "삭제 권한 없음",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "음성 파일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<CommonResponse<Void>> deleteVoiceFile(
            @PathVariable Long voiceFileId,
            @RequestHeader("Authorization") String token) {
        try {
            // 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token.replace("Bearer ", ""));

            // 음성 파일 삭제
            contributionService.deleteVoiceFile(voiceFileId, email);

            return ResponseEntity.ok(new CommonResponse<>("음성 파일이 성공적으로 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(new CommonResponse<>(e.getMessage()));
        } catch (Exception e) {
            log.error("음성 파일 삭제 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(new CommonResponse<>("음성 파일 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    // 프로필과 최근 기여 정보를 함께 반환
    @Getter
    @Schema(description = "기여자 프로필과 최근 기여 정보")
    private static class ProfileWithContributions {
        private final ContributorProfile profile;
        private final List<VoiceFileDTO> recentContributions;

        public ProfileWithContributions(ContributorProfile profile, List<VoiceFileDTO> recentContributions) {
            this.profile = profile;
            this.recentContributions = recentContributions;
        }
    }
}
