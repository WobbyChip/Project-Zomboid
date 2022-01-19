// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

public class RakVoice
{
    public static native void RVInit(final int p0);
    
    public static native void RVInitServer(final boolean p0, final int p1, final int p2, final int p3, final int p4, final float p5, final float p6, final boolean p7);
    
    public static native void RVDeinit();
    
    public static native int GetComplexity();
    
    public static native void SetComplexity(final int p0);
    
    public static native void RequestVoiceChannel(final long p0);
    
    public static native void CloseAllChannels();
    
    public static native int GetBufferSizeBytes();
    
    public static native boolean GetServerVOIPEnable();
    
    public static native int GetSampleRate();
    
    public static native int GetSendFramePeriod();
    
    public static native int GetBuffering();
    
    public static native float GetMinDistance();
    
    public static native float GetMaxDistance();
    
    public static native boolean GetIs3D();
    
    public static native void CloseVoiceChannel(final long p0);
    
    public static native boolean ReceiveFrame(final long p0, final byte[] p1);
    
    public static native void SendFrame(final long p0, final long p1, final byte[] p2, final long p3);
    
    public static native void SetLoopbackMode(final boolean p0);
    
    public static native void SetVoiceBan(final long p0, final boolean p1);
    
    public static native void SetPlayerCoordinate(final long p0, final float p1, final float p2, final float p3, final boolean p4);
}
