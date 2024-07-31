package exam.Kosademo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        String tq = "tlqkf";
        model.addAttribute(tq);
        return "pages/home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 데이터1
//            List<DataRow> dataRows = model.addAttribute("dataRows", dataRows);
        return "dashboard";
    }


    @GetMapping("/cards")
    public String cards() {
        return "fragment/cards";
    }

}
