// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.properties;

import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import zombie.core.TilePropertyAliasMap;
import java.util.List;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.core.Collections.NonBlockingHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

public final class PropertyContainer
{
    private long SpriteFlags1;
    private long SpriteFlags2;
    private final TIntIntHashMap Properties;
    private int[] keyArray;
    public static NonBlockingHashMap<IsoFlagType, MostTested> test;
    public static List<Object> sorted;
    private byte Surface;
    private byte SurfaceFlags;
    private short StackReplaceTileOffset;
    private static final byte SURFACE_VALID = 1;
    private static final byte SURFACE_ISOFFSET = 2;
    private static final byte SURFACE_ISTABLE = 4;
    private static final byte SURFACE_ISTABLETOP = 8;
    
    public PropertyContainer() {
        this.SpriteFlags1 = 0L;
        this.SpriteFlags2 = 0L;
        this.Properties = new TIntIntHashMap();
    }
    
    public void CreateKeySet() {
        this.keyArray = this.Properties.keySet().toArray();
    }
    
    public void AddProperties(final PropertyContainer propertyContainer) {
        if (propertyContainer.keyArray == null) {
            return;
        }
        for (int i = 0; i < propertyContainer.keyArray.length; ++i) {
            final int n = propertyContainer.keyArray[i];
            this.Properties.put(n, propertyContainer.Properties.get(n));
        }
        this.SpriteFlags1 |= propertyContainer.SpriteFlags1;
        this.SpriteFlags2 |= propertyContainer.SpriteFlags2;
    }
    
    public void Clear() {
        this.SpriteFlags1 = 0L;
        this.SpriteFlags2 = 0L;
        this.Properties.clear();
        this.SurfaceFlags &= 0xFFFFFFFE;
    }
    
    public boolean Is(final IsoFlagType isoFlagType) {
        return (((isoFlagType.index() / 64 == 0) ? this.SpriteFlags1 : this.SpriteFlags2) & 1L << isoFlagType.index() % 64) != 0x0L;
    }
    
    public boolean Is(final Double n) {
        return this.Is(IsoFlagType.fromIndex(n.intValue()));
    }
    
    public void Set(final String s, final String s2) {
        this.Set(s, s2, true);
    }
    
    public void Set(final String s, final String s2, final boolean b) {
        if (s == null) {
            return;
        }
        if (b) {
            final IsoFlagType fromString = IsoFlagType.FromString(s);
            if (fromString != IsoFlagType.MAX) {
                this.Set(fromString);
                return;
            }
        }
        final int idFromPropertyName = TilePropertyAliasMap.instance.getIDFromPropertyName(s);
        if (idFromPropertyName == -1) {
            return;
        }
        final int idFromPropertyValue = TilePropertyAliasMap.instance.getIDFromPropertyValue(idFromPropertyName, s2);
        this.SurfaceFlags &= 0xFFFFFFFE;
        this.Properties.put(idFromPropertyName, idFromPropertyValue);
    }
    
    public void Set(final IsoFlagType isoFlagType) {
        if (isoFlagType.index() / 64 == 0) {
            this.SpriteFlags1 |= 1L << isoFlagType.index() % 64;
        }
        else {
            this.SpriteFlags2 |= 1L << isoFlagType.index() % 64;
        }
    }
    
    public void Set(final IsoFlagType isoFlagType, final String s) {
        this.Set(isoFlagType);
    }
    
    public void UnSet(final String s) {
        this.Properties.remove(TilePropertyAliasMap.instance.getIDFromPropertyName(s));
    }
    
    public void UnSet(final IsoFlagType isoFlagType) {
        if (isoFlagType.index() / 64 == 0) {
            this.SpriteFlags1 &= ~(1L << isoFlagType.index() % 64);
        }
        else {
            this.SpriteFlags2 &= ~(1L << isoFlagType.index() % 64);
        }
    }
    
    public String Val(final String s) {
        final int idFromPropertyName = TilePropertyAliasMap.instance.getIDFromPropertyName(s);
        if (!this.Properties.containsKey(idFromPropertyName)) {
            return null;
        }
        return TilePropertyAliasMap.instance.getPropertyValueString(idFromPropertyName, this.Properties.get(idFromPropertyName));
    }
    
    public boolean Is(final String s) {
        return this.Properties.containsKey(TilePropertyAliasMap.instance.getIDFromPropertyName(s));
    }
    
    public ArrayList<IsoFlagType> getFlagsList() {
        final ArrayList<IsoFlagType> list = new ArrayList<IsoFlagType>();
        for (int i = 0; i < 64; ++i) {
            if ((this.SpriteFlags1 & 1L << i) != 0x0L) {
                list.add(IsoFlagType.fromIndex(i));
            }
        }
        for (int j = 0; j < 64; ++j) {
            if ((this.SpriteFlags2 & 1L << j) != 0x0L) {
                list.add(IsoFlagType.fromIndex(64 + j));
            }
        }
        return list;
    }
    
    public ArrayList<String> getPropertyNames() {
        final ArrayList<Comparable> list = new ArrayList<Comparable>();
        this.Properties.keySet().forEach(index -> {
            list.add(TilePropertyAliasMap.instance.Properties.get(index).propertyName);
            return true;
        });
        Collections.sort(list);
        return (ArrayList<String>)list;
    }
    
    private void initSurface() {
        if ((this.SurfaceFlags & 0x1) != 0x0) {
            return;
        }
        this.Surface = 0;
        this.StackReplaceTileOffset = 0;
        this.SurfaceFlags = 1;
        this.Properties.forEachEntry((index, index2) -> {
            final TilePropertyAliasMap.TileProperty tileProperty = TilePropertyAliasMap.instance.Properties.get(index);
            final String propertyName = tileProperty.propertyName;
            final String s = tileProperty.possibleValues.get(index2);
            if ("Surface".equals(propertyName) && s != null) {
                try {
                    final int int1 = Integer.parseInt(s);
                    if (int1 >= 0 && int1 <= 128) {
                        this.Surface = (byte)int1;
                    }
                }
                catch (NumberFormatException ex) {}
            }
            else if ("IsSurfaceOffset".equals(propertyName)) {
                this.SurfaceFlags |= 0x2;
            }
            else if ("IsTable".equals(propertyName)) {
                this.SurfaceFlags |= 0x4;
            }
            else if ("IsTableTop".equals(propertyName)) {
                this.SurfaceFlags |= 0x8;
            }
            else if ("StackReplaceTileOffset".equals(propertyName)) {
                try {
                    this.StackReplaceTileOffset = (short)Integer.parseInt(s);
                }
                catch (NumberFormatException ex2) {}
            }
            return true;
        });
    }
    
    public int getSurface() {
        this.initSurface();
        return this.Surface;
    }
    
    public boolean isSurfaceOffset() {
        this.initSurface();
        return (this.SurfaceFlags & 0x2) != 0x0;
    }
    
    public boolean isTable() {
        this.initSurface();
        return (this.SurfaceFlags & 0x4) != 0x0;
    }
    
    public boolean isTableTop() {
        this.initSurface();
        return (this.SurfaceFlags & 0x8) != 0x0;
    }
    
    public int getStackReplaceTileOffset() {
        this.initSurface();
        return this.StackReplaceTileOffset;
    }
    
    static {
        PropertyContainer.test = new NonBlockingHashMap<IsoFlagType, MostTested>();
        PropertyContainer.sorted = Collections.synchronizedList(new ArrayList<Object>());
    }
    
    private static class ProfileEntryComparitor implements Comparator<Object>
    {
        public ProfileEntryComparitor() {
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            final double n = ((MostTested)o).count;
            final double n2 = ((MostTested)o2).count;
            if (n > n2) {
                return -1;
            }
            if (n2 > n) {
                return 1;
            }
            return 0;
        }
    }
    
    public static class MostTested
    {
        public IsoFlagType flag;
        public int count;
    }
}
