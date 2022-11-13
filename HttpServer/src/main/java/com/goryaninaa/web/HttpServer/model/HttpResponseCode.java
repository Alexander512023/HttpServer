package com.goryaninaa.web.HttpServer.model;

public enum HttpResponseCode {
	
	OK(200, "HTTP/1.1 200 OK\n"),
	NOTFOUND(404, "HTTP/1.1 404 Not Found\n");
	
	private final int code;
	private final String startLine;
	
	HttpResponseCode(int code, String startLine) {
		this.code = code;
		this.startLine = startLine;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getStartLine() {
		return startLine;
	}
}
