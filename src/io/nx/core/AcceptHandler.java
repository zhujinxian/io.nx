package io.nx.core;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;

public class AcceptHandler extends AbstractHandler {
	
	public AcceptHandler(ServerAcceptor acceptor, SelectableChannel channel) {
		this.acceptor = acceptor;
		this.channel = channel;
	}

	@Override
	public void execute() {
		try{
			SocketChannel socket = (SocketChannel) ((ServerSocketChannel) this.channel).accept();
			if(socket == null){
				return;
			}
			socket.configureBlocking(false);
			if(!socket.finishConnect()){
				socket.close();
				return;
			}
			this.acceptor.dispatch(socket);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void write(ByteBuffer buffer) {
		
	}

	@Override
	public void channelOpen() {
		
	}

	@Override
	public void Channelclosed() {
		
	}

}
