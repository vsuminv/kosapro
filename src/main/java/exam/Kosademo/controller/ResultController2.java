package exam.Kosademo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * ResultController2 클래스는 "/api" 경로로 들어오는 요청을 처리하는 컨트롤러입니다.
 * 주로 result2.json 파일을 읽어와 그 내용을 가공하여 모델에 추가하는 역할을 합니다.
 */
@Controller
@RequestMapping("/api")
@Tag(name = "Result Controller 2", description = "결과 데이터 처리 및 view 응답 ")
public class ResultController2 {
    private static final Logger logger = LoggerFactory.getLogger(ResultController2.class);
    private final ClassPathResource resource = new ClassPathResource("result2.json");

    /**
     * "/result2" 경로로 GET 요청이 들어왔을 때 호출되는 메소드입니다.
     * JSON 파일을 읽어와 그 내용을 가공하여 모델에 추가합니다.
     */
    @GetMapping("/result2")
    @Operation(summary = "Get Result Details", description = "JSON 파일에서 결과 데이터를 읽어와 처리하고 시각화를 위한 데이터를 모델에 추가합니다.")
    public String getResultDetail(Model model) {
        try {
            // JSON 파일 읽어오기
            Map<String, Object> jsonMap = readJsonFile();

            // 서버 정보 추출하여 모델에 추가
            addServerInfoToModel(model, jsonMap);

            // 체크 결과 리스트 추출
            List<Map<String, Object>> checkResults = getCheckResults(jsonMap);

            // 카테고리별 결과 분류
            Map<String, Map<String, Integer>> categorizedResults = categorizeResults(checkResults);

            // 카테고리별 보안 지수 계산
            Map<String, Double> categorySecurityIndices = calculateCategorySecurityIndices(categorizedResults);

            // 전체 보안 지수 계산
            double overallSecurityIndex = calculateOverallSecurityIndex(categorizedResults);

            // 로그 출력
            logResults(jsonMap, categorizedResults, categorySecurityIndices, overallSecurityIndex);

            // 모델에 데이터 추가
            addDataToModel(model, categorizedResults, categorySecurityIndices, overallSecurityIndex);

        } catch (IOException e) {
            // 에러 처리
            logger.error("Error reading JSON file", e);
        }

        return "resultGra";
    }

    /**
     * JSON 파일을 읽어와 Map 객체로 변환합니다.
     */
    private Map<String, Object> readJsonFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 서버 정보를 모델에 추가합니다.
     */
    private void addServerInfoToModel(Model model, Map<String, Object> jsonMap) {
        Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
        model.addAttribute("serverInfo", serverInfoMap);
    }

    /**
     * 체크 결과 리스트를 추출합니다.
     */
    private List<Map<String, Object>> getCheckResults(Map<String, Object> jsonMap) {
        return (List<Map<String, Object>>) jsonMap.get("Check_Results");
    }

    /**
     * 카테고리별로 결과를 분류합니다.
     */
    private Map<String, Map<String, Integer>> categorizeResults(List<Map<String, Object>> checkResults) {
        Map<String, Map<String, Integer>> categorizedResults = new TreeMap<>();
        for (Map<String, Object> item : checkResults) {
            String category = (String) item.get("Category");
            String status = (String) item.get("status");

            Map<String, Integer> stats = categorizedResults.getOrDefault(category, new HashMap<>());
            stats.put(status, stats.getOrDefault(status, 0) + 1);
            categorizedResults.put(category, stats);
        }
        return categorizedResults;
    }

    /**
     * 카테고리별 보안 지수를 계산합니다.
     */
    private Map<String, Double> calculateCategorySecurityIndices(Map<String, Map<String, Integer>> categorizedResults) {
        Map<String, Double> categorySecurityIndices = new TreeMap<>();
        for (String category : categorizedResults.keySet()) {
            Map<String, Integer> stats = categorizedResults.get(category);
            int safe = stats.getOrDefault("[양호]", 0);
            int vulnerable = stats.getOrDefault("[취약]", 0);
            int na = stats.getOrDefault("[N/A]", 0);
            int total = safe + vulnerable + na;
            double securityIndex = total == 0 ? 0.0 : (double) safe / total * 100;
            categorySecurityIndices.put(category, securityIndex);
        }
        return categorySecurityIndices;
    }

    /**
     * 전체 보안 지수를 계산합니다.
     */
    private double calculateOverallSecurityIndex(Map<String, Map<String, Integer>> categorizedResults) {
        int totalSafe = 0, totalVulnerable = 0, totalNA = 0;
        for (Map<String, Integer> stats : categorizedResults.values()) {
            totalSafe += stats.getOrDefault("[양호]", 0);
            totalVulnerable += stats.getOrDefault("[취약]", 0);
            totalNA += stats.getOrDefault("[N/A]", 0);
        }
        int overallTotal = totalSafe + totalVulnerable + totalNA;
        return overallTotal == 0 ? 0.0 : (double) totalSafe / overallTotal * 100;
    }

    /**
     * 계산된 결과를 로그로 출력합니다.
     */
    private void logResults(Map<String, Object> serverInfoMap, Map<String, Map<String, Integer>> categorizedResults,
                            Map<String, Double> categorySecurityIndices, double overallSecurityIndex) {
        logger.info("Server Info: {}", serverInfoMap);
        logger.info("Categorized Results: {}", categorizedResults);
        logger.info("Category Security Indices: {}", categorySecurityIndices);
        logger.info("Overall Security Index: {}", overallSecurityIndex);
    }

    /**
     * 모델에 데이터를 추가합니다.
     */
    private void addDataToModel(Model model, Map<String, Map<String, Integer>> categorizedResults,
                                Map<String, Double> categorySecurityIndices, double overallSecurityIndex) throws IOException {
        model.addAttribute("categorizedResults", categorizedResults);
        model.addAttribute("categorySecurityIndices", categorySecurityIndices);
        model.addAttribute("overallSecurityIndex", overallSecurityIndex);

        ObjectMapper objectMapper = new ObjectMapper();
        String categorySecurityIndicesJson = objectMapper.writeValueAsString(categorySecurityIndices);
        model.addAttribute("categorySecurityIndicesJson", categorySecurityIndicesJson);
    }
}