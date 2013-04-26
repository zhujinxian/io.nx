package io.nx.example;

import io.nx.extension.SimpleHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class EchoHandler extends SimpleHandler {

	@Override
	public void process(SelectionKey key, ByteBuffer buffer) {
		super.process(key, buffer);
		ByteBuffer buff = ByteBuffer.wrap("HelloClient".getBytes());
		buff.position(buff.limit());
		this.write(key, buff);
	}
	
}

