package exam.Kosademo.controller.my;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@Slf4j
@Component
@Tags({,})
public class ShellScriptRunner {

    @Tag(name = "runScript")
    public String runScript(String scriptPath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptPath);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        String output = getProcessOutput(process);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Shell script execution failed with exit code: " + exitCode);
        }

        return output;
    }

    private String getProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}