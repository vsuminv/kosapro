package exam.Kosademo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Controller
@Tag(name = "Controller", description = "API 명세서")
@RequiredArgsConstructor
public class HomeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String main(){
        return "pages/main";
    }
    @GetMapping("/result")
    @Operation(summary = "resource/objectMapper", description = "서버 정보 및 결과")
    public String getData(Model model) throws IOException {
        // 최신 JSON 파일의 키를 가져옵니다.
        String mostRecentFileKey = fetchMostRecentJsonFileKey();
        logger.info("Most recent JSON file key: {}", mostRecentFileKey);

        if (mostRecentFileKey == null) {
            model.addAttribute("message", "No JSON file found in the bucket.");
            return "pages/main";
        }

        // JSON 파일을 다운로드합니다.
        File jsonFile = downloadJsonFileFromS3(mostRecentFileKey);

        // JSON 파일을 파싱합니다.
        Map<String, Object> jsonMap = parseJsonFile(jsonFile);
        Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
        List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");

        // checkResults에서 항목, 상태만 뽑은 맵
        Map<String, Map<String, Integer>> categorizedResults = new TreeMap<>();
        for (Map<String, Object> item : checkResults) {
            String category = (String) item.get("Category");
            String status = (String) item.get("status");
            Map<String, Integer> stats = categorizedResults.getOrDefault(category, new HashMap<>());
            stats.put(status, stats.getOrDefault(status, 0) + 1);
            categorizedResults.put(category, stats);
        }

        // 개별 안정성
        Map<String, Double> categorySecurity = new TreeMap<>();
        for (String category : categorizedResults.keySet()) {
            Map<String, Integer> stats = categorizedResults.get(category);
            int safe = stats.getOrDefault("[양호]", 0);
            int vulnerable = stats.getOrDefault("[취약]", 0);
            int na = stats.getOrDefault("[N/A]", 0);
            int total = safe + vulnerable + na;
            double securityIndex = total == 0 ? 0.0 : (double) safe / total * 100;
            categorySecurity.put(category, Double.parseDouble(String.format("%.2f", securityIndex)));
        }


        // 서버 전체 안정성
        int totalSafe = 0, totalVulnerable = 0, totalNA = 0;
        for (Map<String, Integer> stats : categorizedResults.values()) {
            totalSafe += stats.getOrDefault("[양호]", 0);
            totalVulnerable += stats.getOrDefault("[취약]", 0);
            totalNA += stats.getOrDefault("[N/A]", 0);
        }
        int overallTotal = totalSafe + totalVulnerable + totalNA;
        double allSecurity = overallTotal == 0 ? 0.0 : (double) totalSafe / overallTotal * 100;
        allSecurity = Double.parseDouble(String.format("%.2f", allSecurity));


        // checkResults에서 중요도 뽑고, 상태 뽑아서 중요도별로 차트 3개 만들거임.
        Map<String, Map<String, Integer>> chart = new TreeMap<>();
        for (Map<String, Object> item : checkResults) {
            String importance = (String) item.get("Importance");
            String status = (String) item.get("status");
            // Importance가 없으면 새로운 맵 생성
            chart.putIfAbsent(importance, new HashMap<>());
            // status 개수 증가
            Map<String, Integer> statusMap = chart.get(importance);
            statusMap.put(status, statusMap.getOrDefault(status, 0) + 1);
        }


        // section1
        Map<String, Object> section1 = new HashMap<>();
        section1.put("DATE", serverInfoMap.get("DATE"));
        section1.put("SW_INFO", serverInfoMap.get("SW_INFO"));
        section1.put("allSecurity", allSecurity);


        // section2
        Map<String, Object> section2 = new HashMap<>();
        section2.put("Category", serverInfoMap.get("Category"));
        section2.put("Importance", serverInfoMap.get("Importance"));
        section2.put("status", serverInfoMap.get("status"));
        section2.put("Sub_Category", serverInfoMap.get("Sub_Category"));



        // section3
        List<Map<String, Object>> section3 = new ArrayList<>();
        for (Map<String, Object> result : checkResults) {
            Map<String, Object> data = new HashMap<>();
            data.put("OS", serverInfoMap.get("SW_NM"));
            data.put("Host", serverInfoMap.get("HOST_NM"));
            data.put("IP", serverInfoMap.get("IP_ADDRESS"));
            data.put("Category", result.get("Category"));
            data.put("Importance", result.get("Importance"));
            data.put("Status", result.get("status"));
            data.put("Sub_Category", result.get("Sub_Category"));
            section3.add(data);
        }


        // section4
        String report = "업로드 공간";



        // 모델에 데이터 추가
        model.addAttribute("section1", section1);
        model.addAttribute("section2", section2);
        model.addAttribute("section3", section3);
        model.addAttribute("checkResults", checkResults);
        model.addAttribute("categorizedResults", categorizedResults);
        model.addAttribute("categorySecurity", categorySecurity);
        model.addAttribute("allSecurity", allSecurity);
        model.addAttribute("chart", chart);

        log.info(chart.toString());

        return "pages/main";
    }

    private String fetchMostRecentJsonFileKey() throws IOException {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

        String mostRecentFileKey = null;
        long mostRecentTimestamp = Long.MIN_VALUE;

        for (var s3Object : listObjectsResponse.contents()) {
            String key = s3Object.key();
            if (key.endsWith(".json")) {
                if (s3Object.lastModified().toEpochMilli() > mostRecentTimestamp) {
                    mostRecentFileKey = key;
                    mostRecentTimestamp = s3Object.lastModified().toEpochMilli();
                }
            }
        }

        if (mostRecentFileKey == null) {
            logger.error("No JSON files found in the bucket.");
            throw new IOException("No JSON files found in the bucket.");
        }

        return mostRecentFileKey;
    }

    private File downloadJsonFileFromS3(String fileKey) throws IOException {
        Path filePath = Paths.get("downloaded_" + fileKey);
        File file = filePath.toFile();

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build())) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return file;
    }

    private Map<String, Object> parseJsonFile(File jsonFile) throws IOException {
        logger.info("Parsing JSON file: {}", jsonFile.getAbsolutePath());
        try (InputStream inputStream = new FileInputStream(jsonFile)) {
            return objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
        }
    }
}
