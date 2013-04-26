package io.nx.example;

import io.nx.api.Server;
import io.nx.core.ServerBootstrap;

public class EchoServer {
	public static void main(String[] args) {
		Server server = new ServerBootstrap();
		server.bind(7894, EchoHandler.class);
	}
	
	
}


