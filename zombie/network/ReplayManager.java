// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.RakVoice;
import java.io.InputStream;
import java.io.File;
import zombie.core.logger.ExceptionLogger;
import java.io.OutputStream;
import zombie.debug.DebugLog;
import zombie.ZomboidFileSystem;
import java.io.IOException;
import zombie.GameTime;
import zombie.core.network.ByteBufferWriter;
import zombie.network.packets.PlayerPacket;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;

public class ReplayManager
{
    private static final int ReplayManagerVersion = 1;
    private State state;
    private IsoPlayer player;
    private ByteBuffer bbpp;
    private FileOutputStream outStream;
    private DataOutputStream output;
    private FileInputStream inStream;
    private DataInputStream input;
    private int inputVersion;
    private long inputTimeShift;
    private PlayerPacket nextpp;
    private long nextppTime;
    
    public ReplayManager(final IsoPlayer player) {
        this.state = State.Stop;
        this.player = null;
        this.bbpp = ByteBuffer.allocate(43);
        this.outStream = null;
        this.output = null;
        this.inStream = null;
        this.input = null;
        this.inputVersion = 0;
        this.inputTimeShift = 0L;
        this.nextpp = null;
        this.nextppTime = 0L;
        this.player = player;
    }
    
    public State getState() {
        return this.state;
    }
    
    public boolean isPlay() {
        return this.state == State.Playing;
    }
    
    public void recordPlayerPacket(final PlayerPacket playerPacket) {
        if (this.state != State.Recording || playerPacket.id != this.player.OnlineID) {
            return;
        }
        this.bbpp.position(0);
        final ByteBufferWriter byteBufferWriter = new ByteBufferWriter(this.bbpp);
        playerPacket.write(byteBufferWriter);
        try {
            this.output.writeLong(GameTime.getServerTime());
            this.output.write(PacketTypes.PacketType.PlayerUpdate.getId());
            this.output.write(byteBufferWriter.bb.array());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean startRecordReplay(final IsoPlayer player, final String s) {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave(s);
        if (this.player != null && this.state == State.Recording) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.player.getUsername()));
            return false;
        }
        if (fileInCurrentSave.exists()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return false;
        }
        try {
            this.outStream = new FileOutputStream(fileInCurrentSave);
            (this.output = new DataOutputStream(this.outStream)).write(1);
            this.output.writeLong(GameTime.getServerTime());
            this.player = player;
            this.state = State.Recording;
            return true;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
    }
    
    public boolean stopRecordReplay() {
        if (this.state != State.Recording) {
            DebugLog.log("ReplayManager: record inactive");
            return false;
        }
        try {
            this.state = State.Stop;
            this.player = null;
            this.output.flush();
            this.output.close();
            this.outStream.close();
            this.output = null;
            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean startPlayReplay(final IsoPlayer player, final String s) {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave(s);
        if (this.state == State.Playing) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.player.getUsername()));
            return false;
        }
        if (!fileInCurrentSave.exists()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return false;
        }
        try {
            this.inStream = new FileInputStream(fileInCurrentSave);
            this.input = new DataInputStream(this.inStream);
            this.inputVersion = this.input.read();
            this.inputTimeShift = GameTime.getServerTime() - this.input.readLong();
            this.nextppTime = this.input.readLong();
            final int read = this.input.read();
            if (read == PacketTypes.PacketType.PlayerUpdate.getId() || read == PacketTypes.PacketType.PlayerUpdateReliable.getId()) {
                this.input.read(this.bbpp.array());
                this.bbpp.position(0);
                (this.nextpp = new PlayerPacket()).parse(this.bbpp);
            }
            this.player = player;
            this.state = State.Playing;
            return true;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
    }
    
    public boolean stopPlayReplay() {
        if (this.state != State.Playing) {
            DebugLog.log("ReplayManager: play inactive");
            return false;
        }
        try {
            this.state = State.Stop;
            this.player = null;
            this.input.close();
            this.inStream.close();
            this.input = null;
            this.inputVersion = 0;
            this.inputTimeShift = 0L;
            this.nextpp = null;
            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public void update() {
        if (this.state != State.Playing) {
            return;
        }
        if (GameTime.getServerTime() >= this.nextppTime + this.inputTimeShift) {
            this.nextpp.id = this.player.OnlineID;
            final PlayerPacket nextpp = this.nextpp;
            nextpp.realt += (int)(this.inputTimeShift / 1000000L);
            final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get((int)this.nextpp.id);
            final UdpConnection connectionFromPlayer = GameServer.getConnectionFromPlayer(isoPlayer);
            try {
                if (isoPlayer == null) {
                    DebugLog.General.error(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.nextpp.id));
                }
                else {
                    isoPlayer.networkAI.parse(this.nextpp);
                    RakVoice.SetPlayerCoordinate(connectionFromPlayer.getConnectedGUID(), this.nextpp.realx, this.nextpp.realy, this.nextpp.realz, isoPlayer.isInvisible());
                    connectionFromPlayer.ReleventPos[isoPlayer.PlayerIndex].x = this.nextpp.realx;
                    connectionFromPlayer.ReleventPos[isoPlayer.PlayerIndex].y = this.nextpp.realy;
                    connectionFromPlayer.ReleventPos[isoPlayer.PlayerIndex].z = this.nextpp.realz;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                if (connectionFromPlayer.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.PlayerUpdate.doPacket(startPacket);
                    this.nextpp.write(startPacket);
                    PacketTypes.PacketType.PlayerUpdate.send(udpConnection);
                }
            }
            try {
                this.nextppTime = this.input.readLong();
                final int read = this.input.read();
                if (read == PacketTypes.PacketType.PlayerUpdate.getId() || read == PacketTypes.PacketType.PlayerUpdateReliable.getId()) {
                    this.bbpp.position(0);
                    this.input.read(this.bbpp.array());
                    this.bbpp.position(0);
                    (this.nextpp = new PlayerPacket()).parse(this.bbpp);
                }
            }
            catch (IOException ex2) {
                DebugLog.log("ReplayManager: stop playing replay");
                this.stopPlayReplay();
            }
        }
    }
    
    public enum State
    {
        Stop, 
        Recording, 
        Playing;
        
        private static /* synthetic */ State[] $values() {
            return new State[] { State.Stop, State.Recording, State.Playing };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
