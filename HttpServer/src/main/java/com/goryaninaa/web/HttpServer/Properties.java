package com.goryaninaa.web.HttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Properties {
	
	private final int port;
	private final int threadsNumber;

	public Properties(String propertiesPath) {
		Map<String, String> propertiesMap = new HashMap<>();
		File file = new File(propertiesPath);
		try (BufferedReader input = new BufferedReader(new FileReader(file))) {
				
				input.lines().map(line -> line.split("=")).forEach(propArr -> propertiesMap.put(propArr[0], propArr[1]));
				
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IO exception - can't read properties file");
		}
		port = Integer.valueOf(propertiesMap.get("Port"));
		threadsNumber = Integer.valueOf(propertiesMap.get("ThreadsNumber"));
	}

	public int getPort() {
		return port;
	}

	public int getThreadsNumber() {
		return threadsNumber;
	}
	
}
