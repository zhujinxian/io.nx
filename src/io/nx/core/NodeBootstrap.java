package io.nx.core;

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

import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Node;

public class NodeBootstrap implements Node {
	
	private Executor excutor;
	private ServerAcceptor acceptor;
	private List<Processor> processors = new ArrayList<Processor>();
	private Map<Selector, Processor> procMap = new HashMap<Selector, Processor>();
	
	private int count;
	
	public NodeBootstrap() {
		this.excutor =  Executors.newCachedThreadPool();
		int count = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < count*2; i++) {
			Processor p = new Processor();
			this.processors.add(p);
			this.procMap.put(p.getSelector(), p);
		}
		this.acceptor = new ServerAcceptor(this.processors);
		boot();
	}
		
	private void boot() {
		for (Processor p : this.processors) {
			this.excutor.execute(p);
		}
		this.excutor.execute(this.acceptor);
	}

	@Override
	public void bind(InetSocketAddress isa, ChannelHandlerFactory factory) {
		this.acceptor.bind(isa, factory);
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

	@Override
	public void disconnect(InetSocketAddress isa) {
		for (Processor proc : this.processors) {
			proc.unBind(isa, false);
		}
	}

	@Override
	public void unBind(InetSocketAddress isa) {
		this.acceptor.unBind(isa);
	}

	private void dispatch(SocketChannel socket, ChannelHandlerFactory factory) {
		int num = this.processors.size();
		int index = count % num;
		count++;
		Processor processor = this.processors.get(index);
		processor.register(socket, factory.getHandler());
		
	}
}
