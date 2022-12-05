package com.goryaninaa.web.HttpServer.requestHandler;

import java.lang.reflect.InvocationTargetException;

import com.goryaninaa.web.HttpServer.exception.JsonFormatException;
import com.goryaninaa.web.HttpServer.requesthandler.Deserializer;

public class ParserStub implements Deserializer {

	@Override
	public <T> T deserialize(Class<T> clazz, String jsonToParse) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchFieldException, ClassNotFoundException, JsonFormatException {
		return null;
	}

}
