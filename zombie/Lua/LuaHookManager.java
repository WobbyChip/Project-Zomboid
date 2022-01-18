// 
// Decompiled by Procyon v0.5.36
// 

package zombie.Lua;

import se.krka.kahlua.vm.LuaCallFrame;
import java.util.Iterator;
import zombie.debug.DebugLog;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.KahluaTable;
import java.util.HashMap;
import se.krka.kahlua.vm.LuaClosure;
import java.util.ArrayList;
import se.krka.kahlua.vm.JavaFunction;

public final class LuaHookManager implements JavaFunction
{
    public static final ArrayList<LuaClosure> OnTickCallbacks;
    static Object[] a;
    static Object[] b;
    static Object[] c;
    static Object[] d;
    static Object[] f;
    static Object[] g;
    private static final ArrayList<Event> EventList;
    private static final HashMap<String, Event> EventMap;
    
    public static boolean TriggerHook(final String s) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            final Event event = LuaHookManager.EventMap.get(s);
            LuaHookManager.a[0] = null;
            return event.trigger(LuaManager.env, LuaManager.caller, LuaHookManager.a);
        }
        return false;
    }
    
    public static boolean TriggerHook(final String s, final Object o) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            final Event event = LuaHookManager.EventMap.get(s);
            LuaHookManager.a[0] = o;
            return event.trigger(LuaManager.env, LuaManager.caller, LuaHookManager.a);
        }
        return false;
    }
    
    public static boolean TriggerHook(final String s, final Object o, final Object o2) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            final Event event = LuaHookManager.EventMap.get(s);
            LuaHookManager.b[0] = o;
            LuaHookManager.b[1] = o2;
            return event.trigger(LuaManager.env, LuaManager.caller, LuaHookManager.b);
        }
        return false;
    }
    
    public static boolean TriggerHook(final String s, final Object o, final Object o2, final Object o3) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            final Event event = LuaHookManager.EventMap.get(s);
            LuaHookManager.c[0] = o;
            LuaHookManager.c[1] = o2;
            LuaHookManager.c[2] = o3;
            return event.trigger(LuaManager.env, LuaManager.caller, LuaHookManager.c);
        }
        return false;
    }
    
    public static boolean TriggerHook(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            final Event event = LuaHookManager.EventMap.get(s);
            LuaHookManager.d[0] = o;
            LuaHookManager.d[1] = o2;
            LuaHookManager.d[2] = o3;
            LuaHookManager.d[3] = o4;
            return event.trigger(LuaManager.env, LuaManager.caller, LuaHookManager.d);
        }
        return false;
    }
    
    public static boolean TriggerHook(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            final Event event = LuaHookManager.EventMap.get(s);
            LuaHookManager.f[0] = o;
            LuaHookManager.f[1] = o2;
            LuaHookManager.f[2] = o3;
            LuaHookManager.f[3] = o4;
            LuaHookManager.f[4] = o5;
            return event.trigger(LuaManager.env, LuaManager.caller, LuaHookManager.f);
        }
        return false;
    }
    
    public static boolean TriggerHook(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            final Event event = LuaHookManager.EventMap.get(s);
            LuaHookManager.g[0] = o;
            LuaHookManager.g[1] = o2;
            LuaHookManager.g[2] = o3;
            LuaHookManager.g[3] = o4;
            LuaHookManager.g[4] = o5;
            LuaHookManager.g[5] = o6;
            return event.trigger(LuaManager.env, LuaManager.caller, LuaHookManager.g);
        }
        return false;
    }
    
    public static void AddEvent(final String s) {
        if (LuaHookManager.EventMap.containsKey(s)) {
            return;
        }
        final Event event = new Event(s, LuaHookManager.EventList.size());
        LuaHookManager.EventList.add(event);
        LuaHookManager.EventMap.put(s, event);
        final Object rawget = LuaManager.env.rawget((Object)"Hook");
        if (rawget instanceof KahluaTable) {
            event.register((Platform)LuaManager.platform, (KahluaTable)rawget);
        }
        else {
            DebugLog.log("ERROR: 'Hook' table not found or not a table");
        }
    }
    
    private static void AddEvents() {
        AddEvent("AutoDrink");
        AddEvent("UseItem");
        AddEvent("Attack");
        AddEvent("CalculateStats");
        AddEvent("WeaponHitCharacter");
        AddEvent("WeaponSwing");
        AddEvent("WeaponSwingHitPoint");
    }
    
    public static void clear() {
        LuaHookManager.a[0] = null;
        LuaHookManager.b[0] = null;
        LuaHookManager.b[1] = null;
        LuaHookManager.c[0] = null;
        LuaHookManager.c[1] = null;
        LuaHookManager.c[2] = null;
        LuaHookManager.d[0] = null;
        LuaHookManager.d[1] = null;
        LuaHookManager.d[2] = null;
        LuaHookManager.d[3] = null;
        LuaHookManager.f[0] = null;
        LuaHookManager.f[1] = null;
        LuaHookManager.f[2] = null;
        LuaHookManager.f[3] = null;
        LuaHookManager.f[4] = null;
        LuaHookManager.g[0] = null;
        LuaHookManager.g[1] = null;
        LuaHookManager.g[2] = null;
        LuaHookManager.g[3] = null;
        LuaHookManager.g[4] = null;
        LuaHookManager.g[5] = null;
    }
    
    public static void register(final Platform platform, final KahluaTable kahluaTable) {
        kahluaTable.rawset((Object)"Hook", (Object)platform.newTable());
        AddEvents();
    }
    
    public static void Reset() {
        final Iterator<Event> iterator = LuaHookManager.EventList.iterator();
        while (iterator.hasNext()) {
            iterator.next().callbacks.clear();
        }
        LuaHookManager.EventList.clear();
        LuaHookManager.EventMap.clear();
    }
    
    public int call(final LuaCallFrame luaCallFrame, final int n) {
        return 0;
    }
    
    private int OnTick(final LuaCallFrame luaCallFrame, final int n) {
        return 0;
    }
    
    static {
        OnTickCallbacks = new ArrayList<LuaClosure>();
        LuaHookManager.a = new Object[1];
        LuaHookManager.b = new Object[2];
        LuaHookManager.c = new Object[3];
        LuaHookManager.d = new Object[4];
        LuaHookManager.f = new Object[5];
        LuaHookManager.g = new Object[6];
        EventList = new ArrayList<Event>();
        EventMap = new HashMap<String, Event>();
    }
}
