package co.uk.epicguru.net.server;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import co.uk.epicguru.main.Log;
import co.uk.epicguru.net.client.GameConnection;

public final class GameServer extends Server {

	private static final String TAG = "Server";
	private int tcp, udp;
	private boolean disposed;
	private boolean started;
	
	public GameServer(int tcp, int udp){
		super();
	}
	
	/**
	 * Starts the server and binds it to the ports specified in constructor.
	 */
	public void start(){
		
		if(started){
			Log.error(TAG, "The server has allready started!");
			return;
		}
		
		super.start();
		try {
			super.bind(tcp, udp);
			Log.info(TAG, "The server has started and is now bound to ports - TCP: " + tcp + ", UDP: " + udp);
			started = true;
		} catch (IOException e) {
			Log.error(TAG, "Binding of server failed to the ports - TCP: " + tcp + ", UDP: " + udp, e);
		}
	}
	
	/**
	 * Stops this game server by disconnecting all clients. The server is not functional after this.
	 * This also sets the started status to FALSE.
	 */
	public void stop(){
		super.stop();
		if(!started){			
			Log.info(TAG, "Stopped the server");
			started = false;
		}
	}
	
	/**
	 * Closes stops and disposes of this server.
	 */
	public void dispose(){
		if(disposed){
			Log.error(TAG, "Allready disposed!");
			return;
		}
		
		// Stop
		stop();
		
		try {
			super.dispose();
			Log.info(TAG, "Server disposed.");
		} catch (IOException e) {
			Log.error(TAG, "Failed to dispose server!", e);
		}
		disposed = true;
	}
	
	/**
	 * Adds the listener to the server.
	 */
	public void addListener(Listener listener) {
		super.addListener(listener);
		Log.info(TAG, "Added new listener.");
	}

	/**
	 * Gets the TCP port used by this server.
	 */
	public int getTcp() {
		return tcp;
	}

	/**
	 * Gets the UDP port used by this server.
	 */
	public int getUdp() {
		return udp;
	}

	/**
	 * Has disposed been called? Returns true if called even if dispose call threw exception and failed.
	 */
	public boolean isDisposed(){
		return disposed;
	}
	
	/**
	 * Has start been called? Returns true if called even if start call threw exception and failed.
	 */
	public boolean isStarted(){
		return started;
	}
	
	/**
	 * Gets the custom game connection.
	 */
	protected Connection newConnection() {
		return new GameConnection();
	}

	public static void main(String... args){
		new GameServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	}
}
