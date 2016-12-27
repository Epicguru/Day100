package co.uk.epicguru.player.weapons;

import com.badlogic.gdx.graphics.g2d.Batch;

import co.uk.epicguru.main.Log;
import co.uk.epicguru.map.Entity;

public class GunInstance {

	public GunManager gunManager;
	public Entity entity;
	public GunDefinition gun;
	public int selectedFireMode;
	
	public GunInstance(GunManager gunManager, Entity entity){
		this.gunManager = gunManager;
		this.entity = entity;
		this.gun = gunManager.getEquipped();
	}
	
	/**
	 * Checks weather the equipped weapon still matches the weapon that this instance is associated to.
	 * @return
	 */
	public boolean stillEquipped(){
		if(gunManager.getEquipped() == null)
			return false;
		if(gunManager.getEquipped() != gun)
			return false;
		return true;
	}
	
	public void dequipped(){
		
	}
	
	/**
	 * Called every frame when this weapon is equipped.
	 * @param delta
	 */
	public void update(float delta){
		
	}
	
	/**
	 * Called every frame when this weapon is equipped.
	 * @param batch
	 */
	public void render(Batch batch){
		
	}
	
	/**
	 * Called every frame when this weapon is equipped.
	 * @param batch
	 */
	public void renderUI(Batch batch){
		
	}

	/**
	 * Gets the current firing mode.
	 */
	public int getSelectedFireMode() {
		return selectedFireMode;
	}
	
	/**
	 * Sets the selected firing mode from an index. Has the same effect as {@link #setFiringMode(FiringMode)} but this is less user friendly.
	 * @param selectedFireMode
	 */

	public void setSelectedFireMode(int selectedFireMode) {
		this.selectedFireMode = selectedFireMode;
		Log.info(gun.name, "Setting firing mode to " + selectedFireMode + " for owner " + entity.toString());
	}
	
	/**
	 * Sets the firing mode to the new one IF this gun supports that firing mode.
	 * @param mode The new firing mode.
	 * @return Weather the operation was successful.
	 */
	public boolean setFiringMode(FiringMode mode){
		if(!gun.isFiringModeAll(mode))
			return false;
		
		for(int i = 0; i < gun.firingModes.length; i++){
			if(gun.firingModes[i] == mode)
				setSelectedFireMode(i);
		}
		
		return true;
	}
	
}
