// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

import java.util.ArrayList;
import zombie.debug.DebugLog;

public final class BodyPartContacts
{
    private static final ContactNode root;
    private static final ContactNode[] nodes;
    
    public static BodyPartType[] getAllContacts(final BodyPartType bodyPartType) {
        for (int i = 0; i < BodyPartContacts.nodes.length; ++i) {
            final ContactNode contactNode = BodyPartContacts.nodes[i];
            if (contactNode.bodyPart == bodyPartType) {
                return contactNode.bodyPartAllContacts;
            }
        }
        return null;
    }
    
    public static BodyPartType[] getChildren(final BodyPartType bodyPartType) {
        for (int i = 0; i < BodyPartContacts.nodes.length; ++i) {
            final ContactNode contactNode = BodyPartContacts.nodes[i];
            if (contactNode.bodyPart == bodyPartType) {
                return contactNode.bodyPartChildren;
            }
        }
        return null;
    }
    
    public static BodyPartType getParent(final BodyPartType bodyPartType) {
        for (int i = 0; i < BodyPartContacts.nodes.length; ++i) {
            final ContactNode contactNode = BodyPartContacts.nodes[i];
            if (contactNode.bodyPart == bodyPartType) {
                if (contactNode.depth == 0) {
                    DebugLog.log("Warning, root node parent is always null.");
                }
                return contactNode.bodyPartParent;
            }
        }
        return null;
    }
    
    public static int getNodeDepth(final BodyPartType bodyPartType) {
        for (int i = 0; i < BodyPartContacts.nodes.length; ++i) {
            final ContactNode contactNode = BodyPartContacts.nodes[i];
            if (contactNode.bodyPart == bodyPartType) {
                if (!contactNode.initialised) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, contactNode.bodyPart.toString()));
                }
                return contactNode.depth;
            }
        }
        return -1;
    }
    
    private static ContactNode getNodeForBodyPart(final BodyPartType bodyPartType) {
        for (int i = 0; i < BodyPartContacts.nodes.length; ++i) {
            if (BodyPartContacts.nodes[i].bodyPart == bodyPartType) {
                return BodyPartContacts.nodes[i];
            }
        }
        return null;
    }
    
    private static void initNodes(final ContactNode contactNode, final int depth, final ContactNode parent) {
        contactNode.parent = parent;
        contactNode.depth = depth;
        final ArrayList<ContactNode> list = new ArrayList<ContactNode>();
        if (contactNode.parent != null) {
            list.add(contactNode.parent);
        }
        if (contactNode.children != null) {
            for (final ContactNode contactNode2 : contactNode.children) {
                list.add(contactNode2);
                initNodes(contactNode2, depth + 1, contactNode);
            }
        }
        list.toArray(contactNode.allContacts = new ContactNode[list.size()]);
        contactNode.initialised = true;
    }
    
    private static void postInit() {
        for (final ContactNode contactNode : BodyPartContacts.nodes) {
            if (contactNode.parent != null) {
                contactNode.bodyPartParent = contactNode.parent.bodyPart;
            }
            if (contactNode.children != null && contactNode.children.length > 0) {
                contactNode.bodyPartChildren = new BodyPartType[contactNode.children.length];
                for (int j = 0; j < contactNode.children.length; ++j) {
                    contactNode.bodyPartChildren[j] = contactNode.children[j].bodyPart;
                }
            }
            else {
                contactNode.bodyPartChildren = new BodyPartType[0];
            }
            if (contactNode.allContacts != null && contactNode.allContacts.length > 0) {
                contactNode.bodyPartAllContacts = new BodyPartType[contactNode.allContacts.length];
                for (int k = 0; k < contactNode.allContacts.length; ++k) {
                    contactNode.bodyPartAllContacts[k] = contactNode.allContacts[k].bodyPart;
                }
            }
            else {
                contactNode.bodyPartAllContacts = new BodyPartType[0];
            }
            if (!contactNode.initialised) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, contactNode.bodyPart.toString()));
            }
        }
    }
    
    static {
        final int toIndex = BodyPartType.ToIndex(BodyPartType.MAX);
        nodes = new ContactNode[toIndex];
        for (int i = 0; i < toIndex; ++i) {
            BodyPartContacts.nodes[i] = new ContactNode(BodyPartType.FromIndex(i));
        }
        root = getNodeForBodyPart(BodyPartType.Torso_Upper);
        BodyPartContacts.root.children = new ContactNode[] { getNodeForBodyPart(BodyPartType.Neck), getNodeForBodyPart(BodyPartType.Torso_Lower), getNodeForBodyPart(BodyPartType.UpperArm_L), getNodeForBodyPart(BodyPartType.UpperArm_R) };
        getNodeForBodyPart(BodyPartType.Neck).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.Head) };
        getNodeForBodyPart(BodyPartType.UpperArm_L).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.ForeArm_L) };
        getNodeForBodyPart(BodyPartType.ForeArm_L).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.Hand_L) };
        getNodeForBodyPart(BodyPartType.UpperArm_R).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.ForeArm_R) };
        getNodeForBodyPart(BodyPartType.ForeArm_R).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.Hand_R) };
        getNodeForBodyPart(BodyPartType.Torso_Lower).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.Groin) };
        getNodeForBodyPart(BodyPartType.Groin).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.UpperLeg_L), getNodeForBodyPart(BodyPartType.UpperLeg_R) };
        getNodeForBodyPart(BodyPartType.UpperLeg_L).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.LowerLeg_L) };
        getNodeForBodyPart(BodyPartType.LowerLeg_L).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.Foot_L) };
        getNodeForBodyPart(BodyPartType.UpperLeg_R).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.LowerLeg_R) };
        getNodeForBodyPart(BodyPartType.LowerLeg_R).children = new ContactNode[] { getNodeForBodyPart(BodyPartType.Foot_R) };
        initNodes(BodyPartContacts.root, 0, null);
        postInit();
    }
    
    private static class ContactNode
    {
        BodyPartType bodyPart;
        int depth;
        ContactNode parent;
        ContactNode[] children;
        ContactNode[] allContacts;
        BodyPartType bodyPartParent;
        BodyPartType[] bodyPartChildren;
        BodyPartType[] bodyPartAllContacts;
        boolean initialised;
        
        public ContactNode(final BodyPartType bodyPart) {
            this.depth = -1;
            this.initialised = false;
            this.bodyPart = bodyPart;
        }
    }
}
