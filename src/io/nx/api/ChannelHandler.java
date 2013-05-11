package io.nx.api;

import java.nio.ByteBuffer;

public interface ChannelHandler {
	void open(ChannelHandlerContext ctx);
	void read(ChannelHandlerContext ctx);
	void write(ChannelHandlerContext ctx, ByteBuffer data);
	void close(ChannelHandlerContext ctx);
}
