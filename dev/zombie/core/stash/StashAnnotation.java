// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.stash;

import zombie.util.Type;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;

public final class StashAnnotation
{
    public String symbol;
    public String text;
    public float x;
    public float y;
    public float r;
    public float g;
    public float b;
    
    public void fromLua(final KahluaTable kahluaTable) {
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTable;
        this.symbol = Type.tryCastTo(kahluaTable.rawget((Object)"symbol"), String.class);
        this.text = Type.tryCastTo(kahluaTable.rawget((Object)"text"), String.class);
        this.x = kahluaTableImpl.rawgetFloat((Object)"x");
        this.y = kahluaTableImpl.rawgetFloat((Object)"y");
        this.r = kahluaTableImpl.rawgetFloat((Object)"r");
        this.g = kahluaTableImpl.rawgetFloat((Object)"g");
        this.b = kahluaTableImpl.rawgetFloat((Object)"b");
    }
}
