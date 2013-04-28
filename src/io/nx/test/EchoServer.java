package io.nx.test;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Server;
import io.nx.core.ServerBootstrap;
import io.nx.core.extention.SimpleChannelHandler;

public class EchoServer {
	public static void main(String[] args) {
		Server server = new ServerBootstrap();
		server.bind(7894, new EchoHandlerFactory());
	}
}

class EchoHandlerFactory implements ChannelHandlerFactory {

	@Override
	public ChannelHandler getHandler() {
		return new EchoHandler();
	}
	
}

class EchoHandler extends SimpleChannelHandler {

	@Override
	public void read(ChannelHandlerContext ctx) {
		super.read(ctx);
		ctx.writeBytes("hello client".getBytes());
	}
	
}


