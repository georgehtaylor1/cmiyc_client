package graphics;

import game.GameData;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import launcher.Main;

public class GameDrawer {

    private GameData game;
    private Pane pane;
    private Main main;
    public GameDrawer(Pane pane, GameData game, Main main) {
        this.game = game;
        this.pane = pane;
        this.main = main;
        pane.setStyle("-fx-background-color: black;");
        pane.setPrefSize(840, 530);
    }

    public void draw() {
        pane.getChildren().clear();

        double x = main.player.position.x;
        double y = main.player.position.y;
        Circle c = new Circle(10, Color.BLUE);
        c.relocate(x - 10, y - 10);
        pane.getChildren().add(c);
    }
}
