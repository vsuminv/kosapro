package exam.Kosademo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${json.directory}")
    private String jsonFileDirectory;

    @Value("${result.json.path}")
    private String resultJsonPath;

    @Value("${centos.script.path}")
    private String centosScriptPath;

    public void connecting(String username, String password, String host, int port) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        try {
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("exec");
            channel.connect();
            log.info("커넥성공");
        }catch (JSchException e ){
            e.getCause();
        }
    }

    public void runCheck(String username, String password, String host, int port, String scriptPath, String jsonPath) throws JSchException, SftpException, IOException, InterruptedException {
        Session session = createSession(username, password, host, port);
        String output = executeCommand(session, "sudo bash goAgent.sh");
        log.info("커맨드 실행.  output: {}", output);

        downloadLatestJsonFile();

//        downloadFile(session, "result.json", jsonPath);
//        uploadFile(session, scriptPath);
//        cleanupFiles(session, "agent.sh", "result.json");
//        disconnectSession(session);
    }

    private Session createSession(String username, String password, String host, int port) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        return session;
    }

    public void uploadFile(Session session, String localFilePath) throws JSchException, SftpException, FileNotFoundException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        sftpChannel.put(new FileInputStream(localFilePath), "agent.sh");
        sftpChannel.disconnect();
    }

    public String executeCommand(Session session, String command) throws JSchException, IOException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        InputStream in = channel.getInputStream();
        channel.connect();

        StringBuilder output = new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                output.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) continue;
                output.append("exit-status: ").append(channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        channel.disconnect();
        return output.toString();
    }

    public void downloadFile(Session session, String remoteFilePath) throws JSchException, SftpException, IOException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        sftpChannel.get(remoteFilePath);//여기에 localeDate 추가해주고싶다
        sftpChannel.disconnect();
    }

    private void cleanupFiles(Session session, String... fileNames) throws JSchException, IOException {
        StringBuilder command = new StringBuilder("rm");
        for (String fileName : fileNames) {
            command.append(" ").append(fileName);
        }
        executeCommand(session, command.toString());
    }

    private void disconnectSession(Session session) {
        session.disconnect();
    }

    public void uploadJsonFile(File jsonFile) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(jsonFile.getName())
                            .build(),
                    jsonFile.toPath());
            log.info("버켓 : [{}] 으로 업로드 됨 ", bucketName);
        }catch (Exception e){
            log.error("업로드 실패: {}", e.getMessage());
        }

    }

    public File downloadLatestJsonFile() throws IOException {
        Optional<File> jsonFile = findLatestJsonFile();
        if (jsonFile.isEmpty()) {
            throw new FileNotFoundException("No JSON file found in directory: " + jsonFileDirectory);
        }
        return jsonFile.get();
    }

    private Optional<File> findLatestJsonFile() {
        File dir = new File(jsonFileDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("Invalid JSON file directory: {}", jsonFileDirectory);
            return Optional.empty();
        }

        File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length == 0) {
            log.warn("No JSON files found in directory: {}", jsonFileDirectory);
            return Optional.empty();
        }
        return Arrays.stream(jsonFiles)
                .max(Comparator.comparingLong(File::lastModified));
    }

    public Map<String, Object> parseJsonFile(File jsonFile) throws IOException {
        try (InputStream inputStream = new FileInputStream(jsonFile)) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        }
    }
}