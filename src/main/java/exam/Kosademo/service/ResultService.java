package exam.Kosademo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResultService {
    public Map<String, Map<String, Integer>> getCategorizedResults(Map<String, Object> jsonMap) {
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");
        Map<String, Map<String, Integer>> categorizedResults = new HashMap<>();
        for (Map<String, Object> item : checkResults) {
            String category = (String) item.get("Category");
            String status = (String) item.get("status");
            categorizedResults.computeIfAbsent(category, k -> new HashMap<>()).merge(status, 1, Integer::sum);
        }
        return categorizedResults;
    }

    public Map<String, Double> getCategorySecurity(Map<String, Map<String, Integer>> categorizedResults) {
        Map<String, Double> categorySecurity = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : categorizedResults.entrySet()) {
            String category = entry.getKey();
            Map<String, Integer> stats = entry.getValue();
            int safe = stats.getOrDefault("[양호]", 0);
            int vulnerable = stats.getOrDefault("[취약]", 0);
            int na = stats.getOrDefault("[N/A]", 0);
            int total = safe + vulnerable + na;
            double securityIndex = total == 0 ? 0.0 : (double) safe / total * 100;
            categorySecurity.put(category, Double.parseDouble(String.format("%.2f", securityIndex)));
        }
        return categorySecurity;
    }

    public double getAllSecurity(Map<String, Map<String, Integer>> categorizedResults) {
        int totalSafe = 0, totalVulnerable = 0, totalNA = 0;
        for (Map<String, Integer> stats : categorizedResults.values()) {
            totalSafe += stats.getOrDefault("[양호]", 0);
            totalVulnerable += stats.getOrDefault("[취약]", 0);
            totalNA += stats.getOrDefault("[N/A]", 0);
        }
        int overallTotal = totalSafe + totalVulnerable + totalNA;
        double allSecurity = overallTotal == 0 ? 0.0 : (double) totalSafe / overallTotal * 100;
        return Double.parseDouble(String.format("%.2f", allSecurity));
    }

    public Map<String, Map<String, Integer>> getChart(Map<String, Object> jsonMap) {
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");
        Map<String, Map<String, Integer>> chart = new HashMap<>();
        for (Map<String, Object> item : checkResults) {
            String importance = (String) item.get("Importance");
            String status = (String) item.get("status");
            chart.computeIfAbsent(importance, k -> new HashMap<>()).merge(status, 1, Integer::sum);
        }
        return chart;
    }

    public Map<String, Object> getSection1(Map<String, Object> serverInfoMap, double allSecurity) {
        Map<String, Object> section1 = new HashMap<>();
        section1.put("DATE", serverInfoMap.get("DATE"));
        section1.put("SW_INFO", serverInfoMap.get("SW_INFO"));
        section1.put("allSecurity", allSecurity);
        return section1;
    }

    public Map<String, Object> getSection2(Map<String, Object> serverInfoMap) {
        Map<String, Object> section2 = new HashMap<>();
        section2.put("Category", serverInfoMap.get("Category"));
        section2.put("Importance", serverInfoMap.get("Importance"));
        section2.put("status", serverInfoMap.get("status"));
        section2.put("Sub_Category", serverInfoMap.get("Sub_Category"));
        return section2;
    }

    public List<Map<String, Object>> getSection3(Map<String, Object> jsonMap, Map<String, Object> serverInfoMap) {
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");
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
        return section3;
    }
}
