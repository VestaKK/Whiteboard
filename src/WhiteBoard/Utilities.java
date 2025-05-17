package WhiteBoard;

import WhiteBoard.Types.ShapeType;

import java.awt.*;

public class Utilities {

    public static void drawShape(
            Graphics2D g2,
            ShapeType shapeType,
            Color drawColor,
            Color fillColor,
            int cursorSize,
            boolean shouldFill,
            int x1, int y1, int x2, int y2) {
        {
            g2.setColor(drawColor);
            g2.setStroke(new BasicStroke(cursorSize));
            switch (shapeType) {
                case NONE -> {}
                case LINE -> {
                    g2.drawLine(x1, y1, x2, y2);
                }
                case RECTANGLE -> {
                    int _x1, _y1, _x2, _y2;
                    _x1 = x1;
                    _y1 = y1;
                    _x2 = x2;
                    _y2 = y2;
                    if (x2 < x1) {
                        _x1 = x2;
                        _x2 = x1;
                    }
                    if (y2 < y1) {
                        _y1 = y2;
                        _y2 = y1;
                    }
                    g2.drawRect(_x1, _y1, _x2 - _x1, _y2 - _y1);
                    if (shouldFill) {
                        g2.setColor(fillColor);
                        g2.fillRect(_x1, _y1, _x2 - _x1, _y2 - _y1);
                        g2.setColor(drawColor);
                    }
                }
                case TRIANGLE -> {
                    int xt1 = x1;
                    int yt1 = y1;
                    int xt2 = x2;
                    int yt2 = y1;
                    int xt3 = (x2 + x1)/2;
                    int yt3 = y2;

                    g2.drawPolygon(new int[]{xt1, xt2, xt3}, new int[] {yt1, yt2, yt3}, 3);
                    if (shouldFill) {
                        g2.setColor(fillColor);
                        g2.fillPolygon(new int[]{xt1, xt2, xt3}, new int[] {yt1, yt2, yt3}, 3);
                        g2.setColor(drawColor);
                    }
                }
                case OVAL -> {
                    int _x1, _y1, _x2, _y2;
                    _x1 = x1;
                    _y1 = y1;
                    _x2 = x2;
                    _y2 = y2;
                    if (x2 < x1) {
                        _x1 = x2;
                        _x2 = x1;
                    }
                    if (y2 < y1) {
                        _y1 = y2;
                        _y2 = y1;
                    }
                    g2.drawOval(_x1, _y1, _x2 - _x1, _y2 - _y1);
                    if (shouldFill) {
                        g2.setColor(fillColor);
                        g2.fillOval(_x1, _y1, _x2 - _x1, _y2 - _y1);
                        g2.setColor(drawColor);
                    }
                }
            }
        }
    }
}
