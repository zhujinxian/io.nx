package io.nx.test;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Client;
import io.nx.core.ClientBootstrap;
import io.nx.core.extention.SimpleChannelHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class EchoClient {
	private static int n = 1;
	Client client;
	InetSocketAddress isa;
	public EchoClient(String ip, int port) {
		this.client = new ClientBootstrap();
		this.isa = new InetSocketAddress(ip, port);
	}
	
	void boot() {
		for (int i = 0; i < n; i++) {
			 client.connect(this.isa, new ClientHandlerFactory());
		}
		
	}
	public static void main(String[] args) {
		EchoClient client = new EchoClient("192.168.0.141", 7894);
		client.boot();
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		ctx.writeBytes("HelloServer".getBytes());
	}

	@Override
	public void open(ChannelHandlerContext ctx) {
		super.open(ctx);
		ctx.writeBytes("HelloServer".getBytes());
	}
	
	
	
}

