package com.goryaninaa.web.HttpServer.model;

public class HttpResponse {
    private String response;

    public HttpResponse(String response) {
        this.response = response;
    }

    public String getResponseString() {
        return response;
    }
}
