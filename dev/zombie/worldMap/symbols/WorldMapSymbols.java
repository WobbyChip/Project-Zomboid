// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.iso.IsoUtils;
import org.joml.Quaternionfc;
import org.joml.Matrix4fc;
import zombie.vehicles.BaseVehicle;
import zombie.core.Core;
import zombie.worldMap.UIWorldMap;
import zombie.core.textures.Texture;
import zombie.core.math.PZMath;
import zombie.ui.TextManager;
import zombie.network.GameServer;
import zombie.ui.UIFont;
import org.joml.Quaternionf;
import java.util.ArrayList;

public class WorldMapSymbols
{
    public static final int SAVEFILE_VERSION = 1;
    public final float MIN_VISIBLE_ZOOM = 14.5f;
    public static final float COLLAPSED_RADIUS = 3.0f;
    private final ArrayList<WorldMapBaseSymbol> m_symbols;
    private final WorldMapSymbolCollisions m_collision;
    private float m_layoutWorldScale;
    private final Quaternionf m_layoutRotation;
    private boolean m_layoutIsometric;
    private boolean m_layoutMiniMapSymbols;
    
    public WorldMapSymbols() {
        this.m_symbols = new ArrayList<WorldMapBaseSymbol>();
        this.m_collision = new WorldMapSymbolCollisions();
        this.m_layoutWorldScale = 0.0f;
        this.m_layoutRotation = new Quaternionf();
        this.m_layoutIsometric = true;
        this.m_layoutMiniMapSymbols = false;
    }
    
    public WorldMapTextSymbol addTranslatedText(final String s, final UIFont uiFont, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return this.addText(s, true, uiFont, n, n2, 0.0f, 0.0f, WorldMapBaseSymbol.DEFAULT_SCALE, n3, n4, n5, n6);
    }
    
    public WorldMapTextSymbol addUntranslatedText(final String s, final UIFont uiFont, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return this.addText(s, false, uiFont, n, n2, 0.0f, 0.0f, WorldMapBaseSymbol.DEFAULT_SCALE, n3, n4, n5, n6);
    }
    
    public WorldMapTextSymbol addText(final String text, final boolean translated, final UIFont font, final float x, final float y, final float n, final float n2, final float scale, final float r, final float g, final float b, final float a) {
        final WorldMapTextSymbol e = new WorldMapTextSymbol(this);
        e.m_text = text;
        e.m_translated = translated;
        e.m_font = font;
        e.m_x = x;
        e.m_y = y;
        if (!GameServer.bServer) {
            e.m_width = (float)TextManager.instance.MeasureStringX(font, e.getTranslatedText());
            e.m_height = (float)TextManager.instance.getFontHeight(font);
        }
        e.m_anchorX = PZMath.clamp(n, 0.0f, 1.0f);
        e.m_anchorY = PZMath.clamp(n2, 0.0f, 1.0f);
        e.m_scale = scale;
        e.m_r = r;
        e.m_g = g;
        e.m_b = b;
        e.m_a = a;
        this.m_symbols.add(e);
        this.m_layoutWorldScale = 0.0f;
        return e;
    }
    
    public WorldMapTextureSymbol addTexture(final String s, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return this.addTexture(s, n, n2, 0.0f, 0.0f, WorldMapBaseSymbol.DEFAULT_SCALE, n3, n4, n5, n6);
    }
    
    public WorldMapTextureSymbol addTexture(final String symbolID, final float x, final float y, final float n, final float n2, final float scale, final float r, final float g, final float b, final float a) {
        final WorldMapTextureSymbol e = new WorldMapTextureSymbol(this);
        e.setSymbolID(symbolID);
        final MapSymbolDefinitions.MapSymbolDefinition symbolById = MapSymbolDefinitions.getInstance().getSymbolById(symbolID);
        if (symbolById == null) {
            e.m_width = 18.0f;
            e.m_height = 18.0f;
        }
        else {
            e.m_texture = (GameServer.bServer ? null : Texture.getSharedTexture(symbolById.getTexturePath()));
            e.m_width = (float)symbolById.getWidth();
            e.m_height = (float)symbolById.getHeight();
        }
        if (e.m_texture == null && !GameServer.bServer) {
            e.m_texture = Texture.getErrorTexture();
        }
        e.m_x = x;
        e.m_y = y;
        e.m_anchorX = PZMath.clamp(n, 0.0f, 1.0f);
        e.m_anchorY = PZMath.clamp(n2, 0.0f, 1.0f);
        e.m_scale = scale;
        e.m_r = r;
        e.m_g = g;
        e.m_b = b;
        e.m_a = a;
        this.m_symbols.add(e);
        this.m_layoutWorldScale = 0.0f;
        return e;
    }
    
    public void removeSymbolByIndex(final int index) {
        this.m_symbols.remove(index).release();
    }
    
    public void clear() {
        for (int i = 0; i < this.m_symbols.size(); ++i) {
            this.m_symbols.get(i).release();
        }
        this.m_symbols.clear();
        this.m_layoutWorldScale = 0.0f;
    }
    
    public void invalidateLayout() {
        this.m_layoutWorldScale = 0.0f;
    }
    
    public void render(final UIWorldMap uiWorldMap) {
        final float worldOriginX = uiWorldMap.getAPI().worldOriginX();
        final float worldOriginY = uiWorldMap.getAPI().worldOriginY();
        this.checkLayout(uiWorldMap);
        if (Core.bDebug) {}
        final boolean b = false;
        for (int i = 0; i < this.m_symbols.size(); ++i) {
            final WorldMapBaseSymbol worldMapBaseSymbol = this.m_symbols.get(i);
            if (this.isSymbolVisible(uiWorldMap, worldMapBaseSymbol)) {
                final float n = worldOriginX + worldMapBaseSymbol.m_layoutX;
                final float n2 = worldOriginY + worldMapBaseSymbol.m_layoutY;
                if (n + worldMapBaseSymbol.widthScaled(uiWorldMap) > 0.0f && n < uiWorldMap.getWidth() && n2 + worldMapBaseSymbol.heightScaled(uiWorldMap) > 0.0f) {
                    if (n2 < uiWorldMap.getHeight()) {
                        if (b) {
                            uiWorldMap.DrawTextureScaledColor(null, (double)n, (double)n2, (double)worldMapBaseSymbol.widthScaled(uiWorldMap), (double)worldMapBaseSymbol.heightScaled(uiWorldMap), 1.0, 1.0, 1.0, 0.3);
                        }
                        worldMapBaseSymbol.render(uiWorldMap, worldOriginX, worldOriginY);
                    }
                }
            }
        }
    }
    
    void checkLayout(final UIWorldMap uiWorldMap) {
        final Quaternionf setFromUnnormalized = BaseVehicle.TL_quaternionf_pool.get().alloc().setFromUnnormalized((Matrix4fc)uiWorldMap.getAPI().getRenderer().getModelViewMatrix());
        if (this.m_layoutWorldScale == uiWorldMap.getAPI().getWorldScale() && this.m_layoutIsometric == uiWorldMap.getAPI().getBoolean("Isometric") && this.m_layoutMiniMapSymbols == uiWorldMap.getAPI().getBoolean("MiniMapSymbols") && this.m_layoutRotation.equals((Object)setFromUnnormalized)) {
            BaseVehicle.TL_quaternionf_pool.get().release(setFromUnnormalized);
            return;
        }
        this.m_layoutWorldScale = uiWorldMap.getAPI().getWorldScale();
        this.m_layoutIsometric = uiWorldMap.getAPI().getBoolean("Isometric");
        this.m_layoutMiniMapSymbols = uiWorldMap.getAPI().getBoolean("MiniMapSymbols");
        this.m_layoutRotation.set((Quaternionfc)setFromUnnormalized);
        BaseVehicle.TL_quaternionf_pool.get().release(setFromUnnormalized);
        final float worldOriginX = uiWorldMap.getAPI().worldOriginX();
        final float worldOriginY = uiWorldMap.getAPI().worldOriginY();
        this.m_collision.m_boxes.clear();
        boolean b = false;
        for (int i = 0; i < this.m_symbols.size(); ++i) {
            final WorldMapBaseSymbol worldMapBaseSymbol = this.m_symbols.get(i);
            worldMapBaseSymbol.layout(uiWorldMap, this.m_collision, worldOriginX, worldOriginY);
            b |= worldMapBaseSymbol.m_collided;
        }
        if (b) {
            for (int j = 0; j < this.m_symbols.size(); ++j) {
                final WorldMapBaseSymbol worldMapBaseSymbol2 = this.m_symbols.get(j);
                if (!worldMapBaseSymbol2.m_collided && this.m_collision.isCollision(j)) {
                    worldMapBaseSymbol2.m_collided = true;
                }
            }
        }
    }
    
    public int getSymbolCount() {
        return this.m_symbols.size();
    }
    
    public WorldMapBaseSymbol getSymbolByIndex(final int index) {
        return this.m_symbols.get(index);
    }
    
    boolean isSymbolVisible(final UIWorldMap uiWorldMap, final WorldMapBaseSymbol worldMapBaseSymbol) {
        return worldMapBaseSymbol.isVisible() && (worldMapBaseSymbol.m_scale <= 0.0f || uiWorldMap.getAPI().getZoomF() >= 14.5f);
    }
    
    int hitTest(final UIWorldMap uiWorldMap, float n, float n2) {
        n -= uiWorldMap.getAPI().worldOriginX();
        n2 -= uiWorldMap.getAPI().worldOriginY();
        this.checkLayout(uiWorldMap);
        float n3 = Float.MAX_VALUE;
        int n4 = -1;
        for (int i = 0; i < this.m_symbols.size(); ++i) {
            final WorldMapBaseSymbol worldMapBaseSymbol = this.m_symbols.get(i);
            if (this.isSymbolVisible(uiWorldMap, worldMapBaseSymbol)) {
                float layoutX = worldMapBaseSymbol.m_layoutX;
                float layoutY = worldMapBaseSymbol.m_layoutY;
                float n5 = layoutX + worldMapBaseSymbol.widthScaled(uiWorldMap);
                float n6 = layoutY + worldMapBaseSymbol.heightScaled(uiWorldMap);
                if (worldMapBaseSymbol.m_collided) {
                    layoutX += worldMapBaseSymbol.widthScaled(uiWorldMap) / 2.0f - 1.5f;
                    layoutY += worldMapBaseSymbol.heightScaled(uiWorldMap) / 2.0f - 1.5f;
                    n5 = layoutX + 6.0f;
                    n6 = layoutY + 6.0f;
                    final float distanceToSquared = IsoUtils.DistanceToSquared((layoutX + n5) / 2.0f, (layoutY + n6) / 2.0f, n, n2);
                    if (distanceToSquared < n3) {
                        n3 = distanceToSquared;
                        n4 = i;
                    }
                }
                if (n >= layoutX && n < n5 && n2 >= layoutY && n2 < n6) {
                    return i;
                }
            }
        }
        if (n4 != -1 && n3 < 100.0f) {
            return n4;
        }
        return -1;
    }
    
    public boolean getMiniMapSymbols() {
        return this.m_layoutMiniMapSymbols;
    }
    
    public float getLayoutWorldScale() {
        return this.m_layoutWorldScale;
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putShort((short)1);
        byteBuffer.putInt(this.m_symbols.size());
        for (int i = 0; i < this.m_symbols.size(); ++i) {
            final WorldMapBaseSymbol worldMapBaseSymbol = this.m_symbols.get(i);
            byteBuffer.put((byte)worldMapBaseSymbol.getType().index());
            worldMapBaseSymbol.save(byteBuffer);
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        final short short1 = byteBuffer.getShort();
        if (short1 < 1 || short1 > 1) {
            throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, short1));
        }
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            final byte value = byteBuffer.get();
            if (value == WorldMapSymbolType.Text.index()) {
                final WorldMapTextSymbol e = new WorldMapTextSymbol(this);
                e.load(byteBuffer, n, short1);
                this.m_symbols.add(e);
            }
            else {
                if (value != WorldMapSymbolType.Texture.index()) {
                    throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value));
                }
                final WorldMapTextureSymbol e2 = new WorldMapTextureSymbol(this);
                e2.load(byteBuffer, n, short1);
                this.m_symbols.add(e2);
            }
        }
    }
    
    public enum WorldMapSymbolType
    {
        NONE(-1), 
        Text(0), 
        Texture(1);
        
        private final byte m_type;
        
        private WorldMapSymbolType(final int n) {
            this.m_type = (byte)n;
        }
        
        int index() {
            return this.m_type;
        }
        
        private static /* synthetic */ WorldMapSymbolType[] $values() {
            return new WorldMapSymbolType[] { WorldMapSymbolType.NONE, WorldMapSymbolType.Text, WorldMapSymbolType.Texture };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
