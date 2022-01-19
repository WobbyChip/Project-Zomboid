// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.action.conditions;

import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.Core;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.util.StringUtils;
import zombie.characters.action.ActionContext;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import org.w3c.dom.Element;
import zombie.characters.action.IActionCondition;

public final class CharacterVariableCondition implements IActionCondition
{
    private Operator op;
    private Object lhsValue;
    private Object rhsValue;
    
    private static Object parseValue(final String s, final boolean b) {
        if (s.length() <= 0) {
            return s;
        }
        final char char1 = s.charAt(0);
        if (char1 == '-' || char1 == '+' || (char1 >= '0' && char1 <= '9')) {
            int i = 0;
            if (char1 >= '0' && char1 <= '9') {
                i = char1 - '0';
            }
            int j;
            for (j = 1; j < s.length(); ++j) {
                final char char2 = s.charAt(j);
                if (char2 >= '0' && char2 <= '9') {
                    i = i * 10 + (char2 - '0');
                }
                else if (char2 != ',') {
                    if (char2 == '.') {
                        ++j;
                        break;
                    }
                    return s;
                }
            }
            if (j == s.length()) {
                return i;
            }
            float f = (float)i;
            float n = 10.0f;
            while (j < s.length()) {
                final char char3 = s.charAt(j);
                if (char3 >= '0' && char3 <= '9') {
                    f += (char3 - '0') / n;
                    n *= 10.0f;
                }
                else if (char3 != ',') {
                    return s;
                }
                ++j;
            }
            if (char1 == '-') {
                f *= -1.0f;
            }
            return f;
        }
        else {
            if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes")) {
                return true;
            }
            if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("no")) {
                return false;
            }
            if (!b) {
                return s;
            }
            if (char1 == '\'' || char1 == '\"') {
                final StringBuilder sb = new StringBuilder(s.length() - 2);
                for (int k = 1; k < s.length(); ++k) {
                    final char char4 = s.charAt(k);
                    switch (char4) {
                        case 34:
                        case 39: {
                            if (char4 == char1) {
                                return sb.toString();
                            }
                            break;
                        }
                        case 92: {
                            sb.append(s.charAt(k));
                            continue;
                        }
                    }
                    sb.append(char4);
                }
                return sb.toString();
            }
            return new CharacterVariableLookup(s);
        }
    }
    
    private boolean load(final Element element) {
        final String nodeName = element.getNodeName();
        switch (nodeName) {
            case "isTrue": {
                this.op = Operator.Equal;
                this.lhsValue = new CharacterVariableLookup(element.getTextContent().trim());
                this.rhsValue = true;
                return true;
            }
            case "isFalse": {
                this.op = Operator.Equal;
                this.lhsValue = new CharacterVariableLookup(element.getTextContent().trim());
                this.rhsValue = false;
                return true;
            }
            case "compare": {
                final String trim = element.getAttribute("op").trim();
                switch (trim) {
                    default: {
                        return false;
                    }
                    case "=":
                    case "==": {
                        this.op = Operator.Equal;
                        break;
                    }
                    case "!=":
                    case "<>": {
                        this.op = Operator.NotEqual;
                        break;
                    }
                    case "<": {
                        this.op = Operator.Less;
                        break;
                    }
                    case ">": {
                        this.op = Operator.Greater;
                        break;
                    }
                    case "<=": {
                        this.op = Operator.LessEqual;
                        break;
                    }
                    case ">=": {
                        this.op = Operator.GreaterEqual;
                        break;
                    }
                }
                this.loadCompareValues(element);
                return true;
            }
            case "gtr": {
                this.op = Operator.Greater;
                this.loadCompareValues(element);
                return true;
            }
            case "less": {
                this.op = Operator.Less;
                this.loadCompareValues(element);
                return true;
            }
            case "equals": {
                this.op = Operator.Equal;
                this.loadCompareValues(element);
                return true;
            }
            case "notEquals": {
                this.op = Operator.NotEqual;
                this.loadCompareValues(element);
                return true;
            }
            case "lessEqual": {
                this.op = Operator.LessEqual;
                this.loadCompareValues(element);
                return true;
            }
            case "gtrEqual": {
                this.op = Operator.GreaterEqual;
                this.loadCompareValues(element);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void loadCompareValues(final Element element) {
        final String trim = element.getAttribute("a").trim();
        final String trim2 = element.getAttribute("b").trim();
        this.lhsValue = parseValue(trim, true);
        this.rhsValue = parseValue(trim2, false);
    }
    
    private static Object resolveValue(final Object o, final IAnimationVariableSource animationVariableSource) {
        if (!(o instanceof CharacterVariableLookup)) {
            return o;
        }
        final String variableString = animationVariableSource.getVariableString(((CharacterVariableLookup)o).variableName);
        if (variableString != null) {
            return parseValue(variableString, false);
        }
        return null;
    }
    
    private boolean resolveCompareTo(final int n) {
        switch (this.op) {
            case Equal: {
                return n == 0;
            }
            case NotEqual: {
                return n != 0;
            }
            case Less: {
                return n < 0;
            }
            case LessEqual: {
                return n <= 0;
            }
            case Greater: {
                return n > 0;
            }
            case GreaterEqual: {
                return n >= 0;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean passes(final ActionContext actionContext, final int n) {
        final IAnimatable owner = actionContext.getOwner();
        final Object resolveValue = resolveValue(this.lhsValue, owner);
        final Object resolveValue2 = resolveValue(this.rhsValue, owner);
        if (resolveValue == null && resolveValue2 instanceof String && StringUtils.isNullOrEmpty((String)resolveValue2)) {
            if (this.op == Operator.Equal) {
                return true;
            }
            if (this.op == Operator.NotEqual) {
                return false;
            }
        }
        if (resolveValue == null || resolveValue2 == null) {
            return false;
        }
        if (((String)resolveValue).getClass().equals(((String)resolveValue2).getClass())) {
            if (resolveValue instanceof String) {
                return this.resolveCompareTo(((String)resolveValue).compareTo((String)resolveValue2));
            }
            if (resolveValue instanceof Integer) {
                return this.resolveCompareTo(((Integer)resolveValue).compareTo((Integer)resolveValue2));
            }
            if (resolveValue instanceof Float) {
                return this.resolveCompareTo(((Float)resolveValue).compareTo((Float)resolveValue2));
            }
            if (resolveValue instanceof Boolean) {
                return this.resolveCompareTo(((Boolean)resolveValue).compareTo((Boolean)resolveValue2));
            }
        }
        final boolean b = resolveValue instanceof Integer;
        final boolean b2 = resolveValue instanceof Float;
        final boolean b3 = resolveValue2 instanceof Integer;
        final boolean b4 = resolveValue2 instanceof Float;
        if ((!b && !b2) || (!b3 && !b4)) {
            return false;
        }
        final boolean b5 = this.lhsValue instanceof CharacterVariableLookup;
        if (b5 == this.rhsValue instanceof CharacterVariableLookup) {
            return this.resolveCompareTo(Float.compare(b2 ? ((float)resolveValue) : ((float)(int)resolveValue), b4 ? ((float)resolveValue2) : ((float)(int)resolveValue2)));
        }
        if (b5) {
            if (b4) {
                return this.resolveCompareTo(Float.compare(b2 ? ((float)resolveValue) : ((float)(int)resolveValue), (float)resolveValue2));
            }
            return this.resolveCompareTo(Integer.compare(b2 ? ((int)(float)resolveValue) : ((int)resolveValue), (int)resolveValue2));
        }
        else {
            if (b2) {
                return this.resolveCompareTo(Float.compare((float)resolveValue, b4 ? ((float)resolveValue2) : ((float)(int)resolveValue2)));
            }
            return this.resolveCompareTo(Integer.compare((int)resolveValue, b4 ? ((int)(float)resolveValue2) : ((int)resolveValue2)));
        }
    }
    
    @Override
    public IActionCondition clone() {
        return this;
    }
    
    private static String getOpString(final Operator operator) {
        switch (operator) {
            case Equal: {
                return " == ";
            }
            case NotEqual: {
                return " != ";
            }
            case Less: {
                return " < ";
            }
            case LessEqual: {
                return " <= ";
            }
            case Greater: {
                return " > ";
            }
            case GreaterEqual: {
                return " >=";
            }
            default: {
                return " ?? ";
            }
        }
    }
    
    private static String valueToString(final Object o) {
        if (o instanceof String) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)o);
        }
        return o.toString();
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, valueToString(this.lhsValue), getOpString(this.op), valueToString(this.rhsValue));
    }
    
    public static class Factory implements IFactory
    {
        @Override
        public IActionCondition create(final Element element) {
            final CharacterVariableCondition characterVariableCondition = new CharacterVariableCondition();
            if (characterVariableCondition.load(element)) {
                return characterVariableCondition;
            }
            return null;
        }
    }
    
    private static class CharacterVariableLookup
    {
        public String variableName;
        
        public CharacterVariableLookup(final String variableName) {
            this.variableName = variableName;
            if (Core.bDebug) {
                AnimatorDebugMonitor.registerVariable(variableName);
            }
        }
        
        @Override
        public String toString() {
            return this.variableName;
        }
    }
    
    enum Operator
    {
        Equal, 
        NotEqual, 
        Less, 
        Greater, 
        LessEqual, 
        GreaterEqual;
        
        private static /* synthetic */ Operator[] $values() {
            return new Operator[] { Operator.Equal, Operator.NotEqual, Operator.Less, Operator.Greater, Operator.LessEqual, Operator.GreaterEqual };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
