package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.InputAdapter;

import co.uk.epicguru.input.Input;
import co.uk.epicguru.screens.instances.MapEditor;
import co.uk.epicguru.screens.instances.MapEditor.State;

public class MapEditorInput extends InputAdapter {

	public float zoom = 1f;
	
	@Override
	public boolean scrolled(int amount) {
		
		if((Input.getMouseX() < MapEditor.objectsRenderX && MapEditor.state == State.CREATION) || (MapEditor.state == State.PHYSICS && VisUIHandler.changingImage)){
			MapEditor.objectsUIOffsetTarget -= amount * 64;
			VisUIHandler.stage.scrolled(amount);
			return false;
		}
		
		zoom *= amount == 1 ? 1.1f : 0.9f;
		VisUIHandler.stage.scrolled(amount);
		return false;
		
	}

	@Override
	public boolean keyDown(int keycode) {
		VisUIHandler.stage.keyDown(keycode);
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		VisUIHandler.stage.keyTyped(character);
		return super.keyTyped(character);
	}

	@Override
	public boolean keyUp(int keycode) {
		VisUIHandler.stage.keyUp(keycode);
		return super.keyUp(keycode);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		VisUIHandler.stage.mouseMoved(screenX, screenY);
		return super.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		VisUIHandler.stage.touchDown(screenX, screenY, pointer, button);
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		VisUIHandler.stage.touchDragged(screenX, screenY, pointer);
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		VisUIHandler.stage.touchUp(screenX, screenY, pointer, button);
		return super.touchUp(screenX, screenY, pointer, button);
	}

}
