package com.goryaninaa.web.HttpServer.requestHandler;

import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Response;

public class ResponseStub implements Response {

	private final HttpResponseCode httpResponseCode;

	public ResponseStub(HttpResponseCode httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

	@Override
	public String getResponseString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpResponseCode getCode() {
		return httpResponseCode;
	}

}
