package co.uk.epicguru.net;

import co.uk.epicguru.main.Constants;
import co.uk.epicguru.main.Day100;
import co.uk.epicguru.main.Log;
import co.uk.epicguru.net.client.GameClient;
import co.uk.epicguru.net.server.GameServer;

public final class NetUtils {

	public static final String TAG = "Net Utils";
	
	public static boolean createAndConnectClient(String ip, int tcp, int udp){
		if(Day100.client == null){
			Day100.client = new GameClient(ip, tcp, udp);
			Day100.client.register(new NetRegister());
			Day100.client.start();
			return true;
		}else{
			if(Day100.client.isDisposed()){
				Day100.client = new GameClient(ip, tcp, udp);
				Day100.client.register(new NetRegister());
				Day100.client.start();
				return true;
			}else{
				Log.error(TAG, "The main client WAS NOT NULL and WAS NOT DISPOSED. The new client was not created.");
				return false;
			}
		}
	}
	
	public static boolean createAndConnectClient(){
		return createAndConnectClient("localhost", Constants.TCP, Constants.UDP);
	}
	
	public static boolean createServer(int tcp, int udp){
		if(Day100.server == null){
			Day100.server = new GameServer(tcp, udp);
			Day100.server.register(new NetRegister());
			Day100.server.start();
			return true;
		}else{
			if(Day100.server.isDisposed()){
				Day100.server = new GameServer(tcp, udp);
				Day100.server.register(new NetRegister());
				Day100.server.start();
				return true;
			}else{
				Log.error(TAG, "The main server WAS NOT NULL and WAS NOT DISPOSED. The new server was not created.");
				return false;
			}
		}
	}
	
	public static boolean createServer(){
		return createServer(Constants.TCP, Constants.UDP);
	}

	public static boolean shutDownClient(){
		if(Day100.client == null){
			Log.error(TAG, "Main client was null, could not shut down!");
			return false;
		}else{
			if(Day100.client.isConnected()){
				Day100.client.dispose();
				Day100.client = null;
				return true;
			}else{
				if(Day100.client.isDisposed()){
					Log.error(TAG, "The client is NOT connected and IS disposed.");
					return false;
				}else{
					Log.error(TAG, "The client is NOT connected.");
					return false;
				}
			}
		}
	}

	public static boolean shutDownServer(){
		if(Day100.server == null){
			Log.error(TAG, "Main server was null, could not shut down!");
			return false;
		}else{
			if(Day100.server.isStarted()){
				Day100.server.dispose();
				Day100.server = null;
				return true;
			}else{
				if(Day100.server.isDisposed()){
					Log.error(TAG, "The server is NOT open and IS disposed.");
					return false;
				}else{
					Log.error(TAG, "The server is NOT connected.");
					return false;
				}
			}
		}
	}

	public static int getPing(){
		if(Day100.client != null && Day100.client.isConnected()){
			return Day100.client.getPing();
		}else{
			return 0;
		}
	}	
}
