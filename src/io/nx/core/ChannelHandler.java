package io.nx.core;


import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public interface ChannelHandler {
	void open(SelectionKey key);
	void read(SelectionKey key);
	void write(SelectionKey key, ByteBuffer buffer);
	void close(SelectionKey key);
	void setReactor(Reactor reactor);
}
