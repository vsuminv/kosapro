package exam.Kosademo.controller;

import exam.Kosademo.service.PythonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
@Slf4j
@Controller
@RequiredArgsConstructor
public class PythonController {

    private final PythonService pythonService;

    @Value("${python.script.path}")
    private String pythonScriptPath;

    @Value("${python.excel.path}")
    private String excelPath;

    @GetMapping("/run-python")
    public ResponseEntity<InputStreamResource> runPythonScript() {
        try {
            pythonService.executePythonScript(pythonScriptPath);
            File excelFile = pythonService.getLatestExcelFile(excelPath);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException | InterruptedException e) {
            log.error("Error during Python script execution", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}