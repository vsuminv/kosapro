//package exam.Kosademo.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.view.RedirectView;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.Optional;
//
//@Slf4j
//@Controller
//public class GoAgentController {
//
//    @Value("${json.file.path}")
//    private String jsonFileDirectory;
//
//    private final S3Client s3Client;
//    private final String bucketName;
//
//    public GoAgentController(S3Client s3Client, @Value("${aws.s3.bucket.name}") String bucketName) {
//        this.s3Client = s3Client;
//        this.bucketName = bucketName;
//    }
//
//    /* @PostMapping("run")
//     public RedirectView runPythonScript runPythonScript(RedirectAttributes redirectAttributes) {
//    */   @PostMapping("run")
//    public RedirectView runPythonScript(RedirectAttributes redirectAttributes) {
//
//        try {
//            // 클래스패스에서 Python 스크립트 파일 가져오기
//            ClassPathResource scriptResource = new ClassPathResource("goAgent.py");
//            File tempScriptFile = File.createTempFile("goAgent", ".py");
//            Files.copy(scriptResource.getInputStream(), tempScriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            tempScriptFile.deleteOnExit(); // 임시 파일 삭제를 보장
//
//            // Python 스크립트 실행
//            String result = runPythonScript(tempScriptFile.getAbsolutePath());
//            log.info("Python script output: {}", result);
//
//            // 디렉토리에서 JSON 파일 찾기
//            Optional<File> jsonFileOptional = findJsonFile();
//
//            if (jsonFileOptional.isEmpty()) {
//                log.error("No JSON file found in directory: " + jsonFileDirectory);
//                throw new FileNotFoundException("Generated JSON file not found in directory: " + jsonFileDirectory);
//            }
//
//            File file = jsonFileOptional.get();
//            log.info("JSON 파일 위치: " + file.getAbsolutePath());
//
//            // S3로 JSON 파일 업로드
//            s3Client.putObject(PutObjectRequest.builder()
//                            .bucket(bucketName)
//                            .key(file.getName())
//                            .build(),
//                    file.toPath());
//
//            System.out.println("JSON file uploaded to S3 bucket: " + bucketName);
//
//            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
//            // 파일 다운로드 응답 반환
//            /*return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(resource);*/
//            return new RedirectView("/result");
//        } catch (IOException | InterruptedException e) {
//            log.error("Error during script execution or file handling", e);
//            return new RedirectView("/"); // 오류 발생 시 홈으로 리다이렉트
//            //return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
//        } catch (RuntimeException e) {
//            log.error("Runtime exception", e);
//            return new RedirectView("/"); // 오류 발생 시 홈으로 리다이렉트
//            //return ResponseEntity.status(500).body(new InputStreamResource(new ByteArrayInputStream(e.getMessage().getBytes())));
//        }
//    }
//
//    private String runPythonScript(String scriptPath) throws IOException, InterruptedException {
//        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);
//        processBuilder.redirectErrorStream(true);
//
//        Process process = processBuilder.start();
//        StringBuilder output = new StringBuilder();
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//        }
//
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            log.error("Python script execution failed with exit code: " + exitCode);
//            log.error("Python script error output: \n" + output.toString());
//            throw new RuntimeException("Python script execution failed with exit code: " + exitCode);
//        }
//
//        return output.toString();
//    }
//
//    private Optional<File> findJsonFile() {
//        File dir = new File(jsonFileDirectory);
//        if (!dir.exists() || !dir.isDirectory()) {
//            log.error("Invalid JSON file directory: " + jsonFileDirectory);
//            return Optional.empty();
//        }
//
//        // 조건에 맞는 파일을 찾는 로직
//        File[] files = dir.listFiles((d, name) -> name.startsWith("result_") && name.endsWith(".json"));
//        if (files == null || files.length == 0) {
//            return Optional.empty();
//        }
//
//        // 가장 최근에 수정된 파일을 선택
//        File latestFile = files[0];
//        for (File file : files) {
//            if (file.lastModified() > latestFile.lastModified()) {
//                latestFile = file;
//            }
//        }
//
//        return Optional.of(latestFile);
//    }
//}


package exam.Kosademo.controller;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.file.Paths;
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

    @PostMapping("/run")
    public RedirectView runScript(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam("ipAddress") String ipAddress,
                                  @RequestParam("port") int port,
                                  RedirectAttributes redirectAttributes) {

        log.info(" 방화벽 확인해보자 입력 : {}, 아이피: {}, 포트: {}", username, ipAddress, port);

        try {
            // SSH를 통해 스크립트 실행 및 JSON 파일 다운로드
            executeAgentScriptOverSSH(username, password, ipAddress, port);

            // JSON 파일을 S3로 업로드
            Optional<File> jsonFileOptional = findJsonFile();
            if (jsonFileOptional.isEmpty()) {
                log.error("No JSON file found in directory: " + jsonFileDirectory);
                redirectAttributes.addFlashAttribute("errorMessage", "Generated JSON file not found in directory.");
                return new RedirectView("/");
            }

            File file = jsonFileOptional.get();
            log.info("JSON 파일 위치: " + file.getAbsolutePath());

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file.getName())
                            .build(),
                    file.toPath());

            log.info("제이슴ㄴ 파일 S3 bucket 로 : " + bucketName);

            // 성공 메시지 설정
            redirectAttributes.addFlashAttribute("successMessage", "Agent works successfully!");
            return new RedirectView("/result");
        } catch (Exception e) {
            log.error("Error during SSH execution or S3 upload", e);
            // 오류 메시지 설정
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return new RedirectView("/");
        }
    }

    private void executeAgentScriptOverSSH(String username, String password, String host, int port) throws JSchException, IOException, SftpException, InterruptedException {
        log.info("커넥팅 성공 SSH server at {}:{} with username: {}", host, port, username);

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        log.info("SSH 세션 connected.");

        // SFTP를 통해 파일 업로드
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        sftpChannel.put(getResourcePath("CentOS6.sh"), "agent.sh");

        String s = sftpChannel.toString();
        log.info("SFTP를 통해 파일 업로드 connect 파일명 : {} dst: {}", sftpChannel.get(s));

        // SSH를 통해 명령 실행
        ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
        execChannel.setCommand("sudo bash agent.sh");
        execChannel.setErrStream(System.err);

        InputStream in = execChannel.getInputStream();
        execChannel.connect();

        // 명령 실행 후 결과 처리
        StringBuilder output = new StringBuilder();
        log.info("output  : {}", output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }//줄바꿈
        execChannel.disconnect();
        log.info("다 읽고 나오는거: " + output);

        // JSON 파일 다운로드 여기서 No such file 뜸
        sftpChannel.get("result.json", getResourcePath("result.json"));

        Object a = sftpChannel.get("result.json");
        Object b = sftpChannel.get(getResourcePath("result.json"));
        log.info("경로 확인 {} {}",a,b);

        // 서버의 파일 정리
        execChannel = (ChannelExec) session.openChannel("exec");
        execChannel.setCommand("rm agent.sh result.json");
        execChannel.connect();
        execChannel.disconnect();

        sftpChannel.disconnect();
        session.disconnect();

        log.info("SSH session disconnected.");
    }


    private String getResourcePath(String fileName) {
        // 절대 경로 사용
        try {
            String basePath = "C:\\Kosa\\src\\main\\resources"; // 직접 경로를 설정
            return Paths.get(basePath, fileName).toString();
        } catch (Exception e) {
            return e.toString();
        }

    } //여기서 에러

    private Optional<File> findJsonFile() {
        File dir = new File(jsonFileDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("Invalid JSON file directory: " + jsonFileDirectory);
            return Optional.empty();
        }

        // 디렉토리 내용 로그 추가
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                log.info("File in directory: " + file.getName());
            }
        } else {
            log.error("Directory is empty or not accessible.");
        }

        // JSON 파일을 찾는 로직
        File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length == 0) {
            log.error("No JSON file found in directory: " + jsonFileDirectory);
            return Optional.empty();
        }

        // 가장 최근에 수정된 파일을 선택
        File latestFile = jsonFiles[0];
        for (File file : jsonFiles) {
            if (file.lastModified() > latestFile.lastModified()) {
                latestFile = file;
            }
        }

        return Optional.of(latestFile);
    }
}
