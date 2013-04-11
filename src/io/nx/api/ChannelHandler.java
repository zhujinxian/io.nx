package io.nx.api;

import java.nio.ByteBuffer;

public interface ChannelHandler {
	void execute();
	void write(ByteBuffer buffer);
	void channelOpen();
	void Channelclosed();
}
