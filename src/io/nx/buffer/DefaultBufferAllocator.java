package io.nx.buffer;

import io.nx.api.BufferAllocator;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultBufferAllocator implements BufferAllocator {
	private Map<Object, ByteBuffer> buffMap = new HashMap<Object, ByteBuffer>();
	private List<ByteBuffer> buffList = new ArrayList<ByteBuffer>();

	private int defaultSize = 256;
	private int maxSize = 64 * 1024;

	public DefaultBufferAllocator(int defaultBuffSzie) {
		this.defaultSize = defaultBuffSzie;
	}

	@Override
	public ByteBuffer buffer(Object obj) {
		ByteBuffer buff = this.buffMap.get(obj);
		if (buff == null) {
			buff = findFreeBuff();
		}
		this.buffMap.put(obj, buff);
		return buff;
	}

	@Override
	public void release(Object obj) {
		ByteBuffer buff = this.buffMap.remove(obj);
		if (buff != null) {
			buff.clear();
			this.buffList.add(buff);
		}
	}

	private ByteBuffer findFreeBuff() {
		if (this.buffList.isEmpty()) {
			produceOneBuffer();
		}
		return this.buffList.remove(0);

	}

	private ByteBuffer findFreeBuff(int size) {
		for (int i = 0, num = this.buffList.size(); i < num; i++) {
			if (this.buffList.get(i).capacity() >= size) {
				return this.buffList.remove(i);
			}
		}
		produceOneBuffer(size);
		return this.buffList.remove(this.buffList.size() - 1);

	}

	private void produceOneBuffer() {
		this.buffList.add(ByteBuffer.allocate(defaultSize));
	}

	private void produceOneBuffer(int size) {
		this.buffList.add(ByteBuffer.allocate(size));
	}

	@Override
	public ByteBuffer buffer(Object obj, int size) {
		if (size > this.maxSize) {
			size = this.maxSize;
		}
		ByteBuffer oldBuff = this.buffMap.get(obj);
		ByteBuffer newBuff = findFreeBuff(size);
		oldBuff.flip();
		newBuff.put(oldBuff);
		this.release(obj);
		this.buffMap.put(obj, newBuff);
		return newBuff;
	}

	@Override
	public void setMaxBufferSize(int maxSize) {
		this.maxSize = maxSize;
	}

}
