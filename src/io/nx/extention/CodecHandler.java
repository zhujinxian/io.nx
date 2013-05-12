package io.nx.extention;

import java.nio.ByteBuffer;
import java.util.List;

import io.nx.api.ChannelHandlerContext;
import io.nx.api.Decoder;
import io.nx.api.Encoder;
import io.nx.core.AbstractChannelHandler;

public abstract class CodecHandler extends AbstractChannelHandler {
	
	private Encoder encoder;
	private Decoder decoder;
	
	public CodecHandler(Encoder encoder,  Decoder decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	public void read(ChannelHandlerContext ctx) {
		super.read(ctx);
		ByteBuffer buffer = ctx.getBuffer();
		buffer.flip();
		while (buffer.hasRemaining()) {
			Object data = this.decoder.doDecode(buffer);
			if (data != null) {
				this.process(ctx, data);
			} else {
				buffer.compact();
				return;
			}
			
		}
		ctx.releaseBuffer();
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object data) {
		List<ByteBuffer>buffs = this.encoder.doEncode(data);
		for (ByteBuffer buff : buffs) {
			super.write(ctx, buff);
		}
	}
	
	public abstract void process(ChannelHandlerContext ctx, Object data);
	
}
