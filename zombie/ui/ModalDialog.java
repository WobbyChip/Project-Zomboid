// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameWindow;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.gameStates.IngameState;
import zombie.core.Core;

public final class ModalDialog extends NewWindow
{
    public boolean bYes;
    public String Name;
    UIEventHandler handler;
    public boolean Clicked;
    
    public ModalDialog(final String name, final String s, final boolean b) {
        super(Core.getInstance().getOffscreenWidth(0) / 2, Core.getInstance().getOffscreenHeight(0) / 2, 470, 10, false);
        this.bYes = false;
        this.handler = null;
        this.Clicked = false;
        this.Name = name;
        this.ResizeToFitY = false;
        this.IgnoreLossControl = true;
        final TextBox textBox = new TextBox(UIFont.Medium, 0, 0, 450, s);
        textBox.Centred = true;
        textBox.ResizeParent = true;
        textBox.update();
        this.Nest(textBox, 20, 10, 20, 10);
        this.update();
        this.height *= 1.3f;
        if (b) {
            this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2 - 40), (float)(this.getHeight().intValue() - 18), "Yes", "Yes"));
            this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2 + 40), (float)(this.getHeight().intValue() - 18), "No", "No"));
        }
        else {
            this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2), (float)(this.getHeight().intValue() - 18), "Ok", "Ok"));
        }
        this.x -= this.width / 2.0f;
        this.y -= this.height / 2.0f;
    }
    
    @Override
    public void ButtonClicked(final String s) {
        if (this.handler != null) {
            this.handler.ModalClick(this.Name, s);
            this.setVisible(false);
            return;
        }
        if (s.equals("Ok")) {
            UIManager.getSpeedControls().SetCurrentGameSpeed(4);
            this.Clicked(s);
            this.Clicked = true;
            this.bYes = true;
            this.setVisible(false);
            IngameState.instance.Paused = false;
        }
        if (s.equals("Yes")) {
            UIManager.getSpeedControls().SetCurrentGameSpeed(4);
            this.Clicked(s);
            this.Clicked = true;
            this.bYes = true;
            this.setVisible(false);
            IngameState.instance.Paused = false;
        }
        if (s.equals("No")) {
            UIManager.getSpeedControls().SetCurrentGameSpeed(4);
            this.Clicked(s);
            this.Clicked = true;
            this.setVisible(this.bYes = false);
            IngameState.instance.Paused = false;
        }
    }
    
    public void Clicked(final String s) {
        if (this.Name.equals("Sleep") && s.equals("Yes")) {
            float n = 12.0f * IsoPlayer.getInstance().getStats().fatigue;
            if (n < 7.0f) {
                n = 7.0f;
            }
            float n2 = n + GameTime.getInstance().getTimeOfDay();
            if (n2 >= 24.0f) {
                n2 -= 24.0f;
            }
            IsoPlayer.getInstance().setForceWakeUpTime((float)(int)n2);
            IsoPlayer.getInstance().setAsleepTime(0.0f);
            TutorialManager.instance.StealControl = true;
            IsoPlayer.getInstance().setAsleep(true);
            UIManager.setbFadeBeforeUI(true);
            UIManager.FadeOut(4.0);
            UIManager.getSpeedControls().SetCurrentGameSpeed(3);
            try {
                GameWindow.save(true);
            }
            catch (FileNotFoundException thrown) {
                Logger.getLogger(ModalDialog.class.getName()).log(Level.SEVERE, null, thrown);
            }
            catch (IOException thrown2) {
                Logger.getLogger(ModalDialog.class.getName()).log(Level.SEVERE, null, thrown2);
            }
        }
        UIManager.Modal.setVisible(false);
    }
}
