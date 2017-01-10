package co.uk.epicguru.net;

import java.util.Set;

import org.reflections.Reflections;

import com.esotericsoftware.kryo.Kryo;

import co.uk.epicguru.main.Log;

public class NetRegister {

	public void apply(Kryo kryo){
		
		Reflections reflection = new Reflections("co.uk.epicguru");
		Set<Class<? extends NetMessage>> all = reflection.getSubTypesOf(NetMessage.class);
		
		Log.error("Default Net Register", "Found " + all.size() + " objects to be registered using reflection!");
		for(Class<? extends NetMessage> item : all){
			kryo.register(item.getClass());	
			System.out.println("   -" + item.getSimpleName());
		}
		
		Log.info("Default Net Register", "Registered all vanilla classes, using reflection.");
	}
	
}
