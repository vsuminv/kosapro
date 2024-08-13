package exam.Kosademo.controller;

import exam.Kosademo.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/result")
    public String getCheckResult(Model model) throws IOException {
        Map<String, Object> checkResultData = homeService.getCheckResultData();
        Map<String, Map<String, Integer>> importanceStatusData = processImportanceStatusData(checkResultData);
        model.addAttribute("importanceStatusData", importanceStatusData);
        model.addAllAttributes(checkResultData);
        return "pages/main";
    }

    private Map<String, Map<String, Integer>> processImportanceStatusData(Map<String, Object> checkResultData) {
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) checkResultData.get("checkResults");
        Map<String, Map<String, Integer>> importanceStatusData = new HashMap<>();
        for (Map<String, Object> result : checkResults) {
            String importance = (String) result.get("Importance");
            String status = (String) result.get("status");

            importanceStatusData.computeIfAbsent(importance, k -> new HashMap<>())
                    .merge(status, 1, Integer::sum);
        }
        return importanceStatusData;
    }
}
