package exam.Kosademo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonService {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    public Optional<Map<String, Object>> getCheckResultData() {
        try {
            String mostRecentFileKey = fetchMostRecentJsonFileKey();
            File jsonFile = downloadJsonFileFromS3(mostRecentFileKey);
            Map<String, Object> jsonMap = parseJsonFile(jsonFile);
            // JSON 내용 가공 해서 담아서 던짐 . 현재는 다운 받은 후에 가공해서 뿌리는중.

            // ...
            return Optional.of(jsonMap);
        } catch (IOException e) {
            log.error("Failed to get check result data", e);
            return Optional.empty();
        }
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
            log.error("No JSON files found in the bucket.");
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
        log.info("Parsing JSON file: {}", jsonFile.getAbsolutePath());
        try (InputStream inputStream = new FileInputStream(jsonFile)) {
            return objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
        }
    }
}
