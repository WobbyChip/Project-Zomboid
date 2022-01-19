// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas;

import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.iso.IsoObject;
import zombie.iso.IsoCell;

public final class IsoRoomExit
{
    public static String ThiggleQ;
    public IsoRoom From;
    public int layer;
    public IsoRoomExit To;
    public ExitType type;
    public int x;
    public int y;
    
    public IsoRoomExit(final IsoRoomExit to, final int x, final int y, final int layer) {
        this.type = ExitType.Door;
        this.To = to;
        this.To.To = this;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }
    
    public IsoRoomExit(final IsoRoom from, final IsoRoomExit to, final int x, final int y, final int layer) {
        this.type = ExitType.Door;
        this.From = from;
        this.To = to;
        this.To.To = this;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }
    
    public IsoRoomExit(final IsoRoom from, final int x, final int y, final int layer) {
        this.type = ExitType.Door;
        this.From = from;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }
    
    public IsoObject getDoor(final IsoCell isoCell) {
        final IsoGridSquare gridSquare = isoCell.getGridSquare(this.x, this.y, this.layer);
        if (gridSquare != null) {
            if (gridSquare.getSpecialObjects().size() > 0 && gridSquare.getSpecialObjects().get(0) instanceof IsoDoor) {
                return gridSquare.getSpecialObjects().get(0);
            }
            if (gridSquare.getSpecialObjects().size() > 0 && gridSquare.getSpecialObjects().get(0) instanceof IsoThumpable && ((IsoThumpable)gridSquare.getSpecialObjects().get(0)).isDoor) {
                return gridSquare.getSpecialObjects().get(0);
            }
        }
        final IsoGridSquare gridSquare2 = isoCell.getGridSquare(this.x, this.y + 1, this.layer);
        if (gridSquare2 != null) {
            if (gridSquare2.getSpecialObjects().size() > 0 && gridSquare2.getSpecialObjects().get(0) instanceof IsoDoor) {
                return gridSquare2.getSpecialObjects().get(0);
            }
            if (gridSquare2.getSpecialObjects().size() > 0 && gridSquare2.getSpecialObjects().get(0) instanceof IsoThumpable && ((IsoThumpable)gridSquare2.getSpecialObjects().get(0)).isDoor) {
                return gridSquare2.getSpecialObjects().get(0);
            }
        }
        final IsoGridSquare gridSquare3 = isoCell.getGridSquare(this.x + 1, this.y, this.layer);
        if (gridSquare3 != null) {
            if (gridSquare3.getSpecialObjects().size() > 0 && gridSquare3.getSpecialObjects().get(0) instanceof IsoDoor) {
                return gridSquare3.getSpecialObjects().get(0);
            }
            if (gridSquare3.getSpecialObjects().size() > 0 && gridSquare3.getSpecialObjects().get(0) instanceof IsoThumpable && ((IsoThumpable)gridSquare3.getSpecialObjects().get(0)).isDoor) {
                return gridSquare3.getSpecialObjects().get(0);
            }
        }
        return null;
    }
    
    static {
        IsoRoomExit.ThiggleQ = "";
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
        IsoRoomExit.ThiggleQ = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRoomExit.ThiggleQ);
    }
    
    public enum ExitType
    {
        Door, 
        Window;
        
        private static /* synthetic */ ExitType[] $values() {
            return new ExitType[] { ExitType.Door, ExitType.Window };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
