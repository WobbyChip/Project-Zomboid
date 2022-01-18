// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import zombie.scripting.ScriptManager;
import java.util.Map;

public class DictionaryDataClient extends DictionaryData
{
    @Override
    protected boolean isClient() {
        return true;
    }
    
    @Override
    protected void parseItemLoadList(final Map<String, ItemInfo> map) throws WorldDictionaryException {
    }
    
    @Override
    protected void parseCurrentItemSet() throws WorldDictionaryException {
        final Iterator<Map.Entry<String, ItemInfo>> iterator = this.itemTypeToInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final ItemInfo itemInfo = iterator.next().getValue();
            if (!itemInfo.removed && itemInfo.scriptItem == null) {
                itemInfo.scriptItem = ScriptManager.instance.getItem(itemInfo.fullType);
            }
            if (itemInfo.scriptItem != null) {
                itemInfo.scriptItem.setRegistry_id(itemInfo.registryID);
                itemInfo.scriptItem.setModID(itemInfo.modID);
                itemInfo.isLoaded = true;
            }
            else {
                if (!itemInfo.removed) {
                    throw new WorldDictionaryException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, itemInfo.fullType));
                }
                continue;
            }
        }
    }
    
    @Override
    protected void parseObjectNameLoadList(final List<String> list) throws WorldDictionaryException {
    }
    
    @Override
    protected void backupCurrentDataSet() throws IOException {
    }
    
    @Override
    protected void deleteBackupCurrentDataSet() throws IOException {
    }
    
    @Override
    protected void createErrorBackups() {
    }
    
    @Override
    protected void load() throws IOException, WorldDictionaryException {
    }
    
    @Override
    protected void save() throws IOException, WorldDictionaryException {
    }
    
    @Override
    protected void saveToByteBuffer(final ByteBuffer byteBuffer) throws IOException {
    }
}
