// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.ZomboidFileSystem;
import java.nio.file.FileSystems;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.util.Iterator;
import java.io.FileWriter;
import zombie.debug.DebugLog;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Lua.LuaManager;
import java.io.File;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;

public class SpawnRegions
{
    private Region parseRegionTable(final KahluaTable kahluaTable) {
        final Object rawget = kahluaTable.rawget((Object)"name");
        final Object rawget2 = kahluaTable.rawget((Object)"file");
        final Object rawget3 = kahluaTable.rawget((Object)"serverfile");
        if (rawget instanceof String && rawget2 instanceof String) {
            final Region region = new Region();
            region.name = (String)rawget;
            region.file = (String)rawget2;
            return region;
        }
        if (rawget instanceof String && rawget3 instanceof String) {
            final Region region2 = new Region();
            region2.name = (String)rawget;
            region2.serverfile = (String)rawget3;
            return region2;
        }
        return null;
    }
    
    private ArrayList<Profession> parseProfessionsTable(final KahluaTable kahluaTable) {
        ArrayList<Profession> list = null;
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object key = iterator.getKey();
            final Object value = iterator.getValue();
            if (key instanceof String && value instanceof KahluaTable) {
                final ArrayList<Point> pointsTable = this.parsePointsTable((KahluaTable)value);
                if (pointsTable == null) {
                    continue;
                }
                final Profession e = new Profession();
                e.name = (String)key;
                e.points = pointsTable;
                if (list == null) {
                    list = new ArrayList<Profession>();
                }
                list.add(e);
            }
        }
        return list;
    }
    
    private ArrayList<Point> parsePointsTable(final KahluaTable kahluaTable) {
        ArrayList<Point> list = null;
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object value = iterator.getValue();
            if (value instanceof KahluaTable) {
                final Point pointTable = this.parsePointTable((KahluaTable)value);
                if (pointTable == null) {
                    continue;
                }
                if (list == null) {
                    list = new ArrayList<Point>();
                }
                list.add(pointTable);
            }
        }
        return list;
    }
    
    private Point parsePointTable(final KahluaTable kahluaTable) {
        final Object rawget = kahluaTable.rawget((Object)"worldX");
        final Object rawget2 = kahluaTable.rawget((Object)"worldY");
        final Object rawget3 = kahluaTable.rawget((Object)"posX");
        final Object rawget4 = kahluaTable.rawget((Object)"posY");
        final Object rawget5 = kahluaTable.rawget((Object)"posZ");
        if (rawget instanceof Double && rawget2 instanceof Double && rawget3 instanceof Double && rawget4 instanceof Double) {
            final Point point = new Point();
            point.worldX = ((Double)rawget).intValue();
            point.worldY = ((Double)rawget2).intValue();
            point.posX = ((Double)rawget3).intValue();
            point.posY = ((Double)rawget4).intValue();
            point.posZ = ((rawget5 instanceof Double) ? ((Double)rawget5).intValue() : 0);
            return point;
        }
        return null;
    }
    
    public ArrayList<Region> loadRegionsFile(final String pathname) {
        final File file = new File(pathname);
        if (!file.exists()) {
            return null;
        }
        try {
            LuaManager.env.rawset((Object)"SpawnRegions", (Object)null);
            LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
            LuaManager.RunLua(file.getAbsolutePath());
            final Object rawget = LuaManager.env.rawget((Object)"SpawnRegions");
            if (rawget instanceof LuaClosure) {
                final Object[] pcall = LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[0]);
                if (pcall.length > 1 && pcall[1] instanceof KahluaTable) {
                    final ArrayList<Region> list = new ArrayList<Region>();
                    final KahluaTableIterator iterator = ((KahluaTable)pcall[1]).iterator();
                    while (iterator.advance()) {
                        final Object value = iterator.getValue();
                        if (value instanceof KahluaTable) {
                            final Region regionTable = this.parseRegionTable((KahluaTable)value);
                            if (regionTable == null) {
                                continue;
                            }
                            list.add(regionTable);
                        }
                    }
                    return list;
                }
            }
            return null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private String fmtKey(String s) {
        if (s.contains("\\")) {
            s = s.replace("\\", "\\\\");
        }
        if (s.contains("\"")) {
            s = s.replace("\"", "\\\"");
        }
        if (s.contains(" ") || s.contains("\\")) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        if (s.startsWith("\"")) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        return s;
    }
    
    private String fmtValue(String s) {
        if (s.contains("\\")) {
            s = s.replace("\\", "\\\\");
        }
        if (s.contains("\"")) {
            s = s.replace("\"", "\\\"");
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
    }
    
    public boolean saveRegionsFile(final String pathname, final ArrayList<Region> list) {
        final File file = new File(pathname);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
        try {
            final FileWriter fileWriter = new FileWriter(file);
            try {
                final String lineSeparator = System.lineSeparator();
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                for (final Region region : list) {
                    if (region.file != null) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.fmtValue(region.name), this.fmtValue(region.file), lineSeparator));
                    }
                    else if (region.serverfile != null) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.fmtValue(region.name), this.fmtValue(region.serverfile), lineSeparator));
                    }
                    else {
                        if (region.professions == null) {
                            continue;
                        }
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.fmtValue(region.name), lineSeparator));
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                        for (final Profession profession : region.professions) {
                            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.fmtKey(profession.name), lineSeparator));
                            for (final Point point : profession.points) {
                                fileWriter.write(invokedynamic(makeConcatWithConstants:(IIIIILjava/lang/String;)Ljava/lang/String;, point.worldX, point.worldY, point.posX, point.posY, point.posZ, lineSeparator));
                            }
                            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                        }
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                    }
                }
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                final boolean b = true;
                fileWriter.close();
                return b;
            }
            catch (Throwable t) {
                try {
                    fileWriter.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<Profession> loadPointsFile(final String pathname) {
        final File file = new File(pathname);
        if (!file.exists()) {
            return null;
        }
        try {
            LuaManager.env.rawset((Object)"SpawnPoints", (Object)null);
            LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
            LuaManager.RunLua(file.getAbsolutePath());
            final Object rawget = LuaManager.env.rawget((Object)"SpawnPoints");
            if (rawget instanceof LuaClosure) {
                final Object[] pcall = LuaManager.caller.pcall(LuaManager.thread, rawget, new Object[0]);
                if (pcall.length > 1 && pcall[1] instanceof KahluaTable) {
                    return this.parseProfessionsTable((KahluaTable)pcall[1]);
                }
            }
            return null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public boolean savePointsFile(final String pathname, final ArrayList<Profession> list) {
        final File file = new File(pathname);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
        try {
            final FileWriter fileWriter = new FileWriter(file);
            try {
                final String lineSeparator = System.lineSeparator();
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                for (final Profession profession : list) {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.fmtKey(profession.name), lineSeparator));
                    for (final Point point : profession.points) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(IIIIILjava/lang/String;)Ljava/lang/String;, point.worldX, point.worldY, point.posX, point.posY, point.posZ, lineSeparator));
                    }
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                }
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                final boolean b = true;
                fileWriter.close();
                return b;
            }
            catch (Throwable t) {
                try {
                    fileWriter.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public KahluaTable loadPointsTable(final String s) {
        final ArrayList<Profession> loadPointsFile = this.loadPointsFile(s);
        if (loadPointsFile == null) {
            return null;
        }
        final KahluaTable table = LuaManager.platform.newTable();
        for (int i = 0; i < loadPointsFile.size(); ++i) {
            final Profession profession = loadPointsFile.get(i);
            final KahluaTable table2 = LuaManager.platform.newTable();
            for (int j = 0; j < profession.points.size(); ++j) {
                final Point point = profession.points.get(j);
                final KahluaTable table3 = LuaManager.platform.newTable();
                table3.rawset((Object)"worldX", (Object)(double)point.worldX);
                table3.rawset((Object)"worldY", (Object)(double)point.worldY);
                table3.rawset((Object)"posX", (Object)(double)point.posX);
                table3.rawset((Object)"posY", (Object)(double)point.posY);
                table3.rawset((Object)"posZ", (Object)(double)point.posZ);
                table2.rawset(j + 1, (Object)table3);
            }
            table.rawset((Object)profession.name, (Object)table2);
        }
        return table;
    }
    
    public boolean savePointsTable(final String s, final KahluaTable kahluaTable) {
        final ArrayList<Profession> professionsTable = this.parseProfessionsTable(kahluaTable);
        return professionsTable != null && this.savePointsFile(s, professionsTable);
    }
    
    public ArrayList<Region> getDefaultServerRegions() {
        final ArrayList<Region> list = new ArrayList<Region>();
        final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(final Path path) throws IOException {
                return Files.isDirectory(path, new LinkOption[0]) && Files.exists(path.resolve("spawnpoints.lua"), new LinkOption[0]);
            }
        };
        final Path path = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getMediaPath("maps"), new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            return list;
        }
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, filter);
            try {
                for (final Path path2 : directoryStream) {
                    final Region e = new Region();
                    e.name = path2.getFileName().toString();
                    e.file = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.name);
                    list.add(e);
                }
                if (directoryStream != null) {
                    directoryStream.close();
                }
            }
            catch (Throwable t) {
                if (directoryStream != null) {
                    try {
                        directoryStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<Profession> getDefaultServerPoints() {
        final ArrayList<Profession> list = new ArrayList<Profession>();
        final Profession e = new Profession();
        e.name = "unemployed";
        e.points = new ArrayList<Point>();
        list.add(e);
        final Point e2 = new Point();
        e2.worldX = 40;
        e2.worldY = 22;
        e2.posX = 67;
        e2.posY = 201;
        e2.posZ = 0;
        e.points.add(e2);
        return list;
    }
    
    public static class Point
    {
        public int worldX;
        public int worldY;
        public int posX;
        public int posY;
        public int posZ;
    }
    
    public static class Profession
    {
        public String name;
        public ArrayList<Point> points;
    }
    
    public static class Region
    {
        public String name;
        public String file;
        public String serverfile;
        public ArrayList<Profession> professions;
    }
}
