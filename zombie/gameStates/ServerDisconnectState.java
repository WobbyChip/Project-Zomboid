// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import zombie.input.GameKeyboard;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.chat.ChatElement;
import zombie.ui.TextDrawObject;
import zombie.IndieGL;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.core.SpriteRenderer;
import zombie.characters.IsoPlayer;
import zombie.Lua.LuaManager;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.ui.UIManager;
import zombie.ui.TutorialManager;

public final class ServerDisconnectState extends GameState
{
    private boolean keyDown;
    private int gridX;
    private int gridY;
    
    public ServerDisconnectState() {
        this.keyDown = false;
        this.gridX = -1;
        this.gridY = -1;
    }
    
    @Override
    public void enter() {
        TutorialManager.instance.StealControl = false;
        UIManager.UI.clear();
        LuaEventManager.ResetCallbacks();
        LuaManager.call("ISServerDisconnectUI_OnServerDisconnectUI", GameWindow.kickReason);
    }
    
    @Override
    public void exit() {
        GameWindow.kickReason = null;
    }
    
    @Override
    public void render() {
        boolean b = true;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] == null) {
                if (i == 0) {
                    SpriteRenderer.instance.prePopulating();
                }
            }
            else {
                IsoPlayer.setInstance(IsoPlayer.players[i]);
                IsoCamera.CamCharacter = IsoPlayer.players[i];
                Core.getInstance().StartFrame(i, b);
                IsoCamera.frameState.set(i);
                b = false;
                IsoSprite.globalOffsetX = -1.0f;
                IsoWorld.instance.render();
                Core.getInstance().EndFrame(i);
            }
        }
        Core.getInstance().RenderOffScreenBuffer();
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            if (IsoPlayer.players[j] != null) {
                Core.getInstance().StartFrameText(j);
                IndieGL.disableAlphaTest();
                IndieGL.glDisable(2929);
                TextDrawObject.RenderBatch(j);
                ChatElement.RenderBatch(j);
                try {
                    Core.getInstance().EndFrameText(j);
                }
                catch (Exception ex) {}
            }
        }
        if (Core.getInstance().StartFrameUI()) {
            UIManager.render();
            String kickReason = GameWindow.kickReason;
            if (kickReason == null || kickReason.isEmpty()) {
                kickReason = "Connection to server lost";
            }
            TextManager.instance.DrawStringCentre(UIFont.Medium, Core.getInstance().getScreenWidth() / 2, Core.getInstance().getScreenHeight() / 2, kickReason, 1.0, 1.0, 1.0, 1.0);
        }
        Core.getInstance().EndFrameUI();
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        if (Core.bExiting || GameKeyboard.isKeyDown(1)) {
            return GameStateMachine.StateAction.Continue;
        }
        UIManager.update();
        return GameStateMachine.StateAction.Remain;
    }
}
