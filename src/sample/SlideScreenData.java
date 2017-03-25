package sample;

import java.util.Observable;

import states.ClientState;

public class SlideScreenData extends Observable {
	private int players;
	private ClientState state;
	
	public SlideScreenData() {
		this.players = 0;
		this.state = ClientState.IDLE;
	}
	
	public int getPlayers() {
		return this.players;
	}
	
	public void setPlayers(int _players) {
		this.players = _players;
		setChanged();
		notifyObservers();
	}
	
	public void setState(ClientState _state) {
		this.state = _state;
		setChanged();
		notifyObservers();
	}
	
	public ClientState getState() {
		return this.state;
	}
}
