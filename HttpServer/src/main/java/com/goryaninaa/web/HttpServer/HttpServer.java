package com.goryaninaa.web.HttpServer;

import java.io.IOException;
import java.util.List;

import com.goryaninaa.web.HttpServer.entity.IncomingRequest;
import com.goryaninaa.web.HttpServer.entity.OutgoingResponse;
import com.goryaninaa.web.HttpServer.json.deserializer.JsonDeserializer;
import com.goryaninaa.web.HttpServer.requesthandler.Controller;
import com.goryaninaa.web.HttpServer.requesthandler.Deserializer;
import com.goryaninaa.web.HttpServer.requesthandler.HttpRequestHandler;
import com.goryaninaa.web.HttpServer.requesthandler.In;
import com.goryaninaa.web.HttpServer.requesthandler.Out;
import com.goryaninaa.web.HttpServer.server.RequestHandler;
import com.goryaninaa.web.HttpServer.server.Server;

public class HttpServer {
	private final Server server;

	public HttpServer(String propertiesPath, List<Controller> controllers) throws IOException {
		In in = new IncomingRequest();
    	Out out = new OutgoingResponse();
    	Deserializer deserializer = new JsonDeserializer();
		RequestHandler requestHandler = new HttpRequestHandler(in, out, deserializer);
		
		for (Controller controller : controllers) {
			requestHandler.addController(controller);
		}
		
		Properties properties = new Properties(propertiesPath);

		this.server = new Server(properties.getPort(), properties.getThreadsNumber(), requestHandler);
	}
	
	public void start() {
		server.start();
	}
}
