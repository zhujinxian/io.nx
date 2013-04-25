package io.nx.api;

public interface Server {
	void bind(int port, HandlerFactory factory);
	void unBind(int port);
}
