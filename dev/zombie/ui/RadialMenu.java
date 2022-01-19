// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.input.JoypadManager;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.util.StringUtils;
import zombie.input.Mouse;
import zombie.core.SpriteRenderer;
import java.util.ArrayList;

public final class RadialMenu extends UIElement
{
    protected int outerRadius;
    protected int innerRadius;
    protected ArrayList<Slice> slices;
    protected int highlight;
    protected int joypad;
    protected UITransition transition;
    protected UITransition select;
    protected UITransition deselect;
    protected int selectIndex;
    protected int deselectIndex;
    
    public RadialMenu(final int n, final int n2, final int innerRadius, final int outerRadius) {
        this.outerRadius = 200;
        this.innerRadius = 100;
        this.slices = new ArrayList<Slice>();
        this.highlight = -1;
        this.joypad = -1;
        this.transition = new UITransition();
        this.select = new UITransition();
        this.deselect = new UITransition();
        this.selectIndex = -1;
        this.deselectIndex = -1;
        this.setX(n);
        this.setY(n2);
        this.setWidth(outerRadius * 2);
        this.setHeight(outerRadius * 2);
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
    }
    
    @Override
    public void update() {
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        this.transition.setIgnoreUpdateTime(true);
        this.transition.setFadeIn(true);
        this.transition.update();
        if (this.slices.isEmpty()) {
            return;
        }
        final float fraction = this.transition.fraction();
        final float n = this.innerRadius * 0.85f + this.innerRadius * fraction * 0.15f;
        final float n2 = this.outerRadius * 0.85f + this.outerRadius * fraction * 0.15f;
        for (int i = 0; i < 48; ++i) {
            final float n3 = 7.5f;
            final double radians = Math.toRadians(i * n3);
            final double radians2 = Math.toRadians((i + 1) * n3);
            final double n4 = this.x + this.width / 2.0f;
            final double n5 = this.y + this.height / 2.0f;
            final double n6 = this.x + this.width / 2.0f;
            final double n7 = this.y + this.height / 2.0f;
            final double n8 = this.x + this.width / 2.0f + n2 * (float)Math.cos(radians);
            final double n9 = this.y + this.height / 2.0f + n2 * (float)Math.sin(radians);
            final double n10 = this.x + this.width / 2.0f + n2 * (float)Math.cos(radians2);
            double n11 = this.y + this.height / 2.0f + n2 * (float)Math.sin(radians2);
            if (i == 47) {
                n11 = n7;
            }
            SpriteRenderer.instance.renderPoly((float)n4, (float)n5, (float)n8, (float)n9, (float)n10, (float)n11, (float)n6, (float)n7, 0.1f, 0.1f, 0.1f, 0.45f + 0.45f * fraction);
        }
        final float n12 = 360.0f / Math.max(this.slices.size(), 2);
        final float n13 = (this.slices.size() == 1) ? 0.0f : 1.5f;
        int selectIndex = this.highlight;
        if (selectIndex == -1) {
            if (this.joypad != -1) {
                selectIndex = this.getSliceIndexFromJoypad(this.joypad);
            }
            else {
                selectIndex = this.getSliceIndexFromMouse(Mouse.getXA() - this.getAbsoluteX().intValue(), Mouse.getYA() - this.getAbsoluteY().intValue());
            }
        }
        final Slice slice = this.getSlice(selectIndex);
        if (slice != null && slice.isEmpty()) {
            selectIndex = -1;
        }
        if (selectIndex != this.selectIndex) {
            this.select.reset();
            this.select.setIgnoreUpdateTime(true);
            if (this.selectIndex != -1) {
                this.deselectIndex = this.selectIndex;
                this.deselect.reset();
                this.deselect.setFadeIn(false);
                this.deselect.init(66.666664f, true);
            }
            this.selectIndex = selectIndex;
        }
        this.select.update();
        this.deselect.update();
        final float n14 = this.getStartAngle() - 180.0f;
        for (int j = 0; j < this.slices.size(); ++j) {
            for (int max = Math.max(6, 48 / Math.max(this.slices.size(), 2)), k = 0; k < max; ++k) {
                final double radians3 = Math.toRadians(n14 + j * n12 + k * n12 / max + ((k == 0) ? n13 : 0.0f));
                final double radians4 = Math.toRadians(n14 + j * n12 + (k + 1) * n12 / max - ((k == max - 1) ? n13 : 0.0f));
                final double radians5 = Math.toRadians(n14 + j * n12 + k * n12 / max + ((k == 0) ? (n13 / 2.0f) : 0.0f));
                final double radians6 = Math.toRadians(n14 + j * n12 + (k + 1) * n12 / max - ((k == max - 1) ? (n13 / 1.5) : 0.0));
                final double n15 = this.x + this.width / 2.0f + n * (float)Math.cos(radians3);
                final double n16 = this.y + this.height / 2.0f + n * (float)Math.sin(radians3);
                final double n17 = this.x + this.width / 2.0f + n * (float)Math.cos(radians4);
                final double n18 = this.y + this.height / 2.0f + n * (float)Math.sin(radians4);
                final double n19 = this.x + this.width / 2.0f + n2 * (float)Math.cos(radians5);
                final double n20 = this.y + this.height / 2.0f + n2 * (float)Math.sin(radians5);
                final double n21 = this.x + this.width / 2.0f + n2 * (float)Math.cos(radians6);
                final double n22 = this.y + this.height / 2.0f + n2 * (float)Math.sin(radians6);
                final float n23 = 1.0f;
                final float n24 = 1.0f;
                final float n25 = 1.0f;
                float n26 = 0.025f;
                if (j == selectIndex) {
                    n26 = 0.25f + 0.25f * this.select.fraction();
                }
                else if (j == this.deselectIndex) {
                    n26 = 0.025f + 0.475f * this.deselect.fraction();
                }
                SpriteRenderer.instance.renderPoly((float)n15, (float)n16, (float)n19, (float)n20, (float)n21, (float)n22, (float)n17, (float)n18, n23, n24, n25, n26);
            }
            final Texture texture = this.slices.get(j).texture;
            if (texture != null) {
                final double radians7 = Math.toRadians(n14 + j * n12 + n12 / 2.0f);
                this.DrawTexture(texture, 0.0f + this.width / 2.0f + (n + (n2 - n) / 2.0f) * (float)Math.cos(radians7) - texture.getWidth() / 2 - texture.offsetX, 0.0f + this.height / 2.0f + (n + (n2 - n) / 2.0f) * (float)Math.sin(radians7) - texture.getHeight() / 2 - texture.offsetY, fraction);
            }
        }
        if (slice != null && !StringUtils.isNullOrWhitespace(slice.text)) {
            this.formatTextInsideCircle(slice.text);
        }
    }
    
    private void formatTextInsideCircle(final String s) {
        final UIFont medium = UIFont.Medium;
        final AngelCodeFont fontFromEnum = TextManager.instance.getFontFromEnum(medium);
        final int lineHeight = fontFromEnum.getLineHeight();
        int n = 1;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '\n') {
                ++n;
            }
        }
        if (n > 1) {
            final int n2 = n * lineHeight;
            final int n3 = this.getAbsoluteX().intValue() + (int)this.width / 2;
            int n4 = this.getAbsoluteY().intValue() + (int)this.height / 2 - n2 / 2;
            int n5 = 0;
            for (int j = 0; j < s.length(); ++j) {
                if (s.charAt(j) == '\n') {
                    fontFromEnum.drawString((float)(n3 - fontFromEnum.getWidth(s, n5, j) / 2), (float)n4, s, 1.0f, 1.0f, 1.0f, 1.0f, n5, j - 1);
                    n5 = j + 1;
                    n4 += lineHeight;
                }
            }
            if (n5 < s.length()) {
                fontFromEnum.drawString((float)(n3 - fontFromEnum.getWidth(s, n5, s.length() - 1) / 2), (float)n4, s, 1.0f, 1.0f, 1.0f, 1.0f, n5, s.length() - 1);
            }
        }
        else {
            this.DrawTextCentre(medium, s, this.width / 2.0f, this.height / 2.0f - lineHeight / 2, 1.0, 1.0, 1.0, 1.0);
        }
    }
    
    public void clear() {
        this.slices.clear();
        this.transition.reset();
        this.transition.init(66.666664f, false);
        this.selectIndex = -1;
        this.deselectIndex = -1;
    }
    
    public void addSlice(final String text, final Texture texture) {
        final Slice e = new Slice();
        e.text = text;
        e.texture = texture;
        this.slices.add(e);
    }
    
    private Slice getSlice(final int index) {
        if (index < 0 || index >= this.slices.size()) {
            return null;
        }
        return this.slices.get(index);
    }
    
    public void setSliceText(final int n, final String text) {
        final Slice slice = this.getSlice(n);
        if (slice != null) {
            slice.text = text;
        }
    }
    
    public void setSliceTexture(final int n, final Texture texture) {
        final Slice slice = this.getSlice(n);
        if (slice != null) {
            slice.texture = texture;
        }
    }
    
    private float getStartAngle() {
        return 90.0f - 360.0f / Math.max(this.slices.size(), 2) / 2.0f;
    }
    
    public int getSliceIndexFromMouse(final int n, final int n2) {
        final float n3 = 0.0f + this.width / 2.0f;
        final float n4 = 0.0f + this.height / 2.0f;
        final double sqrt = Math.sqrt(Math.pow(n - n3, 2.0) + Math.pow(n2 - n4, 2.0));
        if (sqrt > this.outerRadius || sqrt < this.innerRadius) {
            return -1;
        }
        final double degrees = Math.toDegrees(Math.atan2(n2 - n4, n - n3) + 3.141592653589793);
        final float n5 = 360.0f / Math.max(this.slices.size(), 2);
        if (degrees < this.getStartAngle()) {
            return (int)((degrees + 360.0 - this.getStartAngle()) / n5);
        }
        return (int)((degrees - this.getStartAngle()) / n5);
    }
    
    public int getSliceIndexFromJoypad(final int n) {
        final float aimingAxisX = JoypadManager.instance.getAimingAxisX(n);
        final float aimingAxisY = JoypadManager.instance.getAimingAxisY(n);
        if (Math.abs(aimingAxisX) <= 0.3f && Math.abs(aimingAxisY) <= 0.3f) {
            return -1;
        }
        final double degrees = Math.toDegrees(Math.atan2(-aimingAxisY, -aimingAxisX));
        final float n2 = 360.0f / Math.max(this.slices.size(), 2);
        if (degrees < this.getStartAngle()) {
            return (int)((degrees + 360.0 - this.getStartAngle()) / n2);
        }
        return (int)((degrees - this.getStartAngle()) / n2);
    }
    
    public void setJoypad(final int joypad) {
        this.joypad = joypad;
    }
    
    protected static class Slice
    {
        public String text;
        public Texture texture;
        
        boolean isEmpty() {
            return this.text == null && this.texture == null;
        }
    }
}
