package io.nx.core;

import io.nx.api.ChannelHandler;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

public abstract class AbstractHandler implements ChannelHandler{
	
	private int id;
	protected SelectableChannel channel;
	protected ServerAcceptor acceptor;
	protected Processor processor;

	public int getId() {
		return id;
	}
	
	protected void setAcceptor(ServerAcceptor acceptor) {
		this.acceptor = acceptor;
	}

	protected void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
	protected void setChannel(SelectableChannel channel) {
		this.channel = channel;
	}
	
	
	public void close() {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
