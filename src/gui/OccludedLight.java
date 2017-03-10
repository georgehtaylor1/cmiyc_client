package gui;

import javafx.scene.shape.Shape;

/**
 * A Shape container that maintains centerX and centerY properties.
 */
public class OccludedLight {

    private Shape shape;
    private double centerX;
    private double centerY;

    public OccludedLight(Shape shape, double centerX, double centerY) {
        this.shape = shape;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public Shape getShape() {
        return this.shape;
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterY() {
        return this.centerY;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }
}
