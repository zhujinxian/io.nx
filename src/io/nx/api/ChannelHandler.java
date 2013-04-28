package io.nx.api;

public interface ChannelHandler {
	void open(ChannelHandlerContext ctx);
	void read(ChannelHandlerContext ctx);
	void write(ChannelHandlerContext ctx, byte[] data);
	void close(ChannelHandlerContext ctx);
}
