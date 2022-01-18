// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fog;

import zombie.input.GameKeyboard;
import zombie.IndieGL;
import zombie.core.SpriteRenderer;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.PlayerCamera;
import zombie.core.math.PZMath;
import zombie.iso.IsoWorld;
import zombie.iso.IsoUtils;
import zombie.iso.IsoCamera;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.GameTime;
import zombie.core.PerformanceSettings;
import zombie.iso.weather.fx.SteppedUpdateFloat;
import zombie.core.textures.Texture;
import zombie.iso.weather.ClimateManager;
import zombie.iso.IsoChunkMap;
import org.joml.Vector2i;

public class ImprovedFog
{
    private static final RectangleIterator rectangleIter;
    private static final Vector2i rectangleMatrixPos;
    private static IsoChunkMap chunkMap;
    private static int minY;
    private static int maxY;
    private static int minX;
    private static int maxX;
    private static int zLayer;
    private static Vector2i lastIterPos;
    private static FogRectangle fogRectangle;
    private static boolean drawingThisLayer;
    private static float ZOOM;
    private static int PlayerIndex;
    private static int playerRow;
    private static float screenWidth;
    private static float screenHeight;
    private static float worldOffsetX;
    private static float worldOffsetY;
    private static float topAlphaHeight;
    private static float bottomAlphaHeight;
    private static float secondLayerAlpha;
    private static float scalingX;
    private static float scalingY;
    private static float colorR;
    private static float colorG;
    private static float colorB;
    private static boolean drawDebugColors;
    private static float octaves;
    private static boolean highQuality;
    private static boolean enableEditing;
    private static float alphaCircleAlpha;
    private static float alphaCircleRad;
    private static int lastRow;
    private static ClimateManager climateManager;
    private static Texture noiseTexture;
    private static boolean renderOnlyOneRow;
    private static float baseAlpha;
    private static int renderEveryXRow;
    private static int renderXRowsFromCenter;
    private static boolean renderCurrentLayerOnly;
    private static float rightClickOffX;
    private static float rightClickOffY;
    private static float cameraOffscreenLeft;
    private static float cameraOffscreenTop;
    private static float cameraZoom;
    private static int minXOffset;
    private static int maxXOffset;
    private static int maxYOffset;
    private static boolean renderEndOnly;
    private static final SteppedUpdateFloat fogIntensity;
    private static int keyPause;
    private static final float[] offsets;
    
    public static int getMinXOffset() {
        return ImprovedFog.minXOffset;
    }
    
    public static void setMinXOffset(final int minXOffset) {
        ImprovedFog.minXOffset = minXOffset;
    }
    
    public static int getMaxXOffset() {
        return ImprovedFog.maxXOffset;
    }
    
    public static void setMaxXOffset(final int maxXOffset) {
        ImprovedFog.maxXOffset = maxXOffset;
    }
    
    public static int getMaxYOffset() {
        return ImprovedFog.maxYOffset;
    }
    
    public static void setMaxYOffset(final int maxYOffset) {
        ImprovedFog.maxYOffset = maxYOffset;
    }
    
    public static boolean isRenderEndOnly() {
        return ImprovedFog.renderEndOnly;
    }
    
    public static void setRenderEndOnly(final boolean renderEndOnly) {
        ImprovedFog.renderEndOnly = renderEndOnly;
    }
    
    public static float getAlphaCircleAlpha() {
        return ImprovedFog.alphaCircleAlpha;
    }
    
    public static void setAlphaCircleAlpha(final float alphaCircleAlpha) {
        ImprovedFog.alphaCircleAlpha = alphaCircleAlpha;
    }
    
    public static float getAlphaCircleRad() {
        return ImprovedFog.alphaCircleRad;
    }
    
    public static void setAlphaCircleRad(final float alphaCircleRad) {
        ImprovedFog.alphaCircleRad = alphaCircleRad;
    }
    
    public static boolean isHighQuality() {
        return ImprovedFog.highQuality;
    }
    
    public static void setHighQuality(final boolean highQuality) {
        ImprovedFog.highQuality = highQuality;
    }
    
    public static boolean isEnableEditing() {
        return ImprovedFog.enableEditing;
    }
    
    public static void setEnableEditing(final boolean enableEditing) {
        ImprovedFog.enableEditing = enableEditing;
    }
    
    public static float getTopAlphaHeight() {
        return ImprovedFog.topAlphaHeight;
    }
    
    public static void setTopAlphaHeight(final float topAlphaHeight) {
        ImprovedFog.topAlphaHeight = topAlphaHeight;
    }
    
    public static float getBottomAlphaHeight() {
        return ImprovedFog.bottomAlphaHeight;
    }
    
    public static void setBottomAlphaHeight(final float bottomAlphaHeight) {
        ImprovedFog.bottomAlphaHeight = bottomAlphaHeight;
    }
    
    public static boolean isDrawDebugColors() {
        return ImprovedFog.drawDebugColors;
    }
    
    public static void setDrawDebugColors(final boolean drawDebugColors) {
        ImprovedFog.drawDebugColors = drawDebugColors;
    }
    
    public static float getOctaves() {
        return ImprovedFog.octaves;
    }
    
    public static void setOctaves(final float octaves) {
        ImprovedFog.octaves = octaves;
    }
    
    public static float getColorR() {
        return ImprovedFog.colorR;
    }
    
    public static void setColorR(final float colorR) {
        ImprovedFog.colorR = colorR;
    }
    
    public static float getColorG() {
        return ImprovedFog.colorG;
    }
    
    public static void setColorG(final float colorG) {
        ImprovedFog.colorG = colorG;
    }
    
    public static float getColorB() {
        return ImprovedFog.colorB;
    }
    
    public static void setColorB(final float colorB) {
        ImprovedFog.colorB = colorB;
    }
    
    public static float getSecondLayerAlpha() {
        return ImprovedFog.secondLayerAlpha;
    }
    
    public static void setSecondLayerAlpha(final float secondLayerAlpha) {
        ImprovedFog.secondLayerAlpha = secondLayerAlpha;
    }
    
    public static float getScalingX() {
        return ImprovedFog.scalingX;
    }
    
    public static void setScalingX(final float scalingX) {
        ImprovedFog.scalingX = scalingX;
    }
    
    public static float getScalingY() {
        return ImprovedFog.scalingY;
    }
    
    public static void setScalingY(final float scalingY) {
        ImprovedFog.scalingY = scalingY;
    }
    
    public static boolean isRenderOnlyOneRow() {
        return ImprovedFog.renderOnlyOneRow;
    }
    
    public static void setRenderOnlyOneRow(final boolean renderOnlyOneRow) {
        ImprovedFog.renderOnlyOneRow = renderOnlyOneRow;
    }
    
    public static float getBaseAlpha() {
        return ImprovedFog.baseAlpha;
    }
    
    public static void setBaseAlpha(final float baseAlpha) {
        ImprovedFog.baseAlpha = baseAlpha;
    }
    
    public static int getRenderEveryXRow() {
        return ImprovedFog.renderEveryXRow;
    }
    
    public static void setRenderEveryXRow(final int renderEveryXRow) {
        ImprovedFog.renderEveryXRow = renderEveryXRow;
    }
    
    public static boolean isRenderCurrentLayerOnly() {
        return ImprovedFog.renderCurrentLayerOnly;
    }
    
    public static void setRenderCurrentLayerOnly(final boolean renderCurrentLayerOnly) {
        ImprovedFog.renderCurrentLayerOnly = renderCurrentLayerOnly;
    }
    
    public static int getRenderXRowsFromCenter() {
        return ImprovedFog.renderXRowsFromCenter;
    }
    
    public static void setRenderXRowsFromCenter(final int renderXRowsFromCenter) {
        ImprovedFog.renderXRowsFromCenter = renderXRowsFromCenter;
    }
    
    public static void update() {
        updateKeys();
        if (ImprovedFog.noiseTexture == null) {
            ImprovedFog.noiseTexture = Texture.getSharedTexture("media/textures/weather/fognew/fog_noise.png");
        }
        ImprovedFog.climateManager = ClimateManager.getInstance();
        if (!ImprovedFog.enableEditing) {
            ImprovedFog.highQuality = (PerformanceSettings.FogQuality == 0);
            ImprovedFog.fogIntensity.update(GameTime.getInstance().getMultiplier());
            ImprovedFog.fogIntensity.setTarget(ImprovedFog.climateManager.getFogIntensity());
            ImprovedFog.baseAlpha = ImprovedFog.fogIntensity.value();
            if (ImprovedFog.highQuality) {
                ImprovedFog.renderEveryXRow = 1;
                ImprovedFog.topAlphaHeight = 0.38f;
                ImprovedFog.bottomAlphaHeight = 0.24f;
                ImprovedFog.octaves = 6.0f;
                ImprovedFog.secondLayerAlpha = 0.5f;
            }
            else {
                ImprovedFog.renderEveryXRow = 2;
                ImprovedFog.topAlphaHeight = 0.32f;
                ImprovedFog.bottomAlphaHeight = 0.32f;
                ImprovedFog.octaves = 3.0f;
                ImprovedFog.secondLayerAlpha = 1.0f;
            }
            ImprovedFog.colorR = ImprovedFog.climateManager.getColorNewFog().getExterior().r;
            ImprovedFog.colorG = ImprovedFog.climateManager.getColorNewFog().getExterior().g;
            ImprovedFog.colorB = ImprovedFog.climateManager.getColorNewFog().getExterior().b;
        }
        if (ImprovedFog.baseAlpha <= 0.0f) {
            ImprovedFog.scalingX = 0.0f;
            ImprovedFog.scalingY = 0.0f;
        }
        else {
            final double n = 3.141592653589793 - (ImprovedFog.climateManager.getWindAngleRadians() - 2.356194490192345);
            final float n2 = (float)Math.cos(n);
            final float n3 = (float)Math.sin(n);
            ImprovedFog.scalingX += n2 * ImprovedFog.climateManager.getWindIntensity() * GameTime.getInstance().getMultiplier();
            ImprovedFog.scalingY += n3 * ImprovedFog.climateManager.getWindIntensity() * GameTime.getInstance().getMultiplier();
        }
    }
    
    public static void startRender(final int playerIndex, final int zLayer) {
        ImprovedFog.climateManager = ClimateManager.getInstance();
        if (zLayer >= 2 || ImprovedFog.baseAlpha <= 0.0f || PerformanceSettings.FogQuality == 2) {
            ImprovedFog.drawingThisLayer = false;
            return;
        }
        ImprovedFog.drawingThisLayer = true;
        final IsoPlayer isoPlayer = IsoPlayer.players[playerIndex];
        if (ImprovedFog.renderCurrentLayerOnly && isoPlayer.getZ() != zLayer) {
            ImprovedFog.drawingThisLayer = false;
            return;
        }
        if (isoPlayer.isInARoom() && zLayer > 0) {
            ImprovedFog.drawingThisLayer = false;
            return;
        }
        ImprovedFog.playerRow = (int)isoPlayer.getX() + (int)isoPlayer.getY();
        ImprovedFog.ZOOM = Core.getInstance().getZoom(playerIndex);
        ImprovedFog.zLayer = zLayer;
        ImprovedFog.PlayerIndex = playerIndex;
        final PlayerCamera playerCamera = IsoCamera.cameras[playerIndex];
        ImprovedFog.screenWidth = (float)IsoCamera.getOffscreenWidth(playerIndex);
        ImprovedFog.screenHeight = (float)IsoCamera.getOffscreenHeight(playerIndex);
        ImprovedFog.worldOffsetX = playerCamera.getOffX() - IsoCamera.getOffscreenLeft(ImprovedFog.PlayerIndex) * ImprovedFog.ZOOM;
        ImprovedFog.worldOffsetY = playerCamera.getOffY() + IsoCamera.getOffscreenTop(ImprovedFog.PlayerIndex) * ImprovedFog.ZOOM;
        ImprovedFog.rightClickOffX = playerCamera.RightClickX;
        ImprovedFog.rightClickOffY = playerCamera.RightClickY;
        ImprovedFog.cameraOffscreenLeft = (float)IsoCamera.getOffscreenLeft(playerIndex);
        ImprovedFog.cameraOffscreenTop = (float)IsoCamera.getOffscreenTop(playerIndex);
        ImprovedFog.cameraZoom = ImprovedFog.ZOOM;
        if (!ImprovedFog.enableEditing) {
            if (isoPlayer.getVehicle() != null) {
                ImprovedFog.alphaCircleAlpha = 0.0f;
                ImprovedFog.alphaCircleRad = (ImprovedFog.highQuality ? 2.0f : 2.6f);
            }
            else if (isoPlayer.isInARoom()) {
                ImprovedFog.alphaCircleAlpha = 0.0f;
                ImprovedFog.alphaCircleRad = (ImprovedFog.highQuality ? 1.25f : 1.5f);
            }
            else {
                ImprovedFog.alphaCircleAlpha = (ImprovedFog.highQuality ? 0.1f : 0.16f);
                ImprovedFog.alphaCircleRad = (ImprovedFog.highQuality ? 2.5f : 3.0f);
                if (ImprovedFog.climateManager.getWeatherPeriod().isRunning() && (ImprovedFog.climateManager.getWeatherPeriod().isTropicalStorm() || ImprovedFog.climateManager.getWeatherPeriod().isThunderStorm())) {
                    ImprovedFog.alphaCircleRad *= 0.6f;
                }
            }
        }
        final int n = 0;
        final int n2 = 0;
        final int n3 = n + IsoCamera.getOffscreenWidth(playerIndex);
        final int n4 = n2 + IsoCamera.getOffscreenHeight(playerIndex);
        final float xToIso = IsoUtils.XToIso((float)n, (float)n2, (float)ImprovedFog.zLayer);
        final float yToIso = IsoUtils.YToIso((float)n, (float)n2, (float)ImprovedFog.zLayer);
        final float xToIso2 = IsoUtils.XToIso((float)n3, (float)n4, (float)ImprovedFog.zLayer);
        final float yToIso2 = IsoUtils.YToIso((float)n3, (float)n4, (float)ImprovedFog.zLayer);
        IsoUtils.YToIso((float)n, (float)n4, (float)ImprovedFog.zLayer);
        ImprovedFog.minY = (int)yToIso;
        ImprovedFog.maxY = (int)yToIso2;
        ImprovedFog.minX = (int)xToIso;
        ImprovedFog.maxX = (int)xToIso2;
        if (IsoPlayer.numPlayers > 1) {
            ImprovedFog.maxX = Math.max(ImprovedFog.maxX, IsoWorld.instance.CurrentCell.getMaxX());
            ImprovedFog.maxY = Math.max(ImprovedFog.maxY, IsoWorld.instance.CurrentCell.getMaxY());
        }
        ImprovedFog.minX += ImprovedFog.minXOffset;
        ImprovedFog.maxX += ImprovedFog.maxXOffset;
        ImprovedFog.maxY += ImprovedFog.maxYOffset;
        int n6;
        final int n5 = n6 = ImprovedFog.maxX - ImprovedFog.minX;
        if (ImprovedFog.minY != ImprovedFog.maxY) {
            n6 += (int)PZMath.abs((float)(ImprovedFog.minY - ImprovedFog.maxY));
        }
        ImprovedFog.rectangleIter.reset(n5, n6);
        ImprovedFog.lastRow = -1;
        ImprovedFog.fogRectangle.hasStarted = false;
        ImprovedFog.chunkMap = IsoWorld.instance.getCell().getChunkMap(playerIndex);
    }
    
    public static void renderRowsBehind(final IsoGridSquare isoGridSquare) {
        if (!ImprovedFog.drawingThisLayer) {
            return;
        }
        int n = -1;
        if (isoGridSquare != null) {
            n = isoGridSquare.getX() + isoGridSquare.getY();
            if (n < ImprovedFog.minX + ImprovedFog.minY) {
                return;
            }
        }
        if (ImprovedFog.lastRow >= 0 && ImprovedFog.lastRow == n) {
            return;
        }
        final Vector2i rectangleMatrixPos = ImprovedFog.rectangleMatrixPos;
        while (ImprovedFog.rectangleIter.next(rectangleMatrixPos)) {
            if (rectangleMatrixPos == null) {
                continue;
            }
            final int n2 = rectangleMatrixPos.x + ImprovedFog.minX;
            final int n3 = rectangleMatrixPos.y + ImprovedFog.minY;
            final int lastRow = n2 + n3;
            if (lastRow != ImprovedFog.lastRow) {
                if (ImprovedFog.lastRow >= 0 && (!ImprovedFog.renderEndOnly || isoGridSquare == null)) {
                    endFogRectangle(ImprovedFog.lastIterPos.x, ImprovedFog.lastIterPos.y, ImprovedFog.zLayer);
                }
                ImprovedFog.lastRow = lastRow;
            }
            final IsoGridSquare gridSquare = ImprovedFog.chunkMap.getGridSquare(n2, n3, ImprovedFog.zLayer);
            boolean b = true;
            if (gridSquare != null && (!gridSquare.isExteriorCache || gridSquare.isInARoom())) {
                b = false;
            }
            if (b) {
                if (!ImprovedFog.renderEndOnly || isoGridSquare == null) {
                    startFogRectangle(n2, n3, ImprovedFog.zLayer);
                }
            }
            else if (!ImprovedFog.renderEndOnly || isoGridSquare == null) {
                endFogRectangle(ImprovedFog.lastIterPos.x, ImprovedFog.lastIterPos.y, ImprovedFog.zLayer);
            }
            ImprovedFog.lastIterPos.set(n2, n3);
            if (n != -1 && lastRow == n) {
                break;
            }
        }
    }
    
    public static void endRender() {
        if (!ImprovedFog.drawingThisLayer) {
            return;
        }
        renderRowsBehind(null);
        if (ImprovedFog.fogRectangle.hasStarted) {
            endFogRectangle(ImprovedFog.lastIterPos.x, ImprovedFog.lastIterPos.y, ImprovedFog.zLayer);
        }
    }
    
    private static void startFogRectangle(final int startX, final int startY, final int z) {
        if (!ImprovedFog.fogRectangle.hasStarted) {
            ImprovedFog.fogRectangle.hasStarted = true;
            ImprovedFog.fogRectangle.startX = startX;
            ImprovedFog.fogRectangle.startY = startY;
            ImprovedFog.fogRectangle.Z = z;
        }
    }
    
    private static void endFogRectangle(final int endX, final int endY, final int z) {
        if (ImprovedFog.fogRectangle.hasStarted) {
            ImprovedFog.fogRectangle.hasStarted = false;
            ImprovedFog.fogRectangle.endX = endX;
            ImprovedFog.fogRectangle.endY = endY;
            ImprovedFog.fogRectangle.Z = z;
            renderFogSegment();
        }
    }
    
    private static void renderFogSegment() {
        final int n = ImprovedFog.fogRectangle.startX + ImprovedFog.fogRectangle.startY;
        final int n2 = ImprovedFog.fogRectangle.endX + ImprovedFog.fogRectangle.endY;
        if (Core.bDebug && n != n2) {
            DebugLog.log("ROWS NOT EQUAL");
        }
        if (ImprovedFog.renderOnlyOneRow) {
            if (n != ImprovedFog.playerRow) {
                return;
            }
        }
        else if (n % ImprovedFog.renderEveryXRow != 0) {
            return;
        }
        if (Core.bDebug && ImprovedFog.renderXRowsFromCenter >= 1 && (n < ImprovedFog.playerRow - ImprovedFog.renderXRowsFromCenter || n > ImprovedFog.playerRow + ImprovedFog.renderXRowsFromCenter)) {
            return;
        }
        final float baseAlpha = ImprovedFog.baseAlpha;
        final FogRectangle fogRectangle = ImprovedFog.fogRectangle;
        final float xToScreenExact = IsoUtils.XToScreenExact((float)fogRectangle.startX, (float)fogRectangle.startY, (float)fogRectangle.Z, 0);
        final float yToScreenExact = IsoUtils.YToScreenExact((float)fogRectangle.startX, (float)fogRectangle.startY, (float)fogRectangle.Z, 0);
        final float xToScreenExact2 = IsoUtils.XToScreenExact((float)fogRectangle.endX, (float)fogRectangle.endY, (float)fogRectangle.Z, 0);
        IsoUtils.YToScreenExact((float)fogRectangle.endX, (float)fogRectangle.endY, (float)fogRectangle.Z, 0);
        final float n3 = xToScreenExact - 32.0f * Core.TileScale;
        final float n4 = yToScreenExact - 80.0f * Core.TileScale;
        final float n5 = xToScreenExact2 + 32.0f * Core.TileScale;
        final float n6 = 96.0f * Core.TileScale;
        final float n7 = (n5 - n3) / (64.0f * Core.TileScale);
        final float n8 = fogRectangle.startX % 6.0f / 6.0f;
        final float n9 = n7 / 6.0f;
        final float n10 = n8;
        final float n11 = n9 + n8;
        if (FogShader.instance.StartShader()) {
            FogShader.instance.setScreenInfo(ImprovedFog.screenWidth, ImprovedFog.screenHeight, ImprovedFog.ZOOM, (ImprovedFog.zLayer > 0) ? ImprovedFog.secondLayerAlpha : 1.0f);
            FogShader.instance.setTextureInfo(ImprovedFog.drawDebugColors ? 1.0f : 0.0f, ImprovedFog.octaves, baseAlpha, (float)Core.TileScale);
            FogShader.instance.setRectangleInfo((float)(int)n3, (float)(int)n4, (float)(int)(n5 - n3), (float)(int)n6);
            FogShader.instance.setWorldOffset(ImprovedFog.worldOffsetX, ImprovedFog.worldOffsetY, ImprovedFog.rightClickOffX, ImprovedFog.rightClickOffY);
            FogShader.instance.setScalingInfo(ImprovedFog.scalingX, ImprovedFog.scalingY, (float)ImprovedFog.zLayer, ImprovedFog.highQuality ? 0.0f : 1.0f);
            FogShader.instance.setColorInfo(ImprovedFog.colorR, ImprovedFog.colorG, ImprovedFog.colorB, 1.0f);
            FogShader.instance.setParamInfo(ImprovedFog.topAlphaHeight, ImprovedFog.bottomAlphaHeight, ImprovedFog.alphaCircleAlpha, ImprovedFog.alphaCircleRad);
            FogShader.instance.setCameraInfo(ImprovedFog.cameraOffscreenLeft, ImprovedFog.cameraOffscreenTop, ImprovedFog.cameraZoom, ImprovedFog.offsets[n % ImprovedFog.offsets.length]);
            SpriteRenderer.instance.render(ImprovedFog.noiseTexture, (float)(int)n3, (float)(int)n4, (float)(int)(n5 - n3), (float)(int)n6, 1.0f, 1.0f, 1.0f, baseAlpha, n10, 0.0f, n11, 0.0f, n11, 1.0f, n10, 1.0f);
            IndieGL.EndShader();
        }
    }
    
    public static void DrawSubTextureRGBA(final Texture texture, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
        if (texture == null || n3 <= 0.0 || n4 <= 0.0 || n7 <= 0.0 || n8 <= 0.0) {
            return;
        }
        final double n13 = n5 + texture.offsetX;
        final double n14 = n6 + texture.offsetY;
        if (n14 + n8 < 0.0 || n14 > 4096.0) {
            return;
        }
        final float clamp = PZMath.clamp((float)n, 0.0f, (float)texture.getWidth());
        final float clamp2 = PZMath.clamp((float)n2, 0.0f, (float)texture.getHeight());
        final float n15 = PZMath.clamp((float)(clamp + n3), 0.0f, (float)texture.getWidth()) - clamp;
        final float n16 = PZMath.clamp((float)(clamp2 + n4), 0.0f, (float)texture.getHeight()) - clamp2;
        final float n17 = clamp / texture.getWidth();
        final float n18 = clamp2 / texture.getHeight();
        final float n19 = (clamp + n15) / texture.getWidth();
        final float n20 = (clamp2 + n16) / texture.getHeight();
        final float n21 = texture.getXEnd() - texture.getXStart();
        final float n22 = texture.getYEnd() - texture.getYStart();
        final float n23 = texture.getXStart() + n17 * n21;
        final float n24 = texture.getXStart() + n19 * n21;
        final float n25 = texture.getYStart() + n18 * n22;
        final float n26 = texture.getYStart() + n20 * n22;
        SpriteRenderer.instance.render(texture, (float)n13, (float)n14, (float)n7, (float)n8, (float)n9, (float)n10, (float)n11, (float)n12, n23, n25, n24, n25, n24, n26, n23, n26);
    }
    
    public static void updateKeys() {
        if (!Core.bDebug) {
            return;
        }
        if (ImprovedFog.keyPause > 0) {
            --ImprovedFog.keyPause;
        }
        if (ImprovedFog.keyPause <= 0 && GameKeyboard.isKeyDown(72)) {
            DebugLog.log("Reloading fog shader...");
            ImprovedFog.keyPause = 30;
            FogShader.instance.reloadShader();
        }
    }
    
    static {
        rectangleIter = new RectangleIterator();
        rectangleMatrixPos = new Vector2i();
        ImprovedFog.lastIterPos = new Vector2i();
        ImprovedFog.fogRectangle = new FogRectangle();
        ImprovedFog.drawingThisLayer = false;
        ImprovedFog.ZOOM = 1.0f;
        ImprovedFog.topAlphaHeight = 0.38f;
        ImprovedFog.bottomAlphaHeight = 0.24f;
        ImprovedFog.secondLayerAlpha = 0.5f;
        ImprovedFog.scalingX = 1.0f;
        ImprovedFog.scalingY = 1.0f;
        ImprovedFog.colorR = 1.0f;
        ImprovedFog.colorG = 1.0f;
        ImprovedFog.colorB = 1.0f;
        ImprovedFog.drawDebugColors = false;
        ImprovedFog.octaves = 6.0f;
        ImprovedFog.highQuality = true;
        ImprovedFog.enableEditing = false;
        ImprovedFog.alphaCircleAlpha = 0.3f;
        ImprovedFog.alphaCircleRad = 2.25f;
        ImprovedFog.lastRow = -1;
        ImprovedFog.renderOnlyOneRow = false;
        ImprovedFog.baseAlpha = 0.0f;
        ImprovedFog.renderEveryXRow = 1;
        ImprovedFog.renderXRowsFromCenter = 0;
        ImprovedFog.renderCurrentLayerOnly = false;
        ImprovedFog.rightClickOffX = 0.0f;
        ImprovedFog.rightClickOffY = 0.0f;
        ImprovedFog.cameraOffscreenLeft = 0.0f;
        ImprovedFog.cameraOffscreenTop = 0.0f;
        ImprovedFog.cameraZoom = 0.0f;
        ImprovedFog.minXOffset = -2;
        ImprovedFog.maxXOffset = 12;
        ImprovedFog.maxYOffset = -5;
        ImprovedFog.renderEndOnly = false;
        fogIntensity = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        ImprovedFog.keyPause = 0;
        offsets = new float[] { 0.3f, 0.8f, 0.0f, 0.6f, 0.3f, 0.1f, 0.5f, 0.9f, 0.2f, 0.0f, 0.7f, 0.1f, 0.4f, 0.2f, 0.5f, 0.3f, 0.8f, 0.4f, 0.9f, 0.5f, 0.8f, 0.4f, 0.7f, 0.2f, 0.0f, 0.6f, 0.1f, 0.6f, 0.9f, 0.7f };
    }
    
    private static class RectangleIterator
    {
        private int curX;
        private int curY;
        private int sX;
        private int sY;
        private int rowLen;
        private boolean altRow;
        private int curRow;
        private int rowIndex;
        private int maxRows;
        
        private RectangleIterator() {
            this.curX = 0;
            this.curY = 0;
            this.rowLen = 0;
            this.altRow = false;
            this.curRow = 0;
            this.rowIndex = 0;
            this.maxRows = 0;
        }
        
        public void reset(final int maxRows, final int n) {
            this.sX = 0;
            this.sY = 0;
            this.curX = 0;
            this.curY = 0;
            this.curRow = 0;
            this.altRow = false;
            this.rowIndex = 0;
            this.rowLen = (int)PZMath.ceil(n / 2.0f);
            this.maxRows = maxRows;
        }
        
        public boolean next(final Vector2i vector2i) {
            if (this.rowLen <= 0 || this.maxRows <= 0 || this.curRow >= this.maxRows) {
                vector2i.set(0, 0);
                return false;
            }
            vector2i.set(this.curX, this.curY);
            ++this.rowIndex;
            if (this.rowIndex == this.rowLen) {
                this.rowLen = (this.altRow ? (this.rowLen - 1) : (this.rowLen + 1));
                this.rowIndex = 0;
                this.sX = (this.altRow ? (this.sX + 1) : this.sX);
                this.sY = (this.altRow ? this.sY : (this.sY + 1));
                this.altRow = !this.altRow;
                this.curX = this.sX;
                this.curY = this.sY;
                ++this.curRow;
                return this.curRow != this.maxRows;
            }
            ++this.curX;
            --this.curY;
            return true;
        }
    }
    
    private static class FogRectangle
    {
        int startX;
        int startY;
        int endX;
        int endY;
        int Z;
        boolean hasStarted;
        
        private FogRectangle() {
            this.hasStarted = false;
        }
    }
}
