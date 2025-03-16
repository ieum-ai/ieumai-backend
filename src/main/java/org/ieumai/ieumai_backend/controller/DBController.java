package org.ieumai.ieumai_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ieumai.ieumai_backend.domain.Contributor;
import org.ieumai.ieumai_backend.domain.ContributionScript;
import org.ieumai.ieumai_backend.domain.ContributionScriptVoiceFile;
import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.dto.ScriptDetail;
import org.ieumai.ieumai_backend.dto.ScriptVoice;
import org.ieumai.ieumai_backend.repository.ContributionScriptRepository;
import org.ieumai.ieumai_backend.repository.ContributionScriptVoiceFileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/db")
@RequiredArgsConstructor
public class DBController {

    private final ContributionScriptRepository contributionScriptRepository;
    private final ContributionScriptVoiceFileRepository voiceFileRepository;

    @GetMapping("/scripts/{scriptId}")
    @Operation(summary = "스크립트 상세 정보 조회",
            description = "스크립트 ID로 스크립트 상세 정보와 관련 음성 파일 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "스크립트 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = ScriptDetail.class))),
            @ApiResponse(responseCode = "404",
                    description = "스크립트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<?> getScriptDetail(@PathVariable Long scriptId) {
        try {
            // 스크립트 조회
            ContributionScript script = contributionScriptRepository.findById(scriptId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 스크립트를 찾을 수 없습니다: " + scriptId));

            // 스크립트의 모든 음성 파일 조회
            List<ContributionScriptVoiceFile> voiceFiles = voiceFileRepository
                    .findByContributionScript_ContributionScriptId(scriptId);

            // 음성 파일의 평균 길이 계산
            OptionalDouble avgLength = voiceFiles.stream()
                    .map(vf -> vf.getVoiceFile().getVoiceLength())
                    .mapToLong(Long::longValue)
                    .average();

            // 가장 최근 업데이트 시간 구하기
            LocalDateTime latestUpdate = voiceFiles.stream()
                    .map(ContributionScriptVoiceFile::getCreatedAt)
                    .max(Comparator.naturalOrder())
                    .orElse(script.getCreatedAt());

            // 최근 업데이트로부터 경과 시간 계산 (시간 단위)
            long hoursAgo = Duration.between(latestUpdate, LocalDateTime.now()).toHours();

            // 음성 파일 정보를 DTO로 변환
            List<ScriptVoice> voiceFileDTOs = voiceFiles.stream()
                    .map(vf -> {
                        Contributor contributor = vf.getContribution().getContributor();
                        return new ScriptVoice(
                                vf.getVoiceFile().getVoiceFileId(),
                                vf.getCreatedAt(),
                                contributor.getName(),
                                vf.getVoiceFile().getVoiceLength()
                        );
                    })
                    .collect(Collectors.toList());

            // 응답 DTO 생성
            ScriptDetail response = new ScriptDetail(
                    script.getContributionScriptId(),
                    script.getCreatedAt(),
                    script.getContributionScript(),
                    script.getContributionCount(),
                    avgLength.orElse(0),
                    hoursAgo,
                    voiceFileDTOs
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("스크립트 상세 정보 조회 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(new CommonResponse<>("스크립트 정보 조회에 실패했습니다: " + e.getMessage()));
        }
    }

}