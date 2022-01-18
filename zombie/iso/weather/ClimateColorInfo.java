// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import java.io.Writer;
import java.io.FileWriter;
import zombie.debug.DebugLog;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.io.BufferedWriter;
import zombie.core.Color;

public class ClimateColorInfo
{
    private Color interior;
    private Color exterior;
    private static BufferedWriter writer;
    
    public ClimateColorInfo() {
        this.interior = new Color(0, 0, 0, 1);
        this.exterior = new Color(0, 0, 0, 1);
    }
    
    public ClimateColorInfo(final float n, final float n2, final float n3, final float n4) {
        this(n, n2, n3, n4, n, n2, n3, n4);
    }
    
    public ClimateColorInfo(final float r, final float g, final float b, final float a, final float r2, final float g2, final float b2, final float a2) {
        this.interior = new Color(0, 0, 0, 1);
        this.exterior = new Color(0, 0, 0, 1);
        this.interior.r = r;
        this.interior.g = g;
        this.interior.b = b;
        this.interior.a = a;
        this.exterior.r = r2;
        this.exterior.g = g2;
        this.exterior.b = b2;
        this.exterior.a = a2;
    }
    
    public void setInterior(final Color color) {
        this.interior.set(color);
    }
    
    public void setInterior(final float r, final float g, final float b, final float a) {
        this.interior.r = r;
        this.interior.g = g;
        this.interior.b = b;
        this.interior.a = a;
    }
    
    public Color getInterior() {
        return this.interior;
    }
    
    public void setExterior(final Color color) {
        this.exterior.set(color);
    }
    
    public void setExterior(final float r, final float g, final float b, final float a) {
        this.exterior.r = r;
        this.exterior.g = g;
        this.exterior.b = b;
        this.exterior.a = a;
    }
    
    public Color getExterior() {
        return this.exterior;
    }
    
    public void setTo(final ClimateColorInfo climateColorInfo) {
        this.interior.set(climateColorInfo.interior);
        this.exterior.set(climateColorInfo.exterior);
    }
    
    public ClimateColorInfo interp(final ClimateColorInfo climateColorInfo, final float n, final ClimateColorInfo climateColorInfo2) {
        this.interior.interp(climateColorInfo.interior, n, climateColorInfo2.interior);
        this.exterior.interp(climateColorInfo.exterior, n, climateColorInfo2.exterior);
        return climateColorInfo2;
    }
    
    public void scale(final float n) {
        this.interior.scale(n);
        this.exterior.scale(n);
    }
    
    public static ClimateColorInfo interp(final ClimateColorInfo climateColorInfo, final ClimateColorInfo climateColorInfo2, final float n, final ClimateColorInfo climateColorInfo3) {
        return climateColorInfo.interp(climateColorInfo2, n, climateColorInfo3);
    }
    
    public void write(final ByteBuffer byteBuffer) {
        byteBuffer.putFloat(this.interior.r);
        byteBuffer.putFloat(this.interior.g);
        byteBuffer.putFloat(this.interior.b);
        byteBuffer.putFloat(this.interior.a);
        byteBuffer.putFloat(this.exterior.r);
        byteBuffer.putFloat(this.exterior.g);
        byteBuffer.putFloat(this.exterior.b);
        byteBuffer.putFloat(this.exterior.a);
    }
    
    public void read(final ByteBuffer byteBuffer) {
        this.interior.r = byteBuffer.getFloat();
        this.interior.g = byteBuffer.getFloat();
        this.interior.b = byteBuffer.getFloat();
        this.interior.a = byteBuffer.getFloat();
        this.exterior.r = byteBuffer.getFloat();
        this.exterior.g = byteBuffer.getFloat();
        this.exterior.b = byteBuffer.getFloat();
        this.exterior.a = byteBuffer.getFloat();
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeFloat(this.interior.r);
        dataOutputStream.writeFloat(this.interior.g);
        dataOutputStream.writeFloat(this.interior.b);
        dataOutputStream.writeFloat(this.interior.a);
        dataOutputStream.writeFloat(this.exterior.r);
        dataOutputStream.writeFloat(this.exterior.g);
        dataOutputStream.writeFloat(this.exterior.b);
        dataOutputStream.writeFloat(this.exterior.a);
    }
    
    public void load(final DataInputStream dataInputStream, final int n) throws IOException {
        this.interior.r = dataInputStream.readFloat();
        this.interior.g = dataInputStream.readFloat();
        this.interior.b = dataInputStream.readFloat();
        this.interior.a = dataInputStream.readFloat();
        this.exterior.r = dataInputStream.readFloat();
        this.exterior.g = dataInputStream.readFloat();
        this.exterior.b = dataInputStream.readFloat();
        this.exterior.a = dataInputStream.readFloat();
    }
    
    public static boolean writeColorInfoConfig() {
        final boolean b = false;
        try {
            final String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, format);
            final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, s);
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            final File file = new File(pathname);
            try {
                final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
                try {
                    ClimateColorInfo.writer = writer;
                    final ClimateManager instance = ClimateManager.getInstance();
                    write("--[[");
                    write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                    write("-- Climate color configuration");
                    write("-- File should be placed in: media/lua/server/Climate/ClimateMain.lua (remove date stamp)");
                    write("--]]");
                    writer.newLine();
                    write("ClimateMain = {};");
                    write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, format));
                    writer.newLine();
                    write("local WARM,NORMAL,CLOUDY = 0,1,2;");
                    writer.newLine();
                    write("local SUMMER,FALL,WINTER,SPRING = 0,1,2,3;");
                    writer.newLine();
                    write("function ClimateMain.onClimateManagerInit(_clim)");
                    final int n = 1;
                    write(n, "local c;");
                    write(n, "c = _clim:getColNightNoMoon();");
                    writeColor(n, instance.getColNightNoMoon());
                    writer.newLine();
                    write(n, "c = _clim:getColNightMoon();");
                    writeColor(n, instance.getColNightMoon());
                    writer.newLine();
                    write(n, "c = _clim:getColFog();");
                    writeColor(n, instance.getColFog());
                    writer.newLine();
                    write(n, "c = _clim:getColFogLegacy();");
                    writeColor(n, instance.getColFogLegacy());
                    writer.newLine();
                    write(n, "c = _clim:getColFogNew();");
                    writeColor(n, instance.getColFogNew());
                    writer.newLine();
                    write(n, "c = _clim:getFogTintStorm();");
                    writeColor(n, instance.getFogTintStorm());
                    writer.newLine();
                    write(n, "c = _clim:getFogTintTropical();");
                    writeColor(n, instance.getFogTintTropical());
                    writer.newLine();
                    final WeatherPeriod weatherPeriod = instance.getWeatherPeriod();
                    write(n, "local w = _clim:getWeatherPeriod();");
                    writer.newLine();
                    write(n, "c = w:getCloudColorReddish();");
                    writeColor(n, weatherPeriod.getCloudColorReddish());
                    writer.newLine();
                    write(n, "c = w:getCloudColorGreenish();");
                    writeColor(n, weatherPeriod.getCloudColorGreenish());
                    writer.newLine();
                    write(n, "c = w:getCloudColorBlueish();");
                    writeColor(n, weatherPeriod.getCloudColorBlueish());
                    writer.newLine();
                    write(n, "c = w:getCloudColorPurplish();");
                    writeColor(n, weatherPeriod.getCloudColorPurplish());
                    writer.newLine();
                    write(n, "c = w:getCloudColorTropical();");
                    writeColor(n, weatherPeriod.getCloudColorTropical());
                    writer.newLine();
                    write(n, "c = w:getCloudColorBlizzard();");
                    writeColor(n, weatherPeriod.getCloudColorBlizzard());
                    writer.newLine();
                    final String[] array = { "Dawn", "Day", "Dusk" };
                    final String[] array2 = { "SUMMER", "FALL", "WINTER", "SPRING" };
                    final String[] array3 = { "WARM", "NORMAL", "CLOUDY" };
                    for (int i = 0; i < 3; ++i) {
                        write(n, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, array[i]));
                        for (int j = 0; j < 4; ++j) {
                            for (int k = 0; k < 3; ++k) {
                                if (k == 0 || k == 2 || (k == 1 && i == 2)) {
                                    writeSeasonColor(n, instance.getSeasonColor(i, k, j), array[i], array2[j], array3[k]);
                                    writer.newLine();
                                }
                            }
                        }
                    }
                    write("end");
                    writer.newLine();
                    write("Events.OnClimateManagerInit.Add(ClimateMain.onClimateManagerInit);");
                    ClimateColorInfo.writer = null;
                    writer.flush();
                    writer.close();
                    writer.close();
                }
                catch (Throwable t) {
                    try {
                        writer.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        return b;
    }
    
    private static void writeSeasonColor(final int n, final ClimateColorInfo climateColorInfo, final String s, final String s2, final String s3) throws IOException {
        final Color exterior = climateColorInfo.exterior;
        write(n, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FFFF)Ljava/lang/String;, s, s3, s2, exterior.r, exterior.g, exterior.b, exterior.a));
        final Color interior = climateColorInfo.interior;
        write(n, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FFFF)Ljava/lang/String;, s, s3, s2, interior.r, interior.g, interior.b, interior.a));
    }
    
    private static void writeColor(final int n, final ClimateColorInfo climateColorInfo) throws IOException {
        final Color exterior = climateColorInfo.exterior;
        write(n, invokedynamic(makeConcatWithConstants:(FFFF)Ljava/lang/String;, exterior.r, exterior.g, exterior.b, exterior.a));
        final Color interior = climateColorInfo.interior;
        write(n, invokedynamic(makeConcatWithConstants:(FFFF)Ljava/lang/String;, interior.r, interior.g, interior.b, interior.a));
    }
    
    private static void write(final int n, final String str) throws IOException {
        ClimateColorInfo.writer.write(new String(new char[n]).replace("\u0000", "\t"));
        ClimateColorInfo.writer.write(str);
        ClimateColorInfo.writer.newLine();
    }
    
    private static void write(final String str) throws IOException {
        ClimateColorInfo.writer.write(str);
        ClimateColorInfo.writer.newLine();
    }
}
