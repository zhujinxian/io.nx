package io.nx.test.lengthFiledCodec;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Decoder;
import io.nx.api.Encoder;
import io.nx.api.Server;
import io.nx.buffer.DefaultBufferAllocatorFactory;
import io.nx.codec.simple.LengthFieldFrameCodec;
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
	private LengthFieldFrameCodec codec = new LengthFieldFrameCodec();

	@Override
	public ChannelHandler getHandler() {
		return new LenHandler(codec, codec);
	}

}

class LenHandler extends CodecHandler<ByteBuffer, ByteBuffer> {

	public LenHandler(Encoder<ByteBuffer> encoder, Decoder<ByteBuffer> decoder) {
		super(encoder, decoder);
	}

	@Override
	public void process(ChannelHandlerContext ctx, ByteBuffer buff) {
		String msg = new String(buff.array(), buff.position(), buff.remaining());
		System.out.println(msg);

	}

}
