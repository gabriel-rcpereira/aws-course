package com.grcp.aws.project_01.model;

public class UrlResponse {

    private final String url;
    private final long expirationTime;

    public UrlResponse(String url, long expirationTime) {
        this.url = url;
        this.expirationTime = expirationTime;
    }

    public String getUrl() {
        return url;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}
