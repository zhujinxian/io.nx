package io.nx.codec.http;

import java.nio.ByteBuffer;


import io.nx.api.Decoder;

public class HttpMessageDecoder implements Decoder<HttpMessage> {
	
	
	private State state = State.READ_INITIAL;
	private boolean err = false;
	private HttpRequest req;
	
	private static enum State {
        READ_INITIAL,
        READ_HEADER,
        READ_FIXED_LENGTH_CONTENT,
    }
	

	@Override
	public HttpMessage doDecode(ByteBuffer buffer) {
		switch (this.state) {
		case READ_INITIAL:{
			String initLine = HttpUtil.readLine(buffer);
			this.req = creatHttpRequest(initLine);
			if (this.req == null) {
				this.err = true;
				return null;
			}
			setState(State.READ_HEADER);
		}
		case READ_HEADER: {
			boolean head = false;
			while (buffer.hasRemaining()) {
				String line =  HttpUtil.readLine(buffer);
				if (line.length() == 0) {
					head = true;
					break;
				}
				String[] kv = line.split(":", 2);
				if (line.length() == 1) {
					req.setHeader(kv[0], "");
				} else {
					req.setHeader(kv[0], kv[1]);
				}
			}
			if (head) {
				if (HttpHeaders.getContentLength(req, -1) >= 0) {
		            setState(State.READ_FIXED_LENGTH_CONTENT);
		        } else {
		           this.err = true;
		           return null;
		        }
			}
		}
		case READ_FIXED_LENGTH_CONTENT: {
			int len = (int)HttpHeaders.getContentLength(req);
			if (buffer.remaining() >= len) {
				this.req.setContent(ByteBuffer.wrap(buffer.array(),
						buffer.position()-1, len));
			} else {
				return null;
			}
		}
		}
		return req;
	}

	private HttpRequest creatHttpRequest(String initLine) {
		return null;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	

}
