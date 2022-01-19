// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import zombie.core.logger.ExceptionLogger;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.function.Predicate;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.util.Lambda;
import java.util.Iterator;
import java.io.File;
import zombie.gameStates.ChooseGameInfo;
import zombie.ZomboidFileSystem;
import java.util.ArrayList;

public final class Languages
{
    public static final Languages instance;
    private final ArrayList<Language> m_languages;
    private Language m_defaultLanguage;
    
    public Languages() {
        this.m_languages = new ArrayList<Language>();
        this.m_defaultLanguage = new Language(0, "EN", "English", "UTF-8", null, false);
        this.m_languages.add(this.m_defaultLanguage);
    }
    
    public void init() {
        this.m_languages.clear();
        this.m_defaultLanguage = new Language(0, "EN", "English", "UTF-8", null, false);
        this.m_languages.add(this.m_defaultLanguage);
        this.loadTranslateDirectory(ZomboidFileSystem.instance.getMediaPath("lua/shared/Translate"));
        final Iterator<String> iterator = ZomboidFileSystem.instance.getModIDs().iterator();
        while (iterator.hasNext()) {
            final ChooseGameInfo.Mod availableModDetails = ChooseGameInfo.getAvailableModDetails(iterator.next());
            if (availableModDetails == null) {
                continue;
            }
            final File file = new File(availableModDetails.getDir(), "media/lua/shared/Translate");
            if (!file.isDirectory()) {
                continue;
            }
            this.loadTranslateDirectory(file.getAbsolutePath());
        }
    }
    
    public Language getDefaultLanguage() {
        return this.m_defaultLanguage;
    }
    
    public int getNumLanguages() {
        return this.m_languages.size();
    }
    
    public Language getByIndex(final int index) {
        if (index >= 0 && index < this.m_languages.size()) {
            return this.m_languages.get(index);
        }
        return null;
    }
    
    public Language getByName(final String s) {
        return PZArrayUtil.find(this.m_languages, (Predicate<Language>)Lambda.predicate(s, (language, anotherString) -> language.name().equalsIgnoreCase(anotherString)));
    }
    
    public int getIndexByName(final String s) {
        return PZArrayUtil.indexOf(this.m_languages, (Predicate<Language>)Lambda.predicate(s, (language, anotherString) -> language.name().equalsIgnoreCase(anotherString)));
    }
    
    private void loadTranslateDirectory(final String s) {
        final DirectoryStream.Filter<? super Path> filter = path -> Files.isDirectory(path, new LinkOption[0]) && Files.exists(path.resolve("language.txt"), new LinkOption[0]);
        final Path path2 = FileSystems.getDefault().getPath(s, new String[0]);
        if (!Files.exists(path2, new LinkOption[0])) {
            return;
        }
        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path2, filter);
            try {
                final Iterator<Path> iterator = directoryStream.iterator();
                while (iterator.hasNext()) {
                    final LanguageFileData loadLanguageDirectory = this.loadLanguageDirectory(iterator.next().toAbsolutePath());
                    if (loadLanguageDirectory == null) {
                        continue;
                    }
                    final int indexByName = this.getIndexByName(loadLanguageDirectory.name);
                    if (indexByName == -1) {
                        this.m_languages.add(new Language(this.m_languages.size(), loadLanguageDirectory.name, loadLanguageDirectory.text, loadLanguageDirectory.charset, loadLanguageDirectory.base, loadLanguageDirectory.azerty));
                    }
                    else {
                        final Language language = new Language(indexByName, loadLanguageDirectory.name, loadLanguageDirectory.text, loadLanguageDirectory.charset, loadLanguageDirectory.base, loadLanguageDirectory.azerty);
                        this.m_languages.set(indexByName, language);
                        if (!loadLanguageDirectory.name.equals(this.m_defaultLanguage.name())) {
                            continue;
                        }
                        this.m_defaultLanguage = language;
                    }
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
            ExceptionLogger.logException(ex);
        }
    }
    
    private LanguageFileData loadLanguageDirectory(final Path path) {
        final String string = path.getFileName().toString();
        final LanguageFileData languageFileData = new LanguageFileData();
        languageFileData.name = string;
        if (!new LanguageFile().read(path.resolve("language.txt").toString(), languageFileData)) {
            return null;
        }
        return languageFileData;
    }
    
    static {
        instance = new Languages();
    }
}
