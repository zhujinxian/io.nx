package io.nx.core;


import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public interface Reactor {
	Selector getSelector();
	void register(SocketChannel socket, ChannelHandler handler);
	public void unBind(int port);
	public void flush(SelectionKey key, ChannelHandler handler);
}
