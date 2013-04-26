package io.nx.api;

public interface Server {
	void bind(int port, Class<? extends AbstractHandler> className);
	void unBind(int port);
}
