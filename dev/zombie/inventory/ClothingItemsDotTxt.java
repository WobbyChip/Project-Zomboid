// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.io.FileWriter;
import zombie.util.StringUtils;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import zombie.core.logger.ExceptionLogger;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import zombie.ZomboidFileSystem;

public final class ClothingItemsDotTxt
{
    public static final ClothingItemsDotTxt instance;
    private final StringBuilder buf;
    
    public ClothingItemsDotTxt() {
        this.buf = new StringBuilder();
    }
    
    private int readBlock(final String s, int block, final Block block2) {
        int i;
        for (i = block; i < s.length(); ++i) {
            if (s.charAt(i) == '{') {
                final Block block3 = new Block();
                block2.children.add(block3);
                block2.elements.add(block3);
                final String trim = s.substring(block, i).trim();
                final int max = Math.max(trim.indexOf(32), trim.indexOf(9));
                if (max == -1) {
                    block3.type = trim;
                }
                else {
                    block3.type = trim.substring(0, max);
                    block3.id = trim.substring(max).trim();
                }
                i = (block = this.readBlock(s, i + 1, block3));
            }
            else {
                if (s.charAt(i) == '}') {
                    if (!s.substring(block, i).trim().isEmpty()) {
                        final Value e = new Value();
                        e.string = s.substring(block, i).trim();
                        block2.values.add(e.string);
                        block2.elements.add(e);
                    }
                    return i + 1;
                }
                if (s.charAt(i) == ',') {
                    final Value e2 = new Value();
                    e2.string = s.substring(block, i).trim();
                    block2.values.add(e2.string);
                    block2.elements.add(e2);
                    block = i + 1;
                }
            }
        }
        return i;
    }
    
    public void LoadFile() {
        final String string = ZomboidFileSystem.instance.getString("media/scripts/clothingItems.txt");
        final File file = new File(string);
        if (!file.exists()) {
            return;
        }
        try {
            final FileReader in = new FileReader(string);
            try {
                final BufferedReader bufferedReader = new BufferedReader(in);
                try {
                    this.buf.setLength(0);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        this.buf.append(line);
                    }
                    bufferedReader.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedReader.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Throwable t3) {
            ExceptionLogger.logException(t3);
            return;
        }
        int n;
        for (int i = this.buf.lastIndexOf("*/"); i != -1; i = this.buf.lastIndexOf("*/", n)) {
            n = this.buf.lastIndexOf("/*", i - 1);
            if (n == -1) {
                break;
            }
            int n2;
            for (int j = this.buf.lastIndexOf("*/", i - 1); j > n; j = this.buf.lastIndexOf("*/", n2 - 2)) {
                n2 = n;
                this.buf.substring(n2, j + 2);
                n = this.buf.lastIndexOf("/*", n - 2);
                if (n == -1) {
                    break;
                }
            }
            if (n == -1) {
                break;
            }
            this.buf.substring(n, i + 2);
            this.buf.replace(n, i + 2, "");
        }
        final Block block = new Block();
        this.readBlock(this.buf.toString(), 0, block);
        final Path path = FileSystems.getDefault().getPath("media/clothing/clothingItems", new String[0]);
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
            try {
                for (final Path path2 : directoryStream) {
                    if (Files.isDirectory(path2, new LinkOption[0])) {
                        continue;
                    }
                    final String string2 = path2.getFileName().toString();
                    if (!string2.endsWith(".xml")) {
                        continue;
                    }
                    final String trimSuffix = StringUtils.trimSuffix(string2, ".xml");
                    System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, string2, trimSuffix));
                    this.addClothingItem(trimSuffix, block.children.get(0));
                }
                if (directoryStream != null) {
                    directoryStream.close();
                }
            }
            catch (Throwable t4) {
                if (directoryStream != null) {
                    try {
                        directoryStream.close();
                    }
                    catch (Throwable exception3) {
                        t4.addSuppressed(exception3);
                    }
                }
                throw t4;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            final FileWriter fileWriter = new FileWriter(file);
            try {
                fileWriter.write(block.children.get(0).toString());
                fileWriter.close();
            }
            catch (Throwable t5) {
                try {
                    fileWriter.close();
                }
                catch (Throwable exception4) {
                    t5.addSuppressed(exception4);
                }
                throw t5;
            }
        }
        catch (Throwable t6) {
            ExceptionLogger.logException(t6);
        }
        System.out.println(block.children.get(0));
    }
    
    private void addClothingItem(final String id, final Block block) {
        if (id.startsWith("FemaleHair_")) {
            return;
        }
        if (id.startsWith("MaleBeard_")) {
            return;
        }
        if (id.startsWith("MaleHair_")) {
            return;
        }
        if (id.startsWith("ZedDmg_")) {
            return;
        }
        if (id.startsWith("Bandage_")) {
            return;
        }
        if (id.startsWith("Zed_Skin")) {
            return;
        }
        for (final Block block2 : block.children) {
            if ("item".equals(block2.type) && id.equals(block2.id)) {
                return;
            }
        }
        final Block block3 = new Block();
        block3.type = "item";
        block3.id = id;
        final Value e = new Value();
        e.string = "Type = Clothing";
        block3.elements.add(e);
        block3.values.add(e.string);
        final Value e2 = new Value();
        e2.string = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, id);
        block3.elements.add(e2);
        block3.values.add(e2.string);
        final Value e3 = new Value();
        e3.string = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, id);
        block3.elements.add(e3);
        block3.values.add(e3.string);
        block.elements.add(block3);
        block.children.add(block3);
    }
    
    static {
        instance = new ClothingItemsDotTxt();
    }
    
    private static class Value implements BlockElement
    {
        String string;
        
        @Override
        public Block asBlock() {
            return null;
        }
        
        @Override
        public Value asValue() {
            return this;
        }
        
        @Override
        public String toString() {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.string);
        }
        
        @Override
        public String toXML() {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.string);
        }
    }
    
    private static class Block implements BlockElement
    {
        public String type;
        public String id;
        public ArrayList<BlockElement> elements;
        public ArrayList<String> values;
        public ArrayList<Block> children;
        
        private Block() {
            this.elements = new ArrayList<BlockElement>();
            this.values = new ArrayList<String>();
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
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.type, (this.id == null) ? "" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.id)));
            sb.append("{\n");
            final Iterator<BlockElement> iterator = this.elements.iterator();
            while (iterator.hasNext()) {
                final String[] split = iterator.next().toString().split("\n");
                for (int length = split.length, i = 0; i < length; ++i) {
                    sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, split[i]));
                }
            }
            sb.append("}\n");
            return sb.toString();
        }
        
        @Override
        public String toXML() {
            final StringBuilder sb = new StringBuilder();
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.type, this.id));
            final Iterator<BlockElement> iterator = this.elements.iterator();
            while (iterator.hasNext()) {
                final String[] split = iterator.next().toXML().split("\n");
                for (int length = split.length, i = 0; i < length; ++i) {
                    sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, split[i]));
                }
            }
            sb.append("</Block>\n");
            return sb.toString();
        }
    }
    
    private interface BlockElement
    {
        Block asBlock();
        
        Value asValue();
        
        String toXML();
    }
}
