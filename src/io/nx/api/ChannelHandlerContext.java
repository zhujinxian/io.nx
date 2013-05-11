package io.nx.api;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface ChannelHandlerContext {
	ChannelHandler getHandler();
	SelectionKey getKey();
	SocketChannel getChannel();
	ByteBuffer getBuffer();
	void setBufferSize(int size);
	void read();
	void write(ByteBuffer data);
	void attach(Object parameter);
	Object attachment();
}
