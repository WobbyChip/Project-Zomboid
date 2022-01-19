// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import zombie.core.math.PZMath;
import zombie.core.Core;

public class Bits
{
    public static final boolean ENABLED = true;
    public static final int BIT_0 = 0;
    public static final int BIT_1 = 1;
    public static final int BIT_2 = 2;
    public static final int BIT_3 = 4;
    public static final int BIT_4 = 8;
    public static final int BIT_5 = 16;
    public static final int BIT_6 = 32;
    public static final int BIT_7 = 64;
    public static final int BIT_BYTE_MAX = 64;
    public static final int BIT_8 = 128;
    public static final int BIT_9 = 256;
    public static final int BIT_10 = 512;
    public static final int BIT_11 = 1024;
    public static final int BIT_12 = 2048;
    public static final int BIT_13 = 4096;
    public static final int BIT_14 = 8192;
    public static final int BIT_15 = 16384;
    public static final int BIT_SHORT_MAX = 16384;
    public static final int BIT_16 = 32768;
    public static final int BIT_17 = 65536;
    public static final int BIT_18 = 131072;
    public static final int BIT_19 = 262144;
    public static final int BIT_20 = 524288;
    public static final int BIT_21 = 1048576;
    public static final int BIT_22 = 2097152;
    public static final int BIT_23 = 4194304;
    public static final int BIT_24 = 8388608;
    public static final int BIT_25 = 16777216;
    public static final int BIT_26 = 33554432;
    public static final int BIT_27 = 67108864;
    public static final int BIT_28 = 134217728;
    public static final int BIT_29 = 268435456;
    public static final int BIT_30 = 536870912;
    public static final int BIT_31 = 1073741824;
    public static final int BIT_INT_MAX = 1073741824;
    public static final long BIT_32 = 2147483648L;
    public static final long BIT_33 = 4294967296L;
    public static final long BIT_34 = 8589934592L;
    public static final long BIT_35 = 17179869184L;
    public static final long BIT_36 = 34359738368L;
    public static final long BIT_37 = 68719476736L;
    public static final long BIT_38 = 137438953472L;
    public static final long BIT_39 = 274877906944L;
    public static final long BIT_40 = 549755813888L;
    public static final long BIT_41 = 1099511627776L;
    public static final long BIT_42 = 2199023255552L;
    public static final long BIT_43 = 4398046511104L;
    public static final long BIT_44 = 8796093022208L;
    public static final long BIT_45 = 17592186044416L;
    public static final long BIT_46 = 35184372088832L;
    public static final long BIT_47 = 70368744177664L;
    public static final long BIT_48 = 140737488355328L;
    public static final long BIT_49 = 281474976710656L;
    public static final long BIT_50 = 562949953421312L;
    public static final long BIT_51 = 1125899906842624L;
    public static final long BIT_52 = 2251799813685248L;
    public static final long BIT_53 = 4503599627370496L;
    public static final long BIT_54 = 9007199254740992L;
    public static final long BIT_55 = 18014398509481984L;
    public static final long BIT_56 = 36028797018963968L;
    public static final long BIT_57 = 72057594037927936L;
    public static final long BIT_58 = 144115188075855872L;
    public static final long BIT_59 = 288230376151711744L;
    public static final long BIT_60 = 576460752303423488L;
    public static final long BIT_61 = 1152921504606846976L;
    public static final long BIT_62 = 2305843009213693952L;
    public static final long BIT_63 = 4611686018427387904L;
    public static final long BIT_LONG_MAX = 4611686018427387904L;
    private static StringBuilder sb;
    
    public static byte packFloatUnitToByte(float clamp) {
        if (clamp < 0.0f || clamp > 1.0f) {
            if (Core.bDebug) {
                throw new RuntimeException("UtilsIO Cannot pack float units out of the range 0.0 to 1.0");
            }
            clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        }
        return (byte)(clamp * 255.0f - 128.0f);
    }
    
    public static float unpackByteToFloatUnit(final byte b) {
        return (b + 128) / 255.0f;
    }
    
    public static byte addFlags(final byte b, final int n) {
        if (n < 0 || n > 64) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        }
        return (byte)(b | n);
    }
    
    public static byte addFlags(final byte b, final long n) {
        if (n < 0L || n > 64L) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
        }
        return (byte)((long)b | n);
    }
    
    public static short addFlags(final short n, final int n2) {
        if (n2 < 0 || n2 > 16384) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
        }
        return (short)(n | n2);
    }
    
    public static short addFlags(final short n, final long n2) {
        if (n2 < 0L || n2 > 16384L) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n2));
        }
        return (short)((long)n | n2);
    }
    
    public static int addFlags(int n, final int n2) {
        if (n2 < 0 || n2 > 1073741824) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
        }
        return n |= n2;
    }
    
    public static int addFlags(int n, final long n2) {
        if (n2 < 0L || n2 > 1073741824L) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n2));
        }
        return n = (int)((long)n | n2);
    }
    
    public static long addFlags(long n, final int n2) {
        if (n2 < 0 || n2 > 4611686018427387904L) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
        }
        return n |= n2;
    }
    
    public static long addFlags(long n, final long n2) {
        if (n2 < 0L || n2 > 4611686018427387904L) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n2));
        }
        return n |= n2;
    }
    
    public static boolean hasFlags(final byte b, final int n) {
        return checkFlags(b, n, 64, CompareOption.ContainsAll);
    }
    
    public static boolean hasFlags(final byte b, final long n) {
        return checkFlags(b, n, 64L, CompareOption.ContainsAll);
    }
    
    public static boolean hasEitherFlags(final byte b, final int n) {
        return checkFlags(b, n, 64, CompareOption.HasEither);
    }
    
    public static boolean hasEitherFlags(final byte b, final long n) {
        return checkFlags(b, n, 64L, CompareOption.HasEither);
    }
    
    public static boolean notHasFlags(final byte b, final int n) {
        return checkFlags(b, n, 64, CompareOption.NotHas);
    }
    
    public static boolean notHasFlags(final byte b, final long n) {
        return checkFlags(b, n, 64L, CompareOption.NotHas);
    }
    
    public static boolean hasFlags(final short n, final int n2) {
        return checkFlags(n, n2, 16384, CompareOption.ContainsAll);
    }
    
    public static boolean hasFlags(final short n, final long n2) {
        return checkFlags(n, n2, 16384L, CompareOption.ContainsAll);
    }
    
    public static boolean hasEitherFlags(final short n, final int n2) {
        return checkFlags(n, n2, 16384, CompareOption.HasEither);
    }
    
    public static boolean hasEitherFlags(final short n, final long n2) {
        return checkFlags(n, n2, 16384L, CompareOption.HasEither);
    }
    
    public static boolean notHasFlags(final short n, final int n2) {
        return checkFlags(n, n2, 16384, CompareOption.NotHas);
    }
    
    public static boolean notHasFlags(final short n, final long n2) {
        return checkFlags(n, n2, 16384L, CompareOption.NotHas);
    }
    
    public static boolean hasFlags(final int n, final int n2) {
        return checkFlags(n, n2, 1073741824, CompareOption.ContainsAll);
    }
    
    public static boolean hasFlags(final int n, final long n2) {
        return checkFlags(n, n2, 1073741824L, CompareOption.ContainsAll);
    }
    
    public static boolean hasEitherFlags(final int n, final int n2) {
        return checkFlags(n, n2, 1073741824, CompareOption.HasEither);
    }
    
    public static boolean hasEitherFlags(final int n, final long n2) {
        return checkFlags(n, n2, 1073741824L, CompareOption.HasEither);
    }
    
    public static boolean notHasFlags(final int n, final int n2) {
        return checkFlags(n, n2, 1073741824, CompareOption.NotHas);
    }
    
    public static boolean notHasFlags(final int n, final long n2) {
        return checkFlags(n, n2, 1073741824L, CompareOption.NotHas);
    }
    
    public static boolean hasFlags(final long n, final int n2) {
        return checkFlags(n, n2, 4611686018427387904L, CompareOption.ContainsAll);
    }
    
    public static boolean hasFlags(final long n, final long n2) {
        return checkFlags(n, n2, 4611686018427387904L, CompareOption.ContainsAll);
    }
    
    public static boolean hasEitherFlags(final long n, final int n2) {
        return checkFlags(n, n2, 4611686018427387904L, CompareOption.HasEither);
    }
    
    public static boolean hasEitherFlags(final long n, final long n2) {
        return checkFlags(n, n2, 4611686018427387904L, CompareOption.HasEither);
    }
    
    public static boolean notHasFlags(final long n, final int n2) {
        return checkFlags(n, n2, 4611686018427387904L, CompareOption.NotHas);
    }
    
    public static boolean notHasFlags(final long n, final long n2) {
        return checkFlags(n, n2, 4611686018427387904L, CompareOption.NotHas);
    }
    
    public static boolean checkFlags(final int n, final int n2, final int n3, final CompareOption compareOption) {
        if (n2 < 0 || n2 > n3) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
        }
        if (compareOption == CompareOption.ContainsAll) {
            return (n & n2) == n2;
        }
        if (compareOption == CompareOption.HasEither) {
            return (n & n2) != 0x0;
        }
        if (compareOption == CompareOption.NotHas) {
            return (n & n2) == 0x0;
        }
        throw new RuntimeException("No valid compare option.");
    }
    
    public static boolean checkFlags(final long n, final long n2, final long n3, final CompareOption compareOption) {
        if (n2 < 0L || n2 > n3) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n2));
        }
        if (compareOption == CompareOption.ContainsAll) {
            return (n & n2) == n2;
        }
        if (compareOption == CompareOption.HasEither) {
            return (n & n2) != 0x0L;
        }
        if (compareOption == CompareOption.NotHas) {
            return (n & n2) == 0x0L;
        }
        throw new RuntimeException("No valid compare option.");
    }
    
    public static int getLen(final byte b) {
        return 1;
    }
    
    public static int getLen(final short n) {
        return 2;
    }
    
    public static int getLen(final int n) {
        return 4;
    }
    
    public static int getLen(final long n) {
        return 8;
    }
    
    private static void clearStringBuilder() {
        if (Bits.sb.length() > 0) {
            Bits.sb.delete(0, Bits.sb.length());
        }
    }
    
    public static String getBitsString(final byte b) {
        return getBitsString(b, 8);
    }
    
    public static String getBitsString(final short n) {
        return getBitsString(n, 16);
    }
    
    public static String getBitsString(final int n) {
        return getBitsString(n, 32);
    }
    
    public static String getBitsString(final long n) {
        return getBitsString(n, 64);
    }
    
    private static String getBitsString(final long n, final int n2) {
        clearStringBuilder();
        if (n != 0L) {
            Bits.sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 - 1));
            long n3 = 1L;
            for (int i = 1; i < n2; ++i) {
                Bits.sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                if ((n & n3) == n3) {
                    Bits.sb.append("1");
                }
                else {
                    Bits.sb.append("0");
                }
                if (i < n2 - 1) {
                    Bits.sb.append(" ");
                }
                n3 *= 2L;
            }
        }
        else {
            Bits.sb.append("No bits saved, 0x0.");
        }
        return Bits.sb.toString();
    }
    
    static {
        Bits.sb = new StringBuilder();
    }
    
    public enum CompareOption
    {
        ContainsAll, 
        HasEither, 
        NotHas;
        
        private static /* synthetic */ CompareOption[] $values() {
            return new CompareOption[] { CompareOption.ContainsAll, CompareOption.HasEither, CompareOption.NotHas };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
