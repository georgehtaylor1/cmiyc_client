package gui;

import game.Treasure;
import game.constants.GameSettings;

import javafx.scene.shape.Circle;

public class TreasureShape {

    public Circle circle;
    public Treasure treasure;

    public TreasureShape(Treasure t) {
        Circle c = new Circle(GameSettings.Treasure.radius);
        c.setCenterX(t.position.x);
        c.setCenterY(t.position.y);
        this.circle = c;
        this.treasure = t;
    }
}
