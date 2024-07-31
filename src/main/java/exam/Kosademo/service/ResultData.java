package exam.Kosademo.service;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class ResultData {
    private Map<String, Object> serverInfoMap;
    private Map<String, Map<String, Integer>> categorizedResults;
    private Map<String, Double> categorySecurityIndices;
    private double overallSecurityIndex;
    private String categorySecurityIndicesJson;

}