package com.goryaninaa.web.HttpServer.server;

import com.goryaninaa.web.HttpServer.entity.HttpResponse;
import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;

public class RequestHandlerStub implements RequestHandler {

	@Override
	public HttpResponse handle(String request) {
		try {
			Thread.sleep(100);
			return new HttpResponse(HttpResponseCode.OK, request);
		} catch (InterruptedException e) {
		}
		return null;
	}

	@Override
	public void addController(Controller controller) {
		// TODO Auto-generated method stub
	}

}
