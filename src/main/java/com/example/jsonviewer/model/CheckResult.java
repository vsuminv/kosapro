package com.example.jsonviewer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CheckResult {
    @JsonProperty("Item")
    private String item;

    @JsonProperty("Importance")
    private String importance;

    @JsonProperty("Category")
    private String category;

    @JsonProperty("Sub_Category")
    private String subCategory;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("details")
    private List<String> details;

    @JsonProperty("solutions")
    private List<String> solutions;

    // Getters and setters
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<String> solutions) {
        this.solutions = solutions;
    }
}
