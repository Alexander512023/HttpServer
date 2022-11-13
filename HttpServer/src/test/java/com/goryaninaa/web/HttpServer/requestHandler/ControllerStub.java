package com.goryaninaa.web.HttpServer.requestHandler;

import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Out;
import com.goryaninaa.web.HttpServer.requesthandler.Request;
import com.goryaninaa.web.HttpServer.requesthandler.Response;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.GetMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.RequestMapping;

@RequestMapping("/")
public class ControllerStub implements Controller {
	private final Out out = new OutStub();

	@GetMapping("test")
	public Response test(Request request) {
		return out.httpResponseFrom(HttpResponseCode.OK);
	}
	
}
