package com.goryaninaa.web.HttpServer.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerTest {
	private RequestHandlerStub requestHandler;
	private ExecutorService executor;
	private CountDownLatch countDownLatch;
	private Client client1;
	private Client client2;
	private Client client3;
	private Client client4;
	private Client client5;

	@BeforeEach
	public void init() {
		this.countDownLatch = new CountDownLatch(4);
		this.requestHandler = new RequestHandlerStub(countDownLatch);
		this.executor = Executors.newFixedThreadPool(4);
		this.client1 = new Client(8000);
		this.client2 = new Client(8000);
		this.client3 = new Client(8000);
		this.client4 = new Client(8000);
		this.client5 = new Client(8001);
	}
	
	@Test
	public void serverShouldCorrectlyHandleFourClientSimultaneously() throws InterruptedException, IOException {
		Server server = new Server("8000", "4", requestHandler);
		new Thread(() -> {
			server.start();
		}).start();
		Thread.sleep(5);
		Future<?> future1 = executor.submit(() -> client1.go("first request"));
		Future<?> future2 = executor.submit(() -> client2.go("second request"));
		Future<?> future3 = executor.submit(() -> client3.go("third request"));
		Future<?> future4 = executor.submit(() -> client4.go("fourth request"));
		executor.shutdown();
		while (!(future1.isDone() && future2.isDone() && future3.isDone() && future4.isDone()));
		countDownLatch.await();
		Thread.sleep(10);
		server.shutdown();
		Pattern pattern = Pattern.compile("\\n\\n");
		Matcher matcher1 = pattern.matcher(client1.getResponse());
		Matcher matcher2 = pattern.matcher(client2.getResponse());
		Matcher matcher3 = pattern.matcher(client3.getResponse());
		Matcher matcher4 = pattern.matcher(client4.getResponse());
		matcher1.find(); matcher2.find(); matcher3.find(); matcher4.find();
		
		assertTrue(countDownLatch.getCount() == 0, "Tasks worked in series");
		assertTrue(client1.getResponse().substring(matcher1.end()).equals(client1.getRequest()), "Client #1 get wrong response");
		assertTrue(client2.getResponse().substring(matcher1.end()).equals(client2.getRequest()), "Client #2 get wrong response");
		assertTrue(client3.getResponse().substring(matcher1.end()).equals(client3.getRequest()), "Client #3 get wrong response");
		assertTrue(client4.getResponse().substring(matcher1.end()).equals(client4.getRequest()), "Client #4 get wrong response");
	}
	
	@Test
	public void serverShouldShutdownCorrectly() throws InterruptedException, IOException {
		Server server = new Server("8001", "4", requestHandler);
		new Thread(() -> {
			server.start();
		}).start();
		Thread.sleep(5);
		Future<?> future = executor.submit(() -> client5.go("test"));
		executor.shutdown();
		Thread.sleep(10);
		server.shutdown();
		try {
			future.get(250, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
		}
		
		assertThrows(SocketException.class, () -> new Socket("127.0.0.1", 8000), "Server socket was not closed");
		assertFalse(client5.getRequest().equals(client1.getResponse()), "Server executor was not shutdown");
		assertTrue(countDownLatch.getCount() == 3, "Server was not awaiting while shutdown");
	}

}
