package io.nx.core;


import io.nx.api.ChannelHandler;
import io.nx.api.ChannelHandlerContext;

import java.io.IOException;
import java.nio.ByteBuffer;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Processor implements Runnable {
private static final int TIME_OUT = 100;
	
	private Selector selector;
	private boolean stop;
	
	private BlockingQueue<Integer> unbindQ = new LinkedBlockingQueue<Integer>();
	private BlockingQueue<Entry<SocketChannel, ChannelHandler>> regQ = new LinkedBlockingQueue<Entry<SocketChannel, ChannelHandler>>();
	private BlockingQueue<ChannelHandlerContext> flushQ = new LinkedBlockingQueue<ChannelHandlerContext>();
	
	private ConcurrentHashMap<SelectionKey, ChannelHandlerContext> map = new ConcurrentHashMap<SelectionKey, ChannelHandlerContext>();
	
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

	public void flush(ChannelHandlerContext ctx) {
		this.flushQ.add(ctx);
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
					SelectionKey key = iterator.next();
					iterator.remove();
					try {
						ChannelHandlerContext ctx = this.map.get(key);
						ChannelHandler handler = ctx.getHandler();
						handler.read(ctx);
					} catch(Exception e) {
						e.printStackTrace();
						key.cancel();
						key.channel().close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processFlushQ() {
		List<ChannelHandlerContext> flushList = new ArrayList<ChannelHandlerContext>();
		this.flushQ.drainTo(flushList);
		for (ChannelHandlerContext ctx : flushList) {
			fulshImp(ctx);
		}
	}

	private void fulshImp(ChannelHandlerContext ctx) {
		ctx.writeBytes(null);
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
			ChannelHandlerContext ctx = new DefaultChannelHandlerContext(key, handler);
			handler.open(ctx);
			this.map.put(key, ctx);
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
	
	
	private class DefaultChannelHandlerContext implements ChannelHandlerContext {
		
		private Processor proc;
		private ChannelHandler handler;
		private SelectionKey key;
		private ByteBuffer inputBuff = ByteBuffer.allocate(4096);
		private BlockingQueue<ByteBuffer> outQ = new LinkedBlockingQueue<ByteBuffer>();
		
		private Lock lock = new ReentrantLock();
		
		

		public DefaultChannelHandlerContext(SelectionKey key, ChannelHandler handler) {
			this.handler = handler;
			this.key = key;
			this.proc = Processor.this;
		}

		@Override
		public ByteBuffer getBuffer() {
			return this.inputBuff;
		}

		@Override
		public SelectionKey getKey() {
			return this.key;
		}

		@Override
		public void setBufferSize(int size) {
			ByteBuffer buff = ByteBuffer.allocate(size);
			buff.put(this.inputBuff);
			this.inputBuff = buff;
		}

		@Override
		public void writeBytes(byte[] data) {
			if (data != null) {
				ByteBuffer buffer = ByteBuffer.allocate(data.length);
				buffer.put(data);
				buffer.flip();
				this.outQ.add(buffer);
			}
			this.flush();
		}

		@Override
		public ChannelHandler getHandler() {
			return this.handler;
		}

		@Override
		public void read() {
			try {
				int count = this.getChannel().read(this.inputBuff);
				if (count == -1) {
					this.getHandler().close(this);
				}
			} catch (IOException e) {
				e.printStackTrace();
				this.getHandler().close(this);
			}	
		}
		
		@Override
		public void attach(Object parameter) {
			this.key.attach(parameter);
		}

		@Override
		public Object attachment() {
			return this.key.attachment();
		}
		
		@Override
		public SocketChannel getChannel() {
			return ((SocketChannel)this.key.channel());
		}
		
		
		private void flush() {
			if (lock.tryLock()) {
				try {
					for (;;) {
						ByteBuffer buffer = this.outQ.peek();
						if (buffer == null) {
							return;
						}
						if (!this.write0(buffer)) {
							this.proc.flush(this);
							return;
						}
						this.outQ.poll();
					}
				} finally {
					lock.unlock();
				}
				
			} else {
				this.proc.flush(this);
			}
		}
		
		private boolean write0(ByteBuffer buffer) {
			try {
				this.getChannel().write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				this.getHandler().close(this);
				return true;
			}
			return !buffer.hasRemaining();
		}

	}
}
