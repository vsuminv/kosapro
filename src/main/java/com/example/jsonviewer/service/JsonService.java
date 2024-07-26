package com.example.jsonviewer.service;

import com.example.jsonviewer.model.CheckResult;
import com.example.jsonviewer.model.ServerInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class JsonService {
    private final ObjectMapper mapper = new ObjectMapper();

    public ServerInfo getServerInfo() throws IOException {
        Resource resource = new ClassPathResource("data.json");
        JsonNode jsonNode = mapper.readTree(resource.getInputStream());
        return mapper.treeToValue(jsonNode.get("Server_Info"), ServerInfo.class);
    }

    public List<CheckResult> getCheckResults() throws IOException {
        Resource resource = new ClassPathResource("data.json");
        JsonNode jsonNode = mapper.readTree(resource.getInputStream());
        return mapper.readValue(
            jsonNode.get("Check_Results").toString(),
            mapper.getTypeFactory().constructCollectionType(List.class, CheckResult.class)
        );
    }
}
