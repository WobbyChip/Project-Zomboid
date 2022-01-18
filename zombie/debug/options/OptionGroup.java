// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;
import java.util.ArrayList;

public class OptionGroup implements IDebugOptionGroup
{
    public final IDebugOptionGroup Group;
    private IDebugOptionGroup m_parentGroup;
    private final String m_groupName;
    private final ArrayList<IDebugOption> m_children;
    
    public OptionGroup(final String groupName) {
        this.m_children = new ArrayList<IDebugOption>();
        this.m_groupName = groupName;
        this.Group = this;
    }
    
    public OptionGroup(final IDebugOptionGroup debugOptionGroup, final String s) {
        this.m_children = new ArrayList<IDebugOption>();
        this.m_groupName = getCombinedName(debugOptionGroup, s);
        debugOptionGroup.addChild(this.Group = this);
    }
    
    @Override
    public String getName() {
        return this.m_groupName;
    }
    
    @Override
    public IDebugOptionGroup getParent() {
        return this.m_parentGroup;
    }
    
    @Override
    public void setParent(final IDebugOptionGroup parentGroup) {
        this.m_parentGroup = parentGroup;
    }
    
    @Override
    public Iterable<IDebugOption> getChildren() {
        return this.m_children;
    }
    
    @Override
    public void addChild(final IDebugOption e) {
        this.m_children.add(e);
        e.setParent(this);
        this.onChildAdded(e);
    }
    
    @Override
    public void onChildAdded(final IDebugOption debugOption) {
        this.onDescendantAdded(debugOption);
    }
    
    @Override
    public void onDescendantAdded(final IDebugOption debugOption) {
        if (this.m_parentGroup != null) {
            this.m_parentGroup.onDescendantAdded(debugOption);
        }
    }
    
    public static BooleanDebugOption newOption(final String s, final boolean b) {
        return newOptionInternal(null, s, false, b);
    }
    
    public static BooleanDebugOption newDebugOnlyOption(final String s, final boolean b) {
        return newOptionInternal(null, s, true, b);
    }
    
    public static BooleanDebugOption newOption(final IDebugOptionGroup debugOptionGroup, final String s, final boolean b) {
        return newOptionInternal(debugOptionGroup, s, false, b);
    }
    
    public static BooleanDebugOption newDebugOnlyOption(final IDebugOptionGroup debugOptionGroup, final String s, final boolean b) {
        return newOptionInternal(debugOptionGroup, s, true, b);
    }
    
    private static BooleanDebugOption newOptionInternal(final IDebugOptionGroup debugOptionGroup, final String s, final boolean b, final boolean b2) {
        final BooleanDebugOption booleanDebugOption = new BooleanDebugOption(getCombinedName(debugOptionGroup, s), b, b2);
        if (debugOptionGroup != null) {
            debugOptionGroup.addChild(booleanDebugOption);
        }
        return booleanDebugOption;
    }
    
    private static String getCombinedName(final IDebugOptionGroup debugOptionGroup, final String s) {
        String format;
        if (debugOptionGroup != null) {
            format = String.format("%s.%s", debugOptionGroup.getName(), s);
        }
        else {
            format = s;
        }
        return format;
    }
}
