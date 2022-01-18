// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import zombie.debug.DebugLog;
import zombie.iso.sprite.IsoCursor;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.network.ServerGUI;
import zombie.network.GameServer;
import zombie.IndieGL;
import zombie.interfaces.ITexture;
import zombie.iso.IsoGridSquare;
import zombie.core.PerformanceSettings;
import zombie.util.Type;
import zombie.GameTime;
import zombie.iso.IsoUtils;
import zombie.characters.IsoPlayer;
import zombie.core.utils.ImageUtils;
import java.util.ArrayList;
import zombie.core.Core;
import zombie.iso.IsoCamera;

public final class MultiTextureFBO2
{
    private final float[] zoomLevelsDefault;
    private float[] zoomLevels;
    public TextureFBO Current;
    public volatile TextureFBO FBOrendered;
    public final float[] zoom;
    public final float[] targetZoom;
    public final float[] startZoom;
    private float zoomedInLevel;
    private float zoomedOutLevel;
    public final boolean[] bAutoZoom;
    public boolean bZoomEnabled;
    
    public MultiTextureFBO2() {
        this.zoomLevelsDefault = new float[] { 2.5f, 2.25f, 2.0f, 1.75f, 1.5f, 1.25f, 1.0f, 0.75f, 0.5f };
        this.FBOrendered = null;
        this.zoom = new float[4];
        this.targetZoom = new float[4];
        this.startZoom = new float[4];
        this.bAutoZoom = new boolean[4];
        this.bZoomEnabled = true;
        for (int i = 0; i < 4; ++i) {
            final float[] zoom = this.zoom;
            final int n = i;
            final float[] targetZoom = this.targetZoom;
            final int n2 = i;
            final float[] startZoom = this.startZoom;
            final int n3 = i;
            final float n4 = 1.0f;
            startZoom[n3] = n4;
            zoom[n] = (targetZoom[n2] = n4);
        }
    }
    
    public int getWidth(final int n) {
        return (int)(IsoCamera.getScreenWidth(n) * this.zoom[n] * (Core.TileScale / 2.0f));
    }
    
    public int getHeight(final int n) {
        return (int)(IsoCamera.getScreenHeight(n) * this.zoom[n] * (Core.TileScale / 2.0f));
    }
    
    public void setTargetZoom(final int n, final float n2) {
        if (this.targetZoom[n] != n2) {
            this.targetZoom[n] = n2;
            this.startZoom[n] = this.zoom[n];
        }
    }
    
    public ArrayList<Integer> getDefaultZoomLevels() {
        final ArrayList<Integer> list = new ArrayList<Integer>();
        final float[] zoomLevelsDefault = this.zoomLevelsDefault;
        for (int i = 0; i < zoomLevelsDefault.length; ++i) {
            list.add(Math.round(zoomLevelsDefault[i] * 100.0f));
        }
        return list;
    }
    
    public void setZoomLevelsFromOption(final String s) {
        this.zoomLevels = this.zoomLevelsDefault;
        if (s == null || s.isEmpty()) {
            return;
        }
        final String[] split = s.split(";");
        if (split.length == 0) {
            return;
        }
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (final String s2 : split) {
            if (!s2.isEmpty()) {
                try {
                    final int int1 = Integer.parseInt(s2);
                    final float[] zoomLevels = this.zoomLevels;
                    final int length2 = zoomLevels.length;
                    int j = 0;
                    while (j < length2) {
                        if (Math.round(zoomLevels[j] * 100.0f) == int1) {
                            if (!list.contains(int1)) {
                                list.add(int1);
                                break;
                            }
                            break;
                        }
                        else {
                            ++j;
                        }
                    }
                }
                catch (NumberFormatException ex) {}
            }
        }
        if (!list.contains(100)) {
            list.add(100);
        }
        list.sort((n, n2) -> n2 - n);
        this.zoomLevels = new float[list.size()];
        for (int k = 0; k < list.size(); ++k) {
            this.zoomLevels[k] = list.get(k) / 100.0f;
        }
    }
    
    public void destroy() {
        if (this.Current == null) {
            return;
        }
        this.Current.destroy();
        this.Current = null;
        this.FBOrendered = null;
        for (int i = 0; i < 4; ++i) {
            this.zoom[i] = (this.targetZoom[i] = 1.0f);
        }
    }
    
    public void create(final int n, final int n2) throws Exception {
        if (!this.bZoomEnabled) {
            return;
        }
        if (this.zoomLevels == null) {
            this.zoomLevels = this.zoomLevelsDefault;
        }
        this.zoomedInLevel = this.zoomLevels[this.zoomLevels.length - 1];
        this.zoomedOutLevel = this.zoomLevels[0];
        this.Current = this.createTexture(ImageUtils.getNextPowerOfTwoHW(n), ImageUtils.getNextPowerOfTwoHW(n2), false);
    }
    
    public void update() {
        final int playerIndex = IsoPlayer.getPlayerIndex();
        if (!this.bZoomEnabled) {
            this.zoom[playerIndex] = (this.targetZoom[playerIndex] = 1.0f);
        }
        if (this.bAutoZoom[playerIndex] && IsoCamera.CamCharacter != null && this.bZoomEnabled) {
            float n = IsoUtils.DistanceTo(IsoCamera.getRightClickOffX(), IsoCamera.getRightClickOffY(), 0.0f, 0.0f) / 300.0f;
            if (n > 1.0f) {
                n = 1.0f;
            }
            float maxZoom = (this.shouldAutoZoomIn() ? this.zoomedInLevel : this.zoomedOutLevel) + n;
            if (maxZoom > this.zoomLevels[0]) {
                maxZoom = this.zoomLevels[0];
            }
            if (IsoCamera.CamCharacter.getVehicle() != null) {
                maxZoom = this.getMaxZoom();
            }
            this.setTargetZoom(playerIndex, maxZoom);
        }
        float n2 = 0.004f * GameTime.instance.getMultiplier() / GameTime.instance.getTrueMultiplier() * ((Core.TileScale == 2) ? 1.5f : 1.5f);
        if (!this.bAutoZoom[playerIndex]) {
            n2 *= 5.0f;
        }
        else if (this.targetZoom[playerIndex] > this.zoom[playerIndex]) {
            n2 *= 1.0f;
        }
        if (this.targetZoom[playerIndex] > this.zoom[playerIndex]) {
            final float[] zoom = this.zoom;
            final int n3 = playerIndex;
            zoom[n3] += n2;
            IsoPlayer.players[playerIndex].dirtyRecalcGridStackTime = 2.0f;
            if (this.zoom[playerIndex] > this.targetZoom[playerIndex] || Math.abs(this.zoom[playerIndex] - this.targetZoom[playerIndex]) < 0.001f) {
                this.zoom[playerIndex] = this.targetZoom[playerIndex];
            }
        }
        if (this.targetZoom[playerIndex] < this.zoom[playerIndex]) {
            final float[] zoom2 = this.zoom;
            final int n4 = playerIndex;
            zoom2[n4] -= n2;
            IsoPlayer.players[playerIndex].dirtyRecalcGridStackTime = 2.0f;
            if (this.zoom[playerIndex] < this.targetZoom[playerIndex] || Math.abs(this.zoom[playerIndex] - this.targetZoom[playerIndex]) < 0.001f) {
                this.zoom[playerIndex] = this.targetZoom[playerIndex];
            }
        }
        this.setCameraToCentre();
    }
    
    private boolean shouldAutoZoomIn() {
        if (IsoCamera.CamCharacter == null) {
            return false;
        }
        final IsoGridSquare currentSquare = IsoCamera.CamCharacter.getCurrentSquare();
        if (currentSquare != null && !currentSquare.isOutside()) {
            return true;
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(IsoCamera.CamCharacter, IsoPlayer.class);
        return isoPlayer != null && !isoPlayer.isRunning() && !isoPlayer.isSprinting() && ((isoPlayer.closestZombie < 6.0f && isoPlayer.isTargetedByZombie()) || isoPlayer.lastTargeted < PerformanceSettings.getLockFPS() * 4);
    }
    
    private void setCameraToCentre() {
        IsoCamera.cameras[IsoPlayer.getPlayerIndex()].center();
    }
    
    private TextureFBO createTexture(final int n, final int n2, final boolean b) {
        if (b) {
            new TextureFBO(new Texture(n, n2, 16)).destroy();
            return null;
        }
        return new TextureFBO(new Texture(n, n2, 19));
    }
    
    public void render() {
        if (this.Current == null) {
            return;
        }
        int a = 0;
        for (int i = 3; i >= 0; --i) {
            if (IsoPlayer.players[i] != null) {
                a = ((i > 1) ? 3 : i);
                break;
            }
        }
        for (int max = Math.max(a, IsoPlayer.numPlayers - 1), j = 0; j <= max; ++j) {
            if (Core.getInstance().RenderShader != null) {
                IndieGL.StartShader(Core.getInstance().RenderShader, j);
            }
            final int screenLeft = IsoCamera.getScreenLeft(j);
            final int screenTop = IsoCamera.getScreenTop(j);
            final int screenWidth = IsoCamera.getScreenWidth(j);
            final int screenHeight = IsoCamera.getScreenHeight(j);
            if (IsoPlayer.players[j] == null && (!GameServer.bServer || !ServerGUI.isCreated())) {
                SpriteRenderer.instance.renderi(null, screenLeft, screenTop, screenWidth, screenHeight, 0.0f, 0.0f, 0.0f, 1.0f, null);
            }
            else {
                ((Texture)this.Current.getTexture()).rendershader2((float)screenLeft, (float)screenTop, (float)screenWidth, (float)screenHeight, screenLeft, screenTop, screenWidth, screenHeight, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (Core.getInstance().RenderShader != null) {
            IndieGL.EndShader();
        }
        IsoCursor.getInstance().render(0);
    }
    
    public TextureFBO getCurrent(final int n) {
        return this.Current;
    }
    
    public Texture getTexture(final int n) {
        return (Texture)this.Current.getTexture();
    }
    
    public void doZoomScroll(final int n, final int n2) {
        this.targetZoom[n] = this.getNextZoom(n, n2);
    }
    
    public float getNextZoom(final int n, final int n2) {
        if (!this.bZoomEnabled || this.zoomLevels == null) {
            return 1.0f;
        }
        if (n2 > 0) {
            for (int i = this.zoomLevels.length - 1; i > 0; --i) {
                if (this.targetZoom[n] == this.zoomLevels[i]) {
                    return this.zoomLevels[i - 1];
                }
            }
        }
        else if (n2 < 0) {
            for (int j = 0; j < this.zoomLevels.length - 1; ++j) {
                if (this.targetZoom[n] == this.zoomLevels[j]) {
                    return this.zoomLevels[j + 1];
                }
            }
        }
        return this.targetZoom[n];
    }
    
    public float getMinZoom() {
        if (!this.bZoomEnabled || this.zoomLevels == null || this.zoomLevels.length == 0) {
            return 1.0f;
        }
        return this.zoomLevels[this.zoomLevels.length - 1];
    }
    
    public float getMaxZoom() {
        if (!this.bZoomEnabled || this.zoomLevels == null || this.zoomLevels.length == 0) {
            return 1.0f;
        }
        return this.zoomLevels[0];
    }
    
    public boolean test() {
        try {
            this.createTexture(16, 16, true);
        }
        catch (Exception ex) {
            DebugLog.General.error((Object)"Failed to create Test FBO");
            ex.printStackTrace();
            Core.SafeMode = true;
            return false;
        }
        return true;
    }
}
