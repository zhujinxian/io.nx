package io.nx.test.echo;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Server;
import io.nx.buffer.DefaultBufferAllocatorFactory;
import io.nx.core.NodeBootstrap;
import io.nx.extention.SimpleChannelHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class EchoServer {
	public static void main(String[] args) throws Exception {
		Server server = new NodeBootstrap();
		server.setBufferAllocatorFactory(new DefaultBufferAllocatorFactory(
				102400));
		server.bind(new InetSocketAddress(7894), new EchoHandlerFactory());
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
		ctx.releaseBuffer();

		this.write(ctx, ByteBuffer.wrap("hello client".getBytes()));
	}

}
