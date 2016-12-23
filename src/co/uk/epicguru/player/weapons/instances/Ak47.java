package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.audio.Sound;

import co.uk.epicguru.player.weapons.FiringMode;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunManager;

public final class Ak47 extends GunDefinition {

	public Ak47() {
		super("AK-47");
		firingModes[0] = FiringMode.FULL;
		shotSounds = new Sound[]{GunManager.getGunSound("Ak47.mp3")};
	}

}
