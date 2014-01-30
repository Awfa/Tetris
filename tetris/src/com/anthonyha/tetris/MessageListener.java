package com.anthonyha.tetris;

public interface MessageListener {

	public void recieveMessage(MessageSystem.Message message);
	public void recieveMessage(MessageSystem.Message message, int extra);
	
}
