// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import java.util.Collection;
import zombie.characters.SurvivorDesc;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.ui.ObjectTooltip;
import zombie.core.Translator;
import zombie.util.io.BitHeaderRead;
import java.io.IOException;
import java.util.Iterator;
import zombie.util.io.BitHeaderWrite;
import zombie.GameWindow;
import zombie.util.io.BitHeader;
import java.nio.ByteBuffer;
import zombie.scripting.ScriptManager;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoCompost;
import zombie.Lua.LuaEventManager;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItemFactory;
import zombie.network.GameClient;
import fmod.fmod.FMODManager;
import zombie.util.StringUtils;
import zombie.network.GameServer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.audio.BaseSoundEmitter;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFireplace;
import zombie.core.Rand;
import zombie.SandboxOptions;
import zombie.characters.skills.PerkFactory;
import zombie.characters.IsoPlayer;
import zombie.Lua.LuaManager;
import zombie.iso.IsoWorld;
import zombie.inventory.ItemSoundManager;
import zombie.GameTime;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import zombie.core.textures.Texture;
import java.util.ArrayList;
import java.util.List;
import zombie.inventory.InventoryItem;

public final class Food extends InventoryItem
{
    protected boolean bBadCold;
    protected boolean bGoodHot;
    private static final float MIN_HEAT = 0.2f;
    private static final float MAX_HEAT = 3.0f;
    protected float Heat;
    protected float endChange;
    protected float hungChange;
    protected String useOnConsume;
    protected boolean rotten;
    protected boolean bDangerousUncooked;
    protected int LastCookMinute;
    public float thirstChange;
    public boolean Poison;
    private List<String> ReplaceOnCooked;
    private float baseHunger;
    public ArrayList<String> spices;
    private boolean isSpice;
    private int poisonDetectionLevel;
    private Integer PoisonLevelForRecipe;
    private int UseForPoison;
    private int PoisonPower;
    private String FoodType;
    private String CustomEatSound;
    private boolean RemoveNegativeEffectOnCooked;
    private String Chef;
    private String OnCooked;
    private String WorldTextureCooked;
    private String WorldTextureRotten;
    private String WorldTextureOverdone;
    private int fluReduction;
    private int ReduceFoodSickness;
    private float painReduction;
    private String HerbalistType;
    private float carbohydrates;
    private float lipids;
    private float proteins;
    private float calories;
    private boolean packaged;
    private float freezingTime;
    private boolean frozen;
    private boolean canBeFrozen;
    protected float LastFrozenUpdate;
    public static final float FreezerAgeMultiplier = 0.02f;
    private String replaceOnRotten;
    private boolean forceFoodTypeAsName;
    private float rottenTime;
    private float compostTime;
    private String onEat;
    private boolean badInMicrowave;
    private boolean cookedInMicrowave;
    private long m_cookingSound;
    private int m_cookingParameter;
    private static final int COOKING_STATE_COOKING = 0;
    private static final int COOKING_STATE_BURNING = 1;
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Food";
    }
    
    public Food(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.bBadCold = false;
        this.bGoodHot = false;
        this.Heat = 1.0f;
        this.endChange = 0.0f;
        this.hungChange = 0.0f;
        this.useOnConsume = null;
        this.rotten = false;
        this.bDangerousUncooked = false;
        this.LastCookMinute = 0;
        this.thirstChange = 0.0f;
        this.Poison = false;
        this.ReplaceOnCooked = null;
        this.baseHunger = 0.0f;
        this.spices = null;
        this.isSpice = false;
        this.poisonDetectionLevel = -1;
        this.PoisonLevelForRecipe = 0;
        this.UseForPoison = 0;
        this.PoisonPower = 0;
        this.FoodType = null;
        this.CustomEatSound = null;
        this.RemoveNegativeEffectOnCooked = false;
        this.Chef = null;
        this.OnCooked = null;
        this.fluReduction = 0;
        this.ReduceFoodSickness = 0;
        this.painReduction = 0.0f;
        this.carbohydrates = 0.0f;
        this.lipids = 0.0f;
        this.proteins = 0.0f;
        this.calories = 0.0f;
        this.packaged = false;
        this.freezingTime = 0.0f;
        this.frozen = false;
        this.canBeFrozen = true;
        this.LastFrozenUpdate = -1.0f;
        this.replaceOnRotten = null;
        this.forceFoodTypeAsName = false;
        this.rottenTime = 0.0f;
        this.compostTime = 0.0f;
        this.onEat = null;
        this.badInMicrowave = false;
        this.cookedInMicrowave = false;
        this.m_cookingSound = 0L;
        this.m_cookingParameter = -1;
        Texture.WarnFailFindTexture = false;
        this.texturerotten = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4));
        this.textureCooked = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4));
        this.textureBurnt = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4));
        String replacement = "Overdone.png";
        if (this.textureBurnt == null) {
            this.textureBurnt = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4));
            if (this.textureBurnt != null) {
                replacement = "Burnt.png";
            }
        }
        String replacement2 = "Rotten.png";
        if (this.texturerotten == null) {
            this.texturerotten = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4));
            if (this.texturerotten != null) {
                replacement2 = "Spoiled.png";
            }
        }
        Texture.WarnFailFindTexture = true;
        if (this.texturerotten == null) {
            this.texturerotten = this.texture;
        }
        if (this.textureCooked == null) {
            this.textureCooked = this.texture;
        }
        if (this.textureBurnt == null) {
            this.textureBurnt = this.texture;
        }
        this.WorldTextureCooked = this.WorldTexture.replace(".png", "Cooked.png");
        this.WorldTextureOverdone = this.WorldTexture.replace(".png", replacement);
        this.WorldTextureRotten = this.WorldTexture.replace(".png", replacement2);
        this.cat = ItemType.Food;
    }
    
    public Food(final String s, final String s2, final String s3, final Item item) {
        super(s, s2, s3, item);
        this.bBadCold = false;
        this.bGoodHot = false;
        this.Heat = 1.0f;
        this.endChange = 0.0f;
        this.hungChange = 0.0f;
        this.useOnConsume = null;
        this.rotten = false;
        this.bDangerousUncooked = false;
        this.LastCookMinute = 0;
        this.thirstChange = 0.0f;
        this.Poison = false;
        this.ReplaceOnCooked = null;
        this.baseHunger = 0.0f;
        this.spices = null;
        this.isSpice = false;
        this.poisonDetectionLevel = -1;
        this.PoisonLevelForRecipe = 0;
        this.UseForPoison = 0;
        this.PoisonPower = 0;
        this.FoodType = null;
        this.CustomEatSound = null;
        this.RemoveNegativeEffectOnCooked = false;
        this.Chef = null;
        this.OnCooked = null;
        this.fluReduction = 0;
        this.ReduceFoodSickness = 0;
        this.painReduction = 0.0f;
        this.carbohydrates = 0.0f;
        this.lipids = 0.0f;
        this.proteins = 0.0f;
        this.calories = 0.0f;
        this.packaged = false;
        this.freezingTime = 0.0f;
        this.frozen = false;
        this.canBeFrozen = true;
        this.LastFrozenUpdate = -1.0f;
        this.replaceOnRotten = null;
        this.forceFoodTypeAsName = false;
        this.rottenTime = 0.0f;
        this.compostTime = 0.0f;
        this.onEat = null;
        this.badInMicrowave = false;
        this.cookedInMicrowave = false;
        this.m_cookingSound = 0L;
        this.m_cookingParameter = -1;
        final String itemName = item.ItemName;
        Texture.WarnFailFindTexture = false;
        this.texture = item.NormalTexture;
        if (item.SpecialTextures.size() == 0) {}
        if (item.SpecialTextures.size() > 0) {
            this.texturerotten = item.SpecialTextures.get(0);
        }
        if (item.SpecialTextures.size() > 1) {
            this.textureCooked = item.SpecialTextures.get(1);
        }
        if (item.SpecialTextures.size() > 2) {
            this.textureBurnt = item.SpecialTextures.get(2);
        }
        Texture.WarnFailFindTexture = true;
        if (this.texturerotten == null) {
            this.texturerotten = this.texture;
        }
        if (this.textureCooked == null) {
            this.textureCooked = this.texture;
        }
        if (this.textureBurnt == null) {
            this.textureBurnt = this.texture;
        }
        if (item.SpecialWorldTextureNames.size() > 0) {
            this.WorldTextureRotten = item.SpecialWorldTextureNames.get(0);
        }
        if (item.SpecialWorldTextureNames.size() > 1) {
            this.WorldTextureCooked = item.SpecialWorldTextureNames.get(1);
        }
        if (item.SpecialWorldTextureNames.size() > 2) {
            this.WorldTextureOverdone = item.SpecialWorldTextureNames.get(2);
        }
        this.cat = ItemType.Food;
    }
    
    @Override
    public boolean IsFood() {
        return true;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Food.ordinal();
    }
    
    @Override
    public void update() {
        final ItemContainer outermostContainer = this.getOutermostContainer();
        if (outermostContainer != null) {
            final float temprature = outermostContainer.getTemprature();
            if (this.Heat > temprature) {
                this.Heat -= 0.001f * GameTime.instance.getMultiplier();
                if (this.Heat < Math.max(0.2f, temprature)) {
                    this.Heat = Math.max(0.2f, temprature);
                }
            }
            if (this.Heat < temprature) {
                this.Heat += temprature / 1000.0f * GameTime.instance.getMultiplier();
                if (this.Heat > Math.min(3.0f, temprature)) {
                    this.Heat = Math.min(3.0f, temprature);
                }
            }
            if (this.IsCookable && !this.isFrozen()) {
                if (this.Heat > 1.6f) {
                    final int minutes = GameTime.getInstance().getMinutes();
                    if (minutes != this.LastCookMinute) {
                        this.LastCookMinute = minutes;
                        float n = this.Heat / 1.5f;
                        if (outermostContainer.getTemprature() <= 1.6f) {
                            n *= 0.05f;
                        }
                        this.CookingTime += n;
                        if (this.shouldPlayCookingSound()) {
                            ItemSoundManager.addItem(this);
                        }
                        if (this.isTaintedWater() && this.CookingTime > Math.min(this.MinutesToCook, 10.0f)) {
                            this.setTaintedWater(false);
                        }
                        if (!this.isCooked() && !this.Burnt && this.CookingTime > this.MinutesToCook) {
                            if (this.getReplaceOnCooked() != null) {
                                for (int i = 0; i < this.getReplaceOnCooked().size(); ++i) {
                                    final InventoryItem addItem = this.container.AddItem(this.getReplaceOnCooked().get(i));
                                    if (addItem != null) {
                                        addItem.copyConditionModData(this);
                                        if (addItem instanceof Food && ((Food)addItem).isBadInMicrowave() && this.container.isMicrowave()) {
                                            addItem.setUnhappyChange(5.0f);
                                            addItem.setBoredomChange(5.0f);
                                            ((Food)addItem).cookedInMicrowave = true;
                                        }
                                    }
                                }
                                this.container.Remove(this);
                                IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                                return;
                            }
                            this.setCooked(true);
                            if (this.getScriptItem().RemoveUnhappinessWhenCooked) {
                                this.setUnhappyChange(0.0f);
                            }
                            if (this.type.equals("RicePot") || this.type.equals("PastaPot") || this.type.equals("RicePan") || this.type.equals("PastaPan") || this.type.equals("WaterPotRice") || this.type.equals("WaterPotPasta") || this.type.equals("WaterSaucepanRice") || this.type.equals("WaterSaucepanPasta") || this.type.equals("RiceBowl") || this.type.equals("PastaBowl")) {
                                this.setAge(0.0f);
                                this.setOffAge(1);
                                this.setOffAgeMax(2);
                            }
                            if (this.isRemoveNegativeEffectOnCooked()) {
                                if (this.thirstChange > 0.0f) {
                                    this.setThirstChange(0.0f);
                                }
                                if (this.unhappyChange > 0.0f) {
                                    this.setUnhappyChange(0.0f);
                                }
                                if (this.boredomChange > 0.0f) {
                                    this.setBoredomChange(0.0f);
                                }
                            }
                            if (this.getOnCooked() != null) {
                                LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget((Object)this.getOnCooked()), new Object[] { this });
                            }
                            if (this.isBadInMicrowave() && this.container.isMicrowave()) {
                                this.setUnhappyChange(5.0f);
                                this.setBoredomChange(5.0f);
                                this.cookedInMicrowave = true;
                            }
                            if (this.Chef != null && !this.Chef.isEmpty()) {
                                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                                    final IsoPlayer isoPlayer = IsoPlayer.players[j];
                                    if (isoPlayer != null && !isoPlayer.isDead() && this.Chef.equals(isoPlayer.getFullName())) {
                                        isoPlayer.getXp().AddXP(PerkFactory.Perks.Cooking, 10.0f);
                                        break;
                                    }
                                }
                            }
                        }
                        if (this.CookingTime > this.MinutesToBurn) {
                            this.Burnt = true;
                            this.setCooked(false);
                        }
                        if (GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier() && this.Burnt && this.CookingTime >= 50.0f && this.CookingTime >= this.MinutesToCook * 2.0f + this.MinutesToBurn / 2.0f && Rand.Next(Rand.AdjustForFramerate(200)) == 0) {
                            int n2 = (this.container != null && this.container.getParent() != null && this.container.getParent().getName() != null && this.container.getParent().getName().equals("Campfire")) ? 1 : 0;
                            if (n2 == 0 && this.container != null && this.container.getParent() != null && this.container.getParent() instanceof IsoFireplace) {
                                n2 = 1;
                            }
                            if (this.container != null && this.container.SourceGrid != null && n2 == 0) {
                                IsoFireManager.StartFire(this.container.SourceGrid.getCell(), this.container.SourceGrid, true, 500000);
                                this.IsCookable = false;
                            }
                        }
                    }
                }
            }
            else if (this.isTaintedWater() && this.Heat > 1.6f && !this.isFrozen()) {
                final int minutes2 = GameTime.getInstance().getMinutes();
                if (minutes2 != this.LastCookMinute) {
                    this.LastCookMinute = minutes2;
                    float n3 = 1.0f;
                    if (outermostContainer.getTemprature() <= 1.6f) {
                        n3 *= (float)0.2;
                    }
                    this.CookingTime += n3;
                    if (this.CookingTime > 10.0f) {
                        this.setTaintedWater(false);
                    }
                }
            }
        }
        this.updateRotting(outermostContainer);
    }
    
    @Override
    public void updateSound(final BaseSoundEmitter baseSoundEmitter) {
        if (this.shouldPlayCookingSound()) {
            if (baseSoundEmitter.isPlaying(this.m_cookingSound)) {
                this.setCookingParameter(baseSoundEmitter);
                return;
            }
            final IsoGridSquare square = this.getOutermostContainer().getParent().getSquare();
            baseSoundEmitter.setPos(square.getX() + 0.5f, square.getY() + 0.5f, (float)square.getZ());
            this.m_cookingSound = baseSoundEmitter.playSoundImpl(this.getCookingSound(), (IsoObject)null);
            this.setCookingParameter(baseSoundEmitter);
        }
        else {
            baseSoundEmitter.stopOrTriggerSound(this.m_cookingSound);
            this.m_cookingSound = 0L;
            this.m_cookingParameter = -1;
            ItemSoundManager.removeItem(this);
        }
    }
    
    private boolean shouldPlayCookingSound() {
        if (GameServer.bServer) {
            return false;
        }
        if (StringUtils.isNullOrWhitespace(this.getCookingSound())) {
            return false;
        }
        final ItemContainer outermostContainer = this.getOutermostContainer();
        return outermostContainer != null && outermostContainer.getParent() != null && outermostContainer.getParent().getObjectIndex() != -1 && outermostContainer.getTemprature() > 1.6f && this.isCookable() && !this.isFrozen() && this.getHeat() > 1.6f;
    }
    
    private void setCookingParameter(final BaseSoundEmitter baseSoundEmitter) {
        final boolean cookingParameter = this.CookingTime > this.MinutesToCook;
        if ((cookingParameter ? 1 : 0) != this.m_cookingParameter) {
            this.m_cookingParameter = (cookingParameter ? 1 : 0);
            baseSoundEmitter.setParameterValue(this.m_cookingSound, FMODManager.instance.getParameterDescription("CookingState"), (float)this.m_cookingParameter);
        }
    }
    
    private void updateRotting(final ItemContainer itemContainer) {
        if (this.OffAgeMax == 1.0E9) {
            return;
        }
        if (GameClient.bClient && !this.isInLocalPlayerInventory()) {
            return;
        }
        if (GameServer.bServer && this.container != null && this.getOutermostContainer() != this.container) {
            return;
        }
        if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
            this.updateAge();
            if (this.isRotten()) {
                final InventoryItem createItem = InventoryItemFactory.CreateItem(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getModule(), this.replaceOnRotten), this);
                if (createItem == null) {
                    DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.replaceOnRotten, this.getFullType()));
                    this.destroyThisItem();
                    return;
                }
                createItem.setAge(this.getAge());
                final IsoWorldInventoryObject worldItem = this.getWorldItem();
                if (worldItem != null && worldItem.getSquare() != null) {
                    final IsoGridSquare square = worldItem.getSquare();
                    if (!GameServer.bServer) {
                        (worldItem.item = createItem).setWorldItem(worldItem);
                        worldItem.updateSprite();
                        IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                        LuaEventManager.triggerEvent("OnContainerUpdate");
                        return;
                    }
                    square.AddWorldInventoryItem(createItem, worldItem.xoff, worldItem.yoff, worldItem.zoff, true);
                }
                else if (this.container != null) {
                    this.container.AddItem(createItem);
                    if (GameServer.bServer) {
                        GameServer.sendAddItemToContainer(this.container, createItem);
                    }
                }
                this.destroyThisItem();
                return;
            }
        }
        if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() >= 0) {
            if (itemContainer != null && itemContainer.parent instanceof IsoCompost) {
                return;
            }
            this.updateAge();
            if (this.getAge() > this.getOffAgeMax() + SandboxOptions.instance.DaysForRottenFoodRemoval.getValue()) {
                this.destroyThisItem();
            }
        }
    }
    
    @Override
    public void updateAge() {
        final ItemContainer outermostContainer = this.getOutermostContainer();
        this.updateFreezing(outermostContainer);
        boolean b = false;
        if (outermostContainer != null && outermostContainer.getSourceGrid() != null && outermostContainer.getSourceGrid().haveElectricity()) {
            b = true;
        }
        final float n = (float)GameTime.getInstance().getWorldAgeHours();
        float n2 = 0.2f;
        if (SandboxOptions.instance.FridgeFactor.getValue() == 1) {
            n2 = 0.4f;
        }
        else if (SandboxOptions.instance.FridgeFactor.getValue() == 2) {
            n2 = 0.3f;
        }
        else if (SandboxOptions.instance.FridgeFactor.getValue() == 4) {
            n2 = 0.1f;
        }
        else if (SandboxOptions.instance.FridgeFactor.getValue() == 5) {
            n2 = 0.03f;
        }
        if (this.LastAged < 0.0f) {
            this.LastAged = n;
        }
        else if (this.LastAged > n) {
            this.LastAged = n;
        }
        if (n > this.LastAged) {
            double n3 = n - this.LastAged;
            if (outermostContainer != null && this.Heat != outermostContainer.getTemprature()) {
                if (n3 < 0.3333333432674408) {
                    if (!IsoWorld.instance.getCell().getProcessItems().contains(this)) {
                        this.Heat = GameTime.instance.Lerp(this.Heat, outermostContainer.getTemprature(), (float)n3 / 0.33333334f);
                        IsoWorld.instance.getCell().addToProcessItems(this);
                    }
                }
                else {
                    this.Heat = outermostContainer.getTemprature();
                }
            }
            if (this.isFrozen()) {
                n3 *= 0.019999999552965164;
            }
            else if (outermostContainer != null && (outermostContainer.getType().equals("fridge") || outermostContainer.getType().equals("freezer"))) {
                if (b) {
                    n3 *= n2;
                }
                else if (SandboxOptions.instance.getElecShutModifier() > -1 && this.LastAged < SandboxOptions.instance.getElecShutModifier() * 24) {
                    n3 = (Math.min((float)(SandboxOptions.instance.getElecShutModifier() * 24), n) - this.LastAged) * n2;
                    if (n > SandboxOptions.instance.getElecShutModifier() * 24) {
                        n3 += n - SandboxOptions.instance.getElecShutModifier() * 24;
                    }
                }
            }
            float n4 = 1.0f;
            if (SandboxOptions.instance.FoodRotSpeed.getValue() == 1) {
                n4 = 1.7f;
            }
            else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 2) {
                n4 = 1.4f;
            }
            else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 4) {
                n4 = 0.7f;
            }
            else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 5) {
                n4 = 0.4f;
            }
            final boolean b2 = !this.Burnt && this.OffAge < 1000000000 && this.Age < this.OffAge;
            final boolean b3 = !this.Burnt && this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax;
            this.Age += (float)(n3 * n4 / 24.0);
            this.LastAged = n;
            final boolean b4 = !this.Burnt && this.OffAge < 1000000000 && this.Age < this.OffAge;
            final boolean b5 = !this.Burnt && this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax;
            if (!GameServer.bServer && (b2 != b4 || b3 != b5)) {
                LuaEventManager.triggerEvent("OnContainerUpdate", this);
            }
        }
    }
    
    @Override
    public void setAutoAge() {
        final ItemContainer outermostContainer = this.getOutermostContainer();
        float n;
        final float b = n = (float)GameTime.getInstance().getWorldAgeHours() / 24.0f + (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30;
        boolean is = false;
        if (outermostContainer != null && outermostContainer.getParent() != null && outermostContainer.getParent().getSprite() != null) {
            is = outermostContainer.getParent().getSprite().getProperties().Is("IsFridge");
        }
        if (outermostContainer != null && (is || outermostContainer.getType().equals("fridge") || outermostContainer.getType().equals("freezer"))) {
            final int value = SandboxOptions.instance.ElecShutModifier.getValue();
            if (value > -1) {
                final float min = Math.min((float)value, b);
                final int value2 = SandboxOptions.instance.FridgeFactor.getValue();
                float n2 = 0.2f;
                if (value2 == 1) {
                    n2 = 0.4f;
                }
                else if (value2 == 2) {
                    n2 = 0.3f;
                }
                else if (value2 == 4) {
                    n2 = 0.1f;
                }
                else if (value2 == 5) {
                    n2 = 0.03f;
                }
                if (outermostContainer.getType().equals("fridge") || !this.canBeFrozen() || is) {
                    n = n - min + min * n2;
                }
                else {
                    float n3 = min;
                    float freezingTime = 100.0f;
                    if (b > min) {
                        final float n4 = (b - min) * 24.0f;
                        final float n5 = 1440.0f / GameTime.getInstance().getMinutesPerDay() * 60.0f * 5.0f;
                        final float n6 = 0.0095999995f;
                        freezingTime -= n6 * n5 * n4;
                        if (freezingTime > 0.0f) {
                            n3 += n4 / 24.0f;
                        }
                        else {
                            n3 += 100.0f / (n6 * n5) / 24.0f;
                            freezingTime = 0.0f;
                        }
                    }
                    n = n - n3 + n3 * 0.02f;
                    this.setFreezingTime(freezingTime);
                }
            }
        }
        final int value3 = SandboxOptions.instance.FoodRotSpeed.getValue();
        float n7 = 1.0f;
        if (value3 == 1) {
            n7 = 1.7f;
        }
        else if (value3 == 2) {
            n7 = 1.4f;
        }
        else if (value3 == 4) {
            n7 = 0.7f;
        }
        else if (value3 == 5) {
            n7 = 0.4f;
        }
        this.Age = n * n7;
        this.LastAged = (float)GameTime.getInstance().getWorldAgeHours();
        this.LastFrozenUpdate = this.LastAged;
        if (outermostContainer != null) {
            this.setHeat(outermostContainer.getTemprature());
        }
    }
    
    public void updateFreezing(final ItemContainer itemContainer) {
        final float lastFrozenUpdate = (float)GameTime.getInstance().getWorldAgeHours();
        if (this.LastFrozenUpdate < 0.0f) {
            this.LastFrozenUpdate = lastFrozenUpdate;
        }
        else if (this.LastFrozenUpdate > lastFrozenUpdate) {
            this.LastFrozenUpdate = lastFrozenUpdate;
        }
        if (lastFrozenUpdate > this.LastFrozenUpdate) {
            final float n = lastFrozenUpdate - this.LastFrozenUpdate;
            final float n2 = 4.0f;
            final float n3 = 1.5f;
            if (this.isFreezing()) {
                this.setFreezingTime(this.getFreezingTime() + n / n2 * 100.0f);
            }
            if (this.isThawing()) {
                float n4 = n3;
                if (itemContainer != null && "fridge".equals(itemContainer.getType()) && itemContainer.isPowered()) {
                    n4 *= 2.0f;
                }
                if (itemContainer != null && itemContainer.getTemprature() > 1.0f) {
                    n4 /= 6.0f;
                }
                this.setFreezingTime(this.getFreezingTime() - n / n4 * 100.0f);
            }
            this.LastFrozenUpdate = lastFrozenUpdate;
        }
    }
    
    @Override
    public float getActualWeight() {
        if (this.haveExtraItems()) {
            final float hungChange = this.getHungChange();
            final float baseHunger = this.getBaseHunger();
            final float n = (baseHunger == 0.0f) ? 0.0f : (hungChange / baseHunger);
            float actualWeight = 0.0f;
            if (this.getReplaceOnUse() != null) {
                final Item item = ScriptManager.instance.getItem(this.getReplaceOnUseFullType());
                if (item != null) {
                    actualWeight = item.getActualWeight();
                }
            }
            return (super.getActualWeight() + this.getExtraItemsWeight() - actualWeight) * n + actualWeight;
        }
        if (this.getReplaceOnUse() != null) {
            final Item item2 = ScriptManager.instance.getItem(this.getReplaceOnUseFullType());
            if (item2 != null) {
                float n2 = 1.0f;
                if (this.getScriptItem().getHungerChange() < 0.0f) {
                    n2 = this.getHungChange() * 100.0f / this.getScriptItem().getHungerChange();
                }
                else if (this.getScriptItem().getThirstChange() < 0.0f) {
                    n2 = this.getThirstChange() * 100.0f / this.getScriptItem().getThirstChange();
                }
                return (this.getScriptItem().getActualWeight() - item2.getActualWeight()) * n2 + item2.getActualWeight();
            }
        }
        return super.getActualWeight();
    }
    
    @Override
    public float getWeight() {
        if (this.getReplaceOnUse() != null) {
            return this.getActualWeight();
        }
        return super.getWeight();
    }
    
    @Override
    public boolean CanStack(final InventoryItem inventoryItem) {
        return false;
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putFloat(this.Age);
        byteBuffer.putFloat(this.LastAged);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
        if (this.calories != 0.0f || this.proteins != 0.0f || this.lipids != 0.0f || this.carbohydrates != 0.0f) {
            allocWrite.addFlags(1);
            byteBuffer.putFloat(this.calories);
            byteBuffer.putFloat(this.proteins);
            byteBuffer.putFloat(this.lipids);
            byteBuffer.putFloat(this.carbohydrates);
        }
        if (this.hungChange != 0.0f) {
            allocWrite.addFlags(2);
            byteBuffer.putFloat(this.hungChange);
        }
        if (this.baseHunger != 0.0f) {
            allocWrite.addFlags(4);
            byteBuffer.putFloat(this.baseHunger);
        }
        if (this.unhappyChange != 0.0f) {
            allocWrite.addFlags(8);
            byteBuffer.putFloat(this.unhappyChange);
        }
        if (this.boredomChange != 0.0f) {
            allocWrite.addFlags(16);
            byteBuffer.putFloat(this.boredomChange);
        }
        if (this.thirstChange != 0.0f) {
            allocWrite.addFlags(32);
            byteBuffer.putFloat(this.thirstChange);
        }
        final BitHeaderWrite allocWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, byteBuffer);
        if (this.Heat != 1.0f) {
            allocWrite2.addFlags(1);
            byteBuffer.putFloat(this.Heat);
        }
        if (this.LastCookMinute != 0.0f) {
            allocWrite2.addFlags(2);
            byteBuffer.putInt(this.LastCookMinute);
        }
        if (this.CookingTime != 0.0f) {
            allocWrite2.addFlags(4);
            byteBuffer.putFloat(this.CookingTime);
        }
        if (this.Cooked) {
            allocWrite2.addFlags(8);
        }
        if (this.Burnt) {
            allocWrite2.addFlags(16);
        }
        if (this.IsCookable) {
            allocWrite2.addFlags(32);
        }
        if (this.bDangerousUncooked) {
            allocWrite2.addFlags(64);
        }
        if (this.poisonDetectionLevel != -1) {
            allocWrite2.addFlags(128);
            byteBuffer.put((byte)this.poisonDetectionLevel);
        }
        if (this.spices != null) {
            allocWrite2.addFlags(256);
            byteBuffer.put((byte)this.spices.size());
            final Iterator<String> iterator = this.spices.iterator();
            while (iterator.hasNext()) {
                GameWindow.WriteString(byteBuffer, iterator.next());
            }
        }
        if (this.PoisonPower != 0) {
            allocWrite2.addFlags(512);
            byteBuffer.put((byte)this.PoisonPower);
        }
        if (this.Chef != null) {
            allocWrite2.addFlags(1024);
            GameWindow.WriteString(byteBuffer, this.Chef);
        }
        if (this.OffAge != 1.0E9) {
            allocWrite2.addFlags(2048);
            byteBuffer.putInt(this.OffAge);
        }
        if (this.OffAgeMax != 1.0E9) {
            allocWrite2.addFlags(4096);
            byteBuffer.putInt(this.OffAgeMax);
        }
        if (this.painReduction != 0.0f) {
            allocWrite2.addFlags(8192);
            byteBuffer.putFloat(this.painReduction);
        }
        if (this.fluReduction != 0) {
            allocWrite2.addFlags(16384);
            byteBuffer.putInt(this.fluReduction);
        }
        if (this.ReduceFoodSickness != 0) {
            allocWrite2.addFlags(32768);
            byteBuffer.putInt(this.ReduceFoodSickness);
        }
        if (this.Poison) {
            allocWrite2.addFlags(65536);
        }
        if (this.UseForPoison != 0) {
            allocWrite2.addFlags(131072);
            byteBuffer.putShort((short)this.UseForPoison);
        }
        if (this.freezingTime != 0.0f) {
            allocWrite2.addFlags(262144);
            byteBuffer.putFloat(this.freezingTime);
        }
        if (this.isFrozen()) {
            allocWrite2.addFlags(524288);
        }
        if (this.LastFrozenUpdate != 0.0f) {
            allocWrite2.addFlags(1048576);
            byteBuffer.putFloat(this.LastFrozenUpdate);
        }
        if (this.rottenTime != 0.0f) {
            allocWrite2.addFlags(2097152);
            byteBuffer.putFloat(this.rottenTime);
        }
        if (this.compostTime != 0.0f) {
            allocWrite2.addFlags(4194304);
            byteBuffer.putFloat(this.compostTime);
        }
        if (this.cookedInMicrowave) {
            allocWrite2.addFlags(8388608);
        }
        if (!allocWrite2.equals(0)) {
            allocWrite.addFlags(64);
            allocWrite2.write();
        }
        else {
            byteBuffer.position(allocWrite2.getStartPosition());
        }
        allocWrite.write();
        allocWrite.release();
        allocWrite2.release();
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        this.Age = byteBuffer.getFloat();
        this.LastAged = byteBuffer.getFloat();
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
        if (!allocRead.equals(0)) {
            if (allocRead.hasFlags(1)) {
                this.calories = byteBuffer.getFloat();
                this.proteins = byteBuffer.getFloat();
                this.lipids = byteBuffer.getFloat();
                this.carbohydrates = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(2)) {
                this.hungChange = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(4)) {
                this.baseHunger = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(8)) {
                this.unhappyChange = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(16)) {
                this.boredomChange = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(32)) {
                this.thirstChange = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(64)) {
                final BitHeaderRead allocRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Integer, byteBuffer);
                if (allocRead2.hasFlags(1)) {
                    this.Heat = byteBuffer.getFloat();
                }
                if (allocRead2.hasFlags(2)) {
                    this.LastCookMinute = byteBuffer.getInt();
                }
                if (allocRead2.hasFlags(4)) {
                    this.CookingTime = byteBuffer.getFloat();
                }
                this.Cooked = allocRead2.hasFlags(8);
                this.Burnt = allocRead2.hasFlags(16);
                this.IsCookable = allocRead2.hasFlags(32);
                this.bDangerousUncooked = allocRead2.hasFlags(64);
                if (allocRead2.hasFlags(128)) {
                    this.poisonDetectionLevel = byteBuffer.get();
                }
                if (allocRead2.hasFlags(256)) {
                    this.spices = new ArrayList<String>();
                    for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
                        this.spices.add(GameWindow.ReadString(byteBuffer));
                    }
                }
                if (allocRead2.hasFlags(512)) {
                    this.PoisonPower = byteBuffer.get();
                }
                if (allocRead2.hasFlags(1024)) {
                    this.Chef = GameWindow.ReadString(byteBuffer);
                }
                if (allocRead2.hasFlags(2048)) {
                    this.OffAge = byteBuffer.getInt();
                }
                if (allocRead2.hasFlags(4096)) {
                    this.OffAgeMax = byteBuffer.getInt();
                }
                if (allocRead2.hasFlags(8192)) {
                    this.painReduction = byteBuffer.getFloat();
                }
                if (allocRead2.hasFlags(16384)) {
                    this.fluReduction = byteBuffer.getInt();
                }
                if (allocRead2.hasFlags(32768)) {
                    this.ReduceFoodSickness = byteBuffer.getInt();
                }
                this.Poison = allocRead2.hasFlags(65536);
                if (allocRead2.hasFlags(131072)) {
                    this.UseForPoison = byteBuffer.getShort();
                }
                if (allocRead2.hasFlags(262144)) {
                    this.freezingTime = byteBuffer.getFloat();
                }
                this.setFrozen(allocRead2.hasFlags(524288));
                if (allocRead2.hasFlags(1048576)) {
                    this.LastFrozenUpdate = byteBuffer.getFloat();
                }
                if (allocRead2.hasFlags(2097152)) {
                    this.rottenTime = byteBuffer.getFloat();
                }
                if (allocRead2.hasFlags(4194304)) {
                    this.compostTime = byteBuffer.getFloat();
                }
                this.cookedInMicrowave = allocRead2.hasFlags(8388608);
                allocRead2.release();
            }
        }
        allocRead.release();
        if (GameServer.bServer && this.LastAged == -1.0f) {
            this.LastAged = (float)GameTime.getInstance().getWorldAgeHours();
        }
    }
    
    @Override
    public boolean finishupdate() {
        if (this.container == null && (this.getWorldItem() == null || this.getWorldItem().getSquare() == null)) {
            return true;
        }
        if (this.IsCookable) {
            return false;
        }
        if (this.container != null && (this.Heat != this.container.getTemprature() || this.container.isTemperatureChanging())) {
            return false;
        }
        if (this.isTaintedWater() && this.container != null && this.container.getTemprature() > 1.0f) {
            return false;
        }
        if ((!GameClient.bClient || this.isInLocalPlayerInventory()) && this.OffAgeMax != 1.0E9) {
            if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
                return false;
            }
            if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() != -1) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean shouldUpdateInWorld() {
        if (!GameClient.bClient && this.OffAgeMax != 1.0E9) {
            if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
                return true;
            }
            if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() != -1) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getName() {
        String s = "";
        if (this.Burnt) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.BurntString);
        }
        else if (this.OffAge < 1000000000 && this.Age < this.OffAge) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.FreshString);
        }
        else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.OffString);
        }
        if (this.isCooked() && !this.Burnt) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.CookedString);
        }
        else if (this.IsCookable && !this.Burnt) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.UnCookedString);
        }
        if (this.isFrozen()) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.FrozenString);
        }
        final String trim = s.trim();
        if (trim.isEmpty()) {
            return this.name;
        }
        return Translator.getText("IGUI_FoodNaming", trim, this.name);
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip, final ObjectTooltip.Layout layout) {
        if (this.getHungerChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem = layout.addItem();
            addItem.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Hunger")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem.setValueRight((int)(this.getHungerChange() * 100.0f), false);
        }
        if (this.getThirstChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem2 = layout.addItem();
            addItem2.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Thirst")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem2.setValueRight((int)(this.getThirstChange() * 100.0f), false);
        }
        if (this.getEnduranceChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem3 = layout.addItem();
            final int n = (int)(this.getEnduranceChange() * 100.0f);
            addItem3.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Endurance")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem3.setValueRight(n, true);
        }
        if (this.getStressChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem4 = layout.addItem();
            final int n2 = (int)(this.getStressChange() * 100.0f);
            addItem4.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Stress")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem4.setValueRight(n2, false);
        }
        if (this.getBoredomChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem5 = layout.addItem();
            final int n3 = (int)this.getBoredomChange();
            addItem5.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Boredom")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem5.setValueRight(n3, false);
        }
        if (this.getUnhappyChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem6 = layout.addItem();
            final int n4 = (int)this.getUnhappyChange();
            addItem6.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Unhappiness")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem6.setValueRight(n4, false);
        }
        if (this.isIsCookable() && !this.isFrozen() && !this.Burnt && this.getHeat() > 1.6) {
            final float cookingTime = this.getCookingTime();
            final float minutesToCook = this.getMinutesToCook();
            final float minutesToBurn = this.getMinutesToBurn();
            float n5 = cookingTime / minutesToCook;
            float n6 = 0.0f;
            float n7 = 0.6f;
            float n8 = 0.0f;
            final float n9 = 0.7f;
            float n10 = 1.0f;
            float n11 = 1.0f;
            float n12 = 0.8f;
            String s = Translator.getText("IGUI_invpanel_Cooking");
            if (cookingTime > minutesToCook) {
                s = Translator.getText("IGUI_invpanel_Burning");
                n10 = 1.0f;
                n11 = 0.0f;
                n12 = 0.0f;
                n5 = (cookingTime - minutesToCook) / (minutesToBurn - minutesToCook);
                n6 = 0.6f;
                n7 = 0.0f;
                n8 = 0.0f;
            }
            final ObjectTooltip.LayoutItem addItem7 = layout.addItem();
            addItem7.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), n10, n11, n12, 1.0f);
            addItem7.setProgress(n5, n6, n7, n8, n9);
        }
        if (this.getFreezingTime() < 100.0f && this.getFreezingTime() > 0.0f) {
            final float n13 = this.getFreezingTime() / 100.0f;
            final float n14 = 0.0f;
            final float n15 = 0.6f;
            final float n16 = 0.0f;
            final float n17 = 0.7f;
            final float n18 = 1.0f;
            final float n19 = 1.0f;
            final float n20 = 0.8f;
            final ObjectTooltip.LayoutItem addItem8 = layout.addItem();
            addItem8.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("IGUI_invpanel_FreezingTime")), n18, n19, n20, 1.0f);
            addItem8.setProgress(n13, n14, n15, n16, n17);
        }
        if ((Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) || this.isPackaged() || (objectTooltip.getCharacter() != null && (objectTooltip.getCharacter().Traits.Nutritionist.isSet() || objectTooltip.getCharacter().Traits.Nutritionist2.isSet()))) {
            final ObjectTooltip.LayoutItem addItem9 = layout.addItem();
            addItem9.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Calories")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem9.setValueRightNoPlus(this.getCalories());
            final ObjectTooltip.LayoutItem addItem10 = layout.addItem();
            addItem10.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Carbs")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem10.setValueRightNoPlus(this.getCarbohydrates());
            final ObjectTooltip.LayoutItem addItem11 = layout.addItem();
            addItem11.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Prots")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem11.setValueRightNoPlus(this.getProteins());
            final ObjectTooltip.LayoutItem addItem12 = layout.addItem();
            addItem12.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Fat")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem12.setValueRightNoPlus(this.getLipids());
        }
        if (this.getScriptItem().RemoveUnhappinessWhenCooked && !this.isCooked()) {
            layout.addItem().setLabel(Translator.getText("Tooltip_food_CookToRemoveUnhappiness"), 1.0f, 0.0f, 0.0f, 1.0f);
        }
        if (this.isbDangerousUncooked() && !this.isCooked() && !this.isBurnt()) {
            layout.addItem().setLabel(Translator.getText("Tooltip_food_Dangerous_uncooked"), 1.0f, 0.0f, 0.0f, 1.0f);
        }
        if ((this.isGoodHot() || this.isBadCold()) && this.Heat < 1.3f) {
            layout.addItem().setLabel(Translator.getText("Tooltip_food_BetterHot"), 1.0f, 0.9f, 0.9f, 1.0f);
        }
        if (this.cookedInMicrowave) {
            layout.addItem().setLabel(Translator.getText("Tooltip_food_CookedInMicrowave"), 1.0f, 0.9f, 0.9f, 1.0f);
        }
        if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
            final ObjectTooltip.LayoutItem addItem13 = layout.addItem();
            addItem13.setLabel("DBG: BaseHunger", 0.0f, 1.0f, 0.0f, 1.0f);
            addItem13.setValueRight((int)(this.getBaseHunger() * 100.0f), false);
            final ObjectTooltip.LayoutItem addItem14 = layout.addItem();
            addItem14.setLabel("DBG: Age", 0.0f, 1.0f, 0.0f, 1.0f);
            addItem14.setValueRightNoPlus(this.getAge() * 24.0f);
            if (this.getOffAgeMax() != 1.0E9) {
                final ObjectTooltip.LayoutItem addItem15 = layout.addItem();
                addItem15.setLabel("DBG: Age Fresh", 0.0f, 1.0f, 0.0f, 1.0f);
                addItem15.setValueRightNoPlus(this.getOffAge() * 24.0f);
                final ObjectTooltip.LayoutItem addItem16 = layout.addItem();
                addItem16.setLabel("DBG: Age Rotten", 0.0f, 1.0f, 0.0f, 1.0f);
                addItem16.setValueRightNoPlus(this.getOffAgeMax() * 24);
            }
            final ObjectTooltip.LayoutItem addItem17 = layout.addItem();
            addItem17.setLabel("DBG: Heat", 0.0f, 1.0f, 0.0f, 1.0f);
            addItem17.setValueRightNoPlus(this.getHeat());
            final ObjectTooltip.LayoutItem addItem18 = layout.addItem();
            addItem18.setLabel("DBG: Freeze Time", 0.0f, 1.0f, 0.0f, 1.0f);
            addItem18.setValueRightNoPlus(this.getFreezingTime());
            final ObjectTooltip.LayoutItem addItem19 = layout.addItem();
            addItem19.setLabel("DBG: Compost Time", 0.0f, 1.0f, 0.0f, 1.0f);
            addItem19.setValueRightNoPlus(this.getCompostTime());
        }
    }
    
    public float getEnduranceChange() {
        if (this.Burnt) {
            return this.endChange / 3.0f;
        }
        if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            return this.endChange / 2.0f;
        }
        if (this.isCooked()) {
            return this.endChange * 2.0f;
        }
        return this.endChange;
    }
    
    @Override
    public float getUnhappyChange() {
        float unhappyChange = this.unhappyChange;
        if (this.isFrozen() && !"Icecream".equals(this.getType())) {
            unhappyChange += 30.0f;
        }
        if (this.Burnt) {
            unhappyChange += 20.0f;
        }
        if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            unhappyChange += 10.0f;
        }
        if (this.Age >= this.OffAgeMax) {
            unhappyChange += 20.0f;
        }
        if (this.isBadCold() && this.IsCookable && this.isCooked() && this.Heat < 1.3f) {
            unhappyChange += 2.0f;
        }
        if (this.isGoodHot() && this.IsCookable && this.isCooked() && this.Heat > 1.3f) {
            unhappyChange -= 2.0f;
        }
        return unhappyChange;
    }
    
    @Override
    public float getBoredomChange() {
        float boredomChange = this.boredomChange;
        if (this.isFrozen() && !"Icecream".equals(this.getType())) {
            boredomChange += 30.0f;
        }
        if (this.Burnt) {
            boredomChange += 20.0f;
        }
        if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            boredomChange += 10.0f;
        }
        if (this.Age >= this.OffAgeMax) {
            boredomChange += 20.0f;
        }
        return boredomChange;
    }
    
    public float getHungerChange() {
        final float hungChange = this.hungChange;
        if (this.Burnt) {
            return hungChange / 3.0f;
        }
        if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            return hungChange / 1.3f;
        }
        if (this.Age >= this.OffAgeMax) {
            return hungChange / 2.2f;
        }
        if (this.isCooked()) {
            return hungChange * 1.3f;
        }
        return hungChange;
    }
    
    @Override
    public float getStressChange() {
        if (this.Burnt) {
            return this.stressChange / 4.0f;
        }
        if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            return this.stressChange / 1.3f;
        }
        if (this.Age >= this.OffAgeMax) {
            return this.stressChange / 2.0f;
        }
        if (this.isCooked()) {
            return this.stressChange * 1.3f;
        }
        return this.stressChange;
    }
    
    @Override
    public float getScore(final SurvivorDesc survivorDesc) {
        return 0.0f - this.getHungerChange() * 100.0f;
    }
    
    public boolean isBadCold() {
        return this.bBadCold;
    }
    
    public void setBadCold(final boolean bBadCold) {
        this.bBadCold = bBadCold;
    }
    
    public boolean isGoodHot() {
        return this.bGoodHot;
    }
    
    public void setGoodHot(final boolean bGoodHot) {
        this.bGoodHot = bGoodHot;
    }
    
    public boolean isCookedInMicrowave() {
        return this.cookedInMicrowave;
    }
    
    public float getHeat() {
        return this.Heat;
    }
    
    @Override
    public float getInvHeat() {
        if (this.Heat > 1.0f) {
            return (this.Heat - 1.0f) / 2.0f;
        }
        return 1.0f - (this.Heat - 0.2f) / 0.8f;
    }
    
    public void setHeat(final float heat) {
        this.Heat = heat;
    }
    
    public float getEndChange() {
        return this.endChange;
    }
    
    public void setEndChange(final float endChange) {
        this.endChange = endChange;
    }
    
    @Deprecated
    public float getBaseHungChange() {
        return this.getHungChange();
    }
    
    public float getHungChange() {
        return this.hungChange;
    }
    
    public void setHungChange(final float hungChange) {
        this.hungChange = hungChange;
    }
    
    public String getUseOnConsume() {
        return this.useOnConsume;
    }
    
    public void setUseOnConsume(final String useOnConsume) {
        this.useOnConsume = useOnConsume;
    }
    
    public boolean isRotten() {
        return this.Age >= this.OffAgeMax;
    }
    
    public boolean isFresh() {
        return this.Age < this.OffAge;
    }
    
    public void setRotten(final boolean rotten) {
        this.rotten = rotten;
    }
    
    public boolean isbDangerousUncooked() {
        return this.bDangerousUncooked;
    }
    
    public void setbDangerousUncooked(final boolean bDangerousUncooked) {
        this.bDangerousUncooked = bDangerousUncooked;
    }
    
    public int getLastCookMinute() {
        return this.LastCookMinute;
    }
    
    public void setLastCookMinute(final int lastCookMinute) {
        this.LastCookMinute = lastCookMinute;
    }
    
    public float getThirstChange() {
        final float thirstChange = this.thirstChange;
        if (this.Burnt) {
            return thirstChange / 5.0f;
        }
        if (this.isCooked()) {
            return thirstChange / 2.0f;
        }
        return thirstChange;
    }
    
    public void setThirstChange(final float thirstChange) {
        this.thirstChange = thirstChange;
    }
    
    public void setReplaceOnCooked(final List<String> replaceOnCooked) {
        this.ReplaceOnCooked = replaceOnCooked;
    }
    
    public List<String> getReplaceOnCooked() {
        return this.ReplaceOnCooked;
    }
    
    public float getBaseHunger() {
        return this.baseHunger;
    }
    
    public void setBaseHunger(final float baseHunger) {
        this.baseHunger = baseHunger;
    }
    
    public boolean isSpice() {
        return this.isSpice;
    }
    
    public void setSpice(final boolean isSpice) {
        this.isSpice = isSpice;
    }
    
    public boolean isPoison() {
        return this.Poison;
    }
    
    public int getPoisonDetectionLevel() {
        return this.poisonDetectionLevel;
    }
    
    public void setPoisonDetectionLevel(final int poisonDetectionLevel) {
        this.poisonDetectionLevel = poisonDetectionLevel;
    }
    
    public Integer getPoisonLevelForRecipe() {
        return this.PoisonLevelForRecipe;
    }
    
    public void setPoisonLevelForRecipe(final Integer poisonLevelForRecipe) {
        this.PoisonLevelForRecipe = poisonLevelForRecipe;
    }
    
    public int getUseForPoison() {
        return this.UseForPoison;
    }
    
    public void setUseForPoison(final int useForPoison) {
        this.UseForPoison = useForPoison;
    }
    
    public int getPoisonPower() {
        return this.PoisonPower;
    }
    
    public void setPoisonPower(final int poisonPower) {
        this.PoisonPower = poisonPower;
    }
    
    public String getFoodType() {
        return this.FoodType;
    }
    
    public void setFoodType(final String foodType) {
        this.FoodType = foodType;
    }
    
    public boolean isRemoveNegativeEffectOnCooked() {
        return this.RemoveNegativeEffectOnCooked;
    }
    
    public void setRemoveNegativeEffectOnCooked(final boolean removeNegativeEffectOnCooked) {
        this.RemoveNegativeEffectOnCooked = removeNegativeEffectOnCooked;
    }
    
    public String getCookingSound() {
        return this.getScriptItem().getCookingSound();
    }
    
    public String getCustomEatSound() {
        return this.CustomEatSound;
    }
    
    public void setCustomEatSound(final String customEatSound) {
        this.CustomEatSound = customEatSound;
    }
    
    public String getChef() {
        return this.Chef;
    }
    
    public void setChef(final String chef) {
        this.Chef = chef;
    }
    
    public String getOnCooked() {
        return this.OnCooked;
    }
    
    public void setOnCooked(final String onCooked) {
        this.OnCooked = onCooked;
    }
    
    public String getHerbalistType() {
        return this.HerbalistType;
    }
    
    public void setHerbalistType(final String herbalistType) {
        this.HerbalistType = herbalistType;
    }
    
    public ArrayList<String> getSpices() {
        return this.spices;
    }
    
    public void setSpices(final ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            if (this.spices != null) {
                this.spices.clear();
            }
            return;
        }
        if (this.spices == null) {
            this.spices = new ArrayList<String>(list);
        }
        else {
            this.spices.clear();
            this.spices.addAll(list);
        }
    }
    
    @Override
    public Texture getTex() {
        if (this.Burnt) {
            return this.textureBurnt;
        }
        if (this.Age >= this.OffAgeMax) {
            return this.texturerotten;
        }
        if (this.isCooked()) {
            return this.textureCooked;
        }
        return super.getTex();
    }
    
    @Override
    public String getWorldTexture() {
        if (this.Burnt) {
            return this.WorldTextureOverdone;
        }
        if (this.Age >= this.OffAgeMax) {
            return this.WorldTextureRotten;
        }
        if (this.isCooked()) {
            return this.WorldTextureCooked;
        }
        return this.WorldTexture;
    }
    
    public int getReduceFoodSickness() {
        return this.ReduceFoodSickness;
    }
    
    public void setReduceFoodSickness(final int reduceFoodSickness) {
        this.ReduceFoodSickness = reduceFoodSickness;
    }
    
    public int getFluReduction() {
        return this.fluReduction;
    }
    
    public void setFluReduction(final int fluReduction) {
        this.fluReduction = fluReduction;
    }
    
    public float getPainReduction() {
        return this.painReduction;
    }
    
    public void setPainReduction(final float painReduction) {
        this.painReduction = painReduction;
    }
    
    public float getCarbohydrates() {
        return this.carbohydrates;
    }
    
    public void setCarbohydrates(final float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
    
    public float getLipids() {
        return this.lipids;
    }
    
    public void setLipids(final float lipids) {
        this.lipids = lipids;
    }
    
    public float getProteins() {
        return this.proteins;
    }
    
    public void setProteins(final float proteins) {
        this.proteins = proteins;
    }
    
    public float getCalories() {
        return this.calories;
    }
    
    public void setCalories(final float calories) {
        this.calories = calories;
    }
    
    public boolean isPackaged() {
        return this.packaged;
    }
    
    public void setPackaged(final boolean packaged) {
        this.packaged = packaged;
    }
    
    public float getFreezingTime() {
        return this.freezingTime;
    }
    
    public void setFreezingTime(float freezingTime) {
        if (freezingTime >= 100.0f) {
            this.setFrozen(true);
            freezingTime = 100.0f;
        }
        else if (freezingTime <= 0.0f) {
            freezingTime = 0.0f;
            this.setFrozen(false);
        }
        this.freezingTime = freezingTime;
    }
    
    public void freeze() {
        this.setFreezingTime(100.0f);
    }
    
    public boolean isFrozen() {
        return this.frozen;
    }
    
    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }
    
    public boolean canBeFrozen() {
        return this.canBeFrozen;
    }
    
    public void setCanBeFrozen(final boolean canBeFrozen) {
        this.canBeFrozen = canBeFrozen;
    }
    
    public boolean isFreezing() {
        return this.canBeFrozen() && this.getFreezingTime() < 100.0f && this.getOutermostContainer() != null && "freezer".equals(this.getOutermostContainer().getType()) && this.getOutermostContainer().isPowered();
    }
    
    public boolean isThawing() {
        return this.canBeFrozen() && this.getFreezingTime() > 0.0f && (this.getOutermostContainer() == null || !"freezer".equals(this.getOutermostContainer().getType()) || !this.getOutermostContainer().isPowered());
    }
    
    public String getReplaceOnRotten() {
        return this.replaceOnRotten;
    }
    
    public void setReplaceOnRotten(final String replaceOnRotten) {
        this.replaceOnRotten = replaceOnRotten;
    }
    
    public void multiplyFoodValues(final float n) {
        this.setBoredomChange(this.getBoredomChange() * n);
        this.setUnhappyChange(this.getUnhappyChange() * n);
        this.setHungChange(this.getHungChange() * n);
        this.setFluReduction((int)(this.getFluReduction() * n));
        this.setThirstChange(this.getThirstChange() * n);
        this.setPainReduction(this.getPainReduction() * n);
        this.setReduceFoodSickness((int)(this.getReduceFoodSickness() * n));
        this.setEndChange(this.getEnduranceChange() * n);
        this.setStressChange(this.getStressChange() * n);
        this.setFatigueChange(this.getFatigueChange() * n);
        this.setCalories(this.getCalories() * n);
        this.setCarbohydrates(this.getCarbohydrates() * n);
        this.setProteins(this.getProteins() * n);
        this.setLipids(this.getLipids() * n);
    }
    
    public float getRottenTime() {
        return this.rottenTime;
    }
    
    public void setRottenTime(final float rottenTime) {
        this.rottenTime = rottenTime;
    }
    
    public float getCompostTime() {
        return this.compostTime;
    }
    
    public void setCompostTime(final float compostTime) {
        this.compostTime = compostTime;
    }
    
    public String getOnEat() {
        return this.onEat;
    }
    
    public void setOnEat(final String onEat) {
        this.onEat = onEat;
    }
    
    public boolean isBadInMicrowave() {
        return this.badInMicrowave;
    }
    
    public void setBadInMicrowave(final boolean badInMicrowave) {
        this.badInMicrowave = badInMicrowave;
    }
    
    private void destroyThisItem() {
        final IsoWorldInventoryObject worldItem = this.getWorldItem();
        if (worldItem != null && worldItem.getSquare() != null) {
            if (GameServer.bServer) {
                GameServer.RemoveItemFromMap(worldItem);
            }
            else {
                worldItem.removeFromWorld();
                worldItem.removeFromSquare();
            }
            this.setWorldItem(null);
        }
        else if (this.container != null) {
            final IsoObject parent = this.container.getParent();
            if (GameServer.bServer) {
                if (!this.isInPlayerInventory()) {
                    GameServer.sendRemoveItemFromContainer(this.container, this);
                }
                this.container.Remove(this);
            }
            else {
                this.container.Remove(this);
            }
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
            LuaManager.updateOverlaySprite(parent);
        }
        if (!GameServer.bServer) {
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
}
