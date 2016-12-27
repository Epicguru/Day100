package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.audio.Sound;

import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.player.weapons.AnimatedInstance;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunInstance;
import co.uk.epicguru.player.weapons.GunManager;
import co.uk.epicguru.sound.SoundUtils;

public class Scout extends GunDefinition {

	public static Sound bolt = GunManager.getGunSound("ScoutBolt.mp3");
	
	public Scout() {
		super("Scout");		
	}
	
	public void shot(GunInstance instance, GunManager gun, Entity shooter){
		AnimatedInstance animator = (AnimatedInstance)instance;
		
		// Play animation
		animator.setAnimation("Bolt");
		
		// Play bolt sound
		SoundUtils.playSound(gun.player, bolt, 0.7f, 1, 20);
	}

	public GunInstance getInstance(GunManager gunManager, Entity entity) {
		AnimatedInstance animator = new AnimatedInstance(gunManager, entity, Day100.gunsAtlas.findRegion("Scout"));
		
		// Add bolt animation
		animator.addAnimation("Bolt", 12);
		
		// Set FPS
		animator.setFPS(14);
		
		return animator;
	}
}
