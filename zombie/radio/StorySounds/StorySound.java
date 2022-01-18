// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.StorySounds;

import zombie.characters.IsoPlayer;
import zombie.iso.Vector2;

public final class StorySound
{
    protected String name;
    protected float baseVolume;
    
    public StorySound(final String name, final float baseVolume) {
        this.name = null;
        this.baseVolume = 1.0f;
        this.name = name;
        this.baseVolume = baseVolume;
    }
    
    public long playSound() {
        final Vector2 randomBorderPosition = SLSoundManager.getInstance().getRandomBorderPosition();
        return SLSoundManager.Emitter.playSound(this.name, this.baseVolume, randomBorderPosition.x, randomBorderPosition.y, 0.0f, 100.0f, SLSoundManager.getInstance().getRandomBorderRange());
    }
    
    public long playSound(final float n) {
        return SLSoundManager.Emitter.playSound(this.name, n, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, 10.0f, 50.0f);
    }
    
    public long playSound(final float n, final float n2, final float n3, final float n4, final float n5) {
        return this.playSound(this.baseVolume, n, n2, n3, n4, n5);
    }
    
    public long playSound(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return SLSoundManager.Emitter.playSound(this.name, this.baseVolume * n, n2, n3, n4, n5, n6);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public float getBaseVolume() {
        return this.baseVolume;
    }
    
    public void setBaseVolume(final float baseVolume) {
        this.baseVolume = baseVolume;
    }
    
    public StorySound getClone() {
        return new StorySound(this.name, this.baseVolume);
    }
}
