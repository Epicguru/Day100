package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.helpers.VirtualCoordinate;
import co.uk.epicguru.helpers.GunShell;
import co.uk.epicguru.helpers.SpriteProjecter;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.player.weapons.AnimatedInstance;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunInstance;
import co.uk.epicguru.player.weapons.GunManager;
import co.uk.epicguru.sound.SoundUtils;

public class Scout extends GunDefinition {

	@VirtualCoordinate("GREEN")
	public static Vector2 shellSpawn;
	public static Sound bolt;
	public static Sound bolt1;
	public static Sound bolt2;
	public static Sound reloadOut;
	public static Sound reloadIn;
	
	public Scout() {
		super("Scout");		
	}
	
	public void shot(GunInstance instance, GunManager gun, Entity shooter){
		AnimatedInstance animator = (AnimatedInstance)instance;
		
		// Play animation
		animator.setFPS(14);
		animator.setAnimation("Bolt");
		animator.addCallback(() -> {
			new GunShell(SpriteProjecter.unprojectPosition(gun.getSprite(), shellSpawn), gun.getFinalAngle(), 0.8f);
		}, 5);
		
		// Play bolt sound
		SoundUtils.playSound(gun.player, bolt, 0.7f, 1, 20);
	}

	public void update(GunInstance instance, Entity e, GunManager gunManager, float delta) {
		AnimatedInstance animator = (AnimatedInstance)instance;
		
		if(Input.isKeyJustDown(Keys.R)){
			animator.setFPS(15);
			animator.setAnimation("Reload");	
			animator.addCallback(() -> {
				// Play clip in sound.
				SoundUtils.playSound(e, bolt1, 0.5f, 1.4f, 50);
			}, 1);
			animator.addCallback(() -> {
				// Play clip out sound.
				SoundUtils.playSound(e, bolt2, 0.5f, 1.4f, 50);
			}, 25);		
			animator.addCallback(() -> {
				// Play clip in sound.
				SoundUtils.playSound(e, reloadOut, 0.5f, 1.4f, 50);
			}, 7);
			animator.addCallback(() -> {
				// Play clip out sound.
				SoundUtils.playSound(e, reloadIn, 0.5f, 1.4f, 50);
			}, 21);	
		}
	}

	public GunInstance getInstance(GunManager gunManager, Entity entity) {
		AnimatedInstance animator = new AnimatedInstance(gunManager, entity, Day100.gunsAtlas.findRegion("Scout"));
		
		// Add bolt animation
		animator.addAnimation("Bolt", 12);
		animator.addAnimation("Reload", 30);
		
		return animator;
	}
}
