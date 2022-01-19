// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoGameCharacter;
import zombie.audio.FMODLocalParameter;

public final class ParameterCharacterMovementSpeed extends FMODLocalParameter
{
    private final IsoGameCharacter character;
    private MovementType movementType;
    
    public ParameterCharacterMovementSpeed(final IsoGameCharacter character) {
        super("CharacterMovementSpeed");
        this.movementType = MovementType.Walk;
        this.character = character;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)this.movementType.label;
    }
    
    public void setMovementType(final MovementType movementType) {
        this.movementType = movementType;
    }
    
    public enum MovementType
    {
        SneakWalk(0), 
        SneakRun(1), 
        Strafe(2), 
        Walk(3), 
        Run(4), 
        Sprint(5);
        
        public final int label;
        
        private MovementType(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ MovementType[] $values() {
            return new MovementType[] { MovementType.SneakWalk, MovementType.SneakRun, MovementType.Strafe, MovementType.Walk, MovementType.Run, MovementType.Sprint };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
