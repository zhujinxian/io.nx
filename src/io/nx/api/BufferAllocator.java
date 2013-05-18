package io.nx.api;

import java.nio.ByteBuffer;

public interface BufferAllocator {
	ByteBuffer buffer(Object obj);

	ByteBuffer buffer(Object obj, int size);

	void setMaxBufferSize(int maxSize);

	void release(Object obj);
}
