package exam.Kosademo.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@Tag(name = "Controller", description = "API 명세서") //http://localhost:8081/swagger-ui/index.html
public class HomeController {
    private final ClassPathResource resource = new ClassPathResource("result2.json");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    @Operation(summary = "resource/objectMapper", description = "serverInfoMap , checkResults , categorizedResults , categorySecurity , allSecurity , report")
    public String getData(Model model) throws IOException {
        Map<String, Object> jsonMap = objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");

        // checkResults 에서 항목,상태만 뽑은 맵
        Map<String, Map<String, Integer>> categorizedResults = new TreeMap<>();
        for (Map<String, Object> item : checkResults) {
            String category = (String) item.get("Category");
            String status = (String) item.get("status");
            Map<String, Integer> stats = categorizedResults.getOrDefault(category, new HashMap<>());
            stats.put(status, stats.getOrDefault(status, 0) + 1);
            categorizedResults.put(category, stats);
        }
        // 개별 안정성
        Map<String, Double> categorySecurity = new TreeMap<>();
        for (String category : categorizedResults.keySet()) {
            Map<String, Integer> stats = categorizedResults.get(category);
            int safe = stats.getOrDefault("[양호]", 0);
            int vulnerable = stats.getOrDefault("[취약]", 0);
            int na = stats.getOrDefault("[N/A]", 0);
            int total = safe + vulnerable + na;
            double securityIndex = total == 0 ? 0.0 : (double) safe / total * 100;
            categorySecurity.put(category, Double.parseDouble(String.format("%.2f", securityIndex)));
        }
        // 서버 전체 안정성
        int totalSafe = 0, totalVulnerable = 0, totalNA = 0;
        for (Map<String, Integer> stats : categorizedResults.values()) {
            totalSafe += stats.getOrDefault("[양호]", 0);
            totalVulnerable += stats.getOrDefault("[취약]", 0);
            totalNA += stats.getOrDefault("[N/A]", 0);
        }
        int overallTotal = totalSafe + totalVulnerable + totalNA;
        double allSecurity = overallTotal == 0 ? 0.0 : (double) totalSafe / overallTotal * 100;
        allSecurity = Double.parseDouble(String.format("%.2f", allSecurity));


        //section1
        Map<String, Object> section1 = new HashMap<>();
        section1.put("DATE", serverInfoMap.get("DATE"));
        section1.put("SW_INFO", serverInfoMap.get("SW_INFO"));
        section1.put("allSecurity", allSecurity);
        //section2
        Map<String, Object> section2 = new HashMap<>();
        section2.put("Category", serverInfoMap.get("Category"));
        section2.put("Importance", serverInfoMap.get("Importance"));
        section2.put("status", serverInfoMap.get("status"));
        section2.put("Sub_Category", serverInfoMap.get("Sub_Category"));
        //section3
        List<Map<String, Object>> section3 = new ArrayList<>();
        for (Map<String, Object> result : checkResults) {
            Map<String, Object> data = new HashMap<>();
            data.put("OS", serverInfoMap.get("SW_NM"));
            data.put("Host", serverInfoMap.get("HOST_NM"));
            data.put("IP", serverInfoMap.get("IP_ADDRESS"));
            data.put("Category", result.get("Category"));
            data.put("Importance", result.get("Importance"));
            data.put("Status", result.get("status"));
            data.put("Sub_Category", result.get("Sub_Category"));
            section3.add(data);
        }
        //section4
        String report = "업로드 공간";

        model.addAttribute("section1", section1);
        logger.info("section1 : {}", section1);
        model.addAttribute("section2", section2);
        logger.info("section2 : {}", section2);
        model.addAttribute("section3", section3);
        logger.info("section3 : {}", section3);
        model.addAttribute("checkResults", checkResults);
        logger.info("진단 결과: {}", checkResults);
        model.addAttribute("categorizedResults", categorizedResults);
//        logger.info("항목별 상태: {}", categorizedResults);
        model.addAttribute("categorySecurity", categorySecurity);
        logger.info("개별 보안: {}", categorySecurity);
        model.addAttribute("allSecurity", allSecurity);
        logger.info("서버 보안: {}%", allSecurity);

        return "pages/main";
    }
}
