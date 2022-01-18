// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.skills;

import zombie.util.StringUtils;
import zombie.debug.DebugLog;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.Iterator;
import java.io.File;
import zombie.gameStates.ChooseGameInfo;
import zombie.ZomboidFileSystem;
import java.util.ArrayList;

public final class CustomPerks
{
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    public static final CustomPerks instance;
    private final ArrayList<CustomPerk> m_perks;
    
    public CustomPerks() {
        this.m_perks = new ArrayList<CustomPerk>();
    }
    
    public void init() {
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(modIDs.get(i));
            if (availableModDetails != null) {
                final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, availableModDetails.getDir(), File.separator, File.separator));
                if (file.exists()) {
                    if (!file.isDirectory()) {
                        this.readFile(file.getAbsolutePath());
                    }
                }
            }
        }
        for (final CustomPerk customPerk : this.m_perks) {
            final PerkFactory.Perk fromString = PerkFactory.Perks.FromString(customPerk.m_id);
            if (fromString == null || fromString == PerkFactory.Perks.None || fromString == PerkFactory.Perks.MAX) {
                new PerkFactory.Perk(customPerk.m_id).setCustom();
            }
        }
        for (final CustomPerk customPerk2 : this.m_perks) {
            final PerkFactory.Perk fromString2 = PerkFactory.Perks.FromString(customPerk2.m_id);
            PerkFactory.Perk perk = PerkFactory.Perks.FromString(customPerk2.m_parent);
            if (perk == null || perk == PerkFactory.Perks.None || perk == PerkFactory.Perks.MAX) {
                perk = PerkFactory.Perks.None;
            }
            final int[] xp = customPerk2.m_xp;
            PerkFactory.AddPerk(fromString2, customPerk2.m_translation, perk, xp[0], xp[1], xp[2], xp[3], xp[4], xp[5], xp[6], xp[7], xp[8], xp[9], customPerk2.m_bPassive);
        }
    }
    
    public void initLua() {
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"Perks");
        final Iterator<CustomPerk> iterator = this.m_perks.iterator();
        while (iterator.hasNext()) {
            final PerkFactory.Perk fromString = PerkFactory.Perks.FromString(iterator.next().m_id);
            kahluaTable.rawset((Object)fromString.getId(), (Object)fromString);
        }
    }
    
    public static void Reset() {
        CustomPerks.instance.m_perks.clear();
    }
    
    private boolean readFile(final String fileName) {
        try {
            final FileReader in = new FileReader(fileName);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    final StringBuilder sb = new StringBuilder();
                    for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
                        sb.append(str);
                    }
                    this.parse(sb.toString());
                    final boolean b = true;
                    bufferedReader.close();
                    in.close();
                    return b;
                }
                catch (Throwable t) {
                    try {
                        bufferedReader.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
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
        catch (FileNotFoundException ex2) {
            return false;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
    }
    
    private void parse(String stripComments) {
        stripComments = ScriptParser.stripComments(stripComments);
        final ScriptParser.Block parse = ScriptParser.parse(stripComments);
        int tryParseInt = -1;
        final ScriptParser.Value value = parse.getValue("VERSION");
        if (value != null) {
            tryParseInt = PZMath.tryParseInt(value.getValue(), -1);
        }
        if (tryParseInt < 1 || tryParseInt > 1) {
            throw new RuntimeException("invalid or missing VERSION");
        }
        for (final ScriptParser.Block block : parse.children) {
            if (!block.type.equalsIgnoreCase("perk")) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, block.type));
            }
            final CustomPerk perk = this.parsePerk(block);
            if (perk == null) {
                DebugLog.General.warn("failed to parse custom perk \"%s\"", block.id);
            }
            else {
                this.m_perks.add(perk);
            }
        }
    }
    
    private CustomPerk parsePerk(final ScriptParser.Block block) {
        if (StringUtils.isNullOrWhitespace(block.id)) {
            DebugLog.General.warn((Object)"missing or empty perk id");
            return null;
        }
        final CustomPerk customPerk = new CustomPerk(block.id);
        final ScriptParser.Value value = block.getValue("parent");
        if (value != null && !StringUtils.isNullOrWhitespace(value.getValue())) {
            customPerk.m_parent = value.getValue().trim();
        }
        final ScriptParser.Value value2 = block.getValue("translation");
        if (value2 != null) {
            customPerk.m_translation = StringUtils.discardNullOrWhitespace(value2.getValue().trim());
        }
        if (StringUtils.isNullOrWhitespace(customPerk.m_translation)) {
            customPerk.m_translation = customPerk.m_id;
        }
        final ScriptParser.Value value3 = block.getValue("passive");
        if (value3 != null) {
            customPerk.m_bPassive = StringUtils.tryParseBoolean(value3.getValue().trim());
        }
        for (int i = 1; i <= 10; ++i) {
            final ScriptParser.Value value4 = block.getValue(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            if (value4 != null) {
                final int tryParseInt = PZMath.tryParseInt(value4.getValue().trim(), -1);
                if (tryParseInt > 0) {
                    customPerk.m_xp[i - 1] = tryParseInt;
                }
            }
        }
        return customPerk;
    }
    
    static {
        instance = new CustomPerks();
    }
}
