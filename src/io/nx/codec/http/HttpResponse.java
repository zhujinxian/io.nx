package io.nx.codec.http;

public class HttpResponse extends DefaultHttpMessage {
	
	private HttpResponseStatus status;

	public HttpResponseStatus getStatus() {
		return status;
    }

    void setStatus(HttpResponseStatus status) {
    	this.status = status;
    }
}
