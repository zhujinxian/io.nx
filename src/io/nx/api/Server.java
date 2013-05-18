package io.nx.api;

import java.net.InetSocketAddress;

public interface Server {
	void bind(InetSocketAddress isa, ChannelHandlerFactory factory);

	void unBind(InetSocketAddress isa);

	void setBufferAllocatorFactory(BufferAllocatorFactory factory)
			throws Exception;
}
