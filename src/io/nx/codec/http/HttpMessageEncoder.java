package io.nx.codec.http;

import java.nio.ByteBuffer;
import java.util.List;

import io.nx.api.ChannelHandlerContext;
import io.nx.api.Encoder;

public class HttpMessageEncoder implements Encoder<HttpMessage> {

	@Override
	public List<ByteBuffer> doEncode(ChannelHandlerContext ctx, HttpMessage data) {
		return null;
	}

	

}
