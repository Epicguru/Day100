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

public class P90 extends GunDefinition {

	public static Sound reloadOut;
	public static Sound reloadIn;
	
	public P90() {
		super("P90");
	}

	@Override
	public void update(GunInstance instance, Entity e, GunManager gunManager, float delta) {
		if(Input.isKeyJustDown(Keys.R)){
			AnimatedInstance animator = (AnimatedInstance) instance;
			animator.setFPS(30);
			animator.setAnimation("Reload");
			animator.addCallback(() -> {
				// Play clip in sound.
				SoundUtils.playSound(e, reloadOut, 0.5f, 1, 50);
			}, 1);
			animator.addCallback(() -> {
				// Play clip out sound.
				SoundUtils.playSound(e, reloadIn, 0.5f, 1, 50);
			}, 20);	
		}			
	}

	@Override
	public GunInstance getInstance(GunManager gunManager, Entity entity) {
		AnimatedInstance animator = new AnimatedInstance(gunManager, entity, Day100.gunsAtlas.findRegion("P90"));
		
		// Add animations
		animator.addAnimation("Reload", 30);		
		
		return animator;
	}

}
