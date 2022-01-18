// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.characters.IsoGameCharacter;
import zombie.core.math.PZMath;
import zombie.audio.FMODGlobalParameter;

public final class ParameterZoneWaterSide extends FMODGlobalParameter
{
    private int m_playerX;
    private int m_playerY;
    private int m_distance;
    
    public ParameterZoneWaterSide() {
        super("ZoneWaterSide");
        this.m_playerX = -1;
        this.m_playerY = -1;
        this.m_distance = 40;
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoGameCharacter character = this.getCharacter();
        if (character == null) {
            return 40.0f;
        }
        final int playerX = (int)character.getX();
        final int playerY = (int)character.getY();
        if (playerX != this.m_playerX || playerY != this.m_playerY) {
            this.m_playerX = playerX;
            this.m_playerY = playerY;
            this.m_distance = this.calculate(character);
            if (this.m_distance < 40) {
                this.m_distance = PZMath.clamp(this.m_distance - 5, 0, 40);
            }
        }
        return (float)this.m_distance;
    }
    
    private int calculate(final IsoGameCharacter isoGameCharacter) {
        if (IsoWorld.instance == null || IsoWorld.instance.CurrentCell == null || IsoWorld.instance.CurrentCell.ChunkMap[0] == null) {
            return 40;
        }
        final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
        float n = Float.MAX_VALUE;
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                final IsoChunk chunk = isoChunkMap.getChunk(j, i);
                if (chunk != null) {
                    if (chunk.getNumberOfWaterTiles() == 100) {
                        final float n2 = chunk.wx * 10 + 5.0f;
                        final float n3 = chunk.wy * 10 + 5.0f;
                        final float n4 = isoGameCharacter.x - n2;
                        final float n5 = isoGameCharacter.y - n3;
                        if (n4 * n4 + n5 * n5 < n) {
                            n = n4 * n4 + n5 * n5;
                        }
                    }
                }
            }
        }
        return (int)PZMath.clamp(PZMath.sqrt(n), 0.0f, 40.0f);
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
