// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.GameTime;
import java.util.List;
import zombie.SoundManager;
import zombie.scripting.objects.VehicleScript;
import java.util.Collection;
import zombie.Lua.LuaEventManager;
import java.io.IOException;
import zombie.iso.IsoObject;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.core.physics.Bullet;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.network.ServerOptions;
import zombie.debug.DebugType;
import zombie.core.physics.WorldSimulation;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.GameWindow;
import zombie.inventory.InventoryItem;
import java.nio.ByteBuffer;
import zombie.inventory.types.DrainableComboItem;
import org.joml.Quaternionfc;
import zombie.debug.DebugLog;
import zombie.iso.IsoWorld;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.GameServer;
import zombie.core.Core;
import zombie.core.utils.UpdateLimit;
import zombie.core.raknet.UdpConnection;
import gnu.trove.list.array.TShortArrayList;
import zombie.core.physics.Transform;
import java.util.ArrayList;

public final class VehicleManager
{
    public static VehicleManager instance;
    private final VehicleIDMap IDToVehicle;
    private final ArrayList<BaseVehicle> vehicles;
    private boolean idMapDirty;
    private final Transform tempTransform;
    private final ArrayList<BaseVehicle> send;
    private final TShortArrayList vehiclesWaitUpdates;
    public static short physicsDelay;
    public UdpConnection[] connected;
    private final float[] tempFloats;
    private final PosUpdateVars posUpdateVars;
    private UpdateLimit vehiclesWaitUpdatesFrequency;
    private BaseVehicle tempVehicle;
    private ArrayList<BaseVehicle.ModelInfo> oldModels;
    private ArrayList<BaseVehicle.ModelInfo> curModels;
    private static UpdateLimit sendReqestGetPositionFrequency;
    UpdateLimit VehiclePhysicSyncPacketLimit;
    
    public VehicleManager() {
        this.IDToVehicle = VehicleIDMap.instance;
        this.vehicles = new ArrayList<BaseVehicle>();
        this.idMapDirty = true;
        this.tempTransform = new Transform();
        this.send = new ArrayList<BaseVehicle>();
        this.vehiclesWaitUpdates = new TShortArrayList(128);
        this.connected = new UdpConnection[512];
        this.tempFloats = new float[27];
        this.posUpdateVars = new PosUpdateVars();
        this.vehiclesWaitUpdatesFrequency = new UpdateLimit(1000L);
        this.oldModels = new ArrayList<BaseVehicle.ModelInfo>();
        this.curModels = new ArrayList<BaseVehicle.ModelInfo>();
        this.VehiclePhysicSyncPacketLimit = new UpdateLimit(500L);
    }
    
    private void noise(final String s) {
        if (Core.bDebug) {}
    }
    
    public void registerVehicle(final BaseVehicle baseVehicle) {
        this.IDToVehicle.put(baseVehicle.VehicleID, baseVehicle);
        this.idMapDirty = true;
    }
    
    public void unregisterVehicle(final BaseVehicle baseVehicle) {
        this.IDToVehicle.remove(baseVehicle.VehicleID);
        this.idMapDirty = true;
    }
    
    public BaseVehicle getVehicleByID(final short n) {
        return this.IDToVehicle.get(n);
    }
    
    public ArrayList<BaseVehicle> getVehicles() {
        if (this.idMapDirty) {
            this.vehicles.clear();
            this.IDToVehicle.toArrayList(this.vehicles);
            this.idMapDirty = false;
        }
        return this.vehicles;
    }
    
    public void removeFromWorld(final BaseVehicle baseVehicle) {
        if (baseVehicle.VehicleID != -1) {
            final short vehicleID = baseVehicle.VehicleID;
            if (baseVehicle.trace) {
                this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, baseVehicle.VehicleID));
            }
            this.unregisterVehicle(baseVehicle);
            if (GameServer.bServer) {
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                    if (baseVehicle.connectionState[udpConnection.index] != null) {
                        final ByteBufferWriter startPacket = udpConnection.startPacket();
                        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
                        startPacket.bb.put((byte)8);
                        startPacket.bb.putShort(baseVehicle.VehicleID);
                        PacketTypes.PacketType.Vehicles.send(udpConnection);
                    }
                }
            }
            if (GameClient.bClient) {
                baseVehicle.serverRemovedFromWorld = false;
                if (baseVehicle.interpolation != null) {
                    baseVehicle.interpolation.poolData();
                }
            }
        }
    }
    
    public void serverUpdate() {
        final ArrayList<BaseVehicle> vehicles = IsoWorld.instance.CurrentCell.getVehicles();
        for (int i = 0; i < this.connected.length; ++i) {
            if (this.connected[i] != null && !GameServer.udpEngine.connections.contains(this.connected[i])) {
                this.noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                for (int j = 0; j < vehicles.size(); ++j) {
                    vehicles.get(j).connectionState[i] = null;
                }
                this.connected[i] = null;
            }
            else {
                for (int k = 0; k < vehicles.size(); ++k) {
                    if (vehicles.get(k).connectionState[i] != null) {
                        final BaseVehicle.ServerVehicleState serverVehicleState = vehicles.get(k).connectionState[i];
                        serverVehicleState.flags |= vehicles.get(k).updateFlags;
                    }
                }
            }
        }
        for (int l = 0; l < GameServer.udpEngine.connections.size(); ++l) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(l);
            this.sendVehicles(udpConnection);
            this.connected[udpConnection.index] = udpConnection;
        }
        for (int index = 0; index < vehicles.size(); ++index) {
            final BaseVehicle baseVehicle = vehicles.get(index);
            if ((baseVehicle.updateFlags & 0x4BF0) != 0x0) {
                for (int n = 0; n < baseVehicle.getPartCount(); ++n) {
                    baseVehicle.getPartByIndex(n).updateFlags = 0;
                }
            }
            baseVehicle.updateFlags = 0;
        }
    }
    
    private void sendVehicles(final UdpConnection udpConnection) {
        if (!udpConnection.isFullyConnected()) {
            return;
        }
        this.send.clear();
        final ArrayList<BaseVehicle> vehicles = IsoWorld.instance.CurrentCell.getVehicles();
        for (int i = 0; i < vehicles.size(); ++i) {
            final BaseVehicle e = vehicles.get(i);
            if (e.VehicleID == -1) {
                e.VehicleID = this.IDToVehicle.allocateID();
                this.registerVehicle(e);
            }
            int contains = udpConnection.vehicles.contains(e.VehicleID) ? 1 : 0;
            if (contains != 0 && !udpConnection.RelevantTo(e.x, e.y, udpConnection.ReleventRange * 10 * 2.0f)) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(SI)Ljava/lang/String;, e.VehicleID, udpConnection.index));
                udpConnection.vehicles.remove(e.VehicleID);
                contains = 0;
            }
            if (contains != 0 || udpConnection.RelevantTo(e.x, e.y)) {
                if (e.connectionState[udpConnection.index] == null) {
                    e.connectionState[udpConnection.index] = new BaseVehicle.ServerVehicleState();
                }
                final BaseVehicle.ServerVehicleState serverVehicleState = e.connectionState[udpConnection.index];
                if (contains == 0 || serverVehicleState.shouldSend(e)) {
                    this.send.add(e);
                    udpConnection.vehicles.add(e.VehicleID);
                }
            }
        }
        if (this.send.isEmpty()) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType packetType;
        if (this.VehiclePhysicSyncPacketLimit.Check()) {
            packetType = PacketTypes.PacketType.Vehicles;
        }
        else {
            packetType = PacketTypes.PacketType.VehiclesUnreliable;
        }
        packetType.doPacket(startPacket);
        try {
            final ByteBuffer bb = startPacket.bb;
            bb.put((byte)5);
            bb.putShort((short)this.send.size());
            for (int j = 0; j < this.send.size(); ++j) {
                final BaseVehicle baseVehicle = this.send.get(j);
                final BaseVehicle.ServerVehicleState serverVehicleState2 = baseVehicle.connectionState[udpConnection.index];
                bb.putShort(baseVehicle.VehicleID);
                bb.putShort(serverVehicleState2.flags);
                bb.putFloat(baseVehicle.x);
                bb.putFloat(baseVehicle.y);
                bb.putFloat(baseVehicle.jniTransform.origin.y);
                final int position = bb.position();
                bb.putShort((short)0);
                final int position2 = bb.position();
                if ((serverVehicleState2.flags & 0x1) != 0x0) {
                    final BaseVehicle.ServerVehicleState serverVehicleState3 = serverVehicleState2;
                    serverVehicleState3.flags &= 0xFFFFFFFE;
                    baseVehicle.netPlayerServerSendAuthorisation(bb);
                    serverVehicleState2.setAuthorization(baseVehicle);
                    final int position3 = bb.position();
                    bb.putShort((short)0);
                    baseVehicle.save(bb);
                    final int position4 = bb.position();
                    bb.position(position3);
                    bb.putShort((short)(position4 - position3));
                    bb.position(position4);
                    final int position5 = bb.position();
                    final int n = bb.position() - position2;
                    bb.position(position);
                    bb.putShort((short)n);
                    bb.position(position5);
                    this.writePositionOrientation(bb, baseVehicle);
                    serverVehicleState2.x = baseVehicle.x;
                    serverVehicleState2.y = baseVehicle.y;
                    serverVehicleState2.z = baseVehicle.jniTransform.origin.y;
                    serverVehicleState2.orient.set((Quaternionfc)baseVehicle.savedRot);
                }
                else {
                    if ((serverVehicleState2.flags & 0x4000) != 0x0) {
                        baseVehicle.netPlayerServerSendAuthorisation(bb);
                        serverVehicleState2.setAuthorization(baseVehicle);
                    }
                    if ((serverVehicleState2.flags & 0x2) != 0x0) {
                        this.writePositionOrientation(bb, baseVehicle);
                        serverVehicleState2.x = baseVehicle.x;
                        serverVehicleState2.y = baseVehicle.y;
                        serverVehicleState2.z = baseVehicle.jniTransform.origin.y;
                        serverVehicleState2.orient.set((Quaternionfc)baseVehicle.savedRot);
                    }
                    if ((serverVehicleState2.flags & 0x4) != 0x0) {
                        bb.put((byte)baseVehicle.engineState.ordinal());
                        bb.putInt(baseVehicle.engineLoudness);
                        bb.putInt(baseVehicle.enginePower);
                        bb.putInt(baseVehicle.engineQuality);
                    }
                    if ((serverVehicleState2.flags & 0x1000) != 0x0) {
                        bb.put((byte)(baseVehicle.isHotwired() ? 1 : 0));
                        bb.put((byte)(baseVehicle.isHotwiredBroken() ? 1 : 0));
                        bb.put((byte)(baseVehicle.isKeysInIgnition() ? 1 : 0));
                        bb.put((byte)(baseVehicle.isKeyIsOnDoor() ? 1 : 0));
                        final InventoryItem currentKey = baseVehicle.getCurrentKey();
                        if (currentKey == null) {
                            bb.put((byte)0);
                        }
                        else {
                            bb.put((byte)1);
                            currentKey.saveWithSize(bb, false);
                        }
                        bb.putFloat(baseVehicle.getRust());
                        bb.putFloat(baseVehicle.getBloodIntensity("Front"));
                        bb.putFloat(baseVehicle.getBloodIntensity("Rear"));
                        bb.putFloat(baseVehicle.getBloodIntensity("Left"));
                        bb.putFloat(baseVehicle.getBloodIntensity("Right"));
                    }
                    if ((serverVehicleState2.flags & 0x8) != 0x0) {
                        bb.put((byte)(baseVehicle.getHeadlightsOn() ? 1 : 0));
                        bb.put((byte)(baseVehicle.getStoplightsOn() ? 1 : 0));
                        for (int k = 0; k < baseVehicle.getLightCount(); ++k) {
                            bb.put((byte)(baseVehicle.getLightByIndex(k).getLight().getActive() ? 1 : 0));
                        }
                    }
                    if ((serverVehicleState2.flags & 0x400) != 0x0) {
                        bb.put((byte)(baseVehicle.soundHornOn ? 1 : 0));
                        bb.put((byte)(baseVehicle.soundBackMoveOn ? 1 : 0));
                        bb.put((byte)baseVehicle.lightbarLightsMode.get());
                        bb.put((byte)baseVehicle.lightbarSirenMode.get());
                    }
                    if ((serverVehicleState2.flags & 0x800) != 0x0) {
                        for (int l = 0; l < baseVehicle.getPartCount(); ++l) {
                            final VehiclePart partByIndex = baseVehicle.getPartByIndex(l);
                            if ((partByIndex.updateFlags & 0x800) != 0x0) {
                                bb.put((byte)l);
                                bb.putInt(partByIndex.getCondition());
                            }
                        }
                        bb.put((byte)(-1));
                    }
                    if ((serverVehicleState2.flags & 0x10) != 0x0) {
                        for (int n2 = 0; n2 < baseVehicle.getPartCount(); ++n2) {
                            final VehiclePart partByIndex2 = baseVehicle.getPartByIndex(n2);
                            if ((partByIndex2.updateFlags & 0x10) != 0x0) {
                                bb.put((byte)n2);
                                partByIndex2.getModData().save(bb);
                            }
                        }
                        bb.put((byte)(-1));
                    }
                    if ((serverVehicleState2.flags & 0x20) != 0x0) {
                        for (int n3 = 0; n3 < baseVehicle.getPartCount(); ++n3) {
                            final VehiclePart partByIndex3 = baseVehicle.getPartByIndex(n3);
                            if ((partByIndex3.updateFlags & 0x20) != 0x0) {
                                final InventoryItem inventoryItem = partByIndex3.getInventoryItem();
                                if (inventoryItem instanceof DrainableComboItem) {
                                    bb.put((byte)n3);
                                    bb.putFloat(((DrainableComboItem)inventoryItem).getUsedDelta());
                                }
                            }
                        }
                        bb.put((byte)(-1));
                    }
                    if ((serverVehicleState2.flags & 0x80) != 0x0) {
                        for (int n4 = 0; n4 < baseVehicle.getPartCount(); ++n4) {
                            final VehiclePart partByIndex4 = baseVehicle.getPartByIndex(n4);
                            if ((partByIndex4.updateFlags & 0x80) != 0x0) {
                                bb.put((byte)n4);
                                if (partByIndex4.getInventoryItem() == null) {
                                    bb.put((byte)0);
                                }
                                else {
                                    bb.put((byte)1);
                                    try {
                                        partByIndex4.getInventoryItem().saveWithSize(bb, false);
                                    }
                                    catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        bb.put((byte)(-1));
                    }
                    if ((serverVehicleState2.flags & 0x200) != 0x0) {
                        for (int n5 = 0; n5 < baseVehicle.getPartCount(); ++n5) {
                            final VehiclePart partByIndex5 = baseVehicle.getPartByIndex(n5);
                            if ((partByIndex5.updateFlags & 0x200) != 0x0) {
                                bb.put((byte)n5);
                                partByIndex5.getDoor().save(bb);
                            }
                        }
                        bb.put((byte)(-1));
                    }
                    if ((serverVehicleState2.flags & 0x100) != 0x0) {
                        for (int n6 = 0; n6 < baseVehicle.getPartCount(); ++n6) {
                            final VehiclePart partByIndex6 = baseVehicle.getPartByIndex(n6);
                            if ((partByIndex6.updateFlags & 0x100) != 0x0) {
                                bb.put((byte)n6);
                                partByIndex6.getWindow().save(bb);
                            }
                        }
                        bb.put((byte)(-1));
                    }
                    if ((serverVehicleState2.flags & 0x40) != 0x0) {
                        bb.put((byte)baseVehicle.models.size());
                        for (int index = 0; index < baseVehicle.models.size(); ++index) {
                            final BaseVehicle.ModelInfo modelInfo = baseVehicle.models.get(index);
                            bb.put((byte)modelInfo.part.getIndex());
                            bb.put((byte)modelInfo.part.getScriptPart().models.indexOf(modelInfo.scriptModel));
                        }
                    }
                    if ((serverVehicleState2.flags & 0x2000) != 0x0) {
                        bb.putFloat((float)baseVehicle.engineSpeed);
                        bb.putFloat(baseVehicle.throttle);
                    }
                    final int position6 = bb.position();
                    final int n7 = bb.position() - position2;
                    bb.position(position);
                    bb.putShort((short)n7);
                    bb.position(position6);
                }
            }
            packetType.send(udpConnection);
        }
        catch (Exception ex2) {
            udpConnection.cancelPacket();
            ex2.printStackTrace();
        }
    }
    
    public void serverPacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final byte value = byteBuffer.get();
        switch (value) {
            case 1: {
                final short short1 = byteBuffer.getShort();
                final byte value2 = byteBuffer.get();
                final String readString = GameWindow.ReadString(byteBuffer);
                final BaseVehicle value3 = this.IDToVehicle.get(short1);
                if (value3 == null) {
                    break;
                }
                final IsoGameCharacter character = value3.getCharacter(value2);
                if (character != null) {
                    value3.setCharacterPosition(character, value2, readString);
                    this.sendPassengerPosition(value3, value2, readString, udpConnection);
                    break;
                }
                break;
            }
            case 2: {
                final short short2 = byteBuffer.getShort();
                final byte value4 = byteBuffer.get();
                final short short3 = byteBuffer.getShort();
                final BaseVehicle value5 = this.IDToVehicle.get(short2);
                if (value5 == null) {
                    break;
                }
                final IsoGameCharacter character2 = value5.getCharacter(value4);
                if (character2 != null) {
                    final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short3);
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (isoPlayer == null) ? "unknown player" : isoPlayer.getUsername(), ((IsoPlayer)character2).getUsername()));
                    return;
                }
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                    for (int j = 0; j < 4; ++j) {
                        final IsoPlayer isoPlayer2 = udpConnection2.players[j];
                        if (isoPlayer2 != null && isoPlayer2.OnlineID == short3) {
                            this.noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SI)Ljava/lang/String;, isoPlayer2.getUsername(), value5.VehicleID, value4));
                            value5.enter(value4, isoPlayer2);
                            this.sendREnter(value5, value4, isoPlayer2);
                            value5.authorizationServerOnSeat();
                            break;
                        }
                    }
                }
                final IsoPlayer isoPlayer3 = GameServer.IDToPlayerMap.get(short3);
                if (value5.getVehicleTowing() != null && value5.getDriver() == isoPlayer3) {
                    value5.getVehicleTowing().setNetPlayerAuthorization((byte)3);
                    value5.getVehicleTowing().netPlayerId = isoPlayer3.OnlineID;
                    value5.getVehicleTowing().netPlayerTimeout = 30;
                    break;
                }
                if (value5.getVehicleTowedBy() == null) {
                    break;
                }
                if (value5.getVehicleTowedBy().getDriver() != null) {
                    value5.setNetPlayerAuthorization((byte)3);
                    value5.netPlayerId = value5.getVehicleTowedBy().getDriver().getOnlineID();
                    value5.netPlayerTimeout = 30;
                    break;
                }
                value5.setNetPlayerAuthorization((byte)0);
                value5.netPlayerId = -1;
                break;
            }
            case 18: {
                final boolean b = byteBuffer.get() == 1;
                short short4 = -1;
                short short5 = -1;
                if (b) {
                    short4 = byteBuffer.getShort();
                }
                if (byteBuffer.get() == 1) {
                    short5 = byteBuffer.getShort();
                }
                final BaseVehicle value6 = this.IDToVehicle.get(short4);
                final BaseVehicle value7 = this.IDToVehicle.get(short5);
                if (value6 == null && value7 == null) {
                    break;
                }
                if (value6 != null) {
                    if (value6.getDriver() == null) {
                        value6.setNetPlayerAuthorization((byte)0);
                        value6.netPlayerId = -1;
                    }
                    else {
                        value6.setNetPlayerAuthorization((byte)3);
                        value6.netPlayerId = value6.getDriver().getOnlineID();
                        value6.netPlayerTimeout = 30;
                    }
                    value6.breakConstraint(true, true);
                }
                if (value7 != null) {
                    if (value7.getDriver() == null) {
                        value7.setNetPlayerAuthorization((byte)0);
                        value7.netPlayerId = -1;
                    }
                    else {
                        value7.setNetPlayerAuthorization((byte)3);
                        value7.netPlayerId = value6.getDriver().getOnlineID();
                        value7.netPlayerTimeout = 30;
                    }
                    value7.breakConstraint(true, true);
                }
                for (int k = 0; k < GameServer.udpEngine.connections.size(); ++k) {
                    final UdpConnection udpConnection3 = GameServer.udpEngine.connections.get(k);
                    if (udpConnection3.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket = udpConnection3.startPacket();
                        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
                        startPacket.bb.put((byte)18);
                        if (value6 != null) {
                            startPacket.bb.put((byte)1);
                            startPacket.bb.putShort(value6.VehicleID);
                        }
                        else {
                            startPacket.bb.put((byte)0);
                        }
                        if (value7 != null) {
                            startPacket.bb.put((byte)1);
                            startPacket.bb.putShort(value7.VehicleID);
                        }
                        else {
                            startPacket.bb.put((byte)0);
                        }
                        PacketTypes.PacketType.Vehicles.send(udpConnection3);
                    }
                }
                break;
            }
            case 17: {
                final short short6 = byteBuffer.getShort();
                final short short7 = byteBuffer.getShort();
                final String readString2 = GameWindow.ReadString(byteBuffer);
                final String readString3 = GameWindow.ReadString(byteBuffer);
                final BaseVehicle value8 = this.IDToVehicle.get(short6);
                final BaseVehicle value9 = this.IDToVehicle.get(short7);
                if (value8 == null) {
                    break;
                }
                if (value9 == null) {
                    break;
                }
                value8.addPointConstraint(value9, readString2, readString3);
                if (value8.getDriver() != null && value8.getVehicleTowing() != null) {
                    value8.getVehicleTowing().setNetPlayerAuthorization((byte)3);
                    value8.getVehicleTowing().netPlayerId = value8.getDriver().getOnlineID();
                    value8.getVehicleTowing().netPlayerTimeout = 30;
                }
                for (int l = 0; l < GameServer.udpEngine.connections.size(); ++l) {
                    final UdpConnection udpConnection4 = GameServer.udpEngine.connections.get(l);
                    if (udpConnection4.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket2 = udpConnection4.startPacket();
                        PacketTypes.PacketType.Vehicles.doPacket(startPacket2);
                        startPacket2.bb.put((byte)17);
                        startPacket2.bb.putShort(value8.VehicleID);
                        startPacket2.bb.putShort(value9.VehicleID);
                        GameWindow.WriteString(startPacket2.bb, readString2);
                        GameWindow.WriteString(startPacket2.bb, readString3);
                        PacketTypes.PacketType.Vehicles.send(udpConnection4);
                    }
                }
                break;
            }
            case 15: {
                final short short8 = byteBuffer.getShort();
                final short short9 = byteBuffer.getShort();
                final boolean b2 = byteBuffer.get() == 1;
                final BaseVehicle value10 = this.IDToVehicle.get(short8);
                if (value10 == null) {
                    break;
                }
                value10.authorizationServerCollide(short9, b2);
                break;
            }
            case 4: {
                final short short10 = byteBuffer.getShort();
                final byte value11 = byteBuffer.get();
                final short short11 = byteBuffer.getShort();
                final BaseVehicle value12 = this.IDToVehicle.get(short10);
                if (value12 == null) {
                    break;
                }
                final IsoGameCharacter character3 = value12.getCharacter(value11);
                if (character3 != null) {
                    final IsoPlayer isoPlayer4 = GameServer.IDToPlayerMap.get(short11);
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (isoPlayer4 == null) ? "unknown player" : isoPlayer4.getUsername(), ((IsoPlayer)character3).getUsername()));
                    return;
                }
                for (int n = 0; n < GameServer.udpEngine.connections.size(); ++n) {
                    final UdpConnection udpConnection5 = GameServer.udpEngine.connections.get(n);
                    int n2 = 0;
                    while (n2 < 4) {
                        final IsoPlayer isoPlayer5 = udpConnection5.players[n2];
                        if (isoPlayer5 != null && isoPlayer5.OnlineID == short11) {
                            value12.switchSeat(isoPlayer5, value11);
                            this.sendSwichSeat(value12, value11, isoPlayer5);
                            if (value12.getDriver() == isoPlayer5) {
                                value12.authorizationServerOnSeat();
                                break;
                            }
                            break;
                        }
                        else {
                            ++n2;
                        }
                    }
                }
                break;
            }
            case 3: {
                final short short12 = byteBuffer.getShort();
                final short short13 = byteBuffer.getShort();
                final BaseVehicle value13 = this.IDToVehicle.get(short12);
                if (value13 == null) {
                    break;
                }
                for (int n3 = 0; n3 < GameServer.udpEngine.connections.size(); ++n3) {
                    final UdpConnection udpConnection6 = GameServer.udpEngine.connections.get(n3);
                    int n4 = 0;
                    while (n4 < 4) {
                        final IsoPlayer isoPlayer6 = udpConnection6.players[n4];
                        if (isoPlayer6 != null && isoPlayer6.OnlineID == short13) {
                            value13.exit(isoPlayer6);
                            this.sendRExit(value13, isoPlayer6);
                            if (value13.getVehicleTowedBy() == null) {
                                value13.authorizationServerOnSeat();
                                break;
                            }
                            break;
                        }
                        else {
                            ++n4;
                        }
                    }
                }
                break;
            }
            case 16: {
                final short short14 = byteBuffer.getShort();
                final byte value14 = byteBuffer.get();
                final BaseVehicle value15 = this.IDToVehicle.get(short14);
                if (value15 == null) {
                    break;
                }
                for (int n5 = 0; n5 < GameServer.udpEngine.connections.size(); ++n5) {
                    final UdpConnection udpConnection7 = GameServer.udpEngine.connections.get(n5);
                    if (udpConnection7 != udpConnection) {
                        final ByteBufferWriter startPacket3 = udpConnection7.startPacket();
                        PacketTypes.PacketType.Vehicles.doPacket(startPacket3);
                        startPacket3.bb.put((byte)16);
                        startPacket3.bb.putShort(value15.VehicleID);
                        startPacket3.bb.put(value14);
                        PacketTypes.PacketType.Vehicles.send(udpConnection7);
                    }
                }
                break;
            }
            case 9: {
                final short short15 = byteBuffer.getShort();
                final BaseVehicle value16 = this.IDToVehicle.get(short15);
                if (value16 == null) {
                    break;
                }
                if (!value16.authorizationServerOnOwnerData(udpConnection)) {
                    break;
                }
                final float[] tempFloats = this.tempFloats;
                byteBuffer.getLong();
                value16.physics.clientForce = byteBuffer.getFloat();
                for (int n6 = 0; n6 < tempFloats.length; ++n6) {
                    tempFloats[n6] = byteBuffer.getFloat();
                }
                value16.netLinearVelocity.x = tempFloats[7];
                value16.netLinearVelocity.y = tempFloats[8];
                value16.netLinearVelocity.z = tempFloats[9];
                WorldSimulation.instance.setOwnVehiclePhysics(short15, tempFloats);
                break;
            }
            case 11: {
                for (short short16 = byteBuffer.getShort(), n7 = 0; n7 < short16; ++n7) {
                    final short short17 = byteBuffer.getShort();
                    DebugLog.log(DebugType.Vehicle, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short17));
                    final BaseVehicle value17 = this.IDToVehicle.get(short17);
                    if (value17 != null) {
                        final BaseVehicle.ServerVehicleState serverVehicleState = value17.connectionState[udpConnection.index];
                        serverVehicleState.flags |= 0x1;
                        this.sendVehicles(udpConnection);
                    }
                }
                break;
            }
            case 12: {
                final BaseVehicle value18 = this.IDToVehicle.get(byteBuffer.getShort());
                if (value18 != null) {
                    final BaseVehicle baseVehicle = value18;
                    baseVehicle.updateFlags |= 0x2;
                    this.sendVehicles(udpConnection);
                    break;
                }
                break;
            }
            case 14: {
                final short short18 = byteBuffer.getShort();
                final float float1 = byteBuffer.getFloat();
                final float float2 = byteBuffer.getFloat();
                final BaseVehicle value19 = this.IDToVehicle.get(short18);
                if (value19 != null) {
                    value19.engineSpeed = float1;
                    value19.throttle = float2;
                    final BaseVehicle baseVehicle2 = value19;
                    baseVehicle2.updateFlags |= 0x2000;
                    break;
                }
                break;
            }
            default: {
                this.noise(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, value));
                break;
            }
        }
    }
    
    public static void serverSendVehiclesConfig(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)10);
        startPacket.bb.putShort((short)ServerOptions.getInstance().PhysicsDelay.getValue());
        PacketTypes.PacketType.Vehicles.send(udpConnection);
    }
    
    private void vehiclePosUpdate(final BaseVehicle baseVehicle, final float[] array) {
        int n = 0;
        final Transform transform = this.posUpdateVars.transform;
        final Vector3f vector3f = this.posUpdateVars.vector3f;
        final Quaternionf quatf = this.posUpdateVars.quatf;
        final float[] wheelSteer = this.posUpdateVars.wheelSteer;
        final float[] wheelRotation = this.posUpdateVars.wheelRotation;
        final float[] wheelSkidInfo = this.posUpdateVars.wheelSkidInfo;
        final float[] wheelSuspensionLength = this.posUpdateVars.wheelSuspensionLength;
        transform.origin.set(array[n++] - WorldSimulation.instance.offsetX, array[n++], array[n++] - WorldSimulation.instance.offsetY);
        quatf.set(array[n++], array[n++], array[n++], array[n++]);
        quatf.normalize();
        transform.setRotation(quatf);
        vector3f.set(array[n++], array[n++], array[n++]);
        final float n2 = array[n++];
        for (int i = 0; i < 4; ++i) {
            wheelSteer[i] = array[n++];
            wheelRotation[i] = array[n++];
            wheelSkidInfo[i] = array[n++];
            wheelSuspensionLength[i] = array[n++];
        }
        baseVehicle.jniTransform.set(transform);
        baseVehicle.jniLinearVelocity.set((Vector3fc)vector3f);
        baseVehicle.netLinearVelocity.set((Vector3fc)vector3f);
        baseVehicle.jniTransform.basis.getScale(vector3f);
        if (vector3f.x < 0.99 || vector3f.y < 0.99 || vector3f.z < 0.99) {
            baseVehicle.jniTransform.basis.scale(1.0f / vector3f.x, 1.0f / vector3f.y, 1.0f / vector3f.z);
        }
        baseVehicle.jniSpeed = baseVehicle.jniLinearVelocity.length();
        for (int j = 0; j < 4; ++j) {
            baseVehicle.wheelInfo[j].steering = wheelSteer[j];
            baseVehicle.wheelInfo[j].rotation = wheelRotation[j];
            baseVehicle.wheelInfo[j].skidInfo = wheelSkidInfo[j];
            baseVehicle.wheelInfo[j].suspensionLength = wheelSuspensionLength[j];
        }
        baseVehicle.polyDirty = true;
    }
    
    public void clientUpdate() {
        if (this.vehiclesWaitUpdatesFrequency.Check()) {
            if (this.vehiclesWaitUpdates.size() > 0) {
                final ByteBufferWriter startPacket = GameClient.connection.startPacket();
                PacketTypes.PacketType.Vehicles.doPacket(startPacket);
                startPacket.bb.put((byte)11);
                startPacket.bb.putShort((short)this.vehiclesWaitUpdates.size());
                for (int i = 0; i < this.vehiclesWaitUpdates.size(); ++i) {
                    startPacket.bb.putShort(this.vehiclesWaitUpdates.get(i));
                }
                PacketTypes.PacketType.Vehicles.send(GameClient.connection);
            }
            this.vehiclesWaitUpdates.clear();
        }
        final ArrayList<BaseVehicle> vehicles = this.getVehicles();
        for (int j = 0; j < vehicles.size(); ++j) {
            final BaseVehicle vehicleData = vehicles.get(j);
            if (vehicleData.isKeyboardControlled() || vehicleData.getJoypad() != -1) {
                vehicleData.interpolation.setVehicleData(vehicleData);
            }
            else {
                final float[] tempFloats = this.tempFloats;
                if (vehicleData.interpolation.interpolationDataGetPR(tempFloats) && vehicleData.netPlayerAuthorization != 3) {
                    if (vehicleData.netPlayerAuthorization != 1) {
                        Bullet.setOwnVehiclePhysics(vehicleData.VehicleID, tempFloats);
                        int n = 0;
                        final float n2 = tempFloats[n++];
                        final float n3 = tempFloats[n++];
                        this.clientUpdateVehiclePos(vehicleData, n2, n3, tempFloats[n++], IsoWorld.instance.CurrentCell.getGridSquare(n2, n3, 0.0));
                        vehicleData.limitPhysicValid.BlockCheck();
                        if (GameClient.bClient) {
                            this.vehiclePosUpdate(vehicleData, tempFloats);
                        }
                    }
                }
            }
        }
    }
    
    private void clientUpdateVehiclePos(final BaseVehicle e, final float x, final float y, final float n, final IsoGridSquare square) {
        e.setX(x);
        e.setY(y);
        e.setZ(0.0f);
        e.setCurrent(e.square = square);
        if (square != null) {
            if (e.chunk != null && e.chunk != square.chunk) {
                e.chunk.vehicles.remove(e);
            }
            e.chunk = e.square.chunk;
            if (!e.chunk.vehicles.contains(e)) {
                e.chunk.vehicles.add(e);
                IsoChunk.addFromCheckedVehicles(e);
            }
            if (!e.addedToWorld) {
                e.addToWorld();
            }
        }
        else {
            e.removeFromWorld();
            e.removeFromSquare();
        }
        e.polyDirty = true;
    }
    
    private void clientReceiveUpdateFull(final ByteBuffer byteBuffer, final short vehicleID, final float n, final float n2, final float n3) throws IOException {
        final byte value = byteBuffer.get();
        final short short1 = byteBuffer.getShort();
        byteBuffer.getShort();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, 0.0);
        if (this.IDToVehicle.containsKey(vehicleID)) {
            final BaseVehicle value2 = this.IDToVehicle.get(vehicleID);
            this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, vehicleID));
            byteBuffer.get();
            byteBuffer.get();
            this.tempVehicle.parts.clear();
            this.tempVehicle.load(byteBuffer, 186);
            if (value2.physics != null && (value2.getDriver() == null || !value2.getDriver().isLocal())) {
                this.tempTransform.setRotation(this.tempVehicle.savedRot);
                this.tempTransform.origin.set(n - WorldSimulation.instance.offsetX, n3, n2 - WorldSimulation.instance.offsetY);
                value2.setWorldTransform(this.tempTransform);
            }
            value2.netPlayerFromServerUpdate(value, short1);
            this.clientUpdateVehiclePos(value2, n, n2, n3, gridSquare);
        }
        else {
            final boolean b = byteBuffer.get() != 0;
            final byte value3 = byteBuffer.get();
            if (!b || value3 != IsoObject.getFactoryVehicle().getClassID()) {
                DebugLog.log("Error: clientReceiveUpdateFull: packet broken");
            }
            final BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
            if (baseVehicle == null || !(baseVehicle instanceof BaseVehicle)) {
                return;
            }
            final BaseVehicle e = baseVehicle;
            e.VehicleID = vehicleID;
            e.setCurrent(e.square = gridSquare);
            e.load(byteBuffer, 186);
            if (gridSquare != null) {
                e.chunk = e.square.chunk;
                e.chunk.vehicles.add(e);
                e.addToWorld();
            }
            IsoChunk.addFromCheckedVehicles(e);
            e.netPlayerFromServerUpdate(value, short1);
            this.registerVehicle(e);
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && !isoPlayer.isDead()) {
                    if (isoPlayer.getVehicle() == null) {
                        IsoWorld.instance.CurrentCell.putInVehicle(isoPlayer);
                    }
                }
            }
            if (e.trace) {
                this.noise(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, e.VehicleID, (gridSquare == null) ? " (delayed)" : ""));
            }
        }
    }
    
    private void clientReceiveUpdate(final ByteBuffer byteBuffer) throws IOException {
        final short short1 = byteBuffer.getShort();
        final short short2 = byteBuffer.getShort();
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        final short short3 = byteBuffer.getShort();
        VehicleCache.vehicleUpdate(short1, float1, float2, 0.0f);
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(float1, float2, 0.0);
        BaseVehicle baseVehicle = this.IDToVehicle.get(short1);
        if (baseVehicle == null && gridSquare == null) {
            if (byteBuffer.limit() > byteBuffer.position() + short3) {
                byteBuffer.position(byteBuffer.position() + short3);
            }
            return;
        }
        if (baseVehicle != null && gridSquare == null) {
            boolean b = true;
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && isoPlayer.getVehicle() == baseVehicle) {
                    b = false;
                    isoPlayer.setPosition(float1, float2, 0.0f);
                    this.sendReqestGetPosition(short1);
                }
            }
            if (b) {
                baseVehicle.removeFromWorld();
                baseVehicle.removeFromSquare();
            }
            if (byteBuffer.limit() > byteBuffer.position() + short3) {
                byteBuffer.position(byteBuffer.position() + short3);
            }
            return;
        }
        if ((short2 & 0x1) != 0x0) {
            DebugLog.Vehicle.debugln(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
            this.clientReceiveUpdateFull(byteBuffer, short1, float1, float2, float3);
            if (baseVehicle == null) {
                baseVehicle = this.IDToVehicle.get(short1);
            }
            if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
                byteBuffer.getLong();
                int j = 0;
                final float[] tempFloats = this.tempFloats;
                tempFloats[j++] = float1;
                tempFloats[j++] = float2;
                tempFloats[j++] = float3;
                while (j < 10) {
                    tempFloats[j++] = byteBuffer.getFloat();
                }
                final short short4 = byteBuffer.getShort();
                tempFloats[j++] = short4;
                for (short n = 0; n < short4; ++n) {
                    tempFloats[j++] = byteBuffer.getFloat();
                    tempFloats[j++] = byteBuffer.getFloat();
                    tempFloats[j++] = byteBuffer.getFloat();
                    tempFloats[j++] = byteBuffer.getFloat();
                }
                Bullet.setOwnVehiclePhysics(short1, tempFloats);
            }
            else if (byteBuffer.limit() > byteBuffer.position() + 102) {
                byteBuffer.position(byteBuffer.position() + 102);
            }
            final int index = this.vehiclesWaitUpdates.indexOf(short1);
            if (index >= 0) {
                this.vehiclesWaitUpdates.removeAt(index);
            }
            return;
        }
        if (baseVehicle == null && gridSquare != null) {
            this.sendRequestGetFull(short1);
            if (byteBuffer.limit() > byteBuffer.position() + short3) {
                byteBuffer.position(byteBuffer.position() + short3);
            }
            return;
        }
        if ((short2 & 0x4000) != 0x0) {
            final byte value = byteBuffer.get();
            final short short5 = byteBuffer.getShort();
            if (baseVehicle != null) {
                baseVehicle.netPlayerFromServerUpdate(value, short5);
            }
        }
        if ((short2 & 0x2) != 0x0) {
            if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
                baseVehicle.interpolation.interpolationDataAdd(byteBuffer, float1, float2, float3);
            }
            else if (byteBuffer.limit() > byteBuffer.position() + 102) {
                byteBuffer.position(byteBuffer.position() + 102);
            }
        }
        if ((short2 & 0x4) != 0x0) {
            this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
            final byte value2 = byteBuffer.get();
            if (value2 >= 0 && value2 < BaseVehicle.engineStateTypes.Values.length) {
                switch (BaseVehicle.engineStateTypes.Values[value2]) {
                    case Idle: {
                        baseVehicle.engineDoIdle();
                    }
                    case RetryingStarting: {
                        baseVehicle.engineDoRetryingStarting();
                        break;
                    }
                    case StartingSuccess: {
                        baseVehicle.engineDoStartingSuccess();
                        break;
                    }
                    case StartingFailed: {
                        baseVehicle.engineDoStartingFailed();
                        break;
                    }
                    case StartingFailedNoPower: {
                        baseVehicle.engineDoStartingFailedNoPower();
                        break;
                    }
                    case Running: {
                        baseVehicle.engineDoRunning();
                        break;
                    }
                    case Stalling: {
                        baseVehicle.engineDoStalling();
                        break;
                    }
                    case ShutingDown: {
                        baseVehicle.engineDoShuttingDown();
                        break;
                    }
                }
                baseVehicle.engineLoudness = byteBuffer.getInt();
                baseVehicle.enginePower = byteBuffer.getInt();
                baseVehicle.engineQuality = byteBuffer.getInt();
            }
            else {
                DebugLog.log("ERROR: VehicleManager.clientReceiveUpdate get invalid data");
            }
        }
        if ((short2 & 0x1000) != 0x0) {
            this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
            baseVehicle.setHotwired(byteBuffer.get() == 1);
            baseVehicle.setHotwiredBroken(byteBuffer.get() == 1);
            final boolean b2 = byteBuffer.get() == 1;
            final boolean b3 = byteBuffer.get() == 1;
            InventoryItem loadItem = null;
            if (byteBuffer.get() == 1) {
                try {
                    loadItem = InventoryItem.loadItem(byteBuffer, 186);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            baseVehicle.syncKeyInIgnition(b2, b3, loadItem);
            baseVehicle.setRust(byteBuffer.getFloat());
            baseVehicle.setBloodIntensity("Front", byteBuffer.getFloat());
            baseVehicle.setBloodIntensity("Rear", byteBuffer.getFloat());
            baseVehicle.setBloodIntensity("Left", byteBuffer.getFloat());
            baseVehicle.setBloodIntensity("Right", byteBuffer.getFloat());
        }
        if ((short2 & 0x8) != 0x0) {
            this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
            baseVehicle.setHeadlightsOn(byteBuffer.get() == 1);
            baseVehicle.setStoplightsOn(byteBuffer.get() == 1);
            for (int k = 0; k < baseVehicle.getLightCount(); ++k) {
                baseVehicle.getLightByIndex(k).getLight().setActive(byteBuffer.get() == 1);
            }
        }
        if ((short2 & 0x400) != 0x0) {
            this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
            final boolean b4 = byteBuffer.get() == 1;
            final boolean b5 = byteBuffer.get() == 1;
            final byte value3 = byteBuffer.get();
            final byte value4 = byteBuffer.get();
            if (b4 != baseVehicle.soundHornOn) {
                if (b4) {
                    baseVehicle.onHornStart();
                }
                else {
                    baseVehicle.onHornStop();
                }
            }
            if (b5 != baseVehicle.soundBackMoveOn) {
                if (b5) {
                    baseVehicle.onBackMoveSignalStart();
                }
                else {
                    baseVehicle.onBackMoveSignalStop();
                }
            }
            if (baseVehicle.lightbarLightsMode.get() != value3) {
                baseVehicle.setLightbarLightsMode(value3);
            }
            if (baseVehicle.lightbarSirenMode.get() != value4) {
                baseVehicle.setLightbarSirenMode(value4);
            }
        }
        if ((short2 & 0x800) != 0x0) {
            for (byte b6 = byteBuffer.get(); b6 != -1; b6 = byteBuffer.get()) {
                final VehiclePart partByIndex = baseVehicle.getPartByIndex(b6);
                this.noise(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, short1, partByIndex.getId()));
                final VehiclePart vehiclePart = partByIndex;
                vehiclePart.updateFlags |= 0x800;
                partByIndex.setCondition(byteBuffer.getInt());
            }
            baseVehicle.doDamageOverlay();
        }
        if ((short2 & 0x10) != 0x0) {
            for (byte b7 = byteBuffer.get(); b7 != -1; b7 = byteBuffer.get()) {
                final VehiclePart partByIndex2 = baseVehicle.getPartByIndex(b7);
                this.noise(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, short1, partByIndex2.getId()));
                partByIndex2.getModData().load(byteBuffer, 186);
                if (partByIndex2.isContainer()) {
                    partByIndex2.setContainerContentAmount(partByIndex2.getContainerContentAmount());
                }
            }
        }
        if ((short2 & 0x20) != 0x0) {
            for (byte b8 = byteBuffer.get(); b8 != -1; b8 = byteBuffer.get()) {
                final float float4 = byteBuffer.getFloat();
                final VehiclePart partByIndex3 = baseVehicle.getPartByIndex(b8);
                this.noise(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, short1, partByIndex3.getId()));
                final InventoryItem inventoryItem = partByIndex3.getInventoryItem();
                if (inventoryItem instanceof DrainableComboItem) {
                    ((DrainableComboItem)inventoryItem).setUsedDelta(float4);
                }
            }
        }
        if ((short2 & 0x80) != 0x0) {
            for (byte b9 = byteBuffer.get(); b9 != -1; b9 = byteBuffer.get()) {
                final VehiclePart partByIndex4 = baseVehicle.getPartByIndex(b9);
                this.noise(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, short1, partByIndex4.getId()));
                final VehiclePart vehiclePart2 = partByIndex4;
                vehiclePart2.updateFlags |= 0x80;
                final boolean b10 = byteBuffer.get() != 0;
                if (b10) {
                    InventoryItem loadItem2;
                    try {
                        loadItem2 = InventoryItem.loadItem(byteBuffer, 186);
                    }
                    catch (Exception ex2) {
                        ex2.printStackTrace();
                        return;
                    }
                    if (loadItem2 != null) {
                        partByIndex4.setInventoryItem(loadItem2);
                    }
                }
                else {
                    partByIndex4.setInventoryItem(null);
                }
                final int wheelIndex = partByIndex4.getWheelIndex();
                if (wheelIndex != -1) {
                    baseVehicle.setTireRemoved(wheelIndex, !b10);
                }
                if (partByIndex4.isContainer()) {
                    LuaEventManager.triggerEvent("OnContainerUpdate");
                }
            }
        }
        if ((short2 & 0x200) != 0x0) {
            for (byte b11 = byteBuffer.get(); b11 != -1; b11 = byteBuffer.get()) {
                final VehiclePart partByIndex5 = baseVehicle.getPartByIndex(b11);
                this.noise(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, short1, partByIndex5.getId()));
                partByIndex5.getDoor().load(byteBuffer, 186);
            }
            LuaEventManager.triggerEvent("OnContainerUpdate");
            baseVehicle.doDamageOverlay();
        }
        if ((short2 & 0x100) != 0x0) {
            for (byte b12 = byteBuffer.get(); b12 != -1; b12 = byteBuffer.get()) {
                final VehiclePart partByIndex6 = baseVehicle.getPartByIndex(b12);
                this.noise(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, short1, partByIndex6.getId()));
                partByIndex6.getWindow().load(byteBuffer, 186);
            }
            baseVehicle.doDamageOverlay();
        }
        if ((short2 & 0x40) != 0x0) {
            this.oldModels.clear();
            this.oldModels.addAll(baseVehicle.models);
            this.curModels.clear();
            for (byte value5 = byteBuffer.get(), b13 = 0; b13 < value5; ++b13) {
                final byte value6 = byteBuffer.get();
                final byte value7 = byteBuffer.get();
                final VehiclePart partByIndex7 = baseVehicle.getPartByIndex(value6);
                this.curModels.add(baseVehicle.setModelVisible(partByIndex7, partByIndex7.getScriptPart().models.get(value7), true));
            }
            for (int l = 0; l < this.oldModels.size(); ++l) {
                final BaseVehicle.ModelInfo o = this.oldModels.get(l);
                if (!this.curModels.contains(o)) {
                    baseVehicle.setModelVisible(o.part, o.scriptModel, false);
                }
            }
            baseVehicle.doDamageOverlay();
        }
        if ((short2 & 0x2000) != 0x0) {
            final float float5 = byteBuffer.getFloat();
            final float float6 = byteBuffer.getFloat();
            if (!(baseVehicle.getDriver() instanceof IsoPlayer) || !((IsoPlayer)baseVehicle.getDriver()).isLocalPlayer()) {
                baseVehicle.engineSpeed = float5;
                baseVehicle.throttle = float6;
            }
        }
        boolean b14 = false;
        for (int n2 = 0; n2 < baseVehicle.getPartCount(); ++n2) {
            final VehiclePart partByIndex8 = baseVehicle.getPartByIndex(n2);
            if (partByIndex8.updateFlags != 0) {
                if ((partByIndex8.updateFlags & 0x800) != 0x0 && (partByIndex8.updateFlags & 0x80) == 0x0) {
                    partByIndex8.doInventoryItemStats(partByIndex8.getInventoryItem(), partByIndex8.getMechanicSkillInstaller());
                    b14 = true;
                }
                partByIndex8.updateFlags = 0;
            }
        }
        if (b14) {
            baseVehicle.updatePartStats();
            baseVehicle.updateBulletStats();
        }
    }
    
    public void clientPacket(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        switch (value) {
            case 1: {
                final short short1 = byteBuffer.getShort();
                final byte value2 = byteBuffer.get();
                final String readString = GameWindow.ReadString(byteBuffer);
                final BaseVehicle value3 = this.IDToVehicle.get(short1);
                if (value3 == null) {
                    break;
                }
                final IsoGameCharacter character = value3.getCharacter(value2);
                if (character == null) {
                    break;
                }
                value3.setCharacterPosition(character, value2, readString);
                break;
            }
            case 16: {
                final short short2 = byteBuffer.getShort();
                byteBuffer.get();
                final BaseVehicle value4 = this.IDToVehicle.get(short2);
                if (value4 == null) {
                    break;
                }
                SoundManager.instance.PlayWorldSound("VehicleCrash", value4.square, 1.0f, 20.0f, 1.0f, true);
                break;
            }
            case 18: {
                final boolean b = byteBuffer.get() == 1;
                short short3 = -1;
                short short4 = -1;
                if (b) {
                    short3 = byteBuffer.getShort();
                }
                if (byteBuffer.get() == 1) {
                    short4 = byteBuffer.getShort();
                }
                final BaseVehicle value5 = this.IDToVehicle.get(short3);
                final BaseVehicle value6 = this.IDToVehicle.get(short4);
                if (value5 == null && value6 == null) {
                    break;
                }
                if (value5 != null) {
                    value5.breakConstraint(true, true);
                }
                if (value6 != null) {
                    value6.breakConstraint(true, true);
                    break;
                }
                break;
            }
            case 17: {
                final short short5 = byteBuffer.getShort();
                final short short6 = byteBuffer.getShort();
                final String readString2 = GameWindow.ReadString(byteBuffer);
                final String readString3 = GameWindow.ReadString(byteBuffer);
                final BaseVehicle value7 = this.IDToVehicle.get(short5);
                final BaseVehicle value8 = this.IDToVehicle.get(short6);
                if (value7 == null) {
                    break;
                }
                if (value8 == null) {
                    break;
                }
                value7.addPointConstraint(value8, readString2, readString3, null, true);
                break;
            }
            case 5: {
                if (this.tempVehicle == null || this.tempVehicle.getCell() != IsoWorld.instance.CurrentCell) {
                    this.tempVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
                }
                for (short short7 = byteBuffer.getShort(), n = 0; n < short7; ++n) {
                    try {
                        this.clientReceiveUpdate(byteBuffer);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
                break;
            }
            case 8: {
                final short short8 = byteBuffer.getShort();
                if (this.IDToVehicle.containsKey(short8)) {
                    final BaseVehicle value9 = this.IDToVehicle.get(short8);
                    if (value9.trace) {
                        this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short8));
                    }
                    value9.serverRemovedFromWorld = true;
                    try {
                        value9.removeFromWorld();
                        value9.removeFromSquare();
                    }
                    finally {
                        if (this.IDToVehicle.containsKey(short8)) {
                            this.unregisterVehicle(value9);
                        }
                    }
                }
                VehicleCache.remove(short8);
                break;
            }
            case 6: {
                final short short9 = byteBuffer.getShort();
                final byte value10 = byteBuffer.get();
                final short short10 = byteBuffer.getShort();
                final BaseVehicle value11 = this.IDToVehicle.get(short9);
                if (value11 == null) {
                    break;
                }
                final IsoPlayer isoPlayer = GameClient.IDToPlayerMap.get(short10);
                if (isoPlayer == null) {
                    break;
                }
                final IsoGameCharacter character2 = value11.getCharacter(value10);
                if (character2 == null) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SI)Ljava/lang/String;, isoPlayer.getUsername(), value11.VehicleID, value10));
                    value11.enterRSync(value10, isoPlayer, value11);
                    break;
                }
                if (isoPlayer != character2) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoPlayer.getUsername(), ((IsoPlayer)character2).getUsername()));
                    break;
                }
                break;
            }
            case 7: {
                final short short11 = byteBuffer.getShort();
                final short short12 = byteBuffer.getShort();
                final BaseVehicle value12 = this.IDToVehicle.get(short11);
                if (value12 == null) {
                    break;
                }
                final IsoPlayer isoPlayer2 = GameClient.IDToPlayerMap.get(short12);
                if (isoPlayer2 == null) {
                    break;
                }
                value12.exitRSync(isoPlayer2);
                break;
            }
            case 4: {
                final short short13 = byteBuffer.getShort();
                final byte value13 = byteBuffer.get();
                final short short14 = byteBuffer.getShort();
                final BaseVehicle value14 = this.IDToVehicle.get(short13);
                if (value14 == null) {
                    break;
                }
                final IsoPlayer isoPlayer3 = GameClient.IDToPlayerMap.get(short14);
                if (isoPlayer3 == null) {
                    break;
                }
                final IsoGameCharacter character3 = value14.getCharacter(value13);
                if (character3 == null) {
                    value14.switchSeatRSync(isoPlayer3, value13);
                    break;
                }
                if (isoPlayer3 != character3) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoPlayer3.getUsername(), ((IsoPlayer)character3).getUsername()));
                    break;
                }
                break;
            }
            case 13: {
                final short short15 = byteBuffer.getShort();
                final Vector3f vector3f = new Vector3f();
                final Vector3f vector3f2 = new Vector3f();
                vector3f.x = byteBuffer.getFloat();
                vector3f.y = byteBuffer.getFloat();
                vector3f.z = byteBuffer.getFloat();
                vector3f2.x = byteBuffer.getFloat();
                vector3f2.y = byteBuffer.getFloat();
                vector3f2.z = byteBuffer.getFloat();
                final BaseVehicle value15 = this.IDToVehicle.get(short15);
                if (value15 == null) {
                    break;
                }
                Bullet.applyCentralForceToVehicle(value15.VehicleID, vector3f.x, vector3f.y, vector3f.z);
                final Vector3f cross = vector3f2.cross((Vector3fc)vector3f);
                Bullet.applyTorqueToVehicle(value15.VehicleID, cross.x, cross.y, cross.z);
                break;
            }
            default: {
                this.noise(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, value));
                break;
            }
        }
    }
    
    public static void loadingClientPacket(final ByteBuffer byteBuffer) {
        final int position = byteBuffer.position();
        switch (byteBuffer.get()) {
            case 10: {
                VehicleManager.physicsDelay = byteBuffer.getShort();
                break;
            }
        }
        byteBuffer.position(position);
    }
    
    public void sendCollide(final BaseVehicle baseVehicle, final IsoGameCharacter isoGameCharacter, final boolean b) {
        if (isoGameCharacter == null) {
            return;
        }
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)15);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        startPacket.bb.putShort(((IsoPlayer)isoGameCharacter).OnlineID);
        startPacket.bb.put((byte)(b ? 1 : 0));
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public void sendEnter(final BaseVehicle baseVehicle, final int n, final IsoGameCharacter isoGameCharacter) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)2);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        startPacket.bb.put((byte)n);
        startPacket.bb.putShort(((IsoPlayer)isoGameCharacter).OnlineID);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public static void sendSound(final BaseVehicle baseVehicle, final byte b) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)16);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        startPacket.bb.put(b);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public static void sendSoundFromServer(final BaseVehicle baseVehicle, final byte b) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(startPacket);
            startPacket.bb.put((byte)16);
            startPacket.bb.putShort(baseVehicle.VehicleID);
            startPacket.bb.put(b);
            PacketTypes.PacketType.Vehicles.send(udpConnection);
        }
    }
    
    public void sendPassengerPosition(final BaseVehicle baseVehicle, final int n, final String s) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)1);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        startPacket.bb.put((byte)n);
        startPacket.putUTF(s);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public void sendPassengerPosition(final BaseVehicle baseVehicle, final int n, final String s, final UdpConnection udpConnection) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2 != udpConnection) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.Vehicles.doPacket(startPacket);
                startPacket.bb.put((byte)1);
                startPacket.bb.putShort(baseVehicle.VehicleID);
                startPacket.bb.put((byte)n);
                startPacket.putUTF(s);
                PacketTypes.PacketType.Vehicles.send(udpConnection2);
            }
        }
    }
    
    public void sendRequestGetFull(final short n) {
        if (this.vehiclesWaitUpdates.contains(n)) {
            return;
        }
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)11);
        startPacket.bb.putShort((short)1);
        startPacket.bb.putShort(n);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
        this.vehiclesWaitUpdates.add(n);
        DebugLog.log(DebugType.Vehicle, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
    }
    
    public void sendRequestGetFull(final List<VehicleCache> list) {
        if (list == null) {
            return;
        }
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)11);
        startPacket.bb.putShort((short)list.size());
        for (int i = 0; i < list.size(); ++i) {
            startPacket.bb.putShort(list.get(i).id);
            this.vehiclesWaitUpdates.add(list.get(i).id);
        }
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public void sendReqestGetPosition(final short n) {
        if (!VehicleManager.sendReqestGetPositionFrequency.Check()) {
            return;
        }
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)12);
        startPacket.bb.putShort(n);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
        this.vehiclesWaitUpdates.add(n);
    }
    
    public void sendAddImpulse(final BaseVehicle baseVehicle, final Vector3f vector3f, final Vector3f vector3f2) {
        UdpConnection udpConnection = null;
        for (int n = 0; n < GameServer.udpEngine.connections.size() && udpConnection == null; ++n) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(n);
            for (int i = 0; i < udpConnection2.players.length; ++i) {
                final IsoPlayer isoPlayer = udpConnection2.players[i];
                if (isoPlayer != null && isoPlayer.getVehicle() != null && isoPlayer.getVehicle().VehicleID == baseVehicle.VehicleID) {
                    udpConnection = udpConnection2;
                    break;
                }
            }
        }
        if (udpConnection != null) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(startPacket);
            startPacket.bb.put((byte)13);
            startPacket.bb.putShort(baseVehicle.VehicleID);
            startPacket.bb.putFloat(vector3f.x);
            startPacket.bb.putFloat(vector3f.y);
            startPacket.bb.putFloat(vector3f.z);
            startPacket.bb.putFloat(vector3f2.x);
            startPacket.bb.putFloat(vector3f2.y);
            startPacket.bb.putFloat(vector3f2.z);
            PacketTypes.PacketType.Vehicles.send(udpConnection);
        }
    }
    
    public void sendREnter(final BaseVehicle baseVehicle, final int n, final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(startPacket);
            startPacket.bb.put((byte)6);
            startPacket.bb.putShort(baseVehicle.VehicleID);
            startPacket.bb.put((byte)n);
            startPacket.bb.putShort(((IsoPlayer)isoGameCharacter).OnlineID);
            PacketTypes.PacketType.Vehicles.send(udpConnection);
        }
    }
    
    public void sendSwichSeat(final BaseVehicle baseVehicle, final int n, final IsoGameCharacter isoGameCharacter) {
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(startPacket);
            startPacket.bb.put((byte)4);
            startPacket.bb.putShort(baseVehicle.VehicleID);
            startPacket.bb.put((byte)n);
            startPacket.bb.putShort(((IsoPlayer)isoGameCharacter).OnlineID);
            PacketTypes.PacketType.Vehicles.send(GameClient.connection);
        }
        if (GameServer.bServer) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                final ByteBufferWriter startPacket2 = udpConnection.startPacket();
                PacketTypes.PacketType.Vehicles.doPacket(startPacket2);
                startPacket2.bb.put((byte)4);
                startPacket2.bb.putShort(baseVehicle.VehicleID);
                startPacket2.bb.put((byte)n);
                startPacket2.bb.putShort(((IsoPlayer)isoGameCharacter).OnlineID);
                PacketTypes.PacketType.Vehicles.send(udpConnection);
            }
        }
    }
    
    public void sendExit(final BaseVehicle baseVehicle, final IsoGameCharacter isoGameCharacter) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)3);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        startPacket.bb.putShort(((IsoPlayer)isoGameCharacter).OnlineID);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public void sendRExit(final BaseVehicle baseVehicle, final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(startPacket);
            startPacket.bb.put((byte)7);
            startPacket.bb.putShort(baseVehicle.VehicleID);
            startPacket.bb.putShort(((IsoPlayer)isoGameCharacter).OnlineID);
            PacketTypes.PacketType.Vehicles.send(udpConnection);
        }
    }
    
    public void sendPhysic(final BaseVehicle baseVehicle) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType packetType;
        if (this.VehiclePhysicSyncPacketLimit.Check()) {
            packetType = PacketTypes.PacketType.Vehicles;
        }
        else {
            packetType = PacketTypes.PacketType.VehiclesUnreliable;
        }
        packetType.doPacket(startPacket);
        startPacket.bb.put((byte)9);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        final ByteBuffer bb = startPacket.bb;
        GameTime.getInstance();
        bb.putLong(GameTime.getServerTime());
        startPacket.bb.putFloat(baseVehicle.physics.EngineForce - baseVehicle.physics.BrakingForce);
        if (WorldSimulation.instance.getOwnVehiclePhysics(baseVehicle.VehicleID, startPacket) != 1) {
            GameClient.connection.cancelPacket();
            return;
        }
        packetType.send(GameClient.connection);
    }
    
    public void sendEngineSound(final BaseVehicle baseVehicle, final float n, final float n2) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)14);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        startPacket.bb.putFloat(n);
        startPacket.bb.putFloat(n2);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public void sendTowing(final BaseVehicle baseVehicle, final BaseVehicle baseVehicle2, final String s, final String s2, final Float n) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)17);
        startPacket.bb.putShort(baseVehicle.VehicleID);
        startPacket.bb.putShort(baseVehicle2.VehicleID);
        GameWindow.WriteString(startPacket.bb, s);
        GameWindow.WriteString(startPacket.bb, s2);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    public void sendDetachTowing(final BaseVehicle baseVehicle, final BaseVehicle baseVehicle2) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(startPacket);
        startPacket.bb.put((byte)18);
        if (baseVehicle != null) {
            startPacket.bb.put((byte)1);
            startPacket.bb.putShort(baseVehicle.VehicleID);
        }
        else {
            startPacket.bb.put((byte)0);
        }
        if (baseVehicle2 != null) {
            startPacket.bb.put((byte)1);
            startPacket.bb.putShort(baseVehicle2.VehicleID);
        }
        else {
            startPacket.bb.put((byte)0);
        }
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }
    
    private void writePositionOrientation(final ByteBuffer byteBuffer, final BaseVehicle baseVehicle) {
        byteBuffer.putLong(WorldSimulation.instance.time);
        final Quaternionf savedRot = baseVehicle.savedRot;
        baseVehicle.getWorldTransform(this.tempTransform).getRotation(savedRot);
        byteBuffer.putFloat(savedRot.x);
        byteBuffer.putFloat(savedRot.y);
        byteBuffer.putFloat(savedRot.z);
        byteBuffer.putFloat(savedRot.w);
        byteBuffer.putFloat(baseVehicle.netLinearVelocity.x);
        byteBuffer.putFloat(baseVehicle.netLinearVelocity.y);
        byteBuffer.putFloat(baseVehicle.netLinearVelocity.z);
        byteBuffer.putShort((short)baseVehicle.wheelInfo.length);
        for (int i = 0; i < baseVehicle.wheelInfo.length; ++i) {
            byteBuffer.putFloat(baseVehicle.wheelInfo[i].steering);
            byteBuffer.putFloat(baseVehicle.wheelInfo[i].rotation);
            byteBuffer.putFloat(baseVehicle.wheelInfo[i].skidInfo);
            byteBuffer.putFloat(baseVehicle.wheelInfo[i].suspensionLength);
        }
    }
    
    static {
        VehicleManager.physicsDelay = 100;
        VehicleManager.sendReqestGetPositionFrequency = new UpdateLimit(500L);
    }
    
    public static final class VehiclePacket
    {
        public static final byte PassengerPosition = 1;
        public static final byte Enter = 2;
        public static final byte Exit = 3;
        public static final byte SwichSeat = 4;
        public static final byte Update = 5;
        public static final byte REnter = 6;
        public static final byte RExit = 7;
        public static final byte Remove = 8;
        public static final byte Physic = 9;
        public static final byte Config = 10;
        public static final byte RequestGetFull = 11;
        public static final byte RequestGetPosition = 12;
        public static final byte AddImpulse = 13;
        public static final byte EngineSound = 14;
        public static final byte Collide = 15;
        public static final byte Sound = 16;
        public static final byte TowingCar = 17;
        public static final byte DetachTowingCar = 18;
        public static final byte Sound_Crash = 1;
    }
    
    public static final class PosUpdateVars
    {
        final Transform transform;
        final Vector3f vector3f;
        final Quaternionf quatf;
        final float[] wheelSteer;
        final float[] wheelRotation;
        final float[] wheelSkidInfo;
        final float[] wheelSuspensionLength;
        
        public PosUpdateVars() {
            this.transform = new Transform();
            this.vector3f = new Vector3f();
            this.quatf = new Quaternionf();
            this.wheelSteer = new float[4];
            this.wheelRotation = new float[4];
            this.wheelSkidInfo = new float[4];
            this.wheelSuspensionLength = new float[4];
        }
    }
}
