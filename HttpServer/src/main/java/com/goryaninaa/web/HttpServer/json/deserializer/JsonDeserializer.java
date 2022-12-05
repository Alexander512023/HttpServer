package com.goryaninaa.web.HttpServer.json.deserializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.goryaninaa.web.HttpServer.exception.ClientException;
import com.goryaninaa.web.HttpServer.requesthandler.Deserializer;

public class JsonDeserializer implements Deserializer {
	
	public <T> T deserialize(Class<T> clazz, String jsonString) {
		checkJsonFormat(jsonString);
		String[] jsonLines = extractLines(jsonString);
		Map<String, String> methodNameValueStringMap = splitJsonLinesToStringMap(jsonLines);
		Map<Method, Object> methodValueMap = convertToMethodValueMap(clazz, methodNameValueStringMap);
		return createInstance(clazz, methodValueMap);
	}
	
	private <T> T createInstance(Class<T> clazz, Map<Method, Object> methodNameValueMap) {
		try {
			T instance = clazz.getDeclaredConstructor().newInstance();
			enrichInstanceWithData(instance, methodNameValueMap);
			return instance;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create instance");
		}
	}

	private <T> void enrichInstanceWithData(T instance, Map<Method, Object> methodNameValueMap) {
		for (Entry<Method, Object> methodNameValue : methodNameValueMap.entrySet()) {
			Method method = methodNameValue.getKey();
			Object parameter = methodNameValue.getValue();
			try {
				method.invoke(instance, parameter);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to enrich instance with data");
			}
		}
	}

	private <T> Map<Method, Object> convertToMethodValueMap(Class<T> clazz,
			Map<String, String> methodNameValueStringMap) {
		Map<Method, Object> methodNameValueMap = new HashMap<>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Entry<String, String> methodNameValue : methodNameValueStringMap.entrySet()) {
			String methodName = methodNameValue.getKey();
			String valueString = methodNameValue.getValue();
			Method method = defineMethod(methods, methodName);
			Object value = getRealValue(method, valueString);
			methodNameValueMap.put(method, value);
		}
		return methodNameValueMap;
	}

	private Object getRealValue(Method method, String valueString) {
		if (valueString.equals("null")) {
			return null;
		}
		Class<?> clazz = (Class<?>) method.getParameterTypes()[0];
		String firstSymbol = valueString.substring(0, 1);
		if (firstSymbol.equals("{")) {
			return deserialize(clazz, valueString);
		} else if (firstSymbol.equals("[")) {
			ParameterizedType elementType = (ParameterizedType) method.getGenericParameterTypes()[0];
			return ofList(elementType, valueString);
		} else if (firstSymbol.equals("\"")) {
			return ofStringOrConst(clazz, valueString);
		} else {
			return ofPrimitive(clazz, valueString);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> ofList(ParameterizedType elementType, String valueString) {
		List<T> resArr = new ArrayList<>();
		Class<T> clazz = (Class<T>) elementType.getActualTypeArguments()[0];
		String[] elementLines = extractLines(valueString);
		for (String elementLine : elementLines) {
			resArr.add((T) deserialize(clazz, elementLine));
		}
		return resArr;
	}

	private Object ofPrimitive(Class<?> clazz, String valueString) {
		if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
			return Integer.valueOf(valueString);
		} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
			return Double.valueOf(valueString);
		} else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
			return Boolean.valueOf(valueString);
		} else {
			throw new IllegalArgumentException("Value of unsupported primitive type");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object ofStringOrConst(Class<?> clazz, String valueString) {
		valueString = removeExternalSymbols(valueString);
		if (clazz.isEnum()) {
			try {
				return Enum.valueOf((Class<? extends Enum>) Class.forName(clazz.getCanonicalName()),
						String.valueOf(valueString));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Class not found");
			}
		} else {
			return valueString;
		}
	}

	private Method defineMethod(Method[] methods, String methodName) {
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				method.setAccessible(true);
				return method;
			}
		}
		throw new IllegalArgumentException("There is no such set method");
	}

	private Map<String, String> splitJsonLinesToStringMap(String[] jsonLines) {
		Map<String, String> stringMap = new HashMap<>();
		Pattern pattern = Pattern.compile("\".+?\"\\s*:");
		for (String jsonLine : jsonLines) {
			Matcher matcher = pattern.matcher(jsonLine);
			matcher.find();
			String fieldWithQuotes = jsonLine.substring(0, matcher.end() - 1).trim();
			String field = removeExternalSymbols(fieldWithQuotes);
			String methodName = convertFieldToMethodName(field);
			String value = jsonLine.substring(matcher.end()).trim();
			stringMap.put(methodName, value);
		}
		return stringMap;
	}

	private String convertFieldToMethodName(String fieldName) {
		Pattern pattern = Pattern.compile("_");
		Matcher matcher = pattern.matcher(fieldName);
		String setMethodName = "set";
		while (matcher.find()) {
			setMethodName = setMethodName + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, matcher.start());
			fieldName = fieldName.substring(matcher.start() + 1);
		}
		return setMethodName + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	private String[] returnCommasToLines(String[] jsonLines) {
		for (int i = 0; i < jsonLines.length; i++) {
			jsonLines[i] = returnCommas(jsonLines[i]);
		}
		return jsonLines;
	}

	private String returnCommas(String jsonLine) {
		String res = "";
		int pos = 0;
		Pattern pattern = Pattern.compile("\\[comma\\]");
		Matcher matcher = pattern.matcher(jsonLine);
		while (matcher.find()) {
			res = res + jsonLine.substring(pos, matcher.start()) + ",";
			pos = matcher.end();
		}
		res = res + jsonLine.substring(pos, jsonLine.length());
		return res;
	}

	private String[] splitJsonToLines(String jsonString) {
		return jsonString.split(",");
	}

	private String replaceExcessCommas(String jsonString) {
		String res = "";
		int pos = 0;
		Pattern pattern = Pattern.compile("((?s)\\{.*?\\})|(\".*?\")|((?s)\\[.*?\\])");
		Matcher matcher = pattern.matcher(jsonString);
		while (matcher.find()) {
			if (jsonString.substring(matcher.start(), matcher.end()).contains(",")) {
				res = res + jsonString.substring(pos, matcher.start()) + replace(jsonString.substring(matcher.start(), matcher.end()));
				pos = matcher.end();
			}
		}
		res = res + jsonString.substring(pos, jsonString.length());
		return res;
	}

	private String replace(String jsonSubstring) {
		String res = "";
		int pos = 0;
		Pattern pattern = Pattern.compile(",");
		Matcher matcher = pattern.matcher(jsonSubstring);
		while (matcher.find()) {
			res = res + jsonSubstring.substring(pos, matcher.start()) + "[comma]";
			pos = matcher.end();
		}
		res = res + jsonSubstring.substring(pos, jsonSubstring.length());
		return res;
	}

	private String removeExternalSymbols(String jsonString) {
		jsonString = jsonString.trim();
		return jsonString.substring(1, jsonString.length() - 1).trim();
	}

	private String[] extractLines(String jsonString) {
		jsonString = removeExternalSymbols(jsonString);
		jsonString = replaceExcessCommas(jsonString);
		String[] jsonLines = splitJsonToLines(jsonString);
		jsonLines = returnCommasToLines(jsonLines);
		return jsonLines;
	}
	
	private void checkJsonFormat(String jsonString) {
		if (jsonString == "" || jsonString == null) {
			throw new NullPointerException("Empty JSON string");
		}
		int counter = countLines(jsonString);
		Pattern pattern = Pattern.compile("(?s)\\{(.*?\".+?\".*?:\\s*.+?){" + counter + "}.*?\\}");
		Matcher matcher = pattern.matcher(jsonString);
		if (!matcher.find() || counter == 0) {
			throw new ClientException("Deserializing JSON incorrect format");
		}
	}

	private int countLines(String jsonString) {
		int counter = 0;
		Pattern patternCounter = Pattern.compile("\".+?\".*?:\\s*.?");
		Matcher matcherCounter = patternCounter.matcher(jsonString);
		while (matcherCounter.find()) {
			counter++;
		}
		return counter;
	}
}
