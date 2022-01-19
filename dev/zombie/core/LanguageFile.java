// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import zombie.util.StringUtils;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;

public final class LanguageFile
{
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    
    public boolean read(final String fileName, final LanguageFileData languageFileData) {
        try {
            final FileReader in = new FileReader(fileName);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    final StringBuilder sb = new StringBuilder();
                    for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
                        sb.append(str);
                    }
                    this.fromString(sb.toString(), languageFileData);
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
    
    private void fromString(String stripComments, final LanguageFileData languageFileData) {
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
        final ScriptParser.Value value2 = parse.getValue("text");
        if (value2 == null || StringUtils.isNullOrWhitespace(value2.getValue())) {
            throw new RuntimeException("missing or empty value \"text\"");
        }
        final ScriptParser.Value value3 = parse.getValue("charset");
        if (value3 == null || StringUtils.isNullOrWhitespace(value3.getValue())) {
            throw new RuntimeException("missing or empty value \"charset\"");
        }
        languageFileData.text = value2.getValue().trim();
        languageFileData.charset = value3.getValue().trim();
        final ScriptParser.Value value4 = parse.getValue("base");
        if (value4 != null && !StringUtils.isNullOrWhitespace(value4.getValue())) {
            languageFileData.base = value4.getValue().trim();
        }
        final ScriptParser.Value value5 = parse.getValue("azerty");
        if (value5 != null) {
            languageFileData.azerty = StringUtils.tryParseBoolean(value5.getValue());
        }
    }
}
