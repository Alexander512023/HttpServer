package com.goryaninaa.web.HttpServer;

import com.goryaninaa.web.HttpServer.requesthandler.RequestHandler;
import com.goryaninaa.web.HttpServer.server.Server;

public class App {
    public static void main( String[] args ) {
         RequestHandler requestHandler = new RequestHandler();
         Server server = new Server(8080, 2, requestHandler);

         server.start();
    }
}
