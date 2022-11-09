package com.goryaninaa.web.HttpServer;

import com.goryaninaa.web.HttpServer.requesthandler.HttpRequestHandler;
import com.goryaninaa.web.HttpServer.server.Server;

public class App {
    public static void main( String[] args ) {
         HttpRequestHandler requestHandler = new HttpRequestHandler();
         Server server = new Server(8080, 2, requestHandler);

         server.start();
    }
}
