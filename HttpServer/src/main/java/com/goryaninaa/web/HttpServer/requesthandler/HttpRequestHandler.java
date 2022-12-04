package com.goryaninaa.web.HttpServer.requesthandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.goryaninaa.web.HttpServer.json.deserializer.JsonFormatException;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.DeleteMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.GetMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.HttpMethod;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PatchMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PostMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.PutMapping;
import com.goryaninaa.web.HttpServer.requesthandler.annotation.RequestMapping;
import com.goryaninaa.web.HttpServer.server.RequestHandler;

public class HttpRequestHandler implements RequestHandler {
	private final Map<String, Controller> controllers = new HashMap<>();
	private final In in;
	private final Out out;
	private final Deserializer deserializer;
	
    public HttpRequestHandler(In in, Out out, Deserializer parser) {
    	this.in = in;
    	this.out = out;
    	this.deserializer = parser;
	}

	public Response handle(String requestString) {
		Optional<Response> httpResponse = Optional.empty();
		try {
			Request httpRequest = in.httpRequestFrom(requestString);
			Optional<Controller> controller = defineController(httpRequest);
			if (controller.isPresent()) {
				httpResponse = manage(controller.get(), httpRequest);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			return out.httpResponseFrom(HttpResponseCode.INTERNALSERVERERROR);
		}
		if (httpResponse.isPresent()) {
			return httpResponse.get();
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

	private Optional<Response> manage(Controller controller, Request httpRequest) {
		Method[] methods = controller.getClass().getDeclaredMethods();
		Optional<Method> handlerMethod = Optional.empty();
		int controllerMappingLength = controller.getClass().getAnnotation(RequestMapping.class).value().length();
		for (Method method : methods) {
			String methodMapping = defineMethodMappingIfHttpMethodMatch(method, httpRequest);
			String requestMethodMapping = httpRequest.getMapping().substring(controllerMappingLength);
			if (methodMapping.equals(requestMethodMapping)) {
				handlerMethod = Optional.ofNullable(method);
			}
		}
		Optional<Response> httpResponse = Optional.empty();
		if (handlerMethod.isPresent()) {
			httpResponse = invokeMethod(handlerMethod.get(), controller, httpRequest);
		}
		return httpResponse;
	}


	private Optional<Response> invokeMethod(Method method, Controller controller, Request httpRequest) {
		try {
			if (method.getParameterCount() > 1) {
				Class<?> clazz = method.getParameterTypes()[1];
				Object argument;
				argument = deserializer.deserialize(clazz, httpRequest.getBody().get());
				return Optional.ofNullable((Response) method.invoke(controller, httpRequest, argument));
			} else {
				return Optional.ofNullable((Response) method.invoke(controller, httpRequest));
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException | NoSuchFieldException | ClassNotFoundException
				| JsonFormatException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to handle request");
		}
	}

	private String defineMethodMappingIfHttpMethodMatch(Method method, Request httpRequest) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(GetMapping.class) && httpRequest.getMethod().equals(HttpMethod.GET)) {
				return method.getAnnotation(GetMapping.class).value();
			} else if (annotation.annotationType().equals(PostMapping.class) && httpRequest.getMethod().equals(HttpMethod.POST)) {
				return method.getAnnotation(PostMapping.class).value();
			} else if (annotation.annotationType().equals(PutMapping.class) && httpRequest.getMethod().equals(HttpMethod.PUT)) {
				return method.getAnnotation(PutMapping.class).value();
			} else if (annotation.annotationType().equals(PatchMapping.class) && httpRequest.getMethod().equals(HttpMethod.PATCH)) {
				return method.getAnnotation(PatchMapping.class).value();
			} else if (annotation.annotationType().equals(DeleteMapping.class) && httpRequest.getMethod().equals(HttpMethod.DELETE)) {
				return method.getAnnotation(DeleteMapping.class).value();
			}
		}
		return "";
	}
}
