// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting;

import java.util.Stack;

public final class ScriptParsingUtils
{
    public static String[] SplitExceptInbetween(String s, final String s2, final String str) {
        final Stack<String> stack = new Stack<String>();
        int n = 0;
        while (s.contains(s2)) {
            if (n == 0) {
                final int index = s.indexOf(s2);
                final int index2 = s.indexOf(str);
                if (index2 == -1) {
                    final String[] split = s.split(s2);
                    for (int i = 0; i < split.length; ++i) {
                        stack.add(split[i].trim());
                    }
                    final String[] array = new String[stack.size()];
                    for (int j = 0; j < stack.size(); ++j) {
                        array[j] = (String)stack.get(j);
                    }
                    return array;
                }
                if (index == -1) {
                    final String[] array2 = new String[stack.size()];
                    if (!s.trim().isEmpty()) {
                        stack.add(s.trim());
                    }
                    for (int k = 0; k < stack.size(); ++k) {
                        array2[k] = (String)stack.get(k);
                    }
                    return array2;
                }
                if (index < index2) {
                    stack.add(s.substring(0, index));
                    s = s.substring(index + 1);
                }
                else {
                    n = 1;
                }
            }
            else {
                s.indexOf(str);
                s.indexOf(str);
                final int index3 = s.indexOf(s2, s.indexOf(str, s.indexOf(str) + 1) + 1);
                if (index3 == -1) {
                    break;
                }
                final String trim = s.substring(0, index3).trim();
                if (!trim.isEmpty()) {
                    stack.add(trim);
                }
                s = s.substring(index3 + 1);
                n = 0;
            }
        }
        if (!s.trim().isEmpty()) {
            stack.add(s.trim());
        }
        final String[] array3 = new String[stack.size()];
        for (int l = 0; l < stack.size(); ++l) {
            array3[l] = (String)stack.get(l);
        }
        return array3;
    }
    
    public static String[] SplitExceptInbetween(String s, final String s2, final String s3, final String str) {
        int n = 0;
        int index = 0;
        int index2 = 0;
        int index3 = 0;
        final Stack<String> stack = new Stack<String>();
        if (s.indexOf(s3, index) == -1) {
            return s.split(s2);
        }
        do {
            index = s.indexOf(s3, index + 1);
            index2 = s.indexOf(str, index2 + 1);
            index3 = s.indexOf(s2, index3 + 1);
            if (index3 == -1) {
                stack.add(s.trim());
                s = "";
            }
            else if ((index3 < index || (index == -1 && index3 != -1)) && n == 0) {
                stack.add(s.substring(0, index3));
                s = s.substring(index3 + 1);
                index = 0;
                index2 = 0;
                index3 = 0;
            }
            else if ((index2 < index && index2 != -1) || index == -1) {
                index = index2;
                if (--n != 0) {
                    continue;
                }
                stack.add(s.substring(0, index + 1));
                s = s.substring(index + 1);
                index = 0;
                index2 = 0;
                index3 = 0;
            }
            else if (index != -1 && index2 == -1) {
                index2 = index;
                ++n;
            }
            else {
                if (index == -1 || index2 == -1 || index >= index2 || (index <= index3 && index2 >= index3)) {
                    continue;
                }
                stack.add(s.substring(0, index3));
                s = s.substring(index3 + 1);
                index = 0;
                index2 = 0;
                index3 = 0;
            }
        } while (s.trim().length() > 0);
        if (!s.trim().isEmpty()) {
            stack.add(s.trim());
        }
        final String[] array = new String[stack.size()];
        for (int i = 0; i < stack.size(); ++i) {
            array[i] = ((String)stack.get(i)).trim();
        }
        return array;
    }
}
