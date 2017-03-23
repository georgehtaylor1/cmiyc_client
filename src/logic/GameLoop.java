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
    private Handler aiHandler;
    private boolean fps;

    public GameLoop(GameDrawer drawer, GameLogic logic, Handler aiHandler) {
        this.drawer = drawer;
        this.logic = logic;
        this.aiHandler = aiHandler;
    }

    public void showFps() {
        this.fps = true;
    }

    public void run() {

        double fpsAcc = 0.0;
        int itAcc = 0;

        while (true) {

            if (fpsAcc >= 1000.0) {
                fpsAcc = 0.0;
                if (this.fps) {
                    System.out.println("fps: " + itAcc);
                }
                itAcc = 0;
            }

            final double startTime = milliTime();

            logic.update();
            aiHandler.update();

            Platform.runLater(() -> {
                drawer.draw();
            });

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            }

            itAcc++;
            fpsAcc += Math.abs(milliTime() - startTime);
        }
    }

    /**
     * Returns the current time in milliseconds using System.nanoTime().
     */
    private static double milliTime() {
        return System.nanoTime() / 1000000.0;
    }
}
