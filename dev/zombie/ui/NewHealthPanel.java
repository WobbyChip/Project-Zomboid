// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import zombie.core.Translator;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.core.textures.Texture;

public final class NewHealthPanel extends NewWindow
{
    public static NewHealthPanel instance;
    public Texture BodyOutline;
    public UI_BodyPart Foot_L;
    public UI_BodyPart Foot_R;
    public UI_BodyPart ForeArm_L;
    public UI_BodyPart ForeArm_R;
    public UI_BodyPart Groin;
    public UI_BodyPart Hand_L;
    public UI_BodyPart Hand_R;
    public UI_BodyPart Head;
    public UI_BodyPart LowerLeg_L;
    public UI_BodyPart LowerLeg_R;
    public UI_BodyPart Neck;
    public UI_BodyPart Torso_Lower;
    public UI_BodyPart Torso_Upper;
    public UI_BodyPart UpperArm_L;
    public UI_BodyPart UpperArm_R;
    public UI_BodyPart UpperLeg_L;
    public UI_BodyPart UpperLeg_R;
    public Texture HealthBar;
    public Texture HealthBarBack;
    public Texture HealthIcon;
    IsoGameCharacter ParentChar;
    
    public void SetCharacter(final IsoGameCharacter parentChar) {
        this.ParentChar = parentChar;
    }
    
    public NewHealthPanel(final int n, final int n2, final IsoGameCharacter parentChar) {
        super(n, n2, 10, 10, true);
        this.ParentChar = parentChar;
        this.ResizeToFitY = false;
        this.visible = false;
        NewHealthPanel.instance = this;
        final int n3 = 2;
        this.HealthIcon = Texture.getSharedTexture("media/ui/Heart_On.png", n3);
        this.HealthBarBack = Texture.getSharedTexture("media/ui/BodyDamage/DamageBar_Vert.png", n3);
        this.HealthBar = Texture.getSharedTexture("media/ui/BodyDamage/DamageBar_Vert_Fill.png", n3);
        String s = "male";
        if (parentChar.isFemale()) {
            s = "female";
        }
        this.BodyOutline = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        this.width = 300.0f;
        this.height = (float)(270 + this.titleRight.getHeight() + 5);
        this.Hand_L = new UI_BodyPart(BodyPartType.Hand_L, 0, 0, "hand_left.png", this.ParentChar, false);
        this.Hand_R = new UI_BodyPart(BodyPartType.Hand_R, 0, 0, "hand_right.png", this.ParentChar, false);
        this.ForeArm_L = new UI_BodyPart(BodyPartType.ForeArm_L, 0, 0, "lowerarm_left.png", this.ParentChar, false);
        this.ForeArm_R = new UI_BodyPart(BodyPartType.ForeArm_R, 0, 0, "lowerarm_right.png", this.ParentChar, false);
        this.UpperArm_L = new UI_BodyPart(BodyPartType.UpperArm_L, 0, 0, "upperarm_left.png", this.ParentChar, false);
        this.UpperArm_R = new UI_BodyPart(BodyPartType.UpperArm_R, 0, 0, "upperarm_right.png", this.ParentChar, false);
        this.Torso_Upper = new UI_BodyPart(BodyPartType.Torso_Upper, 0, 0, "chest.png", this.ParentChar, false);
        this.Torso_Lower = new UI_BodyPart(BodyPartType.Torso_Lower, 0, 0, "abdomen.png", this.ParentChar, false);
        this.Head = new UI_BodyPart(BodyPartType.Head, 0, 0, "head.png", this.ParentChar, false);
        this.Neck = new UI_BodyPart(BodyPartType.Neck, 0, 0, "neck.png", this.ParentChar, false);
        this.Groin = new UI_BodyPart(BodyPartType.Groin, 0, 0, "groin.png", this.ParentChar, false);
        this.UpperLeg_L = new UI_BodyPart(BodyPartType.UpperLeg_L, 0, 0, "upperleg_left.png", this.ParentChar, false);
        this.UpperLeg_R = new UI_BodyPart(BodyPartType.UpperLeg_R, 0, 0, "upperleg_right.png", this.ParentChar, false);
        this.LowerLeg_L = new UI_BodyPart(BodyPartType.LowerLeg_L, 0, 0, "lowerleg_left.png", this.ParentChar, false);
        this.LowerLeg_R = new UI_BodyPart(BodyPartType.LowerLeg_R, 0, 0, "lowerleg_right.png", this.ParentChar, false);
        this.Foot_L = new UI_BodyPart(BodyPartType.Foot_L, 0, 0, "foot_left.png", this.ParentChar, false);
        this.Foot_R = new UI_BodyPart(BodyPartType.Foot_R, 0, 0, "foot_right.png", this.ParentChar, false);
        this.AddChild(this.Hand_L);
        this.AddChild(this.Hand_R);
        this.AddChild(this.ForeArm_L);
        this.AddChild(this.ForeArm_R);
        this.AddChild(this.UpperArm_L);
        this.AddChild(this.UpperArm_R);
        this.AddChild(this.Torso_Upper);
        this.AddChild(this.Torso_Lower);
        this.AddChild(this.Head);
        this.AddChild(this.Neck);
        this.AddChild(this.Groin);
        this.AddChild(this.UpperLeg_L);
        this.AddChild(this.UpperLeg_R);
        this.AddChild(this.LowerLeg_L);
        this.AddChild(this.LowerLeg_R);
        this.AddChild(this.Foot_L);
        this.AddChild(this.Foot_R);
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        this.DrawTexture(this.BodyOutline, 0.0, 0.0, this.alpha);
        this.Hand_L.render();
        this.Hand_R.render();
        this.ForeArm_L.render();
        this.ForeArm_R.render();
        this.UpperArm_L.render();
        this.UpperArm_R.render();
        this.Torso_Upper.render();
        this.Torso_Lower.render();
        this.Head.render();
        this.Neck.render();
        this.Groin.render();
        this.UpperLeg_L.render();
        this.UpperLeg_R.render();
        this.LowerLeg_L.render();
        this.LowerLeg_R.render();
        this.Foot_L.render();
        this.Foot_R.render();
        BodyDamage bodyDamage = this.ParentChar.getBodyDamage();
        if (GameClient.bClient && this.ParentChar instanceof IsoPlayer && !((IsoPlayer)this.ParentChar).isLocalPlayer()) {
            bodyDamage = this.ParentChar.getBodyDamageRemote();
        }
        final float n = (100.0f - bodyDamage.getHealth()) * 1.7f;
        this.DrawTexture(this.HealthIcon, 126.0, 200.0, this.alpha);
        this.DrawTextureScaled(this.HealthBarBack, 130.0, 25.0, 18.0, 172.0, this.alpha);
        this.DrawTextureScaled(this.HealthBar, 130.0, 26 + (int)n, 18.0, 170 - (int)n, this.alpha);
        final double d = 0.15;
        final double d2 = 1.0;
        this.DrawTextureScaledColor(null, 130.0, 25.0, 18.0, 1.0, d, d, d, d2);
        this.DrawTextureScaledColor(null, 130.0, 25.0, 1.0, 172.0, d, d, d, d2);
        this.DrawTextureScaledColor(null, 147.0, 25.0, 1.0, 172.0, d, d, d, d2);
        if (Core.bDebug && DebugOptions.instance.UIRenderOutline.getValue()) {
            final Double value = -this.getXScroll();
            final Double value2 = -this.getYScroll();
            this.DrawTextureScaledColor(null, value, value2, 1.0, (double)this.height, 1.0, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + 1.0, value2, this.width - 2.0, 1.0, 1.0, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + this.width - 1.0, value2, 1.0, (double)this.height, 1.0, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + 1.0, value2 + this.height - 1.0, this.width - 2.0, 1.0, 1.0, 1.0, 1.0, 0.5);
        }
    }
    
    @Override
    public void update() {
        if (!this.isVisible()) {
            return;
        }
        super.update();
    }
    
    public String getDamageStatusString() {
        BodyDamage bodyDamage = this.ParentChar.getBodyDamage();
        if (GameClient.bClient && this.ParentChar instanceof IsoPlayer && !((IsoPlayer)this.ParentChar).isLocalPlayer()) {
            bodyDamage = this.ParentChar.getBodyDamageRemote();
        }
        if (bodyDamage.getHealth() == 100.0f) {
            return Translator.getText("IGUI_health_ok");
        }
        if (bodyDamage.getHealth() > 90.0f) {
            return Translator.getText("IGUI_health_Slight_damage");
        }
        if (bodyDamage.getHealth() > 80.0f) {
            return Translator.getText("IGUI_health_Very_Minor_damage");
        }
        if (bodyDamage.getHealth() > 70.0f) {
            return Translator.getText("IGUI_health_Minor_damage");
        }
        if (bodyDamage.getHealth() > 60.0f) {
            return Translator.getText("IGUI_health_Moderate_damage");
        }
        if (bodyDamage.getHealth() > 50.0f) {
            return Translator.getText("IGUI_health_Severe_damage");
        }
        if (bodyDamage.getHealth() > 40.0f) {
            return Translator.getText("IGUI_health_Very_Severe_damage");
        }
        if (bodyDamage.getHealth() > 20.0f) {
            return Translator.getText("IGUI_health_Crital_damage");
        }
        if (bodyDamage.getHealth() > 10.0f) {
            return Translator.getText("IGUI_health_Highly_Crital_damage");
        }
        if (bodyDamage.getHealth() > 0.0f) {
            return Translator.getText("IGUI_health_Terminal_damage");
        }
        return Translator.getText("IGUI_health_Deceased");
    }
}
