package com.goryaninaa.web.HttpServer.requestHandler;

import com.goryaninaa.web.HttpServer.requesthandler.In;
import com.goryaninaa.web.HttpServer.requesthandler.Request;

public class InStub implements In {

	@Override
	public Request httpRequestFrom(String requestString) {
		return new RequestStub(requestString);
	}

}
