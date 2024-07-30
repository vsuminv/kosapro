package exam.Kosademo.service;

import exam.Kosademo.mapper.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResultService {

    private final ClassPathResource resource;
    private final JsonUtils jsonUtils;

    public ResultService(@Value("result2.json") ClassPathResource resource, JsonUtils jsonUtils) {
        this.resource = resource;
        this.jsonUtils = jsonUtils;
    }

    @Operation(summary = "Get Server Info from JSON", description = "JSON 파일에서 서버 정보를 가져옵니다.")
    public Map<String, Object> getServerInfoFromJson() {
        Map<String, Object> jsonMap = jsonUtils.parseJson(resource);
        return (Map<String, Object>) jsonMap.get("Server_Info");
    }

    @Operation(summary = "Get Check Results from JSON", description = "JSON 파일에서 체크 결과를 가져옵니다.")
    public List<Map<String, Object>> getCheckResultsFromJson() {
        Map<String, Object> jsonMap = jsonUtils.parseJson(resource);
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");
        checkResults.forEach(item -> item.put("Server_Info", getServerInfoFromJson()));
        return checkResults;
    }
}