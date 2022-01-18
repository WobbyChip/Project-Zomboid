// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap.styles;

import java.util.HashMap;
import zombie.core.math.PZMath;
import zombie.worldMap.WorldMapFeature;
import java.util.ArrayList;

public class WorldMapLineStyleLayer extends WorldMapStyleLayer
{
    public final ArrayList<ColorStop> m_fill;
    public final ArrayList<FloatStop> m_lineWidth;
    
    public WorldMapLineStyleLayer(final String s) {
        super(s);
        this.m_fill = new ArrayList<ColorStop>();
        this.m_lineWidth = new ArrayList<FloatStop>();
    }
    
    @Override
    public String getTypeString() {
        return "Line";
    }
    
    @Override
    public void render(final WorldMapFeature worldMapFeature, final RenderArgs renderArgs) {
        final RGBAf evalColor = this.evalColor(renderArgs, this.m_fill);
        if (evalColor.a < 0.01f) {
            return;
        }
        float evalFloat;
        if (worldMapFeature.m_properties.containsKey("width")) {
            evalFloat = PZMath.tryParseFloat(((HashMap<K, String>)worldMapFeature.m_properties).get("width"), 1.0f) * renderArgs.drawer.getWorldScale();
        }
        else {
            evalFloat = this.evalFloat(renderArgs, this.m_lineWidth);
        }
        renderArgs.drawer.drawLineString(renderArgs, worldMapFeature, evalColor, evalFloat);
        RGBAf.s_pool.release(evalColor);
    }
}
