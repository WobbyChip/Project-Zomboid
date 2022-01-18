// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.script;

import zombie.radio.globals.CompareResult;

public interface ConditionIter
{
    CompareResult Evaluate();
    
    OperatorType getNextOperator();
    
    void setNextOperator(final OperatorType p0);
}
