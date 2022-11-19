package com.goryaninaa.web.HttpServer.entity;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.goryaninaa.web.HttpServer.json.serializer.JsonSerializer;
import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Response;

public class HttpResponse implements Response {
	private HttpResponseCode httpResponseCode;
	private Map<String, String> headers;
    private String response;
    private Serializer serializer = new JsonSerializer();

    public HttpResponse(HttpResponseCode httpResponseCode) {
    	this.httpResponseCode = httpResponseCode;
    	defineHeaders();
    	
    	response = this.httpResponseCode.getStartLine();
    }
    
    public HttpResponse(HttpResponseCode httpResponseCode, String body) {
    	this.httpResponseCode = httpResponseCode;
    	defineHeaders(body);
    	
        this.response = combine(httpResponseCode, body);
    }
    


	public <T> HttpResponse(HttpResponseCode httpResponseCode, T responseObject) {
    	this.httpResponseCode = httpResponseCode;
    	String body = serializer.serialize(responseObject);
    	defineHeaders(responseObject, body);
    	
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
    
    private void defineHeaders(String body) {
    	defineHeaders();
    	
    	headers.put("Content-Type", "text/html; charset=utf-8");
    	try {
			headers.put("Content-Length", String.valueOf(body.getBytes("UTF-8").length));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("Unsupported encoding");
		}
	}
    
	private <T> void defineHeaders(T responseObject, String body) {
		defineHeaders();
		
		headers.put("Content-Type", "application/json");
		try {
			headers.put("Content-Length", String.valueOf(body.getBytes("UTF-8").length));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("Unsupported encoding");
		}
	}
    
    private void defineHeaders() {
    	headers = new LinkedHashMap<String, String>(15, 0.75f, false);
    	headers.put("Server", "RagingServer");
    	headers.put("Connection", "close");
    	headers.put("Date", LocalDateTime.now().toString());
    }
}
