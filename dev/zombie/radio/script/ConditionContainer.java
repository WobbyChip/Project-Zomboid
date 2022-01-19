// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.script;

import zombie.radio.globals.CompareMethod;
import zombie.radio.globals.RadioGlobal;
import zombie.radio.globals.CompareResult;
import java.util.ArrayList;
import java.util.List;

public final class ConditionContainer implements ConditionIter
{
    private List<ConditionIter> conditions;
    private OperatorType operatorType;
    
    public ConditionContainer() {
        this(OperatorType.NONE);
    }
    
    public ConditionContainer(final OperatorType operatorType) {
        this.conditions = new ArrayList<ConditionIter>();
        this.operatorType = OperatorType.NONE;
        this.operatorType = operatorType;
    }
    
    @Override
    public CompareResult Evaluate() {
        int n = 0;
        for (int i = 0; i < this.conditions.size(); ++i) {
            final ConditionIter conditionIter = this.conditions.get(i);
            final CompareResult compareResult = (conditionIter != null) ? conditionIter.Evaluate() : CompareResult.Invalid;
            if (compareResult.equals(CompareResult.Invalid)) {
                return compareResult;
            }
            final OperatorType nextOperator = conditionIter.getNextOperator();
            if (i == this.conditions.size() - 1) {
                return nextOperator.equals(OperatorType.NONE) ? ((n == 0) ? compareResult : CompareResult.False) : CompareResult.Invalid;
            }
            if (nextOperator.equals(OperatorType.OR)) {
                if (n == 0 && compareResult.equals(CompareResult.True)) {
                    return compareResult;
                }
                n = 0;
            }
            else if (nextOperator.equals(OperatorType.AND)) {
                n = ((n != 0 || compareResult.equals(CompareResult.False)) ? 1 : 0);
            }
            else if (nextOperator.equals(OperatorType.NONE)) {
                return CompareResult.Invalid;
            }
        }
        return CompareResult.Invalid;
    }
    
    @Override
    public OperatorType getNextOperator() {
        return this.operatorType;
    }
    
    @Override
    public void setNextOperator(final OperatorType operatorType) {
        this.operatorType = operatorType;
    }
    
    public void Add(final ConditionContainer conditionContainer) {
        this.conditions.add(conditionContainer);
    }
    
    public void Add(final RadioGlobal radioGlobal, final RadioGlobal radioGlobal2, final CompareMethod compareMethod, final OperatorType operatorType) {
        this.conditions.add(new Condition(radioGlobal, radioGlobal2, compareMethod, operatorType));
    }
    
    private static final class Condition implements ConditionIter
    {
        private OperatorType operatorType;
        private CompareMethod compareMethod;
        private RadioGlobal valueA;
        private RadioGlobal valueB;
        
        public Condition(final RadioGlobal radioGlobal, final RadioGlobal radioGlobal2, final CompareMethod compareMethod) {
            this(radioGlobal, radioGlobal2, compareMethod, OperatorType.NONE);
        }
        
        public Condition(final RadioGlobal valueA, final RadioGlobal valueB, final CompareMethod compareMethod, final OperatorType operatorType) {
            this.operatorType = OperatorType.NONE;
            this.valueA = valueA;
            this.valueB = valueB;
            this.operatorType = operatorType;
            this.compareMethod = compareMethod;
        }
        
        @Override
        public CompareResult Evaluate() {
            if (this.valueA != null && this.valueB != null) {
                return this.valueA.compare(this.valueB, this.compareMethod);
            }
            return CompareResult.Invalid;
        }
        
        @Override
        public OperatorType getNextOperator() {
            return this.operatorType;
        }
        
        @Override
        public void setNextOperator(final OperatorType operatorType) {
            this.operatorType = operatorType;
        }
    }
}
