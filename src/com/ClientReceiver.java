package com;

import com.util.CommandProcessor;

import constants.Commands;
import util.Client;
import util.Transferable;

public class ClientReceiver implements Runnable {
	
	private volatile Client client;
	
	private CommandProcessor commandProcessor;
	
	private final int exhaution = 10;
	
	public ClientReceiver(Client client) {
		this.client = client;
	}
	
	public void run() {		
		new Thread( this.commandProcessor ).start();
		
		while( this.client.connectionState == Client.ConnectionState.CONNECTED ) {
			this.commandProcessor.queue.offer( this.readFromServer() );
			this.commandProcessor.monitor.notifyAll();
		}
	}
	
	private synchronized Transferable readFromServer() { return this.readFromServer( 0 ); }

	private synchronized Transferable readFromServer( int _tries ) {

		if( _tries >= this.exhaution ) {return new Transferable( Commands.Action.KILL_CONNECTION ); }

		Transferable data = null;

		try { data = ( Transferable )this.client.in.readObject(); }
		catch( Exception _exception ) { this.readFromServer( _tries + 1 ); }

		return data;

	}
	
}