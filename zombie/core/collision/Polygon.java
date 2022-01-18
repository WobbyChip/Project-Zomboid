// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.collision;

import zombie.iso.Vector2;
import java.util.ArrayList;

public final class Polygon
{
    public ArrayList<Vector2> points;
    public ArrayList<Vector2> edges;
    float x;
    float y;
    float x2;
    float y2;
    Vector2[] vecs;
    Vector2[] eds;
    static Vector2 temp;
    
    public Polygon() {
        this.points = new ArrayList<Vector2>(4);
        this.edges = new ArrayList<Vector2>(4);
        this.x = 0.0f;
        this.y = 0.0f;
        this.x2 = 0.0f;
        this.y2 = 0.0f;
        this.vecs = new Vector2[4];
        this.eds = new Vector2[4];
    }
    
    public void Set(final float x, final float y, final float x2, final float y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.points.clear();
        if (this.vecs[0] == null) {
            for (int i = 0; i < 4; ++i) {
                this.vecs[i] = new Vector2();
                this.eds[i] = new Vector2();
            }
        }
        this.vecs[0].x = x;
        this.vecs[0].y = y;
        this.vecs[1].x = x2;
        this.vecs[1].y = y;
        this.vecs[2].x = x2;
        this.vecs[2].y = y2;
        this.vecs[3].x = x;
        this.vecs[3].y = y2;
        this.points.add(this.vecs[0]);
        this.points.add(this.vecs[1]);
        this.points.add(this.vecs[2]);
        this.points.add(this.vecs[3]);
        this.BuildEdges();
    }
    
    public Vector2 Center() {
        Polygon.temp.x = this.x + (this.x2 - this.x) / 2.0f;
        Polygon.temp.y = this.y + (this.y2 - this.y) / 2.0f;
        return Polygon.temp;
    }
    
    public void BuildEdges() {
        this.edges.clear();
        for (int i = 0; i < this.points.size(); ++i) {
            final Vector2 vector2 = this.points.get(i);
            Vector2 vector3;
            if (i + 1 >= this.points.size()) {
                vector3 = this.points.get(0);
            }
            else {
                vector3 = this.points.get(i + 1);
            }
            this.eds[i].x = vector3.x - vector2.x;
            this.eds[i].y = vector3.y - vector2.y;
            this.edges.add(this.eds[i]);
        }
    }
    
    public void Offset(final float n, final float n2) {
        for (int i = 0; i < this.points.size(); ++i) {
            final Vector2 vector3;
            final Vector2 vector2 = vector3 = this.points.get(i);
            vector3.x += n;
            final Vector2 vector4 = vector2;
            vector4.y += n2;
        }
    }
    
    public void Offset(final Vector2 vector2) {
        this.Offset(vector2.x, vector2.y);
    }
    
    static {
        Polygon.temp = new Vector2();
    }
}
