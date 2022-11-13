package com.goryaninaa.web.HttpServer.server;

import com.goryaninaa.web.HttpServer.model.HttpResponse;
import com.goryaninaa.web.HttpServer.model.HttpResponseCode;

public class RequestHandlerStab implements RequestHandler {

	@Override
	public HttpResponse handle(String request) {
		try {
			Thread.sleep(100);
			return new HttpResponse(HttpResponseCode.OK, request);
		} catch (InterruptedException e) {
		}
		return null;
	}

}
