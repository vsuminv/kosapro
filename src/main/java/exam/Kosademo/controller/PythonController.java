package exam.Kosademo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Slf4j
@Controller
@Tag(name = "PythonController", description = "파이선 작동")
public class PythonController  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/run-python")
    public ResponseEntity<InputStreamResource> runPythonScript() throws IOException{

        try {
            // Python 스크립트 실행
            ProcessBuilder processBuilder = new ProcessBuilder("python", "C:/Spring/project/KosaProject/src/main/resources/static/make_result.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Python 스크립트의 출력 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script execution failed with exit code " + exitCode + "\n" + output);
            }

            // 생성된 엑셀 파일 이름
            String excelFileName = "result_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            File file = new File("C:/Spring/project/KosaProject/src/main/resources/static/" + excelFileName);
            
            if (!file.exists()) {
                throw new FileNotFoundException("Generated Excel file not found: " + excelFileName);
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // 파일 다운로드 응답 반환
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        }
//        logger.info("dd",);
    }
}
