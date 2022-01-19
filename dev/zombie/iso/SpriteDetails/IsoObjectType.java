// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.SpriteDetails;

import java.util.HashMap;

public enum IsoObjectType
{
    normal(0), 
    jukebox(1), 
    wall(2), 
    stairsTW(3), 
    stairsTN(4), 
    stairsMW(5), 
    stairsMN(6), 
    stairsBW(7), 
    stairsBN(8), 
    UNUSED9(9), 
    UNUSED10(10), 
    doorW(11), 
    doorN(12), 
    lightswitch(13), 
    radio(14), 
    curtainN(15), 
    curtainS(16), 
    curtainW(17), 
    curtainE(18), 
    doorFrW(19), 
    doorFrN(20), 
    tree(21), 
    windowFN(22), 
    windowFW(23), 
    UNUSED24(24), 
    WestRoofB(25), 
    WestRoofM(26), 
    WestRoofT(27), 
    isMoveAbleObject(28), 
    MAX(29);
    
    private final int index;
    private static final HashMap<String, IsoObjectType> fromStringMap;
    
    private IsoObjectType(final int index) {
        this.index = index;
    }
    
    public int index() {
        return this.index;
    }
    
    public static IsoObjectType fromIndex(final int n) {
        return IsoObjectType.class.getEnumConstants()[n];
    }
    
    public static IsoObjectType FromString(final String key) {
        final IsoObjectType isoObjectType = IsoObjectType.fromStringMap.get(key);
        return (isoObjectType == null) ? IsoObjectType.MAX : isoObjectType;
    }
    
    private static /* synthetic */ IsoObjectType[] $values() {
        return new IsoObjectType[] { IsoObjectType.normal, IsoObjectType.jukebox, IsoObjectType.wall, IsoObjectType.stairsTW, IsoObjectType.stairsTN, IsoObjectType.stairsMW, IsoObjectType.stairsMN, IsoObjectType.stairsBW, IsoObjectType.stairsBN, IsoObjectType.UNUSED9, IsoObjectType.UNUSED10, IsoObjectType.doorW, IsoObjectType.doorN, IsoObjectType.lightswitch, IsoObjectType.radio, IsoObjectType.curtainN, IsoObjectType.curtainS, IsoObjectType.curtainW, IsoObjectType.curtainE, IsoObjectType.doorFrW, IsoObjectType.doorFrN, IsoObjectType.tree, IsoObjectType.windowFN, IsoObjectType.windowFW, IsoObjectType.UNUSED24, IsoObjectType.WestRoofB, IsoObjectType.WestRoofM, IsoObjectType.WestRoofT, IsoObjectType.isMoveAbleObject, IsoObjectType.MAX };
    }
    
    static {
        $VALUES = $values();
        fromStringMap = new HashMap<String, IsoObjectType>();
        for (final IsoObjectType value : values()) {
            if (value == IsoObjectType.MAX) {
                break;
            }
            IsoObjectType.fromStringMap.put(value.name(), value);
        }
    }
}
