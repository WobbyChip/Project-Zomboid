// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import java.util.ArrayList;

public class StrokeGeometry
{
    static Point s_firstPoint;
    static Point s_lastPoint;
    static final double EPSILON = 1.0E-4;
    
    static Point newPoint(final double n, final double n2) {
        if (StrokeGeometry.s_firstPoint == null) {
            return new Point(n, n2);
        }
        final Point s_firstPoint = StrokeGeometry.s_firstPoint;
        StrokeGeometry.s_firstPoint = StrokeGeometry.s_firstPoint.next;
        if (StrokeGeometry.s_lastPoint == s_firstPoint) {
            StrokeGeometry.s_lastPoint = null;
        }
        s_firstPoint.next = null;
        return s_firstPoint.set(n, n2);
    }
    
    static void release(final Point point) {
        if (point.next != null || point == StrokeGeometry.s_lastPoint) {
            return;
        }
        point.next = StrokeGeometry.s_firstPoint;
        StrokeGeometry.s_firstPoint = point;
        if (StrokeGeometry.s_lastPoint == null) {
            StrokeGeometry.s_lastPoint = point;
        }
    }
    
    static void release(final ArrayList<Point> list) {
        for (int i = 0; i < list.size(); ++i) {
            release(list.get(i));
        }
    }
    
    static ArrayList<Point> getStrokeGeometry(final Point[] array, final Attrs attrs) {
        if (array.length < 2) {
            return null;
        }
        final String cap = attrs.cap;
        final String join = attrs.join;
        final float n = attrs.width / 2.0f;
        final float miterLimit = attrs.miterLimit;
        final ArrayList<Point> list = new ArrayList<Point>();
        final ArrayList<Point> list2 = new ArrayList<Point>();
        final boolean b = false;
        if (array.length == 2) {
            createTriangles(array[0], Point.Middle(array[0], array[1]), array[1], list, n, "bevel", miterLimit);
        }
        else {
            for (int i = 0; i < array.length - 1; ++i) {
                if (i == 0) {
                    list2.add(array[0]);
                }
                else if (i == array.length - 2) {
                    list2.add(array[array.length - 1]);
                }
                else {
                    list2.add(Point.Middle(array[i], array[i + 1]));
                }
            }
            for (int j = 1; j < list2.size(); ++j) {
                createTriangles(list2.get(j - 1), array[j], list2.get(j), list, n, join, miterLimit);
            }
        }
        if (!b) {
            if (cap.equals("round")) {
                final Point point = list.get(0);
                final Point point2 = list.get(1);
                final Point point3 = array[1];
                final Point point4 = list.get(list.size() - 1);
                final Point point5 = list.get(list.size() - 3);
                final Point point6 = array[array.length - 2];
                createRoundCap(array[0], point, point2, point3, list);
                createRoundCap(array[array.length - 1], point4, point5, point6, list);
            }
            else if (cap.equals("square")) {
                final Point point7 = list.get(list.size() - 1);
                final Point point8 = list.get(list.size() - 3);
                createSquareCap(list.get(0), list.get(1), Point.Sub(array[0], array[1]).normalize().scalarMult(Point.Sub(array[0], list.get(0)).length()), list);
                createSquareCap(point7, point8, Point.Sub(array[array.length - 1], array[array.length - 2]).normalize().scalarMult(Point.Sub(point8, array[array.length - 1]).length()), list);
            }
        }
        return list;
    }
    
    static void createSquareCap(final Point point, final Point e, final Point point2, final ArrayList<Point> list) {
        list.add(point);
        list.add(Point.Add(point, point2));
        list.add(Point.Add(e, point2));
        list.add(e);
        list.add(Point.Add(e, point2));
        list.add(point);
    }
    
    static void createRoundCap(final Point point, final Point point2, final Point point3, final Point point4, final ArrayList<Point> list) {
        final double length = Point.Sub(point, point2).length();
        double atan2 = Math.atan2(point3.y - point.y, point3.x - point.x);
        double atan3 = Math.atan2(point2.y - point.y, point2.x - point.x);
        final double n = atan2;
        if (atan3 > atan2) {
            if (atan3 - atan2 >= 3.141492653589793) {
                atan3 -= 6.283185307179586;
            }
        }
        else if (atan2 - atan3 >= 3.141492653589793) {
            atan2 -= 6.283185307179586;
        }
        double n2 = atan3 - atan2;
        if (Math.abs(n2) >= 3.141492653589793 && Math.abs(n2) <= 3.1416926535897933) {
            final Point sub = Point.Sub(point, point4);
            if (sub.x == 0.0) {
                if (sub.y > 0.0) {
                    n2 = -n2;
                }
            }
            else if (sub.x >= -1.0E-4) {
                n2 = -n2;
            }
        }
        int n3 = (int)(Math.abs(n2 * length) / 7.0);
        ++n3;
        final double n4 = n2 / n3;
        for (int i = 0; i < n3; ++i) {
            list.add(newPoint(point.x, point.y));
            list.add(newPoint(point.x + length * Math.cos(n + n4 * i), point.y + length * Math.sin(n + n4 * i)));
            list.add(newPoint(point.x + length * Math.cos(n + n4 * (1 + i)), point.y + length * Math.sin(n + n4 * (1 + i))));
        }
    }
    
    static double signedArea(final Point point, final Point point2, final Point point3) {
        return (point2.x - point.x) * (point3.y - point.y) - (point3.x - point.x) * (point2.y - point.y);
    }
    
    static Point lineIntersection(final Point point, final Point point2, final Point point3, final Point point4) {
        final double n = point2.y - point.y;
        final double n2 = point.x - point2.x;
        final double n3 = point4.y - point3.y;
        final double n4 = point3.x - point4.x;
        final double n5 = n * n4 - n3 * n2;
        if (n5 > -1.0E-4 && n5 < 1.0E-4) {
            return null;
        }
        final double n6 = n * point.x + n2 * point.y;
        final double n7 = n3 * point3.x + n4 * point3.y;
        return newPoint((n4 * n6 - n2 * n7) / n5, (n * n7 - n3 * n6) / n5);
    }
    
    static void createTriangles(final Point point, final Point e, final Point point2, final ArrayList<Point> list, final float n, final String s, final float n2) {
        final Point sub = Point.Sub(e, point);
        final Point sub2 = Point.Sub(point2, e);
        sub.perpendicular();
        sub2.perpendicular();
        if (signedArea(point, e, point2) > 0.0) {
            sub.invert();
            sub2.invert();
        }
        sub.normalize();
        sub2.normalize();
        sub.scalarMult(n);
        sub2.scalarMult(n);
        final Point lineIntersection = lineIntersection(Point.Add(sub, point), Point.Add(sub, e), Point.Add(sub2, point2), Point.Add(sub2, e));
        Point sub3 = null;
        double length = Double.MAX_VALUE;
        if (lineIntersection != null) {
            sub3 = Point.Sub(lineIntersection, e);
            length = sub3.length();
        }
        final double n3 = (int)(length / n);
        final double length2 = Point.Sub(point, e).length();
        final double length3 = Point.Sub(e, point2).length();
        if (length > length2 || length > length3) {
            list.add(Point.Add(point, sub));
            list.add(Point.Sub(point, sub));
            list.add(Point.Add(e, sub));
            list.add(Point.Sub(point, sub));
            list.add(Point.Add(e, sub));
            list.add(Point.Sub(e, sub));
            if (s.equals("round")) {
                createRoundCap(e, Point.Add(e, sub), Point.Add(e, sub2), point2, list);
            }
            else if (s.equals("bevel") || (s.equals("miter") && n3 >= n2)) {
                list.add(e);
                list.add(Point.Add(e, sub));
                list.add(Point.Add(e, sub2));
            }
            else if (s.equals("miter") && n3 < n2 && lineIntersection != null) {
                list.add(Point.Add(e, sub));
                list.add(e);
                list.add(lineIntersection);
                list.add(Point.Add(e, sub2));
                list.add(e);
                list.add(lineIntersection);
            }
            list.add(Point.Add(point2, sub2));
            list.add(Point.Sub(e, sub2));
            list.add(Point.Add(e, sub2));
            list.add(Point.Add(point2, sub2));
            list.add(Point.Sub(e, sub2));
            list.add(Point.Sub(point2, sub2));
        }
        else {
            list.add(Point.Add(point, sub));
            list.add(Point.Sub(point, sub));
            list.add(Point.Sub(e, sub3));
            list.add(Point.Add(point, sub));
            list.add(Point.Sub(e, sub3));
            list.add(Point.Add(e, sub));
            if (s.equals("round")) {
                final Point add = Point.Add(e, sub);
                final Point add2 = Point.Add(e, sub2);
                final Point sub4 = Point.Sub(e, sub3);
                list.add(add);
                list.add(e);
                list.add(sub4);
                createRoundCap(e, add, add2, sub4, list);
                list.add(e);
                list.add(add2);
                list.add(sub4);
            }
            else {
                if (s.equals("bevel") || (s.equals("miter") && n3 >= n2)) {
                    list.add(Point.Add(e, sub));
                    list.add(Point.Add(e, sub2));
                    list.add(Point.Sub(e, sub3));
                }
                if (s.equals("miter") && n3 < n2) {
                    list.add(lineIntersection);
                    list.add(Point.Add(e, sub));
                    list.add(Point.Add(e, sub2));
                }
            }
            list.add(Point.Add(point2, sub2));
            list.add(Point.Sub(e, sub3));
            list.add(Point.Add(e, sub2));
            list.add(Point.Add(point2, sub2));
            list.add(Point.Sub(e, sub3));
            list.add(Point.Sub(point2, sub2));
        }
    }
    
    static {
        StrokeGeometry.s_firstPoint = null;
        StrokeGeometry.s_lastPoint = null;
    }
    
    public static final class Point
    {
        double x;
        double y;
        Point next;
        
        Point() {
            this.x = 0.0;
            this.y = 0.0;
        }
        
        Point(final double x, final double y) {
            this.x = x;
            this.y = y;
        }
        
        Point set(final double x, final double y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        Point scalarMult(final double n) {
            this.x *= n;
            this.y *= n;
            return this;
        }
        
        Point perpendicular() {
            final double x = this.x;
            this.x = -this.y;
            this.y = x;
            return this;
        }
        
        Point invert() {
            this.x = -this.x;
            this.y = -this.y;
            return this;
        }
        
        double length() {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        }
        
        Point normalize() {
            final double length = this.length();
            this.x /= length;
            this.y /= length;
            return this;
        }
        
        double angle() {
            return this.y / this.x;
        }
        
        static double Angle(final Point point, final Point point2) {
            return Math.atan2(point2.x - point.x, point2.y - point.y);
        }
        
        static Point Add(final Point point, final Point point2) {
            return StrokeGeometry.newPoint(point.x + point2.x, point.y + point2.y);
        }
        
        static Point Sub(final Point point, final Point point2) {
            return StrokeGeometry.newPoint(point.x - point2.x, point.y - point2.y);
        }
        
        static Point Middle(final Point point, final Point point2) {
            return Add(point, point2).scalarMult(0.5);
        }
    }
    
    static class Attrs
    {
        String cap;
        String join;
        float width;
        float miterLimit;
        
        Attrs() {
            this.cap = "butt";
            this.join = "bevel";
            this.width = 1.0f;
            this.miterLimit = 10.0f;
        }
    }
}
