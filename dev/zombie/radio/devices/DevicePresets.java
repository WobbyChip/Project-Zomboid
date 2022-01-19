// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.devices;

import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.ArrayList;

public final class DevicePresets implements Cloneable
{
    protected int maxPresets;
    protected ArrayList<PresetEntry> presets;
    
    public DevicePresets() {
        this.maxPresets = 10;
        this.presets = new ArrayList<PresetEntry>();
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public KahluaTable getPresetsLua() {
        final KahluaTable table = LuaManager.platform.newTable();
        for (int i = 0; i < this.presets.size(); ++i) {
            final PresetEntry presetEntry = this.presets.get(i);
            final KahluaTable table2 = LuaManager.platform.newTable();
            table2.rawset((Object)"name", (Object)presetEntry.name);
            table2.rawset((Object)"frequency", (Object)presetEntry.frequency);
            table.rawset(i, (Object)table2);
        }
        return table;
    }
    
    public ArrayList<PresetEntry> getPresets() {
        return this.presets;
    }
    
    public void setPresets(final ArrayList<PresetEntry> presets) {
        this.presets = presets;
    }
    
    public int getMaxPresets() {
        return this.maxPresets;
    }
    
    public void setMaxPresets(final int maxPresets) {
        this.maxPresets = maxPresets;
    }
    
    public void addPreset(final String s, final int n) {
        if (this.presets.size() < this.maxPresets) {
            this.presets.add(new PresetEntry(s, n));
        }
    }
    
    public void removePreset(final int index) {
        if (this.presets.size() != 0 && index >= 0 && index < this.presets.size()) {
            this.presets.remove(index);
        }
    }
    
    public String getPresetName(final int index) {
        if (this.presets.size() != 0 && index >= 0 && index < this.presets.size()) {
            return this.presets.get(index).name;
        }
        return "";
    }
    
    public int getPresetFreq(final int index) {
        if (this.presets.size() != 0 && index >= 0 && index < this.presets.size()) {
            return this.presets.get(index).frequency;
        }
        return -1;
    }
    
    public void setPresetName(final int index, String name) {
        if (name == null) {
            name = "name-is-null";
        }
        if (this.presets.size() != 0 && index >= 0 && index < this.presets.size()) {
            this.presets.get(index).name = name;
        }
    }
    
    public void setPresetFreq(final int index, final int frequency) {
        if (this.presets.size() != 0 && index >= 0 && index < this.presets.size()) {
            this.presets.get(index).frequency = frequency;
        }
    }
    
    public void setPreset(final int index, String name, final int frequency) {
        if (name == null) {
            name = "name-is-null";
        }
        if (this.presets.size() != 0 && index >= 0 && index < this.presets.size()) {
            final PresetEntry presetEntry = this.presets.get(index);
            presetEntry.name = name;
            presetEntry.frequency = frequency;
        }
    }
    
    public void clearPresets() {
        this.presets.clear();
    }
    
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        byteBuffer.putInt(this.maxPresets);
        byteBuffer.putInt(this.presets.size());
        for (int i = 0; i < this.presets.size(); ++i) {
            final PresetEntry presetEntry = this.presets.get(i);
            GameWindow.WriteString(byteBuffer, presetEntry.name);
            byteBuffer.putInt(presetEntry.frequency);
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        if (n >= 69) {
            this.clearPresets();
            this.maxPresets = byteBuffer.getInt();
            for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
                final String readString = GameWindow.ReadString(byteBuffer);
                final int int2 = byteBuffer.getInt();
                if (this.presets.size() < this.maxPresets) {
                    this.presets.add(new PresetEntry(readString, int2));
                }
            }
        }
    }
}
