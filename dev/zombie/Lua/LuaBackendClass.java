// 
// Decompiled by Procyon v0.5.36
// 

package zombie.Lua;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaTable;

public class LuaBackendClass implements KahluaTable
{
    KahluaTable table;
    KahluaTable typeTable;
    
    public String getString(final String s) {
        return (String)this.rawget(s);
    }
    
    public LuaBackendClass(final String s) {
        this.typeTable = (KahluaTable)LuaManager.env.rawget((Object)s);
    }
    
    public void callVoid(final String s) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget((Object)s), (Object)this.table);
    }
    
    public void callVoid(final String s, final Object o) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o });
    }
    
    public void callVoid(final String s, final Object o, final Object o2) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2 });
    }
    
    public void callVoid(final String s, final Object o, final Object o2, final Object o3) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3 });
    }
    
    public void callVoid(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4 });
    }
    
    public void callVoid(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4, o5 });
    }
    
    public Object call(final String s) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), (Object)this.table)[1];
    }
    
    public Object call(final String s, final Object o) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o })[1];
    }
    
    public Object call(final String s, final Object o, final Object o2) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2 })[1];
    }
    
    public Object call(final String s, final Object o, final Object o2, final Object o3) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3 })[1];
    }
    
    public Object call(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4 })[1];
    }
    
    public Object call(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4, o5 })[1];
    }
    
    public int callInt(final String s) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), (Object)this.table)[1]).intValue();
    }
    
    public int callInt(final String s, final Object o) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o })[1]).intValue();
    }
    
    public int callInt(final String s, final Object o, final Object o2) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2 })[1]).intValue();
    }
    
    public int callInt(final String s, final Object o, final Object o2, final Object o3) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3 })[1]).intValue();
    }
    
    public int callInt(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4 })[1]).intValue();
    }
    
    public int callInt(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4, o5 })[1]).intValue();
    }
    
    public float callFloat(final String s) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), (Object)this.table)[1]).floatValue();
    }
    
    public float callFloat(final String s, final Object o) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o })[1]).floatValue();
    }
    
    public float callFloat(final String s, final Object o, final Object o2) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2 })[1]).floatValue();
    }
    
    public float callFloat(final String s, final Object o, final Object o2, final Object o3) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3 })[1]).floatValue();
    }
    
    public float callFloat(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4 })[1]).floatValue();
    }
    
    public float callFloat(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4, o5 })[1]).floatValue();
    }
    
    public boolean callBool(final String s) {
        return (boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), (Object)this.table)[1];
    }
    
    public boolean callBool(final String s, final Object o) {
        return (boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o })[1];
    }
    
    public boolean callBool(final String s, final Object o, final Object o2) {
        return (boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2 })[1];
    }
    
    public boolean callBool(final String s, final Object o, final Object o2, final Object o3) {
        return (boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3 })[1];
    }
    
    public boolean callBool(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return (boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4 })[1];
    }
    
    public boolean callBool(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return (boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget((Object)s), new Object[] { this.table, o, o2, o3, o4, o5 })[1];
    }
    
    public void setMetatable(final KahluaTable metatable) {
        this.table.setMetatable(metatable);
    }
    
    public KahluaTable getMetatable() {
        return this.table.getMetatable();
    }
    
    public void rawset(final Object o, final Object o2) {
        this.table.rawset(o, o2);
    }
    
    public Object rawget(final Object o) {
        return this.table.rawget(o);
    }
    
    public void rawset(final int n, final Object o) {
        this.table.rawset(n, o);
    }
    
    public Object rawget(final int n) {
        return this.table.rawget(n);
    }
    
    public int len() {
        return this.table.len();
    }
    
    public int size() {
        return this.table.len();
    }
    
    public KahluaTableIterator iterator() {
        return this.table.iterator();
    }
    
    public boolean isEmpty() {
        return this.table.isEmpty();
    }
    
    public void wipe() {
        this.table.wipe();
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        this.table.save(byteBuffer);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.table.load(byteBuffer, n);
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        this.table.save(dataOutputStream);
    }
    
    public void load(final DataInputStream dataInputStream, final int n) throws IOException {
        this.table.load(dataInputStream, n);
    }
}
