package io.nx.extention;

import io.nx.api.ChannelHandlerContext;
import io.nx.core.AbstractChannelHandler;

public class SimpleChannelHandler extends AbstractChannelHandler {

	@Override
	public void open(ChannelHandlerContext ctx) {
		System.out.println(ctx.getChannel().socket());
	}

	@Override
	public void read(ChannelHandlerContext ctx) {
		super.read(ctx);
		ctx.getBuffer().flip();
//		System.out.println(new String(ctx.getBuffer().array(), 0, ctx.getBuffer().remaining()));
	}
}
