package io.nx.core;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;

public abstract class AbstractChannelHandler implements ChannelHandler {

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
		ByteBuffer buff = ctx.getBuffer();
		try {
			((SocketChannel)ctx.getKey().channel()).read(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(ChannelHandlerContext ctx, byte[] data) {
		ctx.writeBytes(data);
	}
}
