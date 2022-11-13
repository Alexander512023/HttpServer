package com.goryaninaa.web.HttpServer;

import java.util.LinkedHashMap;
import java.util.Map;

import com.goryaninaa.web.HttpServer.entity.HttpRequest;
import com.goryaninaa.web.HttpServer.entity.HttpResponse;
import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Response;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.GetMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.RequestMapping;

@RequestMapping("/")
public class TestController implements Controller {

	@GetMapping("test")
	public Response test(HttpRequest request) {
		Map<String, String> additionalHeaders = new LinkedHashMap<>(1, 0.75f, false);
		additionalHeaders.put("Content-Type", "text/html; charset=utf-8");
		String body = "<p>Hello!</p>";
		return new HttpResponse(HttpResponseCode.OK, additionalHeaders, body);
	}
}
