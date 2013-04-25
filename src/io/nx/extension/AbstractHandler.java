package io.nx.extension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import io.nx.api.Handler;


public abstract class AbstractHandler implements Handler {
	
	private ByteBuffer inputBuffer = ByteBuffer.allocate(4096);
	
	
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

	@Override
	public boolean write(SelectionKey key, ByteBuffer buffer) {
		try {
			buffer.flip();
			int num = ((SocketChannel)key.channel()).write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			this.close(key);
		}
		return !buffer.hasRemaining();
	}

}
