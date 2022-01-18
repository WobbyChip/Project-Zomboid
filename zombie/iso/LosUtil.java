// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.characters.IsoGameCharacter;

public final class LosUtil
{
    public static int XSIZE;
    public static int YSIZE;
    public static int ZSIZE;
    public static byte[][][][] cachedresults;
    public static boolean[] cachecleared;
    
    public static void init(final int a, final int a2) {
        LosUtil.XSIZE = Math.min(a, 200);
        LosUtil.YSIZE = Math.min(a2, 200);
        LosUtil.cachedresults = new byte[LosUtil.XSIZE][LosUtil.YSIZE][LosUtil.ZSIZE][4];
    }
    
    public static TestResults lineClear(final IsoCell isoCell, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        return lineClear(isoCell, n, n2, n3, n4, n5, n6, b, 10000);
    }
    
    public static TestResults lineClear(final IsoCell isoCell, int i, int j, int k, final int n, final int n2, int n3, final boolean b, final int n4) {
        if (n3 == k - 1) {
            final IsoGridSquare gridSquare = isoCell.getGridSquare(n, n2, n3);
            if (gridSquare != null && gridSquare.HasElevatedFloor()) {
                n3 = k;
            }
        }
        TestResults clear = TestResults.Clear;
        final int a = n2 - j;
        final int a2 = n - i;
        final int n5 = n3 - k;
        final float n6 = 0.5f;
        final float n7 = 0.5f;
        IsoGridSquare gridSquare2 = isoCell.getGridSquare(i, j, k);
        int n8 = 0;
        int n9 = 0;
        if (Math.abs(a2) > Math.abs(a) && Math.abs(a2) > Math.abs(n5)) {
            final float n10 = a / (float)a2;
            final float n11 = n5 / (float)a2;
            float n12 = n6 + j;
            float n13 = n7 + k;
            final int n14 = (a2 < 0) ? -1 : 1;
            final float n15 = n10 * n14;
            final float n16 = n11 * n14;
            while (i != n) {
                i += n14;
                n12 += n15;
                n13 += n16;
                final IsoGridSquare gridSquare3 = isoCell.getGridSquare(i, (int)n12, (int)n13);
                if (gridSquare3 != null && gridSquare2 != null) {
                    final TestResults testVisionAdjacent = gridSquare3.testVisionAdjacent(gridSquare2.getX() - gridSquare3.getX(), gridSquare2.getY() - gridSquare3.getY(), gridSquare2.getZ() - gridSquare3.getZ(), true, b);
                    if (testVisionAdjacent == TestResults.ClearThroughWindow) {
                        n9 = 1;
                    }
                    if (testVisionAdjacent == TestResults.Blocked || clear == TestResults.Clear || (testVisionAdjacent == TestResults.ClearThroughWindow && clear == TestResults.ClearThroughOpenDoor)) {
                        clear = testVisionAdjacent;
                    }
                    else if (testVisionAdjacent == TestResults.ClearThroughClosedDoor && clear == TestResults.ClearThroughOpenDoor) {
                        clear = testVisionAdjacent;
                    }
                    if (clear == TestResults.Blocked) {
                        return TestResults.Blocked;
                    }
                    if (n9 != 0) {
                        if (n8 > n4) {
                            return TestResults.Blocked;
                        }
                        n8 = 0;
                    }
                }
                gridSquare2 = gridSquare3;
                final int n17 = (int)n12;
                final int n18 = (int)n13;
                ++n8;
                n9 = 0;
            }
        }
        else if (Math.abs(a) >= Math.abs(a2) && Math.abs(a) > Math.abs(n5)) {
            final float n19 = a2 / (float)a;
            final float n20 = n5 / (float)a;
            float n21 = n6 + i;
            float n22 = n7 + k;
            final int n23 = (a < 0) ? -1 : 1;
            final float n24 = n19 * n23;
            final float n25 = n20 * n23;
            while (j != n2) {
                j += n23;
                n21 += n24;
                n22 += n25;
                final IsoGridSquare gridSquare4 = isoCell.getGridSquare((int)n21, j, (int)n22);
                if (gridSquare4 != null && gridSquare2 != null) {
                    final TestResults testVisionAdjacent2 = gridSquare4.testVisionAdjacent(gridSquare2.getX() - gridSquare4.getX(), gridSquare2.getY() - gridSquare4.getY(), gridSquare2.getZ() - gridSquare4.getZ(), true, b);
                    if (testVisionAdjacent2 == TestResults.ClearThroughWindow) {
                        n9 = 1;
                    }
                    if (testVisionAdjacent2 == TestResults.Blocked || clear == TestResults.Clear || (testVisionAdjacent2 == TestResults.ClearThroughWindow && clear == TestResults.ClearThroughOpenDoor)) {
                        clear = testVisionAdjacent2;
                    }
                    else if (testVisionAdjacent2 == TestResults.ClearThroughClosedDoor && clear == TestResults.ClearThroughOpenDoor) {
                        clear = testVisionAdjacent2;
                    }
                    if (clear == TestResults.Blocked) {
                        return TestResults.Blocked;
                    }
                    if (n9 != 0) {
                        if (n8 > n4) {
                            return TestResults.Blocked;
                        }
                        n8 = 0;
                    }
                }
                gridSquare2 = gridSquare4;
                final int n26 = (int)n21;
                final int n27 = (int)n22;
                ++n8;
                n9 = 0;
            }
        }
        else {
            final float n28 = a2 / (float)n5;
            final float n29 = a / (float)n5;
            float n30 = n6 + i;
            float n31 = n7 + j;
            final int n32 = (n5 < 0) ? -1 : 1;
            final float n33 = n28 * n32;
            final float n34 = n29 * n32;
            while (k != n3) {
                k += n32;
                n30 += n33;
                n31 += n34;
                final IsoGridSquare gridSquare5 = isoCell.getGridSquare((int)n30, (int)n31, k);
                if (gridSquare5 != null && gridSquare2 != null) {
                    final TestResults testVisionAdjacent3 = gridSquare5.testVisionAdjacent(gridSquare2.getX() - gridSquare5.getX(), gridSquare2.getY() - gridSquare5.getY(), gridSquare2.getZ() - gridSquare5.getZ(), true, b);
                    if (testVisionAdjacent3 == TestResults.ClearThroughWindow) {
                        n9 = 1;
                    }
                    if (testVisionAdjacent3 == TestResults.Blocked || clear == TestResults.Clear || (testVisionAdjacent3 == TestResults.ClearThroughWindow && clear == TestResults.ClearThroughOpenDoor)) {
                        clear = testVisionAdjacent3;
                    }
                    else if (testVisionAdjacent3 == TestResults.ClearThroughClosedDoor && clear == TestResults.ClearThroughOpenDoor) {
                        clear = testVisionAdjacent3;
                    }
                    if (clear == TestResults.Blocked) {
                        return TestResults.Blocked;
                    }
                    if (n9 != 0) {
                        if (n8 > n4) {
                            return TestResults.Blocked;
                        }
                        n8 = 0;
                    }
                }
                gridSquare2 = gridSquare5;
                final int n35 = (int)n30;
                final int n36 = (int)n31;
                ++n8;
                n9 = 0;
            }
        }
        return clear;
    }
    
    public static boolean lineClearCollide(final int n, final int n2, final int n3, int i, int j, int k, final boolean b) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final int a = n2 - j;
        final int a2 = n - i;
        final int n4 = n3 - k;
        final float n5 = 0.5f;
        final float n6 = 0.5f;
        IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
        if (Math.abs(a2) > Math.abs(a) && Math.abs(a2) > Math.abs(n4)) {
            final float n7 = a / (float)a2;
            final float n8 = n4 / (float)a2;
            float n9 = n5 + j;
            float n10 = n6 + k;
            final int n11 = (a2 < 0) ? -1 : 1;
            final float n12 = n7 * n11;
            final float n13 = n8 * n11;
            while (i != n) {
                i += n11;
                n9 += n12;
                n10 += n13;
                final IsoGridSquare gridSquare2 = currentCell.getGridSquare(i, (int)n9, (int)n10);
                if (gridSquare2 != null && gridSquare != null) {
                    boolean calculateCollide = gridSquare2.CalculateCollide(gridSquare, false, false, true, true);
                    if (!b && gridSquare2.isDoorBlockedTo(gridSquare)) {
                        calculateCollide = true;
                    }
                    if (calculateCollide) {
                        return true;
                    }
                }
                gridSquare = gridSquare2;
                final int n14 = (int)n9;
                final int n15 = (int)n10;
            }
        }
        else if (Math.abs(a) >= Math.abs(a2) && Math.abs(a) > Math.abs(n4)) {
            final float n16 = a2 / (float)a;
            final float n17 = n4 / (float)a;
            float n18 = n5 + i;
            float n19 = n6 + k;
            final int n20 = (a < 0) ? -1 : 1;
            final float n21 = n16 * n20;
            final float n22 = n17 * n20;
            while (j != n2) {
                j += n20;
                n18 += n21;
                n19 += n22;
                final IsoGridSquare gridSquare3 = currentCell.getGridSquare((int)n18, j, (int)n19);
                if (gridSquare3 != null && gridSquare != null) {
                    boolean calculateCollide2 = gridSquare3.CalculateCollide(gridSquare, false, false, true, true);
                    if (!b && gridSquare3.isDoorBlockedTo(gridSquare)) {
                        calculateCollide2 = true;
                    }
                    if (calculateCollide2) {
                        return true;
                    }
                }
                gridSquare = gridSquare3;
                final int n23 = (int)n18;
                final int n24 = (int)n19;
            }
        }
        else {
            final float n25 = a2 / (float)n4;
            final float n26 = a / (float)n4;
            float n27 = n5 + i;
            float n28 = n6 + j;
            final int n29 = (n4 < 0) ? -1 : 1;
            final float n30 = n25 * n29;
            final float n31 = n26 * n29;
            while (k != n3) {
                k += n29;
                n27 += n30;
                n28 += n31;
                final IsoGridSquare gridSquare4 = currentCell.getGridSquare((int)n27, (int)n28, k);
                if (gridSquare4 != null && gridSquare != null && gridSquare4.CalculateCollide(gridSquare, false, false, true, true)) {
                    return true;
                }
                gridSquare = gridSquare4;
                final int n32 = (int)n27;
                final int n33 = (int)n28;
            }
        }
        return false;
    }
    
    public static int lineClearCollideCount(final IsoGameCharacter isoGameCharacter, final IsoCell isoCell, final int n, final int n2, final int n3, int i, int j, int k) {
        int n4 = 0;
        final int a = n2 - j;
        final int a2 = n - i;
        final int n5 = n3 - k;
        final float n6 = 0.5f;
        final float n7 = 0.5f;
        IsoGridSquare gridSquare = isoCell.getGridSquare(i, j, k);
        if (Math.abs(a2) > Math.abs(a) && Math.abs(a2) > Math.abs(n5)) {
            final float n8 = a / (float)a2;
            final float n9 = n5 / (float)a2;
            float n10 = n6 + j;
            float n11 = n7 + k;
            final int n12 = (a2 < 0) ? -1 : 1;
            final float n13 = n8 * n12;
            final float n14 = n9 * n12;
            while (i != n) {
                i += n12;
                n10 += n13;
                n11 += n14;
                final IsoGridSquare gridSquare2 = isoCell.getGridSquare(i, (int)n10, (int)n11);
                if (gridSquare2 != null && gridSquare != null && gridSquare.testCollideAdjacent(isoGameCharacter, gridSquare2.getX() - gridSquare.getX(), gridSquare2.getY() - gridSquare.getY(), gridSquare2.getZ() - gridSquare.getZ())) {
                    return n4;
                }
                ++n4;
                gridSquare = gridSquare2;
                final int n15 = (int)n10;
                final int n16 = (int)n11;
            }
        }
        else if (Math.abs(a) >= Math.abs(a2) && Math.abs(a) > Math.abs(n5)) {
            final float n17 = a2 / (float)a;
            final float n18 = n5 / (float)a;
            float n19 = n6 + i;
            float n20 = n7 + k;
            final int n21 = (a < 0) ? -1 : 1;
            final float n22 = n17 * n21;
            final float n23 = n18 * n21;
            while (j != n2) {
                j += n21;
                n19 += n22;
                n20 += n23;
                final IsoGridSquare gridSquare3 = isoCell.getGridSquare((int)n19, j, (int)n20);
                if (gridSquare3 != null && gridSquare != null && gridSquare.testCollideAdjacent(isoGameCharacter, gridSquare3.getX() - gridSquare.getX(), gridSquare3.getY() - gridSquare.getY(), gridSquare3.getZ() - gridSquare.getZ())) {
                    return n4;
                }
                ++n4;
                gridSquare = gridSquare3;
                final int n24 = (int)n19;
                final int n25 = (int)n20;
            }
        }
        else {
            final float n26 = a2 / (float)n5;
            final float n27 = a / (float)n5;
            float n28 = n6 + i;
            float n29 = n7 + j;
            final int n30 = (n5 < 0) ? -1 : 1;
            final float n31 = n26 * n30;
            final float n32 = n27 * n30;
            while (k != n3) {
                k += n30;
                n28 += n31;
                n29 += n32;
                final IsoGridSquare gridSquare4 = isoCell.getGridSquare((int)n28, (int)n29, k);
                if (gridSquare4 != null && gridSquare != null && gridSquare.testCollideAdjacent(isoGameCharacter, gridSquare4.getX() - gridSquare.getX(), gridSquare4.getY() - gridSquare.getY(), gridSquare4.getZ() - gridSquare.getZ())) {
                    return n4;
                }
                ++n4;
                gridSquare = gridSquare4;
                final int n33 = (int)n28;
                final int n34 = (int)n29;
            }
        }
        return n4;
    }
    
    public static TestResults lineClearCached(final IsoCell isoCell, final int n, final int n2, int n3, int i, int j, int k, final boolean b, final int n4) {
        if (n3 == k - 1) {
            final IsoGridSquare gridSquare = isoCell.getGridSquare(n, n2, n3);
            if (gridSquare != null && gridSquare.HasElevatedFloor()) {
                n3 = k;
            }
        }
        final int n5 = i;
        final int n6 = j;
        final int n7 = k;
        final int a = n2 - j;
        final int a2 = n - i;
        final int n8 = n3 - k;
        final int n9 = a2;
        final int n10 = a;
        final int n11 = n8;
        final int n12 = n9 + LosUtil.XSIZE / 2;
        final int n13 = n10 + LosUtil.YSIZE / 2;
        final int n14 = n11 + LosUtil.ZSIZE / 2;
        if (n12 < 0 || n13 < 0 || n14 < 0 || n12 >= LosUtil.XSIZE || n13 >= LosUtil.YSIZE || n14 >= LosUtil.ZSIZE) {
            return TestResults.Blocked;
        }
        TestResults testResults = TestResults.Clear;
        int n15 = 1;
        if (LosUtil.cachedresults[n12][n13][n14][n4] != 0) {
            if (LosUtil.cachedresults[n12][n13][n14][n4] == 1) {
                testResults = TestResults.Clear;
            }
            if (LosUtil.cachedresults[n12][n13][n14][n4] == 2) {
                testResults = TestResults.ClearThroughOpenDoor;
            }
            if (LosUtil.cachedresults[n12][n13][n14][n4] == 3) {
                testResults = TestResults.ClearThroughWindow;
            }
            if (LosUtil.cachedresults[n12][n13][n14][n4] == 4) {
                testResults = TestResults.Blocked;
            }
            if (LosUtil.cachedresults[n12][n13][n14][n4] == 5) {
                testResults = TestResults.ClearThroughClosedDoor;
            }
            return testResults;
        }
        final float n16 = 0.5f;
        final float n17 = 0.5f;
        IsoGridSquare gridSquare2 = isoCell.getGridSquare(i, j, k);
        if (Math.abs(a2) > Math.abs(a) && Math.abs(a2) > Math.abs(n8)) {
            final float n18 = a / (float)a2;
            final float n19 = n8 / (float)a2;
            float n20 = n16 + j;
            float n21 = n17 + k;
            final int n22 = (a2 < 0) ? -1 : 1;
            final float n23 = n18 * n22;
            final float n24 = n19 * n22;
            while (i != n) {
                i += n22;
                n20 += n23;
                n21 += n24;
                final IsoGridSquare gridSquare3 = isoCell.getGridSquare(i, (int)n20, (int)n21);
                if (gridSquare3 != null && gridSquare2 != null) {
                    if (n15 != 4 && gridSquare3.testVisionAdjacent(gridSquare2.getX() - gridSquare3.getX(), gridSquare2.getY() - gridSquare3.getY(), gridSquare2.getZ() - gridSquare3.getZ(), true, b) == TestResults.Blocked) {
                        n15 = 4;
                    }
                    final int n25 = i - n5;
                    final int n26 = (int)n20 - n6;
                    final int n27 = (int)n21 - n7;
                    final int n28 = n25 + LosUtil.XSIZE / 2;
                    final int n29 = n26 + LosUtil.YSIZE / 2;
                    final int n30 = n27 + LosUtil.ZSIZE / 2;
                    if (LosUtil.cachedresults[n28][n29][n30][n4] == 0) {
                        LosUtil.cachedresults[n28][n29][n30][n4] = (byte)n15;
                    }
                }
                else {
                    final int n31 = i - n5;
                    final int n32 = (int)n20 - n6;
                    final int n33 = (int)n21 - n7;
                    final int n34 = n31 + LosUtil.XSIZE / 2;
                    final int n35 = n32 + LosUtil.YSIZE / 2;
                    final int n36 = n33 + LosUtil.ZSIZE / 2;
                    if (LosUtil.cachedresults[n34][n35][n36][n4] == 0) {
                        LosUtil.cachedresults[n34][n35][n36][n4] = (byte)n15;
                    }
                }
                gridSquare2 = gridSquare3;
                final int n37 = (int)n20;
                final int n38 = (int)n21;
            }
        }
        else if (Math.abs(a) >= Math.abs(a2) && Math.abs(a) > Math.abs(n8)) {
            final float n39 = a2 / (float)a;
            final float n40 = n8 / (float)a;
            float n41 = n16 + i;
            float n42 = n17 + k;
            final int n43 = (a < 0) ? -1 : 1;
            final float n44 = n39 * n43;
            final float n45 = n40 * n43;
            while (j != n2) {
                j += n43;
                n41 += n44;
                n42 += n45;
                final IsoGridSquare gridSquare4 = isoCell.getGridSquare((int)n41, j, (int)n42);
                if (gridSquare4 != null && gridSquare2 != null) {
                    if (n15 != 4 && gridSquare4.testVisionAdjacent(gridSquare2.getX() - gridSquare4.getX(), gridSquare2.getY() - gridSquare4.getY(), gridSquare2.getZ() - gridSquare4.getZ(), true, b) == TestResults.Blocked) {
                        n15 = 4;
                    }
                    final int n46 = (int)n41 - n5;
                    final int n47 = j - n6;
                    final int n48 = (int)n42 - n7;
                    final int n49 = n46 + LosUtil.XSIZE / 2;
                    final int n50 = n47 + LosUtil.YSIZE / 2;
                    final int n51 = n48 + LosUtil.ZSIZE / 2;
                    if (LosUtil.cachedresults[n49][n50][n51][n4] == 0) {
                        LosUtil.cachedresults[n49][n50][n51][n4] = (byte)n15;
                    }
                }
                else {
                    final int n52 = (int)n41 - n5;
                    final int n53 = j - n6;
                    final int n54 = (int)n42 - n7;
                    final int n55 = n52 + LosUtil.XSIZE / 2;
                    final int n56 = n53 + LosUtil.YSIZE / 2;
                    final int n57 = n54 + LosUtil.ZSIZE / 2;
                    if (LosUtil.cachedresults[n55][n56][n57][n4] == 0) {
                        LosUtil.cachedresults[n55][n56][n57][n4] = (byte)n15;
                    }
                }
                gridSquare2 = gridSquare4;
                final int n58 = (int)n41;
                final int n59 = (int)n42;
            }
        }
        else {
            final float n60 = a2 / (float)n8;
            final float n61 = a / (float)n8;
            float n62 = n16 + i;
            float n63 = n17 + j;
            final int n64 = (n8 < 0) ? -1 : 1;
            final float n65 = n60 * n64;
            final float n66 = n61 * n64;
            while (k != n3) {
                k += n64;
                n62 += n65;
                n63 += n66;
                final IsoGridSquare gridSquare5 = isoCell.getGridSquare((int)n62, (int)n63, k);
                if (gridSquare5 != null && gridSquare2 != null) {
                    if (n15 != 4 && gridSquare5.testVisionAdjacent(gridSquare2.getX() - gridSquare5.getX(), gridSquare2.getY() - gridSquare5.getY(), gridSquare2.getZ() - gridSquare5.getZ(), true, b) == TestResults.Blocked) {
                        n15 = 4;
                    }
                    final int n67 = (int)n62 - n5;
                    final int n68 = (int)n63 - n6;
                    final int n69 = k - n7;
                    final int n70 = n67 + LosUtil.XSIZE / 2;
                    final int n71 = n68 + LosUtil.YSIZE / 2;
                    final int n72 = n69 + LosUtil.ZSIZE / 2;
                    if (LosUtil.cachedresults[n70][n71][n72][n4] == 0) {
                        LosUtil.cachedresults[n70][n71][n72][n4] = (byte)n15;
                    }
                }
                else {
                    final int n73 = (int)n62 - n5;
                    final int n74 = (int)n63 - n6;
                    final int n75 = k - n7;
                    final int n76 = n73 + LosUtil.XSIZE / 2;
                    final int n77 = n74 + LosUtil.YSIZE / 2;
                    final int n78 = n75 + LosUtil.ZSIZE / 2;
                    if (LosUtil.cachedresults[n76][n77][n78][n4] == 0) {
                        LosUtil.cachedresults[n76][n77][n78][n4] = (byte)n15;
                    }
                }
                gridSquare2 = gridSquare5;
                final int n79 = (int)n62;
                final int n80 = (int)n63;
            }
        }
        if (n15 == 1) {
            LosUtil.cachedresults[n12][n13][n14][n4] = (byte)n15;
            return TestResults.Clear;
        }
        if (n15 == 2) {
            LosUtil.cachedresults[n12][n13][n14][n4] = (byte)n15;
            return TestResults.ClearThroughOpenDoor;
        }
        if (n15 == 3) {
            LosUtil.cachedresults[n12][n13][n14][n4] = (byte)n15;
            return TestResults.ClearThroughWindow;
        }
        if (n15 == 4) {
            LosUtil.cachedresults[n12][n13][n14][n4] = (byte)n15;
            return TestResults.Blocked;
        }
        if (n15 == 5) {
            LosUtil.cachedresults[n12][n13][n14][n4] = (byte)n15;
            return TestResults.ClearThroughClosedDoor;
        }
        return TestResults.Blocked;
    }
    
    static {
        LosUtil.XSIZE = 200;
        LosUtil.YSIZE = 200;
        LosUtil.ZSIZE = 16;
        LosUtil.cachedresults = new byte[LosUtil.XSIZE][LosUtil.YSIZE][LosUtil.ZSIZE][4];
        LosUtil.cachecleared = new boolean[4];
        for (int i = 0; i < 4; ++i) {
            LosUtil.cachecleared[i] = true;
        }
    }
    
    public enum TestResults
    {
        Clear, 
        ClearThroughOpenDoor, 
        ClearThroughWindow, 
        Blocked, 
        ClearThroughClosedDoor;
        
        private static /* synthetic */ TestResults[] $values() {
            return new TestResults[] { TestResults.Clear, TestResults.ClearThroughOpenDoor, TestResults.ClearThroughWindow, TestResults.Blocked, TestResults.ClearThroughClosedDoor };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
