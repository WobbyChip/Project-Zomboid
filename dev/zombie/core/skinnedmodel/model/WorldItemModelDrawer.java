// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.iso.IsoMovingObject;
import org.lwjgl.opengl.GL11;
import zombie.network.ServerGUI;
import zombie.network.GameServer;
import zombie.scripting.objects.ModelAttachment;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.scripting.objects.ModelScript;
import org.joml.Matrix4fc;
import zombie.inventory.types.HandWeapon;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.ModelManager;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Food;
import zombie.scripting.ScriptManager;
import zombie.util.StringUtils;
import zombie.core.Core;
import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import org.joml.Vector3f;
import zombie.core.ImmutableColor;
import org.joml.Matrix4f;
import zombie.core.textures.ColorInfo;
import zombie.popman.ObjectPool;
import zombie.core.textures.TextureDraw;

public final class WorldItemModelDrawer extends TextureDraw.GenericDrawer
{
    private static final ObjectPool<WorldItemModelDrawer> s_modelDrawerPool;
    private static final ColorInfo tempColorInfo;
    private static final Matrix4f s_attachmentXfrm;
    private static final ImmutableColor ROTTEN_FOOD_COLOR;
    private boolean makeDisapear;
    public static boolean NEW_WAY;
    private Model m_model;
    private float m_hue;
    private float m_tintR;
    private float m_tintG;
    private float m_tintB;
    private float m_x;
    private float m_y;
    private float m_z;
    private final Vector3f m_angle;
    private final Matrix4f m_transform;
    private float m_ambientR;
    private float m_ambientG;
    private float m_ambientB;
    private float alpha;
    static Vector3f temprot;
    
    public WorldItemModelDrawer() {
        this.makeDisapear = false;
        this.m_angle = new Vector3f();
        this.m_transform = new Matrix4f();
        this.alpha = 1.0f;
    }
    
    public static boolean renderMain(final InventoryItem inventoryItem, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final float n4) {
        return renderMain(inventoryItem, isoGridSquare, n, n2, n3, n4, -1.0f);
    }
    
    public static boolean renderMain(final InventoryItem inventoryItem, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final float n4, final float y) {
        if (inventoryItem == null || isoGridSquare == null) {
            return false;
        }
        Core.getInstance();
        if (!Core.Option3DGroundItem) {
            return false;
        }
        if (!StringUtils.isNullOrEmpty(inventoryItem.getWorldStaticItem())) {
            ModelScript modelScript = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem());
            if (modelScript != null) {
                String s = modelScript.getMeshName();
                String s2 = modelScript.getTextureName();
                String s3 = modelScript.getShaderName();
                ImmutableColor immutableColor = ImmutableColor.white;
                final float n5 = 1.0f;
                if (inventoryItem instanceof Food) {
                    if (((Food)inventoryItem).isCooked()) {
                        final ModelScript modelScript2 = ScriptManager.instance.getModelScript(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getWorldStaticItem()));
                        if (modelScript2 != null) {
                            s2 = modelScript2.getTextureName();
                            s = modelScript2.getMeshName();
                            s3 = modelScript2.getShaderName();
                            modelScript = modelScript2;
                        }
                    }
                    if (((Food)inventoryItem).isBurnt()) {
                        final ModelScript modelScript3 = ScriptManager.instance.getModelScript(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getWorldStaticItem()));
                        if (modelScript3 != null) {
                            s2 = modelScript3.getTextureName();
                            s = modelScript3.getMeshName();
                            s3 = modelScript3.getShaderName();
                            modelScript = modelScript3;
                        }
                    }
                    if (((Food)inventoryItem).isRotten()) {
                        final ModelScript modelScript4 = ScriptManager.instance.getModelScript(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, inventoryItem.getWorldStaticItem()));
                        if (modelScript4 != null) {
                            s2 = modelScript4.getTextureName();
                            s = modelScript4.getMeshName();
                            s3 = modelScript4.getShaderName();
                            modelScript = modelScript4;
                        }
                        else {
                            immutableColor = WorldItemModelDrawer.ROTTEN_FOOD_COLOR;
                        }
                    }
                }
                if (inventoryItem instanceof Clothing || inventoryItem.getClothingItem() != null) {
                    s2 = modelScript.getTextureName(true);
                    immutableColor = inventoryItem.getVisual().getTint(inventoryItem.getClothingItem());
                    if (s2 == null) {
                        if (!inventoryItem.getClothingItem().textureChoices.isEmpty()) {
                            s2 = inventoryItem.getClothingItem().textureChoices.get(inventoryItem.getVisual().getTextureChoice());
                        }
                        else {
                            s2 = inventoryItem.getClothingItem().m_BaseTextures.get(inventoryItem.getVisual().getBaseTexture());
                        }
                    }
                }
                final boolean bStatic = modelScript.bStatic;
                if (ModelManager.instance.tryGetLoadedModel(s, s2, bStatic, s3, true) == null) {
                    ModelManager.instance.loadAdditionalModel(s, s2, bStatic, s3);
                }
                final Model loadedModel = ModelManager.instance.getLoadedModel(s, s2, bStatic, s3);
                if (loadedModel != null && loadedModel.isReady()) {
                    final WorldItemModelDrawer worldItemModelDrawer = WorldItemModelDrawer.s_modelDrawerPool.alloc();
                    worldItemModelDrawer.init(inventoryItem, isoGridSquare, n, n2, n3, loadedModel, n5, immutableColor, n4);
                    if (modelScript.scale != 1.0f) {
                        worldItemModelDrawer.m_transform.scale(modelScript.scale);
                    }
                    if (inventoryItem.worldScale != 1.0f) {
                        worldItemModelDrawer.m_transform.scale(modelScript.scale * inventoryItem.worldScale);
                    }
                    worldItemModelDrawer.m_angle.x = 0.0f;
                    if (y < 0.0f) {
                        worldItemModelDrawer.m_angle.y = (float)inventoryItem.worldZRotation;
                    }
                    else {
                        worldItemModelDrawer.m_angle.y = y;
                    }
                    worldItemModelDrawer.m_angle.z = 0.0f;
                    if (Core.bDebug) {}
                    SpriteRenderer.instance.drawGeneric(worldItemModelDrawer);
                    return true;
                }
            }
        }
        else if (inventoryItem instanceof Clothing) {
            final ClothingItem clothingItem = inventoryItem.getClothingItem();
            final ItemVisual visual = inventoryItem.getVisual();
            if (clothingItem != null && visual != null && "Bip01_Head".equalsIgnoreCase(clothingItem.m_AttachBone) && (!((Clothing)inventoryItem).isCosmetic() || "Eyes".equals(inventoryItem.getBodyLocation()))) {
                final String model = clothingItem.getModel(false);
                if (!StringUtils.isNullOrWhitespace(model)) {
                    final String textureChoice = visual.getTextureChoice(clothingItem);
                    final boolean static1 = clothingItem.m_Static;
                    final String shader = clothingItem.m_Shader;
                    if (ModelManager.instance.tryGetLoadedModel(model, textureChoice, static1, shader, false) == null) {
                        ModelManager.instance.loadAdditionalModel(model, textureChoice, static1, shader);
                    }
                    final Model loadedModel2 = ModelManager.instance.getLoadedModel(model, textureChoice, static1, shader);
                    if (loadedModel2 != null && loadedModel2.isReady()) {
                        final WorldItemModelDrawer worldItemModelDrawer2 = WorldItemModelDrawer.s_modelDrawerPool.alloc();
                        worldItemModelDrawer2.init(inventoryItem, isoGridSquare, n, n2, n3, loadedModel2, visual.getHue(clothingItem), visual.getTint(clothingItem), n4);
                        if (WorldItemModelDrawer.NEW_WAY) {
                            worldItemModelDrawer2.m_angle.x = 180.0f + n4;
                            if (y < 0.0f) {
                                worldItemModelDrawer2.m_angle.y = (float)inventoryItem.worldZRotation;
                            }
                            else {
                                worldItemModelDrawer2.m_angle.y = y;
                            }
                            worldItemModelDrawer2.m_angle.z = -90.0f;
                            if (Core.bDebug) {}
                            worldItemModelDrawer2.m_transform.translate(-0.08f, 0.0f, 0.05f);
                        }
                        SpriteRenderer.instance.drawGeneric(worldItemModelDrawer2);
                        return true;
                    }
                }
            }
        }
        if (inventoryItem instanceof HandWeapon) {
            final ModelScript modelScript5 = ScriptManager.instance.getModelScript(inventoryItem.getStaticModel());
            if (modelScript5 != null) {
                final String meshName = modelScript5.getMeshName();
                final String textureName = modelScript5.getTextureName();
                final String shaderName = modelScript5.getShaderName();
                final boolean bStatic2 = modelScript5.bStatic;
                if (ModelManager.instance.tryGetLoadedModel(meshName, textureName, bStatic2, shaderName, false) == null) {
                    ModelManager.instance.loadAdditionalModel(meshName, textureName, bStatic2, shaderName);
                }
                final Model loadedModel3 = ModelManager.instance.getLoadedModel(meshName, textureName, bStatic2, shaderName);
                if (loadedModel3 != null && loadedModel3.isReady()) {
                    final WorldItemModelDrawer worldItemModelDrawer3 = WorldItemModelDrawer.s_modelDrawerPool.alloc();
                    worldItemModelDrawer3.init(inventoryItem, isoGridSquare, n, n2, n3, loadedModel3, 1.0f, ImmutableColor.white, n4);
                    if (modelScript5.scale != 1.0f) {
                        worldItemModelDrawer3.m_transform.scale(modelScript5.scale);
                    }
                    if (inventoryItem.worldScale != 1.0f) {
                        worldItemModelDrawer3.m_transform.scale(modelScript5.scale * inventoryItem.worldScale);
                    }
                    worldItemModelDrawer3.m_angle.x = 0.0f;
                    if (!WorldItemModelDrawer.NEW_WAY) {
                        worldItemModelDrawer3.m_angle.y = 180.0f;
                    }
                    if (WorldItemModelDrawer.NEW_WAY) {
                        WorldItemModelDrawer.s_attachmentXfrm.identity();
                        WorldItemModelDrawer.s_attachmentXfrm.rotateXYZ(0.0f, 3.1415927f, 1.5707964f);
                        WorldItemModelDrawer.s_attachmentXfrm.invert();
                        worldItemModelDrawer3.m_transform.mul((Matrix4fc)WorldItemModelDrawer.s_attachmentXfrm);
                    }
                    final ModelAttachment attachmentById = modelScript5.getAttachmentById("world");
                    if (attachmentById != null) {
                        ModelInstanceRenderData.makeAttachmentTransform(attachmentById, WorldItemModelDrawer.s_attachmentXfrm);
                        WorldItemModelDrawer.s_attachmentXfrm.invert();
                        worldItemModelDrawer3.m_transform.mul((Matrix4fc)WorldItemModelDrawer.s_attachmentXfrm);
                    }
                    if (y < 0.0f) {
                        worldItemModelDrawer3.m_angle.y = (float)inventoryItem.worldZRotation;
                    }
                    else {
                        worldItemModelDrawer3.m_angle.y = y;
                    }
                    SpriteRenderer.instance.drawGeneric(worldItemModelDrawer3);
                    return true;
                }
            }
        }
        return false;
    }
    
    private void init(final InventoryItem inventoryItem, final IsoGridSquare isoGridSquare, final float x, final float y, final float z, final Model model, final float hue, final ImmutableColor immutableColor, final float n) {
        this.m_model = model;
        this.m_tintR = immutableColor.r;
        this.m_tintG = immutableColor.g;
        this.m_tintB = immutableColor.b;
        this.m_hue = hue;
        this.m_x = x;
        this.m_y = y;
        this.m_z = z;
        this.m_transform.rotationZ((90.0f + n) * 0.017453292f);
        if (inventoryItem instanceof Clothing) {
            this.m_transform.translate(-0.08f, 0.0f, 0.05f);
        }
        this.m_angle.x = 0.0f;
        this.m_angle.y = 525.0f;
        this.m_angle.z = 0.0f;
        if (WorldItemModelDrawer.NEW_WAY) {
            this.m_transform.identity();
            this.m_angle.y = 0.0f;
            if (model.Mesh != null && model.Mesh.isReady() && model.Mesh.m_transform != null) {
                model.Mesh.m_transform.transpose();
                this.m_transform.mul((Matrix4fc)model.Mesh.m_transform);
                model.Mesh.m_transform.transpose();
            }
        }
        isoGridSquare.interpolateLight(WorldItemModelDrawer.tempColorInfo, this.m_x % 1.0f, this.m_y % 1.0f);
        if (GameServer.bServer && ServerGUI.isCreated()) {
            WorldItemModelDrawer.tempColorInfo.set(1.0f, 1.0f, 1.0f, 1.0f);
        }
        this.m_ambientR = WorldItemModelDrawer.tempColorInfo.r;
        this.m_ambientG = WorldItemModelDrawer.tempColorInfo.g;
        this.m_ambientB = WorldItemModelDrawer.tempColorInfo.b;
    }
    
    @Override
    public void render() {
        if (!this.m_model.bStatic) {
            return;
        }
        final Model model = this.m_model;
        if (model.Effect == null) {
            model.CreateShader("basicEffect");
        }
        final Shader effect = model.Effect;
        if (effect == null || model.Mesh == null || !model.Mesh.isReady()) {
            return;
        }
        this.alpha = 1.0f;
        GL11.glPushAttrib(1048575);
        GL11.glPushClientAttrib(-1);
        Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, 0.0f, false);
        GL11.glRotated(-180.0, 0.0, 1.0, 0.0);
        GL11.glRotated((double)this.m_angle.x, 1.0, 0.0, 0.0);
        GL11.glRotated((double)this.m_angle.y, 0.0, 1.0, 0.0);
        GL11.glRotated((double)this.m_angle.z, 0.0, 0.0, 1.0);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthFunc(513);
        GL11.glDepthMask(true);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glEnable(2929);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        effect.Start();
        if (model.tex != null) {
            effect.setTexture(model.tex, "Texture", 0);
        }
        effect.setDepthBias(0.0f);
        effect.setAmbient(this.m_ambientR * 0.4f, this.m_ambientG * 0.4f, this.m_ambientB * 0.4f);
        effect.setLightingAmount(1.0f);
        effect.setHueShift(this.m_hue);
        effect.setTint(this.m_tintR, this.m_tintG, this.m_tintB);
        effect.setAlpha(this.alpha);
        for (int i = 0; i < 5; ++i) {
            effect.setLight(i, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, 0.0f, 0.0f, 0.0f, null);
        }
        final Vector3f temprot = WorldItemModelDrawer.temprot;
        temprot.x = 0.0f;
        temprot.y = 5.0f;
        temprot.z = -2.0f;
        temprot.rotateY((float)Math.toRadians(this.m_angle.y));
        final float n = 1.5f;
        effect.setLight(4, temprot.x, temprot.z, temprot.y, this.m_ambientR / 4.0f * n, this.m_ambientG / 4.0f * n, this.m_ambientB / 4.0f * n, 5000.0f, Float.NaN, 0.0f, 0.0f, 0.0f, null);
        effect.setTransformMatrix(this.m_transform, false);
        model.Mesh.Draw(effect);
        effect.End();
        if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
            Model.debugDrawAxis(0.0f, 0.0f, 0.0f, 0.5f, 1.0f);
        }
        Core.getInstance().DoPopIsoStuff();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        SpriteRenderer.ringBuffer.restoreVBOs = true;
    }
    
    @Override
    public void postRender() {
        WorldItemModelDrawer.s_modelDrawerPool.release(this);
    }
    
    static {
        s_modelDrawerPool = new ObjectPool<WorldItemModelDrawer>(WorldItemModelDrawer::new);
        tempColorInfo = new ColorInfo();
        s_attachmentXfrm = new Matrix4f();
        ROTTEN_FOOD_COLOR = new ImmutableColor(0.5f, 0.5f, 0.5f);
        WorldItemModelDrawer.NEW_WAY = true;
        WorldItemModelDrawer.temprot = new Vector3f(0.0f, 5.0f, -2.0f);
    }
}
