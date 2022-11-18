package com.goryaninaa.web.HttpServer.requesthandler;

import java.util.Optional;

import com.goryaninaa.web.HttpServer.requesthandler.annotation.HttpMethod;

public interface Request {

	Optional<String> getConttrollerMapping(int length);

	String getMapping();

	HttpMethod getMethod();
	
	Optional<String> getBody();

}
