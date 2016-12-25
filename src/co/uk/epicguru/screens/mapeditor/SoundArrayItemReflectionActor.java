package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.player.weapons.GunDefinition;

public class SoundArrayItemReflectionActor extends ReflectionActor {
	
	public int index;
	public boolean removed = false;
	
	public SoundArrayItemReflectionActor(String name, String type, Actor[] actors, int index) {
		super(name, type, actors);
		// 0 = Text
		this.index = index;
	}

	@Override
	public void apply(GunDefinition gun) throws Exception {
		
		if(removed)
			return;
		
		if(((VisLabel)actors[0]).getText().toString().equals("Null")){
			Sound[] array = (Sound[])gun.getClass().getField(this.fieldName).get(gun);
			array[index] = null;
			return;
		}
		Sound[] array = (Sound[])gun.getClass().getField(this.fieldName).get(gun);
		array[index] = Day100.assets.get(GunEditor.getSoundAssetFromEnding(((VisLabel)actors[0]).getText().toString()), Sound.class);
	}

}
