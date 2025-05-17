package WhiteBoard.GUI;

import WhiteBoard.Payloads.DrawFree;
import WhiteBoard.Payloads.DrawShape;
import WhiteBoard.Types.ShapeType;
import WhiteBoard.Types.StrokeType;
import WhiteBoard.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PaintPanel extends JPanel {
    Color primary;
    Color secondary;
    int cursorSize;
    StrokeType strokeType;
    ShapeType shapeType;
    boolean shouldFill;
    private boolean shapePreview;
    private BufferedImage canvas;
    private Graphics2D graphics2D;
    private Color drawColor;
    private Color fillColor;
    private int x1, y1;
    private int x2, y2;
    private List<Point> points;
    private static final int POINT_BUFFER_LENGTH = 25;

    public PaintPanel(int width, int height) {
        super();
        this.setPreferredSize(new Dimension(width, height));
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.strokeType = StrokeType.NONE;
        this.shapeType = ShapeType.NONE;
        this.cursorSize = 0;
        this.primary = Color.WHITE;
        this.secondary = Color.WHITE;

        this.drawColor = Color.WHITE;
        this.fillColor = Color.WHITE;

        this.points = new ArrayList<>();

        MousePaintAdaptor mousePaintAdaptor = new MousePaintAdaptor(this);
        this.addMouseListener(mousePaintAdaptor);
        this.addMouseMotionListener(mousePaintAdaptor);
    }

    private void drawShape(Graphics2D g2) {
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

    public synchronized void drawFree(DrawFree p) {
        if (canvas == null) return;
        graphics2D.setColor(p.drawColour);
        for (Point point : p.points) {
            graphics2D.fillRect(point.x - p.cursorSize/2, point.y - p.cursorSize/2, p.cursorSize, p.cursorSize);
        }
    }

    public synchronized void drawShape(DrawShape p) {
        if (canvas == null) return;
        Utilities.drawShape(
                this.graphics2D,
                p.shapeType,
                p.drawColour,
                p.fillColour,
                p.cursorSize,
                p.shouldFill,
                p.x1,
                p.y1,
                p.x2,
                p.y2);
    }

    public synchronized void drawCanvas(Graphics g) {
        if (canvas == null) return;
        g.drawImage(canvas, 0, 0, null);
    }

    public synchronized void loadCanvas(BufferedImage canvas) {
        this.canvas = canvas;
        graphics2D = canvas.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setPaint(Color.white);
        graphics2D.fillRect(0, 0, getSize().width, getSize().height);
        graphics2D.setColor(drawColor);
    }

    public void paintComponent(Graphics g) {
        if (canvas == null) {
            loadCanvas(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB));
        }

        drawCanvas(g);

        synchronized (this) {
            Graphics2D preview = (Graphics2D) g;
            switch (strokeType) {
                case PENCIL -> {
                    preview.setStroke(new BasicStroke(cursorSize));
                    preview.setColor(drawColor);
                    for (Point point : points) {
                        preview.fillRect((point.x-cursorSize/2), (point.y-cursorSize/2), cursorSize, cursorSize);
                    }
                }
                case ERASER -> {
                    preview.setStroke(new BasicStroke(cursorSize));
                    preview.setColor(Color.WHITE);
                    for (Point point : points) {
                        preview.fillRect((point.x-cursorSize/2), (point.y-cursorSize/2), cursorSize, cursorSize);
                    }
                }
                case SHAPE -> {
                    Utilities.drawShape(preview, shapeType, drawColor, fillColor,cursorSize, shouldFill, x1, y1, x2, y2);
                }
            }
            switch (strokeType) {
                case PENCIL -> {
                    preview.setStroke(new BasicStroke(cursorSize));
                    preview.setColor(drawColor);
                    for (Point point : points) {
                        preview.fillRect((point.x-cursorSize/2), (point.y-cursorSize/2), cursorSize, cursorSize);
                    }
                }
                case ERASER -> {
                    preview.setStroke(new BasicStroke(cursorSize));
                    preview.setColor(Color.WHITE);
                    for (Point point : points) {
                        preview.fillRect((point.x-cursorSize/2), (point.y-cursorSize/2), cursorSize, cursorSize);
                    }
                }
                case SHAPE -> {
                    Utilities.drawShape(preview, shapeType, drawColor, fillColor,cursorSize, shouldFill, x1, y1, x2, y2);
                }
            }
        }
    }

    private class MousePaintAdaptor extends MouseAdapter {
        public MousePaintAdaptor(PaintPanel p) {
           super();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isMiddleMouseButton(e)) {
                if (strokeType == StrokeType.ERASER) {
                   strokeType = StrokeType.SHAPE;
                } else {
                    strokeType = StrokeType.ERASER;
                }
            }
            x1 = e.getX();
            y1 = e.getY();
            x2 = x1;
            y2 = y1;

            if (SwingUtilities.isRightMouseButton(e)) {
                drawColor = secondary;
                fillColor = primary;
            } else {
                drawColor = primary;
                fillColor = secondary;
            }

            switch (strokeType) {
                case NONE -> {}
                case PENCIL -> {
                    points.add(new Point(x2, y2));
                    graphics2D.setStroke(new BasicStroke(cursorSize));
                    graphics2D.setColor(drawColor);
                    // graphics2D.fillRect((x2-cursorSize/2), (y2-cursorSize/2), cursorSize, cursorSize);
                }
                case ERASER -> {
                    points.add(new Point(x2, y2));
                    graphics2D.setStroke(new BasicStroke(cursorSize));
                    graphics2D.setColor(Color.WHITE);
                    // graphics2D.fillRect((x2-cursorSize/2), (y2-cursorSize/2), cursorSize, cursorSize);
                }
                case SHAPE -> {
                    shapePreview = true;
                }
            }
            repaint();
        }

        public void mouseDragged(MouseEvent e) {
            switch (strokeType) {
                case PENCIL -> {
                    x1 = x2;
                    y1 = y2;
                    x2 = e.getX();
                    y2 = e.getY();
                    points.add(new Point(x2, y2));
                    if (points.size() > POINT_BUFFER_LENGTH) {
                        // client send thing
                        points.clear();
                    }
                    // graphics2D.setStroke(new BasicStroke(cursorSize));
                    // graphics2D.setColor(drawColor);
                    // graphics2D.fillRect((x2-cursorSize/2), (y2-cursorSize/2), cursorSize, cursorSize);
                }
                case ERASER -> {
                    x1 = x2;
                    y1 = y2;
                    x2 = e.getX();
                    y2 = e.getY();
                    points.add(new Point(x2, y2));
                    if (points.size() > POINT_BUFFER_LENGTH) {
                        // client send thing
                        points.clear();
                    }
                    // graphics2D.setStroke(new BasicStroke(cursorSize));
                    // graphics2D.setColor(Color.WHITE);
                    // graphics2D.fillRect((x2-cursorSize/2), (y2-cursorSize/2), cursorSize, cursorSize);
                }
                case SHAPE -> {
                    x2 = e.getX();
                    y2 = e.getY();
                }
            }
            repaint();
        }

        public void mouseReleased(MouseEvent e) {
            if (strokeType == StrokeType.SHAPE) {
                shapePreview = false;
                // client send thing
                Utilities.drawShape(graphics2D, shapeType, drawColor, fillColor,cursorSize, shouldFill, x1, y1, x2, y2);

            } else if (!points.isEmpty()) {
                // client send thing
            }

            points.clear();

            x1 = x2;
            y1 = y2;
            repaint();
        }
    }


}
