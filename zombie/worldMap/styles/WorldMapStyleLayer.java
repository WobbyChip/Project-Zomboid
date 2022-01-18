// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.styles;

import zombie.worldMap.WorldMapRenderer;
import zombie.popman.ObjectPool;
import zombie.worldMap.WorldMapFeature;
import zombie.core.textures.Texture;
import zombie.core.math.PZMath;
import java.util.ArrayList;

public abstract class WorldMapStyleLayer
{
    public String m_id;
    public float m_minZoom;
    public IWorldMapStyleFilter m_filter;
    public String m_filterKey;
    public String m_filterValue;
    
    public WorldMapStyleLayer(final String id) {
        this.m_minZoom = 0.0f;
        this.m_id = id;
    }
    
    public abstract String getTypeString();
    
    static <S extends Stop> int findStop(final float n, final ArrayList<S> list) {
        if (list.isEmpty()) {
            return -2;
        }
        if (n <= list.get(0).m_zoom) {
            return -1;
        }
        for (int i = 0; i < list.size() - 1; ++i) {
            if (n <= list.get(i + 1).m_zoom) {
                return i;
            }
        }
        return list.size() - 1;
    }
    
    protected RGBAf evalColor(final RenderArgs renderArgs, final ArrayList<ColorStop> list) {
        if (list.isEmpty()) {
            return RGBAf.s_pool.alloc().init(1.0f, 1.0f, 1.0f, 1.0f);
        }
        final float zoomF = renderArgs.drawer.m_zoomF;
        final int stop = findStop(zoomF, (ArrayList<Stop>)list);
        final int index = (stop == -1) ? 0 : stop;
        final int min = PZMath.min(stop + 1, list.size() - 1);
        final ColorStop colorStop = list.get(index);
        final ColorStop colorStop2 = list.get(min);
        final float n = (index == min) ? 1.0f : ((PZMath.clamp(zoomF, colorStop.m_zoom, colorStop2.m_zoom) - colorStop.m_zoom) / (colorStop2.m_zoom - colorStop.m_zoom));
        return RGBAf.s_pool.alloc().init(PZMath.lerp((float)colorStop.r, (float)colorStop2.r, n) / 255.0f, PZMath.lerp((float)colorStop.g, (float)colorStop2.g, n) / 255.0f, PZMath.lerp((float)colorStop.b, (float)colorStop2.b, n) / 255.0f, PZMath.lerp((float)colorStop.a, (float)colorStop2.a, n) / 255.0f);
    }
    
    protected float evalFloat(final RenderArgs renderArgs, final ArrayList<FloatStop> list) {
        if (list.isEmpty()) {
            return 1.0f;
        }
        final float zoomF = renderArgs.drawer.m_zoomF;
        final int stop = findStop(zoomF, (ArrayList<Stop>)list);
        final int index = (stop == -1) ? 0 : stop;
        final int min = PZMath.min(stop + 1, list.size() - 1);
        final FloatStop floatStop = list.get(index);
        final FloatStop floatStop2 = list.get(min);
        return PZMath.lerp(floatStop.f, floatStop2.f, (index == min) ? 1.0f : ((PZMath.clamp(zoomF, floatStop.m_zoom, floatStop2.m_zoom) - floatStop.m_zoom) / (floatStop2.m_zoom - floatStop.m_zoom)));
    }
    
    protected Texture evalTexture(final RenderArgs renderArgs, final ArrayList<TextureStop> list) {
        if (list.isEmpty()) {
            return null;
        }
        final float zoomF = renderArgs.drawer.m_zoomF;
        final int stop = findStop(zoomF, (ArrayList<Stop>)list);
        final int index = (stop == -1) ? 0 : stop;
        final int min = PZMath.min(stop + 1, list.size() - 1);
        final TextureStop textureStop = list.get(index);
        final TextureStop textureStop2 = list.get(min);
        if (textureStop == textureStop2) {
            return (zoomF < textureStop.m_zoom) ? null : textureStop.texture;
        }
        if (zoomF < textureStop.m_zoom || zoomF > textureStop2.m_zoom) {
            return null;
        }
        return (((index == min) ? 1.0f : ((PZMath.clamp(zoomF, textureStop.m_zoom, textureStop2.m_zoom) - textureStop.m_zoom) / (textureStop2.m_zoom - textureStop.m_zoom))) < 0.5f) ? textureStop.texture : textureStop2.texture;
    }
    
    public boolean filter(final WorldMapFeature worldMapFeature, final FilterArgs filterArgs) {
        return this.m_filter != null && this.m_filter.filter(worldMapFeature, filterArgs);
    }
    
    public abstract void render(final WorldMapFeature p0, final RenderArgs p1);
    
    public void renderCell(final RenderArgs renderArgs) {
    }
    
    public static class Stop
    {
        public float m_zoom;
        
        Stop(final float zoom) {
            this.m_zoom = zoom;
        }
    }
    
    public static class ColorStop extends Stop
    {
        public int r;
        public int g;
        public int b;
        public int a;
        
        public ColorStop(final float n, final int r, final int g, final int b, final int a) {
            super(n);
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }
    
    public static class FloatStop extends Stop
    {
        public float f;
        
        public FloatStop(final float n, final float f) {
            super(n);
            this.f = f;
        }
    }
    
    public static class TextureStop extends Stop
    {
        public String texturePath;
        public Texture texture;
        
        public TextureStop(final float n, final String texturePath) {
            super(n);
            this.texturePath = texturePath;
            this.texture = Texture.getTexture(texturePath);
        }
    }
    
    public static final class RGBAf
    {
        public float r;
        public float g;
        public float b;
        public float a;
        public static final ObjectPool<RGBAf> s_pool;
        
        public RGBAf() {
            final float n = 1.0f;
            this.a = n;
            this.b = n;
            this.g = n;
            this.r = n;
        }
        
        public RGBAf init(final float r, final float g, final float b, final float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            return this;
        }
        
        static {
            s_pool = new ObjectPool<RGBAf>(RGBAf::new);
        }
    }
    
    public static final class FilterArgs
    {
        public WorldMapRenderer renderer;
    }
    
    public static final class RenderArgs
    {
        public WorldMapRenderer renderer;
        public WorldMapRenderer.Drawer drawer;
        public int cellX;
        public int cellY;
    }
    
    public interface IWorldMapStyleFilter
    {
        boolean filter(final WorldMapFeature p0, final FilterArgs p1);
    }
}
