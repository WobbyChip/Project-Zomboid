// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.util.regex.Matcher;
import zombie.core.math.PZMath;
import java.util.regex.Pattern;
import java.util.Locale;

public final class GameVersion
{
    private final int m_major;
    private final int m_minor;
    private final String m_suffix;
    private final String m_string;
    
    public GameVersion(final int major, final int minor, final String suffix) {
        if (major < 0) {
            throw new IllegalArgumentException("major version must be greater than zero");
        }
        if (minor < 0 || minor > 999) {
            throw new IllegalArgumentException("minor version must be from 0 to 999");
        }
        this.m_major = major;
        this.m_minor = minor;
        this.m_suffix = suffix;
        this.m_string = String.format(Locale.ENGLISH, "%d.%d%s", this.m_major, this.m_minor, (this.m_suffix == null) ? "" : this.m_suffix);
    }
    
    public int getMajor() {
        return this.m_major;
    }
    
    public int getMinor() {
        return this.m_minor;
    }
    
    public String getSuffix() {
        return this.m_suffix;
    }
    
    public int getInt() {
        return this.m_major * 1000 + this.m_minor;
    }
    
    public boolean isGreaterThan(final GameVersion gameVersion) {
        return this.getInt() > gameVersion.getInt();
    }
    
    public boolean isGreaterThanOrEqualTo(final GameVersion gameVersion) {
        return this.getInt() >= gameVersion.getInt();
    }
    
    public boolean isLessThan(final GameVersion gameVersion) {
        return this.getInt() < gameVersion.getInt();
    }
    
    public boolean isLessThanOrEqualTo(final GameVersion gameVersion) {
        return this.getInt() <= gameVersion.getInt();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof GameVersion) {
            final GameVersion gameVersion = (GameVersion)o;
            return gameVersion.m_major == this.m_major && gameVersion.m_minor == this.m_minor;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.m_string;
    }
    
    public static GameVersion parse(final String input) {
        final Matcher matcher = Pattern.compile("([0-9]+)\\.([0-9]+)(.*)").matcher(input);
        if (matcher.matches()) {
            return new GameVersion(PZMath.tryParseInt(matcher.group(1), 0), PZMath.tryParseInt(matcher.group(2), 0), matcher.group(3));
        }
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, input));
    }
}
