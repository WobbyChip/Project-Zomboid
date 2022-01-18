// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.GameTime;
import zombie.iso.areas.IsoRoom;
import zombie.ui.MoodlesUI;
import zombie.ui.UIManager;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.characters.IsoGameCharacter;

public class IsoCamera
{
    public static final PlayerCamera[] cameras;
    public static IsoGameCharacter CamCharacter;
    public static Vector2 FakePos;
    public static Vector2 FakePosVec;
    public static int TargetTileX;
    public static int TargetTileY;
    public static int PLAYER_OFFSET_X;
    public static int PLAYER_OFFSET_Y;
    public static final FrameState frameState;
    
    public static void init() {
        IsoCamera.PLAYER_OFFSET_Y = -56 / (2 / Core.TileScale);
    }
    
    public static void update() {
        IsoCamera.cameras[IsoPlayer.getPlayerIndex()].update();
    }
    
    public static void updateAll() {
        for (int i = 0; i < 4; ++i) {
            final IsoPlayer camCharacter = IsoPlayer.players[i];
            if (camCharacter != null) {
                IsoCamera.CamCharacter = camCharacter;
                IsoCamera.cameras[i].update();
            }
        }
    }
    
    public static void SetCharacterToFollow(final IsoGameCharacter camCharacter) {
        if (!GameClient.bClient && !GameServer.bServer) {
            IsoCamera.CamCharacter = camCharacter;
            if (IsoCamera.CamCharacter instanceof IsoPlayer && ((IsoPlayer)IsoCamera.CamCharacter).isLocalPlayer() && UIManager.getMoodleUI(((IsoPlayer)IsoCamera.CamCharacter).getPlayerNum()) != null) {
                final int playerNum = ((IsoPlayer)IsoCamera.CamCharacter).getPlayerNum();
                UIManager.getUI().remove(UIManager.getMoodleUI(playerNum));
                UIManager.setMoodleUI(playerNum, new MoodlesUI());
                UIManager.getMoodleUI(playerNum).setCharacter(IsoCamera.CamCharacter);
                UIManager.getUI().add(UIManager.getMoodleUI(playerNum));
            }
        }
    }
    
    public static float getRightClickOffX() {
        return (float)(int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].RightClickX;
    }
    
    public static float getRightClickOffY() {
        return (float)(int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].RightClickY;
    }
    
    public static float getOffX() {
        return IsoCamera.cameras[IsoPlayer.getPlayerIndex()].getOffX();
    }
    
    public static float getTOffX() {
        return IsoCamera.cameras[IsoPlayer.getPlayerIndex()].getTOffX();
    }
    
    public static void setOffX(final float offX) {
        IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffX = offX;
    }
    
    public static float getOffY() {
        return IsoCamera.cameras[IsoPlayer.getPlayerIndex()].getOffY();
    }
    
    public static float getTOffY() {
        return IsoCamera.cameras[IsoPlayer.getPlayerIndex()].getTOffY();
    }
    
    public static void setOffY(final float offY) {
        IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffY = offY;
    }
    
    public static float getLastOffX() {
        return IsoCamera.cameras[IsoPlayer.getPlayerIndex()].getLastOffX();
    }
    
    public static void setLastOffX(final float lastOffX) {
        IsoCamera.cameras[IsoPlayer.getPlayerIndex()].lastOffX = lastOffX;
    }
    
    public static float getLastOffY() {
        return IsoCamera.cameras[IsoPlayer.getPlayerIndex()].getLastOffY();
    }
    
    public static void setLastOffY(final float lastOffY) {
        IsoCamera.cameras[IsoPlayer.getPlayerIndex()].lastOffY = lastOffY;
    }
    
    public static IsoGameCharacter getCamCharacter() {
        return IsoCamera.CamCharacter;
    }
    
    public static void setCamCharacter(final IsoGameCharacter camCharacter) {
        IsoCamera.CamCharacter = camCharacter;
    }
    
    public static Vector2 getFakePos() {
        return IsoCamera.FakePos;
    }
    
    public static void setFakePos(final Vector2 fakePos) {
        IsoCamera.FakePos = fakePos;
    }
    
    public static Vector2 getFakePosVec() {
        return IsoCamera.FakePosVec;
    }
    
    public static void setFakePosVec(final Vector2 fakePosVec) {
        IsoCamera.FakePosVec = fakePosVec;
    }
    
    public static int getTargetTileX() {
        return IsoCamera.TargetTileX;
    }
    
    public static void setTargetTileX(final int targetTileX) {
        IsoCamera.TargetTileX = targetTileX;
    }
    
    public static int getTargetTileY() {
        return IsoCamera.TargetTileY;
    }
    
    public static void setTargetTileY(final int targetTileY) {
        IsoCamera.TargetTileY = targetTileY;
    }
    
    public static int getScreenLeft(final int n) {
        if (n == 1 || n == 3) {
            return Core.getInstance().getScreenWidth() / 2;
        }
        return 0;
    }
    
    public static int getScreenWidth(final int n) {
        if (IsoPlayer.numPlayers > 1) {
            return Core.getInstance().getScreenWidth() / 2;
        }
        return Core.getInstance().getScreenWidth();
    }
    
    public static int getScreenTop(final int n) {
        if (n == 2 || n == 3) {
            return Core.getInstance().getScreenHeight() / 2;
        }
        return 0;
    }
    
    public static int getScreenHeight(final int n) {
        if (IsoPlayer.numPlayers > 2) {
            return Core.getInstance().getScreenHeight() / 2;
        }
        return Core.getInstance().getScreenHeight();
    }
    
    public static int getOffscreenLeft(final int n) {
        if (n == 1 || n == 3) {
            return Core.getInstance().getScreenWidth() / 2;
        }
        return 0;
    }
    
    public static int getOffscreenWidth(final int n) {
        return Core.getInstance().getOffscreenWidth(n);
    }
    
    public static int getOffscreenTop(final int n) {
        if (n >= 2) {
            return Core.getInstance().getScreenHeight() / 2;
        }
        return 0;
    }
    
    public static int getOffscreenHeight(final int n) {
        return Core.getInstance().getOffscreenHeight(n);
    }
    
    static {
        cameras = new PlayerCamera[4];
        for (int i = 0; i < IsoCamera.cameras.length; ++i) {
            IsoCamera.cameras[i] = new PlayerCamera(i);
        }
        IsoCamera.CamCharacter = null;
        IsoCamera.FakePos = new Vector2();
        IsoCamera.FakePosVec = new Vector2();
        IsoCamera.TargetTileX = 0;
        IsoCamera.TargetTileY = 0;
        IsoCamera.PLAYER_OFFSET_X = 0;
        IsoCamera.PLAYER_OFFSET_Y = -56 / (2 / Core.TileScale);
        frameState = new FrameState();
    }
    
    public static class FrameState
    {
        public int frameCount;
        public boolean Paused;
        public int playerIndex;
        public float CamCharacterX;
        public float CamCharacterY;
        public float CamCharacterZ;
        public IsoGameCharacter CamCharacter;
        public IsoGridSquare CamCharacterSquare;
        public IsoRoom CamCharacterRoom;
        public float OffX;
        public float OffY;
        public int OffscreenWidth;
        public int OffscreenHeight;
        
        public void set(final int playerIndex) {
            this.Paused = GameTime.isGamePaused();
            this.playerIndex = playerIndex;
            this.CamCharacter = IsoPlayer.players[playerIndex];
            this.CamCharacterX = this.CamCharacter.getX();
            this.CamCharacterY = this.CamCharacter.getY();
            this.CamCharacterZ = this.CamCharacter.getZ();
            this.CamCharacterSquare = this.CamCharacter.getCurrentSquare();
            this.CamCharacterRoom = ((this.CamCharacterSquare == null) ? null : this.CamCharacterSquare.getRoom());
            this.OffX = IsoCamera.getOffX();
            this.OffY = IsoCamera.getOffY();
            this.OffscreenWidth = IsoCamera.getOffscreenWidth(playerIndex);
            this.OffscreenHeight = IsoCamera.getOffscreenHeight(playerIndex);
        }
    }
}
