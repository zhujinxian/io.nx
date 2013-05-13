package io.nx.extention;

import io.nx.api.ChannelHandlerContext;
import io.nx.api.Decoder;
import io.nx.api.Encoder;
import io.nx.core.AbstractChannelHandler;

import java.nio.ByteBuffer;
import java.util.List;

public abstract class CodecHandler<E, D> extends AbstractChannelHandler {
	
	private Encoder<E> encoder;
	private Decoder<D> decoder;
	
	public CodecHandler(Encoder<E> encoder,  Decoder<D> decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	public void read(ChannelHandlerContext ctx) {
		super.read(ctx);
		ByteBuffer buffer = ctx.getBuffer();
		buffer.flip();
		while (buffer.hasRemaining()) {
			D data = this.decoder.doDecode(buffer);
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
		@SuppressWarnings("unchecked")
		List<ByteBuffer>buffs = this.encoder.doEncode((E)data);
		for (ByteBuffer buff : buffs) {
			super.write(ctx, buff);
		}
	}
	
	public abstract void process(ChannelHandlerContext ctx, D data);
	
}
