// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.util.StringUtils;
import zombie.debug.DebugLog;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.iso.sprite.IsoSprite;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.textures.Texture;
import zombie.scripting.ScriptManager;
import zombie.core.properties.PropertyContainer;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.core.Translator;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.inventory.InventoryItem;

public class Moveable extends InventoryItem
{
    protected String worldSprite;
    private boolean isLight;
    private boolean lightUseBattery;
    private boolean lightHasBattery;
    private String lightBulbItem;
    private float lightPower;
    private float lightDelta;
    private float lightR;
    private float lightG;
    private float lightB;
    private boolean isMultiGridAnchor;
    private IsoSpriteGrid spriteGrid;
    private String customNameFull;
    private String movableFullName;
    protected boolean canBeDroppedOnFloor;
    private boolean hasReadWorldSprite;
    protected String customItem;
    
    public Moveable(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.worldSprite = "";
        this.isLight = false;
        this.lightUseBattery = false;
        this.lightHasBattery = false;
        this.lightBulbItem = "Base.LightBulb";
        this.lightPower = 0.0f;
        this.lightDelta = 2.5E-4f;
        this.lightR = 1.0f;
        this.lightG = 1.0f;
        this.lightB = 1.0f;
        this.isMultiGridAnchor = false;
        this.customNameFull = "Moveable Object";
        this.movableFullName = "Moveable Object";
        this.canBeDroppedOnFloor = false;
        this.hasReadWorldSprite = false;
        this.customItem = null;
        this.cat = ItemType.Moveable;
    }
    
    public Moveable(final String s, final String s2, final String s3, final Item item) {
        super(s, s2, s3, item);
        this.worldSprite = "";
        this.isLight = false;
        this.lightUseBattery = false;
        this.lightHasBattery = false;
        this.lightBulbItem = "Base.LightBulb";
        this.lightPower = 0.0f;
        this.lightDelta = 2.5E-4f;
        this.lightR = 1.0f;
        this.lightG = 1.0f;
        this.lightB = 1.0f;
        this.isMultiGridAnchor = false;
        this.customNameFull = "Moveable Object";
        this.movableFullName = "Moveable Object";
        this.canBeDroppedOnFloor = false;
        this.hasReadWorldSprite = false;
        this.customItem = null;
        this.cat = ItemType.Moveable;
    }
    
    @Override
    public String getName() {
        if ("Moveable Object".equals(this.movableFullName)) {
            return this.name;
        }
        if (this.movableFullName.equals(this.name)) {
            return Translator.getMoveableDisplayName(this.customNameFull);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Translator.getMoveableDisplayName(this.movableFullName), this.customNameFull.substring(this.movableFullName.length()));
    }
    
    @Override
    public String getDisplayName() {
        return this.getName();
    }
    
    public boolean CanBeDroppedOnFloor() {
        if (this.worldSprite != null && this.spriteGrid != null) {
            final PropertyContainer properties = IsoSpriteManager.instance.getSprite(this.worldSprite).getProperties();
            return this.canBeDroppedOnFloor || !properties.Is("ForceSingleItem");
        }
        return this.canBeDroppedOnFloor;
    }
    
    public String getMovableFullName() {
        return this.movableFullName;
    }
    
    public String getCustomNameFull() {
        return this.customNameFull;
    }
    
    public boolean isMultiGridAnchor() {
        return this.isMultiGridAnchor;
    }
    
    public IsoSpriteGrid getSpriteGrid() {
        return this.spriteGrid;
    }
    
    public String getWorldSprite() {
        return this.worldSprite;
    }
    
    public boolean ReadFromWorldSprite(final String worldSprite) {
        if (worldSprite == null) {
            return false;
        }
        if (this.hasReadWorldSprite && this.worldSprite != null && this.worldSprite.equalsIgnoreCase(worldSprite)) {
            return true;
        }
        this.customItem = null;
        try {
            final IsoSprite sprite = IsoSpriteManager.instance.getSprite(worldSprite);
            if (sprite != null) {
                final PropertyContainer properties = sprite.getProperties();
                if (properties.Is("IsMoveAble")) {
                    if (properties.Is("CustomItem")) {
                        this.customItem = properties.Val("CustomItem");
                        final Item findItem = ScriptManager.instance.FindItem(this.customItem);
                        if (findItem != null) {
                            final float actualWeight = findItem.ActualWeight;
                            this.ActualWeight = actualWeight;
                            this.Weight = actualWeight;
                        }
                        this.worldSprite = worldSprite;
                        if (sprite.getSpriteGrid() != null) {
                            this.spriteGrid = sprite.getSpriteGrid();
                            this.isMultiGridAnchor = (sprite.getSpriteGrid().getSpriteIndex(sprite) == 0);
                        }
                        return true;
                    }
                    this.isLight = properties.Is("lightR");
                    this.worldSprite = worldSprite;
                    float n = 1.0f;
                    if (properties.Is("PickUpWeight")) {
                        n = Float.parseFloat(properties.Val("PickUpWeight")) / 10.0f;
                    }
                    this.Weight = n;
                    this.ActualWeight = n;
                    this.setCustomWeight(true);
                    String val = "Moveable Object";
                    if (properties.Is("CustomName")) {
                        if (properties.Is("GroupName")) {
                            val = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, properties.Val("GroupName"), properties.Val("CustomName"));
                        }
                        else {
                            val = properties.Val("CustomName");
                        }
                    }
                    this.movableFullName = val;
                    this.name = val;
                    this.customNameFull = val;
                    if (sprite.getSpriteGrid() != null) {
                        this.spriteGrid = sprite.getSpriteGrid();
                        final int spriteIndex = sprite.getSpriteGrid().getSpriteIndex(sprite);
                        final int spriteCount = sprite.getSpriteGrid().getSpriteCount();
                        this.isMultiGridAnchor = (spriteIndex == 0);
                        if (!properties.Is("ForceSingleItem")) {
                            this.name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.name, spriteIndex + 1, spriteCount);
                        }
                        else {
                            this.name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name);
                        }
                        this.customNameFull = this.name;
                        Texture texture = null;
                        final String s = "Item_Flatpack";
                        if (s != null) {
                            texture = Texture.getSharedTexture(s);
                            this.setColor(new Color(Rand.Next(0.7f, 1.0f), Rand.Next(0.7f, 1.0f), Rand.Next(0.7f, 1.0f)));
                        }
                        if (texture == null) {
                            texture = Texture.getSharedTexture("media/inventory/Question_On.png");
                        }
                        this.setTexture(texture);
                    }
                    else if (this.texture == null || this.texture.getName() == null || this.texture.getName().equals("Item_Moveable_object") || this.texture.getName().equals("Question_On")) {
                        Texture texture2 = null;
                        String s2 = worldSprite;
                        if (s2 != null) {
                            texture2 = Texture.getSharedTexture(s2);
                            if (texture2 != null) {
                                texture2 = texture2.splitIcon();
                            }
                        }
                        if (texture2 == null) {
                            if (!properties.Is("MoveType")) {
                                s2 = "Item_Moveable_object";
                            }
                            else if (properties.Val("MoveType").equals("WallObject")) {
                                s2 = "Item_Moveable_wallobject";
                            }
                            else if (properties.Val("MoveType").equals("WindowObject")) {
                                s2 = "Item_Moveable_windowobject";
                            }
                            else if (properties.Val("MoveType").equals("Window")) {
                                s2 = "Item_Moveable_window";
                            }
                            else if (properties.Val("MoveType").equals("FloorTile")) {
                                s2 = "Item_Moveable_floortile";
                            }
                            else if (properties.Val("MoveType").equals("FloorRug")) {
                                s2 = "Item_Moveable_floorrug";
                            }
                            else if (properties.Val("MoveType").equals("Vegitation")) {
                                s2 = "Item_Moveable_vegitation";
                            }
                            if (s2 != null) {
                                texture2 = Texture.getSharedTexture(s2);
                            }
                        }
                        if (texture2 == null) {
                            texture2 = Texture.getSharedTexture("media/inventory/Question_On.png");
                        }
                        this.setTexture(texture2);
                    }
                    return this.hasReadWorldSprite = true;
                }
            }
        }
        catch (Exception ex) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex.getMessage()));
        }
        System.out.println("Warning: Moveable not valid");
        return false;
    }
    
    public boolean isLight() {
        return this.isLight;
    }
    
    public void setLight(final boolean isLight) {
        this.isLight = isLight;
    }
    
    public boolean isLightUseBattery() {
        return this.lightUseBattery;
    }
    
    public void setLightUseBattery(final boolean lightUseBattery) {
        this.lightUseBattery = lightUseBattery;
    }
    
    public boolean isLightHasBattery() {
        return this.lightHasBattery;
    }
    
    public void setLightHasBattery(final boolean lightHasBattery) {
        this.lightHasBattery = lightHasBattery;
    }
    
    public String getLightBulbItem() {
        return this.lightBulbItem;
    }
    
    public void setLightBulbItem(final String lightBulbItem) {
        this.lightBulbItem = lightBulbItem;
    }
    
    public float getLightPower() {
        return this.lightPower;
    }
    
    public void setLightPower(final float lightPower) {
        this.lightPower = lightPower;
    }
    
    public float getLightDelta() {
        return this.lightDelta;
    }
    
    public void setLightDelta(final float lightDelta) {
        this.lightDelta = lightDelta;
    }
    
    public float getLightR() {
        return this.lightR;
    }
    
    public void setLightR(final float lightR) {
        this.lightR = lightR;
    }
    
    public float getLightG() {
        return this.lightG;
    }
    
    public void setLightG(final float lightG) {
        this.lightG = lightG;
    }
    
    public float getLightB() {
        return this.lightB;
    }
    
    public void setLightB(final float lightB) {
        this.lightB = lightB;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Moveable.ordinal();
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        GameWindow.WriteString(byteBuffer, this.worldSprite);
        byteBuffer.put((byte)(this.isLight ? 1 : 0));
        if (this.isLight) {
            byteBuffer.put((byte)(this.lightUseBattery ? 1 : 0));
            byteBuffer.put((byte)(this.lightHasBattery ? 1 : 0));
            byteBuffer.put((byte)((this.lightBulbItem != null) ? 1 : 0));
            if (this.lightBulbItem != null) {
                GameWindow.WriteString(byteBuffer, this.lightBulbItem);
            }
            byteBuffer.putFloat(this.lightPower);
            byteBuffer.putFloat(this.lightDelta);
            byteBuffer.putFloat(this.lightR);
            byteBuffer.putFloat(this.lightG);
            byteBuffer.putFloat(this.lightB);
        }
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        this.worldSprite = GameWindow.ReadString(byteBuffer);
        if (!this.ReadFromWorldSprite(this.worldSprite) && this instanceof Radio) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.fullType != null) ? this.fullType : "unknown"));
        }
        if (this.customItem == null && !StringUtils.isNullOrWhitespace(this.worldSprite) && !this.type.equalsIgnoreCase(this.worldSprite)) {
            this.type = this.worldSprite;
            this.fullType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module, this.worldSprite);
        }
        this.isLight = (byteBuffer.get() == 1);
        if (this.isLight) {
            this.lightUseBattery = (byteBuffer.get() == 1);
            this.lightHasBattery = (byteBuffer.get() == 1);
            if (byteBuffer.get() == 1) {
                this.lightBulbItem = GameWindow.ReadString(byteBuffer);
            }
            this.lightPower = byteBuffer.getFloat();
            this.lightDelta = byteBuffer.getFloat();
            this.lightR = byteBuffer.getFloat();
            this.lightG = byteBuffer.getFloat();
            this.lightB = byteBuffer.getFloat();
        }
    }
    
    public void setWorldSprite(final String worldSprite) {
        this.worldSprite = worldSprite;
    }
}
