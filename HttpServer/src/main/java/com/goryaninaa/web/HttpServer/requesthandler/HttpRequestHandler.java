package com.goryaninaa.web.HttpServer.requesthandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.goryaninaa.web.HttpServer.requesthandler.annotation.DeleteMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.GetMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PatchMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PostMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PutMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.RequestMapping;
import com.goryaninaa.web.HttpServer.server.RequestHandler;

public class HttpRequestHandler implements RequestHandler {
	private final Map<String, Controller> controllers = new HashMap<>();
	private final In in;
	private final Out out;
	
    public HttpRequestHandler(In in, Out out) {
    	this.in = in;
    	this.out = out;
	}

	public Response handle(String requestString) {
		Optional<Response> optionalHttpResponse = Optional.empty();
		
		try {
		
			Request httpRequest = in.httpRequestFrom(requestString);
			Optional<Controller> controller = defineController(httpRequest);
			if (controller.isPresent()) {
				optionalHttpResponse = manage(controller.get(), httpRequest);
			}
			
		} catch (IllegalAccessException | InvocationTargetException | RuntimeException e) {
			e.printStackTrace();
			return out.httpResponseFrom(HttpResponseCode.INTERNALSERVERERROR);
		}
		
		if (optionalHttpResponse.isPresent()) {
			return optionalHttpResponse.get();
		} else {
			return out.httpResponseFrom(HttpResponseCode.NOTFOUND);
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
    
	private Optional<Controller> defineController(Request httpRequest) {
		Optional<Controller> controller = Optional.empty();

		for (Entry<String, Controller> controllerDefiner : controllers.entrySet()) {
			int mappingLength = controllerDefiner.getKey().length();
			Optional<String> requestConttrollerMapping = httpRequest.getConttrollerMapping(mappingLength);
			
			if (requestConttrollerMapping.isPresent()
					&& requestConttrollerMapping.get().equals(controllerDefiner.getKey())) {
				controller = Optional.ofNullable(controllerDefiner.getValue());
				break;
			}
		}
		
		return controller;
	}

	private Optional<Response> manage(Controller controller, Request httpRequest)
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
		
		Optional<Response> httpResponse = Optional.empty();
		
		if (handlerMethod.isPresent()) {
			httpResponse = Optional.ofNullable((Response) handlerMethod.get().invoke(controller, httpRequest));
		}
		
		return httpResponse;
	}

	@SuppressWarnings("preview")
	private String defineMethodMapping(Method method, Request httpRequest) {
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
