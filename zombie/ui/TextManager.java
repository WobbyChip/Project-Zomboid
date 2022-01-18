// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.network.ServerGUI;
import zombie.network.GameServer;
import java.io.FileNotFoundException;
import java.io.File;
import zombie.debug.DebugLog;
import java.util.Arrays;
import zombie.core.Core;
import zombie.core.Translator;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import java.util.ArrayList;
import zombie.core.fonts.AngelCodeFont;

public final class TextManager
{
    public AngelCodeFont font;
    public AngelCodeFont font2;
    public AngelCodeFont font3;
    public AngelCodeFont font4;
    public AngelCodeFont main1;
    public AngelCodeFont main2;
    public AngelCodeFont zombiefontcredits1;
    public AngelCodeFont zombiefontcredits2;
    public AngelCodeFont zombienew1;
    public AngelCodeFont zombienew2;
    public AngelCodeFont zomboidDialogue;
    public AngelCodeFont codetext;
    public AngelCodeFont debugConsole;
    public AngelCodeFont intro;
    public AngelCodeFont handwritten;
    public final AngelCodeFont[] normal;
    public AngelCodeFont zombienew3;
    public final AngelCodeFont[] enumToFont;
    public static final TextManager instance;
    public ArrayList<DeferedTextDraw> todoTextList;
    
    public TextManager() {
        this.normal = new AngelCodeFont[14];
        this.enumToFont = new AngelCodeFont[UIFont.values().length];
        this.todoTextList = new ArrayList<DeferedTextDraw>();
    }
    
    public void DrawString(final double n, final double n2, final String s) {
        this.font.drawString((float)n, (float)n2, s, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void DrawString(final double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        this.font.drawString((float)n, (float)n2, s, (float)n3, (float)n4, (float)n5, (float)n6);
    }
    
    public void DrawString(final UIFont uiFont, final double n, final double n2, final double n3, final String s, final double n4, final double n5, final double n6, final double n7) {
        this.getFontFromEnum(uiFont).drawString((float)n, (float)n2, (float)n3, s, (float)n4, (float)n5, (float)n6, (float)n7);
    }
    
    public void DrawString(final UIFont uiFont, final double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        this.getFontFromEnum(uiFont).drawString((float)n, (float)n2, s, (float)n3, (float)n4, (float)n5, (float)n6);
    }
    
    public void DrawStringUntrimmed(final UIFont uiFont, final double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        this.getFontFromEnum(uiFont).drawString((float)n, (float)n2, s, (float)n3, (float)n4, (float)n5, (float)n6);
    }
    
    public void DrawStringCentre(double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        n -= this.font.getWidth(s) / 2;
        this.font.drawString((float)n, (float)n2, s, (float)n3, (float)n4, (float)n5, (float)n6);
    }
    
    public void DrawStringCentre(final UIFont uiFont, double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        final AngelCodeFont fontFromEnum = this.getFontFromEnum(uiFont);
        n -= fontFromEnum.getWidth(s) / 2;
        fontFromEnum.drawString((float)n, (float)n2, s, (float)n3, (float)n4, (float)n5, (float)n6);
    }
    
    public void DrawStringCentreDefered(final UIFont uiFont, final double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        this.todoTextList.add(new DeferedTextDraw(uiFont, n, n2, s, n3, n4, n5, n6));
    }
    
    public void DrawTextFromGameWorld() {
        for (int i = 0; i < this.todoTextList.size(); ++i) {
            final DeferedTextDraw deferedTextDraw = this.todoTextList.get(i);
            this.DrawStringCentre(deferedTextDraw.font, deferedTextDraw.x, deferedTextDraw.y, deferedTextDraw.str, deferedTextDraw.r, deferedTextDraw.g, deferedTextDraw.b, deferedTextDraw.a);
        }
        this.todoTextList.clear();
    }
    
    public void DrawStringRight(double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        n -= this.font.getWidth(s);
        this.font.drawString((float)n, (float)n2, s, (float)n3, (float)n4, (float)n5, (float)n6);
    }
    
    public TextDrawObject GetDrawTextObject(final String s, final int n, final boolean b) {
        return new TextDrawObject();
    }
    
    public void DrawTextObject(final double n, final double n2, final TextDrawObject textDrawObject) {
    }
    
    public void DrawStringBBcode(final UIFont uiFont, final double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
    }
    
    public AngelCodeFont getNormalFromFontSize(final int n) {
        return this.normal[n - 11];
    }
    
    public AngelCodeFont getFontFromEnum(final UIFont uiFont) {
        if (uiFont == null) {
            return this.font;
        }
        final AngelCodeFont angelCodeFont = this.enumToFont[uiFont.ordinal()];
        return (angelCodeFont == null) ? this.font : angelCodeFont;
    }
    
    public int getFontHeight(final UIFont uiFont) {
        return this.getFontFromEnum(uiFont).getLineHeight();
    }
    
    public void DrawStringRight(final UIFont uiFont, double n, final double n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        final AngelCodeFont fontFromEnum = this.getFontFromEnum(uiFont);
        n -= fontFromEnum.getWidth(s);
        fontFromEnum.drawString((float)n, (float)n2, s, (float)n3, (float)n4, (float)n5, (float)n6);
    }
    
    private String getFontFilePath(final String anObject, final String s, final String s2) {
        if (s != null) {
            final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, anObject, s, s2);
            if (ZomboidFileSystem.instance.getString(s3) != s3) {
                return s3;
            }
        }
        final String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, anObject, s2);
        if (ZomboidFileSystem.instance.getString(s4) != s4) {
            return s4;
        }
        if (!"EN".equals(anObject)) {
            if (s != null) {
                final String s5 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
                if (ZomboidFileSystem.instance.getString(s5) != s5) {
                    return s5;
                }
            }
            final String s6 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
            if (ZomboidFileSystem.instance.getString(s6) != s6) {
                return s6;
            }
        }
        final String s7 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
        if (ZomboidFileSystem.instance.getString(s7) != s7) {
            return s7;
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
    }
    
    public void Init() throws FileNotFoundException {
        final String string = ZomboidFileSystem.instance.getString("media/fonts/EN/fonts.txt");
        final FontsFile fontsFile = new FontsFile();
        final HashMap<String, FontsFileFont> hashMap = new HashMap<String, FontsFileFont>();
        fontsFile.read(string, hashMap);
        final String name = Translator.getLanguage().name();
        if (!"EN".equals(name)) {
            fontsFile.read(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name)), hashMap);
        }
        final HashMap<String, AngelCodeFont> hashMap2 = new HashMap<String, AngelCodeFont>();
        String s = null;
        if (Core.OptionFontSize == 2) {
            s = "1x";
        }
        else if (Core.OptionFontSize == 3) {
            s = "2x";
        }
        else if (Core.OptionFontSize == 4) {
            s = "3x";
        }
        else if (Core.OptionFontSize == 5) {
            s = "4x";
        }
        for (final AngelCodeFont angelCodeFont : this.enumToFont) {
            if (angelCodeFont != null) {
                angelCodeFont.destroy();
            }
        }
        Arrays.fill(this.enumToFont, null);
        for (final AngelCodeFont angelCodeFont2 : this.normal) {
            if (angelCodeFont2 != null) {
                angelCodeFont2.destroy();
            }
        }
        Arrays.fill(this.normal, null);
        for (final UIFont uiFont : UIFont.values()) {
            final FontsFileFont fontsFileFont = hashMap.get(uiFont.name());
            if (fontsFileFont == null) {
                DebugLog.General.warn("font \"%s\" not found in fonts.txt", uiFont.name());
            }
            else {
                final String fontFilePath = this.getFontFilePath(name, s, fontsFileFont.fnt);
                String fontFilePath2 = null;
                if (fontsFileFont.img != null) {
                    fontFilePath2 = this.getFontFilePath(name, s, fontsFileFont.img);
                }
                final String key = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, fontFilePath, fontFilePath2);
                if (hashMap2.get(key) != null) {
                    this.enumToFont[uiFont.ordinal()] = hashMap2.get(key);
                }
                else {
                    hashMap2.put(key, this.enumToFont[uiFont.ordinal()] = new AngelCodeFont(fontFilePath, fontFilePath2));
                }
            }
        }
        try {
            ZomboidFileSystem.instance.IgnoreActiveFileMap = true;
            final String replaceAll = new File("").getAbsolutePath().replaceAll("\\\\", "/");
            String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, replaceAll);
            final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, replaceAll);
            if (s2.startsWith("/")) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
            }
            this.enumToFont[UIFont.DebugConsole.ordinal()] = new AngelCodeFont(s2, s3);
        }
        finally {
            ZomboidFileSystem.instance.IgnoreActiveFileMap = false;
        }
        for (int l = 0; l < this.normal.length; ++l) {
            this.normal[l] = new AngelCodeFont(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, l + 11), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, l + 11));
        }
        this.font = this.enumToFont[UIFont.Small.ordinal()];
        this.font2 = this.enumToFont[UIFont.Medium.ordinal()];
        this.font3 = this.enumToFont[UIFont.Large.ordinal()];
        this.font4 = this.enumToFont[UIFont.Massive.ordinal()];
        this.main1 = this.enumToFont[UIFont.MainMenu1.ordinal()];
        this.main2 = this.enumToFont[UIFont.MainMenu2.ordinal()];
        this.zombiefontcredits1 = this.enumToFont[UIFont.Cred1.ordinal()];
        this.zombiefontcredits2 = this.enumToFont[UIFont.Cred2.ordinal()];
        this.zombienew1 = this.enumToFont[UIFont.NewSmall.ordinal()];
        this.zombienew2 = this.enumToFont[UIFont.NewMedium.ordinal()];
        this.zombienew3 = this.enumToFont[UIFont.NewLarge.ordinal()];
        this.codetext = this.enumToFont[UIFont.Code.ordinal()];
        this.enumToFont[UIFont.MediumNew.ordinal()] = null;
        this.enumToFont[UIFont.AutoNormSmall.ordinal()] = null;
        this.enumToFont[UIFont.AutoNormMedium.ordinal()] = null;
        this.enumToFont[UIFont.AutoNormLarge.ordinal()] = null;
        this.zomboidDialogue = this.enumToFont[UIFont.Dialogue.ordinal()];
        this.intro = this.enumToFont[UIFont.Intro.ordinal()];
        this.handwritten = this.enumToFont[UIFont.Handwritten.ordinal()];
        this.debugConsole = this.enumToFont[UIFont.DebugConsole.ordinal()];
    }
    
    public int MeasureStringX(final UIFont uiFont, final String s) {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return 0;
        }
        if (s == null) {
            return 0;
        }
        return this.getFontFromEnum(uiFont).getWidth(s);
    }
    
    public int MeasureStringY(final UIFont uiFont, final String s) {
        if (uiFont == null || s == null) {
            return 0;
        }
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return 0;
        }
        return this.getFontFromEnum(uiFont).getHeight(s);
    }
    
    public int MeasureFont(final UIFont uiFont) {
        if (uiFont == UIFont.Small) {
            return 10;
        }
        if (uiFont == UIFont.Dialogue) {
            return 20;
        }
        if (uiFont == UIFont.Medium) {
            return 20;
        }
        if (uiFont == UIFont.Large) {
            return 24;
        }
        if (uiFont == UIFont.Massive) {
            return 30;
        }
        if (uiFont == UIFont.MainMenu1) {
            return 30;
        }
        if (uiFont == UIFont.MainMenu2) {
            return 30;
        }
        return this.getFontFromEnum(uiFont).getLineHeight();
    }
    
    static {
        instance = new TextManager();
    }
    
    public static class DeferedTextDraw
    {
        public double x;
        public double y;
        public UIFont font;
        public String str;
        public double r;
        public double g;
        public double b;
        public double a;
        
        public DeferedTextDraw(final UIFont font, final double x, final double y, final String str, final double r, final double g, final double b, final double a) {
            this.font = font;
            this.x = x;
            this.y = y;
            this.str = str;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }
}
