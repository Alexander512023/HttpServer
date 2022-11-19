package com.goryaninaa.web.HttpServer.requestHandler;

import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.Out;
import com.goryaninaa.web.HttpServer.requesthandler.Response;

public class OutStub implements Out {

	@Override
	public Response httpResponseFrom(HttpResponseCode httpResponseCode) {
		return new ResponseStub(httpResponseCode);
	}

	@Override
	public Response httpResponseFrom(HttpResponseCode httpResponseCode, String body) {
		return null;
	}

	@Override
	public <T> Response httpResponseFrom(HttpResponseCode httpResponseCode, T responseObject) {
		return null;
	}

}
