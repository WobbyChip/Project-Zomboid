// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.utils;

import java.lang.ref.PhantomReference;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.nio.DoubleBuffer;
import java.nio.ShortBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.Buffer;
import java.lang.ref.ReferenceQueue;

public final class BufferUtils
{
    private static boolean trackDirectMemory;
    private static final ReferenceQueue<Buffer> removeCollected;
    private static final ConcurrentHashMap<BufferInfo, BufferInfo> trackedBuffers;
    static ClearReferences cleanupThread;
    private static final AtomicBoolean loadedMethods;
    private static Method cleanerMethod;
    private static Method cleanMethod;
    private static Method viewedBufferMethod;
    private static Method freeMethod;
    
    public static void setTrackDirectMemoryEnabled(final boolean trackDirectMemory) {
        BufferUtils.trackDirectMemory = trackDirectMemory;
    }
    
    private static void onBufferAllocated(final Buffer buffer) {
        if (BufferUtils.trackDirectMemory) {
            if (BufferUtils.cleanupThread == null) {
                (BufferUtils.cleanupThread = new ClearReferences()).start();
            }
            if (buffer instanceof ByteBuffer) {
                final BufferInfo bufferInfo = new BufferInfo(ByteBuffer.class, buffer.capacity(), buffer, BufferUtils.removeCollected);
                BufferUtils.trackedBuffers.put(bufferInfo, bufferInfo);
            }
            else if (buffer instanceof FloatBuffer) {
                final BufferInfo bufferInfo2 = new BufferInfo(FloatBuffer.class, buffer.capacity() * 4, buffer, BufferUtils.removeCollected);
                BufferUtils.trackedBuffers.put(bufferInfo2, bufferInfo2);
            }
            else if (buffer instanceof IntBuffer) {
                final BufferInfo bufferInfo3 = new BufferInfo(IntBuffer.class, buffer.capacity() * 4, buffer, BufferUtils.removeCollected);
                BufferUtils.trackedBuffers.put(bufferInfo3, bufferInfo3);
            }
            else if (buffer instanceof ShortBuffer) {
                final BufferInfo bufferInfo4 = new BufferInfo(ShortBuffer.class, buffer.capacity() * 2, buffer, BufferUtils.removeCollected);
                BufferUtils.trackedBuffers.put(bufferInfo4, bufferInfo4);
            }
            else if (buffer instanceof DoubleBuffer) {
                final BufferInfo bufferInfo5 = new BufferInfo(DoubleBuffer.class, buffer.capacity() * 8, buffer, BufferUtils.removeCollected);
                BufferUtils.trackedBuffers.put(bufferInfo5, bufferInfo5);
            }
        }
    }
    
    public static void printCurrentDirectMemory(StringBuilder sb) {
        long n = 0L;
        final long n2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        final boolean b = sb == null;
        if (sb == null) {
            sb = new StringBuilder();
        }
        if (BufferUtils.trackDirectMemory) {
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int m = 0;
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            int n7 = 0;
            for (final BufferInfo bufferInfo : BufferUtils.trackedBuffers.values()) {
                if (bufferInfo.type == ByteBuffer.class) {
                    n += bufferInfo.size;
                    n4 += bufferInfo.size;
                    ++j;
                }
                else if (bufferInfo.type == FloatBuffer.class) {
                    n += bufferInfo.size;
                    n3 += bufferInfo.size;
                    ++i;
                }
                else if (bufferInfo.type == IntBuffer.class) {
                    n += bufferInfo.size;
                    n5 += bufferInfo.size;
                    ++k;
                }
                else if (bufferInfo.type == ShortBuffer.class) {
                    n += bufferInfo.size;
                    n6 += bufferInfo.size;
                    ++l;
                }
                else {
                    if (bufferInfo.type != DoubleBuffer.class) {
                        continue;
                    }
                    n += bufferInfo.size;
                    n7 += bufferInfo.size;
                    ++m;
                }
            }
            sb.append("Existing buffers: ").append(BufferUtils.trackedBuffers.size()).append("\n");
            sb.append("(b: ").append(j).append("  f: ").append(i).append("  i: ").append(k).append("  s: ").append(l).append("  d: ").append(m).append(")").append("\n");
            sb.append("Total   heap memory held: ").append(n2 / 1024L).append("kb\n");
            sb.append("Total direct memory held: ").append(n / 1024L).append("kb\n");
            sb.append("(b: ").append(n4 / 1024).append("kb  f: ").append(n3 / 1024).append("kb  i: ").append(n5 / 1024).append("kb  s: ").append(n6 / 1024).append("kb  d: ").append(n7 / 1024).append("kb)").append("\n");
        }
        else {
            sb.append("Total   heap memory held: ").append(n2 / 1024L).append("kb\n");
            sb.append("Only heap memory available, if you want to monitor direct memory use BufferUtils.setTrackDirectMemoryEnabled(true) during initialization.").append("\n");
        }
        if (b) {
            System.out.println(sb.toString());
        }
    }
    
    private static Method loadMethod(final String className, final String name) {
        try {
            final Method method = Class.forName(className).getMethod(name, (Class<?>[])new Class[0]);
            method.setAccessible(true);
            return method;
        }
        catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
            return null;
        }
    }
    
    public static ByteBuffer createByteBuffer(final int capacity) {
        final ByteBuffer order = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
        order.clear();
        onBufferAllocated(order);
        return order;
    }
    
    private static void loadCleanerMethods() {
        if (BufferUtils.loadedMethods.getAndSet(true)) {
            return;
        }
        synchronized (BufferUtils.loadedMethods) {
            BufferUtils.cleanerMethod = loadMethod("sun.nio.ch.DirectBuffer", "cleaner");
            BufferUtils.viewedBufferMethod = loadMethod("sun.nio.ch.DirectBuffer", "viewedBuffer");
            if (BufferUtils.viewedBufferMethod == null) {
                BufferUtils.viewedBufferMethod = loadMethod("sun.nio.ch.DirectBuffer", "attachment");
            }
            final Class<? extends ByteBuffer> class1 = createByteBuffer(1).getClass();
            try {
                BufferUtils.freeMethod = class1.getMethod("free", (Class<?>[])new Class[0]);
            }
            catch (NoSuchMethodException ex) {}
            catch (SecurityException ex2) {}
        }
    }
    
    public static void destroyDirectBuffer(final Buffer buffer) {
        if (!buffer.isDirect()) {
            return;
        }
        loadCleanerMethods();
        try {
            if (BufferUtils.freeMethod != null) {
                BufferUtils.freeMethod.invoke(buffer, new Object[0]);
            }
            else if (BufferUtils.cleanerMethod.invoke(buffer, new Object[0]) == null) {
                final Object invoke = BufferUtils.viewedBufferMethod.invoke(buffer, new Object[0]);
                if (invoke != null) {
                    destroyDirectBuffer((Buffer)invoke);
                }
                else {
                    Logger.getLogger(BufferUtils.class.getName()).log(Level.SEVERE, "Buffer cannot be destroyed: {0}", buffer);
                }
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
            final Throwable thrown;
            Logger.getLogger(BufferUtils.class.getName()).log(Level.SEVERE, "{0}", thrown);
        }
    }
    
    static {
        BufferUtils.trackDirectMemory = false;
        removeCollected = new ReferenceQueue<Buffer>();
        trackedBuffers = new ConcurrentHashMap<BufferInfo, BufferInfo>();
        loadedMethods = new AtomicBoolean(false);
        BufferUtils.cleanerMethod = null;
        BufferUtils.cleanMethod = null;
        BufferUtils.viewedBufferMethod = null;
        BufferUtils.freeMethod = null;
    }
    
    private static class BufferInfo extends PhantomReference<Buffer>
    {
        private final Class type;
        private final int size;
        
        public BufferInfo(final Class type, final int size, final Buffer referent, final ReferenceQueue<? super Buffer> q) {
            super(referent, q);
            this.type = type;
            this.size = size;
        }
    }
    
    private static class ClearReferences extends Thread
    {
        ClearReferences() {
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    BufferUtils.trackedBuffers.remove(BufferUtils.removeCollected.remove());
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
