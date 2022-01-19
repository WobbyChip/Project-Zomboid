// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.textures.ColorInfo;
import zombie.core.Rand;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Collection;
import zombie.iso.IsoWorld;
import zombie.characters.WornItems.BodyLocations;
import zombie.Lua.LuaManager;
import java.util.Map;
import zombie.characters.professions.ProfessionFactory;
import zombie.inventory.InventoryItem;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.characters.traits.ObservationFactory;
import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.skills.PerkFactory;
import java.util.HashMap;
import zombie.core.ImmutableColor;
import zombie.core.Color;
import java.util.ArrayList;
import zombie.characters.WornItems.WornItems;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;

public final class SurvivorDesc implements IHumanVisual
{
    public final HumanVisual humanVisual;
    public final WornItems wornItems;
    SurvivorGroup group;
    private static int IDCount;
    public static final ArrayList<Color> TrouserCommonColors;
    public static final ArrayList<ImmutableColor> HairCommonColors;
    private final HashMap<PerkFactory.Perk, Integer> xpBoostMap;
    private KahluaTable metaTable;
    public String Profession;
    protected String forename;
    protected int ID;
    protected IsoGameCharacter Instance;
    private boolean bFemale;
    protected String surname;
    private String InventoryScript;
    protected String torso;
    protected final HashMap<Integer, Integer> MetCount;
    protected float bravery;
    protected float loner;
    protected float aggressiveness;
    protected float compassion;
    protected float temper;
    protected float friendliness;
    private float favourindoors;
    protected float loyalty;
    public final ArrayList<String> extra;
    private final ArrayList<ObservationFactory.Observation> Observations;
    private SurvivorFactory.SurvivorType type;
    public boolean bDead;
    
    @Override
    public HumanVisual getHumanVisual() {
        return this.humanVisual;
    }
    
    @Override
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        this.wornItems.getItemVisuals(itemVisuals);
    }
    
    @Override
    public boolean isFemale() {
        return this.bFemale;
    }
    
    @Override
    public boolean isZombie() {
        return false;
    }
    
    @Override
    public boolean isSkeleton() {
        return false;
    }
    
    public WornItems getWornItems() {
        return this.wornItems;
    }
    
    public void setWornItem(final String s, final InventoryItem inventoryItem) {
        this.wornItems.setItem(s, inventoryItem);
    }
    
    public InventoryItem getWornItem(final String s) {
        return this.wornItems.getItem(s);
    }
    
    public void dressInNamedOutfit(final String s) {
        final ItemVisuals fromItemVisuals = new ItemVisuals();
        this.getHumanVisual().dressInNamedOutfit(s, fromItemVisuals);
        this.getWornItems().setFromItemVisuals(fromItemVisuals);
    }
    
    public SurvivorGroup getGroup() {
        return this.group;
    }
    
    public boolean isLeader() {
        return this.group.getLeader() == this;
    }
    
    public static int getIDCount() {
        return SurvivorDesc.IDCount;
    }
    
    public void setProfessionSkills(final ProfessionFactory.Profession profession) {
        this.getXPBoostMap().clear();
        this.getXPBoostMap().putAll(profession.XPBoostMap);
    }
    
    public HashMap<PerkFactory.Perk, Integer> getXPBoostMap() {
        return this.xpBoostMap;
    }
    
    public KahluaTable getMeta() {
        if (this.metaTable == null) {
            this.metaTable = (KahluaTable)LuaManager.caller.pcall(LuaManager.thread, LuaManager.env.rawget((Object)"createMetaSurvivor"), (Object)this)[1];
        }
        return this.metaTable;
    }
    
    public int getCalculatedToughness() {
        this.metaTable = this.getMeta();
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, ((KahluaTable)LuaManager.env.rawget((Object)"MetaSurvivor")).rawget((Object)"getCalculatedToughness"), (Object)this.metaTable)[1]).intValue();
    }
    
    public static void setIDCount(final int idCount) {
        SurvivorDesc.IDCount = idCount;
    }
    
    public boolean isDead() {
        return this.bDead;
    }
    
    public SurvivorDesc() {
        this.humanVisual = new HumanVisual(this);
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));
        this.group = new SurvivorGroup();
        this.xpBoostMap = new HashMap<PerkFactory.Perk, Integer>();
        this.Profession = "";
        this.forename = "None";
        this.ID = 0;
        this.Instance = null;
        this.bFemale = true;
        this.surname = "None";
        this.InventoryScript = null;
        this.torso = "Base_Torso";
        this.MetCount = new HashMap<Integer, Integer>();
        this.bravery = 1.0f;
        this.loner = 0.0f;
        this.aggressiveness = 1.0f;
        this.compassion = 1.0f;
        this.temper = 0.0f;
        this.friendliness = 0.0f;
        this.favourindoors = 0.0f;
        this.loyalty = 0.0f;
        this.extra = new ArrayList<String>();
        this.Observations = new ArrayList<ObservationFactory.Observation>(0);
        this.type = SurvivorFactory.SurvivorType.Neutral;
        this.ID = SurvivorDesc.IDCount++;
        IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
        this.doStats();
    }
    
    public SurvivorDesc(final boolean b) {
        this.humanVisual = new HumanVisual(this);
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));
        this.group = new SurvivorGroup();
        this.xpBoostMap = new HashMap<PerkFactory.Perk, Integer>();
        this.Profession = "";
        this.forename = "None";
        this.ID = 0;
        this.Instance = null;
        this.bFemale = true;
        this.surname = "None";
        this.InventoryScript = null;
        this.torso = "Base_Torso";
        this.MetCount = new HashMap<Integer, Integer>();
        this.bravery = 1.0f;
        this.loner = 0.0f;
        this.aggressiveness = 1.0f;
        this.compassion = 1.0f;
        this.temper = 0.0f;
        this.friendliness = 0.0f;
        this.favourindoors = 0.0f;
        this.loyalty = 0.0f;
        this.extra = new ArrayList<String>();
        this.Observations = new ArrayList<ObservationFactory.Observation>(0);
        this.type = SurvivorFactory.SurvivorType.Neutral;
        this.ID = SurvivorDesc.IDCount++;
        this.doStats();
    }
    
    public SurvivorDesc(final SurvivorDesc survivorDesc) {
        this.humanVisual = new HumanVisual(this);
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));
        this.group = new SurvivorGroup();
        this.xpBoostMap = new HashMap<PerkFactory.Perk, Integer>();
        this.Profession = "";
        this.forename = "None";
        this.ID = 0;
        this.Instance = null;
        this.bFemale = true;
        this.surname = "None";
        this.InventoryScript = null;
        this.torso = "Base_Torso";
        this.MetCount = new HashMap<Integer, Integer>();
        this.bravery = 1.0f;
        this.loner = 0.0f;
        this.aggressiveness = 1.0f;
        this.compassion = 1.0f;
        this.temper = 0.0f;
        this.friendliness = 0.0f;
        this.favourindoors = 0.0f;
        this.loyalty = 0.0f;
        this.extra = new ArrayList<String>();
        this.Observations = new ArrayList<ObservationFactory.Observation>(0);
        this.type = SurvivorFactory.SurvivorType.Neutral;
        this.aggressiveness = survivorDesc.aggressiveness;
        this.bDead = survivorDesc.bDead;
        this.bFemale = survivorDesc.bFemale;
        this.bravery = survivorDesc.bravery;
        this.compassion = survivorDesc.compassion;
        this.extra.addAll(survivorDesc.extra);
        this.favourindoors = survivorDesc.favourindoors;
        this.forename = survivorDesc.forename;
        this.friendliness = survivorDesc.friendliness;
        this.InventoryScript = survivorDesc.InventoryScript;
        this.loner = survivorDesc.loner;
        this.loyalty = survivorDesc.loyalty;
        this.Profession = survivorDesc.Profession;
        this.surname = survivorDesc.surname;
        this.temper = survivorDesc.temper;
        this.torso = survivorDesc.torso;
        this.type = survivorDesc.type;
    }
    
    public void meet(final SurvivorDesc survivorDesc) {
        if (this.MetCount.containsKey(survivorDesc.ID)) {
            this.MetCount.put(survivorDesc.ID, this.MetCount.get(survivorDesc.ID) + 1);
        }
        else {
            this.MetCount.put(survivorDesc.ID, 1);
        }
        if (survivorDesc.MetCount.containsKey(this.ID)) {
            survivorDesc.MetCount.put(this.ID, survivorDesc.MetCount.get(this.ID) + 1);
        }
        else {
            survivorDesc.MetCount.put(this.ID, 1);
        }
    }
    
    public boolean hasObservation(final String s) {
        for (int i = 0; i < this.Observations.size(); ++i) {
            if (s.equals(this.Observations.get(i).getTraitID())) {
                return true;
            }
        }
        return false;
    }
    
    private void savePerk(final ByteBuffer byteBuffer, final PerkFactory.Perk perk) throws IOException {
        GameWindow.WriteStringUTF(byteBuffer, (perk == null) ? "" : perk.getId());
    }
    
    private PerkFactory.Perk loadPerk(final ByteBuffer byteBuffer, final int n) throws IOException {
        if (n >= 152) {
            final PerkFactory.Perk fromString = PerkFactory.Perks.FromString(GameWindow.ReadStringUTF(byteBuffer));
            return (fromString == PerkFactory.Perks.MAX) ? null : fromString;
        }
        final int int1 = byteBuffer.getInt();
        if (int1 < 0 || int1 >= PerkFactory.Perks.MAX.index()) {
            return null;
        }
        final PerkFactory.Perk fromIndex = PerkFactory.Perks.fromIndex(int1);
        return (fromIndex == PerkFactory.Perks.MAX) ? null : fromIndex;
    }
    
    public void load(final ByteBuffer byteBuffer, final int n, final IsoGameCharacter instance) throws IOException {
        this.ID = byteBuffer.getInt();
        IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
        this.forename = GameWindow.ReadString(byteBuffer);
        this.surname = GameWindow.ReadString(byteBuffer);
        this.torso = GameWindow.ReadString(byteBuffer);
        this.bFemale = (byteBuffer.getInt() == 1);
        this.Profession = GameWindow.ReadString(byteBuffer);
        this.doStats();
        if (SurvivorDesc.IDCount < this.ID) {
            SurvivorDesc.IDCount = this.ID;
        }
        this.extra.clear();
        if (byteBuffer.getInt() == 1) {
            for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
                this.extra.add(GameWindow.ReadString(byteBuffer));
            }
        }
        for (int int2 = byteBuffer.getInt(), j = 0; j < int2; ++j) {
            final PerkFactory.Perk loadPerk = this.loadPerk(byteBuffer, n);
            final int int3 = byteBuffer.getInt();
            if (loadPerk != null) {
                this.getXPBoostMap().put(loadPerk, int3);
            }
        }
        this.Instance = instance;
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putInt(this.ID);
        GameWindow.WriteString(byteBuffer, this.forename);
        GameWindow.WriteString(byteBuffer, this.surname);
        GameWindow.WriteString(byteBuffer, this.torso);
        byteBuffer.putInt(this.bFemale ? 1 : 0);
        GameWindow.WriteString(byteBuffer, this.Profession);
        if (!this.extra.isEmpty()) {
            byteBuffer.putInt(1);
            byteBuffer.putInt(this.extra.size());
            for (int i = 0; i < this.extra.size(); ++i) {
                GameWindow.WriteString(byteBuffer, this.extra.get(i));
            }
        }
        else {
            byteBuffer.putInt(0);
        }
        byteBuffer.putInt(this.getXPBoostMap().size());
        for (final Map.Entry<PerkFactory.Perk, Integer> entry : this.getXPBoostMap().entrySet()) {
            this.savePerk(byteBuffer, entry.getKey());
            byteBuffer.putInt(entry.getValue());
        }
    }
    
    public void loadCompact(final ByteBuffer byteBuffer) {
        this.ID = -1;
        this.torso = GameWindow.ReadString(byteBuffer);
        this.bFemale = (byteBuffer.get() == 1);
        this.extra.clear();
        if (byteBuffer.get() == 1) {
            for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
                this.extra.add(GameWindow.ReadString(byteBuffer));
            }
        }
    }
    
    public void saveCompact(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        GameWindow.WriteString(byteBuffer, this.torso);
        byteBuffer.put((byte)(this.bFemale ? 1 : 0));
        if (!this.extra.isEmpty()) {
            byteBuffer.put((byte)1);
            byteBuffer.put((byte)this.extra.size());
            final Iterator<String> iterator = this.extra.iterator();
            while (iterator.hasNext()) {
                GameWindow.WriteString(byteBuffer, iterator.next());
            }
        }
        else {
            byteBuffer.put((byte)0);
        }
    }
    
    public void addObservation(final String s) {
        final ObservationFactory.Observation observation = ObservationFactory.getObservation(s);
        if (observation == null) {
            return;
        }
        this.Observations.add(observation);
    }
    
    private void doStats() {
        this.bravery = ((Rand.Next(2) == 0) ? 10.0f : 0.0f);
        this.aggressiveness = ((Rand.Next(2) == 0) ? 10.0f : 0.0f);
        this.compassion = 10.0f - this.aggressiveness;
        this.loner = ((Rand.Next(2) == 0) ? 10.0f : 0.0f);
        this.temper = ((Rand.Next(2) == 0) ? 10.0f : 0.0f);
        this.friendliness = 10.0f - this.loner;
        this.favourindoors = ((Rand.Next(2) == 0) ? 10.0f : 0.0f);
        this.loyalty = ((Rand.Next(2) == 0) ? 10.0f : 0.0f);
    }
    
    public int getMetCount(final SurvivorDesc survivorDesc) {
        if (this.MetCount.containsKey(survivorDesc.ID)) {
            return this.MetCount.get(survivorDesc.ID);
        }
        return 0;
    }
    
    public String getForename() {
        return this.forename;
    }
    
    public void setForename(final String forename) {
        this.forename = forename;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public void setID(final int id) {
        this.ID = id;
    }
    
    public IsoGameCharacter getInstance() {
        return this.Instance;
    }
    
    public void setInstance(final IsoGameCharacter instance) {
        this.Instance = instance;
    }
    
    public String getSurname() {
        return this.surname;
    }
    
    public void setSurname(final String surname) {
        this.surname = surname;
    }
    
    public String getInventoryScript() {
        return this.InventoryScript;
    }
    
    public void setInventoryScript(final String inventoryScript) {
        this.InventoryScript = inventoryScript;
    }
    
    public String getTorso() {
        return this.torso;
    }
    
    public void setTorso(final String torso) {
        this.torso = torso;
    }
    
    public HashMap<Integer, Integer> getMetCount() {
        return this.MetCount;
    }
    
    public float getBravery() {
        return this.bravery;
    }
    
    public void setBravery(final float bravery) {
        this.bravery = bravery;
    }
    
    public float getLoner() {
        return this.loner;
    }
    
    public void setLoner(final float loner) {
        this.loner = loner;
    }
    
    public float getAggressiveness() {
        return this.aggressiveness;
    }
    
    public void setAggressiveness(final float aggressiveness) {
        this.aggressiveness = aggressiveness;
    }
    
    public float getCompassion() {
        return this.compassion;
    }
    
    public void setCompassion(final float compassion) {
        this.compassion = compassion;
    }
    
    public float getTemper() {
        return this.temper;
    }
    
    public void setTemper(final float temper) {
        this.temper = temper;
    }
    
    public float getFriendliness() {
        return this.friendliness;
    }
    
    public void setFriendliness(final float friendliness) {
        this.friendliness = friendliness;
    }
    
    public float getFavourindoors() {
        return this.favourindoors;
    }
    
    public void setFavourindoors(final float favourindoors) {
        this.favourindoors = favourindoors;
    }
    
    public float getLoyalty() {
        return this.loyalty;
    }
    
    public void setLoyalty(final float loyalty) {
        this.loyalty = loyalty;
    }
    
    public String getProfession() {
        return this.Profession;
    }
    
    public void setProfession(final String profession) {
        this.Profession = profession;
    }
    
    public boolean isAggressive() {
        final Iterator<ObservationFactory.Observation> iterator = this.Observations.iterator();
        while (iterator.hasNext()) {
            if ("Aggressive".equals(iterator.next().getTraitID())) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<ObservationFactory.Observation> getObservations() {
        return this.Observations;
    }
    
    public boolean isFriendly() {
        final Iterator<ObservationFactory.Observation> iterator = this.Observations.iterator();
        while (iterator.hasNext()) {
            if ("Friendly".equals(iterator.next().getTraitID())) {
                return true;
            }
        }
        return false;
    }
    
    public SurvivorFactory.SurvivorType getType() {
        return this.type;
    }
    
    public void setType(final SurvivorFactory.SurvivorType type) {
        this.type = type;
    }
    
    public void setFemale(final boolean bFemale) {
        this.bFemale = bFemale;
    }
    
    public ArrayList<String> getExtras() {
        return this.extra;
    }
    
    public ArrayList<ImmutableColor> getCommonHairColor() {
        return SurvivorDesc.HairCommonColors;
    }
    
    public static void addTrouserColor(final ColorInfo colorInfo) {
        SurvivorDesc.TrouserCommonColors.add(colorInfo.toColor());
    }
    
    public static void addHairColor(final ColorInfo colorInfo) {
        SurvivorDesc.HairCommonColors.add(colorInfo.toImmutableColor());
    }
    
    public static Color getRandomSkinColor() {
        if (OutfitRNG.Next(3) == 0) {
            return new Color(OutfitRNG.Next(0.5f, 0.6f), OutfitRNG.Next(0.3f, 0.4f), OutfitRNG.Next(0.15f, 0.23f));
        }
        return new Color(OutfitRNG.Next(0.9f, 1.0f), OutfitRNG.Next(0.75f, 0.88f), OutfitRNG.Next(0.45f, 0.58f));
    }
    
    static {
        SurvivorDesc.IDCount = 0;
        TrouserCommonColors = new ArrayList<Color>();
        HairCommonColors = new ArrayList<ImmutableColor>();
    }
}
