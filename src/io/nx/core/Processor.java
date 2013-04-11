package io.nx.core;


import io.nx.api.ChannelHandler;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Processor implements Runnable {
	private static final int TIME_OUT = 100;
	
	private Selector selector;
	private boolean stop;
	
	private ConcurrentLinkedQueue<ChannelHandler> flushQueue = new ConcurrentLinkedQueue<ChannelHandler>();
	private ConcurrentLinkedQueue<ChannelHandler> openQueue = new ConcurrentLinkedQueue<ChannelHandler>();
	private ConcurrentLinkedQueue<ChannelHandler> closeQueue = new ConcurrentLinkedQueue<ChannelHandler>();
	
	public void register(SocketChannel socket, ChannelHandler handler) {
		this.open(handler);
		try{
			socket.configureBlocking(false);
			socket.register(this.selector, SelectionKey.OP_READ, handler);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (!stop) {
			try {
				this.flushAll();
				this.openAll();
				this.closeAll();
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
	
	private void open(ChannelHandler handler) {
		this.openQueue.add(handler);
	}
	
	private void openAll() {
		for (;;) {
			ChannelHandler handler = this.openQueue.poll();
			if (handler != null) {
				handler.channelOpen();
			} else {
				break;
			}
		}
	}
	
	protected void close(ChannelHandler handler) {
		this.closeQueue.add(handler);
	}
	
	private void closeAll() {
		for (;;) {
			ChannelHandler handler = this.closeQueue.poll();
			if (handler != null) {
				handler.Channelclosed();
			} else {
				break;
			}
		}
	}
	
	public void flush(ChannelHandler handler) {
		this.flushQueue.add(handler);
	}
	
	private void flushAll() {
		List<ChannelHandler> flushList = new ArrayList<ChannelHandler>(this.flushQueue);
		this.flushQueue.clear();
		for (ChannelHandler handler : flushList) {
			handler.write(null);
		}
	}
}
