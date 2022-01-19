// 
// Decompiled by Procyon v0.5.36
// 

package zombie.Lua;

import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.Platform;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import java.util.ArrayList;

public final class Event
{
    public static final int ADD = 0;
    public static final int NUM_FUNCTIONS = 1;
    private final Add add;
    private final Remove remove;
    public final ArrayList<LuaClosure> callbacks;
    public String name;
    private int index;
    
    public boolean trigger(final KahluaTable kahluaTable, final LuaCaller luaCaller, final Object[] array) {
        if (this.callbacks.isEmpty()) {
            return false;
        }
        if (DebugOptions.instance.Checks.SlowLuaEvents.getValue()) {
            for (int i = 0; i < this.callbacks.size(); ++i) {
                try {
                    final LuaClosure luaClosure = this.callbacks.get(i);
                    final long nanoTime = System.nanoTime();
                    luaCaller.protectedCallVoid(LuaManager.thread, (Object)luaClosure, array);
                    final double n = (System.nanoTime() - nanoTime) / 1000000.0;
                    if (n > 250.0) {
                        DebugLog.Lua.warn("SLOW Lua event callback %s %s %dms", luaClosure.prototype.file, luaClosure, (int)n);
                    }
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
            return true;
        }
        for (int j = 0; j < this.callbacks.size(); ++j) {
            try {
                luaCaller.protectedCallVoid(LuaManager.thread, (Object)this.callbacks.get(j), array);
            }
            catch (Exception ex2) {
                ExceptionLogger.logException(ex2);
            }
        }
        return true;
    }
    
    public Event(final String name, final int index) {
        this.callbacks = new ArrayList<LuaClosure>();
        this.index = 0;
        this.index = index;
        this.name = name;
        this.add = new Add(this);
        this.remove = new Remove(this);
    }
    
    public void register(final Platform platform, final KahluaTable kahluaTable) {
        final KahluaTable table = platform.newTable();
        table.rawset((Object)"Add", (Object)this.add);
        table.rawset((Object)"Remove", (Object)this.remove);
        kahluaTable.rawset((Object)this.name, (Object)table);
    }
    
    public static final class Add implements JavaFunction
    {
        Event e;
        
        public Add(final Event e) {
            this.e = e;
        }
        
        public int call(final LuaCallFrame luaCallFrame, final int n) {
            if (LuaCompiler.rewriteEvents) {
                return 0;
            }
            final Object value = luaCallFrame.get(0);
            if (this.e.name.contains("CreateUI")) {}
            if (value instanceof LuaClosure) {
                this.e.callbacks.add((LuaClosure)value);
            }
            return 0;
        }
    }
    
    public static final class Remove implements JavaFunction
    {
        Event e;
        
        public Remove(final Event e) {
            this.e = e;
        }
        
        public int call(final LuaCallFrame luaCallFrame, final int n) {
            if (LuaCompiler.rewriteEvents) {
                return 0;
            }
            final Object value = luaCallFrame.get(0);
            if (value instanceof LuaClosure) {
                this.e.callbacks.remove(value);
            }
            return 0;
        }
    }
}
