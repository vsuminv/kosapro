package exam.Kosademo.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public JsonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Parse JSON", description = "JSON 파일을 파싱하여 Map 객체로 변환합니다.")
    public Map<String, Object> parseJson(ClassPathResource resource) {
        try {
            return objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("@@@@@@@@@@@@@@@@Failed to parse JSON", e);
        }
    }
}