// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import java.awt.BasicStroke;
import fmod.FMODSoundBuffer;
import fmod.SoundBuffer;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.JFrame;
import java.util.List;
import java.awt.Stroke;
import java.awt.Color;
import javax.swing.JPanel;

public class VoiceDebug extends JPanel
{
    private static final int PREF_W = 400;
    private static final int PREF_H = 200;
    private static final int BORDER_GAP = 30;
    private static final Color LINE_CURRENT_COLOR;
    private static final Color LINE_LAST_COLOR;
    private static final Color GRAPH_COLOR;
    private static final Color GRAPH_POINT_COLOR;
    private static final Stroke GRAPH_STROKE;
    private static final int GRAPH_POINT_WIDTH = 12;
    private static final int Y_HATCH_CNT = 10;
    public List<Integer> scores;
    public int scores_max;
    public String title;
    public int psize;
    public int last;
    public int current;
    private static VoiceDebug mainPanel;
    private static VoiceDebug mainPanel2;
    private static VoiceDebug mainPanel3;
    private static VoiceDebug mainPanel4;
    private static JFrame frame;
    
    public VoiceDebug(final List<Integer> scores, final String title) {
        this.scores = scores;
        this.title = title;
        this.psize = scores.size();
        this.last = 5;
        this.current = 8;
        this.scores_max = 100;
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final double n = (this.getWidth() - 60.0) / (this.scores.size() - 1);
        final double n2 = (this.getHeight() - 60.0) / (this.scores_max - 1);
        final int n3 = (int)((this.getHeight() - 60.0) / 2.0);
        int n4 = (int)(1.0 / n);
        if (n4 == 0) {
            n4 = 1;
        }
        final ArrayList<Point> list = new ArrayList<Point>();
        for (int i = 0; i < this.scores.size(); i += n4) {
            list.add(new Point((int)(i * n + 30.0), (int)((this.scores_max - this.scores.get(i)) * n2 + 30.0 - n3)));
        }
        graphics2D.setColor(Color.black);
        graphics2D.drawLine(30, this.getHeight() - 30, 30, 30);
        graphics2D.drawLine(30, this.getHeight() - 30, this.getWidth() - 30, this.getHeight() - 30);
        for (int j = 0; j < 10; ++j) {
            final int n5 = 30;
            final int n6 = 42;
            final int n7 = this.getHeight() - ((j + 1) * (this.getHeight() - 60) / 10 + 30);
            graphics2D.drawLine(n5, n7, n6, n7);
        }
        graphics2D.getStroke();
        graphics2D.setColor(VoiceDebug.GRAPH_COLOR);
        graphics2D.setStroke(VoiceDebug.GRAPH_STROKE);
        for (int k = 0; k < list.size() - 1; ++k) {
            graphics2D.drawLine(((Point)list.get(k)).x, ((Point)list.get(k)).y, ((Point)list.get(k + 1)).x, ((Point)list.get(k + 1)).y);
        }
        final double n8 = (this.getWidth() - 60.0) / (this.psize - 1);
        graphics2D.setColor(VoiceDebug.LINE_CURRENT_COLOR);
        final int n9 = (int)(this.current * n8 + 30.0);
        graphics2D.drawLine(n9, this.getHeight() - 30, n9, 30);
        graphics2D.drawString("Current", n9, this.getHeight() - 30);
        graphics2D.setColor(VoiceDebug.LINE_LAST_COLOR);
        final int n10 = (int)(this.last * n8 + 30.0);
        graphics2D.drawLine(n10, this.getHeight() - 30, n10, 30);
        graphics2D.drawString("Last", n10, this.getHeight() - 30);
        graphics2D.setColor(Color.black);
        graphics2D.drawString(this.title, this.getWidth() / 2, 15);
        graphics2D.drawString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.scores.size()), 30, 15);
        graphics2D.drawString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.current), 30, 30);
        graphics2D.drawString(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.last), 30, 45);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 200);
    }
    
    public static void createAndShowGui() {
        final ArrayList<Integer> list = new ArrayList<Integer>();
        final ArrayList<Integer> list2 = new ArrayList<Integer>();
        final ArrayList<Integer> list3 = new ArrayList<Integer>();
        final ArrayList<Integer> list4 = new ArrayList<Integer>();
        VoiceDebug.mainPanel = new VoiceDebug(list, "SoundBuffer");
        VoiceDebug.mainPanel.scores_max = 32000;
        VoiceDebug.mainPanel2 = new VoiceDebug(list2, "SoundBuffer - first 100 sample");
        VoiceDebug.mainPanel2.scores_max = 32000;
        VoiceDebug.mainPanel3 = new VoiceDebug(list3, "FMODSoundBuffer");
        VoiceDebug.mainPanel3.scores_max = 32000;
        VoiceDebug.mainPanel4 = new VoiceDebug(list4, "FMODSoundBuffer - first 100 sample");
        VoiceDebug.mainPanel4.scores_max = 32000;
        (VoiceDebug.frame = new JFrame("DrawGraph")).setDefaultCloseOperation(3);
        VoiceDebug.frame.setLayout(new GridLayout(2, 2));
        VoiceDebug.frame.getContentPane().add(VoiceDebug.mainPanel);
        VoiceDebug.frame.getContentPane().add(VoiceDebug.mainPanel2);
        VoiceDebug.frame.getContentPane().add(VoiceDebug.mainPanel3);
        VoiceDebug.frame.getContentPane().add(VoiceDebug.mainPanel4);
        VoiceDebug.frame.pack();
        VoiceDebug.frame.setLocationByPlatform(true);
        VoiceDebug.frame.setVisible(true);
    }
    
    public static void updateGui(final SoundBuffer soundBuffer, final FMODSoundBuffer fmodSoundBuffer) {
        VoiceDebug.mainPanel.scores.clear();
        if (soundBuffer != null) {
            for (int i = 0; i < soundBuffer.buf().length; ++i) {
                VoiceDebug.mainPanel.scores.add((int)soundBuffer.buf()[i]);
            }
            VoiceDebug.mainPanel.current = soundBuffer.Buf_Write;
            VoiceDebug.mainPanel.last = soundBuffer.Buf_Read;
            VoiceDebug.mainPanel.psize = soundBuffer.Buf_Size;
            VoiceDebug.mainPanel2.scores.clear();
            for (int j = 0; j < 100; ++j) {
                VoiceDebug.mainPanel2.scores.add((int)soundBuffer.buf()[j]);
            }
        }
        VoiceDebug.mainPanel3.scores.clear();
        VoiceDebug.mainPanel4.scores.clear();
        for (int k = 0; k < fmodSoundBuffer.buf().length / 2; k += 2) {
            VoiceDebug.mainPanel4.scores.add(fmodSoundBuffer.buf()[k + 1] * 256 + fmodSoundBuffer.buf()[k]);
        }
        VoiceDebug.frame.repaint();
    }
    
    static {
        LINE_CURRENT_COLOR = Color.blue;
        LINE_LAST_COLOR = Color.red;
        GRAPH_COLOR = Color.green;
        GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
        GRAPH_STROKE = new BasicStroke(3.0f);
    }
}
