package com.goryaninaa.web.HttpServer.requestHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.goryaninaa.web.HttpServer.requesthandler.HttpRequestHandler;

class HttpRequestHandlerTest {
	private static HttpRequestHandler httpRequestHandler;
	
	@BeforeAll
	static void init() {
		httpRequestHandler = new HttpRequestHandler(new InStub(), new OutStub(), new ParserStub());
		httpRequestHandler.addController(new ControllerStub());
	}

	@Test
	void handlerShouldHandleCorrectRequest() {
		int fact = httpRequestHandler.handle("/test").getCode().getCode();
		int expected = 200;
		
		assertEquals(expected, fact);
	}
	
	@Test
	void handlerShouldHandleIncorrectRequest() {
		int fact = httpRequestHandler.handle("/test1").getCode().getCode();
		int expected = 404;
		
		assertEquals(expected, fact);
	}
	
	@Test
	void handlerShouldHandleBrokeRequest() {
		int fact = httpRequestHandler.handle("broke").getCode().getCode();
		int expected = 500;
		
		assertEquals(expected, fact);
	}
}