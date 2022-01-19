// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.Iterator;
import zombie.debug.LineDrawer;
import org.lwjgl.util.vector.Vector2f;
import zombie.characters.IsoPlayer;
import zombie.core.physics.WorldSimulation;
import org.joml.Vector3fc;
import zombie.core.physics.CarController;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class CircleLineIntersect
{
    public static Collideresult checkforcecirclescollidetime(final List<ForceCircle> list, final ArrayList<StaticLine> list2, final double[] array, final boolean[] array2, final boolean b) {
        final PointVector[] array3 = new PointVector[list.size()];
        final double[] array4 = new double[list.size()];
        final Collideclassindex[] array5 = new Collideclassindex[list.size()];
        final double[] array6 = new double[list.size()];
        for (int i = list.size() - 1; i >= 0; --i) {
            array4[i] = -1.0;
            array5[i] = new Collideclassindex();
            array3[i] = list.get(i);
            array6[i] = 1.0;
        }
        for (int j = Math.min(list.size(), array.length) - 1; j >= 0; --j) {
            if (b || array2[j]) {
                final ForceCircle forceCircle = list.get(j);
                for (int k = list2.size() - 1; k >= 0; --k) {
                    final StaticLine staticLine = list2.get(k);
                    final Point closestpointonline = VectorMath.closestpointonline(staticLine.getX1(), staticLine.getY1(), staticLine.getX2(), staticLine.getY2(), forceCircle.getX(), forceCircle.getY());
                    final double distanceSq = Point.distanceSq(closestpointonline.getX(), closestpointonline.getY(), forceCircle.getX(), forceCircle.getY());
                    if (distanceSq < forceCircle.getRadiusSq()) {
                        if (distanceSq == 0.0) {
                            final Point midpoint = Point.midpoint(staticLine.getP1(), staticLine.getP2());
                            final double distance = staticLine.getP1().distance(staticLine.getP2());
                            final double distanceSq2 = forceCircle.distanceSq(midpoint);
                            if (distanceSq2 < Math.pow(forceCircle.getRadius() + distance / 2.0, 2.0)) {
                                double x;
                                double y;
                                if (distanceSq2 != 0.0) {
                                    final double distance2 = forceCircle.distance(midpoint);
                                    final double n = (forceCircle.getX() - midpoint.getX()) / distance2;
                                    final double n2 = (forceCircle.getY() - midpoint.getY()) / distance2;
                                    x = midpoint.getX() + (forceCircle.getRadius() + distance / 2.0) * n;
                                    y = midpoint.getY() + (forceCircle.getRadius() + distance / 2.0) * n2;
                                }
                                else {
                                    x = forceCircle.getX();
                                    y = forceCircle.getY();
                                }
                                if (array4[j] == -1.0) {
                                    array3[j] = new PointVector(x, y);
                                }
                                else {
                                    array3[j].setPoint(x, y);
                                }
                                if (array4[j] == 0.0) {
                                    array5[j].addCollided(staticLine, k, forceCircle.getVector());
                                }
                                else {
                                    array5[j].setCollided(staticLine, k, forceCircle.getVector());
                                }
                                array4[j] = 0.0;
                                continue;
                            }
                            if (distanceSq2 == Math.pow(forceCircle.getRadius() + distance / 2.0, 2.0) && forceCircle.getLength() == 0.0) {
                                continue;
                            }
                        }
                        else {
                            if (Math.min(staticLine.getX1(), staticLine.getX2()) <= closestpointonline.getX() && closestpointonline.getX() <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= closestpointonline.getY() && closestpointonline.getY() <= Math.max(staticLine.getY1(), staticLine.getY2())) {
                                final double sqrt = Math.sqrt(distanceSq);
                                final double n3 = (forceCircle.getX() - closestpointonline.getX()) / sqrt;
                                final double n4 = (forceCircle.getY() - closestpointonline.getY()) / sqrt;
                                final double n5 = closestpointonline.getX() + forceCircle.getRadius() * n3;
                                final double n6 = closestpointonline.getY() + forceCircle.getRadius() * n4;
                                if (array4[j] == -1.0) {
                                    array3[j] = new PointVector(n5, n6);
                                }
                                else {
                                    array3[j].setPoint(n5, n6);
                                }
                                if (array4[j] == 0.0) {
                                    array5[j].addCollided(staticLine, k, forceCircle.getVector());
                                }
                                else {
                                    array5[j].setCollided(staticLine, k, forceCircle.getVector());
                                }
                                array4[j] = 0.0;
                                continue;
                            }
                            if (Point.distanceSq(forceCircle.getX(), forceCircle.getY(), staticLine.getX1(), staticLine.getY1()) < forceCircle.getRadiusSq()) {
                                final double distance3 = Point.distance(forceCircle.getX(), forceCircle.getY(), staticLine.getX1(), staticLine.getY1());
                                final double n7 = (forceCircle.getX() - staticLine.getX1()) / distance3;
                                final double n8 = (forceCircle.getY() - staticLine.getY1()) / distance3;
                                final double n9 = staticLine.getX1() + forceCircle.getRadius() * n7;
                                final double n10 = staticLine.getY1() + forceCircle.getRadius() * n8;
                                if (array4[j] == -1.0) {
                                    array3[j] = new PointVector(n9, n10);
                                }
                                else {
                                    array3[j].setPoint(n9, n10);
                                }
                                if (array4[j] == 0.0) {
                                    array5[j].addCollided(staticLine, k, forceCircle.getVector());
                                }
                                else {
                                    array5[j].setCollided(staticLine, k, forceCircle.getVector());
                                }
                                array4[j] = 0.0;
                                continue;
                            }
                            if (Point.distanceSq(forceCircle.getX(), forceCircle.getY(), staticLine.getX2(), staticLine.getY2()) < forceCircle.getRadiusSq()) {
                                final double distance4 = Point.distance(forceCircle.getX(), forceCircle.getY(), staticLine.getX2(), staticLine.getY2());
                                final double n11 = (forceCircle.getX() - staticLine.getX2()) / distance4;
                                final double n12 = (forceCircle.getY() - staticLine.getY2()) / distance4;
                                final double n13 = staticLine.getX2() + forceCircle.getRadius() * n11;
                                final double n14 = staticLine.getY2() + forceCircle.getRadius() * n12;
                                if (array4[j] == -1.0) {
                                    array3[j] = new PointVector(n13, n14);
                                }
                                else {
                                    array3[j].setPoint(n13, n14);
                                }
                                if (array4[j] == 0.0) {
                                    array5[j].addCollided(staticLine, k, forceCircle.getVector());
                                }
                                else {
                                    array5[j].setCollided(staticLine, k, forceCircle.getVector());
                                }
                                array4[j] = 0.0;
                                continue;
                            }
                        }
                    }
                    final double n15 = staticLine.getY2() - staticLine.getY1();
                    final double n16 = staticLine.getX1() - staticLine.getX2();
                    final double n17 = (staticLine.getY2() - staticLine.getY1()) * staticLine.getX1() + (staticLine.getX1() - staticLine.getX2()) * staticLine.getY1();
                    final double getvy = forceCircle.getvy();
                    final double n18 = -forceCircle.getvx();
                    final double n19 = forceCircle.getvy() * forceCircle.getX() + -forceCircle.getvx() * forceCircle.getY();
                    final double n20 = n15 * n18 - getvy * n16;
                    double n21 = 0.0;
                    double n22 = 0.0;
                    if (n20 != 0.0) {
                        n21 = (n18 * n17 - n16 * n19) / n20;
                        n22 = (n15 * n19 - getvy * n17) / n20;
                    }
                    final Point closestpointonline2 = VectorMath.closestpointonline(staticLine.getX1(), staticLine.getY1(), staticLine.getX2(), staticLine.getY2(), forceCircle.getX2(), forceCircle.getY2());
                    final Point closestpointonline3 = VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX1(), staticLine.getY1());
                    final Point closestpointonline4 = VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX2(), staticLine.getY2());
                    if ((Point.distanceSq(closestpointonline2.getX(), closestpointonline2.getY(), forceCircle.getX2(), forceCircle.getY2()) < forceCircle.getRadiusSq() && Math.min(staticLine.getX1(), staticLine.getX2()) <= closestpointonline2.getX() && closestpointonline2.getX() <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= closestpointonline2.getY() && closestpointonline2.getY() <= Math.max(staticLine.getY1(), staticLine.getY2())) || (Point.distanceSq(closestpointonline3.getX(), closestpointonline3.getY(), staticLine.getX1(), staticLine.getY1()) < forceCircle.getRadiusSq() && Math.min(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) <= closestpointonline3.getX() && closestpointonline3.getX() <= Math.max(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) && Math.min(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) <= closestpointonline3.getY() && closestpointonline3.getY() <= Math.max(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy())) || (Point.distanceSq(closestpointonline4.getX(), closestpointonline4.getY(), staticLine.getX2(), staticLine.getY2()) < forceCircle.getRadiusSq() && Math.min(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) <= closestpointonline4.getX() && closestpointonline4.getX() <= Math.max(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) && Math.min(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) <= closestpointonline4.getY() && closestpointonline4.getY() <= Math.max(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy())) || (Math.min(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) <= n21 && n21 <= Math.max(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) && Math.min(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) <= n22 && n22 <= Math.max(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) && Math.min(staticLine.getX1(), staticLine.getX2()) <= n21 && n21 <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= n22 && n22 <= Math.max(staticLine.getY1(), staticLine.getY2())) || Point.distanceSq(staticLine.getX1(), staticLine.getY1(), forceCircle.getX2(), forceCircle.getY2()) <= forceCircle.getRadiusSq() || Point.distanceSq(staticLine.getX2(), staticLine.getY2(), forceCircle.getX2(), forceCircle.getY2()) <= forceCircle.getRadiusSq()) {
                        final double n23 = -n16;
                        final double n24 = n15;
                        final double n25 = n23 * forceCircle.getX() + n24 * forceCircle.getY();
                        final double n26 = n15 * n24 - n23 * n16;
                        if (n26 != 0.0) {
                            final double n27 = (n24 * n17 - n16 * n25) / n26;
                            final double n28 = (n15 * n25 - n23 * n17) / n26;
                            final double n29 = Point.distance(n21, n22, forceCircle.getX(), forceCircle.getY()) * forceCircle.getRadius() / Point.distance(n27, n28, forceCircle.getX(), forceCircle.getY());
                            final double n30 = n21 + -n29 * forceCircle.getnormvx();
                            final double n31 = n22 + -n29 * forceCircle.getnormvy();
                            final double n32 = n23 * n30 + n24 * n31;
                            final double n33 = (n24 * n17 - n16 * n32) / n26;
                            final double n34 = (n15 * n32 - n23 * n17) / n26;
                            if (Math.min(staticLine.getX1(), staticLine.getX2()) <= n33 && n33 <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= n34 && n34 <= Math.max(staticLine.getY1(), staticLine.getY2())) {
                                final double n35 = n27;
                                final double n36 = n28;
                                final double n37 = n27 + (n30 - n33);
                                final double n38 = n28 + (n31 - n34);
                                final double n39 = Math.pow(n30 - forceCircle.getX(), 2.0) + Math.pow(n31 - forceCircle.getY(), 2.0);
                                if (n39 <= array4[j] || array4[j] < 0.0) {
                                    if (!array5[j].collided() || array4[j] != n39) {
                                        for (int l = 0; l < array5[j].size(); ++l) {
                                            if (array5[j].collided() && array5[j].getColliders().get(l).getCollideobj() instanceof ForceCircle && array4[j] > n39) {
                                                array3[array5[j].getColliders().get(l).getCollidewith()] = new PointVector(list.get(array5[j].getColliders().get(l).getCollidewith()));
                                                array4[array5[j].getColliders().get(l).getCollidewith()] = -1.0;
                                            }
                                        }
                                    }
                                    RectVector rectVector2;
                                    if (Point.distanceSq(n37, n38, forceCircle.getX(), forceCircle.getY()) < 1.0E-8) {
                                        final Point closestpointonline5 = VectorMath.closestpointonline(staticLine.getX1() + (n30 - n35), staticLine.getY1() + (n31 - n36), staticLine.getX2() + (n30 - n35), staticLine.getY2() + (n31 - n36), forceCircle.getX2(), forceCircle.getY2());
                                        final RectVector rectVector = (RectVector)new RectVector(closestpointonline5.getX() + (closestpointonline5.getX() - forceCircle.getX2()) - forceCircle.getX(), closestpointonline5.getY() + (closestpointonline5.getY() - forceCircle.getY2()) - forceCircle.getY()).getUnitVector();
                                        rectVector2 = new RectVector(rectVector.getvx() * forceCircle.getLength(), rectVector.getvy() * forceCircle.getLength());
                                    }
                                    else {
                                        final RectVector rectVector3 = (RectVector)new RectVector(forceCircle.getX() - 2.0 * (n37 - n30) - n30, forceCircle.getY() - 2.0 * (n38 - n31) - n31).getUnitVector();
                                        rectVector2 = new RectVector(rectVector3.getvx() * forceCircle.getLength(), rectVector3.getvy() * forceCircle.getLength());
                                    }
                                    final RectVector rectVector4 = (RectVector)rectVector2.getUnitVector();
                                    final RectVector rectVector5 = new RectVector(rectVector4.getvx() * forceCircle.getLength(), rectVector4.getvy() * forceCircle.getLength());
                                    if (array4[j] == -1.0) {
                                        array3[j] = new PointVector(n30, n31);
                                    }
                                    else {
                                        array3[j].setPoint(n30, n31);
                                    }
                                    if (array4[j] == n39) {
                                        array5[j].addCollided(staticLine, k, rectVector5);
                                    }
                                    else {
                                        array5[j].setCollided(staticLine, k, rectVector5);
                                    }
                                    array4[j] = n39;
                                }
                            }
                            else {
                                final double n40 = forceCircle.getRadius() * forceCircle.getRadius();
                                final Point closestpointonline6 = VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX1(), staticLine.getY1());
                                final double distanceSq3 = Point.distanceSq(closestpointonline6.getX(), closestpointonline6.getY(), staticLine.getX1(), staticLine.getY1());
                                final double distanceSq4 = Point.distanceSq(closestpointonline6.getX(), closestpointonline6.getY(), forceCircle.getX(), forceCircle.getY());
                                final Point closestpointonline7 = VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX2(), staticLine.getY2());
                                final double distanceSq5 = Point.distanceSq(closestpointonline7.getX(), closestpointonline7.getY(), staticLine.getX2(), staticLine.getY2());
                                final double distanceSq6 = Point.distanceSq(closestpointonline7.getX(), closestpointonline7.getY(), forceCircle.getX(), forceCircle.getY());
                                double n41;
                                double n42;
                                double n43;
                                double n44;
                                if (distanceSq4 < distanceSq6 && distanceSq3 <= distanceSq5) {
                                    final double sqrt2 = Math.sqrt(Math.abs(n40 - distanceSq3));
                                    n41 = closestpointonline6.getX() - sqrt2 * forceCircle.getnormvx();
                                    n42 = closestpointonline6.getY() - sqrt2 * forceCircle.getnormvy();
                                    n43 = staticLine.getX1();
                                    n44 = staticLine.getY1();
                                }
                                else if (distanceSq4 > distanceSq6 && distanceSq3 >= distanceSq5) {
                                    final double sqrt3 = Math.sqrt(Math.abs(n40 - distanceSq5));
                                    n41 = closestpointonline7.getX() - sqrt3 * forceCircle.getnormvx();
                                    n42 = closestpointonline7.getY() - sqrt3 * forceCircle.getnormvy();
                                    n43 = staticLine.getX2();
                                    n44 = staticLine.getY2();
                                }
                                else if (distanceSq3 < distanceSq5) {
                                    if (distanceSq4 < distanceSq6 || Point.distanceSq(n33, n34, staticLine.getX1(), staticLine.getY1()) <= n40) {
                                        final double sqrt4 = Math.sqrt(Math.abs(n40 - distanceSq3));
                                        n41 = closestpointonline6.getX() - sqrt4 * forceCircle.getnormvx();
                                        n42 = closestpointonline6.getY() - sqrt4 * forceCircle.getnormvy();
                                        n43 = staticLine.getX1();
                                        n44 = staticLine.getY1();
                                    }
                                    else {
                                        final double sqrt5 = Math.sqrt(Math.abs(n40 - distanceSq5));
                                        n41 = closestpointonline7.getX() - sqrt5 * forceCircle.getnormvx();
                                        n42 = closestpointonline7.getY() - sqrt5 * forceCircle.getnormvy();
                                        n43 = staticLine.getX2();
                                        n44 = staticLine.getY2();
                                    }
                                }
                                else if (distanceSq3 > distanceSq5) {
                                    if (distanceSq6 < distanceSq4 || Point.distanceSq(n33, n34, staticLine.getX2(), staticLine.getY2()) <= n40) {
                                        final double sqrt6 = Math.sqrt(Math.abs(n40 - distanceSq5));
                                        n41 = closestpointonline7.getX() - sqrt6 * forceCircle.getnormvx();
                                        n42 = closestpointonline7.getY() - sqrt6 * forceCircle.getnormvy();
                                        n43 = staticLine.getX2();
                                        n44 = staticLine.getY2();
                                    }
                                    else {
                                        final double sqrt7 = Math.sqrt(Math.abs(n40 - distanceSq3));
                                        n41 = closestpointonline6.getX() - sqrt7 * forceCircle.getnormvx();
                                        n42 = closestpointonline6.getY() - sqrt7 * forceCircle.getnormvy();
                                        n43 = staticLine.getX1();
                                        n44 = staticLine.getY1();
                                    }
                                }
                                else if ((Math.min(forceCircle.getX(), forceCircle.getX2()) > closestpointonline7.getX() || closestpointonline7.getX() > Math.max(forceCircle.getX(), forceCircle.getX2()) || Math.min(forceCircle.getY(), forceCircle.getY2()) > closestpointonline7.getY() || closestpointonline7.getY() > Math.max(forceCircle.getY(), forceCircle.getY2())) && Point.distanceSq(closestpointonline7.getX(), closestpointonline7.getY(), forceCircle.getX2(), forceCircle.getY2()) > forceCircle.getRadiusSq()) {
                                    final double sqrt8 = Math.sqrt(Math.abs(n40 - distanceSq3));
                                    n41 = closestpointonline6.getX() - sqrt8 * forceCircle.getnormvx();
                                    n42 = closestpointonline6.getY() - sqrt8 * forceCircle.getnormvy();
                                    n43 = staticLine.getX1();
                                    n44 = staticLine.getY1();
                                }
                                else if ((Math.min(forceCircle.getX(), forceCircle.getX2()) > closestpointonline6.getX() || closestpointonline6.getX() > Math.max(forceCircle.getX(), forceCircle.getX2()) || Math.min(forceCircle.getY(), forceCircle.getY2()) > closestpointonline6.getY() || closestpointonline6.getY() > Math.max(forceCircle.getY(), forceCircle.getY2())) && Point.distanceSq(closestpointonline7.getX(), closestpointonline7.getY(), forceCircle.getX2(), forceCircle.getY2()) > forceCircle.getRadiusSq()) {
                                    final double sqrt9 = Math.sqrt(Math.abs(n40 - distanceSq5));
                                    n41 = closestpointonline7.getX() - sqrt9 * forceCircle.getnormvx();
                                    n42 = closestpointonline7.getY() - sqrt9 * forceCircle.getnormvy();
                                    n43 = staticLine.getX2();
                                    n44 = staticLine.getY2();
                                }
                                else if (distanceSq4 < distanceSq6) {
                                    final double sqrt10 = Math.sqrt(Math.abs(n40 - distanceSq3));
                                    n41 = closestpointonline6.getX() - sqrt10 * forceCircle.getnormvx();
                                    n42 = closestpointonline6.getY() - sqrt10 * forceCircle.getnormvy();
                                    n43 = staticLine.getX1();
                                    n44 = staticLine.getY1();
                                }
                                else {
                                    final double sqrt11 = Math.sqrt(Math.abs(n40 - distanceSq5));
                                    n41 = closestpointonline7.getX() - sqrt11 * forceCircle.getnormvx();
                                    n42 = closestpointonline7.getY() - sqrt11 * forceCircle.getnormvy();
                                    n43 = staticLine.getX2();
                                    n44 = staticLine.getY2();
                                }
                                final double n45 = Math.pow(n41 - forceCircle.getX(), 2.0) + Math.pow(n42 - forceCircle.getY(), 2.0);
                                if (n45 <= array4[j] || array4[j] < 0.0) {
                                    if (!array5[j].collided() || array4[j] != n45) {
                                        for (int n46 = 0; n46 < array5[j].size(); ++n46) {
                                            if (array5[j].collided() && array5[j].getColliders().get(n46).getCollideobj() instanceof ForceCircle && array4[j] > n45) {
                                                array3[array5[j].getColliders().get(n46).getCollidewith()] = new PointVector(list.get(array5[j].getColliders().get(n46).getCollidewith()));
                                                array4[array5[j].getColliders().get(n46).getCollidewith()] = -1.0;
                                            }
                                        }
                                    }
                                    final RectVector rectVector6 = (RectVector)new RectVector(n41 - (n43 - n41) - n41, n42 - (n44 - n42) - n42).getUnitVector();
                                    final RectVector rectVector7 = new RectVector(rectVector6.getvx() * forceCircle.getLength(), rectVector6.getvy() * forceCircle.getLength());
                                    if (array4[j] == -1.0) {
                                        array3[j] = new PointVector(n41, n42);
                                    }
                                    else {
                                        array3[j].setPoint(n41, n42);
                                    }
                                    if (array4[j] == n45) {
                                        array5[j].addCollided(staticLine, k, rectVector7);
                                    }
                                    else {
                                        array5[j].setCollided(staticLine, k, rectVector7);
                                    }
                                    array4[j] = n45;
                                }
                            }
                        }
                    }
                }
            }
        }
        final ArrayList<Integer> list3 = new ArrayList<Integer>((int)Math.ceil(list.size() / 10));
        for (int m = 0; m < array3.length; ++m) {
            if (array5[m].collided()) {
                if (list.get(m).isFrozen()) {
                    array3[m].setRect(0.0, 0.0);
                }
                else {
                    double n47 = 0.0;
                    double n48 = 0.0;
                    final boolean b2 = false;
                    double n49 = 0.0;
                    for (int index = 0; index < array5[m].size(); ++index) {
                        final Object collideobj = array5[m].getColliders().get(index).getCollideobj();
                        n49 += list.get(m).getRestitution(array5[m].getColliders().get(index).getCollideobj());
                        if (collideobj instanceof StaticLine && array5[m].getColliders().get(index).getCollideforce() != null) {
                            n47 += array5[m].getColliders().get(index).getCollideforce().getvx();
                            n48 += array5[m].getColliders().get(index).getCollideforce().getvy();
                        }
                    }
                    final double n50 = n49 / array5[m].getColliders().size();
                    if (array4[m] == -1.0) {
                        array3[m] = new PointVector(array3[m].getX(), array3[m].getY());
                    }
                    array3[m].setRect(n47 * n50, n48 * n50);
                    list3.add(m);
                    if (array6[m] == 1.0 && list.get(m).getLength() != 0.0 && !b2) {
                        if (array4[m] == 0.0) {
                            array6[m] = 0.0;
                        }
                        else if (array4[m] > 0.0) {
                            array6[m] = Math.sqrt(array4[m]) / list.get(m).getLength();
                        }
                        else {
                            array6[m] = list.get(m).distance(array3[m]) / list.get(m).getLength();
                        }
                    }
                    final int n51 = m;
                    array[n51] += array6[m] * (1.0 - array[m]);
                    if (!array3[m].equals(list.get(m))) {
                        array2[m] = true;
                    }
                }
            }
        }
        return new Collideresult(array3, array5, list3, array, array6, array2);
    }
    
    public static Collideresult checkforcecirclescollide(final List<ForceCircle> list, final ArrayList<StaticLine> list2, final double[] array, final boolean[] array2, final boolean b) {
        final Collideresult checkforcecirclescollidetime = checkforcecirclescollidetime(list, list2, array, array2, b);
        final ArrayList list3 = new ArrayList();
        for (int i = checkforcecirclescollidetime.resultants.length - 1; i >= 0; --i) {
            if (checkforcecirclescollidetime.collideinto[i].collided()) {
                list.get(i).setPointVector(checkforcecirclescollidetime.resultants[i]);
            }
        }
        return checkforcecirclescollidetime;
    }
    
    public static Collideresult checkforcecirclescollide(final List<ForceCircle> list, final ArrayList<StaticLine> list2) {
        final double[] array = new double[list.size()];
        final boolean[] array2 = new boolean[list.size()];
        for (int i = list.size() - 1; i >= 0; --i) {
            array[i] = 1.0;
        }
        return checkforcecirclescollide(list, list2, array, array2, true);
    }
    
    public static boolean TEST(final Vector3f vector3f, final float n, final float n2, final float n3, final float n4, final CarController carController) {
        final Vector3f vector3f2 = new Vector3f();
        vector3f.cross((Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f), vector3f2);
        vector3f.x *= n4;
        vector3f.z *= n4;
        final Vector3f vector3f3 = vector3f2;
        vector3f3.x *= n3;
        final Vector3f vector3f4 = vector3f2;
        vector3f4.z *= n3;
        final float n5 = n + vector3f.x;
        final float n6 = n2 + vector3f.z;
        final float n7 = n - vector3f.x;
        final float n8 = n2 - vector3f.z;
        final float n9 = n5 - vector3f2.x / 2.0f;
        final float n10 = n5 + vector3f2.x / 2.0f;
        final float n11 = n7 - vector3f2.x / 2.0f;
        final float n12 = n7 + vector3f2.x / 2.0f;
        final float n13 = n8 - vector3f2.z / 2.0f;
        final float n14 = n8 + vector3f2.z / 2.0f;
        final float n15 = n6 - vector3f2.z / 2.0f;
        final float n16 = n6 + vector3f2.z / 2.0f;
        final float n17 = n9 + WorldSimulation.instance.offsetX;
        final float n18 = n15 + WorldSimulation.instance.offsetY;
        final float n19 = n10 + WorldSimulation.instance.offsetX;
        final float n20 = n16 + WorldSimulation.instance.offsetY;
        final float n21 = n11 + WorldSimulation.instance.offsetX;
        final float n22 = n13 + WorldSimulation.instance.offsetY;
        final float n23 = n12 + WorldSimulation.instance.offsetX;
        final float n24 = n14 + WorldSimulation.instance.offsetY;
        final ArrayList<StaticLine> list = new ArrayList<StaticLine>();
        final StaticLine staticLine;
        list.add(staticLine = new StaticLine(n17, n18, n19, n20));
        final StaticLine staticLine2;
        list.add(staticLine2 = new StaticLine(n19, n20, n23, n24));
        final StaticLine staticLine3;
        list.add(staticLine3 = new StaticLine(n23, n24, n21, n22));
        final StaticLine staticLine4;
        list.add(staticLine4 = new StaticLine(n21, n22, n17, n18));
        final IsoPlayer instance = IsoPlayer.getInstance();
        final ArrayList<ForceCircle> list2 = new ArrayList<ForceCircle>();
        final ForceCircle e = new ForceCircle(instance.x, instance.y, instance.nx - instance.x, instance.ny - instance.y, 0.295);
        if (carController != null) {
            carController.drawCircle((float)e.getX2(), (float)e.getY2(), 0.3f);
        }
        list2.add(e);
        final Collideresult checkforcecirclescollide = checkforcecirclescollide(list2, list);
        if (carController != null) {
            carController.drawCircle((float)e.getX(), (float)e.getY(), (float)e.getRadius());
        }
        if (checkforcecirclescollide.collidelist.isEmpty()) {
            return false;
        }
        final ForceCircle forceCircle = e;
        int length = checkforcecirclescollide.collideinto.length;
        final Vector2f vector2f = new Vector2f(instance.nx - instance.x, instance.ny - instance.y);
        if (vector2f.length() > 0.0f) {
            vector2f.normalise();
        }
        for (int i = 0; i < checkforcecirclescollide.collideinto.length; ++i) {
            final StaticLine staticLine5 = (StaticLine)checkforcecirclescollide.collideinto[i].getColliders().get(0).getCollideobj();
            if (staticLine5 == staticLine4 || staticLine5 == staticLine2) {
                LineDrawer.addLine(n5 + WorldSimulation.instance.offsetX, n6 + WorldSimulation.instance.offsetY, 0.0f, n7 + WorldSimulation.instance.offsetX, n8 + WorldSimulation.instance.offsetY, 0.0f, 1.0f, 1.0f, 1.0f, null, true);
                final Point closestpointonline = VectorMath.closestpointonline(n5 + WorldSimulation.instance.offsetX, n6 + WorldSimulation.instance.offsetY, n7 + WorldSimulation.instance.offsetX, n8 + WorldSimulation.instance.offsetY, e.getX(), e.getY());
                vector3f.set((float)(closestpointonline.x - instance.x), (float)(closestpointonline.y - instance.y), 0.0f);
                vector3f.normalize();
                if (VectorMath.dotproduct(vector2f.x, vector2f.y, vector3f.x, vector3f.y) < 0.0) {
                    --length;
                }
            }
            if (staticLine5 == staticLine || staticLine5 == staticLine3) {
                LineDrawer.addLine(n - vector3f2.x / 2.0f + WorldSimulation.instance.offsetX, n2 - vector3f2.z / 2.0f + WorldSimulation.instance.offsetY, 0.0f, n + vector3f2.x / 2.0f + WorldSimulation.instance.offsetX, n2 + vector3f2.z / 2.0f + WorldSimulation.instance.offsetY, 0.0f, 1.0f, 1.0f, 1.0f, null, true);
                final Point closestpointonline2 = VectorMath.closestpointonline(n - vector3f2.x / 2.0f + WorldSimulation.instance.offsetX, n2 - vector3f2.z / 2.0f + WorldSimulation.instance.offsetY, n + vector3f2.x / 2.0f + WorldSimulation.instance.offsetX, n2 + vector3f2.z / 2.0f + WorldSimulation.instance.offsetY, e.getX(), e.getY());
                vector3f.set((float)(closestpointonline2.x - instance.x), (float)(closestpointonline2.y - instance.y), 0.0f);
                vector3f.normalize();
                if (VectorMath.dotproduct(vector2f.x, vector2f.y, vector3f.x, vector3f.y) < 0.0) {
                    --length;
                }
            }
        }
        if (length == 0) {
            return false;
        }
        vector3f.set((float)forceCircle.getX(), (float)forceCircle.getY(), 0.0f);
        return true;
    }
    
    static class Point
    {
        double x;
        double y;
        
        public static final Point midpoint(final double n, final double n2, final double n3, final double n4) {
            return new Point((n + n3) / 2.0, (n2 + n4) / 2.0);
        }
        
        public static final Point midpoint(final Point point, final Point point2) {
            return midpoint(point.getX(), point.getY(), point2.getX(), point2.getY());
        }
        
        public Point(double x, double y) {
            if (Double.isNaN(x) || Double.isInfinite(x)) {
                x = 0.0;
            }
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                y = 0.0;
            }
            this.x = x;
            this.y = y;
        }
        
        public double getX() {
            return this.x;
        }
        
        public double getY() {
            return this.y;
        }
        
        public void setPoint(final double x, final double y) {
            this.x = x;
            this.y = y;
        }
        
        public static double distanceSq(double n, double n2, final double n3, final double n4) {
            n -= n3;
            n2 -= n4;
            return n * n + n2 * n2;
        }
        
        public static double distance(double n, double n2, final double n3, final double n4) {
            n -= n3;
            n2 -= n4;
            return Math.sqrt(n * n + n2 * n2);
        }
        
        public double distanceSq(double n, double n2) {
            n -= this.getX();
            n2 -= this.getY();
            return n * n + n2 * n2;
        }
        
        public double distanceSq(final Point point) {
            final double n = point.getX() - this.getX();
            final double n2 = point.getY() - this.getY();
            return n * n + n2 * n2;
        }
        
        public double distance(final Point point) {
            final double n = point.getX() - this.getX();
            final double n2 = point.getY() - this.getY();
            return Math.sqrt(n * n + n2 * n2);
        }
    }
    
    static class PointVector extends Point implements Vector
    {
        protected double vx;
        protected double vy;
        
        public PointVector(final double n, final double n2) {
            this(n, n2, 0.0, 0.0);
        }
        
        public PointVector(final double n, final double n2, final double vx, final double vy) {
            super(n, n2);
            this.vx = 0.0;
            this.vy = 0.0;
            this.vx = vx;
            this.vy = vy;
        }
        
        public PointVector(final PointVector pointVector) {
            this(pointVector.getX(), pointVector.getY(), pointVector.getvx(), pointVector.getvy());
        }
        
        @Override
        public double getLength() {
            return VectorMath.length(this.vx, this.vy);
        }
        
        public Vector getVector() {
            return new RectVector(this.vx, this.vy);
        }
        
        @Override
        public double getvx() {
            return this.vx;
        }
        
        @Override
        public double getvy() {
            return this.vy;
        }
        
        public double getX1() {
            return this.x;
        }
        
        public double getX2() {
            return this.x + this.vx;
        }
        
        public double getY1() {
            return this.y;
        }
        
        public double getY2() {
            return this.y + this.vy;
        }
        
        public void setRect(final double vx, final double vy) {
            this.vx = vx;
            this.vy = vy;
        }
    }
    
    static class RectVector implements Vector
    {
        private double vx;
        private double vy;
        
        public RectVector(final double vx, final double vy) {
            this.vx = vx;
            this.vy = vy;
        }
        
        public RectVector(final Vector vector) {
            this.setVector(vector);
        }
        
        @Override
        public double getLength() {
            return Math.sqrt(Math.abs(this.getvx() * this.getvx() + this.getvy() * this.getvy()));
        }
        
        public Vector getUnitVector() {
            final double length = this.getLength();
            return new RectVector(this.getvx() / length, this.getvy() / length);
        }
        
        @Override
        public double getvx() {
            return this.vx;
        }
        
        @Override
        public double getvy() {
            return this.vy;
        }
        
        public void setVector(final Vector vector) {
            this.vx = vector.getvx();
            this.vy = vector.getvy();
        }
    }
    
    static class Force extends PointVector
    {
        protected double length;
        protected double mass;
        
        public Force(final double n, final double n2, final double n3, final double n4) {
            super(n, n2, n3, n4);
            this.length = VectorMath.length(n3, n4);
        }
        
        @Override
        public double getLength() {
            return this.length;
        }
        
        public double getnormvx() {
            if (this.length > 0.0) {
                return this.vx / this.length;
            }
            return 0.0;
        }
        
        public double getnormvy() {
            if (this.length > 0.0) {
                return this.vy / this.length;
            }
            return 0.0;
        }
        
        public double getRestitution(final Object o) {
            return 1.0;
        }
        
        public void setPointVector(final PointVector pointVector) {
            this.x = pointVector.getX();
            this.y = pointVector.getY();
            if (!this.isFrozen() && (this.vx != pointVector.getvx() || this.vy != pointVector.getvy())) {
                this.vx = pointVector.getvx();
                this.vy = pointVector.getvy();
                this.length = VectorMath.length(this.vx, this.vy);
            }
        }
        
        boolean isFrozen() {
            return false;
        }
    }
    
    static class ForceCircle extends Force
    {
        protected double radius;
        protected double radiussq;
        
        public ForceCircle(final double n, final double n2, final double n3, final double n4, final double radius) {
            super(n, n2, n3, n4);
            this.radius = radius;
            this.radiussq = radius * radius;
        }
        
        double getRadius() {
            return this.radius;
        }
        
        double getRadiusSq() {
            return this.radiussq;
        }
    }
    
    static class StaticLine extends Point
    {
        double x2;
        double y2;
        
        public StaticLine(final double n, final double n2, final double x2, final double y2) {
            super(n, n2);
            this.x2 = x2;
            this.y2 = y2;
        }
        
        public Point getP1() {
            return new Point(this.getX1(), this.getY1());
        }
        
        public Point getP2() {
            return new Point(this.getX2(), this.getY2());
        }
        
        public double getX1() {
            return this.x;
        }
        
        public double getX2() {
            return this.x2;
        }
        
        public double getY1() {
            return this.y;
        }
        
        public double getY2() {
            return this.y2;
        }
    }
    
    static class Collider
    {
        private Object collideobj;
        private Integer collideindex;
        private Vector collideforce;
        
        public Collider(final Vector vector, final Integer collideindex) {
            this.collideobj = vector;
            this.collideindex = collideindex;
            this.collideforce = vector;
        }
        
        public Collider(final Object collideobj, final Integer collideindex, final Vector collideforce) {
            this.collideobj = collideobj;
            this.collideindex = collideindex;
            this.collideforce = collideforce;
        }
        
        public Object getCollideobj() {
            return this.collideobj;
        }
        
        public Integer getCollidewith() {
            return this.collideindex;
        }
        
        public Vector getCollideforce() {
            return this.collideforce;
        }
        
        public void setCollideforce(final Vector collideforce) {
            this.collideforce = collideforce;
        }
        
        @Override
        public String toString() {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;)Ljava/lang/String;, this.collideobj.getClass().getSimpleName(), this.collideindex, this.collideforce.toString());
        }
    }
    
    static class Collideclassindex
    {
        private ArrayList<Collider> colliders;
        private int numforcecircles;
        
        public Collideclassindex() {
            this.colliders = new ArrayList<Collider>(1);
            this.numforcecircles = 0;
        }
        
        public Collideclassindex(final Object o, final int i, final Vector vector) {
            (this.colliders = new ArrayList<Collider>(1)).add(new Collider(o, i, vector));
        }
        
        public boolean collided() {
            return this.size() > 0;
        }
        
        public void reset() {
            this.colliders.trimToSize();
            this.colliders.clear();
            this.numforcecircles = 0;
        }
        
        public void setCollided(final Object o, final int i, final Vector vector) {
            if (this.size() > 0) {
                this.reset();
            }
            if (o instanceof ForceCircle && !((ForceCircle)o).isFrozen()) {
                ++this.numforcecircles;
            }
            this.colliders.add(new Collider(o, i, vector));
        }
        
        public void addCollided(final Object o, final int i, final Vector vector) {
            if (o instanceof ForceCircle && !((ForceCircle)o).isFrozen()) {
                ++this.numforcecircles;
            }
            this.colliders.add(new Collider(o, i, vector));
        }
        
        public ArrayList<Collider> getColliders() {
            return this.colliders;
        }
        
        public int getNumforcecircles() {
            return this.numforcecircles;
        }
        
        public Collider contains(final Object obj) {
            for (final Collider collider : this.colliders) {
                if (collider.getCollideobj().equals(obj)) {
                    return collider;
                }
            }
            return null;
        }
        
        public int size() {
            return this.colliders.size();
        }
        
        @Override
        public String toString() {
            String s = "";
            final Iterator<Collider> iterator = this.colliders.iterator();
            while (iterator.hasNext()) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, iterator.next().toString());
            }
            return s;
        }
    }
    
    static class Collideresult
    {
        protected PointVector[] resultants;
        protected ArrayList<Integer> collidelist;
        protected Collideclassindex[] collideinto;
        protected double[] timepassed;
        protected double[] collidetime;
        protected boolean[] modified;
        
        public Collideresult(final PointVector[] resultants, final Collideclassindex[] collideinto, final ArrayList<Integer> collidelist, final double[] timepassed, final double[] collidetime, final boolean[] modified) {
            this.resultants = resultants;
            this.collideinto = collideinto;
            this.collidelist = collidelist;
            this.timepassed = timepassed;
            this.collidetime = collidetime;
            this.modified = modified;
        }
        
        @Override
        public String toString() {
            return this.collidelist.toString();
        }
    }
    
    static class VectorMath
    {
        public static final Vector add(final Vector vector, final Vector vector2) {
            return new RectVector(vector.getvx() + vector2.getvx(), vector.getvy() + vector2.getvy());
        }
        
        public static final Vector subtract(final Vector vector, final Vector vector2) {
            return new RectVector(vector.getvx() - vector2.getvx(), vector.getvy() - vector2.getvy());
        }
        
        public static final double length(final double n, final double n2) {
            return Point.distance(0.0, 0.0, n, n2);
        }
        
        public static final double dotproduct(final Vector vector, final Vector vector2) {
            return dotproduct(vector.getvx(), vector.getvy(), vector2.getvx(), vector2.getvy());
        }
        
        public static final double dotproduct(final double n, final double n2, final double n3, final double n4) {
            return n * n3 + n2 * n4;
        }
        
        public static final double cosproj(final Vector vector, final Vector vector2) {
            return dotproduct(vector, vector2) / (vector.getLength() * vector2.getLength());
        }
        
        public static final double cosproj(final double n, final double n2, final double n3, final double n4) {
            return dotproduct(n, n2, n3, n4) / (length(n, n2) * length(n3, n4));
        }
        
        public static final double anglebetween(final Vector vector, final Vector vector2) {
            return Math.acos(cosproj(vector, vector2));
        }
        
        public static final double anglebetween(final double n, final double n2, final double n3, final double n4) {
            return Math.acos(cosproj(n, n2, n3, n4));
        }
        
        public static final double crossproduct(final Vector vector, final Vector vector2) {
            return crossproduct(vector.getvx(), vector.getvy(), vector2.getvx(), vector2.getvy());
        }
        
        public static final double crossproduct(final double n, final double n2, final double n3, final double n4) {
            return n * n4 - n2 * n3;
        }
        
        public static final double sinproj(final Vector vector, final Vector vector2) {
            return crossproduct(vector, vector2) / (vector.getLength() * vector2.getLength());
        }
        
        public static final double sinproj(final double n, final double n2, final double n3, final double n4) {
            return crossproduct(n, n2, n3, n4) / (length(n, n2) * length(n3, n4));
        }
        
        public static final boolean equaldirection(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double a, final double a2) {
            if (n - n3 != 0.0 || n2 - n4 != 0.0) {
                final double n7 = ((n - n3) * (n - n5) + (n2 - n4) * (n2 - n6)) / (Math.abs(a) * Math.abs(a2));
                return n7 > 0.995 && n7 <= 1.0;
            }
            return true;
        }
        
        public static final boolean equaldirection(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double a, final double a2, final double n7) {
            if (n - n3 != 0.0 || n2 - n4 != 0.0) {
                final double n8 = ((n - n3) * (n - n5) + (n2 - n4) * (n2 - n6)) / (Math.abs(a) * Math.abs(a2));
                return n8 > n7 && n8 <= 1.0;
            }
            return true;
        }
        
        public static final boolean equaldirection(final double n, final double n2, final double n3, final double n4, final double a, final double a2, final double n5) {
            if (n != 0.0 || n2 != 0.0) {
                final double n6 = (n * n3 + n2 * n4) / (Math.abs(a) * Math.abs(a2));
                return n6 > n5 && n6 <= 1.0;
            }
            return true;
        }
        
        public static final boolean equaldirection(double n, double n2, final double n3) {
            if (n > 6.283185307179586) {
                n -= 6.283185307179586;
            }
            else if (n < 0.0) {
                n += 6.283185307179586;
            }
            if (n2 > 6.283185307179586) {
                n2 -= 6.283185307179586;
            }
            else if (n2 < 0.0) {
                n2 += 6.283185307179586;
            }
            return Math.abs(n - n2) < n3;
        }
        
        public static final double linepointdistance(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            final Point closestpointonline = closestpointonline(n, n2, n3, n4, n5, n6);
            return Point.distance(n5, n6, closestpointonline.getX(), closestpointonline.getY());
        }
        
        public static final double linepointdistancesq(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            final double n7 = n4 - n2;
            final double n8 = n - n3;
            final double n9 = (n4 - n2) * n + (n - n3) * n2;
            final double n10 = -n8 * n5 + n7 * n6;
            final double n11 = n7 * n7 - -n8 * n8;
            double n12 = 0.0;
            double n13 = 0.0;
            if (n11 != 0.0) {
                n12 = (n7 * n9 - n8 * n10) / n11;
                n13 = (n7 * n10 - -n8 * n9) / n11;
            }
            return Math.abs((n12 - n5) * (n12 - n5) + (n13 - n6) * (n13 - n6));
        }
        
        public static final Point closestpointonline(final StaticLine staticLine, final Point point) {
            return closestpointonline(staticLine.getX(), staticLine.getY(), staticLine.getX2(), staticLine.getY2(), point.getX(), point.getY());
        }
        
        public static final Point closestpointonline(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            final double n7 = n4 - n2;
            final double n8 = n - n3;
            final double n9 = (n4 - n2) * n + (n - n3) * n2;
            final double n10 = -n8 * n5 + n7 * n6;
            final double n11 = n7 * n7 - -n8 * n8;
            double n12;
            double n13;
            if (n11 != 0.0) {
                n12 = (n7 * n9 - n8 * n10) / n11;
                n13 = (n7 * n10 - -n8 * n9) / n11;
            }
            else {
                n12 = n5;
                n13 = n6;
            }
            return new Point(n12, n13);
        }
        
        public static final Vector getVector(final Point point, final Point point2) {
            return new RectVector(point.getX() - point2.getX(), point.getY() - point2.getY());
        }
        
        public static final Vector rotate(final Vector vector, final double n) {
            return new RectVector(vector.getvx() * Math.cos(n) - vector.getvy() * Math.sin(n), vector.getvx() * Math.sin(n) + vector.getvy() * Math.cos(n));
        }
    }
    
    interface Vector
    {
        double getvx();
        
        double getvy();
        
        double getLength();
    }
}
