package io.nx.codec.http;

import java.nio.ByteBuffer;


public class HttpRequest extends DefaultHttpMessage {
	
	private String uri;
	private HttpMethod method;
	private ByteBuffer content;
	
	public HttpMethod getMethod() {
		return method;
		
	}
   
    public void setMethod(HttpMethod method) {
    	this.method = method;
    }
  
    public String getUri() {
		return uri;
    }
   
    public void setUri(String uri) {
    	this.uri = uri;
    }

	public ByteBuffer getContent() {
		return content;
	}

	public void setContent(ByteBuffer content) {
		this.content = content;
	}
}
