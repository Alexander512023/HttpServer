package com.goryaninaa.web.HttpServer.server;

import com.goryaninaa.web.HttpServer.model.HttpResponse;

public class RequestHandlerStab implements RequestHandler {

	@Override
	public HttpResponse handle(String request) {
		try {
			Thread.sleep(100);
			return new HttpResponse(request);
		} catch (InterruptedException e) {
		}
		return null;
	}

}
