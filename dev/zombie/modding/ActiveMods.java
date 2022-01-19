// 
// Decompiled by Procyon v0.5.36
// 

package zombie.modding;

import zombie.MapGroups;
import zombie.gameStates.ChooseGameInfo;
import java.util.Collection;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.core.Core;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.GameWindow;
import zombie.debug.DebugOptions;
import java.util.Objects;
import zombie.util.StringUtils;
import java.util.ArrayList;

public final class ActiveMods
{
    private static final ArrayList<ActiveMods> s_activeMods;
    private static final ActiveMods s_loaded;
    private final String id;
    private final ArrayList<String> mods;
    private final ArrayList<String> mapOrder;
    
    private static int count() {
        return ActiveMods.s_activeMods.size();
    }
    
    public static ActiveMods getByIndex(final int index) {
        return ActiveMods.s_activeMods.get(index);
    }
    
    public static ActiveMods getById(final String s) {
        final int index = indexOf(s);
        if (index == -1) {
            return create(s);
        }
        return ActiveMods.s_activeMods.get(index);
    }
    
    public static int indexOf(String trim) {
        trim = trim.trim();
        requireValidId(trim);
        for (int i = 0; i < ActiveMods.s_activeMods.size(); ++i) {
            if (ActiveMods.s_activeMods.get(i).id.equalsIgnoreCase(trim)) {
                return i;
            }
        }
        return -1;
    }
    
    private static ActiveMods create(final String s) {
        requireValidId(s);
        if (indexOf(s) != -1) {
            throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        final ActiveMods e = new ActiveMods(s);
        ActiveMods.s_activeMods.add(e);
        return e;
    }
    
    private static void requireValidId(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            throw new IllegalArgumentException("id is null or whitespace");
        }
    }
    
    public static void setLoadedMods(final ActiveMods activeMods) {
        if (activeMods == null) {
            return;
        }
        ActiveMods.s_loaded.copyFrom(activeMods);
    }
    
    public static boolean requiresResetLua(final ActiveMods obj) {
        Objects.requireNonNull(obj);
        return !ActiveMods.s_loaded.mods.equals(obj.mods);
    }
    
    public static void renderUI() {
        if (!DebugOptions.instance.ModRenderLoaded.getValue()) {
            return;
        }
        if (GameWindow.DrawReloadingLua) {
            return;
        }
        final UIFont debugConsole = UIFont.DebugConsole;
        final int fontHeight = TextManager.instance.getFontHeight(debugConsole);
        final String s = "Active Mods:";
        int a = TextManager.instance.MeasureStringX(debugConsole, s);
        for (int i = 0; i < ActiveMods.s_loaded.mods.size(); ++i) {
            a = Math.max(a, TextManager.instance.MeasureStringX(debugConsole, ActiveMods.s_loaded.mods.get(i)));
        }
        final int n = 10;
        final int n2 = a + n * 2;
        final int n3 = Core.width - 20 - n2;
        final int n4 = 20;
        SpriteRenderer.instance.renderi(null, n3, n4, n2, (1 + ActiveMods.s_loaded.mods.size()) * fontHeight + n * 2, 0.0f, 0.5f, 0.75f, 1.0f, null);
        int n5;
        TextManager.instance.DrawString(debugConsole, n3 + n, n5 = n4 + n, s, 1.0, 1.0, 1.0, 1.0);
        for (int j = 0; j < ActiveMods.s_loaded.mods.size(); ++j) {
            TextManager.instance.DrawString(debugConsole, n3 + n, n5 += fontHeight, ActiveMods.s_loaded.mods.get(j), 1.0, 1.0, 1.0, 1.0);
        }
    }
    
    public static void Reset() {
        ActiveMods.s_loaded.clear();
    }
    
    public ActiveMods(final String id) {
        this.mods = new ArrayList<String>();
        this.mapOrder = new ArrayList<String>();
        requireValidId(id);
        this.id = id;
    }
    
    public void clear() {
        this.mods.clear();
        this.mapOrder.clear();
    }
    
    public ArrayList<String> getMods() {
        return this.mods;
    }
    
    public ArrayList<String> getMapOrder() {
        return this.mapOrder;
    }
    
    public void copyFrom(final ActiveMods activeMods) {
        this.mods.clear();
        this.mapOrder.clear();
        this.mods.addAll(activeMods.mods);
        this.mapOrder.addAll(activeMods.mapOrder);
    }
    
    public void setModActive(String trim, final boolean b) {
        trim = trim.trim();
        if (StringUtils.isNullOrWhitespace(trim)) {
            return;
        }
        if (b) {
            if (!this.mods.contains(trim)) {
                this.mods.add(trim);
            }
        }
        else {
            this.mods.remove(trim);
        }
    }
    
    public boolean isModActive(String trim) {
        trim = trim.trim();
        return !StringUtils.isNullOrWhitespace(trim) && this.mods.contains(trim);
    }
    
    public void removeMod(String trim) {
        trim = trim.trim();
        this.mods.remove(trim);
    }
    
    public void removeMapOrder(final String o) {
        this.mapOrder.remove(o);
    }
    
    public void checkMissingMods() {
        if (this.mods.isEmpty()) {
            return;
        }
        for (int i = this.mods.size() - 1; i >= 0; --i) {
            if (ChooseGameInfo.getAvailableModDetails(this.mods.get(i)) == null) {
                this.mods.remove(i);
            }
        }
    }
    
    public void checkMissingMaps() {
        if (this.mapOrder.isEmpty()) {
            return;
        }
        final MapGroups mapGroups = new MapGroups();
        mapGroups.createGroups(this, false);
        if (mapGroups.checkMapConflicts()) {
            final ArrayList<String> allMapsInOrder = mapGroups.getAllMapsInOrder();
            for (int i = this.mapOrder.size() - 1; i >= 0; --i) {
                if (!allMapsInOrder.contains(this.mapOrder.get(i))) {
                    this.mapOrder.remove(i);
                }
            }
        }
        else {
            this.mapOrder.clear();
        }
    }
    
    static {
        s_activeMods = new ArrayList<ActiveMods>();
        s_loaded = new ActiveMods("loaded");
    }
}
