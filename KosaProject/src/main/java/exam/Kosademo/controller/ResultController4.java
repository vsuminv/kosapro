package exam.Kosademo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/api")
public class ResultController4 {
    private final ClassPathResource resource = new ClassPathResource("result2.json");

    @GetMapping("/result4")
    public String getResultDetail(Model model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(
                    resource.getInputStream(), new TypeReference<Map<String, Object>>() {
                    }
            );

            Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
            model.addAttribute("serverInfo", serverInfoMap);

            List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");


            // 카테고리 번호 순서대로 하기 위해 TreeMap 사용
            Map<String, List<Map<String, String>>> categorizedResults = new TreeMap<>();

            // status 값 계산
            int total = checkResults.size();
            int safeCount = 0;
            int vulnerableCount = 0;
            int otherCount = 0;
            // 보안 계수
            int safecnt = 3;
            int vulnerablecnt = 2;
            int othercnt = 1;

            for (Map<String, Object> item : checkResults) {
                String category = (String) item.get("Category");
                String itemValue = (String) item.get("Item");
                String importance = (String) item.get("Importance");
                String status = (String) item.get("status");
                String sub_category = (String) item.get("sub_category");

                // 카테고리에 해당하는 리스트를 가져오거나 새로 생성
                List<Map<String, String>> itemList = categorizedResults.getOrDefault(category, new ArrayList<>());

                // 필요한 값만 포함된 맵 생성
                Map<String, String> itemMap = new HashMap<>();
                itemMap.put("Item", itemValue);
                itemMap.put("Importance", importance);
                itemMap.put("status", status);
                itemMap.put("sub_category", sub_category);

                itemList.add(itemMap);

                // 맵에 다시 저장
                categorizedResults.put(category, itemList);

                if ("[양호]".equals(status)) {
                    safeCount++;
                } else if ("[취약]".equals(status)) {
                    vulnerableCount++;
                } else {
                    otherCount++;
                }
            }

            // 카테고리별 결과 넣기
            model.addAttribute("categorizedResults", categorizedResults);

            model.addAttribute("total", total);
            model.addAttribute("safeCount", safeCount);
            model.addAttribute("vulnerableCount", vulnerableCount);
            model.addAttribute("otherCount", otherCount);

           //양호의 퍼센트 구하기
            // 양호 * 보안계수
            int safeTotal = safeCount * safecnt;

            // 취약 * 보안계수
            int vulnerableTotal = vulnerableCount * vulnerablecnt;
            // (양호와 취약 ) * 보안계수
            int secuTotal = safeTotal + vulnerableTotal;

            double safePercentage  =  (double) safeTotal / secuTotal *100 ;

            model.addAttribute("safePercentage", safePercentage );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "resultSummary";
    }
}
