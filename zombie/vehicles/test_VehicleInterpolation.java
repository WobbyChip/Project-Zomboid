// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import org.junit.Test;
import java.nio.ByteBuffer;
import zombie.GameTime;
import org.junit.Assert;

public class test_VehicleInterpolation extends Assert
{
    @Test
    public void normalTest() {
        System.out.print("START: normalTest\n");
        GameTime.setServerTimeShift(0L);
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(255);
        final float[] array = new float[27];
        final VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(500);
        for (int i = 1; i < 30; ++i) {
            System.out.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            allocateDirect.position(0);
            allocateDirect.putLong(System.nanoTime());
            allocateDirect.position(0);
            vehicleInterpolation.interpolationDataAdd(allocateDirect, (float)i, (float)i, 0.0f);
            final boolean interpolationDataGet = vehicleInterpolation.interpolationDataGet(array);
            if (i < 6) {
                assertEquals((Object)false, (Object)interpolationDataGet);
            }
            else {
                assertEquals((Object)true, (Object)interpolationDataGet);
                assertEquals((float)(i - 6 + 1), array[0], 0.2f);
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Test
    public void normalZeroTest() {
        System.out.print("START: normalZeroTest\n");
        GameTime.setServerTimeShift(0L);
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(255);
        final float[] array = new float[27];
        final VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(0);
        for (int i = 1; i < 30; ++i) {
            System.out.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            allocateDirect.position(0);
            allocateDirect.putLong(System.nanoTime());
            allocateDirect.position(0);
            vehicleInterpolation.interpolationDataAdd(allocateDirect, (float)i, (float)i, 0.0f);
            assertEquals((Object)true, (Object)vehicleInterpolation.interpolationDataGet(array));
            assertEquals((float)(i - 1 + 1), array[0], 0.2f);
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Test
    public void interpolationTest() {
        System.out.print("START: interpolationTest\n");
        GameTime.setServerTimeShift(0L);
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(255);
        final float[] array = new float[27];
        final VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(500);
        for (int i = 1; i < 30; ++i) {
            System.out.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            allocateDirect.position(0);
            if (i % 2 == 1) {
                allocateDirect.putLong(System.nanoTime());
                allocateDirect.position(0);
                vehicleInterpolation.interpolationDataAdd(allocateDirect, (float)i, (float)i, 0.0f);
            }
            final boolean interpolationDataGet = vehicleInterpolation.interpolationDataGet(array);
            if (i < 7) {
                assertEquals((Object)false, (Object)interpolationDataGet);
            }
            else {
                assertEquals((Object)true, (Object)interpolationDataGet);
                assertEquals((float)(i - 6 + 1), array[0], 0.2f);
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Test
    public void testBufferRestoring() {
        System.out.print("START: normalTestBufferRestoring\n");
        GameTime.setServerTimeShift(0L);
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(255);
        final float[] array = new float[27];
        final VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(400);
        for (int i = 1; i < 30; ++i) {
            System.out.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            allocateDirect.position(0);
            allocateDirect.putLong(System.nanoTime());
            allocateDirect.position(0);
            vehicleInterpolation.interpolationDataAdd(allocateDirect, (float)i, (float)i, 0.0f);
            final boolean interpolationDataGet = vehicleInterpolation.interpolationDataGet(array);
            if (i < 5 || (i >= 11 && i < 15)) {
                assertEquals((Object)false, (Object)interpolationDataGet);
            }
            else {
                assertEquals((Object)true, (Object)interpolationDataGet);
                assertEquals((float)(i - 5 + 1), array[0], 0.2f);
            }
            try {
                if (i == 10) {
                    Thread.sleep(800L);
                }
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Test
    public void normalTestBufferRestoring2() {
        System.out.print("START: normalTestBufferRestoring2\n");
        GameTime.setServerTimeShift(0L);
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(255);
        final float[] array = new float[27];
        final VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(400);
        try {
            for (int i = 1; i < 40; ++i) {
                System.out.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
                allocateDirect.position(0);
                allocateDirect.putLong(System.nanoTime());
                allocateDirect.position(0);
                if (i < 15 || i > 20) {
                    vehicleInterpolation.interpolationDataAdd(allocateDirect, (float)i, 0.0f, 0.0f);
                }
                final boolean interpolationDataGet = vehicleInterpolation.interpolationDataGet(array);
                if (i < 5 || (i >= 18 && i < 25)) {
                    assertEquals((Object)false, (Object)interpolationDataGet);
                }
                else {
                    assertEquals((Object)true, (Object)interpolationDataGet);
                    assertEquals((float)(i - 5 + 1), array[0], 0.1f);
                }
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        finally {}
    }
    
    @Test
    public void normalTestPR() {
        System.out.print("START: normalTestPR\n");
        GameTime.setServerTimeShift(0L);
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(255);
        final float[] array = new float[27];
        final VehicleInterpolation vehicleInterpolation = new VehicleInterpolation(500);
        for (int i = 1; i < 30; ++i) {
            System.out.print(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            allocateDirect.position(0);
            allocateDirect.putLong(System.nanoTime());
            allocateDirect.position(0);
            vehicleInterpolation.interpolationDataAdd(allocateDirect, (float)i, (float)i, 0.0f);
            final boolean interpolationDataGetPR = vehicleInterpolation.interpolationDataGetPR(array);
            if (i < 6) {
                assertEquals((Object)false, (Object)interpolationDataGetPR);
            }
            else {
                assertEquals((Object)true, (Object)interpolationDataGetPR);
                assertEquals((float)(i - 6 + 1), array[0], 1.0f);
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
