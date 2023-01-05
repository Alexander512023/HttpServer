package com.goryaninaa.web.HttpServer.server;

import java.util.concurrent.CountDownLatch;

import com.goryaninaa.web.HttpServer.entity.HttpResponse;
import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;

public class RequestHandlerStub implements RequestHandler {
	
	private final CountDownLatch countDownLatch;

	public RequestHandlerStub(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

	@Override
	public HttpResponse handle(String request) {
		countDownLatch.countDown();
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		return new HttpResponse(HttpResponseCode.OK, request);
	}

	@Override
	public void addController(Controller controller) {
		// TODO Auto-generated method stub
	}

}
