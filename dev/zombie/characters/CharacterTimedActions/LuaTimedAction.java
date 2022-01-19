// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.CharacterTimedActions;

import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import se.krka.kahlua.vm.KahluaTable;

public final class LuaTimedAction extends BaseAction
{
    KahluaTable table;
    public static Object[] statObj;
    
    public LuaTimedAction(final KahluaTable table, final IsoGameCharacter isoGameCharacter) {
        super(isoGameCharacter);
        this.table = table;
        this.MaxTime = (int)LuaManager.converterManager.fromLuaToJava(table.rawget((Object)"maxTime"), (Class)Integer.class);
        final Object rawget = table.rawget((Object)"stopOnWalk");
        final Object rawget2 = table.rawget((Object)"stopOnRun");
        final Object rawget3 = table.rawget((Object)"stopOnAim");
        table.rawget((Object)"onUpdateFunc");
        if (rawget != null) {
            this.StopOnWalk = (boolean)LuaManager.converterManager.fromLuaToJava(rawget, (Class)Boolean.class);
        }
        if (rawget2 != null) {
            this.StopOnRun = (boolean)LuaManager.converterManager.fromLuaToJava(rawget2, (Class)Boolean.class);
        }
        if (rawget3 != null) {
            this.StopOnAim = (boolean)LuaManager.converterManager.fromLuaToJava(rawget3, (Class)Boolean.class);
        }
    }
    
    @Override
    public void update() {
        LuaTimedAction.statObj[0] = this.table.rawget((Object)"character");
        LuaTimedAction.statObj[1] = this.table.rawget((Object)"param1");
        LuaTimedAction.statObj[2] = this.table.rawget((Object)"param2");
        LuaTimedAction.statObj[3] = this.table.rawget((Object)"param3");
        LuaTimedAction.statObj[4] = this.table.rawget((Object)"param4");
        LuaTimedAction.statObj[5] = this.table.rawget((Object)"param5");
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget((Object)"onUpdateFunc"), LuaTimedAction.statObj);
        super.update();
    }
    
    @Override
    public boolean valid() {
        final Object[] pcall = LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"isValidFunc"), new Object[] { this.table.rawget((Object)"character"), this.table.rawget((Object)"param1"), this.table.rawget((Object)"param2"), this.table.rawget((Object)"param3"), this.table.rawget((Object)"param4"), this.table.rawget((Object)"param5") });
        return pcall.length > 0 && (boolean)pcall[0];
    }
    
    @Override
    public void start() {
        super.start();
        this.CurrentTime = 0.0f;
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"startFunc"), new Object[] { this.table.rawget((Object)"character"), this.table.rawget((Object)"param1"), this.table.rawget((Object)"param2"), this.table.rawget((Object)"param3"), this.table.rawget((Object)"param4"), this.table.rawget((Object)"param5") });
    }
    
    @Override
    public void stop() {
        super.stop();
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"onStopFunc"), new Object[] { this.table.rawget((Object)"character"), this.table.rawget((Object)"param1"), this.table.rawget((Object)"param2"), this.table.rawget((Object)"param3"), this.table.rawget((Object)"param4"), this.table.rawget((Object)"param5") });
    }
    
    @Override
    public void perform() {
        super.perform();
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"performFunc"), new Object[] { this.table.rawget((Object)"character"), this.table.rawget((Object)"param1"), this.table.rawget((Object)"param2"), this.table.rawget((Object)"param3"), this.table.rawget((Object)"param4"), this.table.rawget((Object)"param5") });
    }
    
    static {
        LuaTimedAction.statObj = new Object[6];
    }
}
