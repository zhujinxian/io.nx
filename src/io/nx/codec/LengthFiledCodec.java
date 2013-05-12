package io.nx.codec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.nx.api.BufferAllocator;
import io.nx.api.Decoder;
import io.nx.api.Encoder;

public class LengthFiledCodec implements Encoder, Decoder {
	
	private BufferAllocator buffPool;

	@Override
	public Object doDecode(ByteBuffer buffer) {
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
	public List<ByteBuffer> doEncode(Object data) {
		if (data instanceof ByteBuffer) {
			ByteBuffer buffer = (ByteBuffer) data;
			List<ByteBuffer> buffList = new ArrayList<ByteBuffer>();
			ByteBuffer buff = this.buffPool.buffer(this, 2);
			buff.putShort((short)buffer.remaining());
			buffer.flip();
			buffList.add(buff);
			buffList.add(buffer);
			return buffList;
		} else {
			return null;
		}
		
	}

}
