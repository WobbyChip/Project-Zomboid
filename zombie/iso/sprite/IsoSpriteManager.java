// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import zombie.debug.DebugLog;
import zombie.core.Color;
import zombie.iso.SpriteDetails.IsoFlagType;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.HashMap;

public final class IsoSpriteManager
{
    public static final IsoSpriteManager instance;
    public final HashMap<String, IsoSprite> NamedMap;
    public final TIntObjectHashMap<IsoSprite> IntMap;
    private final IsoSprite emptySprite;
    
    public IsoSpriteManager() {
        this.NamedMap = new HashMap<String, IsoSprite>();
        this.IntMap = (TIntObjectHashMap<IsoSprite>)new TIntObjectHashMap();
        this.emptySprite = new IsoSprite(this);
        final IsoSprite emptySprite = this.emptySprite;
        emptySprite.name = "";
        emptySprite.ID = -1;
        emptySprite.Properties.Set(IsoFlagType.invisible);
        emptySprite.CurrentAnim = new IsoAnim();
        emptySprite.CurrentAnim.ID = emptySprite.AnimStack.size();
        emptySprite.AnimStack.add(emptySprite.CurrentAnim);
        emptySprite.AnimMap.put("default", emptySprite.CurrentAnim);
        this.NamedMap.put(emptySprite.name, emptySprite);
    }
    
    public void Dispose() {
        IsoSprite.DisposeAll();
        IsoAnim.DisposeAll();
        final Object[] values = this.IntMap.values();
        for (int i = 0; i < values.length; ++i) {
            final IsoSprite isoSprite = (IsoSprite)values[i];
            isoSprite.Dispose();
            isoSprite.def = null;
            isoSprite.parentManager = null;
        }
        this.IntMap.clear();
        this.NamedMap.clear();
        this.NamedMap.put(this.emptySprite.name, this.emptySprite);
    }
    
    public IsoSprite getSprite(final int n) {
        if (this.IntMap.containsKey(n)) {
            return (IsoSprite)this.IntMap.get(n);
        }
        return null;
    }
    
    public IsoSprite getSprite(final String s) {
        if (this.NamedMap.containsKey(s)) {
            return this.NamedMap.get(s);
        }
        return this.AddSprite(s);
    }
    
    public IsoSprite getOrAddSpriteCache(final String key) {
        if (this.NamedMap.containsKey(key)) {
            return this.NamedMap.get(key);
        }
        final IsoSprite value = new IsoSprite(this);
        value.LoadFramesNoDirPageSimple(key);
        this.NamedMap.put(key, value);
        return value;
    }
    
    public IsoSprite getOrAddSpriteCache(final String s, final Color color) {
        final String key = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, s, (int)(color.r * 255.0f), (int)(color.g * 255.0f), (int)(color.b * 255.0f));
        if (this.NamedMap.containsKey(key)) {
            return this.NamedMap.get(key);
        }
        final IsoSprite value = new IsoSprite(this);
        value.LoadFramesNoDirPageSimple(s);
        this.NamedMap.put(key, value);
        return value;
    }
    
    public IsoSprite AddSprite(final String key) {
        final IsoSprite value = new IsoSprite(this);
        value.LoadFramesNoDirPageSimple(key);
        this.NamedMap.put(key, value);
        return value;
    }
    
    public IsoSprite AddSprite(final String s, int id) {
        final IsoSprite value = new IsoSprite(this);
        value.LoadFramesNoDirPageSimple(s);
        if (this.NamedMap.containsKey(s)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, s, id, this.NamedMap.get(s).ID));
            id = this.NamedMap.get(s).ID;
        }
        this.NamedMap.put(s, value);
        value.ID = id;
        this.IntMap.put(id, (Object)value);
        return value;
    }
    
    static {
        instance = new IsoSpriteManager();
    }
}
