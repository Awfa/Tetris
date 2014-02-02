package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Extra;
import com.anthonyha.tetris.MessageSystem.Message;

public abstract class AbstractMessageListener implements MessageListener {

	@Override
	public void recieveMessage(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recieveMessage(Message message, int extra) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recieveMessage(Message message, boolean extra) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void recieveMessage(MessageSystem.Message message, Extra extra) {
		// TODO Auto-generated method stub
		
	}

}
