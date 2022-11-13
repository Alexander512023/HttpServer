package com.goryaninaa.web.HttpServer.requesthandler;

import java.util.LinkedHashMap;
import java.util.Map;

import com.goryaninaa.web.HttpServer.model.HttpRequest;
import com.goryaninaa.web.HttpServer.model.HttpResponse;
import com.goryaninaa.web.HttpServer.model.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.GetMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.RequestMapping;

@RequestMapping("/")
public class TestController implements Controller {

	@GetMapping("test")
	public HttpResponse test(HttpRequest request) {
		Map<String, String> additionalHeaders = new LinkedHashMap<>(1, 0.75f, false);
		additionalHeaders.put("Content-Type", "text/html; charset=utf-8");
		String body = "<p>Hello!</p>";
		return new HttpResponse(HttpResponseCode.OK, additionalHeaders, body);
	}
}
