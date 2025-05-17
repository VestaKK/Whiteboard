package WhiteBoard.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class Whiteboard extends JFrame {

    private BoardToolBar toolBar;
    private PaintPanel paintPanel;
    private JMenuBar menuBar;
    private Dimension size;

    public Whiteboard(Dimension size) {
        super("Whiteboard");
        this.size = size;
        setPreferredSize(this.size);
        pack();
        setLocationRelativeTo(null);
        addGuiComponents();
    }

    private void addGuiComponents() {
        this.addWindowListener(new CloseWindow());
        paintPanel = new PaintPanel((int)this.size.getWidth(), (int)this.size.getHeight());
        toolBar = new BoardToolBar(paintPanel);
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAs = new JMenuItem("Save As");
        JMenuItem close = new JMenuItem("Close");
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(close);
        menuBar.add(fileMenu);

        this.setLayout(new BorderLayout());
        this.setJMenuBar(menuBar);
        this.add(toolBar, BorderLayout.NORTH);
        this.add(paintPanel, BorderLayout.CENTER);
        this.setResizable(false);
        this.setVisible(true);
        this.setFocusableWindowState(false);
    }

    private static class CloseWindow extends WindowAdapter {
       @Override
       public void windowClosing(WindowEvent event) {
           System.exit(0);
       }
    }

    public Dimension getActualDimensions() {
        return new Dimension(paintPanel.getWidth(), paintPanel.getHeight());
    }

    public void loadCanvas(BufferedImage canvas) {
       this.paintPanel.loadCanvas(canvas);
    }

    public void close() {
        this.dispose();
    }


}
