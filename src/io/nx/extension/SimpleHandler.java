package io.nx.extension;

import io.nx.api.AbstractHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class SimpleHandler extends AbstractHandler {

	@Override
	public void process(SelectionKey key, ByteBuffer buffer) {
		buffer.flip();
		String msg = new String(buffer.array(), 0, buffer.remaining());
//		System.out.println(msg);
	}

	@Override
	public void open(SelectionKey key) {
	}

}
