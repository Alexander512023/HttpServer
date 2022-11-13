package com.goryaninaa.web.HttpServer.server;

import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.Response;

public interface RequestHandler {

	Response handle(String request);

	void addController(Controller controller);

}