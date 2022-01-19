// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.weather.ClimateManager;
import zombie.GameTime;
import zombie.core.math.PZMath;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;
import java.util.List;

public final class WorldMarkers
{
    private static final float CIRCLE_TEXTURE_SCALE = 1.5f;
    public static final WorldMarkers instance;
    private static int NextGridSquareMarkerID;
    private static int NextHomingPointID;
    private final List<GridSquareMarker> gridSquareMarkers;
    private final PlayerHomingPointList[] homingPoints;
    private final DirectionArrowList[] directionArrows;
    private static final ColorInfo stCol;
    private final PlayerScreen playerScreen;
    private Point intersectPoint;
    private Point arrowStart;
    private Point arrowEnd;
    private Line arrowLine;
    
    private WorldMarkers() {
        this.gridSquareMarkers = new ArrayList<GridSquareMarker>();
        this.homingPoints = new PlayerHomingPointList[4];
        this.directionArrows = new DirectionArrowList[4];
        this.playerScreen = new PlayerScreen();
        this.intersectPoint = new Point(0.0f, 0.0f);
        this.arrowStart = new Point(0.0f, 0.0f);
        this.arrowEnd = new Point(0.0f, 0.0f);
        this.arrowLine = new Line(this.arrowStart, this.arrowEnd);
    }
    
    public void init() {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < this.homingPoints.length; ++i) {
            this.homingPoints[i] = new PlayerHomingPointList();
        }
        for (int j = 0; j < this.directionArrows.length; ++j) {
            this.directionArrows[j] = new DirectionArrowList();
        }
    }
    
    public void reset() {
        for (int i = 0; i < this.homingPoints.length; ++i) {
            this.homingPoints[i].clear();
        }
        for (int j = 0; j < this.directionArrows.length; ++j) {
            this.directionArrows[j].clear();
        }
        this.gridSquareMarkers.clear();
    }
    
    private int GetDistance(final int n, final int n2, final int n3, final int n4) {
        return (int)Math.sqrt(Math.pow(n - n3, 2.0) + Math.pow(n2 - n4, 2.0));
    }
    
    private float getAngle(final int n, final int n2, final int n3, final int n4) {
        float n5 = (float)Math.toDegrees(Math.atan2(n4 - n2, n3 - n));
        if (n5 < 0.0f) {
            n5 += 360.0f;
        }
        return n5;
    }
    
    private float angleDegrees(float n) {
        if (n < 0.0f) {
            n += 360.0f;
        }
        if (n > 360.0f) {
            n -= 360.0f;
        }
        return n;
    }
    
    public PlayerHomingPoint getHomingPoint(final int n) {
        for (int i = 0; i < this.homingPoints.length; ++i) {
            for (int j = this.homingPoints[i].size() - 1; j >= 0; ++j) {
                if (this.homingPoints[i].get(j).ID == n) {
                    return this.homingPoints[i].get(j);
                }
            }
        }
        return null;
    }
    
    public PlayerHomingPoint addPlayerHomingPoint(final IsoPlayer isoPlayer, final int n, final int n2) {
        return this.addPlayerHomingPoint(isoPlayer, n, n2, "arrow_triangle", 1.0f, 1.0f, 1.0f, 1.0f, true, 20);
    }
    
    public PlayerHomingPoint addPlayerHomingPoint(final IsoPlayer isoPlayer, final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
        return this.addPlayerHomingPoint(isoPlayer, n, n2, "arrow_triangle", n3, n4, n5, n6, true, 20);
    }
    
    public PlayerHomingPoint addPlayerHomingPoint(final IsoPlayer isoPlayer, final int x, final int y, final String texture, final float r, final float g, final float b, final float a, final boolean homeOnTargetInView, final int homeOnTargetDist) {
        if (GameServer.bServer) {
            return null;
        }
        final PlayerHomingPoint e = new PlayerHomingPoint(isoPlayer.PlayerIndex);
        e.setActive(true);
        e.setTexture(texture);
        e.setX(x);
        e.setY(y);
        e.setR(r);
        e.setG(g);
        e.setB(b);
        e.setA(a);
        e.setHomeOnTargetInView(homeOnTargetInView);
        e.setHomeOnTargetDist(homeOnTargetDist);
        this.homingPoints[isoPlayer.PlayerIndex].add(e);
        return e;
    }
    
    public boolean removeHomingPoint(final PlayerHomingPoint playerHomingPoint) {
        return this.removeHomingPoint(playerHomingPoint.getID());
    }
    
    public boolean removeHomingPoint(final int n) {
        for (int i = 0; i < this.homingPoints.length; ++i) {
            for (int j = this.homingPoints[i].size() - 1; j >= 0; --j) {
                if (this.homingPoints[i].get(j).ID == n) {
                    this.homingPoints[i].get(j).remove();
                    this.homingPoints[i].remove(j);
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean removePlayerHomingPoint(final IsoPlayer isoPlayer, final PlayerHomingPoint playerHomingPoint) {
        return this.removePlayerHomingPoint(isoPlayer, playerHomingPoint.getID());
    }
    
    public boolean removePlayerHomingPoint(final IsoPlayer isoPlayer, final int n) {
        for (int i = this.homingPoints[isoPlayer.PlayerIndex].size() - 1; i >= 0; --i) {
            if (this.homingPoints[isoPlayer.PlayerIndex].get(i).ID == n) {
                this.homingPoints[isoPlayer.PlayerIndex].get(i).remove();
                this.homingPoints[isoPlayer.PlayerIndex].remove(i);
                return true;
            }
        }
        return false;
    }
    
    public void removeAllHomingPoints(final IsoPlayer isoPlayer) {
        this.homingPoints[isoPlayer.PlayerIndex].clear();
    }
    
    public DirectionArrow getDirectionArrow(final int n) {
        for (int i = 0; i < this.directionArrows.length; ++i) {
            for (int j = this.directionArrows[i].size() - 1; j >= 0; --j) {
                if (this.directionArrows[i].get(j).ID == n) {
                    return this.directionArrows[i].get(j);
                }
            }
        }
        return null;
    }
    
    public DirectionArrow addDirectionArrow(final IsoPlayer isoPlayer, final int x, final int y, final int z, final String texture, final float r, final float g, final float b, final float a) {
        if (GameServer.bServer) {
            return null;
        }
        final DirectionArrow e = new DirectionArrow(isoPlayer.PlayerIndex);
        e.setActive(true);
        e.setTexture(texture);
        e.setTexDown("dir_arrow_down");
        e.setTexStairsUp("dir_arrow_stairs_up");
        e.setTexStairsDown("dir_arrow_stairs_down");
        e.setX(x);
        e.setY(y);
        e.setZ(z);
        e.setR(r);
        e.setG(g);
        e.setB(b);
        e.setA(a);
        this.directionArrows[isoPlayer.PlayerIndex].add(e);
        return e;
    }
    
    public boolean removeDirectionArrow(final DirectionArrow directionArrow) {
        return this.removeDirectionArrow(directionArrow.getID());
    }
    
    public boolean removeDirectionArrow(final int n) {
        for (int i = 0; i < this.directionArrows.length; ++i) {
            for (int j = this.directionArrows[i].size() - 1; j >= 0; --j) {
                if (this.directionArrows[i].get(j).ID == n) {
                    this.directionArrows[i].get(j).remove();
                    this.directionArrows[i].remove(j);
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean removePlayerDirectionArrow(final IsoPlayer isoPlayer, final DirectionArrow directionArrow) {
        return this.removePlayerDirectionArrow(isoPlayer, directionArrow.getID());
    }
    
    public boolean removePlayerDirectionArrow(final IsoPlayer isoPlayer, final int n) {
        for (int i = this.directionArrows[isoPlayer.PlayerIndex].size() - 1; i >= 0; --i) {
            if (this.directionArrows[isoPlayer.PlayerIndex].get(i).ID == n) {
                this.directionArrows[isoPlayer.PlayerIndex].get(i).remove();
                this.directionArrows[isoPlayer.PlayerIndex].remove(i);
                return true;
            }
        }
        return false;
    }
    
    public void removeAllDirectionArrows(final IsoPlayer isoPlayer) {
        this.directionArrows[isoPlayer.PlayerIndex].clear();
    }
    
    public void update() {
        if (GameServer.bServer) {
            return;
        }
        this.updateGridSquareMarkers();
        this.updateHomingPoints();
        this.updateDirectionArrows();
    }
    
    private void updateDirectionArrows() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        for (int i = 0; i < this.directionArrows.length; ++i) {
            if (i == playerIndex) {
                if (this.directionArrows[i].size() != 0) {
                    for (int j = this.directionArrows[i].size() - 1; j >= 0; --j) {
                        if (this.directionArrows[i].get(j).isRemoved()) {
                            this.directionArrows[i].remove(j);
                        }
                    }
                    this.playerScreen.update(i);
                    for (int k = 0; k < this.directionArrows[i].size(); ++k) {
                        final DirectionArrow directionArrow = this.directionArrows[i].get(k);
                        if (directionArrow.active && IsoPlayer.players[i] != null) {
                            final IsoPlayer isoPlayer = IsoPlayer.players[i];
                            if (isoPlayer.getSquare() != null) {
                                final PlayerCamera playerCamera = IsoCamera.cameras[i];
                                final float zoom = Core.getInstance().getZoom(i);
                                final int x = isoPlayer.getSquare().getX();
                                final int y = isoPlayer.getSquare().getY();
                                final int z = isoPlayer.getSquare().getZ();
                                final int getDistance = this.GetDistance(x, y, directionArrow.x, directionArrow.y);
                                boolean b = false;
                                boolean b2 = false;
                                float renderScreenX = 0.0f;
                                float renderScreenY = 0.0f;
                                if (getDistance < 300) {
                                    b = true;
                                    renderScreenX = playerCamera.XToScreenExact((float)directionArrow.x, (float)directionArrow.y, (float)z, 0) / zoom;
                                    renderScreenY = playerCamera.YToScreenExact((float)directionArrow.x, (float)directionArrow.y, (float)z, 0) / zoom;
                                    if (this.playerScreen.isWithinInner(renderScreenX, renderScreenY)) {
                                        b2 = true;
                                    }
                                }
                                if (!b2) {
                                    directionArrow.renderWithAngle = true;
                                    directionArrow.isDrawOnWorld = false;
                                    directionArrow.renderTexture = directionArrow.texture;
                                    directionArrow.renderSizeMod = 1.0f;
                                    final float centerX = this.playerScreen.centerX;
                                    final float centerY = this.playerScreen.centerY;
                                    float angle;
                                    if (!b) {
                                        angle = this.angleDegrees(this.angleDegrees(180.0f - this.getAngle(directionArrow.x, directionArrow.y, x, y)) + 45.0f);
                                    }
                                    else {
                                        angle = this.angleDegrees(this.angleDegrees(180.0f - this.getAngle((int)centerX, (int)centerY, (int)renderScreenX, (int)renderScreenY)) - 90.0f);
                                    }
                                    if (angle != directionArrow.angle) {
                                        if (!directionArrow.lastWasWithinView) {
                                            directionArrow.angle = PZMath.lerpAngle(PZMath.degToRad(directionArrow.angle), PZMath.degToRad(angle), directionArrow.angleLerpVal * GameTime.instance.getMultiplier());
                                            directionArrow.angle = PZMath.radToDeg(directionArrow.angle);
                                        }
                                        else {
                                            directionArrow.angle = angle;
                                        }
                                    }
                                    final float n = centerX + 32000.0f * (float)Math.sin(Math.toRadians(directionArrow.angle));
                                    final float n2 = centerY + 32000.0f * (float)Math.cos(Math.toRadians(directionArrow.angle));
                                    directionArrow.renderScreenX = centerX;
                                    directionArrow.renderScreenY = centerY;
                                    this.arrowStart.set(centerX, centerY);
                                    this.arrowEnd.set(n, n2);
                                    final Line[] borders = this.playerScreen.getBorders();
                                    for (int l = 0; l < borders.length; ++l) {
                                        this.intersectPoint.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
                                        if (intersectLineSegments(this.arrowLine, borders[l], this.intersectPoint)) {
                                            directionArrow.renderScreenX = this.intersectPoint.x;
                                            directionArrow.renderScreenY = this.intersectPoint.y;
                                            break;
                                        }
                                    }
                                    directionArrow.lastWasWithinView = false;
                                }
                                else {
                                    directionArrow.renderWithAngle = false;
                                    directionArrow.isDrawOnWorld = false;
                                    directionArrow.renderSizeMod = 1.0f;
                                    if (zoom > 1.0f) {
                                        final DirectionArrow directionArrow2 = directionArrow;
                                        directionArrow2.renderSizeMod /= zoom;
                                    }
                                    directionArrow.renderScreenX = renderScreenX;
                                    directionArrow.renderScreenY = renderScreenY;
                                    if (z == directionArrow.z) {
                                        directionArrow.renderTexture = ((directionArrow.texDown != null) ? directionArrow.texDown : directionArrow.texture);
                                    }
                                    else if (directionArrow.z > z) {
                                        directionArrow.renderTexture = ((directionArrow.texStairsUp != null) ? directionArrow.texStairsUp : directionArrow.texture);
                                    }
                                    else {
                                        directionArrow.renderTexture = ((directionArrow.texStairsDown != null) ? directionArrow.texStairsUp : directionArrow.texture);
                                    }
                                    directionArrow.lastWasWithinView = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void updateHomingPoints() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        for (int i = 0; i < this.homingPoints.length; ++i) {
            if (i == playerIndex) {
                if (this.homingPoints[i].size() != 0) {
                    for (int j = this.homingPoints[i].size() - 1; j >= 0; --j) {
                        if (this.homingPoints[i].get(j).isRemoved) {
                            this.homingPoints[i].remove(j);
                        }
                    }
                    this.playerScreen.update(i);
                    for (int k = 0; k < this.homingPoints[i].size(); ++k) {
                        final PlayerHomingPoint playerHomingPoint = this.homingPoints[i].get(k);
                        if (playerHomingPoint.active && IsoPlayer.players[i] != null) {
                            final IsoPlayer isoPlayer = IsoPlayer.players[i];
                            if (isoPlayer.getSquare() != null) {
                                final PlayerCamera playerCamera = IsoCamera.cameras[i];
                                final float zoom = Core.getInstance().getZoom(i);
                                playerHomingPoint.renderSizeMod = 1.0f;
                                if (zoom > 1.0f) {
                                    final PlayerHomingPoint playerHomingPoint2 = playerHomingPoint;
                                    playerHomingPoint2.renderSizeMod /= zoom;
                                }
                                final int x = isoPlayer.getSquare().getX();
                                final int y = isoPlayer.getSquare().getY();
                                playerHomingPoint.dist = this.GetDistance(x, y, playerHomingPoint.x, playerHomingPoint.y);
                                playerHomingPoint.targetOnScreen = false;
                                if (playerHomingPoint.dist < 200.0f) {
                                    playerHomingPoint.targetScreenX = playerCamera.XToScreenExact((float)playerHomingPoint.x, (float)playerHomingPoint.y, 0.0f, 0) / zoom;
                                    playerHomingPoint.targetScreenY = playerCamera.YToScreenExact((float)playerHomingPoint.x, (float)playerHomingPoint.y, 0.0f, 0) / zoom;
                                    final PlayerHomingPoint playerHomingPoint3 = playerHomingPoint;
                                    playerHomingPoint3.targetScreenX += playerHomingPoint.homeOnOffsetX / zoom;
                                    final PlayerHomingPoint playerHomingPoint4 = playerHomingPoint;
                                    playerHomingPoint4.targetScreenY += playerHomingPoint.homeOnOffsetY / zoom;
                                    playerHomingPoint.targetOnScreen = this.playerScreen.isOnScreen(playerHomingPoint.targetScreenX, playerHomingPoint.targetScreenY);
                                }
                                final float n = this.playerScreen.centerX + playerHomingPoint.renderOffsetX / zoom;
                                final float n2 = this.playerScreen.centerY + playerHomingPoint.renderOffsetY / zoom;
                                if (!playerHomingPoint.customTargetAngle) {
                                    float targetAngle;
                                    if (!playerHomingPoint.targetOnScreen) {
                                        targetAngle = this.angleDegrees(this.angleDegrees(180.0f - this.getAngle(playerHomingPoint.x, playerHomingPoint.y, x, y)) + 45.0f);
                                    }
                                    else {
                                        targetAngle = this.angleDegrees(this.angleDegrees(180.0f - this.getAngle((int)n, (int)n2, (int)playerHomingPoint.targetScreenX, (int)playerHomingPoint.targetScreenY)) - 90.0f);
                                    }
                                    playerHomingPoint.targetAngle = targetAngle;
                                }
                                if (playerHomingPoint.targetAngle != playerHomingPoint.angle) {
                                    playerHomingPoint.angle = PZMath.lerpAngle(PZMath.degToRad(playerHomingPoint.angle), PZMath.degToRad(playerHomingPoint.targetAngle), playerHomingPoint.angleLerpVal * GameTime.instance.getMultiplier());
                                    playerHomingPoint.angle = PZMath.radToDeg(playerHomingPoint.angle);
                                }
                                final float n3 = playerHomingPoint.stickToCharDist / zoom;
                                playerHomingPoint.targRenderX = n + n3 * (float)Math.sin(Math.toRadians(playerHomingPoint.angle));
                                playerHomingPoint.targRenderY = n2 + n3 * (float)Math.cos(Math.toRadians(playerHomingPoint.angle));
                                final float movementLerpVal = playerHomingPoint.movementLerpVal;
                                if (playerHomingPoint.targetOnScreen) {
                                    final float n4 = (float)this.GetDistance((int)playerHomingPoint.targRenderX, (int)playerHomingPoint.targRenderY, (int)playerHomingPoint.targetScreenX, (int)playerHomingPoint.targetScreenY);
                                    final float n5 = (float)this.GetDistance((int)n, (int)n2, (int)playerHomingPoint.targetScreenX, (int)playerHomingPoint.targetScreenY);
                                    if (n5 < n4 || (playerHomingPoint.homeOnTargetInView && playerHomingPoint.dist <= playerHomingPoint.homeOnTargetDist)) {
                                        final float n6 = n5 * 0.75f;
                                        playerHomingPoint.targRenderX = n + n6 * (float)Math.sin(Math.toRadians(playerHomingPoint.targetAngle));
                                        playerHomingPoint.targRenderY = n2 + n6 * (float)Math.cos(Math.toRadians(playerHomingPoint.targetAngle));
                                    }
                                }
                                playerHomingPoint.targRenderX = this.playerScreen.clampToInnerX(playerHomingPoint.targRenderX);
                                playerHomingPoint.targRenderY = this.playerScreen.clampToInnerY(playerHomingPoint.targRenderY);
                                if (playerHomingPoint.targRenderX != playerHomingPoint.renderX) {
                                    playerHomingPoint.renderX = PZMath.lerp(playerHomingPoint.renderX, playerHomingPoint.targRenderX, movementLerpVal * GameTime.instance.getMultiplier());
                                }
                                if (playerHomingPoint.targRenderY != playerHomingPoint.renderY) {
                                    playerHomingPoint.renderY = PZMath.lerp(playerHomingPoint.renderY, playerHomingPoint.targRenderY, movementLerpVal * GameTime.instance.getMultiplier());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void updateGridSquareMarkers() {
        if (IsoCamera.frameState.playerIndex != 0) {
            return;
        }
        if (this.gridSquareMarkers.size() == 0) {
            return;
        }
        for (int i = this.gridSquareMarkers.size() - 1; i >= 0; --i) {
            if (this.gridSquareMarkers.get(i).isRemoved()) {
                this.gridSquareMarkers.remove(i);
            }
        }
        for (int j = 0; j < this.gridSquareMarkers.size(); ++j) {
            final GridSquareMarker gridSquareMarker = this.gridSquareMarkers.get(j);
            if (gridSquareMarker.alphaInc) {
                final GridSquareMarker gridSquareMarker2 = gridSquareMarker;
                gridSquareMarker2.alpha += GameTime.getInstance().getMultiplier() * gridSquareMarker.fadeSpeed;
                if (gridSquareMarker.alpha > gridSquareMarker.alphaMax) {
                    gridSquareMarker.alphaInc = false;
                    gridSquareMarker.alpha = gridSquareMarker.alphaMax;
                }
            }
            else {
                final GridSquareMarker gridSquareMarker3 = gridSquareMarker;
                gridSquareMarker3.alpha -= GameTime.getInstance().getMultiplier() * gridSquareMarker.fadeSpeed;
                if (gridSquareMarker.alpha < gridSquareMarker.alphaMin) {
                    gridSquareMarker.alphaInc = true;
                    gridSquareMarker.alpha = 0.3f;
                }
            }
        }
    }
    
    public boolean removeGridSquareMarker(final GridSquareMarker gridSquareMarker) {
        return this.removeGridSquareMarker(gridSquareMarker.getID());
    }
    
    public boolean removeGridSquareMarker(final int n) {
        for (int i = this.gridSquareMarkers.size() - 1; i >= 0; --i) {
            if (this.gridSquareMarkers.get(i).getID() == n) {
                this.gridSquareMarkers.get(i).remove();
                this.gridSquareMarkers.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public GridSquareMarker getGridSquareMarker(final int n) {
        for (int i = 0; i < this.gridSquareMarkers.size(); ++i) {
            if (this.gridSquareMarkers.get(i).getID() == n) {
                return this.gridSquareMarkers.get(i);
            }
        }
        return null;
    }
    
    public GridSquareMarker addGridSquareMarker(final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b, final float n4) {
        return this.addGridSquareMarker("circle_center", "circle_only_highlight", isoGridSquare, n, n2, n3, b, n4, 0.006f, 0.3f, 1.0f);
    }
    
    public GridSquareMarker addGridSquareMarker(final String s, final String s2, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b, final float n4) {
        return this.addGridSquareMarker(s, s2, isoGridSquare, n, n2, n3, b, n4, 0.006f, 0.3f, 1.0f);
    }
    
    public GridSquareMarker addGridSquareMarker(final String s, final String s2, final IsoGridSquare isoGridSquare, final float r, final float g, final float b, final boolean doAlpha, final float n, final float fadeSpeed, final float alphaMin, final float alphaMax) {
        if (GameServer.bServer) {
            return null;
        }
        final GridSquareMarker gridSquareMarker = new GridSquareMarker();
        gridSquareMarker.init(s, s2, isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, n);
        gridSquareMarker.setR(r);
        gridSquareMarker.setG(g);
        gridSquareMarker.setB(b);
        gridSquareMarker.setA(1.0f);
        gridSquareMarker.setDoAlpha(doAlpha);
        gridSquareMarker.setFadeSpeed(fadeSpeed);
        gridSquareMarker.setAlpha(0.0f);
        gridSquareMarker.setAlphaMin(alphaMin);
        gridSquareMarker.setAlphaMax(alphaMax);
        this.gridSquareMarkers.add(gridSquareMarker);
        return gridSquareMarker;
    }
    
    public void renderGridSquareMarkers(final IsoCell.PerPlayerRender perPlayerRender, final int n, final int n2) {
        if (GameServer.bServer || this.gridSquareMarkers.size() == 0) {
            return;
        }
        final IsoPlayer isoPlayer = IsoPlayer.players[n2];
        if (isoPlayer == null) {
            return;
        }
        for (int i = 0; i < this.gridSquareMarkers.size(); ++i) {
            final GridSquareMarker gridSquareMarker = this.gridSquareMarkers.get(i);
            if (gridSquareMarker.z == n && gridSquareMarker.z == isoPlayer.getZ()) {
                if (gridSquareMarker.active) {
                    final float n3 = 0.0f;
                    final float n4 = 0.0f;
                    WorldMarkers.stCol.set(gridSquareMarker.r, gridSquareMarker.g, gridSquareMarker.b, gridSquareMarker.a);
                    if (gridSquareMarker.doBlink) {
                        gridSquareMarker.sprite.alpha = Core.blinkAlpha;
                    }
                    else {
                        gridSquareMarker.sprite.alpha = (gridSquareMarker.doAlpha ? gridSquareMarker.alpha : 1.0f);
                    }
                    gridSquareMarker.sprite.render(null, gridSquareMarker.x, gridSquareMarker.y, gridSquareMarker.z, IsoDirections.N, n3, n4, WorldMarkers.stCol);
                    if (gridSquareMarker.spriteOverlay != null) {
                        gridSquareMarker.spriteOverlay.alpha = 1.0f;
                        gridSquareMarker.spriteOverlay.render(null, gridSquareMarker.x, gridSquareMarker.y, gridSquareMarker.z, IsoDirections.N, n3, n4, WorldMarkers.stCol);
                    }
                }
            }
        }
    }
    
    public void debugRender() {
    }
    
    public void render() {
        this.update();
        this.renderHomingPoint();
        this.renderDirectionArrow(false);
    }
    
    public void renderHomingPoint() {
        if (GameServer.bServer) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        for (int i = 0; i < this.homingPoints.length; ++i) {
            if (i == playerIndex) {
                if (this.homingPoints[i].size() != 0) {
                    for (int j = 0; j < this.homingPoints[i].size(); ++j) {
                        final PlayerHomingPoint playerHomingPoint = this.homingPoints[i].get(j);
                        if (playerHomingPoint.active && playerHomingPoint.texture != null) {
                            float n = 180.0f - playerHomingPoint.angle;
                            if (n < 0.0f) {
                                n += 360.0f;
                            }
                            float n2 = playerHomingPoint.a;
                            if (ClimateManager.getInstance().getFogIntensity() > 0.0f && n2 < 1.0f) {
                                n2 = PZMath.clamp_01(n2 + (1.0f - n2) * ClimateManager.getInstance().getFogIntensity() * 2.0f);
                            }
                            this.DrawTextureAngle(playerHomingPoint.texture, playerHomingPoint.renderWidth, playerHomingPoint.renderHeight, playerHomingPoint.renderX, playerHomingPoint.renderY, n, playerHomingPoint.r, playerHomingPoint.g, playerHomingPoint.b, n2, playerHomingPoint.renderSizeMod);
                        }
                    }
                }
            }
        }
    }
    
    public void renderDirectionArrow(final boolean b) {
        if (GameServer.bServer) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        for (int i = 0; i < this.directionArrows.length; ++i) {
            if (i == playerIndex) {
                if (this.directionArrows[i].size() != 0) {
                    for (int j = 0; j < this.directionArrows[i].size(); ++j) {
                        final DirectionArrow directionArrow = this.directionArrows[i].get(j);
                        if (directionArrow.active && directionArrow.renderTexture != null && directionArrow.isDrawOnWorld == b) {
                            float n = 0.0f;
                            if (directionArrow.renderWithAngle) {
                                n = 180.0f - directionArrow.angle;
                                if (n < 0.0f) {
                                    n += 360.0f;
                                }
                            }
                            this.DrawTextureAngle(directionArrow.renderTexture, directionArrow.renderWidth, directionArrow.renderHeight, directionArrow.renderScreenX, directionArrow.renderScreenY, n, directionArrow.r, directionArrow.g, directionArrow.b, directionArrow.a, directionArrow.renderSizeMod);
                        }
                    }
                }
            }
        }
    }
    
    private void DrawTextureAngle(final Texture texture, final float n, final float n2, final double n3, final double n4, final double n5, final float n6, final float n7, final float n8, final float n9, final float n10) {
        final float n11 = n * n10 / 2.0f;
        final float n12 = n2 * n10 / 2.0f;
        final double radians = Math.toRadians(180.0 + n5);
        final double n13 = Math.cos(radians) * n11;
        final double n14 = Math.sin(radians) * n11;
        final double n15 = Math.cos(radians) * n12;
        final double n16 = Math.sin(radians) * n12;
        SpriteRenderer.instance.render(texture, n13 - n16 + n3, n15 + n14 + n4, -n13 - n16 + n3, n15 - n14 + n4, -n13 + n16 + n3, -n15 - n14 + n4, n13 + n16 + n3, -n15 + n14 + n4, n6, n7, n8, n9, n6, n7, n8, n9, n6, n7, n8, n9, n6, n7, n8, n9, null);
    }
    
    public static boolean intersectLineSegments(final Line line, final Line line2, final Point point) {
        final float x = line.s.x;
        final float y = line.s.y;
        final float x2 = line.e.x;
        final float y2 = line.e.y;
        final float x3 = line2.s.x;
        final float y3 = line2.s.y;
        final float x4 = line2.e.x;
        final float y4 = line2.e.y;
        final float n = (y4 - y3) * (x2 - x) - (x4 - x3) * (y2 - y);
        if (n == 0.0f) {
            return false;
        }
        final float n2 = y - y3;
        final float n3 = x - x3;
        final float n4 = ((x4 - x3) * n2 - (y4 - y3) * n3) / n;
        if (n4 < 0.0f || n4 > 1.0f) {
            return false;
        }
        final float n5 = ((x2 - x) * n2 - (y2 - y) * n3) / n;
        if (n5 < 0.0f || n5 > 1.0f) {
            return false;
        }
        if (point != null) {
            point.set(x + (x2 - x) * n4, y + (y2 - y) * n4);
        }
        return true;
    }
    
    static {
        instance = new WorldMarkers();
        WorldMarkers.NextGridSquareMarkerID = 0;
        WorldMarkers.NextHomingPointID = 0;
        stCol = new ColorInfo();
    }
    
    public static final class GridSquareMarker
    {
        private int ID;
        private IsoSpriteInstance sprite;
        private IsoSpriteInstance spriteOverlay;
        private float orig_x;
        private float orig_y;
        private float orig_z;
        private float x;
        private float y;
        private float z;
        private float scaleRatio;
        private float r;
        private float g;
        private float b;
        private float a;
        private float size;
        private boolean doBlink;
        private boolean doAlpha;
        private boolean bScaleCircleTexture;
        private float fadeSpeed;
        private float alpha;
        private float alphaMax;
        private float alphaMin;
        private boolean alphaInc;
        private boolean active;
        private boolean isRemoved;
        
        public GridSquareMarker() {
            this.doBlink = false;
            this.bScaleCircleTexture = false;
            this.fadeSpeed = 0.006f;
            this.alpha = 0.0f;
            this.alphaMax = 1.0f;
            this.alphaMin = 0.3f;
            this.alphaInc = true;
            this.active = true;
            this.isRemoved = false;
            this.ID = WorldMarkers.NextGridSquareMarkerID++;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public void remove() {
            this.isRemoved = true;
        }
        
        public boolean isRemoved() {
            return this.isRemoved;
        }
        
        public void init(String s, final String s2, final int n, final int n2, final int n3, final float n4) {
            if (s == null) {
                s = "circle_center";
            }
            this.scaleRatio = 1.0f / (Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)).getWidth() / (64.0f * Core.TileScale));
            this.sprite = new IsoSpriteInstance(IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)));
            if (s2 != null) {
                this.spriteOverlay = new IsoSpriteInstance(IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2)));
            }
            this.setPosAndSize(n, n2, n3, n4);
        }
        
        public void setPosAndSize(final int n, final int n2, final int n3, final float size) {
            final float n4 = size * (this.bScaleCircleTexture ? 1.5f : 1.0f);
            final float n5 = this.scaleRatio * n4;
            this.sprite.setScale(n5, n5);
            if (this.spriteOverlay != null) {
                this.spriteOverlay.setScale(n5, n5);
            }
            this.size = size;
            this.orig_x = (float)n;
            this.orig_y = (float)n2;
            this.orig_z = (float)n3;
            this.x = n - (n4 - 0.5f);
            this.y = n2 + 0.5f;
            this.z = (float)n3;
        }
        
        public void setPos(final int n, final int n2, final int n3) {
            final float n4 = this.size * (this.bScaleCircleTexture ? 1.5f : 1.0f);
            this.orig_x = (float)n;
            this.orig_y = (float)n2;
            this.orig_z = (float)n3;
            this.x = n - (n4 - 0.5f);
            this.y = n2 + 0.5f;
            this.z = (float)n3;
        }
        
        public void setSize(final float size) {
            final float n = size * (this.bScaleCircleTexture ? 1.5f : 1.0f);
            final float n2 = this.scaleRatio * n;
            this.sprite.setScale(n2, n2);
            if (this.spriteOverlay != null) {
                this.spriteOverlay.setScale(n2, n2);
            }
            this.size = size;
            this.x = this.orig_x - (n - 0.5f);
            this.y = this.orig_y + 0.5f;
            this.z = this.orig_z;
        }
        
        public boolean isActive() {
            return this.active;
        }
        
        public void setActive(final boolean active) {
            this.active = active;
        }
        
        public float getSize() {
            return this.size;
        }
        
        public float getX() {
            return this.x;
        }
        
        public float getY() {
            return this.y;
        }
        
        public float getZ() {
            return this.z;
        }
        
        public float getR() {
            return this.r;
        }
        
        public void setR(final float r) {
            this.r = r;
        }
        
        public float getG() {
            return this.g;
        }
        
        public void setG(final float g) {
            this.g = g;
        }
        
        public float getB() {
            return this.b;
        }
        
        public void setB(final float b) {
            this.b = b;
        }
        
        public float getA() {
            return this.a;
        }
        
        public void setA(final float a) {
            this.a = a;
        }
        
        public float getAlpha() {
            return this.alpha;
        }
        
        public void setAlpha(final float alpha) {
            this.alpha = alpha;
        }
        
        public float getAlphaMax() {
            return this.alphaMax;
        }
        
        public void setAlphaMax(final float alphaMax) {
            this.alphaMax = alphaMax;
        }
        
        public float getAlphaMin() {
            return this.alphaMin;
        }
        
        public void setAlphaMin(final float alphaMin) {
            this.alphaMin = alphaMin;
        }
        
        public boolean isDoAlpha() {
            return this.doAlpha;
        }
        
        public void setDoAlpha(final boolean doAlpha) {
            this.doAlpha = doAlpha;
        }
        
        public float getFadeSpeed() {
            return this.fadeSpeed;
        }
        
        public void setFadeSpeed(final float fadeSpeed) {
            this.fadeSpeed = fadeSpeed;
        }
        
        public boolean isDoBlink() {
            return this.doBlink;
        }
        
        public void setDoBlink(final boolean doBlink) {
            this.doBlink = doBlink;
        }
        
        public boolean isScaleCircleTexture() {
            return this.bScaleCircleTexture;
        }
        
        public void setScaleCircleTexture(final boolean bScaleCircleTexture) {
            this.bScaleCircleTexture = bScaleCircleTexture;
            final float n = this.size * (this.bScaleCircleTexture ? 1.5f : 1.0f);
            final float n2 = this.scaleRatio * n;
            if (this.sprite != null) {
                this.sprite.setScale(n2, n2);
            }
            if (this.spriteOverlay != null) {
                this.spriteOverlay.setScale(n2, n2);
            }
            this.x = this.orig_x - (n - 0.5f);
        }
    }
    
    public static class PlayerHomingPoint
    {
        private int ID;
        private Texture texture;
        private int x;
        private int y;
        private float r;
        private float g;
        private float b;
        private float a;
        private float angle;
        private float targetAngle;
        private boolean customTargetAngle;
        private float angleLerpVal;
        private float movementLerpVal;
        private int dist;
        private float targRenderX;
        private float targRenderY;
        private float renderX;
        private float renderY;
        private float renderOffsetX;
        private float renderOffsetY;
        private float renderWidth;
        private float renderHeight;
        private float renderSizeMod;
        private float targetScreenX;
        private float targetScreenY;
        private boolean targetOnScreen;
        private float stickToCharDist;
        private boolean active;
        private boolean homeOnTargetInView;
        private int homeOnTargetDist;
        private float homeOnOffsetX;
        private float homeOnOffsetY;
        private boolean isRemoved;
        
        public PlayerHomingPoint(final int n) {
            this.angle = 0.0f;
            this.targetAngle = 0.0f;
            this.customTargetAngle = false;
            this.angleLerpVal = 0.25f;
            this.movementLerpVal = 0.25f;
            this.dist = 0;
            this.targRenderX = Core.getInstance().getScreenWidth() / 2.0f;
            this.targRenderY = Core.getInstance().getScreenHeight() / 2.0f;
            this.renderX = this.targRenderX;
            this.renderY = this.targRenderY;
            this.renderOffsetX = 0.0f;
            this.renderOffsetY = 50.0f;
            this.renderWidth = 32.0f;
            this.renderHeight = 32.0f;
            this.renderSizeMod = 1.0f;
            this.targetOnScreen = false;
            this.stickToCharDist = 130.0f;
            this.homeOnTargetInView = true;
            this.homeOnTargetDist = 20;
            this.homeOnOffsetX = 0.0f;
            this.homeOnOffsetY = 0.0f;
            this.isRemoved = false;
            this.ID = WorldMarkers.NextHomingPointID++;
            final float n2 = (float)IsoCamera.getScreenLeft(n);
            final float n3 = (float)IsoCamera.getScreenTop(n);
            final float n4 = (float)IsoCamera.getScreenWidth(n);
            final float n5 = (float)IsoCamera.getScreenHeight(n);
            this.targRenderX = n2 + n4 / 2.0f;
            this.targRenderY = n3 + n5 / 2.0f;
        }
        
        public void setTexture(String s) {
            if (s == null) {
                s = "arrow_triangle";
            }
            this.texture = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        
        public void remove() {
            this.isRemoved = true;
        }
        
        public boolean isRemoved() {
            return this.isRemoved;
        }
        
        public boolean isActive() {
            return this.active;
        }
        
        public void setActive(final boolean active) {
            this.active = active;
        }
        
        public float getR() {
            return this.r;
        }
        
        public void setR(final float r) {
            this.r = r;
        }
        
        public float getB() {
            return this.b;
        }
        
        public void setB(final float b) {
            this.b = b;
        }
        
        public float getG() {
            return this.g;
        }
        
        public void setG(final float g) {
            this.g = g;
        }
        
        public float getA() {
            return this.a;
        }
        
        public void setA(final float a) {
            this.a = a;
        }
        
        public int getHomeOnTargetDist() {
            return this.homeOnTargetDist;
        }
        
        public void setHomeOnTargetDist(final int homeOnTargetDist) {
            this.homeOnTargetDist = homeOnTargetDist;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public float getTargetAngle() {
            return this.targetAngle;
        }
        
        public void setTargetAngle(final float targetAngle) {
            this.targetAngle = targetAngle;
        }
        
        public boolean isCustomTargetAngle() {
            return this.customTargetAngle;
        }
        
        public void setCustomTargetAngle(final boolean customTargetAngle) {
            this.customTargetAngle = customTargetAngle;
        }
        
        public int getX() {
            return this.x;
        }
        
        public void setX(final int x) {
            this.x = x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public void setY(final int y) {
            this.y = y;
        }
        
        public float getAngleLerpVal() {
            return this.angleLerpVal;
        }
        
        public void setAngleLerpVal(final float angleLerpVal) {
            this.angleLerpVal = angleLerpVal;
        }
        
        public float getMovementLerpVal() {
            return this.movementLerpVal;
        }
        
        public void setMovementLerpVal(final float movementLerpVal) {
            this.movementLerpVal = movementLerpVal;
        }
        
        public boolean isHomeOnTargetInView() {
            return this.homeOnTargetInView;
        }
        
        public void setHomeOnTargetInView(final boolean homeOnTargetInView) {
            this.homeOnTargetInView = homeOnTargetInView;
        }
        
        public float getRenderWidth() {
            return this.renderWidth;
        }
        
        public void setRenderWidth(final float renderWidth) {
            this.renderWidth = renderWidth;
        }
        
        public float getRenderHeight() {
            return this.renderHeight;
        }
        
        public void setRenderHeight(final float renderHeight) {
            this.renderHeight = renderHeight;
        }
        
        public float getStickToCharDist() {
            return this.stickToCharDist;
        }
        
        public void setStickToCharDist(final float stickToCharDist) {
            this.stickToCharDist = stickToCharDist;
        }
        
        public float getRenderOffsetX() {
            return this.renderOffsetX;
        }
        
        public void setRenderOffsetX(final float renderOffsetX) {
            this.renderOffsetX = renderOffsetX;
        }
        
        public float getRenderOffsetY() {
            return this.renderOffsetY;
        }
        
        public void setRenderOffsetY(final float renderOffsetY) {
            this.renderOffsetY = renderOffsetY;
        }
        
        public float getHomeOnOffsetX() {
            return this.homeOnOffsetX;
        }
        
        public void setHomeOnOffsetX(final float homeOnOffsetX) {
            this.homeOnOffsetX = homeOnOffsetX;
        }
        
        public float getHomeOnOffsetY() {
            return this.homeOnOffsetY;
        }
        
        public void setHomeOnOffsetY(final float homeOnOffsetY) {
            this.homeOnOffsetY = homeOnOffsetY;
        }
        
        public void setTableSurface() {
            this.homeOnOffsetY = -30.0f * Core.TileScale;
        }
        
        public void setHighCounter() {
            this.homeOnOffsetY = -50.0f * Core.TileScale;
        }
        
        public void setYOffsetScaled(final float n) {
            this.homeOnOffsetY = n * Core.TileScale;
        }
        
        public void setXOffsetScaled(final float n) {
            this.homeOnOffsetX = n * Core.TileScale;
        }
    }
    
    class PlayerHomingPointList extends ArrayList<PlayerHomingPoint>
    {
    }
    
    public class DirectionArrow
    {
        public static final boolean doDebug = false;
        private DebugStuff debugStuff;
        private int ID;
        private boolean active;
        private boolean isRemoved;
        private boolean isDrawOnWorld;
        private Texture renderTexture;
        private Texture texture;
        private Texture texStairsUp;
        private Texture texStairsDown;
        private Texture texDown;
        private int x;
        private int y;
        private int z;
        private float r;
        private float g;
        private float b;
        private float a;
        private float renderWidth;
        private float renderHeight;
        private float angle;
        private float angleLerpVal;
        private boolean lastWasWithinView;
        private float renderScreenX;
        private float renderScreenY;
        private boolean renderWithAngle;
        private float renderSizeMod;
        
        public DirectionArrow(final int n) {
            this.active = true;
            this.isRemoved = false;
            this.isDrawOnWorld = false;
            this.renderWidth = 32.0f;
            this.renderHeight = 32.0f;
            this.angleLerpVal = 0.25f;
            this.lastWasWithinView = true;
            this.renderWithAngle = true;
            this.renderSizeMod = 1.0f;
            if (Core.bDebug) {}
            this.ID = WorldMarkers.NextHomingPointID++;
        }
        
        public void setTexture(String s) {
            if (s == null) {
                s = "dir_arrow_up";
            }
            this.texture = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        
        public void setTexDown(final String s) {
            this.texDown = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        
        public void setTexStairsDown(final String s) {
            this.texStairsDown = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        
        public void setTexStairsUp(final String s) {
            this.texStairsUp = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        
        public void remove() {
            this.isRemoved = true;
        }
        
        public boolean isRemoved() {
            return this.isRemoved;
        }
        
        public boolean isActive() {
            return this.active;
        }
        
        public void setActive(final boolean active) {
            this.active = active;
        }
        
        public float getR() {
            return this.r;
        }
        
        public void setR(final float r) {
            this.r = r;
        }
        
        public float getB() {
            return this.b;
        }
        
        public void setB(final float b) {
            this.b = b;
        }
        
        public float getG() {
            return this.g;
        }
        
        public void setG(final float g) {
            this.g = g;
        }
        
        public float getA() {
            return this.a;
        }
        
        public void setA(final float a) {
            this.a = a;
        }
        
        public void setRGBA(final float r, final float g, final float b, final float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
        
        public int getID() {
            return this.ID;
        }
        
        public int getX() {
            return this.x;
        }
        
        public void setX(final int x) {
            this.x = x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public void setY(final int y) {
            this.y = y;
        }
        
        public int getZ() {
            return this.z;
        }
        
        public void setZ(final int z) {
            this.z = z;
        }
        
        public float getRenderWidth() {
            return this.renderWidth;
        }
        
        public void setRenderWidth(final float renderWidth) {
            this.renderWidth = renderWidth;
        }
        
        public float getRenderHeight() {
            return this.renderHeight;
        }
        
        public void setRenderHeight(final float renderHeight) {
            this.renderHeight = renderHeight;
        }
        
        private class DebugStuff
        {
            private float centerX;
            private float centerY;
            private float endX;
            private float endY;
        }
    }
    
    class DirectionArrowList extends ArrayList<DirectionArrow>
    {
    }
    
    class PlayerScreen
    {
        private float centerX;
        private float centerY;
        private float x;
        private float y;
        private float width;
        private float height;
        private float padTop;
        private float padLeft;
        private float padBot;
        private float padRight;
        private float innerX;
        private float innerY;
        private float innerX2;
        private float innerY2;
        private Line borderTop;
        private Line borderRight;
        private Line borderBot;
        private Line borderLeft;
        private Line[] borders;
        
        PlayerScreen() {
            this.padTop = 100.0f;
            this.padLeft = 100.0f;
            this.padBot = 100.0f;
            this.padRight = 100.0f;
            this.borderTop = new Line(new Point(0.0f, 0.0f), new Point(0.0f, 0.0f));
            this.borderRight = new Line(new Point(0.0f, 0.0f), new Point(0.0f, 0.0f));
            this.borderBot = new Line(new Point(0.0f, 0.0f), new Point(0.0f, 0.0f));
            this.borderLeft = new Line(new Point(0.0f, 0.0f), new Point(0.0f, 0.0f));
            this.borders = new Line[4];
        }
        
        private void update(final int n) {
            this.x = 0.0f;
            this.y = 0.0f;
            this.width = (float)IsoCamera.getScreenWidth(n);
            this.height = (float)IsoCamera.getScreenHeight(n);
            this.centerX = this.x + this.width / 2.0f;
            this.centerY = this.y + this.height / 2.0f;
            this.innerX = this.x + this.padLeft;
            this.innerY = this.y + this.padTop;
            final float n2 = this.width - (this.padLeft + this.padRight);
            final float n3 = this.height - (this.padTop + this.padBot);
            this.innerX2 = this.innerX + n2;
            this.innerY2 = this.innerY + n3;
        }
        
        private Line[] getBorders() {
            this.borders[0] = this.getBorderTop();
            this.borders[1] = this.getBorderRight();
            this.borders[2] = this.getBorderBot();
            this.borders[3] = this.getBorderLeft();
            return this.borders;
        }
        
        private Line getBorderTop() {
            this.borderTop.s.set(this.innerX, this.innerY);
            this.borderTop.e.set(this.innerX2, this.innerY);
            return this.borderTop;
        }
        
        private Line getBorderRight() {
            this.borderRight.s.set(this.innerX2, this.innerY);
            this.borderRight.e.set(this.innerX2, this.innerY2);
            return this.borderRight;
        }
        
        private Line getBorderBot() {
            this.borderBot.s.set(this.innerX, this.innerY2);
            this.borderBot.e.set(this.innerX2, this.innerY2);
            return this.borderBot;
        }
        
        private Line getBorderLeft() {
            this.borderLeft.s.set(this.innerX, this.innerY);
            this.borderLeft.e.set(this.innerX, this.innerY2);
            return this.borderLeft;
        }
        
        private float clampToInnerX(final float n) {
            return PZMath.clamp(n, this.innerX, this.innerX2);
        }
        
        private float clampToInnerY(final float n) {
            return PZMath.clamp(n, this.innerY, this.innerY2);
        }
        
        private boolean isOnScreen(final float n, final float n2) {
            return n >= this.x && n < this.x + this.width && n2 >= this.y && n2 < this.y + this.height;
        }
        
        private boolean isWithinInner(final float n, final float n2) {
            return n >= this.innerX && n < this.innerX2 && n2 >= this.innerY && n2 < this.innerY2;
        }
    }
    
    private static class Point
    {
        float x;
        float y;
        
        Point(final float x, final float y) {
            this.x = x;
            this.y = y;
        }
        
        public Point set(final float x, final float y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        public boolean notInfinite() {
            return !Float.isInfinite(this.x) && !Float.isInfinite(this.y);
        }
        
        @Override
        public String toString() {
            return String.format("{%f, %f}", this.x, this.y);
        }
    }
    
    private static class Line
    {
        Point s;
        Point e;
        
        Line(final Point s, final Point e) {
            this.s = s;
            this.e = e;
        }
        
        @Override
        public String toString() {
            return String.format("{s: %s, e: %s}", this.s.toString(), this.e.toString());
        }
    }
}
