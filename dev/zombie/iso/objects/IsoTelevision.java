// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.characters.IsoPlayer;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.radio.ZomboidRadio;
import zombie.iso.LightingJNI;
import zombie.core.Rand;
import zombie.GameTime;
import zombie.iso.IsoWorld;
import zombie.iso.IsoLightSource;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.sprite.IsoSprite;
import java.util.ArrayList;

public class IsoTelevision extends IsoWaveSignal
{
    protected ArrayList<IsoSprite> screenSprites;
    protected boolean defaultToNoise;
    private IsoSprite cacheObjectSprite;
    protected IsoDirections facing;
    private boolean hasSetupScreens;
    private boolean tickIsLightUpdate;
    private Screens currentScreen;
    private int spriteIndex;
    
    @Override
    public String getObjectName() {
        return "Television";
    }
    
    public IsoTelevision(final IsoCell isoCell) {
        super(isoCell);
        this.screenSprites = new ArrayList<IsoSprite>();
        this.defaultToNoise = false;
        this.facing = IsoDirections.Max;
        this.hasSetupScreens = false;
        this.tickIsLightUpdate = false;
        this.currentScreen = Screens.OFFSCREEN;
        this.spriteIndex = 0;
    }
    
    public IsoTelevision(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.screenSprites = new ArrayList<IsoSprite>();
        this.defaultToNoise = false;
        this.facing = IsoDirections.Max;
        this.hasSetupScreens = false;
        this.tickIsLightUpdate = false;
        this.currentScreen = Screens.OFFSCREEN;
        this.spriteIndex = 0;
    }
    
    @Override
    protected void init(final boolean b) {
        super.init(b);
    }
    
    private void setupDefaultScreens() {
        this.hasSetupScreens = true;
        this.cacheObjectSprite = this.sprite;
        if (this.screenSprites.size() == 0) {
            for (int i = 16; i <= 64; i += 16) {
                final IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.sprite.getName(), i);
                if (sprite != null) {
                    this.addTvScreenSprite(sprite);
                }
            }
        }
        this.facing = IsoDirections.Max;
        if (this.sprite != null && this.sprite.getProperties().Is("Facing")) {
            final String val = this.sprite.getProperties().Val("Facing");
            switch (val) {
                case "N": {
                    this.facing = IsoDirections.N;
                    break;
                }
                case "S": {
                    this.facing = IsoDirections.S;
                    break;
                }
                case "W": {
                    this.facing = IsoDirections.W;
                    break;
                }
                case "E": {
                    this.facing = IsoDirections.E;
                    break;
                }
            }
        }
    }
    
    @Override
    public void update() {
        super.update();
        if (this.cacheObjectSprite != null && this.cacheObjectSprite != this.sprite) {
            this.hasSetupScreens = false;
            this.screenSprites.clear();
            this.currentScreen = Screens.OFFSCREEN;
            this.nextLightUpdate = 0.0f;
        }
        if (!this.hasSetupScreens) {
            this.setupDefaultScreens();
        }
        this.updateTvScreen();
    }
    
    @Override
    protected void updateLightSource() {
        this.tickIsLightUpdate = false;
        if (this.lightSource == null) {
            this.lightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.0f, 0.0f, 1.0f, this.lightSourceRadius);
            this.lightWasRemoved = true;
        }
        if (this.lightWasRemoved) {
            IsoWorld.instance.CurrentCell.addLamppost(this.lightSource);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0f;
            this.lightWasRemoved = false;
        }
        this.lightUpdateCnt += GameTime.getInstance().getMultiplier();
        if (this.lightUpdateCnt >= this.nextLightUpdate) {
            float n = 0.0f;
            float nextLightUpdate;
            if (!this.hasChatToDisplay()) {
                n = 0.6f;
                nextLightUpdate = (float)Rand.Next(200, 400);
            }
            else {
                nextLightUpdate = (float)Rand.Next(15, 300);
            }
            final float next = Rand.Next(n, 1.0f);
            this.tickIsLightUpdate = true;
            final float r = 0.58f + 0.25f * next;
            final float next2 = Rand.Next(0.65f, 0.85f);
            final int radius = 1 + (int)((this.lightSourceRadius - 1) * next);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0f;
            this.lightSource.setRadius(radius);
            this.lightSource.setR(r);
            this.lightSource.setG(next2);
            this.lightSource.setB(next2);
            if (LightingJNI.init && this.lightSource.ID != 0) {
                LightingJNI.setLightColor(this.lightSource.ID, this.lightSource.getR(), this.lightSource.getG(), this.lightSource.getB());
            }
            this.lightUpdateCnt = 0.0f;
            this.nextLightUpdate = nextLightUpdate;
        }
    }
    
    private void setScreen(final Screens currentScreen) {
        if (currentScreen == Screens.OFFSCREEN) {
            this.currentScreen = Screens.OFFSCREEN;
            if (this.overlaySprite != null) {
                this.overlaySprite = null;
            }
            return;
        }
        if (this.currentScreen != currentScreen || currentScreen == Screens.ALTERNATESCREEN) {
            this.currentScreen = currentScreen;
            IsoSprite overlaySprite = null;
            switch (currentScreen) {
                case TESTSCREEN: {
                    if (this.screenSprites.size() > 0) {
                        overlaySprite = this.screenSprites.get(0);
                        break;
                    }
                    break;
                }
                case DEFAULTSCREEN: {
                    if (this.screenSprites.size() > 1) {
                        overlaySprite = this.screenSprites.get(1);
                        break;
                    }
                    break;
                }
                case ALTERNATESCREEN: {
                    if (this.screenSprites.size() == 3) {
                        overlaySprite = this.screenSprites.get(2);
                        break;
                    }
                    if (this.screenSprites.size() > 3) {
                        ++this.spriteIndex;
                        if (this.spriteIndex < 2) {
                            this.spriteIndex = 2;
                        }
                        if (this.spriteIndex > this.screenSprites.size() - 1) {
                            this.spriteIndex = 2;
                        }
                        overlaySprite = this.screenSprites.get(this.spriteIndex);
                        break;
                    }
                    break;
                }
            }
            this.overlaySprite = overlaySprite;
        }
    }
    
    protected void updateTvScreen() {
        if (this.deviceData != null && this.deviceData.getIsTurnedOn() && this.screenSprites.size() > 0) {
            if (this.deviceData.isReceivingSignal() || this.deviceData.isPlayingMedia()) {
                if (this.tickIsLightUpdate || this.currentScreen != Screens.ALTERNATESCREEN) {
                    this.setScreen(Screens.ALTERNATESCREEN);
                }
            }
            else if (ZomboidRadio.POST_RADIO_SILENCE) {
                this.setScreen(Screens.TESTSCREEN);
            }
            else {
                this.setScreen(Screens.DEFAULTSCREEN);
            }
        }
        else if (this.currentScreen != Screens.OFFSCREEN) {
            this.setScreen(Screens.OFFSCREEN);
        }
    }
    
    public void addTvScreenSprite(final IsoSprite e) {
        this.screenSprites.add(e);
    }
    
    public void clearTvScreenSprites() {
        this.screenSprites.clear();
    }
    
    public void removeTvScreenSprite(final IsoSprite o) {
        this.screenSprites.remove(o);
    }
    
    @Override
    public void renderlast() {
        super.renderlast();
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.overlaySprite = null;
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
    }
    
    public boolean isFacing(final IsoPlayer isoPlayer) {
        if (isoPlayer == null || !isoPlayer.isLocalPlayer()) {
            return false;
        }
        if (this.getObjectIndex() == -1) {
            return false;
        }
        if (!this.square.isCanSee(isoPlayer.PlayerIndex)) {
            return false;
        }
        if (this.facing == IsoDirections.Max) {
            return false;
        }
        switch (this.facing) {
            case N: {
                return isoPlayer.y < this.square.y && (isoPlayer.dir == IsoDirections.SW || isoPlayer.dir == IsoDirections.S || isoPlayer.dir == IsoDirections.SE);
            }
            case S: {
                return isoPlayer.y >= this.square.y + 1 && (isoPlayer.dir == IsoDirections.NW || isoPlayer.dir == IsoDirections.N || isoPlayer.dir == IsoDirections.NE);
            }
            case W: {
                return isoPlayer.x < this.square.x && (isoPlayer.dir == IsoDirections.SE || isoPlayer.dir == IsoDirections.E || isoPlayer.dir == IsoDirections.NE);
            }
            case E: {
                return isoPlayer.x >= this.square.x + 1 && (isoPlayer.dir == IsoDirections.SW || isoPlayer.dir == IsoDirections.W || isoPlayer.dir == IsoDirections.NW);
            }
            default: {
                return false;
            }
        }
    }
    
    private enum Screens
    {
        OFFSCREEN, 
        TESTSCREEN, 
        DEFAULTSCREEN, 
        ALTERNATESCREEN;
        
        private static /* synthetic */ Screens[] $values() {
            return new Screens[] { Screens.OFFSCREEN, Screens.TESTSCREEN, Screens.DEFAULTSCREEN, Screens.ALTERNATESCREEN };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
