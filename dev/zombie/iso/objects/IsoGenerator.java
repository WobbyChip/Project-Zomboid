// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.network.PacketTypes;
import zombie.core.network.ByteBufferWriter;
import zombie.core.logger.ExceptionLogger;
import java.io.IOException;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import java.util.Iterator;
import zombie.core.properties.PropertyContainer;
import zombie.core.Translator;
import zombie.iso.IsoChunk;
import zombie.network.ServerMap;
import zombie.iso.IsoUtils;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.core.Rand;
import zombie.SandboxOptions;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.GameTime;
import zombie.WorldSoundManager;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoCell;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.iso.IsoObject;

public class IsoGenerator extends IsoObject
{
    public float fuel;
    public boolean activated;
    public int condition;
    private int lastHour;
    public boolean connected;
    private int numberOfElectricalItems;
    private boolean updateSurrounding;
    private final HashMap<String, String> itemsPowered;
    private float totalPowerUsing;
    private static final ArrayList<IsoGenerator> AllGenerators;
    private static final int GENERATOR_RADIUS = 20;
    
    public IsoGenerator(final IsoCell isoCell) {
        super(isoCell);
        this.fuel = 0.0f;
        this.activated = false;
        this.condition = 0;
        this.lastHour = -1;
        this.connected = false;
        this.numberOfElectricalItems = 0;
        this.updateSurrounding = false;
        this.itemsPowered = new HashMap<String, String>();
        this.totalPowerUsing = 0.0f;
    }
    
    public IsoGenerator(final InventoryItem infoFromItem, final IsoCell isoCell, final IsoGridSquare square) {
        super(isoCell, square, IsoSpriteManager.instance.getSprite("appliances_misc_01_0"));
        this.fuel = 0.0f;
        this.activated = false;
        this.condition = 0;
        this.lastHour = -1;
        this.connected = false;
        this.numberOfElectricalItems = 0;
        this.updateSurrounding = false;
        this.itemsPowered = new HashMap<String, String>();
        this.totalPowerUsing = 0.0f;
        if (infoFromItem != null) {
            this.setInfoFromItem(infoFromItem);
        }
        this.sprite = IsoSpriteManager.instance.getSprite("appliances_misc_01_0");
        (this.square = square).AddSpecialObject(this);
        if (GameClient.bClient) {
            this.transmitCompleteItemToServer();
        }
    }
    
    public IsoGenerator(final InventoryItem infoFromItem, final IsoCell isoCell, final IsoGridSquare square, final boolean b) {
        super(isoCell, square, IsoSpriteManager.instance.getSprite("appliances_misc_01_0"));
        this.fuel = 0.0f;
        this.activated = false;
        this.condition = 0;
        this.lastHour = -1;
        this.connected = false;
        this.numberOfElectricalItems = 0;
        this.updateSurrounding = false;
        this.itemsPowered = new HashMap<String, String>();
        this.totalPowerUsing = 0.0f;
        if (infoFromItem != null) {
            this.setInfoFromItem(infoFromItem);
        }
        this.sprite = IsoSpriteManager.instance.getSprite("appliances_misc_01_0");
        (this.square = square).AddSpecialObject(this);
        if (GameClient.bClient && !b) {
            this.transmitCompleteItemToServer();
        }
    }
    
    public void setInfoFromItem(final InventoryItem inventoryItem) {
        this.condition = inventoryItem.getCondition();
        if (inventoryItem.getModData().rawget((Object)"fuel") instanceof Double) {
            this.fuel = ((Double)inventoryItem.getModData().rawget((Object)"fuel")).floatValue();
        }
    }
    
    @Override
    public void update() {
        if (this.updateSurrounding && this.getSquare() != null) {
            this.setSurroundingElectricity();
            this.updateSurrounding = false;
        }
        if (this.isActivated()) {
            if (!GameServer.bServer && (this.emitter == null || !this.emitter.isPlaying("GeneratorLoop"))) {
                if (this.emitter == null) {
                    this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, (float)(int)this.getZ());
                    IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
                }
                this.emitter.playSoundLoopedImpl("GeneratorLoop");
            }
            if (GameClient.bClient) {
                this.emitter.tick();
                return;
            }
            WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 20, 1, false);
            if ((int)GameTime.getInstance().getWorldAgeHours() != this.lastHour) {
                if (!this.getSquare().getProperties().Is(IsoFlagType.exterior) && this.getSquare().getBuilding() != null) {
                    this.getSquare().getBuilding().setToxic(false);
                    this.getSquare().getBuilding().setToxic(this.isActivated());
                }
                final int n = (int)GameTime.getInstance().getWorldAgeHours() - this.lastHour;
                float n2 = 0.0f;
                int n3 = 0;
                for (int i = 0; i < n; ++i) {
                    n2 += (float)(this.totalPowerUsing * SandboxOptions.instance.GeneratorFuelConsumption.getValue());
                    if (Rand.Next(30) == 0) {
                        n3 += Rand.Next(2) + 1;
                    }
                    if (this.fuel - n2 <= 0.0f) {
                        break;
                    }
                    if (this.condition - n3 <= 0) {
                        break;
                    }
                }
                this.fuel -= n2;
                if (this.fuel <= 0.0f) {
                    this.setActivated(false);
                    this.fuel = 0.0f;
                }
                this.condition -= n3;
                if (this.condition <= 0) {
                    this.setActivated(false);
                    this.condition = 0;
                }
                if (this.condition <= 20) {
                    if (Rand.Next(10) == 0) {
                        IsoFireManager.StartFire(this.getCell(), this.square, true, 1000);
                        this.condition = 0;
                        this.setActivated(false);
                    }
                    else if (Rand.Next(20) == 0) {
                        this.square.explode();
                        this.condition = 0;
                        this.setActivated(false);
                    }
                }
                this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
                if (GameServer.bServer) {
                    this.syncIsoObject(false, (byte)0, null, null);
                }
            }
        }
        if (this.emitter != null) {
            this.emitter.tick();
        }
    }
    
    public void setSurroundingElectricity() {
        this.itemsPowered.clear();
        this.totalPowerUsing = 0.02f;
        this.numberOfElectricalItems = 1;
        final boolean value = SandboxOptions.getInstance().AllowExteriorGenerator.getValue();
        final int n = this.square.getX() - 20;
        final int n2 = this.square.getX() + 20;
        final int n3 = this.square.getY() - 20;
        final int n4 = this.square.getY() + 20;
        final int max = Math.max(0, this.getSquare().getZ() - 3);
        for (int min = Math.min(8, this.getSquare().getZ() + 3), i = max; i < min; ++i) {
            for (int j = n; j <= n2; ++j) {
                for (int k = n3; k <= n4; ++k) {
                    if (IsoUtils.DistanceToSquared(j + 0.5f, k + 0.5f, this.getSquare().getX() + 0.5f, this.getSquare().getY() + 0.5f) <= 400.0f) {
                        final IsoGridSquare gridSquare = this.getCell().getGridSquare(j, k, i);
                        if (gridSquare != null) {
                            boolean activated = this.isActivated();
                            if (!value && gridSquare.Is(IsoFlagType.exterior)) {
                                activated = false;
                            }
                            gridSquare.setHaveElectricity(activated);
                            for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                                final IsoObject isoObject = gridSquare.getObjects().get(l);
                                if (isoObject != null) {
                                    if (!(isoObject instanceof IsoWorldInventoryObject)) {
                                        if (isoObject instanceof IsoTelevision && ((IsoTelevision)isoObject).getDeviceData().getIsTurnedOn()) {
                                            this.addPoweredItem(isoObject, 0.03f);
                                        }
                                        if (isoObject instanceof IsoRadio && ((IsoRadio)isoObject).getDeviceData().getIsTurnedOn()) {
                                            this.addPoweredItem(isoObject, 0.01f);
                                        }
                                        if (isoObject instanceof IsoStove && ((IsoStove)isoObject).Activated()) {
                                            this.addPoweredItem(isoObject, 0.09f);
                                        }
                                        final boolean b = isoObject.getContainerByType("fridge") != null;
                                        final boolean b2 = isoObject.getContainerByType("freezer") != null;
                                        if (b && b2) {
                                            this.addPoweredItem(isoObject, 0.13f);
                                        }
                                        else if (b || b2) {
                                            this.addPoweredItem(isoObject, 0.08f);
                                        }
                                        if (isoObject instanceof IsoLightSwitch && ((IsoLightSwitch)isoObject).Activated) {
                                            this.addPoweredItem(isoObject, 0.002f);
                                        }
                                        isoObject.checkHaveElectricity();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.square == null || this.square.chunk == null) {
            return;
        }
        final int wx = this.square.chunk.wx;
        final int wy = this.square.chunk.wy;
        for (int n5 = -2; n5 <= 2; ++n5) {
            for (int n6 = -2; n6 <= 2; ++n6) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(wx + n6, wy + n5) : IsoWorld.instance.CurrentCell.getChunk(wx + n6, wy + n5);
                if (isoChunk != null) {
                    if (this.touchesChunk(isoChunk)) {
                        if (this.isActivated()) {
                            isoChunk.addGeneratorPos(this.square.x, this.square.y, this.square.z);
                        }
                        else {
                            isoChunk.removeGeneratorPos(this.square.x, this.square.y, this.square.z);
                        }
                    }
                }
            }
        }
    }
    
    private void addPoweredItem(final IsoObject isoObject, final float n) {
        String prefix = Translator.getText("IGUI_VehiclePartCatOther");
        final PropertyContainer properties = isoObject.getProperties();
        if (properties != null && properties.Is("CustomName")) {
            String val = "Moveable Object";
            if (properties.Is("CustomName")) {
                if (properties.Is("GroupName")) {
                    val = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, properties.Val("GroupName"), properties.Val("CustomName"));
                }
                else {
                    val = properties.Val("CustomName");
                }
            }
            prefix = Translator.getMoveableDisplayName(val);
        }
        if (isoObject instanceof IsoLightSwitch) {
            prefix = Translator.getText("IGUI_Lights");
        }
        this.totalPowerUsing -= n;
        int int1 = 1;
        for (final String key : this.itemsPowered.keySet()) {
            if (key.startsWith(prefix)) {
                int1 = Integer.parseInt(key.replaceAll("[\\D]", ""));
                ++int1;
                this.itemsPowered.remove(key);
                break;
            }
        }
        this.itemsPowered.put(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, prefix, int1), invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, n * int1));
        if (int1 == 1) {
            this.totalPowerUsing += n * (int1 + 1);
        }
        else {
            this.totalPowerUsing += n * int1;
        }
    }
    
    private void updateFridgeFreezerItems(final IsoObject isoObject) {
        for (int i = 0; i < isoObject.getContainerCount(); ++i) {
            final ItemContainer containerByIndex = isoObject.getContainerByIndex(i);
            if ("fridge".equals(containerByIndex.getType()) || "freezer".equals(containerByIndex.getType())) {
                final ArrayList<InventoryItem> items = containerByIndex.getItems();
                for (int j = 0; j < items.size(); ++j) {
                    final InventoryItem inventoryItem = items.get(j);
                    if (inventoryItem instanceof Food) {
                        inventoryItem.updateAge();
                    }
                }
            }
        }
    }
    
    private void updateFridgeFreezerItems(final IsoGridSquare isoGridSquare) {
        final int size = isoGridSquare.getObjects().size();
        final IsoObject[] array = isoGridSquare.getObjects().getElements();
        for (int i = 0; i < size; ++i) {
            this.updateFridgeFreezerItems(array[i]);
        }
    }
    
    private void updateFridgeFreezerItems() {
        if (this.square == null) {
            return;
        }
        final int n = this.square.getX() - 20;
        final int n2 = this.square.getX() + 20;
        final int n3 = this.square.getY() - 20;
        final int n4 = this.square.getY() + 20;
        final int max = Math.max(0, this.square.getZ() - 3);
        for (int min = Math.min(8, this.square.getZ() + 3), i = max; i < min; ++i) {
            for (int j = n; j <= n2; ++j) {
                for (int k = n3; k <= n4; ++k) {
                    if (IsoUtils.DistanceToSquared((float)j, (float)k, (float)this.square.x, (float)this.square.y) <= 400.0f) {
                        final IsoGridSquare gridSquare = this.getCell().getGridSquare(j, k, i);
                        if (gridSquare != null) {
                            this.updateFridgeFreezerItems(gridSquare);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.connected = (byteBuffer.get() == 1);
        this.activated = (byteBuffer.get() == 1);
        if (n < 138) {
            this.fuel = (float)byteBuffer.getInt();
        }
        else {
            this.fuel = byteBuffer.getFloat();
        }
        this.condition = byteBuffer.getInt();
        this.lastHour = byteBuffer.getInt();
        this.numberOfElectricalItems = byteBuffer.getInt();
        this.updateSurrounding = true;
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.isConnected() ? 1 : 0));
        byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
        byteBuffer.putFloat(this.getFuel());
        byteBuffer.putInt(this.getCondition());
        byteBuffer.putInt(this.lastHour);
        byteBuffer.putInt(this.numberOfElectricalItems);
    }
    
    public void remove() {
        if (this.getSquare() == null) {
            return;
        }
        this.getSquare().transmitRemoveItemFromSquare(this);
    }
    
    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
        if (!IsoGenerator.AllGenerators.contains(this)) {
            IsoGenerator.AllGenerators.add(this);
        }
        if (GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequest(this.square, this);
        }
    }
    
    @Override
    public void removeFromWorld() {
        IsoGenerator.AllGenerators.remove(this);
        if (this.emitter != null) {
            this.emitter.stopAll();
            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
        }
        super.removeFromWorld();
    }
    
    @Override
    public String getObjectName() {
        return "IsoGenerator";
    }
    
    public float getFuel() {
        return this.fuel;
    }
    
    public void setFuel(final float fuel) {
        this.fuel = fuel;
        if (this.fuel > 100.0f) {
            this.fuel = 100.0f;
        }
        if (this.fuel < 0.0f) {
            this.fuel = 0.0f;
        }
        if (GameServer.bServer) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
        if (GameClient.bClient) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
    }
    
    public boolean isActivated() {
        return this.activated;
    }
    
    public void setActivated(final boolean b) {
        if (b == this.activated) {
            return;
        }
        if (!this.getSquare().getProperties().Is(IsoFlagType.exterior) && this.getSquare().getBuilding() != null) {
            this.getSquare().getBuilding().setToxic(false);
            this.getSquare().getBuilding().setToxic(b);
        }
        if (!GameServer.bServer && this.emitter == null) {
            this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, this.getZ());
            IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
        }
        if (b) {
            this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
            if (this.emitter != null) {
                this.emitter.playSound("GeneratorStarting");
            }
        }
        else if (this.emitter != null) {
            if (!this.emitter.isEmpty()) {
                this.emitter.stopAll();
            }
            this.emitter.playSound("GeneratorStopping");
        }
        try {
            this.updateFridgeFreezerItems();
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
        }
        this.activated = b;
        this.setSurroundingElectricity();
        if (GameClient.bClient) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
        if (GameServer.bServer) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
    }
    
    public void failToStart() {
        if (GameServer.bServer) {
            return;
        }
        if (this.emitter == null) {
            this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5f, this.getY() + 0.5f, this.getZ());
            IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
        }
        this.emitter.playSound("GeneratorFailedToStart");
    }
    
    public int getCondition() {
        return this.condition;
    }
    
    public void setCondition(final int condition) {
        this.condition = condition;
        if (this.condition > 100) {
            this.condition = 100;
        }
        if (this.condition < 0) {
            this.condition = 0;
        }
        if (GameServer.bServer) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
        if (GameClient.bClient) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public void setConnected(final boolean connected) {
        this.connected = connected;
        if (GameClient.bClient) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        final byte b = (byte)this.getObjectIndex();
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte(b);
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putByte((byte)0);
        byteBufferWriter.putFloat(this.fuel);
        byteBufferWriter.putInt(this.condition);
        byteBufferWriter.putByte((byte)(this.activated ? 1 : 0));
        byteBufferWriter.putByte((byte)(this.connected ? 1 : 0));
    }
    
    @Override
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        if (this.square == null) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
            return;
        }
        if (this.getObjectIndex() == -1) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getClass().getSimpleName(), this.square.getX(), this.square.getY(), this.square.getZ()));
            return;
        }
        if (GameClient.bClient && !b) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket);
            this.syncIsoObjectSend(startPacket);
            PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
        }
        else if (GameServer.bServer && !b) {
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket2);
                this.syncIsoObjectSend(startPacket2);
                PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
            }
        }
        else if (b) {
            this.sync(byteBuffer.getFloat(), byteBuffer.getInt(), byteBuffer.get() == 1, byteBuffer.get() == 1);
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection3 : GameServer.udpEngine.connections) {
                    if (udpConnection != null && udpConnection3.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket3 = udpConnection3.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket3);
                        this.syncIsoObjectSend(startPacket3);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection3);
                    }
                }
            }
        }
    }
    
    public void sync(final float fuel, final int condition, final boolean connected, final boolean activated) {
        this.fuel = fuel;
        this.condition = condition;
        this.connected = connected;
        if (this.activated != activated) {
            try {
                this.updateFridgeFreezerItems();
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
            this.activated = activated;
            if (activated) {
                this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
            }
            else if (this.emitter != null) {
                this.emitter.stopAll();
            }
            this.setSurroundingElectricity();
        }
    }
    
    private boolean touchesChunk(final IsoChunk isoChunk) {
        final IsoGridSquare square = this.getSquare();
        assert square != null;
        if (square == null) {
            return false;
        }
        final int n = isoChunk.wx * 10;
        final int n2 = isoChunk.wy * 10;
        final int n3 = n + 10 - 1;
        final int n4 = n2 + 10 - 1;
        return square.x - 20 <= n3 && square.x + 20 >= n && square.y - 20 <= n4 && square.y + 20 >= n2;
    }
    
    public static void chunkLoaded(final IsoChunk isoChunk) {
        isoChunk.checkForMissingGenerators();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (j != 0 || i != 0) {
                    final IsoChunk isoChunk2 = GameServer.bServer ? ServerMap.instance.getChunk(isoChunk.wx + j, isoChunk.wy + i) : IsoWorld.instance.CurrentCell.getChunk(isoChunk.wx + j, isoChunk.wy + i);
                    if (isoChunk2 != null) {
                        isoChunk2.checkForMissingGenerators();
                    }
                }
            }
        }
        for (int k = 0; k < IsoGenerator.AllGenerators.size(); ++k) {
            final IsoGenerator isoGenerator = IsoGenerator.AllGenerators.get(k);
            if (!isoGenerator.updateSurrounding) {
                if (isoGenerator.touchesChunk(isoChunk)) {
                    isoGenerator.updateSurrounding = true;
                }
            }
        }
    }
    
    public static void updateSurroundingNow() {
        for (int i = 0; i < IsoGenerator.AllGenerators.size(); ++i) {
            final IsoGenerator isoGenerator = IsoGenerator.AllGenerators.get(i);
            if (isoGenerator.updateSurrounding) {
                if (isoGenerator.getSquare() != null) {
                    isoGenerator.updateSurrounding = false;
                    isoGenerator.setSurroundingElectricity();
                }
            }
        }
    }
    
    public static void updateGenerator(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return;
        }
        for (int i = 0; i < IsoGenerator.AllGenerators.size(); ++i) {
            final IsoGenerator isoGenerator = IsoGenerator.AllGenerators.get(i);
            if (isoGenerator.square.getBuilding() == isoGridSquare.getBuilding()) {
                isoGenerator.setSurroundingElectricity();
                isoGenerator.updateSurrounding = false;
            }
        }
    }
    
    public static void Reset() {
        assert IsoGenerator.AllGenerators.isEmpty();
        IsoGenerator.AllGenerators.clear();
    }
    
    public static boolean isPoweringSquare(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final int max = Math.max(0, n3 - 3);
        final int min = Math.min(8, n3 + 3);
        return n6 >= max && n6 < min && IsoUtils.DistanceToSquared(n + 0.5f, n2 + 0.5f, n4 + 0.5f, n5 + 0.5f) <= 400.0f;
    }
    
    public ArrayList<String> getItemsPowered() {
        final ArrayList<String> list = new ArrayList<String>();
        for (final String key : this.itemsPowered.keySet()) {
            list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, (String)this.itemsPowered.get(key)));
        }
        return list;
    }
    
    public float getTotalPowerUsing() {
        return this.totalPowerUsing;
    }
    
    public void setTotalPowerUsing(final float totalPowerUsing) {
        this.totalPowerUsing = totalPowerUsing;
    }
    
    static {
        AllGenerators = new ArrayList<IsoGenerator>();
    }
}
