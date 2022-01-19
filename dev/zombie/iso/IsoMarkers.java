// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.util.Type;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.characters.IsoPlayer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.network.GameServer;
import java.util.ArrayList;
import java.util.List;

public final class IsoMarkers
{
    public static final IsoMarkers instance;
    private static int NextIsoMarkerID;
    private final List<IsoMarker> markers;
    
    private IsoMarkers() {
        this.markers = new ArrayList<IsoMarker>();
    }
    
    public void init() {
    }
    
    public void reset() {
        this.markers.clear();
    }
    
    public void update() {
        if (GameServer.bServer) {
            return;
        }
        this.updateIsoMarkers();
    }
    
    private void updateIsoMarkers() {
        if (IsoCamera.frameState.playerIndex != 0) {
            return;
        }
        if (this.markers.size() == 0) {
            return;
        }
        for (int i = this.markers.size() - 1; i >= 0; --i) {
            if (this.markers.get(i).isRemoved()) {
                if (this.markers.get(i).hasTempSquareObject()) {
                    this.markers.get(i).removeTempSquareObjects();
                }
                this.markers.remove(i);
            }
        }
        for (int j = 0; j < this.markers.size(); ++j) {
            final IsoMarker isoMarker = this.markers.get(j);
            if (isoMarker.alphaInc) {
                final IsoMarker isoMarker2 = isoMarker;
                isoMarker2.alpha += GameTime.getInstance().getMultiplier() * isoMarker.fadeSpeed;
                if (isoMarker.alpha > isoMarker.alphaMax) {
                    isoMarker.alphaInc = false;
                    isoMarker.alpha = isoMarker.alphaMax;
                }
            }
            else {
                final IsoMarker isoMarker3 = isoMarker;
                isoMarker3.alpha -= GameTime.getInstance().getMultiplier() * isoMarker.fadeSpeed;
                if (isoMarker.alpha < isoMarker.alphaMin) {
                    isoMarker.alphaInc = true;
                    isoMarker.alpha = 0.3f;
                }
            }
        }
    }
    
    public boolean removeIsoMarker(final IsoMarker isoMarker) {
        return this.removeIsoMarker(isoMarker.getID());
    }
    
    public boolean removeIsoMarker(final int n) {
        for (int i = this.markers.size() - 1; i >= 0; --i) {
            if (this.markers.get(i).getID() == n) {
                this.markers.get(i).remove();
                this.markers.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public IsoMarker getIsoMarker(final int n) {
        for (int i = 0; i < this.markers.size(); ++i) {
            if (this.markers.get(i).getID() == n) {
                return this.markers.get(i);
            }
        }
        return null;
    }
    
    public IsoMarker addIsoMarker(final String s, final IsoGridSquare square, final float r, final float g, final float b, final boolean doAlpha, final boolean b2) {
        if (GameServer.bServer) {
            return null;
        }
        final IsoMarker isoMarker = new IsoMarker();
        isoMarker.setSquare(square);
        isoMarker.init(s, square.x, square.y, square.z, square, b2);
        isoMarker.setR(r);
        isoMarker.setG(g);
        isoMarker.setB(b);
        isoMarker.setA(1.0f);
        isoMarker.setDoAlpha(doAlpha);
        isoMarker.setFadeSpeed(0.006f);
        isoMarker.setAlpha(1.0f);
        isoMarker.setAlphaMin(0.3f);
        isoMarker.setAlphaMax(1.0f);
        this.markers.add(isoMarker);
        return isoMarker;
    }
    
    public IsoMarker addIsoMarker(final KahluaTable kahluaTable, final KahluaTable kahluaTable2, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b, final boolean b2) {
        return this.addIsoMarker(kahluaTable, kahluaTable2, isoGridSquare, n, n2, n3, b, b2, 0.006f, 0.3f, 1.0f);
    }
    
    public IsoMarker addIsoMarker(final KahluaTable kahluaTable, final KahluaTable kahluaTable2, final IsoGridSquare square, final float r, final float g, final float b, final boolean doAlpha, final boolean b2, final float fadeSpeed, final float alphaMin, final float alphaMax) {
        if (GameServer.bServer) {
            return null;
        }
        final IsoMarker isoMarker = new IsoMarker();
        isoMarker.init(kahluaTable, kahluaTable2, square.x, square.y, square.z, square, b2);
        isoMarker.setSquare(square);
        isoMarker.setR(r);
        isoMarker.setG(g);
        isoMarker.setB(b);
        isoMarker.setA(1.0f);
        isoMarker.setDoAlpha(doAlpha);
        isoMarker.setFadeSpeed(fadeSpeed);
        isoMarker.setAlpha(0.0f);
        isoMarker.setAlphaMin(alphaMin);
        isoMarker.setAlphaMax(alphaMax);
        this.markers.add(isoMarker);
        return isoMarker;
    }
    
    public void renderIsoMarkers(final IsoCell.PerPlayerRender perPlayerRender, final int n, final int n2) {
        if (GameServer.bServer || this.markers.size() == 0) {
            return;
        }
        final IsoPlayer isoPlayer = IsoPlayer.players[n2];
        if (isoPlayer == null) {
            return;
        }
        for (int i = 0; i < this.markers.size(); ++i) {
            final IsoMarker isoMarker = this.markers.get(i);
            if (isoMarker.z == n && isoMarker.z == isoPlayer.getZ()) {
                if (isoMarker.active) {
                    for (int j = 0; j < isoMarker.textures.size(); ++j) {
                        final Texture texture = isoMarker.textures.get(j);
                        SpriteRenderer.instance.render(texture, IsoUtils.XToScreen(isoMarker.x, isoMarker.y, isoMarker.z, 0) - IsoCamera.cameras[n2].getOffX() - texture.getWidth() / 2.0f, IsoUtils.YToScreen(isoMarker.x, isoMarker.y, isoMarker.z, 0) - IsoCamera.cameras[n2].getOffY() - texture.getHeight(), (float)texture.getWidth(), (float)texture.getHeight(), isoMarker.r, isoMarker.g, isoMarker.b, isoMarker.alpha, null);
                    }
                }
            }
        }
    }
    
    public void renderIsoMarkersDeferred(final IsoCell.PerPlayerRender perPlayerRender, final int n, final int n2) {
        if (GameServer.bServer || this.markers.size() == 0) {
            return;
        }
        final IsoPlayer isoPlayer = IsoPlayer.players[n2];
        if (isoPlayer == null) {
            return;
        }
        for (int i = 0; i < this.markers.size(); ++i) {
            final IsoMarker isoMarker = this.markers.get(i);
            if (isoMarker.z == n && isoMarker.z == isoPlayer.getZ()) {
                if (isoMarker.active) {
                    for (int j = 0; j < isoMarker.overlayTextures.size(); ++j) {
                        final Texture texture = isoMarker.overlayTextures.get(j);
                        SpriteRenderer.instance.render(texture, IsoUtils.XToScreen(isoMarker.x, isoMarker.y, isoMarker.z, 0) - IsoCamera.cameras[n2].getOffX() - texture.getWidth() / 2.0f, IsoUtils.YToScreen(isoMarker.x, isoMarker.y, isoMarker.z, 0) - IsoCamera.cameras[n2].getOffY() - texture.getHeight(), (float)texture.getWidth(), (float)texture.getHeight(), isoMarker.r, isoMarker.g, isoMarker.b, isoMarker.alpha, null);
                    }
                }
            }
        }
    }
    
    public void render() {
        this.update();
    }
    
    static {
        instance = new IsoMarkers();
        IsoMarkers.NextIsoMarkerID = 0;
    }
    
    public static final class IsoMarker
    {
        private int ID;
        private ArrayList<Texture> textures;
        private ArrayList<Texture> overlayTextures;
        private ArrayList<IsoObject> tempObjects;
        private IsoGridSquare square;
        private float x;
        private float y;
        private float z;
        private float r;
        private float g;
        private float b;
        private float a;
        private boolean doAlpha;
        private float fadeSpeed;
        private float alpha;
        private float alphaMax;
        private float alphaMin;
        private boolean alphaInc;
        private boolean active;
        private boolean isRemoved;
        
        public IsoMarker() {
            this.textures = new ArrayList<Texture>();
            this.overlayTextures = new ArrayList<Texture>();
            this.tempObjects = new ArrayList<IsoObject>();
            this.fadeSpeed = 0.006f;
            this.alpha = 0.0f;
            this.alphaMax = 1.0f;
            this.alphaMin = 0.3f;
            this.alphaInc = true;
            this.active = true;
            this.isRemoved = false;
            this.ID = IsoMarkers.NextIsoMarkerID++;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public void remove() {
            this.isRemoved = true;
        }
        
        public boolean isRemoved() {
            return this.isRemoved;
        }
        
        public void init(final KahluaTable kahluaTable, final KahluaTable kahluaTable2, final int n, final int n2, final int n3, final IsoGridSquare square) {
            this.square = square;
            if (kahluaTable != null) {
                for (int len = kahluaTable.len(), i = 1; i <= len; ++i) {
                    final Texture trygetTexture = Texture.trygetTexture(Type.tryCastTo(kahluaTable.rawget(i), String.class));
                    if (trygetTexture != null) {
                        this.textures.add(trygetTexture);
                        this.setPos(n, n2, n3);
                    }
                }
            }
            if (kahluaTable2 != null) {
                for (int len2 = kahluaTable2.len(), j = 1; j <= len2; ++j) {
                    final Texture trygetTexture2 = Texture.trygetTexture(Type.tryCastTo(kahluaTable2.rawget(j), String.class));
                    if (trygetTexture2 != null) {
                        this.overlayTextures.add(trygetTexture2);
                        this.setPos(n, n2, n3);
                    }
                }
            }
        }
        
        public void init(final KahluaTable kahluaTable, final KahluaTable kahluaTable2, final int n, final int n2, final int n3, final IsoGridSquare square, final boolean b) {
            this.square = square;
            if (b) {
                if (kahluaTable != null) {
                    for (int len = kahluaTable.len(), i = 1; i <= len; ++i) {
                        final Texture trygetTexture = Texture.trygetTexture(Type.tryCastTo(kahluaTable.rawget(i), String.class));
                        if (trygetTexture != null) {
                            final IsoObject e = new IsoObject(square.getCell(), square, trygetTexture.getName());
                            this.tempObjects.add(e);
                            this.addTempSquareObject(e);
                            this.setPos(n, n2, n3);
                        }
                    }
                }
            }
            else {
                this.init(kahluaTable, kahluaTable2, n, n2, n3, square);
            }
        }
        
        public void init(final String s, final int n, final int n2, final int n3, final IsoGridSquare square, final boolean b) {
            this.square = square;
            if (b && s != null) {
                final IsoObject new1 = IsoObject.getNew(square, s, s, false);
                this.tempObjects.add(new1);
                this.addTempSquareObject(new1);
                this.setPos(n, n2, n3);
            }
        }
        
        public boolean hasTempSquareObject() {
            return this.tempObjects.size() > 0;
        }
        
        public void addTempSquareObject(final IsoObject isoObject) {
            this.square.localTemporaryObjects.add(isoObject);
        }
        
        public void removeTempSquareObjects() {
            this.square.localTemporaryObjects.clear();
        }
        
        public float getX() {
            return this.x;
        }
        
        public float getY() {
            return this.y;
        }
        
        public float getZ() {
            return this.z;
        }
        
        public float getR() {
            return this.r;
        }
        
        public float getG() {
            return this.g;
        }
        
        public float getB() {
            return this.b;
        }
        
        public float getA() {
            return this.a;
        }
        
        public void setR(final float r) {
            this.r = r;
        }
        
        public void setG(final float g) {
            this.g = g;
        }
        
        public void setB(final float b) {
            this.b = b;
        }
        
        public void setA(final float a) {
            this.a = a;
        }
        
        public float getAlpha() {
            return this.alpha;
        }
        
        public void setAlpha(final float alpha) {
            this.alpha = alpha;
        }
        
        public float getAlphaMax() {
            return this.alphaMax;
        }
        
        public void setAlphaMax(final float alphaMax) {
            this.alphaMax = alphaMax;
        }
        
        public float getAlphaMin() {
            return this.alphaMin;
        }
        
        public void setAlphaMin(final float alphaMin) {
            this.alphaMin = alphaMin;
        }
        
        public boolean isDoAlpha() {
            return this.doAlpha;
        }
        
        public void setDoAlpha(final boolean doAlpha) {
            this.doAlpha = doAlpha;
        }
        
        public float getFadeSpeed() {
            return this.fadeSpeed;
        }
        
        public void setFadeSpeed(final float fadeSpeed) {
            this.fadeSpeed = fadeSpeed;
        }
        
        public IsoGridSquare getSquare() {
            return this.square;
        }
        
        public void setSquare(final IsoGridSquare square) {
            this.square = square;
        }
        
        public void setPos(final int n, final int n2, final int n3) {
            this.x = n + 0.5f;
            this.y = n2 + 0.5f;
            this.z = (float)n3;
        }
        
        public boolean isActive() {
            return this.active;
        }
        
        public void setActive(final boolean active) {
            this.active = active;
        }
    }
}
