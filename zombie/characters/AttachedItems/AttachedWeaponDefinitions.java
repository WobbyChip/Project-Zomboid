// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaTable;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.util.StringUtils;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.iso.IsoWorld;
import zombie.scripting.objects.Item;
import zombie.inventory.InventoryItem;
import zombie.characterTextures.BloodBodyPartType;
import zombie.scripting.ScriptManager;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.InventoryItemFactory;
import java.util.List;
import zombie.core.skinnedmodel.population.Outfit;
import java.util.Collection;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.Core;
import zombie.characters.IsoZombie;
import java.util.ArrayList;

public final class AttachedWeaponDefinitions
{
    public static final AttachedWeaponDefinitions instance;
    public boolean m_dirty;
    public int m_chanceOfAttachedWeapon;
    public final ArrayList<AttachedWeaponDefinition> m_definitions;
    public final ArrayList<AttachedWeaponCustomOutfit> m_outfitDefinitions;
    
    public AttachedWeaponDefinitions() {
        this.m_dirty = true;
        this.m_definitions = new ArrayList<AttachedWeaponDefinition>();
        this.m_outfitDefinitions = new ArrayList<AttachedWeaponCustomOutfit>();
    }
    
    public void checkDirty() {
        if (this.m_dirty) {
            this.m_dirty = false;
            this.init();
        }
    }
    
    public void addRandomAttachedWeapon(final IsoZombie isoZombie) {
        if ("Tutorial".equals(Core.getInstance().getGameMode())) {
            return;
        }
        this.checkDirty();
        if (this.m_definitions.isEmpty()) {
            return;
        }
        final ArrayList<AttachedWeaponDefinition> definitions = L_addRandomAttachedWeapon.definitions;
        definitions.clear();
        int i = 1;
        AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = null;
        final Outfit outfit = isoZombie.getHumanVisual().getOutfit();
        if (outfit != null) {
            for (int j = 0; j < this.m_outfitDefinitions.size(); ++j) {
                attachedWeaponCustomOutfit = this.m_outfitDefinitions.get(j);
                if (attachedWeaponCustomOutfit.outfit.equals(outfit.m_Name) && OutfitRNG.Next(100) < attachedWeaponCustomOutfit.chance) {
                    definitions.addAll(attachedWeaponCustomOutfit.weapons);
                    i = ((attachedWeaponCustomOutfit.maxitem > -1) ? attachedWeaponCustomOutfit.maxitem : 1);
                    break;
                }
                attachedWeaponCustomOutfit = null;
            }
        }
        if (definitions.isEmpty()) {
            if (OutfitRNG.Next(100) > this.m_chanceOfAttachedWeapon) {
                return;
            }
            definitions.addAll(this.m_definitions);
        }
        while (i > 0) {
            final AttachedWeaponDefinition pickRandomInList = this.pickRandomInList(definitions, isoZombie);
            if (pickRandomInList == null) {
                return;
            }
            definitions.remove(pickRandomInList);
            --i;
            this.addAttachedWeapon(pickRandomInList, isoZombie);
            if (attachedWeaponCustomOutfit != null && OutfitRNG.Next(100) >= attachedWeaponCustomOutfit.chance) {
                return;
            }
        }
    }
    
    private void addAttachedWeapon(final AttachedWeaponDefinition attachedWeaponDefinition, final IsoZombie isoZombie) {
        final InventoryItem createItem = InventoryItemFactory.CreateItem(OutfitRNG.pickRandom(attachedWeaponDefinition.weapons));
        if (createItem == null) {
            return;
        }
        if (createItem instanceof HandWeapon) {
            ((HandWeapon)createItem).randomizeBullets();
        }
        createItem.setCondition(OutfitRNG.Next(Math.max(2, createItem.getConditionMax() - 5), createItem.getConditionMax()));
        isoZombie.setAttachedItem(OutfitRNG.pickRandom(attachedWeaponDefinition.weaponLocation), createItem);
        if (attachedWeaponDefinition.ensureItem != null && !this.outfitHasItem(isoZombie, attachedWeaponDefinition.ensureItem)) {
            final Item findItem = ScriptManager.instance.FindItem(attachedWeaponDefinition.ensureItem);
            if (findItem != null && findItem.getClothingItemAsset() != null) {
                isoZombie.getHumanVisual().addClothingItem(isoZombie.getItemVisuals(), findItem);
            }
            else {
                isoZombie.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem(attachedWeaponDefinition.ensureItem));
            }
        }
        if (!attachedWeaponDefinition.bloodLocations.isEmpty()) {
            for (int i = 0; i < attachedWeaponDefinition.bloodLocations.size(); ++i) {
                final BloodBodyPartType bloodBodyPartType = attachedWeaponDefinition.bloodLocations.get(i);
                isoZombie.addBlood(bloodBodyPartType, true, true, true);
                isoZombie.addBlood(bloodBodyPartType, true, true, true);
                isoZombie.addBlood(bloodBodyPartType, true, true, true);
                if (attachedWeaponDefinition.addHoles) {
                    isoZombie.addHole(bloodBodyPartType);
                    isoZombie.addHole(bloodBodyPartType);
                    isoZombie.addHole(bloodBodyPartType);
                    isoZombie.addHole(bloodBodyPartType);
                }
            }
        }
    }
    
    private AttachedWeaponDefinition pickRandomInList(final ArrayList<AttachedWeaponDefinition> list, final IsoZombie isoZombie) {
        AttachedWeaponDefinition attachedWeaponDefinition = null;
        int n = 0;
        final ArrayList<AttachedWeaponDefinition> possibilities = L_addRandomAttachedWeapon.possibilities;
        possibilities.clear();
        for (int i = 0; i < list.size(); ++i) {
            final AttachedWeaponDefinition e = list.get(i);
            if (e.daySurvived > 0) {
                if (IsoWorld.instance.getWorldAgeDays() > e.daySurvived) {
                    n += e.chance;
                    possibilities.add(e);
                }
            }
            else if (!e.outfit.isEmpty()) {
                if (isoZombie.getHumanVisual().getOutfit() != null) {
                    if (e.outfit.contains(isoZombie.getHumanVisual().getOutfit().m_Name)) {
                        n += e.chance;
                        possibilities.add(e);
                    }
                }
            }
            else {
                n += e.chance;
                possibilities.add(e);
            }
        }
        final int next = OutfitRNG.Next(n);
        int n2 = 0;
        for (int j = 0; j < possibilities.size(); ++j) {
            final AttachedWeaponDefinition attachedWeaponDefinition2 = possibilities.get(j);
            n2 += attachedWeaponDefinition2.chance;
            if (next < n2) {
                attachedWeaponDefinition = attachedWeaponDefinition2;
                break;
            }
        }
        return attachedWeaponDefinition;
    }
    
    public boolean outfitHasItem(final IsoZombie isoZombie, final String s) {
        assert s.contains(".");
        final ItemVisuals itemVisuals = isoZombie.getItemVisuals();
        for (int i = 0; i < itemVisuals.size(); ++i) {
            final ItemVisual itemVisual = itemVisuals.get(i);
            if (StringUtils.equals(itemVisual.getItemType(), s)) {
                return true;
            }
            if ("Base.HolsterSimple".equals(s) && StringUtils.equals(itemVisual.getItemType(), "Base.HolsterDouble")) {
                return true;
            }
            if ("Base.HolsterDouble".equals(s) && StringUtils.equals(itemVisual.getItemType(), "Base.HolsterSimple")) {
                return true;
            }
        }
        return false;
    }
    
    private void init() {
        this.m_definitions.clear();
        this.m_outfitDefinitions.clear();
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget((Object)"AttachedWeaponDefinitions");
        if (kahluaTableImpl == null) {
            return;
        }
        this.m_chanceOfAttachedWeapon = kahluaTableImpl.rawgetInt((Object)"chanceOfAttachedWeapon");
        for (final Map.Entry<K, KahluaTableImpl> entry : kahluaTableImpl.delegate.entrySet()) {
            if (entry.getValue() instanceof KahluaTableImpl) {
                final KahluaTableImpl kahluaTableImpl2 = entry.getValue();
                if ("attachedWeaponCustomOutfit".equals(entry.getKey())) {
                    for (final Map.Entry<String, V> entry2 : entry.getValue().delegate.entrySet()) {
                        final AttachedWeaponCustomOutfit initOutfit = this.initOutfit(entry2.getKey(), (KahluaTableImpl)entry2.getValue());
                        if (initOutfit != null) {
                            this.m_outfitDefinitions.add(initOutfit);
                        }
                    }
                }
                else {
                    final AttachedWeaponDefinition init = this.init((String)entry.getKey(), kahluaTableImpl2);
                    if (init == null) {
                        continue;
                    }
                    this.m_definitions.add(init);
                }
            }
        }
        Collections.sort(this.m_definitions, (attachedWeaponDefinition, attachedWeaponDefinition2) -> attachedWeaponDefinition.id.compareTo(attachedWeaponDefinition2.id));
    }
    
    private AttachedWeaponCustomOutfit initOutfit(final String outfit, final KahluaTableImpl kahluaTableImpl) {
        final AttachedWeaponCustomOutfit attachedWeaponCustomOutfit = new AttachedWeaponCustomOutfit();
        attachedWeaponCustomOutfit.outfit = outfit;
        attachedWeaponCustomOutfit.chance = kahluaTableImpl.rawgetInt((Object)"chance");
        attachedWeaponCustomOutfit.maxitem = kahluaTableImpl.rawgetInt((Object)"maxitem");
        final Iterator<Map.Entry<K, KahluaTableImpl>> iterator = ((KahluaTableImpl)kahluaTableImpl.rawget((Object)"weapons")).delegate.entrySet().iterator();
        while (iterator.hasNext()) {
            final KahluaTableImpl kahluaTableImpl2 = iterator.next().getValue();
            final AttachedWeaponDefinition init = this.init(kahluaTableImpl2.rawgetStr((Object)"id"), kahluaTableImpl2);
            if (init != null) {
                attachedWeaponCustomOutfit.weapons.add(init);
            }
        }
        return attachedWeaponCustomOutfit;
    }
    
    private AttachedWeaponDefinition init(final String id, final KahluaTableImpl kahluaTableImpl) {
        final AttachedWeaponDefinition attachedWeaponDefinition = new AttachedWeaponDefinition();
        attachedWeaponDefinition.id = id;
        attachedWeaponDefinition.chance = kahluaTableImpl.rawgetInt((Object)"chance");
        this.tableToArrayList((KahluaTable)kahluaTableImpl, "outfit", attachedWeaponDefinition.outfit);
        this.tableToArrayList((KahluaTable)kahluaTableImpl, "weaponLocation", attachedWeaponDefinition.weaponLocation);
        final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.rawget((Object)"bloodLocations");
        if (kahluaTableImpl2 != null) {
            final KahluaTableIterator iterator = kahluaTableImpl2.iterator();
            while (iterator.advance()) {
                final BloodBodyPartType fromString = BloodBodyPartType.FromString(iterator.getValue().toString());
                if (fromString != BloodBodyPartType.MAX) {
                    attachedWeaponDefinition.bloodLocations.add(fromString);
                }
            }
        }
        attachedWeaponDefinition.addHoles = kahluaTableImpl.rawgetBool((Object)"addHoles");
        attachedWeaponDefinition.daySurvived = kahluaTableImpl.rawgetInt((Object)"daySurvived");
        attachedWeaponDefinition.ensureItem = kahluaTableImpl.rawgetStr((Object)"ensureItem");
        this.tableToArrayList((KahluaTable)kahluaTableImpl, "weapons", attachedWeaponDefinition.weapons);
        Collections.sort(attachedWeaponDefinition.weaponLocation);
        Collections.sort(attachedWeaponDefinition.bloodLocations);
        Collections.sort(attachedWeaponDefinition.weapons);
        return attachedWeaponDefinition;
    }
    
    private void tableToArrayList(final KahluaTable kahluaTable, final String s, final ArrayList<String> list) {
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTable.rawget((Object)s);
        if (kahluaTableImpl == null) {
            return;
        }
        for (int i = 1; i <= kahluaTableImpl.len(); ++i) {
            final Object rawget = kahluaTableImpl.rawget(i);
            if (rawget != null) {
                list.add(rawget.toString());
            }
        }
    }
    
    static {
        instance = new AttachedWeaponDefinitions();
    }
    
    private static final class L_addRandomAttachedWeapon
    {
        static final ArrayList<AttachedWeaponDefinition> possibilities;
        static final ArrayList<AttachedWeaponDefinition> definitions;
        
        static {
            possibilities = new ArrayList<AttachedWeaponDefinition>();
            definitions = new ArrayList<AttachedWeaponDefinition>();
        }
    }
}
