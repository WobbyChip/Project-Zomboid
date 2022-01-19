// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.booleanrectangles;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collection;
import org.lwjgl.util.Rectangle;
import java.util.ArrayList;

public class BooleanRectangleCollection extends ArrayList<Rectangle>
{
    static boolean[][] donemap;
    private static Point intersection;
    static int retWidth;
    static int retHeight;
    
    public void doIt(final ArrayList<Rectangle> list, final Rectangle rectangle) {
        final ArrayList<Rectangle> c = new ArrayList<Rectangle>();
        final Iterator<Rectangle> iterator = list.iterator();
        while (iterator.hasNext()) {
            c.addAll(this.doIt(iterator.next(), rectangle));
        }
        this.clear();
        this.addAll(c);
        this.optimize();
    }
    
    public void cutRectangle(final Rectangle rectangle) {
        final ArrayList<Rectangle> list = new ArrayList<Rectangle>();
        list.addAll(this);
        this.doIt(list, rectangle);
    }
    
    public ArrayList<Rectangle> doIt(final Rectangle rectangle, final Rectangle rectangle2) {
        final ArrayList<Rectangle> list = new ArrayList<Rectangle>();
        final ArrayList<Object> list2 = new ArrayList<Object>();
        final ArrayList<Integer> list3 = new ArrayList<Integer>();
        final ArrayList<Integer> list4 = new ArrayList<Integer>();
        final ArrayList<Line> list5 = new ArrayList<Line>();
        final ArrayList<Line> list6 = new ArrayList<Line>();
        list5.add(new Line(new Point(rectangle.getX(), rectangle.getY()), new Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY())));
        list5.add(new Line(new Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY()), new Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight())));
        list5.add(new Line(new Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight()), new Point(rectangle.getX(), rectangle.getY() + rectangle.getHeight())));
        list5.add(new Line(new Point(rectangle.getX(), rectangle.getY() + rectangle.getHeight()), new Point(rectangle.getX(), rectangle.getY())));
        list6.add(new Line(new Point(rectangle2.getX(), rectangle2.getY()), new Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY())));
        list6.add(new Line(new Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY()), new Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight())));
        list6.add(new Line(new Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight()), new Point(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight())));
        list6.add(new Line(new Point(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight()), new Point(rectangle2.getX(), rectangle2.getY())));
        for (int i = 0; i < list5.size(); ++i) {
            for (int j = 0; j < list6.size(); ++j) {
                if (this.IntesectsLine(list5.get(i), list6.get(j)) != 0 && this.IsPointInRect(BooleanRectangleCollection.intersection.X, BooleanRectangleCollection.intersection.Y, rectangle)) {
                    list2.add(new Point(BooleanRectangleCollection.intersection.X, BooleanRectangleCollection.intersection.Y));
                }
            }
        }
        if (this.IsPointInRect(rectangle2.getX(), rectangle2.getY(), rectangle)) {
            list2.add(new Point(rectangle2.getX(), rectangle2.getY()));
        }
        if (this.IsPointInRect(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY(), rectangle)) {
            list2.add(new Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY()));
        }
        if (this.IsPointInRect(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight(), rectangle)) {
            list2.add(new Point(rectangle2.getX() + rectangle2.getWidth(), rectangle2.getY() + rectangle2.getHeight()));
        }
        if (this.IsPointInRect(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight(), rectangle)) {
            list2.add(new Point(rectangle2.getX(), rectangle2.getY() + rectangle2.getHeight()));
        }
        list2.add(new Point(rectangle.getX(), rectangle.getY()));
        list2.add(new Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY()));
        list2.add(new Point(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight()));
        list2.add(new Point(rectangle.getX(), rectangle.getY() + rectangle.getHeight()));
        Collections.sort(list2, (Comparator<? super Object>)new Comparator<Point>() {
            @Override
            public int compare(final Point point, final Point point2) {
                if (point.Y != point2.Y) {
                    return point.Y - point2.Y;
                }
                return point.X - point2.X;
            }
        });
        int n = ((Point)list2.get(0)).X;
        int n2 = ((Point)list2.get(0)).Y;
        list3.add(n);
        list4.add(n2);
        for (final Point point : list2) {
            if (point.X > n) {
                n = point.X;
                list3.add(n);
            }
            if (point.Y > n2) {
                n2 = point.Y;
                list4.add(n2);
            }
        }
        for (int k = 0; k < list4.size() - 1; ++k) {
            for (int l = 0; l < list3.size() - 1; ++l) {
                final int intValue = list3.get(l);
                final int intValue2 = list4.get(k);
                final Rectangle e = new Rectangle(intValue, intValue2, list3.get(l + 1) - intValue, list4.get(k + 1) - intValue2);
                if (!this.Intersects(e, rectangle2)) {
                    list.add(e);
                }
            }
        }
        return list;
    }
    
    public void optimize() {
        final ArrayList<Rectangle> c = new ArrayList<Rectangle>();
        int x = 1000000;
        int y = 1000000;
        int n = -1000000;
        int n2 = -1000000;
        for (int i = 0; i < this.size(); ++i) {
            final Rectangle rectangle = this.get(i);
            if (rectangle.getX() < x) {
                x = rectangle.getX();
            }
            if (rectangle.getY() < y) {
                y = rectangle.getY();
            }
            if (rectangle.getX() + rectangle.getWidth() > n) {
                n = rectangle.getX() + rectangle.getWidth();
            }
            if (rectangle.getY() + rectangle.getHeight() > n2) {
                n2 = rectangle.getY() + rectangle.getHeight();
            }
        }
        final int n3 = n - x;
        final int n4 = n2 - y;
        for (int j = 0; j < n3; ++j) {
            for (int k = 0; k < n4; ++k) {
                BooleanRectangleCollection.donemap[j][k] = true;
            }
        }
        for (int l = 0; l < this.size(); ++l) {
            final Rectangle rectangle2 = this.get(l);
            final int n5 = rectangle2.getX() - x;
            final int n6 = rectangle2.getY() - y;
            for (int n7 = 0; n7 < rectangle2.getWidth(); ++n7) {
                for (int n8 = 0; n8 < rectangle2.getHeight(); ++n8) {
                    BooleanRectangleCollection.donemap[n7 + n5][n8 + n6] = false;
                }
            }
        }
        for (int n9 = 0; n9 < n3; ++n9) {
            for (int n10 = 0; n10 < n4; ++n10) {
                if (!BooleanRectangleCollection.donemap[n9][n10]) {
                    final int doHeight = this.DoHeight(n9, n10, n4);
                    final int doWidth = this.DoWidth(n9, n10, doHeight, n3);
                    for (int n11 = 0; n11 < doWidth; ++n11) {
                        for (int n12 = 0; n12 < doHeight; ++n12) {
                            BooleanRectangleCollection.donemap[n11 + n9][n12 + n10] = true;
                        }
                    }
                    c.add(new Rectangle(n9 + x, n10 + y, doWidth, doHeight));
                }
            }
        }
        this.clear();
        this.addAll(c);
    }
    
    public boolean IsPointInRect(final int n, final int n2, final Rectangle rectangle) {
        return n >= rectangle.getX() && n <= rectangle.getX() + rectangle.getWidth() && n2 >= rectangle.getY() && n2 <= rectangle.getY() + rectangle.getHeight();
    }
    
    public int IntesectsLine(final Line line, final Line line2) {
        BooleanRectangleCollection.intersection.X = 0;
        BooleanRectangleCollection.intersection.Y = 0;
        final int n = line.End.X - line.Start.X;
        final int n2 = line.End.Y - line.Start.Y;
        final int n3 = line2.End.X - line2.Start.X;
        final int n4 = line2.End.Y - line2.Start.Y;
        if (n == n3 || n2 == n4) {
            return 0;
        }
        if (n2 == 0) {
            Math.min(line.Start.X, line.End.X);
            Math.max(line.Start.X, line.End.X);
            Math.min(line2.Start.Y, line2.End.Y);
            Math.max(line2.Start.Y, line2.End.Y);
            BooleanRectangleCollection.intersection.X = line2.Start.X;
            BooleanRectangleCollection.intersection.Y = line.Start.Y;
            return 1;
        }
        Math.min(line2.Start.X, line2.End.X);
        Math.max(line2.Start.X, line2.End.X);
        Math.min(line.Start.Y, line.End.Y);
        Math.max(line.Start.Y, line.End.Y);
        BooleanRectangleCollection.intersection.X = line.Start.X;
        BooleanRectangleCollection.intersection.Y = line2.Start.Y;
        return -1;
    }
    
    public boolean Intersects(final Rectangle rectangle, final Rectangle rectangle2) {
        final int n = rectangle.getX() + rectangle.getWidth();
        final int x = rectangle.getX();
        final int n2 = rectangle.getY() + rectangle.getHeight();
        final int y = rectangle.getY();
        final int n3 = rectangle2.getX() + rectangle2.getWidth();
        final int x2 = rectangle2.getX();
        final int n4 = rectangle2.getY() + rectangle2.getHeight();
        final int y2 = rectangle2.getY();
        return n > x2 && n2 > y2 && x < n3 && y < n4;
    }
    
    private int DoHeight(final int n, final int n2, final int n3) {
        int n4 = 0;
        for (int i = n2; i < n3; ++i) {
            if (BooleanRectangleCollection.donemap[n][i]) {
                return n4;
            }
            ++n4;
        }
        return n4;
    }
    
    private int DoWidth(final int n, final int n2, final int n3, final int n4) {
        int n5 = 0;
        for (int i = n; i < n4; ++i) {
            for (int j = n2; j < n3; ++j) {
                if (BooleanRectangleCollection.donemap[i][j]) {
                    return n5;
                }
            }
            ++n5;
        }
        return n5;
    }
    
    private void DoRect(final int n, final int n2) {
    }
    
    static {
        BooleanRectangleCollection.donemap = new boolean[400][400];
        BooleanRectangleCollection.intersection = new Point();
        BooleanRectangleCollection.retWidth = 0;
        BooleanRectangleCollection.retHeight = 0;
    }
    
    public static class Point
    {
        public int X;
        public int Y;
        
        public Point() {
        }
        
        public Point(final int x, final int y) {
            this.X = x;
            this.Y = y;
        }
    }
    
    public class Line
    {
        public Point Start;
        public Point End;
        
        public Line(final Point point, final Point point2) {
            this.Start = new Point();
            this.End = new Point();
            this.Start.X = point.X;
            this.Start.Y = point.Y;
            this.End.X = point2.X;
            this.End.Y = point2.Y;
        }
    }
}
