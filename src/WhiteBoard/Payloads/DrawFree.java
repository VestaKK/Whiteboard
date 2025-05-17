package WhiteBoard.Payloads;

import java.awt.*;
import java.io.Serializable;

public class DrawFree implements Serializable {
    public Point[] points;
    public Color drawColour;
    public int cursorSize;
    public DrawFree(Point[] points, Color drawColour, int cursorSize) {
        this.points = points;
        this.drawColour = drawColour;
        this.cursorSize = cursorSize;
    }
}
