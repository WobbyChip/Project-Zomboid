// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.util;

public class Display
{
    private static final String displayChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#¤%&/()=?'@£${[]}+|^~*-_.:,;<>\\";
    
    public static String display(final int i) {
        return String.valueOf(i);
    }
    
    static String hexChar(final char i) {
        final String hexString = Integer.toHexString(i);
        switch (hexString.length()) {
            case 1: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hexString);
            }
            case 2: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hexString);
            }
            case 3: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hexString);
            }
            case 4: {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hexString);
            }
            default: {
                throw new RuntimeException("Internal error");
            }
        }
    }
}
