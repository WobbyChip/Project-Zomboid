// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation.debug;

import zombie.util.list.PZArrayUtil;

public class GenericNameWeightRecordingFrame extends GenericNameValueRecordingFrame
{
    private float[] m_weights;
    
    public GenericNameWeightRecordingFrame(final String s) {
        super(s, "_weights");
        this.m_weights = new float[0];
    }
    
    @Override
    protected void onColumnAdded() {
        this.m_weights = PZArrayUtil.add(this.m_weights, 0.0f);
    }
    
    public void logWeight(final String s, final int n, final float n2) {
        final int orCreateColumn = this.getOrCreateColumn(s, n);
        final float[] weights = this.m_weights;
        final int n3 = orCreateColumn;
        weights[n3] += n2;
    }
    
    public int getOrCreateColumn(final String s, final int n) {
        final String s2 = (n != 0) ? invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n) : "";
        final int orCreateColumn = super.getOrCreateColumn(String.format("%s%s", s2, s));
        if (this.m_weights[orCreateColumn] == 0.0f) {
            return orCreateColumn;
        }
        int i = 1;
        int orCreateColumn2;
        while (true) {
            orCreateColumn2 = super.getOrCreateColumn(String.format("%s%s-%d", s2, s, i));
            if (this.m_weights[orCreateColumn2] == 0.0f) {
                break;
            }
            ++i;
        }
        return orCreateColumn2;
    }
    
    public float getWeightAt(final int n) {
        return this.m_weights[n];
    }
    
    @Override
    public String getValueAt(final int n) {
        return String.valueOf(this.getWeightAt(n));
    }
    
    @Override
    public void reset() {
        for (int i = 0; i < this.m_weights.length; ++i) {
            this.m_weights[i] = 0.0f;
        }
    }
}
