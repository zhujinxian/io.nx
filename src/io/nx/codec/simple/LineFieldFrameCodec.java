package io.nx.codec.simple;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.nx.api.ChannelHandlerContext;
import io.nx.api.Decoder;
import io.nx.api.Encoder;

public class LineFieldFrameCodec implements Decoder<String>, Encoder<String> {
	public static final String CRLF = "\r\n";
	public static final byte CR = (byte)'\r';
	public static final byte LF = (byte)'\n';
	
	@Override
	public List<ByteBuffer> doEncode(ChannelHandlerContext ctx, String data) {
		List<ByteBuffer> buffs = new ArrayList<ByteBuffer>(1);
		buffs.add( ByteBuffer.wrap((data + CRLF).getBytes()));
		return buffs;
	}

	@Override
	public String doDecode(ChannelHandlerContext ctx) {
		ByteBuffer buffer = ctx.getBuffer();
		buffer.mark();
		while (buffer.hasRemaining()) {
			byte a = buffer.get();
			if (a == CR) {
				byte b = buffer.get();
				if (b == LF) {
					return new String(buffer.array(), 0, buffer.position());
				} else if (b == CR) {
					buffer.position(buffer.position() - 1);
				}
			}
		}
		buffer.reset();
		return null;
	}
}
