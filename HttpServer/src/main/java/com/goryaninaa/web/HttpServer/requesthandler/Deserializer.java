package com.goryaninaa.web.HttpServer.requesthandler;

public interface Deserializer {

	<T> T deserialize(Class<T> clazz, String jsonToParse);

}
