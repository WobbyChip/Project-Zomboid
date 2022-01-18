// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiWrapperProvider;
import jassimp.AiBuiltInWrapperProvider;
import jassimp.AiMesh;
import jassimp.AiMaterial;
import jassimp.AiAnimation;
import jassimp.AiScene;
import java.util.Arrays;
import java.util.function.Consumer;
import zombie.util.list.PZArrayUtil;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTexture;
import zombie.core.Core;
import java.util.Iterator;
import java.util.Map;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import jassimp.AiNodeAnim;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTextureSkin;
import jassimp.AiBone;
import java.util.ArrayList;
import org.lwjgl.util.vector.Matrix4f;
import jassimp.AiMatrix4f;
import java.util.List;
import jassimp.AiNode;
import jassimp.JassimpLibraryLoader;
import jassimp.Jassimp;
import java.util.HashMap;
import zombie.util.SharedStrings;
import gnu.trove.map.hash.TObjectIntHashMap;

public final class JAssImpImporter
{
    private static final TObjectIntHashMap<String> sharedStringCounts;
    private static final SharedStrings sharedStrings;
    private static final HashMap<String, Integer> tempHashMap;
    
    public static void Init() {
        Jassimp.setLibraryLoader((JassimpLibraryLoader)new LibraryLoader());
    }
    
    static AiNode FindNode(final String anObject, final AiNode aiNode) {
        final List children = aiNode.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            final AiNode aiNode2 = children.get(i);
            if (aiNode2.getName().equals(anObject)) {
                return aiNode2;
            }
            final AiNode findNode = FindNode(anObject, aiNode2);
            if (findNode != null) {
                return findNode;
            }
        }
        return null;
    }
    
    static Matrix4f getMatrixFromAiMatrix(final AiMatrix4f aiMatrix4f) {
        return getMatrixFromAiMatrix(aiMatrix4f, new Matrix4f());
    }
    
    static Matrix4f getMatrixFromAiMatrix(final AiMatrix4f aiMatrix4f, final Matrix4f matrix4f) {
        matrix4f.m00 = aiMatrix4f.get(0, 0);
        matrix4f.m01 = aiMatrix4f.get(0, 1);
        matrix4f.m02 = aiMatrix4f.get(0, 2);
        matrix4f.m03 = aiMatrix4f.get(0, 3);
        matrix4f.m10 = aiMatrix4f.get(1, 0);
        matrix4f.m11 = aiMatrix4f.get(1, 1);
        matrix4f.m12 = aiMatrix4f.get(1, 2);
        matrix4f.m13 = aiMatrix4f.get(1, 3);
        matrix4f.m20 = aiMatrix4f.get(2, 0);
        matrix4f.m21 = aiMatrix4f.get(2, 1);
        matrix4f.m22 = aiMatrix4f.get(2, 2);
        matrix4f.m23 = aiMatrix4f.get(2, 3);
        matrix4f.m30 = aiMatrix4f.get(3, 0);
        matrix4f.m31 = aiMatrix4f.get(3, 1);
        matrix4f.m32 = aiMatrix4f.get(3, 2);
        matrix4f.m33 = aiMatrix4f.get(3, 3);
        return matrix4f;
    }
    
    static void CollectBoneNodes(final ArrayList<AiNode> list, final AiNode e) {
        list.add(e);
        for (int i = 0; i < e.getNumChildren(); ++i) {
            CollectBoneNodes(list, (AiNode)e.getChildren().get(i));
        }
    }
    
    static String DumpAiMatrix(final AiMatrix4f aiMatrix4f) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, "", String.format("%1$.8f, ", aiMatrix4f.get(0, 0))), String.format("%1$.8f, ", aiMatrix4f.get(0, 1))), String.format("%1$.8f, ", aiMatrix4f.get(0, 2))), String.format("%1$.8f\n ", aiMatrix4f.get(0, 3))), String.format("%1$.8f, ", aiMatrix4f.get(1, 0))), String.format("%1$.8f, ", aiMatrix4f.get(1, 1))), String.format("%1$.8f, ", aiMatrix4f.get(1, 2))), String.format("%1$.8f\n ", aiMatrix4f.get(1, 3))), String.format("%1$.8f, ", aiMatrix4f.get(2, 0))), String.format("%1$.8f, ", aiMatrix4f.get(2, 1))), String.format("%1$.8f, ", aiMatrix4f.get(2, 2))), String.format("%1$.8f\n ", aiMatrix4f.get(2, 3))), String.format("%1$.8f, ", aiMatrix4f.get(3, 0))), String.format("%1$.8f, ", aiMatrix4f.get(3, 1))), String.format("%1$.8f, ", aiMatrix4f.get(3, 2))), String.format("%1$.8f\n ", aiMatrix4f.get(3, 3)));
    }
    
    static String DumpMatrix(final Matrix4f matrix4f) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, "", String.format("%1$.8f, ", matrix4f.m00)), String.format("%1$.8f, ", matrix4f.m01)), String.format("%1$.8f, ", matrix4f.m02)), String.format("%1$.8f\n ", matrix4f.m03)), String.format("%1$.8f, ", matrix4f.m10)), String.format("%1$.8f, ", matrix4f.m11)), String.format("%1$.8f, ", matrix4f.m12)), String.format("%1$.8f\n ", matrix4f.m13)), String.format("%1$.8f, ", matrix4f.m20)), String.format("%1$.8f, ", matrix4f.m21)), String.format("%1$.8f, ", matrix4f.m22)), String.format("%1$.8f\n ", matrix4f.m23)), String.format("%1$.8f, ", matrix4f.m30)), String.format("%1$.8f, ", matrix4f.m31)), String.format("%1$.8f, ", matrix4f.m32)), String.format("%1$.8f\n ", matrix4f.m33));
    }
    
    static AiBone FindAiBone(final String anObject, final List<AiBone> list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final AiBone aiBone = list.get(i);
            if (aiBone.getName().equals(anObject)) {
                return aiBone;
            }
        }
        return null;
    }
    
    private static void DumpMesh(final VertexPositionNormalTangentTextureSkin[] array) {
        final StringBuilder sb = new StringBuilder();
        for (final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin : array) {
            sb.append(vertexPositionNormalTangentTextureSkin.Position.x()).append('\t').append(vertexPositionNormalTangentTextureSkin.Position.y()).append('\t').append(vertexPositionNormalTangentTextureSkin.Position.z()).append('\t').append('\n');
        }
        sb.toString();
    }
    
    static Vector3f GetKeyFramePosition(final AiNodeAnim aiNodeAnim, final float n) {
        int n2 = -1;
        for (int i = 0; i < aiNodeAnim.getNumPosKeys(); ++i) {
            final float n3 = (float)aiNodeAnim.getPosKeyTime(i);
            if (n3 > n) {
                break;
            }
            n2 = i;
            if (n3 == n) {
                return new Vector3f(aiNodeAnim.getPosKeyX(i), aiNodeAnim.getPosKeyY(i), aiNodeAnim.getPosKeyZ(i));
            }
        }
        if (n2 < 0) {
            return new Vector3f();
        }
        if (aiNodeAnim.getNumPosKeys() > n2 + 1) {
            final float n4 = (float)aiNodeAnim.getPosKeyTime(n2);
            final float n5 = (n - n4) / ((float)aiNodeAnim.getPosKeyTime(n2 + 1) - n4);
            final float posKeyX = aiNodeAnim.getPosKeyX(n2);
            final float n6 = posKeyX + n5 * (aiNodeAnim.getPosKeyX(n2 + 1) - posKeyX);
            final float posKeyY = aiNodeAnim.getPosKeyY(n2);
            final float n7 = posKeyY + n5 * (aiNodeAnim.getPosKeyY(n2 + 1) - posKeyY);
            final float posKeyZ = aiNodeAnim.getPosKeyZ(n2);
            return new Vector3f(n6, n7, posKeyZ + n5 * (aiNodeAnim.getPosKeyZ(n2 + 1) - posKeyZ));
        }
        return new Vector3f(aiNodeAnim.getPosKeyX(n2), aiNodeAnim.getPosKeyY(n2), aiNodeAnim.getPosKeyZ(n2));
    }
    
    static Quaternion GetKeyFrameRotation(final AiNodeAnim aiNodeAnim, final float n) {
        int n2 = 0;
        final Quaternion quaternion = new Quaternion();
        int n3 = -1;
        for (int i = 0; i < aiNodeAnim.getNumRotKeys(); ++i) {
            final float n4 = (float)aiNodeAnim.getRotKeyTime(i);
            if (n4 > n) {
                break;
            }
            n3 = i;
            if (n4 == n) {
                quaternion.set(aiNodeAnim.getRotKeyX(i), aiNodeAnim.getRotKeyY(i), aiNodeAnim.getRotKeyZ(i), aiNodeAnim.getRotKeyW(i));
                n2 = 1;
                break;
            }
        }
        if (n2 == 0 && n3 < 0) {
            return new Quaternion();
        }
        if (n2 == 0 && aiNodeAnim.getNumRotKeys() > n3 + 1) {
            final float n5 = (float)aiNodeAnim.getRotKeyTime(n3);
            final float n6 = (n - n5) / ((float)aiNodeAnim.getRotKeyTime(n3 + 1) - n5);
            final float rotKeyX = aiNodeAnim.getRotKeyX(n3);
            final float n7 = rotKeyX + n6 * (aiNodeAnim.getRotKeyX(n3 + 1) - rotKeyX);
            final float rotKeyY = aiNodeAnim.getRotKeyY(n3);
            final float n8 = rotKeyY + n6 * (aiNodeAnim.getRotKeyY(n3 + 1) - rotKeyY);
            final float rotKeyZ = aiNodeAnim.getRotKeyZ(n3);
            final float n9 = rotKeyZ + n6 * (aiNodeAnim.getRotKeyZ(n3 + 1) - rotKeyZ);
            final float rotKeyW = aiNodeAnim.getRotKeyW(n3);
            quaternion.set(n7, n8, n9, rotKeyW + n6 * (aiNodeAnim.getRotKeyW(n3 + 1) - rotKeyW));
            n2 = 1;
        }
        if (n2 == 0 && aiNodeAnim.getNumRotKeys() > n3) {
            quaternion.set(aiNodeAnim.getRotKeyX(n3), aiNodeAnim.getRotKeyY(n3), aiNodeAnim.getRotKeyZ(n3), aiNodeAnim.getRotKeyW(n3));
        }
        return quaternion;
    }
    
    static Vector3f GetKeyFrameScale(final AiNodeAnim aiNodeAnim, final float n) {
        int n2 = -1;
        for (int i = 0; i < aiNodeAnim.getNumScaleKeys(); ++i) {
            final float n3 = (float)aiNodeAnim.getScaleKeyTime(i);
            if (n3 > n) {
                break;
            }
            n2 = i;
            if (n3 == n) {
                return new Vector3f(aiNodeAnim.getScaleKeyX(i), aiNodeAnim.getScaleKeyY(i), aiNodeAnim.getScaleKeyZ(i));
            }
        }
        if (n2 < 0) {
            return new Vector3f(1.0f, 1.0f, 1.0f);
        }
        if (aiNodeAnim.getNumScaleKeys() > n2 + 1) {
            final float n4 = (float)aiNodeAnim.getScaleKeyTime(n2);
            final float n5 = (n - n4) / ((float)aiNodeAnim.getScaleKeyTime(n2 + 1) - n4);
            final float scaleKeyX = aiNodeAnim.getScaleKeyX(n2);
            final float n6 = scaleKeyX + n5 * (aiNodeAnim.getScaleKeyX(n2 + 1) - scaleKeyX);
            final float scaleKeyY = aiNodeAnim.getScaleKeyY(n2);
            final float n7 = scaleKeyY + n5 * (aiNodeAnim.getScaleKeyY(n2 + 1) - scaleKeyY);
            final float scaleKeyZ = aiNodeAnim.getScaleKeyZ(n2);
            return new Vector3f(n6, n7, scaleKeyZ + n5 * (aiNodeAnim.getScaleKeyZ(n2 + 1) - scaleKeyZ));
        }
        return new Vector3f(aiNodeAnim.getScaleKeyX(n2), aiNodeAnim.getScaleKeyY(n2), aiNodeAnim.getScaleKeyZ(n2));
    }
    
    static void replaceHashMapKeys(final HashMap<String, Integer> m, final String s) {
        JAssImpImporter.tempHashMap.clear();
        JAssImpImporter.tempHashMap.putAll(m);
        m.clear();
        for (final Map.Entry<String, Integer> entry : JAssImpImporter.tempHashMap.entrySet()) {
            m.put(getSharedString(entry.getKey(), s), entry.getValue());
        }
        JAssImpImporter.tempHashMap.clear();
    }
    
    public static String getSharedString(final String s, final String s2) {
        final String value = JAssImpImporter.sharedStrings.get(s);
        if (Core.bDebug && s != value) {
            JAssImpImporter.sharedStringCounts.adjustOrPutValue((Object)s2, 1, 0);
        }
        return value;
    }
    
    private static void takeOutTheTrash(final VertexPositionNormalTangentTexture[] a) {
        PZArrayUtil.forEach(a, JAssImpImporter::takeOutTheTrash);
        Arrays.fill(a, null);
    }
    
    private static void takeOutTheTrash(final VertexPositionNormalTangentTextureSkin[] a) {
        PZArrayUtil.forEach(a, JAssImpImporter::takeOutTheTrash);
        Arrays.fill(a, null);
    }
    
    private static void takeOutTheTrash(final VertexPositionNormalTangentTexture vertexPositionNormalTangentTexture) {
        vertexPositionNormalTangentTexture.Normal = null;
        vertexPositionNormalTangentTexture.Position = null;
        vertexPositionNormalTangentTexture.TextureCoordinates = null;
        vertexPositionNormalTangentTexture.Tangent = null;
    }
    
    private static void takeOutTheTrash(final VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin) {
        vertexPositionNormalTangentTextureSkin.Normal = null;
        vertexPositionNormalTangentTextureSkin.Position = null;
        vertexPositionNormalTangentTextureSkin.TextureCoordinates = null;
        vertexPositionNormalTangentTextureSkin.Tangent = null;
        vertexPositionNormalTangentTextureSkin.BlendWeights = null;
        vertexPositionNormalTangentTextureSkin.BlendIndices = null;
    }
    
    public static void takeOutTheTrash(final AiScene aiScene) {
        final Iterator<AiAnimation> iterator = aiScene.getAnimations().iterator();
        while (iterator.hasNext()) {
            iterator.next().getChannels().clear();
        }
        aiScene.getAnimations().clear();
        aiScene.getCameras().clear();
        aiScene.getLights().clear();
        final Iterator<AiMaterial> iterator2 = aiScene.getMaterials().iterator();
        while (iterator2.hasNext()) {
            iterator2.next().getProperties().clear();
        }
        aiScene.getMaterials().clear();
        for (final AiMesh aiMesh : aiScene.getMeshes()) {
            final Iterator iterator4 = aiMesh.getBones().iterator();
            while (iterator4.hasNext()) {
                iterator4.next().getBoneWeights().clear();
            }
            aiMesh.getBones().clear();
        }
        aiScene.getMeshes().clear();
        takeOutTheTrash((AiNode)aiScene.getSceneRoot((AiWrapperProvider)new AiBuiltInWrapperProvider()));
    }
    
    private static void takeOutTheTrash(final AiNode aiNode) {
        final Iterator<AiNode> iterator = aiNode.getChildren().iterator();
        while (iterator.hasNext()) {
            takeOutTheTrash(iterator.next());
        }
        aiNode.getChildren().clear();
    }
    
    static {
        sharedStringCounts = new TObjectIntHashMap();
        sharedStrings = new SharedStrings();
        tempHashMap = new HashMap<String, Integer>();
    }
    
    private static class LibraryLoader extends JassimpLibraryLoader
    {
        public void loadLibrary() {
            if (System.getProperty("os.name").contains("OS X")) {
                System.loadLibrary("jassimp");
            }
            else if (System.getProperty("os.name").startsWith("Win")) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    System.loadLibrary("jassimp64");
                }
                else {
                    System.loadLibrary("jassimp32");
                }
            }
            else if (System.getProperty("sun.arch.data.model").equals("64")) {
                System.loadLibrary("jassimp64");
            }
            else {
                System.loadLibrary("jassimp32");
            }
        }
    }
    
    public enum LoadMode
    {
        Normal, 
        StaticMesh, 
        AnimationOnly;
        
        private static /* synthetic */ LoadMode[] $values() {
            return new LoadMode[] { LoadMode.Normal, LoadMode.StaticMesh, LoadMode.AnimationOnly };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
