// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.Objects;
import zombie.core.znet.SteamUtils;
import zombie.network.CoopMaster;
import zombie.core.Translator;
import java.util.ArrayList;
import zombie.network.ICoopServerMessageListener;

public final class UIServerToolbox extends NewWindow implements ICoopServerMessageListener, UIEventHandler
{
    public static UIServerToolbox instance;
    ScrollBar ScrollBarV;
    UITextBox2 OutputLog;
    private final ArrayList<String> incomingConnections;
    DialogButton buttonAccept;
    DialogButton buttonReject;
    private String externalAddress;
    private String steamID;
    public boolean autoAccept;
    
    public UIServerToolbox(final int n, final int n2) {
        super(n, n2, 10, 10, true);
        this.incomingConnections = new ArrayList<String>();
        this.externalAddress = null;
        this.steamID = null;
        this.autoAccept = false;
        this.ResizeToFitY = false;
        this.visible = true;
        if (UIServerToolbox.instance != null) {
            UIServerToolbox.instance.shutdown();
        }
        UIServerToolbox.instance = this;
        this.width = 340.0f;
        this.height = 325.0f;
        this.OutputLog = new UITextBox2(UIFont.Small, 5, 33, 330, 260, Translator.getText("IGUI_ServerToolBox_Status"), true);
        this.OutputLog.multipleLine = true;
        (this.ScrollBarV = new ScrollBar("ServerToolboxScrollbar", this, (int)(this.OutputLog.getX() + this.OutputLog.getWidth()) - 14, this.OutputLog.getY().intValue() + 4, this.OutputLog.getHeight().intValue() - 8, true)).SetParentTextBox(this.OutputLog);
        this.AddChild(this.OutputLog);
        this.AddChild(this.ScrollBarV);
        this.buttonAccept = new DialogButton(this, 30, 225, Translator.getText("IGUI_ServerToolBox_acccept"), "accept");
        this.buttonReject = new DialogButton(this, 80, 225, Translator.getText("IGUI_ServerToolBox_reject"), "reject");
        this.AddChild(this.buttonAccept);
        this.AddChild(this.buttonReject);
        this.buttonAccept.setVisible(false);
        this.buttonReject.setVisible(false);
        this.PrintLine("\n");
        if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
            CoopMaster.instance.addListener(this);
            CoopMaster.instance.invokeServer("get-parameter", "external-ip", new ICoopServerMessageListener() {
                @Override
                public void OnCoopServerMessage(final String s, final String s2, final String externalAddress) {
                    UIServerToolbox.this.externalAddress = externalAddress;
                    UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_ServerAddress", "null".equals(UIServerToolbox.this.externalAddress) ? Translator.getText("IGUI_ServerToolBox_IPUnknown") : UIServerToolbox.this.externalAddress));
                    UIServerToolbox.this.PrintLine("");
                    UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_AdminPanel"));
                    UIServerToolbox.this.PrintLine("");
                }
            });
            if (SteamUtils.isSteamModeEnabled()) {
                CoopMaster.instance.invokeServer("get-parameter", "steam-id", new ICoopServerMessageListener() {
                    @Override
                    public void OnCoopServerMessage(final String s, final String s2, final String steamID) {
                        UIServerToolbox.this.steamID = steamID;
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_SteamID", UIServerToolbox.this.steamID));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite1"));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite2"));
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite3"));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite4"));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite5"));
                    }
                });
            }
        }
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        super.render();
        this.DrawTextCentre(Translator.getText("IGUI_ServerToolBox_Title"), this.getWidth() / 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
        this.DrawText(Translator.getText("IGUI_ServerToolBox_ExternalIP", "null".equals(this.externalAddress) ? Translator.getText("IGUI_ServerToolBox_IPUnknown") : this.externalAddress), 7.0, 19.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
        if (!this.incomingConnections.isEmpty()) {
            final String s = this.incomingConnections.get(0);
            if (s != null) {
                this.DrawText(Translator.getText("IGUI_ServerToolBox_UserConnecting", s), 10.0, 205.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
            }
        }
    }
    
    @Override
    public void update() {
        if (!this.isVisible()) {
            return;
        }
        if (this.incomingConnections.isEmpty()) {
            this.buttonReject.setVisible(false);
            this.buttonAccept.setVisible(false);
        }
        else {
            this.buttonReject.setVisible(true);
            this.buttonAccept.setVisible(true);
        }
        super.update();
    }
    
    void UpdateViewPos() {
        this.OutputLog.TopLineIndex = this.OutputLog.Lines.size() - this.OutputLog.NumVisibleLines;
        if (this.OutputLog.TopLineIndex < 0) {
            this.OutputLog.TopLineIndex = 0;
        }
        this.ScrollBarV.scrollToBottom();
    }
    
    @Override
    public synchronized void OnCoopServerMessage(final String a, final String s, final String e) {
        if (Objects.equals(a, "login-attempt")) {
            this.PrintLine(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
            if (this.autoAccept) {
                this.PrintLine(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
                CoopMaster.instance.sendMessage("approve-login-attempt", e);
            }
            else {
                this.incomingConnections.add(e);
                this.setVisible(true);
            }
        }
    }
    
    void PrintLine(final String s) {
        this.OutputLog.SetText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.OutputLog.Text, s));
        this.UpdateViewPos();
    }
    
    public void shutdown() {
        if (CoopMaster.instance != null) {
            CoopMaster.instance.removeListener(this);
        }
    }
    
    @Override
    public void DoubleClick(final String s, final int n, final int n2) {
    }
    
    @Override
    public void ModalClick(final String s, final String s2) {
    }
    
    @Override
    public void Selected(final String s, final int n, final int n2) {
        if (Objects.equals(s, "accept")) {
            final String s2 = this.incomingConnections.get(0);
            this.incomingConnections.remove(0);
            this.PrintLine(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
            CoopMaster.instance.sendMessage("approve-login-attempt", s2);
        }
        if (Objects.equals(s, "reject")) {
            final String s3 = this.incomingConnections.get(0);
            this.incomingConnections.remove(0);
            this.PrintLine(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3));
            CoopMaster.instance.sendMessage("reject-login-attempt", s3);
        }
    }
}
