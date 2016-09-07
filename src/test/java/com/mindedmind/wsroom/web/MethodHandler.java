package com.mindedmind.wsroom.web;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;

class MethodHandler extends SimpAnnotationMethodMessageHandler {

	public MethodHandler(SubscribableChannel inChannel, MessageChannel outChannel,
			SimpMessageSendingOperations brokerTemplate) {

		super(inChannel, outChannel, brokerTemplate);
	}

	public void registerHandler(Object handler) {
		super.detectHandlerMethods(handler);
	}
}