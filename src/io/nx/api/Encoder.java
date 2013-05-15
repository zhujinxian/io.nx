package io.nx.api;

import java.nio.ByteBuffer;
import java.util.List;

public interface Encoder<T> {
	List<ByteBuffer> doEncode(ChannelHandlerContext ctx, T data);
}
