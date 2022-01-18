// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

public class SimplexNoise
{
    private static Grad[] grad3;
    private static Grad[] grad4;
    private static short[] p;
    private static short[] perm;
    private static short[] permMod12;
    private static final double F2;
    private static final double G2;
    private static final double F3 = 0.3333333333333333;
    private static final double G3 = 0.16666666666666666;
    private static final double F4;
    private static final double G4;
    
    private static int fastfloor(final double n) {
        final int n2 = (int)n;
        return (n < n2) ? (n2 - 1) : n2;
    }
    
    private static double dot(final Grad grad, final double n, final double n2) {
        return grad.x * n + grad.y * n2;
    }
    
    private static double dot(final Grad grad, final double n, final double n2, final double n3) {
        return grad.x * n + grad.y * n2 + grad.z * n3;
    }
    
    private static double dot(final Grad grad, final double n, final double n2, final double n3, final double n4) {
        return grad.x * n + grad.y * n2 + grad.z * n3 + grad.w * n4;
    }
    
    public static double noise(final double n, final double n2) {
        final double n3 = (n + n2) * SimplexNoise.F2;
        final int fastfloor = fastfloor(n + n3);
        final int fastfloor2 = fastfloor(n2 + n3);
        final double n4 = (fastfloor + fastfloor2) * SimplexNoise.G2;
        final double n5 = fastfloor - n4;
        final double n6 = fastfloor2 - n4;
        final double n7 = n - n5;
        final double n8 = n2 - n6;
        short n9;
        int n10;
        if (n7 > n8) {
            n9 = 1;
            n10 = 0;
        }
        else {
            n9 = 0;
            n10 = 1;
        }
        final double n11 = n7 - n9 + SimplexNoise.G2;
        final double n12 = n8 - n10 + SimplexNoise.G2;
        final double n13 = n7 - 1.0 + 2.0 * SimplexNoise.G2;
        final double n14 = n8 - 1.0 + 2.0 * SimplexNoise.G2;
        final int n15 = fastfloor & 0xFF;
        final int n16 = fastfloor2 & 0xFF;
        final short n17 = SimplexNoise.permMod12[n15 + SimplexNoise.perm[n16]];
        final short n18 = SimplexNoise.permMod12[n15 + n9 + SimplexNoise.perm[n16 + n10]];
        final short n19 = SimplexNoise.permMod12[n15 + 1 + SimplexNoise.perm[n16 + 1]];
        final double n20 = 0.5 - n7 * n7 - n8 * n8;
        double n21;
        if (n20 < 0.0) {
            n21 = 0.0;
        }
        else {
            final double n22 = n20 * n20;
            n21 = n22 * n22 * dot(SimplexNoise.grad3[n17], n7, n8);
        }
        final double n23 = 0.5 - n11 * n11 - n12 * n12;
        double n24;
        if (n23 < 0.0) {
            n24 = 0.0;
        }
        else {
            final double n25 = n23 * n23;
            n24 = n25 * n25 * dot(SimplexNoise.grad3[n18], n11, n12);
        }
        final double n26 = 0.5 - n13 * n13 - n14 * n14;
        double n27;
        if (n26 < 0.0) {
            n27 = 0.0;
        }
        else {
            final double n28 = n26 * n26;
            n27 = n28 * n28 * dot(SimplexNoise.grad3[n19], n13, n14);
        }
        return 70.0 * (n21 + n24 + n27);
    }
    
    public static double noise(final double n, final double n2, final double n3) {
        final double n4 = (n + n2 + n3) * 0.3333333333333333;
        final int fastfloor = fastfloor(n + n4);
        final int fastfloor2 = fastfloor(n2 + n4);
        final int fastfloor3 = fastfloor(n3 + n4);
        final double n5 = (fastfloor + fastfloor2 + fastfloor3) * 0.16666666666666666;
        final double n6 = fastfloor - n5;
        final double n7 = fastfloor2 - n5;
        final double n8 = fastfloor3 - n5;
        final double n9 = n - n6;
        final double n10 = n2 - n7;
        final double n11 = n3 - n8;
        short n12;
        short n13;
        int n14;
        short n15;
        short n16;
        int n17;
        if (n9 >= n10) {
            if (n10 >= n11) {
                n12 = 1;
                n13 = 0;
                n14 = 0;
                n15 = 1;
                n16 = 1;
                n17 = 0;
            }
            else if (n9 >= n11) {
                n12 = 1;
                n13 = 0;
                n14 = 0;
                n15 = 1;
                n16 = 0;
                n17 = 1;
            }
            else {
                n12 = 0;
                n13 = 0;
                n14 = 1;
                n15 = 1;
                n16 = 0;
                n17 = 1;
            }
        }
        else if (n10 < n11) {
            n12 = 0;
            n13 = 0;
            n14 = 1;
            n15 = 0;
            n16 = 1;
            n17 = 1;
        }
        else if (n9 < n11) {
            n12 = 0;
            n13 = 1;
            n14 = 0;
            n15 = 0;
            n16 = 1;
            n17 = 1;
        }
        else {
            n12 = 0;
            n13 = 1;
            n14 = 0;
            n15 = 1;
            n16 = 1;
            n17 = 0;
        }
        final double n18 = n9 - n12 + 0.16666666666666666;
        final double n19 = n10 - n13 + 0.16666666666666666;
        final double n20 = n11 - n14 + 0.16666666666666666;
        final double n21 = n9 - n15 + 0.3333333333333333;
        final double n22 = n10 - n16 + 0.3333333333333333;
        final double n23 = n11 - n17 + 0.3333333333333333;
        final double n24 = n9 - 1.0 + 0.5;
        final double n25 = n10 - 1.0 + 0.5;
        final double n26 = n11 - 1.0 + 0.5;
        final int n27 = fastfloor & 0xFF;
        final int n28 = fastfloor2 & 0xFF;
        final int n29 = fastfloor3 & 0xFF;
        final short n30 = SimplexNoise.permMod12[n27 + SimplexNoise.perm[n28 + SimplexNoise.perm[n29]]];
        final short n31 = SimplexNoise.permMod12[n27 + n12 + SimplexNoise.perm[n28 + n13 + SimplexNoise.perm[n29 + n14]]];
        final short n32 = SimplexNoise.permMod12[n27 + n15 + SimplexNoise.perm[n28 + n16 + SimplexNoise.perm[n29 + n17]]];
        final short n33 = SimplexNoise.permMod12[n27 + 1 + SimplexNoise.perm[n28 + 1 + SimplexNoise.perm[n29 + 1]]];
        final double n34 = 0.6 - n9 * n9 - n10 * n10 - n11 * n11;
        double n35;
        if (n34 < 0.0) {
            n35 = 0.0;
        }
        else {
            final double n36 = n34 * n34;
            n35 = n36 * n36 * dot(SimplexNoise.grad3[n30], n9, n10, n11);
        }
        final double n37 = 0.6 - n18 * n18 - n19 * n19 - n20 * n20;
        double n38;
        if (n37 < 0.0) {
            n38 = 0.0;
        }
        else {
            final double n39 = n37 * n37;
            n38 = n39 * n39 * dot(SimplexNoise.grad3[n31], n18, n19, n20);
        }
        final double n40 = 0.6 - n21 * n21 - n22 * n22 - n23 * n23;
        double n41;
        if (n40 < 0.0) {
            n41 = 0.0;
        }
        else {
            final double n42 = n40 * n40;
            n41 = n42 * n42 * dot(SimplexNoise.grad3[n32], n21, n22, n23);
        }
        final double n43 = 0.6 - n24 * n24 - n25 * n25 - n26 * n26;
        double n44;
        if (n43 < 0.0) {
            n44 = 0.0;
        }
        else {
            final double n45 = n43 * n43;
            n44 = n45 * n45 * dot(SimplexNoise.grad3[n33], n24, n25, n26);
        }
        return 32.0 * (n35 + n38 + n41 + n44);
    }
    
    public static double noise(final double n, final double n2, final double n3, final double n4) {
        final double n5 = (n + n2 + n3 + n4) * SimplexNoise.F4;
        final int fastfloor = fastfloor(n + n5);
        final int fastfloor2 = fastfloor(n2 + n5);
        final int fastfloor3 = fastfloor(n3 + n5);
        final int fastfloor4 = fastfloor(n4 + n5);
        final double n6 = (fastfloor + fastfloor2 + fastfloor3 + fastfloor4) * SimplexNoise.G4;
        final double n7 = fastfloor - n6;
        final double n8 = fastfloor2 - n6;
        final double n9 = fastfloor3 - n6;
        final double n10 = fastfloor4 - n6;
        final double n11 = n - n7;
        final double n12 = n2 - n8;
        final double n13 = n3 - n9;
        final double n14 = n4 - n10;
        int n15 = 0;
        int n16 = 0;
        int n17 = 0;
        int n18 = 0;
        if (n11 > n12) {
            ++n15;
        }
        else {
            ++n16;
        }
        if (n11 > n13) {
            ++n15;
        }
        else {
            ++n17;
        }
        if (n11 > n14) {
            ++n15;
        }
        else {
            ++n18;
        }
        if (n12 > n13) {
            ++n16;
        }
        else {
            ++n17;
        }
        if (n12 > n14) {
            ++n16;
        }
        else {
            ++n18;
        }
        if (n13 > n14) {
            ++n17;
        }
        else {
            ++n18;
        }
        final int n19 = (n15 >= 3) ? 1 : 0;
        final int n20 = (n16 >= 3) ? 1 : 0;
        final int n21 = (n17 >= 3) ? 1 : 0;
        final int n22 = (n18 >= 3) ? 1 : 0;
        final int n23 = (n15 >= 2) ? 1 : 0;
        final int n24 = (n16 >= 2) ? 1 : 0;
        final int n25 = (n17 >= 2) ? 1 : 0;
        final int n26 = (n18 >= 2) ? 1 : 0;
        final int n27 = (n15 >= 1) ? 1 : 0;
        final int n28 = (n16 >= 1) ? 1 : 0;
        final int n29 = (n17 >= 1) ? 1 : 0;
        final int n30 = (n18 >= 1) ? 1 : 0;
        final double n31 = n11 - n19 + SimplexNoise.G4;
        final double n32 = n12 - n20 + SimplexNoise.G4;
        final double n33 = n13 - n21 + SimplexNoise.G4;
        final double n34 = n14 - n22 + SimplexNoise.G4;
        final double n35 = n11 - n23 + 2.0 * SimplexNoise.G4;
        final double n36 = n12 - n24 + 2.0 * SimplexNoise.G4;
        final double n37 = n13 - n25 + 2.0 * SimplexNoise.G4;
        final double n38 = n14 - n26 + 2.0 * SimplexNoise.G4;
        final double n39 = n11 - n27 + 3.0 * SimplexNoise.G4;
        final double n40 = n12 - n28 + 3.0 * SimplexNoise.G4;
        final double n41 = n13 - n29 + 3.0 * SimplexNoise.G4;
        final double n42 = n14 - n30 + 3.0 * SimplexNoise.G4;
        final double n43 = n11 - 1.0 + 4.0 * SimplexNoise.G4;
        final double n44 = n12 - 1.0 + 4.0 * SimplexNoise.G4;
        final double n45 = n13 - 1.0 + 4.0 * SimplexNoise.G4;
        final double n46 = n14 - 1.0 + 4.0 * SimplexNoise.G4;
        final int n47 = fastfloor & 0xFF;
        final int n48 = fastfloor2 & 0xFF;
        final int n49 = fastfloor3 & 0xFF;
        final int n50 = fastfloor4 & 0xFF;
        final int n51 = SimplexNoise.perm[n47 + SimplexNoise.perm[n48 + SimplexNoise.perm[n49 + SimplexNoise.perm[n50]]]] % 32;
        final int n52 = SimplexNoise.perm[n47 + n19 + SimplexNoise.perm[n48 + n20 + SimplexNoise.perm[n49 + n21 + SimplexNoise.perm[n50 + n22]]]] % 32;
        final int n53 = SimplexNoise.perm[n47 + n23 + SimplexNoise.perm[n48 + n24 + SimplexNoise.perm[n49 + n25 + SimplexNoise.perm[n50 + n26]]]] % 32;
        final int n54 = SimplexNoise.perm[n47 + n27 + SimplexNoise.perm[n48 + n28 + SimplexNoise.perm[n49 + n29 + SimplexNoise.perm[n50 + n30]]]] % 32;
        final int n55 = SimplexNoise.perm[n47 + 1 + SimplexNoise.perm[n48 + 1 + SimplexNoise.perm[n49 + 1 + SimplexNoise.perm[n50 + 1]]]] % 32;
        final double n56 = 0.6 - n11 * n11 - n12 * n12 - n13 * n13 - n14 * n14;
        double n57;
        if (n56 < 0.0) {
            n57 = 0.0;
        }
        else {
            final double n58 = n56 * n56;
            n57 = n58 * n58 * dot(SimplexNoise.grad4[n51], n11, n12, n13, n14);
        }
        final double n59 = 0.6 - n31 * n31 - n32 * n32 - n33 * n33 - n34 * n34;
        double n60;
        if (n59 < 0.0) {
            n60 = 0.0;
        }
        else {
            final double n61 = n59 * n59;
            n60 = n61 * n61 * dot(SimplexNoise.grad4[n52], n31, n32, n33, n34);
        }
        final double n62 = 0.6 - n35 * n35 - n36 * n36 - n37 * n37 - n38 * n38;
        double n63;
        if (n62 < 0.0) {
            n63 = 0.0;
        }
        else {
            final double n64 = n62 * n62;
            n63 = n64 * n64 * dot(SimplexNoise.grad4[n53], n35, n36, n37, n38);
        }
        final double n65 = 0.6 - n39 * n39 - n40 * n40 - n41 * n41 - n42 * n42;
        double n66;
        if (n65 < 0.0) {
            n66 = 0.0;
        }
        else {
            final double n67 = n65 * n65;
            n66 = n67 * n67 * dot(SimplexNoise.grad4[n54], n39, n40, n41, n42);
        }
        final double n68 = 0.6 - n43 * n43 - n44 * n44 - n45 * n45 - n46 * n46;
        double n69;
        if (n68 < 0.0) {
            n69 = 0.0;
        }
        else {
            final double n70 = n68 * n68;
            n69 = n70 * n70 * dot(SimplexNoise.grad4[n55], n43, n44, n45, n46);
        }
        return 27.0 * (n57 + n60 + n63 + n66 + n69);
    }
    
    static {
        SimplexNoise.grad3 = new Grad[] { new Grad(1.0, 1.0, 0.0), new Grad(-1.0, 1.0, 0.0), new Grad(1.0, -1.0, 0.0), new Grad(-1.0, -1.0, 0.0), new Grad(1.0, 0.0, 1.0), new Grad(-1.0, 0.0, 1.0), new Grad(1.0, 0.0, -1.0), new Grad(-1.0, 0.0, -1.0), new Grad(0.0, 1.0, 1.0), new Grad(0.0, -1.0, 1.0), new Grad(0.0, 1.0, -1.0), new Grad(0.0, -1.0, -1.0) };
        SimplexNoise.grad4 = new Grad[] { new Grad(0.0, 1.0, 1.0, 1.0), new Grad(0.0, 1.0, 1.0, -1.0), new Grad(0.0, 1.0, -1.0, 1.0), new Grad(0.0, 1.0, -1.0, -1.0), new Grad(0.0, -1.0, 1.0, 1.0), new Grad(0.0, -1.0, 1.0, -1.0), new Grad(0.0, -1.0, -1.0, 1.0), new Grad(0.0, -1.0, -1.0, -1.0), new Grad(1.0, 0.0, 1.0, 1.0), new Grad(1.0, 0.0, 1.0, -1.0), new Grad(1.0, 0.0, -1.0, 1.0), new Grad(1.0, 0.0, -1.0, -1.0), new Grad(-1.0, 0.0, 1.0, 1.0), new Grad(-1.0, 0.0, 1.0, -1.0), new Grad(-1.0, 0.0, -1.0, 1.0), new Grad(-1.0, 0.0, -1.0, -1.0), new Grad(1.0, 1.0, 0.0, 1.0), new Grad(1.0, 1.0, 0.0, -1.0), new Grad(1.0, -1.0, 0.0, 1.0), new Grad(1.0, -1.0, 0.0, -1.0), new Grad(-1.0, 1.0, 0.0, 1.0), new Grad(-1.0, 1.0, 0.0, -1.0), new Grad(-1.0, -1.0, 0.0, 1.0), new Grad(-1.0, -1.0, 0.0, -1.0), new Grad(1.0, 1.0, 1.0, 0.0), new Grad(1.0, 1.0, -1.0, 0.0), new Grad(1.0, -1.0, 1.0, 0.0), new Grad(1.0, -1.0, -1.0, 0.0), new Grad(-1.0, 1.0, 1.0, 0.0), new Grad(-1.0, 1.0, -1.0, 0.0), new Grad(-1.0, -1.0, 1.0, 0.0), new Grad(-1.0, -1.0, -1.0, 0.0) };
        SimplexNoise.p = new short[] { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180 };
        SimplexNoise.perm = new short[512];
        SimplexNoise.permMod12 = new short[512];
        for (int i = 0; i < 512; ++i) {
            SimplexNoise.perm[i] = SimplexNoise.p[i & 0xFF];
            SimplexNoise.permMod12[i] = (short)(SimplexNoise.perm[i] % 12);
        }
        F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
        F4 = (Math.sqrt(5.0) - 1.0) / 4.0;
        G4 = (5.0 - Math.sqrt(5.0)) / 20.0;
    }
    
    private static class Grad
    {
        double x;
        double y;
        double z;
        double w;
        
        Grad(final double x, final double y, final double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        Grad(final double x, final double y, final double z, final double w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
    }
}
