package logic;

import ai.handler.Handler;
import gui.GameDrawer;
import javafx.application.Platform;

/**
 * A Runnable that updates the client in a loop.
 */
public class GameLoop implements Runnable {

    private GameDrawer drawer;
    private GameLogic logic;
	//private Handler aiHandler;

    public GameLoop(GameDrawer drawer, GameLogic logic, Handler aiHandler) {
        this.drawer = drawer;
        this.logic = logic;
        //this.aiHandler = aiHandler;
    }

    public GameLoop(GameDrawer drawer, GameLogic logic) {
        this.drawer = drawer;
        this.logic = logic;
    }

    public void run() {
        while (true) {

            logic.update();
            //aiHandler.update();

            Platform.runLater(() -> {
                drawer.draw();
            });

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
