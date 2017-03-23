package util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ClientReceiver;
import com.ClientSender;

import constants.Commands.Action;
import constants.Commands.Key;
import game.GameData;
import game.Player;
import states.ClientState;

public class Client {

	public static enum ConnectionState { CONNECTED, DISCONNECTED; }
	
	public volatile String id;
	public volatile String username;
	
	public volatile ClientState state;
	public volatile ConnectionState connectionState;
	
	public volatile Player player;
	
	public volatile GameData gameData;
	
	public volatile ObjectInputStream in;
	public volatile ObjectOutputStream out;
	
	public ClientSender sender;
	public ClientReceiver receiver;
	
	public volatile ConcurrentLinkedQueue<Transferable> queue;
	
	public int port;
	public String host;
	private Socket socket;
	
	public Client( String _username ) {
		this.id = UUID.randomUUID().toString();
		this.username = _username;
		this.player = new Player( this.id );
		this.state = ClientState.IDLE; // change to IDLE;
		this.connectionState = ConnectionState.DISCONNECTED;
		this.in = null;
		this.out = null;
		this.receiver = null;
		this.sender = null;
		this.queue = new ConcurrentLinkedQueue<Transferable>();
		this.gameData = new GameData();
	}
	
	public void connect(int _port, String _host, String _username) {
		this.username = _username;
		this.port = _port;
		this.host = _host;
		
		System.out.println("Connecting to " + host + ":" + port);

		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try { socket = new Socket( this.host, this.port ); }
		catch( Exception _exception ) { return; } // TODO: Socket Error output

		try {
			out = new ObjectOutputStream( socket.getOutputStream() );
			out.flush();
		}
		catch( Exception _exception ) {
			Debug.say("Error Occured while trying to open new Output Stream.");
			return;
		}

		try { in = new ObjectInputStream( socket.getInputStream() ); }
		catch( Exception _exception ) {
			Debug.say("Error Occured while trying to open new Input Stream.");
			return;
		}
		
		this.start(in, out);
	}
	
	public void start( ObjectInputStream _in, ObjectOutputStream _out ) {
		this.in = _in;
		this.out = _out;

		this.sender = new ClientSender( this );
		this.receiver = new ClientReceiver( this );
		
		this.connectionState = ConnectionState.CONNECTED;
		
		new Thread( this.sender ).start();
		new Thread( this.receiver ).start();
		
		HashMap<Key, Object> data = new HashMap<Key, Object>();
		
		Action action = Action.INIT_CLIENT;

		data.put( Key.CLIENT_ID, this.id );
		data.put( Key.CLIENT_USERNAME, this.username );
				
		this.send( ( new Transferable( action, data ) ) );
	}

	public void disconnect() {

		this.connectionState = ConnectionState.DISCONNECTED;

		try { this.in.close(); this.out.close(); }
		catch( Exception _exception ) { /* Then they are already closed */ }

		this.in = null;
		this.out = null;

		this.sender = null;
		this.receiver = null;
		
		try {
			this.socket.close();
		} catch (Exception e) {
			Debug.say("Cannot close socket");
		}
		
	}

	public void send( Transferable _data ) {
		this.queue.offer( _data ); 
		//synchronized( this.sender.monitor ) { this.sender.monitor.notifyAll(); }
	}
	
	public void setState( ClientState _state ) {
		if( _state != ClientState.IDLE || _state != ClientState.POSTGAME ) { synchronized( this.sender.monitor ) { this.sender.monitor.notifyAll(); } }
		this.state = _state;
	}
	
	public ClientState getState() { return this.state; }

}
