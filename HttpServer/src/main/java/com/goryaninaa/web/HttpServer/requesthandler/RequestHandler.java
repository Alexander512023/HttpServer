package com.goryaninaa.web.HttpServer.requesthandler;

import java.util.HashMap;
import java.util.Map;

import com.goryaninaa.web.HttpServer.model.HttpResponse;

public class RequestHandler {
	private final Map<String, Controller> httpContext = new HashMap<>();
	
    public RequestHandler() {
	}
    
    public void createContext(String mapping, Controller controller) {
    	httpContext.put(mapping, controller);
    }
    
	public HttpResponse handle(String request) {
		String response = "HTTP/1.1 200 OK\nContent-Type: text/html; charset=utf-8\n\n<p>Hello!</p>";
		 
        return new HttpResponse(response);
    }
}
