// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import java.util.function.BiFunction;

public class StringUtils
{
    public static final String s_emptyString = "";
    public static final char UTF8_BOM = '\ufeff';
    
    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.length() == 0;
    }
    
    public static boolean isNullOrWhitespace(final String s) {
        return isNullOrEmpty(s) || isWhitespace(s);
    }
    
    private static boolean isWhitespace(final String s) {
        final int length = s.length();
        if (length > 0) {
            for (int i = 0, n = length / 2, index = length - 1; i <= n; ++i, --index) {
                if (!Character.isWhitespace(s.charAt(i)) || !Character.isWhitespace(s.charAt(index))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static String discardNullOrWhitespace(final String s) {
        return isNullOrWhitespace(s) ? null : s;
    }
    
    public static String trimPrefix(final String s, final String prefix) {
        if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }
    
    public static String trimSuffix(final String s, final String suffix) {
        if (s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }
    
    public static boolean equals(final String s, final String anObject) {
        return s == anObject || (s != null && s.equals(anObject));
    }
    
    public static boolean startsWithIgnoreCase(final String s, final String other) {
        return s.regionMatches(true, 0, other, 0, other.length());
    }
    
    public static boolean endsWithIgnoreCase(final String s, final String other) {
        final int length = other.length();
        return s.regionMatches(true, s.length() - length, other, 0, length);
    }
    
    public static boolean containsIgnoreCase(final String s, final String other) {
        for (int i = s.length() - other.length(); i >= 0; --i) {
            if (s.regionMatches(true, i, other, 0, other.length())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean equalsIgnoreCase(final String s, final String anotherString) {
        return s == anotherString || (s != null && s.equalsIgnoreCase(anotherString));
    }
    
    public static boolean tryParseBoolean(final String s) {
        if (isNullOrWhitespace(s)) {
            return false;
        }
        final String trim = s.trim();
        return trim.equalsIgnoreCase("true") || trim.equals("1") || trim.equals("1.0");
    }
    
    public static boolean isBoolean(final String s) {
        final String trim = s.trim();
        return trim.equalsIgnoreCase("true") || trim.equals("1") || trim.equals("1.0") || (trim.equalsIgnoreCase("false") || trim.equals("0") || trim.equals("0.0"));
    }
    
    public static boolean contains(final String[] array, final String s, final BiFunction<String, String, Boolean> biFunction) {
        return indexOf(array, s, biFunction) > -1;
    }
    
    public static int indexOf(final String[] array, final String s, final BiFunction<String, String, Boolean> biFunction) {
        int n = -1;
        for (int i = 0; i < array.length; ++i) {
            if (biFunction.apply(array[i], s)) {
                n = i;
                break;
            }
        }
        return n;
    }
    
    public static String indent(final String s) {
        return indent(s, "", "\t");
    }
    
    private static String indent(final String s, final String s2, final String s3) {
        return indent(s, System.lineSeparator(), s2, s3);
    }
    
    private static String indent(final String s, final String str, final String str2, final String str3) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        final int length = s.length();
        final StringBuilder sb = new StringBuilder(length);
        final StringBuilder sb2 = new StringBuilder(length);
        int n = 0;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 13: {
                    break;
                }
                case 10: {
                    sb.append((CharSequence)sb2);
                    sb.append(str);
                    sb2.setLength(0);
                    ++n;
                    break;
                }
                default: {
                    if (sb2.length() == 0) {
                        if (n == 0) {
                            sb2.append(str2);
                        }
                        else {
                            sb2.append(str3);
                        }
                    }
                    sb2.append(char1);
                    break;
                }
            }
        }
        sb.append((CharSequence)sb2);
        sb2.setLength(0);
        return sb.toString();
    }
    
    public static String leftJustify(final String s, final int n) {
        if (s == null) {
            return leftJustify("", n);
        }
        final int length = s.length();
        if (length >= n) {
            return s;
        }
        final int n2 = n - length;
        final char[] value = new char[n2];
        for (int i = 0; i < n2; ++i) {
            value[i] = ' ';
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, new String(value));
    }
    
    public static String moduleDotType(final String s, final String s2) {
        if (s2 == null) {
            return null;
        }
        if (s2.contains(".")) {
            return s2;
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
    }
    
    public static String stripBOM(final String s) {
        if (s != null && s.length() > 0 && s.charAt(0) == '\ufeff') {
            return s.substring(1);
        }
        return s;
    }
}
