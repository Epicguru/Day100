package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import co.uk.epicguru.player.weapons.GunDefinition;

public final class VectorReflectionActor extends ReflectionActor {

	public VectorReflectionActor(String name, String type, Actor... actors) {
		super(name, type, actors);
	}

	private Vector2 vector = new Vector2();
	
	@Override
	public void apply(GunDefinition gun) throws Exception{
		
		vector.set(Float.parseFloat(((VisValidatableTextField)actors[0]).getText()), Float.parseFloat(((VisValidatableTextField)actors[1]).getText()));
		
		gun.getClass().getField(this.fieldName).set(gun, vector);
	}

}
