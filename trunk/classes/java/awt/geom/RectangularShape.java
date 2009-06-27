package java.awt.geom;

import java.awt.Shape;

public abstract class RectangularShape implements Shape {
    public abstract double getX();
    
    public abstract double getY();
    
    public abstract double getWidth();
    
    public abstract double getHeight();
    
    public double getMinX() {
        return getX();
    }
    
    public double getMinY() {
        return getY();
    }
    
    public double getMaxX() {
        return getX() + getWidth();
    }
    
    public double getMaxY() {
        return getY() + getHeight();
    }
    
    public double getCenterX() {
        return getX() + getWidth() / 2.0;
    }
    
    public double getCenterY() {
        return getY() + getHeight() / 2.0;
    }
    
    public Rectangle2D getFrame() {
        return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }
    
    public abstract boolean isEmpty();
    
    public abstract void setFrame(double x, double y, double w, double h);
  
}
