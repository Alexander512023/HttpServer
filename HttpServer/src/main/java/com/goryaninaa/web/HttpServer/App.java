package com.goryaninaa.web.HttpServer;

import java.io.IOException;

import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.HttpRequestHandler;
import com.goryaninaa.web.HttpServer.requesthandler.TestController;
import com.goryaninaa.web.HttpServer.server.Server;

public class App {
    public static void main( String[] args ) {
		HttpRequestHandler requestHandler = new HttpRequestHandler();
		Controller controller = new TestController();
		requestHandler.addController(controller);
		Server server = new Server(8080, 2, requestHandler);

		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
