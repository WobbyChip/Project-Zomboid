// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai;

import zombie.characters.SurvivorDesc;
import zombie.ai.states.ThumpState;
import zombie.iso.LosUtil;
import zombie.characters.Stats;
import zombie.characters.SurvivorGroup;
import zombie.characters.IsoPlayer;
import java.util.Stack;
import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import java.util.HashMap;
import zombie.characters.Stance;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;

public final class GameCharacterAIBrain
{
    private final IsoGameCharacter character;
    public final ArrayList<IsoGameCharacter> spottedCharacters;
    public boolean StepBehaviors;
    public Stance stance;
    public boolean controlledByAdvancedPathfinder;
    public boolean isInMeta;
    public final HashMap<Vector3, ArrayList<Vector3>> BlockedMemories;
    public final Vector2 AIFocusPoint;
    public final Vector3 nextPathTarget;
    public IsoMovingObject aiTarget;
    public boolean NextPathNodeInvalidated;
    public final AIBrainPlayerControlVars HumanControlVars;
    String order;
    public ArrayList<IsoZombie> teammateChasingZombies;
    public ArrayList<IsoZombie> chasingZombies;
    public boolean allowLongTermTick;
    public boolean isAI;
    static ArrayList<IsoZombie> tempZombies;
    static IsoGameCharacter compare;
    private static final Stack<Vector3> Vectors;
    
    public IsoGameCharacter getCharacter() {
        return this.character;
    }
    
    public GameCharacterAIBrain(final IsoGameCharacter character) {
        this.spottedCharacters = new ArrayList<IsoGameCharacter>();
        this.BlockedMemories = new HashMap<Vector3, ArrayList<Vector3>>();
        this.AIFocusPoint = new Vector2();
        this.nextPathTarget = new Vector3();
        this.HumanControlVars = new AIBrainPlayerControlVars();
        this.teammateChasingZombies = new ArrayList<IsoZombie>();
        this.chasingZombies = new ArrayList<IsoZombie>();
        this.allowLongTermTick = true;
        this.isAI = false;
        this.character = character;
    }
    
    public void update() {
    }
    
    public void postUpdateHuman(final IsoPlayer isoPlayer) {
    }
    
    public String getOrder() {
        return this.order;
    }
    
    public void setOrder(final String order) {
        this.order = order;
    }
    
    public SurvivorGroup getGroup() {
        return this.character.getDescriptor().getGroup();
    }
    
    public int getCloseZombieCount() {
        this.character.getStats();
        return Stats.NumCloseZombies;
    }
    
    public IsoZombie getClosestChasingZombie(final boolean b) {
        IsoZombie isoZombie = null;
        float n = 1.0E7f;
        for (int i = 0; i < this.chasingZombies.size(); ++i) {
            final IsoZombie isoZombie2 = this.chasingZombies.get(i);
            float distTo = isoZombie2.DistTo(this.character);
            if (isoZombie2.isOnFloor()) {
                distTo += 2.0f;
            }
            if (!LosUtil.lineClearCollide((int)isoZombie2.x, (int)isoZombie2.y, (int)isoZombie2.z, (int)this.character.x, (int)this.character.y, (int)this.character.z, false)) {
                if (isoZombie2.getStateMachine().getCurrent() != ThumpState.instance()) {
                    if (distTo < n && isoZombie2.target == this.character) {
                        n = distTo;
                        isoZombie = this.chasingZombies.get(i);
                    }
                }
            }
        }
        if (isoZombie == null && b) {
            for (int j = 0; j < this.getGroup().Members.size(); ++j) {
                final IsoZombie closestChasingZombie = this.getGroup().Members.get(j).getInstance().getGameCharacterAIBrain().getClosestChasingZombie(false);
                if (closestChasingZombie != null) {
                    final float distTo2 = closestChasingZombie.DistTo(this.character);
                    if (distTo2 < n) {
                        n = distTo2;
                        isoZombie = closestChasingZombie;
                    }
                }
            }
        }
        if (isoZombie == null && b) {
            for (int k = 0; k < this.spottedCharacters.size(); ++k) {
                final IsoZombie closestChasingZombie2 = this.spottedCharacters.get(k).getGameCharacterAIBrain().getClosestChasingZombie(false);
                if (closestChasingZombie2 != null) {
                    final float distTo3 = closestChasingZombie2.DistTo(this.character);
                    if (distTo3 < n) {
                        n = distTo3;
                        isoZombie = closestChasingZombie2;
                    }
                }
            }
        }
        if (isoZombie != null && isoZombie.DistTo(this.character) > 30.0f) {
            return null;
        }
        return isoZombie;
    }
    
    public IsoZombie getClosestChasingZombie() {
        return this.getClosestChasingZombie(true);
    }
    
    public ArrayList<IsoZombie> getClosestChasingZombies(final int n) {
        GameCharacterAIBrain.tempZombies.clear();
        for (int i = 0; i < this.chasingZombies.size(); ++i) {
            final IsoZombie e = this.chasingZombies.get(i);
            e.DistTo(this.character);
            if (!LosUtil.lineClearCollide((int)e.x, (int)e.y, (int)e.z, (int)this.character.x, (int)this.character.y, (int)this.character.z, false)) {
                GameCharacterAIBrain.tempZombies.add(e);
            }
        }
        GameCharacterAIBrain.compare = this.character;
        final float n2;
        final float n3;
        GameCharacterAIBrain.tempZombies.sort((isoZombie, isoZombie2) -> {
            GameCharacterAIBrain.compare.DistTo(isoZombie);
            GameCharacterAIBrain.compare.DistTo(isoZombie2);
            if (n2 > n3) {
                return 1;
            }
            else if (n2 < n3) {
                return -1;
            }
            else {
                return 0;
            }
        });
        int n4 = n - GameCharacterAIBrain.tempZombies.size();
        if (n4 > GameCharacterAIBrain.tempZombies.size() - 2) {
            n4 = GameCharacterAIBrain.tempZombies.size() - 2;
        }
        for (int j = 0; j < n4; ++j) {
            GameCharacterAIBrain.tempZombies.remove(GameCharacterAIBrain.tempZombies.size() - 1);
        }
        return GameCharacterAIBrain.tempZombies;
    }
    
    public void AddBlockedMemory(final int n, final int n2, final int n3) {
        synchronized (this.BlockedMemories) {
            final Vector3 key = new Vector3((float)(int)this.character.x, (float)(int)this.character.y, (float)(int)this.character.z);
            if (!this.BlockedMemories.containsKey(key)) {
                this.BlockedMemories.put(key, new ArrayList<Vector3>());
            }
            final ArrayList<Vector3> list = this.BlockedMemories.get(key);
            final Vector3 vector3 = new Vector3((float)n, (float)n2, (float)n3);
            if (!list.contains(vector3)) {
                list.add(vector3);
            }
        }
    }
    
    public boolean HasBlockedMemory(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        synchronized (this.BlockedMemories) {
            synchronized (GameCharacterAIBrain.Vectors) {
                Vector3 item;
                if (GameCharacterAIBrain.Vectors.isEmpty()) {
                    item = new Vector3();
                }
                else {
                    item = GameCharacterAIBrain.Vectors.pop();
                }
                Vector3 vector3;
                if (GameCharacterAIBrain.Vectors.isEmpty()) {
                    vector3 = new Vector3();
                }
                else {
                    vector3 = GameCharacterAIBrain.Vectors.pop();
                }
                item.x = (float)n;
                item.y = (float)n2;
                item.z = (float)n3;
                vector3.x = (float)n4;
                vector3.y = (float)n5;
                vector3.z = (float)n6;
                if (!this.BlockedMemories.containsKey(item)) {
                    GameCharacterAIBrain.Vectors.push(item);
                    GameCharacterAIBrain.Vectors.push(vector3);
                    return false;
                }
                if (this.BlockedMemories.get(item).contains(vector3)) {
                    GameCharacterAIBrain.Vectors.push(item);
                    GameCharacterAIBrain.Vectors.push(vector3);
                    return true;
                }
                GameCharacterAIBrain.Vectors.push(item);
                GameCharacterAIBrain.Vectors.push(vector3);
            }
        }
        return false;
    }
    
    public void renderlast() {
    }
    
    static {
        GameCharacterAIBrain.tempZombies = new ArrayList<IsoZombie>();
        Vectors = new Stack<Vector3>();
    }
}
