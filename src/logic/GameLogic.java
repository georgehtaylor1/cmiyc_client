package logic;

import java.util.HashMap;
import java.util.Map;

import game.Camera;
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
import util.Debug;
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

    
    public GameLogic(Main client, Pane pane) {
        this.client = client;
        this.faction = client.player.faction;
        this.fullMap = new Rectangle(20, 20, 800, 450); // Must change this to
                                                      // inner arena size
        this.walkableArea = this.fullMap;

        // Makes the walkable area
        for (Obstacle o : client.gameData.obstacles) {
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
        // First we find the angle from the mouse to the player
        double angle = Maths.angle(client.player.position.x,
                client.player.position.y, mouseX, mouseY);
        client.player.direction = Maths.normalizeAngle(angle); // Updates client's direction
                                         // (currently in radians)

        if (Math.pow(client.player.position.x - mouseX, 2) + Math.pow(client.player.position.y - mouseY, 2) < Math.pow(10, 2)) {
        	return;
        }
        
        if (keys.containsKey(KeyCode.W) && keys.get(KeyCode.W)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX += client.player.speed * Math.cos(angle);
            tempY += client.player.speed * Math.sin(angle);
            
            if (walkableArea.contains(tempX, client.player.position.y)) 
            	client.player.position.x = tempX;

            if (walkableArea.contains(client.player.position.x, tempY)) 
            	client.player.position.y = tempY;
        }
        if (keys.containsKey(KeyCode.S) && keys.get(KeyCode.S)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX -= client.player.speed * Math.cos(angle);
            tempY -= client.player.speed * Math.sin(angle);
            if (walkableArea.contains(tempX, client.player.position.y)) 
            	client.player.position.x = tempX;

            if (walkableArea.contains(client.player.position.x, tempY)) 
            	client.player.position.y = tempY;
        }
        if (keys.containsKey(KeyCode.A) && keys.get(KeyCode.A)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX += client.player.speed * Math.cos(angle - Math.PI / 2);
            tempY += client.player.speed * Math.sin(angle - Math.PI / 2);
            if (walkableArea.contains(tempX, client.player.position.y)) 
            	client.player.position.x = tempX;

            if (walkableArea.contains(client.player.position.x, tempY)) 
            	client.player.position.y = tempY;
        }
        if (keys.containsKey(KeyCode.D) && keys.get(KeyCode.D)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX += client.player.speed * Math.cos(angle + Math.PI / 2);
            tempY += client.player.speed * Math.sin(angle + Math.PI / 2);
            if (walkableArea.contains(tempX, client.player.position.y)) 
            	client.player.position.x = tempX;

            if (walkableArea.contains(client.player.position.x, tempY)) 
            	client.player.position.y = tempY;
        }
        if (Faction.THIEF == faction && keys.containsKey(KeyCode.SPACE)
                && keys.get(KeyCode.SPACE)) { // Action button to collect
                                              // treasures (FOR THIEVES)
        	double px = client.player.position.x;
        	double py = client.player.position.y;

        	Treasure tempT = null; // Saves a treasures to be collected
            for (Treasure t : client.gameData.treasures) {
                double tx = t.position.x;
                double ty = t.position.y;
                
                if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) <= Math
                        .pow(GameSettings.Thief.stealRadius, 2)) { // Treasure
                                                                   // is in
                                                                   // catch
                                                                   // range.
                    tempT = t; // This is the treasure to delete
                    break;
                }
            }
            collectTreasure(tempT);
        }
        
        if (faction == Faction.SECURITY && keys.containsKey(KeyCode.SPACE) && keys.get(KeyCode.SPACE)) {
        	double px = client.player.position.x;
        	double py = client.player.position.y;
        	String nameToBeRemoved = null;
        	Player thiefToBeRemoved = null;
        	for (Map.Entry<String, Player> p : client.gameData.players.entrySet()) {
        		Player tempP = p.getValue();
        		
        		if (tempP.faction == Faction.SECURITY) {
        			continue;
        		}
        		
        		double tx = tempP.position.x;
        		double ty = tempP.position.y;	
    			if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) <= Math.pow(GameSettings.Security.catchRadius, 2)) {
    				nameToBeRemoved = p.getKey();
    				thiefToBeRemoved = tempP;
    				break;
        		}
        	}
        	captureThief(nameToBeRemoved,thiefToBeRemoved);
        }
        	
        // Deploy camera
        if (faction == Faction.SECURITY && keys.containsKey(KeyCode.C) && keys.get(KeyCode.C)) {
        	if (client.player.cameras > 0) {
        		Camera deployed = new Camera(client.player.position.x, client.player.position.y, -Math.toDegrees(angle), GameSettings.Security.lightRadius);
        		client.gameData.cameras.add(deployed);
        		client.player.cameras--;
        		Debug.say("deployed camera. Left " + client.player.cameras + " cameras");
        	}
        }
    }
    
    /**
     * Method to collect treasure for thief if in range
     * @param t to be removed
     */
    public void collectTreasure(Treasure treasure) {
    	if (treasure != null) {
    		treasure.state = TreasureState.PICKED;
    		client.gameData.treasures.remove(treasure);
            Debug.say("Score! Add: " + treasure.value);
            try { // Adds a little delay so villains won't spam action button.
            	Thread.sleep(100);
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
    }
    
    /**
     * Method to capture thief
     * @param thief to be removed
     */
    public void captureThief(String name, Player thief) {
    	if (thief != null) {
    		thief.state = PlayerState.CAUGHT;
    		client.gameData.players.remove(name);
    		Debug.say("Thief " + thief.clientID + " captured. Add 100 points!");
    		try { // Adds a little delay so villains won't spam action button.
    			Thread.sleep(100);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
   
}
