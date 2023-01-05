package com.goryaninaa.web.HttpServer.requestHandler;

import com.goryaninaa.web.HttpServer.requesthandler.Deserializer;

public class ParserStub implements Deserializer {

	@Override
	public <T> T deserialize(Class<T> clazz, String jsonToParse) {
		return null;
	}

}
