// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoCell;
import zombie.core.Rand;
import java.util.ArrayList;

public final class SurvivorFactory
{
    public static final ArrayList<String> FemaleForenames;
    public static final ArrayList<String> MaleForenames;
    public static final ArrayList<String> Surnames;
    
    public static void Reset() {
        SurvivorFactory.FemaleForenames.clear();
        SurvivorFactory.MaleForenames.clear();
        SurvivorFactory.Surnames.clear();
        SurvivorDesc.HairCommonColors.clear();
        SurvivorDesc.TrouserCommonColors.clear();
    }
    
    public static SurvivorDesc[] CreateFamily(final int n) {
        final SurvivorDesc[] array = new SurvivorDesc[n];
        for (int i = 0; i < n; ++i) {
            array[i] = CreateSurvivor();
            if (i > 0) {
                array[i].surname = array[0].surname;
            }
        }
        return array;
    }
    
    public static SurvivorDesc CreateSurvivor() {
        switch (Rand.Next(3)) {
            case 0: {
                return CreateSurvivor(SurvivorType.Friendly);
            }
            case 1: {
                return CreateSurvivor(SurvivorType.Neutral);
            }
            case 2: {
                return CreateSurvivor(SurvivorType.Aggressive);
            }
            default: {
                return null;
            }
        }
    }
    
    public static SurvivorDesc CreateSurvivor(final SurvivorType type, final boolean female) {
        final SurvivorDesc torso = new SurvivorDesc();
        torso.setType(type);
        IsoGameCharacter.getSurvivorMap().put(torso.ID, torso);
        torso.setFemale(female);
        randomName(torso);
        if (torso.isFemale()) {
            setTorso(torso);
        }
        else {
            setTorso(torso);
        }
        return torso;
    }
    
    public static void setTorso(final SurvivorDesc survivorDesc) {
        if (survivorDesc.isFemale()) {
            survivorDesc.torso = "Kate";
        }
        else {
            survivorDesc.torso = "Male";
        }
    }
    
    public static SurvivorDesc CreateSurvivor(final SurvivorType survivorType) {
        return CreateSurvivor(survivorType, Rand.Next(2) == 0);
    }
    
    public static SurvivorDesc[] CreateSurvivorGroup(final int n) {
        final SurvivorDesc[] array = new SurvivorDesc[n];
        for (int i = 0; i < n; ++i) {
            array[i] = CreateSurvivor();
        }
        return array;
    }
    
    public static IsoSurvivor InstansiateInCell(final SurvivorDesc survivorDesc, final IsoCell isoCell, final int n, final int n2, final int n3) {
        survivorDesc.Instance = new IsoSurvivor(survivorDesc, isoCell, n, n2, n3);
        return (IsoSurvivor)survivorDesc.Instance;
    }
    
    public static void randomName(final SurvivorDesc survivorDesc) {
        if (survivorDesc.isFemale()) {
            survivorDesc.forename = SurvivorFactory.FemaleForenames.get(Rand.Next(SurvivorFactory.FemaleForenames.size()));
        }
        else {
            survivorDesc.forename = SurvivorFactory.MaleForenames.get(Rand.Next(SurvivorFactory.MaleForenames.size()));
        }
        survivorDesc.surname = SurvivorFactory.Surnames.get(Rand.Next(SurvivorFactory.Surnames.size()));
    }
    
    public static void addSurname(final String e) {
        SurvivorFactory.Surnames.add(e);
    }
    
    public static void addFemaleForename(final String e) {
        SurvivorFactory.FemaleForenames.add(e);
    }
    
    public static void addMaleForename(final String e) {
        SurvivorFactory.MaleForenames.add(e);
    }
    
    static {
        FemaleForenames = new ArrayList<String>();
        MaleForenames = new ArrayList<String>();
        Surnames = new ArrayList<String>();
    }
    
    public enum SurvivorType
    {
        Friendly, 
        Neutral, 
        Aggressive;
        
        private static /* synthetic */ SurvivorType[] $values() {
            return new SurvivorType[] { SurvivorType.Friendly, SurvivorType.Neutral, SurvivorType.Aggressive };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
