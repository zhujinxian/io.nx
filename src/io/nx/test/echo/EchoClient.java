package io.nx.test.echo;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Client;
import io.nx.buffer.DefaultBufferAllocatorFactory;
import io.nx.core.NodeBootstrap;
import io.nx.extention.SimpleChannelHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class EchoClient {
	private static int n = 800;
	Client client;
	InetSocketAddress isa;
	public EchoClient(String ip, int port) throws Exception {
		this.client = new NodeBootstrap();
		this.client.setBufferAllocatorFactory(new DefaultBufferAllocatorFactory(20));
		this.isa = new InetSocketAddress(ip, port);
	}
	
	void boot() {
		for (int i = 0; i < n; i++) {
			 client.connect(this.isa, new ClientHandlerFactory());
		}
		
	}
	public static void main(String[] args) throws Exception {
		EchoClient client = new EchoClient("127.0.0.1", 7894);
		client.boot();
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ClientHandlerFactory implements ChannelHandlerFactory {

	@Override
	public ChannelHandler getHandler() {
		return new ClientHandler();
	}
	
}

class ClientHandler extends SimpleChannelHandler {
	
	static AtomicLong num = new AtomicLong(0);
	static AtomicLong time = new AtomicLong(System.currentTimeMillis());

	@Override
	public void read(ChannelHandlerContext ctx) {
		super.read(ctx);
		ByteBuffer buffer = ctx.getBuffer();
		if (buffer.remaining() > 0) {
			buffer.clear();
			num.addAndGet(1);
			long t = System.currentTimeMillis() - time.longValue();
			float xx = num.longValue() / (t * 1.0f) * 1000;
			System.out.println("q/s= " + xx);
		}
		ctx.releaseBuffer();
		this.write(ctx, ByteBuffer.wrap("HelloServer".getBytes()));
	}

	@Override
	public void open(ChannelHandlerContext ctx) {
		super.open(ctx);
		ctx.write(ByteBuffer.wrap("HelloServer".getBytes()));
	}
	
	
	
}

