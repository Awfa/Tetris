package com.anthonyha.tetris;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MessageSystem {
	
	public enum Message {
		SCORE_CHANGE, ROWS_SCORED, ROW_CLEARED, HARD_DROPPED, SOFT_DROPPED, SHIFTED, LOCKED_IN, LEVEL_UP, // Game events
		LEFT, RIGHT, ROTATE_LEFT, ROTATE_RIGHT, SOFT_DROP, HARD_DROP, HOLD, RESTART_GAME, PAUSE, UNPAUSE,// Command changes
		GAME_PAUSED, GAME_RESUMED, GAME_OVER // Game state updates
	}
	
	public enum Extra {
		SINGLE_SCORED, DOUBLE_SCORED, TRIPLE_SCORED, TETRIS_SCORED, BACKTOBACK_SCORED, TSPIN_SCORED
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
	
	public void postMessage(Message m, boolean extra) {
		Array<MessageListener> listeners = listenerMap.get(m);
		for (MessageListener listener : listeners) {
			listener.recieveMessage(m, extra);
		}
	}
	
	public void postMessage(Message m, Extra extra) {
		Array<MessageListener> listeners = listenerMap.get(m);
		for (MessageListener listener : listeners) {
			listener.recieveMessage(m, extra);
		}
	}
}
