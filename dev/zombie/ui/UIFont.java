// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

public enum UIFont
{
    Small, 
    Medium, 
    Large, 
    Massive, 
    MainMenu1, 
    MainMenu2, 
    Cred1, 
    Cred2, 
    NewSmall, 
    NewMedium, 
    NewLarge, 
    Code, 
    MediumNew, 
    AutoNormSmall, 
    AutoNormMedium, 
    AutoNormLarge, 
    Dialogue, 
    Intro, 
    Handwritten, 
    DebugConsole, 
    Title;
    
    public static UIFont FromString(final String s) {
        try {
            return valueOf(s);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    private static /* synthetic */ UIFont[] $values() {
        return new UIFont[] { UIFont.Small, UIFont.Medium, UIFont.Large, UIFont.Massive, UIFont.MainMenu1, UIFont.MainMenu2, UIFont.Cred1, UIFont.Cred2, UIFont.NewSmall, UIFont.NewMedium, UIFont.NewLarge, UIFont.Code, UIFont.MediumNew, UIFont.AutoNormSmall, UIFont.AutoNormMedium, UIFont.AutoNormLarge, UIFont.Dialogue, UIFont.Intro, UIFont.Handwritten, UIFont.DebugConsole, UIFont.Title };
    }
    
    static {
        $VALUES = $values();
    }
}
