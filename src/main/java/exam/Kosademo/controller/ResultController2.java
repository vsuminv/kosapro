package exam.Kosademo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class ResultController2 {
    private static final Logger logger = LoggerFactory.getLogger(ResultController2.class);
    private final ClassPathResource resource = new ClassPathResource("result2.json");

    /**
     * "/result2" 경로로 GET 요청이 들어왔을 때 호출되는 메소드입니다.
     * JSON 파일을 읽어와 그 내용을 가공하여 모델에 추가합니다.
     */

    @GetMapping("/resultGra")
    public String getResultDetail(Model model) {
        try {
            // ObjectMapper를 사용하여 JSON 파일을 읽어옵니다.
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(
                    resource.getInputStream(), new TypeReference<Map<String, Object>>() {}
            );

            // 서버 정보를 추출하여 모델에 추가합니다.
            Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
            model.addAttribute("serverInfo", serverInfoMap);

            // 체크 결과 리스트를 추출합니다.
            List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");

            // 카테고리별로 결과를 분류하여 저장할 맵을 초기화합니다.
            Map<String, Map<String, Integer>> categorizedResults = new TreeMap<>();
            for (Map<String, Object> item : checkResults) {
                String category = (String) item.get("Category");
                String status = (String) item.get("status");

                // 각 카테고리별로 상태별 개수를 계산하여 맵에 저장합니다.
                Map<String, Integer> stats = categorizedResults.getOrDefault(category, new HashMap<>());
                stats.put(status, stats.getOrDefault(status, 0) + 1);
                categorizedResults.put(category, stats);
            }

            // 카테고리별 보안 지수를 계산합니다.
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

            // 전체 보안 지수를 계산합니다.
            int totalSafe = 0, totalVulnerable = 0, totalNA = 0;
            for (Map<String, Integer> stats : categorizedResults.values()) {
                totalSafe += stats.getOrDefault("[양호]", 0);
                totalVulnerable += stats.getOrDefault("[취약]", 0);
                totalNA += stats.getOrDefault("[N/A]", 0);
            }
            int overallTotal = totalSafe + totalVulnerable + totalNA;
            double overallSecurityIndex = overallTotal == 0 ? 0.0 : (double) totalSafe / overallTotal * 100;

            // 로그를 통해 계산된 정보를 출력합니다.
            logger.info("Server Info: {}", serverInfoMap);
            logger.info("Categorized Results: {}", categorizedResults);
            logger.info("Category Security Indices: {}", categorySecurityIndices);
            logger.info("Overall Security Index: {}", overallSecurityIndex);

            // 모델에 각 정보를 추가합니다.
            model.addAttribute("categorizedResults", categorizedResults);
            model.addAttribute("categorySecurityIndices", categorySecurityIndices);
            model.addAttribute("overallSecurityIndex", overallSecurityIndex);

            // 카테고리별 보안 지수를 JSON 형식으로 변환하여 모델에 추가합니다.
            String categorySecurityIndicesJson = objectMapper.writeValueAsString(categorySecurityIndices);
            model.addAttribute("categorySecurityIndicesJson", categorySecurityIndicesJson);

        } catch (IOException e) {
            // JSON 파일을 읽는 도중 에러가 발생하면 로그에 에러 메시지를 출력합니다.
            logger.error("Error reading JSON file", e);
        }

        // 결과를 표시할 뷰의 이름을 반환합니다.
        return "pages/graPage";
    }
}
