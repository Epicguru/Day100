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

public final class Ak47 extends GunDefinition {

	@VirtualCoordinate("GREEN")
	public Vector2 shellSpawn;
	
	public Ak47() {
		super("AK-47");
	}
	
	public void shot(GunInstance instance, GunManager gunManager, Entity e) {
		AnimatedInstance animator = (AnimatedInstance) instance;
		
		animator.setFPS(60);
		animator.setAnimation("Fire");
		new GunShell(SpriteProjecter.unprojectPosition(gunManager.getSprite(), shellSpawn), gunManager.getFinalAngle(), 0.5f);
		
	}

	public GunInstance getInstance(GunManager gun, Entity e){
		
		AnimatedInstance animator = new AnimatedInstance(gun, e, Day100.gunsAtlas.findRegion("AK-47"));
		
		// Add animation(s)
		animator.addAnimation("Fire", 5);
		
		return animator;
	}

}
