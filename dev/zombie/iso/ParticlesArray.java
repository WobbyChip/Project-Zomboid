// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.debug.DebugLog;
import java.util.ArrayList;

public final class ParticlesArray<E> extends ArrayList<E>
{
    private boolean needToUpdate;
    private int ParticleSystemsCount;
    private int ParticleSystemsLast;
    
    public ParticlesArray() {
        this.ParticleSystemsCount = 0;
        this.ParticleSystemsLast = 0;
        this.ParticleSystemsCount = 0;
        this.ParticleSystemsLast = 0;
        this.needToUpdate = true;
    }
    
    public synchronized int addParticle(final E element) {
        if (element == null) {
            return -1;
        }
        if (this.size() == this.ParticleSystemsCount) {
            this.add(element);
            ++this.ParticleSystemsCount;
            this.needToUpdate = true;
            return this.size() - 1;
        }
        for (int i = this.ParticleSystemsLast; i < this.size(); ++i) {
            if (this.get(i) == null) {
                this.set(this.ParticleSystemsLast = i, element);
                ++this.ParticleSystemsCount;
                this.needToUpdate = true;
                return i;
            }
        }
        for (int j = 0; j < this.ParticleSystemsLast; ++j) {
            if (this.get(j) == null) {
                this.set(this.ParticleSystemsLast = j, element);
                ++this.ParticleSystemsCount;
                this.needToUpdate = true;
                return j;
            }
        }
        DebugLog.log("ERROR: ParticlesArray.addParticle has unknown error");
        return -1;
    }
    
    public synchronized boolean deleteParticle(final int n) {
        if (n >= 0 && n < this.size() && this.get(n) != null) {
            this.set(n, null);
            --this.ParticleSystemsCount;
            return this.needToUpdate = true;
        }
        return false;
    }
    
    public synchronized void defragmentParticle() {
        this.needToUpdate = false;
        if (this.ParticleSystemsCount == this.size() || this.size() == 0) {
            return;
        }
        int n = -1;
        for (int i = 0; i < this.size(); ++i) {
            if (this.get(i) == null) {
                n = i;
                break;
            }
        }
        for (int j = this.size() - 1; j >= 0; --j) {
            if (this.get(j) != null) {
                this.set(n, this.get(j));
                this.set(j, null);
                for (int k = n; k < this.size(); ++k) {
                    if (this.get(k) == null) {
                        n = k;
                        break;
                    }
                }
                if (n + 1 >= j) {
                    this.ParticleSystemsLast = n;
                    break;
                }
            }
        }
    }
    
    public synchronized int getCount() {
        return this.ParticleSystemsCount;
    }
    
    public synchronized boolean getNeedToUpdate() {
        return this.needToUpdate;
    }
}
