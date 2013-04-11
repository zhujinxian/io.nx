package io.nx.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class IoHandler extends AbstractHandler {
	
	private ConcurrentLinkedQueue<ByteBuffer> outQueue = new ConcurrentLinkedQueue<ByteBuffer>();
	
	private Lock lock = new ReentrantLock();
	
	protected ByteBuffer inputBuffer;
	
	public IoHandler() {
		this.inputBuffer = this.acceptor.getBuffer();
	}
	

	@Override
	public void execute() {
		SocketChannel ch = (SocketChannel)this.channel;
		try {
			ch.read(inputBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void write(ByteBuffer buffer) {
		buffer.flip();
		if (buffer != null) {
			this.outQueue.add(buffer);
		}
		if (lock.tryLock()) {
			for (;;) {
				buffer = this.outQueue.peek();
				if (buffer == null) {
					return;
				}
				writeToChannel(buffer);
				if (buffer.hasRemaining()) {
					break;
				}
				this.outQueue.poll();
			}
		}
		this.processor.flush(this);
	}
	
	private void writeToChannel(ByteBuffer buffer) {
		try {
			((SocketChannel)this.channel).write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
