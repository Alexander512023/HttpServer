package com.goryaninaa.web.HttpServer.entity;

public interface Serializer {

	<T> String serialize(T responseObject);

}
