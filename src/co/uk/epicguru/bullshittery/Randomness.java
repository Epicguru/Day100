package co.uk.epicguru.bullshittery;

import com.badlogic.gdx.math.MathUtils;

public class Randomness {

	public static String[] things = new String[]{
			"Loading fonts",
			"Scanning your personal files",
			"Grabbing random assets",
			"Stopping the internet",
			"Uploading personal data",
			"Doing something",
			"Idling",
			"Starting the chopper",
			"Running to the chopper",
			"Buffing weapons",
			"Nerfing weapons",
			"Raging",
			"Creating pointless stuff",
			"Pathcing bugs",
			"Bugs patching me",
			"Stealing your house plans",
			"Hacking into the Pentagon",
			"Scanning for enemies",
			"Tanking up",
			"Deploying shield",
			"Rage quitting",
			"Killing PC",
			"Using all your CPU",
			"Not using your GPU",
			"Not trying hard enough",
			"Distracted",
			"Gaming",
			"Deploying YOUR MAMA",
			"Making bad jokes",
			"Ensuring total realism",
			"Creating an immersive experience",
			"Tracking down griefers",
			"Banning people",
			"Programming",
			"Being lazy",
			"Not doing homework",
			"Not studying for iGCSEs",
			"Dancing",
			"Placing rubble",
			"Sorting all items",
			"Loading individual pixels",
			"Stalking you on facebook",
			"Moving to N Korea",
			"Benchmarking your PC",
			"Benchmarking your phone",
			"Dominating the world",
			"Getting one-shotted",
			"Loosing all gear",
			"Creating random spawn points",
			"Buffing enemies",
			"Stopping all threads",
			"Why",
			"Please stop",
			"When was the last time you saw the sun",
			"Pausing to eat",
			"Making a game",
			"Failing all tests"
	};	

	public static String getRandomness(){
		return things[MathUtils.random(things.length - 1)];
	}
	
	public static String getRandomness(final String notThis){
		String returnVal =  things[MathUtils.random(things.length - 1)];
		while(returnVal.equals(notThis)){
			returnVal =  things[MathUtils.random(things.length - 1)];
		}
		return returnVal;
	}
}
