package io.nx.codec.simple;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.nx.api.BufferAllocator;
import io.nx.api.ChannelHandlerContext;
import io.nx.api.Decoder;
import io.nx.api.Encoder;

public class LengthFieldFrameCodec implements Encoder<ByteBuffer>, Decoder<ByteBuffer> {
	
	private BufferAllocator buffPool;

	@Override
	public ByteBuffer doDecode(ChannelHandlerContext ctx) {
		ByteBuffer buffer = ctx.getBuffer();
		buffer.mark();
		if (buffer.remaining() >= 2) {
			int len = buffer.getShort() & 0xffff;
			if (buffer.remaining() >= len) {
				ByteBuffer buff = ByteBuffer.wrap(buffer.array(), buffer.position(), len);
				buffer.position(buffer.position() + len);
				return buff;
			} else {
				buffer.reset();
			}
		}
		return null;
	}

	@Override
	public List<ByteBuffer> doEncode(ChannelHandlerContext ctx, ByteBuffer data) {
		ByteBuffer buffer = (ByteBuffer) data;
		List<ByteBuffer> buffList = new ArrayList<ByteBuffer>();
		ByteBuffer buff = this.buffPool.buffer(this, 2);
		buff.putShort((short)buffer.remaining());
		buffer.flip();
		buffList.add(buff);
		buffList.add(buffer);
		return buffList;
	}

}
