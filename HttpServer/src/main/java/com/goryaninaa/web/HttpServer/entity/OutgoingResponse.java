package com.goryaninaa.web.HttpServer.entity;

import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Out;
import com.goryaninaa.web.HttpServer.requesthandler.Response;

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
	public <T> Response httpResponseFrom(HttpResponseCode httpResponseCode, T responseObject) {
		return new <T> HttpResponse(httpResponseCode, responseObject);
	}

}
