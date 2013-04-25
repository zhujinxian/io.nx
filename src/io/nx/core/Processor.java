package io.nx.core;

import io.nx.api.Handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable{
private static final int TIME_OUT = 100;
	
	private Selector selector;
	private boolean stop;
	
	private BlockingQueue<Integer> unbindQ = new LinkedBlockingQueue<Integer>();
	private BlockingQueue<Entry<SocketChannel, Handler>> regQ = new LinkedBlockingQueue<Entry<SocketChannel, Handler>>();
	
	private ConcurrentHashMap<SelectionKey, Handler> map = new ConcurrentHashMap<SelectionKey, Handler>();
	
	public Processor() {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void register(SocketChannel socket, Handler handler) {
		Entry<SocketChannel, Handler> entry = new AbstractMap.SimpleEntry<SocketChannel, Handler>(socket, handler);
		this.regQ.add(entry);
	}
	
	private void registerImp(Entry<SocketChannel, Handler> entry) {
		SocketChannel socket = entry.getKey();
		Handler handler = entry.getValue();
		try{
			socket.configureBlocking(false);
			SelectionKey key = socket.register(this.selector, SelectionKey.OP_READ);
			handler.open(key);
			this.map.put(key, handler);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				processUbindQ();
				processRegQ();
				long t = System.currentTimeMillis();
				int count = this.selector.select(TIME_OUT);
//				System.out.println(System.currentTimeMillis() - t);
				if (count == 0) {
					continue;
				}
				System.out.println("select " + count + "/" + this.selector.keys().size() );
				Set<SelectionKey> ready = selector.selectedKeys();
				Iterator<SelectionKey> iterator = ready.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					iterator.remove();
					Handler handler = this.map.get(key);
					if (handler != null && key.isReadable()) {
						handler.read(key);
					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processRegQ() {
		for (;;) {
			Entry<SocketChannel, Handler> entry = this.regQ.poll();
			if (entry == null) {
				break;
			}
			registerImp(entry);
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

	public void unBind(int port) {
		this.unbindQ.add(port);
	}
	
}
