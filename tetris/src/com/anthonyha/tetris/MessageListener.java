package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Extra;

public interface MessageListener {

	public void recieveMessage(MessageSystem.Message message);
	public void recieveMessage(MessageSystem.Message message, int extra);
	public void recieveMessage(MessageSystem.Message message, boolean extra);
	public void recieveMessage(MessageSystem.Message message, Extra extra);
	
	
}
