// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.SpriteDetails;

import java.util.HashMap;

public enum IsoFlagType
{
    collideW(0), 
    collideN(1), 
    solidfloor(2), 
    noStart(3), 
    windowW(4), 
    windowN(5), 
    hidewalls(6), 
    exterior(7), 
    NoWallLighting(8), 
    doorW(9), 
    doorN(10), 
    transparentW(11), 
    transparentN(12), 
    WallOverlay(13), 
    FloorOverlay(14), 
    vegitation(15), 
    burning(16), 
    burntOut(17), 
    unflamable(18), 
    cutW(19), 
    cutN(20), 
    tableN(21), 
    tableNW(22), 
    tableW(23), 
    tableSW(24), 
    tableS(25), 
    tableSE(26), 
    tableE(27), 
    tableNE(28), 
    halfheight(29), 
    HasRainSplashes(30), 
    HasRaindrop(31), 
    solid(32), 
    trans(33), 
    pushable(34), 
    solidtrans(35), 
    invisible(36), 
    floorS(37), 
    floorE(38), 
    shelfS(39), 
    shelfE(40), 
    alwaysDraw(41), 
    ontable(42), 
    transparentFloor(43), 
    climbSheetW(44), 
    climbSheetN(45), 
    climbSheetTopN(46), 
    climbSheetTopW(47), 
    attachtostairs(48), 
    sheetCurtains(49), 
    waterPiped(50), 
    HoppableN(51), 
    HoppableW(52), 
    bed(53), 
    blueprint(54), 
    canPathW(55), 
    canPathN(56), 
    blocksight(57), 
    climbSheetE(58), 
    climbSheetS(59), 
    climbSheetTopE(60), 
    climbSheetTopS(61), 
    makeWindowInvincible(62), 
    water(63), 
    canBeCut(64), 
    canBeRemoved(65), 
    taintedWater(66), 
    smoke(67), 
    attachedN(68), 
    attachedS(69), 
    attachedE(70), 
    attachedW(71), 
    attachedFloor(72), 
    attachedSurface(73), 
    attachedCeiling(74), 
    attachedNW(75), 
    ForceAmbient(76), 
    WallSE(77), 
    WindowN(78), 
    WindowW(79), 
    FloorHeightOneThird(80), 
    FloorHeightTwoThirds(81), 
    CantClimb(82), 
    diamondFloor(83), 
    attachedSE(84), 
    TallHoppableW(85), 
    WallWTrans(86), 
    TallHoppableN(87), 
    WallNTrans(88), 
    container(89), 
    DoorWallW(90), 
    DoorWallN(91), 
    WallW(92), 
    WallN(93), 
    WallNW(94), 
    SpearOnlyAttackThrough(95), 
    forceRender(96), 
    MAX(97);
    
    private final int index;
    private static final IsoFlagType[] EnumConstants;
    private static final HashMap<String, IsoFlagType> fromStringMap;
    
    private IsoFlagType(final int index) {
        this.index = index;
    }
    
    public int index() {
        return this.index;
    }
    
    public static IsoFlagType fromIndex(final int n) {
        return IsoFlagType.EnumConstants[n];
    }
    
    public static IsoFlagType FromString(final String key) {
        final IsoFlagType isoFlagType = IsoFlagType.fromStringMap.get(key);
        return (isoFlagType == null) ? IsoFlagType.MAX : isoFlagType;
    }
    
    private static /* synthetic */ IsoFlagType[] $values() {
        return new IsoFlagType[] { IsoFlagType.collideW, IsoFlagType.collideN, IsoFlagType.solidfloor, IsoFlagType.noStart, IsoFlagType.windowW, IsoFlagType.windowN, IsoFlagType.hidewalls, IsoFlagType.exterior, IsoFlagType.NoWallLighting, IsoFlagType.doorW, IsoFlagType.doorN, IsoFlagType.transparentW, IsoFlagType.transparentN, IsoFlagType.WallOverlay, IsoFlagType.FloorOverlay, IsoFlagType.vegitation, IsoFlagType.burning, IsoFlagType.burntOut, IsoFlagType.unflamable, IsoFlagType.cutW, IsoFlagType.cutN, IsoFlagType.tableN, IsoFlagType.tableNW, IsoFlagType.tableW, IsoFlagType.tableSW, IsoFlagType.tableS, IsoFlagType.tableSE, IsoFlagType.tableE, IsoFlagType.tableNE, IsoFlagType.halfheight, IsoFlagType.HasRainSplashes, IsoFlagType.HasRaindrop, IsoFlagType.solid, IsoFlagType.trans, IsoFlagType.pushable, IsoFlagType.solidtrans, IsoFlagType.invisible, IsoFlagType.floorS, IsoFlagType.floorE, IsoFlagType.shelfS, IsoFlagType.shelfE, IsoFlagType.alwaysDraw, IsoFlagType.ontable, IsoFlagType.transparentFloor, IsoFlagType.climbSheetW, IsoFlagType.climbSheetN, IsoFlagType.climbSheetTopN, IsoFlagType.climbSheetTopW, IsoFlagType.attachtostairs, IsoFlagType.sheetCurtains, IsoFlagType.waterPiped, IsoFlagType.HoppableN, IsoFlagType.HoppableW, IsoFlagType.bed, IsoFlagType.blueprint, IsoFlagType.canPathW, IsoFlagType.canPathN, IsoFlagType.blocksight, IsoFlagType.climbSheetE, IsoFlagType.climbSheetS, IsoFlagType.climbSheetTopE, IsoFlagType.climbSheetTopS, IsoFlagType.makeWindowInvincible, IsoFlagType.water, IsoFlagType.canBeCut, IsoFlagType.canBeRemoved, IsoFlagType.taintedWater, IsoFlagType.smoke, IsoFlagType.attachedN, IsoFlagType.attachedS, IsoFlagType.attachedE, IsoFlagType.attachedW, IsoFlagType.attachedFloor, IsoFlagType.attachedSurface, IsoFlagType.attachedCeiling, IsoFlagType.attachedNW, IsoFlagType.ForceAmbient, IsoFlagType.WallSE, IsoFlagType.WindowN, IsoFlagType.WindowW, IsoFlagType.FloorHeightOneThird, IsoFlagType.FloorHeightTwoThirds, IsoFlagType.CantClimb, IsoFlagType.diamondFloor, IsoFlagType.attachedSE, IsoFlagType.TallHoppableW, IsoFlagType.WallWTrans, IsoFlagType.TallHoppableN, IsoFlagType.WallNTrans, IsoFlagType.container, IsoFlagType.DoorWallW, IsoFlagType.DoorWallN, IsoFlagType.WallW, IsoFlagType.WallN, IsoFlagType.WallNW, IsoFlagType.SpearOnlyAttackThrough, IsoFlagType.forceRender, IsoFlagType.MAX };
    }
    
    static {
        $VALUES = $values();
        EnumConstants = IsoFlagType.class.getEnumConstants();
        fromStringMap = new HashMap<String, IsoFlagType>();
        for (final IsoFlagType value : values()) {
            if (value == IsoFlagType.MAX) {
                break;
            }
            IsoFlagType.fromStringMap.put(value.name(), value);
        }
    }
}
