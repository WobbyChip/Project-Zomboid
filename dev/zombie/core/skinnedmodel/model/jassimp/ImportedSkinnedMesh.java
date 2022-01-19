// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model.jassimp;

import java.util.List;
import jassimp.AiBoneWeight;
import jassimp.AiBone;
import jassimp.AiMesh;
import zombie.core.skinnedmodel.model.VertexBufferObject;

public final class ImportedSkinnedMesh
{
    final ImportedSkeleton skeleton;
    String name;
    VertexBufferObject.VertexArray vertices;
    int[] elements;
    
    public ImportedSkinnedMesh(final ImportedSkeleton skeleton, final AiMesh aiMesh) {
        this.vertices = null;
        this.elements = null;
        this.skeleton = skeleton;
        this.processAiScene(aiMesh);
    }
    
    private void processAiScene(final AiMesh aiMesh) {
        this.name = aiMesh.getName();
        final int numVertices = aiMesh.getNumVertices();
        final int n = numVertices * 4;
        final int[] array = new int[n];
        final float[] array2 = new float[n];
        for (int i = 0; i < n; ++i) {
            array2[i] = 0.0f;
        }
        final List bones = aiMesh.getBones();
        for (int size = bones.size(), j = 0; j < size; ++j) {
            final AiBone aiBone = bones.get(j);
            final int intValue = this.skeleton.boneIndices.get(aiBone.getName());
            final List boneWeights = aiBone.getBoneWeights();
            for (int k = 0; k < aiBone.getNumWeights(); ++k) {
                final AiBoneWeight aiBoneWeight = boneWeights.get(k);
                final int n2 = aiBoneWeight.getVertexId() * 4;
                for (int l = 0; l < 4; ++l) {
                    if (array2[n2 + l] == 0.0f) {
                        array2[n2 + l] = aiBoneWeight.getWeight();
                        array[n2 + l] = intValue;
                        break;
                    }
                }
            }
        }
        final int numUVs = getNumUVs(aiMesh);
        final VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(5 + numUVs);
        vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
        vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
        vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
        vertexFormat.setElement(3, VertexBufferObject.VertexType.BlendWeightArray, 16);
        vertexFormat.setElement(4, VertexBufferObject.VertexType.BlendIndexArray, 16);
        for (int n3 = 0; n3 < numUVs; ++n3) {
            vertexFormat.setElement(5 + n3, VertexBufferObject.VertexType.TextureCoordArray, 8);
        }
        vertexFormat.calculate();
        this.vertices = new VertexBufferObject.VertexArray(vertexFormat, numVertices);
        for (int n4 = 0; n4 < numVertices; ++n4) {
            this.vertices.setElement(n4, 0, aiMesh.getPositionX(n4), aiMesh.getPositionY(n4), aiMesh.getPositionZ(n4));
            if (aiMesh.hasNormals()) {
                this.vertices.setElement(n4, 1, aiMesh.getNormalX(n4), aiMesh.getNormalY(n4), aiMesh.getNormalZ(n4));
            }
            else {
                this.vertices.setElement(n4, 1, 0.0f, 1.0f, 0.0f);
            }
            if (aiMesh.hasTangentsAndBitangents()) {
                this.vertices.setElement(n4, 2, aiMesh.getTangentX(n4), aiMesh.getTangentY(n4), aiMesh.getTangentZ(n4));
            }
            else {
                this.vertices.setElement(n4, 2, 0.0f, 0.0f, 1.0f);
            }
            this.vertices.setElement(n4, 3, array2[n4 * 4], array2[n4 * 4 + 1], array2[n4 * 4 + 2], array2[n4 * 4 + 3]);
            this.vertices.setElement(n4, 4, (float)array[n4 * 4], (float)array[n4 * 4 + 1], (float)array[n4 * 4 + 2], (float)array[n4 * 4 + 3]);
            if (numUVs > 0) {
                int n5 = 0;
                for (int n6 = 0; n6 < 8; ++n6) {
                    if (aiMesh.hasTexCoords(n6)) {
                        this.vertices.setElement(n4, 5 + n5, aiMesh.getTexCoordU(n4, n6), 1.0f - aiMesh.getTexCoordV(n4, n6));
                        ++n5;
                    }
                }
            }
        }
        final int numFaces = aiMesh.getNumFaces();
        this.elements = new int[numFaces * 3];
        for (int n7 = 0; n7 < numFaces; ++n7) {
            this.elements[n7 * 3 + 2] = aiMesh.getFaceVertex(n7, 0);
            this.elements[n7 * 3 + 1] = aiMesh.getFaceVertex(n7, 1);
            this.elements[n7 * 3 + 0] = aiMesh.getFaceVertex(n7, 2);
        }
    }
    
    private static int getNumUVs(final AiMesh aiMesh) {
        int n = 0;
        for (int i = 0; i < 8; ++i) {
            if (aiMesh.hasTexCoords(i)) {
                ++n;
            }
        }
        return n;
    }
}
