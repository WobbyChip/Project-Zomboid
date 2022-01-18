// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Iterator;
import java.util.Stack;

public final class TextBox extends UIElement
{
    public boolean ResizeParent;
    UIFont font;
    Stack<String> Lines;
    String Text;
    public boolean Centred;
    
    public TextBox(final UIFont font, final int n, final int n2, final int n3, final String text) {
        this.Lines = new Stack<String>();
        this.Centred = false;
        this.font = font;
        this.x = n;
        this.y = n2;
        this.Text = text;
        this.width = (float)n3;
        this.Paginate();
    }
    
    @Override
    public void onresize() {
        this.Paginate();
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        super.render();
        this.Paginate();
        int n = 0;
        for (final String s : this.Lines) {
            if (this.Centred) {
                TextManager.instance.DrawStringCentre(this.font, this.getAbsoluteX().intValue() + this.getWidth() / 2.0, this.getAbsoluteY().intValue() + n, s, 1.0, 1.0, 1.0, 1.0);
            }
            else {
                TextManager.instance.DrawString(this.font, this.getAbsoluteX().intValue(), this.getAbsoluteY().intValue() + n, s, 1.0, 1.0, 1.0, 1.0);
            }
            n += TextManager.instance.MeasureStringY(this.font, this.Lines.get(0));
        }
        this.setHeight(n);
    }
    
    @Override
    public void update() {
        this.Paginate();
        int n = 0;
        for (final String s : this.Lines) {
            n += TextManager.instance.MeasureStringY(this.font, this.Lines.get(0));
        }
        this.setHeight(n);
    }
    
    private void Paginate() {
        int endIndex = 0;
        this.Lines.clear();
        for (String substring : this.Text.split("<br>")) {
            if (substring.length() == 0) {
                this.Lines.add(" ");
            }
            else {
                do {
                    int endIndex2;
                    int n = endIndex2 = substring.indexOf(" ", endIndex + 1);
                    if (endIndex2 == -1) {
                        endIndex2 = substring.length();
                    }
                    if (TextManager.instance.MeasureStringX(this.font, substring.substring(0, endIndex2)) >= this.getWidth()) {
                        final String substring2 = substring.substring(0, endIndex);
                        substring = substring.substring(endIndex + 1);
                        this.Lines.add(substring2);
                        n = 0;
                    }
                    else if (n == -1) {
                        this.Lines.add(substring);
                        break;
                    }
                    endIndex = n;
                } while (substring.length() > 0);
            }
        }
    }
    
    public void SetText(final String text) {
        this.Text = text;
        this.Paginate();
    }
}
