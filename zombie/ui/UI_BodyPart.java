// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.core.textures.Texture;
import zombie.characters.IsoGameCharacter;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.Color;

public final class UI_BodyPart extends UIElement
{
    public float alpha;
    public final Color color;
    public BodyPartType BodyPartType;
    public boolean IsFlipped;
    public float MaxOscilatorRate;
    public float MinOscilatorRate;
    public float Oscilator;
    public float OscilatorRate;
    public float OscilatorStep;
    IsoGameCharacter chr;
    boolean mouseOver;
    Texture scratchTex;
    Texture bandageTex;
    Texture dirtyBandageTex;
    Texture infectionTex;
    Texture deepWoundTex;
    Texture stitchTex;
    Texture biteTex;
    Texture glassTex;
    Texture boneTex;
    Texture splintTex;
    Texture burnTex;
    Texture bulletTex;
    
    public UI_BodyPart(final BodyPartType bodyPartType, final int n, final int n2, final String s, final IsoGameCharacter chr, final boolean isFlipped) {
        this.alpha = 1.0f;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.IsFlipped = false;
        this.MaxOscilatorRate = 0.58f;
        this.MinOscilatorRate = 0.025f;
        this.Oscilator = 0.0f;
        this.OscilatorRate = 0.02f;
        this.OscilatorStep = 0.0f;
        this.mouseOver = false;
        String s2 = "male";
        if (chr.isFemale()) {
            s2 = "female";
        }
        this.chr = chr;
        this.BodyPartType = bodyPartType;
        this.scratchTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.bandageTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.dirtyBandageTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.infectionTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.biteTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.deepWoundTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.stitchTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.glassTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.boneTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.splintTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.burnTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.bulletTex = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        this.x = n;
        this.y = n2;
        this.width = (float)this.scratchTex.getWidth();
        this.height = (float)this.scratchTex.getHeight();
        this.IsFlipped = isFlipped;
    }
    
    @Override
    public void onMouseMoveOutside(final double n, final double n2) {
        this.mouseOver = false;
    }
    
    @Override
    public void render() {
        BodyDamage bodyDamage = this.chr.getBodyDamage();
        if (GameClient.bClient && this.chr instanceof IsoPlayer && !((IsoPlayer)this.chr).isLocalPlayer()) {
            bodyDamage = this.chr.getBodyDamageRemote();
        }
        if (this.infectionTex != null && !bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getWoundInfectionLevel() > 0.0f) {
            this.DrawTexture(this.infectionTex, 0.0, 0.0, bodyDamage.getBodyPart(this.BodyPartType).getWoundInfectionLevel() / 10.0f);
        }
        if (this.bandageTex != null && bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getBandageLife() > 0.0f) {
            this.DrawTexture(this.bandageTex, 0.0, 0.0, 1.0);
        }
        else if (this.dirtyBandageTex != null && bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getBandageLife() <= 0.0f) {
            this.DrawTexture(this.dirtyBandageTex, 0.0, 0.0, 1.0);
        }
        else if (this.scratchTex != null && bodyDamage.IsScratched(this.BodyPartType)) {
            this.DrawTexture(this.scratchTex, 0.0, 0.0, bodyDamage.getBodyPart(this.BodyPartType).getScratchTime() / 20.0f);
        }
        else if (this.scratchTex != null && bodyDamage.IsCut(this.BodyPartType)) {
            this.DrawTexture(this.scratchTex, 0.0, 0.0, bodyDamage.getBodyPart(this.BodyPartType).getCutTime() / 20.0f);
        }
        else if (this.biteTex != null && !bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.IsBitten(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getBiteTime() >= 0.0f) {
            this.DrawTexture(this.biteTex, 0.0, 0.0, 1.0);
        }
        else if (this.deepWoundTex != null && bodyDamage.IsDeepWounded(this.BodyPartType)) {
            this.DrawTexture(this.deepWoundTex, 0.0, 0.0, bodyDamage.getBodyPart(this.BodyPartType).getDeepWoundTime() / 15.0f);
        }
        else if (this.stitchTex != null && bodyDamage.IsStitched(this.BodyPartType)) {
            this.DrawTexture(this.stitchTex, 0.0, 0.0, 1.0);
        }
        if (this.boneTex != null && bodyDamage.getBodyPart(this.BodyPartType).getFractureTime() > 0.0f && bodyDamage.getBodyPart(this.BodyPartType).getSplintFactor() == 0.0f) {
            this.DrawTexture(this.boneTex, 0.0, 0.0, 1.0);
        }
        else if (this.splintTex != null && bodyDamage.getBodyPart(this.BodyPartType).getSplintFactor() > 0.0f) {
            this.DrawTexture(this.splintTex, 0.0, 0.0, 1.0);
        }
        if (this.glassTex != null && bodyDamage.getBodyPart(this.BodyPartType).haveGlass() && !bodyDamage.getBodyPart(this.BodyPartType).bandaged()) {
            this.DrawTexture(this.glassTex, 0.0, 0.0, 1.0);
        }
        if (this.bulletTex != null && bodyDamage.getBodyPart(this.BodyPartType).haveBullet() && !bodyDamage.getBodyPart(this.BodyPartType).bandaged()) {
            this.DrawTexture(this.bulletTex, 0.0, 0.0, 1.0);
        }
        if (this.burnTex != null && bodyDamage.getBodyPart(this.BodyPartType).getBurnTime() > 0.0f && !bodyDamage.getBodyPart(this.BodyPartType).bandaged()) {
            this.DrawTexture(this.burnTex, 0.0, 0.0, bodyDamage.getBodyPart(this.BodyPartType).getBurnTime() / 100.0f);
        }
        super.render();
    }
}
