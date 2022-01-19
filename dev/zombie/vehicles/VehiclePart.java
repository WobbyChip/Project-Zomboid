// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.inventory.types.Drainable;
import zombie.inventory.InventoryItemFactory;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.Lua.LuaEventManager;
import zombie.ui.UIFont;
import zombie.iso.IsoGridSquare;
import zombie.network.GameServer;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.Lua.LuaManager;
import zombie.chat.ChatElement;
import zombie.radio.devices.DeviceData;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.scripting.objects.VehicleScript;
import zombie.radio.devices.WaveSignalDevice;
import zombie.chat.ChatElementOwner;

public final class VehiclePart implements ChatElementOwner, WaveSignalDevice
{
    protected BaseVehicle vehicle;
    protected boolean bCreated;
    protected String partId;
    protected VehicleScript.Part scriptPart;
    protected ItemContainer container;
    protected InventoryItem item;
    protected KahluaTable modData;
    protected float lastUpdated;
    protected short updateFlags;
    protected VehiclePart parent;
    protected VehicleDoor door;
    protected VehicleWindow window;
    protected ArrayList<VehiclePart> children;
    protected String category;
    protected int condition;
    protected boolean specificItem;
    protected float wheelFriction;
    protected int mechanicSkillInstaller;
    private float suspensionDamping;
    private float suspensionCompression;
    private float engineLoudness;
    protected VehicleLight light;
    protected DeviceData deviceData;
    protected ChatElement chatElement;
    protected boolean hasPlayerInRange;
    
    public VehiclePart(final BaseVehicle vehicle) {
        this.lastUpdated = -1.0f;
        this.condition = -1;
        this.specificItem = true;
        this.wheelFriction = 0.0f;
        this.mechanicSkillInstaller = 0;
        this.suspensionDamping = 0.0f;
        this.suspensionCompression = 0.0f;
        this.engineLoudness = 0.0f;
        this.vehicle = vehicle;
    }
    
    public BaseVehicle getVehicle() {
        return this.vehicle;
    }
    
    public void setScriptPart(final VehicleScript.Part scriptPart) {
        this.scriptPart = scriptPart;
    }
    
    public VehicleScript.Part getScriptPart() {
        return this.scriptPart;
    }
    
    public ItemContainer getItemContainer() {
        return this.container;
    }
    
    public void setItemContainer(final ItemContainer container) {
        if (container != null) {
            container.parent = this.getVehicle();
            container.vehiclePart = this;
        }
        this.container = container;
    }
    
    public boolean hasModData() {
        return this.modData != null && !this.modData.isEmpty();
    }
    
    public KahluaTable getModData() {
        if (this.modData == null) {
            this.modData = LuaManager.platform.newTable();
        }
        return this.modData;
    }
    
    public float getLastUpdated() {
        return this.lastUpdated;
    }
    
    public void setLastUpdated(final float lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getId() {
        if (this.scriptPart == null) {
            return this.partId;
        }
        return this.scriptPart.id;
    }
    
    public int getIndex() {
        return this.vehicle.parts.indexOf(this);
    }
    
    public String getArea() {
        if (this.scriptPart == null) {
            return null;
        }
        return this.scriptPart.area;
    }
    
    public ArrayList<String> getItemType() {
        if (this.scriptPart == null) {
            return null;
        }
        return this.scriptPart.itemType;
    }
    
    public KahluaTable getTable(final String key) {
        if (this.scriptPart == null || this.scriptPart.tables == null) {
            return null;
        }
        final KahluaTable kahluaTable = this.scriptPart.tables.get(key);
        if (kahluaTable == null) {
            return null;
        }
        return LuaManager.copyTable(kahluaTable);
    }
    
    public InventoryItem getInventoryItem() {
        return this.item;
    }
    
    public void setInventoryItem(final InventoryItem item, final int n) {
        this.doInventoryItemStats(this.item = item, n);
        this.getVehicle().updateTotalMass();
        this.getVehicle().bDoDamageOverlay = true;
        if (this.scriptPart != null && this.scriptPart.itemType != null && !this.scriptPart.itemType.isEmpty() && this.scriptPart.models != null) {
            for (int i = 0; i < this.scriptPart.models.size(); ++i) {
                this.vehicle.setModelVisible(this, this.scriptPart.models.get(i), item != null);
            }
        }
        this.getVehicle().updatePartStats();
        this.getVehicle().updateBulletStats();
    }
    
    public void setInventoryItem(final InventoryItem inventoryItem) {
        this.setInventoryItem(inventoryItem, 0);
    }
    
    public void doInventoryItemStats(final InventoryItem inventoryItem, final int mechanicSkillInstaller) {
        if (inventoryItem != null) {
            if (this.isContainer()) {
                if (inventoryItem.getMaxCapacity() > 0 && this.getScriptPart().container.conditionAffectsCapacity) {
                    this.setContainerCapacity((int)getNumberByCondition((float)inventoryItem.getMaxCapacity(), (float)inventoryItem.getCondition(), 5.0f));
                }
                else if (inventoryItem.getMaxCapacity() > 0) {
                    this.setContainerCapacity(inventoryItem.getMaxCapacity());
                }
                this.setContainerContentAmount(inventoryItem.getItemCapacity());
            }
            this.setSuspensionCompression(getNumberByCondition(inventoryItem.getSuspensionCompression(), (float)inventoryItem.getCondition(), 0.6f));
            this.setSuspensionDamping(getNumberByCondition(inventoryItem.getSuspensionDamping(), (float)inventoryItem.getCondition(), 0.6f));
            if (inventoryItem.getEngineLoudness() > 0.0f) {
                this.setEngineLoudness(getNumberByCondition(inventoryItem.getEngineLoudness(), (float)inventoryItem.getCondition(), 10.0f));
            }
            this.setCondition(inventoryItem.getCondition());
            this.setMechanicSkillInstaller(mechanicSkillInstaller);
        }
        else {
            if (this.scriptPart != null && this.scriptPart.container != null) {
                if (this.scriptPart.container.capacity > 0) {
                    this.setContainerCapacity(this.scriptPart.container.capacity);
                }
                else {
                    this.setContainerCapacity(0);
                }
            }
            this.setMechanicSkillInstaller(0);
            this.setContainerContentAmount(0.0f);
            this.setSuspensionCompression(0.0f);
            this.setSuspensionDamping(0.0f);
            this.setWheelFriction(0.0f);
            this.setEngineLoudness(0.0f);
        }
    }
    
    public void setRandomCondition(final InventoryItem inventoryItem) {
        final VehicleType typeFromName = VehicleType.getTypeFromName(this.getVehicle().getVehicleType());
        if (this.getVehicle().isGoodCar()) {
            int conditionMax = 100;
            if (inventoryItem != null) {
                conditionMax = inventoryItem.getConditionMax();
            }
            this.setCondition(Rand.Next(conditionMax - conditionMax / 3, conditionMax));
            if (inventoryItem != null) {
                inventoryItem.setCondition(this.getCondition());
            }
            return;
        }
        int conditionMax2 = 100;
        if (inventoryItem != null) {
            conditionMax2 = inventoryItem.getConditionMax();
        }
        if (typeFromName != null) {
            conditionMax2 *= (int)typeFromName.getRandomBaseVehicleQuality();
        }
        float next = 100.0f;
        if (inventoryItem != null) {
            int chanceToSpawnDamaged = inventoryItem.getChanceToSpawnDamaged();
            if (typeFromName != null) {
                chanceToSpawnDamaged += typeFromName.chanceToPartDamage;
            }
            if (chanceToSpawnDamaged > 0 && Rand.Next(100) < chanceToSpawnDamaged) {
                next = (float)Rand.Next(conditionMax2 - conditionMax2 / 2, conditionMax2);
            }
        }
        else {
            int n = 30;
            if (typeFromName != null) {
                n += typeFromName.chanceToPartDamage;
            }
            if (Rand.Next(100) < n) {
                next = Rand.Next(conditionMax2 * 0.5f, (float)conditionMax2);
            }
        }
        switch (SandboxOptions.instance.CarGeneralCondition.getValue()) {
            case 1: {
                next -= Rand.Next(next * 0.3f, Rand.Next(next * 0.3f, next * 0.9f));
                break;
            }
            case 2: {
                next -= Rand.Next(next * 0.1f, next * 0.3f);
                break;
            }
            case 4: {
                next += Rand.Next(next * 0.2f, next * 0.4f);
                break;
            }
            case 5: {
                next += Rand.Next(next * 0.5f, next * 0.9f);
                break;
            }
        }
        this.setCondition((int)Math.min(100.0f, Math.max(0.0f, next)));
        if (inventoryItem != null) {
            inventoryItem.setCondition(this.getCondition());
        }
    }
    
    public void setGeneralCondition(final InventoryItem inventoryItem, final float n, final float n2) {
        final int n3 = (int)(100 * n);
        float next = 100.0f;
        if (inventoryItem != null) {
            final int n4 = (int)(inventoryItem.getChanceToSpawnDamaged() + n2);
            if (n4 > 0 && Rand.Next(100) < n4) {
                next = (float)Rand.Next(n3 - n3 / 2, n3);
            }
        }
        else if (Rand.Next(100) < (int)(30 + n2)) {
            next = Rand.Next(n3 * 0.5f, (float)n3);
        }
        switch (SandboxOptions.instance.CarGeneralCondition.getValue()) {
            case 1: {
                next -= Rand.Next(next * 0.3f, Rand.Next(next * 0.3f, next * 0.9f));
                break;
            }
            case 2: {
                next -= Rand.Next(next * 0.1f, next * 0.3f);
                break;
            }
            case 4: {
                next += Rand.Next(next * 0.2f, next * 0.4f);
                break;
            }
            case 5: {
                next += Rand.Next(next * 0.5f, next * 0.9f);
                break;
            }
        }
        this.setCondition((int)Math.min(100.0f, Math.max(0.0f, next)));
        if (inventoryItem != null) {
            inventoryItem.setCondition(this.getCondition());
        }
    }
    
    public static float getNumberByCondition(final float n, float n2, final float a) {
        n2 += 20.0f * (100.0f - n2) / 100.0f;
        return Math.round(Math.max(a, n * (n2 / 100.0f)) * 100.0f) / 100.0f;
    }
    
    public boolean isContainer() {
        return this.scriptPart != null && this.scriptPart.container != null;
    }
    
    public int getContainerCapacity() {
        return this.getContainerCapacity(null);
    }
    
    public int getContainerCapacity(final IsoGameCharacter isoGameCharacter) {
        if (!this.isContainer()) {
            return 0;
        }
        if (this.getItemContainer() != null) {
            if (isoGameCharacter == null) {
                return this.getItemContainer().getCapacity();
            }
            return this.getItemContainer().getEffectiveCapacity(isoGameCharacter);
        }
        else {
            if (this.getInventoryItem() == null) {
                return this.scriptPart.container.capacity;
            }
            if (this.scriptPart.container.conditionAffectsCapacity) {
                return (int)getNumberByCondition((float)this.getInventoryItem().getMaxCapacity(), (float)this.getCondition(), 5.0f);
            }
            return this.getInventoryItem().getMaxCapacity();
        }
    }
    
    public void setContainerCapacity(final int capacity) {
        if (!this.isContainer()) {
            return;
        }
        if (this.getItemContainer() != null) {
            this.getItemContainer().Capacity = capacity;
        }
    }
    
    public String getContainerContentType() {
        if (!this.isContainer()) {
            return null;
        }
        return this.scriptPart.container.contentType;
    }
    
    public float getContainerContentAmount() {
        if (!this.isContainer()) {
            return 0.0f;
        }
        if (this.hasModData()) {
            final Object rawget = this.getModData().rawget((Object)"contentAmount");
            if (rawget instanceof Double) {
                return ((Double)rawget).floatValue();
            }
        }
        return 0.0f;
    }
    
    public void setContainerContentAmount(final float n) {
        this.setContainerContentAmount(n, false, false);
    }
    
    public void setContainerContentAmount(float itemCapacity, final boolean b, final boolean b2) {
        if (!this.isContainer()) {
            return;
        }
        int n = this.scriptPart.container.capacity;
        if (this.getInventoryItem() != null) {
            n = this.getInventoryItem().getMaxCapacity();
        }
        if (!b) {
            itemCapacity = Math.min(itemCapacity, (float)n);
        }
        itemCapacity = Math.max(itemCapacity, 0.0f);
        this.getModData().rawset((Object)"contentAmount", (Object)(double)itemCapacity);
        if (this.getInventoryItem() != null) {
            this.getInventoryItem().setItemCapacity(itemCapacity);
        }
        if (!b2) {
            this.getVehicle().updateTotalMass();
        }
    }
    
    public int getContainerSeatNumber() {
        if (!this.isContainer()) {
            return -1;
        }
        return this.scriptPart.container.seat;
    }
    
    public String getLuaFunction(final String key) {
        if (this.scriptPart == null || this.scriptPart.luaFunctions == null) {
            return null;
        }
        return this.scriptPart.luaFunctions.get(key);
    }
    
    protected VehicleScript.Model getScriptModelById(final String s) {
        if (this.scriptPart == null || this.scriptPart.models == null) {
            return null;
        }
        for (int i = 0; i < this.scriptPart.models.size(); ++i) {
            final VehicleScript.Model model = this.scriptPart.models.get(i);
            if (s.equals(model.id)) {
                return model;
            }
        }
        return null;
    }
    
    public void setModelVisible(final String s, final boolean b) {
        final VehicleScript.Model scriptModelById = this.getScriptModelById(s);
        if (scriptModelById == null) {
            return;
        }
        this.vehicle.setModelVisible(this, scriptModelById, b);
    }
    
    public VehiclePart getParent() {
        return this.parent;
    }
    
    public void addChild(final VehiclePart e) {
        if (this.children == null) {
            this.children = new ArrayList<VehiclePart>();
        }
        this.children.add(e);
    }
    
    public int getChildCount() {
        if (this.children == null) {
            return 0;
        }
        return this.children.size();
    }
    
    public VehiclePart getChild(final int index) {
        if (this.children == null || index < 0 || index >= this.children.size()) {
            return null;
        }
        return this.children.get(index);
    }
    
    public VehicleDoor getDoor() {
        return this.door;
    }
    
    public VehicleWindow getWindow() {
        return this.window;
    }
    
    public VehiclePart getChildWindow() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final VehiclePart child = this.getChild(i);
            if (child.getWindow() != null) {
                return child;
            }
        }
        return null;
    }
    
    public VehicleWindow findWindow() {
        final VehiclePart childWindow = this.getChildWindow();
        return (childWindow == null) ? null : childWindow.getWindow();
    }
    
    public VehicleScript.Anim getAnimById(final String anObject) {
        if (this.scriptPart == null || this.scriptPart.anims == null) {
            return null;
        }
        for (int i = 0; i < this.scriptPart.anims.size(); ++i) {
            final VehicleScript.Anim anim = this.scriptPart.anims.get(i);
            if (anim.id.equals(anObject)) {
                return anim;
            }
        }
        return null;
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        GameWindow.WriteStringUTF(byteBuffer, this.getId());
        byteBuffer.put((byte)(this.bCreated ? 1 : 0));
        byteBuffer.putFloat(this.lastUpdated);
        if (this.getInventoryItem() == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.getInventoryItem().saveWithSize(byteBuffer, false);
        }
        if (this.getItemContainer() == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.getItemContainer().save(byteBuffer);
        }
        if (!this.hasModData() || this.getModData().isEmpty()) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.getModData().save(byteBuffer);
        }
        if (this.getDeviceData() == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.getDeviceData().save(byteBuffer, false);
        }
        if (this.light == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.light.save(byteBuffer);
        }
        if (this.door == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.door.save(byteBuffer);
        }
        if (this.window == null) {
            byteBuffer.put((byte)0);
        }
        else {
            byteBuffer.put((byte)1);
            this.window.save(byteBuffer);
        }
        byteBuffer.putInt(this.condition);
        byteBuffer.putFloat(this.wheelFriction);
        byteBuffer.putInt(this.mechanicSkillInstaller);
        byteBuffer.putFloat(this.suspensionCompression);
        byteBuffer.putFloat(this.suspensionDamping);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.partId = GameWindow.ReadStringUTF(byteBuffer);
        this.bCreated = (byteBuffer.get() == 1);
        this.lastUpdated = byteBuffer.getFloat();
        if (byteBuffer.get() == 1) {
            this.item = InventoryItem.loadItem(byteBuffer, n);
        }
        if (byteBuffer.get() == 1) {
            if (this.container == null) {
                this.container = new ItemContainer();
                this.container.parent = this.getVehicle();
                this.container.vehiclePart = this;
            }
            this.container.getItems().clear();
            this.container.ID = 0;
            this.container.load(byteBuffer, n);
        }
        if (byteBuffer.get() == 1) {
            this.getModData().load(byteBuffer, n);
        }
        if (byteBuffer.get() == 1) {
            if (this.getDeviceData() == null) {
                this.createSignalDevice();
            }
            this.getDeviceData().load(byteBuffer, n, false);
        }
        if (byteBuffer.get() == 1) {
            if (this.light == null) {
                this.light = new VehicleLight();
            }
            this.light.load(byteBuffer, n);
        }
        if (byteBuffer.get() == 1) {
            if (this.door == null) {
                this.door = new VehicleDoor(this);
            }
            this.door.load(byteBuffer, n);
        }
        if (byteBuffer.get() == 1) {
            if (this.window == null) {
                this.window = new VehicleWindow(this);
            }
            this.window.load(byteBuffer, n);
        }
        if (n >= 116) {
            this.setCondition(byteBuffer.getInt());
        }
        if (n >= 118) {
            this.setWheelFriction(byteBuffer.getFloat());
            this.setMechanicSkillInstaller(byteBuffer.getInt());
        }
        if (n >= 119) {
            this.setSuspensionCompression(byteBuffer.getFloat());
            this.setSuspensionDamping(byteBuffer.getFloat());
        }
    }
    
    public int getWheelIndex() {
        if (this.scriptPart == null || this.scriptPart.wheel == null) {
            return -1;
        }
        for (int i = 0; i < this.vehicle.script.getWheelCount(); ++i) {
            if (this.scriptPart.wheel.equals(this.vehicle.script.getWheel(i).id)) {
                return i;
            }
        }
        return -1;
    }
    
    public void createSpotLight(final float n, final float n2, final float dist, final float intensity, final float dot, final int focusing) {
        this.light = ((this.light == null) ? new VehicleLight() : this.light);
        this.light.offset.set(n, n2, 0.0f);
        this.light.dist = dist;
        this.light.intensity = intensity;
        this.light.dot = dot;
        this.light.focusing = focusing;
    }
    
    public VehicleLight getLight() {
        return this.light;
    }
    
    public float getLightDistance() {
        return (this.light == null) ? 0.0f : (8.0f + 16.0f * this.getCondition() / 100.0f);
    }
    
    public float getLightIntensity() {
        return (this.light == null) ? 0.0f : (0.5f + 0.25f * this.getCondition() / 100.0f);
    }
    
    public float getLightFocusing() {
        return (this.light == null) ? 0.0f : ((float)(10 + (int)(90.0f * (1.0f - this.getCondition() / 100.0f))));
    }
    
    public void setLightActive(final boolean active) {
        if (this.light == null || this.light.active == active) {
            return;
        }
        this.light.active = active;
        if (GameServer.bServer) {
            final BaseVehicle vehicle = this.vehicle;
            vehicle.updateFlags |= 0x8;
        }
    }
    
    public DeviceData createSignalDevice() {
        if (this.deviceData == null) {
            this.deviceData = new DeviceData(this);
        }
        if (this.chatElement == null) {
            this.chatElement = new ChatElement(this, 5, "device");
        }
        return this.deviceData;
    }
    
    public boolean hasDevicePower() {
        return this.vehicle.getBatteryCharge() > 0.0f;
    }
    
    @Override
    public DeviceData getDeviceData() {
        return this.deviceData;
    }
    
    @Override
    public void setDeviceData(DeviceData deviceData) {
        if (deviceData == null) {
            deviceData = new DeviceData(this);
        }
        (this.deviceData = deviceData).setParent(this);
    }
    
    @Override
    public float getDelta() {
        if (this.deviceData != null) {
            return this.deviceData.getPower();
        }
        return 0.0f;
    }
    
    @Override
    public void setDelta(final float power) {
        if (this.deviceData != null) {
            this.deviceData.setPower(power);
        }
    }
    
    @Override
    public float getX() {
        return this.vehicle.getX();
    }
    
    @Override
    public float getY() {
        return this.vehicle.getY();
    }
    
    @Override
    public float getZ() {
        return this.vehicle.getZ();
    }
    
    @Override
    public IsoGridSquare getSquare() {
        return this.vehicle.getSquare();
    }
    
    @Override
    public void AddDeviceText(final String s, final float n, final float n2, final float n3, final String s2, final int n4) {
        if (this.deviceData != null && this.deviceData.getIsTurnedOn()) {
            this.deviceData.doReceiveSignal(n4);
            if (this.deviceData.getDeviceVolume() > 0.0f) {
                this.chatElement.addChatLine(s, n, n2, n3, UIFont.Medium, (float)this.deviceData.getDeviceVolumeRange(), "default", true, true, true, true, true, true);
                if (s2 != null) {
                    LuaEventManager.triggerEvent("OnDeviceText", s2, this.getX(), this.getY(), this.getZ(), s, this);
                }
            }
        }
    }
    
    @Override
    public boolean HasPlayerInRange() {
        return this.hasPlayerInRange;
    }
    
    private boolean playerWithinBounds(final IsoPlayer isoPlayer, final float n) {
        return isoPlayer != null && !isoPlayer.isDead() && (isoPlayer.getX() > this.getX() - n || this.getX() < this.getX() + n) && (isoPlayer.getY() > this.getY() - n || this.getY() < this.getY() + n);
    }
    
    public void updateSignalDevice() {
        if (this.deviceData == null) {
            return;
        }
        if (GameClient.bClient) {
            this.deviceData.updateSimple();
        }
        else {
            this.deviceData.update(true, this.hasPlayerInRange);
        }
        if (!GameServer.bServer) {
            this.hasPlayerInRange = false;
            if (this.deviceData.getIsTurnedOn()) {
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    if (this.playerWithinBounds(IsoPlayer.players[i], this.deviceData.getDeviceVolumeRange() * 0.6f)) {
                        this.hasPlayerInRange = true;
                        break;
                    }
                }
            }
            this.chatElement.setHistoryRange(this.deviceData.getDeviceVolumeRange() * 0.6f);
            this.chatElement.update();
        }
        else {
            this.hasPlayerInRange = false;
        }
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(final String category) {
        this.category = category;
    }
    
    public int getCondition() {
        return this.condition;
    }
    
    public void setCondition(int n) {
        n = Math.min(100, n);
        n = Math.max(0, n);
        if (this.getVehicle().getDriver() != null) {
            if (this.condition > 60 && n < 60 && n > 40) {
                LuaEventManager.triggerEvent("OnVehicleDamageTexture", this.getVehicle().getDriver());
            }
            if (this.condition > 40 && n < 40) {
                LuaEventManager.triggerEvent("OnVehicleDamageTexture", this.getVehicle().getDriver());
            }
        }
        this.condition = n;
        if (this.getInventoryItem() != null) {
            this.getInventoryItem().setCondition(n);
        }
        this.getVehicle().bDoDamageOverlay = true;
        if ("lightbar".equals(this.getId())) {
            this.getVehicle().lightbarLightsMode.set(0);
            this.getVehicle().setLightbarSirenMode(0);
        }
    }
    
    public void damage(final int n) {
        if (this.getWindow() != null) {
            this.getWindow().damage(n);
            return;
        }
        this.setCondition(this.getCondition() - n);
        this.getVehicle().transmitPartCondition(this);
    }
    
    public boolean isSpecificItem() {
        return this.specificItem;
    }
    
    public void setSpecificItem(final boolean specificItem) {
        this.specificItem = specificItem;
    }
    
    public float getWheelFriction() {
        return this.wheelFriction;
    }
    
    public void setWheelFriction(final float wheelFriction) {
        this.wheelFriction = wheelFriction;
    }
    
    public int getMechanicSkillInstaller() {
        return this.mechanicSkillInstaller;
    }
    
    public void setMechanicSkillInstaller(final int mechanicSkillInstaller) {
        this.mechanicSkillInstaller = mechanicSkillInstaller;
    }
    
    public float getSuspensionDamping() {
        return this.suspensionDamping;
    }
    
    public void setSuspensionDamping(final float suspensionDamping) {
        this.suspensionDamping = suspensionDamping;
    }
    
    public float getSuspensionCompression() {
        return this.suspensionCompression;
    }
    
    public void setSuspensionCompression(final float suspensionCompression) {
        this.suspensionCompression = suspensionCompression;
    }
    
    public float getEngineLoudness() {
        return this.engineLoudness;
    }
    
    public void setEngineLoudness(final float engineLoudness) {
        this.engineLoudness = engineLoudness;
    }
    
    public void repair() {
        final VehicleScript script = this.vehicle.getScript();
        final float containerContentAmount = this.getContainerContentAmount();
        if (this.getItemType() != null && !this.getItemType().isEmpty() && this.getInventoryItem() == null) {
            final String s = this.getItemType().get(Rand.Next(this.getItemType().size()));
            if (s != null && !s.isEmpty()) {
                final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
                if (createItem != null) {
                    this.setInventoryItem(createItem);
                    if (createItem.getMaxCapacity() > 0) {
                        createItem.setItemCapacity((float)createItem.getMaxCapacity());
                    }
                    this.vehicle.transmitPartItem(this);
                    final int wheelIndex = this.getWheelIndex();
                    if (wheelIndex != -1) {
                        this.vehicle.setTireRemoved(wheelIndex, false);
                        this.setModelVisible("InflatedTirePlusWheel", true);
                    }
                }
            }
        }
        if (this.getDoor() != null && this.getDoor().isLockBroken()) {
            this.getDoor().setLockBroken(false);
            this.vehicle.transmitPartDoor(this);
        }
        if (this.getCondition() != 100) {
            this.setCondition(100);
            if (this.getInventoryItem() != null) {
                this.doInventoryItemStats(this.getInventoryItem(), this.getMechanicSkillInstaller());
            }
            this.vehicle.transmitPartCondition(this);
        }
        if (this.isContainer() && this.getItemContainer() == null && containerContentAmount != this.getContainerCapacity()) {
            this.setContainerContentAmount((float)this.getContainerCapacity());
            this.vehicle.transmitPartModData(this);
        }
        if (this.getInventoryItem() instanceof Drainable && ((Drainable)this.getInventoryItem()).getUsedDelta() < 1.0f) {
            ((Drainable)this.getInventoryItem()).setUsedDelta(1.0f);
            this.vehicle.transmitPartUsedDelta(this);
        }
        if ("Engine".equalsIgnoreCase(this.getId())) {
            this.vehicle.setEngineFeature(100, (int)(script.getEngineLoudness() * SandboxOptions.getInstance().ZombieAttractionMultiplier.getValue()), (int)script.getEngineForce());
            this.vehicle.transmitEngine();
        }
        this.vehicle.updatePartStats();
        this.vehicle.updateBulletStats();
    }
}
