package com.example.jsonviewer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerInfo {
    @JsonProperty("SW_TYPE")
    private String swType;

    @JsonProperty("SW_NM")
    private String swNm;

    @JsonProperty("SW_INFO")
    private String swInfo;

    @JsonProperty("HOST_NM")
    private String hostNm;

    @JsonProperty("DATE")
    private String date;

    @JsonProperty("TIME")
    private String time;

    @JsonProperty("IP_ADDRESS")
    private String ipAddress;

    @JsonProperty("UNIQ_ID")
    private String uniqId;

    // Getters and setters
    public String getSwType() {
        return swType;
    }

    public void setSwType(String swType) {
        this.swType = swType;
    }

    public String getSwNm() {
        return swNm;
    }

    public void setSwNm(String swNm) {
        this.swNm = swNm;
    }

    public String getSwInfo() {
        return swInfo;
    }

    public void setSwInfo(String swInfo) {
        this.swInfo = swInfo;
    }

    public String getHostNm() {
        return hostNm;
    }

    public void setHostNm(String hostNm) {
        this.hostNm = hostNm;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUniqId() {
        return uniqId;
    }

    public void setUniqId(String uniqId) {
        this.uniqId = uniqId;
    }
}
