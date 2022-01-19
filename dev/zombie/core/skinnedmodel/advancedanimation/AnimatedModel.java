// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.advancedanimation;

import org.joml.Math;
import zombie.scripting.objects.ModelAttachment;
import zombie.core.skinnedmodel.model.ModelInstanceRenderData;
import zombie.vehicles.BaseVehicle;
import org.joml.Matrix4fc;
import zombie.core.skinnedmodel.model.VehicleSubModelInstance;
import zombie.core.skinnedmodel.model.VehicleModelInstance;
import org.lwjglx.BufferUtils;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.characters.action.ActionState;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.util.Pool;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.Color;
import zombie.core.skinnedmodel.shader.Shader;
import java.nio.FloatBuffer;
import zombie.iso.IsoMovingObject;
import zombie.debug.DebugOptions;
import zombie.core.textures.Texture;
import org.lwjgl.opengl.GL11;
import zombie.core.skinnedmodel.ModelCamera;
import java.util.ArrayList;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.GameWindow;
import zombie.ui.UIManager;
import zombie.GameProfiler;
import zombie.core.SpriteRenderer;
import zombie.scripting.objects.ModelScript;
import zombie.characters.AttachedItems.AttachedModelName;
import zombie.core.skinnedmodel.model.ModelInstanceTextureInitializer;
import java.util.Locale;
import zombie.characters.WornItems.BodyLocations;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.ClothingItem;
import java.util.List;
import zombie.util.Lambda;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.Core;
import zombie.GameTime;
import zombie.util.list.PZArrayUtil;
import zombie.characters.action.ActionGroup;
import zombie.scripting.ScriptManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.visual.ItemVisual;
import java.util.Collection;
import zombie.characters.SurvivorDesc;
import zombie.util.StringUtils;
import java.util.UUID;
import zombie.popman.ObjectPool;
import zombie.core.skinnedmodel.model.ModelInstanceTextureCreator;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoGridSquare;
import zombie.characters.action.ActionContext;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import org.joml.Vector3f;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.characters.AttachedItems.AttachedModelNames;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.characters.action.IActionStateChanged;

public final class AnimatedModel extends AnimationVariableSource implements IAnimatable, IAnimEventCallback, IActionStateChanged, IHumanVisual
{
    private String animSetName;
    private String outfitName;
    private IsoGameCharacter character;
    private final HumanVisual humanVisual;
    private final ItemVisuals itemVisuals;
    private String primaryHandModelName;
    private String secondaryHandModelName;
    private final AttachedModelNames attachedModelNames;
    private ModelInstance modelInstance;
    private boolean bFemale;
    private boolean bZombie;
    private boolean bSkeleton;
    private String state;
    private final Vector2 angle;
    private final Vector3f offset;
    private boolean bIsometric;
    private boolean flipY;
    private float m_alpha;
    private AnimationPlayer animPlayer;
    private final ActionContext actionContext;
    private final AdvancedAnimator advancedAnimator;
    private float trackTime;
    private final String m_UID;
    private float lightsOriginX;
    private float lightsOriginY;
    private float lightsOriginZ;
    private final IsoGridSquare.ResultLight[] lights;
    private final ColorInfo ambient;
    private boolean bOutside;
    private boolean bRoom;
    private boolean bUpdateTextures;
    private boolean bClothingChanged;
    private boolean bAnimate;
    private ModelInstanceTextureCreator textureCreator;
    private final StateInfo[] stateInfos;
    private boolean bReady;
    private static final ObjectPool<AnimatedModelInstanceRenderData> instDataPool;
    private final UIModelCamera uiModelCamera;
    private static final WorldModelCamera worldModelCamera;
    
    public AnimatedModel() {
        this.animSetName = "player-avatar";
        this.humanVisual = new HumanVisual(this);
        this.itemVisuals = new ItemVisuals();
        this.attachedModelNames = new AttachedModelNames();
        this.bFemale = false;
        this.bZombie = false;
        this.bSkeleton = false;
        this.angle = new Vector2();
        this.offset = new Vector3f(0.0f, -0.45f, 0.0f);
        this.bIsometric = true;
        this.flipY = false;
        this.m_alpha = 1.0f;
        this.animPlayer = null;
        this.actionContext = new ActionContext(this);
        this.advancedAnimator = new AdvancedAnimator();
        this.trackTime = 0.0f;
        this.lights = new IsoGridSquare.ResultLight[5];
        this.ambient = new ColorInfo();
        this.bOutside = true;
        this.bRoom = false;
        this.bAnimate = true;
        this.stateInfos = new StateInfo[3];
        this.uiModelCamera = new UIModelCamera();
        this.m_UID = String.format("%s-%s", this.getClass().getSimpleName(), UUID.randomUUID().toString());
        this.advancedAnimator.init(this);
        this.advancedAnimator.animCallbackHandlers.add(this);
        this.actionContext.onStateChanged.add(this);
        for (int i = 0; i < this.lights.length; ++i) {
            this.lights[i] = new IsoGridSquare.ResultLight();
        }
        for (int j = 0; j < this.stateInfos.length; ++j) {
            this.stateInfos[j] = new StateInfo();
        }
    }
    
    @Override
    public HumanVisual getHumanVisual() {
        return this.humanVisual;
    }
    
    @Override
    public void getItemVisuals(final ItemVisuals itemVisuals) {
        itemVisuals.clear();
    }
    
    @Override
    public boolean isFemale() {
        return this.bFemale;
    }
    
    @Override
    public boolean isZombie() {
        return this.bZombie;
    }
    
    @Override
    public boolean isSkeleton() {
        return this.bSkeleton;
    }
    
    public void setAnimSetName(final String animSetName) {
        if (StringUtils.isNullOrWhitespace(animSetName)) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, animSetName));
        }
        this.animSetName = animSetName;
    }
    
    public void setOutfitName(final String outfitName, final boolean bFemale, final boolean bZombie) {
        this.outfitName = outfitName;
        this.bFemale = bFemale;
        this.bZombie = bZombie;
    }
    
    public void setCharacter(final IsoGameCharacter character) {
        this.outfitName = null;
        this.humanVisual.clear();
        this.itemVisuals.clear();
        if (!(character instanceof IHumanVisual)) {
            return;
        }
        character.getItemVisuals(this.itemVisuals);
        this.character = character;
        this.attachedModelNames.initFrom(character.getAttachedItems());
        this.setModelData(((IHumanVisual)character).getHumanVisual(), this.itemVisuals);
    }
    
    public void setSurvivorDesc(final SurvivorDesc survivorDesc) {
        this.outfitName = null;
        this.humanVisual.clear();
        this.itemVisuals.clear();
        survivorDesc.getWornItems().getItemVisuals(this.itemVisuals);
        this.attachedModelNames.clear();
        this.setModelData(survivorDesc.getHumanVisual(), this.itemVisuals);
    }
    
    public void setPrimaryHandModelName(final String primaryHandModelName) {
        this.primaryHandModelName = primaryHandModelName;
    }
    
    public void setSecondaryHandModelName(final String secondaryHandModelName) {
        this.secondaryHandModelName = secondaryHandModelName;
    }
    
    public void setAttachedModelNames(final AttachedModelNames attachedModelNames) {
        this.attachedModelNames.copyFrom(attachedModelNames);
    }
    
    public void setModelData(final HumanVisual humanVisual, final ItemVisuals c) {
        final AnimationPlayer animPlayer = this.animPlayer;
        final Model model = (this.animPlayer == null) ? null : animPlayer.getModel();
        if (this.humanVisual != humanVisual) {
            this.humanVisual.copyFrom(humanVisual);
        }
        if (this.itemVisuals != c) {
            this.itemVisuals.clear();
            this.itemVisuals.addAll(c);
        }
        this.bFemale = humanVisual.isFemale();
        this.bZombie = humanVisual.isZombie();
        this.bSkeleton = humanVisual.isSkeleton();
        if (this.modelInstance != null) {
            ModelManager.instance.resetModelInstanceRecurse(this.modelInstance, this);
        }
        Model model2;
        if (this.isSkeleton()) {
            model2 = (this.bFemale ? ModelManager.instance.m_skeletonFemaleModel : ModelManager.instance.m_skeletonMaleModel);
        }
        else {
            model2 = (this.bFemale ? ModelManager.instance.m_femaleModel : ModelManager.instance.m_maleModel);
        }
        this.modelInstance = ModelManager.instance.newInstance(model2, null, this.getAnimationPlayer());
        this.modelInstance.m_modelScript = ScriptManager.instance.getModelScript(this.bFemale ? "FemaleBody" : "MaleBody");
        this.modelInstance.setOwner(this);
        this.populateCharacterModelSlot();
        this.DoCharacterModelEquipped();
        boolean b = false;
        if (this.bAnimate) {
            if (AnimationSet.GetAnimationSet(this.GetAnimSetName(), false) != this.advancedAnimator.animSet || animPlayer != this.getAnimationPlayer() || model != model2) {
                b = true;
            }
        }
        else {
            b = true;
        }
        if (b) {
            this.advancedAnimator.OnAnimDataChanged(false);
        }
        if (this.bAnimate) {
            final ActionGroup actionGroup = ActionGroup.getActionGroup(this.GetAnimSetName());
            if (actionGroup != this.actionContext.getGroup()) {
                this.actionContext.setGroup(actionGroup);
            }
            this.advancedAnimator.SetState(this.actionContext.getCurrentStateName(), PZArrayUtil.listConvert(this.actionContext.getChildStates(), actionState -> actionState.name));
        }
        else if (!StringUtils.isNullOrWhitespace(this.state)) {
            this.advancedAnimator.SetState(this.state);
        }
        if (b) {
            final float fpsMultiplier = GameTime.getInstance().FPSMultiplier;
            GameTime.getInstance().FPSMultiplier = 100.0f;
            try {
                this.advancedAnimator.update();
            }
            finally {
                GameTime.getInstance().FPSMultiplier = fpsMultiplier;
            }
        }
        if (Core.bDebug && !this.bAnimate && this.stateInfoMain().readyData.isEmpty()) {
            this.getAnimationPlayer().resetBoneModelTransforms();
        }
        this.trackTime = 0.0f;
        this.stateInfoMain().bModelsReady = this.isReadyToRender();
    }
    
    public void setAmbient(final ColorInfo colorInfo, final boolean bOutside, final boolean bRoom) {
        this.ambient.set(colorInfo.r, colorInfo.g, colorInfo.b, 1.0f);
        this.bOutside = bOutside;
        this.bRoom = bRoom;
    }
    
    public void setLights(final IsoGridSquare.ResultLight[] array, final float lightsOriginX, final float lightsOriginY, final float lightsOriginZ) {
        this.lightsOriginX = lightsOriginX;
        this.lightsOriginY = lightsOriginY;
        this.lightsOriginZ = lightsOriginZ;
        for (int i = 0; i < array.length; ++i) {
            this.lights[i].copyFrom(array[i]);
        }
    }
    
    public void setState(final String state) {
        this.state = state;
    }
    
    public String getState() {
        return this.state;
    }
    
    public void setAngle(final Vector2 vector2) {
        this.angle.set(vector2);
    }
    
    public void setOffset(final float n, final float n2, final float n3) {
        this.offset.set(n, n2, n3);
    }
    
    public void setIsometric(final boolean bIsometric) {
        this.bIsometric = bIsometric;
    }
    
    public boolean isIsometric() {
        return this.bIsometric;
    }
    
    public void setFlipY(final boolean flipY) {
        this.flipY = flipY;
    }
    
    public void setAlpha(final float alpha) {
        this.m_alpha = alpha;
    }
    
    public void setTrackTime(final float trackTime) {
        this.trackTime = trackTime;
    }
    
    public void clothingItemChanged(final String s) {
        this.bClothingChanged = true;
    }
    
    public void setAnimate(final boolean bAnimate) {
        this.bAnimate = bAnimate;
    }
    
    private void initOutfit() {
        final String outfitName = this.outfitName;
        this.outfitName = null;
        if (StringUtils.isNullOrWhitespace(outfitName)) {
            return;
        }
        ModelManager.instance.create();
        this.humanVisual.dressInNamedOutfit(outfitName, this.itemVisuals);
        this.setModelData(this.humanVisual, this.itemVisuals);
    }
    
    private void populateCharacterModelSlot() {
        if (HumanVisual.GetMask(this.itemVisuals).isPartVisible(CharacterMask.Part.Head)) {
            this.addHeadHair(this.itemVisuals.findHat());
        }
        for (int i = this.itemVisuals.size() - 1; i >= 0; --i) {
            final ItemVisual itemVisual = this.itemVisuals.get(i);
            final ClothingItem clothingItem = itemVisual.getClothingItem();
            if (clothingItem != null) {
                if (clothingItem.isReady()) {
                    if (!this.isItemModelHidden(this.itemVisuals, itemVisual)) {
                        this.addClothingItem(itemVisual, clothingItem);
                    }
                }
            }
        }
        for (int j = this.humanVisual.getBodyVisuals().size() - 1; j >= 0; --j) {
            final ItemVisual itemVisual2 = this.humanVisual.getBodyVisuals().get(j);
            final ClothingItem clothingItem2 = itemVisual2.getClothingItem();
            if (clothingItem2 != null) {
                if (clothingItem2.isReady()) {
                    this.addClothingItem(itemVisual2, clothingItem2);
                }
            }
        }
        this.bUpdateTextures = true;
        Lambda.forEachFrom(PZArrayUtil::forEach, (List<ModelInstance>)this.modelInstance.sub, this.modelInstance, (modelInstance, modelInstance2) -> modelInstance.AnimPlayer = modelInstance2.AnimPlayer);
    }
    
    private void addHeadHair(final ItemVisual itemVisual) {
        final ImmutableColor hairColor = this.humanVisual.getHairColor();
        final ImmutableColor beardColor = this.humanVisual.getBeardColor();
        if (this.isFemale()) {
            HairStyle hairStyle = HairStyles.instance.FindFemaleStyle(this.humanVisual.getHairModel());
            if (hairStyle != null && itemVisual != null && itemVisual.getClothingItem() != null) {
                hairStyle = HairStyles.instance.getAlternateForHat(hairStyle, itemVisual.getClothingItem().m_HatCategory);
            }
            if (hairStyle != null && hairStyle.isValid()) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hairStyle.name));
                }
                this.addHeadHairItem(hairStyle.model, hairStyle.texture, hairColor);
            }
        }
        else {
            HairStyle hairStyle2 = HairStyles.instance.FindMaleStyle(this.humanVisual.getHairModel());
            if (hairStyle2 != null && itemVisual != null && itemVisual.getClothingItem() != null) {
                hairStyle2 = HairStyles.instance.getAlternateForHat(hairStyle2, itemVisual.getClothingItem().m_HatCategory);
            }
            if (hairStyle2 != null && hairStyle2.isValid()) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hairStyle2.name));
                }
                this.addHeadHairItem(hairStyle2.model, hairStyle2.texture, hairColor);
            }
            final BeardStyle findStyle = BeardStyles.instance.FindStyle(this.humanVisual.getBeardModel());
            if (findStyle != null && findStyle.isValid()) {
                if (itemVisual != null && itemVisual.getClothingItem() != null && !StringUtils.isNullOrEmpty(itemVisual.getClothingItem().m_HatCategory) && itemVisual.getClothingItem().m_HatCategory.contains("nobeard")) {
                    return;
                }
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, findStyle.name));
                }
                this.addHeadHairItem(findStyle.model, findStyle.texture, beardColor);
            }
        }
    }
    
    private void addHeadHairItem(String processModelFileName, final String s, final ImmutableColor immutableColor) {
        if (StringUtils.isNullOrWhitespace(processModelFileName)) {
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.warn((Object)"No model specified.");
            }
            return;
        }
        processModelFileName = this.processModelFileName(processModelFileName);
        final ModelInstance additionalModelInstance = ModelManager.instance.newAdditionalModelInstance(processModelFileName, s, null, this.modelInstance.AnimPlayer, null);
        if (additionalModelInstance == null) {
            return;
        }
        this.postProcessNewItemInstance(additionalModelInstance, immutableColor);
    }
    
    private void addClothingItem(final ItemVisual itemVisual, final ClothingItem clothingItem) {
        final String model = clothingItem.getModel(this.bFemale);
        if (StringUtils.isNullOrWhitespace(model)) {
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clothingItem.m_Name));
            }
            return;
        }
        final String processModelFileName = this.processModelFileName(model);
        final String textureChoice = itemVisual.getTextureChoice(clothingItem);
        final ImmutableColor tint = itemVisual.getTint(clothingItem);
        final String attachBone = clothingItem.m_AttachBone;
        final String shader = clothingItem.m_Shader;
        ModelInstance modelInstance;
        if (attachBone != null && attachBone.length() > 0) {
            modelInstance = this.addStatic(processModelFileName, textureChoice, attachBone, shader);
        }
        else {
            modelInstance = ModelManager.instance.newAdditionalModelInstance(processModelFileName, textureChoice, null, this.modelInstance.AnimPlayer, shader);
        }
        if (modelInstance == null) {
            return;
        }
        this.postProcessNewItemInstance(modelInstance, tint);
        modelInstance.setItemVisual(itemVisual);
    }
    
    private boolean isItemModelHidden(final ItemVisuals itemVisuals, final ItemVisual itemVisual) {
        return PopTemplateManager.instance.isItemModelHidden(BodyLocations.getGroup("Human"), itemVisuals, itemVisual);
    }
    
    private String processModelFileName(String s) {
        s = s.replaceAll("\\\\", "/");
        s = s.toLowerCase(Locale.ENGLISH);
        return s;
    }
    
    private void postProcessNewItemInstance(final ModelInstance e, final ImmutableColor immutableColor) {
        e.depthBias = 0.0f;
        e.matrixModel = this.modelInstance;
        e.tintR = immutableColor.r;
        e.tintG = immutableColor.g;
        e.tintB = immutableColor.b;
        e.AnimPlayer = this.modelInstance.AnimPlayer;
        this.modelInstance.sub.add(e);
        e.setOwner(this);
    }
    
    private void DoCharacterModelEquipped() {
        if (!StringUtils.isNullOrWhitespace(this.primaryHandModelName)) {
            this.postProcessNewItemInstance(this.addStatic(this.primaryHandModelName, "Bip01_Prop1"), ImmutableColor.white);
        }
        if (!StringUtils.isNullOrWhitespace(this.secondaryHandModelName)) {
            this.postProcessNewItemInstance(this.addStatic(this.secondaryHandModelName, "Bip01_Prop2"), ImmutableColor.white);
        }
        for (int i = 0; i < this.attachedModelNames.size(); ++i) {
            final AttachedModelName value = this.attachedModelNames.get(i);
            final ModelInstance addStatic = ModelManager.instance.addStatic(null, value.modelName, value.attachmentName, value.attachmentName);
            this.postProcessNewItemInstance(addStatic, ImmutableColor.white);
            if (value.bloodLevel > 0.0f && !Core.getInstance().getOptionSimpleWeaponTextures()) {
                final ModelInstanceTextureInitializer alloc = ModelInstanceTextureInitializer.alloc();
                alloc.init(addStatic, value.bloodLevel);
                addStatic.setTextureInitializer(alloc);
            }
        }
    }
    
    private ModelInstance addStatic(final String s, final String s2) {
        String meshName = s;
        String textureName = s;
        String shaderName = null;
        final ModelScript modelScript = ScriptManager.instance.getModelScript(s);
        if (modelScript != null) {
            meshName = modelScript.getMeshName();
            textureName = modelScript.getTextureName();
            shaderName = modelScript.getShaderName();
        }
        return this.addStatic(meshName, textureName, s2, shaderName);
    }
    
    private ModelInstance addStatic(final String s, final String s2, final String parentBoneName, final String s3) {
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        Model model = ModelManager.instance.tryGetLoadedModel(s, s2, true, s3, false);
        if (model == null) {
            ModelManager.instance.loadStaticModel(s.toLowerCase(), s2, s3);
            model = ModelManager.instance.getLoadedModel(s, s2, true, s3);
            if (model == null) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
                return null;
            }
        }
        final ModelInstance instance = ModelManager.instance.newInstance(model, null, this.modelInstance.AnimPlayer);
        instance.parent = this.modelInstance;
        if (this.modelInstance.AnimPlayer != null) {
            instance.parentBone = this.modelInstance.AnimPlayer.getSkinningBoneIndex(parentBoneName, instance.parentBone);
            instance.parentBoneName = parentBoneName;
        }
        return instance;
    }
    
    private StateInfo stateInfoMain() {
        return this.stateInfos[SpriteRenderer.instance.getMainStateIndex()];
    }
    
    private StateInfo stateInfoRender() {
        return this.stateInfos[SpriteRenderer.instance.getRenderStateIndex()];
    }
    
    public void update() {
        GameProfiler.getInstance().invokeAndMeasure("AnimatedModel.Update", this, AnimatedModel::updateInternal);
    }
    
    private void updateInternal() {
        this.initOutfit();
        if (this.bClothingChanged) {
            this.bClothingChanged = false;
            this.setModelData(this.humanVisual, this.itemVisuals);
        }
        this.modelInstance.SetForceDir(this.angle);
        final GameTime instance = GameTime.getInstance();
        final float fpsMultiplier = instance.FPSMultiplier;
        if (this.bAnimate) {
            if (UIManager.useUIFBO) {
                final GameTime gameTime = instance;
                gameTime.FPSMultiplier *= GameWindow.averageFPS / Core.OptionUIRenderFPS;
            }
            this.actionContext.update();
            this.advancedAnimator.update();
            this.animPlayer.Update();
            final StateInfo stateInfo = this.stateInfos[SpriteRenderer.instance.getMainStateIndex()];
            if (!stateInfo.readyData.isEmpty()) {
                final ModelInstance modelInstance = stateInfo.readyData.get(0).modelInstance;
                if (modelInstance != this.modelInstance && modelInstance.AnimPlayer != this.modelInstance.AnimPlayer) {
                    modelInstance.Update();
                }
            }
            instance.FPSMultiplier = fpsMultiplier;
        }
        else {
            instance.FPSMultiplier = 100.0f;
            try {
                this.advancedAnimator.update();
            }
            finally {
                instance.FPSMultiplier = fpsMultiplier;
            }
            if (this.trackTime > 0.0f && this.animPlayer.getMultiTrack().getTrackCount() > 0) {
                this.animPlayer.getMultiTrack().getTracks().get(0).setCurrentTimeValue(this.trackTime);
            }
            this.animPlayer.Update(0.0f);
        }
    }
    
    private boolean isModelInstanceReady(final ModelInstance modelInstance) {
        return modelInstance.model != null && modelInstance.model.isReady() && modelInstance.model.Mesh.isReady() && modelInstance.model.Mesh.vb != null;
    }
    
    public boolean isReadyToRender() {
        if (!this.animPlayer.isReady()) {
            return false;
        }
        if (!this.isModelInstanceReady(this.modelInstance)) {
            return false;
        }
        for (int i = 0; i < this.modelInstance.sub.size(); ++i) {
            if (!this.isModelInstanceReady(this.modelInstance.sub.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public int renderMain() {
        final StateInfo stateInfoMain = this.stateInfoMain();
        if (this.modelInstance != null) {
            if (this.bUpdateTextures) {
                this.bUpdateTextures = false;
                (this.textureCreator = ModelInstanceTextureCreator.alloc()).init(this.humanVisual, this.itemVisuals, this.modelInstance);
            }
            final ModelInstance modelInstance = this.modelInstance;
            ++modelInstance.renderRefCount;
            for (int i = 0; i < this.modelInstance.sub.size(); ++i) {
                final ModelInstance modelInstance2 = this.modelInstance.sub.get(i);
                ++modelInstance2.renderRefCount;
            }
            AnimatedModel.instDataPool.release(stateInfoMain.instData);
            stateInfoMain.instData.clear();
            if (!stateInfoMain.bModelsReady && this.isReadyToRender()) {
                final float fpsMultiplier = GameTime.getInstance().FPSMultiplier;
                GameTime.getInstance().FPSMultiplier = 100.0f;
                try {
                    this.advancedAnimator.update();
                }
                finally {
                    GameTime.getInstance().FPSMultiplier = fpsMultiplier;
                }
                this.animPlayer.Update(0.0f);
                stateInfoMain.bModelsReady = true;
            }
            final AnimatedModelInstanceRenderData init = AnimatedModel.instDataPool.alloc().init(this.modelInstance);
            stateInfoMain.instData.add(init);
            for (int j = 0; j < this.modelInstance.sub.size(); ++j) {
                final AnimatedModelInstanceRenderData init2 = AnimatedModel.instDataPool.alloc().init(this.modelInstance.sub.get(j));
                init2.transformToParent(init);
                stateInfoMain.instData.add(init2);
            }
        }
        stateInfoMain.modelInstance = this.modelInstance;
        stateInfoMain.textureCreator = ((this.textureCreator != null && !this.textureCreator.isRendered()) ? this.textureCreator : null);
        for (int k = 0; k < stateInfoMain.readyData.size(); ++k) {
            final AnimatedModelInstanceRenderData animatedModelInstanceRenderData = stateInfoMain.readyData.get(k);
            animatedModelInstanceRenderData.init(animatedModelInstanceRenderData.modelInstance);
            animatedModelInstanceRenderData.transformToParent(stateInfoMain.getParentData(animatedModelInstanceRenderData.modelInstance));
        }
        stateInfoMain.bRendered = false;
        return SpriteRenderer.instance.getMainStateIndex();
    }
    
    public boolean isRendered() {
        return this.stateInfoRender().bRendered;
    }
    
    private void doneWithTextureCreator(final ModelInstanceTextureCreator modelInstanceTextureCreator) {
        if (modelInstanceTextureCreator == null) {
            return;
        }
        for (int i = 0; i < this.stateInfos.length; ++i) {
            if (this.stateInfos[i].textureCreator == modelInstanceTextureCreator) {
                return;
            }
        }
        if (modelInstanceTextureCreator.isRendered()) {
            modelInstanceTextureCreator.postRender();
            if (modelInstanceTextureCreator == this.textureCreator) {
                this.textureCreator = null;
            }
        }
        else if (modelInstanceTextureCreator != this.textureCreator) {
            modelInstanceTextureCreator.postRender();
        }
    }
    
    private void release(final ArrayList<AnimatedModelInstanceRenderData> list) {
        for (int i = 0; i < list.size(); ++i) {
            final AnimatedModelInstanceRenderData animatedModelInstanceRenderData = list.get(i);
            if (animatedModelInstanceRenderData.modelInstance.getTextureInitializer() != null) {
                animatedModelInstanceRenderData.modelInstance.getTextureInitializer().postRender();
            }
            ModelManager.instance.derefModelInstance(animatedModelInstanceRenderData.modelInstance);
        }
        AnimatedModel.instDataPool.release(list);
    }
    
    public void postRender(final boolean b) {
        final StateInfo stateInfo = this.stateInfos[SpriteRenderer.instance.getMainStateIndex()];
        final ModelInstanceTextureCreator textureCreator = stateInfo.textureCreator;
        stateInfo.textureCreator = null;
        this.doneWithTextureCreator(textureCreator);
        stateInfo.modelInstance = null;
        if (this.bAnimate && stateInfo.bRendered) {
            this.release(stateInfo.readyData);
            stateInfo.readyData.clear();
            stateInfo.readyData.addAll(stateInfo.instData);
            stateInfo.instData.clear();
        }
        else if (!this.bAnimate) {}
        this.release(stateInfo.instData);
        stateInfo.instData.clear();
    }
    
    public void DoRender(final ModelCamera modelCamera) {
        final StateInfo stateInfo = this.stateInfos[SpriteRenderer.instance.getRenderStateIndex()];
        this.bReady = true;
        final ModelInstanceTextureCreator textureCreator = stateInfo.textureCreator;
        if (textureCreator != null && !textureCreator.isRendered()) {
            textureCreator.render();
            if (!textureCreator.isRendered()) {
                this.bReady = false;
            }
        }
        if (this.modelInstance.model == null || !this.modelInstance.model.isReady() || !this.modelInstance.model.Mesh.isReady() || this.modelInstance.model.Mesh.vb == null) {
            this.bReady = false;
        }
        for (int i = 0; i < this.modelInstance.sub.size(); ++i) {
            final ModelInstance modelInstance = this.modelInstance.sub.get(i);
            if (modelInstance.model == null || !modelInstance.model.isReady() || !modelInstance.model.Mesh.isReady() || modelInstance.model.Mesh.vb == null) {
                this.bReady = false;
            }
        }
        for (int j = 0; j < stateInfo.instData.size(); ++j) {
            final ModelInstanceTextureInitializer textureInitializer = stateInfo.instData.get(j).modelInstance.getTextureInitializer();
            if (textureInitializer != null && !textureInitializer.isRendered()) {
                textureInitializer.render();
                if (!textureInitializer.isRendered()) {
                    this.bReady = false;
                }
            }
        }
        if (this.bReady && !stateInfo.bModelsReady) {
            this.bReady = false;
        }
        if (!this.bReady && stateInfo.readyData.isEmpty()) {
            return;
        }
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        modelCamera.Begin();
        this.StartCharacter();
        this.Render();
        this.EndCharacter();
        modelCamera.End();
        GL11.glDepthFunc(519);
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        stateInfo.bRendered = this.bReady;
    }
    
    public void DoRender(final int x, final int y, final int w, final int h, final float sizeV, final float animPlayerAngle) {
        GL11.glClear(256);
        this.uiModelCamera.x = x;
        this.uiModelCamera.y = y;
        this.uiModelCamera.w = w;
        this.uiModelCamera.h = h;
        this.uiModelCamera.sizeV = sizeV;
        this.uiModelCamera.m_animPlayerAngle = animPlayerAngle;
        this.DoRender(this.uiModelCamera);
    }
    
    public void DoRenderToWorld(final float x, final float y, final float z, final float angle) {
        AnimatedModel.worldModelCamera.x = x;
        AnimatedModel.worldModelCamera.y = y;
        AnimatedModel.worldModelCamera.z = z;
        AnimatedModel.worldModelCamera.angle = angle;
        this.DoRender(AnimatedModel.worldModelCamera);
    }
    
    private void debugDrawAxes() {
        if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
            Model.debugDrawAxis(0.0f, 0.0f, 0.0f, 1.0f, 4.0f);
        }
    }
    
    private void StartCharacter() {
        GL11.glEnable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glDisable(3089);
        GL11.glDepthMask(true);
    }
    
    private void EndCharacter() {
        GL11.glDepthMask(false);
        GL11.glViewport(0, 0, Core.width, Core.height);
    }
    
    private void Render() {
        final StateInfo stateInfo = this.stateInfos[SpriteRenderer.instance.getRenderStateIndex()];
        if (stateInfo.modelInstance != null) {
            final ArrayList<AnimatedModelInstanceRenderData> list = this.bReady ? stateInfo.instData : stateInfo.readyData;
            for (int i = 0; i < list.size(); ++i) {
                this.DrawChar(list.get(i));
            }
        }
        this.debugDrawAxes();
    }
    
    private void DrawChar(final AnimatedModelInstanceRenderData animatedModelInstanceRenderData) {
        final ModelInstance modelInstance = animatedModelInstanceRenderData.modelInstance;
        final FloatBuffer matrixPalette = animatedModelInstanceRenderData.matrixPalette;
        if (modelInstance == null) {
            return;
        }
        if (modelInstance.AnimPlayer == null) {
            return;
        }
        if (!modelInstance.AnimPlayer.hasSkinningData()) {
            return;
        }
        if (modelInstance.model == null) {
            return;
        }
        if (!modelInstance.model.isReady()) {
            return;
        }
        if (modelInstance.tex == null && modelInstance.model.tex == null) {
            return;
        }
        GL11.glEnable(2884);
        GL11.glCullFace(1028);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glDepthFunc(513);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glAlphaFunc(516, 0.01f);
        if (modelInstance.model.Effect == null) {
            modelInstance.model.CreateShader("basicEffect");
        }
        final Shader effect = modelInstance.model.Effect;
        if (effect != null) {
            effect.Start();
            if (modelInstance.model.bStatic) {
                effect.setTransformMatrix(animatedModelInstanceRenderData.xfrm, true);
            }
            else {
                effect.setMatrixPalette(matrixPalette, true);
            }
            effect.setLight(0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, modelInstance);
            effect.setLight(1, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, modelInstance);
            effect.setLight(2, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, modelInstance);
            effect.setLight(3, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, modelInstance);
            effect.setLight(4, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Float.NaN, modelInstance);
            final float n = 0.7f;
            for (int i = 0; i < this.lights.length; ++i) {
                final IsoGridSquare.ResultLight resultLight = this.lights[i];
                if (resultLight.radius > 0) {
                    effect.setLight(i, resultLight.x + 0.5f, resultLight.y + 0.5f, resultLight.z + 0.5f, resultLight.r * n, resultLight.g * n, resultLight.b * n, (float)resultLight.radius, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, null);
                }
            }
            if (modelInstance.tex != null) {
                effect.setTexture(modelInstance.tex, "Texture", 0);
            }
            else if (modelInstance.model.tex != null) {
                effect.setTexture(modelInstance.model.tex, "Texture", 0);
            }
            if (this.bOutside) {
                final float model_LIGHT_MULT_OUTSIDE = ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
                effect.setLight(3, this.lightsOriginX - 2.0f, this.lightsOriginY - 2.0f, this.lightsOriginZ + 1.0f, this.ambient.r * model_LIGHT_MULT_OUTSIDE / 4.0f, this.ambient.g * model_LIGHT_MULT_OUTSIDE / 4.0f, this.ambient.b * model_LIGHT_MULT_OUTSIDE / 4.0f, 5000.0f, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, null);
                effect.setLight(4, this.lightsOriginX + 2.0f, this.lightsOriginY + 2.0f, this.lightsOriginZ + 1.0f, this.ambient.r * model_LIGHT_MULT_OUTSIDE / 4.0f, this.ambient.g * model_LIGHT_MULT_OUTSIDE / 4.0f, this.ambient.b * model_LIGHT_MULT_OUTSIDE / 4.0f, 5000.0f, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, null);
            }
            else if (this.bRoom) {
                final float model_LIGHT_MULT_ROOM = ModelInstance.MODEL_LIGHT_MULT_ROOM;
                effect.setLight(4, this.lightsOriginX + 2.0f, this.lightsOriginY + 2.0f, this.lightsOriginZ + 1.0f, this.ambient.r * model_LIGHT_MULT_ROOM / 4.0f, this.ambient.g * model_LIGHT_MULT_ROOM / 4.0f, this.ambient.b * model_LIGHT_MULT_ROOM / 4.0f, 5000.0f, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, null);
            }
            effect.setDepthBias(modelInstance.depthBias / 50.0f);
            effect.setAmbient(this.ambient.r * 0.45f, this.ambient.g * 0.45f, this.ambient.b * 0.45f);
            effect.setLightingAmount(1.0f);
            effect.setHueShift(modelInstance.hue);
            effect.setTint(modelInstance.tintR, modelInstance.tintG, modelInstance.tintB);
            effect.setAlpha(this.m_alpha);
        }
        modelInstance.model.Mesh.Draw(effect);
        if (effect != null) {
            effect.End();
        }
        if (Core.bDebug && DebugOptions.instance.ModelRenderLights.getValue() && modelInstance.parent == null) {
            if (this.lights[0].radius > 0) {
                final Model model = modelInstance.model;
                Model.debugDrawLightSource((float)this.lights[0].x, (float)this.lights[0].y, (float)this.lights[0].z, 0.0f, 0.0f, 0.0f, -animatedModelInstanceRenderData.m_animPlayerAngle);
            }
            if (this.lights[1].radius > 0) {
                final Model model2 = modelInstance.model;
                Model.debugDrawLightSource((float)this.lights[1].x, (float)this.lights[1].y, (float)this.lights[1].z, 0.0f, 0.0f, 0.0f, -animatedModelInstanceRenderData.m_animPlayerAngle);
            }
            if (this.lights[2].radius > 0) {
                final Model model3 = modelInstance.model;
                Model.debugDrawLightSource((float)this.lights[2].x, (float)this.lights[2].y, (float)this.lights[2].z, 0.0f, 0.0f, 0.0f, -animatedModelInstanceRenderData.m_animPlayerAngle);
            }
        }
        if (Core.bDebug && DebugOptions.instance.ModelRenderBones.getValue()) {
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GL11.glLineWidth(1.0f);
            GL11.glBegin(1);
            for (int j = 0; j < modelInstance.AnimPlayer.modelTransforms.length; ++j) {
                final int intValue = modelInstance.AnimPlayer.getSkinningData().SkeletonHierarchy.get(j);
                if (intValue >= 0) {
                    final Color color = Model.debugDrawColours[j % Model.debugDrawColours.length];
                    GL11.glColor3f(color.r, color.g, color.b);
                    final Matrix4f matrix4f = modelInstance.AnimPlayer.modelTransforms[j];
                    GL11.glVertex3f(matrix4f.m03, matrix4f.m13, matrix4f.m23);
                    final Matrix4f matrix4f2 = modelInstance.AnimPlayer.modelTransforms[intValue];
                    GL11.glVertex3f(matrix4f2.m03, matrix4f2.m13, matrix4f2.m23);
                }
            }
            GL11.glEnd();
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glEnable(2929);
        }
    }
    
    public void releaseAnimationPlayer() {
        if (this.animPlayer != null) {
            this.animPlayer = Pool.tryRelease(this.animPlayer);
        }
    }
    
    @Override
    public void OnAnimEvent(final AnimLayer animLayer, final AnimEvent animEvent) {
        if (StringUtils.isNullOrWhitespace(animEvent.m_EventName)) {
            return;
        }
        this.actionContext.reportEvent(animLayer.getDepth(), animEvent.m_EventName);
    }
    
    @Override
    public AnimationPlayer getAnimationPlayer() {
        Model model;
        if (this.isSkeleton()) {
            model = (this.bFemale ? ModelManager.instance.m_skeletonFemaleModel : ModelManager.instance.m_skeletonMaleModel);
        }
        else {
            model = (this.bFemale ? ModelManager.instance.m_femaleModel : ModelManager.instance.m_maleModel);
        }
        if (this.animPlayer != null && this.animPlayer.getModel() != model) {
            this.animPlayer = Pool.tryRelease(this.animPlayer);
        }
        if (this.animPlayer == null) {
            this.animPlayer = AnimationPlayer.alloc(model);
        }
        return this.animPlayer;
    }
    
    @Override
    public void actionStateChanged(final ActionContext actionContext) {
        this.advancedAnimator.SetState(actionContext.getCurrentStateName(), PZArrayUtil.listConvert(actionContext.getChildStates(), actionState -> actionState.name));
    }
    
    @Override
    public AnimationPlayerRecorder getAnimationPlayerRecorder() {
        return null;
    }
    
    @Override
    public boolean isAnimationRecorderActive() {
        return false;
    }
    
    @Override
    public ActionContext getActionContext() {
        return this.actionContext;
    }
    
    @Override
    public AdvancedAnimator getAdvancedAnimator() {
        return this.advancedAnimator;
    }
    
    @Override
    public ModelInstance getModelInstance() {
        return this.modelInstance;
    }
    
    @Override
    public String GetAnimSetName() {
        return this.animSetName;
    }
    
    @Override
    public String getUID() {
        return this.m_UID;
    }
    
    static {
        instDataPool = new ObjectPool<AnimatedModelInstanceRenderData>(AnimatedModelInstanceRenderData::new);
        worldModelCamera = new WorldModelCamera();
    }
    
    public static final class StateInfo
    {
        ModelInstance modelInstance;
        ModelInstanceTextureCreator textureCreator;
        final ArrayList<AnimatedModelInstanceRenderData> instData;
        final ArrayList<AnimatedModelInstanceRenderData> readyData;
        boolean bModelsReady;
        boolean bRendered;
        
        public StateInfo() {
            this.instData = new ArrayList<AnimatedModelInstanceRenderData>();
            this.readyData = new ArrayList<AnimatedModelInstanceRenderData>();
        }
        
        AnimatedModelInstanceRenderData getParentData(final ModelInstance modelInstance) {
            for (int i = 0; i < this.readyData.size(); ++i) {
                final AnimatedModelInstanceRenderData animatedModelInstanceRenderData = this.readyData.get(i);
                if (animatedModelInstanceRenderData.modelInstance == modelInstance.parent) {
                    return animatedModelInstanceRenderData;
                }
            }
            return null;
        }
    }
    
    private static final class AnimatedModelInstanceRenderData
    {
        ModelInstance modelInstance;
        FloatBuffer matrixPalette;
        public final org.joml.Matrix4f xfrm;
        float m_animPlayerAngle;
        
        private AnimatedModelInstanceRenderData() {
            this.xfrm = new org.joml.Matrix4f();
        }
        
        AnimatedModelInstanceRenderData init(final ModelInstance modelInstance) {
            this.modelInstance = modelInstance;
            this.xfrm.identity();
            this.m_animPlayerAngle = Float.NaN;
            if (modelInstance.AnimPlayer != null) {
                this.m_animPlayerAngle = modelInstance.AnimPlayer.getRenderedAngle();
                if (!modelInstance.model.bStatic) {
                    final SkinningData skinningData = (SkinningData)modelInstance.model.Tag;
                    if (Core.bDebug && skinningData == null) {
                        DebugLog.General.warn((Object)"skinningData is null, matrixPalette may be invalid");
                    }
                    final Matrix4f[] skinTransforms = modelInstance.AnimPlayer.getSkinTransforms(skinningData);
                    if (this.matrixPalette == null || this.matrixPalette.capacity() < skinTransforms.length * 16) {
                        this.matrixPalette = BufferUtils.createFloatBuffer(skinTransforms.length * 16);
                    }
                    this.matrixPalette.clear();
                    for (int i = 0; i < skinTransforms.length; ++i) {
                        skinTransforms[i].store(this.matrixPalette);
                    }
                    this.matrixPalette.flip();
                }
            }
            if (modelInstance.getTextureInitializer() != null) {
                modelInstance.getTextureInitializer().renderMain();
            }
            return this;
        }
        
        public AnimatedModelInstanceRenderData transformToParent(final AnimatedModelInstanceRenderData animatedModelInstanceRenderData) {
            if (this.modelInstance instanceof VehicleModelInstance || this.modelInstance instanceof VehicleSubModelInstance) {
                return this;
            }
            if (animatedModelInstanceRenderData == null) {
                return this;
            }
            this.xfrm.set((Matrix4fc)animatedModelInstanceRenderData.xfrm);
            this.xfrm.transpose();
            final org.joml.Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
            final ModelAttachment attachmentById = animatedModelInstanceRenderData.modelInstance.getAttachmentById(this.modelInstance.attachmentNameParent);
            if (attachmentById == null) {
                if (this.modelInstance.parentBoneName != null && animatedModelInstanceRenderData.modelInstance.AnimPlayer != null) {
                    ModelInstanceRenderData.applyBoneTransform(animatedModelInstanceRenderData.modelInstance, this.modelInstance.parentBoneName, this.xfrm);
                }
            }
            else {
                ModelInstanceRenderData.applyBoneTransform(animatedModelInstanceRenderData.modelInstance, attachmentById.getBone(), this.xfrm);
                ModelInstanceRenderData.makeAttachmentTransform(attachmentById, matrix4f);
                this.xfrm.mul((Matrix4fc)matrix4f);
            }
            final ModelAttachment attachmentById2 = this.modelInstance.getAttachmentById(this.modelInstance.attachmentNameSelf);
            if (attachmentById2 != null) {
                ModelInstanceRenderData.makeAttachmentTransform(attachmentById2, matrix4f);
                matrix4f.invert();
                this.xfrm.mul((Matrix4fc)matrix4f);
            }
            if (this.modelInstance.model.Mesh != null && this.modelInstance.model.Mesh.isReady() && this.modelInstance.model.Mesh.m_transform != null) {
                this.xfrm.mul((Matrix4fc)this.modelInstance.model.Mesh.m_transform);
            }
            if (this.modelInstance.scale != 1.0f) {
                this.xfrm.scale(this.modelInstance.scale);
            }
            this.xfrm.transpose();
            BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
            return this;
        }
    }
    
    private final class UIModelCamera extends ModelCamera
    {
        int x;
        int y;
        int w;
        int h;
        float sizeV;
        float m_animPlayerAngle;
        
        @Override
        public void Begin() {
            GL11.glViewport(this.x, this.y, this.w, this.h);
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            final float n = this.w / (float)this.h;
            if (AnimatedModel.this.flipY) {
                GL11.glOrtho((double)(-this.sizeV * n), (double)(this.sizeV * n), (double)this.sizeV, (double)(-this.sizeV), -100.0, 100.0);
            }
            else {
                GL11.glOrtho((double)(-this.sizeV * n), (double)(this.sizeV * n), (double)(-this.sizeV), (double)this.sizeV, -100.0, 100.0);
            }
            final float sqrt = Math.sqrt(2048.0f);
            GL11.glScalef(-sqrt, sqrt, sqrt);
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(AnimatedModel.this.offset.x(), AnimatedModel.this.offset.y(), AnimatedModel.this.offset.z());
            if (AnimatedModel.this.bIsometric) {
                GL11.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotated((double)(this.m_animPlayerAngle * 57.295776f + 45.0f), 0.0, 1.0, 0.0);
            }
            else {
                GL11.glRotated((double)(this.m_animPlayerAngle * 57.295776f), 0.0, 1.0, 0.0);
            }
        }
        
        @Override
        public void End() {
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
        }
    }
    
    private static final class WorldModelCamera extends ModelCamera
    {
        float x;
        float y;
        float z;
        float angle;
        
        @Override
        public void Begin() {
            Core.getInstance().DoPushIsoStuff(this.x, this.y, this.z, this.angle, false);
            GL11.glDepthMask(true);
        }
        
        @Override
        public void End() {
            Core.getInstance().DoPopIsoStuff();
        }
    }
}
