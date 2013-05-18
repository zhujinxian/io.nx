package io.nx.core;

import io.nx.api.ChannelHandlerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerAcceptor implements Runnable {

	private static final int TIME_OUT = 100;

	private boolean stop;
	private Selector selector;
	private List<Processor> processors;
	private ConcurrentHashMap<SelectionKey, ChannelHandlerFactory> factoryMap = new ConcurrentHashMap<SelectionKey, ChannelHandlerFactory>();

	private BlockingQueue<InetSocketAddress> unbindQ = new LinkedBlockingQueue<InetSocketAddress>();
	private BlockingQueue<Entry<InetSocketAddress, ChannelHandlerFactory>> bindQ = new LinkedBlockingQueue<Entry<InetSocketAddress, ChannelHandlerFactory>>();

	private int count;

	public ServerAcceptor(List<Processor> processors) {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.processors = processors;
	}

	public void bind(InetSocketAddress isa, ChannelHandlerFactory factory) {
		Entry<InetSocketAddress, ChannelHandlerFactory> entry = new AbstractMap.SimpleEntry<InetSocketAddress, ChannelHandlerFactory>(
				isa, factory);
		this.bindQ.add(entry);
	}

	public void unBind(InetSocketAddress isa) {
		this.unbindQ.add(isa);
	}

	private void bindImp(InetSocketAddress isa, ChannelHandlerFactory factory) {
		try {
			ServerSocketChannel server = ServerSocketChannel.open();
			server.socket().bind(isa);
			server.configureBlocking(false);
			SelectionKey key = server.register(this.selector,
					SelectionKey.OP_ACCEPT);
			this.factoryMap.put(key, factory);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unBindImp(InetSocketAddress isa) {
		for (SelectionKey key : factoryMap.keySet()) {
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			if (server.socket().getInetAddress().equals(isa.getAddress())
					&& server.socket().getLocalPort() == isa.getPort()) {
				this.factoryMap.remove(key);
				key.cancel();
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				for (Processor pro : this.processors) {
					pro.unBind(isa, true);
				}
				break;
			}
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				processBindQ();
				processUbindQ();
				int count = this.selector.select(TIME_OUT);
				if (count == 0) {
					continue;
				}
				Set<SelectionKey> ready = selector.selectedKeys();
				Iterator<SelectionKey> iterator = ready.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					iterator.remove();
					acceptKey(key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processUbindQ() {
		for (;;) {
			InetSocketAddress isa = this.unbindQ.poll();
			if (isa == null) {
				break;
			}
			this.unBindImp(isa);
		}
	}

	private void processBindQ() {
		for (;;) {
			Entry<InetSocketAddress, ChannelHandlerFactory> entry = this.bindQ
					.poll();
			if (entry == null) {
				break;
			}
			this.bindImp(entry.getKey(), entry.getValue());
		}
	}

	private void acceptKey(SelectionKey key) {
		ChannelHandlerFactory factory = this.factoryMap.get(key);
		if (factory == null) {
			return;
		}
		try {
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel socket = (SocketChannel) server.accept();
			if (socket == null) {
				return;
			}
			socket.configureBlocking(false);
			if (!socket.finishConnect()) {
				socket.close();
				return;
			}
			dispatch(socket, factory);
		} catch (Exception e) {
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
