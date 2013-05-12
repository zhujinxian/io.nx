package io.nx.buffer;

import io.nx.api.BufferAllocator;
import io.nx.api.BufferAllocatorFactory;

public class DefaultBufferAllocatorFactory implements BufferAllocatorFactory {
	private int defaultBuffSzie;
	
	public DefaultBufferAllocatorFactory(int defaultBuffSzie) {
		this.defaultBuffSzie = defaultBuffSzie;
	}

	@Override
	public BufferAllocator getBufferAllocator() {
		return new DefaultBufferAllocator(this.defaultBuffSzie);
	}

}
