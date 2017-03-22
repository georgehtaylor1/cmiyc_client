package com.util;

import java.util.concurrent.ConcurrentLinkedQueue;

import util.Client;
import util.Transferable;

public class CommandProcessor implements Runnable {

	private volatile Client client;
	
	public volatile Object monitor;
	
	public volatile ConcurrentLinkedQueue<Transferable> queue;
	
	public CommandProcessor( Client _client ) {
		this.client = _client;
	}
	
	public void run() {
		
		while( this.client.connectionState == Client.ConnectionState.CONNECTED ) {
		
			if( this.queue.isEmpty() ) {
				synchronized( this.monitor ) {
					try { this.monitor.wait(); } catch( Exception _exception ) { /* God knows. */ }
				}
			}
			
			if( !this.queue.isEmpty() ){ this.processTransferable( this.queue.poll() ); }

		}
	}
	
	private void processTransferable( Transferable _data ) {
		switch( _data.action ) {
			
			case UPDATE_PLAYER_STATE : break;
			case UPDATE_CLIENT_STATE : break;
			case UPDATE_MOVEMENT : break;
			case UPDATE_TREASURE_STATE : break;
			case DEPLOY_CAMERA : break;
			// Actions to be completed above
			// Actions to be added below
			default : break;
			
		}
	}

}
