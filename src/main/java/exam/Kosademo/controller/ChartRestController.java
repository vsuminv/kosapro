package exam.Kosademo.controller;

import exam.Kosademo.service.ResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Slf4j
@ResponseBody
@Tag(name = "ChartController", description = "chart")
@RequiredArgsConstructor
public class ChartRestController {
    private final ResultService resultService;

    @GetMapping("/result")
    public Map<String, Map<String, Integer>> getChartData(@RequestBody Map<String, Object> jsonMap) {
        return resultService.getChart(jsonMap);
    }
}