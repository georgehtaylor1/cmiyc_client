package gui;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * A Shape container that maintains centerX, centerY and radius properties.
 */
public class CenteredShape {

    public Shape shape;

    private double centerX;
    private double centerY;
    private double radius;

    public CenteredShape(Shape shape, double centerX, double centerY,
            double radius) {
        this.shape = shape;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public CenteredShape(Arc arc) {
        this(arc, arc.getCenterX(), arc.getCenterY(), arc.getRadiusX());
    }

    public CenteredShape(Circle circle) {
        this(circle, circle.getCenterX(), circle.getCenterY(),
                circle.getRadius());
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterY() {
        return this.centerY;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
