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

import com.goryaninaa.logger.LoggingMech.Logger;
import com.goryaninaa.logger.LoggingMech.LoggingMech;
import com.goryaninaa.logger.LoggingMech.StackTraceString;
import com.goryaninaa.web.HttpServer.exception.ServerException;
import com.goryaninaa.web.HttpServer.requesthandler.Response;

public class Server {
    private volatile boolean started;
    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private final RequestHandler requestHandler;
    private final Logger logger = LoggingMech.getLogger(this.getClass().getCanonicalName());

    public Server(int port, int threadsNumber, RequestHandler requestHandler) throws IOException {
        this.requestHandler = requestHandler;
        this.executor = Executors.newFixedThreadPool(threadsNumber);
        this.serverSocket = new ServerSocket(port);
        started = true;
        
    }

    public void start() {
		try {
			while (started) {
				Socket clientSocket = serverSocket.accept();
				executor.submit(() -> {
					run(clientSocket);
				});
			}
		} catch (IOException e) {
			logger.error(StackTraceString.get(e));
			throw new ServerException("Connection failed");
		}
    }
    
    public void shutdown() {
    	started = false;
    	if (!serverSocket.isClosed()) {
    		try {
				serverSocket.close();
				logger.info("Server shut down correctly");
			} catch (IOException e) {
				logger.error(StackTraceString.get(e));
			}
    	}
    	if (!executor.isShutdown()) {
    		executor.shutdownNow();
    		logger.info("Server threads completed correctly");
    	}
    }

	private void run(Socket socket) {
		logger.info("New connection accepted");
		try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter output = new PrintWriter(socket.getOutputStream())) {
			Optional<String> request = getRequest(input);
			if (request.isPresent()) {
				String requestString = request.get();
				Response response = requestHandler.handle(requestString);
				sendResponse(response, output);
				logger.info("Response with code " + response.getCode().getCode() + " was sent");
			}
			socket.close();
		} catch (IOException e) {
			logger.error("Error on handling request/n" + StackTraceString.get(e));
		}
    }

    private Optional<String> getRequest(BufferedReader input) throws IOException {
			long before = System.currentTimeMillis();
			while (!input.ready()) {
				long after = System.currentTimeMillis();
				if (after - before > 50) {
					logger.debug("Technical connection handled");
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
			logger.debug(requestString);
			Optional<String> request = Optional.ofNullable(requestString);
			return request;
	}

	private void sendResponse(Response response, PrintWriter output) {
        output.println(response.getResponseString());
        output.flush();
    }
}
