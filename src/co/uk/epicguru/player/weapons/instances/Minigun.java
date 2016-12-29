package co.uk.epicguru.player.weapons.instances;

import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.helpers.GunShell;
import co.uk.epicguru.helpers.SpriteProjecter;
import co.uk.epicguru.helpers.VirtualCoordinate;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.player.weapons.AnimatedInstance;
import co.uk.epicguru.player.weapons.GunDefinition;
import co.uk.epicguru.player.weapons.GunInstance;
import co.uk.epicguru.player.weapons.GunManager;

public class Minigun extends GunDefinition {

	@VirtualCoordinate("MAGENTA")
	public Vector2 shellSpawn;
	
	public Minigun() {
		super("Minigun");
	}

	public void shot(GunInstance instance, GunManager gunManager, Entity e) {
		AnimatedInstance animator = (AnimatedInstance) instance;
		animator.setFPS(60);
		animator.setAnimation("Fire");
		new GunShell(SpriteProjecter.unprojectPosition(gunManager.getSprite(), shellSpawn), gunManager.getFinalAngle(), 0.4f);
	}

	public GunInstance getInstance(GunManager gun, Entity e){

		AnimatedInstance animator = new AnimatedInstance(gun, e, Day100.gunsAtlas.findRegion("Minigun"));

		// Add animation(s)
		animator.addAnimation("Fire", 1);

		return animator;
	}

}
