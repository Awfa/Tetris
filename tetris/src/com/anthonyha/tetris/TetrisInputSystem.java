package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class TetrisInputSystem extends AbstractMessageListener implements InputProcessor {

	private final Tetris game;
	private MessageSystem messageSystem;
	
	public TetrisInputSystem(final Tetris game) {
		this.game = game;
		messageSystem = game.messageSystem;
		
		messageSystem.add(this, Message.GAME_PAUSED);
		messageSystem.add(this, Message.GAME_RESUMED);
	}
	
	// Input Processing
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.A || keycode == Keys.LEFT) {
			messageSystem.postMessage(Message.LEFT, true);
			return true;
			
		} else if (keycode == Keys.D || keycode == Keys.RIGHT) {
			messageSystem.postMessage(Message.RIGHT, true);
			return true;
			
		} else if (keycode == Keys.S || keycode == Keys.DOWN) {
			messageSystem.postMessage(Message.SOFT_DROP, true);
			return true;
			
		} else if (keycode == Keys.Q || keycode == Keys.Z || keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			messageSystem.postMessage(Message.ROTATE_LEFT);
			return true;
			
		} else if (keycode == Keys.E || keycode == Keys.X || keycode == Keys.UP) {
			messageSystem.postMessage(Message.ROTATE_RIGHT);
			return true;
			
		} else if (keycode == Keys.W || keycode == Keys.SPACE) {
			messageSystem.postMessage(Message.HARD_DROP);
			return true;
			
		} else if (keycode == Keys.SHIFT_LEFT || keycode == Keys.C || keycode == Keys.SHIFT_RIGHT) {
			messageSystem.postMessage(Message.HOLD);
			return true;
			
		} else if (keycode == Keys.ESCAPE) {
			if (game.gameScreen.isPaused()) {
				messageSystem.postMessage(Message.UNPAUSE);
			} else {
				messageSystem.postMessage(Message.PAUSE);
			}
			return true;
			
		} else if (keycode == Keys.BACKSPACE) {
			if (Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setDisplayMode(1280, 720, false);
			} else {
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
			}
			return true;
			
		} else if (keycode == Keys.R){
			if (game.gameScreen.isPaused() && game.getScreen() == game.gameScreen) {
				messageSystem.postMessage(Message.RESTART_GAME);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.A || keycode == Keys.LEFT) {
			messageSystem.postMessage(Message.LEFT, false);
			return true;
			
		} else if (keycode == Keys.D || keycode == Keys.RIGHT) {
			messageSystem.postMessage(Message.RIGHT, false);
			return true;
			
		} else if (keycode == Keys.S || keycode == Keys.DOWN) {
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
