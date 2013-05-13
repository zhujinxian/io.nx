package io.nx.core;


import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;

import java.nio.ByteBuffer;

public abstract class AbstractChannelHandler implements ChannelHandler {
		
	@Override
	public void open(ChannelHandlerContext ctx) {
		
	}

	@Override
	public void close(ChannelHandlerContext ctx) {
		ctx.destroy();
	}
	
	@Override
	public void read(ChannelHandlerContext ctx) {
		ctx.read();
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object data) {
		if (data instanceof ByteBuffer) {
			ctx.write((ByteBuffer) data);
		}
	}
}
