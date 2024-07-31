package exam.Kosademo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import exam.Kosademo.mapper.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ResultService {

    private final ClassPathResource resource;
    private final JsonUtils jsonUtils;

    public ResultService(@Value("result2.json") ClassPathResource resource, JsonUtils jsonUtils) {
        this.resource = resource;
        this.jsonUtils = jsonUtils;
    }

    /**
     * JSON 파일을 읽어와 Map 객체로 변환합니다.
     */
    @Operation(summary = "Get Server Info from JSON", description = "JSON 파일에서 서버 정보를 가져옵니다.")
    public Map<String, Object> getServerInfoFromJson() {
        Map<String, Object> jsonMap = jsonUtils.parseJson(resource);
        return (Map<String, Object>) jsonMap.get("Server_Info");
    }
    /**
     * 체크 결과 리스트를 추출합니다.
     */
    @Operation(summary = "Get Check Results from JSON", description = "JSON 파일에서 체크 결과를 가져옵니다.")
    public List<Map<String, Object>> getCheckResultsFromJson() {
        Map<String, Object> jsonMap = jsonUtils.parseJson(resource);
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");
        checkResults.forEach(item -> item.put("Server_Info", getServerInfoFromJson()));
        return checkResults;
    }
/**
 * 카테고리별로 결과를 분류합니다.
 */
@Operation(summary  = "Classifies the results by category.", description = "카테고리별로 결과를 분류합니다.")
public Map<String, Map<String, Integer>> categorizeResults(List<Map<String, Object>> checkResults) {
    Map<String, Map<String, Integer>> categorizedResults = new TreeMap<>();
    for (Map<String, Object> item : checkResults) {
        String category = (String) item.get("Category");
        String status = (String) item.get("status");

        Map<String, Integer> stats = categorizedResults.getOrDefault(category, new HashMap<>());
        stats.put(status, stats.getOrDefault(status, 0) + 1);
        categorizedResults.put(category, stats);
    }
    return categorizedResults;
}
}