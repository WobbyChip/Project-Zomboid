// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.util.Map;

abstract class AbstractEntry<TypeK, TypeV> implements Map.Entry<TypeK, TypeV>
{
    protected final TypeK _key;
    protected TypeV _val;
    
    public AbstractEntry(final TypeK key, final TypeV val) {
        this._key = key;
        this._val = val;
    }
    
    public AbstractEntry(final Map.Entry<TypeK, TypeV> entry) {
        this._key = entry.getKey();
        this._val = entry.getValue();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, this._key, this._val);
    }
    
    @Override
    public TypeK getKey() {
        return this._key;
    }
    
    @Override
    public TypeV getValue() {
        return this._val;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        final Map.Entry entry = (Map.Entry)o;
        return eq(this._key, entry.getKey()) && eq(this._val, entry.getValue());
    }
    
    @Override
    public int hashCode() {
        return ((this._key == null) ? 0 : this._key.hashCode()) ^ ((this._val == null) ? 0 : this._val.hashCode());
    }
    
    private static boolean eq(final Object o, final Object obj) {
        return (o == null) ? (obj == null) : o.equals(obj);
    }
}
