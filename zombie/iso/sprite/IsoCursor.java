// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import org.lwjgl.opengl.GL11;
import zombie.core.opengl.Shader;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoCamera;
import zombie.input.Mouse;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.core.opengl.RenderThread;

public final class IsoCursor
{
    private static IsoCursor instance;
    IsoCursorShader m_shader;
    
    public static IsoCursor getInstance() {
        if (IsoCursor.instance == null) {
            IsoCursor.instance = new IsoCursor();
        }
        return IsoCursor.instance;
    }
    
    private IsoCursor() {
        this.m_shader = null;
        RenderThread.invokeOnRenderContext(this::createShader);
        if (this.m_shader != null) {
            this.m_shader.m_textureCursor = Texture.getSharedTexture("media/ui/isocursor.png");
        }
    }
    
    private void createShader() {
        this.m_shader = new IsoCursorShader();
    }
    
    public void render(final int n) {
        if (Core.getInstance().getOffscreenBuffer() == null) {
            return;
        }
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer == null || isoPlayer.isDead() || !isoPlayer.isAiming() || isoPlayer.PlayerIndex != 0 || isoPlayer.JoypadBind != -1) {
            return;
        }
        if (GameTime.isGamePaused()) {
            return;
        }
        if (this.m_shader == null || !this.m_shader.isCompiled()) {
            return;
        }
        final float n2 = 1.0f / Core.getInstance().getZoom(n);
        final int width = (int)(this.m_shader.m_textureCursor.getWidth() * Core.TileScale / 2.0f * n2);
        final int height = (int)(this.m_shader.m_textureCursor.getHeight() * Core.TileScale / 2.0f * n2);
        this.m_shader.m_screenX = Mouse.getXA() - width / 2;
        this.m_shader.m_screenY = Mouse.getYA() - height / 2;
        this.m_shader.width = width;
        this.m_shader.height = height;
        final int screenLeft = IsoCamera.getScreenLeft(n);
        final int screenTop = IsoCamera.getScreenTop(n);
        final int screenWidth = IsoCamera.getScreenWidth(n);
        final int screenHeight = IsoCamera.getScreenHeight(n);
        SpriteRenderer.instance.StartShader(this.m_shader.getID(), n);
        SpriteRenderer.instance.renderClamped(this.m_shader.m_textureCursor, this.m_shader.m_screenX, this.m_shader.m_screenY, width, height, screenLeft, screenTop, screenWidth, screenHeight, 1.0f, 1.0f, 1.0f, 1.0f, this.m_shader);
        SpriteRenderer.instance.EndShader();
    }
    
    static {
        IsoCursor.instance = null;
    }
    
    private static class IsoCursorShader extends Shader implements Consumer<TextureDraw>
    {
        private float m_alpha;
        private Texture m_textureCursor;
        private Texture m_textureWorld;
        private int m_screenX;
        private int m_screenY;
        
        IsoCursorShader() {
            super("isocursor");
            this.m_alpha = 1.0f;
        }
        
        @Override
        public void startMainThread(final TextureDraw textureDraw, final int n) {
            this.m_alpha = this.calculateAlpha();
            this.m_textureWorld = Core.getInstance().OffscreenBuffer.getTexture(n);
        }
        
        @Override
        public void startRenderThread(final TextureDraw textureDraw) {
            this.getProgram().setValue("u_alpha", this.m_alpha);
            this.getProgram().setValue("TextureCursor", this.m_textureCursor, 0);
            this.getProgram().setValue("TextureBackground", this.m_textureWorld, 1);
            SpriteRenderer.ringBuffer.shaderChangedTexture1();
            GL11.glEnable(3042);
        }
        
        @Override
        public void accept(final TextureDraw textureDraw) {
            final int n = 0;
            final int n2 = (int)textureDraw.x0 - this.m_screenX;
            final int n3 = (int)textureDraw.y0 - this.m_screenY;
            final int n4 = this.m_screenX + this.width - (int)textureDraw.x2;
            final int n5 = this.m_screenY + this.height - (int)textureDraw.y2;
            this.m_screenX += n2;
            this.m_screenY += n3;
            this.width -= n2 + n4;
            this.height -= n3 + n5;
            final float n6 = (float)this.m_textureWorld.getWidthHW();
            final float n7 = (float)this.m_textureWorld.getHeightHW();
            final float n8 = (float)(IsoCamera.getScreenTop(n) + IsoCamera.getScreenHeight(n) - (this.m_screenY + this.height));
            textureDraw.tex1 = this.m_textureWorld;
            textureDraw.tex1_u0 = this.m_screenX / n6;
            textureDraw.tex1_v3 = n8 / n7;
            textureDraw.tex1_u1 = (this.m_screenX + this.width) / n6;
            textureDraw.tex1_v2 = n8 / n7;
            textureDraw.tex1_u2 = (this.m_screenX + this.width) / n6;
            textureDraw.tex1_v1 = (n8 + this.height) / n7;
            textureDraw.tex1_u3 = this.m_screenX / n6;
            textureDraw.tex1_v0 = (n8 + this.height) / n7;
        }
        
        float calculateAlpha() {
            float n = 0.05f;
            switch (Core.getInstance().getIsoCursorVisibility()) {
                case 0: {
                    n = 0.0f;
                    break;
                }
                case 1: {
                    n = 0.05f;
                    break;
                }
                case 2: {
                    n = 0.1f;
                    break;
                }
                case 3: {
                    n = 0.15f;
                    break;
                }
                case 4: {
                    n = 0.3f;
                    break;
                }
                case 5: {
                    n = 0.5f;
                    break;
                }
                case 6: {
                    n = 0.75f;
                    break;
                }
            }
            return n;
        }
    }
}
