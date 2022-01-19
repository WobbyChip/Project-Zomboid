// 
// Decompiled by Procyon v0.5.36
// 

package zombie.spnetwork;

import java.nio.ByteBuffer;

public abstract class UdpEngine
{
    public abstract void Send(final ByteBuffer p0);
    
    public abstract void Receive(final ByteBuffer p0);
}
