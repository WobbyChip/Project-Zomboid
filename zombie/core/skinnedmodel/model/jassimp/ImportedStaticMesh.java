// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiMesh;
import zombie.core.skinnedmodel.model.VertexBufferObject;

public final class ImportedStaticMesh
{
    VertexBufferObject.VertexArray verticesUnskinned;
    int[] elements;
    
    public ImportedStaticMesh(final AiMesh aiMesh) {
        this.verticesUnskinned = null;
        this.elements = null;
        this.processAiScene(aiMesh);
    }
    
    private void processAiScene(final AiMesh aiMesh) {
        final int numVertices = aiMesh.getNumVertices();
        int n = 0;
        for (int i = 0; i < 8; ++i) {
            if (aiMesh.hasTexCoords(i)) {
                ++n;
            }
        }
        final VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(3 + n);
        vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
        vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
        vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
        for (int j = 0; j < n; ++j) {
            vertexFormat.setElement(3 + j, VertexBufferObject.VertexType.TextureCoordArray, 8);
        }
        vertexFormat.calculate();
        this.verticesUnskinned = new VertexBufferObject.VertexArray(vertexFormat, numVertices);
        for (int k = 0; k < numVertices; ++k) {
            this.verticesUnskinned.setElement(k, 0, aiMesh.getPositionX(k), aiMesh.getPositionY(k), aiMesh.getPositionZ(k));
            if (aiMesh.hasNormals()) {
                this.verticesUnskinned.setElement(k, 1, aiMesh.getNormalX(k), aiMesh.getNormalY(k), aiMesh.getNormalZ(k));
            }
            else {
                this.verticesUnskinned.setElement(k, 1, 0.0f, 1.0f, 0.0f);
            }
            if (aiMesh.hasTangentsAndBitangents()) {
                this.verticesUnskinned.setElement(k, 2, aiMesh.getTangentX(k), aiMesh.getTangentY(k), aiMesh.getTangentZ(k));
            }
            else {
                this.verticesUnskinned.setElement(k, 2, 0.0f, 0.0f, 1.0f);
            }
            if (n > 0) {
                int n2 = 0;
                for (int l = 0; l < 8; ++l) {
                    if (aiMesh.hasTexCoords(l)) {
                        this.verticesUnskinned.setElement(k, 3 + n2, aiMesh.getTexCoordU(k, l), 1.0f - aiMesh.getTexCoordV(k, l));
                        ++n2;
                    }
                }
            }
        }
        final int numFaces = aiMesh.getNumFaces();
        this.elements = new int[numFaces * 3];
        for (int n3 = 0; n3 < numFaces; ++n3) {
            this.elements[n3 * 3 + 2] = aiMesh.getFaceVertex(n3, 0);
            this.elements[n3 * 3 + 1] = aiMesh.getFaceVertex(n3, 1);
            this.elements[n3 * 3 + 0] = aiMesh.getFaceVertex(n3, 2);
        }
    }
}
