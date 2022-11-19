package com.goryaninaa.web.HttpServer.requestHandler;

import java.lang.reflect.InvocationTargetException;

import com.goryaninaa.web.HttpServer.json.parser.JsonFormatException;
import com.goryaninaa.web.HttpServer.requesthandler.Parser;

public class ParserStub implements Parser {

	@Override
	public <T> T deserialize(Class<T> clazz, String jsonToParse) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchFieldException, ClassNotFoundException, JsonFormatException {
		return null;
	}

}
