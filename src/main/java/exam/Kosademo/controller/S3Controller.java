package exam.Kosademo.controller;

import com.jcraft.jsch.*;
;
import exam.Kosademo.service.S3Service;
import exam.Kosademo.service.TestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3;
    private final TestService t;
    private final HttpSession httpSession;

    @Value("${centos.script.path}")
    private String centosScriptPath;

    @Value("${result.json.path}")
    private String resultJsonPath;

    @Value("${ssh.script.path}")
    private String scriptPath;

    @Value("${ssh.json.path}")
    private String jsonPath;

    @PostMapping("/run")
    public RedirectView start(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("ipAddress") String ipAddress,
                              @RequestParam("port") int port,
                              RedirectAttributes redirectAttributes) {
        try {
            s3.connecting(username, password, ipAddress, port);
            s3.runCheck(username, password, ipAddress, port, scriptPath, jsonPath);
            s3.uploadJsonFile(s3.downloadLatestJsonFile());

            redirectAttributes.addFlashAttribute("successMessage", "에이전트 작업 성공");
            return new RedirectView("/result");
        } catch (JSchException | SftpException | IOException e) {
            log.error("에이전트 실행 또는 S3 업로드 중 에러 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "에러: " + e.getMessage());
            return new RedirectView("/");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/Test")
    public RedirectView test(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("ipAddress") String ipAddress,
                              @RequestParam("port") int port,
                              RedirectAttributes redirectAttributes) {
        try {
            log.info("CentOS6.sh 경로: {}", centosScriptPath);
            log.info("result.json 경로: {}", resultJsonPath);
            t.connecting(username, password, ipAddress, port);
            t.test(username, password, port, ipAddress, centosScriptPath, resultJsonPath);
//            t.renameJsonFile(resultJsonPath);
//            t.renameJsonFile(resultJsonPath);

            redirectAttributes.addFlashAttribute("successMessage", " 실행 성공");
            return new RedirectView("/result");
        } catch (JSchException e) {

            log.error("에이전트 실행 또는 S3 업로드 중 에러 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "에러: " + e.getMessage());
            return new RedirectView("/");
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }



    //세션에서 클라로 받기 나주ㅡㅇ에하자
    @PostMapping("/down")
    public RedirectView down(Model model, RedirectAttributes redirectAttributes) {
        List<String> models = new ArrayList<>();
        model.addAttribute("username", httpSession.getAttribute("username"));
        model.addAttribute("ipAddress", httpSession.getAttribute("ipAddress"));
        model.addAttribute("port", httpSession.getAttribute("port"));
        model.addAttribute("scriptPath", scriptPath);
        model.addAttribute("jsonPath", jsonPath);
        model.addAttribute("path", scriptPath);
        return null;
    }
}



