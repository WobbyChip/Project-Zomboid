// 
// Decompiled by Procyon v0.5.36
// 

package zombie.creative.creativerects;

public class OpenSimplexNoise
{
    private static final double STRETCH_CONSTANT_2D = -0.211324865405187;
    private static final double SQUISH_CONSTANT_2D = 0.366025403784439;
    private static final double STRETCH_CONSTANT_3D = -0.16666666666666666;
    private static final double SQUISH_CONSTANT_3D = 0.3333333333333333;
    private static final double STRETCH_CONSTANT_4D = -0.138196601125011;
    private static final double SQUISH_CONSTANT_4D = 0.309016994374947;
    private static final double NORM_CONSTANT_2D = 47.0;
    private static final double NORM_CONSTANT_3D = 103.0;
    private static final double NORM_CONSTANT_4D = 30.0;
    private static final long DEFAULT_SEED = 0L;
    private short[] perm;
    private short[] permGradIndex3D;
    private static byte[] gradients2D;
    private static byte[] gradients3D;
    private static byte[] gradients4D;
    
    public OpenSimplexNoise() {
        this(0L);
    }
    
    public OpenSimplexNoise(final short[] perm) {
        this.perm = perm;
        this.permGradIndex3D = new short[256];
        for (int i = 0; i < 256; ++i) {
            this.permGradIndex3D[i] = (short)(perm[i] % (OpenSimplexNoise.gradients3D.length / 3) * 3);
        }
    }
    
    public OpenSimplexNoise(long n) {
        this.perm = new short[256];
        this.permGradIndex3D = new short[256];
        final short[] array = new short[256];
        for (short n2 = 0; n2 < 256; ++n2) {
            array[n2] = n2;
        }
        n = n * 6364136223846793005L + 1442695040888963407L;
        n = n * 6364136223846793005L + 1442695040888963407L;
        n = n * 6364136223846793005L + 1442695040888963407L;
        for (int i = 255; i >= 0; --i) {
            n = n * 6364136223846793005L + 1442695040888963407L;
            int n3 = (int)((n + 31L) % (i + 1));
            if (n3 < 0) {
                n3 += i + 1;
            }
            this.perm[i] = array[n3];
            this.permGradIndex3D[i] = (short)(this.perm[i] % (OpenSimplexNoise.gradients3D.length / 3) * 3);
            array[n3] = array[i];
        }
    }
    
    public double eval(final double n, final double n2) {
        final double n3 = (n + n2) * -0.211324865405187;
        final double n4 = n + n3;
        final double n5 = n2 + n3;
        int fastFloor = fastFloor(n4);
        int fastFloor2 = fastFloor(n5);
        final double n6 = (fastFloor + fastFloor2) * 0.366025403784439;
        final double n7 = fastFloor + n6;
        final double n8 = fastFloor2 + n6;
        final double n9 = n4 - fastFloor;
        final double n10 = n5 - fastFloor2;
        final double n11 = n9 + n10;
        double n12 = n - n7;
        double n13 = n2 - n8;
        double n14 = 0.0;
        final double n15 = n12 - 1.0 - 0.366025403784439;
        final double n16 = n13 - 0.0 - 0.366025403784439;
        final double n17 = 2.0 - n15 * n15 - n16 * n16;
        if (n17 > 0.0) {
            final double n18 = n17 * n17;
            n14 += n18 * n18 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, n15, n16);
        }
        final double n19 = n12 - 0.0 - 0.366025403784439;
        final double n20 = n13 - 1.0 - 0.366025403784439;
        final double n21 = 2.0 - n19 * n19 - n20 * n20;
        if (n21 > 0.0) {
            final double n22 = n21 * n21;
            n14 += n22 * n22 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, n19, n20);
        }
        int n24;
        int n25;
        double n26;
        double n27;
        if (n11 <= 1.0) {
            final double n23 = 1.0 - n11;
            if (n23 > n9 || n23 > n10) {
                if (n9 > n10) {
                    n24 = fastFloor + 1;
                    n25 = fastFloor2 - 1;
                    n26 = n12 - 1.0;
                    n27 = n13 + 1.0;
                }
                else {
                    n24 = fastFloor - 1;
                    n25 = fastFloor2 + 1;
                    n26 = n12 + 1.0;
                    n27 = n13 - 1.0;
                }
            }
            else {
                n24 = fastFloor + 1;
                n25 = fastFloor2 + 1;
                n26 = n12 - 1.0 - 0.732050807568878;
                n27 = n13 - 1.0 - 0.732050807568878;
            }
        }
        else {
            final double n28 = 2.0 - n11;
            if (n28 < n9 || n28 < n10) {
                if (n9 > n10) {
                    n24 = fastFloor + 2;
                    n25 = fastFloor2 + 0;
                    n26 = n12 - 2.0 - 0.732050807568878;
                    n27 = n13 + 0.0 - 0.732050807568878;
                }
                else {
                    n24 = fastFloor + 0;
                    n25 = fastFloor2 + 2;
                    n26 = n12 + 0.0 - 0.732050807568878;
                    n27 = n13 - 2.0 - 0.732050807568878;
                }
            }
            else {
                n26 = n12;
                n27 = n13;
                n24 = fastFloor;
                n25 = fastFloor2;
            }
            ++fastFloor;
            ++fastFloor2;
            n12 = n12 - 1.0 - 0.732050807568878;
            n13 = n13 - 1.0 - 0.732050807568878;
        }
        final double n29 = 2.0 - n12 * n12 - n13 * n13;
        if (n29 > 0.0) {
            final double n30 = n29 * n29;
            n14 += n30 * n30 * this.extrapolate(fastFloor, fastFloor2, n12, n13);
        }
        final double n31 = 2.0 - n26 * n26 - n27 * n27;
        if (n31 > 0.0) {
            final double n32 = n31 * n31;
            n14 += n32 * n32 * this.extrapolate(n24, n25, n26, n27);
        }
        return n14 / 47.0;
    }
    
    public double eval(final double n, final double n2, final double n3) {
        final double n4 = (n + n2 + n3) * -0.16666666666666666;
        final double n5 = n + n4;
        final double n6 = n2 + n4;
        final double n7 = n3 + n4;
        final int fastFloor = fastFloor(n5);
        final int fastFloor2 = fastFloor(n6);
        final int fastFloor3 = fastFloor(n7);
        final double n8 = (fastFloor + fastFloor2 + fastFloor3) * 0.3333333333333333;
        final double n9 = fastFloor + n8;
        final double n10 = fastFloor2 + n8;
        final double n11 = fastFloor3 + n8;
        final double n12 = n5 - fastFloor;
        final double n13 = n6 - fastFloor2;
        final double n14 = n7 - fastFloor3;
        final double n15 = n12 + n13 + n14;
        final double n16 = n - n9;
        final double n17 = n2 - n10;
        final double n18 = n3 - n11;
        double n19 = 0.0;
        int n26;
        int n27;
        double n28;
        double n29;
        int n31;
        int n30;
        double n33;
        double n32;
        int n34;
        int n35;
        double n36;
        double n37;
        if (n15 <= 1.0) {
            int n20 = 1;
            double n21 = n12;
            int n22 = 2;
            double n23 = n13;
            if (n21 >= n23 && n14 > n23) {
                n23 = n14;
                n22 = 4;
            }
            else if (n21 < n23 && n14 > n21) {
                n21 = n14;
                n20 = 4;
            }
            final double n24 = 1.0 - n15;
            if (n24 > n21 || n24 > n23) {
                final int n25 = (n23 > n21) ? n22 : n20;
                if ((n25 & 0x1) == 0x0) {
                    n26 = fastFloor - 1;
                    n27 = fastFloor;
                    n28 = n16 + 1.0;
                    n29 = n16;
                }
                else {
                    n27 = (n26 = fastFloor + 1);
                    n29 = (n28 = n16 - 1.0);
                }
                if ((n25 & 0x2) == 0x0) {
                    n30 = (n31 = fastFloor2);
                    n32 = (n33 = n17);
                    if ((n25 & 0x1) == 0x0) {
                        --n30;
                        ++n32;
                    }
                    else {
                        --n31;
                        ++n33;
                    }
                }
                else {
                    n30 = (n31 = fastFloor2 + 1);
                    n32 = (n33 = n17 - 1.0);
                }
                if ((n25 & 0x4) == 0x0) {
                    n34 = fastFloor3;
                    n35 = fastFloor3 - 1;
                    n36 = n18;
                    n37 = n18 + 1.0;
                }
                else {
                    n35 = (n34 = fastFloor3 + 1);
                    n37 = (n36 = n18 - 1.0);
                }
            }
            else {
                final byte b = (byte)(n20 | n22);
                if ((b & 0x1) == 0x0) {
                    n26 = fastFloor;
                    n27 = fastFloor - 1;
                    n28 = n16 - 0.6666666666666666;
                    n29 = n16 + 1.0 - 0.3333333333333333;
                }
                else {
                    n27 = (n26 = fastFloor + 1);
                    n28 = n16 - 1.0 - 0.6666666666666666;
                    n29 = n16 - 1.0 - 0.3333333333333333;
                }
                if ((b & 0x2) == 0x0) {
                    n31 = fastFloor2;
                    n30 = fastFloor2 - 1;
                    n33 = n17 - 0.6666666666666666;
                    n32 = n17 + 1.0 - 0.3333333333333333;
                }
                else {
                    n30 = (n31 = fastFloor2 + 1);
                    n33 = n17 - 1.0 - 0.6666666666666666;
                    n32 = n17 - 1.0 - 0.3333333333333333;
                }
                if ((b & 0x4) == 0x0) {
                    n34 = fastFloor3;
                    n35 = fastFloor3 - 1;
                    n36 = n18 - 0.6666666666666666;
                    n37 = n18 + 1.0 - 0.3333333333333333;
                }
                else {
                    n35 = (n34 = fastFloor3 + 1);
                    n36 = n18 - 1.0 - 0.6666666666666666;
                    n37 = n18 - 1.0 - 0.3333333333333333;
                }
            }
            final double n38 = 2.0 - n16 * n16 - n17 * n17 - n18 * n18;
            if (n38 > 0.0) {
                final double n39 = n38 * n38;
                n19 += n39 * n39 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 0, n16, n17, n18);
            }
            final double n40 = n16 - 1.0 - 0.3333333333333333;
            final double n41 = n17 - 0.0 - 0.3333333333333333;
            final double n42 = n18 - 0.0 - 0.3333333333333333;
            final double n43 = 2.0 - n40 * n40 - n41 * n41 - n42 * n42;
            if (n43 > 0.0) {
                final double n44 = n43 * n43;
                n19 += n44 * n44 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 0, n40, n41, n42);
            }
            final double n45 = n16 - 0.0 - 0.3333333333333333;
            final double n46 = n17 - 1.0 - 0.3333333333333333;
            final double n47 = n42;
            final double n48 = 2.0 - n45 * n45 - n46 * n46 - n47 * n47;
            if (n48 > 0.0) {
                final double n49 = n48 * n48;
                n19 += n49 * n49 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 0, n45, n46, n47);
            }
            final double n50 = n45;
            final double n51 = n41;
            final double n52 = n18 - 1.0 - 0.3333333333333333;
            final double n53 = 2.0 - n50 * n50 - n51 * n51 - n52 * n52;
            if (n53 > 0.0) {
                final double n54 = n53 * n53;
                n19 += n54 * n54 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 1, n50, n51, n52);
            }
        }
        else if (n15 >= 2.0) {
            int n55 = 6;
            double n56 = n12;
            int n57 = 5;
            double n58 = n13;
            if (n56 <= n58 && n14 < n58) {
                n58 = n14;
                n57 = 3;
            }
            else if (n56 > n58 && n14 < n56) {
                n56 = n14;
                n55 = 3;
            }
            final double n59 = 3.0 - n15;
            if (n59 < n56 || n59 < n58) {
                final int n60 = (n58 < n56) ? n57 : n55;
                if ((n60 & 0x1) != 0x0) {
                    n26 = fastFloor + 2;
                    n27 = fastFloor + 1;
                    n28 = n16 - 2.0 - 1.0;
                    n29 = n16 - 1.0 - 1.0;
                }
                else {
                    n27 = (n26 = fastFloor);
                    n29 = (n28 = n16 - 1.0);
                }
                if ((n60 & 0x2) != 0x0) {
                    n30 = (n31 = fastFloor2 + 1);
                    n32 = (n33 = n17 - 1.0 - 1.0);
                    if ((n60 & 0x1) != 0x0) {
                        ++n30;
                        --n32;
                    }
                    else {
                        ++n31;
                        --n33;
                    }
                }
                else {
                    n30 = (n31 = fastFloor2);
                    n32 = (n33 = n17 - 1.0);
                }
                if ((n60 & 0x4) != 0x0) {
                    n34 = fastFloor3 + 1;
                    n35 = fastFloor3 + 2;
                    n36 = n18 - 1.0 - 1.0;
                    n37 = n18 - 2.0 - 1.0;
                }
                else {
                    n35 = (n34 = fastFloor3);
                    n37 = (n36 = n18 - 1.0);
                }
            }
            else {
                final byte b2 = (byte)(n55 & n57);
                if ((b2 & 0x1) != 0x0) {
                    n26 = fastFloor + 1;
                    n27 = fastFloor + 2;
                    n28 = n16 - 1.0 - 0.3333333333333333;
                    n29 = n16 - 2.0 - 0.6666666666666666;
                }
                else {
                    n27 = (n26 = fastFloor);
                    n28 = n16 - 0.3333333333333333;
                    n29 = n16 - 0.6666666666666666;
                }
                if ((b2 & 0x2) != 0x0) {
                    n31 = fastFloor2 + 1;
                    n30 = fastFloor2 + 2;
                    n33 = n17 - 1.0 - 0.3333333333333333;
                    n32 = n17 - 2.0 - 0.6666666666666666;
                }
                else {
                    n30 = (n31 = fastFloor2);
                    n33 = n17 - 0.3333333333333333;
                    n32 = n17 - 0.6666666666666666;
                }
                if ((b2 & 0x4) != 0x0) {
                    n34 = fastFloor3 + 1;
                    n35 = fastFloor3 + 2;
                    n36 = n18 - 1.0 - 0.3333333333333333;
                    n37 = n18 - 2.0 - 0.6666666666666666;
                }
                else {
                    n35 = (n34 = fastFloor3);
                    n36 = n18 - 0.3333333333333333;
                    n37 = n18 - 0.6666666666666666;
                }
            }
            final double n61 = n16 - 1.0 - 0.6666666666666666;
            final double n62 = n17 - 1.0 - 0.6666666666666666;
            final double n63 = n18 - 0.0 - 0.6666666666666666;
            final double n64 = 2.0 - n61 * n61 - n62 * n62 - n63 * n63;
            if (n64 > 0.0) {
                final double n65 = n64 * n64;
                n19 += n65 * n65 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 0, n61, n62, n63);
            }
            final double n66 = n61;
            final double n67 = n17 - 0.0 - 0.6666666666666666;
            final double n68 = n18 - 1.0 - 0.6666666666666666;
            final double n69 = 2.0 - n66 * n66 - n67 * n67 - n68 * n68;
            if (n69 > 0.0) {
                final double n70 = n69 * n69;
                n19 += n70 * n70 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 1, n66, n67, n68);
            }
            final double n71 = n16 - 0.0 - 0.6666666666666666;
            final double n72 = n62;
            final double n73 = n68;
            final double n74 = 2.0 - n71 * n71 - n72 * n72 - n73 * n73;
            if (n74 > 0.0) {
                final double n75 = n74 * n74;
                n19 += n75 * n75 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 1, n71, n72, n73);
            }
            final double n76 = n16 - 1.0 - 1.0;
            final double n77 = n17 - 1.0 - 1.0;
            final double n78 = n18 - 1.0 - 1.0;
            final double n79 = 2.0 - n76 * n76 - n77 * n77 - n78 * n78;
            if (n79 > 0.0) {
                final double n80 = n79 * n79;
                n19 += n80 * n80 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 1, n76, n77, n78);
            }
        }
        else {
            final double n81 = n12 + n13;
            double n82;
            int n83;
            boolean b3;
            if (n81 > 1.0) {
                n82 = n81 - 1.0;
                n83 = 3;
                b3 = true;
            }
            else {
                n82 = 1.0 - n81;
                n83 = 4;
                b3 = false;
            }
            final double n84 = n12 + n14;
            double n85;
            int n86;
            boolean b4;
            if (n84 > 1.0) {
                n85 = n84 - 1.0;
                n86 = 5;
                b4 = true;
            }
            else {
                n85 = 1.0 - n84;
                n86 = 2;
                b4 = false;
            }
            final double n87 = n13 + n14;
            if (n87 > 1.0) {
                final double n88 = n87 - 1.0;
                if (n82 <= n85 && n82 < n88) {
                    n83 = 6;
                    b3 = true;
                }
                else if (n82 > n85 && n85 < n88) {
                    n86 = 6;
                    b4 = true;
                }
            }
            else {
                final double n89 = 1.0 - n87;
                if (n82 <= n85 && n82 < n89) {
                    n83 = 1;
                    b3 = false;
                }
                else if (n82 > n85 && n85 < n89) {
                    n86 = 1;
                    b4 = false;
                }
            }
            if (b3 == b4) {
                if (b3) {
                    n28 = n16 - 1.0 - 1.0;
                    n33 = n17 - 1.0 - 1.0;
                    n36 = n18 - 1.0 - 1.0;
                    n26 = fastFloor + 1;
                    n31 = fastFloor2 + 1;
                    n34 = fastFloor3 + 1;
                    final byte b5 = (byte)(n83 & n86);
                    if ((b5 & 0x1) != 0x0) {
                        n29 = n16 - 2.0 - 0.6666666666666666;
                        n32 = n17 - 0.6666666666666666;
                        n37 = n18 - 0.6666666666666666;
                        n27 = fastFloor + 2;
                        n30 = fastFloor2;
                        n35 = fastFloor3;
                    }
                    else if ((b5 & 0x2) != 0x0) {
                        n29 = n16 - 0.6666666666666666;
                        n32 = n17 - 2.0 - 0.6666666666666666;
                        n37 = n18 - 0.6666666666666666;
                        n27 = fastFloor;
                        n30 = fastFloor2 + 2;
                        n35 = fastFloor3;
                    }
                    else {
                        n29 = n16 - 0.6666666666666666;
                        n32 = n17 - 0.6666666666666666;
                        n37 = n18 - 2.0 - 0.6666666666666666;
                        n27 = fastFloor;
                        n30 = fastFloor2;
                        n35 = fastFloor3 + 2;
                    }
                }
                else {
                    n28 = n16;
                    n33 = n17;
                    n36 = n18;
                    n26 = fastFloor;
                    n31 = fastFloor2;
                    n34 = fastFloor3;
                    final byte b6 = (byte)(n83 | n86);
                    if ((b6 & 0x1) == 0x0) {
                        n29 = n16 + 1.0 - 0.3333333333333333;
                        n32 = n17 - 1.0 - 0.3333333333333333;
                        n37 = n18 - 1.0 - 0.3333333333333333;
                        n27 = fastFloor - 1;
                        n30 = fastFloor2 + 1;
                        n35 = fastFloor3 + 1;
                    }
                    else if ((b6 & 0x2) == 0x0) {
                        n29 = n16 - 1.0 - 0.3333333333333333;
                        n32 = n17 + 1.0 - 0.3333333333333333;
                        n37 = n18 - 1.0 - 0.3333333333333333;
                        n27 = fastFloor + 1;
                        n30 = fastFloor2 - 1;
                        n35 = fastFloor3 + 1;
                    }
                    else {
                        n29 = n16 - 1.0 - 0.3333333333333333;
                        n32 = n17 - 1.0 - 0.3333333333333333;
                        n37 = n18 + 1.0 - 0.3333333333333333;
                        n27 = fastFloor + 1;
                        n30 = fastFloor2 + 1;
                        n35 = fastFloor3 - 1;
                    }
                }
            }
            else {
                int n90;
                int n91;
                if (b3) {
                    n90 = n83;
                    n91 = n86;
                }
                else {
                    n90 = n86;
                    n91 = n83;
                }
                if ((n90 & 0x1) == 0x0) {
                    n28 = n16 + 1.0 - 0.3333333333333333;
                    n33 = n17 - 1.0 - 0.3333333333333333;
                    n36 = n18 - 1.0 - 0.3333333333333333;
                    n26 = fastFloor - 1;
                    n31 = fastFloor2 + 1;
                    n34 = fastFloor3 + 1;
                }
                else if ((n90 & 0x2) == 0x0) {
                    n28 = n16 - 1.0 - 0.3333333333333333;
                    n33 = n17 + 1.0 - 0.3333333333333333;
                    n36 = n18 - 1.0 - 0.3333333333333333;
                    n26 = fastFloor + 1;
                    n31 = fastFloor2 - 1;
                    n34 = fastFloor3 + 1;
                }
                else {
                    n28 = n16 - 1.0 - 0.3333333333333333;
                    n33 = n17 - 1.0 - 0.3333333333333333;
                    n36 = n18 + 1.0 - 0.3333333333333333;
                    n26 = fastFloor + 1;
                    n31 = fastFloor2 + 1;
                    n34 = fastFloor3 - 1;
                }
                n29 = n16 - 0.6666666666666666;
                n32 = n17 - 0.6666666666666666;
                n37 = n18 - 0.6666666666666666;
                n27 = fastFloor;
                n30 = fastFloor2;
                n35 = fastFloor3;
                if ((n91 & 0x1) != 0x0) {
                    n29 -= 2.0;
                    n27 += 2;
                }
                else if ((n91 & 0x2) != 0x0) {
                    n32 -= 2.0;
                    n30 += 2;
                }
                else {
                    n37 -= 2.0;
                    n35 += 2;
                }
            }
            final double n92 = n16 - 1.0 - 0.3333333333333333;
            final double n93 = n17 - 0.0 - 0.3333333333333333;
            final double n94 = n18 - 0.0 - 0.3333333333333333;
            final double n95 = 2.0 - n92 * n92 - n93 * n93 - n94 * n94;
            if (n95 > 0.0) {
                final double n96 = n95 * n95;
                n19 += n96 * n96 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 0, n92, n93, n94);
            }
            final double n97 = n16 - 0.0 - 0.3333333333333333;
            final double n98 = n17 - 1.0 - 0.3333333333333333;
            final double n99 = n94;
            final double n100 = 2.0 - n97 * n97 - n98 * n98 - n99 * n99;
            if (n100 > 0.0) {
                final double n101 = n100 * n100;
                n19 += n101 * n101 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 0, n97, n98, n99);
            }
            final double n102 = n97;
            final double n103 = n93;
            final double n104 = n18 - 1.0 - 0.3333333333333333;
            final double n105 = 2.0 - n102 * n102 - n103 * n103 - n104 * n104;
            if (n105 > 0.0) {
                final double n106 = n105 * n105;
                n19 += n106 * n106 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 1, n102, n103, n104);
            }
            final double n107 = n16 - 1.0 - 0.6666666666666666;
            final double n108 = n17 - 1.0 - 0.6666666666666666;
            final double n109 = n18 - 0.0 - 0.6666666666666666;
            final double n110 = 2.0 - n107 * n107 - n108 * n108 - n109 * n109;
            if (n110 > 0.0) {
                final double n111 = n110 * n110;
                n19 += n111 * n111 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 0, n107, n108, n109);
            }
            final double n112 = n107;
            final double n113 = n17 - 0.0 - 0.6666666666666666;
            final double n114 = n18 - 1.0 - 0.6666666666666666;
            final double n115 = 2.0 - n112 * n112 - n113 * n113 - n114 * n114;
            if (n115 > 0.0) {
                final double n116 = n115 * n115;
                n19 += n116 * n116 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 1, n112, n113, n114);
            }
            final double n117 = n16 - 0.0 - 0.6666666666666666;
            final double n118 = n108;
            final double n119 = n114;
            final double n120 = 2.0 - n117 * n117 - n118 * n118 - n119 * n119;
            if (n120 > 0.0) {
                final double n121 = n120 * n120;
                n19 += n121 * n121 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 1, n117, n118, n119);
            }
        }
        final double n122 = 2.0 - n28 * n28 - n33 * n33 - n36 * n36;
        if (n122 > 0.0) {
            final double n123 = n122 * n122;
            n19 += n123 * n123 * this.extrapolate(n26, n31, n34, n28, n33, n36);
        }
        final double n124 = 2.0 - n29 * n29 - n32 * n32 - n37 * n37;
        if (n124 > 0.0) {
            final double n125 = n124 * n124;
            n19 += n125 * n125 * this.extrapolate(n27, n30, n35, n29, n32, n37);
        }
        return n19 / 103.0;
    }
    
    public double eval(final double n, final double n2, final double n3, final double n4) {
        final double n5 = (n + n2 + n3 + n4) * -0.138196601125011;
        final double n6 = n + n5;
        final double n7 = n2 + n5;
        final double n8 = n3 + n5;
        final double n9 = n4 + n5;
        final int fastFloor = fastFloor(n6);
        final int fastFloor2 = fastFloor(n7);
        final int fastFloor3 = fastFloor(n8);
        final int fastFloor4 = fastFloor(n9);
        final double n10 = (fastFloor + fastFloor2 + fastFloor3 + fastFloor4) * 0.309016994374947;
        final double n11 = fastFloor + n10;
        final double n12 = fastFloor2 + n10;
        final double n13 = fastFloor3 + n10;
        final double n14 = fastFloor4 + n10;
        final double n15 = n6 - fastFloor;
        final double n16 = n7 - fastFloor2;
        final double n17 = n8 - fastFloor3;
        final double n18 = n9 - fastFloor4;
        final double n19 = n15 + n16 + n17 + n18;
        final double n20 = n - n11;
        final double n21 = n2 - n12;
        final double n22 = n3 - n13;
        final double n23 = n4 - n14;
        double n24 = 0.0;
        int n31;
        int n33;
        int n32;
        double n34;
        double n36;
        double n35;
        int n39;
        int n38;
        int n37;
        double n42;
        double n41;
        double n40;
        int n45;
        int n44;
        int n43;
        double n48;
        double n47;
        double n46;
        int n50;
        int n49;
        int n51;
        double n53;
        double n52;
        double n54;
        if (n19 <= 1.0) {
            int n25 = 1;
            double n26 = n15;
            int n27 = 2;
            double n28 = n16;
            if (n26 >= n28 && n17 > n28) {
                n28 = n17;
                n27 = 4;
            }
            else if (n26 < n28 && n17 > n26) {
                n26 = n17;
                n25 = 4;
            }
            if (n26 >= n28 && n18 > n28) {
                n28 = n18;
                n27 = 8;
            }
            else if (n26 < n28 && n18 > n26) {
                n26 = n18;
                n25 = 8;
            }
            final double n29 = 1.0 - n19;
            if (n29 > n26 || n29 > n28) {
                final int n30 = (n28 > n26) ? n27 : n25;
                if ((n30 & 0x1) == 0x0) {
                    n31 = fastFloor - 1;
                    n32 = (n33 = fastFloor);
                    n34 = n20 + 1.0;
                    n35 = (n36 = n20);
                }
                else {
                    n33 = (n31 = (n32 = fastFloor + 1));
                    n36 = (n34 = (n35 = n20 - 1.0));
                }
                if ((n30 & 0x2) == 0x0) {
                    n37 = (n38 = (n39 = fastFloor2));
                    n40 = (n41 = (n42 = n21));
                    if ((n30 & 0x1) == 0x1) {
                        --n38;
                        ++n41;
                    }
                    else {
                        --n37;
                        ++n40;
                    }
                }
                else {
                    n37 = (n38 = (n39 = fastFloor2 + 1));
                    n40 = (n41 = (n42 = n21 - 1.0));
                }
                if ((n30 & 0x4) == 0x0) {
                    n43 = (n44 = (n45 = fastFloor3));
                    n46 = (n47 = (n48 = n22));
                    if ((n30 & 0x3) != 0x0) {
                        if ((n30 & 0x3) == 0x3) {
                            --n44;
                            ++n47;
                        }
                        else {
                            --n43;
                            ++n46;
                        }
                    }
                    else {
                        --n45;
                        ++n48;
                    }
                }
                else {
                    n43 = (n44 = (n45 = fastFloor3 + 1));
                    n46 = (n47 = (n48 = n22 - 1.0));
                }
                if ((n30 & 0x8) == 0x0) {
                    n49 = (n50 = fastFloor4);
                    n51 = fastFloor4 - 1;
                    n52 = (n53 = n23);
                    n54 = n23 + 1.0;
                }
                else {
                    n49 = (n50 = (n51 = fastFloor4 + 1));
                    n52 = (n53 = (n54 = n23 - 1.0));
                }
            }
            else {
                final byte b = (byte)(n25 | n27);
                if ((b & 0x1) == 0x0) {
                    n32 = (n31 = fastFloor);
                    n33 = fastFloor - 1;
                    n34 = n20 - 0.618033988749894;
                    n36 = n20 + 1.0 - 0.309016994374947;
                    n35 = n20 - 0.309016994374947;
                }
                else {
                    n33 = (n31 = (n32 = fastFloor + 1));
                    n34 = n20 - 1.0 - 0.618033988749894;
                    n35 = (n36 = n20 - 1.0 - 0.309016994374947);
                }
                if ((b & 0x2) == 0x0) {
                    n37 = (n38 = (n39 = fastFloor2));
                    n41 = n21 - 0.618033988749894;
                    n42 = (n40 = n21 - 0.309016994374947);
                    if ((b & 0x1) == 0x1) {
                        --n37;
                        ++n40;
                    }
                    else {
                        --n39;
                        ++n42;
                    }
                }
                else {
                    n37 = (n38 = (n39 = fastFloor2 + 1));
                    n41 = n21 - 1.0 - 0.618033988749894;
                    n42 = (n40 = n21 - 1.0 - 0.309016994374947);
                }
                if ((b & 0x4) == 0x0) {
                    n43 = (n44 = (n45 = fastFloor3));
                    n47 = n22 - 0.618033988749894;
                    n48 = (n46 = n22 - 0.309016994374947);
                    if ((b & 0x3) == 0x3) {
                        --n43;
                        ++n46;
                    }
                    else {
                        --n45;
                        ++n48;
                    }
                }
                else {
                    n43 = (n44 = (n45 = fastFloor3 + 1));
                    n47 = n22 - 1.0 - 0.618033988749894;
                    n48 = (n46 = n22 - 1.0 - 0.309016994374947);
                }
                if ((b & 0x8) == 0x0) {
                    n49 = (n50 = fastFloor4);
                    n51 = fastFloor4 - 1;
                    n53 = n23 - 0.618033988749894;
                    n52 = n23 - 0.309016994374947;
                    n54 = n23 + 1.0 - 0.309016994374947;
                }
                else {
                    n49 = (n50 = (n51 = fastFloor4 + 1));
                    n53 = n23 - 1.0 - 0.618033988749894;
                    n54 = (n52 = n23 - 1.0 - 0.309016994374947);
                }
            }
            final double n55 = 2.0 - n20 * n20 - n21 * n21 - n22 * n22 - n23 * n23;
            if (n55 > 0.0) {
                final double n56 = n55 * n55;
                n24 += n56 * n56 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 0, fastFloor4 + 0, n20, n21, n22, n23);
            }
            final double n57 = n20 - 1.0 - 0.309016994374947;
            final double n58 = n21 - 0.0 - 0.309016994374947;
            final double n59 = n22 - 0.0 - 0.309016994374947;
            final double n60 = n23 - 0.0 - 0.309016994374947;
            final double n61 = 2.0 - n57 * n57 - n58 * n58 - n59 * n59 - n60 * n60;
            if (n61 > 0.0) {
                final double n62 = n61 * n61;
                n24 += n62 * n62 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 0, fastFloor4 + 0, n57, n58, n59, n60);
            }
            final double n63 = n20 - 0.0 - 0.309016994374947;
            final double n64 = n21 - 1.0 - 0.309016994374947;
            final double n65 = n59;
            final double n66 = n60;
            final double n67 = 2.0 - n63 * n63 - n64 * n64 - n65 * n65 - n66 * n66;
            if (n67 > 0.0) {
                final double n68 = n67 * n67;
                n24 += n68 * n68 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 0, n63, n64, n65, n66);
            }
            final double n69 = n63;
            final double n70 = n58;
            final double n71 = n22 - 1.0 - 0.309016994374947;
            final double n72 = n60;
            final double n73 = 2.0 - n69 * n69 - n70 * n70 - n71 * n71 - n72 * n72;
            if (n73 > 0.0) {
                final double n74 = n73 * n73;
                n24 += n74 * n74 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 0, n69, n70, n71, n72);
            }
            final double n75 = n63;
            final double n76 = n58;
            final double n77 = n59;
            final double n78 = n23 - 1.0 - 0.309016994374947;
            final double n79 = 2.0 - n75 * n75 - n76 * n76 - n77 * n77 - n78 * n78;
            if (n79 > 0.0) {
                final double n80 = n79 * n79;
                n24 += n80 * n80 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 0, fastFloor4 + 1, n75, n76, n77, n78);
            }
        }
        else if (n19 >= 3.0) {
            int n81 = 14;
            double n82 = n15;
            int n83 = 13;
            double n84 = n16;
            if (n82 <= n84 && n17 < n84) {
                n84 = n17;
                n83 = 11;
            }
            else if (n82 > n84 && n17 < n82) {
                n82 = n17;
                n81 = 11;
            }
            if (n82 <= n84 && n18 < n84) {
                n84 = n18;
                n83 = 7;
            }
            else if (n82 > n84 && n18 < n82) {
                n82 = n18;
                n81 = 7;
            }
            final double n85 = 4.0 - n19;
            if (n85 < n82 || n85 < n84) {
                final int n86 = (n84 < n82) ? n83 : n81;
                if ((n86 & 0x1) != 0x0) {
                    n31 = fastFloor + 2;
                    n32 = (n33 = fastFloor + 1);
                    n34 = n20 - 2.0 - 1.236067977499788;
                    n35 = (n36 = n20 - 1.0 - 1.236067977499788);
                }
                else {
                    n33 = (n31 = (n32 = fastFloor));
                    n36 = (n34 = (n35 = n20 - 1.236067977499788));
                }
                if ((n86 & 0x2) != 0x0) {
                    n37 = (n38 = (n39 = fastFloor2 + 1));
                    n40 = (n41 = (n42 = n21 - 1.0 - 1.236067977499788));
                    if ((n86 & 0x1) != 0x0) {
                        ++n37;
                        --n40;
                    }
                    else {
                        ++n38;
                        --n41;
                    }
                }
                else {
                    n37 = (n38 = (n39 = fastFloor2));
                    n40 = (n41 = (n42 = n21 - 1.236067977499788));
                }
                if ((n86 & 0x4) != 0x0) {
                    n43 = (n44 = (n45 = fastFloor3 + 1));
                    n46 = (n47 = (n48 = n22 - 1.0 - 1.236067977499788));
                    if ((n86 & 0x3) != 0x3) {
                        if ((n86 & 0x3) == 0x0) {
                            ++n44;
                            --n47;
                        }
                        else {
                            ++n43;
                            --n46;
                        }
                    }
                    else {
                        ++n45;
                        --n48;
                    }
                }
                else {
                    n43 = (n44 = (n45 = fastFloor3));
                    n46 = (n47 = (n48 = n22 - 1.236067977499788));
                }
                if ((n86 & 0x8) != 0x0) {
                    n49 = (n50 = fastFloor4 + 1);
                    n51 = fastFloor4 + 2;
                    n52 = (n53 = n23 - 1.0 - 1.236067977499788);
                    n54 = n23 - 2.0 - 1.236067977499788;
                }
                else {
                    n49 = (n50 = (n51 = fastFloor4));
                    n52 = (n53 = (n54 = n23 - 1.236067977499788));
                }
            }
            else {
                final byte b2 = (byte)(n81 & n83);
                if ((b2 & 0x1) != 0x0) {
                    n32 = (n31 = fastFloor + 1);
                    n33 = fastFloor + 2;
                    n34 = n20 - 1.0 - 0.618033988749894;
                    n36 = n20 - 2.0 - 0.927050983124841;
                    n35 = n20 - 1.0 - 0.927050983124841;
                }
                else {
                    n33 = (n31 = (n32 = fastFloor));
                    n34 = n20 - 0.618033988749894;
                    n35 = (n36 = n20 - 0.927050983124841);
                }
                if ((b2 & 0x2) != 0x0) {
                    n37 = (n38 = (n39 = fastFloor2 + 1));
                    n41 = n21 - 1.0 - 0.618033988749894;
                    n42 = (n40 = n21 - 1.0 - 0.927050983124841);
                    if ((b2 & 0x1) != 0x0) {
                        ++n39;
                        --n42;
                    }
                    else {
                        ++n37;
                        --n40;
                    }
                }
                else {
                    n37 = (n38 = (n39 = fastFloor2));
                    n41 = n21 - 0.618033988749894;
                    n42 = (n40 = n21 - 0.927050983124841);
                }
                if ((b2 & 0x4) != 0x0) {
                    n43 = (n44 = (n45 = fastFloor3 + 1));
                    n47 = n22 - 1.0 - 0.618033988749894;
                    n48 = (n46 = n22 - 1.0 - 0.927050983124841);
                    if ((b2 & 0x3) != 0x0) {
                        ++n45;
                        --n48;
                    }
                    else {
                        ++n43;
                        --n46;
                    }
                }
                else {
                    n43 = (n44 = (n45 = fastFloor3));
                    n47 = n22 - 0.618033988749894;
                    n48 = (n46 = n22 - 0.927050983124841);
                }
                if ((b2 & 0x8) != 0x0) {
                    n49 = (n50 = fastFloor4 + 1);
                    n51 = fastFloor4 + 2;
                    n53 = n23 - 1.0 - 0.618033988749894;
                    n52 = n23 - 1.0 - 0.927050983124841;
                    n54 = n23 - 2.0 - 0.927050983124841;
                }
                else {
                    n49 = (n50 = (n51 = fastFloor4));
                    n53 = n23 - 0.618033988749894;
                    n54 = (n52 = n23 - 0.927050983124841);
                }
            }
            final double n87 = n20 - 1.0 - 0.927050983124841;
            final double n88 = n21 - 1.0 - 0.927050983124841;
            final double n89 = n22 - 1.0 - 0.927050983124841;
            final double n90 = n23 - 0.927050983124841;
            final double n91 = 2.0 - n87 * n87 - n88 * n88 - n89 * n89 - n90 * n90;
            if (n91 > 0.0) {
                final double n92 = n91 * n91;
                n24 += n92 * n92 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 1, fastFloor4 + 0, n87, n88, n89, n90);
            }
            final double n93 = n87;
            final double n94 = n88;
            final double n95 = n22 - 0.927050983124841;
            final double n96 = n23 - 1.0 - 0.927050983124841;
            final double n97 = 2.0 - n93 * n93 - n94 * n94 - n95 * n95 - n96 * n96;
            if (n97 > 0.0) {
                final double n98 = n97 * n97;
                n24 += n98 * n98 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 1, n93, n94, n95, n96);
            }
            final double n99 = n87;
            final double n100 = n21 - 0.927050983124841;
            final double n101 = n89;
            final double n102 = n96;
            final double n103 = 2.0 - n99 * n99 - n100 * n100 - n101 * n101 - n102 * n102;
            if (n103 > 0.0) {
                final double n104 = n103 * n103;
                n24 += n104 * n104 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 1, n99, n100, n101, n102);
            }
            final double n105 = n20 - 0.927050983124841;
            final double n106 = n89;
            final double n107 = n88;
            final double n108 = n96;
            final double n109 = 2.0 - n105 * n105 - n107 * n107 - n106 * n106 - n108 * n108;
            if (n109 > 0.0) {
                final double n110 = n109 * n109;
                n24 += n110 * n110 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 1, fastFloor4 + 1, n105, n107, n106, n108);
            }
            final double n111 = n20 - 1.0 - 1.236067977499788;
            final double n112 = n21 - 1.0 - 1.236067977499788;
            final double n113 = n22 - 1.0 - 1.236067977499788;
            final double n114 = n23 - 1.0 - 1.236067977499788;
            final double n115 = 2.0 - n111 * n111 - n112 * n112 - n113 * n113 - n114 * n114;
            if (n115 > 0.0) {
                final double n116 = n115 * n115;
                n24 += n116 * n116 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 1, fastFloor4 + 1, n111, n112, n113, n114);
            }
        }
        else if (n19 <= 2.0) {
            boolean b3 = true;
            boolean b4 = true;
            double n117;
            int n118;
            if (n15 + n16 > n17 + n18) {
                n117 = n15 + n16;
                n118 = 3;
            }
            else {
                n117 = n17 + n18;
                n118 = 12;
            }
            double n119;
            int n120;
            if (n15 + n17 > n16 + n18) {
                n119 = n15 + n17;
                n120 = 5;
            }
            else {
                n119 = n16 + n18;
                n120 = 10;
            }
            if (n15 + n18 > n16 + n17) {
                final double n121 = n15 + n18;
                if (n117 >= n119 && n121 > n119) {
                    n119 = n121;
                    n120 = 9;
                }
                else if (n117 < n119 && n121 > n117) {
                    n117 = n121;
                    n118 = 9;
                }
            }
            else {
                final double n122 = n16 + n17;
                if (n117 >= n119 && n122 > n119) {
                    n119 = n122;
                    n120 = 6;
                }
                else if (n117 < n119 && n122 > n117) {
                    n117 = n122;
                    n118 = 6;
                }
            }
            final double n123 = 2.0 - n19 + n15;
            if (n117 >= n119 && n123 > n119) {
                n119 = n123;
                n120 = 1;
                b4 = false;
            }
            else if (n117 < n119 && n123 > n117) {
                n117 = n123;
                n118 = 1;
                b3 = false;
            }
            final double n124 = 2.0 - n19 + n16;
            if (n117 >= n119 && n124 > n119) {
                n119 = n124;
                n120 = 2;
                b4 = false;
            }
            else if (n117 < n119 && n124 > n117) {
                n117 = n124;
                n118 = 2;
                b3 = false;
            }
            final double n125 = 2.0 - n19 + n17;
            if (n117 >= n119 && n125 > n119) {
                n119 = n125;
                n120 = 4;
                b4 = false;
            }
            else if (n117 < n119 && n125 > n117) {
                n117 = n125;
                n118 = 4;
                b3 = false;
            }
            final double n126 = 2.0 - n19 + n18;
            if (n117 >= n119 && n126 > n119) {
                n120 = 8;
                b4 = false;
            }
            else if (n117 < n119 && n126 > n117) {
                n118 = 8;
                b3 = false;
            }
            if (b3 == b4) {
                if (b3) {
                    final byte b5 = (byte)(n118 | n120);
                    final byte b6 = (byte)(n118 & n120);
                    if ((b5 & 0x1) == 0x0) {
                        n31 = fastFloor;
                        n33 = fastFloor - 1;
                        n34 = n20 - 0.927050983124841;
                        n36 = n20 + 1.0 - 0.618033988749894;
                    }
                    else {
                        n33 = (n31 = fastFloor + 1);
                        n34 = n20 - 1.0 - 0.927050983124841;
                        n36 = n20 - 1.0 - 0.618033988749894;
                    }
                    if ((b5 & 0x2) == 0x0) {
                        n38 = fastFloor2;
                        n37 = fastFloor2 - 1;
                        n41 = n21 - 0.927050983124841;
                        n40 = n21 + 1.0 - 0.618033988749894;
                    }
                    else {
                        n37 = (n38 = fastFloor2 + 1);
                        n41 = n21 - 1.0 - 0.927050983124841;
                        n40 = n21 - 1.0 - 0.618033988749894;
                    }
                    if ((b5 & 0x4) == 0x0) {
                        n44 = fastFloor3;
                        n43 = fastFloor3 - 1;
                        n47 = n22 - 0.927050983124841;
                        n46 = n22 + 1.0 - 0.618033988749894;
                    }
                    else {
                        n43 = (n44 = fastFloor3 + 1);
                        n47 = n22 - 1.0 - 0.927050983124841;
                        n46 = n22 - 1.0 - 0.618033988749894;
                    }
                    if ((b5 & 0x8) == 0x0) {
                        n50 = fastFloor4;
                        n49 = fastFloor4 - 1;
                        n53 = n23 - 0.927050983124841;
                        n52 = n23 + 1.0 - 0.618033988749894;
                    }
                    else {
                        n49 = (n50 = fastFloor4 + 1);
                        n53 = n23 - 1.0 - 0.927050983124841;
                        n52 = n23 - 1.0 - 0.618033988749894;
                    }
                    n32 = fastFloor;
                    n39 = fastFloor2;
                    n45 = fastFloor3;
                    n51 = fastFloor4;
                    n35 = n20 - 0.618033988749894;
                    n42 = n21 - 0.618033988749894;
                    n48 = n22 - 0.618033988749894;
                    n54 = n23 - 0.618033988749894;
                    if ((b6 & 0x1) != 0x0) {
                        n32 += 2;
                        n35 -= 2.0;
                    }
                    else if ((b6 & 0x2) != 0x0) {
                        n39 += 2;
                        n42 -= 2.0;
                    }
                    else if ((b6 & 0x4) != 0x0) {
                        n45 += 2;
                        n48 -= 2.0;
                    }
                    else {
                        n51 += 2;
                        n54 -= 2.0;
                    }
                }
                else {
                    n32 = fastFloor;
                    n39 = fastFloor2;
                    n45 = fastFloor3;
                    n51 = fastFloor4;
                    n35 = n20;
                    n42 = n21;
                    n48 = n22;
                    n54 = n23;
                    final byte b7 = (byte)(n118 | n120);
                    if ((b7 & 0x1) == 0x0) {
                        n31 = fastFloor - 1;
                        n33 = fastFloor;
                        n34 = n20 + 1.0 - 0.309016994374947;
                        n36 = n20 - 0.309016994374947;
                    }
                    else {
                        n33 = (n31 = fastFloor + 1);
                        n36 = (n34 = n20 - 1.0 - 0.309016994374947);
                    }
                    if ((b7 & 0x2) == 0x0) {
                        n37 = (n38 = fastFloor2);
                        n40 = (n41 = n21 - 0.309016994374947);
                        if ((b7 & 0x1) == 0x1) {
                            --n38;
                            ++n41;
                        }
                        else {
                            --n37;
                            ++n40;
                        }
                    }
                    else {
                        n37 = (n38 = fastFloor2 + 1);
                        n40 = (n41 = n21 - 1.0 - 0.309016994374947);
                    }
                    if ((b7 & 0x4) == 0x0) {
                        n43 = (n44 = fastFloor3);
                        n46 = (n47 = n22 - 0.309016994374947);
                        if ((b7 & 0x3) == 0x3) {
                            --n44;
                            ++n47;
                        }
                        else {
                            --n43;
                            ++n46;
                        }
                    }
                    else {
                        n43 = (n44 = fastFloor3 + 1);
                        n46 = (n47 = n22 - 1.0 - 0.309016994374947);
                    }
                    if ((b7 & 0x8) == 0x0) {
                        n50 = fastFloor4;
                        n49 = fastFloor4 - 1;
                        n53 = n23 - 0.309016994374947;
                        n52 = n23 + 1.0 - 0.309016994374947;
                    }
                    else {
                        n49 = (n50 = fastFloor4 + 1);
                        n52 = (n53 = n23 - 1.0 - 0.309016994374947);
                    }
                }
            }
            else {
                int n127;
                int n128;
                if (b3) {
                    n127 = n118;
                    n128 = n120;
                }
                else {
                    n127 = n120;
                    n128 = n118;
                }
                if ((n127 & 0x1) == 0x0) {
                    n31 = fastFloor - 1;
                    n33 = fastFloor;
                    n34 = n20 + 1.0 - 0.309016994374947;
                    n36 = n20 - 0.309016994374947;
                }
                else {
                    n33 = (n31 = fastFloor + 1);
                    n36 = (n34 = n20 - 1.0 - 0.309016994374947);
                }
                if ((n127 & 0x2) == 0x0) {
                    n37 = (n38 = fastFloor2);
                    n40 = (n41 = n21 - 0.309016994374947);
                    if ((n127 & 0x1) == 0x1) {
                        --n38;
                        ++n41;
                    }
                    else {
                        --n37;
                        ++n40;
                    }
                }
                else {
                    n37 = (n38 = fastFloor2 + 1);
                    n40 = (n41 = n21 - 1.0 - 0.309016994374947);
                }
                if ((n127 & 0x4) == 0x0) {
                    n43 = (n44 = fastFloor3);
                    n46 = (n47 = n22 - 0.309016994374947);
                    if ((n127 & 0x3) == 0x3) {
                        --n44;
                        ++n47;
                    }
                    else {
                        --n43;
                        ++n46;
                    }
                }
                else {
                    n43 = (n44 = fastFloor3 + 1);
                    n46 = (n47 = n22 - 1.0 - 0.309016994374947);
                }
                if ((n127 & 0x8) == 0x0) {
                    n50 = fastFloor4;
                    n49 = fastFloor4 - 1;
                    n53 = n23 - 0.309016994374947;
                    n52 = n23 + 1.0 - 0.309016994374947;
                }
                else {
                    n49 = (n50 = fastFloor4 + 1);
                    n52 = (n53 = n23 - 1.0 - 0.309016994374947);
                }
                n32 = fastFloor;
                n39 = fastFloor2;
                n45 = fastFloor3;
                n51 = fastFloor4;
                n35 = n20 - 0.618033988749894;
                n42 = n21 - 0.618033988749894;
                n48 = n22 - 0.618033988749894;
                n54 = n23 - 0.618033988749894;
                if ((n128 & 0x1) != 0x0) {
                    n32 += 2;
                    n35 -= 2.0;
                }
                else if ((n128 & 0x2) != 0x0) {
                    n39 += 2;
                    n42 -= 2.0;
                }
                else if ((n128 & 0x4) != 0x0) {
                    n45 += 2;
                    n48 -= 2.0;
                }
                else {
                    n51 += 2;
                    n54 -= 2.0;
                }
            }
            final double n129 = n20 - 1.0 - 0.309016994374947;
            final double n130 = n21 - 0.0 - 0.309016994374947;
            final double n131 = n22 - 0.0 - 0.309016994374947;
            final double n132 = n23 - 0.0 - 0.309016994374947;
            final double n133 = 2.0 - n129 * n129 - n130 * n130 - n131 * n131 - n132 * n132;
            if (n133 > 0.0) {
                final double n134 = n133 * n133;
                n24 += n134 * n134 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 0, fastFloor4 + 0, n129, n130, n131, n132);
            }
            final double n135 = n20 - 0.0 - 0.309016994374947;
            final double n136 = n21 - 1.0 - 0.309016994374947;
            final double n137 = n131;
            final double n138 = n132;
            final double n139 = 2.0 - n135 * n135 - n136 * n136 - n137 * n137 - n138 * n138;
            if (n139 > 0.0) {
                final double n140 = n139 * n139;
                n24 += n140 * n140 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 0, n135, n136, n137, n138);
            }
            final double n141 = n135;
            final double n142 = n130;
            final double n143 = n22 - 1.0 - 0.309016994374947;
            final double n144 = n132;
            final double n145 = 2.0 - n141 * n141 - n142 * n142 - n143 * n143 - n144 * n144;
            if (n145 > 0.0) {
                final double n146 = n145 * n145;
                n24 += n146 * n146 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 0, n141, n142, n143, n144);
            }
            final double n147 = n135;
            final double n148 = n130;
            final double n149 = n131;
            final double n150 = n23 - 1.0 - 0.309016994374947;
            final double n151 = 2.0 - n147 * n147 - n148 * n148 - n149 * n149 - n150 * n150;
            if (n151 > 0.0) {
                final double n152 = n151 * n151;
                n24 += n152 * n152 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 0, fastFloor4 + 1, n147, n148, n149, n150);
            }
            final double n153 = n20 - 1.0 - 0.618033988749894;
            final double n154 = n21 - 1.0 - 0.618033988749894;
            final double n155 = n22 - 0.0 - 0.618033988749894;
            final double n156 = n23 - 0.0 - 0.618033988749894;
            final double n157 = 2.0 - n153 * n153 - n154 * n154 - n155 * n155 - n156 * n156;
            if (n157 > 0.0) {
                final double n158 = n157 * n157;
                n24 += n158 * n158 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 0, n153, n154, n155, n156);
            }
            final double n159 = n20 - 1.0 - 0.618033988749894;
            final double n160 = n21 - 0.0 - 0.618033988749894;
            final double n161 = n22 - 1.0 - 0.618033988749894;
            final double n162 = n23 - 0.0 - 0.618033988749894;
            final double n163 = 2.0 - n159 * n159 - n160 * n160 - n161 * n161 - n162 * n162;
            if (n163 > 0.0) {
                final double n164 = n163 * n163;
                n24 += n164 * n164 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 0, n159, n160, n161, n162);
            }
            final double n165 = n20 - 1.0 - 0.618033988749894;
            final double n166 = n21 - 0.0 - 0.618033988749894;
            final double n167 = n22 - 0.0 - 0.618033988749894;
            final double n168 = n23 - 1.0 - 0.618033988749894;
            final double n169 = 2.0 - n165 * n165 - n166 * n166 - n167 * n167 - n168 * n168;
            if (n169 > 0.0) {
                final double n170 = n169 * n169;
                n24 += n170 * n170 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 0, fastFloor4 + 1, n165, n166, n167, n168);
            }
            final double n171 = n20 - 0.0 - 0.618033988749894;
            final double n172 = n21 - 1.0 - 0.618033988749894;
            final double n173 = n22 - 1.0 - 0.618033988749894;
            final double n174 = n23 - 0.0 - 0.618033988749894;
            final double n175 = 2.0 - n171 * n171 - n172 * n172 - n173 * n173 - n174 * n174;
            if (n175 > 0.0) {
                final double n176 = n175 * n175;
                n24 += n176 * n176 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 1, fastFloor4 + 0, n171, n172, n173, n174);
            }
            final double n177 = n20 - 0.0 - 0.618033988749894;
            final double n178 = n21 - 1.0 - 0.618033988749894;
            final double n179 = n22 - 0.0 - 0.618033988749894;
            final double n180 = n23 - 1.0 - 0.618033988749894;
            final double n181 = 2.0 - n177 * n177 - n178 * n178 - n179 * n179 - n180 * n180;
            if (n181 > 0.0) {
                final double n182 = n181 * n181;
                n24 += n182 * n182 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 1, n177, n178, n179, n180);
            }
            final double n183 = n20 - 0.0 - 0.618033988749894;
            final double n184 = n21 - 0.0 - 0.618033988749894;
            final double n185 = n22 - 1.0 - 0.618033988749894;
            final double n186 = n23 - 1.0 - 0.618033988749894;
            final double n187 = 2.0 - n183 * n183 - n184 * n184 - n185 * n185 - n186 * n186;
            if (n187 > 0.0) {
                final double n188 = n187 * n187;
                n24 += n188 * n188 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 1, n183, n184, n185, n186);
            }
        }
        else {
            boolean b8 = true;
            boolean b9 = true;
            double n189;
            int n190;
            if (n15 + n16 < n17 + n18) {
                n189 = n15 + n16;
                n190 = 12;
            }
            else {
                n189 = n17 + n18;
                n190 = 3;
            }
            double n191;
            int n192;
            if (n15 + n17 < n16 + n18) {
                n191 = n15 + n17;
                n192 = 10;
            }
            else {
                n191 = n16 + n18;
                n192 = 5;
            }
            if (n15 + n18 < n16 + n17) {
                final double n193 = n15 + n18;
                if (n189 <= n191 && n193 < n191) {
                    n191 = n193;
                    n192 = 6;
                }
                else if (n189 > n191 && n193 < n189) {
                    n189 = n193;
                    n190 = 6;
                }
            }
            else {
                final double n194 = n16 + n17;
                if (n189 <= n191 && n194 < n191) {
                    n191 = n194;
                    n192 = 9;
                }
                else if (n189 > n191 && n194 < n189) {
                    n189 = n194;
                    n190 = 9;
                }
            }
            final double n195 = 3.0 - n19 + n15;
            if (n189 <= n191 && n195 < n191) {
                n191 = n195;
                n192 = 14;
                b9 = false;
            }
            else if (n189 > n191 && n195 < n189) {
                n189 = n195;
                n190 = 14;
                b8 = false;
            }
            final double n196 = 3.0 - n19 + n16;
            if (n189 <= n191 && n196 < n191) {
                n191 = n196;
                n192 = 13;
                b9 = false;
            }
            else if (n189 > n191 && n196 < n189) {
                n189 = n196;
                n190 = 13;
                b8 = false;
            }
            final double n197 = 3.0 - n19 + n17;
            if (n189 <= n191 && n197 < n191) {
                n191 = n197;
                n192 = 11;
                b9 = false;
            }
            else if (n189 > n191 && n197 < n189) {
                n189 = n197;
                n190 = 11;
                b8 = false;
            }
            final double n198 = 3.0 - n19 + n18;
            if (n189 <= n191 && n198 < n191) {
                n192 = 7;
                b9 = false;
            }
            else if (n189 > n191 && n198 < n189) {
                n190 = 7;
                b8 = false;
            }
            if (b8 == b9) {
                if (b8) {
                    final byte b10 = (byte)(n190 & n192);
                    final byte b11 = (byte)(n190 | n192);
                    n33 = (n31 = fastFloor);
                    n37 = (n38 = fastFloor2);
                    n43 = (n44 = fastFloor3);
                    n49 = (n50 = fastFloor4);
                    n34 = n20 - 0.309016994374947;
                    n41 = n21 - 0.309016994374947;
                    n47 = n22 - 0.309016994374947;
                    n53 = n23 - 0.309016994374947;
                    n36 = n20 - 0.618033988749894;
                    n40 = n21 - 0.618033988749894;
                    n46 = n22 - 0.618033988749894;
                    n52 = n23 - 0.618033988749894;
                    if ((b10 & 0x1) != 0x0) {
                        ++n31;
                        --n34;
                        n33 += 2;
                        n36 -= 2.0;
                    }
                    else if ((b10 & 0x2) != 0x0) {
                        ++n38;
                        --n41;
                        n37 += 2;
                        n40 -= 2.0;
                    }
                    else if ((b10 & 0x4) != 0x0) {
                        ++n44;
                        --n47;
                        n43 += 2;
                        n46 -= 2.0;
                    }
                    else {
                        ++n50;
                        --n53;
                        n49 += 2;
                        n52 -= 2.0;
                    }
                    n32 = fastFloor + 1;
                    n39 = fastFloor2 + 1;
                    n45 = fastFloor3 + 1;
                    n51 = fastFloor4 + 1;
                    n35 = n20 - 1.0 - 0.618033988749894;
                    n42 = n21 - 1.0 - 0.618033988749894;
                    n48 = n22 - 1.0 - 0.618033988749894;
                    n54 = n23 - 1.0 - 0.618033988749894;
                    if ((b11 & 0x1) == 0x0) {
                        n32 -= 2;
                        n35 += 2.0;
                    }
                    else if ((b11 & 0x2) == 0x0) {
                        n39 -= 2;
                        n42 += 2.0;
                    }
                    else if ((b11 & 0x4) == 0x0) {
                        n45 -= 2;
                        n48 += 2.0;
                    }
                    else {
                        n51 -= 2;
                        n54 += 2.0;
                    }
                }
                else {
                    n32 = fastFloor + 1;
                    n39 = fastFloor2 + 1;
                    n45 = fastFloor3 + 1;
                    n51 = fastFloor4 + 1;
                    n35 = n20 - 1.0 - 1.236067977499788;
                    n42 = n21 - 1.0 - 1.236067977499788;
                    n48 = n22 - 1.0 - 1.236067977499788;
                    n54 = n23 - 1.0 - 1.236067977499788;
                    final byte b12 = (byte)(n190 & n192);
                    if ((b12 & 0x1) != 0x0) {
                        n31 = fastFloor + 2;
                        n33 = fastFloor + 1;
                        n34 = n20 - 2.0 - 0.927050983124841;
                        n36 = n20 - 1.0 - 0.927050983124841;
                    }
                    else {
                        n33 = (n31 = fastFloor);
                        n36 = (n34 = n20 - 0.927050983124841);
                    }
                    if ((b12 & 0x2) != 0x0) {
                        n37 = (n38 = fastFloor2 + 1);
                        n40 = (n41 = n21 - 1.0 - 0.927050983124841);
                        if ((b12 & 0x1) == 0x0) {
                            ++n38;
                            --n41;
                        }
                        else {
                            ++n37;
                            --n40;
                        }
                    }
                    else {
                        n37 = (n38 = fastFloor2);
                        n40 = (n41 = n21 - 0.927050983124841);
                    }
                    if ((b12 & 0x4) != 0x0) {
                        n43 = (n44 = fastFloor3 + 1);
                        n46 = (n47 = n22 - 1.0 - 0.927050983124841);
                        if ((b12 & 0x3) == 0x0) {
                            ++n44;
                            --n47;
                        }
                        else {
                            ++n43;
                            --n46;
                        }
                    }
                    else {
                        n43 = (n44 = fastFloor3);
                        n46 = (n47 = n22 - 0.927050983124841);
                    }
                    if ((b12 & 0x8) != 0x0) {
                        n50 = fastFloor4 + 1;
                        n49 = fastFloor4 + 2;
                        n53 = n23 - 1.0 - 0.927050983124841;
                        n52 = n23 - 2.0 - 0.927050983124841;
                    }
                    else {
                        n49 = (n50 = fastFloor4);
                        n52 = (n53 = n23 - 0.927050983124841);
                    }
                }
            }
            else {
                int n199;
                int n200;
                if (b8) {
                    n199 = n190;
                    n200 = n192;
                }
                else {
                    n199 = n192;
                    n200 = n190;
                }
                if ((n199 & 0x1) != 0x0) {
                    n31 = fastFloor + 2;
                    n33 = fastFloor + 1;
                    n34 = n20 - 2.0 - 0.927050983124841;
                    n36 = n20 - 1.0 - 0.927050983124841;
                }
                else {
                    n33 = (n31 = fastFloor);
                    n36 = (n34 = n20 - 0.927050983124841);
                }
                if ((n199 & 0x2) != 0x0) {
                    n37 = (n38 = fastFloor2 + 1);
                    n40 = (n41 = n21 - 1.0 - 0.927050983124841);
                    if ((n199 & 0x1) == 0x0) {
                        ++n38;
                        --n41;
                    }
                    else {
                        ++n37;
                        --n40;
                    }
                }
                else {
                    n37 = (n38 = fastFloor2);
                    n40 = (n41 = n21 - 0.927050983124841);
                }
                if ((n199 & 0x4) != 0x0) {
                    n43 = (n44 = fastFloor3 + 1);
                    n46 = (n47 = n22 - 1.0 - 0.927050983124841);
                    if ((n199 & 0x3) == 0x0) {
                        ++n44;
                        --n47;
                    }
                    else {
                        ++n43;
                        --n46;
                    }
                }
                else {
                    n43 = (n44 = fastFloor3);
                    n46 = (n47 = n22 - 0.927050983124841);
                }
                if ((n199 & 0x8) != 0x0) {
                    n50 = fastFloor4 + 1;
                    n49 = fastFloor4 + 2;
                    n53 = n23 - 1.0 - 0.927050983124841;
                    n52 = n23 - 2.0 - 0.927050983124841;
                }
                else {
                    n49 = (n50 = fastFloor4);
                    n52 = (n53 = n23 - 0.927050983124841);
                }
                n32 = fastFloor + 1;
                n39 = fastFloor2 + 1;
                n45 = fastFloor3 + 1;
                n51 = fastFloor4 + 1;
                n35 = n20 - 1.0 - 0.618033988749894;
                n42 = n21 - 1.0 - 0.618033988749894;
                n48 = n22 - 1.0 - 0.618033988749894;
                n54 = n23 - 1.0 - 0.618033988749894;
                if ((n200 & 0x1) == 0x0) {
                    n32 -= 2;
                    n35 += 2.0;
                }
                else if ((n200 & 0x2) == 0x0) {
                    n39 -= 2;
                    n42 += 2.0;
                }
                else if ((n200 & 0x4) == 0x0) {
                    n45 -= 2;
                    n48 += 2.0;
                }
                else {
                    n51 -= 2;
                    n54 += 2.0;
                }
            }
            final double n201 = n20 - 1.0 - 0.927050983124841;
            final double n202 = n21 - 1.0 - 0.927050983124841;
            final double n203 = n22 - 1.0 - 0.927050983124841;
            final double n204 = n23 - 0.927050983124841;
            final double n205 = 2.0 - n201 * n201 - n202 * n202 - n203 * n203 - n204 * n204;
            if (n205 > 0.0) {
                final double n206 = n205 * n205;
                n24 += n206 * n206 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 1, fastFloor4 + 0, n201, n202, n203, n204);
            }
            final double n207 = n201;
            final double n208 = n202;
            final double n209 = n22 - 0.927050983124841;
            final double n210 = n23 - 1.0 - 0.927050983124841;
            final double n211 = 2.0 - n207 * n207 - n208 * n208 - n209 * n209 - n210 * n210;
            if (n211 > 0.0) {
                final double n212 = n211 * n211;
                n24 += n212 * n212 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 1, n207, n208, n209, n210);
            }
            final double n213 = n201;
            final double n214 = n21 - 0.927050983124841;
            final double n215 = n203;
            final double n216 = n210;
            final double n217 = 2.0 - n213 * n213 - n214 * n214 - n215 * n215 - n216 * n216;
            if (n217 > 0.0) {
                final double n218 = n217 * n217;
                n24 += n218 * n218 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 1, n213, n214, n215, n216);
            }
            final double n219 = n20 - 0.927050983124841;
            final double n220 = n203;
            final double n221 = n202;
            final double n222 = n210;
            final double n223 = 2.0 - n219 * n219 - n221 * n221 - n220 * n220 - n222 * n222;
            if (n223 > 0.0) {
                final double n224 = n223 * n223;
                n24 += n224 * n224 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 1, fastFloor4 + 1, n219, n221, n220, n222);
            }
            final double n225 = n20 - 1.0 - 0.618033988749894;
            final double n226 = n21 - 1.0 - 0.618033988749894;
            final double n227 = n22 - 0.0 - 0.618033988749894;
            final double n228 = n23 - 0.0 - 0.618033988749894;
            final double n229 = 2.0 - n225 * n225 - n226 * n226 - n227 * n227 - n228 * n228;
            if (n229 > 0.0) {
                final double n230 = n229 * n229;
                n24 += n230 * n230 * this.extrapolate(fastFloor + 1, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 0, n225, n226, n227, n228);
            }
            final double n231 = n20 - 1.0 - 0.618033988749894;
            final double n232 = n21 - 0.0 - 0.618033988749894;
            final double n233 = n22 - 1.0 - 0.618033988749894;
            final double n234 = n23 - 0.0 - 0.618033988749894;
            final double n235 = 2.0 - n231 * n231 - n232 * n232 - n233 * n233 - n234 * n234;
            if (n235 > 0.0) {
                final double n236 = n235 * n235;
                n24 += n236 * n236 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 0, n231, n232, n233, n234);
            }
            final double n237 = n20 - 1.0 - 0.618033988749894;
            final double n238 = n21 - 0.0 - 0.618033988749894;
            final double n239 = n22 - 0.0 - 0.618033988749894;
            final double n240 = n23 - 1.0 - 0.618033988749894;
            final double n241 = 2.0 - n237 * n237 - n238 * n238 - n239 * n239 - n240 * n240;
            if (n241 > 0.0) {
                final double n242 = n241 * n241;
                n24 += n242 * n242 * this.extrapolate(fastFloor + 1, fastFloor2 + 0, fastFloor3 + 0, fastFloor4 + 1, n237, n238, n239, n240);
            }
            final double n243 = n20 - 0.0 - 0.618033988749894;
            final double n244 = n21 - 1.0 - 0.618033988749894;
            final double n245 = n22 - 1.0 - 0.618033988749894;
            final double n246 = n23 - 0.0 - 0.618033988749894;
            final double n247 = 2.0 - n243 * n243 - n244 * n244 - n245 * n245 - n246 * n246;
            if (n247 > 0.0) {
                final double n248 = n247 * n247;
                n24 += n248 * n248 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 1, fastFloor4 + 0, n243, n244, n245, n246);
            }
            final double n249 = n20 - 0.0 - 0.618033988749894;
            final double n250 = n21 - 1.0 - 0.618033988749894;
            final double n251 = n22 - 0.0 - 0.618033988749894;
            final double n252 = n23 - 1.0 - 0.618033988749894;
            final double n253 = 2.0 - n249 * n249 - n250 * n250 - n251 * n251 - n252 * n252;
            if (n253 > 0.0) {
                final double n254 = n253 * n253;
                n24 += n254 * n254 * this.extrapolate(fastFloor + 0, fastFloor2 + 1, fastFloor3 + 0, fastFloor4 + 1, n249, n250, n251, n252);
            }
            final double n255 = n20 - 0.0 - 0.618033988749894;
            final double n256 = n21 - 0.0 - 0.618033988749894;
            final double n257 = n22 - 1.0 - 0.618033988749894;
            final double n258 = n23 - 1.0 - 0.618033988749894;
            final double n259 = 2.0 - n255 * n255 - n256 * n256 - n257 * n257 - n258 * n258;
            if (n259 > 0.0) {
                final double n260 = n259 * n259;
                n24 += n260 * n260 * this.extrapolate(fastFloor + 0, fastFloor2 + 0, fastFloor3 + 1, fastFloor4 + 1, n255, n256, n257, n258);
            }
        }
        final double n261 = 2.0 - n34 * n34 - n41 * n41 - n47 * n47 - n53 * n53;
        if (n261 > 0.0) {
            final double n262 = n261 * n261;
            n24 += n262 * n262 * this.extrapolate(n31, n38, n44, n50, n34, n41, n47, n53);
        }
        final double n263 = 2.0 - n36 * n36 - n40 * n40 - n46 * n46 - n52 * n52;
        if (n263 > 0.0) {
            final double n264 = n263 * n263;
            n24 += n264 * n264 * this.extrapolate(n33, n37, n43, n49, n36, n40, n46, n52);
        }
        final double n265 = 2.0 - n35 * n35 - n42 * n42 - n48 * n48 - n54 * n54;
        if (n265 > 0.0) {
            final double n266 = n265 * n265;
            n24 += n266 * n266 * this.extrapolate(n32, n39, n45, n51, n35, n42, n48, n54);
        }
        return n24 / 30.0;
    }
    
    private double extrapolate(final int n, final int n2, final double n3, final double n4) {
        final int n5 = this.perm[this.perm[n & 0xFF] + n2 & 0xFF] & 0xE;
        return OpenSimplexNoise.gradients2D[n5] * n3 + OpenSimplexNoise.gradients2D[n5 + 1] * n4;
    }
    
    private double extrapolate(final int n, final int n2, final int n3, final double n4, final double n5, final double n6) {
        final short n7 = this.permGradIndex3D[this.perm[this.perm[n & 0xFF] + n2 & 0xFF] + n3 & 0xFF];
        return OpenSimplexNoise.gradients3D[n7] * n4 + OpenSimplexNoise.gradients3D[n7 + 1] * n5 + OpenSimplexNoise.gradients3D[n7 + 2] * n6;
    }
    
    private double extrapolate(final int n, final int n2, final int n3, final int n4, final double n5, final double n6, final double n7, final double n8) {
        final int n9 = this.perm[this.perm[this.perm[this.perm[n & 0xFF] + n2 & 0xFF] + n3 & 0xFF] + n4 & 0xFF] & 0xFC;
        return OpenSimplexNoise.gradients4D[n9] * n5 + OpenSimplexNoise.gradients4D[n9 + 1] * n6 + OpenSimplexNoise.gradients4D[n9 + 2] * n7 + OpenSimplexNoise.gradients4D[n9 + 3] * n8;
    }
    
    private static int fastFloor(final double n) {
        final int n2 = (int)n;
        return (n < n2) ? (n2 - 1) : n2;
    }
    
    public double evalOct(final float n, final float n2, final int n3) {
        double eval = this.eval(n, n2, n3);
        for (int i = 2; i <= 64; ++i) {
            eval += this.eval(n * i * n, n2 * i * n2, n3 * i * n3);
        }
        return eval;
    }
    
    static {
        OpenSimplexNoise.gradients2D = new byte[] { 5, 2, 2, 5, -5, 2, -2, 5, 5, -2, 2, -5, -5, -2, -2, -5 };
        OpenSimplexNoise.gradients3D = new byte[] { -11, 4, 4, -4, 11, 4, -4, 4, 11, 11, 4, 4, 4, 11, 4, 4, 4, 11, -11, -4, 4, -4, -11, 4, -4, -4, 11, 11, -4, 4, 4, -11, 4, 4, -4, 11, -11, 4, -4, -4, 11, -4, -4, 4, -11, 11, 4, -4, 4, 11, -4, 4, 4, -11, -11, -4, -4, -4, -11, -4, -4, -4, -11, 11, -4, -4, 4, -11, -4, 4, -4, -11 };
        OpenSimplexNoise.gradients4D = new byte[] { 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3, 3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3, -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3, 3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3, -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3, 3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3, -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3, 3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3, -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3, 3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3, -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3, 3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3, -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3, 3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3, -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3 };
    }
}
