package co.uk.epicguru.vehicles.definitions;

import com.badlogic.gdx.math.Vector2;

import co.uk.epicguru.helpers.VirtualCoordinate;
import co.uk.epicguru.vehicles.VehicleDefinition;
import co.uk.epicguru.vehicles.VehicleInstance;
import co.uk.epicguru.vehicles.definitions.instances.HelicopterInstance;

public class Helicopter extends VehicleDefinition {

	@VirtualCoordinate("RED")
	public Vector2 rotorPosition;
	
	@VirtualCoordinate("GREEN")
	public Vector2 backRotorPosition;
	
	@VirtualCoordinate("MAGENTA")
	public Vector2 playerPosition;
	
	public Helicopter() {
		super("Helicopter");
	}

	@Override
	public Vector2 getPosForRider(int index) {
		useMe.set(playerPosition);
		return useMe;
	}

	@Override
	public VehicleInstance getInstance(Vector2 position, float angle, Vector2 linearVelocity, float angularVelocity) {
		return new HelicopterInstance(this, position, angle, linearVelocity, angularVelocity);
	}

}
