// 
// Decompiled by Procyon v0.5.36
// 

package zombie.profanity;

import zombie.profanity.locales.Locale;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import zombie.ZomboidFileSystem;

public class ProfanityTest
{
    public static void runTest() {
        ProfanityFilter.getInstance();
        System.out.println("");
        loadDictionary();
        testString(1, "profane stuff:  f u c k. sex xex h4rd \u00c3\u0178hit knight hello, @ $ $ H O L E   ass-hole f-u-c-k f_u_c_k_ @$$h0le fu'ckeerr: sdsi: KUNT as'as!! ffffuuuccckkkerrr");
    }
    
    public static void testString(final int n, final String s) {
        final ProfanityFilter instance = ProfanityFilter.getInstance();
        String filterString = "";
        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        final long nanoTime = System.nanoTime();
        for (int i = 0; i < n; ++i) {
            filterString = instance.filterString(s);
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, (System.nanoTime() - nanoTime) / 1.0E9f));
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filterString));
        System.out.println("");
    }
    
    public static void loadDictionary() {
        System.out.println("");
        System.out.println("Dictionary: ");
        final long nanoTime = System.nanoTime();
        final ProfanityFilter instance = ProfanityFilter.getInstance();
        try {
            final FileReader in = new FileReader(ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, File.separator)));
            final BufferedReader bufferedReader = new BufferedReader(in);
            final StringBuffer sb = new StringBuffer();
            int n = 0;
            int n2 = 0;
            final Locale locale = instance.getLocale();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String returnMatchSetForWord = locale.returnMatchSetForWord(line);
                if (returnMatchSetForWord != null) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, line.trim(), locale.returnPhonizedWord(line.trim()), returnMatchSetForWord));
                    ++n2;
                }
                ++n;
            }
            in.close();
            System.out.println(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, instance.getFilterWordsCount(), n));
            System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, (System.nanoTime() - nanoTime) / 1.0E9f));
        System.out.println("");
    }
}
