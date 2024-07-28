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

@Controller
@RequestMapping("/api")
public class ResultController2 {
    private static final Logger logger = LoggerFactory.getLogger(ResultController2.class);
    private final ClassPathResource resource = new ClassPathResource("result2.json");

    @GetMapping("/result2")
    public String getResultDetail(Model model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(
                    resource.getInputStream(), new TypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
            model.addAttribute("serverInfo", serverInfoMap);

            List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");

            Map<String, Map<String, Integer>> categorizedResults = new TreeMap<>();
            for (Map<String, Object> item : checkResults) {
                String category = (String) item.get("Category");
                String status = (String) item.get("status");

                Map<String, Integer> stats = categorizedResults.getOrDefault(category, new HashMap<>());
                stats.put(status, stats.getOrDefault(status, 0) + 1);
                categorizedResults.put(category, stats);
            }

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

            int totalSafe = 0, totalVulnerable = 0, totalNA = 0;
            for (Map<String, Integer> stats : categorizedResults.values()) {
                totalSafe += stats.getOrDefault("[양호]", 0);
                totalVulnerable += stats.getOrDefault("[취약]", 0);
                totalNA += stats.getOrDefault("[N/A]", 0);
            }
            int overallTotal = totalSafe + totalVulnerable + totalNA;
            double overallSecurityIndex = overallTotal == 0 ? 0.0 : (double) totalSafe / overallTotal * 100;

            logger.info("Server Info: {}", serverInfoMap);
            logger.info("Categorized Results: {}", categorizedResults);
            logger.info("Category Security Indices: {}", categorySecurityIndices);
            logger.info("Overall Security Index: {}", overallSecurityIndex);

            model.addAttribute("categorizedResults", categorizedResults);
            model.addAttribute("categorySecurityIndices", categorySecurityIndices);
            model.addAttribute("overallSecurityIndex", overallSecurityIndex);

            // JSON 형식으로 변환하여 모델에 추가
            String categorySecurityIndicesJson = objectMapper.writeValueAsString(categorySecurityIndices);
            model.addAttribute("categorySecurityIndicesJson", categorySecurityIndicesJson);

        } catch (IOException e) {
            logger.error("Error reading JSON file", e);
        }

        return "resultGra";
    }
}
