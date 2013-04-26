package io.nx.core;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public abstract class AbstractChannelHandler implements ChannelHandler {
	
	private ByteBuffer inputBuffer = ByteBuffer.allocate(4096);
	
	private BlockingQueue<ByteBuffer> outQ = new LinkedBlockingQueue<ByteBuffer>();
	private Reactor reactor;
	
	private Lock lock = new ReentrantLock();
	
	public abstract void process(SelectionKey key, ByteBuffer buffer);
	
	public final void setReactor(Reactor reactor) {
		this.reactor = reactor;
	}

	@Override
	public void open(SelectionKey key) {
		SocketChannel socket = (SocketChannel)key.channel();
		System.out.println("open socket: " + socket.socket().toString());		
	}

	@Override
	public void close(SelectionKey key) {
		key.cancel();
		try {
			key.channel().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public final void read(SelectionKey key) {
		try {
			int count = ((SocketChannel)key.channel()).read(this.inputBuffer);
			if (count == -1) {
				this.close(key);
			} else {
				this.process(key, inputBuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.close(key);
		}
	}
	
	private void writeOut(SelectionKey key) {
		if (lock.tryLock()) {
			try {
				for (;;) {
					ByteBuffer buffer = this.outQ.peek();
					if (buffer == null) {
						return;
					}
					if (!this.write0(key, buffer)) {
						this.reactor.flush(key, this);
						return;
					}
					this.outQ.poll();
				}
			} finally {
				lock.unlock();
			}
			
		} else {
			this.reactor.flush(key, this);
		}
		
	}

	private boolean write0(SelectionKey key, ByteBuffer buffer) {
		try {
			((SocketChannel)key.channel()).write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			this.close(key);
		}
		return !buffer.hasRemaining();
	}
	
	@Override
	public final void write(SelectionKey key, ByteBuffer buffer) {
		if (buffer != null) {
			buffer.flip();
			this.outQ.add(buffer);
		}
		writeOut(key);
	}

}
