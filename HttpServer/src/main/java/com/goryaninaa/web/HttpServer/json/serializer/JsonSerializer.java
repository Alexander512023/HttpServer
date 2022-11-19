package com.goryaninaa.web.HttpServer.json.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.goryaninaa.web.HttpServer.entity.Serializer;

public class JsonSerializer implements Serializer {

	@Override
	public <T> String serialize(T responseObject) {
		String body;
		body = getStringRepresentation(responseObject);

		return body;
	}

	private <T> String getStringRepresentation(T object) {
		Map<String, Type> fieldTypeMap = collectFieldTypeMap(object.getClass());
		Map<String, String> fieldValueMap = collectFieldValueMap(object, fieldTypeMap);

		return wrap(fieldValueMap);
	}

	private <T> Map<String, Type> collectFieldTypeMap(Class<? extends T> clazz) {
		Map<String, Type> fieldTypeMap = new LinkedHashMap<>(15, 0.75f, false);
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			String name = field.getName();
			Type type = field.getType();

			fieldTypeMap.put(name, type);
		}

		return fieldTypeMap;
	}

	private <T> Map<String, String> collectFieldValueMap(T object, Map<String, Type> fieldTypeMap) {
		Map<String, String> fieldValueMap = new LinkedHashMap<>(15, 0.75f, false);

		for (Entry<String, Type> fieldType : fieldTypeMap.entrySet()) {
			String name = fieldType.getKey();
			Type type = fieldType.getValue();

			String value = getFieldValue(object, name, type);

			fieldValueMap.put(name, value);
		}

		return fieldValueMap;
	}

	public <T> String getFieldValue(T object, String name, Type type) {
		String fieldValue = "";
		
		try {
			String methodName = defineMethodName(name, type);

			if (type.equals(int.class) || type.equals(double.class) || type.equals(Boolean.class)) {
				Method getter = object.getClass().getDeclaredMethod(methodName, new Class<?>[0]);
				String value = String.valueOf(getter.invoke(object, new Object[0]));

				fieldValue = value;
			} else if (type.equals(String.class)) {
				Method getter = object.getClass().getDeclaredMethod(methodName, new Class<?>[0]);

				fieldValue = "\"" + String.valueOf(getter.invoke(object, new Object[0])) + "\"";
			} else if (type.equals(List.class)) {
				Method getter = object.getClass().getDeclaredMethod(methodName, new Class<?>[0]);
				List<?> fieldList = (List<?>) getter.invoke(object, new Object[0]);

				fieldValue = wrap(fieldList);
			} else {
				Method getter = object.getClass().getDeclaredMethod(methodName, new Class<?>[0]);
				Object fieldObject = getter.invoke(object, new Object[0]);

				fieldValue = getStringRepresentation(fieldObject);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Serialization failed");
		}
		return fieldValue;
	}

	private String defineMethodName(String name, Type type) {
		if (type.equals(Boolean.class)) {
			return "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
		} else {
			return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}

	private String wrap(Map<String, String> fieldValueMap) {
		String stringRepresentation = "{";

		for (Entry<String, String> fieldValue : fieldValueMap.entrySet()) {
			stringRepresentation += "\"" + fieldValue.getKey() + "\": " + fieldValue.getValue() + ",";
		}

		stringRepresentation = stringRepresentation.substring(0, stringRepresentation.length() - 1) + "}";

		return stringRepresentation;
	}

	private <T> String wrap(List<T> fieldList) {
		String stringRepresentation = "[";

		for (T value : fieldList) {
			Type valueType = value.getClass();

			if (valueType.equals(int.class) || valueType.equals(double.class) || valueType.equals(Boolean.class)) {
				stringRepresentation += value + ",";
			} else if (valueType.equals(String.class)) {
				stringRepresentation += " \"" + value + "\",";
			} else {
				stringRepresentation += getStringRepresentation(value) + ",";
			}
		}

		stringRepresentation = stringRepresentation.substring(0, stringRepresentation.length() - 1) + "]";

		return stringRepresentation;
	}

}
