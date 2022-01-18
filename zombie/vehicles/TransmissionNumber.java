// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

public enum TransmissionNumber
{
    R(-1), 
    N(0), 
    Speed1(1), 
    Speed2(2), 
    Speed3(3), 
    Speed4(4), 
    Speed5(5), 
    Speed6(6), 
    Speed7(7), 
    Speed8(8);
    
    private final int index;
    
    private TransmissionNumber(final int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public static TransmissionNumber fromIndex(final int n) {
        switch (n) {
            case -1: {
                return TransmissionNumber.R;
            }
            case 0: {
                return TransmissionNumber.N;
            }
            case 1: {
                return TransmissionNumber.Speed1;
            }
            case 2: {
                return TransmissionNumber.Speed2;
            }
            case 3: {
                return TransmissionNumber.Speed3;
            }
            case 4: {
                return TransmissionNumber.Speed4;
            }
            case 5: {
                return TransmissionNumber.Speed5;
            }
            case 6: {
                return TransmissionNumber.Speed6;
            }
            case 7: {
                return TransmissionNumber.Speed7;
            }
            case 8: {
                return TransmissionNumber.Speed8;
            }
            default: {
                return TransmissionNumber.N;
            }
        }
    }
    
    public TransmissionNumber getNext(final int n) {
        if (this.index == -1 || this.index == n) {
            return this;
        }
        return fromIndex(this.index + 1);
    }
    
    public TransmissionNumber getPrev(final int n) {
        if (this.index == -1 || this.index == n) {
            return this;
        }
        return fromIndex(this.index - 1);
    }
    
    public String getString() {
        switch (this.index) {
            case -1: {
                return "R";
            }
            case 0: {
                return "N";
            }
            case 1: {
                return "1";
            }
            case 2: {
                return "2";
            }
            case 3: {
                return "3";
            }
            case 4: {
                return "4";
            }
            case 5: {
                return "5";
            }
            case 6: {
                return "6";
            }
            case 7: {
                return "7";
            }
            case 8: {
                return "8";
            }
            default: {
                return "";
            }
        }
    }
    
    private static /* synthetic */ TransmissionNumber[] $values() {
        return new TransmissionNumber[] { TransmissionNumber.R, TransmissionNumber.N, TransmissionNumber.Speed1, TransmissionNumber.Speed2, TransmissionNumber.Speed3, TransmissionNumber.Speed4, TransmissionNumber.Speed5, TransmissionNumber.Speed6, TransmissionNumber.Speed7, TransmissionNumber.Speed8 };
    }
    
    static {
        $VALUES = $values();
    }
}
