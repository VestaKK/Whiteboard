package WhiteBoard;

import javax.swing.*;
import javax.swing.plaf.ColorChooserUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Whiteboard extends JFrame {

    private BoardToolBar toolBar;
    private PaintPanel paintPanel;
    private JMenuBar menuBar;
    private JPanel guiPanel;

    public Whiteboard() {
        super("Whiteboard");
        setPreferredSize(new Dimension(1500, 1000));
        pack();
        setLocationRelativeTo(null);
        addGuiComponents();
        this.setVisible(true);
    }

    private void addGuiComponents() {
        this.addWindowListener(new CloseWindow());
        paintPanel = new PaintPanel(1500, 950);
        toolBar = new BoardToolBar(paintPanel);
        menuBar = new JMenuBar();
        guiPanel = new JPanel();

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
    }

    private class CloseWindow extends WindowAdapter {
       @Override
       public void windowClosing(WindowEvent event) {
           System.exit(0);
       }
    }
}
