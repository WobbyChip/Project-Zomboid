// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import zombie.network.GameClient;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import zombie.iso.SliceY;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import zombie.util.Type;
import java.nio.ByteBuffer;
import zombie.iso.IsoObject;
import zombie.ZomboidFileSystem;
import java.io.IOException;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.util.HashSet;
import se.krka.kahlua.vm.KahluaTable;

public final class SGlobalObjectSystem extends GlobalObjectSystem
{
    private static KahluaTable tempTable;
    protected int loadedWorldVersion;
    protected final HashSet<String> modDataKeys;
    protected final HashSet<String> objectModDataKeys;
    protected final HashSet<String> objectSyncKeys;
    
    public SGlobalObjectSystem(final String s) {
        super(s);
        this.loadedWorldVersion = -1;
        this.modDataKeys = new HashSet<String>();
        this.objectModDataKeys = new HashSet<String>();
        this.objectSyncKeys = new HashSet<String>();
    }
    
    @Override
    protected GlobalObject makeObject(final int n, final int n2, final int n3) {
        return new SGlobalObject(this, n, n2, n3);
    }
    
    public void setModDataKeys(final KahluaTable kahluaTable) {
        this.modDataKeys.clear();
        if (kahluaTable == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object value = iterator.getValue();
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, value));
            }
            this.modDataKeys.add((String)value);
        }
    }
    
    public void setObjectModDataKeys(final KahluaTable kahluaTable) {
        this.objectModDataKeys.clear();
        if (kahluaTable == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object value = iterator.getValue();
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, value));
            }
            this.objectModDataKeys.add((String)value);
        }
    }
    
    public void setObjectSyncKeys(final KahluaTable kahluaTable) {
        this.objectSyncKeys.clear();
        if (kahluaTable == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object value = iterator.getValue();
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;)Ljava/lang/String;, value));
            }
            this.objectSyncKeys.add((String)value);
        }
    }
    
    public void update() {
    }
    
    public void chunkLoaded(final int n, final int n2) {
        if (!this.hasObjectsInChunk(n, n2)) {
            return;
        }
        final Object rawget = this.modData.rawget((Object)"OnChunkLoaded");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[] { this.modData, BoxedStaticValues.toDouble(n), BoxedStaticValues.toDouble(n2) });
    }
    
    public void sendCommand(final String s, final KahluaTable kahluaTable) {
        SGlobalObjectNetwork.sendServerCommand(this.name, s, kahluaTable);
    }
    
    public void receiveClientCommand(final String s, final IsoPlayer isoPlayer, final KahluaTable kahluaTable) {
        final Object rawget = this.modData.rawget((Object)"OnClientCommand");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[] { this.modData, s, isoPlayer, kahluaTable });
    }
    
    public void addGlobalObjectOnClient(final SGlobalObject sGlobalObject) throws IOException {
        if (sGlobalObject == null) {
            throw new IllegalArgumentException("globalObject is null");
        }
        if (sGlobalObject.system != this) {
            throw new IllegalArgumentException("object not in this system");
        }
        SGlobalObjectNetwork.addGlobalObjectOnClient(sGlobalObject);
    }
    
    public void removeGlobalObjectOnClient(final SGlobalObject sGlobalObject) throws IOException {
        if (sGlobalObject == null) {
            throw new IllegalArgumentException("globalObject is null");
        }
        if (sGlobalObject.system != this) {
            throw new IllegalArgumentException("object not in this system");
        }
        SGlobalObjectNetwork.removeGlobalObjectOnClient(sGlobalObject);
    }
    
    public void updateGlobalObjectOnClient(final SGlobalObject sGlobalObject) throws IOException {
        if (sGlobalObject == null) {
            throw new IllegalArgumentException("globalObject is null");
        }
        if (sGlobalObject.system != this) {
            throw new IllegalArgumentException("object not in this system");
        }
        SGlobalObjectNetwork.updateGlobalObjectOnClient(sGlobalObject);
    }
    
    private String getFileName() {
        return ZomboidFileSystem.instance.getFileNameInCurrentSave(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
    }
    
    public KahluaTable getInitialStateForClient() {
        final Object rawget = this.modData.rawget((Object)"getInitialStateForClient");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        final Object[] pcall = LuaManager.caller.pcall(LuaManager.thread, rawget, (Object)this.modData);
        if (pcall != null && pcall[0].equals(Boolean.TRUE) && pcall[1] instanceof KahluaTable) {
            return (KahluaTable)pcall[1];
        }
        return null;
    }
    
    public void OnIsoObjectChangedItself(final IsoObject isoObject) {
        if (this.getObjectAt(isoObject.getSquare().x, isoObject.getSquare().y, isoObject.getSquare().z) == null) {
            return;
        }
        final Object rawget = this.modData.rawget((Object)"OnIsoObjectChangedItself");
        if (rawget == null) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[] { this.modData, isoObject });
    }
    
    public int loadedWorldVersion() {
        return this.loadedWorldVersion;
    }
    
    public void load(final ByteBuffer byteBuffer, final int loadedWorldVersion) throws IOException {
        if (byteBuffer.get() != 0) {
            this.modData.load(byteBuffer, loadedWorldVersion);
        }
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            Type.tryCastTo(this.newObject(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.get()), SGlobalObject.class).load(byteBuffer, loadedWorldVersion);
        }
        this.loadedWorldVersion = loadedWorldVersion;
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        if (SGlobalObjectSystem.tempTable == null) {
            SGlobalObjectSystem.tempTable = LuaManager.platform.newTable();
        }
        SGlobalObjectSystem.tempTable.wipe();
        final KahluaTableIterator iterator = this.modData.iterator();
        while (iterator.advance()) {
            final Object key = iterator.getKey();
            if (this.modDataKeys.contains(key)) {
                SGlobalObjectSystem.tempTable.rawset(key, this.modData.rawget(key));
            }
        }
        if (SGlobalObjectSystem.tempTable.isEmpty()) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            SGlobalObjectSystem.tempTable.save(byteBuffer);
        }
        byteBuffer.putInt(this.objects.size());
        for (int i = 0; i < this.objects.size(); ++i) {
            Type.tryCastTo(this.objects.get(i), SGlobalObject.class).save(byteBuffer);
        }
    }
    
    public void load() {
        final File file = new File(this.getFileName());
        try {
            final FileInputStream in = new FileInputStream(file);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                        sliceBuffer.clear();
                        sliceBuffer.limit(bufferedInputStream.read(sliceBuffer.array()));
                        final byte value = sliceBuffer.get();
                        final byte value2 = sliceBuffer.get();
                        final byte value3 = sliceBuffer.get();
                        final byte value4 = sliceBuffer.get();
                        if (value != 71 || value2 != 76 || value3 != 79 || value4 != 83) {
                            throw new IOException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
                        }
                        final int int1 = sliceBuffer.getInt();
                        if (int1 < 134) {
                            throw new IOException(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, int1, file.getAbsolutePath()));
                        }
                        if (int1 > 186) {
                            throw new IOException(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, int1, file.getAbsolutePath()));
                        }
                        this.load(sliceBuffer, int1);
                    }
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (FileNotFoundException ex) {}
        catch (Throwable t3) {
            ExceptionLogger.logException(t3);
        }
    }
    
    public void save() {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        if (GameClient.bClient) {
            return;
        }
        final File file = new File(this.getFileName());
        try {
            final FileOutputStream out = new FileOutputStream(file);
            try {
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                        sliceBuffer.clear();
                        sliceBuffer.put((byte)71);
                        sliceBuffer.put((byte)76);
                        sliceBuffer.put((byte)79);
                        sliceBuffer.put((byte)83);
                        sliceBuffer.putInt(186);
                        this.save(sliceBuffer);
                        bufferedOutputStream.write(sliceBuffer.array(), 0, sliceBuffer.position());
                    }
                    bufferedOutputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedOutputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out.close();
            }
            catch (Throwable t2) {
                try {
                    out.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Throwable t3) {
            ExceptionLogger.logException(t3);
        }
    }
    
    @Override
    public void Reset() {
        super.Reset();
        this.modDataKeys.clear();
        this.objectModDataKeys.clear();
        this.objectSyncKeys.clear();
    }
}
