package com.goryaninaa.web.HttpServer.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {
	private RequestHandlerStab requestHandler;
	private Server server;
	private ExecutorService executor;
	private Client client1;
	private Client client2;
	private Client client3;
	private Client client4;
	

	@Before
	public void init() {
		this.requestHandler = new RequestHandlerStab();
		this.server = new Server(8000, 4, requestHandler);
		this.executor = Executors.newFixedThreadPool(4);
		this.client1 = new Client();
		this.client2 = new Client();
		this.client3 = new Client();
		this.client4 = new Client();
	}
	
	@After
	public void finalize() throws IOException, InterruptedException {
		Thread.sleep(5);
		server.shutdown();
	}
	
	@Test
	public void serverShouldCorrectlyHandleFourClientSimultaneously() throws InterruptedException {
		new Thread(() -> {
			try {
				server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		long before = System.currentTimeMillis();
		Thread.sleep(5);
		Future<?> future1 = executor.submit(() -> client1.go("first request"));
		Future<?> future2 = executor.submit(() -> client2.go("second request"));
		Future<?> future3 = executor.submit(() -> client3.go("third request"));
		Future<?> future4 = executor.submit(() -> client4.go("fourth request"));
		while (!(future1.isDone() && future2.isDone() && future3.isDone() && future4.isDone()));
		long after = System.currentTimeMillis();
		
		assertTrue(after - before < 250, "Tasks worked in series");
		assertTrue(client1.getRequest().equals(client1.getResponse()), "Client #1 get wrong response");
		assertTrue(client2.getRequest().equals(client2.getResponse()), "Client #2 get wrong response");
		assertTrue(client3.getRequest().equals(client3.getResponse()), "Client #3 get wrong response");
		assertTrue(client4.getRequest().equals(client4.getResponse()), "Client #4 get wrong response");
	}
	
	@Test
	public void serverShouldShutdownCorrectly() throws InterruptedException {
		Thread.sleep(5);
		new Thread(() -> {
			try {
				server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		Thread.sleep(5);
		Future<?> future = executor.submit(() -> client1.go("test"));
		
		try {
			server.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			future.get(250, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
		}
		
		assertThrows(SocketException.class, () -> new Socket("127.0.0.1", 8000), "Server socket was not closed");
		assertFalse(client1.getRequest().equals(client1.getResponse()), "Server executor was not shutdown");
	}

}
