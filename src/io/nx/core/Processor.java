package io.nx.core;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable, Reactor{
private static final int TIME_OUT = 100;
	
	private Selector selector;
	private boolean stop;
	
	private BlockingQueue<Integer> unbindQ = new LinkedBlockingQueue<Integer>();
	private BlockingQueue<Entry<SocketChannel, ChannelHandler>> regQ = new LinkedBlockingQueue<Entry<SocketChannel, ChannelHandler>>();
	private BlockingQueue<Entry<SelectionKey, ChannelHandler>> flushQ = new LinkedBlockingQueue<Entry<SelectionKey, ChannelHandler>>();
	
	private ConcurrentHashMap<SelectionKey, ChannelHandler> map = new ConcurrentHashMap<SelectionKey, ChannelHandler>();
	
	public Processor() {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Selector getSelector() {
		return selector;
	}

	public void register(SocketChannel socket, ChannelHandler handler) {
		Entry<SocketChannel, ChannelHandler> entry = new AbstractMap.SimpleEntry<SocketChannel, ChannelHandler>(socket, handler);
		this.regQ.add(entry);
	}
	
	public void unBind(int port) {
		this.unbindQ.add(port);
	}

	public void flush(SelectionKey key, ChannelHandler handler) {
		this.flushQ.add(new AbstractMap.SimpleEntry<SelectionKey, ChannelHandler>(key, handler));
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				processUbindQ();
				processRegQ();
				processFlushQ();
				int count = this.selector.select(TIME_OUT);
				if (count == 0) {
					if (this.selector.selectedKeys().size() > 0) {
						this.selector.selectedKeys().clear();
					}
					continue;
				}
				Set<SelectionKey> ready = selector.selectedKeys();
				Iterator<SelectionKey> iterator = ready.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					iterator.remove();
					ChannelHandler handler = this.map.get(key);
					if (handler != null && key.isValid()) {
						if (key.isReadable()) {
							handler.read(key);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processFlushQ() {
		List<Entry<SelectionKey, ChannelHandler>> flushList = new ArrayList<Entry<SelectionKey, ChannelHandler>>();
		this.flushQ.drainTo(flushList);
		for (Entry<SelectionKey, ChannelHandler> entry : flushList) {
			fulshImp(entry);
		}
	}

	private void fulshImp(Entry<SelectionKey, ChannelHandler> entry) {
		SelectionKey key = entry.getKey();
		ChannelHandler handler = entry.getValue();
		handler.write(key, null);
		
	}

	private void processRegQ() {
		for (;;) {
			Entry<SocketChannel, ChannelHandler> entry = this.regQ.poll();
			if (entry == null) {
				break;
			}
			registerImp(entry);
		}
	}
	
	private void registerImp(Entry<SocketChannel, ChannelHandler> entry) {
		SocketChannel socket = entry.getKey();
		ChannelHandler handler = entry.getValue();
		try{
			socket.configureBlocking(false);
			SelectionKey key = socket.register(this.selector, SelectionKey.OP_READ);
			handler.open(key);
			this.map.put(key, handler);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void processUbindQ() {
		for (;;) {
			Integer port = this.unbindQ.poll();
			if (port == null) {
				break;
			}
			this.unBindImp(port);
		}		
	}

	private void unBindImp(Integer port) {
		for (SelectionKey key : this.selector.keys()) {
			SocketChannel soc = (SocketChannel)key.channel();
			if (soc.socket().getLocalPort() == port) {
				key.cancel();
				try {
					soc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
