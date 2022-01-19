// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.debug.DebugLog;
import zombie.core.Translator;

public enum BodyPartType
{
    Hand_L, 
    Hand_R, 
    ForeArm_L, 
    ForeArm_R, 
    UpperArm_L, 
    UpperArm_R, 
    Torso_Upper, 
    Torso_Lower, 
    Head, 
    Neck, 
    Groin, 
    UpperLeg_L, 
    UpperLeg_R, 
    LowerLeg_L, 
    LowerLeg_R, 
    Foot_L, 
    Foot_R, 
    MAX;
    
    public static BodyPartType FromIndex(final int n) {
        switch (n) {
            case 0: {
                return BodyPartType.Hand_L;
            }
            case 1: {
                return BodyPartType.Hand_R;
            }
            case 2: {
                return BodyPartType.ForeArm_L;
            }
            case 3: {
                return BodyPartType.ForeArm_R;
            }
            case 4: {
                return BodyPartType.UpperArm_L;
            }
            case 5: {
                return BodyPartType.UpperArm_R;
            }
            case 6: {
                return BodyPartType.Torso_Upper;
            }
            case 7: {
                return BodyPartType.Torso_Lower;
            }
            case 8: {
                return BodyPartType.Head;
            }
            case 9: {
                return BodyPartType.Neck;
            }
            case 10: {
                return BodyPartType.Groin;
            }
            case 11: {
                return BodyPartType.UpperLeg_L;
            }
            case 12: {
                return BodyPartType.UpperLeg_R;
            }
            case 13: {
                return BodyPartType.LowerLeg_L;
            }
            case 14: {
                return BodyPartType.LowerLeg_R;
            }
            case 15: {
                return BodyPartType.Foot_L;
            }
            case 16: {
                return BodyPartType.Foot_R;
            }
            default: {
                return BodyPartType.MAX;
            }
        }
    }
    
    public int index() {
        return ToIndex(this);
    }
    
    public static BodyPartType FromString(final String s) {
        if (s.equals("Hand_L")) {
            return BodyPartType.Hand_L;
        }
        if (s.equals("Hand_R")) {
            return BodyPartType.Hand_R;
        }
        if (s.equals("ForeArm_L")) {
            return BodyPartType.ForeArm_L;
        }
        if (s.equals("ForeArm_R")) {
            return BodyPartType.ForeArm_R;
        }
        if (s.equals("UpperArm_L")) {
            return BodyPartType.UpperArm_L;
        }
        if (s.equals("UpperArm_R")) {
            return BodyPartType.UpperArm_R;
        }
        if (s.equals("Torso_Upper")) {
            return BodyPartType.Torso_Upper;
        }
        if (s.equals("Torso_Lower")) {
            return BodyPartType.Torso_Lower;
        }
        if (s.equals("Head")) {
            return BodyPartType.Head;
        }
        if (s.equals("Neck")) {
            return BodyPartType.Neck;
        }
        if (s.equals("Groin")) {
            return BodyPartType.Groin;
        }
        if (s.equals("UpperLeg_L")) {
            return BodyPartType.UpperLeg_L;
        }
        if (s.equals("UpperLeg_R")) {
            return BodyPartType.UpperLeg_R;
        }
        if (s.equals("LowerLeg_L")) {
            return BodyPartType.LowerLeg_L;
        }
        if (s.equals("LowerLeg_R")) {
            return BodyPartType.LowerLeg_R;
        }
        if (s.equals("Foot_L")) {
            return BodyPartType.Foot_L;
        }
        if (s.equals("Foot_R")) {
            return BodyPartType.Foot_R;
        }
        return BodyPartType.MAX;
    }
    
    public static float getPainModifyer(final int n) {
        switch (n) {
            case 0: {
                return 0.5f;
            }
            case 1: {
                return 0.5f;
            }
            case 2: {
                return 0.6f;
            }
            case 3: {
                return 0.6f;
            }
            case 4: {
                return 0.6f;
            }
            case 5: {
                return 0.6f;
            }
            case 6: {
                return 0.7f;
            }
            case 7: {
                return 0.78f;
            }
            case 8: {
                return 0.8f;
            }
            case 9: {
                return 0.8f;
            }
            case 10: {
                return 0.7f;
            }
            case 11: {
                return 0.7f;
            }
            case 12: {
                return 0.7f;
            }
            case 13: {
                return 0.6f;
            }
            case 14: {
                return 0.6f;
            }
            case 15: {
                return 0.5f;
            }
            case 16: {
                return 0.5f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public static String getDisplayName(final BodyPartType bodyPartType) {
        if (bodyPartType == BodyPartType.Hand_L) {
            return Translator.getText("IGUI_health_Left_Hand");
        }
        if (bodyPartType == BodyPartType.Hand_R) {
            return Translator.getText("IGUI_health_Right_Hand");
        }
        if (bodyPartType == BodyPartType.ForeArm_L) {
            return Translator.getText("IGUI_health_Left_Forearm");
        }
        if (bodyPartType == BodyPartType.ForeArm_R) {
            return Translator.getText("IGUI_health_Right_Forearm");
        }
        if (bodyPartType == BodyPartType.UpperArm_L) {
            return Translator.getText("IGUI_health_Left_Upper_Arm");
        }
        if (bodyPartType == BodyPartType.UpperArm_R) {
            return Translator.getText("IGUI_health_Right_Upper_Arm");
        }
        if (bodyPartType == BodyPartType.Torso_Upper) {
            return Translator.getText("IGUI_health_Upper_Torso");
        }
        if (bodyPartType == BodyPartType.Torso_Lower) {
            return Translator.getText("IGUI_health_Lower_Torso");
        }
        if (bodyPartType == BodyPartType.Head) {
            return Translator.getText("IGUI_health_Head");
        }
        if (bodyPartType == BodyPartType.Neck) {
            return Translator.getText("IGUI_health_Neck");
        }
        if (bodyPartType == BodyPartType.Groin) {
            return Translator.getText("IGUI_health_Groin");
        }
        if (bodyPartType == BodyPartType.UpperLeg_L) {
            return Translator.getText("IGUI_health_Left_Thigh");
        }
        if (bodyPartType == BodyPartType.UpperLeg_R) {
            return Translator.getText("IGUI_health_Right_Thigh");
        }
        if (bodyPartType == BodyPartType.LowerLeg_L) {
            return Translator.getText("IGUI_health_Left_Shin");
        }
        if (bodyPartType == BodyPartType.LowerLeg_R) {
            return Translator.getText("IGUI_health_Right_Shin");
        }
        if (bodyPartType == BodyPartType.Foot_L) {
            return Translator.getText("IGUI_health_Left_Foot");
        }
        if (bodyPartType == BodyPartType.Foot_R) {
            return Translator.getText("IGUI_health_Right_Foot");
        }
        return Translator.getText("IGUI_health_Unknown_Body_Part");
    }
    
    public static int ToIndex(final BodyPartType bodyPartType) {
        if (bodyPartType == null) {
            return 0;
        }
        switch (bodyPartType) {
            case Hand_L: {
                return 0;
            }
            case Hand_R: {
                return 1;
            }
            case ForeArm_L: {
                return 2;
            }
            case ForeArm_R: {
                return 3;
            }
            case UpperArm_L: {
                return 4;
            }
            case UpperArm_R: {
                return 5;
            }
            case Torso_Upper: {
                return 6;
            }
            case Torso_Lower: {
                return 7;
            }
            case Head: {
                return 8;
            }
            case Neck: {
                return 9;
            }
            case Groin: {
                return 10;
            }
            case UpperLeg_L: {
                return 11;
            }
            case UpperLeg_R: {
                return 12;
            }
            case LowerLeg_L: {
                return 13;
            }
            case LowerLeg_R: {
                return 14;
            }
            case Foot_L: {
                return 15;
            }
            case Foot_R: {
                return 16;
            }
            case MAX: {
                return 17;
            }
            default: {
                return 17;
            }
        }
    }
    
    public static String ToString(final BodyPartType bodyPartType) {
        if (bodyPartType == BodyPartType.Hand_L) {
            return "Hand_L";
        }
        if (bodyPartType == BodyPartType.Hand_R) {
            return "Hand_R";
        }
        if (bodyPartType == BodyPartType.ForeArm_L) {
            return "ForeArm_L";
        }
        if (bodyPartType == BodyPartType.ForeArm_R) {
            return "ForeArm_R";
        }
        if (bodyPartType == BodyPartType.UpperArm_L) {
            return "UpperArm_L";
        }
        if (bodyPartType == BodyPartType.UpperArm_R) {
            return "UpperArm_R";
        }
        if (bodyPartType == BodyPartType.Torso_Upper) {
            return "Torso_Upper";
        }
        if (bodyPartType == BodyPartType.Torso_Lower) {
            return "Torso_Lower";
        }
        if (bodyPartType == BodyPartType.Head) {
            return "Head";
        }
        if (bodyPartType == BodyPartType.Neck) {
            return "Neck";
        }
        if (bodyPartType == BodyPartType.Groin) {
            return "Groin";
        }
        if (bodyPartType == BodyPartType.UpperLeg_L) {
            return "UpperLeg_L";
        }
        if (bodyPartType == BodyPartType.UpperLeg_R) {
            return "UpperLeg_R";
        }
        if (bodyPartType == BodyPartType.LowerLeg_L) {
            return "LowerLeg_L";
        }
        if (bodyPartType == BodyPartType.LowerLeg_R) {
            return "LowerLeg_R";
        }
        if (bodyPartType == BodyPartType.Foot_L) {
            return "Foot_L";
        }
        if (bodyPartType == BodyPartType.Foot_R) {
            return "Foot_R";
        }
        return "Unkown Body Part";
    }
    
    public static float getDamageModifyer(final int n) {
        switch (n) {
            case 0: {
                return 0.1f;
            }
            case 1: {
                return 0.1f;
            }
            case 2: {
                return 0.2f;
            }
            case 3: {
                return 0.2f;
            }
            case 4: {
                return 0.3f;
            }
            case 5: {
                return 0.3f;
            }
            case 6: {
                return 0.35f;
            }
            case 7: {
                return 0.4f;
            }
            case 8: {
                return 0.6f;
            }
            case 9: {
                return 0.7f;
            }
            case 10: {
                return 0.4f;
            }
            case 11: {
                return 0.3f;
            }
            case 12: {
                return 0.3f;
            }
            case 13: {
                return 0.2f;
            }
            case 14: {
                return 0.2f;
            }
            case 15: {
                return 0.2f;
            }
            case 16: {
                return 0.2f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public static float getBleedingTimeModifyer(final int n) {
        switch (n) {
            case 0: {
                return 0.2f;
            }
            case 1: {
                return 0.2f;
            }
            case 2: {
                return 0.3f;
            }
            case 3: {
                return 0.3f;
            }
            case 4: {
                return 0.4f;
            }
            case 5: {
                return 0.4f;
            }
            case 6: {
                return 0.5f;
            }
            case 7: {
                return 0.9f;
            }
            case 8: {
                return 1.0f;
            }
            case 9: {
                return 1.5f;
            }
            case 10: {
                return 0.5f;
            }
            case 11: {
                return 0.4f;
            }
            case 12: {
                return 0.4f;
            }
            case 13: {
                return 0.3f;
            }
            case 14: {
                return 0.3f;
            }
            case 15: {
                return 0.2f;
            }
            case 16: {
                return 0.2f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public static float GetSkinSurface(final BodyPartType bodyPartType) {
        if (bodyPartType == null) {
            return 0.001f;
        }
        switch (bodyPartType) {
            case Torso_Upper: {
                return 0.18f;
            }
            case Head: {
                return 0.08f;
            }
            case Neck: {
                return 0.02f;
            }
            case Torso_Lower: {
                return 0.12f;
            }
            case Groin: {
                return 0.06f;
            }
            case UpperLeg_L:
            case UpperLeg_R: {
                return 0.09f;
            }
            case LowerLeg_L:
            case LowerLeg_R: {
                return 0.07f;
            }
            case Foot_L:
            case Foot_R: {
                return 0.02f;
            }
            case UpperArm_L:
            case UpperArm_R: {
                return 0.045f;
            }
            case ForeArm_L:
            case ForeArm_R: {
                return 0.035f;
            }
            case Hand_L:
            case Hand_R: {
                return 0.01f;
            }
            default: {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/characters/BodyDamage/BodyPartType;)Ljava/lang/String;, bodyPartType));
                return 0.001f;
            }
        }
    }
    
    public static float GetDistToCore(final BodyPartType bodyPartType) {
        if (bodyPartType == null) {
            return 0.0f;
        }
        switch (bodyPartType) {
            case Torso_Upper: {
                return 0.0f;
            }
            case Head: {
                return 0.05f;
            }
            case Neck: {
                return 0.02f;
            }
            case Torso_Lower: {
                return 0.05f;
            }
            case Groin: {
                return 0.3f;
            }
            case UpperLeg_L:
            case UpperLeg_R: {
                return 0.45f;
            }
            case LowerLeg_L:
            case LowerLeg_R: {
                return 0.75f;
            }
            case Foot_L:
            case Foot_R: {
                return 1.0f;
            }
            case UpperArm_L:
            case UpperArm_R: {
                return 0.3f;
            }
            case ForeArm_L:
            case ForeArm_R: {
                return 0.6f;
            }
            case Hand_L:
            case Hand_R: {
                return 0.8f;
            }
            default: {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/characters/BodyDamage/BodyPartType;)Ljava/lang/String;, bodyPartType));
                return 0.0f;
            }
        }
    }
    
    public static float GetUmbrellaMod(final BodyPartType bodyPartType) {
        if (bodyPartType == null) {
            return 1.0f;
        }
        switch (bodyPartType) {
            case Torso_Upper: {
                return 0.2f;
            }
            case Head: {
                return 0.05f;
            }
            case Neck: {
                return 0.1f;
            }
            case Torso_Lower: {
                return 0.25f;
            }
            case Groin: {
                return 0.3f;
            }
            case UpperLeg_L:
            case UpperLeg_R: {
                return 0.55f;
            }
            case LowerLeg_L:
            case LowerLeg_R: {
                return 0.75f;
            }
            case Foot_L:
            case Foot_R: {
                return 1.0f;
            }
            case UpperArm_L:
            case UpperArm_R: {
                return 0.25f;
            }
            case ForeArm_L:
            case ForeArm_R: {
                return 0.3f;
            }
            case Hand_L:
            case Hand_R: {
                return 0.35f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public static float GetMaxActionPenalty(final BodyPartType bodyPartType) {
        if (bodyPartType == null) {
            return 0.0f;
        }
        switch (bodyPartType) {
            case Torso_Upper: {
                return 0.2f;
            }
            case Head: {
                return 0.4f;
            }
            case Neck: {
                return 0.05f;
            }
            case Torso_Lower: {
                return 0.1f;
            }
            case Groin: {
                return 0.05f;
            }
            case UpperLeg_L:
            case UpperLeg_R: {
                return 0.1f;
            }
            case LowerLeg_L:
            case LowerLeg_R: {
                return 0.1f;
            }
            case Foot_L:
            case Foot_R: {
                return 0.1f;
            }
            case UpperArm_L:
            case UpperArm_R: {
                return 0.4f;
            }
            case ForeArm_L:
            case ForeArm_R: {
                return 0.6f;
            }
            case Hand_L:
            case Hand_R: {
                return 1.0f;
            }
            default: {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/characters/BodyDamage/BodyPartType;)Ljava/lang/String;, bodyPartType));
                return 0.0f;
            }
        }
    }
    
    public static float GetMaxMovementPenalty(final BodyPartType bodyPartType) {
        if (bodyPartType == null) {
            return 0.0f;
        }
        switch (bodyPartType) {
            case Torso_Upper: {
                return 0.05f;
            }
            case Head: {
                return 0.25f;
            }
            case Neck: {
                return 0.05f;
            }
            case Torso_Lower: {
                return 0.05f;
            }
            case Groin: {
                return 0.15f;
            }
            case UpperLeg_L:
            case UpperLeg_R: {
                return 0.4f;
            }
            case LowerLeg_L:
            case LowerLeg_R: {
                return 0.6f;
            }
            case Foot_L:
            case Foot_R: {
                return 1.0f;
            }
            case UpperArm_L:
            case UpperArm_R: {
                return 0.1f;
            }
            case ForeArm_L:
            case ForeArm_R: {
                return 0.1f;
            }
            case Hand_L:
            case Hand_R: {
                return 0.05f;
            }
            default: {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/characters/BodyDamage/BodyPartType;)Ljava/lang/String;, bodyPartType));
                return 0.0f;
            }
        }
    }
    
    public String getBandageModel() {
        switch (this) {
            case Torso_Upper: {
                return "Base.Bandage_Chest";
            }
            case Head: {
                return "Base.Bandage_Head";
            }
            case Neck: {
                return "Base.Bandage_Neck";
            }
            case Torso_Lower: {
                return "Base.Bandage_Abdomen";
            }
            case Groin: {
                return "Base.Bandage_Groin";
            }
            case UpperLeg_L: {
                return "Base.Bandage_LeftUpperLeg";
            }
            case UpperLeg_R: {
                return "Base.Bandage_RightUpperLeg";
            }
            case LowerLeg_L: {
                return "Base.Bandage_LeftLowerLeg";
            }
            case LowerLeg_R: {
                return "Base.Bandage_RightLowerLeg";
            }
            case Foot_L: {
                return "Base.Bandage_LeftFoot";
            }
            case Foot_R: {
                return "Base.Bandage_RightFoot";
            }
            case UpperArm_L: {
                return "Base.Bandage_LeftUpperArm";
            }
            case UpperArm_R: {
                return "Base.Bandage_RightUpperArm";
            }
            case ForeArm_L: {
                return "Base.Bandage_LeftLowerArm";
            }
            case ForeArm_R: {
                return "Base.Bandage_RightLowerArm";
            }
            case Hand_L: {
                return "Base.Bandage_LeftHand";
            }
            case Hand_R: {
                return "Base.Bandage_RightHand";
            }
            default: {
                return null;
            }
        }
    }
    
    public String getBiteWoundModel(final boolean b) {
        String s = "Female";
        if (!b) {
            s = "Male";
        }
        switch (this) {
            case Torso_Upper: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Head: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Neck: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Torso_Lower: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Groin: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case UpperLeg_L: {
                return null;
            }
            case UpperLeg_R: {
                return null;
            }
            case LowerLeg_L: {
                return null;
            }
            case LowerLeg_R: {
                return null;
            }
            case Foot_L: {
                return null;
            }
            case Foot_R: {
                return null;
            }
            case UpperArm_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case UpperArm_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case ForeArm_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case ForeArm_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Hand_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Hand_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            default: {
                return null;
            }
        }
    }
    
    public String getScratchWoundModel(final boolean b) {
        String s = "Female";
        if (!b) {
            s = "Male";
        }
        switch (this) {
            case Torso_Upper: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Head: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Neck: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Torso_Lower: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Groin: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case UpperLeg_L: {
                return null;
            }
            case UpperLeg_R: {
                return null;
            }
            case LowerLeg_L: {
                return null;
            }
            case LowerLeg_R: {
                return null;
            }
            case Foot_L: {
                return null;
            }
            case Foot_R: {
                return null;
            }
            case UpperArm_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case UpperArm_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case ForeArm_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case ForeArm_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Hand_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Hand_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            default: {
                return null;
            }
        }
    }
    
    public String getCutWoundModel(final boolean b) {
        String s = "Female";
        if (!b) {
            s = "Male";
        }
        switch (this) {
            case Torso_Upper: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Head: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Neck: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Torso_Lower: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Groin: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case UpperLeg_L: {
                return null;
            }
            case UpperLeg_R: {
                return null;
            }
            case LowerLeg_L: {
                return null;
            }
            case LowerLeg_R: {
                return null;
            }
            case Foot_L: {
                return null;
            }
            case Foot_R: {
                return null;
            }
            case UpperArm_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case UpperArm_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case ForeArm_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case ForeArm_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Hand_L: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            case Hand_R: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            default: {
                return null;
            }
        }
    }
    
    public static BodyPartType getRandom() {
        return FromIndex(OutfitRNG.Next(0, BodyPartType.MAX.index()));
    }
    
    private static /* synthetic */ BodyPartType[] $values() {
        return new BodyPartType[] { BodyPartType.Hand_L, BodyPartType.Hand_R, BodyPartType.ForeArm_L, BodyPartType.ForeArm_R, BodyPartType.UpperArm_L, BodyPartType.UpperArm_R, BodyPartType.Torso_Upper, BodyPartType.Torso_Lower, BodyPartType.Head, BodyPartType.Neck, BodyPartType.Groin, BodyPartType.UpperLeg_L, BodyPartType.UpperLeg_R, BodyPartType.LowerLeg_L, BodyPartType.LowerLeg_R, BodyPartType.Foot_L, BodyPartType.Foot_R, BodyPartType.MAX };
    }
    
    static {
        $VALUES = $values();
    }
}
