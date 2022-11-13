package com.goryaninaa.web.HttpServer.requestHandler;

import java.util.Optional;

import com.goryaninaa.web.HttpServer.requesthandler.Request;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.HttpMethod;

public class RequestStub implements Request {
	private final String requestString;
	

	public RequestStub(String requestString) {
		this.requestString = requestString;
		if (requestString.equals("broke")) {
			throw new RuntimeException();
		}
	}

	@Override
	public Optional<String> getConttrollerMapping(int length) {
		return Optional.ofNullable(requestString.substring(0, length));
	}

	@Override
	public String getMapping() {
		return requestString;
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.GET;
	}

}
