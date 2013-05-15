package io.nx.codec.http;

import java.util.Map;
import java.util.Set;


public interface HttpMessage {
	String getHeader(String name);
	Set<Map.Entry<String, String>> getHeaders();
	boolean containsHeader(String name);
	HttpVersion getProtocolVersion();
	void setProtocolVersion(HttpVersion version);
	void setHeader(String name, String value);
	void removeHeader(String name);
	void clearHeaders();

}
