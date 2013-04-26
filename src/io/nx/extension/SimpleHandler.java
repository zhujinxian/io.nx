package io.nx.extension;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SimpleHandler extends AbstractHandler {

	@Override
	public void process(SelectionKey key, ByteBuffer buffer) {
		buffer.flip();
		String msg = new String(buffer.array(), 0, buffer.remaining());
//		System.out.println(msg);
	}

	@Override
	public void open(SelectionKey key) {
//		SocketChannel socket = (SocketChannel)key.channel();
//		System.out.println("open socket: " + socket.socket().toString());
	}

}
