package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisCheckBox;

import co.uk.epicguru.player.weapons.FiringMode;

public final class FiringModeReflectionActor extends ReflectionActor {
		
	public FiringModeReflectionActor(String name, String type, Actor... actors) {
		super(name, type, actors);
	}

	@Override
	public void apply(Object gun) throws Exception{
		int amount = 0;
		for(Actor actor : this.actors){
			VisCheckBox box = (VisCheckBox)actor;
			if(box.isChecked())
				amount++;
		}
		FiringMode[] array = new FiringMode[amount];
		
		int index = 0;
		for(Actor actor : this.actors){
			VisCheckBox box = (VisCheckBox)actor;
			if(box.isChecked()){
				array[index] = FiringMode.valueOf(box.getText().toString());
				index++;
			}
		}
		gun.getClass().getField(this.fieldName).set(gun, array);
	}

}
