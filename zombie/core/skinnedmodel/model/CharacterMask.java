// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.util.Pool;
import java.util.function.Consumer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import java.util.ArrayList;
import java.util.Arrays;
import zombie.characterTextures.BloodBodyPartType;

public final class CharacterMask
{
    private final boolean[] m_visibleFlags;
    
    public CharacterMask() {
        this.m_visibleFlags = createFlags(Part.values().length, true);
    }
    
    public boolean isBloodBodyPartVisible(final BloodBodyPartType bloodBodyPartType) {
        final Part[] characterMaskParts = bloodBodyPartType.getCharacterMaskParts();
        for (int length = characterMaskParts.length, i = 0; i < length; ++i) {
            if (this.isPartVisible(characterMaskParts[i])) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean[] createFlags(final int n, final boolean b) {
        final boolean[] array = new boolean[n];
        for (int i = 0; i < n; ++i) {
            array[i] = b;
        }
        return array;
    }
    
    public void setAllVisible(final boolean val) {
        Arrays.fill(this.m_visibleFlags, val);
    }
    
    public void copyFrom(final CharacterMask characterMask) {
        System.arraycopy(characterMask.m_visibleFlags, 0, this.m_visibleFlags, 0, this.m_visibleFlags.length);
    }
    
    public void setPartVisible(final Part part, final boolean b) {
        if (part.hasSubdivisions()) {
            final Part[] subDivisions = part.subDivisions();
            for (int length = subDivisions.length, i = 0; i < length; ++i) {
                this.setPartVisible(subDivisions[i], b);
            }
        }
        else {
            this.m_visibleFlags[part.getValue()] = b;
        }
    }
    
    public void setPartsVisible(final ArrayList<Integer> list, final boolean b) {
        for (int i = 0; i < list.size(); ++i) {
            final int intValue = list.get(i);
            final Part fromInt = Part.fromInt(intValue);
            if (fromInt == null) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, intValue));
                }
            }
            else {
                this.setPartVisible(fromInt, b);
            }
        }
    }
    
    public boolean isPartVisible(final Part part) {
        if (part == null) {
            return false;
        }
        if (part.hasSubdivisions()) {
            boolean b = true;
            for (int n = 0; b && n < part.subDivisions().length; b = this.m_visibleFlags[part.subDivisions()[n].getValue()], ++n) {}
            return b;
        }
        return this.m_visibleFlags[part.getValue()];
    }
    
    public boolean isTorsoVisible() {
        return this.isPartVisible(Part.Torso);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this.contentsToString());
    }
    
    public String contentsToString() {
        if (this.isAllVisible()) {
            return "All Visible";
        }
        if (this.isNothingVisible()) {
            return "Nothing Visible";
        }
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        int n = 0;
        while (i < Part.leaves().length) {
            final Part obj = Part.leaves()[i];
            if (this.isPartVisible(obj)) {
                if (n > 0) {
                    sb.append(',');
                }
                sb.append(obj);
                ++n;
            }
            ++i;
        }
        return sb.toString();
    }
    
    private boolean isAll(final boolean b) {
        boolean b2 = true;
        for (int n = 0, length = Part.leaves().length; b2 && n < length; b2 = (this.isPartVisible(Part.leaves()[n]) == b), ++n) {}
        return b2;
    }
    
    public boolean isNothingVisible() {
        return this.isAll(false);
    }
    
    public boolean isAllVisible() {
        return this.isAll(true);
    }
    
    public void forEachVisible(final Consumer<Part> consumer) {
        try {
            for (int i = 0; i < Part.leaves().length; ++i) {
                final Part part = Part.leaves()[i];
                if (this.isPartVisible(part)) {
                    consumer.accept(part);
                }
            }
        }
        finally {
            Pool.tryRelease(consumer);
        }
    }
    
    public enum Part
    {
        Head(0), 
        Torso(1, true), 
        Pelvis(2, true), 
        LeftArm(3), 
        LeftHand(4), 
        RightArm(5), 
        RightHand(6), 
        LeftLeg(7), 
        LeftFoot(8), 
        RightLeg(9), 
        RightFoot(10), 
        Dress(11), 
        Chest(12, Part.Torso), 
        Waist(13, Part.Torso), 
        Belt(14, Part.Pelvis), 
        Crotch(15, Part.Pelvis);
        
        private final int value;
        private final Part parent;
        private final boolean isSubdivided;
        private Part[] subDivisions;
        private BloodBodyPartType[] m_bloodBodyPartTypes;
        private static final Part[] s_leaves;
        
        private Part(final int value) {
            this.value = value;
            this.parent = null;
            this.isSubdivided = false;
        }
        
        private Part(final int value, final Part parent) {
            this.value = value;
            this.parent = parent;
            this.isSubdivided = false;
        }
        
        private Part(final int value, final boolean isSubdivided) {
            this.value = value;
            this.parent = null;
            this.isSubdivided = isSubdivided;
        }
        
        public static int count() {
            return values().length;
        }
        
        public static Part[] leaves() {
            return Part.s_leaves;
        }
        
        public static Part fromInt(final int n) {
            return (n >= 0 && n < count()) ? values()[n] : null;
        }
        
        public int getValue() {
            return this.value;
        }
        
        public Part getParent() {
            return this.parent;
        }
        
        public boolean isSubdivision() {
            return this.parent != null;
        }
        
        public boolean hasSubdivisions() {
            return this.isSubdivided;
        }
        
        public Part[] subDivisions() {
            if (this.subDivisions != null) {
                return this.subDivisions;
            }
            if (!this.isSubdivided) {
                this.subDivisions = new Part[0];
            }
            final ArrayList<Part> list = new ArrayList<Part>();
            for (final Part e : values()) {
                if (e.parent == this) {
                    list.add(e);
                }
            }
            return this.subDivisions = list.toArray(new Part[0]);
        }
        
        private static Part[] leavesInternal() {
            final ArrayList<Part> list = new ArrayList<Part>();
            for (final Part e : values()) {
                if (!e.hasSubdivisions()) {
                    list.add(e);
                }
            }
            return list.toArray(new Part[0]);
        }
        
        public BloodBodyPartType[] getBloodBodyPartTypes() {
            if (this.m_bloodBodyPartTypes != null) {
                return this.m_bloodBodyPartTypes;
            }
            final ArrayList<BloodBodyPartType> list = new ArrayList<BloodBodyPartType>();
            switch (this) {
                case Head: {
                    list.add(BloodBodyPartType.Head);
                    break;
                }
                case Torso: {
                    list.add(BloodBodyPartType.Torso_Upper);
                    list.add(BloodBodyPartType.Torso_Lower);
                    break;
                }
                case Pelvis: {
                    list.add(BloodBodyPartType.UpperLeg_L);
                    list.add(BloodBodyPartType.UpperLeg_R);
                    list.add(BloodBodyPartType.Groin);
                    break;
                }
                case LeftArm: {
                    list.add(BloodBodyPartType.UpperArm_L);
                    list.add(BloodBodyPartType.ForeArm_L);
                    break;
                }
                case LeftHand: {
                    list.add(BloodBodyPartType.Hand_L);
                    break;
                }
                case RightArm: {
                    list.add(BloodBodyPartType.UpperArm_R);
                    list.add(BloodBodyPartType.ForeArm_R);
                    break;
                }
                case RightHand: {
                    list.add(BloodBodyPartType.Hand_R);
                    break;
                }
                case LeftLeg: {
                    list.add(BloodBodyPartType.UpperLeg_L);
                    list.add(BloodBodyPartType.LowerLeg_L);
                    break;
                }
                case LeftFoot: {
                    list.add(BloodBodyPartType.Foot_L);
                    break;
                }
                case RightLeg: {
                    list.add(BloodBodyPartType.UpperLeg_R);
                    list.add(BloodBodyPartType.LowerLeg_R);
                    break;
                }
                case RightFoot: {
                    list.add(BloodBodyPartType.Foot_R);
                }
                case Chest: {
                    list.add(BloodBodyPartType.Torso_Upper);
                    break;
                }
                case Waist: {
                    list.add(BloodBodyPartType.Torso_Lower);
                    break;
                }
                case Belt: {
                    list.add(BloodBodyPartType.UpperLeg_L);
                    list.add(BloodBodyPartType.UpperLeg_R);
                    break;
                }
                case Crotch: {
                    list.add(BloodBodyPartType.Groin);
                    break;
                }
            }
            this.m_bloodBodyPartTypes = new BloodBodyPartType[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                this.m_bloodBodyPartTypes[i] = list.get(i);
            }
            return this.m_bloodBodyPartTypes;
        }
        
        private static /* synthetic */ Part[] $values() {
            return new Part[] { Part.Head, Part.Torso, Part.Pelvis, Part.LeftArm, Part.LeftHand, Part.RightArm, Part.RightHand, Part.LeftLeg, Part.LeftFoot, Part.RightLeg, Part.RightFoot, Part.Dress, Part.Chest, Part.Waist, Part.Belt, Part.Crotch };
        }
        
        static {
            $VALUES = $values();
            s_leaves = leavesInternal();
        }
    }
}
