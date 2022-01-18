// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.debug.LineDrawer;
import zombie.iso.SpriteDetails.IsoFlagType;

public class NearestWalls
{
    private static final int CPW = 10;
    private static final int CPWx4 = 40;
    private static final int LEVELS = 8;
    private static int CHANGE_COUNT;
    private static int renderX;
    private static int renderY;
    private static int renderZ;
    
    public static void chunkLoaded(final IsoChunk isoChunk) {
        ++NearestWalls.CHANGE_COUNT;
        if (NearestWalls.CHANGE_COUNT < 0) {
            NearestWalls.CHANGE_COUNT = 0;
        }
        isoChunk.nearestWalls.changeCount = -1;
    }
    
    private static void calcDistanceOnThisChunkOnly(final IsoChunk isoChunk) {
        final byte[] distanceSelf = isoChunk.nearestWalls.distanceSelf;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                int n = -1;
                for (int k = 0; k < 10; ++k) {
                    isoChunk.nearestWalls.closest[k + j * 10 + i * 10 * 10] = -1;
                    final int n2 = k * 4 + j * 40 + i * 10 * 40;
                    distanceSelf[n2 + 0] = (byte)((n == -1) ? -1 : ((byte)(k - n)));
                    distanceSelf[n2 + 1] = -1;
                    final IsoGridSquare gridSquare = isoChunk.getGridSquare(k, j, i);
                    if (gridSquare != null) {
                        if (gridSquare.Is(IsoFlagType.WallW) || gridSquare.Is(IsoFlagType.DoorWallW) || gridSquare.Is(IsoFlagType.WallNW) || gridSquare.Is(IsoFlagType.WindowW)) {
                            n = (byte)k;
                            distanceSelf[n2 + 0] = 0;
                            for (int l = k - 1; l >= 0; --l) {
                                final int n3 = l * 4 + j * 40 + i * 10 * 40;
                                if (distanceSelf[n3 + 1] != -1) {
                                    break;
                                }
                                distanceSelf[n3 + 1] = (byte)(n - l);
                            }
                        }
                    }
                }
            }
            for (int n4 = 0; n4 < 10; ++n4) {
                int n5 = -1;
                for (int n6 = 0; n6 < 10; ++n6) {
                    final int n7 = n4 * 4 + n6 * 40 + i * 10 * 40;
                    distanceSelf[n7 + 2] = (byte)((n5 == -1) ? -1 : ((byte)(n6 - n5)));
                    distanceSelf[n7 + 3] = -1;
                    final IsoGridSquare gridSquare2 = isoChunk.getGridSquare(n4, n6, i);
                    if (gridSquare2 != null) {
                        if (gridSquare2.Is(IsoFlagType.WallN) || gridSquare2.Is(IsoFlagType.DoorWallN) || gridSquare2.Is(IsoFlagType.WallNW) || gridSquare2.Is(IsoFlagType.WindowN)) {
                            n5 = (byte)n6;
                            distanceSelf[n7 + 2] = 0;
                            for (int n8 = n6 - 1; n8 >= 0; --n8) {
                                final int n9 = n4 * 4 + n8 * 40 + i * 10 * 40;
                                if (distanceSelf[n9 + 3] != -1) {
                                    break;
                                }
                                distanceSelf[n9 + 3] = (byte)(n5 - n8);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static int getIndex(final IsoChunk isoChunk, final int n, final int n2, final int n3) {
        return (n - isoChunk.wx * 10) * 4 + (n2 - isoChunk.wy * 10) * 40 + n3 * 10 * 40;
    }
    
    private static int getNearestWallOnSameChunk(final IsoChunk isoChunk, final int n, final int n2, final int n3, final int n4) {
        final ChunkData nearestWalls = isoChunk.nearestWalls;
        if (nearestWalls.changeCount != NearestWalls.CHANGE_COUNT) {
            calcDistanceOnThisChunkOnly(isoChunk);
            nearestWalls.changeCount = NearestWalls.CHANGE_COUNT;
        }
        return nearestWalls.distanceSelf[getIndex(isoChunk, n, n2, n3) + n4];
    }
    
    private static boolean hasWall(final IsoChunk isoChunk, final int n, final int n2, final int n3, final int n4) {
        return getNearestWallOnSameChunk(isoChunk, n, n2, n3, n4) == 0;
    }
    
    private static int getNearestWallWest(final IsoChunk isoChunk, final int n, final int n2, final int n3) {
        final int n4 = 0;
        final int n5 = -1;
        final int n6 = 0;
        final int nearestWallOnSameChunk = getNearestWallOnSameChunk(isoChunk, n, n2, n3, n4);
        if (nearestWallOnSameChunk != -1) {
            return n - nearestWallOnSameChunk;
        }
        for (int i = 1; i <= 3; ++i) {
            final IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(isoChunk.wx + i * n5, isoChunk.wy + i * n6);
            if (chunk == null) {
                break;
            }
            final int n7 = (chunk.wx + 1) * 10 - 1;
            final int nearestWallOnSameChunk2 = getNearestWallOnSameChunk(chunk, n7, n2, n3, n4);
            if (nearestWallOnSameChunk2 != -1) {
                return n7 - nearestWallOnSameChunk2;
            }
        }
        return -1;
    }
    
    private static int getNearestWallEast(final IsoChunk isoChunk, final int n, final int n2, final int n3) {
        final int n4 = 1;
        final int n5 = 1;
        final int n6 = 0;
        final int nearestWallOnSameChunk = getNearestWallOnSameChunk(isoChunk, n, n2, n3, n4);
        if (nearestWallOnSameChunk != -1) {
            return n + nearestWallOnSameChunk;
        }
        for (int i = 1; i <= 3; ++i) {
            final IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(isoChunk.wx + i * n5, isoChunk.wy + i * n6);
            if (chunk == null) {
                break;
            }
            final int n7 = chunk.wx * 10;
            final int n8 = hasWall(chunk, n7, n2, n3, 0) ? 0 : getNearestWallOnSameChunk(chunk, n7, n2, n3, n4);
            if (n8 != -1) {
                return n7 + n8;
            }
        }
        return -1;
    }
    
    private static int getNearestWallNorth(final IsoChunk isoChunk, final int n, final int n2, final int n3) {
        final int n4 = 2;
        final int n5 = 0;
        final int n6 = -1;
        final int nearestWallOnSameChunk = getNearestWallOnSameChunk(isoChunk, n, n2, n3, n4);
        if (nearestWallOnSameChunk != -1) {
            return n2 - nearestWallOnSameChunk;
        }
        for (int i = 1; i <= 3; ++i) {
            final IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(isoChunk.wx + i * n5, isoChunk.wy + i * n6);
            if (chunk == null) {
                break;
            }
            final int n7 = (chunk.wy + 1) * 10 - 1;
            final int nearestWallOnSameChunk2 = getNearestWallOnSameChunk(chunk, n, n7, n3, n4);
            if (nearestWallOnSameChunk2 != -1) {
                return n7 - nearestWallOnSameChunk2;
            }
        }
        return -1;
    }
    
    private static int getNearestWallSouth(final IsoChunk isoChunk, final int n, final int n2, final int n3) {
        final int n4 = 3;
        final int n5 = 0;
        final int n6 = 1;
        final int nearestWallOnSameChunk = getNearestWallOnSameChunk(isoChunk, n, n2, n3, n4);
        if (nearestWallOnSameChunk != -1) {
            return n2 + nearestWallOnSameChunk;
        }
        for (int i = 1; i <= 3; ++i) {
            final IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(isoChunk.wx + i * n5, isoChunk.wy + i * n6);
            if (chunk == null) {
                break;
            }
            final int n7 = chunk.wy * 10;
            final int n8 = hasWall(chunk, n, n7, n3, 2) ? 0 : getNearestWallOnSameChunk(chunk, n, n7, n3, n4);
            if (n8 != -1) {
                return n7 + n8;
            }
        }
        return -1;
    }
    
    public static void render(final int renderX, final int renderY, final int renderZ) {
        final IsoChunk chunkForGridSquare = IsoWorld.instance.CurrentCell.getChunkForGridSquare(renderX, renderY, renderZ);
        if (chunkForGridSquare == null) {
            return;
        }
        if (NearestWalls.renderX != renderX || NearestWalls.renderY != renderY || NearestWalls.renderZ != renderZ) {
            NearestWalls.renderX = renderX;
            NearestWalls.renderY = renderY;
            NearestWalls.renderZ = renderZ;
            System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ClosestWallDistance(chunkForGridSquare, renderX, renderY, renderZ)));
        }
        final int nearestWallWest = getNearestWallWest(chunkForGridSquare, renderX, renderY, renderZ);
        if (nearestWallWest != -1) {
            DrawIsoLine((float)nearestWallWest, renderY + 0.5f, renderX + 0.5f, renderY + 0.5f, (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
            DrawIsoLine((float)nearestWallWest, (float)renderY, (float)nearestWallWest, (float)(renderY + 1), (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
        }
        final int nearestWallEast = getNearestWallEast(chunkForGridSquare, renderX, renderY, renderZ);
        if (nearestWallEast != -1) {
            DrawIsoLine((float)nearestWallEast, renderY + 0.5f, renderX + 0.5f, renderY + 0.5f, (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
            DrawIsoLine((float)nearestWallEast, (float)renderY, (float)nearestWallEast, (float)(renderY + 1), (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
        }
        final int nearestWallNorth = getNearestWallNorth(chunkForGridSquare, renderX, renderY, renderZ);
        if (nearestWallNorth != -1) {
            DrawIsoLine(renderX + 0.5f, (float)nearestWallNorth, renderX + 0.5f, renderY + 0.5f, (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
            DrawIsoLine((float)renderX, (float)nearestWallNorth, (float)(renderX + 1), (float)nearestWallNorth, (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
        }
        final int nearestWallSouth = getNearestWallSouth(chunkForGridSquare, renderX, renderY, renderZ);
        if (nearestWallSouth != -1) {
            DrawIsoLine(renderX + 0.5f, (float)nearestWallSouth, renderX + 0.5f, renderY + 0.5f, (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
            DrawIsoLine((float)renderX, (float)nearestWallSouth, (float)(renderX + 1), (float)nearestWallSouth, (float)renderZ, 1.0f, 1.0f, 1.0f, 1.0f, 1);
        }
    }
    
    private static void DrawIsoLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final int n10) {
        LineDrawer.drawLine(IsoUtils.XToScreenExact(n, n2, n5, 0), IsoUtils.YToScreenExact(n, n2, n5, 0), IsoUtils.XToScreenExact(n3, n4, n5, 0), IsoUtils.YToScreenExact(n3, n4, n5, 0), n6, n7, n8, n9, n10);
    }
    
    public static int ClosestWallDistance(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare.chunk == null) {
            return 127;
        }
        return ClosestWallDistance(isoGridSquare.chunk, isoGridSquare.x, isoGridSquare.y, isoGridSquare.z);
    }
    
    public static int ClosestWallDistance(final IsoChunk isoChunk, final int n, final int n2, final int n3) {
        if (isoChunk == null) {
            return 127;
        }
        final ChunkData nearestWalls = isoChunk.nearestWalls;
        final byte[] closest = nearestWalls.closest;
        if (nearestWalls.changeCount != NearestWalls.CHANGE_COUNT) {
            calcDistanceOnThisChunkOnly(isoChunk);
            nearestWalls.changeCount = NearestWalls.CHANGE_COUNT;
        }
        final int n4 = n - isoChunk.wx * 10 + (n2 - isoChunk.wy * 10) * 10 + n3 * 10 * 10;
        final byte b = closest[n4];
        if (b != -1) {
            return b;
        }
        final int nearestWallWest = getNearestWallWest(isoChunk, n, n2, n3);
        final int nearestWallEast = getNearestWallEast(isoChunk, n, n2, n3);
        final int nearestWallNorth = getNearestWallNorth(isoChunk, n, n2, n3);
        final int nearestWallSouth = getNearestWallSouth(isoChunk, n, n2, n3);
        if (nearestWallWest == -1 && nearestWallEast == -1 && nearestWallNorth == -1 && nearestWallSouth == -1) {
            return closest[n4] = 127;
        }
        int a = -1;
        if (nearestWallWest != -1 && nearestWallEast != -1) {
            a = nearestWallEast - nearestWallWest;
        }
        int b2 = -1;
        if (nearestWallNorth != -1 && nearestWallSouth != -1) {
            b2 = nearestWallSouth - nearestWallNorth;
        }
        if (a != -1 && b2 != -1) {
            return closest[n4] = (byte)Math.min(a, b2);
        }
        if (a != -1) {
            return closest[n4] = (byte)a;
        }
        if (b2 != -1) {
            return closest[n4] = (byte)b2;
        }
        final IsoGridSquare gridSquare = isoChunk.getGridSquare(n - isoChunk.wx * 10, n2 - isoChunk.wy * 10, n3);
        if (gridSquare != null && gridSquare.isOutside()) {
            return closest[n4] = (byte)Math.min((nearestWallWest == -1) ? 127 : (n - nearestWallWest), Math.min((nearestWallEast == -1) ? 127 : (nearestWallEast - n - 1), Math.min((nearestWallNorth == -1) ? 127 : (n2 - nearestWallNorth), (nearestWallSouth == -1) ? 127 : (nearestWallSouth - n2 - 1))));
        }
        return closest[n4] = 127;
    }
    
    static {
        NearestWalls.CHANGE_COUNT = 0;
    }
    
    public static final class ChunkData
    {
        int changeCount;
        final byte[] distanceSelf;
        final byte[] closest;
        
        public ChunkData() {
            this.changeCount = -1;
            this.distanceSelf = new byte[3200];
            this.closest = new byte[800];
        }
    }
}
