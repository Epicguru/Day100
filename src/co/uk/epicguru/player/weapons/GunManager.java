package co.uk.epicguru.player.weapons;

import java.util.ArrayList;
import java.util.Set;

import org.reflections.Reflections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.helpers.AlphabeticalHelper;
import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.DamageData;
import co.uk.epicguru.map.Entity;
import co.uk.epicguru.sound.SoundUtils;

public final class GunManager {

	public static GunManager flashInstance;
	public static final String TAG = "Gun Manager";
	public int colleateralCount = 0;
	public static ArrayList<GunDefinition> guns = new ArrayList<GunDefinition>();
	/**
	 * NULL WARNING! CHECK
	 */
	private GunDefinition equipped;
	private GunInstance equippedInstance;
	private int index = 0;
	private float timer = 0;
	private boolean canShoot = false;
	private PointLight flash;
	private ConeLight flashCone;
	private float angleOffset = 0;
	private float angleVelocity = 0;
	private boolean inBurst;
	private int burstShotsFired = 0;
	private boolean shoot = false;
	public Entity player;
	public Vector2 crosshair = new Vector2();
	private boolean requestingShoot = true;

	public GunManager(Entity player){
		this.player = player;
	}
	
	/**
	 * Gets the recoil angle offset.
	 * @return The current recoil angle offset.
	 */
	public float getOffset(){
		return angleOffset;
	}
	
	/**
	 * Gets the direction the gun is facing in.
	 * @return
	 */
	public float getBaseAngle(){
		return getAngle();
	}
	
	/**
	 * Gets the final angle that the gun is rendered at.
	 * @return
	 */
	public float getFinalAngle(){
		return getBaseAngle() + getOffset();
	}
	
	/**
	 * Gets the equipped weapon. May be null.
	 */
	public GunDefinition getEquipped(){
		return equipped;
	}
	
	/**
	 * Gets the equipped weapon's instance. May be null if weapon is null/
	 */
	public GunInstance getEquippedInstance(){
		return equippedInstance;
	}
	
	/**
	 * Sets the equipped weapon to the gun specified and returns new new gun.
	 */
	public GunDefinition setEquipped(GunDefinition gun){
		this.equipped = gun;
		if(gun == null){
			if(this.equippedInstance != null) this.equippedInstance.dequipped();
			this.equippedInstance = null;
		}else{
			this.equippedInstance = gun.getInstance(this, player);	
			if(flash != null) flash.remove();
			flash = equipped.getPointFlash(Day100.map.rayHandler, player.body.getPosition());
			if(flashCone != null) flashCone.remove();
			flashCone = equipped.getConeFlash(Day100.map.rayHandler, Vector2.Zero, 0);
		}
		gunChanged();
		return gun;
	}
	
	/**
	 * Sets the equipped weapon to the gun at the specified index in the {@link #guns} array and returns new new gun.
	 */
	public GunDefinition setEquipped(int gunIndex){
		if(gunIndex < 0 || gunIndex > guns.size() - 1){
			setEquipped(null);
			return null;
		}
		setEquipped(guns.get(gunIndex));
		return this.equipped;
	}
	
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
					// Adds itself to list automatically
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
		
		// Sort them
		String[] names = new String[guns.size()];
		int index = 0;
		for(GunDefinition gun : guns){
			names[index++] = gun.name;
		}
		AlphabeticalHelper.sort(names);
		ArrayList<GunDefinition> newGuns = new ArrayList<GunDefinition>();
		for(String s : names){
			newGuns.add(find(s));
		}
		guns = newGuns;

		// Load gun data from file - TODO use internal path in cache or similar
		// to load from internal
		// to avoid changing gun data as a user.
		String data = Gdx.files.internal("Cache/Guns.txt").readString();
		String[] lines = data.split("\n");
		JLineReader reader = new JLineReader(lines[0]);
		index = 0;
		do {	
			final String ERROR = "ERROR_WTF_EMPTY?";
			String name = reader.readString("name", ERROR);
			if (name.equals(ERROR)) {
				continue;
			}

			// Find that gun and load it.
			Log.info(TAG, "Loading data for '" + name + "' ...");
			find(name).load(reader);
			index++;
			if(index < lines.length)
				reader.setLine(lines[index]);
		} while (index < lines.length);

		reader.dispose();
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
	public GunDefinition next() {
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
	public GunDefinition previous() {
		if (index == 0)
			return guns.get(index = guns.size() - 1);
		return guns.get(--index);
	}

	/**
	 * Gets the next firing mode for the equipped weapon. Utilises the Gun Instance.
	 */
	public void nextFiringMode(){
		if(equipped == null || inBurst)
			return;
		
		equippedInstance.setSelectedFireMode((equippedInstance.getSelectedFireMode() + 1) % equipped.firingModes.length);
	}
	
	/**
	 * Call to indicate a change in weapon.
	 */
	public void gunChanged() {
		timer = 0;
		canShoot = false;
		angleOffset = -40;
		angleVelocity = 0;
		inBurst = false;
		burstShotsFired = 0;
		shoot = false;
		requestingShoot = false;
		if(equipped != null && player != null) equipped.equipped(getEquippedInstance(), this, player);
	}

	/**
	 * Requests this gun manager to shoot with the equipped weapon.
	 */
	public void shoot(){
		requestingShoot = true;
	}
	
	public void update(float delta) {
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
			flashCone.setActive(false);
		}

		switch (equipped.firingModes[equippedInstance.getSelectedFireMode()]) {
		case BURST:

			canShoot = timer >= equipped.shotInterval && !inBurst;
			if (requestingShoot && canShoot) {
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
			if (requestingShoot && canShoot) {
				timer = 0;
				// Shoot
				shoot = true;
			}
			break;
		case SEMI:
			canShoot = timer >= equipped.shotInterval;
			if (requestingShoot && canShoot) {
				timer = 0;
				// Shoot
				shoot = true;
			}
			break;

		}
		
		requestingShoot = false;
		
		equipped.update(getEquippedInstance(), player, this, delta);
		if(equippedInstance != null) equippedInstance.update(delta);
	}

	public void shootEquiped(float angle, Vector2 bulletSpawn) {
		if (equipped == null)
			return;

		// Add recoil
		angleVelocity = equipped.recoil;

		// Play sound.
		if(equipped.shotSounds.length > 0){
			Sound sound = equipped.shotSounds[MathUtils.random(equipped.shotSounds.length - 1)];
			float volume = MathUtils.random(equipped.minVolume, equipped.maxVolume);
			float pitch = MathUtils.random(equipped.minPitch, equipped.maxPitch);
			SoundUtils.playSound(player, sound, volume, pitch, equipped.shotSoundDistance);
			
		}

		// TESTING

		//Day100.map.blood.add(new MapBlood(Input.getMouseWorldPos(), angle));

		// Flash
		if(!player.isDead()){
			flash.setPosition(player.getBody().getPosition());
			flash.setActive(true);
			flashCone.setPosition(bulletSpawn);
			flashCone.setDirection(angle);
			flashCone.setActive(true);
		}
		
		// Visual flash
		new FlashFade(new Vector2(bulletSpawn.x, bulletSpawn.y),  angle, equipped.range);
		
		// RAY
		Vector2 end = new Vector2();
		float MAX = 200; // If range is very high, this will limit. It is the end point of the ray.
		end.x = MathUtils.cosDeg(angle) * MAX + bulletSpawn.x;
		end.y = MathUtils.sinDeg(angle) * MAX + bulletSpawn.y;
		colleateralCount = 0;
		GunManager.flashInstance = this;
		Day100.map.world.rayCast(new RayCastCallback() {
			
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				//DevCross.drawCentred(point.x, point.y);
				Object user = fixture.getBody().getUserData();
				if(user != null && user instanceof Entity){
					if(point.dst(bulletSpawn) <= equipped.range){
						// HIT AN ENTITY
						Entity e = ((Entity)user);
						e.takeDamage(equipped.damage, new DamageData(GunManager.flashInstance.player, angle + 180, new Vector2(GunManager.flashInstance.player.getBody().getPosition()), point));		
					}
				}
				return colleateralCount++ + 1 < equipped.collaterals ? 1 : 0;
			}
		}, bulletSpawn, end);
		
		if(equipped != null && player != null) equipped.shot(getEquippedInstance(), this, player);
		
	}

	public static TextureRegion[] getAnim(GunDefinition gun, String animName, int frames){
		TextureRegion[] textures = new TextureRegion[frames];
		for(int i = 0; i < frames; i++){
			textures[i] = Day100.gunsAtlas.findRegion(gun.name + animName + i);
		}
		return textures;
	}
	
	public static TextureRegion[] getAnim(String gun, String animName, int frames){
		return getAnim(find(gun), animName, frames);
	}
	
	/**
	 * Is the gun to the right side of the player?
	 */
	public boolean toRight() {
		if(this.player.body == null)
			return true; // Dunno
		return crosshair.x >= this.player.body.getPosition().x;
	}
	
	private float getAngle(){
		if(equipped == null)
			return 0;
		float originX = equippedInstance.getTexture().getRegionWidth() / Constants.PPM * equipped.holdPoint.x / 2;
		float originY = equippedInstance.getTexture().getRegionHeight() / Constants.PPM * equipped.holdPoint.y / 2;
		Vector2 playerPosition = this.player.body.getPosition();
		float angle = MathUtils.atan2(crosshair.y - (playerPosition.y - originX), crosshair.x - (playerPosition.x - originY)) * MathUtils.radiansToDegrees;
		return angle;
	}

	private static Sprite gun = new Sprite();
	public void render(Batch batch) {
		if (equipped == null)
			return;
		
		equipped.render(getEquippedInstance(), player, this, batch);
		
		if(equippedInstance != null) equippedInstance.render(batch);
		
		Vector2 playerPosition = this.player.body.getPosition();
		float offset = (toRight()) ? equipped.distanceFromPlayer : -equipped.distanceFromPlayer;
		
		// RENDER WEAPON
		gun.setRegion(equippedInstance.getTexture());
		gun.setSize(equippedInstance.getTexture().getRegionWidth() / Constants.PPM / 2, equippedInstance.getTexture().getRegionHeight() / Constants.PPM / 2);
		gun.setFlip(false, (!toRight()));
		float originX = equippedInstance.getTexture().getRegionWidth() / Constants.PPM * equipped.holdPoint.x / 2;
		float originY = equippedInstance.getTexture().getRegionHeight() / Constants.PPM * equipped.holdPoint.y / 2;
		gun.setOrigin(originX, originY);
		gun.setPosition(playerPosition.x + offset - originX, playerPosition.y - originY);
		gun.setRotation(getAngle() + angleOffset * (toRight() ? 1 : -1));
		gun.draw(batch);
		
		
		if(!shoot)
			return;
		
		// TIP
		
		// Fire!
		shootEquiped(getAngle() + angleOffset * (toRight() ? 1 : -1), getGunVectorPosition(equipped.bulletSpawn));
		
		shoot = false;
	}
	
	private static Vector2 tempReturn = new Vector2();
	private static float[] topRight = new float[2];
	private static float[] bottomRight = new float[2];
	private static float[] topLeft = new float[2];
	private static float[] bottomLeft = new float[2];
	private static float[] interpolationLeft = new float[2];
	private static float[] interpolationRight = new float[2];
	private static float[] interpolationFinal = new float[2];

	public Vector2 getGunVectorPosition(Vector2 localCoordinates){
		float[] verts = gun.getVertices();
		
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
		
		interpolationLeft[0] = interpolation.apply(topLeft[0], bottomLeft[0], 1 - localCoordinates.y);
		interpolationLeft[1] = interpolation.apply(topLeft[1], bottomLeft[1], 1 - localCoordinates.y);
		interpolationRight[0] = interpolation.apply(topRight[0], bottomRight[0], 1 - localCoordinates.y);
		interpolationRight[1] = interpolation.apply(topRight[1], bottomRight[1], 1 - localCoordinates.y);
		interpolationFinal[0] = interpolation.apply(interpolationRight[0], interpolationLeft[0], 1 - localCoordinates.x);
		interpolationFinal[1] = interpolation.apply(interpolationRight[1], interpolationLeft[1], 1 - localCoordinates.x);
	
		tempReturn.set(interpolationFinal[0], interpolationFinal[1]);
		return tempReturn;
	}

	public void renderUI(Batch batch){
		if(equipped == null)
			return;
		if(equippedInstance.getTexture().isFlipY())
			equippedInstance.getTexture().flip(false, true);
		batch.draw(equippedInstance.getTexture(), 5, 5);
		Day100.smallFont.setColor(1, 1, 1, 1);
		Day100.smallFont.draw(batch, "Firing mode : " + equipped.firingModes[equippedInstance.getSelectedFireMode()].toString().toLowerCase(), equippedInstance.getTexture().getRegionWidth(), 30);
	
		if(equipped != null) equipped.renderUI(getEquippedInstance(), player, this, batch);
		if(equippedInstance != null) equippedInstance.renderUI(batch);
	}
	
	public static Sound getGunSound(final String name) {
		return Day100.assets.get("Audio/SFX/Guns/" + name);
	}

	public void dispose(){
		if(flashInstance == this){
			flashInstance = null;
		}
	}
	
}
