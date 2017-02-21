package ai;

import ai.handler.Handler;
import ai.states.ThiefState;
import game.Faction;

public class Thief extends AI {

	private ThiefState state;
	
	public Thief(Handler handler) {
		super("AIThief", handler);
		this.faction = Faction.THIEF;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		
		while(isRunning()){
			
		}

	}

}
