package com.goryaninaa.web.HttpServer.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.goryaninaa.web.HttpServer.model.HttpResponse;

public class Server {
    private int port;
    private boolean started;
    private ServerSocket serverSocket;
    private final ExecutorService executor;
    private final RequestHandler requestHandler;

    public Server(int port, int threadsNumber, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        this.executor = Executors.newFixedThreadPool(threadsNumber);
        started = true;
    }

    public void start() throws IOException {
		this.serverSocket = new ServerSocket(port);
		while (started) {
			executor.submit(() -> {
				try {
					run(serverSocket.accept());
				} catch (IOException e) {
					if (started) {
						e.printStackTrace();
					}
				}
			});
		}
    }
    
    public void shutdown() throws IOException {
    	started = false;
    	executor.shutdownNow();
    	serverSocket.close();
    }

	private void run(Socket socket) {
		try (BufferedReader input = new BufferedReader(
				new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter output = new PrintWriter(socket.getOutputStream())) {

			HttpResponse response = requestHandler.handle(getString(input));
			sendResponse(response, output);
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private String getString(BufferedReader input) throws IOException {
    	String request = "";
        while (!input.ready()) ;

        while (input.ready()) {
        	request = request + input.readLine();
        }

        return request;
    }

    private void sendResponse(HttpResponse response, PrintWriter output) {
        output.println(response.getResponseString());
        output.flush();
    }
}
