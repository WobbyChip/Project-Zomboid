// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel;

import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.ImmutableColor;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Arrays;
import zombie.util.StringUtils;
import java.util.Map;
import java.util.Collection;
import zombie.iso.IsoGridSquare;
import zombie.core.textures.ColorInfo;
import zombie.characters.AttachedItems.AttachedModelNames;
import zombie.core.opengl.RenderThread;
import zombie.interfaces.ITexture;
import zombie.characters.IsoPlayer;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.opengl.GL11;
import java.util.function.Consumer;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.core.textures.TextureDraw;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.characters.AttachedItems.AttachedModelName;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoMannequin;
import zombie.characters.IsoZombie;
import zombie.core.textures.Texture;
import zombie.iso.objects.IsoDeadBody;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.iso.Vector2;
import zombie.core.textures.TextureFBO;

public final class DeadBodyAtlas
{
    public static final int ATLAS_SIZE = 1024;
    public static final int ENTRY_WID;
    public static final int ENTRY_HGT;
    private TextureFBO fbo;
    public static final DeadBodyAtlas instance;
    private static final Vector2 tempVector2;
    private final HashMap<String, AtlasEntry> EntryMap;
    private final ArrayList<Atlas> AtlasList;
    private final BodyParams bodyParams;
    private int updateCounter;
    private final Checksummer checksummer;
    private static final Stack<RenderJob> JobPool;
    private final DebugDrawInWorld[] debugDrawInWorld;
    private long debugDrawTime;
    private final ArrayList<RenderJob> RenderJobs;
    private final CharacterTextureVisual characterTextureVisualFemale;
    private final CharacterTextureVisual characterTextureVisualMale;
    private final CharacterTextures characterTexturesFemale;
    private final CharacterTextures characterTexturesMale;
    
    public DeadBodyAtlas() {
        this.EntryMap = new HashMap<String, AtlasEntry>();
        this.AtlasList = new ArrayList<Atlas>();
        this.bodyParams = new BodyParams();
        this.updateCounter = -1;
        this.checksummer = new Checksummer();
        this.debugDrawInWorld = new DebugDrawInWorld[3];
        this.RenderJobs = new ArrayList<RenderJob>();
        this.characterTextureVisualFemale = new CharacterTextureVisual(true);
        this.characterTextureVisualMale = new CharacterTextureVisual(false);
        this.characterTexturesFemale = new CharacterTextures();
        this.characterTexturesMale = new CharacterTextures();
    }
    
    public void lightingUpdate(final int updateCounter, final boolean b) {
        if (updateCounter != this.updateCounter && b) {
            this.updateCounter = updateCounter;
        }
    }
    
    public Texture getBodyTexture(final IsoDeadBody isoDeadBody) {
        this.bodyParams.init(isoDeadBody);
        return this.getBodyTexture(this.bodyParams);
    }
    
    public Texture getBodyTexture(final IsoZombie isoZombie) {
        this.bodyParams.init(isoZombie);
        return this.getBodyTexture(this.bodyParams);
    }
    
    public Texture getBodyTexture(final IsoMannequin isoMannequin) {
        this.bodyParams.init(isoMannequin);
        return this.getBodyTexture(this.bodyParams);
    }
    
    public Texture getBodyTexture(final boolean b, final String s, final String s2, final IsoDirections isoDirections, final int n, final float n2) {
        final CharacterTextures characterTextures = b ? this.characterTexturesFemale : this.characterTexturesMale;
        final Texture texture = characterTextures.getTexture(s, s2, isoDirections, n);
        if (texture != null) {
            return texture;
        }
        this.bodyParams.init(b ? this.characterTextureVisualFemale : this.characterTextureVisualMale, isoDirections, s, s2, n2);
        this.bodyParams.variables.put("zombieWalkType", "1");
        final Texture bodyTexture = this.getBodyTexture(this.bodyParams);
        characterTextures.addTexture(s, s2, isoDirections, n, bodyTexture);
        return bodyTexture;
    }
    
    public Texture getBodyTexture(final BodyParams bodyParams) {
        final String bodyKey = this.getBodyKey(bodyParams);
        final AtlasEntry atlasEntry = this.EntryMap.get(bodyKey);
        if (atlasEntry != null) {
            return atlasEntry.tex;
        }
        Atlas e = null;
        for (int i = 0; i < this.AtlasList.size(); ++i) {
            final Atlas atlas = this.AtlasList.get(i);
            if (!atlas.isFull()) {
                e = atlas;
                break;
            }
        }
        if (e == null) {
            e = new Atlas(1024, 1024);
            if (this.fbo == null) {
                return null;
            }
            this.AtlasList.add(e);
        }
        final AtlasEntry addBody = e.addBody(bodyKey);
        addBody.lightKey = this.getLightKey(bodyParams);
        addBody.updateCounter = this.updateCounter;
        this.EntryMap.put(bodyKey, addBody);
        this.RenderJobs.add(RenderJob.getNew().init(bodyParams, addBody));
        return addBody.tex;
    }
    
    public void checkLights(final Texture texture, final IsoDeadBody isoDeadBody) {
        if (texture == null) {
            return;
        }
        final AtlasEntry value = this.EntryMap.get(texture.getName());
        if (value == null || value.tex != texture) {
            return;
        }
        if (value.updateCounter == this.updateCounter) {
            return;
        }
        value.updateCounter = this.updateCounter;
        this.bodyParams.init(isoDeadBody);
        final String lightKey = this.getLightKey(this.bodyParams);
        if (value.lightKey.equals(lightKey)) {
            return;
        }
        this.EntryMap.remove(value.key);
        value.key = this.getBodyKey(this.bodyParams);
        value.lightKey = lightKey;
        texture.setNameOnly(value.key);
        this.EntryMap.put(value.key, value);
        final RenderJob init = RenderJob.getNew().init(this.bodyParams, value);
        init.bClearThisSlotOnly = true;
        this.RenderJobs.add(init);
        this.render();
    }
    
    public void checkLights(final Texture texture, final IsoZombie isoZombie) {
        if (texture == null) {
            return;
        }
        final AtlasEntry value = this.EntryMap.get(texture.getName());
        if (value == null || value.tex != texture) {
            return;
        }
        if (value.updateCounter == this.updateCounter) {
            return;
        }
        value.updateCounter = this.updateCounter;
        this.bodyParams.init(isoZombie);
        final String lightKey = this.getLightKey(this.bodyParams);
        if (value.lightKey.equals(lightKey)) {
            return;
        }
        this.EntryMap.remove(value.key);
        value.key = this.getBodyKey(this.bodyParams);
        value.lightKey = lightKey;
        texture.setNameOnly(value.key);
        this.EntryMap.put(value.key, value);
        final RenderJob init = RenderJob.getNew().init(this.bodyParams, value);
        init.bClearThisSlotOnly = true;
        this.RenderJobs.add(init);
        this.render();
    }
    
    private String getBodyKey(final BodyParams bodyParams) {
        if (bodyParams.humanVisual == this.characterTextureVisualFemale.humanVisual) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Lzombie/iso/IsoDirections;F)Ljava/lang/String;, bodyParams.animSetName, bodyParams.stateName, bodyParams.dir, bodyParams.trackTime);
        }
        if (bodyParams.humanVisual == this.characterTextureVisualMale.humanVisual) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Lzombie/iso/IsoDirections;F)Ljava/lang/String;, bodyParams.animSetName, bodyParams.stateName, bodyParams.dir, bodyParams.trackTime);
        }
        try {
            this.checksummer.reset();
            final HumanVisual humanVisual = bodyParams.humanVisual;
            this.checksummer.update((byte)bodyParams.dir.index());
            this.checksummer.update((int)(PZMath.wrap(bodyParams.angle, 0.0f, 6.2831855f) * 57.295776f));
            this.checksummer.update(humanVisual.getHairModel());
            this.checksummer.update(humanVisual.getBeardModel());
            this.checksummer.update(humanVisual.getSkinColor());
            this.checksummer.update(humanVisual.getSkinTexture());
            this.checksummer.update((int)(humanVisual.getTotalBlood() * 100.0f));
            this.checksummer.update(bodyParams.primaryHandItem);
            this.checksummer.update(bodyParams.secondaryHandItem);
            for (int i = 0; i < bodyParams.attachedModelNames.size(); ++i) {
                final AttachedModelName value = bodyParams.attachedModelNames.get(i);
                this.checksummer.update(value.attachmentName);
                this.checksummer.update(value.modelName);
                this.checksummer.update((int)(value.bloodLevel * 100.0f));
            }
            this.checksummer.update(bodyParams.bFemale);
            this.checksummer.update(bodyParams.bZombie);
            this.checksummer.update(bodyParams.bSkeleton);
            final ItemVisuals itemVisuals = bodyParams.itemVisuals;
            for (int j = 0; j < itemVisuals.size(); ++j) {
                final ItemVisual itemVisual = itemVisuals.get(j);
                final ClothingItem clothingItem = itemVisual.getClothingItem();
                if (clothingItem != null) {
                    this.checksummer.update(itemVisual.getBaseTexture(clothingItem));
                    this.checksummer.update(itemVisual.getTextureChoice(clothingItem));
                    this.checksummer.update(itemVisual.getTint(clothingItem));
                    this.checksummer.update(clothingItem.getModel(humanVisual.isFemale()));
                    this.checksummer.update((int)(itemVisual.getTotalBlood() * 100.0f));
                }
            }
            this.checksummer.update(bodyParams.fallOnFront);
            this.checksummer.update(bodyParams.bStanding);
            this.checksummer.update(bodyParams.bOutside);
            this.checksummer.update(bodyParams.bRoom);
            this.checksummer.update((byte)((int)(bodyParams.ambient.r * 10.0f) / 10.0f * 255.0f));
            this.checksummer.update((byte)((int)(bodyParams.ambient.g * 10.0f) / 10.0f * 255.0f));
            this.checksummer.update((byte)((int)(bodyParams.ambient.b * 10.0f) / 10.0f * 255.0f));
            this.checksummer.update((int)bodyParams.trackTime);
            for (int k = 0; k < bodyParams.lights.length; ++k) {
                this.checksummer.update(bodyParams.lights[k], bodyParams.x, bodyParams.y, bodyParams.z);
            }
            return this.checksummer.checksumToString();
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
            return "bogus";
        }
    }
    
    private String getLightKey(final BodyParams bodyParams) {
        try {
            this.checksummer.reset();
            this.checksummer.update(bodyParams.bOutside);
            this.checksummer.update(bodyParams.bRoom);
            this.checksummer.update((byte)((int)(bodyParams.ambient.r * 10.0f) / 10.0f * 255.0f));
            this.checksummer.update((byte)((int)(bodyParams.ambient.g * 10.0f) / 10.0f * 255.0f));
            this.checksummer.update((byte)((int)(bodyParams.ambient.b * 10.0f) / 10.0f * 255.0f));
            for (int i = 0; i < bodyParams.lights.length; ++i) {
                this.checksummer.update(bodyParams.lights[i], bodyParams.x, bodyParams.y, bodyParams.z);
            }
            return this.checksummer.checksumToString();
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
            return "bogus";
        }
    }
    
    public void render() {
        for (int i = 0; i < this.AtlasList.size(); ++i) {
            final Atlas atlas = this.AtlasList.get(i);
            if (atlas.clear) {
                SpriteRenderer.instance.drawGeneric(new ClearAtlasTexture(atlas));
            }
        }
        if (this.RenderJobs.isEmpty()) {
            return;
        }
        for (int j = 0; j < this.RenderJobs.size(); ++j) {
            final RenderJob renderJob = this.RenderJobs.get(j);
            if (renderJob.done != 1 || renderJob.renderRefCount <= 0) {
                if (renderJob.done == 1 && renderJob.renderRefCount == 0) {
                    this.RenderJobs.remove(j--);
                    assert !DeadBodyAtlas.JobPool.contains(renderJob);
                    DeadBodyAtlas.JobPool.push(renderJob);
                }
                else if (renderJob.renderMain()) {
                    final RenderJob renderJob2 = renderJob;
                    ++renderJob2.renderRefCount;
                    SpriteRenderer.instance.drawGeneric(renderJob);
                }
            }
        }
    }
    
    public void renderDebug() {
        if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
            if (DeadBodyAtlas.JobPool.isEmpty()) {
                return;
            }
            if (DeadBodyAtlas.JobPool.get(DeadBodyAtlas.JobPool.size() - 1).entry.atlas == null) {
                return;
            }
            if (this.debugDrawInWorld[0] == null) {
                for (int i = 0; i < this.debugDrawInWorld.length; ++i) {
                    this.debugDrawInWorld[i] = new DebugDrawInWorld();
                }
            }
            final int mainStateIndex = SpriteRenderer.instance.getMainStateIndex();
            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.debugDrawTime < 500L) {
                final RenderJob e = DeadBodyAtlas.JobPool.pop();
                e.done = 0;
                e.bClearThisSlotOnly = true;
                this.RenderJobs.add(e);
            }
            else if (currentTimeMillis - this.debugDrawTime < 1000L) {
                final RenderJob renderJob = DeadBodyAtlas.JobPool.pop();
                renderJob.done = 0;
                renderJob.renderMain();
                this.debugDrawInWorld[mainStateIndex].init(renderJob);
                SpriteRenderer.instance.drawGeneric(this.debugDrawInWorld[mainStateIndex]);
            }
            else {
                this.debugDrawTime = currentTimeMillis;
            }
        }
    }
    
    public void renderUI() {
        if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
            final int n = 512 / Core.TileScale;
            int n2 = 0;
            int n3 = 0;
            for (int i = 0; i < this.AtlasList.size(); ++i) {
                SpriteRenderer.instance.renderi(null, n2, n3, n, n, 1.0f, 1.0f, 1.0f, 0.75f, null);
                SpriteRenderer.instance.renderi(this.AtlasList.get(i).tex, n2, n3, n, n, 1.0f, 1.0f, 1.0f, 1.0f, null);
                final float n4 = n / (float)this.AtlasList.get(i).tex.getWidth();
                for (int j = 0; j < this.AtlasList.get(i).tex.getWidth() / DeadBodyAtlas.ENTRY_WID; ++j) {
                    SpriteRenderer.instance.renderline(null, (int)(n2 + j * DeadBodyAtlas.ENTRY_WID * n4), n3, (int)(n2 + j * DeadBodyAtlas.ENTRY_WID * n4), n3 + n, 0.5f, 0.5f, 0.5f, 1.0f);
                }
                for (int k = 0; k < this.AtlasList.get(i).tex.getHeight() / DeadBodyAtlas.ENTRY_HGT; ++k) {
                    SpriteRenderer.instance.renderline(null, n2, (int)(n3 + k * DeadBodyAtlas.ENTRY_HGT * n4), n2 + n, (int)(n3 + k * DeadBodyAtlas.ENTRY_HGT * n4), 0.5f, 0.5f, 0.5f, 1.0f);
                }
                n3 += n;
                if (n3 + n > Core.getInstance().getScreenHeight()) {
                    n3 = 0;
                    n2 += n;
                }
            }
            SpriteRenderer.instance.renderi(null, n2, n3, n, n, 1.0f, 1.0f, 1.0f, 0.5f, null);
            SpriteRenderer.instance.renderi((Texture)ModelManager.instance.bitmap.getTexture(), n2, n3, n, n, 1.0f, 1.0f, 1.0f, 1.0f, null);
        }
    }
    
    public void Reset() {
        if (this.fbo != null) {
            this.fbo.destroyLeaveTexture();
            this.fbo = null;
        }
        this.AtlasList.forEach(Atlas::Reset);
        this.AtlasList.clear();
        this.EntryMap.clear();
        this.characterTexturesFemale.clear();
        this.characterTexturesMale.clear();
        DeadBodyAtlas.JobPool.forEach(RenderJob::Reset);
        DeadBodyAtlas.JobPool.clear();
        this.RenderJobs.clear();
    }
    
    private void toBodyAtlas(final RenderJob renderJob) {
        GL11.glPushAttrib(2048);
        if (this.fbo.getTexture() != renderJob.entry.atlas.tex) {
            this.fbo.setTexture(renderJob.entry.atlas.tex);
        }
        this.fbo.startDrawing();
        GL11.glViewport(0, 0, this.fbo.getWidth(), this.fbo.getHeight());
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0.0f, (float)renderJob.entry.atlas.tex.getWidth(), (float)renderJob.entry.atlas.tex.getHeight(), 0.0f);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glEnable(3553);
        GL11.glDisable(3089);
        if (renderJob.entry.atlas.clear) {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glClear(16640);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            renderJob.entry.atlas.clear = false;
        }
        if (renderJob.bClearThisSlotOnly) {
            GL11.glEnable(3089);
            GL11.glScissor(renderJob.entry.x, 1024 - renderJob.entry.y - renderJob.entry.h, renderJob.entry.w, renderJob.entry.h);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glClear(16640);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            final int renderingPlayerIndex = SpriteRenderer.instance.getRenderingPlayerIndex();
            final int n = (renderingPlayerIndex == 0 || renderingPlayerIndex == 2) ? 0 : (Core.getInstance().getOffscreenTrueWidth() / 2);
            final int n2 = (renderingPlayerIndex == 0 || renderingPlayerIndex == 1) ? 0 : (Core.getInstance().getOffscreenTrueHeight() / 2);
            int offscreenTrueWidth = Core.getInstance().getOffscreenTrueWidth();
            int offscreenTrueHeight = Core.getInstance().getOffscreenTrueHeight();
            if (IsoPlayer.numPlayers > 1) {
                offscreenTrueWidth /= 2;
            }
            if (IsoPlayer.numPlayers > 2) {
                offscreenTrueHeight /= 2;
            }
            GL11.glScissor(n, n2, offscreenTrueWidth, offscreenTrueHeight);
            GL11.glDisable(3089);
        }
        final int n3 = ModelManager.instance.bitmap.getTexture().getWidth() / 8 * Core.TileScale;
        final int n4 = ModelManager.instance.bitmap.getTexture().getHeight() / 8 * Core.TileScale;
        final int n5 = renderJob.entry.x - (n3 - DeadBodyAtlas.ENTRY_WID) / 2;
        final int n6 = renderJob.entry.y - (n4 - DeadBodyAtlas.ENTRY_HGT) / 2;
        ModelManager.instance.bitmap.getTexture().bind();
        GL11.glBegin(7);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2i(n5, n6);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2i(n5 + n3, n6);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2i(n5 + n3, n6 + n4);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2i(n5, n6 + n4);
        GL11.glEnd();
        GL11.glBindTexture(3553, Texture.lastTextureID = 0);
        this.fbo.endDrawing();
        GL11.glEnable(3089);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        renderJob.entry.ready = true;
        renderJob.done = 1;
    }
    
    static {
        ENTRY_WID = 102 * Core.TileScale;
        ENTRY_HGT = 102 * Core.TileScale;
        instance = new DeadBodyAtlas();
        tempVector2 = new Vector2();
        JobPool = new Stack<RenderJob>();
    }
    
    private static final class AtlasEntry
    {
        public Atlas atlas;
        public String key;
        public String lightKey;
        public int updateCounter;
        public int x;
        public int y;
        public int w;
        public int h;
        public Texture tex;
        public boolean ready;
        
        private AtlasEntry() {
            this.ready = false;
        }
        
        public void Reset() {
            this.atlas = null;
            this.tex.destroy();
            this.tex = null;
            this.ready = false;
        }
    }
    
    private final class Atlas
    {
        public Texture tex;
        public final ArrayList<AtlasEntry> EntryList;
        public boolean clear;
        
        public Atlas(final int n, final int n2) {
            this.EntryList = new ArrayList<AtlasEntry>();
            this.clear = true;
            this.tex = new Texture(n, n2, 16);
            if (DeadBodyAtlas.this.fbo != null) {
                return;
            }
            DeadBodyAtlas.this.fbo = new TextureFBO(this.tex, false);
        }
        
        public boolean isFull() {
            return this.EntryList.size() >= this.tex.getWidth() / DeadBodyAtlas.ENTRY_WID * (this.tex.getHeight() / DeadBodyAtlas.ENTRY_HGT);
        }
        
        public AtlasEntry addBody(final String s) {
            final int n = this.tex.getWidth() / DeadBodyAtlas.ENTRY_WID;
            final int size = this.EntryList.size();
            final int n2 = size % n;
            final int n3 = size / n;
            final AtlasEntry e = new AtlasEntry();
            e.atlas = this;
            e.key = s;
            e.x = n2 * DeadBodyAtlas.ENTRY_WID;
            e.y = n3 * DeadBodyAtlas.ENTRY_HGT;
            e.w = DeadBodyAtlas.ENTRY_WID;
            e.h = DeadBodyAtlas.ENTRY_HGT;
            (e.tex = this.tex.split(s, e.x, this.tex.getHeight() - (e.y + DeadBodyAtlas.ENTRY_HGT), e.w, e.h)).setName(s);
            this.EntryList.add(e);
            return e;
        }
        
        public void Reset() {
            this.EntryList.forEach(AtlasEntry::Reset);
            this.EntryList.clear();
            if (!this.tex.isDestroyed()) {
                RenderThread.invokeOnRenderContext(() -> GL11.glDeleteTextures(this.tex.getID()));
            }
            this.tex = null;
        }
    }
    
    private static final class BodyParams
    {
        HumanVisual humanVisual;
        final ItemVisuals itemVisuals;
        IsoDirections dir;
        float angle;
        boolean bFemale;
        boolean bZombie;
        boolean bSkeleton;
        String animSetName;
        String stateName;
        final HashMap<String, String> variables;
        boolean bStanding;
        String primaryHandItem;
        String secondaryHandItem;
        final AttachedModelNames attachedModelNames;
        float x;
        float y;
        float z;
        float trackTime;
        boolean bOutside;
        boolean bRoom;
        final ColorInfo ambient;
        boolean fallOnFront;
        final IsoGridSquare.ResultLight[] lights;
        
        BodyParams() {
            this.itemVisuals = new ItemVisuals();
            this.variables = new HashMap<String, String>();
            this.attachedModelNames = new AttachedModelNames();
            this.ambient = new ColorInfo();
            this.fallOnFront = false;
            this.lights = new IsoGridSquare.ResultLight[5];
            for (int i = 0; i < this.lights.length; ++i) {
                this.lights[i] = new IsoGridSquare.ResultLight();
            }
        }
        
        void init(final BodyParams bodyParams) {
            this.humanVisual = bodyParams.humanVisual;
            this.itemVisuals.clear();
            this.itemVisuals.addAll(bodyParams.itemVisuals);
            this.dir = bodyParams.dir;
            this.angle = bodyParams.angle;
            this.bFemale = bodyParams.bFemale;
            this.bZombie = bodyParams.bZombie;
            this.bSkeleton = bodyParams.bSkeleton;
            this.animSetName = bodyParams.animSetName;
            this.stateName = bodyParams.stateName;
            this.variables.clear();
            this.variables.putAll(bodyParams.variables);
            this.bStanding = bodyParams.bStanding;
            this.primaryHandItem = bodyParams.primaryHandItem;
            this.secondaryHandItem = bodyParams.secondaryHandItem;
            this.attachedModelNames.copyFrom(bodyParams.attachedModelNames);
            this.x = bodyParams.x;
            this.y = bodyParams.y;
            this.z = bodyParams.z;
            this.trackTime = bodyParams.trackTime;
            this.fallOnFront = bodyParams.fallOnFront;
            this.bOutside = bodyParams.bOutside;
            this.bRoom = bodyParams.bRoom;
            this.ambient.set(bodyParams.ambient.r, bodyParams.ambient.g, bodyParams.ambient.b, 1.0f);
            for (int i = 0; i < this.lights.length; ++i) {
                this.lights[i].copyFrom(bodyParams.lights[i]);
            }
        }
        
        void init(final IsoDeadBody isoDeadBody) {
            this.humanVisual = isoDeadBody.getHumanVisual();
            isoDeadBody.getItemVisuals(this.itemVisuals);
            this.dir = isoDeadBody.dir;
            this.angle = isoDeadBody.getAngle();
            this.bFemale = isoDeadBody.isFemale();
            this.bZombie = isoDeadBody.isZombie();
            this.bSkeleton = isoDeadBody.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.initFrom(isoDeadBody.getAttachedItems());
            this.animSetName = "zombie";
            this.stateName = "onground";
            this.variables.clear();
            this.bStanding = false;
            if (isoDeadBody.getPrimaryHandItem() != null || isoDeadBody.getSecondaryHandItem() != null) {
                if (isoDeadBody.getPrimaryHandItem() != null && !StringUtils.isNullOrEmpty(isoDeadBody.getPrimaryHandItem().getStaticModel())) {
                    this.primaryHandItem = isoDeadBody.getPrimaryHandItem().getStaticModel();
                }
                if (isoDeadBody.getSecondaryHandItem() != null && !StringUtils.isNullOrEmpty(isoDeadBody.getSecondaryHandItem().getStaticModel())) {
                    this.secondaryHandItem = isoDeadBody.getSecondaryHandItem().getStaticModel();
                }
                this.animSetName = "player";
                this.stateName = "deadbody";
            }
            this.x = isoDeadBody.x;
            this.y = isoDeadBody.y;
            this.z = isoDeadBody.z;
            this.trackTime = 0.0f;
            this.fallOnFront = isoDeadBody.isFallOnFront();
            this.bOutside = (isoDeadBody.square != null && isoDeadBody.square.isOutside());
            this.bRoom = (isoDeadBody.square != null && isoDeadBody.square.getRoom() != null);
            this.initAmbient(isoDeadBody.square);
            this.initLights(isoDeadBody.square);
        }
        
        void init(final IsoZombie isoZombie) {
            this.humanVisual = isoZombie.getHumanVisual();
            isoZombie.getItemVisuals(this.itemVisuals);
            this.dir = isoZombie.dir;
            this.angle = isoZombie.getAnimAngleRadians();
            this.bFemale = isoZombie.isFemale();
            this.bZombie = true;
            this.bSkeleton = isoZombie.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.initFrom(isoZombie.getAttachedItems());
            this.animSetName = "zombie";
            this.stateName = "onground";
            this.variables.clear();
            this.bStanding = false;
            this.x = isoZombie.x;
            this.y = isoZombie.y;
            this.z = isoZombie.z;
            this.trackTime = 0.0f;
            this.fallOnFront = isoZombie.isFallOnFront();
            this.bOutside = (isoZombie.getCurrentSquare() != null && isoZombie.getCurrentSquare().isOutside());
            this.bRoom = (isoZombie.getCurrentSquare() != null && isoZombie.getCurrentSquare().getRoom() != null);
            this.initAmbient(isoZombie.getCurrentSquare());
            this.initLights(isoZombie.getCurrentSquare());
        }
        
        void init(final IsoMannequin isoMannequin) {
            this.humanVisual = isoMannequin.getHumanVisual();
            isoMannequin.getItemVisuals(this.itemVisuals);
            this.dir = isoMannequin.dir;
            this.angle = this.dir.ToVector().getDirection();
            this.bFemale = isoMannequin.isFemale();
            this.bZombie = isoMannequin.isZombie();
            this.bSkeleton = isoMannequin.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.clear();
            this.animSetName = "mannequin";
            this.stateName = (isoMannequin.isFemale() ? "female" : "male");
            this.variables.clear();
            isoMannequin.getVariables(this.variables);
            this.bStanding = true;
            this.x = isoMannequin.getX();
            this.y = isoMannequin.getY();
            this.z = isoMannequin.getZ();
            this.trackTime = 0.0f;
            this.fallOnFront = false;
            this.bOutside = (isoMannequin.square != null && isoMannequin.square.isOutside());
            this.bRoom = (isoMannequin.square != null && isoMannequin.square.getRoom() != null);
            this.initAmbient(isoMannequin.square);
            this.initLights(null);
        }
        
        void init(final IHumanVisual humanVisual, final IsoDirections dir, final String animSetName, final String stateName, final float trackTime) {
            this.humanVisual = humanVisual.getHumanVisual();
            humanVisual.getItemVisuals(this.itemVisuals);
            this.dir = dir;
            this.angle = dir.ToVector().getDirection();
            this.bFemale = humanVisual.isFemale();
            this.bZombie = humanVisual.isZombie();
            this.bSkeleton = humanVisual.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.clear();
            this.animSetName = animSetName;
            this.stateName = stateName;
            this.variables.clear();
            this.bStanding = true;
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.trackTime = trackTime;
            this.fallOnFront = false;
            this.bOutside = true;
            this.bRoom = false;
            this.ambient.set(1.0f, 1.0f, 1.0f, 1.0f);
            this.initLights(null);
        }
        
        void initAmbient(final IsoGridSquare isoGridSquare) {
            this.ambient.set(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        void initLights(final IsoGridSquare isoGridSquare) {
            for (int i = 0; i < this.lights.length; ++i) {
                this.lights[i].radius = 0;
            }
            if (isoGridSquare != null) {
                final IsoGridSquare.ILighting lighting = isoGridSquare.lighting[0];
                for (int resultLightCount = lighting.resultLightCount(), j = 0; j < resultLightCount; ++j) {
                    this.lights[j].copyFrom(lighting.getResultLight(j));
                }
            }
        }
        
        void Reset() {
            this.humanVisual = null;
            this.itemVisuals.clear();
            Arrays.fill(this.lights, null);
        }
    }
    
    private static final class CharacterTextureVisual implements IHumanVisual
    {
        final HumanVisual humanVisual;
        boolean bFemale;
        
        CharacterTextureVisual(final boolean bFemale) {
            this.humanVisual = new HumanVisual(this);
            this.bFemale = bFemale;
            this.humanVisual.setHairModel("");
            this.humanVisual.setBeardModel("");
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
            return true;
        }
        
        @Override
        public boolean isSkeleton() {
            return false;
        }
    }
    
    private static final class Checksummer
    {
        private MessageDigest md;
        private final StringBuilder sb;
        
        private Checksummer() {
            this.sb = new StringBuilder();
        }
        
        public void reset() throws NoSuchAlgorithmException {
            if (this.md == null) {
                this.md = MessageDigest.getInstance("MD5");
            }
            this.md.reset();
        }
        
        public void update(final byte input) {
            this.md.update(input);
        }
        
        public void update(final boolean b) {
            this.md.update((byte)(b ? 1 : 0));
        }
        
        public void update(final int n) {
            this.md.update((byte)(n & 0xFF));
            this.md.update((byte)(n >> 8 & 0xFF));
            this.md.update((byte)(n >> 16 & 0xFF));
            this.md.update((byte)(n >> 24 & 0xFF));
        }
        
        public void update(final String s) {
            if (s == null || s.isEmpty()) {
                return;
            }
            this.md.update(s.getBytes());
        }
        
        public void update(final ImmutableColor immutableColor) {
            this.update((byte)(immutableColor.r * 255.0f));
            this.update((byte)(immutableColor.g * 255.0f));
            this.update((byte)(immutableColor.b * 255.0f));
        }
        
        public void update(final IsoGridSquare.ResultLight resultLight, final float n, final float n2, final float n3) {
            if (resultLight == null || resultLight.radius <= 0) {
                return;
            }
            this.update((int)(resultLight.x - n));
            this.update((int)(resultLight.y - n2));
            this.update((int)(resultLight.z - n3));
            this.update((byte)(resultLight.r * 255.0f));
            this.update((byte)(resultLight.g * 255.0f));
            this.update((byte)(resultLight.b * 255.0f));
            this.update((byte)resultLight.radius);
        }
        
        public String checksumToString() {
            final byte[] digest = this.md.digest();
            this.sb.setLength(0);
            for (int i = 0; i < digest.length; ++i) {
                this.sb.append(digest[i] & 0xFF);
            }
            return this.sb.toString();
        }
    }
    
    private static final class ClearAtlasTexture extends TextureDraw.GenericDrawer
    {
        Atlas m_atlas;
        
        ClearAtlasTexture(final Atlas atlas) {
            this.m_atlas = atlas;
        }
        
        @Override
        public void render() {
            final TextureFBO fbo = DeadBodyAtlas.instance.fbo;
            if (fbo == null || this.m_atlas.tex == null) {
                return;
            }
            if (!this.m_atlas.clear) {
                return;
            }
            if (fbo.getTexture() != this.m_atlas.tex) {
                fbo.setTexture(this.m_atlas.tex);
            }
            fbo.startDrawing(false, false);
            GL11.glPushAttrib(2048);
            GL11.glViewport(0, 0, fbo.getWidth(), fbo.getHeight());
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GLU.gluOrtho2D(0.0f, (float)this.m_atlas.tex.getWidth(), (float)this.m_atlas.tex.getHeight(), 0.0f);
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glDisable(3089);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glClear(16640);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            fbo.endDrawing();
            GL11.glEnable(3089);
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
            this.m_atlas.clear = false;
        }
    }
    
    private static final class RenderJob extends TextureDraw.GenericDrawer
    {
        public final BodyParams body;
        public AtlasEntry entry;
        public AnimatedModel animatedModel;
        public float m_animPlayerAngle;
        public int done;
        public int renderRefCount;
        public boolean bClearThisSlotOnly;
        
        private RenderJob() {
            this.body = new BodyParams();
            this.done = 0;
        }
        
        public static RenderJob getNew() {
            if (DeadBodyAtlas.JobPool.isEmpty()) {
                return new RenderJob();
            }
            return DeadBodyAtlas.JobPool.pop();
        }
        
        public RenderJob init(final BodyParams bodyParams, final AtlasEntry entry) {
            this.body.init(bodyParams);
            this.entry = entry;
            if (this.animatedModel == null) {
                (this.animatedModel = new AnimatedModel()).setAnimate(false);
            }
            if (bodyParams.bStanding) {
                this.animatedModel.setOffset(0.0f, -0.45f, 0.0f);
            }
            else {
                this.animatedModel.setOffset(0.0f, 0.0f, 0.0f);
            }
            this.animatedModel.setAnimSetName(bodyParams.animSetName);
            this.animatedModel.setState(bodyParams.stateName);
            this.animatedModel.setPrimaryHandModelName(bodyParams.primaryHandItem);
            this.animatedModel.setSecondaryHandModelName(bodyParams.secondaryHandItem);
            this.animatedModel.setAttachedModelNames(bodyParams.attachedModelNames);
            this.animatedModel.setAmbient(bodyParams.ambient, bodyParams.bOutside, bodyParams.bRoom);
            this.animatedModel.setLights(bodyParams.lights, bodyParams.x, bodyParams.y, bodyParams.z);
            this.animatedModel.setModelData(bodyParams.humanVisual, bodyParams.itemVisuals);
            this.animatedModel.setAngle(DeadBodyAtlas.tempVector2.setLengthAndDirection(bodyParams.angle, 1.0f));
            this.animatedModel.setVariable("FallOnFront", bodyParams.fallOnFront);
            bodyParams.variables.forEach((s, s2) -> this.animatedModel.setVariable(s, s2));
            this.animatedModel.setTrackTime(bodyParams.trackTime);
            this.animatedModel.update();
            this.bClearThisSlotOnly = false;
            this.done = 0;
            this.renderRefCount = 0;
            return this;
        }
        
        public boolean renderMain() {
            if (this.animatedModel.isReadyToRender()) {
                this.animatedModel.renderMain();
                this.m_animPlayerAngle = this.animatedModel.getAnimationPlayer().getRenderedAngle();
                return true;
            }
            return false;
        }
        
        @Override
        public void render() {
            if (this.done == 1) {
                return;
            }
            GL11.glDepthMask(true);
            GL11.glColorMask(true, true, true, true);
            GL11.glDisable(3089);
            GL11.glPushAttrib(2048);
            ModelManager.instance.bitmap.startDrawing(true, true);
            GL11.glViewport(0, 0, ModelManager.instance.bitmap.getWidth(), ModelManager.instance.bitmap.getHeight());
            this.animatedModel.DoRender(0, 0, ModelManager.instance.bitmap.getTexture().getWidth(), ModelManager.instance.bitmap.getTexture().getHeight(), 42.75f, this.m_animPlayerAngle);
            ModelManager.instance.bitmap.endDrawing();
            GL11.glPopAttrib();
            if (!this.animatedModel.isRendered()) {
                return;
            }
            DeadBodyAtlas.instance.toBodyAtlas(this);
        }
        
        @Override
        public void postRender() {
            this.animatedModel.postRender(this.done == 1);
            assert this.renderRefCount > 0;
            --this.renderRefCount;
        }
        
        public void Reset() {
            this.body.Reset();
            this.entry = null;
            if (this.animatedModel != null) {
                this.animatedModel.releaseAnimationPlayer();
                this.animatedModel = null;
            }
        }
    }
    
    private static final class DebugDrawInWorld extends TextureDraw.GenericDrawer
    {
        RenderJob job;
        boolean bRendered;
        
        public void init(final RenderJob job) {
            this.job = job;
            this.bRendered = false;
        }
        
        @Override
        public void render() {
            this.job.animatedModel.DoRenderToWorld(this.job.body.x, this.job.body.y, this.job.body.z, this.job.m_animPlayerAngle);
            this.bRendered = true;
        }
        
        @Override
        public void postRender() {
            if (this.bRendered) {
                assert !DeadBodyAtlas.JobPool.contains(this.job);
                DeadBodyAtlas.JobPool.push(this.job);
            }
            else {
                assert !DeadBodyAtlas.JobPool.contains(this.job);
                DeadBodyAtlas.JobPool.push(this.job);
            }
            this.job.animatedModel.postRender(this.bRendered);
        }
    }
}
