package exam.Kosademo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class ResultController {

    // Class-level resource, initialized directly
    private final ClassPathResource resource = new ClassPathResource("result2.json");


    // serverInfo 여러개 있을 때
//    @GetMapping("/result1")
//    public String getResultDetail(Model model) {
//        try {
//            // JSON 파일을 읽어와서 Map으로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, Object> jsonMap = objectMapper.readValue(
//                    resource.getInputStream(),
//                    new TypeReference<Map<String, Object>>() {});
//
//            // JSON 객체에서 'servers' 배열을 추출
//            List<Map<String, Object>> serverInfoList = (List<Map<String, Object>>) jsonMap.get("Server_Info");
//
//            // 모델에 serverInfoList 추가
//            model.addAttribute("serverInfoList", serverInfoList);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return "resultTarget";
//    }

    @GetMapping("/result1")
    public String getResultTarget(Model model) {
        try {
            // ObjectMapper를 사용하여 JSON을 Java Map 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});

            // JSON 객체에서 'Server_Info' 객체를 추출
            Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");

            // 모델에 serverInfoMap 추가
            model.addAttribute("serverInfo", serverInfoMap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "resultTarget";
    }


    @GetMapping("/result5")
    public String getResultDetail(Model model) {
        try {
            // ObjectMapper를 사용하여 JSON을 Java Map 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});

            Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
            model.addAttribute("serverInfo", serverInfoMap);

            // Check_Results 리스트 가져오기
            List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");

            // 각 Check_Result에 Server_Info 추가
            for (Map<String, Object> item : checkResults) {
                item.put("Server_Info", serverInfoMap);
            }

            // 모델에 Check_Results 추가
            model.addAttribute("checkResults", checkResults);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "resultDetail";
    }


}
