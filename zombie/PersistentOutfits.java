// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.scripting.objects.Item;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import zombie.util.Type;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import java.io.IOException;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.io.File;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.SliceY;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import zombie.core.Core;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.network.GameServer;
import zombie.characters.ZombiesZoneDefinition;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.iso.IsoWorld;
import java.util.Iterator;
import java.util.Collection;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.Rand;
import zombie.network.GameClient;
import java.util.Comparator;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import java.util.TreeMap;
import java.util.ArrayList;

public class PersistentOutfits
{
    public static final PersistentOutfits instance;
    public static final int INVALID_ID = 0;
    public static final int FEMALE_BIT = Integer.MIN_VALUE;
    public static final int NO_HAT_BIT = 32768;
    private static final int FILE_VERSION_1 = 1;
    private static final int FILE_VERSION_LATEST = 1;
    private static final byte[] FILE_MAGIC;
    private static final int NUM_SEEDS = 500;
    private final long[] m_seeds;
    private final ArrayList<String> m_outfitNames;
    private final DataList m_all;
    private final DataList m_female;
    private final DataList m_male;
    private final TreeMap<String, Data> m_outfitToData;
    private final TreeMap<String, Data> m_outfitToFemale;
    private final TreeMap<String, Data> m_outfitToMale;
    private static final ItemVisuals tempItemVisuals;
    
    public PersistentOutfits() {
        this.m_seeds = new long[500];
        this.m_outfitNames = new ArrayList<String>();
        this.m_all = new DataList();
        this.m_female = new DataList();
        this.m_male = new DataList();
        this.m_outfitToData = new TreeMap<String, Data>(String.CASE_INSENSITIVE_ORDER);
        this.m_outfitToFemale = new TreeMap<String, Data>(String.CASE_INSENSITIVE_ORDER);
        this.m_outfitToMale = new TreeMap<String, Data>(String.CASE_INSENSITIVE_ORDER);
    }
    
    public void init() {
        this.m_all.clear();
        this.m_female.clear();
        this.m_male.clear();
        this.m_outfitToData.clear();
        this.m_outfitToFemale.clear();
        this.m_outfitToMale.clear();
        this.m_outfitNames.clear();
        if (!GameClient.bClient) {
            for (int i = 0; i < 500; ++i) {
                this.m_seeds[i] = Rand.Next(Integer.MAX_VALUE);
            }
        }
        this.initOutfitList(OutfitManager.instance.m_FemaleOutfits, true);
        this.initOutfitList(OutfitManager.instance.m_MaleOutfits, false);
        this.registerCustomOutfits();
        if (GameClient.bClient) {
            return;
        }
        this.load();
        this.save();
    }
    
    private void initOutfitList(final ArrayList<Outfit> c, final boolean b) {
        final ArrayList<Outfit> list = new ArrayList<Outfit>(c);
        list.sort((outfit, outfit2) -> outfit.m_Name.compareTo(outfit2.m_Name));
        final Iterator<Outfit> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.initOutfit(iterator.next().m_Name, b, true, PersistentOutfits::ApplyOutfit);
        }
    }
    
    private void initOutfit(final String s, final boolean b, final boolean useSeed, final IOutfitter outfitter) {
        final TreeMap<String, Data> treeMap = b ? this.m_outfitToFemale : this.m_outfitToMale;
        Data e = this.m_outfitToData.get(s);
        if (e == null) {
            e = new Data();
            e.m_index = (short)this.m_all.size();
            e.m_outfitName = s;
            e.m_useSeed = useSeed;
            e.m_outfitter = outfitter;
            this.m_outfitNames.add(s);
            this.m_outfitToData.put(s, e);
            this.m_all.add(e);
        }
        (b ? this.m_female : this.m_male).add(e);
        treeMap.put(s, e);
    }
    
    private void registerCustomOutfits() {
        final ArrayList<RandomizedVehicleStoryBase> randomizedVehicleStoryList = IsoWorld.instance.getRandomizedVehicleStoryList();
        for (int i = 0; i < randomizedVehicleStoryList.size(); ++i) {
            randomizedVehicleStoryList.get(i).registerCustomOutfits();
        }
        ZombiesZoneDefinition.registerCustomOutfits();
        if (GameServer.bServer || GameClient.bClient) {
            this.registerOutfitter("ReanimatedPlayer", false, SharedDescriptors::ApplyReanimatedPlayerOutfit);
        }
    }
    
    public ArrayList<String> getOutfitNames() {
        return this.m_outfitNames;
    }
    
    public int pickRandomFemale() {
        if (this.m_female.isEmpty()) {
            return 0;
        }
        return this.pickOutfitFemale(PZArrayUtil.pickRandom((List<Data>)this.m_female).m_outfitName);
    }
    
    public int pickRandomMale() {
        if (this.m_male.isEmpty()) {
            return 0;
        }
        return this.pickOutfitMale(PZArrayUtil.pickRandom((List<Data>)this.m_male).m_outfitName);
    }
    
    public int pickOutfitFemale(final String key) {
        final Data data = this.m_outfitToFemale.get(key);
        if (data == null) {
            return 0;
        }
        return Integer.MIN_VALUE | (short)data.m_index << 16 | (data.m_useSeed ? ((short)Rand.Next(500)) : 0) + 1;
    }
    
    public int pickOutfitMale(final String key) {
        final Data data = this.m_outfitToMale.get(key);
        if (data == null) {
            return 0;
        }
        return (short)data.m_index << 16 | (data.m_useSeed ? ((short)Rand.Next(500)) : 0) + 1;
    }
    
    public int pickOutfit(final String s, final boolean b) {
        return b ? this.pickOutfitFemale(s) : this.pickOutfitMale(s);
    }
    
    public int getOutfit(int n) {
        if (n == 0) {
            return 0;
        }
        final int n2 = n & Integer.MIN_VALUE;
        n &= Integer.MAX_VALUE;
        final int n3 = n & 0x8000;
        n &= 0xFFFF7FFF;
        final short index = (short)(n >> 16);
        short n4 = (short)(n & 0xFFFF);
        if (index < 0 || index >= this.m_all.size()) {
            return 0;
        }
        if (this.m_all.get(index).m_useSeed && (n4 < 1 || n4 > 500)) {
            n4 = (short)(Rand.Next(500) + 1);
        }
        return n2 | n3 | index << 16 | n4;
    }
    
    public void save() {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin");
        try {
            final FileOutputStream out = new FileOutputStream(fileInCurrentSave);
            try {
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                        this.save(sliceBuffer);
                        bufferedOutputStream.write(sliceBuffer.array(), 0, sliceBuffer.position());
                    }
                    bufferedOutputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedOutputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out.close();
            }
            catch (Throwable t2) {
                try {
                    out.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.put(PersistentOutfits.FILE_MAGIC);
        byteBuffer.putInt(1);
        byteBuffer.putShort((short)500);
        for (int i = 0; i < 500; ++i) {
            byteBuffer.putLong(this.m_seeds[i]);
        }
    }
    
    public void load() {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin");
        try {
            final FileInputStream in = new FileInputStream(fileInCurrentSave);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                        sliceBuffer.limit(bufferedInputStream.read(sliceBuffer.array()));
                        this.load(sliceBuffer);
                    }
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (FileNotFoundException ex2) {}
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void load(final ByteBuffer byteBuffer) throws IOException {
        final byte[] array = new byte[4];
        byteBuffer.get(array);
        if (!Arrays.equals(array, PersistentOutfits.FILE_MAGIC)) {
            throw new IOException("not magic");
        }
        final int int1 = byteBuffer.getInt();
        if (int1 < 1 || int1 > 1) {
            return;
        }
        for (short short1 = byteBuffer.getShort(), n = 0; n < short1; ++n) {
            if (n < 500) {
                this.m_seeds[n] = byteBuffer.getLong();
            }
        }
    }
    
    public void registerOutfitter(final String s, final boolean b, final IOutfitter outfitter) {
        this.initOutfit(s, true, b, outfitter);
        this.initOutfit(s, false, b, outfitter);
    }
    
    private static void ApplyOutfit(final int n, final String s, final IsoGameCharacter isoGameCharacter) {
        PersistentOutfits.instance.applyOutfit(n, s, isoGameCharacter);
    }
    
    private void applyOutfit(int n, final String s, final IsoGameCharacter isoGameCharacter) {
        final boolean femaleEtc = (n & Integer.MIN_VALUE) != 0x0;
        n &= Integer.MAX_VALUE;
        final Data data = this.m_all.get((short)(n >> 16));
        final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (isoZombie != null) {
            isoZombie.setFemaleEtc(femaleEtc);
        }
        isoGameCharacter.dressInNamedOutfit(data.m_outfitName);
        if (isoZombie != null && isoGameCharacter.doDirtBloodEtc) {
            AttachedWeaponDefinitions.instance.addRandomAttachedWeapon(isoZombie);
            isoZombie.addRandomBloodDirtHolesEtc();
        }
        this.removeFallenHat(n, isoGameCharacter);
    }
    
    public boolean isHatFallen(final IsoGameCharacter isoGameCharacter) {
        return this.isHatFallen(isoGameCharacter.getPersistentOutfitID());
    }
    
    public boolean isHatFallen(final int n) {
        return (n & 0x8000) != 0x0;
    }
    
    public void setFallenHat(final IsoGameCharacter isoGameCharacter, final boolean b) {
        final int persistentOutfitID = isoGameCharacter.getPersistentOutfitID();
        if (persistentOutfitID == 0) {
            return;
        }
        int n;
        if (b) {
            n = (persistentOutfitID | 0x8000);
        }
        else {
            n = (persistentOutfitID & 0xFFFF7FFF);
        }
        isoGameCharacter.setPersistentOutfitID(n, isoGameCharacter.isPersistentOutfitInit());
    }
    
    public boolean removeFallenHat(final int n, final IsoGameCharacter isoGameCharacter) {
        if ((n & 0x8000) == 0x0) {
            return false;
        }
        if (isoGameCharacter.isUsingWornItems()) {
            return false;
        }
        boolean b = false;
        isoGameCharacter.getItemVisuals(PersistentOutfits.tempItemVisuals);
        for (int i = 0; i < PersistentOutfits.tempItemVisuals.size(); ++i) {
            final ItemVisual o = PersistentOutfits.tempItemVisuals.get(i);
            final Item scriptItem = o.getScriptItem();
            if (scriptItem != null && scriptItem.getChanceToFall() > 0) {
                isoGameCharacter.getItemVisuals().remove(o);
                b = true;
            }
        }
        return b;
    }
    
    public void dressInOutfit(final IsoGameCharacter isoGameCharacter, int outfit) {
        outfit = this.getOutfit(outfit);
        if (outfit == 0) {
            return;
        }
        final int n = outfit & 0x7FFF7FFF;
        final short index = (short)(n >> 16);
        final short n2 = (short)(n & 0xFFFF);
        final Data data = this.m_all.get(index);
        if (data.m_useSeed) {
            OutfitRNG.setSeed(this.m_seeds[n2 - 1]);
        }
        data.m_outfitter.accept(outfit, data.m_outfitName, isoGameCharacter);
    }
    
    static {
        instance = new PersistentOutfits();
        FILE_MAGIC = new byte[] { 80, 83, 84, 90 };
        tempItemVisuals = new ItemVisuals();
    }
    
    private static final class Data
    {
        int m_index;
        String m_outfitName;
        boolean m_useSeed;
        IOutfitter m_outfitter;
        
        private Data() {
            this.m_useSeed = true;
        }
    }
    
    private static final class DataList extends ArrayList<Data>
    {
    }
    
    public interface IOutfitter
    {
        void accept(final int p0, final String p1, final IsoGameCharacter p2);
    }
}
