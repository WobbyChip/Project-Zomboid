// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.core.math.PZMath;
import zombie.iso.IsoWorld;
import zombie.iso.IsoMetaGrid;
import java.util.ArrayList;
import zombie.audio.FMODGlobalParameter;

public final class ParameterZone extends FMODGlobalParameter
{
    private final String m_zoneName;
    private final ArrayList<IsoMetaGrid.Zone> m_zones;
    
    public ParameterZone(final String s, final String zoneName) {
        super(s);
        this.m_zones = new ArrayList<IsoMetaGrid.Zone>();
        this.m_zoneName = zoneName;
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoGameCharacter character = this.getCharacter();
        if (character == null) {
            return 40.0f;
        }
        final int n = 0;
        this.m_zones.clear();
        IsoWorld.instance.MetaGrid.getZonesIntersecting((int)character.x - 40, (int)character.y - 40, n, 80, 80, this.m_zones);
        float min = Float.MAX_VALUE;
        for (int i = 0; i < this.m_zones.size(); ++i) {
            final IsoMetaGrid.Zone zone = this.m_zones.get(i);
            if (this.m_zoneName.equalsIgnoreCase(zone.getType())) {
                if (zone.contains((int)character.x, (int)character.y, n)) {
                    return 0.0f;
                }
                final float n2 = zone.x + zone.w / 2.0f;
                final float n3 = zone.y + zone.h / 2.0f;
                final float max = PZMath.max(PZMath.abs(character.x - n2) - zone.w / 2.0f, 0.0f);
                final float max2 = PZMath.max(PZMath.abs(character.y - n3) - zone.h / 2.0f, 0.0f);
                min = PZMath.min(min, max * max + max2 * max2);
            }
        }
        return (float)(int)PZMath.clamp(PZMath.sqrt(min), 0.0f, 40.0f);
    }
    
    private IsoGameCharacter getCharacter() {
        IsoGameCharacter isoGameCharacter = null;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null && (isoGameCharacter == null || (isoGameCharacter.isDead() && isoPlayer.isAlive()))) {
                isoGameCharacter = isoPlayer;
            }
        }
        return isoGameCharacter;
    }
}
