package io.nx.codec.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DefaultHttpMessage implements HttpMessage {
	private Map<String, String> headers = new HashMap<String, String>();
	private HttpVersion version;

	@Override
	public String getHeader(String name) {
		return this.headers.get(name);
	}

	@Override
	public Set<Entry<String, String>> getHeaders() {
		return this.headers.entrySet();
	}

	@Override
	public boolean containsHeader(String name) {
		return this.headers.containsKey(name);
	}

	@Override
	public HttpVersion getProtocolVersion() {
		return this.version;
	}

	@Override
	public void setProtocolVersion(HttpVersion version) {
		this.version = version;

	}

	@Override
	public void setHeader(String name, String value) {
		this.headers.put(name, value);
	}

	@Override
	public void removeHeader(String name) {
		this.headers.remove(name);
	}

	@Override
	public void clearHeaders() {
		this.headers.clear();
	}
}
