package com.goryaninaa.web.HttpServer.entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Response;

public class HttpResponse implements Response {
	private HttpResponseCode httpResponseCode;
	private Map<String, String> headers;
    private String response;

    public HttpResponse(HttpResponseCode httpResponseCode) {
    	this.httpResponseCode = httpResponseCode;
    	response = this.httpResponseCode.getStartLine();
    }
    
    public HttpResponse(HttpResponseCode httpResponseCode, String body) {
    	this.httpResponseCode = httpResponseCode;
    	defineHeaders();
    	
        this.response = combine(httpResponseCode, body);
    }
    
    public HttpResponse(HttpResponseCode httpResponseCode, Map<String, String> additionalHeaders, String body) {
    	this.httpResponseCode = httpResponseCode;
    	defineHeaders(additionalHeaders);
    	
        this.response = combine(httpResponseCode, body);
    }

	public String getResponseString() {
        return response;
    }
	
	public HttpResponseCode getCode() {
		return httpResponseCode;
	}
	
    private String combine(HttpResponseCode httpResponseCode, String body) {
    	String response = httpResponseCode.getStartLine();
    	
    	for (Entry<String, String> header : headers.entrySet()) {
    		response += header.getKey() + ": " + header.getValue() + "\n";
    	}
    	
    	response += "\n" + body;
    	
		return response;
	}
    
    private void defineHeaders() {
    	headers = new LinkedHashMap<String, String>(15, 0.75f, false);
    	headers.put("Server", "RagingServer");
    }
    
    private void defineHeaders(Map<String, String> additionalHeaders) {
    	defineHeaders();
    	
    	for (Entry<String, String> additionalHeader : additionalHeaders.entrySet()) {
    		headers.put(additionalHeader.getKey(), additionalHeader.getValue());
    	}
    }
}
