package io.nx.api;

import java.nio.ByteBuffer;

public interface Decoder {
	Object doDecode(ByteBuffer buffer);
}
