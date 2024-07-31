package exam.Kosademo.controller;

import exam.Kosademo.model.ExtendedModel;
import exam.Kosademo.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Result Controller", description = "결과 정보 조회")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/result1")
    @Operation(summary = "Get Result Target", description = "결과 대상 정보를 가져옵니다.")
    public String getResultTarget(ExtendedModel model) {
        model.addAttribute("serverInfo", resultService.getServerInfoFromJson(), true);
        model.validateAttribute("serverInfo");
        return "resultTarget";
    }

    @GetMapping("/result5")
    @Operation(summary = "Get Result Detail", description = "결과 상세 정보를 가져옵니다.")
    public String getResultDetail(ExtendedModel model) {
        model.addAttribute("serverInfo", resultService.getServerInfoFromJson(), true);
        model.addAttribute("checkResults", resultService.getCheckResultsFromJson(), true);
        model.validateAttribute("serverInfo");
        model.validateAttribute("checkResults");
        return "resultDetail";
    }
}