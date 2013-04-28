package io.nx.core;

import io.nx.api.ChannelHandlerContext;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientBootstrap implements Client {
	
	private Executor excutor;
	private List<Processor> processors = new ArrayList<Processor>();
	private Map<Selector, Processor> procMap = new HashMap<Selector, Processor>();
	private int count;
	public ClientBootstrap() {
		this.excutor =  Executors.newCachedThreadPool();
		int count = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < count*2; i++) {
			Processor p = new Processor();
			this.processors.add(p);
			this.procMap.put(p.getSelector(), p);
		}
		boot();
	}
	
	private void boot() {
		for (Processor p : this.processors) {
			this.excutor.execute(p);
		}
	}

	@Override
	public void connect(InetSocketAddress isa, ChannelHandlerFactory factory) {
		try {
			SocketChannel socket = SocketChannel.open();
			dispatch(socket, factory);
			socket.connect(isa);
			if (!socket.finishConnect()) {
				throw new IOException();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	private void dispatch(SocketChannel socket, ChannelHandlerFactory factory) {
		int num = this.processors.size();
		int index = count % num;
		count++;
		Processor processor = this.processors.get(index);
		processor.register(socket, factory.getHandler());
		
	}

}
