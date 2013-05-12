package io.nx.test.lengthFiledCodec;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Decoder;
import io.nx.api.Encoder;
import io.nx.api.Server;
import io.nx.buffer.DefaultBufferAllocatorFactory;
import io.nx.codec.LengthFiledCodec;
import io.nx.core.NodeBootstrap;
import io.nx.extention.CodecHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


public class LenServer {

	
	public static void main(String[] args) throws Exception {
		Server server = new NodeBootstrap();
		server.setBufferAllocatorFactory(new DefaultBufferAllocatorFactory(256));
		server.bind(new InetSocketAddress(7894), new LenHandlerFactory());
	}

}

class LenHandlerFactory implements ChannelHandlerFactory {
	private LengthFiledCodec codec = new LengthFiledCodec();

	@Override
	public ChannelHandler getHandler() {
		return new LenHandler(codec, codec);
	}
	
}

class LenHandler extends CodecHandler {

	public LenHandler(Encoder encoder, Decoder decoder) {
		super(encoder, decoder);
	}

	@Override
	public void process(ChannelHandlerContext ctx, Object data) {
		ByteBuffer buff = (ByteBuffer)data;
		String msg = new String(buff.array(), buff.position(), buff.remaining());
		System.out.println(msg);
		
	}
	
}
