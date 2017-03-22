package logic;

import java.util.HashMap;

import constants.Commands.Key;
import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.constants.GameSettings;
import game.states.PlayerState;
import game.states.TreasureState;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import launcher.Main;
import util.Maths;

/**
 * The main logic of the game.
 */
public class GameLogic {

    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();

    private Main client;
    private Faction faction; // Client's faction
    private double mouseX;
    private double mouseY;

    private Rectangle fullMap;
    private Shape walkableArea;
    private Rectangle secHome;

    public GameLogic(Main client, Pane pane) {
        this.client = client;
        this.faction = client.client.player.faction;
        this.fullMap = new Rectangle(0, 0, 800, 450); // Must change this to
                                                      // inner arena size
        this.walkableArea = this.fullMap;
        this.secHome = new Rectangle(0,0,GameSettings.Arena.secHomeSize.getWidth(),GameSettings.Arena.secHomeSize.getHeight());

        // Makes the walkable area
        for (Obstacle o : client.client.gameData.obstacles) {
            Rectangle object = new Rectangle(o.topLeft.x, o.topLeft.y, o.width,
                    o.height);
            Shape s = Rectangle.subtract(walkableArea, object);
            walkableArea = s;
        }

        // Adds listeners
        pane.setFocusTraversable(true);
        pane.setOnKeyPressed(e -> {
            keys.put(e.getCode(), true);
        });
        pane.setOnKeyReleased(e -> {
            keys.put(e.getCode(), false);
        });
        pane.setOnMouseMoved(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });
    }

    public void update() {
    	// Check if player is not in the game
    	if (client.client.player.state != PlayerState.CAUGHT && client.client.player.state != PlayerState.ESCAPED) {
	        // First we find the angle from the mouse to the player
	        double angle = Maths.angle(client.client.player.position.x,
	                client.client.player.position.y, mouseX, mouseY);
	        client.client.player.direction = angle; // Updates client's direction
	                                         // (currently in radians)
	
	        if (keys.containsKey(KeyCode.W) && keys.get(KeyCode.W)) {
	            double tempX = client.client.player.position.x,
	                    tempY = client.client.player.position.y;
	            tempX += client.client.player.speed * Math.cos(angle);
	            tempY += client.client.player.speed * Math.sin(angle);
	            if (walkableArea.contains(tempX, tempY)) { // Only update movement
	                                                       // when the area is still
	                                                       // walkable
	                client.client.player.position.x = tempX;
	                client.client.player.position.y = tempY;
	            }
	        }
	        if (keys.containsKey(KeyCode.S) && keys.get(KeyCode.S)) {
	            double tempX = client.client.player.position.x,
	                    tempY = client.client.player.position.y;
	            tempX -= client.client.player.speed * Math.cos(angle);
	            tempY -= client.client.player.speed * Math.sin(angle);
	            if (walkableArea.contains(tempX, tempY)) { // Only update movement
	                                                       // when the area is still
	                                                       // walkable
	                client.client.player.position.x = tempX;
	                client.client.player.position.y = tempY;
	            }
	        }
	        if (keys.containsKey(KeyCode.A) && keys.get(KeyCode.A)) {
	            double tempX = client.client.player.position.x,
	                    tempY = client.client.player.position.y;
	            tempX += client.client.player.speed * Math.cos(angle - Math.PI / 2);
	            tempY += client.client.player.speed * Math.sin(angle - Math.PI / 2);
	            if (walkableArea.contains(tempX, tempY)) { // Only update movement
	                                                       // when the area is still
	                                                       // walkable
	                client.client.player.position.x = tempX;
	                client.client.player.position.y = tempY;
	            }
	        }
	        if (keys.containsKey(KeyCode.D) && keys.get(KeyCode.D)) {
	            double tempX = client.client.player.position.x,
	                    tempY = client.client.player.position.y;
	            tempX += client.client.player.speed * Math.cos(angle + Math.PI / 2);
	            tempY += client.client.player.speed * Math.sin(angle + Math.PI / 2);
	            if (walkableArea.contains(tempX, tempY)) { // Only update movement
	                                                       // when the area is still
	                                                       // walkable
	                client.client.player.position.x = tempX;
	                client.client.player.position.y = tempY;
	            }
	        }
	        if (Faction.THIEF == faction && keys.containsKey(KeyCode.SPACE)
	                && keys.get(KeyCode.SPACE)) { // Action button to collect
	                                              // treasures (FOR THIEVES)
	        	if (GameSettings.Arena.exit.at(client.client.player.position, 20)) {
	        		client.client.player.state = PlayerState.ESCAPED;
	        	}
	        	else {
		            Treasure tempT = null; // Saves a treasures to be collected
		            for (Treasure t : client.client.gameData.treasures) {
		                double tx = t.position.x;
		                double ty = t.position.y;
		                double px = client.client.player.position.x;
		                double py = client.client.player.position.y;
		                if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) < Math
		                        .pow(GameSettings.Thief.stealRadius, 2)) { // Treasure
		                                                                   // is in
		                                                                   // catch
		                                                                   // range.
		                    tempT = t; // This is the treasure to delete
		                }
		            }
		            if (tempT != null) { // We can't remove the treasure in the for loop
		                                 // because it will cause
		                                 // concurrentModificationException.
		                System.out.println("Score! Add: " + tempT.value);
		                client.client.gameData.thiefScore += tempT.value;
		                client.client.player.treasureScore += tempT.value;
		                client.client.gameData.treasures.remove(tempT); // So we remove it here
		                HashMap<Key, Object> map = new HashMap<Key, Object>();
		                map.put(Key.TREASURE_ID, tempT.id);
		                map.put(Key.TREASURE_STATE, TreasureState.PICKED);
		                //client.send(new Transferable(Action.UPDATE_TREASURE_STATE, new HashMap<Key, Object>()));
		            }
		
		            try { // Adds a little delay so villains won't spam action button.
		                Thread.sleep(100);
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
	        	}
	        }
	        
	        if (Faction.SECURITY == faction && keys.containsKey(KeyCode.SPACE)
	                && keys.get(KeyCode.SPACE)) { // Action button to catch
	                                              // thieves (FOR SECURITY)
	            String id = null; // initialisation
	            for (String k : client.client.gameData.players.keySet()) {
	            	Player p = client.client.gameData.players.get(k);
	            	if (p.faction == Faction.THIEF && p.state == PlayerState.NORMAL) {
		                double tx = p.position.x;
		                double ty = p.position.y;
		                double px = client.client.player.position.x;
		                double py = client.client.player.position.y;
		                if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) < Math
		                        .pow(GameSettings.Security.catchRadius, 2)) { // Player
		                                                                   // is in
		                                                                   // drag
		                                                                   // range.
		                	id = p.clientID;
		                }
	            	}
		            if (id != null) {
		                System.out.println("Catching Thief");
		                client.client.gameData.players.get(id).state = PlayerState.CAUGHT;
		                HashMap<Key, Object> map = new HashMap<Key, Object>();
		                map.put(Key.CLIENT_ID, id);
		                map.put(Key.PLAYER_STATE, PlayerState.CAUGHT);
		                //client.send(new Transferable(Action.UPDATE_PLAYER_STATE, new HashMap<Key, Object>()));
		            }
		
		            try { // Adds a little delay so guards won't spam action button.
		                Thread.sleep(100);
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
	        	}
	        }
    	}
        
        if (client.client.player.faction == Faction.SECURITY) {
        	double tempX = client.client.player.position.x;
        	double tempY = client.client.player.position.y;
        	if (this.secHome.contains(tempX,tempY)) {
        		if (client.client.player.battery < GameSettings.Security.fullBattery) {
	        		client.client.player.battery += (GameSettings.Security.chargeValue);
	        		if (client.client.player.state == PlayerState.STUCK)
	    				client.client.player.state = PlayerState.NORMAL;
        		}
        		else
        			client.client.player.battery = 1;
        	}
        	else {
	        	if (client.client.player.state == PlayerState.NORMAL) {
	        		client.client.player.battery -= (GameSettings.Security.drainValue);
	        		if (client.client.player.battery <= GameSettings.Security.noBattery) {
	        			client.client.player.battery = GameSettings.Security.noBattery;
	        			client.client.player.state = PlayerState.STUCK;
	        		}
	        	}
        	}
        }
        
        client.client.gameData.secScore += (1.0/60.0);

        // TODO Catch thieves for camera
    }
}
