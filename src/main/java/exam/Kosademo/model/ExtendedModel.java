package exam.Kosademo.model;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.ui.ModelMap;

public class ExtendedModel extends ModelMap {
    @Operation(summary = "Add Attribute", description = "모델에 속성을 추가합니다.")
    public ExtendedModel addAttribute(String attributeName, Object attributeValue, boolean required) {
        addAttribute(attributeName, attributeValue);
        if (required && attributeValue == null) {
            throw new IllegalArgumentException("@@@@@@@@@@@@@@@@@@Required attribute '" + attributeName + "' is missing.");
        }
        return this;
    }
    @Operation(summary = "Validate Attribute", description = "모델의 속성 존재 여부를 검사합니다.")
    public void validateAttribute(String attributeName) {
        if (!containsAttribute(attributeName)) {
            throw new IllegalArgumentException("@@@@@@@@@@@@@@@@@@@@@Attribute '" + attributeName + "' is not present in the model.");
        }
    }
}