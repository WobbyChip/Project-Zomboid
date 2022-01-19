// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite.shapers;

import zombie.core.Core;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import zombie.ZomboidFileSystem;
import zombie.DebugFileWatcher;
import zombie.util.PZXmlParserException;
import zombie.debug.LogSeverity;
import zombie.util.PZXmlUtil;
import java.io.File;
import zombie.debug.DebugLog;
import zombie.PredicatedFileWatcher;

public class SpritePaddingSettings
{
    private static Settings m_settings;
    private static String m_settingsFilePath;
    private static PredicatedFileWatcher m_fileWatcher;
    
    public static void settingsFileChanged(final Settings settings) {
        DebugLog.General.println("Settings file changed.");
        SpritePaddingSettings.m_settings = settings;
    }
    
    private static void loadSettings() {
        final String settingsFilePath = getSettingsFilePath();
        final File absoluteFile = new File(settingsFilePath).getAbsoluteFile();
        if (absoluteFile.isFile()) {
            try {
                SpritePaddingSettings.m_settings = PZXmlUtil.parse(Settings.class, absoluteFile.getPath());
            }
            catch (PZXmlParserException ex) {
                DebugLog.General.printException((Throwable)ex, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, settingsFilePath), LogSeverity.Warning);
                SpritePaddingSettings.m_settings = new Settings();
            }
        }
        else {
            SpritePaddingSettings.m_settings = new Settings();
            saveSettings();
        }
        if (SpritePaddingSettings.m_fileWatcher == null) {
            SpritePaddingSettings.m_fileWatcher = new PredicatedFileWatcher(settingsFilePath, (Class<T>)Settings.class, (PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T>)SpritePaddingSettings::settingsFileChanged);
            DebugFileWatcher.instance.add(SpritePaddingSettings.m_fileWatcher);
        }
    }
    
    private static String getSettingsFilePath() {
        if (SpritePaddingSettings.m_settingsFilePath == null) {
            SpritePaddingSettings.m_settingsFilePath = ZomboidFileSystem.instance.getLocalWorkDirSub("SpritePaddingSettings.xml");
        }
        return SpritePaddingSettings.m_settingsFilePath;
    }
    
    private static void saveSettings() {
        try {
            PZXmlUtil.write(SpritePaddingSettings.m_settings, new File(getSettingsFilePath()).getAbsoluteFile());
        }
        catch (TransformerException | IOException | JAXBException ex) {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    public static Settings getSettings() {
        if (SpritePaddingSettings.m_settings == null) {
            loadSettings();
        }
        return SpritePaddingSettings.m_settings;
    }
    
    static {
        SpritePaddingSettings.m_settings = null;
        SpritePaddingSettings.m_settingsFilePath = null;
        SpritePaddingSettings.m_fileWatcher = null;
    }
    
    @XmlRootElement(name = "FloorShaperDeDiamondSettings")
    public static class Settings
    {
        public SpritePadding.IsoPaddingSettings IsoPadding;
        public FloorShaperDeDiamond.Settings FloorDeDiamond;
        public FloorShaperAttachedSprites.Settings AttachedSprites;
        
        public Settings() {
            this.IsoPadding = new SpritePadding.IsoPaddingSettings();
            this.FloorDeDiamond = new FloorShaperDeDiamond.Settings();
            this.AttachedSprites = new FloorShaperAttachedSprites.Settings();
        }
    }
    
    public abstract static class GenericZoomBasedSettingGroup
    {
        public abstract <ZoomBasedSetting> ZoomBasedSetting getCurrentZoomSetting();
        
        public static <ZoomBasedSetting> ZoomBasedSetting getCurrentZoomSetting(final ZoomBasedSetting zoomBasedSetting, final ZoomBasedSetting zoomBasedSetting2, final ZoomBasedSetting zoomBasedSetting3) {
            final float currentPlayerZoom = Core.getInstance().getCurrentPlayerZoom();
            if (currentPlayerZoom < 1.0f) {
                return zoomBasedSetting;
            }
            if (currentPlayerZoom == 1.0f) {
                return zoomBasedSetting2;
            }
            return zoomBasedSetting3;
        }
    }
}
