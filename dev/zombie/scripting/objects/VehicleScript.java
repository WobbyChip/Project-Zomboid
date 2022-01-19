// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import java.util.Map;
import zombie.core.BoxedStaticValues;
import zombie.core.textures.Texture;
import org.joml.Vector3fc;
import java.util.regex.Pattern;
import zombie.core.math.PZMath;
import zombie.vehicles.BaseVehicle;
import zombie.Lua.LuaManager;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;
import java.util.Collection;
import java.util.Arrays;
import org.joml.Vector2i;
import org.joml.Vector4f;
import zombie.core.physics.Bullet;
import zombie.SystemDisabler;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;
import zombie.scripting.ScriptParser;
import zombie.scripting.ScriptManager;
import gnu.trove.list.array.TFloatArrayList;
import zombie.core.ImmutableColor;
import org.joml.Vector2f;
import org.joml.Vector3f;
import java.util.ArrayList;

public final class VehicleScript extends BaseScriptObject
{
    private String fileName;
    private String name;
    private final ArrayList<Model> models;
    public final ArrayList<ModelAttachment> m_attachments;
    private float mass;
    private final Vector3f centerOfMassOffset;
    private float engineForce;
    private float engineIdleSpeed;
    private float steeringIncrement;
    private float steeringClamp;
    private float steeringClampMax;
    private float wheelFriction;
    private float stoppingMovementForce;
    private float suspensionStiffness;
    private float suspensionDamping;
    private float suspensionCompression;
    private float suspensionRestLength;
    private float maxSuspensionTravelCm;
    private float rollInfluence;
    private final Vector3f extents;
    private final Vector2f shadowExtents;
    private final Vector2f shadowOffset;
    private boolean bHadShadowOExtents;
    private boolean bHadShadowOffset;
    private final Vector2f extentsOffset;
    private final Vector3f physicsChassisShape;
    private final ArrayList<PhysicsShape> m_physicsShapes;
    private final ArrayList<Wheel> wheels;
    private final ArrayList<Passenger> passengers;
    public float maxSpeed;
    public boolean isSmallVehicle;
    public float spawnOffsetY;
    private int frontEndHealth;
    private int rearEndHealth;
    private int storageCapacity;
    private int engineLoudness;
    private int engineQuality;
    private int seats;
    private int mechanicType;
    private int engineRepairLevel;
    private float playerDamageProtection;
    private float forcedHue;
    private float forcedSat;
    private float forcedVal;
    public ImmutableColor leftSirenCol;
    public ImmutableColor rightSirenCol;
    private String engineRPMType;
    private float offroadEfficiency;
    private final TFloatArrayList crawlOffsets;
    public int gearRatioCount;
    public final float[] gearRatio;
    private final Skin textures;
    private final ArrayList<Skin> skins;
    private final ArrayList<Area> areas;
    private final ArrayList<Part> parts;
    private boolean hasSiren;
    private final LightBar lightbar;
    private final Sounds sound;
    public boolean textureMaskEnable;
    private static final int PHYSICS_SHAPE_BOX = 1;
    private static final int PHYSICS_SHAPE_SPHERE = 2;
    
    public VehicleScript() {
        this.models = new ArrayList<Model>();
        this.m_attachments = new ArrayList<ModelAttachment>();
        this.mass = 800.0f;
        this.centerOfMassOffset = new Vector3f();
        this.engineForce = 3000.0f;
        this.engineIdleSpeed = 750.0f;
        this.steeringIncrement = 0.04f;
        this.steeringClamp = 0.4f;
        this.steeringClampMax = 0.9f;
        this.wheelFriction = 800.0f;
        this.stoppingMovementForce = 1.0f;
        this.suspensionStiffness = 20.0f;
        this.suspensionDamping = 2.3f;
        this.suspensionCompression = 4.4f;
        this.suspensionRestLength = 0.6f;
        this.maxSuspensionTravelCm = 500.0f;
        this.rollInfluence = 0.1f;
        this.extents = new Vector3f(0.75f, 0.5f, 2.0f);
        this.shadowExtents = new Vector2f(0.0f, 0.0f);
        this.shadowOffset = new Vector2f(0.0f, 0.0f);
        this.bHadShadowOExtents = false;
        this.bHadShadowOffset = false;
        this.extentsOffset = new Vector2f(0.5f, 0.5f);
        this.physicsChassisShape = new Vector3f(0.75f, 0.5f, 1.0f);
        this.m_physicsShapes = new ArrayList<PhysicsShape>();
        this.wheels = new ArrayList<Wheel>();
        this.passengers = new ArrayList<Passenger>();
        this.maxSpeed = 20.0f;
        this.isSmallVehicle = true;
        this.spawnOffsetY = 0.0f;
        this.frontEndHealth = 100;
        this.rearEndHealth = 100;
        this.storageCapacity = 100;
        this.engineLoudness = 100;
        this.engineQuality = 100;
        this.seats = 2;
        this.forcedHue = -1.0f;
        this.forcedSat = -1.0f;
        this.forcedVal = -1.0f;
        this.engineRPMType = "jeep";
        this.offroadEfficiency = 1.0f;
        this.crawlOffsets = new TFloatArrayList();
        this.gearRatioCount = 0;
        this.gearRatio = new float[9];
        this.textures = new Skin();
        this.skins = new ArrayList<Skin>();
        this.areas = new ArrayList<Area>();
        this.parts = new ArrayList<Part>();
        this.hasSiren = false;
        this.lightbar = new LightBar();
        this.sound = new Sounds();
        this.textureMaskEnable = false;
        this.gearRatioCount = 4;
        this.gearRatio[0] = 7.09f;
        this.gearRatio[1] = 6.44f;
        this.gearRatio[2] = 4.1f;
        this.gearRatio[3] = 2.29f;
        this.gearRatio[4] = 1.47f;
        this.gearRatio[5] = 1.0f;
    }
    
    public void Load(final String name, final String s) {
        final ScriptManager instance = ScriptManager.instance;
        this.fileName = instance.currentFileName;
        if (!instance.scriptsWithVehicles.contains(this.fileName)) {
            instance.scriptsWithVehicles.add(this.fileName);
        }
        this.name = name;
        for (final ScriptParser.BlockElement blockElement : ScriptParser.parse(s).children.get(0).elements) {
            if (blockElement.asValue() != null) {
                final String[] split = blockElement.asValue().string.split("=");
                final String trim = split[0].trim();
                final String trim2 = split[1].trim();
                if ("extents".equals(trim)) {
                    this.LoadVector3f(trim2, this.extents);
                }
                else if ("shadowExtents".equals(trim)) {
                    this.LoadVector2f(trim2, this.shadowExtents);
                    this.bHadShadowOExtents = true;
                }
                else if ("shadowOffset".equals(trim)) {
                    this.LoadVector2f(trim2, this.shadowOffset);
                    this.bHadShadowOffset = true;
                }
                else if ("physicsChassisShape".equals(trim)) {
                    this.LoadVector3f(trim2, this.physicsChassisShape);
                }
                else if ("extentsOffset".equals(trim)) {
                    this.LoadVector2f(trim2, this.extentsOffset);
                }
                else if ("mass".equals(trim)) {
                    this.mass = Float.parseFloat(trim2);
                }
                else if ("offRoadEfficiency".equalsIgnoreCase(trim)) {
                    this.offroadEfficiency = Float.parseFloat(trim2);
                }
                else if ("centerOfMassOffset".equals(trim)) {
                    this.LoadVector3f(trim2, this.centerOfMassOffset);
                }
                else if ("engineForce".equals(trim)) {
                    this.engineForce = Float.parseFloat(trim2);
                }
                else if ("engineIdleSpeed".equals(trim)) {
                    this.engineIdleSpeed = Float.parseFloat(trim2);
                }
                else if ("gearRatioCount".equals(trim)) {
                    this.gearRatioCount = Integer.parseInt(trim2);
                }
                else if ("gearRatioR".equals(trim)) {
                    this.gearRatio[0] = Float.parseFloat(trim2);
                }
                else if ("gearRatio1".equals(trim)) {
                    this.gearRatio[1] = Float.parseFloat(trim2);
                }
                else if ("gearRatio2".equals(trim)) {
                    this.gearRatio[2] = Float.parseFloat(trim2);
                }
                else if ("gearRatio3".equals(trim)) {
                    this.gearRatio[3] = Float.parseFloat(trim2);
                }
                else if ("gearRatio4".equals(trim)) {
                    this.gearRatio[4] = Float.parseFloat(trim2);
                }
                else if ("gearRatio5".equals(trim)) {
                    this.gearRatio[5] = Float.parseFloat(trim2);
                }
                else if ("gearRatio6".equals(trim)) {
                    this.gearRatio[6] = Float.parseFloat(trim2);
                }
                else if ("gearRatio7".equals(trim)) {
                    this.gearRatio[7] = Float.parseFloat(trim2);
                }
                else if ("gearRatio8".equals(trim)) {
                    this.gearRatio[8] = Float.parseFloat(trim2);
                }
                else if ("textureMaskEnable".equals(trim)) {
                    this.textureMaskEnable = Boolean.parseBoolean(trim2);
                }
                else if ("textureRust".equals(trim)) {
                    this.textures.textureRust = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("textureMask".equals(trim)) {
                    this.textures.textureMask = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("textureLights".equals(trim)) {
                    this.textures.textureLights = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("textureDamage1Overlay".equals(trim)) {
                    this.textures.textureDamage1Overlay = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("textureDamage1Shell".equals(trim)) {
                    this.textures.textureDamage1Shell = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("textureDamage2Overlay".equals(trim)) {
                    this.textures.textureDamage2Overlay = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("textureDamage2Shell".equals(trim)) {
                    this.textures.textureDamage2Shell = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("textureShadow".equals(trim)) {
                    this.textures.textureShadow = StringUtils.discardNullOrWhitespace(trim2);
                }
                else if ("rollInfluence".equals(trim)) {
                    this.rollInfluence = Float.parseFloat(trim2);
                }
                else if ("steeringIncrement".equals(trim)) {
                    this.steeringIncrement = Float.parseFloat(trim2);
                }
                else if ("steeringClamp".equals(trim)) {
                    this.steeringClamp = Float.parseFloat(trim2);
                }
                else if ("suspensionStiffness".equals(trim)) {
                    this.suspensionStiffness = Float.parseFloat(trim2);
                }
                else if ("suspensionDamping".equals(trim)) {
                    this.suspensionDamping = Float.parseFloat(trim2);
                }
                else if ("suspensionCompression".equals(trim)) {
                    this.suspensionCompression = Float.parseFloat(trim2);
                }
                else if ("suspensionRestLength".equals(trim)) {
                    this.suspensionRestLength = Float.parseFloat(trim2);
                }
                else if ("maxSuspensionTravelCm".equals(trim)) {
                    this.maxSuspensionTravelCm = Float.parseFloat(trim2);
                }
                else if ("wheelFriction".equals(trim)) {
                    this.wheelFriction = Float.parseFloat(trim2);
                }
                else if ("stoppingMovementForce".equals(trim)) {
                    this.stoppingMovementForce = Float.parseFloat(trim2);
                }
                else if ("maxSpeed".equals(trim)) {
                    this.maxSpeed = Float.parseFloat(trim2);
                }
                else if ("isSmallVehicle".equals(trim)) {
                    this.isSmallVehicle = Boolean.parseBoolean(trim2);
                }
                else if ("spawnOffsetY".equals(trim)) {
                    this.spawnOffsetY = Float.parseFloat(trim2) - 0.995f;
                }
                else if ("frontEndDurability".equals(trim)) {
                    this.frontEndHealth = Integer.parseInt(trim2);
                }
                else if ("rearEndDurability".equals(trim)) {
                    this.rearEndHealth = Integer.parseInt(trim2);
                }
                else if ("storageCapacity".equals(trim)) {
                    this.storageCapacity = Integer.parseInt(trim2);
                }
                else if ("engineLoudness".equals(trim)) {
                    this.engineLoudness = Integer.parseInt(trim2);
                }
                else if ("engineQuality".equals(trim)) {
                    this.engineQuality = Integer.parseInt(trim2);
                }
                else if ("seats".equals(trim)) {
                    this.seats = Integer.parseInt(trim2);
                }
                else if ("hasSiren".equals(trim)) {
                    this.hasSiren = Boolean.parseBoolean(trim2);
                }
                else if ("mechanicType".equals(trim)) {
                    this.mechanicType = Integer.parseInt(trim2);
                }
                else if ("forcedColor".equals(trim)) {
                    final String[] split2 = trim2.split(" ");
                    this.setForcedHue(Float.parseFloat(split2[0]));
                    this.setForcedSat(Float.parseFloat(split2[1]));
                    this.setForcedVal(Float.parseFloat(split2[2]));
                }
                else if ("engineRPMType".equals(trim)) {
                    this.engineRPMType = trim2.trim();
                }
                else if ("template".equals(trim)) {
                    this.LoadTemplate(trim2);
                }
                else if ("template!".equals(trim)) {
                    final String s2 = trim2;
                    final VehicleTemplate vehicleTemplate = ScriptManager.instance.getVehicleTemplate(s2);
                    if (vehicleTemplate == null) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
                    }
                    else {
                        this.Load(name, vehicleTemplate.body);
                    }
                }
                else if ("engineRepairLevel".equals(trim)) {
                    this.engineRepairLevel = Integer.parseInt(trim2);
                }
                else {
                    if (!"playerDamageProtection".equals(trim)) {
                        continue;
                    }
                    this.setPlayerDamageProtection(Float.parseFloat(trim2));
                }
            }
            else {
                final ScriptParser.Block block = blockElement.asBlock();
                if ("area".equals(block.type)) {
                    this.LoadArea(block);
                }
                else if ("attachment".equals(block.type)) {
                    this.LoadAttachment(block);
                }
                else if ("model".equals(block.type)) {
                    this.LoadModel(block, this.models);
                }
                else if ("part".equals(block.type)) {
                    if (block.id != null && block.id.contains("*")) {
                        final String id = block.id;
                        for (final Part part : this.parts) {
                            if (this.globMatch(id, part.id)) {
                                block.id = part.id;
                                this.LoadPart(block);
                            }
                        }
                    }
                    else {
                        this.LoadPart(block);
                    }
                }
                else if ("passenger".equals(block.type)) {
                    if (block.id != null && block.id.contains("*")) {
                        final String id2 = block.id;
                        for (final Passenger passenger : this.passengers) {
                            if (this.globMatch(id2, passenger.id)) {
                                block.id = passenger.id;
                                this.LoadPassenger(block);
                            }
                        }
                    }
                    else {
                        this.LoadPassenger(block);
                    }
                }
                else if ("physics".equals(block.type)) {
                    final PhysicsShape loadPhysicsShape = this.LoadPhysicsShape(block);
                    if (loadPhysicsShape == null || this.m_physicsShapes.size() >= 10) {
                        continue;
                    }
                    this.m_physicsShapes.add(loadPhysicsShape);
                }
                else if ("skin".equals(block.type)) {
                    final Skin loadSkin = this.LoadSkin(block);
                    if (StringUtils.isNullOrWhitespace(loadSkin.texture)) {
                        continue;
                    }
                    this.skins.add(loadSkin);
                }
                else if ("wheel".equals(block.type)) {
                    this.LoadWheel(block);
                }
                else if ("lightbar".equals(block.type)) {
                    for (final ScriptParser.Value value : block.values) {
                        final String trim3 = value.getKey().trim();
                        final String trim4 = value.getValue().trim();
                        if ("soundSiren".equals(trim3)) {
                            this.lightbar.soundSiren0 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim4);
                            this.lightbar.soundSiren1 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim4);
                            this.lightbar.soundSiren2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim4);
                        }
                        if ("soundSiren0".equals(trim3)) {
                            this.lightbar.soundSiren0 = trim4;
                        }
                        if ("soundSiren1".equals(trim3)) {
                            this.lightbar.soundSiren1 = trim4;
                        }
                        if ("soundSiren2".equals(trim3)) {
                            this.lightbar.soundSiren2 = trim4;
                        }
                        if ("leftCol".equals(trim3)) {
                            final String[] split3 = trim4.split(";");
                            this.leftSirenCol = new ImmutableColor(Float.parseFloat(split3[0]), Float.parseFloat(split3[1]), Float.parseFloat(split3[2]));
                        }
                        if ("rightCol".equals(trim3)) {
                            final String[] split4 = trim4.split(";");
                            this.rightSirenCol = new ImmutableColor(Float.parseFloat(split4[0]), Float.parseFloat(split4[1]), Float.parseFloat(split4[2]));
                        }
                        this.lightbar.enable = true;
                        final Part e = new Part();
                        e.id = "lightbar";
                        this.parts.add(e);
                    }
                }
                else {
                    if (!"sound".equals(block.type)) {
                        continue;
                    }
                    for (final ScriptParser.Value value2 : block.values) {
                        final String trim5 = value2.getKey().trim();
                        final String trim6 = value2.getValue().trim();
                        if ("backSignal".equals(trim5)) {
                            this.sound.backSignal = StringUtils.discardNullOrWhitespace(trim6);
                            this.sound.backSignalEnable = (this.sound.backSignal != null);
                        }
                        else if ("engine".equals(trim5)) {
                            this.sound.engine = StringUtils.discardNullOrWhitespace(trim6);
                        }
                        else if ("engineStart".equals(trim5)) {
                            this.sound.engineStart = StringUtils.discardNullOrWhitespace(trim6);
                        }
                        else if ("engineTurnOff".equals(trim5)) {
                            this.sound.engineTurnOff = StringUtils.discardNullOrWhitespace(trim6);
                        }
                        else if ("horn".equals(trim5)) {
                            this.sound.horn = StringUtils.discardNullOrWhitespace(trim6);
                            this.sound.hornEnable = (this.sound.horn != null);
                        }
                        else if ("ignitionFail".equals(trim5)) {
                            this.sound.ignitionFail = StringUtils.discardNullOrWhitespace(trim6);
                        }
                        else {
                            if (!"ignitionFailNoPower".equals(trim5)) {
                                continue;
                            }
                            this.sound.ignitionFailNoPower = StringUtils.discardNullOrWhitespace(trim6);
                        }
                    }
                }
            }
        }
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void Loaded() {
        final float modelScale = this.getModelScale();
        this.extents.mul(modelScale);
        this.maxSuspensionTravelCm *= modelScale;
        this.suspensionRestLength *= modelScale;
        this.centerOfMassOffset.mul(modelScale);
        this.physicsChassisShape.mul(modelScale);
        if (this.bHadShadowOExtents) {
            this.shadowExtents.mul(modelScale);
        }
        else {
            this.shadowExtents.set(this.extents.x(), this.extents.z());
        }
        if (this.bHadShadowOffset) {
            this.shadowOffset.mul(modelScale);
        }
        else {
            this.shadowOffset.set(this.centerOfMassOffset.x(), this.centerOfMassOffset.z());
        }
        final Iterator<Model> iterator = this.models.iterator();
        while (iterator.hasNext()) {
            iterator.next().offset.mul(modelScale);
        }
        final Iterator<ModelAttachment> iterator2 = this.m_attachments.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().getOffset().mul(modelScale);
        }
        for (final PhysicsShape physicsShape : this.m_physicsShapes) {
            physicsShape.offset.mul(modelScale);
            switch (physicsShape.type) {
                case 1: {
                    physicsShape.extents.mul(modelScale);
                    continue;
                }
                case 2: {
                    final PhysicsShape physicsShape2 = physicsShape;
                    physicsShape2.radius *= modelScale;
                    continue;
                }
            }
        }
        for (final Wheel wheel2 : this.wheels) {
            final Wheel wheel = wheel2;
            wheel2.radius *= modelScale;
            wheel.offset.mul(modelScale);
        }
        for (final Area area2 : this.areas) {
            final Area area = area2;
            area2.x *= modelScale;
            final Area area3 = area;
            area3.y *= modelScale;
            final Area area4 = area;
            area4.w *= modelScale;
            final Area area5 = area;
            area5.h *= modelScale;
        }
        if (!this.extents.equals((Object)this.physicsChassisShape)) {
            DebugLog.Script.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
        for (int i = 0; i < this.passengers.size(); ++i) {
            final Passenger passenger = this.passengers.get(i);
            for (int j = 0; j < passenger.getPositionCount(); ++j) {
                passenger.getPosition(j).getOffset().mul(modelScale);
            }
            for (int k = 0; k < passenger.switchSeats.size(); ++k) {
                final Passenger.SwitchSeat switchSeat = passenger.switchSeats.get(k);
                switchSeat.seat = this.getPassengerIndex(switchSeat.id);
                assert switchSeat.seat != -1;
            }
        }
        for (int l = 0; l < this.parts.size(); ++l) {
            final Part part = this.parts.get(l);
            if (part.container != null && part.container.seatID != null && !part.container.seatID.isEmpty()) {
                part.container.seat = this.getPassengerIndex(part.container.seatID);
            }
            if (part.specificItem && part.itemType != null) {
                for (int n = 0; n < part.itemType.size(); ++n) {
                    part.itemType.set(n, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, (String)part.itemType.get(n), this.mechanicType));
                }
            }
        }
        this.initCrawlOffsets();
        this.toBullet();
    }
    
    public void toBullet() {
        final float[] array = new float[200];
        int n = 0;
        array[n++] = this.getModelScale();
        array[n++] = this.extents.x;
        array[n++] = this.extents.y;
        array[n++] = this.extents.z;
        array[n++] = this.physicsChassisShape.x;
        array[n++] = this.physicsChassisShape.y;
        array[n++] = this.physicsChassisShape.z;
        array[n++] = this.mass;
        array[n++] = this.centerOfMassOffset.x;
        array[n++] = this.centerOfMassOffset.y;
        array[n++] = this.centerOfMassOffset.z;
        array[n++] = this.rollInfluence;
        array[n++] = this.suspensionStiffness;
        array[n++] = this.suspensionCompression;
        array[n++] = this.suspensionDamping;
        array[n++] = this.maxSuspensionTravelCm;
        array[n++] = this.suspensionRestLength;
        if (SystemDisabler.getdoHighFriction()) {
            array[n++] = this.wheelFriction * 100.0f;
        }
        else {
            array[n++] = this.wheelFriction;
        }
        array[n++] = this.stoppingMovementForce;
        array[n++] = (float)this.getWheelCount();
        for (int i = 0; i < this.getWheelCount(); ++i) {
            final Wheel wheel = this.getWheel(i);
            array[n++] = (wheel.front ? 1.0f : 0.0f);
            array[n++] = wheel.offset.x + this.getModel().offset.x - 0.0f * this.centerOfMassOffset.x;
            array[n++] = wheel.offset.y + this.getModel().offset.y - 0.0f * this.centerOfMassOffset.y + 1.0f * this.suspensionRestLength;
            array[n++] = wheel.offset.z + this.getModel().offset.z - 0.0f * this.centerOfMassOffset.z;
            array[n++] = wheel.radius;
        }
        array[n++] = (float)(this.m_physicsShapes.size() + 1);
        array[n++] = 1.0f;
        array[n++] = this.centerOfMassOffset.x;
        array[n++] = this.centerOfMassOffset.y;
        array[n++] = this.centerOfMassOffset.z;
        array[n++] = this.physicsChassisShape.x;
        array[n++] = this.physicsChassisShape.y;
        array[n++] = this.physicsChassisShape.z;
        array[n++] = 0.0f;
        array[n++] = 0.0f;
        array[n++] = 0.0f;
        for (int j = 0; j < this.m_physicsShapes.size(); ++j) {
            final PhysicsShape physicsShape = this.m_physicsShapes.get(j);
            array[n++] = (float)physicsShape.type;
            array[n++] = physicsShape.offset.x;
            array[n++] = physicsShape.offset.y;
            array[n++] = physicsShape.offset.z;
            if (physicsShape.type == 1) {
                array[n++] = physicsShape.extents.x;
                array[n++] = physicsShape.extents.y;
                array[n++] = physicsShape.extents.z;
                array[n++] = physicsShape.rotate.x;
                array[n++] = physicsShape.rotate.y;
                array[n++] = physicsShape.rotate.z;
            }
            else if (physicsShape.type == 2) {
                array[n++] = physicsShape.radius;
            }
        }
        Bullet.defineVehicleScript(this.getFullName(), array);
    }
    
    private void LoadVector2f(final String s, final Vector2f vector2f) {
        final String[] split = s.split(" ");
        vector2f.set(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
    }
    
    private void LoadVector3f(final String s, final Vector3f vector3f) {
        final String[] split = s.split(" ");
        vector3f.set(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }
    
    private void LoadVector4f(final String s, final Vector4f vector4f) {
        final String[] split = s.split(" ");
        vector4f.set(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
    }
    
    private void LoadVector2i(final String s, final Vector2i vector2i) {
        final String[] split = s.split(" ");
        vector2i.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
    
    private ModelAttachment LoadAttachment(final ScriptParser.Block block) {
        ModelAttachment attachmentById = this.getAttachmentById(block.id);
        if (attachmentById == null) {
            attachmentById = new ModelAttachment(block.id);
            this.m_attachments.add(attachmentById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("bone".equals(trim)) {
                attachmentById.setBone(trim2);
            }
            else if ("offset".equals(trim)) {
                this.LoadVector3f(trim2, attachmentById.getOffset());
            }
            else if ("rotate".equals(trim)) {
                this.LoadVector3f(trim2, attachmentById.getRotate());
            }
            else if ("canAttach".equals(trim)) {
                attachmentById.setCanAttach(new ArrayList<String>(Arrays.asList(trim2.split(","))));
            }
            else if ("zoffset".equals(trim)) {
                attachmentById.setZOffset(Float.parseFloat(trim2));
            }
            else {
                if (!"updateconstraint".equals(trim)) {
                    continue;
                }
                attachmentById.setUpdateConstraint(Boolean.parseBoolean(trim2));
            }
        }
        return attachmentById;
    }
    
    private Model LoadModel(final ScriptParser.Block block, final ArrayList<Model> list) {
        Model modelById = this.getModelById(block.id, list);
        if (modelById == null) {
            modelById = new Model();
            modelById.id = block.id;
            list.add(modelById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("file".equals(trim)) {
                modelById.file = trim2;
            }
            else if ("offset".equals(trim)) {
                this.LoadVector3f(trim2, modelById.offset);
            }
            else if ("rotate".equals(trim)) {
                this.LoadVector3f(trim2, modelById.rotate);
            }
            else {
                if (!"scale".equals(trim)) {
                    continue;
                }
                modelById.scale = Float.parseFloat(trim2);
            }
        }
        return modelById;
    }
    
    private Skin LoadSkin(final ScriptParser.Block block) {
        final Skin skin = new Skin();
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("texture".equals(trim)) {
                skin.texture = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if ("textureRust".equals(trim)) {
                skin.textureRust = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if ("textureMask".equals(trim)) {
                skin.textureMask = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if ("textureLights".equals(trim)) {
                skin.textureLights = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if ("textureDamage1Overlay".equals(trim)) {
                skin.textureDamage1Overlay = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if ("textureDamage1Shell".equals(trim)) {
                skin.textureDamage1Shell = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if ("textureDamage2Overlay".equals(trim)) {
                skin.textureDamage2Overlay = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if ("textureDamage2Shell".equals(trim)) {
                skin.textureDamage2Shell = StringUtils.discardNullOrWhitespace(trim2);
            }
            else {
                if (!"textureShadow".equals(trim)) {
                    continue;
                }
                skin.textureShadow = StringUtils.discardNullOrWhitespace(trim2);
            }
        }
        return skin;
    }
    
    private Wheel LoadWheel(final ScriptParser.Block block) {
        Wheel wheelById = this.getWheelById(block.id);
        if (wheelById == null) {
            wheelById = new Wheel();
            wheelById.id = block.id;
            this.wheels.add(wheelById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("model".equals(trim)) {
                wheelById.model = trim2;
            }
            else if ("front".equals(trim)) {
                wheelById.front = Boolean.parseBoolean(trim2);
            }
            else if ("offset".equals(trim)) {
                this.LoadVector3f(trim2, wheelById.offset);
            }
            else if ("radius".equals(trim)) {
                wheelById.radius = Float.parseFloat(trim2);
            }
            else {
                if (!"width".equals(trim)) {
                    continue;
                }
                wheelById.width = Float.parseFloat(trim2);
            }
        }
        return wheelById;
    }
    
    private Passenger LoadPassenger(final ScriptParser.Block block) {
        Passenger passengerById = this.getPassengerById(block.id);
        if (passengerById == null) {
            passengerById = new Passenger();
            passengerById.id = block.id;
            this.passengers.add(passengerById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("area".equals(trim)) {
                passengerById.area = trim2;
            }
            else if ("door".equals(trim)) {
                passengerById.door = trim2;
            }
            else if ("door2".equals(trim)) {
                passengerById.door2 = trim2;
            }
            else if ("hasRoof".equals(trim)) {
                passengerById.hasRoof = Boolean.parseBoolean(trim2);
            }
            else {
                if (!"showPassenger".equals(trim)) {
                    continue;
                }
                passengerById.showPassenger = Boolean.parseBoolean(trim2);
            }
        }
        for (final ScriptParser.Block block2 : block.children) {
            if ("anim".equals(block2.type)) {
                this.LoadAnim(block2, passengerById.anims);
            }
            else if ("position".equals(block2.type)) {
                this.LoadPosition(block2, passengerById.positions);
            }
            else {
                if (!"switchSeat".equals(block2.type)) {
                    continue;
                }
                this.LoadPassengerSwitchSeat(block2, passengerById);
            }
        }
        return passengerById;
    }
    
    private Anim LoadAnim(final ScriptParser.Block block, final ArrayList<Anim> list) {
        Anim animationById = this.getAnimationById(block.id, list);
        if (animationById == null) {
            animationById = new Anim();
            animationById.id = block.id;
            list.add(animationById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("angle".equals(trim)) {
                this.LoadVector3f(trim2, animationById.angle);
            }
            else if ("anim".equals(trim)) {
                animationById.anim = trim2;
            }
            else if ("animate".equals(trim)) {
                animationById.bAnimate = Boolean.parseBoolean(trim2);
            }
            else if ("loop".equals(trim)) {
                animationById.bLoop = Boolean.parseBoolean(trim2);
            }
            else if ("reverse".equals(trim)) {
                animationById.bReverse = Boolean.parseBoolean(trim2);
            }
            else if ("rate".equals(trim)) {
                animationById.rate = Float.parseFloat(trim2);
            }
            else if ("offset".equals(trim)) {
                this.LoadVector3f(trim2, animationById.offset);
            }
            else {
                if (!"sound".equals(trim)) {
                    continue;
                }
                animationById.sound = trim2;
            }
        }
        return animationById;
    }
    
    private Passenger.SwitchSeat LoadPassengerSwitchSeat(final ScriptParser.Block block, final Passenger passenger) {
        Passenger.SwitchSeat switchSeatById = passenger.getSwitchSeatById(block.id);
        if (block.isEmpty()) {
            if (switchSeatById != null) {
                passenger.switchSeats.remove(switchSeatById);
            }
            return null;
        }
        if (switchSeatById == null) {
            switchSeatById = new Passenger.SwitchSeat();
            switchSeatById.id = block.id;
            passenger.switchSeats.add(switchSeatById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("anim".equals(trim)) {
                switchSeatById.anim = trim2;
            }
            else if ("rate".equals(trim)) {
                switchSeatById.rate = Float.parseFloat(trim2);
            }
            else {
                if (!"sound".equals(trim)) {
                    continue;
                }
                switchSeatById.sound = (trim2.isEmpty() ? null : trim2);
            }
        }
        return switchSeatById;
    }
    
    private Area LoadArea(final ScriptParser.Block block) {
        Area areaById = this.getAreaById(block.id);
        if (areaById == null) {
            areaById = new Area();
            areaById.id = block.id;
            this.areas.add(areaById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("xywh".equals(trim)) {
                final String[] split = trim2.split(" ");
                areaById.x = Float.parseFloat(split[0]);
                areaById.y = Float.parseFloat(split[1]);
                areaById.w = Float.parseFloat(split[2]);
                areaById.h = Float.parseFloat(split[3]);
            }
        }
        return areaById;
    }
    
    private Part LoadPart(final ScriptParser.Block block) {
        Part partById = this.getPartById(block.id);
        if (partById == null) {
            partById = new Part();
            partById.id = block.id;
            this.parts.add(partById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("area".equals(trim)) {
                partById.area = (trim2.isEmpty() ? null : trim2);
            }
            else if ("itemType".equals(trim)) {
                partById.itemType = new ArrayList<String>();
                final String[] split = trim2.split(";");
                for (int length = split.length, i = 0; i < length; ++i) {
                    partById.itemType.add(split[i]);
                }
            }
            else if ("parent".equals(trim)) {
                partById.parent = (trim2.isEmpty() ? null : trim2);
            }
            else if ("mechanicRequireKey".equals(trim)) {
                partById.mechanicRequireKey = Boolean.parseBoolean(trim2);
            }
            else if ("repairMechanic".equals(trim)) {
                partById.setRepairMechanic(Boolean.parseBoolean(trim2));
            }
            else if ("wheel".equals(trim)) {
                partById.wheel = trim2;
            }
            else if ("category".equals(trim)) {
                partById.category = trim2;
            }
            else if ("specificItem".equals(trim)) {
                partById.specificItem = Boolean.parseBoolean(trim2);
            }
            else {
                if (!"hasLightsRear".equals(trim)) {
                    continue;
                }
                partById.hasLightsRear = Boolean.parseBoolean(trim2);
            }
        }
        for (final ScriptParser.Block block2 : block.children) {
            if ("anim".equals(block2.type)) {
                if (partById.anims == null) {
                    partById.anims = new ArrayList<Anim>();
                }
                this.LoadAnim(block2, partById.anims);
            }
            else if ("container".equals(block2.type)) {
                partById.container = this.LoadContainer(block2, partById.container);
            }
            else if ("door".equals(block2.type)) {
                partById.door = this.LoadDoor(block2);
            }
            else if ("lua".equals(block2.type)) {
                partById.luaFunctions = this.LoadLuaFunctions(block2);
            }
            else if ("model".equals(block2.type)) {
                if (partById.models == null) {
                    partById.models = new ArrayList<Model>();
                }
                this.LoadModel(block2, partById.models);
            }
            else if ("table".equals(block2.type)) {
                final KahluaTable kahluaTable = (partById.tables == null) ? null : partById.tables.get(block2.id);
                final KahluaTable loadTable = this.LoadTable(block2, (kahluaTable instanceof KahluaTable) ? kahluaTable : null);
                if (partById.tables == null) {
                    partById.tables = new HashMap<String, KahluaTable>();
                }
                partById.tables.put(block2.id, loadTable);
            }
            else {
                if (!"window".equals(block2.type)) {
                    continue;
                }
                partById.window = this.LoadWindow(block2);
            }
        }
        return partById;
    }
    
    private PhysicsShape LoadPhysicsShape(final ScriptParser.Block block) {
        final String id = block.id;
        int type = 0;
        switch (id) {
            case "box": {
                type = 1;
                break;
            }
            case "sphere": {
                type = 2;
                break;
            }
            default: {
                return null;
            }
        }
        final PhysicsShape physicsShape = new PhysicsShape();
        physicsShape.type = type;
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("extents".equalsIgnoreCase(trim)) {
                this.LoadVector3f(trim2, physicsShape.extents);
            }
            else if ("offset".equalsIgnoreCase(trim)) {
                this.LoadVector3f(trim2, physicsShape.offset);
            }
            else if ("radius".equalsIgnoreCase(trim)) {
                physicsShape.radius = Float.parseFloat(trim2);
            }
            else {
                if (!"rotate".equalsIgnoreCase(trim)) {
                    continue;
                }
                this.LoadVector3f(trim2, physicsShape.rotate);
            }
        }
        switch (physicsShape.type) {
            case 1: {
                if (physicsShape.extents.x() <= 0.0f || physicsShape.extents.y() <= 0.0f || physicsShape.extents.z() <= 0.0f) {
                    return null;
                }
                break;
            }
            case 2: {
                if (physicsShape.radius <= 0.0f) {
                    return null;
                }
                break;
            }
        }
        return physicsShape;
    }
    
    private Door LoadDoor(final ScriptParser.Block block) {
        final Door door = new Door();
        for (final ScriptParser.Value value : block.values) {
            value.getKey().trim();
            value.getValue().trim();
        }
        return door;
    }
    
    private Window LoadWindow(final ScriptParser.Block block) {
        final Window window = new Window();
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("openable".equals(trim)) {
                window.openable = Boolean.parseBoolean(trim2);
            }
        }
        return window;
    }
    
    private Container LoadContainer(final ScriptParser.Block block, final Container container) {
        final Container container2 = (container == null) ? new Container() : container;
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("capacity".equals(trim)) {
                container2.capacity = Integer.parseInt(trim2);
            }
            else if ("conditionAffectsCapacity".equals(trim)) {
                container2.conditionAffectsCapacity = Boolean.parseBoolean(trim2);
            }
            else if ("contentType".equals(trim)) {
                container2.contentType = trim2;
            }
            else if ("seat".equals(trim)) {
                container2.seatID = trim2;
            }
            else {
                if (!"test".equals(trim)) {
                    continue;
                }
                container2.luaTest = trim2;
            }
        }
        return container2;
    }
    
    private HashMap<String, String> LoadLuaFunctions(final ScriptParser.Block block) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        for (final ScriptParser.Value value : block.values) {
            if (value.string.indexOf(61) == -1) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, value.string.trim(), this.getFullName()));
            }
            hashMap.put(value.getKey().trim(), value.getValue().trim());
        }
        return hashMap;
    }
    
    private Object checkIntegerKey(final Object o) {
        if (!(o instanceof String)) {
            return o;
        }
        final String s = (String)o;
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isDigit(s.charAt(i))) {
                return o;
            }
        }
        return Double.valueOf(s);
    }
    
    private KahluaTable LoadTable(final ScriptParser.Block block, final KahluaTable kahluaTable) {
        final KahluaTable kahluaTable2 = (kahluaTable == null) ? LuaManager.platform.newTable() : kahluaTable;
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            String trim2 = value.getValue().trim();
            if (trim2.isEmpty()) {
                trim2 = null;
            }
            kahluaTable2.rawset(this.checkIntegerKey(trim), (Object)trim2);
        }
        for (final ScriptParser.Block block2 : block.children) {
            final Object rawget = kahluaTable2.rawget((Object)block2.type);
            kahluaTable2.rawset(this.checkIntegerKey(block2.type), (Object)this.LoadTable(block2, (rawget instanceof KahluaTable) ? ((KahluaTable)rawget) : null));
        }
        return kahluaTable2;
    }
    
    private void LoadTemplate(final String s) {
        if (s.contains("/")) {
            final String[] split = s.split("/");
            if (split.length == 0 || split.length > 3) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                return;
            }
            for (int i = 0; i < split.length; ++i) {
                split[i] = split[i].trim();
                if (split[i].isEmpty()) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    return;
                }
            }
            final VehicleTemplate vehicleTemplate = ScriptManager.instance.getVehicleTemplate(split[0]);
            if (vehicleTemplate == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                return;
            }
            final VehicleScript script = vehicleTemplate.getScript();
            final String s2 = split[1];
            switch (s2) {
                case "area": {
                    if (split.length == 2) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        return;
                    }
                    this.copyAreasFrom(script, split[2]);
                    break;
                }
                case "part": {
                    if (split.length == 2) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        return;
                    }
                    this.copyPartsFrom(script, split[2]);
                    break;
                }
                case "passenger": {
                    if (split.length == 2) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        return;
                    }
                    this.copyPassengersFrom(script, split[2]);
                    break;
                }
                case "wheel": {
                    if (split.length == 2) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        return;
                    }
                    this.copyWheelsFrom(script, split[2]);
                    break;
                }
                default: {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
            }
        }
        else {
            final VehicleTemplate vehicleTemplate2 = ScriptManager.instance.getVehicleTemplate(s.trim());
            if (vehicleTemplate2 == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                return;
            }
            final VehicleScript script2 = vehicleTemplate2.getScript();
            this.copyAreasFrom(script2, "*");
            this.copyPartsFrom(script2, "*");
            this.copyPassengersFrom(script2, "*");
            this.copyWheelsFrom(script2, "*");
        }
    }
    
    public void copyAreasFrom(final VehicleScript vehicleScript, final String anObject) {
        if ("*".equals(anObject)) {
            for (int i = 0; i < vehicleScript.getAreaCount(); ++i) {
                final Area area = vehicleScript.getArea(i);
                final int indexOfAreaById = this.getIndexOfAreaById(area.id);
                if (indexOfAreaById == -1) {
                    this.areas.add(area.makeCopy());
                }
                else {
                    this.areas.set(indexOfAreaById, area.makeCopy());
                }
            }
        }
        else {
            final Area areaById = vehicleScript.getAreaById(anObject);
            if (areaById == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                return;
            }
            final int indexOfAreaById2 = this.getIndexOfAreaById(areaById.id);
            if (indexOfAreaById2 == -1) {
                this.areas.add(areaById.makeCopy());
            }
            else {
                this.areas.set(indexOfAreaById2, areaById.makeCopy());
            }
        }
    }
    
    public void copyPartsFrom(final VehicleScript vehicleScript, final String anObject) {
        if ("*".equals(anObject)) {
            for (int i = 0; i < vehicleScript.getPartCount(); ++i) {
                final Part part = vehicleScript.getPart(i);
                final int indexOfPartById = this.getIndexOfPartById(part.id);
                if (indexOfPartById == -1) {
                    this.parts.add(part.makeCopy());
                }
                else {
                    this.parts.set(indexOfPartById, part.makeCopy());
                }
            }
        }
        else {
            final Part partById = vehicleScript.getPartById(anObject);
            if (partById == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                return;
            }
            final int indexOfPartById2 = this.getIndexOfPartById(partById.id);
            if (indexOfPartById2 == -1) {
                this.parts.add(partById.makeCopy());
            }
            else {
                this.parts.set(indexOfPartById2, partById.makeCopy());
            }
        }
    }
    
    public void copyPassengersFrom(final VehicleScript vehicleScript, final String anObject) {
        if ("*".equals(anObject)) {
            for (int i = 0; i < vehicleScript.getPassengerCount(); ++i) {
                final Passenger passenger = vehicleScript.getPassenger(i);
                final int passengerIndex = this.getPassengerIndex(passenger.id);
                if (passengerIndex == -1) {
                    this.passengers.add(passenger.makeCopy());
                }
                else {
                    this.passengers.set(passengerIndex, passenger.makeCopy());
                }
            }
        }
        else {
            final Passenger passengerById = vehicleScript.getPassengerById(anObject);
            if (passengerById == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                return;
            }
            final int passengerIndex2 = this.getPassengerIndex(passengerById.id);
            if (passengerIndex2 == -1) {
                this.passengers.add(passengerById.makeCopy());
            }
            else {
                this.passengers.set(passengerIndex2, passengerById.makeCopy());
            }
        }
    }
    
    public void copyWheelsFrom(final VehicleScript vehicleScript, final String anObject) {
        if ("*".equals(anObject)) {
            for (int i = 0; i < vehicleScript.getWheelCount(); ++i) {
                final Wheel wheel = vehicleScript.getWheel(i);
                final int indexOfWheelById = this.getIndexOfWheelById(wheel.id);
                if (indexOfWheelById == -1) {
                    this.wheels.add(wheel.makeCopy());
                }
                else {
                    this.wheels.set(indexOfWheelById, wheel.makeCopy());
                }
            }
        }
        else {
            final Wheel wheelById = vehicleScript.getWheelById(anObject);
            if (wheelById == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, anObject));
                return;
            }
            final int indexOfWheelById2 = this.getIndexOfWheelById(wheelById.id);
            if (indexOfWheelById2 == -1) {
                this.wheels.add(wheelById.makeCopy());
            }
            else {
                this.wheels.set(indexOfWheelById2, wheelById.makeCopy());
            }
        }
    }
    
    private Position LoadPosition(final ScriptParser.Block block, final ArrayList<Position> list) {
        Position positionById = this.getPositionById(block.id, list);
        if (block.isEmpty()) {
            if (positionById != null) {
                list.remove(positionById);
            }
            return null;
        }
        if (positionById == null) {
            positionById = new Position();
            positionById.id = block.id;
            list.add(positionById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("rotate".equals(trim)) {
                this.LoadVector3f(trim2, positionById.rotate);
            }
            else if ("offset".equals(trim)) {
                this.LoadVector3f(trim2, positionById.offset);
            }
            else {
                if (!"area".equals(trim)) {
                    continue;
                }
                positionById.area = (trim2.isEmpty() ? null : trim2);
            }
        }
        return positionById;
    }
    
    private void initCrawlOffsets() {
        for (int i = 0; i < this.getWheelCount(); ++i) {
            final Wheel wheel = this.getWheel(i);
            if (wheel.id.contains("Left")) {
                this.initCrawlOffsets(wheel);
            }
        }
        final float n = this.extents.z + BaseVehicle.PLUS_RADIUS * 2.0f;
        for (int j = 0; j < this.crawlOffsets.size(); ++j) {
            this.crawlOffsets.set(j, (this.extents.z / 2.0f + BaseVehicle.PLUS_RADIUS + this.crawlOffsets.get(j) - this.centerOfMassOffset.z) / n);
        }
        this.crawlOffsets.sort();
        for (int k = 0; k < this.crawlOffsets.size(); ++k) {
            final float value = this.crawlOffsets.get(k);
            for (int l = k + 1; l < this.crawlOffsets.size(); ++l) {
                if ((this.crawlOffsets.get(l) - value) * n < 0.15f) {
                    this.crawlOffsets.removeAt(l--);
                }
            }
        }
    }
    
    private void initCrawlOffsets(final Wheel wheel) {
        final float n = 0.3f;
        final float n2 = (this.getModel() == null) ? 0.0f : this.getModel().getOffset().z;
        final float n3 = this.centerOfMassOffset.z + this.extents.z / 2.0f;
        final float n4 = this.centerOfMassOffset.z - this.extents.z / 2.0f;
        for (int i = 0; i < 10; ++i) {
            final float n5 = n2 + wheel.offset.z + wheel.radius + n + n * i;
            if (n5 + n <= n3 && !this.isOverlappingWheel(n5)) {
                this.crawlOffsets.add(n5);
            }
            final float n6 = n2 + wheel.offset.z - wheel.radius - n - n * i;
            if (n6 - n >= n4 && !this.isOverlappingWheel(n6)) {
                this.crawlOffsets.add(n6);
            }
        }
    }
    
    private boolean isOverlappingWheel(final float n) {
        final float n2 = 0.3f;
        final float n3 = (this.getModel() == null) ? 0.0f : this.getModel().getOffset().z;
        for (int i = 0; i < this.getWheelCount(); ++i) {
            final Wheel wheel = this.getWheel(i);
            if (wheel.id.contains("Left") && Math.abs(n3 + wheel.offset.z - n) < (wheel.radius + n2) * 0.99f) {
                return true;
            }
        }
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFullName() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getModule().getName(), this.getName());
    }
    
    public Model getModel() {
        return this.models.isEmpty() ? null : this.models.get(0);
    }
    
    public Vector3f getModelOffset() {
        return (this.getModel() == null) ? null : this.getModel().getOffset();
    }
    
    public float getModelScale() {
        return (this.getModel() == null) ? 1.0f : this.getModel().scale;
    }
    
    public void setModelScale(final float n) {
        final Model model = this.getModel();
        if (model != null) {
            model.scale = 1.0f / model.scale;
            this.Loaded();
            model.scale = PZMath.clamp(n, 0.01f, 100.0f);
            this.Loaded();
        }
    }
    
    public int getModelCount() {
        return this.models.size();
    }
    
    public Model getModelByIndex(final int index) {
        return this.models.get(index);
    }
    
    public Model getModelById(final String anObject, final ArrayList<Model> list) {
        for (int i = 0; i < list.size(); ++i) {
            final Model model = list.get(i);
            if (StringUtils.isNullOrWhitespace(model.id) && StringUtils.isNullOrWhitespace(anObject)) {
                return model;
            }
            if (model.id != null && model.id.equals(anObject)) {
                return model;
            }
        }
        return null;
    }
    
    public Model getModelById(final String s) {
        return this.getModelById(s, this.models);
    }
    
    public int getAttachmentCount() {
        return this.m_attachments.size();
    }
    
    public ModelAttachment getAttachment(final int index) {
        return this.m_attachments.get(index);
    }
    
    public ModelAttachment getAttachmentById(final String anObject) {
        for (int i = 0; i < this.m_attachments.size(); ++i) {
            final ModelAttachment modelAttachment = this.m_attachments.get(i);
            if (modelAttachment.getId().equals(anObject)) {
                return modelAttachment;
            }
        }
        return null;
    }
    
    public ModelAttachment addAttachment(final ModelAttachment e) {
        this.m_attachments.add(e);
        return e;
    }
    
    public ModelAttachment removeAttachment(final ModelAttachment o) {
        this.m_attachments.remove(o);
        return o;
    }
    
    public ModelAttachment addAttachmentAt(final int index, final ModelAttachment element) {
        this.m_attachments.add(index, element);
        return element;
    }
    
    public ModelAttachment removeAttachment(final int index) {
        return this.m_attachments.remove(index);
    }
    
    public LightBar getLightbar() {
        return this.lightbar;
    }
    
    public Sounds getSounds() {
        return this.sound;
    }
    
    public boolean getHasSiren() {
        return this.hasSiren;
    }
    
    public Vector3f getExtents() {
        return this.extents;
    }
    
    public Vector3f getPhysicsChassisShape() {
        return this.physicsChassisShape;
    }
    
    public Vector2f getShadowExtents() {
        return this.shadowExtents;
    }
    
    public Vector2f getShadowOffset() {
        return this.shadowOffset;
    }
    
    public Vector2f getExtentsOffset() {
        return this.extentsOffset;
    }
    
    public float getMass() {
        return this.mass;
    }
    
    public Vector3f getCenterOfMassOffset() {
        return this.centerOfMassOffset;
    }
    
    public float getEngineForce() {
        return this.engineForce;
    }
    
    public float getEngineIdleSpeed() {
        return this.engineIdleSpeed;
    }
    
    public int getEngineQuality() {
        return this.engineQuality;
    }
    
    public int getEngineLoudness() {
        return this.engineLoudness;
    }
    
    public float getRollInfluence() {
        return this.rollInfluence;
    }
    
    public float getSteeringIncrement() {
        return this.steeringIncrement;
    }
    
    public float getSteeringClamp(float abs) {
        abs = Math.abs(abs);
        float n = abs / this.maxSpeed;
        if (n > 1.0f) {
            n = 1.0f;
        }
        return (this.steeringClampMax - this.steeringClamp) * (1.0f - n) + this.steeringClamp;
    }
    
    public float getSuspensionStiffness() {
        return this.suspensionStiffness;
    }
    
    public float getSuspensionDamping() {
        return this.suspensionDamping;
    }
    
    public float getSuspensionCompression() {
        return this.suspensionCompression;
    }
    
    public float getSuspensionRestLength() {
        return this.suspensionRestLength;
    }
    
    public float getSuspensionTravel() {
        return this.maxSuspensionTravelCm;
    }
    
    public float getWheelFriction() {
        return this.wheelFriction;
    }
    
    public int getWheelCount() {
        return this.wheels.size();
    }
    
    public Wheel getWheel(final int index) {
        return this.wheels.get(index);
    }
    
    public Wheel getWheelById(final String anObject) {
        for (int i = 0; i < this.wheels.size(); ++i) {
            final Wheel wheel = this.wheels.get(i);
            if (wheel.id != null && wheel.id.equals(anObject)) {
                return wheel;
            }
        }
        return null;
    }
    
    public int getIndexOfWheelById(final String anObject) {
        for (int i = 0; i < this.wheels.size(); ++i) {
            final Wheel wheel = this.wheels.get(i);
            if (wheel.id != null && wheel.id.equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getPassengerCount() {
        return this.passengers.size();
    }
    
    public Passenger getPassenger(final int index) {
        return this.passengers.get(index);
    }
    
    public Passenger getPassengerById(final String anObject) {
        for (int i = 0; i < this.passengers.size(); ++i) {
            final Passenger passenger = this.passengers.get(i);
            if (passenger.id != null && passenger.id.equals(anObject)) {
                return passenger;
            }
        }
        return null;
    }
    
    public int getPassengerIndex(final String anObject) {
        for (int i = 0; i < this.passengers.size(); ++i) {
            final Passenger passenger = this.passengers.get(i);
            if (passenger.id != null && passenger.id.equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getPhysicsShapeCount() {
        return this.m_physicsShapes.size();
    }
    
    public PhysicsShape getPhysicsShape(final int index) {
        if (index < 0 || index >= this.m_physicsShapes.size()) {
            return null;
        }
        return this.m_physicsShapes.get(index);
    }
    
    public int getFrontEndHealth() {
        return this.frontEndHealth;
    }
    
    public int getRearEndHealth() {
        return this.rearEndHealth;
    }
    
    public int getStorageCapacity() {
        return this.storageCapacity;
    }
    
    public Skin getTextures() {
        return this.textures;
    }
    
    public int getSkinCount() {
        return this.skins.size();
    }
    
    public Skin getSkin(final int index) {
        return this.skins.get(index);
    }
    
    public int getAreaCount() {
        return this.areas.size();
    }
    
    public Area getArea(final int index) {
        return this.areas.get(index);
    }
    
    public Area getAreaById(final String anObject) {
        for (int i = 0; i < this.areas.size(); ++i) {
            final Area area = this.areas.get(i);
            if (area.id != null && area.id.equals(anObject)) {
                return area;
            }
        }
        return null;
    }
    
    public int getIndexOfAreaById(final String anObject) {
        for (int i = 0; i < this.areas.size(); ++i) {
            final Area area = this.areas.get(i);
            if (area.id != null && area.id.equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getPartCount() {
        return this.parts.size();
    }
    
    public Part getPart(final int index) {
        return this.parts.get(index);
    }
    
    public Part getPartById(final String anObject) {
        for (int i = 0; i < this.parts.size(); ++i) {
            final Part part = this.parts.get(i);
            if (part.id != null && part.id.equals(anObject)) {
                return part;
            }
        }
        return null;
    }
    
    public int getIndexOfPartById(final String anObject) {
        for (int i = 0; i < this.parts.size(); ++i) {
            final Part part = this.parts.get(i);
            if (part.id != null && part.id.equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    private Anim getAnimationById(final String anObject, final ArrayList<Anim> list) {
        for (int i = 0; i < list.size(); ++i) {
            final Anim anim = list.get(i);
            if (anim.id != null && anim.id.equals(anObject)) {
                return anim;
            }
        }
        return null;
    }
    
    private Position getPositionById(final String anObject, final ArrayList<Position> list) {
        for (int i = 0; i < list.size(); ++i) {
            final Position position = list.get(i);
            if (position.id != null && position.id.equals(anObject)) {
                return position;
            }
        }
        return null;
    }
    
    public boolean globMatch(final String s, final String input) {
        return Pattern.compile(s.replaceAll("\\*", ".*")).matcher(input).matches();
    }
    
    public int getGearRatioCount() {
        return this.gearRatioCount;
    }
    
    public int getSeats() {
        return this.seats;
    }
    
    public void setSeats(final int seats) {
        this.seats = seats;
    }
    
    public int getMechanicType() {
        return this.mechanicType;
    }
    
    public void setMechanicType(final int mechanicType) {
        this.mechanicType = mechanicType;
    }
    
    public int getEngineRepairLevel() {
        return this.engineRepairLevel;
    }
    
    public int getHeadlightConfigLevel() {
        return 2;
    }
    
    public void setEngineRepairLevel(final int engineRepairLevel) {
        this.engineRepairLevel = engineRepairLevel;
    }
    
    public float getPlayerDamageProtection() {
        return this.playerDamageProtection;
    }
    
    public void setPlayerDamageProtection(final float playerDamageProtection) {
        this.playerDamageProtection = playerDamageProtection;
    }
    
    public float getForcedHue() {
        return this.forcedHue;
    }
    
    public void setForcedHue(final float forcedHue) {
        this.forcedHue = forcedHue;
    }
    
    public float getForcedSat() {
        return this.forcedSat;
    }
    
    public void setForcedSat(final float forcedSat) {
        this.forcedSat = forcedSat;
    }
    
    public float getForcedVal() {
        return this.forcedVal;
    }
    
    public void setForcedVal(final float forcedVal) {
        this.forcedVal = forcedVal;
    }
    
    public String getEngineRPMType() {
        return this.engineRPMType;
    }
    
    public void setEngineRPMType(final String engineRPMType) {
        this.engineRPMType = engineRPMType;
    }
    
    public float getOffroadEfficiency() {
        return this.offroadEfficiency;
    }
    
    public void setOffroadEfficiency(final float offroadEfficiency) {
        this.offroadEfficiency = offroadEfficiency;
    }
    
    public TFloatArrayList getCrawlOffsets() {
        return this.crawlOffsets;
    }
    
    public static final class LightBar
    {
        public boolean enable;
        public String soundSiren0;
        public String soundSiren1;
        public String soundSiren2;
        
        public LightBar() {
            this.enable = false;
            this.soundSiren0 = "";
            this.soundSiren1 = "";
            this.soundSiren2 = "";
        }
    }
    
    public static final class Sounds
    {
        public boolean hornEnable;
        public String horn;
        public boolean backSignalEnable;
        public String backSignal;
        public String engine;
        public String engineStart;
        public String engineTurnOff;
        public String ignitionFail;
        public String ignitionFailNoPower;
        
        public Sounds() {
            this.hornEnable = false;
            this.horn = "";
            this.backSignalEnable = false;
            this.backSignal = "";
            this.engine = null;
            this.engineStart = null;
            this.engineTurnOff = null;
            this.ignitionFail = null;
            this.ignitionFailNoPower = null;
        }
    }
    
    public static final class Wheel
    {
        public String id;
        public String model;
        public boolean front;
        public final Vector3f offset;
        public float radius;
        public float width;
        
        public Wheel() {
            this.offset = new Vector3f();
            this.radius = 0.5f;
            this.width = 0.4f;
        }
        
        public String getId() {
            return this.id;
        }
        
        public Vector3f getOffset() {
            return this.offset;
        }
        
        Wheel makeCopy() {
            final Wheel wheel = new Wheel();
            wheel.id = this.id;
            wheel.model = this.model;
            wheel.front = this.front;
            wheel.offset.set((Vector3fc)this.offset);
            wheel.radius = this.radius;
            wheel.width = this.width;
            return wheel;
        }
    }
    
    public static final class Anim
    {
        public String id;
        public String anim;
        public float rate;
        public boolean bAnimate;
        public boolean bLoop;
        public boolean bReverse;
        public final Vector3f offset;
        public final Vector3f angle;
        public String sound;
        
        public Anim() {
            this.rate = 1.0f;
            this.bAnimate = true;
            this.bLoop = false;
            this.bReverse = false;
            this.offset = new Vector3f();
            this.angle = new Vector3f();
        }
        
        Anim makeCopy() {
            final Anim anim = new Anim();
            anim.id = this.id;
            anim.anim = this.anim;
            anim.rate = this.rate;
            anim.bAnimate = this.bAnimate;
            anim.bLoop = this.bLoop;
            anim.bReverse = this.bReverse;
            anim.offset.set((Vector3fc)this.offset);
            anim.angle.set((Vector3fc)this.angle);
            anim.sound = this.sound;
            return anim;
        }
    }
    
    public static final class Passenger
    {
        public String id;
        public final ArrayList<Anim> anims;
        public final ArrayList<SwitchSeat> switchSeats;
        public boolean hasRoof;
        public boolean showPassenger;
        public String door;
        public String door2;
        public String area;
        public final ArrayList<Position> positions;
        
        public Passenger() {
            this.anims = new ArrayList<Anim>();
            this.switchSeats = new ArrayList<SwitchSeat>();
            this.hasRoof = true;
            this.showPassenger = false;
            this.positions = new ArrayList<Position>();
        }
        
        public String getId() {
            return this.id;
        }
        
        public Passenger makeCopy() {
            final Passenger passenger = new Passenger();
            passenger.id = this.id;
            for (int i = 0; i < this.anims.size(); ++i) {
                passenger.anims.add(this.anims.get(i).makeCopy());
            }
            for (int j = 0; j < this.switchSeats.size(); ++j) {
                passenger.switchSeats.add(this.switchSeats.get(j).makeCopy());
            }
            passenger.hasRoof = this.hasRoof;
            passenger.showPassenger = this.showPassenger;
            passenger.door = this.door;
            passenger.door2 = this.door2;
            passenger.area = this.area;
            for (int k = 0; k < this.positions.size(); ++k) {
                passenger.positions.add(this.positions.get(k).makeCopy());
            }
            return passenger;
        }
        
        public int getPositionCount() {
            return this.positions.size();
        }
        
        public Position getPosition(final int index) {
            return this.positions.get(index);
        }
        
        public Position getPositionById(final String anObject) {
            for (int i = 0; i < this.positions.size(); ++i) {
                final Position position = this.positions.get(i);
                if (position.id != null && position.id.equals(anObject)) {
                    return position;
                }
            }
            return null;
        }
        
        public SwitchSeat getSwitchSeatById(final String anObject) {
            for (int i = 0; i < this.switchSeats.size(); ++i) {
                final SwitchSeat switchSeat = this.switchSeats.get(i);
                if (switchSeat.id != null && switchSeat.id.equals(anObject)) {
                    return switchSeat;
                }
            }
            return null;
        }
        
        public static final class SwitchSeat
        {
            public String id;
            public int seat;
            public String anim;
            public float rate;
            public String sound;
            
            public SwitchSeat() {
                this.rate = 1.0f;
            }
            
            public String getId() {
                return this.id;
            }
            
            public SwitchSeat makeCopy() {
                final SwitchSeat switchSeat = new SwitchSeat();
                switchSeat.id = this.id;
                switchSeat.seat = this.seat;
                switchSeat.anim = this.anim;
                switchSeat.rate = this.rate;
                switchSeat.sound = this.sound;
                return switchSeat;
            }
        }
    }
    
    public static final class Model
    {
        public String id;
        public String file;
        public float scale;
        public final Vector3f offset;
        public final Vector3f rotate;
        
        public Model() {
            this.scale = 1.0f;
            this.offset = new Vector3f();
            this.rotate = new Vector3f();
        }
        
        public String getId() {
            return this.id;
        }
        
        public Vector3f getOffset() {
            return this.offset;
        }
        
        public Vector3f getRotate() {
            return this.rotate;
        }
        
        Model makeCopy() {
            final Model model = new Model();
            model.id = this.id;
            model.file = this.file;
            model.scale = this.scale;
            model.offset.set((Vector3fc)this.offset);
            model.rotate.set((Vector3fc)this.rotate);
            return model;
        }
    }
    
    public static final class Skin
    {
        public String texture;
        public String textureRust;
        public String textureMask;
        public String textureLights;
        public String textureDamage1Overlay;
        public String textureDamage1Shell;
        public String textureDamage2Overlay;
        public String textureDamage2Shell;
        public String textureShadow;
        public Texture textureData;
        public Texture textureDataRust;
        public Texture textureDataMask;
        public Texture textureDataLights;
        public Texture textureDataDamage1Overlay;
        public Texture textureDataDamage1Shell;
        public Texture textureDataDamage2Overlay;
        public Texture textureDataDamage2Shell;
        public Texture textureDataShadow;
        
        public Skin() {
            this.textureRust = null;
            this.textureMask = null;
            this.textureLights = null;
            this.textureDamage1Overlay = null;
            this.textureDamage1Shell = null;
            this.textureDamage2Overlay = null;
            this.textureDamage2Shell = null;
            this.textureShadow = null;
        }
        
        public void copyMissingFrom(final Skin skin) {
            if (this.textureRust == null) {
                this.textureRust = skin.textureRust;
            }
            if (this.textureMask == null) {
                this.textureMask = skin.textureMask;
            }
            if (this.textureLights == null) {
                this.textureLights = skin.textureLights;
            }
            if (this.textureDamage1Overlay == null) {
                this.textureDamage1Overlay = skin.textureDamage1Overlay;
            }
            if (this.textureDamage1Shell == null) {
                this.textureDamage1Shell = skin.textureDamage1Shell;
            }
            if (this.textureDamage2Overlay == null) {
                this.textureDamage2Overlay = skin.textureDamage2Overlay;
            }
            if (this.textureDamage2Shell == null) {
                this.textureDamage2Shell = skin.textureDamage2Shell;
            }
            if (this.textureShadow == null) {
                this.textureShadow = skin.textureShadow;
            }
        }
    }
    
    public static final class Area
    {
        public String id;
        public float x;
        public float y;
        public float w;
        public float h;
        
        public String getId() {
            return this.id;
        }
        
        public Double getX() {
            return BoxedStaticValues.toDouble(this.x);
        }
        
        public Double getY() {
            return BoxedStaticValues.toDouble(this.y);
        }
        
        public Double getW() {
            return BoxedStaticValues.toDouble(this.w);
        }
        
        public Double getH() {
            return BoxedStaticValues.toDouble(this.h);
        }
        
        public void setX(final Double n) {
            this.x = n.floatValue();
        }
        
        public void setY(final Double n) {
            this.y = n.floatValue();
        }
        
        public void setW(final Double n) {
            this.w = n.floatValue();
        }
        
        public void setH(final Double n) {
            this.h = n.floatValue();
        }
        
        private Area makeCopy() {
            final Area area = new Area();
            area.id = this.id;
            area.x = this.x;
            area.y = this.y;
            area.w = this.w;
            area.h = this.h;
            return area;
        }
    }
    
    public static final class Container
    {
        public int capacity;
        public int seat;
        public String seatID;
        public String luaTest;
        public String contentType;
        public boolean conditionAffectsCapacity;
        
        public Container() {
            this.seat = -1;
            this.conditionAffectsCapacity = false;
        }
        
        Container makeCopy() {
            final Container container = new Container();
            container.capacity = this.capacity;
            container.seat = this.seat;
            container.seatID = this.seatID;
            container.luaTest = this.luaTest;
            container.contentType = this.contentType;
            container.conditionAffectsCapacity = this.conditionAffectsCapacity;
            return container;
        }
    }
    
    public static final class Part
    {
        public String id;
        public String parent;
        public ArrayList<String> itemType;
        public Container container;
        public String area;
        public String wheel;
        public HashMap<String, KahluaTable> tables;
        public HashMap<String, String> luaFunctions;
        public ArrayList<Model> models;
        public Door door;
        public Window window;
        public ArrayList<Anim> anims;
        public String category;
        public boolean specificItem;
        public boolean mechanicRequireKey;
        public boolean repairMechanic;
        public boolean hasLightsRear;
        
        public Part() {
            this.id = "Unknown";
            this.specificItem = true;
            this.mechanicRequireKey = false;
            this.repairMechanic = false;
            this.hasLightsRear = false;
        }
        
        public boolean isMechanicRequireKey() {
            return this.mechanicRequireKey;
        }
        
        public void setMechanicRequireKey(final boolean mechanicRequireKey) {
            this.mechanicRequireKey = mechanicRequireKey;
        }
        
        public boolean isRepairMechanic() {
            return this.repairMechanic;
        }
        
        public void setRepairMechanic(final boolean repairMechanic) {
            this.repairMechanic = repairMechanic;
        }
        
        Part makeCopy() {
            final Part part = new Part();
            part.id = this.id;
            part.parent = this.parent;
            if (this.itemType != null) {
                (part.itemType = new ArrayList<String>()).addAll(this.itemType);
            }
            if (this.container != null) {
                part.container = this.container.makeCopy();
            }
            part.area = this.area;
            part.wheel = this.wheel;
            if (this.tables != null) {
                part.tables = new HashMap<String, KahluaTable>();
                for (final Map.Entry<String, KahluaTable> entry : this.tables.entrySet()) {
                    part.tables.put(entry.getKey(), LuaManager.copyTable(entry.getValue()));
                }
            }
            if (this.luaFunctions != null) {
                (part.luaFunctions = new HashMap<String, String>()).putAll(this.luaFunctions);
            }
            if (this.models != null) {
                part.models = new ArrayList<Model>();
                for (int i = 0; i < this.models.size(); ++i) {
                    part.models.add(this.models.get(i).makeCopy());
                }
            }
            if (this.door != null) {
                part.door = this.door.makeCopy();
            }
            if (this.window != null) {
                part.window = this.window.makeCopy();
            }
            if (this.anims != null) {
                part.anims = new ArrayList<Anim>();
                for (int j = 0; j < this.anims.size(); ++j) {
                    part.anims.add(this.anims.get(j).makeCopy());
                }
            }
            part.category = this.category;
            part.specificItem = this.specificItem;
            part.mechanicRequireKey = this.mechanicRequireKey;
            part.repairMechanic = this.repairMechanic;
            part.hasLightsRear = this.hasLightsRear;
            return part;
        }
    }
    
    public static final class Door
    {
        Door makeCopy() {
            return new Door();
        }
    }
    
    public static final class Window
    {
        public boolean openable;
        
        Window makeCopy() {
            final Window window = new Window();
            window.openable = this.openable;
            return window;
        }
    }
    
    public static final class PhysicsShape
    {
        public int type;
        public final Vector3f offset;
        public final Vector3f rotate;
        public final Vector3f extents;
        public float radius;
        
        public PhysicsShape() {
            this.offset = new Vector3f();
            this.rotate = new Vector3f();
            this.extents = new Vector3f();
        }
        
        public String getTypeString() {
            switch (this.type) {
                case 1: {
                    return "box";
                }
                case 2: {
                    return "sphere";
                }
                default: {
                    throw new RuntimeException("unhandled VehicleScript.PhysicsShape");
                }
            }
        }
        
        public Vector3f getOffset() {
            return this.offset;
        }
        
        public Vector3f getExtents() {
            return this.extents;
        }
        
        public Vector3f getRotate() {
            return this.rotate;
        }
        
        public float getRadius() {
            return this.radius;
        }
        
        public void setRadius(final float n) {
            this.radius = PZMath.clamp(n, 0.05f, 5.0f);
        }
    }
    
    public static final class Position
    {
        public String id;
        public final Vector3f offset;
        public final Vector3f rotate;
        public String area;
        
        public Position() {
            this.offset = new Vector3f();
            this.rotate = new Vector3f();
            this.area = null;
        }
        
        public String getId() {
            return this.id;
        }
        
        public Vector3f getOffset() {
            return this.offset;
        }
        
        public Vector3f getRotate() {
            return this.rotate;
        }
        
        public String getArea() {
            return this.area;
        }
        
        Position makeCopy() {
            final Position position = new Position();
            position.id = this.id;
            position.offset.set((Vector3fc)this.offset);
            position.rotate.set((Vector3fc)this.rotate);
            return position;
        }
    }
}
