// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.StorySounds;

import zombie.iso.Vector2;
import zombie.core.Core;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.core.Rand;
import java.util.Iterator;
import zombie.input.GameKeyboard;
import java.util.Map;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.ArrayList;
import java.util.HashMap;

public final class SLSoundManager
{
    public static boolean ENABLED;
    public static boolean DEBUG;
    public static boolean LUA_DEBUG;
    public static StoryEmitter Emitter;
    private static SLSoundManager instance;
    private HashMap<Integer, Boolean> state;
    private ArrayList<StorySound> storySounds;
    private int nextTick;
    private float borderCenterX;
    private float borderCenterY;
    private float borderRadiusMin;
    private float borderRadiusMax;
    private float borderScale;
    
    public static SLSoundManager getInstance() {
        if (SLSoundManager.instance == null) {
            SLSoundManager.instance = new SLSoundManager();
        }
        return SLSoundManager.instance;
    }
    
    private SLSoundManager() {
        this.state = new HashMap<Integer, Boolean>();
        this.storySounds = new ArrayList<StorySound>();
        this.nextTick = 0;
        this.borderCenterX = 10500.0f;
        this.borderCenterY = 9000.0f;
        this.borderRadiusMin = 12000.0f;
        this.borderRadiusMax = 16000.0f;
        this.borderScale = 1.0f;
        this.state.put(12, false);
        this.state.put(13, false);
    }
    
    public boolean getDebug() {
        return SLSoundManager.DEBUG;
    }
    
    public boolean getLuaDebug() {
        return SLSoundManager.LUA_DEBUG;
    }
    
    public ArrayList<StorySound> getStorySounds() {
        return this.storySounds;
    }
    
    public void print(final String x) {
        if (SLSoundManager.DEBUG) {
            System.out.println(x);
        }
    }
    
    public void init() {
        this.loadSounds();
    }
    
    public void loadSounds() {
        this.storySounds.clear();
        try {
            final File mediaFile = ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, File.separator));
            if (mediaFile.exists() && mediaFile.isDirectory()) {
                final File[] listFiles = mediaFile.listFiles();
                for (int i = 0; i < listFiles.length; ++i) {
                    if (listFiles[i].isFile()) {
                        final String name = listFiles[i].getName();
                        if (name.lastIndexOf(".") != -1 && name.lastIndexOf(".") != 0 && name.substring(name.lastIndexOf(".") + 1).equals("ogg")) {
                            final String substring = name.substring(0, name.lastIndexOf("."));
                            this.print(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, substring));
                            this.addStorySound(new StorySound(substring, 1.0f));
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }
    
    private void addStorySound(final StorySound e) {
        this.storySounds.add(e);
    }
    
    public void updateKeys() {
        for (final Map.Entry<Integer, Boolean> entry : this.state.entrySet()) {
            final boolean keyDown = GameKeyboard.isKeyDown(entry.getKey());
            if (keyDown && entry.getValue() != keyDown) {
                switch (entry.getKey()) {
                    case 13: {
                        SLSoundManager.Emitter.coordinate3D = !SLSoundManager.Emitter.coordinate3D;
                    }
                }
            }
            entry.setValue(keyDown);
        }
    }
    
    public void update(final int n, final int n2, final int n3) {
        this.updateKeys();
        SLSoundManager.Emitter.tick();
    }
    
    public void thunderTest() {
        --this.nextTick;
        if (this.nextTick <= 0) {
            this.nextTick = Rand.Next(10, 180);
            final float next = Rand.Next(0.0f, 8000.0f);
            final double n = Math.random() * 3.141592653589793 * 2.0;
            final float n2 = this.borderCenterX + (float)(Math.cos(n) * next);
            final float n3 = this.borderCenterY + (float)(Math.sin(n) * next);
            if (Rand.Next(0, 100) < 60) {
                SLSoundManager.Emitter.playSound("thunder", 1.0f, n2, n3, 0.0f, 100.0f, 8500.0f);
            }
            else {
                SLSoundManager.Emitter.playSound("thundereffect", 1.0f, n2, n3, 0.0f, 100.0f, 8500.0f);
            }
        }
    }
    
    public void render() {
        this.renderDebug();
    }
    
    public void renderDebug() {
        if (SLSoundManager.DEBUG) {
            final String s = SLSoundManager.Emitter.coordinate3D ? "3D coordinates, X-Z-Y" : "2D coordinates X-Y-Z";
            final int n = TextManager.instance.MeasureStringX(UIFont.Large, s) / 2;
            TextManager.instance.MeasureStringY(UIFont.Large, s);
            this.renderLine(UIFont.Large, s, Core.getInstance().getScreenWidth() / 2 - n, Core.getInstance().getScreenHeight() / 2);
        }
    }
    
    private void renderLine(final UIFont uiFont, final String s, final int n, final int n2) {
        TextManager.instance.DrawString(uiFont, n + 1, n2 + 1, s, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uiFont, n - 1, n2 - 1, s, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uiFont, n + 1, n2 - 1, s, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uiFont, n - 1, n2 + 1, s, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uiFont, n, n2, s, 1.0, 1.0, 1.0, 1.0);
    }
    
    public Vector2 getRandomBorderPosition() {
        final float next = Rand.Next(this.borderRadiusMin * this.borderScale, this.borderRadiusMax * this.borderScale);
        final double n = Math.random() * 3.141592653589793 * 2.0;
        return new Vector2(this.borderCenterX + (float)(Math.cos(n) * next), this.borderCenterY + (float)(Math.sin(n) * next));
    }
    
    public float getRandomBorderRange() {
        return Rand.Next(this.borderRadiusMin * this.borderScale * 1.5f, this.borderRadiusMax * this.borderScale * 1.5f);
    }
    
    static {
        SLSoundManager.ENABLED = false;
        SLSoundManager.DEBUG = false;
        SLSoundManager.LUA_DEBUG = false;
        SLSoundManager.Emitter = new StoryEmitter();
    }
}
