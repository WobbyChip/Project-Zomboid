// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;

public class SearchMode
{
    private static SearchMode instance;
    private float fadeTime;
    private PlayerSearchMode[] plrModes;
    
    public static SearchMode getInstance() {
        if (SearchMode.instance == null) {
            SearchMode.instance = new SearchMode();
        }
        return SearchMode.instance;
    }
    
    private SearchMode() {
        this.fadeTime = 1.0f;
        this.plrModes = new PlayerSearchMode[4];
        for (int i = 0; i < this.plrModes.length; ++i) {
            this.plrModes[i] = new PlayerSearchMode(i, this);
            this.plrModes[i].blur.setTargets(1.0f, 1.0f);
            this.plrModes[i].desat.setTargets(0.85f, 0.85f);
            this.plrModes[i].radius.setTargets(4.0f, 4.0f);
            this.plrModes[i].darkness.setTargets(0.0f, 0.0f);
            this.plrModes[i].gradientWidth.setTargets(4.0f, 4.0f);
        }
    }
    
    public PlayerSearchMode getSearchModeForPlayer(final int n) {
        return this.plrModes[n];
    }
    
    public float getFadeTime() {
        return this.fadeTime;
    }
    
    public void setFadeTime(final float fadeTime) {
        this.fadeTime = fadeTime;
    }
    
    public boolean isOverride(final int n) {
        return this.plrModes[n].override;
    }
    
    public void setOverride(final int n, final boolean override) {
        this.plrModes[n].override = override;
    }
    
    public SearchModeFloat getRadius(final int n) {
        return this.plrModes[n].radius;
    }
    
    public SearchModeFloat getGradientWidth(final int n) {
        return this.plrModes[n].gradientWidth;
    }
    
    public SearchModeFloat getBlur(final int n) {
        return this.plrModes[n].blur;
    }
    
    public SearchModeFloat getDesat(final int n) {
        return this.plrModes[n].desat;
    }
    
    public SearchModeFloat getDarkness(final int n) {
        return this.plrModes[n].darkness;
    }
    
    public boolean isEnabled(final int n) {
        return this.plrModes[n].enabled;
    }
    
    public void setEnabled(final int n, final boolean b) {
        final PlayerSearchMode playerSearchMode = this.plrModes[n];
        if (b && !playerSearchMode.enabled) {
            playerSearchMode.enabled = true;
            this.FadeIn(n);
        }
        else if (!b && playerSearchMode.enabled) {
            playerSearchMode.enabled = false;
            this.FadeOut(n);
        }
    }
    
    private void FadeIn(final int n) {
        final PlayerSearchMode playerSearchMode = this.plrModes[n];
        playerSearchMode.timer = Math.max(playerSearchMode.timer, 0.0f);
        playerSearchMode.doFadeIn = true;
        playerSearchMode.doFadeOut = false;
    }
    
    private void FadeOut(final int n) {
        final PlayerSearchMode playerSearchMode = this.plrModes[n];
        playerSearchMode.timer = Math.min(playerSearchMode.timer, this.fadeTime);
        playerSearchMode.doFadeIn = false;
        playerSearchMode.doFadeOut = true;
    }
    
    public void update() {
        for (int i = 0; i < this.plrModes.length; ++i) {
            this.plrModes[i].update();
        }
    }
    
    public static void reset() {
        SearchMode.instance = null;
    }
    
    public static class SearchModeFloat
    {
        private final float min;
        private final float max;
        private final float stepsize;
        private float exterior;
        private float targetExterior;
        private float interior;
        private float targetInterior;
        
        private SearchModeFloat(final float min, final float max, final float stepsize) {
            this.min = min;
            this.max = max;
            this.stepsize = stepsize;
        }
        
        public void set(final float exterior, final float targetExterior, final float interior, final float targetInterior) {
            this.setExterior(exterior);
            this.setTargetExterior(targetExterior);
            this.setInterior(interior);
            this.setTargetInterior(targetInterior);
        }
        
        public void setTargets(final float targetExterior, final float targetInterior) {
            this.setTargetExterior(targetExterior);
            this.setTargetInterior(targetInterior);
        }
        
        public float getExterior() {
            return this.exterior;
        }
        
        public void setExterior(final float exterior) {
            this.exterior = exterior;
        }
        
        public float getTargetExterior() {
            return this.targetExterior;
        }
        
        public void setTargetExterior(final float targetExterior) {
            this.targetExterior = targetExterior;
        }
        
        public float getInterior() {
            return this.interior;
        }
        
        public void setInterior(final float interior) {
            this.interior = interior;
        }
        
        public float getTargetInterior() {
            return this.targetInterior;
        }
        
        public void setTargetInterior(final float targetInterior) {
            this.targetInterior = targetInterior;
        }
        
        public void update(final float n) {
            this.exterior = n * this.targetExterior;
            this.interior = n * this.targetInterior;
        }
        
        public void equalise() {
            if (!PZMath.equal(this.exterior, this.targetExterior, 0.001f)) {
                this.exterior = PZMath.lerp(this.exterior, this.targetExterior, 0.01f);
            }
            else {
                this.exterior = this.targetExterior;
            }
            if (!PZMath.equal(this.interior, this.targetInterior, 0.001f)) {
                this.interior = PZMath.lerp(this.interior, this.targetInterior, 0.01f);
            }
            else {
                this.interior = this.targetInterior;
            }
        }
        
        public void reset() {
            this.exterior = 0.0f;
            this.interior = 0.0f;
        }
        
        public float getMin() {
            return this.min;
        }
        
        public float getMax() {
            return this.max;
        }
        
        public float getStepsize() {
            return this.stepsize;
        }
    }
    
    public static class PlayerSearchMode
    {
        private final int plrIndex;
        private final SearchMode parent;
        private boolean override;
        private boolean enabled;
        private final SearchModeFloat radius;
        private final SearchModeFloat gradientWidth;
        private final SearchModeFloat blur;
        private final SearchModeFloat desat;
        private final SearchModeFloat darkness;
        private float timer;
        private boolean doFadeOut;
        private boolean doFadeIn;
        
        public PlayerSearchMode(final int plrIndex, final SearchMode parent) {
            this.override = false;
            this.enabled = false;
            this.radius = new SearchModeFloat(0.0f, 50.0f, 1.0f);
            this.gradientWidth = new SearchModeFloat(0.0f, 20.0f, 1.0f);
            this.blur = new SearchModeFloat(0.0f, 1.0f, 0.01f);
            this.desat = new SearchModeFloat(0.0f, 1.0f, 0.01f);
            this.darkness = new SearchModeFloat(0.0f, 1.0f, 0.01f);
            this.plrIndex = plrIndex;
            this.parent = parent;
        }
        
        public boolean isShaderEnabled() {
            return this.enabled || this.doFadeIn || this.doFadeOut;
        }
        
        private boolean isPlayerExterior() {
            final IsoPlayer isoPlayer = IsoPlayer.players[this.plrIndex];
            return isoPlayer != null && isoPlayer.getCurrentSquare() != null && !isoPlayer.getCurrentSquare().isInARoom();
        }
        
        public float getShaderBlur() {
            return this.isPlayerExterior() ? this.blur.getExterior() : this.blur.getInterior();
        }
        
        public float getShaderDesat() {
            return this.isPlayerExterior() ? this.desat.getExterior() : this.desat.getInterior();
        }
        
        public float getShaderRadius() {
            return this.isPlayerExterior() ? this.radius.getExterior() : this.radius.getInterior();
        }
        
        public float getShaderGradientWidth() {
            return this.isPlayerExterior() ? this.gradientWidth.getExterior() : this.gradientWidth.getInterior();
        }
        
        public float getShaderDarkness() {
            return this.isPlayerExterior() ? this.darkness.getExterior() : this.darkness.getInterior();
        }
        
        public SearchModeFloat getBlur() {
            return this.blur;
        }
        
        public SearchModeFloat getDesat() {
            return this.desat;
        }
        
        public SearchModeFloat getRadius() {
            return this.radius;
        }
        
        public SearchModeFloat getGradientWidth() {
            return this.gradientWidth;
        }
        
        public SearchModeFloat getDarkness() {
            return this.darkness;
        }
        
        private void update() {
            if (this.override) {
                return;
            }
            if (this.doFadeIn) {
                this.timer += GameTime.getInstance().getTimeDelta();
                this.timer = PZMath.clamp(this.timer, 0.0f, this.parent.fadeTime);
                final float clamp = PZMath.clamp(this.timer / this.parent.fadeTime, 0.0f, 1.0f);
                this.blur.update(clamp);
                this.desat.update(clamp);
                this.radius.update(clamp);
                this.darkness.update(clamp);
                this.gradientWidth.equalise();
                if (this.timer >= this.parent.fadeTime) {
                    this.doFadeIn = false;
                }
                return;
            }
            if (this.doFadeOut) {
                this.timer -= GameTime.getInstance().getTimeDelta();
                this.timer = PZMath.clamp(this.timer, 0.0f, this.parent.fadeTime);
                final float clamp2 = PZMath.clamp(this.timer / this.parent.fadeTime, 0.0f, 1.0f);
                this.blur.update(clamp2);
                this.desat.update(clamp2);
                this.radius.update(clamp2);
                this.darkness.update(clamp2);
                this.gradientWidth.equalise();
                if (this.timer <= 0.0f) {
                    this.doFadeOut = false;
                }
                return;
            }
            if (this.enabled) {
                this.blur.equalise();
                this.desat.equalise();
                this.radius.equalise();
                this.darkness.equalise();
                this.gradientWidth.equalise();
            }
            else {
                this.blur.reset();
                this.desat.reset();
                this.radius.reset();
                this.darkness.reset();
                this.gradientWidth.equalise();
            }
        }
    }
}
