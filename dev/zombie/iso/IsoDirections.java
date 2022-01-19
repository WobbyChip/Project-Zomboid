// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.Rand;

public enum IsoDirections
{
    N(0), 
    NW(1), 
    W(2), 
    SW(3), 
    S(4), 
    SE(5), 
    E(6), 
    NE(7), 
    Max(8);
    
    private static final IsoDirections[] VALUES;
    private static IsoDirections[][] directionLookup;
    private static final Vector2 temp;
    private final int index;
    
    private IsoDirections(final int index) {
        this.index = index;
    }
    
    public static IsoDirections fromIndex(int i) {
        while (i < 0) {
            i += 8;
        }
        i %= 8;
        return IsoDirections.VALUES[i];
    }
    
    public IsoDirections RotLeft(final int n) {
        IsoDirections isoDirections = RotLeft(this);
        for (int i = 0; i < n - 1; ++i) {
            isoDirections = RotLeft(isoDirections);
        }
        return isoDirections;
    }
    
    public IsoDirections RotRight(final int n) {
        IsoDirections isoDirections = RotRight(this);
        for (int i = 0; i < n - 1; ++i) {
            isoDirections = RotRight(isoDirections);
        }
        return isoDirections;
    }
    
    public IsoDirections RotLeft() {
        return RotLeft(this);
    }
    
    public IsoDirections RotRight() {
        return RotRight(this);
    }
    
    public static IsoDirections RotLeft(final IsoDirections isoDirections) {
        switch (isoDirections) {
            case NE: {
                return IsoDirections.N;
            }
            case N: {
                return IsoDirections.NW;
            }
            case NW: {
                return IsoDirections.W;
            }
            case W: {
                return IsoDirections.SW;
            }
            case SW: {
                return IsoDirections.S;
            }
            case S: {
                return IsoDirections.SE;
            }
            case SE: {
                return IsoDirections.E;
            }
            case E: {
                return IsoDirections.NE;
            }
            default: {
                return IsoDirections.Max;
            }
        }
    }
    
    public static IsoDirections RotRight(final IsoDirections isoDirections) {
        switch (isoDirections) {
            case N: {
                return IsoDirections.NE;
            }
            case NE: {
                return IsoDirections.E;
            }
            case E: {
                return IsoDirections.SE;
            }
            case SE: {
                return IsoDirections.S;
            }
            case S: {
                return IsoDirections.SW;
            }
            case SW: {
                return IsoDirections.W;
            }
            case W: {
                return IsoDirections.NW;
            }
            case NW: {
                return IsoDirections.N;
            }
            default: {
                return IsoDirections.Max;
            }
        }
    }
    
    public static void generateTables() {
        IsoDirections.directionLookup = new IsoDirections[200][200];
        for (int i = 0; i < 200; ++i) {
            for (int j = 0; j < 200; ++j) {
                final Vector2 vector2 = new Vector2((i - 100) / 100.0f, (j - 100) / 100.0f);
                vector2.normalize();
                IsoDirections.directionLookup[i][j] = fromAngleActual(vector2);
            }
        }
    }
    
    public static IsoDirections fromAngleActual(final Vector2 vector2) {
        IsoDirections.temp.x = vector2.x;
        IsoDirections.temp.y = vector2.y;
        IsoDirections.temp.normalize();
        final float directionNeg = IsoDirections.temp.getDirectionNeg();
        final float n = 0.7853982f;
        float n2 = (float)(6.2831855f + Math.toRadians(112.5));
        for (int i = 0; i < 8; ++i) {
            n2 += n;
            if ((directionNeg >= n2 && directionNeg <= n2 + n) || (directionNeg + 6.2831855f >= n2 && directionNeg + 6.2831855f <= n2 + n) || (directionNeg - 6.2831855f >= n2 && directionNeg - 6.2831855f <= n2 + n)) {
                return fromIndex(i);
            }
            if (n2 > 6.283185307179586) {
                n2 -= (float)6.283185307179586;
            }
        }
        if (IsoDirections.temp.x > 0.5f) {
            if (IsoDirections.temp.y < -0.5f) {
                return IsoDirections.NE;
            }
            if (IsoDirections.temp.y > 0.5f) {
                return IsoDirections.SE;
            }
            return IsoDirections.E;
        }
        else if (IsoDirections.temp.x < -0.5f) {
            if (IsoDirections.temp.y < -0.5f) {
                return IsoDirections.NW;
            }
            if (IsoDirections.temp.y > 0.5f) {
                return IsoDirections.SW;
            }
            return IsoDirections.W;
        }
        else {
            if (IsoDirections.temp.y < -0.5f) {
                return IsoDirections.N;
            }
            if (IsoDirections.temp.y > 0.5f) {
                return IsoDirections.S;
            }
            return IsoDirections.N;
        }
    }
    
    public static IsoDirections fromAngle(final float n) {
        return fromAngle((float)Math.cos(n), (float)Math.sin(n));
    }
    
    public static IsoDirections fromAngle(final Vector2 vector2) {
        return fromAngle(vector2.x, vector2.y);
    }
    
    public static IsoDirections fromAngle(final float x, final float y) {
        IsoDirections.temp.x = x;
        IsoDirections.temp.y = y;
        if (IsoDirections.temp.getLengthSquared() != 1.0f) {
            IsoDirections.temp.normalize();
        }
        if (IsoDirections.directionLookup == null) {
            generateTables();
        }
        int n = (int)((IsoDirections.temp.x + 1.0f) * 100.0f);
        int n2 = (int)((IsoDirections.temp.y + 1.0f) * 100.0f);
        if (n >= 200) {
            n = 199;
        }
        if (n2 >= 200) {
            n2 = 199;
        }
        if (n < 0) {
            n = 0;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        return IsoDirections.directionLookup[n][n2];
    }
    
    public static IsoDirections cardinalFromAngle(final Vector2 vector2) {
        final boolean b = vector2.getX() >= vector2.getY();
        final boolean b2 = vector2.getX() > -vector2.getY();
        if (b) {
            if (b2) {
                return IsoDirections.E;
            }
            return IsoDirections.N;
        }
        else {
            if (b2) {
                return IsoDirections.S;
            }
            return IsoDirections.W;
        }
    }
    
    public static IsoDirections reverse(final IsoDirections isoDirections) {
        switch (isoDirections) {
            case S: {
                return IsoDirections.N;
            }
            case SE: {
                return IsoDirections.NW;
            }
            case E: {
                return IsoDirections.W;
            }
            case NE: {
                return IsoDirections.SW;
            }
            case N: {
                return IsoDirections.S;
            }
            case NW: {
                return IsoDirections.SE;
            }
            case W: {
                return IsoDirections.E;
            }
            case SW: {
                return IsoDirections.NE;
            }
            default: {
                return IsoDirections.Max;
            }
        }
    }
    
    public int index() {
        return this.index % 8;
    }
    
    public String toCompassString() {
        switch (this.index) {
            case 0: {
                return "9";
            }
            case 1: {
                return "8";
            }
            case 2: {
                return "7";
            }
            case 3: {
                return "4";
            }
            case 4: {
                return "1";
            }
            case 5: {
                return "2";
            }
            case 6: {
                return "3";
            }
            case 7: {
                return "6";
            }
            default: {
                return "";
            }
        }
    }
    
    public Vector2 ToVector() {
        switch (this) {
            case S: {
                IsoDirections.temp.x = 0.0f;
                IsoDirections.temp.y = 1.0f;
                break;
            }
            case SE: {
                IsoDirections.temp.x = 1.0f;
                IsoDirections.temp.y = 1.0f;
                break;
            }
            case E: {
                IsoDirections.temp.x = 1.0f;
                IsoDirections.temp.y = 0.0f;
                break;
            }
            case NE: {
                IsoDirections.temp.x = 1.0f;
                IsoDirections.temp.y = -1.0f;
                break;
            }
            case N: {
                IsoDirections.temp.x = 0.0f;
                IsoDirections.temp.y = -1.0f;
                break;
            }
            case NW: {
                IsoDirections.temp.x = -1.0f;
                IsoDirections.temp.y = -1.0f;
                break;
            }
            case W: {
                IsoDirections.temp.x = -1.0f;
                IsoDirections.temp.y = 0.0f;
                break;
            }
            case SW: {
                IsoDirections.temp.x = -1.0f;
                IsoDirections.temp.y = 1.0f;
                break;
            }
        }
        IsoDirections.temp.normalize();
        return IsoDirections.temp;
    }
    
    public float toAngle() {
        final float n = 0.7853982f;
        switch (this) {
            case N: {
                return n * 0.0f;
            }
            case NW: {
                return n * 1.0f;
            }
            case W: {
                return n * 2.0f;
            }
            case SW: {
                return n * 3.0f;
            }
            case S: {
                return n * 4.0f;
            }
            case SE: {
                return n * 5.0f;
            }
            case E: {
                return n * 6.0f;
            }
            case NE: {
                return n * 7.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    public static IsoDirections getRandom() {
        return fromIndex(Rand.Next(0, IsoDirections.Max.index));
    }
    
    private static /* synthetic */ IsoDirections[] $values() {
        return new IsoDirections[] { IsoDirections.N, IsoDirections.NW, IsoDirections.W, IsoDirections.SW, IsoDirections.S, IsoDirections.SE, IsoDirections.E, IsoDirections.NE, IsoDirections.Max };
    }
    
    static {
        $VALUES = $values();
        VALUES = values();
        temp = new Vector2();
    }
}
