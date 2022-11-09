package com.goryaninaa.web.HttpServer.server;

import com.goryaninaa.web.HttpServer.model.HttpResponse;

public interface RequestHandler {

	HttpResponse handle(String string);

}