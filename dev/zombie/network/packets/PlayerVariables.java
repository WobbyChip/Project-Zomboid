// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;

public class PlayerVariables implements INetworkPacket
{
    byte count;
    NetworkPlayerVariable[] variables;
    
    public PlayerVariables() {
        this.count = 0;
        this.variables = new NetworkPlayerVariable[2];
        for (int i = 0; i < this.variables.length; i = (byte)(i + 1)) {
            this.variables[i] = new NetworkPlayerVariable();
        }
    }
    
    public void set(final IsoPlayer isoPlayer) {
        final String actionStateName = isoPlayer.getActionStateName();
        if (actionStateName.equals("idle")) {
            this.variables[0].set(isoPlayer, NetworkPlayerVariableIDs.IdleSpeed);
            this.count = 1;
        }
        else if (actionStateName.equals("maskingleft") || actionStateName.equals("maskingright") || actionStateName.equals("movement") || actionStateName.equals("run") || actionStateName.equals("sprint")) {
            this.variables[0].set(isoPlayer, NetworkPlayerVariableIDs.WalkInjury);
            this.variables[1].set(isoPlayer, NetworkPlayerVariableIDs.WalkSpeed);
            this.count = 2;
        }
    }
    
    public void apply(final IsoPlayer isoPlayer) {
        for (byte b = 0; b < this.count; ++b) {
            isoPlayer.setVariable(this.variables[b].id.name(), this.variables[b].value);
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.count = byteBuffer.get();
        for (byte b = 0; b < this.count; ++b) {
            this.variables[b].id = NetworkPlayerVariableIDs.values()[byteBuffer.get()];
            this.variables[b].value = byteBuffer.getFloat();
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte(this.count);
        for (byte b = 0; b < this.count; ++b) {
            byteBufferWriter.putByte((byte)this.variables[b].id.ordinal());
            byteBufferWriter.putFloat(this.variables[b].value);
        }
    }
    
    @Override
    public int getPacketSizeBytes() {
        return 1 + this.count * 5;
    }
    
    @Override
    public String getDescription() {
        String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;B)Ljava/lang/String;, "PlayerVariables: ", this.count);
        for (byte b = 0; b < this.count; ++b) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;F)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.variables[b].id.name()), this.variables[b].value);
        }
        return s;
    }
    
    public void copy(final PlayerVariables playerVariables) {
        this.count = playerVariables.count;
        for (byte b = 0; b < this.count; ++b) {
            this.variables[b].id = playerVariables.variables[b].id;
            this.variables[b].value = playerVariables.variables[b].value;
        }
    }
    
    private enum NetworkPlayerVariableIDs
    {
        IdleSpeed, 
        WalkInjury, 
        WalkSpeed, 
        DeltaX, 
        DeltaY, 
        AttackVariationX, 
        AttackVariationY, 
        targetDist, 
        autoShootVarX, 
        autoShootVarY, 
        recoilVarX, 
        recoilVarY, 
        ShoveAimX, 
        ShoveAimY;
        
        private static /* synthetic */ NetworkPlayerVariableIDs[] $values() {
            return new NetworkPlayerVariableIDs[] { NetworkPlayerVariableIDs.IdleSpeed, NetworkPlayerVariableIDs.WalkInjury, NetworkPlayerVariableIDs.WalkSpeed, NetworkPlayerVariableIDs.DeltaX, NetworkPlayerVariableIDs.DeltaY, NetworkPlayerVariableIDs.AttackVariationX, NetworkPlayerVariableIDs.AttackVariationY, NetworkPlayerVariableIDs.targetDist, NetworkPlayerVariableIDs.autoShootVarX, NetworkPlayerVariableIDs.autoShootVarY, NetworkPlayerVariableIDs.recoilVarX, NetworkPlayerVariableIDs.recoilVarY, NetworkPlayerVariableIDs.ShoveAimX, NetworkPlayerVariableIDs.ShoveAimY };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private class NetworkPlayerVariable
    {
        NetworkPlayerVariableIDs id;
        float value;
        
        public void set(final IsoPlayer isoPlayer, final NetworkPlayerVariableIDs id) {
            this.id = id;
            this.value = isoPlayer.getVariableFloat(id.name(), 0.0f);
        }
    }
}
