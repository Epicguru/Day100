package co.uk.epicguru.screens.mapeditor;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.player.weapons.GunDefinition;

public class SoundReflectionActor extends ReflectionActor {
	
	public SoundReflectionActor(String name, String type, Actor[] actors) {
		super(name, type, actors);
		// 0 = Text
	}

	@Override
	public void apply(GunDefinition gun) throws Exception {
		if(((VisLabel)actors[0]).getText().toString().equals("Null")){
			gun.getClass().getField(this.fieldName).set(gun, null);
			return;
		}
		
		gun.getClass().getField(this.fieldName).set(gun, Day100.assets.get(GunEditor.getSoundAssetFromEnding(((VisLabel)actors[0]).getText().toString()), Sound.class));
	}

}
