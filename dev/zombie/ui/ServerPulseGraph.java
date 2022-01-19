// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.Core;
import java.util.ArrayList;

public final class ServerPulseGraph extends UIElement
{
    public static ServerPulseGraph instance;
    private final ArrayList<Long> times;
    private final ArrayList<Integer> bars;
    private final int NUM_BARS = 30;
    private final int BAR_WID = 4;
    private final int BAR_PAD = 1;
    
    public ServerPulseGraph() {
        this.times = new ArrayList<Long>();
        this.bars = new ArrayList<Integer>();
        this.setVisible(false);
    }
    
    public void add(final long l) {
        if (l < 0L) {
            this.setVisible(false);
            return;
        }
        this.setVisible(true);
        this.times.add(l);
        this.bars.clear();
        long n = this.times.get(0);
        int i = 1;
        for (int j = 1; j < this.times.size(); ++j) {
            if (j == this.times.size() - 1 || this.times.get(j) - n > 1000L) {
                final long n2 = (this.times.get(j) - n) / 1000L - 1L;
                for (int n3 = 0; n3 < n2; ++n3) {
                    this.bars.add(0);
                }
                this.bars.add(i);
                i = 1;
                n = this.times.get(j);
            }
            else {
                ++i;
            }
        }
        while (this.bars.size() > 30) {
            for (int intValue = this.bars.get(0), k = 0; k < intValue; ++k) {
                this.times.remove(0);
            }
            this.bars.remove(0);
        }
    }
    
    @Override
    public void update() {
        if (!this.isVisible()) {
            return;
        }
        this.setX(20.0);
        this.setY(Core.getInstance().getScreenHeight() - 20 - 36);
        this.setHeight(36.0);
        this.setWidth(149.0);
        super.update();
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        if (UIManager.getClock() != null && !UIManager.getClock().isVisible()) {
            return;
        }
        this.DrawTextureScaledCol(UIElement.white, 0.0, 0.0, this.getWidth(), this.getHeight(), 0.0, 0.0, 0.0, 0.5);
        if (this.bars.isEmpty()) {
            return;
        }
        int n = 0;
        for (int i = 0; i < this.bars.size(); ++i) {
            final float n2 = this.getHeight().intValue() * (Math.min(10, this.bars.get(i)) / 10.0f);
            this.DrawTextureScaledCol(UIElement.white, n, this.getHeight() - n2, 4.0, n2, 1.0, 1.0, 1.0, 0.3499999940395355);
            n += 5;
        }
    }
}
