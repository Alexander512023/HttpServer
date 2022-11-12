package com.goryaninaa.web.HttpServer.requesthandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.goryaninaa.web.HttpServer.model.HttpRequest;
import com.goryaninaa.web.HttpServer.model.HttpResponse;
import com.goryaninaa.web.HttpServer.server.RequestHandler;

public class HttpRequestHandler implements RequestHandler {
	private final Map<String, Controller> httpContext = new HashMap<>();
	
    public HttpRequestHandler() {
	}
    
    public void createContext(String mapping, Controller controller) {
    	httpContext.put(mapping, controller);
    }
    
	public HttpResponse handle(String request) {
		
		HttpRequest httpRequest = new HttpRequest(request);
		
		Controller controller = defineControllerForSuchRequest(httpRequest);
		
		HttpResponse httpResponse = manage(controller, httpRequest);
		
		String response = "HTTP/1.1 200 OK\nContent-Type: text/html; charset=utf-8\n\n<p>Hello!</p>";
		 
        return new HttpResponse(response);
    }

	private Controller defineControllerForSuchRequest(HttpRequest httpRequest) {
		Optional<Controller> controllerOptional = defineControllerOptional(httpRequest);
		
		Controller controller = null;
		if (controllerOptional.isPresent()) {
			controller = controllerOptional.get();
		} else {
			throw new IllegalArgumentException("Server is not able to handle such request");
		}
		
		return controller;
	}

	private Optional<Controller> defineControllerOptional(HttpRequest httpRequest) {
		Optional<Controller> controllerOptional = Optional.empty();

		for (Entry<String, Controller> controllerDefiner : httpContext.entrySet()) {
			String controllerMapping = controllerDefiner.getKey();

			if (httpRequest.getConttrollerMapping(controllerMapping.length()).isPresent() && httpRequest
					.getConttrollerMapping(controllerMapping.length()).get().equals(controllerDefiner.getKey())) {
				controllerOptional = Optional.ofNullable(controllerDefiner.getValue());
				break;
			}
		}
		
		return controllerOptional;
	}

	private HttpResponse manage(Controller controller, HttpRequest httpRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
