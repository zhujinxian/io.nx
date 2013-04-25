package io.nx.extension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.nx.api.Handler;
import io.nx.core.Processor;


public abstract class AbstractHandler implements Handler {
	
	private ByteBuffer inputBuffer = ByteBuffer.allocate(4096);
	
	private BlockingQueue<ByteBuffer> outQ = new LinkedBlockingQueue<ByteBuffer>();
	
	private Lock lock = new ReentrantLock();
	
	public abstract void process(SelectionKey key, ByteBuffer buffer);

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
	public void read(SelectionKey key) {
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
		for (;;) {
			ByteBuffer buffer = this.outQ.peek();
			if (buffer == null) {
				notifyProcessor(key, SelectionKey.OP_READ);
				break;
			}
			if (!this.write0(key, buffer)) {
				break;
			}
			this.outQ.poll();
		}
	}
	
	private void notifyProcessor(SelectionKey key, int ops) {
		try {
			key.interestOps(ops);
			key.selector().wakeup();
		} catch (Exception e) {
			e.printStackTrace();
			this.close(key);
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
	public void write(SelectionKey key, ByteBuffer buffer) {
		if (buffer != null) {
			buffer.flip();
			this.outQ.add(buffer);
			try {
				if (lock.tryLock()) {
					notifyProcessor(key, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				}
			} finally {
				lock.unlock();
			}
		} else {
			writeOut(key);
		}
	}

}
