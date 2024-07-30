package exam.Kosademo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@Tag(name = "Python Controller", description = "Python 스크립트 실행 및 엑셀 파일 다운로드")
public class PythonController {

    @Value("classpath:static/make_result.py")
    private String pythonScriptPath;

    @Value("classpath:static/")
    private String excelFilePath;

    @GetMapping("/run-python")
    public ResponseEntity<InputStreamResource> runPythonScript() {
        try {
            // Python 스크립트 파일 경로 잡기
            File pythonScriptFile = ResourceUtils.getFile(pythonScriptPath);
            String absolutePythonScriptPath = pythonScriptFile.getAbsolutePath();

            // Python 스크립트 파일 실행
            ProcessBuilder processBuilder = new ProcessBuilder("python", absolutePythonScriptPath);
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

            // 생성된 엑셀 파일 이름 yyyyMMdd로 포매팅저장함. 확장자는 엑셀
            String excelFileName = "result_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            File excelFile = ResourceUtils.getFile(excelFilePath + excelFileName);

            if (!excelFile.exists()) {
                throw new FileNotFoundException("Generated Excel file not found: " + excelFileName);
            }

            InputStreamResource fileResource = new InputStreamResource(new FileInputStream(excelFile));

            // 파일 다운로드 받으면 응답 반환함
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileResource);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        }
    }
}