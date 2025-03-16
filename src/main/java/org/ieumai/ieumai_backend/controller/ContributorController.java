package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.domain.Contributor;
import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.dto.ContributorProfile;
import org.ieumai.ieumai_backend.dto.ContributorUpdateRequest;
import org.ieumai.ieumai_backend.dto.UserSignUpRequest;
import org.ieumai.ieumai_backend.service.ContributorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/contributors")
@RequiredArgsConstructor
public class ContributorController {

    private final ContributorService contributorService;

    @GetMapping("/{email}")
    @Operation(summary = "기여자 프로필 조회",
            description = "이메일로 기여자 프로필을 조회합니다",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = ContributorProfile.class))),
            @ApiResponse(responseCode = "404",
                    description = "기여자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<?> getContributorProfile(@PathVariable String email) {
        try {
            ContributorProfile profile = contributorService.getContributorProfile(email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("기여자 프로필 조회 중 오류 발생: ", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "기여자 등록",
            description = "새로운 기여자를 등록합니다",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "기여자 등록 성공",
                    content = @Content(schema = @Schema(implementation = ContributorProfile.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<?> registerContributor(@Valid @RequestBody UserSignUpRequest request) {
        try {
            Contributor contributor = contributorService.getOrCreateUser(request);
            ContributorProfile profile = contributorService.convertToProfile(contributor);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("기여자 등록 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(new CommonResponse<>("기여자 등록에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{email}")
    @Operation(summary = "기여자 정보 수정",
            description = "기존 기여자 정보를 수정합니다",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "기여자 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = ContributorProfile.class))),
            @ApiResponse(responseCode = "404",
                    description = "기여자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<?> updateContributor(
            @PathVariable String email,
            @Valid @RequestBody ContributorUpdateRequest request) {
        try {
            ContributorProfile updatedProfile = contributorService.updateContributor(email, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("기여자 정보 수정 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(new CommonResponse<>("기여자 정보 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/count")
    @Operation(summary = "총 기여자 수 조회",
            description = "시스템에 등록된 총 기여자 수를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "총 기여자 수 조회 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<?> getTotalContributorsCount() {
        try {
            long totalCount = contributorService.getTotalContributorsCount();
            return ResponseEntity.ok(new CommonResponse<>("총 기여자 수 조회 성공", totalCount));
        } catch (Exception e) {
            log.error("총 기여자 수 조회 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(new CommonResponse<>("총 기여자 수 조회에 실패했습니다: " + e.getMessage()));
        }
    }
}
