package com.goryaninaa.web.HttpServer.json.deserializer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonDeserializerTest {

	private String correctReqresListUsersJson;
	private String correctPersonJson;
	private String incorrectDataJson;
	private String emptyStringJson = "";
	private JsonDeserializer deserializer = new JsonDeserializer();
	
	
	@BeforeEach
	public void init() {
		createCorrectReqresListUsersJson();
		createCorrectPersonJson();
		createIncorrectDataJson();
	}
	
	@Test
	public void testDeserializeListAndFieldObject()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException, NoSuchFieldException, ClassNotFoundException,
			JsonFormatException, URISyntaxException {
		
		Person person1 = new Person(7, "michael.lawson@reqres.in", "Michael", "Lawson", "https://reqres.in/img/faces/7-image.jpg");
		Person person2 = new Person(8, "lindsay.ferguson@reqres.in", "Lindsay", "Ferguson", "https://reqres.in/img/faces/8-image.jpg");
		Person person3 = new Person(9, "tobias.funke@reqres.in", "Tobias", "Funke", "https://reqres.in/img/faces/9-image.jpg");
		List<Person> dataList = new ArrayList<>();
		dataList.add(person1);
		dataList.add(person2);
		dataList.add(person3);
		
		Support support = new Support("https://reqres.in/#support-heading", 
				"To keep ReqRes free, contributions towards server costs are appreciated!");
		
		ReqresListUsers reqresListUsersConstructor = new ReqresListUsers(2, 6, 12, 2, dataList, support);
		ReqresListUsers reqresListUsersCorrectJson = deserializer.deserialize(ReqresListUsers.class, correctReqresListUsersJson);
		
		assertTrue(reqresListUsersCorrectJson.equals(reqresListUsersConstructor));
	}
	
	
	@Test
	public void testDeserialize() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchFieldException, ClassNotFoundException, JsonFormatException {
		Person personConstructor = new Person(7, "michael.lawson@reqres.in", "Michael", "Lawson", "https://reqres.in/img/faces/7-image.jpg");
		Person personCorrectJson = deserializer.deserialize(Person.class, correctPersonJson);
		
		assertTrue(personCorrectJson.equals(personConstructor));
	}
	
	@Test
	public void testDeserializeCheckIncorrect() {
		Exception exceptionIncorrect = assertThrows(JsonFormatException.class, () -> {
			deserializer.deserialize(Person.class, incorrectDataJson);
		});
		
		String expectedMessage = "Deserializing JSON incorrect format";
		String actualMessage = exceptionIncorrect.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	@Test
	public void testDeserializeCheckEmpty() {
		Exception exceptionIncorrect = assertThrows(NullPointerException.class, () -> {
			deserializer.deserialize(Person.class, emptyStringJson);
		});
		
		String expectedMessage = "Empty JSON string";
		String actualMessage = exceptionIncorrect.getMessage();
		
		assertTrue(expectedMessage.equals(actualMessage));
	}
	
	private void createCorrectReqresListUsersJson() {
		correctReqresListUsersJson = "{\n"
				+ "    \"page\": 2,\n"
				+ "    \"per_page\": 6,\n"
				+ "    \"total\": 12,\n"
				+ "    \"total_pages\": 2,\n"
				+ "    \"data\": [\n"
				+ "        {\n"
				+ "            \"id\": 7,\n"
				+ "            \"email\": \"michael.lawson@reqres.in\",\n"
				+ "            \"first_name\": \"Michael\",\n"
				+ "            \"last_name\": \"Lawson\",\n"
				+ "            \"avatar\": \"https://reqres.in/img/faces/7-image.jpg\"\n"
				+ "        },\n"
				+ "        {\n"
				+ "            \"id\": 8,\n"
				+ "            \"email\": \"lindsay.ferguson@reqres.in\",\n"
				+ "            \"first_name\": \"Lindsay\",\n"
				+ "            \"last_name\": \"Ferguson\",\n"
				+ "            \"avatar\": \"https://reqres.in/img/faces/8-image.jpg\"\n"
				+ "        },\n"
				+ "        {\n"
				+ "            \"id\": 9,\n"
				+ "            \"email\": \"tobias.funke@reqres.in\",\n"
				+ "            \"first_name\": \"Tobias\",\n"
				+ "            \"last_name\": \"Funke\",\n"
				+ "            \"avatar\": \"https://reqres.in/img/faces/9-image.jpg\"\n"
				+ "        }\n"
				+ "    ],\n"
				+ "    \"support\": {\n"
				+ "        \"url\": \"https://reqres.in/#support-heading\",\n"
				+ "        \"text\": \"To keep ReqRes free, contributions towards server costs are appreciated!\"\n"
				+ "    }\n"
				+ "}";
	}
	
	private void createCorrectPersonJson() {
		correctPersonJson = "{\n"
				+ "            \"id\": 7,\n"
				+ "            \"email\": \"michael.lawson@reqres.in\",\n"
				+ "            \"first_name\": \"Michael\",\n"
				+ "            \"last_name\": \"Lawson\",\n"
				+ "            \"avatar\": \"https://reqres.in/img/faces/7-image.jpg\"\n"
				+ "        }";
	}
	
	private void createIncorrectDataJson() {
		incorrectDataJson = "{\n"
				+ "            \"id\": 7,\n"
				+ "            \"email\": \"michael.lawson@reqres.in\",\n"
				+ "            \"address\": \"Carl Marks 4E\"\n"
				+ "        ";
	}

}
