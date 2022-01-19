// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.io.OutputStream;
import java.io.PrintStream;

public final class ProxyPrintStream extends PrintStream
{
    private PrintStream fileStream;
    private PrintStream systemStream;
    
    public ProxyPrintStream(final PrintStream printStream, final PrintStream fileStream) {
        super(printStream);
        this.fileStream = null;
        this.systemStream = null;
        this.systemStream = printStream;
        this.fileStream = fileStream;
    }
    
    @Override
    public void print(final String s) {
        this.systemStream.print(s);
        this.fileStream.print(s);
        this.fileStream.flush();
    }
    
    @Override
    public void println(final String s) {
        this.systemStream.println(s);
        this.fileStream.println(s);
        this.fileStream.flush();
    }
    
    @Override
    public void println(final Object o) {
        this.systemStream.println(o);
        this.fileStream.println(o);
        this.fileStream.flush();
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) {
        this.systemStream.write(array, n, n2);
        this.fileStream.write(array, n, n2);
        this.fileStream.flush();
    }
    
    @Override
    public void flush() {
        this.systemStream.flush();
        this.fileStream.flush();
    }
}
