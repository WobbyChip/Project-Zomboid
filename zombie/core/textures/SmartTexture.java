// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.util.List;
import zombie.core.logger.ExceptionLogger;
import zombie.util.list.PZArrayUtil;
import zombie.debug.DebugLog;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.utils.WrappedBuffer;
import zombie.util.Lambda;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.characterTextures.CharacterSmartTexture;
import zombie.characterTextures.BloodBodyPartType;
import java.util.HashMap;
import zombie.core.opengl.SmartShader;
import java.util.ArrayList;

public class SmartTexture extends Texture
{
    public final ArrayList<TextureCombinerCommand> commands;
    public Texture result;
    private boolean dirty;
    private static SmartShader hue;
    private static SmartShader tint;
    private static SmartShader masked;
    private static SmartShader dirtMask;
    private final HashMap<Integer, ArrayList<Integer>> categoryMap;
    private static SmartShader bodyMask;
    private static SmartShader bodyMaskTint;
    private static SmartShader bodyMaskHue;
    private static final ArrayList<TextureCombinerShaderParam> bodyMaskParams;
    private static SmartShader addHole;
    private static final ArrayList<TextureCombinerShaderParam> addHoleParams;
    private static SmartShader removeHole;
    private static final ArrayList<TextureCombinerShaderParam> removeHoleParams;
    private static SmartShader blit;
    
    public SmartTexture() {
        this.commands = new ArrayList<TextureCombinerCommand>();
        this.dirty = true;
        this.categoryMap = new HashMap<Integer, ArrayList<Integer>>();
        this.name = "SmartTexture";
    }
    
    void addToCat(final int i) {
        ArrayList<Integer> value;
        if (!this.categoryMap.containsKey(i)) {
            value = new ArrayList<Integer>();
            this.categoryMap.put(i, value);
        }
        else {
            value = this.categoryMap.get(i);
        }
        value.add(this.commands.size());
    }
    
    public TextureCombinerCommand getFirstFromCategory(final int n) {
        if (!this.categoryMap.containsKey(n)) {
            return null;
        }
        return this.commands.get(this.categoryMap.get(n).get(0));
    }
    
    public void addOverlayPatches(final String s, final String s2, final int n) {
        if (SmartTexture.blit == null) {
            this.create();
        }
        this.addToCat(n);
        this.add(s, SmartTexture.blit, s2, new ArrayList<TextureCombinerShaderParam>(), 770, 771);
    }
    
    public void addOverlay(final String s, final String s2, final float n, final int n2) {
        if (SmartTexture.masked == null) {
            this.create();
        }
        this.addToCat(n2);
        final ArrayList<TextureCombinerShaderParam> list = new ArrayList<TextureCombinerShaderParam>();
        list.add(new TextureCombinerShaderParam("intensity", n));
        list.add(new TextureCombinerShaderParam("bloodDark", 0.5f, 0.5f));
        this.add(s, SmartTexture.masked, s2, list, 774, 771);
    }
    
    public void addDirtOverlay(final String s, final String s2, final float n, final int n2) {
        if (SmartTexture.dirtMask == null) {
            this.create();
        }
        this.addToCat(n2);
        final ArrayList<TextureCombinerShaderParam> list = new ArrayList<TextureCombinerShaderParam>();
        list.add(new TextureCombinerShaderParam("intensity", n));
        this.add(s, SmartTexture.dirtMask, s2, list, 774, 771);
    }
    
    public void addOverlay(final String s) {
        if (SmartTexture.tint == null) {
            this.create();
        }
        this.add(s, 774, 771);
    }
    
    public void addRect(final String s, final int n, final int n2, final int n3, final int n4) {
        this.commands.add(TextureCombinerCommand.get().init(Texture.getSharedTexture(s), n, n2, n3, n4));
        this.dirty = true;
    }
    
    @Override
    public void destroy() {
        if (this.result != null) {
            TextureCombiner.instance.releaseTexture(this.result);
        }
        this.clear();
        this.dirty = false;
    }
    
    public void addTint(final String s, final int n, final float n2, final float n3, final float n4) {
        this.addTint(Texture.getSharedTexture(s), n, n2, n3, n4);
    }
    
    public void addTint(final Texture texture, final int n, final float n2, final float n3, final float n4) {
        if (SmartTexture.tint == null) {
            this.create();
        }
        this.addToCat(n);
        final ArrayList<TextureCombinerShaderParam> list = new ArrayList<TextureCombinerShaderParam>();
        list.add(new TextureCombinerShaderParam("R", n2));
        list.add(new TextureCombinerShaderParam("G", n3));
        list.add(new TextureCombinerShaderParam("B", n4));
        this.add(texture, SmartTexture.tint, list);
    }
    
    public void addHue(final String s, final int n, final float n2) {
        this.addHue(Texture.getSharedTexture(s), n, n2);
    }
    
    public void addHue(final Texture texture, final int n, final float n2) {
        if (SmartTexture.hue == null) {
            this.create();
        }
        this.addToCat(n);
        final ArrayList<TextureCombinerShaderParam> list = new ArrayList<TextureCombinerShaderParam>();
        list.add(new TextureCombinerShaderParam("HueChange", n2));
        this.add(texture, SmartTexture.hue, list);
    }
    
    public Texture addHole(final BloodBodyPartType bloodBodyPartType) {
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()]);
        if (SmartTexture.addHole == null) {
            this.create();
        }
        this.addToCat(CharacterSmartTexture.ClothingItemCategory);
        this.calculate();
        final Texture result = this.result;
        this.clear();
        this.result = null;
        this.commands.add(TextureCombinerCommand.get().init(result, SmartTexture.addHole, SmartTexture.addHoleParams, Texture.getSharedTexture(s), 770, 0));
        this.dirty = true;
        return result;
    }
    
    public void removeHole(final String s, final BloodBodyPartType bloodBodyPartType) {
        this.removeHole(Texture.getSharedTexture(s), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()])), bloodBodyPartType);
    }
    
    public void removeHole(final Texture texture, final BloodBodyPartType bloodBodyPartType) {
        this.removeHole(texture, Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()])), bloodBodyPartType);
    }
    
    public void removeHole(final Texture texture, final Texture texture2, final BloodBodyPartType bloodBodyPartType) {
        if (SmartTexture.removeHole == null) {
            this.create();
        }
        this.addToCat(CharacterSmartTexture.ClothingItemCategory);
        this.commands.add(TextureCombinerCommand.get().init(texture, SmartTexture.removeHole, SmartTexture.removeHoleParams, texture2, 770, 771));
        this.dirty = true;
    }
    
    public void mask(final String s, final String s2, final int n) {
        this.mask(Texture.getSharedTexture(s), Texture.getSharedTexture(s2), n);
    }
    
    public void mask(final Texture texture, final Texture texture2, final int n) {
        if (SmartTexture.bodyMask == null) {
            this.create();
        }
        this.addToCat(n);
        this.commands.add(TextureCombinerCommand.get().init(texture, SmartTexture.bodyMask, SmartTexture.bodyMaskParams, texture2, 770, 771));
        this.dirty = true;
    }
    
    public void maskHue(final String s, final String s2, final int n, final float n2) {
        this.maskHue(Texture.getSharedTexture(s), Texture.getSharedTexture(s2), n, n2);
    }
    
    public void maskHue(final Texture texture, final Texture texture2, final int n, final float n2) {
        if (SmartTexture.bodyMask == null) {
            this.create();
        }
        this.addToCat(n);
        final ArrayList<TextureCombinerShaderParam> list = new ArrayList<TextureCombinerShaderParam>();
        list.add(new TextureCombinerShaderParam("HueChange", n2));
        this.commands.add(TextureCombinerCommand.get().init(texture, SmartTexture.bodyMaskHue, list, texture2, 770, 771));
        this.dirty = true;
    }
    
    public void maskTint(final String s, final String s2, final int n, final float n2, final float n3, final float n4) {
        this.maskTint(Texture.getSharedTexture(s), Texture.getSharedTexture(s2), n, n2, n3, n4);
    }
    
    public void maskTint(final Texture texture, final Texture texture2, final int n, final float n2, final float n3, final float n4) {
        if (SmartTexture.bodyMask == null) {
            this.create();
        }
        this.addToCat(n);
        final ArrayList<TextureCombinerShaderParam> list = new ArrayList<TextureCombinerShaderParam>();
        list.add(new TextureCombinerShaderParam("R", n2));
        list.add(new TextureCombinerShaderParam("G", n3));
        list.add(new TextureCombinerShaderParam("B", n4));
        this.commands.add(TextureCombinerCommand.get().init(texture, SmartTexture.bodyMaskTint, list, texture2, 770, 771));
        this.dirty = true;
    }
    
    public void addMaskedTexture(final CharacterMask characterMask, final String s, final String s2, final int n, final ImmutableColor immutableColor, final float n2) {
        addMaskedTexture(this, characterMask, s, Texture.getSharedTexture(s2), n, immutableColor, n2);
    }
    
    public void addMaskedTexture(final CharacterMask characterMask, final String s, final Texture texture, final int n, final ImmutableColor immutableColor, final float n2) {
        addMaskedTexture(this, characterMask, s, texture, n, immutableColor, n2);
    }
    
    private static void addMaskFlags(final SmartTexture smartTexture, final CharacterMask characterMask, final String s, final Texture texture, final int i) {
        characterMask.forEachVisible(Lambda.consumer(smartTexture, s, texture, i, (part, smartTexture2, s2, texture2, n) -> smartTexture2.mask(texture2, Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/core/skinnedmodel/model/CharacterMask$Part;)Ljava/lang/String;, s2, part)), n)));
    }
    
    private static void addMaskFlagsHue(final SmartTexture smartTexture, final CharacterMask characterMask, final String s, final Texture texture, final int i, final float f) {
        characterMask.forEachVisible(Lambda.consumer(smartTexture, s, texture, i, f, (part, smartTexture2, s2, texture2, n, n2) -> smartTexture2.maskHue(texture2, Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/core/skinnedmodel/model/CharacterMask$Part;)Ljava/lang/String;, s2, part)), n, n2)));
    }
    
    private static void addMaskFlagsTint(final SmartTexture smartTexture, final CharacterMask characterMask, final String s, final Texture texture, final int i, final ImmutableColor immutableColor) {
        characterMask.forEachVisible(Lambda.consumer(smartTexture, s, texture, i, immutableColor, (part, smartTexture2, s2, texture2, n, immutableColor2) -> smartTexture2.maskTint(texture2, Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/core/skinnedmodel/model/CharacterMask$Part;)Ljava/lang/String;, s2, part)), n, immutableColor2.r, immutableColor2.g, immutableColor2.b)));
    }
    
    private static void addMaskedTexture(final SmartTexture smartTexture, final CharacterMask characterMask, final String s, final Texture texture, final int n, final ImmutableColor immutableColor, final float n2) {
        if (characterMask.isNothingVisible()) {
            return;
        }
        if (characterMask.isAllVisible()) {
            if (!ImmutableColor.white.equals(immutableColor)) {
                smartTexture.addTint(texture, n, immutableColor.r, immutableColor.g, immutableColor.b);
            }
            else if (n2 < -1.0E-4f || n2 > 1.0E-4f) {
                smartTexture.addHue(texture, n, n2);
            }
            else {
                smartTexture.add(texture);
            }
            return;
        }
        if (!ImmutableColor.white.equals(immutableColor)) {
            addMaskFlagsTint(smartTexture, characterMask, s, texture, n, immutableColor);
        }
        else if (n2 < -1.0E-4f || n2 > 1.0E-4f) {
            addMaskFlagsHue(smartTexture, characterMask, s, texture, n, n2);
        }
        else {
            addMaskFlags(smartTexture, characterMask, s, texture, n);
        }
    }
    
    public void addTexture(final String s, final int n, final ImmutableColor immutableColor, final float n2) {
        addTexture(this, s, n, immutableColor, n2);
    }
    
    private static void addTexture(final SmartTexture smartTexture, final String s, final int n, final ImmutableColor immutableColor, final float n2) {
        if (!ImmutableColor.white.equals(immutableColor)) {
            smartTexture.addTint(s, n, immutableColor.r, immutableColor.g, immutableColor.b);
        }
        else if (n2 < -1.0E-4f || n2 > 1.0E-4f) {
            smartTexture.addHue(s, n, n2);
        }
        else {
            smartTexture.add(s);
        }
    }
    
    private void create() {
        SmartTexture.tint = new SmartShader("hueChange");
        SmartTexture.hue = new SmartShader("hueChange");
        SmartTexture.masked = new SmartShader("overlayMask");
        SmartTexture.dirtMask = new SmartShader("dirtMask");
        SmartTexture.bodyMask = new SmartShader("bodyMask");
        SmartTexture.bodyMaskHue = new SmartShader("bodyMaskHue");
        SmartTexture.bodyMaskTint = new SmartShader("bodyMaskTint");
        SmartTexture.addHole = new SmartShader("addHole");
        SmartTexture.removeHole = new SmartShader("removeHole");
        SmartTexture.blit = new SmartShader("blit");
    }
    
    @Override
    public WrappedBuffer getData() {
        synchronized (this) {
            if (this.dirty) {
                this.calculate();
            }
            return this.result.dataid.getData();
        }
    }
    
    @Override
    public synchronized void bind() {
        if (this.dirty) {
            this.calculate();
        }
        this.result.bind(3553);
    }
    
    @Override
    public int getID() {
        synchronized (this) {
            if (this.dirty) {
                this.calculate();
            }
        }
        return this.result.dataid.id;
    }
    
    public void calculate() {
        synchronized (this) {
            if (Core.bDebug) {
                GL11.glGetError();
            }
            try {
                this.result = TextureCombiner.instance.combine(this.commands);
            }
            catch (Exception ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ex.getClass().getSimpleName()));
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, TextureCombiner.getResultingWidth(this.commands)));
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, TextureCombiner.getResultingHeight(this.commands)));
                DebugLog.General.error((Object)"");
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PZArrayUtil.arrayToString(this.commands)));
                DebugLog.General.error((Object)"");
                DebugLog.General.error((Object)"Stack trace: ");
                ExceptionLogger.logException(ex);
                DebugLog.General.error((Object)"This SmartTexture will no longer be valid.");
                this.width = -1;
                this.height = -1;
                this.dirty = false;
                return;
            }
            this.width = this.result.width;
            this.height = this.result.height;
            this.dirty = false;
        }
    }
    
    public void clear() {
        TextureCombinerCommand.pool.release(this.commands);
        this.commands.clear();
        this.categoryMap.clear();
        this.dirty = false;
    }
    
    public void add(final String s) {
        this.add(Texture.getSharedTexture(s));
    }
    
    public void add(final Texture texture) {
        if (SmartTexture.blit == null) {
            this.create();
        }
        this.commands.add(TextureCombinerCommand.get().init(texture, SmartTexture.blit));
        this.dirty = true;
    }
    
    public void add(final String s, final SmartShader smartShader, final ArrayList<TextureCombinerShaderParam> list) {
        this.add(Texture.getSharedTexture(s), smartShader, list);
    }
    
    public void add(final Texture texture, final SmartShader smartShader, final ArrayList<TextureCombinerShaderParam> list) {
        this.commands.add(TextureCombinerCommand.get().init(texture, smartShader, list));
        this.dirty = true;
    }
    
    public void add(final String s, final SmartShader smartShader, final String s2, final int n, final int n2) {
        this.add(Texture.getSharedTexture(s), smartShader, Texture.getSharedTexture(s2), n, n2);
    }
    
    public void add(final Texture texture, final SmartShader smartShader, final Texture texture2, final int n, final int n2) {
        this.commands.add(TextureCombinerCommand.get().init(texture, smartShader, texture2, n, n2));
        this.dirty = true;
    }
    
    public void add(final String s, final int n, final int n2) {
        this.add(Texture.getSharedTexture(s), n, n2);
    }
    
    public void add(final Texture texture, final int n, final int n2) {
        this.commands.add(TextureCombinerCommand.get().init(texture, n, n2));
        this.dirty = true;
    }
    
    public void add(final String s, final SmartShader smartShader, final String s2, final ArrayList<TextureCombinerShaderParam> list, final int n, final int n2) {
        this.add(Texture.getSharedTexture(s), smartShader, Texture.getSharedTexture(s2), list, n, n2);
    }
    
    public void add(final Texture texture, final SmartShader smartShader, final Texture texture2, final ArrayList<TextureCombinerShaderParam> list, final int n, final int n2) {
        this.commands.add(TextureCombinerCommand.get().init(texture, smartShader, list, texture2, n, n2));
        this.dirty = true;
    }
    
    @Override
    public void save(final String s) {
        if (this.dirty) {
            this.calculate();
        }
        this.result.save(s);
    }
    
    protected void setDirty() {
        this.dirty = true;
    }
    
    @Override
    public boolean isEmpty() {
        return this.result == null || this.result.isEmpty();
    }
    
    @Override
    public boolean isFailure() {
        return this.result != null && this.result.isFailure();
    }
    
    @Override
    public boolean isReady() {
        return this.result != null && this.result.isReady();
    }
    
    static {
        bodyMaskParams = new ArrayList<TextureCombinerShaderParam>();
        addHoleParams = new ArrayList<TextureCombinerShaderParam>();
        removeHoleParams = new ArrayList<TextureCombinerShaderParam>();
    }
}
