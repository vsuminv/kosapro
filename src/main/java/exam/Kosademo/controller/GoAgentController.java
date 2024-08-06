package exam.Kosademo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Controller
public class GoAgentController {

    @Value("${python.script.path}")
    private String pythonScriptPath;

    @Value("${json.file.path}")
    private String jsonFilePath;

    @PostMapping("runrun")
    public ResponseEntity<InputStreamResource> runPythonScript(RedirectAttributes redirectAttributes) {
        try {
            // Python 스크립트 실행
            String result = runPythonScript(pythonScriptPath);
            // 생성된 JSON 파일 이름
            String jsonFileName = "result.json"; // 여기서 result.json은 스크립트에 의해 생성된 파일입니다
            File file = new File(jsonFilePath + jsonFileName);
            if (!file.exists()) {
                throw new FileNotFoundException("Generated JSON file not found: " + jsonFileName);
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            // 파일 다운로드 응답 반환
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + jsonFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        }
    }

    public static String runPythonScript(String scriptPath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script execution failed with exit code: " + exitCode);
        }

        return output.toString();
    }
}

//ssh -i "testkey1.pem" testClient1@3.35.206.80
