package io.nx.test.lengthFiledCodec;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Client;
import io.nx.api.Decoder;
import io.nx.api.Encoder;
import io.nx.buffer.DefaultBufferAllocatorFactory;
import io.nx.codec.LengthFiledCodec;
import io.nx.core.NodeBootstrap;
import io.nx.extention.CodecHandler;

public class LenClient {

	
	private static int n = 1;
	Client client;
	InetSocketAddress isa;
	public LenClient(String ip, int port) throws Exception {
		this.client = new NodeBootstrap();
		this.client.setBufferAllocatorFactory(new DefaultBufferAllocatorFactory(256));
		this.isa = new InetSocketAddress(ip, port);
	}
	
	void boot() {
		for (int i = 0; i < n; i++) {
			 client.connect(this.isa, new LenHandlerFactoryC());
		}
		
	}
	
	
	public static void main(String[] args) throws Exception {
		LenClient client = new LenClient("127.0.0.1", 7894);
		client.boot();
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

class LenHandlerFactoryC implements ChannelHandlerFactory {
	private LengthFiledCodec codec = new LengthFiledCodec();

	@Override
	public ChannelHandler getHandler() {
		return new LenHandlerC(codec, codec);
	}
	
}

class LenHandlerC extends CodecHandler {

	public LenHandlerC(Encoder encoder, Decoder decoder) {
		super(encoder, decoder);
	}
	
	@Override
	public void open(ChannelHandlerContext ctx) {
		String msg = "HelloServer";
		ByteBuffer buff = ByteBuffer.allocate(2);
		short len = (short)msg.getBytes().length;
		buff.putShort(len);
		buff.flip();
		ctx.write(buff);
		ctx.write(ByteBuffer.wrap(msg.getBytes()));
		
		//llllllllll
		
		ByteBuffer buff1 = ByteBuffer.allocate(2);
		msg = "123456789abcdefgh";
		buff1.putShort((short)msg.getBytes().length);
		buff1.flip();
		ctx.write(buff1);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ctx.write(ByteBuffer.wrap(msg.getBytes()));
		
	}

	@Override
	public void process(ChannelHandlerContext ctx, Object data) {
				
	}
	
}
