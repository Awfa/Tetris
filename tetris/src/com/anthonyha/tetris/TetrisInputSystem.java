package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class TetrisInputSystem implements InputProcessor {

	private MessageSystem messageSystem;
	
	public TetrisInputSystem(MessageSystem m) {
		messageSystem = m;
	}
	
	// Input Processing
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.A) {
			messageSystem.postMessage(Message.LEFT, true);
			return true;
			
		} else if (keycode == Keys.D) {
			messageSystem.postMessage(Message.RIGHT, true);
			return true;
			
		} else if (keycode == Keys.S) {
			messageSystem.postMessage(Message.SOFT_DROP, true);
			return true;
			
		} else if (keycode == Keys.Q) {
			messageSystem.postMessage(Message.ROTATE_LEFT);
			return true;
			
		} else if (keycode == Keys.E) {
			messageSystem.postMessage(Message.ROTATE_RIGHT);
			return true;
			
		} else if (keycode == Keys.W) {
			messageSystem.postMessage(Message.HARD_DROP);
			return true;
			
		} else if (keycode == Keys.SHIFT_LEFT) {
			messageSystem.postMessage(Message.HOLD);
			return true;
			
		} else if (keycode == Keys.ESCAPE) {
			Gdx.graphics.setDisplayMode(1366, 768, false);
			return true;
			
		} else if (keycode == Keys.BACKSPACE) {
			Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
			return true;
			
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.A) {
			messageSystem.postMessage(Message.LEFT, false);
			return true;
			
		} else if (keycode == Keys.D) {
			messageSystem.postMessage(Message.RIGHT, false);
			return true;
			
		} else if (keycode == Keys.S) {
			messageSystem.postMessage(Message.SOFT_DROP, false);
			return true;
			
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
