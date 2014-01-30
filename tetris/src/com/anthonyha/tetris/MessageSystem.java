package com.anthonyha.tetris;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MessageSystem {
	
	public enum Message {
		ROW_CLEARED, HARD_DROPPED, SOFT_DROPPED, LOCKED_IN, LEVEL_UP, GAME_OVER
	}
	
	private ObjectMap<Message, Array<MessageListener>> listenerMap;
	
	public MessageSystem() {
		listenerMap = new ObjectMap<Message, Array<MessageListener>>();
		for (int i = 0; i < Message.values().length; ++i) {
			listenerMap.put(Message.values()[i], new Array<MessageListener>());
		}
	}
	
	public void add(MessageListener listener, Message m) {
		Array<MessageListener> listeners = listenerMap.get(m);
		listeners.add(listener);
	}
	
	public void remove(MessageListener listener, Message m) {
		Array<MessageListener> listeners = listenerMap.get(m);
		listeners.removeValue(listener, true);
	}
	
	public void postMessage(Message m) {
		Array<MessageListener> listeners = listenerMap.get(m);
		for (MessageListener listener : listeners) {
			listener.recieveMessage(m);
		}
	}
	
	public void postMessage(Message m, int extra) {
		Array<MessageListener> listeners = listenerMap.get(m);
		for (MessageListener listener : listeners) {
			listener.recieveMessage(m, extra);
		}
	}
	
}
