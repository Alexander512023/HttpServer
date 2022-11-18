package com.goryaninaa.web.HttpServer.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser<T> {

	private Class<T> clazz;
	private String jsonToParse;
	
	public JsonParser(Class<T> clazz, String jsonToParse) {
		super();
		this.clazz = clazz;
		this.jsonToParse = jsonToParse;
	}
	
	public T deserialize()
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchFieldException, ClassNotFoundException, JsonFormatException {
		
		checkJson();
		jsonToParse = preHandleJson(jsonToParse);
		
		return createInstance();
	}
	
	private String preHandleJson(String json) {
		Pattern pattern = Pattern.compile("(?s)\\{.*\\}\\Z");
		Matcher matcher = pattern.matcher(json);
		
		if (matcher.find()) {
			json = json.substring(1, json.length() - 1);
		}
		json = json.trim();
		
		return json;
	}

	private void checkJson() throws JsonFormatException {
		Pattern patternCounter = Pattern.compile("\".+?\".*?:\\s*.?");
		Matcher matcherCounter = patternCounter.matcher(jsonToParse);
		int counter = 0;
		
		while (matcherCounter.find()) {
			counter++;
		}
		
		Pattern pattern = Pattern.compile("(?s)\\{(.*?\".+?\".*?:\\s*.+?){" + counter + "}.*?\\}");
		Matcher matcher = pattern.matcher(jsonToParse);
		
		if (jsonToParse == "") {
			throw new NullPointerException("Empty JSON string");
		}
		
		if (!matcher.find() || counter == 0) {
			throw new JsonFormatException("Deserializing JSON incorrect format");
		}
	}
	
	private T createInstance()
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchFieldException, ClassNotFoundException {
		
		Map<String, Object> keyValueListMap = parseJsonToMapWithList();
		Map<String, Object> methodArgumentMap = transformToMethodArgumentMapWithList(keyValueListMap, clazz);
		
		T instance = initializeTargetObject(clazz.getDeclaredConstructor().newInstance(), methodArgumentMap);
		
		return instance;
	}
	
	private Map<String, Object> parseJsonToMapWithList()
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchFieldException, ClassNotFoundException {
		
		Map<String, String> keyListStringMap = collectListsFromJsonToMap();
		
		replaceListsWithKeysInJson();
		
		Map<String, String> keyObjectFieldsStringMap = collectObjectFieldsFromJsonToMap();
		
		replaceObjectsWithKeysInJson();
		
		Map<String, String> keyValueMap = parseJsonToMap(jsonToParse);
		
		Map<String, List<Object>> keyListObjectMap = convertToListsOfObjectsMap(keyListStringMap, keyValueMap);
		
		Map<String, Object> keyObjectFieldMap = convertToObjectsFieldsMap(keyObjectFieldsStringMap, keyValueMap);
		
		Map<String, Object> keyValueListMap = joinKeyValueWithList(keyValueMap, keyListObjectMap, keyObjectFieldMap);
		
		return keyValueListMap;
	}

	private Map<String, Object> joinKeyValueWithList(Map<String, String> keyValueMap,
			Map<String, List<Object>> keyListObjectMap, Map<String, Object> keyObjectFieldMap) {
		
		Map<String, Object> keyValueListMap = new HashMap<>();
		
		for (Entry<String, String> entry: keyValueMap.entrySet()) {
			keyValueListMap.put(entry.getKey(), entry.getValue());
		}
		
		for(Entry<String, Object> entry: keyValueListMap.entrySet()) {
			if (keyListObjectMap.containsKey(entry.getValue())) {
				keyValueListMap.replace(entry.getKey(), entry.getValue(), keyListObjectMap.get(entry.getValue()));
			}
		}
		
		for(Entry<String, Object> entry: keyValueListMap.entrySet()) {
			if (keyObjectFieldMap.containsKey(entry.getValue())) {
				keyValueListMap.replace(entry.getKey(), entry.getValue(), keyObjectFieldMap.get(entry.getValue()));
			}
		}
		
		return keyValueListMap;
	}

	private Map<String, List<Object>> convertToListsOfObjectsMap(Map<String, String> keyListStringMap,
			Map<String, String> keyValueMap) 
					throws NoSuchMethodException, SecurityException, IllegalAccessException,
					IllegalArgumentException, InvocationTargetException, InstantiationException,
					NoSuchFieldException, ClassNotFoundException {
		
		Map<String, List<Object>> keyListObjectMap = new HashMap<>();
		Pattern pattern = Pattern.compile("(?s)\\{.*?\\}");
		
		for (Entry<String, String> entry: keyListStringMap.entrySet()) {
			List<Object> objectsList = new ArrayList<>();
			String json = entry.getValue();
			String key = entry.getKey();
			Matcher matcher = pattern.matcher(json);
			
			while (matcher.find()) {
				String objectString = json.substring(matcher.start(), matcher.end());
				String field = keyValueMap.entrySet().stream()
						.filter(e -> e.getValue().equals(key))
						.map(e -> e.getKey())
						.findAny()
						.get();
				objectsList.add(createInstanceOfObjectInList(objectString, field));
			}
			keyListObjectMap.put(key, objectsList);
		}
		return keyListObjectMap;
	}
	
	//TODO
	private Map<String, Object> convertToObjectsFieldsMap(Map<String, String> keyObjectFieldsStringMap,
			Map<String, String> keyValueMap) 
					throws NoSuchMethodException, SecurityException, IllegalAccessException,
					IllegalArgumentException, InvocationTargetException, InstantiationException,
					NoSuchFieldException, ClassNotFoundException {
		
		Map<String, Object> keyObjectFieldMap = new HashMap<>();
		
		for (Entry<String, String> entry: keyObjectFieldsStringMap.entrySet()) {
			String json = entry.getValue();
			String key = entry.getKey();
			String field = keyValueMap.entrySet().stream().filter(e -> e.getValue().equals(key)).map(e -> e.getKey())
					.findAny().get();
			
			Object fieldObject = createInstanceOfObjectField(json, field);
			keyObjectFieldMap.put(key, fieldObject);
		}
		return keyObjectFieldMap;
	}

	private Object createInstanceOfObjectField(String json, String field)
			throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

		Map<String, String> keyValueMap = parseJsonToMap(json);
		
		Type fieldObjectType = clazz.getDeclaredField(field).getType();
		
		String className = fieldObjectType.getTypeName();
		
		Object newInstance = Class.forName(className).getDeclaredConstructor().newInstance();
		Map<String, Object> methodArgumentMap = transformToMethodArgumentMap(keyValueMap, newInstance.getClass());
		
		Object instance = initializeTargetObjectRaw(newInstance, methodArgumentMap);
		
		return instance;
	}

	private Object createInstanceOfObjectInList(String json, String field)
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchFieldException, ClassNotFoundException {
		
		Map<String, String> keyValueMap = parseJsonToMap(json);
		
		Type genericType = clazz.getDeclaredField(field).getGenericType();
		Pattern pattern = Pattern.compile("\\<.*?\\>");
		Matcher matcher = pattern.matcher(genericType.getTypeName());
		
		matcher.find();
		
		String className = genericType.getTypeName().substring(matcher.start() + 1, matcher.end() - 1);
		
		Object newInstance = Class.forName(className).getDeclaredConstructor().newInstance();
		Map<String, Object> methodArgumentMap = transformToMethodArgumentMap(keyValueMap, newInstance.getClass());
		
		Object instance = initializeTargetObjectRaw(newInstance, methodArgumentMap);
		
		return instance;
	}
	
	private Map<String, Object> transformToMethodArgumentMapWithList(
			Map<String, Object> keyValueListMap, Class<?> objectClass) {
		
		Map<String, Object> methodArgumentMap = new HashMap<>();

		for (Entry<String, Object> entry : keyValueListMap.entrySet()) {
			String method = convertToSetMethodName(entry.getKey());
			Object argument = convertValueToTargetType(method, entry.getValue(), objectClass);

			methodArgumentMap.put(method, argument);
		}

		return methodArgumentMap;
	}

	private Map<String, String> parseJsonToMap(String json) {
		Map<String, String> keyValueMap = new HashMap<>();
		json = preHandleJson(json);
		String jsonToParseBeforeSubstring = json;
		
		do {
			jsonToParseBeforeSubstring = json;
			String key = findCurrentKey(json);
			json = removeCurrentKey(json);
			
			String value = findCurrentValue(json);
			value = removeQuotes(value);
			value = removeQuotes(value);
			json = removeCurrentValue(json);
			
			keyValueMap.put(key, value);
		} while (checkForLastIteration(jsonToParseBeforeSubstring));
		
		return keyValueMap;
	}
	
	private Map<String, Object> transformToMethodArgumentMap(Map<String, String> keyValueMap,
			Class<?> objectClass) throws NoSuchMethodException, SecurityException {
		
		Map<String, Object> methodArgumentMap = new HashMap<>();
		
		for (Entry<String, String> entry: keyValueMap.entrySet()) {
			String method = convertToSetMethodName( entry.getKey() );
			Object argument = convertValueToTargetType(method, entry.getValue(), objectClass);
			
			methodArgumentMap.put(method, argument);
		}
		
		return methodArgumentMap;
	}
	
	private T initializeTargetObject(T target, Map<String, Object> setMethodNameValueMap)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		
		for (Entry<String, Object> entry: setMethodNameValueMap.entrySet()) {
					clazz
						.getDeclaredMethod(entry.getKey(), getSetMethodParameterTypeByFieldName( entry.getKey(), target.getClass() ))
						.invoke(target, entry.getValue().getClass().cast(entry.getValue()));
		}
		
		return target;
	}
	
	private Object initializeTargetObjectRaw(Object target, Map<String, Object> setMethodNameValueMap)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		
		for (Entry<String, Object> entry: setMethodNameValueMap.entrySet()) {
					target.getClass()
						.getDeclaredMethod(entry.getKey(), getSetMethodParameterTypeByFieldName( entry.getKey(), target.getClass() ))
						.invoke(target, entry.getValue().getClass().cast(entry.getValue()));
		}
		
		return target;
	}
	
	@SuppressWarnings("rawtypes")
	private Class getSetMethodParameterTypeByFieldName(String methodName, Class objectClass) {
		Method[] declaredMethods = objectClass.getDeclaredMethods();
		Class[] parameterTypes = new Class[0];
		
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(methodName)) {
            	parameterTypes = declaredMethod.getParameterTypes();
                break;
            }
        }
        
        return parameterTypes[0];
	}
	
	private Map<String, String> collectObjectFieldsFromJsonToMap() {
		Map<String, String> keyListObjectMap = new HashMap<>();
		Pattern pattern = Pattern.compile("(?s)\\{.*?\\}");
		Matcher matcher = pattern.matcher(jsonToParse);
		int counter = 1;
		
		while (matcher.find()) {
			String key = generateKeyForObject(counter++);
			String objectString = jsonToParse.substring(matcher.start(), matcher.end());
			keyListObjectMap.put(key, objectString);
		}
		
		return keyListObjectMap;
	}
	
	private void replaceObjectsWithKeysInJson() {
		Pattern pattern = Pattern.compile("(?s)\\{.*?\\}");
		Matcher matcher = pattern.matcher(jsonToParse);
		int counter = 1;
		
		while (matcher.find()) {
			String key = generateKeyForObject(counter++);
			
			jsonToParse = jsonToParse.replaceFirst("(?s)\\{.*?\\}", key);
		}
	}
	
	private String generateKeyForObject(int counter) {
		return "|{" + counter + "}|";
	}

	private Map<String, String> collectListsFromJsonToMap() {
		Map<String, String> keyListStringMap = new HashMap<>();
		Pattern pattern = Pattern.compile("(?s)\\[.*?\\]");
		Matcher matcher = pattern.matcher(jsonToParse);
		int counter = 1;
		
		while (matcher.find()) {
			String key = generateKeyForList(counter++);
			String listString = jsonToParse.substring(matcher.start(), matcher.end());
			keyListStringMap.put(key, listString);
		}
		
		return keyListStringMap;
	}
	
	private void replaceListsWithKeysInJson() {
		Pattern pattern = Pattern.compile("(?s)\\[.*?\\]");
		Matcher matcher = pattern.matcher(jsonToParse);
		int counter = 1;
		
		while (matcher.find()) {
			String key = generateKeyForList(counter++);
			
			jsonToParse = jsonToParse.replaceFirst("(?s)\\[.*?\\]", key);
		}
	}

	private String generateKeyForList(int counter) {
		return "|[" + counter + "]|";
	}

	private String convertToSetMethodName(String fieldName) {
		Pattern pattern = Pattern.compile("_");
		Matcher matcher = pattern.matcher(fieldName);
		
		String setMethodName = "set";
		while (matcher.find()) {
			setMethodName = setMethodName
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1, matcher.start());
			fieldName = fieldName.substring(matcher.start() + 1);
		}
		
		return setMethodName + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	
	@SuppressWarnings("rawtypes")
	private Object convertValueToTargetType(String methodName, Object value, Class objectClass) {
		Class methodParameterType = getSetMethodParameterTypeByFieldName(methodName, objectClass);
        
        if (methodParameterType.isPrimitive()) {
        	return wrapStringToNonPrimitive(methodParameterType, value);
        } else {
        	return methodParameterType.cast(value);
        }
	}
	
	//TODO
	@SuppressWarnings({ "rawtypes", "removal" })
	private Object wrapStringToNonPrimitive(Class fieldType, Object value) {
		if (fieldType.equals(int.class)) {
			return new Integer((String) value);
		} else {
			return value;
		}
	}
	
	private String findCurrentKey(String json) {
		Pattern pattern = Pattern.compile("\"\\w*\"");
		Matcher matcher = pattern.matcher(json);
		matcher.find();
		
		return json.substring(matcher.start() + 1, matcher.end() - 1);
	}
	
	private String removeCurrentKey(String json) {
		Pattern pattern = Pattern.compile("\"\\w*\"");
		Matcher matcher = pattern.matcher(json);
		matcher.find();
		
		return json.substring(matcher.end());
	}
	
	private String findCurrentValue(String json) {
		Pattern pattern = Pattern.compile("(\"|\\w|\\d|\\|).*?,\\s*\"");
		Matcher matcher = pattern.matcher(json);
		if (matcher.find()) {
			 json = json.substring(matcher.start(), matcher.end() - 1);
			 json = json.trim();
			 json = json.substring(0, json.length() - 1);
			 return json;
		} else {
			return json.substring(1);
		}
	}
	
	private String removeCurrentValue(String json) {
		Pattern pattern = Pattern.compile("(\"|\\w|\\d|\\|).*?,");
		Matcher matcher = pattern.matcher(json);
		if (matcher.find()) {
			return json.substring(matcher.end()).trim();
		} else {
			return "";
		}
	}
	
	private boolean checkForLastIteration(String jsonToParseBefore) {
		Pattern pattern = Pattern.compile(",\\s*\"");
		Matcher matcher = pattern.matcher(jsonToParseBefore);
		
		return matcher.find();
	}
	
	private String removeQuotes(String value) {
		value = value.trim();
		Pattern pattern = Pattern.compile("\".*\"");
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			return value.substring(matcher.start() + 1, matcher.end() - 1);
		} else {
			return value;
		}
	}
}
