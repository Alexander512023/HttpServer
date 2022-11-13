package com.goryaninaa.web.HttpServer.requesthandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.goryaninaa.web.HttpServer.model.HttpRequest;
import com.goryaninaa.web.HttpServer.model.HttpResponse;
import com.goryaninaa.web.HttpServer.model.HttpResponseCode;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.DeleteMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.GetMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PatchMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PostMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PutMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.RequestMapping;
import com.goryaninaa.web.HttpServer.server.RequestHandler;

public class HttpRequestHandler implements RequestHandler {
	private final Map<String, Controller> controllers = new HashMap<>();
	
    public HttpRequestHandler() {
	}

	public HttpResponse handle(String request) {
		Optional<HttpResponse> optionalHttpResponse = Optional.empty();
		
		try {
		
			HttpRequest httpRequest = new HttpRequest(request);
			Controller controller = defineControllerForSuchRequest(httpRequest);
			optionalHttpResponse = manage(controller, httpRequest);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
//			return new HttpResponse("HTTP/1.0 404 Not Found\n");TODO
		}
		
		if (optionalHttpResponse.isPresent()) {
			return optionalHttpResponse.get();
		} else {
			return new HttpResponse(HttpResponseCode.NOTFOUND);
		}
    }
	
    public void addController(Controller controller) {
    	if (controller.getClass().isAnnotationPresent(RequestMapping.class)) {
    		String mapping = ((RequestMapping)controller.getClass().getAnnotation(RequestMapping.class)).value();
        	controllers.put(mapping, controller);
    	} else {
    		throw new IllegalArgumentException("Controller should be annotated with the request mapping");
    	}
    }
    
	private Controller defineControllerForSuchRequest(HttpRequest httpRequest) {
		Optional<Controller> optionalController = defineController(httpRequest);
		
		Controller controller = null;
		if (optionalController.isPresent()) {
			controller = optionalController.get();
		} else {
			throw new IllegalArgumentException("Server is not able to handle such request");
		}
		
		return controller;
	}

	private Optional<Controller> defineController(HttpRequest httpRequest) {
		Optional<Controller> controller = Optional.empty();

		for (Entry<String, Controller> controllerDefiner : controllers.entrySet()) {
			String controllerMapping = controllerDefiner.getKey();

			if (httpRequest.getConttrollerMapping(controllerMapping.length()).isPresent() && httpRequest
					.getConttrollerMapping(controllerMapping.length()).get().equals(controllerDefiner.getKey())) {
				controller = Optional.ofNullable(controllerDefiner.getValue());
				break;
			}
		}
		
		return controller;
	}

	private Optional<HttpResponse> manage(Controller controller, HttpRequest httpRequest)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method[] methods = controller.getClass().getDeclaredMethods();
		Optional<Method> handlerMethod = Optional.empty();
		int controllerMappingLength = controller.getClass().getAnnotation(RequestMapping.class).value().length();
		
		for (Method method : methods) {
			String methodMapping = defineMethodMapping(method, httpRequest);
			String requestMethodMapping = httpRequest.getMapping().substring(controllerMappingLength);
			
			if (methodMapping.equals(requestMethodMapping)) {
				handlerMethod = Optional.ofNullable(method);
			}
		}
		
		Optional<HttpResponse> httpResponse = Optional.empty();
		
		if (handlerMethod.isPresent()) {
			httpResponse = Optional.ofNullable((HttpResponse) handlerMethod.get().invoke(controller, httpRequest));
		}
		
		return httpResponse;
	}

	@SuppressWarnings("preview")
	private String defineMethodMapping(Method method, HttpRequest httpRequest) {
		switch (httpRequest.getMethod()) {
			case GET: 
				return method.getAnnotation(GetMapping.class).value();
			case POST:
				return method.getAnnotation(PostMapping.class).value();
			case PUT: 
				return method.getAnnotation(PutMapping.class).value();
			case PATCH:
				return method.getAnnotation(PatchMapping.class).value();
			case DELETE: 
				return method.getAnnotation(DeleteMapping.class).value();
			case default:
				return "";
		}
	}
}
