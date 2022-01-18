// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.animation;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public final class AnimationMultiTrack
{
    private final ArrayList<AnimationTrack> m_tracks;
    private static final ArrayList<AnimationTrack> tempTracks;
    
    public AnimationMultiTrack() {
        this.m_tracks = new ArrayList<AnimationTrack>();
    }
    
    public AnimationTrack findTrack(final String anObject) {
        for (int i = 0; i < this.m_tracks.size(); ++i) {
            final AnimationTrack animationTrack = this.m_tracks.get(i);
            if (animationTrack.name.equals(anObject)) {
                return animationTrack;
            }
        }
        return null;
    }
    
    public void addTrack(final AnimationTrack e) {
        this.m_tracks.add(e);
    }
    
    public void removeTrack(final AnimationTrack animationTrack) {
        final int indexOfTrack = this.getIndexOfTrack(animationTrack);
        if (indexOfTrack > -1) {
            this.removeTrackAt(indexOfTrack);
        }
    }
    
    public void removeTracks(final List<AnimationTrack> c) {
        AnimationMultiTrack.tempTracks.clear();
        AnimationMultiTrack.tempTracks.addAll(c);
        for (int i = 0; i < AnimationMultiTrack.tempTracks.size(); ++i) {
            this.removeTrack(AnimationMultiTrack.tempTracks.get(i));
        }
    }
    
    public void removeTrackAt(final int index) {
        this.m_tracks.remove(index).release();
    }
    
    public int getIndexOfTrack(final AnimationTrack animationTrack) {
        if (animationTrack == null) {
            return -1;
        }
        int n = -1;
        for (int i = 0; i < this.m_tracks.size(); ++i) {
            if (this.m_tracks.get(i) == animationTrack) {
                n = i;
                break;
            }
        }
        return n;
    }
    
    public void Update(final float n) {
        for (int i = 0; i < this.m_tracks.size(); ++i) {
            final AnimationTrack animationTrack = this.m_tracks.get(i);
            animationTrack.Update(n);
            if (animationTrack.CurrentClip == null) {
                this.removeTrackAt(i);
                --i;
            }
        }
    }
    
    public float getDuration() {
        float n = 0.0f;
        for (int i = 0; i < this.m_tracks.size(); ++i) {
            final AnimationTrack animationTrack = this.m_tracks.get(i);
            final float duration = animationTrack.getDuration();
            if (animationTrack.CurrentClip != null && duration > n) {
                n = duration;
            }
        }
        return n;
    }
    
    public void reset() {
        for (int i = 0; i < this.m_tracks.size(); ++i) {
            this.m_tracks.get(i).reset();
        }
        AnimationPlayer.releaseTracks(this.m_tracks);
        this.m_tracks.clear();
    }
    
    public List<AnimationTrack> getTracks() {
        return this.m_tracks;
    }
    
    public int getTrackCount() {
        return this.m_tracks.size();
    }
    
    public AnimationTrack getTrackAt(final int index) {
        return this.m_tracks.get(index);
    }
    
    static {
        tempTracks = new ArrayList<AnimationTrack>();
    }
}
