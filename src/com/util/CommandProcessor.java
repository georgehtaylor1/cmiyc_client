package com.util;

import java.util.concurrent.ConcurrentLinkedQueue;

import constants.Commands.Key;
import game.Camera;
import game.states.PlayerState;
import game.states.TreasureState;
import states.ClientState;
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
			
			case UPDATE_PLAYER_STATE:
				this.client.gameData.players.get(_data.object.get(Key.CLIENT_ID)).state = (PlayerState) _data.object.get(Key.PLAYER_STATE);
				break;
			case UPDATE_CLIENT_STATE:
				this.client.state = (ClientState) _data.object.get(Key.CLIENT_STATE);
				break;
			case UPDATE_MOVEMENT:
				break;
			case UPDATE_TREASURE_STATE:
				for (int i=0; i < client.gameData.treasures.size(); i++) {
					if (client.gameData.treasures.get(i).id == _data.object.get(Key.TREASURE_ID)) {
						client.gameData.treasures.get(i).state = (TreasureState) _data.object.get(Key.TREASURE_STATE);
						break;
					}
				}
				break;
			case DEPLOY_CAMERA:
				client.gameData.cameras.add((Camera) _data.object.get(Key.CAMERA));
				break;
			// Actions to be completed above
			// Actions to be added below
			default:
				break;
			
		}
	}

}