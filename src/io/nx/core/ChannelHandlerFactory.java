package io.nx.core;

import io.nx.api.AbstractHandler;

public class ChannelHandlerFactory {
	private Class<? extends AbstractHandler> clazz;
	public ChannelHandlerFactory(Class<? extends AbstractHandler> className) {
		this.clazz = className;
	}
	
	public AbstractHandler getHandler() {
		try {
			return this.clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
