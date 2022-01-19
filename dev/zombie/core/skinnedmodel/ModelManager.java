// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

import zombie.characters.AttachedItems.AttachedModels;
import zombie.PredicatedFileWatcher;
import zombie.gameStates.ChooseGameInfo;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import java.util.Locale;
import java.util.Map;
import zombie.core.textures.TextureID;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.VehicleScript;
import java.util.Comparator;
import zombie.util.list.PZArrayUtil;
import zombie.util.Lambda;
import zombie.iso.LosUtil;
import zombie.iso.IsoUtils;
import zombie.iso.IsoMovingObject;
import zombie.iso.LightingJNI;
import zombie.vehicles.BaseVehicle;
import java.util.Collection;
import zombie.iso.IsoWorld;
import zombie.core.skinnedmodel.model.VehicleSubModelInstance;
import zombie.core.skinnedmodel.model.VehicleModelInstance;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.inventory.types.WeaponPart;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.scripting.objects.ItemReplacement;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.IsoPlayer;
import zombie.inventory.InventoryItem;
import zombie.core.skinnedmodel.model.ModelInstanceTextureInitializer;
import zombie.inventory.types.HandWeapon;
import zombie.util.StringUtils;
import zombie.debug.DebugOptions;
import zombie.network.GameClient;
import zombie.util.Type;
import zombie.characters.IsoZombie;
import zombie.core.logger.ExceptionLogger;
import java.util.Objects;
import java.util.Iterator;
import zombie.scripting.ScriptManager;
import zombie.iso.SmokeShader;
import zombie.iso.FireShader;
import zombie.iso.ParticlesFire;
import zombie.iso.PuddlesShader;
import zombie.iso.IsoPuddles;
import java.io.PrintStream;
import zombie.core.opengl.PZGLUtil;
import zombie.iso.WaterShader;
import zombie.iso.IsoWater;
import org.lwjglx.opengl.Util;
import zombie.iso.PlayerCamera;
import zombie.iso.sprite.SkyBox;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL30;
import zombie.core.textures.TextureDraw;
import zombie.DebugFileWatcher;
import org.lwjglx.opengl.Display;
import zombie.core.opengl.RenderThread;
import java.net.URI;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugType;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.asset.Asset;
import zombie.core.Core;
import zombie.GameWindow;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.debug.DebugLog;
import zombie.interfaces.ITexture;
import zombie.core.PerformanceSettings;
import zombie.core.textures.Texture;
import zombie.network.ServerGUI;
import zombie.network.GameServer;
import java.util.TreeMap;
import zombie.iso.Vector2;
import zombie.iso.IsoLightSource;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import java.util.HashSet;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.popman.ObjectPool;
import java.util.ArrayList;
import zombie.core.skinnedmodel.animation.SoftwareSkinnedModelAnim;
import zombie.core.textures.TextureFBO;
import zombie.core.skinnedmodel.model.Model;
import java.util.HashMap;

public final class ModelManager
{
    public static boolean NoOpenGL;
    public static final ModelManager instance;
    private final HashMap<String, Model> m_modelMap;
    public Model m_maleModel;
    public Model m_femaleModel;
    public Model m_skeletonMaleModel;
    public Model m_skeletonFemaleModel;
    public TextureFBO bitmap;
    private boolean m_bCreated;
    public boolean bDebugEnableModels;
    public boolean bCreateSoftwareMeshes;
    public final HashMap<String, SoftwareSkinnedModelAnim> SoftwareMeshAnims;
    private final ArrayList<ModelSlot> m_modelSlots;
    private final ObjectPool<ModelInstance> m_modelInstancePool;
    private ModelMesh m_animModel;
    private final HashMap<String, AnimationAsset> m_animationAssets;
    private final ModAnimations m_gameAnimations;
    private final HashMap<String, ModAnimations> m_modAnimations;
    private final ArrayList<StaticAnimation> m_cachedAnims;
    private final HashSet<IsoGameCharacter> m_contains;
    private final ArrayList<IsoGameCharacter.TorchInfo> m_torches;
    private final Stack<IsoLightSource> m_freeLights;
    private final ArrayList<IsoLightSource> m_torchLights;
    private final ArrayList<IsoGameCharacter> ToRemove;
    private final ArrayList<IsoGameCharacter> ToResetNextFrame;
    private final ArrayList<IsoGameCharacter> ToResetEquippedNextFrame;
    private final ArrayList<ModelSlot> m_resetAfterRender;
    private final Stack<IsoLightSource> m_lights;
    private final Stack<IsoLightSource> m_lightsTemp;
    private final Vector2 m_tempVec2;
    private final Vector2 m_tempVec2_2;
    private static final TreeMap<String, ModelMetaData> modelMetaData;
    static String basicEffect;
    static String isStaticTrue;
    static String shaderEquals;
    static String texA;
    static String amp;
    static HashMap<String, String> toLower;
    static HashMap<String, String> toLowerTex;
    static HashMap<String, String> toLowerKeyRoot;
    static StringBuilder builder;
    
    public ModelManager() {
        this.m_modelMap = new HashMap<String, Model>();
        this.m_bCreated = false;
        this.bDebugEnableModels = true;
        this.bCreateSoftwareMeshes = false;
        this.SoftwareMeshAnims = new HashMap<String, SoftwareSkinnedModelAnim>();
        this.m_modelSlots = new ArrayList<ModelSlot>();
        this.m_modelInstancePool = new ObjectPool<ModelInstance>(ModelInstance::new);
        this.m_animationAssets = new HashMap<String, AnimationAsset>();
        this.m_gameAnimations = new ModAnimations("game");
        this.m_modAnimations = new HashMap<String, ModAnimations>();
        this.m_cachedAnims = new ArrayList<StaticAnimation>();
        this.m_contains = new HashSet<IsoGameCharacter>();
        this.m_torches = new ArrayList<IsoGameCharacter.TorchInfo>();
        this.m_freeLights = new Stack<IsoLightSource>();
        this.m_torchLights = new ArrayList<IsoLightSource>();
        this.ToRemove = new ArrayList<IsoGameCharacter>();
        this.ToResetNextFrame = new ArrayList<IsoGameCharacter>();
        this.ToResetEquippedNextFrame = new ArrayList<IsoGameCharacter>();
        this.m_resetAfterRender = new ArrayList<ModelSlot>();
        this.m_lights = new Stack<IsoLightSource>();
        this.m_lightsTemp = new Stack<IsoLightSource>();
        this.m_tempVec2 = new Vector2();
        this.m_tempVec2_2 = new Vector2();
    }
    
    public boolean isCreated() {
        return this.m_bCreated;
    }
    
    public void create() {
        if (this.m_bCreated) {
            return;
        }
        if (!GameServer.bServer || ServerGUI.isCreated()) {
            final Texture texture = new Texture(1024, 1024, 16);
            PerformanceSettings.UseFBOs = false;
            try {
                this.bitmap = new TextureFBO(texture, false);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                PerformanceSettings.UseFBOs = false;
                DebugLog.Animation.error((Object)"FBO not compatible with gfx card at this time.");
                return;
            }
        }
        DebugLog.Animation.println("Loading 3D models");
        final ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
        meshAssetParams.bStatic = false;
        meshAssetParams.animationsMesh = null;
        final ModelMesh animModel = (ModelMesh)MeshAssetManager.instance.load(new AssetPath("skinned/malebody"), meshAssetParams);
        animModel.m_animationsMesh = animModel;
        this.m_modAnimations.put(this.m_gameAnimations.m_modID, this.m_gameAnimations);
        while (animModel.isEmpty()) {
            GameWindow.fileSystem.updateAsyncTransactions();
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException ex3) {}
            if (!GameServer.bServer) {
                Core.getInstance().StartFrame();
                Core.getInstance().EndFrame();
                Core.getInstance().StartFrameUI();
                Core.getInstance().EndFrameUI();
            }
        }
        try {
            this.loadAnimsFromDir("media/anims/", animModel);
            this.loadAnimsFromDir("media/anims_X/", animModel);
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        if (!ModelManager.NoOpenGL && this.bCreateSoftwareMeshes) {
            this.SoftwareMeshAnims.put(animModel.getPath().getPath(), new SoftwareSkinnedModelAnim(this.m_cachedAnims.toArray(new StaticAnimation[0]), animModel.softwareMesh, animModel.skinningData));
        }
        final Model loadModel = this.loadModel("skinned/malebody", null, animModel);
        final Model loadModel2 = this.loadModel("skinned/femalebody", null, animModel);
        final Model loadModel3 = this.loadModel("skinned/Male_Skeleton", null, animModel);
        final Model loadModel4 = this.loadModel("skinned/Female_Skeleton", null, animModel);
        this.m_animModel = animModel;
        this.loadModAnimations();
        loadModel.addDependency(this.getAnimationAssetRequired("bob/bob_idle"));
        loadModel.addDependency(this.getAnimationAssetRequired("bob/bob_walk"));
        loadModel.addDependency(this.getAnimationAssetRequired("bob/bob_run"));
        loadModel2.addDependency(this.getAnimationAssetRequired("bob/bob_idle"));
        loadModel2.addDependency(this.getAnimationAssetRequired("bob/bob_walk"));
        loadModel2.addDependency(this.getAnimationAssetRequired("bob/bob_run"));
        this.m_animModel = animModel;
        this.m_maleModel = loadModel;
        this.m_femaleModel = loadModel2;
        this.m_skeletonMaleModel = loadModel3;
        this.m_skeletonFemaleModel = loadModel4;
        this.m_bCreated = true;
        AdvancedAnimator.systemInit();
        PopTemplateManager.instance.init();
    }
    
    public void loadAdditionalModel(final String s, final String s2, final boolean b, final String s3) {
        final boolean bCreateSoftwareMeshes = this.bCreateSoftwareMeshes;
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.debugln("createSoftwareMesh: %B, model: %s", bCreateSoftwareMeshes, s);
        }
        final Model loadModelInternal = this.loadModelInternal(s, s2, s3, this.m_animModel, b);
        if (bCreateSoftwareMeshes) {
            this.SoftwareMeshAnims.put(s.toLowerCase(), new SoftwareSkinnedModelAnim(this.m_cachedAnims.toArray(new StaticAnimation[0]), loadModelInternal.softwareMesh, (SkinningData)loadModelInternal.Tag));
        }
    }
    
    public ModelInstance newAdditionalModelInstance(final String s, final String s2, final IsoGameCharacter isoGameCharacter, final AnimationPlayer animationPlayer, final String s3) {
        if (this.tryGetLoadedModel(s, s2, false, s3, false) == null) {
            ModelManager.instance.loadAdditionalModel(s, s2, false, s3);
        }
        return this.newInstance(this.getLoadedModel(s, s2, false, s3), isoGameCharacter, animationPlayer);
    }
    
    private void loadAnimsFromDir(final String child, final ModelMesh modelMesh) {
        this.loadAnimsFromDir(ZomboidFileSystem.instance.baseURI, ZomboidFileSystem.instance.getMediaRootFile().toURI(), new File(ZomboidFileSystem.instance.base, child), modelMesh, this.m_gameAnimations);
    }
    
    private void loadAnimsFromDir(final URI uri, final URI uri2, final File file, final ModelMesh modelMesh, final ModAnimations modAnimations) {
        if (!file.exists()) {
            DebugLog.General.error("ERROR: %s", file.getPath());
            for (File file2 = file.getParentFile(); file2 != null; file2 = file2.getParentFile()) {
                DebugLog.General.error(" - Parent exists: %B, %s", file2.exists(), file2.getPath());
            }
        }
        if (!file.isDirectory()) {
            return;
        }
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        boolean b = false;
        for (final File file3 : listFiles) {
            if (file3.isDirectory()) {
                this.loadAnimsFromDir(uri, uri2, file3, modelMesh, modAnimations);
            }
            else {
                this.loadAnim(ZomboidFileSystem.instance.getAnimName(uri2, file3), modelMesh, modAnimations);
                b = true;
                if (!ModelManager.NoOpenGL && RenderThread.RenderThread == null) {
                    Display.processMessages();
                }
            }
        }
        if (b) {
            DebugFileWatcher.instance.add(new AnimDirReloader(uri, uri2, file.getPath(), modelMesh, modAnimations).GetFileWatcher());
        }
    }
    
    public void RenderSkyBox(final TextureDraw textureDraw, final int n, final int n2, final int n3, final int n4) {
        final int currentID = TextureFBO.getCurrentID();
        switch (n3) {
            case 1: {
                GL30.glBindFramebuffer(36160, n4);
                break;
            }
            case 2: {
                ARBFramebufferObject.glBindFramebuffer(36160, n4);
                break;
            }
            case 3: {
                EXTFramebufferObject.glBindFramebufferEXT(36160, n4);
                break;
            }
        }
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, 1.0, 1.0, 0.0, -1.0, 1.0);
        GL11.glViewport(0, 0, 512, 512);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ARBShaderObjects.glUseProgramObjectARB(n);
        if (Shader.ShaderMap.containsKey(n)) {
            Shader.ShaderMap.get(n).startRenderThread(textureDraw);
        }
        GL11.glColor4f(0.13f, 0.96f, 0.13f, 1.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(0.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(1.0f, 0.0f);
        GL11.glEnd();
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        final PlayerCamera renderingPlayerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(n2);
        GL11.glViewport(0, 0, renderingPlayerCamera.OffscreenWidth, renderingPlayerCamera.OffscreenHeight);
        switch (n3) {
            case 1: {
                GL30.glBindFramebuffer(36160, currentID);
                break;
            }
            case 2: {
                ARBFramebufferObject.glBindFramebuffer(36160, currentID);
                break;
            }
            case 3: {
                EXTFramebufferObject.glBindFramebufferEXT(36160, currentID);
                break;
            }
        }
        SkyBox.getInstance().swapTextureFBO();
    }
    
    public void RenderWater(final TextureDraw textureDraw, final int i, final int n, final boolean b) {
        try {
            Util.checkGLError();
        }
        catch (Throwable t) {}
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        IsoWater.getInstance().waterProjection();
        SpriteRenderer.instance.getRenderingPlayerCamera(n);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ARBShaderObjects.glUseProgramObjectARB(i);
        final Shader shader = Shader.ShaderMap.get(i);
        if (shader instanceof WaterShader) {
            ((WaterShader)shader).updateWaterParams(textureDraw, n);
        }
        IsoWater.getInstance().waterGeometry(b);
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        if (!PZGLUtil.checkGLError(true)) {
            DebugLog.General.println("DEBUG: EXCEPTION RenderWater");
            PZGLUtil.printGLState(DebugLog.General);
        }
    }
    
    public void RenderPuddles(final int i, final int n, final int n2) {
        PZGLUtil.checkGLError(true);
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        IsoPuddles.getInstance().puddlesProjection();
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ARBShaderObjects.glUseProgramObjectARB(i);
        final Shader shader = Shader.ShaderMap.get(i);
        if (shader instanceof PuddlesShader) {
            ((PuddlesShader)shader).updatePuddlesParams(n, n2);
        }
        IsoPuddles.getInstance().puddlesGeometry(n2);
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        if (!PZGLUtil.checkGLError(true)) {
            DebugLog.General.println("DEBUG: EXCEPTION RenderPuddles");
            PZGLUtil.printGLState(DebugLog.General);
        }
    }
    
    public void RenderParticles(final TextureDraw textureDraw, final int n, final int n2) {
        final int fireShaderID = ParticlesFire.getInstance().getFireShaderID();
        final int smokeShaderID = ParticlesFire.getInstance().getSmokeShaderID();
        ParticlesFire.getInstance().getVapeShaderID();
        try {
            Util.checkGLError();
        }
        catch (Throwable t) {}
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, SpriteRenderer.instance.getRenderingPlayerCamera(n).OffscreenWidth, SpriteRenderer.instance.getRenderingPlayerCamera(n).OffscreenHeight);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        final float shaderTime = ParticlesFire.getInstance().getShaderTime();
        GL11.glBlendFunc(770, 1);
        ARBShaderObjects.glUseProgramObjectARB(fireShaderID);
        final Shader shader = Shader.ShaderMap.get(fireShaderID);
        if (shader instanceof FireShader) {
            ((FireShader)shader).updateFireParams(textureDraw, n, shaderTime);
        }
        ParticlesFire.getInstance().getGeometryFire(n2);
        GL11.glBlendFunc(770, 771);
        ARBShaderObjects.glUseProgramObjectARB(smokeShaderID);
        final Shader shader2 = Shader.ShaderMap.get(smokeShaderID);
        if (shader2 instanceof SmokeShader) {
            ((SmokeShader)shader2).updateSmokeParams(textureDraw, n, shaderTime);
        }
        ParticlesFire.getInstance().getGeometry(n2);
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glViewport(0, 0, SpriteRenderer.instance.getRenderingPlayerCamera(n).OffscreenWidth, SpriteRenderer.instance.getRenderingPlayerCamera(n).OffscreenHeight);
        if (!PZGLUtil.checkGLError(true)) {
            DebugLog.General.println("DEBUG: EXCEPTION RenderParticles");
            PZGLUtil.printGLState(DebugLog.General);
        }
    }
    
    public void Reset(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.legsSprite == null || isoGameCharacter.legsSprite.modelSlot == null) {
            return;
        }
        final ModelSlot modelSlot = isoGameCharacter.legsSprite.modelSlot;
        this.resetModelInstance(modelSlot.model, modelSlot);
        for (int i = 0; i < modelSlot.sub.size(); ++i) {
            final ModelInstance o = modelSlot.sub.get(i);
            if (o != isoGameCharacter.primaryHandModel) {
                if (o != isoGameCharacter.secondaryHandModel) {
                    if (!modelSlot.attachedModels.contains(o)) {
                        this.resetModelInstanceRecurse(o, modelSlot);
                    }
                }
            }
        }
        this.derefModelInstances(isoGameCharacter.getReadyModelData());
        isoGameCharacter.getReadyModelData().clear();
        this.dressInRandomOutfit(isoGameCharacter);
        (modelSlot.model = this.newInstance(this.getBodyModel(isoGameCharacter), isoGameCharacter, isoGameCharacter.getAnimationPlayer())).setOwner(modelSlot);
        modelSlot.model.m_modelScript = ScriptManager.instance.getModelScript(isoGameCharacter.isFemale() ? "FemaleBody" : "MaleBody");
        this.DoCharacterModelParts(isoGameCharacter, modelSlot);
    }
    
    public void reloadAllOutfits() {
        final Iterator<IsoGameCharacter> iterator = this.m_contains.iterator();
        while (iterator.hasNext()) {
            iterator.next().reloadOutfit();
        }
    }
    
    public void Add(final IsoGameCharacter isoGameCharacter) {
        if (!this.m_bCreated) {
            return;
        }
        if (!isoGameCharacter.isSceneCulled()) {
            return;
        }
        if (this.ToRemove.contains(isoGameCharacter)) {
            this.ToRemove.remove(isoGameCharacter);
            isoGameCharacter.legsSprite.modelSlot.bRemove = false;
            return;
        }
        final ModelSlot slot = this.getSlot(isoGameCharacter);
        slot.framesSinceStart = 0;
        if (slot.model != null) {
            final ModelInstance model = slot.model;
            Objects.requireNonNull(model);
            RenderThread.invokeOnRenderContext(model::destroySmartTextures);
        }
        this.dressInRandomOutfit(isoGameCharacter);
        (slot.model = this.newInstance(this.getBodyModel(isoGameCharacter), isoGameCharacter, isoGameCharacter.getAnimationPlayer())).setOwner(slot);
        slot.model.m_modelScript = ScriptManager.instance.getModelScript(isoGameCharacter.isFemale() ? "FemaleBody" : "MaleBody");
        this.DoCharacterModelParts(isoGameCharacter, slot);
        slot.active = true;
        slot.character = isoGameCharacter;
        slot.model.character = isoGameCharacter;
        slot.model.object = isoGameCharacter;
        slot.model.SetForceDir(slot.model.character.getForwardDirection());
        for (int i = 0; i < slot.sub.size(); ++i) {
            final ModelInstance modelInstance = slot.sub.get(i);
            modelInstance.character = isoGameCharacter;
            modelInstance.object = isoGameCharacter;
        }
        isoGameCharacter.legsSprite.modelSlot = slot;
        this.m_contains.add(isoGameCharacter);
        isoGameCharacter.onCullStateChanged(this, false);
        if (slot.model.AnimPlayer != null && slot.model.AnimPlayer.isBoneTransformsNeedFirstFrame()) {
            try {
                slot.Update();
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
    }
    
    public void dressInRandomOutfit(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (isoZombie == null || isoZombie.isReanimatedPlayer() || isoZombie.wasFakeDead()) {
            if (GameClient.bClient && isoZombie != null && !isoGameCharacter.isPersistentOutfitInit() && isoGameCharacter.getPersistentOutfitID() != 0) {
                isoZombie.dressInPersistentOutfitID(isoGameCharacter.getPersistentOutfitID());
            }
            return;
        }
        if (DebugOptions.instance.ZombieOutfitRandom.getValue() && !isoGameCharacter.isPersistentOutfitInit()) {
            isoZombie.bDressInRandomOutfit = true;
        }
        if (isoZombie.bDressInRandomOutfit) {
            isoZombie.bDressInRandomOutfit = false;
            isoZombie.dressInRandomOutfit();
        }
        if (!isoGameCharacter.isPersistentOutfitInit()) {
            isoZombie.dressInPersistentOutfitID(isoGameCharacter.getPersistentOutfitID());
        }
    }
    
    public Model getBodyModel(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.isZombie() && ((IsoZombie)isoGameCharacter).isSkeleton()) {
            if (isoGameCharacter.isFemale()) {
                return this.m_skeletonFemaleModel;
            }
            return this.m_skeletonMaleModel;
        }
        else {
            if (isoGameCharacter.isFemale()) {
                return this.m_femaleModel;
            }
            return this.m_maleModel;
        }
    }
    
    public boolean ContainsChar(final IsoGameCharacter isoGameCharacter) {
        return this.m_contains.contains(isoGameCharacter) && !this.ToRemove.contains(isoGameCharacter);
    }
    
    public void ResetCharacterEquippedHands(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null || isoGameCharacter.legsSprite == null || isoGameCharacter.legsSprite.modelSlot == null) {
            return;
        }
        this.DoCharacterModelEquipped(isoGameCharacter, isoGameCharacter.legsSprite.modelSlot);
    }
    
    private void DoCharacterModelEquipped(final IsoGameCharacter isoGameCharacter, final ModelSlot owner) {
        if (isoGameCharacter.primaryHandModel != null) {
            isoGameCharacter.clearVariable("RightHandMask");
            isoGameCharacter.primaryHandModel.maskVariableValue = null;
            this.resetModelInstanceRecurse(isoGameCharacter.primaryHandModel, owner);
            owner.sub.remove(isoGameCharacter.primaryHandModel);
            owner.model.sub.remove(isoGameCharacter.primaryHandModel);
            isoGameCharacter.primaryHandModel = null;
        }
        if (isoGameCharacter.secondaryHandModel != null) {
            isoGameCharacter.clearVariable("LeftHandMask");
            isoGameCharacter.secondaryHandModel.maskVariableValue = null;
            this.resetModelInstanceRecurse(isoGameCharacter.secondaryHandModel, owner);
            owner.sub.remove(isoGameCharacter.secondaryHandModel);
            owner.model.sub.remove(isoGameCharacter.secondaryHandModel);
            isoGameCharacter.secondaryHandModel = null;
        }
        for (int i = 0; i < owner.attachedModels.size(); ++i) {
            final ModelInstance modelInstance = owner.attachedModels.get(i);
            this.resetModelInstanceRecurse(modelInstance, owner);
            owner.sub.remove(modelInstance);
            owner.model.sub.remove(modelInstance);
        }
        owner.attachedModels.clear();
        for (int j = 0; j < isoGameCharacter.getAttachedItems().size(); ++j) {
            final AttachedItem value = isoGameCharacter.getAttachedItems().get(j);
            final String staticModel = value.getItem().getStaticModel();
            if (!StringUtils.isNullOrWhitespace(staticModel)) {
                final String attachmentName = isoGameCharacter.getAttachedItems().getGroup().getLocation(value.getLocation()).getAttachmentName();
                final ModelInstance addStatic = this.addStatic(owner.model, staticModel, attachmentName, attachmentName);
                if (addStatic != null) {
                    addStatic.setOwner(owner);
                    owner.sub.add(addStatic);
                    final HandWeapon handWeapon = Type.tryCastTo(value.getItem(), HandWeapon.class);
                    if (handWeapon != null) {
                        this.addWeaponPartModels(owner, handWeapon, addStatic);
                        if (!Core.getInstance().getOptionSimpleWeaponTextures()) {
                            final ModelInstanceTextureInitializer alloc = ModelInstanceTextureInitializer.alloc();
                            alloc.init(addStatic, handWeapon);
                            addStatic.setTextureInitializer(alloc);
                        }
                    }
                    owner.attachedModels.add(addStatic);
                }
            }
        }
        if (isoGameCharacter instanceof IsoZombie) {}
        InventoryItem inventoryItem = isoGameCharacter.getPrimaryHandItem();
        InventoryItem inventoryItem2 = isoGameCharacter.getSecondaryHandItem();
        if (isoGameCharacter.isHideWeaponModel()) {
            inventoryItem = null;
            inventoryItem2 = null;
        }
        if (isoGameCharacter instanceof IsoPlayer && isoGameCharacter.forceNullOverride) {
            inventoryItem = null;
            inventoryItem2 = null;
            isoGameCharacter.forceNullOverride = false;
        }
        boolean b = false;
        final BaseAction baseAction = isoGameCharacter.getCharacterActions().isEmpty() ? null : isoGameCharacter.getCharacterActions().get(0);
        if (baseAction != null && baseAction.overrideHandModels) {
            b = true;
            inventoryItem = null;
            if (baseAction.getPrimaryHandItem() != null) {
                inventoryItem = baseAction.getPrimaryHandItem();
            }
            else if (baseAction.getPrimaryHandMdl() != null) {
                isoGameCharacter.primaryHandModel = this.addStatic(owner, baseAction.getPrimaryHandMdl(), "Bip01_Prop1");
            }
            inventoryItem2 = null;
            if (baseAction.getSecondaryHandItem() != null) {
                inventoryItem2 = baseAction.getSecondaryHandItem();
            }
            else if (baseAction.getSecondaryHandMdl() != null) {
                isoGameCharacter.secondaryHandModel = this.addStatic(owner, baseAction.getSecondaryHandMdl(), "Bip01_Prop2");
            }
        }
        if (!StringUtils.isNullOrEmpty(isoGameCharacter.overridePrimaryHandModel)) {
            b = true;
            isoGameCharacter.primaryHandModel = this.addStatic(owner, isoGameCharacter.overridePrimaryHandModel, "Bip01_Prop1");
        }
        if (!StringUtils.isNullOrEmpty(isoGameCharacter.overrideSecondaryHandModel)) {
            b = true;
            isoGameCharacter.secondaryHandModel = this.addStatic(owner, isoGameCharacter.overrideSecondaryHandModel, "Bip01_Prop2");
        }
        if (inventoryItem != null) {
            isoGameCharacter.primaryHandModel = this.addEquippedModelInstance(isoGameCharacter, owner, inventoryItem, "Bip01_Prop1", inventoryItem.getItemReplacementPrimaryHand(), b);
        }
        if (inventoryItem2 != null && inventoryItem != inventoryItem2) {
            isoGameCharacter.secondaryHandModel = this.addEquippedModelInstance(isoGameCharacter, owner, inventoryItem2, "Bip01_Prop2", inventoryItem2.getItemReplacementSecondHand(), b);
        }
    }
    
    private ModelInstance addEquippedModelInstance(final IsoGameCharacter isoGameCharacter, final ModelSlot modelSlot, final InventoryItem inventoryItem, final String s, final ItemReplacement itemReplacement, final boolean b) {
        final HandWeapon handWeapon = Type.tryCastTo(inventoryItem, HandWeapon.class);
        if (handWeapon == null) {
            if (inventoryItem != null) {
                if (itemReplacement != null && !StringUtils.isNullOrEmpty(itemReplacement.maskVariableValue) && (itemReplacement.clothingItem != null || !StringUtils.isNullOrWhitespace(inventoryItem.getStaticModel()))) {
                    return this.addMaskingModel(modelSlot, isoGameCharacter, inventoryItem, itemReplacement, itemReplacement.maskVariableValue, itemReplacement.attachment, s);
                }
                if (b && !StringUtils.isNullOrWhitespace(inventoryItem.getStaticModel())) {
                    return this.addStatic(modelSlot, inventoryItem.getStaticModel(), s);
                }
            }
            return null;
        }
        final ModelInstance addStatic = this.addStatic(modelSlot, handWeapon.getStaticModel(), s);
        this.addWeaponPartModels(modelSlot, handWeapon, addStatic);
        if (Core.getInstance().getOptionSimpleWeaponTextures()) {
            return addStatic;
        }
        final ModelInstanceTextureInitializer alloc = ModelInstanceTextureInitializer.alloc();
        alloc.init(addStatic, handWeapon);
        addStatic.setTextureInitializer(alloc);
        return addStatic;
    }
    
    private ModelInstance addMaskingModel(final ModelSlot modelSlot, final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem, final ItemReplacement itemReplacement, final String maskVariableValue, final String s, final String s2) {
        final ItemVisual visual = inventoryItem.getVisual();
        ModelInstance modelInstance;
        if (itemReplacement.clothingItem != null && visual != null) {
            modelInstance = PopTemplateManager.instance.addClothingItem(isoGameCharacter, modelSlot, visual, itemReplacement.clothingItem);
        }
        else {
            if (StringUtils.isNullOrWhitespace(inventoryItem.getStaticModel())) {
                return null;
            }
            String s3 = null;
            if (visual != null && inventoryItem.getClothingItem() != null) {
                s3 = inventoryItem.getClothingItem().getTextureChoices().get(visual.getTextureChoice());
            }
            if (!StringUtils.isNullOrEmpty(s)) {
                modelInstance = this.addStaticForcedTex(modelSlot.model, inventoryItem.getStaticModel(), s, s, s3);
            }
            else {
                modelInstance = this.addStaticForcedTex(modelSlot, inventoryItem.getStaticModel(), s2, s3);
            }
            modelInstance.maskVariableValue = maskVariableValue;
            if (visual != null) {
                modelInstance.tintR = visual.m_Tint.r;
                modelInstance.tintG = visual.m_Tint.g;
                modelInstance.tintB = visual.m_Tint.b;
            }
        }
        if (!StringUtils.isNullOrEmpty(maskVariableValue)) {
            isoGameCharacter.setVariable(itemReplacement.maskVariableName, maskVariableValue);
            isoGameCharacter.bUpdateEquippedTextures = true;
        }
        return modelInstance;
    }
    
    private void addWeaponPartModels(final ModelSlot owner, final HandWeapon handWeapon, final ModelInstance modelInstance) {
        final ArrayList<ModelWeaponPart> modelWeaponPart = handWeapon.getModelWeaponPart();
        if (modelWeaponPart != null) {
            final ArrayList<WeaponPart> allWeaponParts = handWeapon.getAllWeaponParts();
            for (int i = 0; i < allWeaponParts.size(); ++i) {
                final WeaponPart weaponPart = allWeaponParts.get(i);
                for (int j = 0; j < modelWeaponPart.size(); ++j) {
                    final ModelWeaponPart modelWeaponPart2 = modelWeaponPart.get(j);
                    if (weaponPart.getFullType().equals(modelWeaponPart2.partType)) {
                        this.addStatic(modelInstance, modelWeaponPart2.modelName, modelWeaponPart2.attachmentNameSelf, modelWeaponPart2.attachmentParent).setOwner(owner);
                    }
                }
            }
        }
    }
    
    public void resetModelInstance(final ModelInstance modelInstance, final Object o) {
        if (modelInstance == null) {
            return;
        }
        modelInstance.clearOwner(o);
        if (modelInstance.isRendering()) {
            modelInstance.bResetAfterRender = true;
        }
        else {
            if (modelInstance instanceof VehicleModelInstance) {
                return;
            }
            if (modelInstance instanceof VehicleSubModelInstance) {
                return;
            }
            modelInstance.reset();
            this.m_modelInstancePool.release(modelInstance);
        }
    }
    
    public void resetModelInstanceRecurse(final ModelInstance modelInstance, final Object o) {
        if (modelInstance == null) {
            return;
        }
        this.resetModelInstancesRecurse(modelInstance.sub, o);
        this.resetModelInstance(modelInstance, o);
    }
    
    public void resetModelInstancesRecurse(final ArrayList<ModelInstance> list, final Object o) {
        for (int i = 0; i < list.size(); ++i) {
            this.resetModelInstance(list.get(i), o);
        }
    }
    
    public void derefModelInstance(final ModelInstance modelInstance) {
        if (modelInstance == null) {
            return;
        }
        assert modelInstance.renderRefCount > 0;
        --modelInstance.renderRefCount;
        if (modelInstance.bResetAfterRender && !modelInstance.isRendering()) {
            assert modelInstance.getOwner() == null;
            if (modelInstance instanceof VehicleModelInstance) {
                return;
            }
            if (modelInstance instanceof VehicleSubModelInstance) {
                return;
            }
            modelInstance.reset();
            this.m_modelInstancePool.release(modelInstance);
        }
    }
    
    public void derefModelInstances(final ArrayList<ModelInstance> list) {
        for (int i = 0; i < list.size(); ++i) {
            this.derefModelInstance(list.get(i));
        }
    }
    
    private void DoCharacterModelParts(final IsoGameCharacter isoGameCharacter, final ModelSlot modelSlot) {
        if (modelSlot.isRendering()) {}
        if (DebugLog.isEnabled(DebugType.Clothing)) {
            DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoGameCharacter;Lzombie/core/skinnedmodel/ModelManager$ModelSlot;)Ljava/lang/String;, isoGameCharacter, modelSlot));
        }
        modelSlot.sub.clear();
        PopTemplateManager.instance.populateCharacterModelSlot(isoGameCharacter, modelSlot);
        this.DoCharacterModelEquipped(isoGameCharacter, modelSlot);
    }
    
    public void update() {
        for (int i = 0; i < this.ToResetNextFrame.size(); ++i) {
            this.Reset(this.ToResetNextFrame.get(i));
        }
        this.ToResetNextFrame.clear();
        for (int j = 0; j < this.ToResetEquippedNextFrame.size(); ++j) {
            this.ResetCharacterEquippedHands(this.ToResetEquippedNextFrame.get(j));
        }
        this.ToResetEquippedNextFrame.clear();
        for (int k = 0; k < this.ToRemove.size(); ++k) {
            this.DoRemove(this.ToRemove.get(k));
        }
        this.ToRemove.clear();
        for (int l = 0; l < this.m_resetAfterRender.size(); ++l) {
            final ModelSlot modelSlot = this.m_resetAfterRender.get(l);
            if (!modelSlot.isRendering()) {
                modelSlot.reset();
                this.m_resetAfterRender.remove(l--);
            }
        }
        this.m_lights.clear();
        if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
            this.m_lights.addAll((Collection<?>)IsoWorld.instance.CurrentCell.getLamppostPositions());
            final ArrayList<BaseVehicle> vehicles = IsoWorld.instance.CurrentCell.getVehicles();
            for (int index = 0; index < vehicles.size(); ++index) {
                final BaseVehicle baseVehicle = vehicles.get(index);
                if (baseVehicle.sprite != null && baseVehicle.sprite.hasActiveModel()) {
                    ((VehicleModelInstance)baseVehicle.sprite.modelSlot.model).UpdateLights();
                }
            }
        }
        this.m_freeLights.addAll((Collection<?>)this.m_torchLights);
        this.m_torchLights.clear();
        this.m_torches.clear();
        LightingJNI.getTorches(this.m_torches);
        for (int index2 = 0; index2 < this.m_torches.size(); ++index2) {
            final IsoGameCharacter.TorchInfo torchInfo = this.m_torches.get(index2);
            final IsoLightSource e = this.m_freeLights.isEmpty() ? new IsoLightSource(0, 0, 0, 1.0f, 1.0f, 1.0f, 1) : this.m_freeLights.pop();
            e.x = (int)torchInfo.x;
            e.y = (int)torchInfo.y;
            e.z = (int)torchInfo.z;
            e.r = 1.0f;
            e.g = 0.85f;
            e.b = 0.6f;
            e.radius = (int)Math.ceil(torchInfo.dist);
            this.m_torchLights.add(e);
        }
    }
    
    private ModelSlot addNewSlot(final IsoGameCharacter isoGameCharacter) {
        final ModelSlot e = new ModelSlot(this.m_modelSlots.size(), null, isoGameCharacter);
        this.m_modelSlots.add(e);
        return e;
    }
    
    public ModelSlot getSlot(final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < this.m_modelSlots.size(); ++i) {
            final ModelSlot modelSlot = this.m_modelSlots.get(i);
            if (!modelSlot.bRemove) {
                if (!modelSlot.isRendering()) {
                    if (!modelSlot.active) {
                        return modelSlot;
                    }
                }
            }
        }
        return this.addNewSlot(isoGameCharacter);
    }
    
    private boolean DoRemove(final IsoGameCharacter isoGameCharacter) {
        if (!this.m_contains.contains(isoGameCharacter)) {
            return false;
        }
        boolean b = false;
        for (int i = 0; i < this.m_modelSlots.size(); ++i) {
            final ModelSlot modelSlot = this.m_modelSlots.get(i);
            if (modelSlot.character == isoGameCharacter) {
                isoGameCharacter.legsSprite.modelSlot = null;
                this.m_contains.remove(isoGameCharacter);
                if (!isoGameCharacter.isSceneCulled()) {
                    isoGameCharacter.onCullStateChanged(this, true);
                }
                if (!this.m_resetAfterRender.contains(modelSlot)) {
                    this.m_resetAfterRender.add(modelSlot);
                }
                b = true;
            }
        }
        return b;
    }
    
    public void Remove(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.isSceneCulled()) {
            return;
        }
        if (!this.ToRemove.contains(isoGameCharacter)) {
            isoGameCharacter.legsSprite.modelSlot.bRemove = true;
            this.ToRemove.add(isoGameCharacter);
            isoGameCharacter.onCullStateChanged(this, true);
        }
        else if (this.ContainsChar(isoGameCharacter)) {
            throw new IllegalStateException("IsoGameCharacter.isSceneCulled() = true inconsistent with ModelManager.ContainsChar() = true");
        }
    }
    
    public void Remove(final BaseVehicle baseVehicle) {
        if (baseVehicle.sprite != null && baseVehicle.sprite.modelSlot != null) {
            final ModelSlot modelSlot = baseVehicle.sprite.modelSlot;
            if (!this.m_resetAfterRender.contains(modelSlot)) {
                this.m_resetAfterRender.add(modelSlot);
            }
            baseVehicle.sprite.modelSlot = null;
        }
    }
    
    public void ResetNextFrame(final IsoGameCharacter isoGameCharacter) {
        if (this.ToResetNextFrame.contains(isoGameCharacter)) {
            return;
        }
        this.ToResetNextFrame.add(isoGameCharacter);
    }
    
    public void ResetEquippedNextFrame(final IsoGameCharacter isoGameCharacter) {
        if (this.ToResetEquippedNextFrame.contains(isoGameCharacter)) {
            return;
        }
        this.ToResetEquippedNextFrame.add(isoGameCharacter);
    }
    
    public void Reset() {
        final Iterator<IsoGameCharacter> iterator;
        IsoGameCharacter[] array;
        int length;
        int i = 0;
        RenderThread.invokeOnRenderContext(() -> {
            this.ToRemove.iterator();
            while (iterator.hasNext()) {
                this.DoRemove(iterator.next());
            }
            this.ToRemove.clear();
            try {
                if (!this.m_contains.isEmpty()) {
                    array = this.m_contains.toArray(new IsoGameCharacter[0]);
                    for (length = array.length; i < length; ++i) {
                        this.DoRemove(array[i]);
                    }
                }
                this.m_modelSlots.clear();
            }
            catch (Exception ex) {
                DebugLog.Animation.error((Object)"Exception thrown removing Models.");
                ex.printStackTrace();
            }
            return;
        });
        this.m_lights.clear();
        this.m_lightsTemp.clear();
    }
    
    public void getClosestThreeLights(final IsoMovingObject isoMovingObject, final IsoLightSource[] array) {
        this.m_lightsTemp.clear();
        for (final IsoLightSource e : this.m_lights) {
            if (!e.bActive) {
                continue;
            }
            if (e.life == 0) {
                continue;
            }
            if (e.localToBuilding != null && isoMovingObject.getCurrentBuilding() != e.localToBuilding) {
                continue;
            }
            if (IsoUtils.DistanceTo(isoMovingObject.x, isoMovingObject.y, e.x + 0.5f, e.y + 0.5f) >= e.radius) {
                continue;
            }
            if (LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)isoMovingObject.x, (int)isoMovingObject.y, (int)isoMovingObject.z, e.x, e.y, e.z, false) == LosUtil.TestResults.Blocked) {
                continue;
            }
            this.m_lightsTemp.add(e);
        }
        if (isoMovingObject instanceof BaseVehicle) {
            for (int i = 0; i < this.m_torches.size(); ++i) {
                final IsoGameCharacter.TorchInfo torchInfo = this.m_torches.get(i);
                if (IsoUtils.DistanceTo(isoMovingObject.x, isoMovingObject.y, torchInfo.x, torchInfo.y) < torchInfo.dist) {
                    if (LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)isoMovingObject.x, (int)isoMovingObject.y, (int)isoMovingObject.z, (int)torchInfo.x, (int)torchInfo.y, (int)torchInfo.z, false) != LosUtil.TestResults.Blocked) {
                        if (torchInfo.bCone) {
                            final Vector2 tempVec2 = this.m_tempVec2;
                            tempVec2.x = torchInfo.x - isoMovingObject.x;
                            tempVec2.y = torchInfo.y - isoMovingObject.y;
                            tempVec2.normalize();
                            final Vector2 tempVec2_2 = this.m_tempVec2_2;
                            tempVec2_2.x = torchInfo.angleX;
                            tempVec2_2.y = torchInfo.angleY;
                            tempVec2_2.normalize();
                            if (tempVec2.dot(tempVec2_2) >= -0.92f) {
                                continue;
                            }
                        }
                        this.m_lightsTemp.add(this.m_torchLights.get(i));
                    }
                }
            }
        }
        final float n;
        final float n2;
        PZArrayUtil.sort(this.m_lightsTemp, (Comparator<IsoLightSource>)Lambda.comparator(isoMovingObject, (isoLightSource, isoLightSource2, isoMovingObject2) -> {
            isoMovingObject2.DistTo(isoLightSource.x, isoLightSource.y);
            isoMovingObject2.DistTo(isoLightSource2.x, isoLightSource2.y);
            if (n > n2) {
                return 1;
            }
            else if (n < n2) {
                return -1;
            }
            else {
                return 0;
            }
        }));
        final int n3 = 0;
        final int n4 = 1;
        final int n5 = 2;
        final IsoLightSource isoLightSource3 = null;
        array[n5] = isoLightSource3;
        array[n3] = (array[n4] = isoLightSource3);
        if (this.m_lightsTemp.size() > 0) {
            array[0] = this.m_lightsTemp.get(0);
        }
        if (this.m_lightsTemp.size() > 1) {
            array[1] = this.m_lightsTemp.get(1);
        }
        if (this.m_lightsTemp.size() > 2) {
            array[2] = this.m_lightsTemp.get(2);
        }
    }
    
    public void addVehicle(final BaseVehicle baseVehicle) {
        if (!this.m_bCreated) {
            return;
        }
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return;
        }
        if (baseVehicle == null || baseVehicle.getScript() == null) {
            return;
        }
        final VehicleScript script = baseVehicle.getScript();
        final String file = baseVehicle.getScript().getModel().file;
        final Model loadedModel = this.getLoadedModel(file);
        if (loadedModel == null) {
            DebugLog.Animation.error("Failed to find vehicle model: %s", file);
            return;
        }
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.debugln("%s", file);
        }
        final VehicleModelInstance vehicleModelInstance = new VehicleModelInstance();
        vehicleModelInstance.init(loadedModel, null, baseVehicle.getAnimationPlayer());
        vehicleModelInstance.applyModelScriptScale(file);
        baseVehicle.getSkin();
        VehicleScript.Skin skin = script.getTextures();
        if (baseVehicle.getSkinIndex() >= 0 && baseVehicle.getSkinIndex() < script.getSkinCount()) {
            skin = script.getSkin(baseVehicle.getSkinIndex());
        }
        vehicleModelInstance.LoadTexture(skin.texture);
        vehicleModelInstance.tex = skin.textureData;
        vehicleModelInstance.textureMask = skin.textureDataMask;
        vehicleModelInstance.textureDamage1Overlay = skin.textureDataDamage1Overlay;
        vehicleModelInstance.textureDamage1Shell = skin.textureDataDamage1Shell;
        vehicleModelInstance.textureDamage2Overlay = skin.textureDataDamage2Overlay;
        vehicleModelInstance.textureDamage2Shell = skin.textureDataDamage2Shell;
        vehicleModelInstance.textureLights = skin.textureDataLights;
        vehicleModelInstance.textureRust = skin.textureDataRust;
        if (vehicleModelInstance.tex != null) {
            vehicleModelInstance.tex.bindAlways = true;
        }
        else {
            DebugLog.Animation.error("texture not found:", baseVehicle.getSkin());
        }
        final ModelSlot slot = this.getSlot(null);
        (slot.model = vehicleModelInstance).setOwner(slot);
        vehicleModelInstance.object = baseVehicle;
        slot.sub.clear();
        for (int i = 0; i < baseVehicle.models.size(); ++i) {
            final BaseVehicle.ModelInfo modelInfo = baseVehicle.models.get(i);
            final Model loadedModel2 = this.getLoadedModel(modelInfo.scriptModel.file);
            if (loadedModel2 == null) {
                DebugLog.Animation.error("vehicle.models[%d] not found: %s", i, modelInfo.scriptModel.file);
            }
            else {
                final VehicleSubModelInstance modelInstance = new VehicleSubModelInstance();
                modelInstance.init(loadedModel2, null, modelInfo.getAnimationPlayer());
                modelInstance.setOwner(slot);
                modelInstance.applyModelScriptScale(modelInfo.scriptModel.file);
                modelInstance.object = baseVehicle;
                modelInstance.parent = vehicleModelInstance;
                vehicleModelInstance.sub.add(modelInstance);
                modelInstance.modelInfo = modelInfo;
                if (modelInstance.tex == null) {
                    modelInstance.tex = vehicleModelInstance.tex;
                }
                slot.sub.add(modelInstance);
                modelInfo.modelInstance = modelInstance;
            }
        }
        slot.active = true;
        baseVehicle.sprite.modelSlot = slot;
    }
    
    public ModelInstance addStatic(final ModelSlot owner, final String s, final String s2, final String s3, final String s4) {
        final ModelInstance staticInstance = this.newStaticInstance(owner, s, s2, s3, s4);
        if (staticInstance == null) {
            return null;
        }
        owner.sub.add(staticInstance);
        staticInstance.setOwner(owner);
        owner.model.sub.add(staticInstance);
        return staticInstance;
    }
    
    public ModelInstance newStaticInstance(final ModelSlot modelSlot, final String s, final String s2, final String parentBoneName, final String s3) {
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        Model model = this.tryGetLoadedModel(s, s2, true, s3, false);
        if (model == null && s != null) {
            this.loadStaticModel(s, s2, s3);
            model = this.getLoadedModel(s, s2, true, s3);
            if (model == null) {
                if (DebugLog.isEnabled(DebugType.Animation)) {
                    DebugLog.Animation.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
                }
                return null;
            }
        }
        if (s == null) {
            model = this.tryGetLoadedModel("vehicles_wheel02", "vehicles/vehicle_wheel02", true, "vehiclewheel", false);
        }
        final ModelInstance instance = this.newInstance(model, modelSlot.character, modelSlot.model.AnimPlayer);
        instance.parent = modelSlot.model;
        if (modelSlot.model.AnimPlayer != null) {
            instance.parentBone = modelSlot.model.AnimPlayer.getSkinningBoneIndex(parentBoneName, instance.parentBone);
            instance.parentBoneName = parentBoneName;
        }
        instance.AnimPlayer = modelSlot.model.AnimPlayer;
        return instance;
    }
    
    private ModelInstance addStatic(final ModelSlot modelSlot, final String s, final String s2) {
        return this.addStaticForcedTex(modelSlot, s, s2, null);
    }
    
    private ModelInstance addStaticForcedTex(final ModelSlot modelSlot, final String key, final String s, final String s2) {
        String meshName = key;
        String textureName = key;
        String shaderName = null;
        final ModelMetaData modelMetaData = ModelManager.modelMetaData.get(key);
        if (modelMetaData != null) {
            if (!StringUtils.isNullOrWhitespace(modelMetaData.meshName)) {
                meshName = modelMetaData.meshName;
            }
            if (!StringUtils.isNullOrWhitespace(modelMetaData.textureName)) {
                textureName = modelMetaData.textureName;
            }
            if (!StringUtils.isNullOrWhitespace(modelMetaData.shaderName)) {
                shaderName = modelMetaData.shaderName;
            }
        }
        if (!StringUtils.isNullOrEmpty(s2)) {
            textureName = s2;
        }
        final ModelScript modelScript = ScriptManager.instance.getModelScript(key);
        if (modelScript != null) {
            final ModelInstance addStatic = this.addStatic(modelSlot, modelScript.getMeshName(), modelScript.getTextureName(), s, modelScript.getShaderName());
            if (addStatic != null) {
                addStatic.applyModelScriptScale(key);
            }
            return addStatic;
        }
        return this.addStatic(modelSlot, meshName, textureName, s, shaderName);
    }
    
    public ModelInstance addStatic(final ModelInstance modelInstance, final String s, final String s2, final String s3) {
        return this.addStaticForcedTex(modelInstance, s, s2, s3, null);
    }
    
    public ModelInstance addStaticForcedTex(final ModelInstance parent, final String s, final String attachmentNameSelf, final String attachmentNameParent, final String s2) {
        String meshName = s;
        String textureName = s;
        String shaderName = null;
        final ModelScript modelScript = ScriptManager.instance.getModelScript(s);
        if (modelScript != null) {
            meshName = modelScript.getMeshName();
            textureName = modelScript.getTextureName();
            shaderName = modelScript.getShaderName();
        }
        if (!StringUtils.isNullOrEmpty(s2)) {
            textureName = s2;
        }
        Model model = this.tryGetLoadedModel(meshName, textureName, true, shaderName, false);
        if (model == null && meshName != null) {
            this.loadStaticModel(meshName, textureName, shaderName);
            model = this.getLoadedModel(meshName, textureName, true, shaderName);
            if (model == null) {
                if (DebugLog.isEnabled(DebugType.Animation)) {
                    DebugLog.Animation.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, meshName, textureName));
                }
                return null;
            }
        }
        if (meshName == null) {
            model = this.tryGetLoadedModel("vehicles_wheel02", "vehicles/vehicle_wheel02", true, "vehiclewheel", false);
        }
        if (model == null) {
            return null;
        }
        final ModelInstance e = this.m_modelInstancePool.alloc();
        if (parent != null) {
            e.init(model, parent.character, parent.AnimPlayer);
            e.parent = parent;
            parent.sub.add(e);
        }
        else {
            e.init(model, null, null);
        }
        if (modelScript != null) {
            e.applyModelScriptScale(s);
        }
        e.attachmentNameSelf = attachmentNameSelf;
        e.attachmentNameParent = attachmentNameParent;
        return e;
    }
    
    private String modifyShaderName(String s) {
        if ((StringUtils.equals(s, "vehicle") || StringUtils.equals(s, "vehicle_multiuv") || StringUtils.equals(s, "vehicle_norandom_multiuv")) && !Core.getInstance().getPerfReflectionsOnLoad()) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        return s;
    }
    
    private Model loadModelInternal(final String meshName, final String textureName, String modifyShaderName, final ModelMesh animationsModel, final boolean bStatic) {
        modifyShaderName = this.modifyShaderName(modifyShaderName);
        final Model.ModelAssetParams modelAssetParams = new Model.ModelAssetParams();
        modelAssetParams.animationsModel = animationsModel;
        modelAssetParams.bStatic = bStatic;
        modelAssetParams.meshName = meshName;
        modelAssetParams.shaderName = modifyShaderName;
        modelAssetParams.textureName = textureName;
        modelAssetParams.textureFlags = this.getTextureFlags();
        final Model model = (Model)ModelAssetManager.instance.load(new AssetPath(this.createModelKey(meshName, textureName, bStatic, modifyShaderName)), modelAssetParams);
        if (model != null) {
            this.putLoadedModel(meshName, textureName, bStatic, modifyShaderName, model);
        }
        return model;
    }
    
    public int getTextureFlags() {
        final int n = TextureID.bUseCompression ? 4 : 0;
        if (Core.OptionModelTextureMipmaps) {}
        return n;
    }
    
    public void setModelMetaData(final String s, final String s2, final String s3, final boolean b) {
        this.setModelMetaData(s, s, s2, s3, b);
    }
    
    public void setModelMetaData(final String key, final String meshName, final String textureName, final String shaderName, final boolean bStatic) {
        final ModelMetaData value = new ModelMetaData();
        value.meshName = meshName;
        value.textureName = textureName;
        value.shaderName = shaderName;
        value.bStatic = bStatic;
        ModelManager.modelMetaData.put(key, value);
    }
    
    public Model loadStaticModel(final String s, final String s2, final String s3) {
        return this.loadModelInternal(s, s2, this.modifyShaderName(s3), null, true);
    }
    
    private Model loadModel(final String s, final String s2, final ModelMesh modelMesh) {
        return this.loadModelInternal(s, s2, "basicEffect", modelMesh, false);
    }
    
    public Model getLoadedModel(final String key) {
        final ModelScript modelScript = ScriptManager.instance.getModelScript(key);
        if (modelScript != null) {
            if (modelScript.loadedModel != null) {
                return modelScript.loadedModel;
            }
            modelScript.shaderName = this.modifyShaderName(modelScript.shaderName);
            final ModelScript modelScript2 = modelScript;
            final Model tryGetLoadedModel = this.tryGetLoadedModel(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.bStatic, modelScript.getShaderName(), false);
            modelScript2.loadedModel = tryGetLoadedModel;
            final Model model = tryGetLoadedModel;
            if (model != null) {
                return model;
            }
            return modelScript.bStatic ? this.loadModelInternal(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.getShaderName(), null, true) : this.loadModelInternal(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.getShaderName(), null, false);
        }
        else {
            final ModelMetaData modelMetaData = ModelManager.modelMetaData.get(key);
            if (modelMetaData != null) {
                modelMetaData.shaderName = this.modifyShaderName(modelMetaData.shaderName);
                final Model tryGetLoadedModel2 = this.tryGetLoadedModel(modelMetaData.meshName, modelMetaData.textureName, modelMetaData.bStatic, modelMetaData.shaderName, false);
                if (tryGetLoadedModel2 != null) {
                    return tryGetLoadedModel2;
                }
                return modelMetaData.bStatic ? this.loadStaticModel(modelMetaData.meshName, modelMetaData.textureName, modelMetaData.shaderName) : this.loadModel(modelMetaData.meshName, modelMetaData.textureName, this.m_animModel);
            }
            else {
                Model tryGetLoadedModel3 = this.tryGetLoadedModel(key, null, false, null, false);
                if (tryGetLoadedModel3 != null) {
                    return tryGetLoadedModel3;
                }
                final String trim = key.toLowerCase().trim();
                for (final Map.Entry<String, Model> entry : this.m_modelMap.entrySet()) {
                    final String s = entry.getKey();
                    if (!s.startsWith(trim)) {
                        continue;
                    }
                    final Model model2 = entry.getValue();
                    if (model2 == null) {
                        continue;
                    }
                    if (s.length() == trim.length() || s.charAt(trim.length()) == '&') {
                        tryGetLoadedModel3 = model2;
                        break;
                    }
                }
                if (tryGetLoadedModel3 == null && DebugLog.isEnabled(DebugType.Animation)) {
                    DebugLog.Animation.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
                }
                return tryGetLoadedModel3;
            }
        }
    }
    
    public Model getLoadedModel(final String s, final String s2, final boolean b, final String s3) {
        return this.tryGetLoadedModel(s, s2, b, s3, true);
    }
    
    public Model tryGetLoadedModel(final String s, final String s2, final boolean b, final String s3, final boolean b2) {
        final String modelKey = this.createModelKey(s, s2, b, s3);
        if (modelKey == null) {
            return null;
        }
        final Model model = this.m_modelMap.get(modelKey);
        if (model == null && b2 && DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, modelKey));
        }
        return model;
    }
    
    public void putLoadedModel(final String s, final String s2, final boolean b, final String s3, final Model value) {
        final String modelKey = this.createModelKey(s, s2, b, s3);
        if (modelKey == null) {
            return;
        }
        final Model model = this.m_modelMap.get(modelKey);
        if (model == value) {
            return;
        }
        if (model != null) {
            DebugLog.Animation.debugln("Override key=\"%s\" old=%s new=%s", modelKey, model, value);
        }
        else {
            DebugLog.Animation.debugln("key=\"%s\" model=%s", modelKey, value);
        }
        this.m_modelMap.put(modelKey, value);
        value.Name = modelKey;
    }
    
    private String createModelKey(final String key, final String key2, final boolean b, String basicEffect) {
        ModelManager.builder.delete(0, ModelManager.builder.length());
        if (key == null) {
            return null;
        }
        if (!ModelManager.toLowerKeyRoot.containsKey(key)) {
            ModelManager.toLowerKeyRoot.put(key, key.toLowerCase(Locale.ENGLISH).trim());
        }
        ModelManager.builder.append(ModelManager.toLowerKeyRoot.get(key));
        ModelManager.builder.append(ModelManager.amp);
        if (StringUtils.isNullOrWhitespace(basicEffect)) {
            basicEffect = ModelManager.basicEffect;
        }
        ModelManager.builder.append(ModelManager.shaderEquals);
        if (!ModelManager.toLower.containsKey(basicEffect)) {
            ModelManager.toLower.put(basicEffect, basicEffect.toLowerCase().trim());
        }
        ModelManager.builder.append(ModelManager.toLower.get(basicEffect));
        if (!StringUtils.isNullOrWhitespace(key2)) {
            ModelManager.builder.append(ModelManager.texA);
            if (!ModelManager.toLowerTex.containsKey(key2)) {
                ModelManager.toLowerTex.put(key2, key2.toLowerCase().trim());
            }
            ModelManager.builder.append(ModelManager.toLowerTex.get(key2));
        }
        if (b) {
            ModelManager.builder.append(ModelManager.isStaticTrue);
        }
        return ModelManager.builder.toString();
    }
    
    private String createModelKey2(final String s, final String s2, final boolean b, String s3) {
        if (s == null) {
            return null;
        }
        if (StringUtils.isNullOrWhitespace(s3)) {
            s3 = "basicEffect";
        }
        String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3.toLowerCase().trim());
        if (!StringUtils.isNullOrWhitespace(s2)) {
            s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s2.toLowerCase().trim());
        }
        if (b) {
            s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s.toLowerCase(Locale.ENGLISH).trim(), s4);
    }
    
    private AnimationAsset loadAnim(final String s, final ModelMesh animationsMesh, final ModAnimations modAnimations) {
        DebugLog.Animation.debugln("Adding asset to queue: %s", s);
        final AnimationAsset.AnimationAssetParams animationAssetParams = new AnimationAsset.AnimationAssetParams();
        animationAssetParams.animationsMesh = animationsMesh;
        final AnimationAsset animationAsset = (AnimationAsset)AnimationAssetManager.instance.load(new AssetPath(s), animationAssetParams);
        animationAsset.skinningData = animationsMesh.skinningData;
        this.putAnimationAsset(s, animationAsset, modAnimations);
        return animationAsset;
    }
    
    private void putAnimationAsset(final String s, final AnimationAsset animationAsset, final ModAnimations modAnimations) {
        final String lowerCase = s.toLowerCase();
        final AnimationAsset o = modAnimations.m_animationAssetMap.getOrDefault(lowerCase, null);
        if (o != null) {
            DebugLog.Animation.debugln("Overwriting asset: %s", this.animAssetToString(o));
            DebugLog.Animation.debugln("New asset        : %s", this.animAssetToString(animationAsset));
            modAnimations.m_animationAssetList.remove(o);
        }
        animationAsset.modelManagerKey = lowerCase;
        animationAsset.modAnimations = modAnimations;
        modAnimations.m_animationAssetMap.put(lowerCase, animationAsset);
        modAnimations.m_animationAssetList.add(animationAsset);
    }
    
    private String animAssetToString(final AnimationAsset animationAsset) {
        if (animationAsset == null) {
            return "null";
        }
        final AssetPath path = animationAsset.getPath();
        if (path == null) {
            return "null-path";
        }
        return String.valueOf(path.getPath());
    }
    
    private AnimationAsset getAnimationAsset(final String s) {
        return this.m_animationAssets.get(s.toLowerCase(Locale.ENGLISH));
    }
    
    private AnimationAsset getAnimationAssetRequired(final String s) {
        final AnimationAsset animationAsset = this.getAnimationAsset(s);
        if (animationAsset == null) {
            throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        return animationAsset;
    }
    
    public void addAnimationClip(final String key, final AnimationClip value) {
        this.m_animModel.skinningData.AnimationClips.put(key, value);
    }
    
    public AnimationClip getAnimationClip(final String key) {
        return this.m_animModel.skinningData.AnimationClips.get(key);
    }
    
    public Collection<AnimationClip> getAllAnimationClips() {
        return this.m_animModel.skinningData.AnimationClips.values();
    }
    
    public ModelInstance newInstance(final Model model, final IsoGameCharacter isoGameCharacter, final AnimationPlayer animationPlayer) {
        if (model == null) {
            System.err.println("ModelManager.newInstance> Model is null.");
            return null;
        }
        final ModelInstance modelInstance = this.m_modelInstancePool.alloc();
        modelInstance.init(model, isoGameCharacter, animationPlayer);
        return modelInstance;
    }
    
    public boolean isLoadingAnimations() {
        final Iterator<AnimationAsset> iterator = this.m_animationAssets.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public void reloadModelsMatching(String lowerCase) {
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        for (final String key : this.m_modelMap.keySet()) {
            if (key.contains(lowerCase)) {
                final Model model = this.m_modelMap.get(key);
                if (model.isEmpty()) {
                    continue;
                }
                DebugLog.General.printf("reloading model %s\n", key);
                final ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
                meshAssetParams.animationsMesh = null;
                if (model.Mesh.vb == null) {
                    meshAssetParams.bStatic = key.contains(";isStatic=true");
                }
                else {
                    meshAssetParams.bStatic = model.Mesh.vb.bStatic;
                }
                MeshAssetManager.instance.reload(model.Mesh, meshAssetParams);
            }
        }
    }
    
    public void loadModAnimations() {
        for (final ModAnimations modAnimations : this.m_modAnimations.values()) {
            modAnimations.setPriority((modAnimations == this.m_gameAnimations) ? 0 : -1);
        }
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final String s = modIDs.get(i);
            final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(s);
            if (availableModDetails != null) {
                if (availableModDetails.animsXFile.isDirectory()) {
                    final ModAnimations modAnimations2 = this.m_modAnimations.get(s);
                    if (modAnimations2 != null) {
                        modAnimations2.setPriority(i + 1);
                    }
                    else {
                        final ModAnimations value = new ModAnimations(s);
                        value.setPriority(i + 1);
                        this.m_modAnimations.put(s, value);
                        this.loadAnimsFromDir(availableModDetails.baseFile.toURI(), availableModDetails.mediaFile.toURI(), availableModDetails.animsXFile, this.m_animModel, value);
                    }
                }
            }
        }
        this.setActiveAnimations();
    }
    
    void setActiveAnimations() {
        this.m_animationAssets.clear();
        this.m_animModel.skinningData.AnimationClips.clear();
        for (final ModAnimations modAnimations : this.m_modAnimations.values()) {
            if (!modAnimations.isActive()) {
                continue;
            }
            for (final AnimationAsset value : modAnimations.m_animationAssetList) {
                final AnimationAsset animationAsset = this.m_animationAssets.get(value.modelManagerKey);
                if (animationAsset != null && animationAsset != value && animationAsset.modAnimations.m_priority > modAnimations.m_priority) {
                    continue;
                }
                this.m_animationAssets.put(value.modelManagerKey, value);
                if (!value.isReady()) {
                    continue;
                }
                value.skinningData.AnimationClips.putAll(value.AnimationClips);
            }
        }
    }
    
    public void animationAssetLoaded(final AnimationAsset value) {
        if (!value.modAnimations.isActive()) {
            return;
        }
        final AnimationAsset animationAsset = this.m_animationAssets.get(value.modelManagerKey);
        if (animationAsset != null && animationAsset != value && animationAsset.modAnimations.m_priority > value.modAnimations.m_priority) {
            return;
        }
        this.m_animationAssets.put(value.modelManagerKey, value);
        value.skinningData.AnimationClips.putAll(value.AnimationClips);
    }
    
    static {
        ModelManager.NoOpenGL = false;
        instance = new ModelManager();
        modelMetaData = new TreeMap<String, ModelMetaData>(String.CASE_INSENSITIVE_ORDER);
        ModelManager.basicEffect = "basicEffect";
        ModelManager.isStaticTrue = ";isStatic=true";
        ModelManager.shaderEquals = "shader=";
        ModelManager.texA = ";tex=";
        ModelManager.amp = "&";
        ModelManager.toLower = new HashMap<String, String>();
        ModelManager.toLowerTex = new HashMap<String, String>();
        ModelManager.toLowerKeyRoot = new HashMap<String, String>();
        ModelManager.builder = new StringBuilder();
    }
    
    private static final class ModelMetaData
    {
        String meshName;
        String textureName;
        String shaderName;
        boolean bStatic;
    }
    
    class AnimDirReloader implements PredicatedFileWatcher.IPredicatedFileWatcherCallback
    {
        URI m_baseURI;
        URI m_mediaURI;
        String m_dir;
        String m_dirSecondary;
        String m_dirAbsolute;
        String m_dirSecondaryAbsolute;
        ModelMesh m_animationsModel;
        ModAnimations m_modAnimations;
        
        public AnimDirReloader(final URI baseURI, final URI mediaURI, String relativeFile, final ModelMesh animationsModel, final ModAnimations modAnimations) {
            relativeFile = ZomboidFileSystem.instance.getRelativeFile(baseURI, relativeFile);
            this.m_baseURI = baseURI;
            this.m_mediaURI = mediaURI;
            this.m_dir = ZomboidFileSystem.instance.normalizeFolderPath(relativeFile);
            this.m_dirAbsolute = ZomboidFileSystem.instance.normalizeFolderPath(new File(new File(this.m_baseURI), this.m_dir).toString());
            if (this.m_dir.contains("/anims/")) {
                this.m_dirSecondary = this.m_dir.replace("/anims/", "/anims_X/");
                this.m_dirSecondaryAbsolute = ZomboidFileSystem.instance.normalizeFolderPath(new File(new File(this.m_baseURI), this.m_dirSecondary).toString());
            }
            this.m_animationsModel = animationsModel;
            this.m_modAnimations = modAnimations;
        }
        
        private boolean IsInDir(String normalizeFolderPath) {
            normalizeFolderPath = ZomboidFileSystem.instance.normalizeFolderPath(normalizeFolderPath);
            try {
                if (this.m_dirSecondary != null) {
                    return normalizeFolderPath.startsWith(this.m_dirAbsolute) || normalizeFolderPath.startsWith(this.m_dirSecondaryAbsolute);
                }
                return normalizeFolderPath.startsWith(this.m_dirAbsolute);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        
        @Override
        public void call(final String pathname) {
            final String lowerCase = pathname.toLowerCase();
            if (!lowerCase.endsWith(".fbx") && !lowerCase.endsWith(".x") && !lowerCase.endsWith(".txt")) {
                return;
            }
            final String animName = ZomboidFileSystem.instance.getAnimName(this.m_mediaURI, new File(pathname));
            final AnimationAsset animationAsset = ModelManager.this.getAnimationAsset(animName);
            if (animationAsset != null) {
                if (!animationAsset.isEmpty()) {
                    DebugLog.General.debugln("Reloading animation: %s", ModelManager.this.animAssetToString(animationAsset));
                    assert animationAsset.getRefCount() == 1;
                    final AnimationAsset.AnimationAssetParams animationAssetParams = new AnimationAsset.AnimationAssetParams();
                    animationAssetParams.animationsMesh = this.m_animationsModel;
                    AnimationAssetManager.instance.reload(animationAsset, animationAssetParams);
                }
                return;
            }
            ModelManager.this.loadAnim(animName, this.m_animationsModel, this.m_modAnimations);
        }
        
        public PredicatedFileWatcher GetFileWatcher() {
            return new PredicatedFileWatcher(this.m_dir, this::IsInDir, this);
        }
    }
    
    public static class ModelSlot
    {
        public int ID;
        public ModelInstance model;
        public IsoGameCharacter character;
        public final ArrayList<ModelInstance> sub;
        protected final AttachedModels attachedModels;
        public boolean active;
        public boolean bRemove;
        public int renderRefCount;
        public int framesSinceStart;
        
        public ModelSlot(final int id, final ModelInstance model, final IsoGameCharacter character) {
            this.sub = new ArrayList<ModelInstance>();
            this.attachedModels = new AttachedModels();
            this.renderRefCount = 0;
            this.ID = id;
            this.model = model;
            this.character = character;
        }
        
        public void Update() {
            if (this.character == null || this.bRemove) {
                return;
            }
            ++this.framesSinceStart;
            if (this != this.character.legsSprite.modelSlot) {}
            if (this.model.AnimPlayer != this.character.getAnimationPlayer()) {
                this.model.AnimPlayer = this.character.getAnimationPlayer();
            }
            synchronized (this.model.m_lock) {
                this.model.UpdateDir();
                this.model.Update();
                for (int i = 0; i < this.sub.size(); ++i) {
                    this.sub.get(i).AnimPlayer = this.model.AnimPlayer;
                }
            }
        }
        
        public boolean isRendering() {
            return this.renderRefCount > 0;
        }
        
        public void reset() {
            ModelManager.instance.resetModelInstanceRecurse(this.model, this);
            if (this.character != null) {
                this.character.primaryHandModel = null;
                this.character.secondaryHandModel = null;
                ModelManager.instance.derefModelInstances(this.character.getReadyModelData());
                this.character.getReadyModelData().clear();
            }
            this.active = false;
            this.character = null;
            this.bRemove = false;
            this.renderRefCount = 0;
            this.model = null;
            this.sub.clear();
            this.attachedModels.clear();
        }
    }
    
    public static final class ModAnimations
    {
        public final String m_modID;
        public final ArrayList<AnimationAsset> m_animationAssetList;
        public final HashMap<String, AnimationAsset> m_animationAssetMap;
        public int m_priority;
        
        public ModAnimations(final String modID) {
            this.m_animationAssetList = new ArrayList<AnimationAsset>();
            this.m_animationAssetMap = new HashMap<String, AnimationAsset>();
            this.m_modID = modID;
        }
        
        public void setPriority(final int priority) {
            assert priority >= -1;
            this.m_priority = priority;
        }
        
        public boolean isActive() {
            return this.m_priority != -1;
        }
    }
}
