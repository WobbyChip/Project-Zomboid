// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

public class BodyPartLast
{
    private boolean bandaged;
    private boolean bitten;
    private boolean scratched;
    private boolean cut;
    
    public BodyPartLast() {
        this.cut = false;
    }
    
    public boolean bandaged() {
        return this.bandaged;
    }
    
    public boolean bitten() {
        return this.bitten;
    }
    
    public boolean scratched() {
        return this.scratched;
    }
    
    public boolean isCut() {
        return this.cut;
    }
    
    public void copy(final BodyPart bodyPart) {
        this.bandaged = bodyPart.bandaged();
        this.bitten = bodyPart.bitten();
        this.scratched = bodyPart.scratched();
        this.cut = bodyPart.isCut();
    }
}
