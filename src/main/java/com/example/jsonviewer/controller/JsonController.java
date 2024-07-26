package com.example.jsonviewer.controller;

import com.example.jsonviewer.model.CheckResult;
import com.example.jsonviewer.model.ServerInfo;
import com.example.jsonviewer.service.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JsonController {

    @Autowired
    private JsonService jsonService;

    @GetMapping("/test")
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        try {
            // JSON 데이터 로드
            ServerInfo serverInfo = jsonService.getServerInfo();
            List<CheckResult> checkResults = jsonService.getCheckResults();

            // JSON 응답 데이터 추가
            response.put("serverInfo", serverInfo);
            response.put("checkResults", checkResults);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("errorMessage", "Error loading data: " + e.getMessage());
        }
        return response;
    }
}
