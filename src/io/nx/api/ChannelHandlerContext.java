package io.nx.api;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface ChannelHandlerContext {
	ChannelHandler getHandler();
	void destroy();
	SocketChannel getChannel();
	ByteBuffer getBuffer();
	void releaseBuffer();
	void read();
	void write(ByteBuffer data);
	void setAttribute(Object k, Object v);
	Object getAttribute(Object k);
}
