// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

public class BlendInfo
{
    public String name;
    public BlendType Type;
    public String mulDec;
    public String mulInc;
    public float dec;
    public float inc;
    
    public static class BlendInstance
    {
        public float current;
        public float target;
        BlendInfo info;
        
        public String GetDebug() {
            String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.info.name);
            switch (this.info.Type) {
                case Linear: {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
                    break;
                }
                case InverseExponential: {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
                    break;
                }
            }
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;F)Ljava/lang/String;, s, this.current);
        }
        
        public BlendInstance(final BlendInfo info) {
            this.current = -1.0f;
            this.info = info;
        }
        
        public void set(final float target) {
            this.target = target;
            if (this.current == -1.0f) {
                this.current = this.target;
            }
        }
        
        public void update() {
            if (this.current < this.target) {
                float n = 1.0f;
                switch (this.info.Type) {
                    case InverseExponential: {
                        n = 1.0f - this.current / 1.0f;
                        if (n < 0.1f) {
                            n = 0.1f;
                            break;
                        }
                        break;
                    }
                }
                this.current += this.info.inc * n;
                if (this.current > this.target) {
                    this.current = this.target;
                }
            }
            else if (this.current > this.target) {
                float n2 = 1.0f;
                switch (this.info.Type) {
                    case InverseExponential: {
                        n2 = 1.0f - this.current / 1.0f;
                        if (n2 < 0.1f) {
                            n2 = 0.1f;
                            break;
                        }
                        break;
                    }
                }
                this.current += -this.info.dec * n2;
                if (this.current < this.target) {
                    this.current = this.target;
                }
            }
        }
    }
}
