package exam.Kosademo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
@Controller
public class GoAgentController {

    @Value("${json.file.path}")
    private String jsonFileDirectory;

    private final S3Client s3Client;
    private final String bucketName;

    public GoAgentController(S3Client s3Client, @Value("${aws.s3.bucket.name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

   /* @PostMapping("run")
    public RedirectView runPythonScript runPythonScript(RedirectAttributes redirectAttributes) {
   */   @PostMapping("/run")
        public RedirectView runPythonScript(RedirectAttributes redirectAttributes) {

       try {
            // 클래스패스에서 Python 스크립트 파일 가져오기
            ClassPathResource scriptResource = new ClassPathResource("goAgent.py");
            File tempScriptFile = File.createTempFile("goAgent", ".py");
            Files.copy(scriptResource.getInputStream(), tempScriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempScriptFile.deleteOnExit(); // 임시 파일 삭제를 보장

            // Python 스크립트 실행
            String result = runPythonScript(tempScriptFile.getAbsolutePath());
            log.info("Python script output: {}", result);

            // 디렉토리에서 JSON 파일 찾기
            Optional<File> jsonFileOptional = findJsonFile();

            if (jsonFileOptional.isEmpty()) {
                log.error("No JSON file found in directory: " + jsonFileDirectory);
                throw new FileNotFoundException("Generated JSON file not found in directory: " + jsonFileDirectory);
            }

            File file = jsonFileOptional.get();
            log.info("JSON 파일 위치: " + file.getAbsolutePath());

            // S3로 JSON 파일 업로드
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file.getName())
                            .build(),
                    file.toPath());

            System.out.println("JSON file uploaded to S3 bucket: " + bucketName);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // 파일 다운로드 응답 반환
            /*return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);*/
            return new RedirectView("/result");
        } catch (IOException | InterruptedException e) {
            log.error("Error during script execution or file handling", e);
           return new RedirectView("/"); // 오류 발생 시 홈으로 리다이렉트
            //return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        } catch (RuntimeException e) {
            log.error("Runtime exception", e);
           return new RedirectView("/"); // 오류 발생 시 홈으로 리다이렉트
            //return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
        }
    }

    private String runPythonScript(String scriptPath) throws IOException, InterruptedException {
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
            log.error("Python script execution failed with exit code: " + exitCode);
            log.error("Python script error output: \n" + output.toString());
            throw new RuntimeException("Python script execution failed with exit code: " + exitCode);
        }

        return output.toString();
    }

    private Optional<File> findJsonFile() {
        File dir = new File(jsonFileDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("Invalid JSON file directory: " + jsonFileDirectory);
            return Optional.empty();
        }

        // 조건에 맞는 파일을 찾는 로직
        File[] files = dir.listFiles((d, name) -> name.startsWith("result_") && name.endsWith(".json"));
        if (files == null || files.length == 0) {
            return Optional.empty();
        }

        // 가장 최근에 수정된 파일을 선택
        File latestFile = files[0];
        for (File file : files) {
            if (file.lastModified() > latestFile.lastModified()) {
                latestFile = file;
            }
        }

        return Optional.of(latestFile);
    }
}
