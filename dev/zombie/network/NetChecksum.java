// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.core.raknet.UdpConnection;
import zombie.ZomboidFileSystem;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.scripting.ScriptManager;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import zombie.core.logger.ExceptionLogger;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import zombie.debug.DebugLog;
import zombie.core.Core;

public final class NetChecksum
{
    public static final Checksummer checksummer;
    public static final Comparer comparer;
    
    private static void noise(final String s) {
        if (!Core.bDebug) {}
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    static {
        checksummer = new Checksummer();
        comparer = new Comparer();
    }
    
    public static final class Checksummer
    {
        private MessageDigest md;
        private final byte[] fileBytes;
        private final byte[] convertBytes;
        private boolean convertLineEndings;
        
        public Checksummer() {
            this.fileBytes = new byte[1024];
            this.convertBytes = new byte[1024];
        }
        
        public void reset(final boolean convertLineEndings) throws NoSuchAlgorithmException {
            if (this.md == null) {
                this.md = MessageDigest.getInstance("MD5");
            }
            this.convertLineEndings = convertLineEndings;
            this.md.reset();
        }
        
        public void addFile(final String s, final String name) throws NoSuchAlgorithmException {
            if (this.md == null) {
                this.md = MessageDigest.getInstance("MD5");
            }
            try {
                final FileInputStream fileInputStream = new FileInputStream(name);
                try {
                    GroupOfFiles.addFile(s, name);
                    int read;
                    while ((read = fileInputStream.read(this.fileBytes)) != -1) {
                        if (this.convertLineEndings) {
                            boolean b = false;
                            int len = 0;
                            for (int i = 0; i < read - 1; ++i) {
                                if (this.fileBytes[i] == 13 && this.fileBytes[i + 1] == 10) {
                                    this.convertBytes[len++] = 10;
                                    b = true;
                                }
                                else {
                                    b = false;
                                    this.convertBytes[len++] = this.fileBytes[i];
                                }
                            }
                            if (!b) {
                                this.convertBytes[len++] = this.fileBytes[read - 1];
                            }
                            this.md.update(this.convertBytes, 0, len);
                            GroupOfFiles.updateFile(this.convertBytes, len);
                        }
                        else {
                            this.md.update(this.fileBytes, 0, read);
                            GroupOfFiles.updateFile(this.fileBytes, read);
                        }
                    }
                    GroupOfFiles.endFile();
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
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        public String checksumToString() {
            final byte[] digest = this.md.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; ++i) {
                sb.append(Integer.toString((digest[i] & 0xFF) + 256, 16).substring(1));
            }
            return sb.toString();
        }
    }
    
    public static final class GroupOfFiles
    {
        static final int MAX_FILES = 20;
        static MessageDigest mdTotal;
        static MessageDigest mdCurrentFile;
        static final ArrayList<GroupOfFiles> groups;
        static GroupOfFiles currentGroup;
        byte[] totalChecksum;
        short fileCount;
        final String[] relPaths;
        final String[] absPaths;
        final byte[][] checksums;
        
        private GroupOfFiles() throws NoSuchAlgorithmException {
            this.relPaths = new String[20];
            this.absPaths = new String[20];
            this.checksums = new byte[20][];
            if (GroupOfFiles.mdTotal == null) {
                GroupOfFiles.mdTotal = MessageDigest.getInstance("MD5");
                GroupOfFiles.mdCurrentFile = MessageDigest.getInstance("MD5");
            }
            GroupOfFiles.mdTotal.reset();
            GroupOfFiles.groups.add(this);
        }
        
        private void gc_() {
            Arrays.fill(this.relPaths, null);
            Arrays.fill(this.absPaths, null);
            Arrays.fill(this.checksums, null);
        }
        
        public static void initChecksum() {
            GroupOfFiles.groups.clear();
            GroupOfFiles.currentGroup = null;
        }
        
        public static void finishChecksum() {
            if (GroupOfFiles.currentGroup != null) {
                GroupOfFiles.currentGroup.totalChecksum = GroupOfFiles.mdTotal.digest();
                GroupOfFiles.currentGroup = null;
            }
        }
        
        private static void addFile(final String s, final String s2) throws NoSuchAlgorithmException {
            if (GroupOfFiles.currentGroup == null) {
                GroupOfFiles.currentGroup = new GroupOfFiles();
            }
            GroupOfFiles.currentGroup.relPaths[GroupOfFiles.currentGroup.fileCount] = s;
            GroupOfFiles.currentGroup.absPaths[GroupOfFiles.currentGroup.fileCount] = s2;
            GroupOfFiles.mdCurrentFile.reset();
        }
        
        private static void updateFile(final byte[] array, final int n) {
            GroupOfFiles.mdCurrentFile.update(array, 0, n);
            GroupOfFiles.mdTotal.update(array, 0, n);
        }
        
        private static void endFile() {
            GroupOfFiles.currentGroup.checksums[GroupOfFiles.currentGroup.fileCount] = GroupOfFiles.mdCurrentFile.digest();
            final GroupOfFiles currentGroup = GroupOfFiles.currentGroup;
            ++currentGroup.fileCount;
            if (GroupOfFiles.currentGroup.fileCount >= 20) {
                GroupOfFiles.currentGroup.totalChecksum = GroupOfFiles.mdTotal.digest();
                GroupOfFiles.currentGroup = null;
            }
        }
        
        public static void gc() {
            final Iterator<GroupOfFiles> iterator = GroupOfFiles.groups.iterator();
            while (iterator.hasNext()) {
                iterator.next().gc_();
            }
            GroupOfFiles.groups.clear();
        }
        
        static {
            groups = new ArrayList<GroupOfFiles>();
        }
    }
    
    public static final class Comparer
    {
        private static final short PacketTotalChecksum = 1;
        private static final short PacketGroupChecksum = 2;
        private static final short PacketFileChecksums = 3;
        private static final short PacketError = 4;
        private static final byte FileDifferent = 1;
        private static final byte FileNotOnServer = 2;
        private static final byte FileNotOnClient = 3;
        private static final short NUM_GROUPS_TO_SEND = 10;
        private State state;
        private short currentIndex;
        private String error;
        private final byte[] checksum;
        
        public Comparer() {
            this.state = State.Init;
            this.checksum = new byte[64];
        }
        
        public void beginCompare() {
            this.error = null;
            this.sendTotalChecksum();
        }
        
        private void sendTotalChecksum() {
            if (!GameClient.bClient) {
                return;
            }
            NetChecksum.noise("send total checksum");
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.Checksum.doPacket(startPacket);
            startPacket.putShort((short)1);
            startPacket.putUTF(GameClient.checksum);
            startPacket.putUTF(ScriptManager.instance.getChecksum());
            PacketTypes.PacketType.Checksum.send(GameClient.connection);
            this.state = State.SentTotalChecksum;
        }
        
        private void sendGroupChecksum() {
            if (!GameClient.bClient) {
                return;
            }
            if (this.currentIndex >= GroupOfFiles.groups.size()) {
                this.state = State.Success;
                return;
            }
            final short n = (short)Math.min(this.currentIndex + 10 - 1, GroupOfFiles.groups.size() - 1);
            NetChecksum.noise(invokedynamic(makeConcatWithConstants:(SS)Ljava/lang/String;, this.currentIndex, n));
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.Checksum.doPacket(startPacket);
            startPacket.putShort((short)2);
            startPacket.putShort(this.currentIndex);
            startPacket.putShort(n);
            for (short currentIndex = this.currentIndex; currentIndex <= n; ++currentIndex) {
                final GroupOfFiles groupOfFiles = GroupOfFiles.groups.get(currentIndex);
                startPacket.putShort((short)groupOfFiles.totalChecksum.length);
                startPacket.bb.put(groupOfFiles.totalChecksum);
            }
            PacketTypes.PacketType.Checksum.send(GameClient.connection);
            this.state = State.SentGroupChecksum;
        }
        
        private void sendFileChecksums() {
            if (!GameClient.bClient) {
                return;
            }
            NetChecksum.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.currentIndex));
            final GroupOfFiles groupOfFiles = GroupOfFiles.groups.get(this.currentIndex);
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.Checksum.doPacket(startPacket);
            startPacket.putShort((short)3);
            startPacket.bb.putShort(this.currentIndex);
            startPacket.putShort(groupOfFiles.fileCount);
            for (short n = 0; n < groupOfFiles.fileCount; ++n) {
                startPacket.putUTF(groupOfFiles.relPaths[n]);
                startPacket.putByte((byte)groupOfFiles.checksums[n].length);
                startPacket.bb.put(groupOfFiles.checksums[n]);
            }
            PacketTypes.PacketType.Checksum.send(GameClient.connection);
            this.state = State.SentFileChecksums;
        }
        
        public void clientPacket(final ByteBuffer byteBuffer) {
            if (!GameClient.bClient) {
                return;
            }
            final short short1 = byteBuffer.getShort();
            switch (short1) {
                case 1: {
                    if (this.state != State.SentTotalChecksum) {
                        this.error = invokedynamic(makeConcatWithConstants:(Lzombie/network/NetChecksum$Comparer$State;)Ljava/lang/String;, this.state);
                        this.state = State.Failed;
                        break;
                    }
                    final boolean b = byteBuffer.get() == 1;
                    final boolean b2 = byteBuffer.get() == 1;
                    NetChecksum.noise(invokedynamic(makeConcatWithConstants:(ZZ)Ljava/lang/String;, b, b2));
                    if (b && b2) {
                        this.state = State.Success;
                        break;
                    }
                    this.currentIndex = 0;
                    this.sendGroupChecksum();
                    break;
                }
                case 2: {
                    if (this.state != State.SentGroupChecksum) {
                        this.error = invokedynamic(makeConcatWithConstants:(Lzombie/network/NetChecksum$Comparer$State;)Ljava/lang/String;, this.state);
                        this.state = State.Failed;
                        break;
                    }
                    final short short2 = byteBuffer.getShort();
                    final boolean b3 = byteBuffer.get() == 1;
                    if (short2 < this.currentIndex || short2 >= this.currentIndex + 10) {
                        this.error = invokedynamic(makeConcatWithConstants:(SS)Ljava/lang/String;, this.currentIndex, short2);
                        this.state = State.Failed;
                        break;
                    }
                    NetChecksum.noise(invokedynamic(makeConcatWithConstants:(SZ)Ljava/lang/String;, short2, b3));
                    if (b3) {
                        this.currentIndex += 10;
                        this.sendGroupChecksum();
                        break;
                    }
                    this.currentIndex = short2;
                    this.sendFileChecksums();
                    break;
                }
                case 3: {
                    if (this.state != State.SentFileChecksums) {
                        this.error = invokedynamic(makeConcatWithConstants:(Lzombie/network/NetChecksum$Comparer$State;)Ljava/lang/String;, this.state);
                        this.state = State.Failed;
                        break;
                    }
                    final short short3 = byteBuffer.getShort();
                    final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
                    final byte value = byteBuffer.get();
                    if (short3 != this.currentIndex) {
                        this.error = invokedynamic(makeConcatWithConstants:(SS)Ljava/lang/String;, this.currentIndex, short3);
                        this.state = State.Failed;
                        break;
                    }
                    switch (value) {
                        case 1: {
                            this.error = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readStringUTF);
                            break;
                        }
                        case 2: {
                            this.error = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readStringUTF);
                            break;
                        }
                        case 3: {
                            this.error = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readStringUTF);
                            break;
                        }
                        default: {
                            this.error = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readStringUTF);
                            break;
                        }
                    }
                    final String string = ZomboidFileSystem.instance.getString(readStringUTF);
                    if (!string.equals(readStringUTF)) {
                        this.error = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.error, string);
                    }
                    this.state = State.Failed;
                    break;
                }
                case 4: {
                    this.error = GameWindow.ReadStringUTF(byteBuffer);
                    this.state = State.Failed;
                    break;
                }
                default: {
                    this.error = invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1);
                    this.state = State.Failed;
                    break;
                }
            }
        }
        
        private boolean checksumEquals(final byte[] array) {
            if (array == null) {
                return false;
            }
            if (this.checksum.length < array.length) {
                return false;
            }
            for (int i = 0; i < array.length; ++i) {
                if (this.checksum[i] != array[i]) {
                    return false;
                }
            }
            return true;
        }
        
        private void sendFileMismatch(final UdpConnection udpConnection, final short n, final String s, final byte b) {
            if (!GameServer.bServer) {
                return;
            }
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Checksum.doPacket(startPacket);
            startPacket.putShort((short)3);
            startPacket.putShort(n);
            startPacket.putUTF(s);
            startPacket.putByte(b);
            PacketTypes.PacketType.Checksum.send(udpConnection);
        }
        
        private void sendError(final UdpConnection udpConnection, final String s) {
            if (!GameServer.bServer) {
                return;
            }
            NetChecksum.noise(s);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Checksum.doPacket(startPacket);
            startPacket.putShort((short)4);
            startPacket.putUTF(s);
            PacketTypes.PacketType.Checksum.send(udpConnection);
        }
        
        public void serverPacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
            if (!GameServer.bServer) {
                return;
            }
            final short short1 = byteBuffer.getShort();
            switch (short1) {
                case 1: {
                    final String readString = GameWindow.ReadString(byteBuffer);
                    final String readString2 = GameWindow.ReadString(byteBuffer);
                    boolean equals = readString.equals(GameServer.checksum);
                    boolean equals2 = readString2.equals(ScriptManager.instance.getChecksum());
                    NetChecksum.noise(invokedynamic(makeConcatWithConstants:(ZZ)Ljava/lang/String;, equals, equals2));
                    if (udpConnection.accessLevel.equals("admin")) {
                        equals2 = (equals = true);
                    }
                    udpConnection.checksumState = ((equals && equals2) ? UdpConnection.ChecksumState.Done : UdpConnection.ChecksumState.Different);
                    udpConnection.checksumTime = System.currentTimeMillis();
                    if (!equals || !equals2) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.username));
                        ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.LuaChecksum, "", "server", 1);
                    }
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.Checksum.doPacket(startPacket);
                    startPacket.putShort((short)1);
                    startPacket.putBoolean(equals);
                    startPacket.putBoolean(equals2);
                    PacketTypes.PacketType.Checksum.send(udpConnection);
                    break;
                }
                case 2: {
                    final short short2 = byteBuffer.getShort();
                    final short short3 = byteBuffer.getShort();
                    if (short2 < 0 || short3 < short2 || short3 >= short2 + 10) {
                        this.sendError(udpConnection, "PacketGroupChecksum: firstIndex and/or lastIndex are invalid");
                        break;
                    }
                    for (short index = short2; index <= short3; ++index) {
                        final short short4 = byteBuffer.getShort();
                        if (short4 < 0 || short4 > this.checksum.length) {
                            this.sendError(udpConnection, "PacketGroupChecksum: numBytes is invalid");
                            return;
                        }
                        byteBuffer.get(this.checksum, 0, short4);
                        if (index >= GroupOfFiles.groups.size() || !this.checksumEquals(GroupOfFiles.groups.get(index).totalChecksum)) {
                            final ByteBufferWriter startPacket2 = udpConnection.startPacket();
                            PacketTypes.PacketType.Checksum.doPacket(startPacket2);
                            startPacket2.putShort((short)2);
                            startPacket2.putShort(index);
                            startPacket2.putBoolean(false);
                            PacketTypes.PacketType.Checksum.send(udpConnection);
                            return;
                        }
                    }
                    final ByteBufferWriter startPacket3 = udpConnection.startPacket();
                    PacketTypes.PacketType.Checksum.doPacket(startPacket3);
                    startPacket3.putShort((short)2);
                    startPacket3.putShort(short2);
                    startPacket3.putBoolean(true);
                    PacketTypes.PacketType.Checksum.send(udpConnection);
                    break;
                }
                case 3: {
                    final short short5 = byteBuffer.getShort();
                    final short short6 = byteBuffer.getShort();
                    if (short5 < 0 || short6 <= 0 || short6 > 20) {
                        this.sendError(udpConnection, "PacketFileChecksums: groupIndex and/or fileCount are invalid");
                        return;
                    }
                    if (short5 >= GroupOfFiles.groups.size()) {
                        this.sendFileMismatch(udpConnection, short5, GameWindow.ReadStringUTF(byteBuffer), (byte)2);
                        return;
                    }
                    final GroupOfFiles groupOfFiles = GroupOfFiles.groups.get(short5);
                    short n = 0;
                    while (n < short6) {
                        final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
                        final byte value = byteBuffer.get();
                        if (value < 0 || value > this.checksum.length) {
                            this.sendError(udpConnection, "PacketFileChecksums: numBytes is invalid");
                            return;
                        }
                        if (n >= groupOfFiles.fileCount) {
                            this.sendFileMismatch(udpConnection, short5, readStringUTF, (byte)2);
                            return;
                        }
                        if (!readStringUTF.equals(groupOfFiles.relPaths[n])) {
                            if (ZomboidFileSystem.instance.getString(readStringUTF).equals(readStringUTF)) {
                                this.sendFileMismatch(udpConnection, short5, readStringUTF, (byte)2);
                                return;
                            }
                            this.sendFileMismatch(udpConnection, short5, groupOfFiles.relPaths[n], (byte)3);
                            return;
                        }
                        else {
                            if (value > groupOfFiles.checksums[n].length) {
                                this.sendFileMismatch(udpConnection, short5, groupOfFiles.relPaths[n], (byte)1);
                                return;
                            }
                            byteBuffer.get(this.checksum, 0, value);
                            if (!this.checksumEquals(groupOfFiles.checksums[n])) {
                                this.sendFileMismatch(udpConnection, short5, groupOfFiles.relPaths[n], (byte)1);
                                return;
                            }
                            ++n;
                        }
                    }
                    if (groupOfFiles.fileCount > short6) {
                        this.sendFileMismatch(udpConnection, short5, groupOfFiles.relPaths[short6], (byte)3);
                        return;
                    }
                    this.sendError(udpConnection, "PacketFileChecksums: all checks passed when they shouldn't");
                    break;
                }
                default: {
                    this.sendError(udpConnection, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
                    break;
                }
            }
        }
        
        private void gc() {
            GroupOfFiles.gc();
        }
        
        public void update() {
            switch (this.state) {
                case Init: {}
                case SentTotalChecksum: {}
                case SentGroupChecksum: {}
                case Success: {
                    this.gc();
                    GameClient.checksumValid = true;
                    break;
                }
                case Failed: {
                    this.gc();
                    GameClient.connection.forceDisconnect();
                    GameWindow.bServerDisconnected = true;
                    GameWindow.kickReason = this.error;
                    break;
                }
            }
        }
        
        private enum State
        {
            Init, 
            SentTotalChecksum, 
            SentGroupChecksum, 
            SentFileChecksums, 
            Success, 
            Failed;
            
            private static /* synthetic */ State[] $values() {
                return new State[] { State.Init, State.SentTotalChecksum, State.SentGroupChecksum, State.SentFileChecksums, State.Success, State.Failed };
            }
            
            static {
                $VALUES = $values();
            }
        }
    }
}
