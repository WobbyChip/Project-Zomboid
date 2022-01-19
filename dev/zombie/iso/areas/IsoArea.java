// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas;

import com.jcraft.jorbis.Mapping0;
import com.jcraft.jorbis.Block;
import java.io.FileInputStream;

public class IsoArea
{
    public static String version;
    public static boolean Doobo;
    
    public static byte[] asasa(final String name) throws Exception {
        final FileInputStream fileInputStream = new FileInputStream(name);
        return new byte[1024];
    }
    
    public static String Ardo(final String s) throws Exception {
        final byte[] asasa = asasa(s);
        String asdsadsa = "";
        for (int i = 0; i < asasa.length; ++i) {
            asdsadsa = Block.asdsadsa(asdsadsa, asasa, i);
        }
        return asdsadsa;
    }
    
    public static boolean Thigglewhat2(final String s, final String s2) {
        String s3;
        try {
            s3 = Ardo(s);
            if (!s3.equals(s2)) {
                return false;
            }
        }
        catch (Exception ex) {
            try {
                s3 = Ardo(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ, s));
            }
            catch (Exception ex2) {
                return false;
            }
        }
        return s3.equals(s2);
    }
    
    public static String Thigglewhat22(final String s) {
        String s2;
        try {
            s2 = Ardo(s);
        }
        catch (Exception ex) {
            try {
                s2 = Ardo(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ, s));
            }
            catch (Exception ex2) {
                return "";
            }
        }
        return s2;
    }
    
    public static boolean Thigglewhat() {
        invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, "", Thigglewhat22(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Mapping0.ThiggleAQQ2, Mapping0.ThiggleA, Mapping0.ThiggleAQ, Mapping0.ThiggleAQ2))), Thigglewhat22(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Mapping0.ThiggleAQQ2, Mapping0.ThiggleB, Mapping0.ThiggleBB, Mapping0.ThiggleAQ, Mapping0.ThiggleAQ2))), Thigglewhat22(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Mapping0.ThiggleAQQ2, Mapping0.ThiggleC, Mapping0.ThiggleCC, Mapping0.ThiggleAQ, Mapping0.ThiggleAQ2))), Thigglewhat22(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Mapping0.ThiggleAQQ2, Mapping0.ThiggleD, Mapping0.ThiggleDA, Mapping0.ThiggleDB, Mapping0.ThiggleAQ, Mapping0.ThiggleAQ2))), Thigglewhat22(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Mapping0.ThiggleAQQ2, Mapping0.ThiggleE, Mapping0.ThiggleEA, Mapping0.ThiggleAQ, Mapping0.ThiggleAQ2))), Thigglewhat22(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Mapping0.ThiggleAQQ2, Mapping0.ThiggleF, Mapping0.ThiggleFA, Mapping0.ThiggleAQ, Mapping0.ThiggleAQ2))), Thigglewhat22(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Mapping0.ThiggleAQQ2, Mapping0.ThiggleG, Mapping0.ThiggleGA, Mapping0.ThiggleGB, Mapping0.ThiggleGC, Mapping0.ThiggleAQ, Mapping0.ThiggleAQ2))).toUpperCase();
        return true;
    }
    
    static {
        IsoArea.version = "0a2a0q";
    }
}
