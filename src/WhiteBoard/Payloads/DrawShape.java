package WhiteBoard.Payloads;

import WhiteBoard.Types.ShapeType;

import java.awt.*;

public class DrawShape {
    public ShapeType shapeType;
    public Color drawColour;
    public Color fillColour;
    public int cursorSize;
    public boolean shouldFill;
    public int x1, y1, x2, y2;
}
