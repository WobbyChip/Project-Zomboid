// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characterTextures;

import zombie.core.Translator;
import java.util.ArrayList;
import zombie.core.skinnedmodel.model.CharacterMask;

public enum BloodBodyPartType
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
    Back, 
    MAX;
    
    private CharacterMask.Part[] m_characterMaskParts;
    
    public int index() {
        return ToIndex(this);
    }
    
    public static BloodBodyPartType FromIndex(final int n) {
        switch (n) {
            case 0: {
                return BloodBodyPartType.Hand_L;
            }
            case 1: {
                return BloodBodyPartType.Hand_R;
            }
            case 2: {
                return BloodBodyPartType.ForeArm_L;
            }
            case 3: {
                return BloodBodyPartType.ForeArm_R;
            }
            case 4: {
                return BloodBodyPartType.UpperArm_L;
            }
            case 5: {
                return BloodBodyPartType.UpperArm_R;
            }
            case 6: {
                return BloodBodyPartType.Torso_Upper;
            }
            case 7: {
                return BloodBodyPartType.Torso_Lower;
            }
            case 8: {
                return BloodBodyPartType.Head;
            }
            case 9: {
                return BloodBodyPartType.Neck;
            }
            case 10: {
                return BloodBodyPartType.Groin;
            }
            case 11: {
                return BloodBodyPartType.UpperLeg_L;
            }
            case 12: {
                return BloodBodyPartType.UpperLeg_R;
            }
            case 13: {
                return BloodBodyPartType.LowerLeg_L;
            }
            case 14: {
                return BloodBodyPartType.LowerLeg_R;
            }
            case 15: {
                return BloodBodyPartType.Foot_L;
            }
            case 16: {
                return BloodBodyPartType.Foot_R;
            }
            case 17: {
                return BloodBodyPartType.Back;
            }
            default: {
                return BloodBodyPartType.MAX;
            }
        }
    }
    
    public static int ToIndex(final BloodBodyPartType bloodBodyPartType) {
        if (bloodBodyPartType == null) {
            return 0;
        }
        switch (bloodBodyPartType) {
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
            case Back: {
                return 17;
            }
            case MAX: {
                return 18;
            }
            default: {
                return 17;
            }
        }
    }
    
    public static BloodBodyPartType FromString(final String s) {
        if (s.equals("Hand_L")) {
            return BloodBodyPartType.Hand_L;
        }
        if (s.equals("Hand_R")) {
            return BloodBodyPartType.Hand_R;
        }
        if (s.equals("ForeArm_L")) {
            return BloodBodyPartType.ForeArm_L;
        }
        if (s.equals("ForeArm_R")) {
            return BloodBodyPartType.ForeArm_R;
        }
        if (s.equals("UpperArm_L")) {
            return BloodBodyPartType.UpperArm_L;
        }
        if (s.equals("UpperArm_R")) {
            return BloodBodyPartType.UpperArm_R;
        }
        if (s.equals("Torso_Upper")) {
            return BloodBodyPartType.Torso_Upper;
        }
        if (s.equals("Torso_Lower")) {
            return BloodBodyPartType.Torso_Lower;
        }
        if (s.equals("Head")) {
            return BloodBodyPartType.Head;
        }
        if (s.equals("Neck")) {
            return BloodBodyPartType.Neck;
        }
        if (s.equals("Groin")) {
            return BloodBodyPartType.Groin;
        }
        if (s.equals("UpperLeg_L")) {
            return BloodBodyPartType.UpperLeg_L;
        }
        if (s.equals("UpperLeg_R")) {
            return BloodBodyPartType.UpperLeg_R;
        }
        if (s.equals("LowerLeg_L")) {
            return BloodBodyPartType.LowerLeg_L;
        }
        if (s.equals("LowerLeg_R")) {
            return BloodBodyPartType.LowerLeg_R;
        }
        if (s.equals("Foot_L")) {
            return BloodBodyPartType.Foot_L;
        }
        if (s.equals("Foot_R")) {
            return BloodBodyPartType.Foot_R;
        }
        if (s.equals("Back")) {
            return BloodBodyPartType.Back;
        }
        return BloodBodyPartType.MAX;
    }
    
    public CharacterMask.Part[] getCharacterMaskParts() {
        if (this.m_characterMaskParts != null) {
            return this.m_characterMaskParts;
        }
        final ArrayList<CharacterMask.Part> list = new ArrayList<CharacterMask.Part>();
        switch (this) {
            case Hand_L: {
                list.add(CharacterMask.Part.LeftHand);
                break;
            }
            case Hand_R: {
                list.add(CharacterMask.Part.RightHand);
                break;
            }
            case ForeArm_L: {
                list.add(CharacterMask.Part.LeftArm);
                break;
            }
            case ForeArm_R: {
                list.add(CharacterMask.Part.RightArm);
                break;
            }
            case UpperArm_L: {
                list.add(CharacterMask.Part.LeftArm);
                break;
            }
            case UpperArm_R: {
                list.add(CharacterMask.Part.RightArm);
                break;
            }
            case Torso_Upper: {
                list.add(CharacterMask.Part.Chest);
                break;
            }
            case Torso_Lower: {
                list.add(CharacterMask.Part.Waist);
                break;
            }
            case Head: {
                list.add(CharacterMask.Part.Head);
                break;
            }
            case Neck: {
                list.add(CharacterMask.Part.Head);
                break;
            }
            case Groin: {
                list.add(CharacterMask.Part.Crotch);
                break;
            }
            case UpperLeg_L: {
                list.add(CharacterMask.Part.LeftLeg);
                list.add(CharacterMask.Part.Pelvis);
                break;
            }
            case UpperLeg_R: {
                list.add(CharacterMask.Part.RightLeg);
                list.add(CharacterMask.Part.Pelvis);
                break;
            }
            case LowerLeg_L: {
                list.add(CharacterMask.Part.LeftLeg);
                break;
            }
            case LowerLeg_R: {
                list.add(CharacterMask.Part.RightLeg);
                break;
            }
            case Foot_L: {
                list.add(CharacterMask.Part.LeftFoot);
                break;
            }
            case Foot_R: {
                list.add(CharacterMask.Part.RightFoot);
                break;
            }
            case Back: {
                list.add(CharacterMask.Part.Torso);
                break;
            }
        }
        this.m_characterMaskParts = new CharacterMask.Part[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            this.m_characterMaskParts[i] = list.get(i);
        }
        return this.m_characterMaskParts;
    }
    
    public String getDisplayName() {
        return getDisplayName(this);
    }
    
    public static String getDisplayName(final BloodBodyPartType bloodBodyPartType) {
        if (bloodBodyPartType == BloodBodyPartType.Hand_L) {
            return Translator.getText("IGUI_health_Left_Hand");
        }
        if (bloodBodyPartType == BloodBodyPartType.Hand_R) {
            return Translator.getText("IGUI_health_Right_Hand");
        }
        if (bloodBodyPartType == BloodBodyPartType.ForeArm_L) {
            return Translator.getText("IGUI_health_Left_Forearm");
        }
        if (bloodBodyPartType == BloodBodyPartType.ForeArm_R) {
            return Translator.getText("IGUI_health_Right_Forearm");
        }
        if (bloodBodyPartType == BloodBodyPartType.UpperArm_L) {
            return Translator.getText("IGUI_health_Left_Upper_Arm");
        }
        if (bloodBodyPartType == BloodBodyPartType.UpperArm_R) {
            return Translator.getText("IGUI_health_Right_Upper_Arm");
        }
        if (bloodBodyPartType == BloodBodyPartType.Torso_Upper) {
            return Translator.getText("IGUI_health_Upper_Torso");
        }
        if (bloodBodyPartType == BloodBodyPartType.Torso_Lower) {
            return Translator.getText("IGUI_health_Lower_Torso");
        }
        if (bloodBodyPartType == BloodBodyPartType.Head) {
            return Translator.getText("IGUI_health_Head");
        }
        if (bloodBodyPartType == BloodBodyPartType.Neck) {
            return Translator.getText("IGUI_health_Neck");
        }
        if (bloodBodyPartType == BloodBodyPartType.Groin) {
            return Translator.getText("IGUI_health_Groin");
        }
        if (bloodBodyPartType == BloodBodyPartType.UpperLeg_L) {
            return Translator.getText("IGUI_health_Left_Thigh");
        }
        if (bloodBodyPartType == BloodBodyPartType.UpperLeg_R) {
            return Translator.getText("IGUI_health_Right_Thigh");
        }
        if (bloodBodyPartType == BloodBodyPartType.LowerLeg_L) {
            return Translator.getText("IGUI_health_Left_Shin");
        }
        if (bloodBodyPartType == BloodBodyPartType.LowerLeg_R) {
            return Translator.getText("IGUI_health_Right_Shin");
        }
        if (bloodBodyPartType == BloodBodyPartType.Foot_L) {
            return Translator.getText("IGUI_health_Left_Foot");
        }
        if (bloodBodyPartType == BloodBodyPartType.Foot_R) {
            return Translator.getText("IGUI_health_Right_Foot");
        }
        if (bloodBodyPartType == BloodBodyPartType.Back) {
            return Translator.getText("IGUI_health_Back");
        }
        return Translator.getText("IGUI_health_Unknown_Body_Part");
    }
    
    private static /* synthetic */ BloodBodyPartType[] $values() {
        return new BloodBodyPartType[] { BloodBodyPartType.Hand_L, BloodBodyPartType.Hand_R, BloodBodyPartType.ForeArm_L, BloodBodyPartType.ForeArm_R, BloodBodyPartType.UpperArm_L, BloodBodyPartType.UpperArm_R, BloodBodyPartType.Torso_Upper, BloodBodyPartType.Torso_Lower, BloodBodyPartType.Head, BloodBodyPartType.Neck, BloodBodyPartType.Groin, BloodBodyPartType.UpperLeg_L, BloodBodyPartType.UpperLeg_R, BloodBodyPartType.LowerLeg_L, BloodBodyPartType.LowerLeg_R, BloodBodyPartType.Foot_L, BloodBodyPartType.Foot_R, BloodBodyPartType.Back, BloodBodyPartType.MAX };
    }
    
    static {
        $VALUES = $values();
    }
}
