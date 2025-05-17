package WhiteBoard.Payloads;

import java.awt.*;
import java.io.Serializable;

public class DrawLine implements Serializable {
    public Point[] point;
    public Color drawColour;
    public int cursorSize;
}
