package co.uk.epicguru.net.client;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;

import co.uk.epicguru.main.Log;
import co.uk.epicguru.net.NetRegister;

public final class GameClient extends Client {

	private static final String TAG = "Client";
	private int tcp, udp;
	private String ip;
	private boolean connected;
	private boolean disposed;
	
	public GameClient(String ip, int tcp, int udp){
		super();
		this.ip = ip;
		this.tcp = tcp;
		this.udp = udp;
	}
	
	/**
	 * Starts the client and connects it to the remote server with the connection details as
	 * specified in the constructor.
	 */
	public void start(){
		
		if(isConnected()){
			Log.error(TAG, "Client is allready connected!");
			return;
		}
		
		super.start();
		Log.info(TAG, "Starting...");
		try {
			connect(5, ip, tcp, udp);
			connected = true;
			Log.info(TAG, "Sucessfully connected to " + ip + " on ports " + tcp + ", " + udp);
			Log.info(TAG, "Connection is with " + getRemoteAddressTCP().getHostString());
			disposed = false;
		} catch (IOException e) {
			Log.error(TAG, "Exception in connecting to '" + ip + "' on ports " + tcp + ", " + udp, e);
			connected = false;
		}		
	}
	
	/**
	 * Stops the client by disconnecting it from the server.
	 */
	public void stop(){
		if(!isConnected()){
			Log.error(TAG, "Client is not connected, cannot disconnect.");
			return;
		}
		super.stop();
		Log.info(TAG, "Client stopped.");
		connected = false;
	}
	
	/**
	 * Is this client connected to the server?
	 */
	public boolean isConnected(){
		return super.isConnected() && connected;
	}
	
	/**
	 * Registers net objects to this client using the supplied register.
	 */
	public void register(NetRegister register){
		register.apply(getKryo());
	}
	
	/**
	 * Stops the client and disposes all used resources.
	 */
	public void dispose(){
		
		if(disposed){
			Log.error(TAG, "Client is allready disposed.");
			return;
		}
		
		// Stop
		stop();
		
		try {
			super.dispose();
		} catch (IOException e) {
			Log.error(TAG, "Exception in dispose!", e);
		}
		Log.info(TAG, "Disposed");
	}

	/**
	 * Has this client been disposed of?
	 */
	public boolean isDisposed(){
		return disposed;
	}
	
	/**
	 * Gets the TCP connection port.
	 * @see {@link #isConnected()}
	 */
	public int getTcp() {
		return tcp;
	}

	/**
	 * Gets the UDP connection port.
	 * @see {@link #isConnected()}
	 */
	public int getUdp() {
		return udp;
	}
	
	/**
	 * Gets the time in milliseconds that a message takes to get from from the server to this client.
	 */
	public int getPing(){
		return super.getReturnTripTime();
	}
	
	/**
	 * Gets the remote IP address that was specified in the constructor.
	 * @return
	 */
	public String getIp() {
		return ip;
	}
	
}
