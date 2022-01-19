// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.fonts;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.asset.Asset;
import java.util.Iterator;
import java.io.IOException;
import java.util.Arrays;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.list.array.TShortArrayList;
import java.util.ArrayList;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import org.lwjgl.opengl.GL11;
import zombie.ZomboidFileSystem;
import zombie.core.textures.TextureID;
import zombie.util.StringUtils;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Map;
import zombie.core.Color;
import java.io.File;
import java.util.HashMap;
import zombie.core.textures.Texture;
import java.util.LinkedHashMap;
import zombie.asset.AssetStateObserver;

public final class AngelCodeFont implements Font, AssetStateObserver
{
    private static final int DISPLAY_LIST_CACHE_SIZE = 200;
    private static final int MAX_CHAR = 255;
    private int baseDisplayListID;
    public CharDef[] chars;
    private boolean displayListCaching;
    private DisplayList eldestDisplayList;
    private int eldestDisplayListID;
    private final LinkedHashMap displayLists;
    private Texture fontImage;
    private int lineHeight;
    private HashMap<Short, Texture> pages;
    private File fntFile;
    public static int xoff;
    public static int yoff;
    public static Color curCol;
    public static float curR;
    public static float curG;
    public static float curB;
    public static float curA;
    private static float s_scale;
    private static char[] data;
    
    public AngelCodeFont(final String s, final Texture fontImage) throws FileNotFoundException {
        this.baseDisplayListID = -1;
        this.displayListCaching = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry entry) {
                AngelCodeFont.this.eldestDisplayList = entry.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.pages = new HashMap<Short, Texture>();
        this.fontImage = fontImage;
        String substring = s;
        final FileInputStream fileInputStream = new FileInputStream(new File(substring));
        if (substring.startsWith("/")) {
            substring = substring.substring(1);
        }
        int index;
        while ((index = substring.indexOf("\\")) != -1) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring.substring(0, index), substring.substring(index + 1));
        }
        this.parseFnt(fileInputStream);
    }
    
    public AngelCodeFont(final String s, final String s2) throws FileNotFoundException {
        this.baseDisplayListID = -1;
        this.displayListCaching = false;
        this.displayLists = new LinkedHashMap(200, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry entry) {
                AngelCodeFont.this.eldestDisplayList = entry.getValue();
                AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
                return false;
            }
        };
        this.pages = new HashMap<Short, Texture>();
        if (!StringUtils.isNullOrWhitespace(s2)) {
            this.fontImage = Texture.getSharedTexture(s2, 0x0 | (TextureID.bUseCompression ? 4 : 0));
            if (this.fontImage != null && !this.fontImage.isReady()) {
                ((ArrayList<AngelCodeFont>)this.fontImage.getObserverCb()).add(this);
            }
        }
        String substring = s;
        if (substring.startsWith("/")) {
            substring = substring.substring(1);
        }
        int index;
        while ((index = substring.indexOf("\\")) != -1) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring.substring(0, index), substring.substring(index + 1));
        }
        this.fntFile = new File(ZomboidFileSystem.instance.getString(substring));
        this.parseFnt(new FileInputStream(ZomboidFileSystem.instance.getString(substring)));
    }
    
    @Override
    public void drawString(final float n, final float n2, final String s) {
        this.drawString(n, n2, s, Color.white);
    }
    
    @Override
    public void drawString(final float n, final float n2, final String s, final Color color) {
        this.drawString(n, n2, s, color, 0, s.length() - 1);
    }
    
    public void drawString(final float n, final float n2, final String s, final float n3, final float n4, final float n5, final float n6) {
        this.drawString(n, n2, s, n3, n4, n5, n6, 0, s.length() - 1);
    }
    
    public void drawString(final float n, final float n2, final float n3, final String s, final float n4, final float n5, final float n6, final float n7) {
        this.drawString(n, n2, n3, s, n4, n5, n6, n7, 0, s.length() - 1);
    }
    
    @Override
    public void drawString(final float n, final float n2, final String key, final Color color, final int n3, final int n4) {
        AngelCodeFont.xoff = (int)n;
        AngelCodeFont.yoff = (int)n2;
        AngelCodeFont.curR = color.r;
        AngelCodeFont.curG = color.g;
        AngelCodeFont.curB = color.b;
        AngelCodeFont.curA = color.a;
        AngelCodeFont.s_scale = 0.0f;
        Texture.lr = color.r;
        Texture.lg = color.g;
        Texture.lb = color.b;
        Texture.la = color.a;
        if (this.displayListCaching && n3 == 0 && n4 == key.length() - 1) {
            final DisplayList list = this.displayLists.get(key);
            if (list != null) {
                GL11.glCallList(list.id);
            }
            else {
                final DisplayList value = new DisplayList();
                value.text = key;
                final int size = this.displayLists.size();
                if (size < 200) {
                    value.id = this.baseDisplayListID + size;
                }
                else {
                    value.id = this.eldestDisplayListID;
                    this.displayLists.remove(this.eldestDisplayList.text);
                }
                this.displayLists.put(key, value);
                GL11.glNewList(value.id, 4865);
                this.render(key, n3, n4);
                GL11.glEndList();
            }
        }
        else {
            this.render(key, n3, n4);
        }
    }
    
    public void drawString(final float n, final float n2, final String s, final float n3, final float n4, final float n5, final float n6, final int n7, final int n8) {
        this.drawString(n, n2, 0.0f, s, n3, n4, n5, n6, n7, n8);
    }
    
    public void drawString(final float n, final float n2, final float s_scale, final String key, final float n3, final float n4, final float n5, final float n6, final int n7, final int n8) {
        AngelCodeFont.xoff = (int)n;
        AngelCodeFont.yoff = (int)n2;
        AngelCodeFont.curR = n3;
        AngelCodeFont.curG = n4;
        AngelCodeFont.curB = n5;
        AngelCodeFont.curA = n6;
        AngelCodeFont.s_scale = s_scale;
        Texture.lr = n3;
        Texture.lg = n4;
        Texture.lb = n5;
        Texture.la = n6;
        if (this.displayListCaching && n7 == 0 && n8 == key.length() - 1) {
            final DisplayList list = this.displayLists.get(key);
            if (list != null) {
                GL11.glCallList(list.id);
            }
            else {
                final DisplayList value = new DisplayList();
                value.text = key;
                final int size = this.displayLists.size();
                if (size < 200) {
                    value.id = this.baseDisplayListID + size;
                }
                else {
                    value.id = this.eldestDisplayListID;
                    this.displayLists.remove(this.eldestDisplayList.text);
                }
                this.displayLists.put(key, value);
                GL11.glNewList(value.id, 4865);
                this.render(key, n7, n8);
                GL11.glEndList();
            }
        }
        else {
            this.render(key, n7, n8);
        }
    }
    
    @Override
    public int getHeight(final String key) {
        DisplayList list = null;
        if (this.displayListCaching) {
            list = this.displayLists.get(key);
            if (list != null && list.height != null) {
                return list.height;
            }
        }
        int n = 1;
        int max = 0;
        for (int i = 0; i < key.length(); ++i) {
            final char char1 = key.charAt(i);
            if (char1 == '\n') {
                ++n;
                max = 0;
            }
            else if (char1 != ' ') {
                if (char1 < this.chars.length) {
                    final CharDef charDef = this.chars[char1];
                    if (charDef != null) {
                        max = Math.max(charDef.height + charDef.yoffset, max);
                    }
                }
            }
        }
        final int n2 = n * this.getLineHeight();
        if (list != null) {
            list.height = new Short((short)n2);
        }
        return n2;
    }
    
    @Override
    public int getLineHeight() {
        return this.lineHeight;
    }
    
    @Override
    public int getWidth(final String s) {
        return this.getWidth(s, 0, s.length() - 1, false);
    }
    
    @Override
    public int getWidth(final String s, final boolean b) {
        return this.getWidth(s, 0, s.length() - 1, b);
    }
    
    @Override
    public int getWidth(final String s, final int n, final int n2) {
        return this.getWidth(s, n, n2, false);
    }
    
    @Override
    public int getWidth(final String key, final int n, final int n2, final boolean b) {
        DisplayList list = null;
        if (this.displayListCaching && n == 0 && n2 == key.length() - 1) {
            list = this.displayLists.get(key);
            if (list != null && list.width != null) {
                return list.width;
            }
        }
        final int n3 = n2 - n + 1;
        int max = 0;
        int b2 = 0;
        CharDef charDef = null;
        for (int i = 0; i < n3; ++i) {
            final char char1 = key.charAt(n + i);
            if (char1 == '\n') {
                b2 = 0;
            }
            else if (char1 < this.chars.length) {
                final CharDef charDef2 = this.chars[char1];
                if (charDef2 != null) {
                    if (charDef != null) {
                        b2 += charDef.getKerning(char1);
                    }
                    charDef = charDef2;
                    if (b || i < n3 - 1) {
                        b2 += charDef2.xadvance;
                    }
                    else {
                        b2 += charDef2.width;
                    }
                    max = Math.max(max, b2);
                }
            }
        }
        if (list != null) {
            list.width = new Short((short)max);
        }
        return max;
    }
    
    public int getYOffset(final String key) {
        DisplayList list = null;
        if (this.displayListCaching) {
            list = this.displayLists.get(key);
            if (list != null && list.yOffset != null) {
                return list.yOffset;
            }
        }
        int n = key.indexOf(10);
        if (n == -1) {
            n = key.length();
        }
        int min = 10000;
        for (int i = 0; i < n; ++i) {
            final CharDef charDef = this.chars[key.charAt(i)];
            if (charDef != null) {
                min = Math.min(charDef.yoffset, min);
            }
        }
        if (list != null) {
            list.yOffset = new Short((short)min);
        }
        return min;
    }
    
    private CharDef parseChar(final String str) {
        final CharDef charDef = new CharDef();
        final StringTokenizer stringTokenizer = new StringTokenizer(str, " =");
        stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        charDef.id = Integer.parseInt(stringTokenizer.nextToken());
        if (charDef.id < 0) {
            return null;
        }
        if (charDef.id > 255) {}
        stringTokenizer.nextToken();
        charDef.x = Short.parseShort(stringTokenizer.nextToken());
        stringTokenizer.nextToken();
        charDef.y = Short.parseShort(stringTokenizer.nextToken());
        stringTokenizer.nextToken();
        charDef.width = Short.parseShort(stringTokenizer.nextToken());
        stringTokenizer.nextToken();
        charDef.height = Short.parseShort(stringTokenizer.nextToken());
        stringTokenizer.nextToken();
        charDef.xoffset = Short.parseShort(stringTokenizer.nextToken());
        stringTokenizer.nextToken();
        charDef.yoffset = Short.parseShort(stringTokenizer.nextToken());
        stringTokenizer.nextToken();
        charDef.xadvance = Short.parseShort(stringTokenizer.nextToken());
        stringTokenizer.nextToken();
        charDef.page = Short.parseShort(stringTokenizer.nextToken());
        Texture fontImage = this.fontImage;
        if (this.pages.containsKey(charDef.page)) {
            fontImage = this.pages.get(charDef.page);
        }
        if (fontImage != null && fontImage.isReady()) {
            charDef.init();
        }
        if (charDef.id != 32) {
            this.lineHeight = Math.max(charDef.height + charDef.yoffset, this.lineHeight);
        }
        return charDef;
    }
    
    private void parseFnt(final InputStream in) {
        if (this.displayListCaching) {
            this.baseDisplayListID = GL11.glGenLists(200);
            if (this.baseDisplayListID == 0) {
                this.displayListCaching = false;
            }
        }
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            bufferedReader.readLine();
            bufferedReader.readLine();
            final TShortObjectHashMap tShortObjectHashMap = new TShortObjectHashMap(64);
            final ArrayList<CharDef> list = new ArrayList<CharDef>(255);
            int max = 0;
            int i = 0;
            while (i == 0) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    i = 1;
                }
                else {
                    if (line.startsWith("page")) {
                        final StringTokenizer stringTokenizer = new StringTokenizer(line, " =");
                        stringTokenizer.nextToken();
                        stringTokenizer.nextToken();
                        final short short1 = Short.parseShort(stringTokenizer.nextToken());
                        stringTokenizer.nextToken();
                        final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;CLjava/lang/String;)Ljava/lang/String;, this.fntFile.getParent(), File.separatorChar, stringTokenizer.nextToken().replace("\"", "")).replace("\\", "/");
                        final Texture sharedTexture = Texture.getSharedTexture(replace, 0x0 | (TextureID.bUseCompression ? 4 : 0));
                        if (sharedTexture == null) {
                            System.out.println(invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, short1, replace));
                        }
                        else {
                            this.pages.put(short1, sharedTexture);
                            if (!sharedTexture.isReady()) {
                                ((ArrayList<AngelCodeFont>)sharedTexture.getObserverCb()).add(this);
                            }
                        }
                    }
                    if (!line.startsWith("chars c")) {
                        if (line.startsWith("char")) {
                            final CharDef char1 = this.parseChar(line);
                            if (char1 != null) {
                                max = Math.max(max, char1.id);
                                list.add(char1);
                            }
                        }
                    }
                    if (line.startsWith("kernings c")) {
                        continue;
                    }
                    if (!line.startsWith("kerning")) {
                        continue;
                    }
                    final StringTokenizer stringTokenizer2 = new StringTokenizer(line, " =");
                    stringTokenizer2.nextToken();
                    stringTokenizer2.nextToken();
                    final short short2 = Short.parseShort(stringTokenizer2.nextToken());
                    stringTokenizer2.nextToken();
                    final int int1 = Integer.parseInt(stringTokenizer2.nextToken());
                    stringTokenizer2.nextToken();
                    final int int2 = Integer.parseInt(stringTokenizer2.nextToken());
                    TShortArrayList list2 = (TShortArrayList)tShortObjectHashMap.get(short2);
                    if (list2 == null) {
                        list2 = new TShortArrayList();
                        tShortObjectHashMap.put(short2, (Object)list2);
                    }
                    list2.add((short)int1);
                    list2.add((short)int2);
                }
            }
            this.chars = new CharDef[max + 1];
            for (final CharDef charDef : list) {
                this.chars[charDef.id] = charDef;
            }
            tShortObjectHashMap.forEachEntry((TShortObjectProcedure)new TShortObjectProcedure<TShortArrayList>() {
                public boolean execute(final short n, final TShortArrayList list) {
                    final CharDef charDef = AngelCodeFont.this.chars[n];
                    charDef.kerningSecond = new short[list.size() / 2];
                    charDef.kerningAmount = new short[list.size() / 2];
                    int n2 = 0;
                    for (int i = 0; i < list.size(); i += 2) {
                        charDef.kerningSecond[n2] = list.get(i);
                        charDef.kerningAmount[n2] = list.get(i + 1);
                        ++n2;
                    }
                    final short[] copy = Arrays.copyOf(charDef.kerningSecond, charDef.kerningSecond.length);
                    final short[] copy2 = Arrays.copyOf(charDef.kerningAmount, charDef.kerningAmount.length);
                    Arrays.sort(copy);
                    for (int j = 0; j < copy.length; ++j) {
                        for (int k = 0; k < charDef.kerningSecond.length; ++k) {
                            if (charDef.kerningSecond[k] == copy[j]) {
                                charDef.kerningAmount[j] = copy2[k];
                                break;
                            }
                        }
                    }
                    charDef.kerningSecond = copy;
                    return true;
                }
            });
            bufferedReader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void render(final String s, final int srcBegin, int srcEnd) {
        final int n = ++srcEnd - srcBegin;
        float n2 = 0.0f;
        float n3 = 0.0f;
        CharDef charDef = null;
        if (AngelCodeFont.data.length < n) {
            AngelCodeFont.data = new char[(n + 128 - 1) / 128 * 128];
        }
        s.getChars(srcBegin, srcEnd, AngelCodeFont.data, 0);
        for (int i = 0; i < n; ++i) {
            final char c = AngelCodeFont.data[i];
            if (c == '\n') {
                n2 = 0.0f;
                n3 += this.getLineHeight();
            }
            else if (c < this.chars.length) {
                final CharDef charDef2 = this.chars[c];
                if (charDef2 != null) {
                    if (charDef != null) {
                        if (AngelCodeFont.s_scale > 0.0f) {
                            n2 += charDef.getKerning(c) * AngelCodeFont.s_scale;
                        }
                        else {
                            n2 += charDef.getKerning(c);
                        }
                    }
                    charDef = charDef2;
                    charDef2.draw(n2, n3);
                    if (AngelCodeFont.s_scale > 0.0f) {
                        n2 += charDef2.xadvance * AngelCodeFont.s_scale;
                    }
                    else {
                        n2 += charDef2.xadvance;
                    }
                }
            }
        }
    }
    
    @Override
    public void onStateChanged(final Asset.State state, final Asset.State state2, final Asset value) {
        if (value != this.fontImage && !this.pages.containsValue(value)) {
            return;
        }
        if (state2 != Asset.State.READY) {
            return;
        }
        for (final CharDef charDef : this.chars) {
            if (charDef != null) {
                if (charDef.image == null) {
                    Texture fontImage = this.fontImage;
                    if (this.pages.containsKey(charDef.page)) {
                        fontImage = this.pages.get(charDef.page);
                    }
                    if (value == fontImage) {
                        charDef.init();
                    }
                }
            }
        }
    }
    
    public boolean isEmpty() {
        if (this.fontImage != null && this.fontImage.isEmpty()) {
            return true;
        }
        final Iterator<Texture> iterator = this.pages.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public void destroy() {
        for (final CharDef charDef : this.chars) {
            if (charDef != null) {
                charDef.destroy();
            }
        }
        Arrays.fill(this.chars, null);
        this.pages.clear();
    }
    
    static {
        AngelCodeFont.xoff = 0;
        AngelCodeFont.yoff = 0;
        AngelCodeFont.curCol = null;
        AngelCodeFont.curR = 0.0f;
        AngelCodeFont.curG = 0.0f;
        AngelCodeFont.curB = 0.0f;
        AngelCodeFont.curA = 0.0f;
        AngelCodeFont.s_scale = 0.0f;
        AngelCodeFont.data = new char[256];
    }
    
    public static final class CharDefTexture extends Texture
    {
        public CharDefTexture(final TextureID textureID, final String s) {
            super(textureID, s);
        }
        
        public void releaseCharDef() {
            this.removeDependency(this.dataid);
        }
    }
    
    public class CharDef
    {
        public short dlIndex;
        public short height;
        public int id;
        public Texture image;
        public short[] kerningSecond;
        public short[] kerningAmount;
        public short width;
        public short x;
        public short xadvance;
        public short xoffset;
        public short y;
        public short yoffset;
        public short page;
        
        public void draw(final float n, final float n2) {
            final Texture image = this.image;
            if (AngelCodeFont.s_scale > 0.0f) {
                SpriteRenderer.instance.m_states.getPopulatingActiveState().render(image, n + this.xoffset * AngelCodeFont.s_scale + AngelCodeFont.xoff, n2 + this.yoffset * AngelCodeFont.s_scale + AngelCodeFont.yoff, this.width * AngelCodeFont.s_scale, this.height * AngelCodeFont.s_scale, AngelCodeFont.curR, AngelCodeFont.curG, AngelCodeFont.curB, AngelCodeFont.curA, null);
            }
            else {
                SpriteRenderer.instance.renderi(image, (int)(n + this.xoffset + AngelCodeFont.xoff), (int)(n2 + this.yoffset + AngelCodeFont.yoff), this.width, this.height, AngelCodeFont.curR, AngelCodeFont.curG, AngelCodeFont.curB, AngelCodeFont.curA, null);
            }
        }
        
        public int getKerning(final int n) {
            if (this.kerningSecond == null) {
                return 0;
            }
            int i = 0;
            int n2 = this.kerningSecond.length - 1;
            while (i <= n2) {
                final int n3 = i + n2 >>> 1;
                if (this.kerningSecond[n3] < n) {
                    i = n3 + 1;
                }
                else {
                    if (this.kerningSecond[n3] <= n) {
                        return this.kerningAmount[n3];
                    }
                    n2 = n3 - 1;
                }
            }
            return 0;
        }
        
        public void init() {
            Texture fontImage = AngelCodeFont.this.fontImage;
            if (AngelCodeFont.this.pages.containsKey(this.page)) {
                fontImage = AngelCodeFont.this.pages.get(this.page);
            }
            (this.image = new CharDefTexture(fontImage.getTextureId(), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SS)Ljava/lang/String;, fontImage.getName(), this.x, this.y))).setRegion(this.x + (int)(fontImage.xStart * fontImage.getWidthHW()), this.y + (int)(fontImage.yStart * fontImage.getHeightHW()), this.width, this.height);
        }
        
        public void destroy() {
            if (this.image != null && this.image.getTextureId() != null) {
                ((CharDefTexture)this.image).releaseCharDef();
                this.image = null;
            }
        }
        
        @Override
        public String toString() {
            return invokedynamic(makeConcatWithConstants:(ISS)Ljava/lang/String;, this.id, this.x, this.y);
        }
    }
    
    private static class DisplayList
    {
        Short height;
        int id;
        String text;
        Short width;
        Short yOffset;
    }
}
