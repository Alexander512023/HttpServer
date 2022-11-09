package com.goryaninaa.web.HttpServer.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.goryaninaa.web.HttpServer.model.HttpResponse;
import com.goryaninaa.web.HttpServer.requesthandler.RequestHandler;

public class Server {
    private int port;
    private int threadsNumber;
    private final ExecutorService executor;
    private final Queue<Socket> acceptedSockets = new ConcurrentLinkedQueue<>();
    private final RequestHandler requestHandler;

    public Server(int port, int threadsNumber, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        this.threadsNumber = threadsNumber;
        this.executor = Executors.newFixedThreadPool(threadsNumber);
    }

    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
        	startExecutor();
            while (true) {
                acceptedSockets.add(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void handle() {
    	Optional<Socket> socket = Optional.ofNullable(acceptedSockets.poll());
    	
    	if (socket.isPresent()) {
			try (BufferedReader input = new BufferedReader(
					new InputStreamReader(socket.get().getInputStream(), StandardCharsets.UTF_8));
					PrintWriter output = new PrintWriter(socket.get().getOutputStream())) {

				HttpResponse response = requestHandler.handle(getString(input));
				sendResponse(response, output);
				socket.get().close();

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

    private void startExecutor() {
    	for (int i = 0; i < threadsNumber; i++) {
            executor.execute(() -> {
                while (true) {
                    handle();
                }
            });
        }
		
	}
	
    private String getString(BufferedReader input) throws IOException {
        // ждем первой строки запроса
        while (!input.ready()) ;

        // считываем и печатаем все что было отправлено клиентом
        System.out.println();
        while (input.ready()) {
            System.out.println(input.readLine());
        }

        return "";
    }

    private void sendResponse(HttpResponse response, PrintWriter output) {
        output.println(response.getResponseString());
        output.flush();
    }
}
