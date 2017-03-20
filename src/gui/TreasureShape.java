package gui;

import game.Treasure;
import game.constants.GameSettings;

import javafx.scene.shape.Circle;

public class TreasureShape {

    public Circle circle;
    public Treasure treasure;

    
    public TreasureShape(Treasure t, double scalingRatio) {
        Circle c = new Circle(GameSettings.Treasure.radius * scalingRatio);
        c.setCenterX(t.position.x * scalingRatio);
        c.setCenterY(t.position.y * scalingRatio);
        this.circle = c;
        this.treasure = t;
    }
}
