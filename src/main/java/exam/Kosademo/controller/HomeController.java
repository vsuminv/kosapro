package exam.Kosademo.controller;

import exam.Kosademo.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/result")
    public String getCheckResult(Model model) throws IOException {
        Map<String, Object> checkResultData = homeService.getCheckResultData();
        model.addAllAttributes(checkResultData);
        return "pages/main";
    }
}