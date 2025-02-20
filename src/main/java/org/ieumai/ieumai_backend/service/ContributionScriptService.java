package org.ieumai.ieumai_backend.service;

import org.ieumai.ieumai_backend.domain.ContributionScript;
import org.ieumai.ieumai_backend.repository.ContributionScriptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionScriptService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ContributionScriptRepository contributionScriptRepository;

    @Value("${openai.api-key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_PROMPT = """
        다음 조건을 만족하는 다양한 한국어 제시문을 생성할 것.
    
        생성된 제시문은 방언 분류 모델 학습 데이터로 사용될 예정이며,
        나이 구분 없이 범용적으로 사용 가능하고 10초에서 15초 이내로 발화될 수 있는 분량으로,
        되도록 긴 문장을 사용하여 문장 구조, 억양, 발화 속도 등의 다양한 음성 특징을 포착할 수 있도록 할 것.
    
        화자 특성: 특정 연령대나 성별에 치우치지 않는 보편적인 주제와 어휘를 사용하고,
        일상생활에서 자주 사용되는 중립적인 어휘와 문장 구조를 활용하여 모든 화자가 편안하게 발화할 수 있도록 할 것.
    
        대화 상황: 일상적인 대화 상황을 설정하고 질문이나 응답 형식 등 다양한 문장 구조를 사용하여,
        일상적인 감정 표현을 포함하여 발화자의 자연스러운 반응을 유도할 것. 간단하고 명확한 상황을 설정하고,
        발화자의 역할을 명확하게 제시하여 발화자가 그 상황에 몰입하여 자연스럽게 발화하도록 유도할 것.
        다양한 주제(예: 음식, 날씨, 여행, 쇼핑, 가족, 일상, 직장 등)를 포함하여 발화의 범위를 넓힐 것.
    
        방언 특성: 특정 방언에 치우치지 않고 다양한 지역의 방언 특징을 골고루 있게 반영하고,
        억양이나 발음 차이를 문장 내에서 자연스럽게 드러낼 것. 문장 내에서 억양 변화가 자연스럽게 이루어지도록 구성을 나누고,
        발화 속도의 변화를 유도할 것.
    
        제시문 분량: 최소 2문장 이상으로 구성된 긴 문장 형태의 제시문을 생성하고,
        10초에서 15초 내외로 발화할 수 있는 분량으로 9문단 생성할 것.
    
        추가 조건: 자연스러운 일상 대화체로 작성하고, 문법적으로 정확하고 의미가 명확해야 하며,
        흥미롭고 유익한 내용을 포함할 것. 다양한 주제와 어휘를 사용하여 모델이 다양한 음성 특징을 학습할 수 있도록 할 것.
    
        출력: 각 문장을 문자열 JSON 배열 안에 포함되도록 출력을 고정함. 출력되는 문장들의 내용이 동일하지 않아야 함.
    """;
    private static final int SCRIPTS_PER_REQUEST = 3;
    private static final int MAX_CONTRIBUTION_COUNT = 50;
    private static final int MINIMUM_ACTIVE_SCRIPTS = 3;

    // 스크립트 생성 메서드
    private List<ContributionScript> createContributionScripts() {
        String jsonResponse = generateResponse(DEFAULT_PROMPT);
        List<ContributionScript> scripts = new ArrayList<>();

        try {
            // JSON 배열 파싱
            JsonNode scriptsArray = objectMapper.readTree(jsonResponse);

            if (scriptsArray.isArray()) {
                for (JsonNode scriptNode : scriptsArray) {
                    String scriptContent = scriptNode.asText().trim();
                    if (!scriptContent.isEmpty()) {
                        ContributionScript newScript = ContributionScript.builder()
                                .contributionScript(scriptContent)
                                .contributionCount(0)
                                .isActive(true)
                                .build();
                        scripts.add(contributionScriptRepository.save(newScript));
                    }
                }
            }
        } catch (Exception e) {
            log.error("스크립트 파싱 중 오류 발생: ", e);
            throw new RuntimeException("스크립트 생성 실패: " + e.getMessage());
        }

        return scripts;
    }

    // 프롬프트로 응답을 생성하는 메서드
    public String generateResponse(String prompt) {
        try {
            HttpHeaders headers = createHeaders();
            Map<String, Object> requestBody = createRequestBody(prompt);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생: ", e);
            throw new RuntimeException("GPT 응답 생성 실패: " + e.getMessage());
        }
    }

    // HTTP 헤더 생성
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    // 요청 본문 생성
    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "RESPONSE IN JSON STRING ARRAY WITH NO NUMBER INDEX."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("max_tokens", 900);
        requestBody.put("temperature", 0.7);
        return requestBody;
    }

    // API 응답 파싱
    private String parseResponse(String responseBody) throws Exception {
        JsonNode jsonResponse = objectMapper.readTree(responseBody);
        String content = jsonResponse.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText()
                .trim();

        // JSON 문자열이 실제 JSON 배열 형식인지 확인
        try {
            JsonNode parsedContent = objectMapper.readTree(content);
            if (!parsedContent.isArray()) {
                throw new IllegalStateException("응답이 JSON 배열 형식이 아닙니다.");
            }
            return content;
        } catch (Exception e) {
            log.error("JSON 파싱 오류: ", e);
            throw new IllegalStateException("응답을 JSON 배열로 파싱할 수 없습니다: " + e.getMessage());
        }
    }

    private List<ContributionScript> selectRandomScripts(List<ContributionScript> scripts, int count) {
        if (scripts.size() <= count) {
            return new ArrayList<>(scripts);
        }

        Collections.shuffle(scripts);
        return scripts.subList(0, count);
    }

    @Transactional
    public String getRandomActiveScriptsAsJson() {
        List<ContributionScript> activeScripts = getActiveScripts();

        // 활성 스크립트가 최소 개수보다 적으면 새로 생성
        if (activeScripts.size() < MINIMUM_ACTIVE_SCRIPTS) {
            createContributionScripts();
            activeScripts = getActiveScripts();
        }

        // 랜덤하게 3개 선택
        List<ContributionScript> selectedScripts = selectRandomScripts(activeScripts, SCRIPTS_PER_REQUEST);

        // 스크립트 내용만 추출하여 리스트 생성
        List<String> scriptTexts = selectedScripts.stream()
                .map(ContributionScript::getContributionScript)
                .collect(Collectors.toList());

        try {
            // JSON 배열로 변환
            return objectMapper.writeValueAsString(scriptTexts);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 중 오류 발생: ", e);
            throw new RuntimeException("스크립트 JSON 변환 실패: " + e.getMessage());
        }
    }

    // 활성 상태 스크립트 목록 조회 메서드
    private List<ContributionScript> getActiveScripts() {
        return contributionScriptRepository.findByIsActiveTrue();
    }

    // contribution_count 증가 메서드
    @Transactional
    public ContributionScript incrementContributionCount(Long contributionScriptId) {
        ContributionScript script = contributionScriptRepository.findById(contributionScriptId)
                .orElseThrow(() -> new RuntimeException("스크립트를 찾을 수 없습니다: " + contributionScriptId));

        script.incrementContributionCount();

        // 최대 기여 횟수 도달 시 비활성화
        if (script.getContributionCount() >= MAX_CONTRIBUTION_COUNT) {
            script.deactivate();
        }

        return contributionScriptRepository.save(script);
    }
}
