package io.nx.codec.http;

import java.nio.ByteBuffer;

public class HttpUtil {
	
	public static String readLine(ByteBuffer buffer) {
		 StringBuilder sb = new StringBuilder(64);
	        while (buffer.hasRemaining()) {
	            byte nextByte = buffer.get();
	            if (nextByte == HttpConstants.CR) {
	                nextByte = buffer.get();
	                if (nextByte == HttpConstants.LF) {
	                    return sb.toString();
	                }
	            } else if (nextByte == HttpConstants.LF) {
	                return sb.toString();
	            } else {
	                sb.append((char) nextByte);
	            }
	        }
	        return null;
	}
}
