// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.animation.AnimationClip;
import org.lwjgl.util.vector.Matrix4f;
import java.util.ArrayList;
import java.util.HashMap;

public final class ModelTxt
{
    boolean bStatic;
    boolean bReverse;
    VertexBufferObject.VertexArray vertices;
    int[] elements;
    HashMap<String, Integer> boneIndices;
    ArrayList<Integer> SkeletonHierarchy;
    ArrayList<Matrix4f> bindPose;
    ArrayList<Matrix4f> skinOffsetMatrices;
    ArrayList<Matrix4f> invBindPose;
    HashMap<String, AnimationClip> clips;
    
    public ModelTxt() {
        this.boneIndices = new HashMap<String, Integer>();
        this.SkeletonHierarchy = new ArrayList<Integer>();
        this.bindPose = new ArrayList<Matrix4f>();
        this.skinOffsetMatrices = new ArrayList<Matrix4f>();
        this.invBindPose = new ArrayList<Matrix4f>();
        this.clips = new HashMap<String, AnimationClip>();
    }
}
