package io.nx.api;

import java.nio.ByteBuffer;

public interface Decoder<T> {
	T doDecode(ByteBuffer buffer);
}
