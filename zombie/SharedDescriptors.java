// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.util.ArrayList;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.util.Type;
import zombie.characters.IsoGameCharacter;
import zombie.network.GameClient;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.characters.IsoZombie;
import zombie.debug.DebugLog;
import zombie.network.GameServer;

public final class SharedDescriptors
{
    private static final int DESCRIPTOR_COUNT = 500;
    private static final int DESCRIPTOR_ID_START = 500;
    private static final byte[] DESCRIPTOR_MAGIC;
    private static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;
    private static final int VERSION = 2;
    private static Descriptor[] PlayerZombieDescriptors;
    private static final int FIRST_PLAYER_ZOMBIE_DESCRIPTOR_ID = 1000;
    
    public static void initSharedDescriptors() {
        if (!GameServer.bServer) {
            return;
        }
    }
    
    private static void noise(final String s) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public static void createPlayerZombieDescriptor(final IsoZombie isoZombie) {
        if (!GameServer.bServer) {
            return;
        }
        if (!isoZombie.isReanimatedPlayer()) {
            return;
        }
        if (isoZombie.getDescriptor().getID() != 0) {
            return;
        }
        int length = -1;
        for (int i = 0; i < SharedDescriptors.PlayerZombieDescriptors.length; ++i) {
            if (SharedDescriptors.PlayerZombieDescriptors[i] == null) {
                length = i;
                break;
            }
        }
        if (length == -1) {
            final Descriptor[] playerZombieDescriptors = new Descriptor[SharedDescriptors.PlayerZombieDescriptors.length + 10];
            System.arraycopy(SharedDescriptors.PlayerZombieDescriptors, 0, playerZombieDescriptors, 0, SharedDescriptors.PlayerZombieDescriptors.length);
            length = SharedDescriptors.PlayerZombieDescriptors.length;
            SharedDescriptors.PlayerZombieDescriptors = playerZombieDescriptors;
            noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, SharedDescriptors.PlayerZombieDescriptors.length));
        }
        isoZombie.getDescriptor().setID(1000 + length);
        final int n = (PersistentOutfits.instance.pickOutfit("ReanimatedPlayer", isoZombie.isFemale()) & 0xFFFF0000) | length + 1;
        isoZombie.setPersistentOutfitID(n);
        final Descriptor descriptor = new Descriptor();
        descriptor.bFemale = isoZombie.isFemale();
        descriptor.bZombie = false;
        descriptor.ID = 1000 + length;
        descriptor.persistentOutfitID = n;
        descriptor.getHumanVisual().copyFrom(isoZombie.getHumanVisual());
        final ItemVisuals itemVisuals = new ItemVisuals();
        isoZombie.getItemVisuals(itemVisuals);
        for (int j = 0; j < itemVisuals.size(); ++j) {
            descriptor.itemVisuals.add(new ItemVisual(itemVisuals.get(j)));
        }
        SharedDescriptors.PlayerZombieDescriptors[length] = descriptor;
        noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, descriptor.getID()));
        for (int k = 0; k < GameServer.udpEngine.connections.size(); ++k) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(k);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            try {
                PacketTypes.PacketType.ZombieDescriptors.doPacket(startPacket);
                descriptor.save(startPacket.bb);
                PacketTypes.PacketType.ZombieDescriptors.send(udpConnection);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                udpConnection.cancelPacket();
            }
        }
    }
    
    public static void releasePlayerZombieDescriptor(final IsoZombie isoZombie) {
        if (!GameServer.bServer) {
            return;
        }
        if (!isoZombie.isReanimatedPlayer()) {
            return;
        }
        final int n = isoZombie.getDescriptor().getID() - 1000;
        if (n < 0 || n >= SharedDescriptors.PlayerZombieDescriptors.length) {
            return;
        }
        noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoZombie.getDescriptor().getID()));
        isoZombie.getDescriptor().setID(0);
        SharedDescriptors.PlayerZombieDescriptors[n] = null;
    }
    
    public static Descriptor[] getPlayerZombieDescriptors() {
        return SharedDescriptors.PlayerZombieDescriptors;
    }
    
    public static void registerPlayerZombieDescriptor(final Descriptor descriptor) {
        if (!GameClient.bClient) {
            return;
        }
        final int n = descriptor.getID() - 1000;
        if (n < 0 || n >= 32767) {
            return;
        }
        if (SharedDescriptors.PlayerZombieDescriptors.length <= n) {
            final Descriptor[] playerZombieDescriptors = new Descriptor[(n + 10) / 10 * 10];
            System.arraycopy(SharedDescriptors.PlayerZombieDescriptors, 0, playerZombieDescriptors, 0, SharedDescriptors.PlayerZombieDescriptors.length);
            SharedDescriptors.PlayerZombieDescriptors = playerZombieDescriptors;
            noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, SharedDescriptors.PlayerZombieDescriptors.length));
        }
        SharedDescriptors.PlayerZombieDescriptors[n] = descriptor;
        noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, descriptor.getID()));
    }
    
    public static void ApplyReanimatedPlayerOutfit(final int n, final String s, final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (isoZombie == null) {
            return;
        }
        final short n2 = (short)(n & 0xFFFF);
        if (n2 < 1 || n2 > SharedDescriptors.PlayerZombieDescriptors.length) {
            return;
        }
        final Descriptor descriptor = SharedDescriptors.PlayerZombieDescriptors[n2 - 1];
        if (descriptor == null) {
            return;
        }
        isoZombie.useDescriptor(descriptor);
    }
    
    static {
        DESCRIPTOR_MAGIC = new byte[] { 68, 69, 83, 67 };
        SharedDescriptors.PlayerZombieDescriptors = new Descriptor[10];
    }
    
    public static final class Descriptor implements IHumanVisual
    {
        public int ID;
        public int persistentOutfitID;
        public String outfitName;
        public final HumanVisual humanVisual;
        public final ItemVisuals itemVisuals;
        public boolean bFemale;
        public boolean bZombie;
        
        public Descriptor() {
            this.ID = 0;
            this.persistentOutfitID = 0;
            this.humanVisual = new HumanVisual(this);
            this.itemVisuals = new ItemVisuals();
            this.bFemale = false;
            this.bZombie = false;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public int getPersistentOutfitID() {
            return this.persistentOutfitID;
        }
        
        @Override
        public HumanVisual getHumanVisual() {
            return this.humanVisual;
        }
        
        @Override
        public void getItemVisuals(final ItemVisuals itemVisuals) {
            itemVisuals.clear();
            itemVisuals.addAll(this.itemVisuals);
        }
        
        @Override
        public boolean isFemale() {
            return this.bFemale;
        }
        
        @Override
        public boolean isZombie() {
            return this.bZombie;
        }
        
        @Override
        public boolean isSkeleton() {
            return false;
        }
        
        public void save(final ByteBuffer byteBuffer) throws IOException {
            byte b = 0;
            if (this.bFemale) {
                b |= 0x1;
            }
            if (this.bZombie) {
                b |= 0x2;
            }
            byteBuffer.put(b);
            byteBuffer.putInt(this.ID);
            byteBuffer.putInt(this.persistentOutfitID);
            GameWindow.WriteStringUTF(byteBuffer, this.outfitName);
            this.humanVisual.save(byteBuffer);
            this.itemVisuals.save(byteBuffer);
        }
        
        public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
            this.humanVisual.clear();
            this.itemVisuals.clear();
            final byte value = byteBuffer.get();
            this.bFemale = ((value & 0x1) != 0x0);
            this.bZombie = ((value & 0x2) != 0x0);
            this.ID = byteBuffer.getInt();
            this.persistentOutfitID = byteBuffer.getInt();
            this.outfitName = GameWindow.ReadStringUTF(byteBuffer);
            this.humanVisual.load(byteBuffer, n);
            for (short short1 = byteBuffer.getShort(), n2 = 0; n2 < short1; ++n2) {
                final ItemVisual e = new ItemVisual();
                e.load(byteBuffer, n);
                this.itemVisuals.add(e);
            }
        }
    }
    
    private static final class DescriptorList extends ArrayList<Descriptor>
    {
    }
}
