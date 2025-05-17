package WhiteBoard;

import net.sourceforge.argparse4j.ArgumentParsers;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorChooserUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BoardToolBar extends JToolBar implements ActionListener {
    JButton primary;
    JButton secondary;
    JButton pencil;
    JButton eraser;
    JButton shapes;
    JComboBox<ShapeType> selectedShape;
    JTextField cursorSize;
    JButton current;
    JCheckBox fill;


    private final PaintPanel paintPanel;
    public BoardToolBar(PaintPanel paintPanel) {
        super(JToolBar.HORIZONTAL);

        this.paintPanel = paintPanel;

        primary = new JButton("");
        primary.setPreferredSize(new Dimension(30, 30));

        secondary = new JButton("");
        secondary.setPreferredSize(new Dimension(30, 30));

        try {
           Image pencilIcon = ImageIO.read(new File("icons/pencil.png"));
           Image eraserIcon = ImageIO.read(new File("icons/eraser.png"));
           Image shapesIcon = ImageIO.read(new File("icons/shapes.png"));

           pencil = new JButton("");
           pencil.setPreferredSize(new Dimension(32, 32));
           pencil.setIcon(new ImageIcon(pencilIcon));
           pencil.setBackground(Color.WHITE);

           eraser = new JButton("");
           eraser.setPreferredSize(new Dimension(32, 32));
           eraser.setIcon(new ImageIcon(eraserIcon));
           eraser.setBackground(Color.WHITE);

           shapes = new JButton("");
           shapes.setPreferredSize(new Dimension(32, 32));
           shapes.setIcon(new ImageIcon(shapesIcon));
           shapes.setBackground(Color.WHITE);

        } catch (Exception e) {
           System.exit(1);
        }

        cursorSize = new JTextField("" + paintPanel.cursorSize);
        cursorSize.setPreferredSize(new Dimension(40, 30));

        this.selectedShape = new JComboBox<>();
        this.selectedShape.addItem(ShapeType.LINE);
        this.selectedShape.addItem(ShapeType.RECTANGLE);
        this.selectedShape.addItem(ShapeType.TRIANGLE);
        this.selectedShape.addItem(ShapeType.OVAL);

        this.fill = new JCheckBox();
        this.fill.setText("fill");

        primary.addActionListener(this);
        secondary.addActionListener(this);
        pencil.addActionListener(this);
        eraser.addActionListener(this);
        shapes.addActionListener(this);
        cursorSize.addActionListener(this);
        selectedShape.addActionListener(this);
        fill.addActionListener(this);

        // Set Defaults

        current = pencil;
        current.setBackground(Color.GRAY);

        this.paintPanel.strokeType = StrokeType.PENCIL;
        this.paintPanel.shapeType = (ShapeType) selectedShape.getItemAt(0);

        this.paintPanel.primary = Color.BLACK;
        this.paintPanel.secondary = Color.WHITE;
        this.primary.setBackground(this.paintPanel.primary);
        this.secondary.setBackground(this.paintPanel.secondary);

        this.paintPanel.cursorSize = 10;
        this.cursorSize.setText(Integer.toString(this.paintPanel.cursorSize));

        this.setLayout(new FlowLayout());
        this.setFloatable(false);
        this.add(pencil);
        this.add(eraser);
        this.add(shapes);
        this.add(selectedShape);
        this.add(primary);
        this.add(secondary);
        this.add(cursorSize);
        this.add(fill);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == primary) {
            Color c = JColorChooser.showDialog(this, "Choose a colour", Color.BLACK);
            this.paintPanel.primary = c;
            this.primary.setBackground(c);
        } else if (e.getSource() == secondary) {
            Color c = JColorChooser.showDialog(this, "Choose a colour", Color.BLACK);
            this.paintPanel.secondary = c;
            this.secondary.setBackground(c);
        } else if (e.getSource() == cursorSize) {
            String text = cursorSize.getText();
            try {
                int size = Integer.parseInt(text);
                if (0 < size && size <= 100) {
                    paintPanel.cursorSize = size;
                    cursorSize.setText(Integer.toString(size));
                } else {
                    JOptionPane.showMessageDialog(this, "Cursor size can only be between 0 and 100");
                    cursorSize.setText(Integer.toString(this.paintPanel.cursorSize));
                }
            } catch(Exception exception) {
               cursorSize.setText(Integer.toString(this.paintPanel.cursorSize));
            }
        } else if (e.getSource() == this.pencil && this.current != this.pencil) {
            this.current.setBackground(Color.WHITE);
            this.current = this.pencil;
            this.current.setBackground(Color.GRAY);
            this.paintPanel.strokeType = StrokeType.PENCIL;
        } else if (e.getSource() == this.eraser && this.current != this.eraser) {
            this.current.setBackground(Color.WHITE);
            this.current = this.eraser;
            this.current.setBackground(Color.GRAY);
            this.paintPanel.strokeType = StrokeType.ERASER;
        } else if (e.getSource() == this.shapes && this.current != this.shapes) {
            this.current.setBackground(Color.WHITE);
            this.current = this.shapes;
            this.current.setBackground(Color.GRAY);
            this.paintPanel.strokeType = StrokeType.SHAPE;
        } else if (e.getSource() == this.selectedShape) {
            ShapeType shapeType = (ShapeType) selectedShape.getSelectedItem();
            this.paintPanel.shapeType = shapeType;
        } else if (e.getSource() == this.fill) {
            if(this.fill.isSelected()) {
                this.paintPanel.shouldFill = true;
            } else {
                this.paintPanel.shouldFill = false;
            }
        }
    }
}
