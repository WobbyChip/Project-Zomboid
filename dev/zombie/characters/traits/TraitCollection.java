// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.traits;

import java.util.function.Predicate;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import java.util.function.Consumer;
import zombie.util.list.PZArrayUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class TraitCollection
{
    private final List<String> m_activeTraitNames;
    private final List<TraitSlot> m_traits;
    
    public TraitCollection() {
        this.m_activeTraitNames = new ArrayList<String>();
        this.m_traits = new ArrayList<TraitSlot>();
    }
    
    public boolean remove(final Object obj) {
        return this.remove(String.valueOf(obj));
    }
    
    public boolean remove(final String s) {
        final int indexOfTrait = this.indexOfTrait(s);
        if (indexOfTrait > -1) {
            this.deactivateTraitSlot(indexOfTrait);
        }
        return indexOfTrait > -1;
    }
    
    public void addAll(final Collection<? extends String> collection) {
        PZArrayUtil.forEach((Iterable<Object>)collection, (Consumer<? super Object>)this::add);
    }
    
    public void removeAll(final Collection<?> collection) {
        PZArrayUtil.forEach(collection, this::remove);
    }
    
    public void clear() {
        PZArrayUtil.forEach(this.m_traits, traitSlot -> traitSlot.m_isSet = false);
        this.m_activeTraitNames.clear();
    }
    
    public int size() {
        return this.m_activeTraitNames.size();
    }
    
    public boolean isEmpty() {
        return this.m_activeTraitNames.isEmpty();
    }
    
    public boolean contains(final Object obj) {
        return this.contains(String.valueOf(obj));
    }
    
    public boolean contains(final String s) {
        final int indexOfTrait = this.indexOfTrait(s);
        return indexOfTrait > -1 && this.getSlotInternal(indexOfTrait).m_isSet;
    }
    
    public void add(final String s) {
        if (s == null) {
            return;
        }
        this.getOrCreateSlotInternal(s).m_isSet = true;
        this.m_activeTraitNames.add(s);
    }
    
    public String get(final int n) {
        return this.m_activeTraitNames.get(n);
    }
    
    public void set(final String s, final boolean b) {
        if (b) {
            this.add(s);
        }
        else {
            this.remove(s);
        }
    }
    
    public TraitSlot getTraitSlot(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        return this.getOrCreateSlotInternal(s);
    }
    
    private int indexOfTrait(final String s) {
        return PZArrayUtil.indexOf(this.m_traits, (Predicate<TraitSlot>)Lambda.predicate(s, TraitSlot::isName));
    }
    
    private TraitSlot getSlotInternal(final int n) {
        return this.m_traits.get(n);
    }
    
    private TraitSlot getOrCreateSlotInternal(final String s) {
        int n = this.indexOfTrait(s);
        if (n == -1) {
            n = this.m_traits.size();
            this.m_traits.add(new TraitSlot(s));
        }
        return this.getSlotInternal(n);
    }
    
    private void deactivateTraitSlot(final int n) {
        final TraitSlot slotInternal = this.getSlotInternal(n);
        slotInternal.m_isSet = false;
        final int index = PZArrayUtil.indexOf(this.m_activeTraitNames, (Predicate<String>)Lambda.predicate(slotInternal.Name, String::equalsIgnoreCase));
        if (index != -1) {
            this.m_activeTraitNames.remove(index);
        }
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PZArrayUtil.arrayToString(this.m_activeTraitNames, "", "", ", "));
    }
    
    public class TraitSlot
    {
        public final String Name;
        private boolean m_isSet;
        
        private TraitSlot(final String name) {
            this.Name = name;
            this.m_isSet = false;
        }
        
        public boolean isName(final String s) {
            return StringUtils.equalsIgnoreCase(this.Name, s);
        }
        
        public boolean isSet() {
            return this.m_isSet;
        }
        
        public void set(final boolean b) {
            if (this.m_isSet == b) {
                return;
            }
            TraitCollection.this.set(this.Name, b);
        }
        
        @Override
        public String toString() {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, this.Name, this.m_isSet);
        }
    }
}
