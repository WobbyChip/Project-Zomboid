// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world.moddata;

import java.io.FileInputStream;
import com.google.common.io.Files;
import java.io.FileOutputStream;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.nio.BufferOverflowException;
import zombie.core.Core;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.GameWindow;
import zombie.network.PacketTypes;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import java.util.UUID;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import zombie.Lua.LuaEventManager;
import zombie.world.WorldDictionary;
import java.util.HashMap;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.Map;

public final class GlobalModData
{
    public static final String SAVE_EXT = ".bin";
    public static final String SAVE_FILE = "global_mod_data";
    public static GlobalModData instance;
    private Map<String, KahluaTable> modData;
    private static final int BLOCK_SIZE = 524288;
    private static int LAST_BLOCK_SIZE;
    
    private KahluaTable createModDataTable() {
        return LuaManager.platform.newTable();
    }
    
    public GlobalModData() {
        this.modData = new HashMap<String, KahluaTable>();
        this.reset();
    }
    
    public void init() throws IOException {
        this.reset();
        this.load();
        LuaEventManager.triggerEvent("OnInitGlobalModData", WorldDictionary.isIsNewGame());
    }
    
    public void reset() {
        GlobalModData.LAST_BLOCK_SIZE = -1;
        this.modData.clear();
    }
    
    public void collectTableNames(final List<String> list) {
        list.clear();
        final Iterator<Map.Entry<String, KahluaTable>> iterator = this.modData.entrySet().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getKey());
        }
    }
    
    public boolean exists(final String s) {
        return this.modData.containsKey(s);
    }
    
    public KahluaTable getOrCreate(final String s) {
        KahluaTable kahluaTable = this.get(s);
        if (kahluaTable == null) {
            kahluaTable = this.create(s);
        }
        return kahluaTable;
    }
    
    public KahluaTable get(final String s) {
        return this.modData.get(s);
    }
    
    public String create() {
        final String string = UUID.randomUUID().toString();
        this.create(string);
        return string;
    }
    
    public KahluaTable create(final String s) {
        if (this.exists(s)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return null;
        }
        final KahluaTable modDataTable = this.createModDataTable();
        this.modData.put(s, modDataTable);
        return modDataTable;
    }
    
    public KahluaTable remove(final String s) {
        return this.modData.remove(s);
    }
    
    public void add(final String s, final KahluaTable kahluaTable) {
        this.modData.put(s, kahluaTable);
    }
    
    public void transmit(final String s) {
        final KahluaTable value = this.get(s);
        if (value != null) {
            if (GameClient.bClient) {
                final ByteBufferWriter startPacket = GameClient.connection.startPacket();
                PacketTypes.PacketType.GlobalModData.doPacket(startPacket);
                final ByteBuffer bb = startPacket.bb;
                try {
                    GameWindow.WriteString(bb, s);
                    bb.put((byte)1);
                    value.save(bb);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    GameClient.connection.cancelPacket();
                }
                finally {
                    PacketTypes.PacketType.GlobalModData.send(GameClient.connection);
                }
            }
            else if (GameServer.bServer) {
                try {
                    for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                        final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                        final ByteBufferWriter startPacket2 = udpConnection.startPacket();
                        PacketTypes.PacketType.GlobalModData.doPacket(startPacket2);
                        final ByteBuffer bb2 = startPacket2.bb;
                        try {
                            GameWindow.WriteString(bb2, s);
                            bb2.put((byte)1);
                            value.save(bb2);
                        }
                        catch (Exception ex2) {
                            ex2.printStackTrace();
                            udpConnection.cancelPacket();
                        }
                        finally {
                            PacketTypes.PacketType.GlobalModData.send(udpConnection);
                        }
                    }
                }
                catch (Exception ex3) {
                    DebugLog.log(ex3.getMessage());
                }
            }
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public void receive(final ByteBuffer byteBuffer) {
        try {
            final String readString = GameWindow.ReadString(byteBuffer);
            if (byteBuffer.get() != 1) {
                LuaEventManager.triggerEvent("OnReceiveGlobalModData", readString, false);
                return;
            }
            final KahluaTable modDataTable = this.createModDataTable();
            modDataTable.load(byteBuffer, 186);
            LuaEventManager.triggerEvent("OnReceiveGlobalModData", readString, modDataTable);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void request(final String s) {
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.GlobalModDataRequest.doPacket(startPacket);
            final ByteBuffer bb = startPacket.bb;
            try {
                GameWindow.WriteString(bb, s);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                GameClient.connection.cancelPacket();
            }
            finally {
                PacketTypes.PacketType.GlobalModDataRequest.send(GameClient.connection);
            }
        }
        else {
            DebugLog.log("GlobalModData -> can only request from Client.");
        }
    }
    
    public void receiveRequest(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final KahluaTable value = this.get(readString);
        if (value == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString));
        }
        if (GameServer.bServer) {
            try {
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                    if (udpConnection2 == udpConnection) {
                        final ByteBufferWriter startPacket = udpConnection2.startPacket();
                        PacketTypes.PacketType.GlobalModData.doPacket(startPacket);
                        final ByteBuffer bb = startPacket.bb;
                        try {
                            GameWindow.WriteString(bb, readString);
                            bb.put((byte)((value != null) ? 1 : 0));
                            if (value != null) {
                                value.save(bb);
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            udpConnection2.cancelPacket();
                        }
                        finally {
                            PacketTypes.PacketType.GlobalModData.send(udpConnection2);
                        }
                    }
                }
            }
            catch (Exception ex2) {
                DebugLog.log(ex2.getMessage());
            }
        }
    }
    
    private static ByteBuffer ensureCapacity(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            GlobalModData.LAST_BLOCK_SIZE = 1048576;
            return ByteBuffer.allocate(GlobalModData.LAST_BLOCK_SIZE);
        }
        GlobalModData.LAST_BLOCK_SIZE = byteBuffer.capacity() + 524288;
        return ByteBuffer.allocate(GlobalModData.LAST_BLOCK_SIZE).put(byteBuffer.array(), 0, byteBuffer.position());
    }
    
    public void save() throws IOException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        try {
            DebugLog.log("Saving GlobalModData");
            ByteBuffer byteBuffer = ByteBuffer.allocate((GlobalModData.LAST_BLOCK_SIZE == -1) ? 1048576 : GlobalModData.LAST_BLOCK_SIZE);
            byteBuffer.putInt(186);
            byteBuffer.putInt(this.modData.size());
            int n = 0;
            for (final Map.Entry<String, KahluaTable> entry : this.modData.entrySet()) {
                if (byteBuffer.capacity() - byteBuffer.position() < 4) {
                    n = byteBuffer.position();
                    ensureCapacity(byteBuffer);
                    byteBuffer.position(n);
                }
                final int position = byteBuffer.position();
                byteBuffer.putInt(0);
                final int position2 = byteBuffer.position();
                while (true) {
                    try {
                        n = byteBuffer.position();
                        GameWindow.WriteString(byteBuffer, entry.getKey());
                        entry.getValue().save(byteBuffer);
                    }
                    catch (BufferOverflowException ex) {
                        byteBuffer = ensureCapacity(byteBuffer);
                        byteBuffer.position(n);
                        continue;
                    }
                    break;
                }
                final int position3 = byteBuffer.position();
                byteBuffer.position(position);
                byteBuffer.putInt(position3 - position2);
                byteBuffer.position(position3);
            }
            byteBuffer.flip();
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.getChannel().truncate(0L);
            fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
            fileOutputStream.flush();
            fileOutputStream.close();
            Files.copy(file, new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator)));
            file.delete();
        }
        catch (Exception cause) {
            cause.printStackTrace();
            throw new IOException("Error saving GlobalModData.", cause);
        }
    }
    
    public void load() throws IOException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator);
        final File file = new File(pathname);
        if (!file.exists()) {
            if (!WorldDictionary.isIsNewGame()) {}
            return;
        }
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            try {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
                this.modData.clear();
                final ByteBuffer allocate = ByteBuffer.allocate((int)file.length());
                allocate.clear();
                allocate.limit(fileInputStream.read(allocate.array()));
                final int int1 = allocate.getInt();
                for (int int2 = allocate.getInt(), i = 0; i < int2; ++i) {
                    allocate.getInt();
                    final String readString = GameWindow.ReadString(allocate);
                    final KahluaTable modDataTable = this.createModDataTable();
                    modDataTable.load(allocate, int1);
                    this.modData.put(readString, modDataTable);
                }
                fileInputStream.close();
            }
            catch (Throwable t) {
                try {
                    fileInputStream.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Exception cause) {
            cause.printStackTrace();
            throw new IOException("Error loading GlobalModData.", cause);
        }
    }
    
    static {
        GlobalModData.instance = new GlobalModData();
        GlobalModData.LAST_BLOCK_SIZE = -1;
    }
}
