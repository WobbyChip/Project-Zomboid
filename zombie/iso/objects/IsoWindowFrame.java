// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.network.GameServer;
import zombie.characters.IsoGameCharacter;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoPlayer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoObject;

public class IsoWindowFrame
{
    private static Direction getDirection(final IsoObject isoObject) {
        if (isoObject instanceof IsoWindow || isoObject instanceof IsoThumpable) {
            return Direction.INVALID;
        }
        if (isoObject == null || isoObject.getProperties() == null || isoObject.getObjectIndex() == -1) {
            return Direction.INVALID;
        }
        if (isoObject.getProperties().Is(IsoFlagType.WindowN)) {
            return Direction.NORTH;
        }
        if (isoObject.getProperties().Is(IsoFlagType.WindowW)) {
            return Direction.WEST;
        }
        return Direction.INVALID;
    }
    
    public static boolean isWindowFrame(final IsoObject isoObject) {
        return getDirection(isoObject).isValid();
    }
    
    public static boolean isWindowFrame(final IsoObject isoObject, final boolean b) {
        final Direction direction = getDirection(isoObject);
        return (b && direction == Direction.NORTH) || (!b && direction == Direction.WEST);
    }
    
    public static int countAddSheetRope(final IsoObject isoObject) {
        final Direction direction = getDirection(isoObject);
        return direction.isValid() ? IsoWindow.countAddSheetRope(isoObject.getSquare(), direction == Direction.NORTH) : 0;
    }
    
    public static boolean canAddSheetRope(final IsoObject isoObject) {
        final Direction direction = getDirection(isoObject);
        return direction.isValid() && IsoWindow.canAddSheetRope(isoObject.getSquare(), direction == Direction.NORTH);
    }
    
    public static boolean haveSheetRope(final IsoObject isoObject) {
        final Direction direction = getDirection(isoObject);
        return direction.isValid() && IsoWindow.isTopOfSheetRopeHere(isoObject.getSquare(), direction == Direction.NORTH);
    }
    
    public static boolean addSheetRope(final IsoObject isoObject, final IsoPlayer isoPlayer, final String s) {
        return canAddSheetRope(isoObject) && IsoWindow.addSheetRope(isoPlayer, isoObject.getSquare(), getDirection(isoObject) == Direction.NORTH, s);
    }
    
    public static boolean removeSheetRope(final IsoObject isoObject, final IsoPlayer isoPlayer) {
        return haveSheetRope(isoObject) && IsoWindow.removeSheetRope(isoPlayer, isoObject.getSquare(), getDirection(isoObject) == Direction.NORTH);
    }
    
    public static IsoGridSquare getOppositeSquare(final IsoObject isoObject) {
        final Direction direction = getDirection(isoObject);
        if (!direction.isValid()) {
            return null;
        }
        return isoObject.getSquare().getAdjacentSquare((direction == Direction.NORTH) ? IsoDirections.N : IsoDirections.W);
    }
    
    public static IsoGridSquare getIndoorSquare(final IsoObject isoObject) {
        if (!getDirection(isoObject).isValid()) {
            return null;
        }
        final IsoGridSquare square = isoObject.getSquare();
        if (square.getRoom() != null) {
            return square;
        }
        final IsoGridSquare oppositeSquare = getOppositeSquare(isoObject);
        if (oppositeSquare != null && oppositeSquare.getRoom() != null) {
            return oppositeSquare;
        }
        return null;
    }
    
    public static IsoCurtain getCurtain(final IsoObject isoObject) {
        final Direction direction = getDirection(isoObject);
        if (!direction.isValid()) {
            return null;
        }
        final boolean b = direction == Direction.NORTH;
        final IsoCurtain curtain = isoObject.getSquare().getCurtain(b ? IsoObjectType.curtainN : IsoObjectType.curtainW);
        if (curtain != null) {
            return curtain;
        }
        final IsoGridSquare oppositeSquare = getOppositeSquare(isoObject);
        return (oppositeSquare == null) ? null : oppositeSquare.getCurtain(b ? IsoObjectType.curtainS : IsoObjectType.curtainE);
    }
    
    public static IsoGridSquare getAddSheetSquare(final IsoObject isoObject, final IsoGameCharacter isoGameCharacter) {
        final Direction direction = getDirection(isoObject);
        if (!direction.isValid()) {
            return null;
        }
        final boolean b = direction == Direction.NORTH;
        if (isoGameCharacter == null || isoGameCharacter.getCurrentSquare() == null) {
            return null;
        }
        final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
        final IsoGridSquare square = isoObject.getSquare();
        if (b) {
            if (currentSquare.getY() < square.getY()) {
                return square.getAdjacentSquare(IsoDirections.N);
            }
        }
        else if (currentSquare.getX() < square.getX()) {
            return square.getAdjacentSquare(IsoDirections.W);
        }
        return square;
    }
    
    public static void addSheet(final IsoObject isoObject, final IsoGameCharacter isoGameCharacter) {
        final Direction direction = getDirection(isoObject);
        if (!direction.isValid()) {
            return;
        }
        final boolean b = direction == Direction.NORTH;
        IsoGridSquare isoGridSquare = getIndoorSquare(isoObject);
        if (isoGridSquare == null) {
            isoGridSquare = isoObject.getSquare();
        }
        if (isoGameCharacter != null) {
            isoGridSquare = getAddSheetSquare(isoObject, isoGameCharacter);
        }
        if (isoGridSquare == null) {
            return;
        }
        IsoObjectType isoObjectType;
        if (isoGridSquare == isoObject.getSquare()) {
            isoObjectType = (b ? IsoObjectType.curtainN : IsoObjectType.curtainW);
        }
        else {
            isoObjectType = (b ? IsoObjectType.curtainS : IsoObjectType.curtainE);
        }
        if (isoGridSquare.getCurtain(isoObjectType) != null) {
            return;
        }
        int n = 16;
        if (isoObjectType == IsoObjectType.curtainE) {
            ++n;
        }
        if (isoObjectType == IsoObjectType.curtainS) {
            n += 3;
        }
        if (isoObjectType == IsoObjectType.curtainN) {
            n += 2;
        }
        n += 4;
        final IsoCurtain isoCurtain = new IsoCurtain(isoObject.getCell(), isoGridSquare, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n), b);
        isoGridSquare.AddSpecialTileObject(isoCurtain);
        if (GameServer.bServer) {
            isoCurtain.transmitCompleteItemToClients();
            if (isoGameCharacter != null) {
                isoGameCharacter.sendObjectChange("removeOneOf", new Object[] { "type", "Sheet" });
            }
        }
        else if (isoGameCharacter != null) {
            isoGameCharacter.getInventory().RemoveOneOf("Sheet");
        }
    }
    
    public static boolean canClimbThrough(final IsoObject isoObject, final IsoGameCharacter isoGameCharacter) {
        final Direction direction = getDirection(isoObject);
        if (!direction.isValid()) {
            return false;
        }
        if (isoObject.getSquare() == null) {
            return false;
        }
        final IsoWindow window = isoObject.getSquare().getWindow(direction == Direction.NORTH);
        return (window == null || !window.isBarricaded()) && (isoGameCharacter == null || IsoWindow.canClimbThroughHelper(isoGameCharacter, isoObject.getSquare(), (direction == Direction.NORTH) ? isoObject.getSquare().nav[IsoDirections.N.index()] : isoObject.getSquare().nav[IsoDirections.W.index()], direction == Direction.NORTH));
    }
    
    private enum Direction
    {
        INVALID, 
        NORTH, 
        WEST;
        
        public boolean isValid() {
            return this != Direction.INVALID;
        }
        
        private static /* synthetic */ Direction[] $values() {
            return new Direction[] { Direction.INVALID, Direction.NORTH, Direction.WEST };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
