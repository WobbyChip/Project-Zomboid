// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.Vector2;

public class NetworkCharacter
{
    float minMovement;
    float maxMovement;
    long deltaTime;
    public final Transform transform;
    final Vector2 movement;
    final Point d1;
    final Point d2;
    
    public NetworkCharacter() {
        this.transform = new Transform();
        this.movement = new Vector2();
        this.d1 = new Point();
        this.d2 = new Point();
        this.minMovement = 0.075f;
        this.maxMovement = 0.5f;
        this.deltaTime = 10L;
    }
    
    NetworkCharacter(final float minMovement, final float maxMovement, final long deltaTime) {
        this.transform = new Transform();
        this.movement = new Vector2();
        this.d1 = new Point();
        this.d2 = new Point();
        this.minMovement = minMovement;
        this.maxMovement = maxMovement;
        this.deltaTime = deltaTime;
    }
    
    public void updateTransform(final float x, final float y, final float x2, final float y2) {
        this.transform.position.x = x;
        this.transform.position.y = y;
        this.transform.rotation.x = x2;
        this.transform.rotation.y = y2;
    }
    
    public void updateInterpolationPoint(final int t, final float px, final float py, final float rx, final float ry) {
        if (this.d2.t == 0) {
            this.updateTransform(px, py, rx, ry);
        }
        this.d2.t = t;
        this.d2.px = px;
        this.d2.py = py;
        this.d2.rx = rx;
        this.d2.ry = ry;
    }
    
    public void updatePointInternal(final float px, final float py, final float rx, final float ry) {
        this.d1.px = px;
        this.d1.py = py;
        this.d1.rx = rx;
        this.d1.ry = ry;
    }
    
    public void updateExtrapolationPoint(final int t, final float px, final float py, final float rx, final float ry) {
        if (t > this.d1.t) {
            this.d2.t = this.d1.t;
            this.d2.px = this.d1.px;
            this.d2.py = this.d1.py;
            this.d2.rx = this.d1.rx;
            this.d2.ry = this.d1.ry;
            this.d1.t = t;
            this.d1.px = px;
            this.d1.py = py;
            this.d1.rx = rx;
            this.d1.ry = ry;
        }
    }
    
    void extrapolate(final int n) {
        final float n2 = (n - this.d1.t) / (float)(this.d1.t - this.d2.t);
        final float n3 = this.d1.px - this.d2.px;
        final float n4 = this.d1.py - this.d2.py;
        this.movement.x = n3 * n2;
        this.movement.y = n4 * n2;
        if (n3 > this.minMovement || n4 > this.minMovement || -n3 > this.minMovement || -n4 > this.minMovement) {
            this.transform.moving = true;
            this.transform.rotation.x = this.movement.x;
            this.transform.rotation.y = this.movement.y;
            this.transform.rotation.normalize();
        }
        this.transform.position.x = this.d1.px + this.movement.x;
        this.transform.position.y = this.d1.py + this.movement.y;
        this.transform.operation = Operation.EXTRAPOLATION;
    }
    
    void extrapolateInternal(final int n, final float n2, final float n3) {
        final float n4 = (n - this.d1.t) / (float)(n - this.d1.t);
        final float n5 = n2 - this.d1.px;
        final float n6 = n3 - this.d1.py;
        this.movement.x = n5 * n4;
        this.movement.y = n6 * n4;
        if (this.movement.getLength() > this.maxMovement) {
            this.movement.setLength(this.maxMovement);
        }
        if (n5 > this.minMovement || n6 > this.minMovement || -n5 > this.minMovement || -n6 > this.minMovement) {
            this.transform.moving = true;
            this.transform.rotation.x = this.movement.x;
            this.transform.rotation.y = this.movement.y;
            this.transform.rotation.normalize();
        }
        this.transform.position.x = n2 + this.movement.x;
        this.transform.position.y = n3 + this.movement.y;
        this.transform.operation = Operation.EXTRAPOLATION;
    }
    
    void interpolate(final int n) {
        final float n2 = (n - this.d1.t) / (float)(this.d2.t - this.d1.t);
        final float n3 = this.d2.px - this.d1.px;
        final float n4 = this.d2.py - this.d1.py;
        this.movement.x = n3 * n2;
        this.movement.y = n4 * n2;
        if (this.movement.getLength() > this.maxMovement) {
            this.movement.setLength(this.maxMovement);
        }
        if (n3 > this.minMovement || n4 > this.minMovement || -n3 > this.minMovement || -n4 > this.minMovement) {
            this.transform.moving = true;
            this.transform.rotation.x = this.movement.x;
            this.transform.rotation.y = this.movement.y;
            this.transform.rotation.normalize();
        }
        this.transform.position.x = this.d1.px + this.movement.x;
        this.transform.position.y = this.d1.py + this.movement.y;
        this.transform.operation = Operation.INTERPOLATION;
    }
    
    public Transform predict(final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
        this.transform.moving = false;
        this.transform.operation = Operation.NONE;
        this.transform.time = n2 + n;
        this.updateExtrapolationPoint(n2, n3, n4, n5, n6);
        if (this.d1.t != 0 && this.d2.t != 0) {
            this.extrapolate(n + n2);
        }
        else {
            this.updateTransform(n3, n4, n5, n6);
        }
        return this.transform;
    }
    
    public Transform reconstruct(final int n, final float n2, final float n3, final float n4, final float n5) {
        this.transform.moving = false;
        this.transform.operation = Operation.NONE;
        if (this.d2.t != 0) {
            if (n + this.deltaTime <= this.d2.t) {
                this.updatePointInternal(n2, n3, n4, n5);
                if (this.d1.t != 0 && this.d1.t != n) {
                    this.interpolate(n);
                }
                this.d1.t = n;
            }
            else if (n > this.d2.t && n < this.d2.t + 2000) {
                this.extrapolateInternal(n, n2, n3);
                this.updatePointInternal(n2, n3, n4, n5);
                this.d1.t = n;
            }
        }
        return this.transform;
    }
    
    public void checkReset(final int n) {
        if (n > 2000 + this.d2.t) {
            this.reset();
        }
    }
    
    public void checkResetPlayer(final int n) {
        if (n > 2000 + this.d1.t) {
            this.reset();
        }
    }
    
    public void reset() {
        this.d1.t = 0;
        this.d1.px = 0.0f;
        this.d1.py = 0.0f;
        this.d1.rx = 0.0f;
        this.d1.ry = 0.0f;
        this.d2.t = 0;
        this.d2.px = 0.0f;
        this.d2.py = 0.0f;
        this.d2.rx = 0.0f;
        this.d2.ry = 0.0f;
    }
    
    public static class Transform
    {
        public Vector2 position;
        public Vector2 rotation;
        public Operation operation;
        public boolean moving;
        public int time;
        
        public Transform() {
            this.position = new Vector2();
            this.rotation = new Vector2();
            this.operation = Operation.NONE;
            this.moving = false;
            this.time = 0;
        }
    }
    
    public enum Operation
    {
        INTERPOLATION, 
        EXTRAPOLATION, 
        NONE;
        
        private static /* synthetic */ Operation[] $values() {
            return new Operation[] { Operation.INTERPOLATION, Operation.EXTRAPOLATION, Operation.NONE };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    static class Point
    {
        public float px;
        public float py;
        public float rx;
        public float ry;
        public int t;
        
        Point() {
            this.px = 0.0f;
            this.py = 0.0f;
            this.rx = 0.0f;
            this.ry = 0.0f;
            this.t = 0;
        }
    }
}
