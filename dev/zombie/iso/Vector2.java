// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.awt.Dimension;
import zombie.core.math.PZMath;
import java.awt.Point;

public final class Vector2 implements Cloneable
{
    public float x;
    public float y;
    
    public Vector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }
    
    public Vector2(final Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }
    
    public Vector2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public static Vector2 fromAwtPoint(final Point point) {
        return new Vector2((float)point.x, (float)point.y);
    }
    
    public static Vector2 fromLengthDirection(final float n, final float n2) {
        final Vector2 vector2 = new Vector2();
        vector2.setLengthAndDirection(n2, n);
        return vector2;
    }
    
    public static float dot(final float n, final float n2, final float n3, final float n4) {
        return n * n3 + n2 * n4;
    }
    
    public static Vector2 addScaled(final Vector2 vector2, final Vector2 vector3, final float n, final Vector2 vector4) {
        vector4.set(vector2.x + vector3.x * n, vector2.y + vector3.y * n);
        return vector4;
    }
    
    public void rotate(final float n) {
        final double n2 = this.x * Math.cos(n) - this.y * Math.sin(n);
        final double n3 = this.x * Math.sin(n) + this.y * Math.cos(n);
        this.x = (float)n2;
        this.y = (float)n3;
    }
    
    public Vector2 add(final Vector2 vector2) {
        this.x += vector2.x;
        this.y += vector2.y;
        return this;
    }
    
    public Vector2 aimAt(final Vector2 vector2) {
        this.setLengthAndDirection(this.angleTo(vector2), this.getLength());
        return this;
    }
    
    public float angleTo(final Vector2 vector2) {
        return (float)Math.atan2(vector2.y - this.y, vector2.x - this.x);
    }
    
    public Vector2 clone() {
        return new Vector2(this);
    }
    
    public float distanceTo(final Vector2 vector2) {
        return (float)Math.sqrt(Math.pow(vector2.x - this.x, 2.0) + Math.pow(vector2.y - this.y, 2.0));
    }
    
    public float dot(final Vector2 vector2) {
        return this.x * vector2.x + this.y * vector2.y;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Vector2) {
            final Vector2 vector2 = (Vector2)o;
            return vector2.x == this.x && vector2.y == this.y;
        }
        return false;
    }
    
    public float getDirection() {
        return PZMath.wrap((float)Math.atan2(this.y, this.x), -3.1415927f, 3.1415927f);
    }
    
    public static float getDirection(final float n, final float n2) {
        return PZMath.wrap((float)Math.atan2(n2, n), -3.1415927f, 3.1415927f);
    }
    
    @Deprecated
    public float getDirectionNeg() {
        return (float)Math.atan2(this.x, this.y);
    }
    
    public Vector2 setDirection(final float n) {
        this.setLengthAndDirection(n, this.getLength());
        return this;
    }
    
    public float getLength() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public float getLengthSquared() {
        return this.x * this.x + this.y * this.y;
    }
    
    public Vector2 setLength(final float n) {
        this.normalize();
        this.x *= n;
        this.y *= n;
        return this;
    }
    
    public float normalize() {
        final float length = this.getLength();
        if (length == 0.0f) {
            this.x = 0.0f;
            this.y = 0.0f;
        }
        else {
            this.x /= length;
            this.y /= length;
        }
        return length;
    }
    
    public Vector2 set(final Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
        return this;
    }
    
    public Vector2 set(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Vector2 setLengthAndDirection(final float n, final float n2) {
        this.x = (float)(Math.cos(n) * n2);
        this.y = (float)(Math.sin(n) * n2);
        return this;
    }
    
    public Dimension toAwtDimension() {
        return new Dimension((int)this.x, (int)this.y);
    }
    
    public Point toAwtPoint() {
        return new Point((int)this.x, (int)this.y);
    }
    
    @Override
    public String toString() {
        return String.format("Vector2 (X: %f, Y: %f) (L: %f, D:%f)", this.x, this.y, this.getLength(), this.getDirection());
    }
    
    public float getX() {
        return this.x;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public void tangent() {
        final double n = this.x * Math.cos(Math.toRadians(90.0)) - this.y * Math.sin(Math.toRadians(90.0));
        final double n2 = this.x * Math.sin(Math.toRadians(90.0)) + this.y * Math.cos(Math.toRadians(90.0));
        this.x = (float)n;
        this.y = (float)n2;
    }
    
    public void scale(final float n) {
        scale(this, n);
    }
    
    public static Vector2 scale(final Vector2 vector2, final float n) {
        vector2.x *= n;
        vector2.y *= n;
        return vector2;
    }
}
