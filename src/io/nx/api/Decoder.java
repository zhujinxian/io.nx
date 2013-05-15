package io.nx.api;

public interface Decoder<T> {
	T doDecode(ChannelHandlerContext ctx);
}
