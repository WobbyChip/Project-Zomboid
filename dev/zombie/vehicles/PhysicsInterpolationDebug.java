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

public class PhysicsInterpolationDebug extends JPanel
{
    private static final int PREF_W = 400;
    private static final int PREF_H = 200;
    private static final int BORDER_GAP = 30;
    private static final Color GRAPH_POINT_COLOR;
    private static final Stroke GRAPH_STROKE;
    private static final int GRAPH_POINT_WIDTH = 12;
    private static final int Y_HATCH_CNT = 10;
    public VehicleInterpolation idata;
    private static int time_divider;
    private long ci_time;
    private float ci_x;
    private float ci_y;
    private String ci_user;
    private List<Point> graphPoints_x;
    private List<Point> graphPoints_y;
    private List<Point> graphPoints_i;
    private static PhysicsInterpolationDebug mainPanel;
    private static JFrame frame;
    
    public PhysicsInterpolationDebug(final VehicleInterpolation idata) {
        this.graphPoints_x = new ArrayList<Point>();
        this.graphPoints_y = new ArrayList<Point>();
        this.graphPoints_i = new ArrayList<Point>();
        this.idata = idata;
    }
    
    public void addCurrentData(final long ci_time, final float ci_x, final float ci_y, final String ci_user) {
        this.ci_time = ci_time;
        this.ci_x = ci_x;
        this.ci_y = ci_y;
        this.ci_user = ci_user;
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (this.idata == null) {
            return;
        }
        final Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final double n = (this.getWidth() - 60.0) / (PhysicsInterpolationDebug.time_divider - 1);
        final double n2 = (this.getHeight() - 60.0) / 99.0;
        this.graphPoints_x.clear();
        this.graphPoints_y.clear();
        this.graphPoints_i.clear();
        for (int i = 0; i < this.idata.dataList.size(); ++i) {
            final VehicleInterpolationData vehicleInterpolationData = this.idata.dataList.get(i);
            final int n3 = (int)(vehicleInterpolationData.time / 1000000L % PhysicsInterpolationDebug.time_divider * n + 30.0);
            this.graphPoints_x.add(new Point(n3, (int)(vehicleInterpolationData.x * 10.0f % 100.0f * n2 + 30.0)));
            this.graphPoints_y.add(new Point(n3, (int)(vehicleInterpolationData.y * 10.0f % 100.0f * n2 + 30.0)));
        }
        final int n4 = (int)(this.ci_time / 1000000L % PhysicsInterpolationDebug.time_divider * n + 30.0);
        this.graphPoints_i.add(new Point(n4, (int)(this.ci_x * 10.0f % 100.0f * n2 + 30.0)));
        this.graphPoints_i.add(new Point(n4, (int)(this.ci_y * 10.0f % 100.0f * n2 + 30.0)));
        graphics2D.setColor(Color.black);
        graphics2D.drawLine(30, this.getHeight() - 30, 30, 30);
        graphics2D.drawLine(30, this.getHeight() - 30, this.getWidth() - 30, this.getHeight() - 30);
        for (int j = 0; j < 10; ++j) {
            final int n5 = 30;
            final int n6 = 42;
            final int n7 = this.getHeight() - ((j + 1) * (this.getHeight() - 60) / 10 + 30);
            graphics2D.drawLine(n5, n7, n6, n7);
        }
        final Stroke stroke = graphics2D.getStroke();
        graphics2D.setColor(Color.red);
        graphics2D.setStroke(PhysicsInterpolationDebug.GRAPH_STROKE);
        for (int k = 0; k < this.graphPoints_x.size() - 1; ++k) {
            graphics2D.drawLine(this.graphPoints_x.get(k).x, this.graphPoints_x.get(k).y, this.graphPoints_x.get(k + 1).x, this.graphPoints_x.get(k + 1).y);
        }
        graphics2D.setColor(Color.green);
        graphics2D.setStroke(PhysicsInterpolationDebug.GRAPH_STROKE);
        for (int l = 0; l < this.graphPoints_y.size() - 1; ++l) {
            graphics2D.drawLine(this.graphPoints_y.get(l).x, this.graphPoints_y.get(l).y, this.graphPoints_y.get(l + 1).x, this.graphPoints_y.get(l + 1).y);
        }
        graphics2D.setStroke(stroke);
        graphics2D.setColor(PhysicsInterpolationDebug.GRAPH_POINT_COLOR);
        for (int n8 = 0; n8 < this.graphPoints_i.size(); ++n8) {
            graphics2D.fillOval(this.graphPoints_i.get(n8).x - 6, this.graphPoints_i.get(n8).y - 6, 12, 12);
        }
        graphics2D.setColor(Color.black);
        graphics2D.drawString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.idata.dataList.size()), 30, 15);
        graphics2D.drawString(invokedynamic(makeConcatWithConstants:(JFFLjava/lang/String;)Ljava/lang/String;, this.ci_time, this.ci_x, this.ci_y, this.ci_user), 30, 30);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 200);
    }
    
    public static void createAndShowGui() {
        PhysicsInterpolationDebug.mainPanel = new PhysicsInterpolationDebug((VehicleInterpolation)null);
        (PhysicsInterpolationDebug.frame = new JFrame("DrawGraph")).setDefaultCloseOperation(3);
        PhysicsInterpolationDebug.frame.setLayout(new GridLayout(1, 1));
        PhysicsInterpolationDebug.frame.getContentPane().add(PhysicsInterpolationDebug.mainPanel);
        PhysicsInterpolationDebug.frame.pack();
        PhysicsInterpolationDebug.frame.setLocationByPlatform(true);
        PhysicsInterpolationDebug.frame.setVisible(true);
    }
    
    public static void updateGui() {
        final ArrayList<BaseVehicle> vehicles = VehicleManager.instance.getVehicles();
        for (int i = 0; i < vehicles.size(); ++i) {
            final BaseVehicle baseVehicle = vehicles.get(i);
            if (baseVehicle.getPassenger(0).character != null && !baseVehicle.isKeyboardControlled()) {
                PhysicsInterpolationDebug.mainPanel.idata = baseVehicle.interpolation;
                break;
            }
        }
        PhysicsInterpolationDebug.frame.repaint();
    }
    
    public static void addCurrentDataS(final long n, final float n2, final float n3, final String s) {
        PhysicsInterpolationDebug.mainPanel.addCurrentData(n, n2, n3, s);
    }
    
    static {
        GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
        GRAPH_STROKE = new BasicStroke(3.0f);
        PhysicsInterpolationDebug.time_divider = 1000;
    }
}
