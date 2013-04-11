package io.nx.core.extension;

import java.nio.ByteBuffer;

import io.nx.core.IoHandler;

public abstract class DefaultChannelHandler extends IoHandler {
	
	@Override
	public void execute() {
		super.execute();
		process(this.inputBuffer);
	}

	public abstract void process(ByteBuffer inputBuffer);

	@Override
	public void channelOpen() {
		
	}

	@Override
	public void Channelclosed() {
		
	}
	
}
