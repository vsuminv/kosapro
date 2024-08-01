package exam.Kosademo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class Tester {
    private static final Logger logger = LoggerFactory.getLogger(ResultController2.class);
    private final ClassPathResource resource = new ClassPathResource("result2.json");

    @GetMapping("/dd")
    public String test(Model model) {
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
            model.addAttribute("ds");

            // 카테고리별 보안 지수를 JSON 형식으로 변환하여 모델에 추가합니다.
            String categorySecurityIndicesJson = objectMapper.writeValueAsString(categorySecurityIndices);
            model.addAttribute("categorySecurityIndicesJson", categorySecurityIndicesJson);

        } catch (IOException e) {
            logger.error("제이슨 못읽음", e);
        }
        return "pages/mainPage";
    }

}
