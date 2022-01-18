// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import java.util.List;

public final class AnimCondition
{
    public String m_Name;
    public Type m_Type;
    public float m_FloatValue;
    public boolean m_BoolValue;
    public String m_StringValue;
    private AnimationVariableHandle m_variableHandle;
    
    public AnimCondition() {
        this.m_Name = "";
        this.m_Type = Type.STRING;
        this.m_FloatValue = 0.0f;
        this.m_BoolValue = false;
        this.m_StringValue = "";
    }
    
    @Override
    public String toString() {
        return String.format("AnimCondition{name:%s type:%s value:%s }", this.m_Name, this.m_Type.toString(), this.getValueString());
    }
    
    public String getConditionString() {
        if (this.m_Type == Type.OR) {
            return "OR";
        }
        return String.format("( %s %s %s )", this.m_Name, this.m_Type.toString(), this.getValueString());
    }
    
    public String getValueString() {
        switch (this.m_Type) {
            case EQU:
            case NEQ:
            case LESS:
            case GTR: {
                return String.valueOf(this.m_FloatValue);
            }
            case BOOL: {
                return this.m_BoolValue ? "true" : "false";
            }
            case STRING:
            case STRNEQ: {
                return this.m_StringValue;
            }
            case OR: {
                return " -- OR -- ";
            }
            default: {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/advancedanimation/AnimCondition$Type;)Ljava/lang/String;, this.m_Type));
            }
        }
    }
    
    public boolean check(final IAnimationVariableSource animationVariableSource) {
        return this.checkInternal(animationVariableSource);
    }
    
    private boolean checkInternal(final IAnimationVariableSource animationVariableSource) {
        final Type type = this.m_Type;
        if (type == Type.OR) {
            return false;
        }
        if (this.m_variableHandle == null) {
            this.m_variableHandle = AnimationVariableHandle.alloc(this.m_Name);
        }
        final IAnimationVariableSlot variable = animationVariableSource.getVariable(this.m_variableHandle);
        switch (type) {
            case EQU: {
                return variable != null && this.m_FloatValue == variable.getValueFloat();
            }
            case NEQ: {
                return variable != null && this.m_FloatValue != variable.getValueFloat();
            }
            case LESS: {
                return variable != null && variable.getValueFloat() < this.m_FloatValue;
            }
            case GTR: {
                return variable != null && variable.getValueFloat() > this.m_FloatValue;
            }
            case BOOL: {
                return (variable != null && variable.getValueBool()) == this.m_BoolValue;
            }
            case STRING: {
                return this.m_StringValue.equalsIgnoreCase((variable != null) ? variable.getValueString() : "");
            }
            case STRNEQ: {
                return !this.m_StringValue.equalsIgnoreCase((variable != null) ? variable.getValueString() : "");
            }
            case OR: {
                return false;
            }
            default: {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Lzombie/core/skinnedmodel/advancedanimation/AnimCondition$Type;)Ljava/lang/String;, this.m_Type));
            }
        }
    }
    
    public static boolean pass(final IAnimationVariableSource animationVariableSource, final List<AnimCondition> list) {
        boolean b = true;
        for (int i = 0; i < list.size(); ++i) {
            final AnimCondition animCondition = list.get(i);
            if (animCondition.m_Type == Type.OR) {
                if (b) {
                    break;
                }
                b = true;
            }
            else {
                b = (b && animCondition.check(animationVariableSource));
            }
        }
        return b;
    }
    
    public enum Type
    {
        STRING, 
        STRNEQ, 
        BOOL, 
        EQU, 
        NEQ, 
        LESS, 
        GTR, 
        OR;
        
        private static /* synthetic */ Type[] $values() {
            return new Type[] { Type.STRING, Type.STRNEQ, Type.BOOL, Type.EQU, Type.NEQ, Type.LESS, Type.GTR, Type.OR };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
