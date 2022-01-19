// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.CharacterTimedActions;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.math.PZMath;
import zombie.ai.astar.Path;
import zombie.ai.astar.Mover;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ai.astar.IPathfinder;

public final class LuaTimedActionNew extends BaseAction implements IPathfinder
{
    KahluaTable table;
    
    public LuaTimedActionNew(final KahluaTable table, final IsoGameCharacter isoGameCharacter) {
        super(isoGameCharacter);
        this.table = table;
        this.MaxTime = (int)LuaManager.converterManager.fromLuaToJava(table.rawget((Object)"maxTime"), (Class)Integer.class);
        final Object rawget = table.rawget((Object)"stopOnWalk");
        final Object rawget2 = table.rawget((Object)"stopOnRun");
        final Object rawget3 = table.rawget((Object)"stopOnAim");
        final Object rawget4 = table.rawget((Object)"caloriesModifier");
        final Object rawget5 = table.rawget((Object)"useProgressBar");
        final Object rawget6 = table.rawget((Object)"forceProgressBar");
        final Object rawget7 = table.rawget((Object)"loopedAction");
        if (rawget != null) {
            this.StopOnWalk = (boolean)LuaManager.converterManager.fromLuaToJava(rawget, (Class)Boolean.class);
        }
        if (rawget2 != null) {
            this.StopOnRun = (boolean)LuaManager.converterManager.fromLuaToJava(rawget2, (Class)Boolean.class);
        }
        if (rawget3 != null) {
            this.StopOnAim = (boolean)LuaManager.converterManager.fromLuaToJava(rawget3, (Class)Boolean.class);
        }
        if (rawget4 != null) {
            this.caloriesModifier = (float)LuaManager.converterManager.fromLuaToJava(rawget4, (Class)Float.class);
        }
        if (rawget5 != null) {
            this.UseProgressBar = (boolean)LuaManager.converterManager.fromLuaToJava(rawget5, (Class)Boolean.class);
        }
        if (rawget6 != null) {
            this.ForceProgressBar = (boolean)LuaManager.converterManager.fromLuaToJava(rawget6, (Class)Boolean.class);
        }
        if (rawget7 != null) {
            this.loopAction = (boolean)LuaManager.converterManager.fromLuaToJava(rawget7, (Class)Boolean.class);
        }
    }
    
    @Override
    public void waitToStart() {
        if (LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.table.rawget((Object)"waitToStart"), (Object)this.table) == Boolean.FALSE) {
            super.waitToStart();
        }
    }
    
    @Override
    public void update() {
        super.update();
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget((Object)"update"), (Object)this.table);
    }
    
    @Override
    public boolean valid() {
        final Object[] pcall = LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"isValid"), (Object)this.table);
        return pcall.length > 1 && pcall[1] instanceof Boolean && (boolean)pcall[1];
    }
    
    @Override
    public void start() {
        super.start();
        this.CurrentTime = 0.0f;
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"start"), (Object)this.table);
    }
    
    @Override
    public void stop() {
        super.stop();
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"stop"), (Object)this.table);
    }
    
    @Override
    public void perform() {
        super.perform();
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget((Object)"perform"), (Object)this.table);
    }
    
    @Override
    public void Failed(final Mover mover) {
        this.table.rawset((Object)"path", (Object)null);
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget((Object)"failedPathfind"), (Object)this.table);
    }
    
    @Override
    public void Succeeded(final Path path, final Mover mover) {
        this.table.rawset((Object)"path", (Object)path);
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget((Object)"succeededPathfind"), (Object)this.table);
    }
    
    public void Pathfind(final IsoGameCharacter isoGameCharacter, final int n, final int n2, final int n3) {
    }
    
    @Override
    public String getName() {
        return "timedActionPathfind";
    }
    
    public void setCurrentTime(final float n) {
        this.CurrentTime = PZMath.clamp(n, 0.0f, (float)this.MaxTime);
    }
    
    public void setTime(final int maxTime) {
        this.MaxTime = maxTime;
    }
    
    @Override
    public void OnAnimEvent(final AnimEvent animEvent) {
        final Object rawget = this.table.rawget((Object)"animEvent");
        if (rawget != null) {
            LuaManager.caller.pcallvoid(LuaManager.thread, rawget, (Object)this.table, (Object)animEvent.m_EventName, (Object)animEvent.m_ParameterValue);
        }
    }
    
    public String getMetaType() {
        if (this.table != null && this.table.getMetatable() != null) {
            return this.table.getMetatable().getString("Type");
        }
        return "";
    }
}
