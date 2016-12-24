package co.uk.epicguru.player.weapons;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;

import org.reflections.Reflections;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import box2dLight.PointLight;
import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.input.Input;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.DamageData;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.map.building.BuildingObject;
import co.uk.epicguru.particles.ParticleExplosion;

public final class GunManager {

	public static final String TAG = "Gun Manager";
	public static int colleateralCount = 0;
	public static ArrayList<GunDefinition> guns = new ArrayList<GunDefinition>();
	public static ArrayList<FlashFade> flashes = new ArrayList<FlashFade>();
	public static GunDefinition equipped;
	private static int index = 0;
	private static float timer = 0;
	private static boolean canShoot = false;
	private static PointLight flash;
	private static float angleOffset = 0;
	private static float angleVelocity = 0;
	private static boolean inBurst;
	private static int burstShotsFired = 0;
	private static boolean shoot = false;

	public static void reset() {
		guns.clear();

		Log.info(TAG, "Registering new guns...");

		// Reflection to register guns.
		final String path = "co.uk.epicguru.player.weapons.instances";
		Log.info(TAG, "Scanning for guns in " + path);
		Set<Class<? extends GunDefinition>> found = new Reflections(path).getSubTypesOf(GunDefinition.class);
		for(Class<? extends GunDefinition> gun : found){
			for(int i = 0; i < gun.getConstructors().length; i++){
				try {
					GunDefinition newGun = (GunDefinition) gun.getConstructors()[i].newInstance();
					// Add to list
					guns.add(newGun);
					Log.info(TAG, "Found gun '" + newGun.name + "' of class '" + gun.getSimpleName() + "' using constructor #" + i);
					break; // Because we found an adequate constructor.
				} catch (Exception e){
					if(i != gun.getConstructors().length - 1)
						return;
					Log.error(TAG, "ERROR LOADING GUN '" + gun.getSimpleName() + "'. Constructor with 0 arguments failed.");
					Log.error(TAG, "Ensure that the class has at least one constructor with 0 args.");
					Log.error(TAG, "The stack trace for the LAST constructor tried follows:");
					Log.error(TAG, "Error in constructor call for gun class '" + gun.getName() + "'", e);
				}
			}
			
		}

		// Load gun data from file - TODO use internal path in cache or similar
		// to load from internal
		// to avoid changing gun data as a user.
		JLineReader reader = new JLineReader(new File(Constants.DAY_100_FOLDER + "Gun Data\\Data TEMP.txt"));
		try {
			reader.open();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		do {
			final String ERROR = "ERROR_WTF_EMPTY?";
			String name = reader.readString("name", ERROR);
			if (name.equals(ERROR)) {
				continue;
			}

			// Find that gun and load it.
			Log.info(TAG, "Loading data for '" + name + "' ...");
			GunManager.find(name).load(reader);

		} while (!reader.nextLine());

		reader.dispose();

		index = 0;
		equipped = guns.get(0);
		angleOffset = 0;
		angleVelocity = 0;
		flash = null;
		inBurst = false;
		burstShotsFired = 0;
		shoot = false;
		FlashFade.clearAll();
	}

	/**
	 * Finds a gun by name.
	 * 
	 * @param name
	 *            The name of the gun
	 * @return The gun or null if not found in the array.
	 */
	public static GunDefinition find(final String name) {
		for (GunDefinition gun : guns) {
			if (gun.name.equals(name))
				return gun;
		}
		return null;
	}

	/**
	 * Gets the next gun definition in the loaded guns. Loops round if at end of
	 * list.
	 * 
	 * @return The GunDefinition.
	 */
	public static GunDefinition next() {
		if (index == guns.size() - 1)
			return guns.get(index = 0);
		return guns.get(++index);
	}

	/**
	 * Gets the previous gun definition in the loaded guns. Loops round if at
	 * beginning of list.
	 * 
	 * @return The GunDefinition.
	 */
	public static GunDefinition previous() {
		if (index == 0)
			return guns.get(index = guns.size() - 1);
		return guns.get(--index);
	}

	/**
	 * Gets the next firing mode for the equipped weapon.
	 */
	public static void nextFiringMode(){
		if(equipped == null || inBurst)
			return;
		
		equipped.firingModeSelected++;
		equipped.firingModeSelected %= equipped.firingModes.length;
	}
	
	/**
	 * Call to indicate a change in weapon.
	 */
	public static void gunChanged() {
		timer = 0;
		canShoot = false;
		angleOffset = -40;
		angleVelocity = 0;
		inBurst = false;
		burstShotsFired = 0;
		shoot = false;
	}

	/**
	 * If any textures have been flipped, this will fix them.
	 */
	public static void cleanTextures() {
		for (GunDefinition gun : guns) {
			TextureRegion t = gun.texture;
			if (t.isFlipX())
				t.flip(true, false);
			if (t.isFlipY())
				t.flip(false, true);
		}
	}

	public static void update(float delta) {
		if (equipped == null)
			return;

		timer += delta;

		float timeScale = Day100.timeScale;
		
		angleOffset += angleVelocity * timeScale;
		float angleAdd = -angleOffset * (1f - equipped.recovery);
		angleAdd *= timeScale;
		angleOffset += angleAdd;
		float angleVelAdd = -angleVelocity * (1f - equipped.recovery);
		angleVelAdd *= timeScale;
		angleVelocity += angleVelAdd;

		// Deactivate flash
		if (flash != null) {
			flash.setActive(false);
			flash.remove();
		}
		flash = null;

		switch (equipped.firingModes[equipped.firingModeSelected]) {
		case BURST:

			canShoot = timer >= equipped.shotInterval && !inBurst;
			if (Input.clickLeft() && canShoot) {
				inBurst = true;
				// Shoot
				shoot = true;
				timer = 0;
				burstShotsFired = 1;
			}
			if (inBurst && timer != 0) {
				if (timer >= equipped.shotBurstInterval) {
					timer -= equipped.shotBurstInterval;
					// Shoot
					shoot = true;
					burstShotsFired++;
					if(burstShotsFired >= equipped.burstBullets){
						burstShotsFired = 0;
						timer = 0;
						inBurst = false;
					}
				}
			}

			break;
		case FULL:
			canShoot = timer >= equipped.shotInterval;
			if (Input.clickingLeft() && canShoot) {
				timer = 0;
				// Shoot
				shoot = true;
			}
			break;
		case SEMI:
			canShoot = timer >= equipped.shotInterval;
			if (Input.clickLeft() && canShoot) {
				timer = 0;
				// Shoot
				shoot = true;
			}
			break;

		}

		FlashFade.updateAll(delta);
	}

	public static void shootEquiped(float angle, float[] bulletSpawn) {
		if (equipped == null)
			return;

		// Add recoil
		angleVelocity = equipped.recoil;

		// Play sound.
		equipped.shotSounds[MathUtils.random(equipped.shotSounds.length - 1)].play(
				MathUtils.random(equipped.minVolume, equipped.maxVolume),
				MathUtils.random(equipped.minPitch, equipped.maxPitch) * Day100.timeScale, MathUtils.random(-0.1f, 0.1f));

		// TESTING

		//Day100.map.blood.add(new MapBlood(Input.getMouseWorldPos(), angle));

		// Flash
		flash = new PointLight(Day100.map.rayHandler, Constants.RAYS, new Color(1, 1, 0, 0.4f), 10f,
				Day100.player.body.getPosition().x, Day100.player.body.getPosition().y);
		flash.setActive(true);
		
		// Visual flash
		flashes.add(new FlashFade(new Vector2(bulletSpawn[0], bulletSpawn[1]),  angle, equipped.range));
		
		// RAY
		Vector2 end = new Vector2();
		float MAX = 200; // If range is very high, this will limit. It is the end point of the ray.
		end.x = MathUtils.cosDeg(angle) * MAX + bulletSpawn[0];
		end.y = MathUtils.sinDeg(angle) * MAX + bulletSpawn[1];
		colleateralCount = 0;
		Day100.map.world.rayCast(new RayCastCallback() {
			
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				//DevCross.drawCentred(point.x, point.y);
				Object user = fixture.getBody().getUserData();
				if(user != null && user instanceof Entity){
					if(point.dst(bulletSpawn[0], bulletSpawn[1]) <= equipped.range){
						// HIT AN ENTITY
						Entity e = ((Entity)user);
						e.takeDamage(equipped.damage, new DamageData(Day100.player, angle, new Vector2(Day100.player.getBody().getPosition())));
						if(e instanceof BuildingObject){
							BuildingObject object = (BuildingObject)e;
							int particleSize = 5; // In pixels
							int x = object.texture.getRegionX();
							int y = object.texture.getRegionY();
							int width = object.texture.getRegionWidth();
							int height = object.texture.getRegionHeight();
							object.texture.setRegion(MathUtils.random(x, x + width), MathUtils.random(y, y + height), particleSize, particleSize);
							new ParticleExplosion(point, angle + 180, angle + 180 + MathUtils.random(-50, 50), 5, 12, 2, 10, 1, 3, new TextureRegion(object.texture), MathUtils.random(987123897123L));
							object.texture.setRegion(x, y, width, height);
						}
						
					}
				}
				return colleateralCount++ + 1 < GunManager.equipped.collaterals ? 1 : 0;
			}
		}, new Vector2(bulletSpawn[0], bulletSpawn[1]), end);
		
	}

	/**
	 * Is the gun to the right side of the player?
	 */
	public static boolean toRight() {
		return Input.getMouseWorldX() >= Day100.player.body.getPosition().x;
	}
	
	private static float getAngle(){
		if(equipped == null)
			return 0;
		float originX = equipped.texture.getRegionWidth() / Constants.PPM * equipped.holdPoint.x / 2;
		float originY = equipped.texture.getRegionHeight() / Constants.PPM * equipped.holdPoint.y / 2;
		Vector2 playerPosition = Day100.player.body.getPosition();
		float angle = MathUtils.atan2(Input.getMouseWorldY() - (playerPosition.y - originX), Input.getMouseWorldX() - (playerPosition.x - originY)) * MathUtils.radiansToDegrees;
		return angle;
	}

	private static Sprite gun = new Sprite();
	public static void render(Batch batch) {
		if (equipped == null)
			return;
		
		Vector2 playerPosition = Day100.player.body.getPosition();
		float offset = (toRight()) ? equipped.distanceFromPlayer : -equipped.distanceFromPlayer;
		
		// RENDER WEAPON
		gun.setRegion(equipped.texture);
		gun.setSize(equipped.texture.getRegionWidth() / Constants.PPM / 2, equipped.texture.getRegionHeight() / Constants.PPM / 2);
		gun.setFlip(false, (!toRight()));
		float originX = equipped.texture.getRegionWidth() / Constants.PPM * equipped.holdPoint.x / 2;
		float originY = equipped.texture.getRegionHeight() / Constants.PPM * equipped.holdPoint.y / 2;
		gun.setOrigin(originX, originY);
		gun.setPosition(playerPosition.x + offset - originX, playerPosition.y - originY);
		gun.setRotation(getAngle() + angleOffset * (toRight() ? 1 : -1));
		gun.draw(batch);

		// FLASH
		FlashFade.renderAll(batch);
		
		
		if(!shoot)
			return;
		
		// TIP
		float[] verts = gun.getVertices();
		float[] topRight = new float[2];
		float[] bottomRight = new float[2];
		float[] topLeft = new float[2];
		float[] bottomLeft = new float[2];
		float[] interpolationLeft = new float[2];
		float[] interpolationRight = new float[2];
		float[] interpolationFinal = new float[2];
		
		Interpolation interpolation = Interpolation.linear;
		
		if(toRight()){
			topRight[0] = verts[SpriteBatch.X3];
			topRight[1] = verts[SpriteBatch.Y3];
			bottomRight[0] = verts[SpriteBatch.X4];
			bottomRight[1] = verts[SpriteBatch.Y4];
			
			topLeft[0] = verts[SpriteBatch.X2];
			topLeft[1] = verts[SpriteBatch.Y2];	
			bottomLeft[0] = verts[SpriteBatch.X1];
			bottomLeft[1] = verts[SpriteBatch.Y1];	
		}else{
			topRight[0] = verts[SpriteBatch.X4];
			topRight[1] = verts[SpriteBatch.Y4];
			bottomRight[0] = verts[SpriteBatch.X3];
			bottomRight[1] = verts[SpriteBatch.Y3];
			
			topLeft[0] = verts[SpriteBatch.X1];
			topLeft[1] = verts[SpriteBatch.Y1];	
			bottomLeft[0] = verts[SpriteBatch.X2];
			bottomLeft[1] = verts[SpriteBatch.Y2];	
		}
		
		interpolationLeft[0] = interpolation.apply(topLeft[0], bottomLeft[0], 1 - equipped.bulletSpawn.y);
		interpolationLeft[1] = interpolation.apply(topLeft[1], bottomLeft[1], 1 - equipped.bulletSpawn.y);
		interpolationRight[0] = interpolation.apply(topRight[0], bottomRight[0], 1 - equipped.bulletSpawn.y);
		interpolationRight[1] = interpolation.apply(topRight[1], bottomRight[1], 1 - equipped.bulletSpawn.y);
		interpolationFinal[0] = interpolation.apply(interpolationRight[0], interpolationLeft[0], 1 - equipped.bulletSpawn.x);
		interpolationFinal[1] = interpolation.apply(interpolationRight[1], interpolationLeft[1], 1 - equipped.bulletSpawn.x);
		
		// DEBUG
//		DevCross.drawCentred(topRight[0], topRight[1], 0.3f);			
//		DevCross.drawCentred(bottomLeft[0], bottomLeft[1], 0.3f);				
//		DevCross.drawCentred(bottomRight[0], bottomRight[1], 0.3f);				
//		DevCross.drawCentred(topLeft[0], topLeft[1], 0.3f);				
//		DevCross.drawCentred(interpolationLeft[0], interpolationLeft[1], 0.3f);				
//		DevCross.drawCentred(interpolationRight[0], interpolationRight[1], 0.3f);				
//		DevCross.drawCentred(interpolationFinal[0], interpolationFinal[1], 0.3f);
		
		// Fire!
		shootEquiped(getAngle() + angleOffset * (toRight() ? 1 : -1), interpolationFinal);
		
		shoot = false;
	}

	public static void renderUI(Batch batch){
		if(equipped == null)
			return;
		if(equipped.texture.isFlipY())
			equipped.texture.flip(false, true);
		batch.draw(equipped.texture, 5, 5);
		Day100.smallFont.setColor(1, 1, 1, 1);
		Day100.smallFont.draw(batch, "Firing mode : " + equipped.firingModes[equipped.firingModeSelected].toString().toLowerCase(), equipped.texture.getRegionWidth(), 30);
	}
	
	public static Sound getGunSound(final String name) {
		return Day100.assets.get("Audio/SFX/Guns/" + name);
	}

}
