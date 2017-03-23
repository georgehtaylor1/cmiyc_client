package gui;

import javafx.scene.layout.Pane;

public class DrawValues {

    public final double width;
    public final double height;
    public final double scaleFactor;
    public final double xOffset;
    public final double yOffset;

    public DrawValues(Pane pane) {
        final double drawRatio = GraphicsSettings.initialPaneWidth
                / GraphicsSettings.initalPaneHeight;

        double pWidth = pane.getWidth();
        double pHeight = pane.getHeight();
        double pRatio = pWidth / pHeight;
        double drawWidth;
        double drawHeight;
        double scaleFactor;
        if (pRatio > drawRatio) {
            drawHeight = pHeight;
            drawWidth = drawRatio * drawHeight;
            scaleFactor = drawWidth / GraphicsSettings.initialPaneWidth;
        } else {
            // pRatio <= drawRatio
            drawWidth = pWidth;
            drawHeight = drawWidth / drawRatio;
            scaleFactor = drawHeight / GraphicsSettings.initalPaneHeight;
        }

        double drawXOffset = pWidth / 2.0 - drawWidth / 2.0;
        double drawYOffset = pHeight / 2.0 - drawHeight / 2.0;

        this.width = drawWidth;
        this.height = drawHeight;
        this.scaleFactor = scaleFactor;
        this.xOffset = drawXOffset;
        this.yOffset = drawYOffset;
    }
}
