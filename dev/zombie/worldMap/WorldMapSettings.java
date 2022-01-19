// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.ZomboidFileSystem;
import zombie.config.ConfigFile;
import zombie.config.DoubleConfigOption;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import java.util.ArrayList;

public final class WorldMapSettings
{
    public static int VERSION1;
    public static int VERSION;
    private static WorldMapSettings instance;
    final ArrayList<ConfigOption> m_options;
    final WorldMap mWorldMap;
    final MiniMap mMiniMap;
    private int m_readVersion;
    
    public WorldMapSettings() {
        this.m_options = new ArrayList<ConfigOption>();
        this.mWorldMap = new WorldMap();
        this.mMiniMap = new MiniMap();
        this.m_readVersion = 0;
    }
    
    public static WorldMapSettings getInstance() {
        if (WorldMapSettings.instance == null) {
            (WorldMapSettings.instance = new WorldMapSettings()).load();
        }
        return WorldMapSettings.instance;
    }
    
    private BooleanConfigOption newOption(final String s, final boolean b) {
        final BooleanConfigOption e = new BooleanConfigOption(s, b);
        this.m_options.add(e);
        return e;
    }
    
    private DoubleConfigOption newOption(final String s, final double n, final double n2, final double n3) {
        final DoubleConfigOption e = new DoubleConfigOption(s, n, n2, n3);
        this.m_options.add(e);
        return e;
    }
    
    public ConfigOption getOptionByName(final String anObject) {
        for (int i = 0; i < this.m_options.size(); ++i) {
            final ConfigOption configOption = this.m_options.get(i);
            if (configOption.getName().equals(anObject)) {
                return configOption;
            }
        }
        return null;
    }
    
    public int getOptionCount() {
        return this.m_options.size();
    }
    
    public ConfigOption getOptionByIndex(final int index) {
        return this.m_options.get(index);
    }
    
    public void setBoolean(final String s, final boolean value) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof BooleanConfigOption) {
            ((BooleanConfigOption)optionByName).setValue(value);
        }
    }
    
    public boolean getBoolean(final String s) {
        final ConfigOption optionByName = this.getOptionByName(s);
        return optionByName instanceof BooleanConfigOption && ((BooleanConfigOption)optionByName).getValue();
    }
    
    public void setDouble(final String s, final double value) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof DoubleConfigOption) {
            ((DoubleConfigOption)optionByName).setValue(value);
        }
    }
    
    public double getDouble(final String s, final double n) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof DoubleConfigOption) {
            return ((DoubleConfigOption)optionByName).getValue();
        }
        return n;
    }
    
    public int getFileVersion() {
        return this.m_readVersion;
    }
    
    public void save() {
        new ConfigFile().write(ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini"), WorldMapSettings.VERSION, this.m_options);
        this.m_readVersion = WorldMapSettings.VERSION;
    }
    
    public void load() {
        this.m_readVersion = 0;
        final String fileNameInCurrentSave = ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini");
        final ConfigFile configFile = new ConfigFile();
        if (!configFile.read(fileNameInCurrentSave)) {
            return;
        }
        this.m_readVersion = configFile.getVersion();
        if (this.m_readVersion < WorldMapSettings.VERSION1 || this.m_readVersion > WorldMapSettings.VERSION) {
            return;
        }
        for (int i = 0; i < configFile.getOptions().size(); ++i) {
            final ConfigOption configOption = configFile.getOptions().get(i);
            try {
                final ConfigOption optionByName = this.getOptionByName(configOption.getName());
                if (optionByName != null) {
                    optionByName.parse(configOption.getValueAsString());
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public static void Reset() {
        if (WorldMapSettings.instance == null) {
            return;
        }
        WorldMapSettings.instance.m_options.clear();
        WorldMapSettings.instance = null;
    }
    
    static {
        WorldMapSettings.VERSION1 = 1;
        WorldMapSettings.VERSION = WorldMapSettings.VERSION1;
    }
    
    public final class WorldMap
    {
        public DoubleConfigOption CenterX;
        public DoubleConfigOption CenterY;
        public DoubleConfigOption Zoom;
        public BooleanConfigOption Isometric;
        public BooleanConfigOption ShowSymbolsUI;
        
        public WorldMap() {
            this.CenterX = WorldMapSettings.this.newOption("WorldMap.CenterX", -1.7976931348623157E308, Double.MAX_VALUE, 0.0);
            this.CenterY = WorldMapSettings.this.newOption("WorldMap.CenterY", -1.7976931348623157E308, Double.MAX_VALUE, 0.0);
            this.Zoom = WorldMapSettings.this.newOption("WorldMap.Zoom", 0.0, 24.0, 0.0);
            this.Isometric = WorldMapSettings.this.newOption("WorldMap.Isometric", true);
            this.ShowSymbolsUI = WorldMapSettings.this.newOption("WorldMap.ShowSymbolsUI", true);
        }
    }
    
    public class MiniMap
    {
        public DoubleConfigOption Zoom;
        public BooleanConfigOption Isometric;
        public BooleanConfigOption ShowSymbols;
        public BooleanConfigOption StartVisible;
        
        public MiniMap() {
            this.Zoom = WorldMapSettings.this.newOption("MiniMap.Zoom", 0.0, 24.0, 19.0);
            this.Isometric = WorldMapSettings.this.newOption("MiniMap.Isometric", true);
            this.ShowSymbols = WorldMapSettings.this.newOption("MiniMap.ShowSymbols", false);
            this.StartVisible = WorldMapSettings.this.newOption("MiniMap.StartVisible", true);
        }
    }
}
