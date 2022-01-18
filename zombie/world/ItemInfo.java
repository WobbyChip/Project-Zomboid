// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world;

import zombie.core.utils.Bits;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.FileWriter;
import zombie.debug.DebugLog;
import java.util.Collection;
import java.util.ArrayList;
import zombie.scripting.objects.Item;
import java.util.List;

public class ItemInfo
{
    protected String itemName;
    protected String moduleName;
    protected String fullType;
    protected short registryID;
    protected boolean existsAsVanilla;
    protected boolean isModded;
    protected String modID;
    protected boolean obsolete;
    protected boolean removed;
    protected boolean isLoaded;
    protected List<String> modOverrides;
    protected Item scriptItem;
    
    public ItemInfo() {
        this.existsAsVanilla = false;
        this.isModded = false;
        this.obsolete = false;
        this.removed = false;
        this.isLoaded = false;
    }
    
    public String getFullType() {
        return this.fullType;
    }
    
    public short getRegistryID() {
        return this.registryID;
    }
    
    public boolean isExistsAsVanilla() {
        return this.existsAsVanilla;
    }
    
    public boolean isModded() {
        return this.isModded;
    }
    
    public String getModID() {
        return this.modID;
    }
    
    public boolean isObsolete() {
        return this.obsolete;
    }
    
    public boolean isRemoved() {
        return this.removed;
    }
    
    public boolean isLoaded() {
        return this.isLoaded;
    }
    
    public Item getScriptItem() {
        return this.scriptItem;
    }
    
    public List<String> getModOverrides() {
        return this.modOverrides;
    }
    
    public ItemInfo copy() {
        final ItemInfo itemInfo = new ItemInfo();
        itemInfo.fullType = this.fullType;
        itemInfo.registryID = this.registryID;
        itemInfo.existsAsVanilla = this.existsAsVanilla;
        itemInfo.isModded = this.isModded;
        itemInfo.modID = this.modID;
        itemInfo.obsolete = this.obsolete;
        itemInfo.removed = this.removed;
        itemInfo.isLoaded = this.isLoaded;
        itemInfo.scriptItem = this.scriptItem;
        if (this.modOverrides != null) {
            (itemInfo.modOverrides = new ArrayList<String>()).addAll(this.modOverrides);
        }
        return itemInfo;
    }
    
    public boolean isValid() {
        return !this.obsolete && !this.removed && this.isLoaded;
    }
    
    public void DebugPrint() {
        DebugLog.log(this.GetDebugString());
    }
    
    public String GetDebugString() {
        final String s = invokedynamic(makeConcatWithConstants:(SLjava/lang/String;Ljava/lang/String;ZZZZI)Ljava/lang/String;, this.registryID, this.fullType, this.modID, this.existsAsVanilla, this.isModded, this.obsolete, this.removed, (this.modOverrides != null) ? this.modOverrides.size() : 0);
        if (this.modOverrides != null) {
            String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            if (this.existsAsVanilla) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
            }
            for (int i = 0; i < this.modOverrides.size(); ++i) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, (String)this.modOverrides.get(i));
                if (i < this.modOverrides.size() - 1) {
                    s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
                }
            }
        }
        // invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2)
        return "===================================\n";
    }
    
    public String ToString() {
        return invokedynamic(makeConcatWithConstants:(SLjava/lang/String;Ljava/lang/String;ZZZZI)Ljava/lang/String;, this.registryID, this.fullType, this.modID, this.existsAsVanilla, this.isModded, this.obsolete, this.removed, (this.modOverrides != null) ? this.modOverrides.size() : 0);
    }
    
    protected void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SLjava/lang/String;)Ljava/lang/String;, s, this.registryID, System.lineSeparator()));
        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.fullType, System.lineSeparator()));
        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.modID, System.lineSeparator()));
        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ZLjava/lang/String;)Ljava/lang/String;, s, this.existsAsVanilla, System.lineSeparator()));
        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ZLjava/lang/String;)Ljava/lang/String;, s, this.isModded, System.lineSeparator()));
        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ZLjava/lang/String;)Ljava/lang/String;, s, this.obsolete, System.lineSeparator()));
        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ZLjava/lang/String;)Ljava/lang/String;, s, this.removed, System.lineSeparator()));
        if (this.modOverrides != null) {
            String s2 = "modOverrides = { ";
            for (int i = 0; i < this.modOverrides.size(); ++i) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, (String)this.modOverrides.get(i));
                if (i < this.modOverrides.size() - 1) {
                    s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
                }
            }
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2), System.lineSeparator()));
        }
    }
    
    protected void save(final ByteBuffer byteBuffer, final List<String> list, final List<String> list2) {
        byteBuffer.putShort(this.registryID);
        if (list2.size() > 127) {
            byteBuffer.putShort((short)list2.indexOf(this.moduleName));
        }
        else {
            byteBuffer.put((byte)list2.indexOf(this.moduleName));
        }
        GameWindow.WriteString(byteBuffer, this.itemName);
        byte b = 0;
        final int position = byteBuffer.position();
        byteBuffer.put((byte)0);
        if (this.isModded) {
            b = Bits.addFlags(b, 1);
            if (list.size() > 127) {
                byteBuffer.putShort((short)list.indexOf(this.modID));
            }
            else {
                byteBuffer.put((byte)list.indexOf(this.modID));
            }
        }
        if (this.existsAsVanilla) {
            b = Bits.addFlags(b, 2);
        }
        if (this.obsolete) {
            b = Bits.addFlags(b, 4);
        }
        if (this.removed) {
            b = Bits.addFlags(b, 8);
        }
        if (this.modOverrides != null) {
            b = Bits.addFlags(b, 16);
            if (this.modOverrides.size() == 1) {
                if (list.size() > 127) {
                    byteBuffer.putShort((short)list.indexOf(this.modOverrides.get(0)));
                }
                else {
                    byteBuffer.put((byte)list.indexOf(this.modOverrides.get(0)));
                }
            }
            else {
                b = Bits.addFlags(b, 32);
                byteBuffer.put((byte)this.modOverrides.size());
                for (int i = 0; i < this.modOverrides.size(); ++i) {
                    if (list.size() > 127) {
                        byteBuffer.putShort((short)list.indexOf(this.modOverrides.get(i)));
                    }
                    else {
                        byteBuffer.put((byte)list.indexOf(this.modOverrides.get(i)));
                    }
                }
            }
        }
        final int position2 = byteBuffer.position();
        byteBuffer.position(position);
        byteBuffer.put(b);
        byteBuffer.position(position2);
    }
    
    protected void load(final ByteBuffer byteBuffer, final int n, final List<String> list, final List<String> list2) {
        this.registryID = byteBuffer.getShort();
        this.moduleName = list2.get((list2.size() > 127) ? byteBuffer.getShort() : byteBuffer.get());
        this.itemName = GameWindow.ReadString(byteBuffer);
        this.fullType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.moduleName, this.itemName);
        final byte value = byteBuffer.get();
        if (Bits.hasFlags(value, 1)) {
            this.modID = list.get((list.size() > 127) ? byteBuffer.getShort() : byteBuffer.get());
            this.isModded = true;
        }
        else {
            this.modID = "pz-vanilla";
            this.isModded = false;
        }
        this.existsAsVanilla = Bits.hasFlags(value, 2);
        this.obsolete = Bits.hasFlags(value, 4);
        this.removed = Bits.hasFlags(value, 8);
        if (Bits.hasFlags(value, 16)) {
            if (this.modOverrides == null) {
                this.modOverrides = new ArrayList<String>();
            }
            this.modOverrides.clear();
            if (!Bits.hasFlags(value, 32)) {
                this.modOverrides.add(list.get((list.size() > 127) ? byteBuffer.getShort() : byteBuffer.get()));
            }
            else {
                for (byte value2 = byteBuffer.get(), b = 0; b < value2; ++b) {
                    this.modOverrides.add(list.get((list.size() > 127) ? byteBuffer.getShort() : byteBuffer.get()));
                }
            }
        }
    }
}
