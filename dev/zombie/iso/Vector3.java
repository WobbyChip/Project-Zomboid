// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.awt.Dimension;
import java.awt.Point;

public final class Vector3 implements Cloneable
{
    public float x;
    public float y;
    public float z;
    
    public Vector3() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }
    
    public Vector3(final Vector3 vector3) {
        this.x = vector3.x;
        this.y = vector3.y;
        this.z = vector3.z;
    }
    
    public Vector3(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
    
    public void rotate(final float n) {
        final double n2 = this.x * Math.cos(n) - this.y * Math.sin(n);
        final double n3 = this.x * Math.sin(n) + this.y * Math.cos(n);
        this.x = (float)n2;
        this.y = (float)n3;
    }
    
    public void rotatey(final float n) {
        final double n2 = this.x * Math.cos(n) - this.z * Math.sin(n);
        final double n3 = this.x * Math.sin(n) + this.z * Math.cos(n);
        this.x = (float)n2;
        this.z = (float)n3;
    }
    
    public Vector2 add(final Vector2 vector2) {
        return new Vector2(this.x + vector2.x, this.y + vector2.y);
    }
    
    public Vector3 addToThis(final Vector2 vector2) {
        this.x += vector2.x;
        this.y += vector2.y;
        return this;
    }
    
    public Vector3 addToThis(final Vector3 vector3) {
        this.x += vector3.x;
        this.y += vector3.y;
        this.z += vector3.z;
        return this;
    }
    
    public Vector3 div(final float n) {
        this.x /= n;
        this.y /= n;
        this.z /= n;
        return this;
    }
    
    public Vector3 aimAt(final Vector2 vector2) {
        this.setLengthAndDirection(this.angleTo(vector2), this.getLength());
        return this;
    }
    
    public float angleTo(final Vector2 vector2) {
        return (float)Math.atan2(vector2.y - this.y, vector2.x - this.x);
    }
    
    public Vector3 clone() {
        return new Vector3(this);
    }
    
    public float distanceTo(final Vector2 vector2) {
        return (float)Math.sqrt(Math.pow(vector2.x - this.x, 2.0) + Math.pow(vector2.y - this.y, 2.0));
    }
    
    public float dot(final Vector2 vector2) {
        return this.x * vector2.x + this.y * vector2.y;
    }
    
    public float dot3d(final Vector3 vector3) {
        return this.x * vector3.x + this.y * vector3.y + this.z * vector3.z;
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
        return (float)Math.atan2(this.x, this.y);
    }
    
    public Vector3 setDirection(final float n) {
        this.setLengthAndDirection(n, this.getLength());
        return this;
    }
    
    public float getLength() {
        return (float)Math.sqrt(this.getLengthSq());
    }
    
    public float getLengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public Vector3 setLength(final float n) {
        this.normalize();
        this.x *= n;
        this.y *= n;
        this.z *= n;
        return this;
    }
    
    public void normalize() {
        final float length = this.getLength();
        if (length == 0.0f) {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        }
        else {
            this.x /= length;
            this.y /= length;
            this.z /= length;
        }
        this.getLength();
    }
    
    public Vector3 set(final Vector3 vector3) {
        this.x = vector3.x;
        this.y = vector3.y;
        this.z = vector3.z;
        return this;
    }
    
    public Vector3 set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    public Vector3 setLengthAndDirection(final float n, final float n2) {
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
    
    public Vector3 sub(final Vector3 vector3, final Vector3 vector4) {
        return sub(this, vector3, vector4);
    }
    
    public static Vector3 sub(final Vector3 vector3, final Vector3 vector4, final Vector3 vector5) {
        vector5.set(vector3.x - vector4.x, vector3.y - vector4.y, vector3.z - vector4.z);
        return vector5;
    }
}
