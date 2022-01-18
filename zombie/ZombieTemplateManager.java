// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.textures.Texture;
import java.util.ArrayList;

public class ZombieTemplateManager
{
    public Texture addOverlayToTexture(final ArrayList<BodyOverlay> list, final Texture texture) {
        return null;
    }
    
    public enum OverlayType
    {
        BloodLight, 
        BloodMedium, 
        BloodHeavy;
        
        private static /* synthetic */ OverlayType[] $values() {
            return new OverlayType[] { OverlayType.BloodLight, OverlayType.BloodMedium, OverlayType.BloodHeavy };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public class BodyOverlay
    {
        public BodyPartType location;
        public OverlayType type;
    }
    
    public class ZombieTemplate
    {
        public Texture tex;
    }
}
