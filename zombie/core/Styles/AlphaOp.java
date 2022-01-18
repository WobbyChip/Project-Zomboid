// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

import java.nio.FloatBuffer;
import org.lwjgl.util.ReadableColor;

public enum AlphaOp
{
    PREMULTIPLY {
        @Override
        protected int calc(final ReadableColor readableColor, final int n) {
            final float n2 = readableColor.getAlpha() * n * 0.003921569f;
            final float n3 = n2 * 0.003921569f;
            return (int)(readableColor.getRed() * n3) << 0 | (int)(readableColor.getGreen() * n3) << 8 | (int)(readableColor.getBlue() * n3) << 16 | (int)n2 << 24;
        }
    }, 
    KEEP {
        @Override
        protected int calc(final ReadableColor readableColor, final int n) {
            return readableColor.getRed() << 0 | readableColor.getGreen() << 8 | readableColor.getBlue() << 16 | readableColor.getAlpha() << 24;
        }
    }, 
    ZERO {
        @Override
        protected int calc(final ReadableColor readableColor, final int n) {
            final float n2 = readableColor.getAlpha() * n * 0.003921569f * 0.003921569f;
            return (int)(readableColor.getRed() * n2) << 0 | (int)(readableColor.getGreen() * n2) << 8 | (int)(readableColor.getBlue() * n2) << 16;
        }
    };
    
    private static final float PREMULT_ALPHA = 0.003921569f;
    
    public final void op(final ReadableColor readableColor, final int n, final FloatBuffer floatBuffer) {
        floatBuffer.put(Float.intBitsToFloat(this.calc(readableColor, n)));
    }
    
    public final void op(final int n, final int n2, final FloatBuffer floatBuffer) {
        floatBuffer.put(Float.intBitsToFloat(n));
    }
    
    protected abstract int calc(final ReadableColor p0, final int p1);
    
    private static /* synthetic */ AlphaOp[] $values() {
        return new AlphaOp[] { AlphaOp.PREMULTIPLY, AlphaOp.KEEP, AlphaOp.ZERO };
    }
    
    static {
        $VALUES = $values();
    }
}
