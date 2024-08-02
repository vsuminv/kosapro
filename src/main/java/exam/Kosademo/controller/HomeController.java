package exam.Kosademo.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exam.Kosademo.controller.service.APIDoc;
import exam.Kosademo.controller.service.ResultMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Controller", description = "API 명세서")
public class HomeController {
    private final ClassPathResource resource = new ClassPathResource("result2.json");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Operation(summary = "8081홈",description = "메인화면 잡아주는 컴트롤러")
    @GetMapping("/")
    public String home() {
//        logger.trace("{} {} 출력", "값1", "값2");
//        logger.debug("{} {} 출력", "값1", "값2");
        logger.info("{}", "LOCAL ON!");
//        logger.warn("{} {} 출력", "값1", "값2");
        logger.atError();
        return "pages/home";
    }
    @GetMapping("/main")
    @Operation(summary = "get Server info", description = "result2.json 파일 읽은거임")
    @ApiResponses(value = {
            @ApiResponse(),
            @ApiResponse(responseCode = "500", description = "내부 서버 문제임")
    })
    public String getServerInfo(Model model) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> jsonMap = objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});
        Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");

        for (Map<String, Object> item : checkResults) {
            item.put("Server_Info", serverInfoMap);
        }


        int total = checkResults.size();
        int safeCount = 0;
        int vulnerableCount = 0;
        int otherCount = 0;

        model.addAttribute("serverInfo", serverInfoMap); //서버 인포
        model.addAttribute("checkResults", checkResults); //체크 리스트

        logger.info("서버 켜짐");
        logger.error("타임 리프 문법 에러");
        logger.error("타임 리프 매핑 에러");

//        logger.atError("{}", "ㅅㅂ");
        return "pages/main";
    }
}
