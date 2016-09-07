package com.mindedmind.wsroom.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;

public class MessageChannel extends AbstractSubscribableChannel {

	private final List<Message<?>> messages = new ArrayList<>();


	public List<Message<?>> getMessages() {
		return this.messages;
	}

	@Override
	protected boolean sendInternal(Message<?> message, long timeout) {
		this.messages.add(message);
		return true;
	}
}