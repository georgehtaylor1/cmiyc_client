package com;

import java.util.HashMap;

import constants.Commands.Action;
import constants.Commands.Key;
import game.Faction;
import states.ClientState;
import util.Client;
import util.Debug;
import util.Transferable;
import util.Client.ConnectionState;

public class ClientSender implements Runnable {

	private volatile Client client;
	
	public volatile Object monitor;
	
	private int exhaution = 10;

	public ClientSender( Client _client) { this.client = _client; }

	@Override
	public void run() {

		Debug.say("Sender Started for [ " + this.client.id + " ]");

		while( this.client.connectionState == ConnectionState.CONNECTED ) {
			
			if( ( this.client.queue.isEmpty() ) && ( this.client.state == ClientState.IDLE ) ) {
				synchronized( this.monitor ) {
					try { this.monitor.wait(); } catch( Exception _exception ) { /* God knows. */ }
				}
			}

			if( !this.client.queue.isEmpty() ) { this.send( this.client.queue.poll() ); }
			if( this.needsPosition() ) { this.send( this.buildMovement() ); }
			
		}

	}

	private boolean needsPosition() {
		switch (this.client.getState()) {
		case FINDING: return true;
		case PREGAME: return true;
		case PLAYING: return true;
		default: return false;
		}
	}

	private Transferable buildMovement() {

		Action action = Action.UPDATE_MOVEMENT;
		HashMap<Key, Object> map = new HashMap<Key, Object>();

		map.put( Key.POSITION, this.client.player.position );

		if (this.client.player.faction == Faction.SECURITY) {
			map.put(Key.DIRECTION, this.client.player.direction);
			map.put( Key.BATTERY, this.client.player.battery );
		}

		return (new Transferable(action, map));

	}

	private void send(Transferable _data) {

		if( !this.send(_data, 0) ) { this.client.disconnect(); return; }

	}

	private boolean send(Transferable _data, int _triesCount) {

		if (_triesCount >= this.exhaution) {
			Debug.say("Sender Exhausted, preparing to stop.");
			return false;
		}

		Transferable data = _data;

		try { this.client.out.reset(); }
		catch (Exception _exception) { return this.send(data, (_triesCount + 1)); }

		try { this.client.out.writeObject(data); }
		catch (Exception _exception) { return this.send(data, (_triesCount + 1)); }

		try { this.client.out.flush(); }
		catch (Exception _exception) { return this.send(data, (_triesCount + 1)); }

		return true;

	}

}
