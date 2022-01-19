// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.inventory.types.DrainableComboItem;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.characters.IsoGameCharacter;
import java.util.List;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Fixing;
import java.util.ArrayList;

public final class FixingManager
{
    public static ArrayList<Fixing> getFixes(final InventoryItem inventoryItem) {
        final ArrayList<Fixing> list = new ArrayList<Fixing>();
        final List<Fixing> allFixing = ScriptManager.instance.getAllFixing(new ArrayList<Fixing>());
        for (int i = 0; i < allFixing.size(); ++i) {
            final Fixing e = allFixing.get(i);
            if (e.getRequiredItem().contains(inventoryItem.getType())) {
                list.add(e);
            }
        }
        return list;
    }
    
    public static InventoryItem fixItem(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter, final Fixing fixing, final Fixing.Fixer fixer) {
        if (Rand.Next(100) >= getChanceOfFail(inventoryItem, isoGameCharacter, fixing, fixer)) {
            int n = (int)Math.round(new Double((inventoryItem.getConditionMax() - inventoryItem.getCondition()) * (getCondRepaired(inventoryItem, isoGameCharacter, fixing, fixer) / 100.0)));
            if (n == 0) {
                n = 1;
            }
            inventoryItem.setCondition(inventoryItem.getCondition() + n);
            inventoryItem.setHaveBeenRepaired(inventoryItem.getHaveBeenRepaired() + 1);
        }
        else if (inventoryItem.getCondition() > 0 && Rand.Next(5) == 0) {
            inventoryItem.setCondition(inventoryItem.getCondition() - 1);
            isoGameCharacter.getEmitter().playSound("FixingItemFailed");
        }
        useFixer(isoGameCharacter, fixer, inventoryItem);
        if (fixing.getGlobalItem() != null) {
            useFixer(isoGameCharacter, fixing.getGlobalItem(), inventoryItem);
        }
        addXp(isoGameCharacter, fixer);
        return inventoryItem;
    }
    
    private static void addXp(final IsoGameCharacter isoGameCharacter, final Fixing.Fixer fixer) {
        if (fixer.getFixerSkills() == null) {
            return;
        }
        for (int i = 0; i < fixer.getFixerSkills().size(); ++i) {
            isoGameCharacter.getXp().AddXP(PerkFactory.Perks.FromString(fixer.getFixerSkills().get(i).getSkillName()), (float)Rand.Next(3, 6));
        }
    }
    
    public static void useFixer(final IsoGameCharacter isoGameCharacter, final Fixing.Fixer fixer, final InventoryItem inventoryItem) {
        int numberOfUse = fixer.getNumberOfUse();
        for (int i = 0; i < isoGameCharacter.getInventory().getItems().size(); ++i) {
            if (inventoryItem != isoGameCharacter.getInventory().getItems().get(i)) {
                final InventoryItem o = isoGameCharacter.getInventory().getItems().get(i);
                if (o != null && o.getType().equals(fixer.getFixerName())) {
                    if (o instanceof DrainableComboItem) {
                        if ("DuctTape".equals(o.getType()) || "Scotchtape".equals(o.getType())) {
                            isoGameCharacter.getEmitter().playSound("FixWithTape");
                        }
                        for (int min = Math.min(((DrainableComboItem)o).getDrainableUsesInt(), numberOfUse), j = 0; j < min; ++j) {
                            o.Use();
                            --numberOfUse;
                            if (!isoGameCharacter.getInventory().getItems().contains(o)) {
                                --i;
                                break;
                            }
                        }
                    }
                    else {
                        isoGameCharacter.getInventory().Remove(o);
                        --i;
                        --numberOfUse;
                    }
                }
                if (numberOfUse == 0) {
                    break;
                }
            }
        }
    }
    
    public static double getChanceOfFail(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter, final Fixing fixing, final Fixing.Fixer fixer) {
        double n = 3.0;
        if (fixer.getFixerSkills() != null) {
            for (int i = 0; i < fixer.getFixerSkills().size(); ++i) {
                if (isoGameCharacter.getPerkLevel(PerkFactory.Perks.FromString(fixer.getFixerSkills().get(i).getSkillName())) < fixer.getFixerSkills().get(i).getSkillLevel()) {
                    n += (fixer.getFixerSkills().get(i).getSkillLevel() - isoGameCharacter.getPerkLevel(PerkFactory.Perks.FromString(fixer.getFixerSkills().get(i).getSkillName()))) * 30;
                }
                else {
                    n -= (isoGameCharacter.getPerkLevel(PerkFactory.Perks.FromString(fixer.getFixerSkills().get(i).getSkillName())) - fixer.getFixerSkills().get(i).getSkillLevel()) * 5;
                }
            }
        }
        double n2 = n + inventoryItem.getHaveBeenRepaired() * 2;
        if (isoGameCharacter.Traits.Lucky.isSet()) {
            n2 -= 5.0;
        }
        if (isoGameCharacter.Traits.Unlucky.isSet()) {
            n2 += 5.0;
        }
        if (n2 > 100.0) {
            n2 = 100.0;
        }
        if (n2 < 0.0) {
            n2 = 0.0;
        }
        return n2;
    }
    
    public static double getCondRepaired(final InventoryItem inventoryItem, final IsoGameCharacter isoGameCharacter, final Fixing fixing, final Fixing.Fixer o) {
        double n = 0.0;
        switch (fixing.getFixers().indexOf(o)) {
            case 0: {
                n = 50.0 * (1.0 / inventoryItem.getHaveBeenRepaired());
                break;
            }
            case 1: {
                n = 20.0 * (1.0 / inventoryItem.getHaveBeenRepaired());
                break;
            }
            default: {
                n = 10.0 * (1.0 / inventoryItem.getHaveBeenRepaired());
                break;
            }
        }
        if (o.getFixerSkills() != null) {
            for (int i = 0; i < o.getFixerSkills().size(); ++i) {
                final Fixing.FixerSkill fixerSkill = o.getFixerSkills().get(i);
                final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.FromString(fixerSkill.getSkillName()));
                if (perkLevel > fixerSkill.getSkillLevel()) {
                    n += Math.min((perkLevel - fixerSkill.getSkillLevel()) * 5, 25);
                }
                else {
                    n -= (fixerSkill.getSkillLevel() - perkLevel) * 15;
                }
            }
        }
        return Math.min(100.0, Math.max(0.0, n * fixing.getConditionModifier()));
    }
}
