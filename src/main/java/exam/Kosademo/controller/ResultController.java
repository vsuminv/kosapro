package exam.Kosademo.controller;

import exam.Kosademo.service.JsonService;
import exam.Kosademo.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Slf4j
@Controller
@Tag(name = "Controller", description = "API 명세서")
@RequiredArgsConstructor
public class ResultController {
    private final JsonService jsonService;
    private final ResultService resultService;

    @GetMapping("/result")
    @Operation(summary = "resource/objectMapper", description = "서버 정보 및 결과")
    public String getData(Model model) {
        Optional<Map<String, Object>> jsonMapOptional = jsonService.getCheckResultData();
        Map<String, Object> jsonMap = jsonMapOptional.get();

        Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
        Map<String, Map<String, Integer>> categorizedResults = resultService.getCategorizedResults(jsonMap);
        Map<String, Double> categorySecurity = resultService.getCategorySecurity(categorizedResults);
        double allSecurity = resultService.getAllSecurity(categorizedResults);
        Map<String, Object> section1 = resultService.getSection1(serverInfoMap, allSecurity);
        Map<String, Object> section2 = resultService.getSection2(serverInfoMap);
        Map<String, Object> section3 = (Map<String, Object>) resultService.getSection3(jsonMap, serverInfoMap);

        model.addAttribute("section1", section1);
        model.addAttribute("section2", section2);
        model.addAttribute("section3", section3);
        model.addAttribute("categorizedResults", categorizedResults);
        model.addAttribute("categorySecurity", categorySecurity);
        model.addAttribute("allSecurity", allSecurity);

        log.info("dd");
        return "pages/main";
    }
}
