// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import zombie.asset.AssetPath;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.util.StringUtils;
import zombie.debug.DebugOptions;
import java.util.ArrayList;
import java.util.List;

public final class AnimState
{
    public String m_Name;
    public final List<AnimNode> m_Nodes;
    public int m_DefaultIndex;
    public AnimationSet m_Set;
    private static final boolean s_bDebugLog_NodeConditions = false;
    
    public AnimState() {
        this.m_Name = "";
        this.m_Nodes = new ArrayList<AnimNode>();
        this.m_DefaultIndex = 0;
        this.m_Set = null;
    }
    
    public List<AnimNode> getAnimNodes(final IAnimationVariableSource animationVariableSource, final List<AnimNode> list) {
        list.clear();
        if (this.m_Nodes.size() <= 0) {
            return list;
        }
        if (DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue() && animationVariableSource.getVariableBoolean("dbgForceAnim") && animationVariableSource.isVariable("dbgForceAnimStateName", this.m_Name)) {
            final String variableString = animationVariableSource.getVariableString("dbgForceAnimNodeName");
            for (int i = 0; i < this.m_Nodes.size(); ++i) {
                final AnimNode animNode = this.m_Nodes.get(i);
                if (StringUtils.equalsIgnoreCase(animNode.m_Name, variableString)) {
                    list.add(animNode);
                    break;
                }
            }
            return list;
        }
        int size = -1;
        for (int j = 0; j < this.m_Nodes.size(); ++j) {
            final AnimNode animNode2 = this.m_Nodes.get(j);
            if (!animNode2.isAbstract()) {
                if (animNode2.m_Conditions.size() >= size) {
                    if (animNode2.checkConditions(animationVariableSource)) {
                        if (size < animNode2.m_Conditions.size()) {
                            list.clear();
                            size = animNode2.m_Conditions.size();
                        }
                        list.add(animNode2);
                    }
                }
            }
        }
        if (!list.isEmpty()) {}
        return list;
    }
    
    public static AnimState Parse(final String name, final String s) {
        final boolean enabled = DebugLog.isEnabled(DebugType.Animation);
        final AnimState state = new AnimState();
        state.m_Name = name;
        if (enabled) {
            DebugLog.Animation.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        }
        for (final String pathname : ZomboidFileSystem.instance.resolveAllFiles(s, file -> file.getName().endsWith(".xml"), true)) {
            final String lowerCase = new File(pathname).getName().split(".xml")[0].toLowerCase();
            if (enabled) {
                DebugLog.Animation.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, lowerCase));
            }
            final AnimNodeAsset animNodeAsset = (AnimNodeAsset)AnimNodeAssetManager.instance.load(new AssetPath(ZomboidFileSystem.instance.resolveFileOrGUID(pathname)));
            if (animNodeAsset.isReady()) {
                final AnimNode animNode = animNodeAsset.m_animNode;
                animNode.m_State = state;
                state.m_Nodes.add(animNode);
            }
        }
        return state;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.m_Name, this.m_Nodes.size(), this.m_DefaultIndex);
    }
    
    public static String getStateName(final AnimState animState) {
        return (animState != null) ? animState.m_Name : null;
    }
    
    protected void clear() {
        this.m_Nodes.clear();
        this.m_Set = null;
    }
}
