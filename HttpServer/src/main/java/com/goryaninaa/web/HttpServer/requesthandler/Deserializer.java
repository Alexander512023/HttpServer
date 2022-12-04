package com.goryaninaa.web.HttpServer.requesthandler;

import java.lang.reflect.InvocationTargetException;

import com.goryaninaa.web.HttpServer.json.deserializer.JsonFormatException;

public interface Deserializer {

	<T> T deserialize(Class<T> clazz, String jsonToParse) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchFieldException, ClassNotFoundException, JsonFormatException;

}
