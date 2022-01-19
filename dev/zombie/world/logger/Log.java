// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world.logger;

import zombie.world.ItemInfo;
import java.util.List;
import java.io.IOException;
import java.io.FileWriter;

public class Log
{
    public abstract static class BaseLog
    {
        protected boolean ignoreSaveCheck;
        
        public BaseLog() {
            this.ignoreSaveCheck = false;
        }
        
        public boolean isIgnoreSaveCheck() {
            return this.ignoreSaveCheck;
        }
        
        abstract void saveAsText(final FileWriter p0, final String p1) throws IOException;
    }
    
    public static class Info extends BaseLog
    {
        protected final List<String> mods;
        protected final String timeStamp;
        protected final String saveWorld;
        protected final int worldVersion;
        public boolean HasErrored;
        
        public Info(final String timeStamp, final String saveWorld, final int worldVersion, final List<String> mods) {
            this.HasErrored = false;
            this.ignoreSaveCheck = true;
            this.timeStamp = timeStamp;
            this.saveWorld = saveWorld;
            this.worldVersion = worldVersion;
            this.mods = mods;
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.timeStamp, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.saveWorld, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, s, this.worldVersion, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ZLjava/lang/String;)Ljava/lang/String;, s, this.HasErrored, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, System.lineSeparator()));
            for (int i = 0; i < this.mods.size(); ++i) {
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, (String)this.mods.get(i), System.lineSeparator()));
            }
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, System.lineSeparator()));
        }
    }
    
    public static class Comment extends BaseLog
    {
        protected String txt;
        
        public Comment(final String txt) {
            this.ignoreSaveCheck = true;
            this.txt = txt;
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.txt, System.lineSeparator()));
        }
    }
    
    public static class RegisterObject extends BaseLog
    {
        protected final String objectName;
        protected final int ID;
        
        public RegisterObject(final String objectName, final int id) {
            this.objectName = objectName;
            this.ID = id;
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.ID, this.objectName, System.lineSeparator()));
        }
    }
    
    public abstract static class BaseItemLog extends BaseLog
    {
        protected final ItemInfo itemInfo;
        
        public BaseItemLog(final ItemInfo itemInfo) {
            this.itemInfo = itemInfo;
        }
        
        @Override
        abstract void saveAsText(final FileWriter p0, final String p1) throws IOException;
        
        protected String getItemString() {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SZZLjava/lang/String;ZZZ)Ljava/lang/String;, this.itemInfo.getFullType(), this.itemInfo.getRegistryID(), this.itemInfo.isExistsAsVanilla(), this.itemInfo.isModded(), this.itemInfo.getModID(), this.itemInfo.isObsolete(), this.itemInfo.isRemoved(), this.itemInfo.isLoaded());
        }
    }
    
    public static class RegisterItem extends BaseItemLog
    {
        public RegisterItem(final ItemInfo itemInfo) {
            super(itemInfo);
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.getItemString(), System.lineSeparator()));
        }
    }
    
    public static class ReinstateItem extends BaseItemLog
    {
        public ReinstateItem(final ItemInfo itemInfo) {
            super(itemInfo);
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.getItemString(), System.lineSeparator()));
        }
    }
    
    public static class ObsoleteItem extends BaseItemLog
    {
        public ObsoleteItem(final ItemInfo itemInfo) {
            super(itemInfo);
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.getItemString(), System.lineSeparator()));
        }
    }
    
    public static class RemovedItem extends BaseItemLog
    {
        protected final boolean isScriptMissing;
        
        public RemovedItem(final ItemInfo itemInfo, final boolean isScriptMissing) {
            super(itemInfo);
            this.isScriptMissing = isScriptMissing;
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.isScriptMissing, this.getItemString(), System.lineSeparator()));
        }
    }
    
    public static class ModIDChangedItem extends BaseItemLog
    {
        protected final String oldModID;
        protected final String newModID;
        
        public ModIDChangedItem(final ItemInfo itemInfo, final String oldModID, final String newModID) {
            super(itemInfo);
            this.oldModID = oldModID;
            this.newModID = newModID;
        }
        
        public void saveAsText(final FileWriter fileWriter, final String s) throws IOException {
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.oldModID, this.getItemString(), System.lineSeparator()));
        }
    }
}
