package io.nx.test;

import java.nio.ByteBuffer;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Server;
import io.nx.core.ServerAcceptor;
import io.nx.core.extension.DefaultChannelHandler;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new ServerAcceptor();
		server.bind(8000, new HandlerFactory());

	}
}

class HandlerFactory implements ChannelHandlerFactory {

	@Override
	public ChannelHandler getChannelHandler() {
		return new SimpleChannleHandler();
	}
	
}
 
class SimpleChannleHandler extends DefaultChannelHandler {

	@Override
	public void process(ByteBuffer inputBuffer) {
		
	}
	
}
