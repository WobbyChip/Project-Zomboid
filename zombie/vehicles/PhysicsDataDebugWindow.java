// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JFrame;
import java.awt.Point;
import java.util.List;
import java.awt.Stroke;
import java.awt.Color;
import javax.swing.JPanel;

public class PhysicsDataDebugWindow extends JPanel
{
    private static final int PREF_W = 400;
    private static final int PREF_H = 200;
    private static final int BORDER_GAP = 30;
    private static final Color GRAPH_POINT_COLOR;
    private static final Stroke GRAPH_STROKE;
    private static final int GRAPH_POINT_WIDTH = 12;
    private static final int Y_HATCH_CNT = 10;
    private static int time_divider;
    private List<Point> graphPoints_x;
    private List<Point> graphPoints_y;
    private static PhysicsDataDebugWindow mainPanel;
    private static JFrame frame;
    
    public PhysicsDataDebugWindow() {
        this.graphPoints_x = new ArrayList<Point>();
        this.graphPoints_y = new ArrayList<Point>();
    }
    
    public void addCurrentData(final long n, final float n2, final float n3) {
        if (this.graphPoints_x.size() > 100) {
            this.graphPoints_x.clear();
            this.graphPoints_y.clear();
        }
        final double n4 = (this.getWidth() - 60.0) / (PhysicsDataDebugWindow.time_divider - 1);
        final double n5 = (this.getHeight() - 60.0) / 99.0;
        final int n6 = (int)(n % PhysicsDataDebugWindow.time_divider * n4 + 30.0);
        this.graphPoints_x.add(new Point(n6, (int)(n2 * 10.0f % 100.0f * n5 + 30.0)));
        this.graphPoints_y.add(new Point(n6, (int)(n3 * 10.0f % 100.0f * n5 + 30.0)));
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.black);
        graphics2D.drawLine(30, this.getHeight() - 30, 30, 30);
        graphics2D.drawLine(30, this.getHeight() - 30, this.getWidth() - 30, this.getHeight() - 30);
        for (int i = 0; i < 10; ++i) {
            final int n = 30;
            final int n2 = 42;
            final int n3 = this.getHeight() - ((i + 1) * (this.getHeight() - 60) / 10 + 30);
            graphics2D.drawLine(n, n3, n2, n3);
        }
        graphics2D.getStroke();
        graphics2D.setColor(Color.red);
        graphics2D.setStroke(PhysicsDataDebugWindow.GRAPH_STROKE);
        for (int j = 0; j < this.graphPoints_x.size() - 1; ++j) {
            graphics2D.drawLine(this.graphPoints_x.get(j).x, this.graphPoints_x.get(j).y, this.graphPoints_x.get(j + 1).x, this.graphPoints_x.get(j + 1).y);
        }
        graphics2D.setColor(Color.green);
        graphics2D.setStroke(PhysicsDataDebugWindow.GRAPH_STROKE);
        for (int k = 0; k < this.graphPoints_y.size() - 1; ++k) {
            graphics2D.drawLine(this.graphPoints_y.get(k).x, this.graphPoints_y.get(k).y, this.graphPoints_y.get(k + 1).x, this.graphPoints_y.get(k + 1).y);
        }
        graphics2D.setColor(Color.black);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 200);
    }
    
    public static void createAndShowGui() {
        PhysicsDataDebugWindow.mainPanel = new PhysicsDataDebugWindow();
        (PhysicsDataDebugWindow.frame = new JFrame("PhysicsData")).setDefaultCloseOperation(3);
        PhysicsDataDebugWindow.frame.setLayout(new GridLayout(1, 1));
        PhysicsDataDebugWindow.frame.getContentPane().add(PhysicsDataDebugWindow.mainPanel);
        PhysicsDataDebugWindow.frame.pack();
        PhysicsDataDebugWindow.frame.setLocationByPlatform(true);
        PhysicsDataDebugWindow.frame.setVisible(true);
    }
    
    public static void updateGui() {
        PhysicsDataDebugWindow.frame.repaint();
    }
    
    public static void addCurrentDataS(final long n, final float n2, final float n3) {
        PhysicsDataDebugWindow.mainPanel.addCurrentData(n, n2, n3);
        updateGui();
    }
    
    static {
        GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
        GRAPH_STROKE = new BasicStroke(3.0f);
        PhysicsDataDebugWindow.time_divider = 1000;
    }
}
