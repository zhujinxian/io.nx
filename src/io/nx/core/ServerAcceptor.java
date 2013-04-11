package io.nx.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerFactory;
import io.nx.api.Server;

public class ServerAcceptor implements Server, Runnable {
	
	private static final int TIME_OUT = 100;
	private static final int DEFAULT_INPUT_BUFFER_SIZE = 4096;
	
	private Selector selector;
	private int count;
	private boolean stop;
	private List<Processor> processors;
	
	private int inputBufferSize = DEFAULT_INPUT_BUFFER_SIZE;
	
	private ChannelHandlerFactory factory;
	
	public ServerAcceptor() {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void bind(int port, ChannelHandlerFactory factory) {
		try {
			ServerSocketChannel server = ServerSocketChannel.open();
			InetSocketAddress isa = new InetSocketAddress(port);
			server.socket().bind(isa);
			server.configureBlocking(false);
			server.register(this.selector, SelectionKey.OP_ACCEPT, 
					new AcceptHandler(this, server));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void dispatch(SocketChannel socket) {
		int num = this.processors.size();
		int index = count % num;
		count++;
		Processor processor = this.processors.get(index);
		AbstractHandler handler = (AbstractHandler)this.factory.getChannelHandler();
		handler.setChannel(socket);
		handler.setAcceptor(this);
		handler.setProcessor(processor);
		processor.register(socket, handler);
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				int count = this.selector.select(TIME_OUT);
				if (count == 0) {
					continue;
				}
				Set<SelectionKey> ready = selector.selectedKeys();
				Iterator<SelectionKey> iterator = ready.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					iterator.remove();
					ChannelHandler handler = (ChannelHandler)key.attachment();
					handler.execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setInputBufferSize(int size) {
		this.inputBufferSize = size;
	}
	
	public ByteBuffer getBuffer() {
		return ByteBuffer.allocate(inputBufferSize);
	}

}