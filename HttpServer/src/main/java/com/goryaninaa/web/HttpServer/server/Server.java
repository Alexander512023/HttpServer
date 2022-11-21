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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.goryaninaa.web.HttpServer.requesthandler.Response;

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

    public void start() {
		try {
			this.serverSocket = new ServerSocket(port);

			while (started) {
				Socket clientSocket = serverSocket.accept();

				executor.submit(() -> {
					run(clientSocket);
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Server start failed");
		}
    }
    
    public void shutdown() throws IOException, InterruptedException {
    	started = false;
    	if (!serverSocket.isClosed()) {
    		serverSocket.close();
    	}
    	if (!executor.isShutdown()) {
    		executor.shutdownNow();
    	}
    }

	private void run(Socket socket) {
		System.out.println("New connection accepted");
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter output = new PrintWriter(socket.getOutputStream())) {
			
			Optional<String> request = getRequest(input);
			if (request.isPresent()) {
				String requestString = request.get();
				Response response = requestHandler.handle(requestString);
				sendResponse(response, output);
				System.out.println("Response with code " + response.getCode().getCode() + " was sent");
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
			int contentLength = 0;

			Pattern patternBodyLength = Pattern.compile("Content-Length");
			Pattern patternHeadersEnd = Pattern.compile("^$");
			while (input.ready()) {
				String currentLine = input.readLine();
				requestString = requestString + currentLine + "\n";
				
				Matcher matcherBodyLength = patternBodyLength.matcher(currentLine);
				Matcher matcherHeadersEnd = patternHeadersEnd.matcher(currentLine);
				
				if (matcherBodyLength.find()) {
					contentLength = Integer.valueOf(currentLine.split(":")[1].trim());
				}
				if (matcherHeadersEnd.find()) {
					char[] charArr = new char[contentLength];
					input.read(charArr);
					for (char symbol : charArr){
						requestString += symbol;
					}
				}
			}

			System.out.println(requestString);
			Optional<String> request = Optional.ofNullable(requestString);
			return request;
	}

	private void sendResponse(Response response, PrintWriter output) {
        output.println(response.getResponseString());
        output.flush();
    }
}
