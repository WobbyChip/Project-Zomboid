// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting;

import java.util.Iterator;
import java.util.ArrayList;

public final class ScriptParser
{
    private static StringBuilder stringBuilder;
    
    public static int readBlock(final String s, int block, final Block block2) {
        int i;
        for (i = block; i < s.length(); ++i) {
            if (s.charAt(i) == '{') {
                final Block block3 = new Block();
                block2.children.add(block3);
                block2.elements.add(block3);
                final String[] split = s.substring(block, i).trim().split("\\s+");
                block3.type = split[0];
                block3.id = ((split.length > 1) ? split[1] : null);
                i = (block = readBlock(s, i + 1, block3));
            }
            else {
                if (s.charAt(i) == '}') {
                    return i + 1;
                }
                if (s.charAt(i) == ',') {
                    final Value value = new Value();
                    value.string = s.substring(block, i);
                    block2.values.add(value);
                    block2.elements.add(value);
                    block = i + 1;
                }
            }
        }
        return i;
    }
    
    public static Block parse(final String s) {
        final Block block = new Block();
        readBlock(s, 0, block);
        return block;
    }
    
    public static String stripComments(String string) {
        ScriptParser.stringBuilder.setLength(0);
        ScriptParser.stringBuilder.append(string);
        int n;
        for (int i = ScriptParser.stringBuilder.lastIndexOf("*/"); i != -1; i = ScriptParser.stringBuilder.lastIndexOf("*/", n)) {
            n = ScriptParser.stringBuilder.lastIndexOf("/*", i - 1);
            if (n == -1) {
                break;
            }
            int n2;
            for (int j = ScriptParser.stringBuilder.lastIndexOf("*/", i - 1); j > n; j = ScriptParser.stringBuilder.lastIndexOf("*/", n2 - 2)) {
                n2 = n;
                n = ScriptParser.stringBuilder.lastIndexOf("/*", n - 2);
                if (n == -1) {
                    break;
                }
            }
            if (n == -1) {
                break;
            }
            ScriptParser.stringBuilder.replace(n, i + 2, "");
        }
        string = ScriptParser.stringBuilder.toString();
        ScriptParser.stringBuilder.setLength(0);
        return string;
    }
    
    public static ArrayList<String> parseTokens(String substring) {
        final ArrayList<String> list = new ArrayList<String>();
        while (true) {
            int i = 0;
            int index = 0;
            int index2 = 0;
            if (substring.indexOf("}", index + 1) == -1) {
                break;
            }
            do {
                index = substring.indexOf("{", index + 1);
                index2 = substring.indexOf("}", index2 + 1);
                if ((index2 < index && index2 != -1) || index == -1) {
                    index = index2;
                    --i;
                }
                else {
                    index2 = index;
                    ++i;
                }
            } while (i > 0);
            list.add(substring.substring(0, index + 1).trim());
            substring = substring.substring(index + 1);
        }
        if (substring.trim().length() > 0) {
            list.add(substring.trim());
        }
        return list;
    }
    
    static {
        ScriptParser.stringBuilder = new StringBuilder();
    }
    
    public static class Value implements BlockElement
    {
        public String string;
        
        @Override
        public Block asBlock() {
            return null;
        }
        
        @Override
        public Value asValue() {
            return this;
        }
        
        @Override
        public void prettyPrint(final int n, final StringBuilder sb, final String str) {
            for (int i = 0; i < n; ++i) {
                sb.append('\t');
            }
            sb.append(this.string.trim());
            sb.append(',');
            sb.append(str);
        }
        
        public String getKey() {
            final int index = this.string.indexOf(61);
            return (index == -1) ? this.string : this.string.substring(0, index);
        }
        
        public String getValue() {
            final int index = this.string.indexOf(61);
            return (index == -1) ? "" : this.string.substring(index + 1);
        }
    }
    
    public static class Block implements BlockElement
    {
        public String type;
        public String id;
        public final ArrayList<BlockElement> elements;
        public final ArrayList<Value> values;
        public final ArrayList<Block> children;
        
        public Block() {
            this.elements = new ArrayList<BlockElement>();
            this.values = new ArrayList<Value>();
            this.children = new ArrayList<Block>();
        }
        
        @Override
        public Block asBlock() {
            return this;
        }
        
        @Override
        public Value asValue() {
            return null;
        }
        
        public boolean isEmpty() {
            return this.elements.isEmpty();
        }
        
        @Override
        public void prettyPrint(final int n, final StringBuilder sb, final String str) {
            for (int i = 0; i < n; ++i) {
                sb.append('\t');
            }
            sb.append(this.type);
            if (this.id != null) {
                sb.append(" ");
                sb.append(this.id);
            }
            sb.append(str);
            for (int j = 0; j < n; ++j) {
                sb.append('\t');
            }
            sb.append('{');
            sb.append(str);
            this.prettyPrintElements(n + 1, sb, str);
            for (int k = 0; k < n; ++k) {
                sb.append('\t');
            }
            sb.append('}');
            sb.append(str);
        }
        
        public void prettyPrintElements(final int n, final StringBuilder sb, final String s) {
            BlockElement blockElement = null;
            for (final BlockElement blockElement2 : this.elements) {
                if (blockElement2.asBlock() != null && blockElement != null) {
                    sb.append(s);
                }
                if (blockElement2.asValue() != null && blockElement instanceof Block) {
                    sb.append(s);
                }
                blockElement2.prettyPrint(n, sb, s);
                blockElement = blockElement2;
            }
        }
        
        public Block addBlock(final String type, final String id) {
            final Block block = new Block();
            block.type = type;
            block.id = id;
            this.elements.add(block);
            this.children.add(block);
            return block;
        }
        
        public Block getBlock(final String anObject, final String anObject2) {
            for (final Block block : this.children) {
                if (block.type.equals(anObject) && ((block.id != null && block.id.equals(anObject2)) || (block.id == null && anObject2 == null))) {
                    return block;
                }
            }
            return null;
        }
        
        public Value getValue(final String anObject) {
            for (final Value value : this.values) {
                if (value.string.indexOf(61) > 0 && value.getKey().trim().equals(anObject)) {
                    return value;
                }
            }
            return null;
        }
        
        public void setValue(final String s, final String s2) {
            final Value value = this.getValue(s);
            if (value == null) {
                this.addValue(s, s2);
            }
            else {
                value.string = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
            }
        }
        
        public Value addValue(final String s, final String s2) {
            final Value value = new Value();
            value.string = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2);
            this.elements.add(value);
            this.values.add(value);
            return value;
        }
        
        public void moveValueAfter(final String s, final String s2) {
            final Value value = this.getValue(s);
            final Value value2 = this.getValue(s2);
            if (value == null || value2 == null) {
                return;
            }
            this.elements.remove(value);
            this.values.remove(value);
            this.elements.add(this.elements.indexOf(value2) + 1, value);
            this.values.add(this.values.indexOf(value2) + 1, value);
        }
    }
    
    public interface BlockElement
    {
        Block asBlock();
        
        Value asValue();
        
        void prettyPrint(final int p0, final StringBuilder p1, final String p2);
    }
}
