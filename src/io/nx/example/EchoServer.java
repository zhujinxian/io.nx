package io.nx.example;

import io.nx.api.Handler;
import io.nx.api.HandlerFactory;
import io.nx.api.Server;
import io.nx.core.ServerBootstrap;
import io.nx.extension.SimpleHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class EchoServer {
	public static void main(String[] args) {
		Server server = new ServerBootstrap();
		server.bind(7894, new EchoHandlerFactory());
	}
}

class EchoHandlerFactory implements HandlerFactory {

	@Override
	public Handler getHandler() {
		return new EchoHandler();
	}
	
}

class EchoHandler extends SimpleHandler {

	@Override
	public void process(SelectionKey key, ByteBuffer buffer) {
		super.process(key, buffer);
		ByteBuffer buff = ByteBuffer.wrap("HelloClient".getBytes());
		buff.position(buff.limit());
		this.write(key, buff);
	}
	
}

