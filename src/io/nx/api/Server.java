package io.nx.api;

public interface Server {
	void bind(int port, ChannelHandlerFactory factory);
	void unBind(int port);
}
