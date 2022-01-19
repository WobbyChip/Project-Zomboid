// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.ui.UIManager;
import zombie.Lua.LuaManager;
import zombie.GameTime;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.IsoGameCharacter;

public class IsoLuaMover extends IsoGameCharacter
{
    public KahluaTable luaMoverTable;
    
    public IsoLuaMover(final KahluaTable luaMoverTable) {
        super(null, 0.0f, 0.0f, 0.0f);
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        this.luaMoverTable = luaMoverTable;
        if (this.def == null) {
            this.def = IsoSpriteInstance.get(this.sprite);
        }
    }
    
    public void playAnim(final String s, final float n, final boolean looped, final boolean b) {
        this.sprite.PlayAnim(s);
        this.def.AnimFrameIncrease = 1000.0f / this.sprite.CurrentAnim.Frames.size() * n * GameTime.getInstance().getMultiplier();
        this.def.Finished = !b;
        this.def.Looped = looped;
    }
    
    @Override
    public String getObjectName() {
        return "IsoLuaMover";
    }
    
    @Override
    public void update() {
        try {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget((Object)"update"), (Object)this.luaMoverTable);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.sprite.update(this.def);
        super.update();
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        this.sprite.render(this.def, this, this.x, this.y, this.z, this.dir, this.offsetX - 34.0f, this.offsetY - 100.0f, colorInfo, true);
        try {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget((Object)"postrender"), (Object)this.luaMoverTable, (Object)colorInfo, (Object)b);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
