package io.nx.api;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public interface Handler {
	void open(SelectionKey key);
	void read(SelectionKey key);
	boolean write(SelectionKey key, ByteBuffer buffer);
	void close(SelectionKey key);
}
