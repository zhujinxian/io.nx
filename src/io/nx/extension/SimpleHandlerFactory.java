package io.nx.extension;

import io.nx.api.Handler;
import io.nx.api.HandlerFactory;

public class SimpleHandlerFactory implements HandlerFactory {

	@Override
	public Handler getHandler() {
		return new SimpleHandler();
	}

}
