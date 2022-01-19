// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

import zombie.core.textures.Texture;
import zombie.iso.IsoDirections;
import java.util.ArrayList;

public final class CharacterTextures
{
    final ArrayList<CTAnimSet> m_animSets;
    
    public CharacterTextures() {
        this.m_animSets = new ArrayList<CTAnimSet>();
    }
    
    CTAnimSet getAnimSet(final String anObject) {
        for (int i = 0; i < this.m_animSets.size(); ++i) {
            final CTAnimSet set = this.m_animSets.get(i);
            if (set.m_name.equals(anObject)) {
                return set;
            }
        }
        return null;
    }
    
    Texture getTexture(final String s, final String s2, final IsoDirections isoDirections, final int n) {
        final CTAnimSet animSet = this.getAnimSet(s);
        if (animSet == null) {
            return null;
        }
        final CTState state = animSet.getState(s2);
        if (state == null) {
            return null;
        }
        final CTEntry entry = state.getEntry(isoDirections, n);
        if (entry == null) {
            return null;
        }
        return entry.m_texture;
    }
    
    void addTexture(final String name, final String s, final IsoDirections isoDirections, final int n, final Texture texture) {
        CTAnimSet animSet = this.getAnimSet(name);
        if (animSet == null) {
            animSet = new CTAnimSet();
            animSet.m_name = name;
            this.m_animSets.add(animSet);
        }
        animSet.addEntry(s, isoDirections, n, texture);
    }
    
    void clear() {
        this.m_animSets.clear();
    }
    
    private static final class CTEntry
    {
        int m_frame;
        Texture m_texture;
    }
    
    private static final class CTEntryList extends ArrayList<CTEntry>
    {
    }
    
    private static final class CTState
    {
        String m_name;
        final CTEntryList[] m_entries;
        
        CTState() {
            this.m_entries = new CTEntryList[IsoDirections.values().length];
            for (int i = 0; i < this.m_entries.length; ++i) {
                this.m_entries[i] = new CTEntryList();
            }
        }
        
        CTEntry getEntry(final IsoDirections isoDirections, final int n) {
            final CTEntryList list = this.m_entries[isoDirections.index()];
            for (int i = 0; i < list.size(); ++i) {
                final CTEntry ctEntry = list.get(i);
                if (ctEntry.m_frame == n) {
                    return ctEntry;
                }
            }
            return null;
        }
        
        void addEntry(final IsoDirections isoDirections, final int frame, final Texture texture) {
            final CTEntryList list = this.m_entries[isoDirections.index()];
            final CTEntry e = new CTEntry();
            e.m_frame = frame;
            e.m_texture = texture;
            list.add(e);
        }
    }
    
    private static final class CTAnimSet
    {
        String m_name;
        final ArrayList<CTState> m_states;
        
        private CTAnimSet() {
            this.m_states = new ArrayList<CTState>();
        }
        
        CTState getState(final String anObject) {
            for (int i = 0; i < this.m_states.size(); ++i) {
                final CTState ctState = this.m_states.get(i);
                if (ctState.m_name.equals(anObject)) {
                    return ctState;
                }
            }
            return null;
        }
        
        void addEntry(final String name, final IsoDirections isoDirections, final int n, final Texture texture) {
            CTState state = this.getState(name);
            if (state == null) {
                state = new CTState();
                state.m_name = name;
                this.m_states.add(state);
            }
            state.addEntry(isoDirections, n, texture);
        }
    }
}
