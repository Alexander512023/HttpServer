package com.goryaninaa.web.HttpServer.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
			Socket clientSocket = serverSocket.accept();
			
			executor.submit(() -> {
				run(clientSocket);
			});
		}
    }
    
    public void shutdown() throws IOException, InterruptedException {
    	started = false;
    	executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    	serverSocket.close();
    }

	private void run(Socket socket) {
		System.out.println("New connection accepted");
		try (BufferedReader input = new BufferedReader(
				new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter output = new PrintWriter(socket.getOutputStream())) {
			
			Optional<String> request = getRequest(input);
			if (request.isPresent()) {
				String requestString = request.get();
				HttpResponse response = requestHandler.handle(requestString);
				sendResponse(response, output);
				System.out.println("Response sent");
			}
			
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private Optional<String> getRequest(BufferedReader input) throws IOException {
    	long before = System.currentTimeMillis();
    	
        while (!input.ready()) {
        	long after = System.currentTimeMillis();
        	if (after - before > 50) {
        		System.out.println("Technical connection handled");
            	return Optional.empty();
        	}
        }
        
        return readRequest(input);
    }

    private Optional<String> readRequest(BufferedReader input) throws IOException {
    	String requestString = "";
        while (input.ready()) {
        	requestString = requestString + input.readLine() + "\n";
        }
        
        System.out.println(requestString);
        Optional<String> request = Optional.ofNullable(requestString);
		return request;
	}

	private void sendResponse(HttpResponse response, PrintWriter output) {
        output.println(response.getResponseString());
        output.flush();
    }
}
