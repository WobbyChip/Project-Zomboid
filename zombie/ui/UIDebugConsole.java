// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import zombie.Lua.LuaManager;
import zombie.debug.DebugOptions;
import java.util.Iterator;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Consumer;
import se.krka.kahlua.stdlib.BaseLib;
import zombie.core.Core;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.ByteBuffer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;

public final class UIDebugConsole extends NewWindow
{
    public static UIDebugConsole instance;
    IsoGameCharacter ParentChar;
    ScrollBar ScrollBarV;
    UITextBox2 OutputLog;
    public UITextBox2 CommandLine;
    UITextBox2 autosuggest;
    String ConsoleVersion;
    int inputlength;
    private final ArrayList<String> Previous;
    private final ArrayList<Method> globalLuaMethods;
    public int PreviousIndex;
    Method prevSuggestion;
    String[] AvailableCommands;
    String[] AvailableCommandsHelp;
    public boolean bDebounceUp;
    public boolean bDebounceDown;
    private static final Object outputLock;
    private static final ByteBuffer outputBB;
    private static boolean outputChanged;
    private static CharsetDecoder outputDecoder;
    private static char[] outputChars;
    private static CharBuffer outputCharBuf;
    
    public UIDebugConsole(final int n, final int n2) {
        super(n, n2, 10, 10, true);
        this.ConsoleVersion = "v1.1.0";
        this.inputlength = 0;
        this.Previous = new ArrayList<String>();
        this.globalLuaMethods = new ArrayList<Method>();
        this.PreviousIndex = 0;
        this.prevSuggestion = null;
        this.AvailableCommands = new String[] { "?", "help", "commands", "clr", "AddInvItem", "SpawnZombie" };
        this.AvailableCommandsHelp = new String[] { "'?' - Shows available commands", "'help' - Shows available commands", "'commands' - Shows available commands", "'clr' - Clears the command log", "'AddInvItem' - Adds an item to player inventory. USAGE - AddInvItem 'ItemName' [ammount]", "'SpawnZombie' - Spawn a zombie at a map location. USAGE - SpawnZombie X,Y,Z (integers)" };
        this.bDebounceUp = false;
        this.bDebounceDown = false;
        this.ResizeToFitY = false;
        this.visible = true;
        UIDebugConsole.instance = this;
        this.width = 640.0f;
        this.OutputLog = new UITextBox2(UIFont.DebugConsole, 5, 33, 630, TextManager.instance.getFontHeight(UIFont.DebugConsole) * 11 + 5 * 2, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Core.getInstance().getVersionNumber(), this.ConsoleVersion), true);
        this.OutputLog.multipleLine = true;
        this.OutputLog.bAlwaysPaginate = false;
        this.CommandLine = new CommandEntry(UIFont.DebugConsole, 5, (int)(this.OutputLog.getY() + this.OutputLog.getHeight()) + 15, 630, 24, "", true);
        this.CommandLine.IsEditable = true;
        this.CommandLine.TextEntryMaxLength = 256;
        this.autosuggest = new UITextBox2(UIFont.DebugConsole, 5, 180, 15, 25, "", true);
        this.height = (float)((int)(this.CommandLine.getY() + this.CommandLine.getHeight()) + 6);
        (this.ScrollBarV = new ScrollBar("UIDebugConsoleScrollbar", null, (int)(this.OutputLog.getX() + this.OutputLog.getWidth()) - 14, this.OutputLog.getY().intValue() + 4, this.OutputLog.getHeight().intValue() - 8, true)).SetParentTextBox(this.OutputLog);
        this.AddChild(this.OutputLog);
        this.AddChild(this.ScrollBarV);
        this.AddChild(this.CommandLine);
        this.AddChild(this.autosuggest);
        this.InitSuggestionEngine();
        if (Core.bDebug) {
            BaseLib.setPrintCallback((Consumer)this::SpoolText);
        }
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        super.render();
        this.DrawTextCentre(UIFont.DebugConsole, "Command Console", this.getWidth() / 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
        this.DrawText(UIFont.DebugConsole, "Output Log", 7.0, 19.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
        this.DrawText(UIFont.DebugConsole, "Lua Command Line", 7.0, this.OutputLog.getY() + this.OutputLog.getHeight() + 1.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
    }
    
    @Override
    public void update() {
        if (!this.isVisible()) {
            return;
        }
        this.handleOutput();
        super.update();
        if (this.CommandLine.getText().length() != this.inputlength && this.CommandLine.getText().length() != 0) {
            this.inputlength = this.CommandLine.getText().length();
            final String[] split = this.CommandLine.getText().split(":");
            String s = "";
            if (split.length > 0) {
                s = split[split.length - 1];
                if (split[split.length - 1].isEmpty() && this.autosuggest.isVisible()) {
                    this.autosuggest.setVisible(false);
                    return;
                }
            }
            Method method = null;
            if (split.length > 1 && split[0].indexOf(")") > 0 && !split[split.length - 1].contains("(")) {
                final ArrayList<Method> list = new ArrayList<Method>(this.globalLuaMethods);
                for (int i = 0; i < split.length; ++i) {
                    final String s2 = split[i];
                    if (s2.indexOf(")") > 0) {
                        final String anObject = s2.split("\\(", 0)[0];
                        for (final Method method2 : list) {
                            if (method2.getName().equals(anObject)) {
                                list.clear();
                                for (Class<?> clazz = method2.getReturnType(); clazz != null; clazz = clazz.getSuperclass()) {
                                    for (final Method e : clazz.getDeclaredMethods()) {
                                        if (Modifier.isPublic(e.getModifiers())) {
                                            list.add(e);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                method = this.SuggestionEngine(s, list);
            }
            else if (split.length == 1) {
                method = this.SuggestionEngine(s);
            }
            String s3 = "void";
            if (method != null) {
                if (!method.getReturnType().toString().equals("void")) {
                    final String[] split2 = method.getReturnType().toString().split("\\.");
                    s3 = split2[split2.length - 1];
                }
                if (!this.autosuggest.isVisible()) {
                    this.autosuggest.setVisible(true);
                }
                this.autosuggest.SetText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, method.getName()));
                this.autosuggest.setX(5 * this.CommandLine.getText().length());
                this.autosuggest.setWidth(15 * (s3.length() + method.getName().length()));
                this.autosuggest.Frame.width = (float)(10 * (s3.length() + method.getName().length()));
            }
        }
        else if (this.CommandLine.getText().length() == 0 && this.autosuggest.isVisible()) {
            this.autosuggest.setVisible(false);
        }
    }
    
    public void ProcessCommand() {
        if (this.CommandLine.internalText == null) {
            return;
        }
        final String internalText = this.CommandLine.internalText;
        this.CommandLine.internalText = "";
        final String trim = internalText.trim();
        final String[] split = trim.split(" ");
        split[0] = split[0].trim();
        if (this.Previous.isEmpty() || !trim.equals(this.Previous.get(this.Previous.size() - 1))) {
            this.Previous.add(trim);
        }
        this.PreviousIndex = this.Previous.size();
        this.CommandLine.DoingTextEntry = true;
        Core.CurrentTextEntryBox = this.CommandLine;
        if ("clear".equals(trim)) {
            this.OutputLog.bTextChanged = true;
            this.OutputLog.clearInput();
            return;
        }
        if (DebugOptions.instance.UIDebugConsoleEchoCommand.getValue()) {
            this.SpoolText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
        }
        try {
            LuaManager.caller.protectedCall(LuaManager.thread, (Object)LuaCompiler.loadstring(trim, "console", LuaManager.env), new Object[0]);
        }
        catch (KahluaException ex) {
            this.SpoolText(ex.getMessage());
        }
        catch (Exception thrown) {
            Logger.getLogger(UIDebugConsole.class.getName()).log(Level.SEVERE, null, thrown);
        }
    }
    
    void historyPrev() {
        --this.PreviousIndex;
        if (this.PreviousIndex < 0) {
            this.PreviousIndex = 0;
        }
        if (this.PreviousIndex >= 0 && this.PreviousIndex < this.Previous.size()) {
            this.CommandLine.SetText(this.Previous.get(this.PreviousIndex));
        }
    }
    
    void historyNext() {
        ++this.PreviousIndex;
        if (this.PreviousIndex >= this.Previous.size()) {
            this.PreviousIndex = this.Previous.size() - 1;
        }
        if (this.PreviousIndex >= 0 && this.PreviousIndex < this.Previous.size()) {
            this.CommandLine.SetText(this.Previous.get(this.PreviousIndex));
        }
    }
    
    public void onOtherKey(final int n) {
        switch (n) {
            case 15: {
                if (this.prevSuggestion == null) {
                    break;
                }
                final String[] split = this.CommandLine.getText().split(":");
                final StringBuilder sb = new StringBuilder();
                if (split.length > 0) {
                    split[split.length - 1] = this.prevSuggestion.getName();
                    for (int i = 0; i < split.length; ++i) {
                        sb.append(split[i]);
                        if (i != split.length - 1) {
                            sb.append(":");
                        }
                    }
                }
                if (this.prevSuggestion.getParameterTypes().length == 0) {
                    this.CommandLine.SetText(invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, sb));
                    break;
                }
                this.CommandLine.SetText(invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, sb));
                break;
            }
        }
    }
    
    void ClearConsole() {
        this.OutputLog.bTextChanged = true;
        this.OutputLog.SetText("");
        this.UpdateViewPos();
    }
    
    void UpdateViewPos() {
        this.OutputLog.TopLineIndex = this.OutputLog.Lines.size() - this.OutputLog.NumVisibleLines;
        if (this.OutputLog.TopLineIndex < 0) {
            this.OutputLog.TopLineIndex = 0;
        }
        this.ScrollBarV.scrollToBottom();
    }
    
    void SpoolText(final String s) {
        this.OutputLog.bTextChanged = true;
        this.OutputLog.SetText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.OutputLog.Text, s));
        this.UpdateViewPos();
    }
    
    Method SuggestionEngine(final String s) {
        return this.SuggestionEngine(s, this.globalLuaMethods);
    }
    
    Method SuggestionEngine(final String s, final ArrayList<Method> list) {
        int levenshteinDistance = 0;
        Method prevSuggestion = null;
        for (final Method method : list) {
            if (prevSuggestion == null) {
                prevSuggestion = method;
                levenshteinDistance = this.levenshteinDistance(s, method.getName());
            }
            else {
                final int levenshteinDistance2 = this.levenshteinDistance(s, method.getName());
                if (levenshteinDistance2 >= levenshteinDistance) {
                    continue;
                }
                levenshteinDistance = levenshteinDistance2;
                prevSuggestion = method;
            }
        }
        return this.prevSuggestion = prevSuggestion;
    }
    
    void InitSuggestionEngine() {
        this.globalLuaMethods.addAll(Arrays.asList(LuaManager.GlobalObject.class.getDeclaredMethods()));
    }
    
    public int levenshteinDistance(final CharSequence charSequence, final CharSequence charSequence2) {
        final int n = charSequence.length() + 1;
        final int n2 = charSequence2.length() + 1;
        int[] array = new int[n];
        int[] array2 = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = i;
        }
        for (int j = 1; j < n2; ++j) {
            array2[0] = j;
            for (int k = 1; k < n; ++k) {
                array2[k] = Math.min(Math.min(array[k] + 1, array2[k - 1] + 1), array[k - 1] + ((charSequence.charAt(k - 1) != charSequence2.charAt(j - 1)) ? 1 : 0));
            }
            final int[] array3 = array;
            array = array2;
            array2 = array3;
        }
        return array[n - 1];
    }
    
    void setSuggestWidth(final int n) {
        this.autosuggest.setWidth(n);
        this.autosuggest.Frame.width = (float)n;
    }
    
    public void addOutput(final byte[] src, int offset, int length) {
        if (length < 1) {
            return;
        }
        synchronized (UIDebugConsole.outputLock) {
            final int n = length - UIDebugConsole.outputBB.capacity();
            if (n > 0) {
                offset += n;
                length -= n;
            }
            if (UIDebugConsole.outputBB.position() + length > UIDebugConsole.outputBB.capacity()) {
                UIDebugConsole.outputBB.clear();
            }
            UIDebugConsole.outputBB.put(src, offset, length);
            if (src[offset + length - 1] == 10) {
                UIDebugConsole.outputChanged = true;
            }
        }
    }
    
    private void handleOutput() {
        synchronized (UIDebugConsole.outputLock) {
            if (UIDebugConsole.outputChanged) {
                UIDebugConsole.outputChanged = false;
                try {
                    if (UIDebugConsole.outputDecoder == null) {
                        UIDebugConsole.outputDecoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
                    }
                    UIDebugConsole.outputDecoder.reset();
                    final int position = UIDebugConsole.outputBB.position();
                    UIDebugConsole.outputBB.flip();
                    final int n = (int)(position * (double)UIDebugConsole.outputDecoder.maxCharsPerByte());
                    if (UIDebugConsole.outputChars == null || UIDebugConsole.outputChars.length < n) {
                        UIDebugConsole.outputChars = new char[(n + 128 - 1) / 128 * 128];
                        UIDebugConsole.outputCharBuf = CharBuffer.wrap(UIDebugConsole.outputChars);
                    }
                    UIDebugConsole.outputCharBuf.clear();
                    UIDebugConsole.outputDecoder.decode(UIDebugConsole.outputBB, UIDebugConsole.outputCharBuf, true);
                    UIDebugConsole.outputBB.clear();
                    final String s = new String(UIDebugConsole.outputChars, 0, UIDebugConsole.outputCharBuf.position());
                    this.OutputLog.bTextChanged = true;
                    this.OutputLog.SetText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.OutputLog.Text, s));
                    final int n2 = 8192;
                    if (this.OutputLog.Text.length() > n2) {
                        int index;
                        for (index = this.OutputLog.Text.length() - n2; index < this.OutputLog.Text.length() && this.OutputLog.Text.charAt(index) != '\n'; ++index) {}
                        this.OutputLog.bTextChanged = true;
                        this.OutputLog.SetText(this.OutputLog.Text.substring(index + 1));
                    }
                }
                catch (Exception ex) {}
                this.UpdateViewPos();
            }
        }
    }
    
    static {
        outputLock = "DebugConsole Output Lock";
        outputBB = ByteBuffer.allocate(8192);
        UIDebugConsole.outputChanged = false;
        UIDebugConsole.outputCharBuf = null;
    }
    
    private class CommandEntry extends UITextBox2
    {
        public CommandEntry(final UIFont uiFont, final int n, final int n2, final int n3, final int n4, final String s, final boolean b) {
            super(uiFont, n, n2, n3, n4, s, b);
        }
        
        @Override
        public void onPressUp() {
            UIDebugConsole.this.historyPrev();
        }
        
        @Override
        public void onPressDown() {
            UIDebugConsole.this.historyNext();
        }
        
        @Override
        public void onOtherKey(final int n) {
            UIDebugConsole.this.onOtherKey(n);
        }
    }
}
