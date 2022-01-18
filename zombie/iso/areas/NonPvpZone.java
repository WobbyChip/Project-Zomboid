// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas;

import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.network.GameClient;
import java.util.ArrayList;

public final class NonPvpZone
{
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int size;
    private String title;
    public static final ArrayList<NonPvpZone> nonPvpZoneList;
    
    public NonPvpZone() {
    }
    
    public NonPvpZone(final String title, int x, int y, int x2, int y2) {
        if (x > x2) {
            final int n = x2;
            x2 = x;
            x = n;
        }
        if (y > y2) {
            final int n2 = y2;
            y2 = y;
            y = n2;
        }
        this.setX(x);
        this.setX2(x2);
        this.setY(y);
        this.setY2(y2);
        this.title = title;
        this.size = Math.abs(x - x2 + (y - y2));
        this.syncNonPvpZone(false);
    }
    
    public static NonPvpZone addNonPvpZone(final String s, final int n, final int n2, final int n3, final int n4) {
        final NonPvpZone e = new NonPvpZone(s, n, n2, n3, n4);
        NonPvpZone.nonPvpZoneList.add(e);
        return e;
    }
    
    public static void removeNonPvpZone(final String s, final boolean b) {
        final NonPvpZone zoneByTitle = getZoneByTitle(s);
        if (zoneByTitle != null) {
            NonPvpZone.nonPvpZoneList.remove(zoneByTitle);
            if (!b) {
                zoneByTitle.syncNonPvpZone(true);
            }
        }
    }
    
    public static NonPvpZone getZoneByTitle(final String anObject) {
        for (int i = 0; i < NonPvpZone.nonPvpZoneList.size(); ++i) {
            final NonPvpZone nonPvpZone = NonPvpZone.nonPvpZoneList.get(i);
            if (nonPvpZone.getTitle().equals(anObject)) {
                return nonPvpZone;
            }
        }
        return null;
    }
    
    public static NonPvpZone getNonPvpZone(final int n, final int n2) {
        for (int i = 0; i < NonPvpZone.nonPvpZoneList.size(); ++i) {
            final NonPvpZone nonPvpZone = NonPvpZone.nonPvpZoneList.get(i);
            if (n >= nonPvpZone.getX() && n < nonPvpZone.getX2() && n2 >= nonPvpZone.getY() && n2 < nonPvpZone.getY2()) {
                return nonPvpZone;
            }
        }
        return null;
    }
    
    public static ArrayList<NonPvpZone> getAllZones() {
        return NonPvpZone.nonPvpZoneList;
    }
    
    public void syncNonPvpZone(final boolean b) {
        if (GameClient.bClient) {
            GameClient.sendNonPvpZone(this, b);
        }
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.putInt(this.getX());
        byteBuffer.putInt(this.getY());
        byteBuffer.putInt(this.getX2());
        byteBuffer.putInt(this.getY2());
        byteBuffer.putInt(this.getSize());
        GameWindow.WriteString(byteBuffer, this.getTitle());
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) {
        this.setX(byteBuffer.getInt());
        this.setY(byteBuffer.getInt());
        this.setX2(byteBuffer.getInt());
        this.setY2(byteBuffer.getInt());
        this.setSize(byteBuffer.getInt());
        this.setTitle(GameWindow.ReadString(byteBuffer));
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getX2() {
        return this.x2;
    }
    
    public void setX2(final int x2) {
        this.x2 = x2;
    }
    
    public int getY2() {
        return this.y2;
    }
    
    public void setY2(final int y2) {
        this.y2 = y2;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setSize(final int size) {
        this.size = size;
    }
    
    static {
        nonPvpZoneList = new ArrayList<NonPvpZone>();
    }
}
