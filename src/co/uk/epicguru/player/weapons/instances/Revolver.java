package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.audio.Sound;

import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunManager;

public final class Revolver extends GunDefinition {

	public Revolver() {
		super("Revolver");
		shotSounds = new Sound[]{GunManager.getGunSound("Ak47.mp3")};
	}

}
