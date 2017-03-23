package gui;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * A Shape container that maintains centerX and centerY properties.
 */
public class CenteredShape {

    public Shape shape;

    private double centerX;
    private double centerY;

    public CenteredShape(Shape shape, double centerX, double centerY) {
        this.shape = shape;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public CenteredShape(Arc arc) {
        this(arc, arc.getCenterX(), arc.getCenterY());
    }

    public CenteredShape(Circle circle) {
        this(circle, circle.getCenterX(), circle.getCenterY());
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterY() {
        return this.centerY;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }
}
