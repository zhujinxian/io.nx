package io.nx.api;

import java.net.InetSocketAddress;

public interface Client {
	void connect(InetSocketAddress isa, ChannelHandlerFactory factory);

	void disconnect(InetSocketAddress isa);

	void setBufferAllocatorFactory(BufferAllocatorFactory factory)
			throws Exception;
}
