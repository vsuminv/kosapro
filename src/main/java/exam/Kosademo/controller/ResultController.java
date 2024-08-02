package exam.Kosademo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "api 정보", description = "API 엔드포인트")
@Tag(name = "컨트롤러 정보", description = "결과값 문서로")
public class ResultController {
    private final ClassPathResource resource = new ClassPathResource("result2.json");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "명세서 뽑은거", description = "반환.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<String> getApiDocs() {
        OpenAPI openAPI = new OpenAPI();
        // API 문서 기본 정보
        Info info = new Info()
                .title("Kosain")
                .version("1.0.0")
                .description("인프라 진단 서비스");
        openAPI.info(info);
        // XML OpenAPI 명세서를 엔티티에 담아서 http로 뿌릴거임
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(openAPI.toString(), headers, HttpStatus.OK);
    }
}
