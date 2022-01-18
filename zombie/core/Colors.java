// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

public final class Colors
{
    private static final ArrayList<Color> colors;
    private static final HashMap<String, Color> colorMap;
    private static final ArrayList<String> colorNames;
    private static final HashSet<String> colorSet;
    public static final Color IndianRed;
    public static final Color LightCoral;
    public static final Color Salmon;
    public static final Color DarkSalmon;
    public static final Color LightSalmon;
    public static final Color Crimson;
    public static final Color Red;
    public static final Color FireBrick;
    public static final Color DarkRed;
    public static final Color Pink;
    public static final Color LightPink;
    public static final Color HotPink;
    public static final Color DeepPink;
    public static final Color MediumVioletRed;
    public static final Color PaleVioletRed;
    public static final Color Coral;
    public static final Color Tomato;
    public static final Color OrangeRed;
    public static final Color DarkOrange;
    public static final Color Orange;
    public static final Color Gold;
    public static final Color Yellow;
    public static final Color LightYellow;
    public static final Color LemonChiffon;
    public static final Color LightGoldenrodYellow;
    public static final Color PapayaWhip;
    public static final Color Moccasin;
    public static final Color PeachPu;
    public static final Color PaleGoldenrod;
    public static final Color Khaki;
    public static final Color DarkKhaki;
    public static final Color Lavender;
    public static final Color Thistle;
    public static final Color Plum;
    public static final Color Violet;
    public static final Color Orchid;
    public static final Color Fuchsia;
    public static final Color Magenta;
    public static final Color MediumOrchid;
    public static final Color MediumPurple;
    public static final Color BlueViolet;
    public static final Color DarkViolet;
    public static final Color DarkOrchid;
    public static final Color DarkMagenta;
    public static final Color Purple;
    public static final Color Indigo;
    public static final Color SlateBlue;
    public static final Color DarkSlateBlue;
    public static final Color GreenYellow;
    public static final Color Chartreuse;
    public static final Color LawnGreen;
    public static final Color Lime;
    public static final Color LimeGreen;
    public static final Color PaleGreen;
    public static final Color LightGreen;
    public static final Color MediumSpringGreen;
    public static final Color SpringGreen;
    public static final Color MediumSeaGreen;
    public static final Color SeaGreen;
    public static final Color ForestGreen;
    public static final Color Green;
    public static final Color DarkGreen;
    public static final Color YellowGreen;
    public static final Color OliveDrab;
    public static final Color Olive;
    public static final Color DarkOliveGreen;
    public static final Color MediumAquamarine;
    public static final Color DarkSeaGreen;
    public static final Color LightSeaGreen;
    public static final Color DarkCyan;
    public static final Color Teal;
    public static final Color Aqua;
    public static final Color Cyan;
    public static final Color LightCyan;
    public static final Color PaleTurquoise;
    public static final Color Aquamarine;
    public static final Color Turquoise;
    public static final Color MediumTurquoise;
    public static final Color DarkTurquoise;
    public static final Color CadetBlue;
    public static final Color SteelBlue;
    public static final Color LightSteelBlue;
    public static final Color PowderBlue;
    public static final Color LightBlue;
    public static final Color SkyBlue;
    public static final Color LightSkyBlue;
    public static final Color DeepSkyBlue;
    public static final Color DodgerBlue;
    public static final Color CornFlowerBlue;
    public static final Color MediumSlateBlue;
    public static final Color RoyalBlue;
    public static final Color Blue;
    public static final Color MediumBlue;
    public static final Color DarkBlue;
    public static final Color Navy;
    public static final Color MidnightBlue;
    public static final Color CornSilk;
    public static final Color BlanchedAlmond;
    public static final Color Bisque;
    public static final Color NavajoWhite;
    public static final Color Wheat;
    public static final Color BurlyWood;
    public static final Color Tan;
    public static final Color RosyBrown;
    public static final Color SandyBrown;
    public static final Color Goldenrod;
    public static final Color DarkGoldenrod;
    public static final Color Peru;
    public static final Color Chocolate;
    public static final Color SaddleBrown;
    public static final Color Sienna;
    public static final Color Brown;
    public static final Color Maroon;
    
    private static Color addColor(final String e, final Color color) {
        Colors.colors.add(color);
        Colors.colorMap.put(e.toLowerCase(), color);
        Colors.colorNames.add(e);
        Colors.colorSet.add(e.toLowerCase());
        return color;
    }
    
    public static Color GetRandomColor() {
        return Colors.colors.get(Rand.Next(0, Colors.colors.size() - 1));
    }
    
    public static Color GetColorFromIndex(final int index) {
        return Colors.colors.get(index);
    }
    
    public static int GetColorsCount() {
        return Colors.colors.size();
    }
    
    public static Color GetColorByName(final String s) {
        return Colors.colorMap.get(s.toLowerCase());
    }
    
    public static ArrayList<String> GetColorNames() {
        return Colors.colorNames;
    }
    
    public static boolean ColorExists(final String s) {
        return Colors.colorSet.contains(s.toLowerCase());
    }
    
    static {
        colors = new ArrayList<Color>();
        colorMap = new HashMap<String, Color>();
        colorNames = new ArrayList<String>();
        colorSet = new HashSet<String>();
        IndianRed = addColor("IndianRed", new Color(0.804f, 0.361f, 0.361f));
        LightCoral = addColor("LightCoral", new Color(0.941f, 0.502f, 0.502f));
        Salmon = addColor("Salmon", new Color(0.98f, 0.502f, 0.447f));
        DarkSalmon = addColor("DarkSalmon", new Color(0.914f, 0.588f, 0.478f));
        LightSalmon = addColor("LightSalmon", new Color(1.0f, 0.627f, 0.478f));
        Crimson = addColor("Crimson", new Color(0.863f, 0.078f, 0.235f));
        Red = addColor("Red", new Color(1.0f, 0.0f, 0.0f));
        FireBrick = addColor("FireBrick", new Color(0.698f, 0.133f, 0.133f));
        DarkRed = addColor("DarkRed", new Color(0.545f, 0.0f, 0.0f));
        Pink = addColor("Pink", new Color(1.0f, 0.753f, 0.796f));
        LightPink = addColor("LightPink", new Color(1.0f, 0.714f, 0.757f));
        HotPink = addColor("HotPink", new Color(1.0f, 0.412f, 0.706f));
        DeepPink = addColor("DeepPink", new Color(1.0f, 0.078f, 0.576f));
        MediumVioletRed = addColor("MediumVioletRed", new Color(0.78f, 0.082f, 0.522f));
        PaleVioletRed = addColor("PaleVioletRed", new Color(0.859f, 0.439f, 0.576f));
        Coral = addColor("Coral", new Color(1.0f, 0.498f, 0.314f));
        Tomato = addColor("Tomato", new Color(1.0f, 0.388f, 0.278f));
        OrangeRed = addColor("OrangeRed", new Color(1.0f, 0.271f, 0.0f));
        DarkOrange = addColor("DarkOrange", new Color(1.0f, 0.549f, 0.0f));
        Orange = addColor("Orange", new Color(1.0f, 0.647f, 0.0f));
        Gold = addColor("Gold", new Color(1.0f, 0.843f, 0.0f));
        Yellow = addColor("Yellow", new Color(1.0f, 1.0f, 0.0f));
        LightYellow = addColor("LightYellow", new Color(1.0f, 1.0f, 0.878f));
        LemonChiffon = addColor("LemonChiffon", new Color(1.0f, 0.98f, 0.804f));
        LightGoldenrodYellow = addColor("LightGoldenrodYellow", new Color(0.98f, 0.98f, 0.824f));
        PapayaWhip = addColor("PapayaWhip", new Color(1.0f, 0.937f, 0.835f));
        Moccasin = addColor("Moccasin", new Color(1.0f, 0.894f, 0.71f));
        PeachPu = addColor("PeachPu", new Color(1.0f, 0.855f, 0.725f));
        PaleGoldenrod = addColor("PaleGoldenrod", new Color(0.933f, 0.91f, 0.667f));
        Khaki = addColor("Khaki", new Color(0.941f, 0.902f, 0.549f));
        DarkKhaki = addColor("DarkKhaki", new Color(0.741f, 0.718f, 0.42f));
        Lavender = addColor("Lavender", new Color(0.902f, 0.902f, 0.98f));
        Thistle = addColor("Thistle", new Color(0.847f, 0.749f, 0.847f));
        Plum = addColor("Plum", new Color(0.867f, 0.627f, 0.867f));
        Violet = addColor("Violet", new Color(0.933f, 0.51f, 0.933f));
        Orchid = addColor("Orchid", new Color(0.855f, 0.439f, 0.839f));
        Fuchsia = addColor("Fuchsia", new Color(1.0f, 0.0f, 1.0f));
        Magenta = addColor("Magenta", new Color(1.0f, 0.0f, 1.0f));
        MediumOrchid = addColor("MediumOrchid", new Color(0.729f, 0.333f, 0.827f));
        MediumPurple = addColor("MediumPurple", new Color(0.576f, 0.439f, 0.859f));
        BlueViolet = addColor("BlueViolet", new Color(0.541f, 0.169f, 0.886f));
        DarkViolet = addColor("DarkViolet", new Color(0.58f, 0.0f, 0.827f));
        DarkOrchid = addColor("DarkOrchid", new Color(0.6f, 0.196f, 0.8f));
        DarkMagenta = addColor("DarkMagenta", new Color(0.545f, 0.0f, 0.545f));
        Purple = addColor("Purple", new Color(0.502f, 0.0f, 0.502f));
        Indigo = addColor("Indigo", new Color(0.294f, 0.0f, 0.51f));
        SlateBlue = addColor("SlateBlue", new Color(0.416f, 0.353f, 0.804f));
        DarkSlateBlue = addColor("DarkSlateBlue", new Color(0.282f, 0.239f, 0.545f));
        GreenYellow = addColor("GreenYellow", new Color(0.678f, 1.0f, 0.184f));
        Chartreuse = addColor("Chartreuse", new Color(0.498f, 1.0f, 0.0f));
        LawnGreen = addColor("LawnGreen", new Color(0.486f, 0.988f, 0.0f));
        Lime = addColor("Lime", new Color(0.0f, 1.0f, 0.0f));
        LimeGreen = addColor("LimeGreen", new Color(0.196f, 0.804f, 0.196f));
        PaleGreen = addColor("PaleGreen", new Color(0.596f, 0.984f, 0.596f));
        LightGreen = addColor("LightGreen", new Color(0.565f, 0.933f, 0.565f));
        MediumSpringGreen = addColor("MediumSpringGreen", new Color(0.0f, 0.98f, 0.604f));
        SpringGreen = addColor("SpringGreen", new Color(0.0f, 1.0f, 0.498f));
        MediumSeaGreen = addColor("MediumSeaGreen", new Color(0.235f, 0.702f, 0.443f));
        SeaGreen = addColor("SeaGreen", new Color(0.18f, 0.545f, 0.341f));
        ForestGreen = addColor("ForestGreen", new Color(0.133f, 0.545f, 0.133f));
        Green = addColor("Green", new Color(0.0f, 0.502f, 0.0f));
        DarkGreen = addColor("DarkGreen", new Color(0.0f, 0.392f, 0.0f));
        YellowGreen = addColor("YellowGreen", new Color(0.604f, 0.804f, 0.196f));
        OliveDrab = addColor("OliveDrab", new Color(0.42f, 0.557f, 0.137f));
        Olive = addColor("Olive", new Color(0.502f, 0.502f, 0.0f));
        DarkOliveGreen = addColor("DarkOliveGreen", new Color(0.333f, 0.42f, 0.184f));
        MediumAquamarine = addColor("MediumAquamarine", new Color(0.4f, 0.804f, 0.667f));
        DarkSeaGreen = addColor("DarkSeaGreen", new Color(0.561f, 0.737f, 0.561f));
        LightSeaGreen = addColor("LightSeaGreen", new Color(0.125f, 0.698f, 0.667f));
        DarkCyan = addColor("DarkCyan", new Color(0.0f, 0.545f, 0.545f));
        Teal = addColor("Teal", new Color(0.0f, 0.502f, 0.502f));
        Aqua = addColor("Aqua", new Color(0.0f, 1.0f, 1.0f));
        Cyan = addColor("Cyan", new Color(0.0f, 1.0f, 1.0f));
        LightCyan = addColor("LightCyan", new Color(0.878f, 1.0f, 1.0f));
        PaleTurquoise = addColor("PaleTurquoise", new Color(0.686f, 0.933f, 0.933f));
        Aquamarine = addColor("Aquamarine", new Color(0.498f, 1.0f, 0.831f));
        Turquoise = addColor("Turquoise", new Color(0.251f, 0.878f, 0.816f));
        MediumTurquoise = addColor("MediumTurquoise", new Color(0.282f, 0.82f, 0.8f));
        DarkTurquoise = addColor("DarkTurquoise", new Color(0.0f, 0.808f, 0.82f));
        CadetBlue = addColor("CadetBlue", new Color(0.373f, 0.62f, 0.627f));
        SteelBlue = addColor("SteelBlue", new Color(0.275f, 0.51f, 0.706f));
        LightSteelBlue = addColor("LightSteelBlue", new Color(0.69f, 0.769f, 0.871f));
        PowderBlue = addColor("PowderBlue", new Color(0.69f, 0.878f, 0.902f));
        LightBlue = addColor("LightBlue", new Color(0.678f, 0.847f, 0.902f));
        SkyBlue = addColor("SkyBlue", new Color(0.529f, 0.808f, 0.922f));
        LightSkyBlue = addColor("LightSkyBlue", new Color(0.529f, 0.808f, 0.98f));
        DeepSkyBlue = addColor("DeepSkyBlue", new Color(0.0f, 0.749f, 1.0f));
        DodgerBlue = addColor("DodgerBlue", new Color(0.118f, 0.565f, 1.0f));
        CornFlowerBlue = addColor("CornFlowerBlue", new Color(0.392f, 0.584f, 0.929f));
        MediumSlateBlue = addColor("MediumSlateBlue", new Color(0.482f, 0.408f, 0.933f));
        RoyalBlue = addColor("RoyalBlue", new Color(0.255f, 0.412f, 0.882f));
        Blue = addColor("Blue", new Color(0.0f, 0.0f, 1.0f));
        MediumBlue = addColor("MediumBlue", new Color(0.0f, 0.0f, 0.804f));
        DarkBlue = addColor("DarkBlue", new Color(0.0f, 0.0f, 0.545f));
        Navy = addColor("Navy", new Color(0.0f, 0.0f, 0.502f));
        MidnightBlue = addColor("MidnightBlue", new Color(0.098f, 0.098f, 0.439f));
        CornSilk = addColor("CornSilk", new Color(1.0f, 0.973f, 0.863f));
        BlanchedAlmond = addColor("BlanchedAlmond", new Color(1.0f, 0.922f, 0.804f));
        Bisque = addColor("Bisque", new Color(1.0f, 0.894f, 0.769f));
        NavajoWhite = addColor("NavajoWhite", new Color(1.0f, 0.871f, 0.678f));
        Wheat = addColor("Wheat", new Color(0.961f, 0.871f, 0.702f));
        BurlyWood = addColor("BurlyWood", new Color(0.871f, 0.722f, 0.529f));
        Tan = addColor("Tan", new Color(0.824f, 0.706f, 0.549f));
        RosyBrown = addColor("RosyBrown", new Color(0.737f, 0.561f, 0.561f));
        SandyBrown = addColor("SandyBrown", new Color(0.957f, 0.643f, 0.376f));
        Goldenrod = addColor("Goldenrod", new Color(0.855f, 0.647f, 0.125f));
        DarkGoldenrod = addColor("DarkGoldenrod", new Color(0.722f, 0.525f, 0.043f));
        Peru = addColor("Peru", new Color(0.804f, 0.522f, 0.247f));
        Chocolate = addColor("Chocolate", new Color(0.824f, 0.412f, 0.118f));
        SaddleBrown = addColor("SaddleBrown", new Color(0.545f, 0.271f, 0.075f));
        Sienna = addColor("Sienna", new Color(0.627f, 0.322f, 0.176f));
        Brown = addColor("Brown", new Color(0.647f, 0.165f, 0.165f));
        Maroon = addColor("Maroon", new Color(0.502f, 0.0f, 0.0f));
    }
}
