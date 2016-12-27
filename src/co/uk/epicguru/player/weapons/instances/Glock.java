package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;

import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.player.weapons.AnimatedInstance;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunInstance;
import co.uk.epicguru.player.weapons.GunManager;
import co.uk.epicguru.sound.SoundUtils;

public final class Glock extends GunDefinition {
	
	public static Sound reloadOut;
	public static Sound reloadIn;
	
	public Glock() {
		super("Glock");
	}
	
	public void shot(GunInstance instance, GunManager gun, Entity shooter){
		AnimatedInstance animator = (AnimatedInstance) instance;
		
		// Set animation
		animator.setFPS(60);
		animator.setAnimation("Fire");
		
	}

	public void update(GunInstance instance, Entity e, GunManager gunManager, float delta) {
		if(Input.isKeyJustDown(Keys.R)){
			AnimatedInstance animator = (AnimatedInstance) instance;
			animator.setFPS(30);
			animator.setAnimation("Reload");
			animator.addCallback(() -> {
				// Play clip in sound.
				SoundUtils.playSound(e, reloadOut, 0.5f, 1.4f, 50);
			}, 5);
			animator.addCallback(() -> {
				// Play clip out sound.
				SoundUtils.playSound(e, reloadIn, 0.5f, 1.4f, 50);
			}, 17);	
		}			
	}

	public GunInstance getInstance(GunManager gun, Entity e){
		
		AnimatedInstance animator = new AnimatedInstance(gun, e, Day100.gunsAtlas.findRegion("Glock"));
		
		// Add animation(s)
		animator.addAnimation("Fire", 6);
		animator.addAnimation("Reload", 27);
		
		return animator;
	}
	
}
