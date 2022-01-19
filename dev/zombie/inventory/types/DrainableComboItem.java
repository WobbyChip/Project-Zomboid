// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.util.StringUtils;
import zombie.scripting.ScriptManager;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemUser;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.RainManager;
import zombie.characters.IsoPlayer;
import zombie.GameTime;
import zombie.iso.IsoGridSquare;
import zombie.network.GameServer;
import zombie.core.math.PZMath;
import zombie.scripting.objects.Item;
import zombie.interfaces.IUpdater;
import zombie.inventory.InventoryItem;

public final class DrainableComboItem extends InventoryItem implements Drainable, IUpdater
{
    protected boolean bUseWhileEquiped;
    protected boolean bUseWhileUnequiped;
    protected int ticksPerEquipUse;
    protected float useDelta;
    protected float delta;
    protected float ticks;
    protected String ReplaceOnDeplete;
    protected String ReplaceOnDepleteFullType;
    private float rainFactor;
    private boolean canConsolidate;
    private float WeightEmpty;
    protected float Heat;
    protected int LastCookMinute;
    public String OnCooked;
    
    public DrainableComboItem(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.bUseWhileEquiped = true;
        this.bUseWhileUnequiped = false;
        this.ticksPerEquipUse = 30;
        this.useDelta = 0.03125f;
        this.delta = 1.0f;
        this.ticks = 0.0f;
        this.ReplaceOnDeplete = null;
        this.ReplaceOnDepleteFullType = null;
        this.rainFactor = 0.0f;
        this.canConsolidate = true;
        this.WeightEmpty = 0.0f;
        this.Heat = 1.0f;
        this.LastCookMinute = 0;
        this.OnCooked = null;
    }
    
    public DrainableComboItem(final String s, final String s2, final String s3, final Item item) {
        super(s, s2, s3, item);
        this.bUseWhileEquiped = true;
        this.bUseWhileUnequiped = false;
        this.ticksPerEquipUse = 30;
        this.useDelta = 0.03125f;
        this.delta = 1.0f;
        this.ticks = 0.0f;
        this.ReplaceOnDeplete = null;
        this.ReplaceOnDepleteFullType = null;
        this.rainFactor = 0.0f;
        this.canConsolidate = true;
        this.WeightEmpty = 0.0f;
        this.Heat = 1.0f;
        this.LastCookMinute = 0;
        this.OnCooked = null;
    }
    
    @Override
    public boolean IsDrainable() {
        return true;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Drainable.ordinal();
    }
    
    @Override
    public boolean CanStack(final InventoryItem inventoryItem) {
        return false;
    }
    
    @Override
    public float getUsedDelta() {
        return this.delta;
    }
    
    public int getDrainableUsesInt() {
        return (int)Math.floor((this.getUsedDelta() + 1.0E-4) / this.getUseDelta());
    }
    
    public float getDrainableUsesFloat() {
        return this.getUsedDelta() / this.getUseDelta();
    }
    
    @Override
    public void render() {
    }
    
    @Override
    public void renderlast() {
    }
    
    @Override
    public void setUsedDelta(final float n) {
        this.delta = PZMath.clamp(n, 0.0f, 1.0f);
        this.updateWeight();
    }
    
    @Override
    public boolean shouldUpdateInWorld() {
        if (!GameServer.bServer && this.Heat != 1.0f) {
            return true;
        }
        if (this.canStoreWater() && this.isWaterSource() && this.getUsedDelta() < 1.0f) {
            final IsoGridSquare square = this.getWorldItem().getSquare();
            return square != null && square.isOutside();
        }
        return false;
    }
    
    @Override
    public void update() {
        if (this.container != null) {
            final float min = Math.min(this.container.getTemprature(), 3.0f);
            if (this.Heat > min) {
                this.Heat -= 0.001f * GameTime.instance.getMultiplier();
                if (this.Heat < min) {
                    this.Heat = min;
                }
            }
            if (this.Heat < min) {
                this.Heat += min / 1000.0f * GameTime.instance.getMultiplier();
                if (this.Heat > min) {
                    this.Heat = min;
                }
            }
            if (this.IsCookable && this.Heat > 1.6f) {
                final int minutes = GameTime.getInstance().getMinutes();
                if (minutes != this.LastCookMinute) {
                    this.LastCookMinute = minutes;
                    float n = 1.0f;
                    if (this.container.getTemprature() <= 1.6f) {
                        n *= 0.05f;
                    }
                    this.CookingTime += n;
                    if (this.CookingTime > 10.0f) {
                        this.setTaintedWater(false);
                    }
                }
            }
        }
        if (this.container == null && this.Heat != 1.0f) {
            final float n2 = 1.0f;
            if (this.Heat > n2) {
                this.Heat -= 0.001f * GameTime.instance.getMultiplier();
                if (this.Heat < n2) {
                    this.Heat = n2;
                }
            }
            if (this.Heat < n2) {
                this.Heat += n2 / 1000.0f * GameTime.instance.getMultiplier();
                if (this.Heat > n2) {
                    this.Heat = n2;
                }
            }
        }
        if (this.bUseWhileEquiped && this.delta > 0.0f) {
            IsoGameCharacter isoGameCharacter = null;
            if (this.container != null && this.container.parent instanceof IsoPlayer) {
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    if (this.container.parent == IsoPlayer.players[i]) {
                        isoGameCharacter = IsoPlayer.players[i];
                    }
                }
            }
            if (isoGameCharacter != null && ((this.canBeActivated() && this.isActivated()) || !this.canBeActivated()) && (isoGameCharacter.isHandItem(this) || isoGameCharacter.isAttachedItem(this))) {
                this.ticks += GameTime.instance.getMultiplier();
                while (this.ticks >= this.ticksPerEquipUse) {
                    this.ticks -= this.ticksPerEquipUse;
                    if (this.delta > 0.0f) {
                        this.Use();
                    }
                }
            }
        }
        if (this.bUseWhileUnequiped && this.delta > 0.0f && ((this.canBeActivated() && this.isActivated()) || !this.canBeActivated())) {
            this.ticks += GameTime.instance.getMultiplier();
            while (this.ticks >= this.ticksPerEquipUse) {
                this.ticks -= this.ticksPerEquipUse;
                if (this.delta > 0.0f) {
                    this.Use();
                }
            }
        }
        if (this.getWorldItem() != null && this.canStoreWater() && this.isWaterSource() && RainManager.isRaining() && this.getRainFactor() > 0.0f) {
            final IsoGridSquare square = this.getWorldItem().getSquare();
            if (square != null && square.isOutside()) {
                this.setUsedDelta(this.getUsedDelta() + 0.001f * RainManager.getRainIntensity() * GameTime.instance.getMultiplier() * this.getRainFactor());
                if (this.getUsedDelta() > 1.0f) {
                    this.setUsedDelta(1.0f);
                }
                this.setTaintedWater(true);
                this.updateWeight();
            }
        }
    }
    
    @Override
    public void Use() {
        if (this.getWorldItem() != null) {
            ItemUser.UseItem(this);
            return;
        }
        this.delta -= this.useDelta;
        if (this.uses > 1) {
            final int uses = this.uses - 1;
            this.uses = 1;
            final InventoryItem createItem = InventoryItemFactory.CreateItem(this.getFullType());
            createItem.setUses(uses);
            this.container.AddItem(createItem);
        }
        if (this.delta <= 1.0E-4f) {
            this.delta = 0.0f;
            if (this.getReplaceOnDeplete() != null) {
                final String replaceOnDepleteFullType = this.getReplaceOnDepleteFullType();
                if (this.container != null) {
                    final InventoryItem addItem = this.container.AddItem(replaceOnDepleteFullType);
                    if (this.container.parent instanceof IsoGameCharacter) {
                        final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)this.container.parent;
                        if (isoGameCharacter.getPrimaryHandItem() == this) {
                            isoGameCharacter.setPrimaryHandItem(addItem);
                        }
                        if (isoGameCharacter.getSecondaryHandItem() == this) {
                            isoGameCharacter.setSecondaryHandItem(addItem);
                        }
                    }
                    addItem.setCondition(this.getCondition());
                    addItem.setFavorite(this.isFavorite());
                    this.container.Remove(this);
                }
            }
            else {
                super.Use();
            }
        }
        this.updateWeight();
    }
    
    public void updateWeight() {
        if (this.getReplaceOnDeplete() != null) {
            if (this.getUsedDelta() >= 1.0f) {
                this.setCustomWeight(true);
                this.setActualWeight(this.getScriptItem().getActualWeight());
                this.setWeight(this.getActualWeight());
                return;
            }
            final Item item = ScriptManager.instance.getItem(this.ReplaceOnDepleteFullType);
            if (item != null) {
                this.setCustomWeight(true);
                this.setActualWeight((this.getScriptItem().getActualWeight() - item.getActualWeight()) * this.getUsedDelta() + item.getActualWeight());
                this.setWeight(this.getActualWeight());
            }
        }
        if (this.getWeightEmpty() != 0.0f) {
            this.setCustomWeight(true);
            this.setActualWeight((this.getScriptItem().getActualWeight() - this.WeightEmpty) * this.getUsedDelta() + this.WeightEmpty);
        }
    }
    
    public float getWeightEmpty() {
        return this.WeightEmpty;
    }
    
    public void setWeightEmpty(final float weightEmpty) {
        this.WeightEmpty = weightEmpty;
    }
    
    public boolean isUseWhileEquiped() {
        return this.bUseWhileEquiped;
    }
    
    public void setUseWhileEquiped(final boolean bUseWhileEquiped) {
        this.bUseWhileEquiped = bUseWhileEquiped;
    }
    
    public boolean isUseWhileUnequiped() {
        return this.bUseWhileUnequiped;
    }
    
    public void setUseWhileUnequiped(final boolean bUseWhileUnequiped) {
        this.bUseWhileUnequiped = bUseWhileUnequiped;
    }
    
    public int getTicksPerEquipUse() {
        return this.ticksPerEquipUse;
    }
    
    public void setTicksPerEquipUse(final int ticksPerEquipUse) {
        this.ticksPerEquipUse = ticksPerEquipUse;
    }
    
    public float getUseDelta() {
        return this.useDelta;
    }
    
    public void setUseDelta(final float useDelta) {
        this.useDelta = useDelta;
    }
    
    public float getDelta() {
        return this.delta;
    }
    
    public void setDelta(final float delta) {
        this.delta = delta;
    }
    
    public float getTicks() {
        return this.ticks;
    }
    
    public void setTicks(final float ticks) {
        this.ticks = ticks;
    }
    
    public void setReplaceOnDeplete(final String replaceOnDeplete) {
        this.ReplaceOnDeplete = replaceOnDeplete;
        this.ReplaceOnDepleteFullType = this.getReplaceOnDepleteFullType();
    }
    
    public String getReplaceOnDeplete() {
        return this.ReplaceOnDeplete;
    }
    
    public String getReplaceOnDepleteFullType() {
        return StringUtils.moduleDotType(this.getModule(), this.ReplaceOnDeplete);
    }
    
    public void setHeat(final float n) {
        this.Heat = PZMath.clamp(n, 0.0f, 3.0f);
    }
    
    public float getHeat() {
        return this.Heat;
    }
    
    @Override
    public float getInvHeat() {
        return (1.0f - this.Heat) / 3.0f;
    }
    
    @Override
    public boolean finishupdate() {
        if (this.canStoreWater() && this.isWaterSource() && this.getWorldItem() != null && this.getWorldItem().getSquare() != null) {
            return this.getUsedDelta() >= 1.0f;
        }
        if (this.isTaintedWater()) {
            return false;
        }
        if (this.container != null) {
            if (this.Heat != this.container.getTemprature() || this.container.isTemperatureChanging()) {
                return false;
            }
            if (this.container.type.equals("campfire") || this.container.type.equals("barbecue")) {
                return false;
            }
        }
        return true;
    }
    
    public int getRemainingUses() {
        return Math.round(this.getUsedDelta() / this.getUseDelta());
    }
    
    public float getRainFactor() {
        return this.rainFactor;
    }
    
    public void setRainFactor(final float rainFactor) {
        this.rainFactor = rainFactor;
    }
    
    public boolean canConsolidate() {
        return this.canConsolidate;
    }
    
    public void setCanConsolidate(final boolean canConsolidate) {
        this.canConsolidate = canConsolidate;
    }
}
