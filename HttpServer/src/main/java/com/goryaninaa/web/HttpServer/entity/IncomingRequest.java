package com.goryaninaa.web.HttpServer.entity;

import com.goryaninaa.web.HttpServer.requesthandler.In;

public class IncomingRequest implements In {

	@Override
	public HttpRequest httpRequestFrom(String requestString) {
		return new HttpRequest(requestString);
	}

}
