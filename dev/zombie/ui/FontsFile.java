// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public final class FontsFile
{
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    
    public boolean read(final String fileName, final HashMap<String, FontsFileFont> hashMap) {
        try {
            final FileReader in = new FileReader(fileName);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    final StringBuilder sb = new StringBuilder();
                    for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
                        sb.append(str);
                    }
                    this.fromString(sb.toString(), hashMap);
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
    
    private void fromString(String stripComments, final HashMap<String, FontsFileFont> hashMap) {
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
            if (!block.type.equalsIgnoreCase("font")) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, block.type));
            }
            if (StringUtils.isNullOrWhitespace(block.id)) {
                DebugLog.General.warn((Object)"missing or empty font id");
            }
            else {
                final ScriptParser.Value value2 = block.getValue("fnt");
                final ScriptParser.Value value3 = block.getValue("img");
                if (value2 == null || StringUtils.isNullOrWhitespace(value2.getValue())) {
                    DebugLog.General.warn((Object)"missing or empty value \"fnt\"");
                }
                else {
                    final FontsFileFont value4 = new FontsFileFont();
                    value4.id = block.id;
                    value4.fnt = value2.getValue().trim();
                    if (value3 != null && !StringUtils.isNullOrWhitespace(value3.getValue())) {
                        value4.img = value3.getValue().trim();
                    }
                    hashMap.put(value4.id, value4);
                }
            }
        }
    }
}
