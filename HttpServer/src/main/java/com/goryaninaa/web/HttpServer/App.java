package com.goryaninaa.web.HttpServer;

import java.io.IOException;

import com.goryaninaa.web.HttpServer.entity.IncomingRequest;
import com.goryaninaa.web.HttpServer.entity.OutgoingResponse;
import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.HttpRequestHandler;
import com.goryaninaa.web.HttpServer.requesthandler.In;
import com.goryaninaa.web.HttpServer.requesthandler.Out;
import com.goryaninaa.web.HttpServer.server.RequestHandler;
import com.goryaninaa.web.HttpServer.server.Server;

public class App {
    public static void main( String[] args ) {
    	In in = new IncomingRequest();
    	Out out = new OutgoingResponse();
		RequestHandler requestHandler = new HttpRequestHandler(in, out);
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
