package com.goryaninaa.web.HttpServer.requesthandler;

import com.goryaninaa.web.HttpServer.model.HttpRequest;
import com.goryaninaa.web.HttpServer.model.HttpResponse;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.GetMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.RequestMapping;

@RequestMapping("/")
public class TestController implements Controller {

	@GetMapping("test")
	public HttpResponse test(HttpRequest request) {
		return new HttpResponse("HTTP/1.1 200 OK\nContent-Type: text/html; charset=utf-8\n\n<p>Hello!</p>");
	}
}
