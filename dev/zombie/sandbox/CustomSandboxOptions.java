// 
// Decompiled by Procyon v0.5.36
// 

package zombie.sandbox;

import zombie.util.StringUtils;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import zombie.SandboxOptions;
import java.io.File;
import zombie.gameStates.ChooseGameInfo;
import zombie.ZomboidFileSystem;
import java.util.ArrayList;

public final class CustomSandboxOptions
{
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    public static final CustomSandboxOptions instance;
    private final ArrayList<CustomSandboxOption> m_options;
    
    public CustomSandboxOptions() {
        this.m_options = new ArrayList<CustomSandboxOption>();
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
    }
    
    public static void Reset() {
        CustomSandboxOptions.instance.m_options.clear();
    }
    
    public void initInstance(final SandboxOptions sandboxOptions) {
        for (int i = 0; i < this.m_options.size(); ++i) {
            sandboxOptions.newCustomOption(this.m_options.get(i));
        }
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
            if (!block.type.equalsIgnoreCase("option")) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, block.type));
            }
            final CustomSandboxOption option = this.parseOption(block);
            if (option == null) {
                DebugLog.General.warn("failed to parse custom sandbox option \"%s\"", block.id);
            }
            else {
                this.m_options.add(option);
            }
        }
    }
    
    private CustomSandboxOption parseOption(final ScriptParser.Block block) {
        if (StringUtils.isNullOrWhitespace(block.id)) {
            DebugLog.General.warn((Object)"missing or empty option id");
            return null;
        }
        final ScriptParser.Value value = block.getValue("type");
        if (value == null || StringUtils.isNullOrWhitespace(value.getValue())) {
            DebugLog.General.warn((Object)"missing or empty value \"type\"");
            return null;
        }
        final String trim = value.getValue().trim();
        switch (trim) {
            case "boolean": {
                return CustomBooleanSandboxOption.parse(block);
            }
            case "double": {
                return CustomDoubleSandboxOption.parse(block);
            }
            case "enum": {
                return CustomEnumSandboxOption.parse(block);
            }
            case "integer": {
                return CustomIntegerSandboxOption.parse(block);
            }
            case "string": {
                return CustomStringSandboxOption.parse(block);
            }
            default: {
                DebugLog.General.warn("unknown option type \"%s\"", value.getValue().trim());
                return null;
            }
        }
    }
    
    static {
        instance = new CustomSandboxOptions();
    }
}
