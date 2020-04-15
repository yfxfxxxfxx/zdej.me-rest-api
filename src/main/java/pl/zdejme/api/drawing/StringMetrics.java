package pl.zdejme.api.drawing;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class StringMetrics {

    Font font;
    FontRenderContext context;

    public StringMetrics(Font font, FontRenderContext context) {
        this.font = font;
        this.context = context;
    }

    public Rectangle2D getBounds(String text) {
        return font.getStringBounds(text, context);
    }

    public double getWidth(String message) {
        Rectangle2D bounds = getBounds(message);
        return bounds.getWidth();
    }

    public double getHeight(String message) {
        Rectangle2D bounds = getBounds(message);
        return bounds.getWidth();
    }
}
