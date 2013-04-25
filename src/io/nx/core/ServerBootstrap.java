package io.nx.core;

import io.nx.api.HandlerFactory;
import io.nx.api.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerBootstrap implements Server{
	private Executor excutor;
	private ServerAcceptor acceptor;
	private List<Processor> processors = new ArrayList<Processor>();
	public ServerBootstrap() {
		this.excutor =  Executors.newCachedThreadPool();
		int count = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < count*2; i++) {
			Processor p = new Processor();
			this.processors.add(p);
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
	public void bind(int port, HandlerFactory factory) {
		acceptor.bind(port, factory);
		
	}

	@Override
	public void unBind(int port) {
		this.acceptor.unBind(port);
		
	}
}
