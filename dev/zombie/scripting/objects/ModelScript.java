// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import zombie.network.GameServer;
import zombie.debug.DebugLog;
import zombie.ZomboidFileSystem;
import java.util.Locale;
import zombie.util.StringUtils;
import org.joml.Vector3f;
import java.util.Iterator;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.scripting.ScriptManager;
import java.util.HashSet;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.core.skinnedmodel.model.Model;
import java.util.ArrayList;

public final class ModelScript extends BaseScriptObject
{
    public static final String DEFAULT_SHADER_NAME = "basicEffect";
    public String fileName;
    public String name;
    public String meshName;
    public String textureName;
    public String shaderName;
    public boolean bStatic;
    public float scale;
    public final ArrayList<ModelAttachment> m_attachments;
    public boolean invertX;
    public Model loadedModel;
    public final ArrayList<AnimBoneWeight> boneWeights;
    private static final HashSet<String> reported;
    
    public ModelScript() {
        this.bStatic = true;
        this.scale = 1.0f;
        this.m_attachments = new ArrayList<ModelAttachment>();
        this.invertX = false;
        this.boneWeights = new ArrayList<AnimBoneWeight>();
    }
    
    public void Load(final String name, final String s) {
        this.fileName = ScriptManager.instance.currentFileName;
        this.name = name;
        final ScriptParser.Block block = ScriptParser.parse(s).children.get(0);
        for (final ScriptParser.Block block2 : block.children) {
            if ("attachment".equals(block2.type)) {
                this.LoadAttachment(block2);
            }
        }
        final Iterator<ScriptParser.Value> iterator2 = block.values.iterator();
        while (iterator2.hasNext()) {
            final String[] split = iterator2.next().string.split("=");
            final String trim = split[0].trim();
            final String trim2 = split[1].trim();
            if ("mesh".equalsIgnoreCase(trim)) {
                this.meshName = trim2;
            }
            else if ("scale".equalsIgnoreCase(trim)) {
                this.scale = Float.parseFloat(trim2);
            }
            else if ("shader".equalsIgnoreCase(trim)) {
                this.shaderName = trim2;
            }
            else if ("static".equalsIgnoreCase(trim)) {
                this.bStatic = Boolean.parseBoolean(trim2);
            }
            else if ("texture".equalsIgnoreCase(trim)) {
                this.textureName = trim2;
            }
            else if ("invertX".equalsIgnoreCase(trim)) {
                this.invertX = Boolean.parseBoolean(trim2);
            }
            else {
                if (!"boneWeight".equalsIgnoreCase(trim)) {
                    continue;
                }
                final String[] split2 = trim2.split("\\s+");
                if (split2.length != 2) {
                    continue;
                }
                final AnimBoneWeight e = new AnimBoneWeight(split2[0], PZMath.tryParseFloat(split2[1], 1.0f));
                e.includeDescendants = false;
                this.boneWeights.add(e);
            }
        }
    }
    
    private ModelAttachment LoadAttachment(final ScriptParser.Block block) {
        ModelAttachment attachmentById = this.getAttachmentById(block.id);
        if (attachmentById == null) {
            attachmentById = new ModelAttachment(block.id);
            this.m_attachments.add(attachmentById);
        }
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("bone".equals(trim)) {
                attachmentById.setBone(trim2);
            }
            else if ("offset".equals(trim)) {
                this.LoadVector3f(trim2, attachmentById.getOffset());
            }
            else {
                if (!"rotate".equals(trim)) {
                    continue;
                }
                this.LoadVector3f(trim2, attachmentById.getRotate());
            }
        }
        return attachmentById;
    }
    
    private void LoadVector3f(final String s, final Vector3f vector3f) {
        final String[] split = s.split(" ");
        vector3f.set(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFullType() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module.name, this.name);
    }
    
    public String getMeshName() {
        return this.meshName;
    }
    
    public String getTextureName() {
        if (StringUtils.isNullOrWhitespace(this.textureName)) {
            return this.meshName;
        }
        return this.textureName;
    }
    
    public String getTextureName(final boolean b) {
        if (StringUtils.isNullOrWhitespace(this.textureName) && !b) {
            return this.meshName;
        }
        return this.textureName;
    }
    
    public String getShaderName() {
        if (StringUtils.isNullOrWhitespace(this.shaderName)) {
            return "basicEffect";
        }
        return this.shaderName;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public int getAttachmentCount() {
        return this.m_attachments.size();
    }
    
    public ModelAttachment getAttachment(final int index) {
        return this.m_attachments.get(index);
    }
    
    public ModelAttachment getAttachmentById(final String anObject) {
        for (int i = 0; i < this.m_attachments.size(); ++i) {
            final ModelAttachment modelAttachment = this.m_attachments.get(i);
            if (modelAttachment.getId().equals(anObject)) {
                return modelAttachment;
            }
        }
        return null;
    }
    
    public ModelAttachment addAttachment(final ModelAttachment e) {
        this.m_attachments.add(e);
        return e;
    }
    
    public ModelAttachment removeAttachment(final ModelAttachment o) {
        this.m_attachments.remove(o);
        return o;
    }
    
    public ModelAttachment addAttachmentAt(final int index, final ModelAttachment element) {
        this.m_attachments.add(index, element);
        return element;
    }
    
    public ModelAttachment removeAttachment(final int index) {
        return this.m_attachments.remove(index);
    }
    
    public void reset() {
        this.invertX = false;
        this.name = null;
        this.meshName = null;
        this.textureName = null;
        this.shaderName = null;
        this.bStatic = true;
        this.scale = 1.0f;
        this.boneWeights.clear();
    }
    
    private static void checkMesh(final String s, final String e) {
        if (StringUtils.isNullOrWhitespace(e)) {
            return;
        }
        final String lowerCase = e.toLowerCase(Locale.ENGLISH);
        if (!ZomboidFileSystem.instance.ActiveFileMap.containsKey(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lowerCase)) && !ZomboidFileSystem.instance.ActiveFileMap.containsKey(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lowerCase)) && !ZomboidFileSystem.instance.ActiveFileMap.containsKey(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lowerCase))) {
            ModelScript.reported.add(e);
            DebugLog.Script.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, e, s));
        }
    }
    
    private static void checkTexture(final String s, final String e) {
        if (GameServer.bServer) {
            return;
        }
        if (StringUtils.isNullOrWhitespace(e)) {
            return;
        }
        if (!ZomboidFileSystem.instance.ActiveFileMap.containsKey(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.toLowerCase(Locale.ENGLISH)))) {
            ModelScript.reported.add(e);
            DebugLog.Script.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, e, s));
        }
    }
    
    private static void check(final String s, final String s2) {
        if (StringUtils.isNullOrWhitespace(s2)) {
            return;
        }
        if (ModelScript.reported.contains(s2)) {
            return;
        }
        final ModelScript modelScript = ScriptManager.instance.getModelScript(s2);
        if (modelScript == null) {
            ModelScript.reported.add(s2);
            DebugLog.Script.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s));
        }
        else {
            checkMesh(modelScript.getFullType(), modelScript.getMeshName());
            checkTexture(modelScript.getFullType(), modelScript.getTextureName());
        }
    }
    
    public static void ScriptsLoaded() {
        ModelScript.reported.clear();
        for (final Item item : ScriptManager.instance.getAllItems()) {
            check(item.getFullName(), item.getStaticModel());
            check(item.getFullName(), item.getWeaponSprite());
        }
        for (final Recipe recipe : ScriptManager.instance.getAllRecipes()) {
            if (recipe.getProp1() != null && !recipe.getProp1().startsWith("Source=")) {
                check(recipe.getFullType(), recipe.getProp1());
            }
            if (recipe.getProp2() != null && !recipe.getProp2().startsWith("Source=")) {
                check(recipe.getFullType(), recipe.getProp2());
            }
        }
    }
    
    static {
        reported = new HashSet<String>();
    }
}
