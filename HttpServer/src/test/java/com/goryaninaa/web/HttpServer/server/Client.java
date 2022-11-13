package com.goryaninaa.web.HttpServer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private String request;
	private String response = "";
	
	public Client() {
	}
	
	public void go(String request) {
		this.request = request;
		
		try(Socket clientSocket = new Socket("127.0.0.1", 8000);
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			
			out.println(request);
			out.flush();
			
			while (!in.ready());
			
			while (in.ready()) {
				response += in.readLine() + "\n";
			}
			
			response = response.trim();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		
	}

	public String getRequest() {
		return request;
	}

	public String getResponse() {
		return response;
	}
}
