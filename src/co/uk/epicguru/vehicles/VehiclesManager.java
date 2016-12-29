package co.uk.epicguru.vehicles;

import java.util.ArrayList;
import java.util.Set;

import org.reflections.Reflections;

import com.badlogic.gdx.Gdx;

import co.uk.epicguru.IO.JLineReader;
import co.uk.epicguru.helpers.AlphabeticalHelper;
import co.uk.epicguru.main.Log;

public class VehiclesManager {

	private static final String TAG = "Vehicles Manager";
	public static ArrayList<VehicleDefinition> vehicles = new ArrayList<VehicleDefinition>();

	public static void reset(){
		vehicles.clear();
		Log.info(TAG, "Finding vehicles.");

		final String path = "co.uk.epicguru.vehicles.definitions";
		Log.info(TAG, "Scanning for vehicles in " + path);
		Set<Class<? extends VehicleDefinition>> found = new Reflections(path).getSubTypesOf(VehicleDefinition.class);
		for(Class<? extends VehicleDefinition> vehicle : found){
			for(int i = 0; i < vehicle.getConstructors().length; i++){
				try {
					VehicleDefinition newVehicle = (VehicleDefinition) vehicle.getConstructors()[i].newInstance();
					// Adds itself to list automatically
					Log.info(TAG, "Found vehicle '" + newVehicle.name + "' of class '" + vehicle.getSimpleName() + "' using constructor #" + i);
					break; // Because we found an adequate constructor.
				} catch (Exception e){
					if(i != vehicle.getConstructors().length - 1)
						return;
					Log.error(TAG, "ERROR LOADING VEHICLE '" + vehicle.getSimpleName() + "'. Constructor with 0 arguments failed.");
					Log.error(TAG, "Ensure that the class has at least one constructor with 0 args.");
					Log.error(TAG, "The stack trace for the LAST constructor tried follows:");
					Log.error(TAG, "Error in constructor call for vehicle class '" + vehicle.getName() + "'", e);
				}
			}

		}

		// Sort them
		Log.info(TAG, "Sorting vehicles...");
		String[] names = new String[vehicles.size()];
		int index = 0;
		for(VehicleDefinition vehicleDefinition : vehicles){
			names[index++] = vehicleDefinition.name;
		}
		AlphabeticalHelper.sort(names);
		ArrayList<VehicleDefinition> newVehciles = new ArrayList<VehicleDefinition>();
		for(String s : names){
			newVehciles.add(find(s));
		}
		vehicles = newVehciles;
		Log.info(TAG, "Done!");

		// Load vehicle data from file
		// to load from internal
		// to avoid changing gun data as a user.
		String data = Gdx.files.internal("Cache/Vehicles.txt").readString();
		String[] lines = data.split("\n");
		JLineReader reader = new JLineReader(lines[0]);
		index = 0;
		do {	
			final String ERROR = "ERROR_WTF_EMPTY?";
			String name = reader.readString("name", ERROR);
			if (name.equals(ERROR)) {
				index++;
				continue;
			}

			// Find that gun and load it.
			Log.info(TAG, "Loading data for '" + name + "' ...");
			VehicleDefinition def = find(name);
			if(def != null) def.load(reader);
			index++;
			if(index < lines.length)
				reader.setLine(lines[index]);
		} while (index < lines.length);

		reader.dispose();
	}
	
	/**
	 * Finds a vehicle by name.
	 * 
	 * @param name The name of the vehicle.
	 * @return The vehicle definition or null if not found in the array.
	 */
	public static VehicleDefinition find(final String name) {
		for (VehicleDefinition vehicleDefinition : vehicles) {
			if (vehicleDefinition.name.equals(name))
				return vehicleDefinition;
		}
		return null;
	}

}
