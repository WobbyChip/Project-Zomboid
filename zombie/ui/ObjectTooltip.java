// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Collection;
import java.util.ArrayList;
import zombie.core.Core;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.textures.Texture;
import zombie.iso.IsoObject;
import zombie.inventory.InventoryItem;

public final class ObjectTooltip extends UIElement
{
    public static float alphaStep;
    public boolean bIsItem;
    public InventoryItem Item;
    public IsoObject Object;
    float alpha;
    int showDelay;
    float targetAlpha;
    Texture texture;
    public int padRight;
    public int padBottom;
    private IsoGameCharacter character;
    private boolean measureOnly;
    private float weightOfStack;
    private static int lineSpacing;
    private static String fontSize;
    private static UIFont font;
    private static Stack<Layout> freeLayouts;
    
    public ObjectTooltip() {
        this.bIsItem = false;
        this.Item = null;
        this.alpha = 0.0f;
        this.showDelay = 0;
        this.targetAlpha = 0.0f;
        this.padRight = 5;
        this.padBottom = 5;
        this.weightOfStack = 0.0f;
        this.texture = Texture.getSharedTexture("black");
        this.width = 130.0f;
        this.height = 130.0f;
        this.defaultDraw = false;
        ObjectTooltip.lineSpacing = TextManager.instance.getFontFromEnum(ObjectTooltip.font).getLineHeight();
        checkFont();
    }
    
    public static void checkFont() {
        if (!ObjectTooltip.fontSize.equals(Core.getInstance().getOptionTooltipFont())) {
            ObjectTooltip.fontSize = Core.getInstance().getOptionTooltipFont();
            if ("Large".equals(ObjectTooltip.fontSize)) {
                ObjectTooltip.font = UIFont.Large;
            }
            else if ("Medium".equals(ObjectTooltip.fontSize)) {
                ObjectTooltip.font = UIFont.Medium;
            }
            else {
                ObjectTooltip.font = UIFont.Small;
            }
            ObjectTooltip.lineSpacing = TextManager.instance.getFontFromEnum(ObjectTooltip.font).getLineHeight();
        }
    }
    
    public UIFont getFont() {
        return ObjectTooltip.font;
    }
    
    public int getLineSpacing() {
        return ObjectTooltip.lineSpacing;
    }
    
    @Override
    public void DrawText(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        if (this.measureOnly) {
            return;
        }
        super.DrawText(uiFont, s, n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void DrawTextCentre(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        if (this.measureOnly) {
            return;
        }
        super.DrawTextCentre(uiFont, s, n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void DrawTextRight(final UIFont uiFont, final String s, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        if (this.measureOnly) {
            return;
        }
        super.DrawTextRight(uiFont, s, n, n2, n3, n4, n5, n6);
    }
    
    public void DrawValueRight(final int i, final int n, final int n2, final boolean b) {
        String string = Integer.valueOf(i).toString();
        float n3 = 0.3f;
        float n4 = 1.0f;
        float n5 = 0.2f;
        final float n6 = 1.0f;
        if (i > 0) {
            string = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, string);
        }
        if ((i < 0 && b) || (i > 0 && !b)) {
            n3 = 0.8f;
            n4 = 0.3f;
            n5 = 0.2f;
        }
        this.DrawTextRight(ObjectTooltip.font, string, n, n2, n3, n4, n5, n6);
    }
    
    public void DrawValueRightNoPlus(final int i, final int n, final int n2) {
        this.DrawTextRight(ObjectTooltip.font, Integer.valueOf(i).toString(), n, n2, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void DrawValueRightNoPlus(final float f, final int n, final int n2) {
        this.DrawTextRight(ObjectTooltip.font, Float.valueOf((int)((f + 0.01) * 10.0) / 10.0f).toString(), n, n2, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    @Override
    public void DrawTextureScaled(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5) {
        if (this.measureOnly) {
            return;
        }
        super.DrawTextureScaled(texture, n, n2, n3, n4, n5);
    }
    
    @Override
    public void DrawTextureScaledAspect(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        if (this.measureOnly) {
            return;
        }
        super.DrawTextureScaledAspect(texture, n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public void DrawProgressBar(final int n, final int n2, final int n3, final int n4, float n5, final double d, final double d2, final double d3, final double d4) {
        if (this.measureOnly) {
            return;
        }
        if (n5 < 0.0f) {
            n5 = 0.0f;
        }
        if (n5 > 1.0f) {
            n5 = 1.0f;
        }
        int n6 = (int)Math.floor(n3 * n5);
        if (n5 > 0.0f && n6 == 0) {
            n6 = 1;
        }
        this.DrawTextureScaledColor(null, (double)n, (double)n2, (double)n6, 3.0, d, d2, d3, d4);
        this.DrawTextureScaledColor(null, n + (double)n6, (double)n2, n3 - (double)n6, 3.0, 0.25, 0.25, 0.25, 1.0);
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        this.setX(this.getX() + n);
        this.setY(this.getY() + n2);
        return Boolean.FALSE;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        this.setX(this.getX() + n);
        this.setY(this.getY() + n2);
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        if (this.alpha <= 0.0f) {
            return;
        }
        if (!this.bIsItem) {
            if (this.Object != null && this.Object.haveSpecialTooltip()) {
                this.Object.DoSpecialTooltip(this, this.Object.square);
            }
        }
        super.render();
    }
    
    public void show(final IsoObject object, final double x, final double y) {
        this.bIsItem = false;
        this.Object = object;
        this.setX(x);
        this.setY(y);
        this.targetAlpha = 0.5f;
        this.showDelay = 15;
        this.alpha = 0.0f;
    }
    
    public void hide() {
        this.Object = null;
        this.showDelay = 0;
        this.setVisible(false);
    }
    
    @Override
    public void update() {
        if (this.alpha <= 0.0f && this.targetAlpha == 0.0f) {
            return;
        }
        if (this.showDelay > 0) {
            if (--this.showDelay == 0) {
                this.setVisible(true);
            }
            return;
        }
        if (this.alpha < this.targetAlpha) {
            this.alpha += ObjectTooltip.alphaStep;
            if (this.alpha > 0.5f) {
                this.alpha = 0.5f;
            }
        }
        else if (this.alpha > this.targetAlpha) {
            this.alpha -= ObjectTooltip.alphaStep;
            if (this.alpha < this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        }
    }
    
    void show(final InventoryItem item, final int n, final int n2) {
        this.Object = null;
        this.Item = item;
        this.bIsItem = true;
        this.setX(this.getX());
        this.setY(this.getY());
        this.targetAlpha = 0.5f;
        this.showDelay = 15;
        this.alpha = 0.0f;
        this.setVisible(true);
    }
    
    public void adjustWidth(final int n, final String s) {
        final int measureStringX = TextManager.instance.MeasureStringX(ObjectTooltip.font, s);
        if (n + measureStringX + this.padRight > this.width) {
            this.setWidth(n + measureStringX + this.padRight);
        }
    }
    
    public Layout beginLayout() {
        Layout layout;
        if (ObjectTooltip.freeLayouts.isEmpty()) {
            layout = new Layout();
        }
        else {
            layout = ObjectTooltip.freeLayouts.pop();
        }
        return layout;
    }
    
    public void endLayout(Layout item) {
        while (item != null) {
            final Layout next = item.next;
            item.free();
            ObjectTooltip.freeLayouts.push(item);
            item = next;
        }
    }
    
    public Texture getTexture() {
        return this.texture;
    }
    
    public void setCharacter(final IsoGameCharacter character) {
        this.character = character;
    }
    
    public IsoGameCharacter getCharacter() {
        return this.character;
    }
    
    public void setMeasureOnly(final boolean measureOnly) {
        this.measureOnly = measureOnly;
    }
    
    public boolean isMeasureOnly() {
        return this.measureOnly;
    }
    
    public float getWeightOfStack() {
        return this.weightOfStack;
    }
    
    public void setWeightOfStack(final float weightOfStack) {
        this.weightOfStack = weightOfStack;
    }
    
    static {
        ObjectTooltip.alphaStep = 0.1f;
        ObjectTooltip.lineSpacing = 14;
        ObjectTooltip.fontSize = "Small";
        ObjectTooltip.font = UIFont.Small;
        ObjectTooltip.freeLayouts = new Stack<Layout>();
    }
    
    public static class LayoutItem
    {
        public String label;
        public float r0;
        public float g0;
        public float b0;
        public float a0;
        public boolean hasValue;
        public String value;
        public boolean rightJustify;
        public float r1;
        public float g1;
        public float b1;
        public float a1;
        public float progressFraction;
        public int labelWidth;
        public int valueWidth;
        public int valueWidthRight;
        public int progressWidth;
        public int height;
        
        public LayoutItem() {
            this.hasValue = false;
            this.rightJustify = false;
            this.progressFraction = -1.0f;
        }
        
        public void reset() {
            this.label = null;
            this.value = null;
            this.hasValue = false;
            this.rightJustify = false;
            this.progressFraction = -1.0f;
        }
        
        public void setLabel(final String label, final float r0, final float g0, final float b0, final float a0) {
            this.label = label;
            this.r0 = r0;
            this.b0 = b0;
            this.g0 = g0;
            this.a0 = a0;
        }
        
        public void setValue(final String value, final float r1, final float g1, final float b1, final float a1) {
            this.value = value;
            this.r1 = r1;
            this.b1 = b1;
            this.g1 = g1;
            this.a1 = a1;
            this.hasValue = true;
        }
        
        public void setValueRight(final int i, final boolean b) {
            this.value = Integer.toString(i);
            if (i > 0) {
                this.value = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.value);
            }
            if ((i < 0 && b) || (i > 0 && !b)) {
                this.r1 = 0.8f;
                this.g1 = 0.3f;
                this.b1 = 0.2f;
            }
            else {
                this.r1 = 0.3f;
                this.g1 = 1.0f;
                this.b1 = 0.2f;
            }
            this.a1 = 1.0f;
            this.hasValue = true;
            this.rightJustify = true;
        }
        
        public void setValueRightNoPlus(float f) {
            f = (int)((f + 0.005f) * 100.0f) / 100.0f;
            this.value = Float.toString(f);
            this.r1 = 1.0f;
            this.g1 = 1.0f;
            this.b1 = 1.0f;
            this.a1 = 1.0f;
            this.hasValue = true;
            this.rightJustify = true;
        }
        
        public void setValueRightNoPlus(final int i) {
            this.value = Integer.toString(i);
            this.r1 = 1.0f;
            this.g1 = 1.0f;
            this.b1 = 1.0f;
            this.a1 = 1.0f;
            this.hasValue = true;
            this.rightJustify = true;
        }
        
        public void setProgress(final float progressFraction, final float r1, final float g1, final float b1, final float a1) {
            this.progressFraction = progressFraction;
            this.r1 = r1;
            this.b1 = b1;
            this.g1 = g1;
            this.a1 = a1;
            this.hasValue = true;
        }
        
        public void calcSizes() {
            final int n = 0;
            this.progressWidth = n;
            this.valueWidthRight = n;
            this.valueWidth = n;
            this.labelWidth = n;
            if (this.label != null) {
                this.labelWidth = TextManager.instance.MeasureStringX(ObjectTooltip.font, this.label);
            }
            if (this.hasValue) {
                if (this.value != null) {
                    final int measureStringX = TextManager.instance.MeasureStringX(ObjectTooltip.font, this.value);
                    this.valueWidth = (this.rightJustify ? 0 : measureStringX);
                    this.valueWidthRight = (this.rightJustify ? measureStringX : false);
                }
                else if (this.progressFraction != -1.0f) {
                    this.progressWidth = 80;
                }
            }
            int n2 = 1;
            if (this.label != null) {
                int b = 1;
                for (int i = 0; i < this.label.length(); ++i) {
                    if (this.label.charAt(i) == '\n') {
                        ++b;
                    }
                }
                n2 = Math.max(n2, b);
            }
            if (this.hasValue && this.value != null) {
                int b2 = 1;
                for (int j = 0; j < this.value.length(); ++j) {
                    if (this.value.charAt(j) == '\n') {
                        ++b2;
                    }
                }
                n2 = Math.max(n2, b2);
            }
            this.height = n2 * ObjectTooltip.lineSpacing;
        }
        
        public void render(final int n, final int n2, final int n3, final int n4, final ObjectTooltip objectTooltip) {
            if (this.label != null) {
                objectTooltip.DrawText(ObjectTooltip.font, this.label, n, n2, this.r0, this.g0, this.b0, this.a0);
            }
            if (this.value != null) {
                if (this.rightJustify) {
                    objectTooltip.DrawTextRight(ObjectTooltip.font, this.value, n + n3 + n4, n2, this.r1, this.g1, this.b1, this.a1);
                }
                else {
                    objectTooltip.DrawText(ObjectTooltip.font, this.value, n + n3, n2, this.r1, this.g1, this.b1, this.a1);
                }
            }
            if (this.progressFraction != -1.0f) {
                objectTooltip.DrawProgressBar(n + n3, n2 + ObjectTooltip.lineSpacing / 2 - 1, this.progressWidth, 2, this.progressFraction, this.r1, this.g1, this.b1, this.a1);
            }
        }
    }
    
    public static class Layout
    {
        public ArrayList<LayoutItem> items;
        public int minLabelWidth;
        public int minValueWidth;
        public Layout next;
        public int nextPadY;
        private static Stack<LayoutItem> freeItems;
        
        public Layout() {
            this.items = new ArrayList<LayoutItem>();
        }
        
        public LayoutItem addItem() {
            LayoutItem e;
            if (Layout.freeItems.isEmpty()) {
                e = new LayoutItem();
            }
            else {
                e = Layout.freeItems.pop();
            }
            e.reset();
            this.items.add(e);
            return e;
        }
        
        public void setMinLabelWidth(final int minLabelWidth) {
            this.minLabelWidth = minLabelWidth;
        }
        
        public void setMinValueWidth(final int minValueWidth) {
            this.minValueWidth = minValueWidth;
        }
        
        public int render(final int n, int n2, final ObjectTooltip objectTooltip) {
            int n3 = this.minLabelWidth;
            int n4 = this.minValueWidth;
            int n5 = this.minValueWidth;
            int max = 0;
            int n6 = 0;
            final int n7 = 8;
            int max2 = 0;
            for (int i = 0; i < this.items.size(); ++i) {
                final LayoutItem layoutItem = this.items.get(i);
                layoutItem.calcSizes();
                if (layoutItem.hasValue) {
                    n3 = Math.max(n3, layoutItem.labelWidth);
                    n4 = Math.max(n4, layoutItem.valueWidth);
                    n5 = Math.max(n5, layoutItem.valueWidthRight);
                    max = Math.max(max, layoutItem.progressWidth);
                    max2 = Math.max(max2, Math.max(layoutItem.labelWidth, this.minLabelWidth) + n7);
                    n6 = Math.max(n6, n3 + n7 + Math.max(Math.max(n4, n5), max));
                }
                else {
                    n3 = Math.max(n3, layoutItem.labelWidth);
                    n6 = Math.max(n6, layoutItem.labelWidth);
                }
            }
            if (n + n6 + objectTooltip.padRight > objectTooltip.width) {
                objectTooltip.setWidth(n + n6 + objectTooltip.padRight);
            }
            for (int j = 0; j < this.items.size(); ++j) {
                final LayoutItem layoutItem2 = this.items.get(j);
                layoutItem2.render(n, n2, max2, n5, objectTooltip);
                n2 += layoutItem2.height;
            }
            if (this.next != null) {
                return this.next.render(n, n2 + this.next.nextPadY, objectTooltip);
            }
            return n2;
        }
        
        public void free() {
            Layout.freeItems.addAll((Collection<?>)this.items);
            this.items.clear();
            this.minLabelWidth = 0;
            this.minValueWidth = 0;
            this.next = null;
            this.nextPadY = 0;
        }
        
        static {
            Layout.freeItems = new Stack<LayoutItem>();
        }
    }
}
