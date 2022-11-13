package com.goryaninaa.web.HttpServer.entity;

import java.util.Map;

import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Out;

public class OutgoingResponse implements Out {

	@Override
	public HttpResponse httpResponseFrom(HttpResponseCode httpResponseCode) {
		return new HttpResponse(httpResponseCode);
	}

	@Override
	public HttpResponse httpResponseFrom(HttpResponseCode httpResponseCode, String body) {
		return new HttpResponse(httpResponseCode, body);
	}

	@Override
	public HttpResponse httpResponseFrom(HttpResponseCode httpResponseCode, Map<String, String> additionalHeaders,
			String body) {
		return new HttpResponse(httpResponseCode, additionalHeaders, body);
	}

}
