// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public final class TilePropertyAliasMap
{
    public static final TilePropertyAliasMap instance;
    public final HashMap<String, Integer> PropertyToID;
    public final ArrayList<TileProperty> Properties;
    
    public TilePropertyAliasMap() {
        this.PropertyToID = new HashMap<String, Integer>();
        this.Properties = new ArrayList<TileProperty>();
    }
    
    public void Generate(final HashMap<String, ArrayList<String>> hashMap) {
        this.Properties.clear();
        this.PropertyToID.clear();
        for (final Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
            final String s = entry.getKey();
            final ArrayList<String> c = entry.getValue();
            this.PropertyToID.put(s, this.Properties.size());
            final TileProperty e = new TileProperty();
            this.Properties.add(e);
            e.propertyName = s;
            e.possibleValues.addAll(c);
            final ArrayList<String> possibleValues = e.possibleValues;
            for (int i = 0; i < possibleValues.size(); ++i) {
                e.idMap.put(possibleValues.get(i), i);
            }
        }
    }
    
    public int getIDFromPropertyName(final String s) {
        if (!this.PropertyToID.containsKey(s)) {
            return -1;
        }
        return this.PropertyToID.get(s);
    }
    
    public int getIDFromPropertyValue(final int index, final String s) {
        final TileProperty tileProperty = this.Properties.get(index);
        if (tileProperty.possibleValues.isEmpty()) {
            return 0;
        }
        if (!tileProperty.idMap.containsKey(s)) {
            return 0;
        }
        return tileProperty.idMap.get(s);
    }
    
    public String getPropertyValueString(final int index, final int index2) {
        final TileProperty tileProperty = this.Properties.get(index);
        if (tileProperty.possibleValues.isEmpty()) {
            return "";
        }
        return tileProperty.possibleValues.get(index2);
    }
    
    static {
        instance = new TilePropertyAliasMap();
    }
    
    public static final class TileProperty
    {
        public String propertyName;
        public final ArrayList<String> possibleValues;
        public final HashMap<String, Integer> idMap;
        
        public TileProperty() {
            this.possibleValues = new ArrayList<String>();
            this.idMap = new HashMap<String, Integer>();
        }
    }
}
