package io.nx.api;

import java.nio.ByteBuffer;
import java.util.List;

public interface Encoder {
	List<ByteBuffer> doEncode(Object data);
}
