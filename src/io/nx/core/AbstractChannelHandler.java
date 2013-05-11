package io.nx.core;


import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractChannelHandler implements ChannelHandler {
		
	@Override
	public void open(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(ChannelHandlerContext ctx) {
		ctx.getKey().cancel();
		try {
			ctx.getKey().channel().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void read(ChannelHandlerContext ctx) {
		ctx.read();
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuffer buffer) {
		ctx.write(buffer);
	}
}
