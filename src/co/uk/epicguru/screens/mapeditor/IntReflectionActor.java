package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

public final class IntReflectionActor extends ReflectionActor {

	public IntReflectionActor(String name, String type, Actor... actors) {
		super(name, type, actors);
	}

	@Override
	public void apply(Object gun) throws Exception{
		gun.getClass().getField(this.fieldName).set(gun, Integer.parseInt(((VisValidatableTextField)actors[0]).getText()));
	}

}
