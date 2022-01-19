// 
// Decompiled by Procyon v0.5.36
// 

package zombie.modding;

import java.util.Iterator;
import zombie.util.StringUtils;
import zombie.core.math.PZMath;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import zombie.scripting.ScriptParser;
import zombie.core.logger.ExceptionLogger;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import zombie.core.Core;

public final class ActiveModsFile
{
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    
    public boolean write(final String pathname, final ActiveMods activeMods) {
        if (Core.getInstance().isNoSave()) {
            return false;
        }
        final File file = new File(pathname);
        try {
            final FileWriter out = new FileWriter(file);
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(out);
                try {
                    bufferedWriter.write(this.toString(activeMods));
                    bufferedWriter.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedWriter.close();
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
            return false;
        }
        return true;
    }
    
    private String toString(final ActiveMods activeMods) {
        final ScriptParser.Block block = new ScriptParser.Block();
        block.setValue("VERSION", String.valueOf(1));
        final ScriptParser.Block addBlock = block.addBlock("mods", null);
        final ArrayList<String> mods = activeMods.getMods();
        for (int i = 0; i < mods.size(); ++i) {
            addBlock.addValue("mod", mods.get(i));
        }
        final ScriptParser.Block addBlock2 = block.addBlock("maps", null);
        final ArrayList<String> mapOrder = activeMods.getMapOrder();
        for (int j = 0; j < mapOrder.size(); ++j) {
            addBlock2.addValue("map", mapOrder.get(j));
        }
        final StringBuilder sb = new StringBuilder();
        block.prettyPrintElements(0, sb, System.lineSeparator());
        return sb.toString();
    }
    
    public boolean read(final String fileName, final ActiveMods activeMods) {
        activeMods.clear();
        try {
            final FileReader in = new FileReader(fileName);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    final StringBuilder sb = new StringBuilder();
                    for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
                        sb.append(str);
                    }
                    this.fromString(sb.toString(), activeMods);
                    bufferedReader.close();
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
        catch (FileNotFoundException ex2) {
            return false;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
        return true;
    }
    
    private void fromString(String stripComments, final ActiveMods activeMods) {
        stripComments = ScriptParser.stripComments(stripComments);
        final ScriptParser.Block parse = ScriptParser.parse(stripComments);
        int tryParseInt = -1;
        final ScriptParser.Value value = parse.getValue("VERSION");
        if (value != null) {
            tryParseInt = PZMath.tryParseInt(value.getValue(), -1);
        }
        if (tryParseInt < 1 || tryParseInt > 1) {
            return;
        }
        final ScriptParser.Block block = parse.getBlock("mods", null);
        if (block != null) {
            for (final ScriptParser.Value value2 : block.values) {
                if (!value2.getKey().trim().equalsIgnoreCase("mod")) {
                    continue;
                }
                final String trim = value2.getValue().trim();
                if (StringUtils.isNullOrWhitespace(trim)) {
                    continue;
                }
                activeMods.getMods().add(trim);
            }
        }
        final ScriptParser.Block block2 = parse.getBlock("maps", null);
        if (block2 != null) {
            for (final ScriptParser.Value value3 : block2.values) {
                if (!value3.getKey().trim().equalsIgnoreCase("map")) {
                    continue;
                }
                final String trim2 = value3.getValue().trim();
                if (StringUtils.isNullOrWhitespace(trim2)) {
                    continue;
                }
                activeMods.getMapOrder().add(trim2);
            }
        }
    }
}
