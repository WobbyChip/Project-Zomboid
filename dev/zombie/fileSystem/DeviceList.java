// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;

public final class DeviceList
{
    private final IFileDevice[] m_devices;
    
    public DeviceList() {
        this.m_devices = new IFileDevice[8];
    }
    
    public void add(final IFileDevice fileDevice) {
        for (int i = 0; i < this.m_devices.length; ++i) {
            if (this.m_devices[i] == null) {
                this.m_devices[i] = fileDevice;
                break;
            }
        }
    }
    
    public IFile createFile() {
        IFile file = null;
        for (int n = 0; n < this.m_devices.length && this.m_devices[n] != null; ++n) {
            file = this.m_devices[n].createFile(file);
        }
        return file;
    }
    
    public InputStream createStream(final String s) throws IOException {
        InputStream stream = null;
        for (int n = 0; n < this.m_devices.length && this.m_devices[n] != null; ++n) {
            stream = this.m_devices[n].createStream(s, stream);
        }
        return stream;
    }
}
