// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.GameTime;

public class HaloTextHelper
{
    public static final ColorRGB COLOR_WHITE;
    public static final ColorRGB COLOR_GREEN;
    public static final ColorRGB COLOR_RED;
    private static String[] queuedLines;
    private static String[] currentLines;
    private static boolean ignoreOverheadCheckOnce;
    
    public static ColorRGB getColorWhite() {
        return HaloTextHelper.COLOR_WHITE;
    }
    
    public static ColorRGB getColorGreen() {
        return HaloTextHelper.COLOR_GREEN;
    }
    
    public static ColorRGB getColorRed() {
        return HaloTextHelper.COLOR_RED;
    }
    
    public static void forceNextAddText() {
        HaloTextHelper.ignoreOverheadCheckOnce = true;
    }
    
    public static void addTextWithArrow(final IsoPlayer isoPlayer, final String s, final boolean b, final ColorRGB colorRGB) {
        addTextWithArrow(isoPlayer, s, b, colorRGB.r, colorRGB.g, colorRGB.b, colorRGB.r, colorRGB.g, colorRGB.b);
    }
    
    public static void addTextWithArrow(final IsoPlayer isoPlayer, final String s, final boolean b, final int n, final int n2, final int n3) {
        addTextWithArrow(isoPlayer, s, b, n, n2, n3, n, n2, n3);
    }
    
    public static void addTextWithArrow(final IsoPlayer isoPlayer, final String s, final boolean b, final ColorRGB colorRGB, final ColorRGB colorRGB2) {
        addTextWithArrow(isoPlayer, s, b, colorRGB.r, colorRGB.g, colorRGB.b, colorRGB2.r, colorRGB2.g, colorRGB2.b);
    }
    
    public static void addTextWithArrow(final IsoPlayer isoPlayer, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        addText(isoPlayer, invokedynamic(makeConcatWithConstants:(IIILjava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, n, n2, n3, s, b ? "ArrowUp.png" : "ArrowDown.png", n4, n5, n6));
    }
    
    public static void addText(final IsoPlayer isoPlayer, final String s, final ColorRGB colorRGB) {
        addText(isoPlayer, s, colorRGB.r, colorRGB.g, colorRGB.b);
    }
    
    public static void addText(final IsoPlayer isoPlayer, final String s, final int n, final int n2, final int n3) {
        addText(isoPlayer, invokedynamic(makeConcatWithConstants:(IIILjava/lang/String;)Ljava/lang/String;, n, n2, n3, s));
    }
    
    public static void addText(final IsoPlayer isoPlayer, final String s) {
        final int playerNum = isoPlayer.getPlayerNum();
        if (overheadContains(playerNum, s)) {
            return;
        }
        final String s2 = HaloTextHelper.queuedLines[playerNum];
        String s3;
        if (s2 == null) {
            s3 = s;
        }
        else {
            if (s2.contains(s)) {
                return;
            }
            s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s);
        }
        HaloTextHelper.queuedLines[playerNum] = s3;
    }
    
    private static boolean overheadContains(final int n, final String s) {
        if (HaloTextHelper.ignoreOverheadCheckOnce) {
            return HaloTextHelper.ignoreOverheadCheckOnce = false;
        }
        return HaloTextHelper.currentLines[n] != null && HaloTextHelper.currentLines[n].contains(s);
    }
    
    public static void update() {
        for (int i = 0; i < 4; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null) {
                if (HaloTextHelper.currentLines[i] != null && isoPlayer.getHaloTimerCount() <= 0.2f * GameTime.getInstance().getMultiplier()) {
                    HaloTextHelper.currentLines[i] = null;
                }
                if (HaloTextHelper.queuedLines[i] != null && isoPlayer.getHaloTimerCount() <= 0.2f * GameTime.getInstance().getMultiplier()) {
                    isoPlayer.setHaloNote(HaloTextHelper.queuedLines[i]);
                    HaloTextHelper.currentLines[i] = HaloTextHelper.queuedLines[i];
                    HaloTextHelper.queuedLines[i] = null;
                }
            }
            else {
                if (HaloTextHelper.queuedLines[i] != null) {
                    HaloTextHelper.queuedLines[i] = null;
                }
                if (HaloTextHelper.currentLines[i] != null) {
                    HaloTextHelper.currentLines[i] = null;
                }
            }
        }
    }
    
    static {
        COLOR_WHITE = new ColorRGB(255, 255, 255);
        COLOR_GREEN = new ColorRGB(137, 232, 148);
        COLOR_RED = new ColorRGB(255, 105, 97);
        HaloTextHelper.queuedLines = new String[4];
        HaloTextHelper.currentLines = new String[4];
        HaloTextHelper.ignoreOverheadCheckOnce = false;
    }
    
    public static class ColorRGB
    {
        public int r;
        public int g;
        public int b;
        public int a;
        
        public ColorRGB(final int r, final int g, final int b) {
            this.a = 255;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
