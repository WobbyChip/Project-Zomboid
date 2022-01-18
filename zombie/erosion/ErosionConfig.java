// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion;

import zombie.debug.DebugLog;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import zombie.core.Core;
import java.nio.ByteBuffer;

public final class ErosionConfig
{
    public final Seeds seeds;
    public final Time time;
    public final Debug debug;
    public final Season season;
    
    public ErosionConfig() {
        this.seeds = new Seeds();
        this.time = new Time();
        this.debug = new Debug();
        this.season = new Season();
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.putInt(this.seeds.seedMain_0);
        byteBuffer.putInt(this.seeds.seedMain_1);
        byteBuffer.putInt(this.seeds.seedMain_2);
        byteBuffer.putInt(this.seeds.seedMoisture_0);
        byteBuffer.putInt(this.seeds.seedMoisture_1);
        byteBuffer.putInt(this.seeds.seedMoisture_2);
        byteBuffer.putInt(this.seeds.seedMinerals_0);
        byteBuffer.putInt(this.seeds.seedMinerals_1);
        byteBuffer.putInt(this.seeds.seedMinerals_2);
        byteBuffer.putInt(this.seeds.seedKudzu_0);
        byteBuffer.putInt(this.seeds.seedKudzu_1);
        byteBuffer.putInt(this.seeds.seedKudzu_2);
        byteBuffer.putInt(this.time.tickunit);
        byteBuffer.putInt(this.time.ticks);
        byteBuffer.putInt(this.time.eticks);
        byteBuffer.putInt(this.time.epoch);
        byteBuffer.putInt(this.season.lat);
        byteBuffer.putInt(this.season.tempMax);
        byteBuffer.putInt(this.season.tempMin);
        byteBuffer.putInt(this.season.tempDiff);
        byteBuffer.putInt(this.season.seasonLag);
        byteBuffer.putFloat(this.season.noon);
        byteBuffer.putInt(this.season.seedA);
        byteBuffer.putInt(this.season.seedB);
        byteBuffer.putInt(this.season.seedC);
        byteBuffer.putFloat(this.season.jan);
        byteBuffer.putFloat(this.season.feb);
        byteBuffer.putFloat(this.season.mar);
        byteBuffer.putFloat(this.season.apr);
        byteBuffer.putFloat(this.season.may);
        byteBuffer.putFloat(this.season.jun);
        byteBuffer.putFloat(this.season.jul);
        byteBuffer.putFloat(this.season.aug);
        byteBuffer.putFloat(this.season.sep);
        byteBuffer.putFloat(this.season.oct);
        byteBuffer.putFloat(this.season.nov);
        byteBuffer.putFloat(this.season.dec);
    }
    
    public void load(final ByteBuffer byteBuffer) {
        this.seeds.seedMain_0 = byteBuffer.getInt();
        this.seeds.seedMain_1 = byteBuffer.getInt();
        this.seeds.seedMain_2 = byteBuffer.getInt();
        this.seeds.seedMoisture_0 = byteBuffer.getInt();
        this.seeds.seedMoisture_1 = byteBuffer.getInt();
        this.seeds.seedMoisture_2 = byteBuffer.getInt();
        this.seeds.seedMinerals_0 = byteBuffer.getInt();
        this.seeds.seedMinerals_1 = byteBuffer.getInt();
        this.seeds.seedMinerals_2 = byteBuffer.getInt();
        this.seeds.seedKudzu_0 = byteBuffer.getInt();
        this.seeds.seedKudzu_1 = byteBuffer.getInt();
        this.seeds.seedKudzu_2 = byteBuffer.getInt();
        this.time.tickunit = byteBuffer.getInt();
        this.time.ticks = byteBuffer.getInt();
        this.time.eticks = byteBuffer.getInt();
        this.time.epoch = byteBuffer.getInt();
        this.season.lat = byteBuffer.getInt();
        this.season.tempMax = byteBuffer.getInt();
        this.season.tempMin = byteBuffer.getInt();
        this.season.tempDiff = byteBuffer.getInt();
        this.season.seasonLag = byteBuffer.getInt();
        this.season.noon = byteBuffer.getFloat();
        this.season.seedA = byteBuffer.getInt();
        this.season.seedB = byteBuffer.getInt();
        this.season.seedC = byteBuffer.getInt();
        this.season.jan = byteBuffer.getFloat();
        this.season.feb = byteBuffer.getFloat();
        this.season.mar = byteBuffer.getFloat();
        this.season.apr = byteBuffer.getFloat();
        this.season.may = byteBuffer.getFloat();
        this.season.jun = byteBuffer.getFloat();
        this.season.jul = byteBuffer.getFloat();
        this.season.aug = byteBuffer.getFloat();
        this.season.sep = byteBuffer.getFloat();
        this.season.oct = byteBuffer.getFloat();
        this.season.nov = byteBuffer.getFloat();
        this.season.dec = byteBuffer.getFloat();
    }
    
    public void writeFile(final String pathname) {
        try {
            if (Core.getInstance().isNoSave()) {
                return;
            }
            final File file = new File(pathname);
            if (!file.exists()) {
                file.createNewFile();
            }
            final FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMain_0));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMain_1));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMain_2));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMoisture_0));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMoisture_1));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMoisture_2));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMinerals_0));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMinerals_1));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedMinerals_2));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedKudzu_0));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedKudzu_1));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.seeds.seedKudzu_2));
            fileWriter.write("\n");
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.time.tickunit));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.time.ticks));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.time.eticks));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.time.epoch));
            fileWriter.write("\n");
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.lat));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.tempMax));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.tempMin));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.tempDiff));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.seasonLag));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.noon));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.seedA));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.seedB));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.season.seedC));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.jan));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.feb));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.mar));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.apr));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.may));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.jun));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.jul));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.aug));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.sep));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.oct));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.nov));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.season.dec));
            fileWriter.write("\n");
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, this.debug.enabled));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.debug.startday));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.debug.startmonth));
            fileWriter.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean readFile(final String pathname) {
        try {
            final File file = new File(pathname);
            if (!file.exists()) {
                return false;
            }
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.trim().startsWith("--")) {
                    continue;
                }
                if (!line.contains("=")) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
                }
                else {
                    final String[] split = line.split("=");
                    if (split.length != 2) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
                    }
                    else {
                        final String trim = split[0].trim();
                        final String trim2 = split[1].trim();
                        if (trim.startsWith("seeds.")) {
                            if ("seeds.seedMain_0".equals(trim)) {
                                this.seeds.seedMain_0 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMain_1".equals(trim)) {
                                this.seeds.seedMain_1 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMain_2".equals(trim)) {
                                this.seeds.seedMain_2 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMoisture_0".equals(trim)) {
                                this.seeds.seedMoisture_0 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMoisture_1".equals(trim)) {
                                this.seeds.seedMoisture_1 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMoisture_2".equals(trim)) {
                                this.seeds.seedMoisture_2 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMinerals_0".equals(trim)) {
                                this.seeds.seedMinerals_0 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMinerals_1".equals(trim)) {
                                this.seeds.seedMinerals_1 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedMinerals_2".equals(trim)) {
                                this.seeds.seedMinerals_2 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedKudzu_0".equals(trim)) {
                                this.seeds.seedKudzu_0 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedKudzu_1".equals(trim)) {
                                this.seeds.seedKudzu_1 = Integer.parseInt(trim2);
                            }
                            else if ("seeds.seedKudzu_2".equals(trim)) {
                                this.seeds.seedKudzu_2 = Integer.parseInt(trim2);
                            }
                            else {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
                            }
                        }
                        else if (trim.startsWith("time.")) {
                            if ("time.tickunit".equals(trim)) {
                                this.time.tickunit = Integer.parseInt(trim2);
                            }
                            else if ("time.ticks".equals(trim)) {
                                this.time.ticks = Integer.parseInt(trim2);
                            }
                            else if ("time.eticks".equals(trim)) {
                                this.time.eticks = Integer.parseInt(trim2);
                            }
                            else if ("time.epoch".equals(trim)) {
                                this.time.epoch = Integer.parseInt(trim2);
                            }
                            else {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
                            }
                        }
                        else if (trim.startsWith("season.")) {
                            if ("season.lat".equals(trim)) {
                                this.season.lat = Integer.parseInt(trim2);
                            }
                            else if ("season.tempMax".equals(trim)) {
                                this.season.tempMax = Integer.parseInt(trim2);
                            }
                            else if ("season.tempMin".equals(trim)) {
                                this.season.tempMin = Integer.parseInt(trim2);
                            }
                            else if ("season.tempDiff".equals(trim)) {
                                this.season.tempDiff = Integer.parseInt(trim2);
                            }
                            else if ("season.seasonLag".equals(trim)) {
                                this.season.seasonLag = Integer.parseInt(trim2);
                            }
                            else if ("season.noon".equals(trim)) {
                                this.season.noon = Float.parseFloat(trim2);
                            }
                            else if ("season.seedA".equals(trim)) {
                                this.season.seedA = Integer.parseInt(trim2);
                            }
                            else if ("season.seedB".equals(trim)) {
                                this.season.seedB = Integer.parseInt(trim2);
                            }
                            else if ("season.seedC".equals(trim)) {
                                this.season.seedC = Integer.parseInt(trim2);
                            }
                            else if ("season.jan".equals(trim)) {
                                this.season.jan = Float.parseFloat(trim2);
                            }
                            else if ("season.feb".equals(trim)) {
                                this.season.feb = Float.parseFloat(trim2);
                            }
                            else if ("season.mar".equals(trim)) {
                                this.season.mar = Float.parseFloat(trim2);
                            }
                            else if ("season.apr".equals(trim)) {
                                this.season.apr = Float.parseFloat(trim2);
                            }
                            else if ("season.may".equals(trim)) {
                                this.season.may = Float.parseFloat(trim2);
                            }
                            else if ("season.jun".equals(trim)) {
                                this.season.jun = Float.parseFloat(trim2);
                            }
                            else if ("season.jul".equals(trim)) {
                                this.season.jul = Float.parseFloat(trim2);
                            }
                            else if ("season.aug".equals(trim)) {
                                this.season.aug = Float.parseFloat(trim2);
                            }
                            else if ("season.sep".equals(trim)) {
                                this.season.sep = Float.parseFloat(trim2);
                            }
                            else if ("season.oct".equals(trim)) {
                                this.season.oct = Float.parseFloat(trim2);
                            }
                            else if ("season.nov".equals(trim)) {
                                this.season.nov = Float.parseFloat(trim2);
                            }
                            else if ("season.dec".equals(trim)) {
                                this.season.dec = Float.parseFloat(trim2);
                            }
                            else {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
                            }
                        }
                        else if (trim.startsWith("debug.")) {
                            if ("debug.enabled".equals(trim)) {
                                this.debug.enabled = Boolean.parseBoolean(trim2);
                            }
                            else if ("debug.startday".equals(trim)) {
                                this.debug.startday = Integer.parseInt(trim2);
                            }
                            else {
                                if (!"debug.startmonth".equals(trim)) {
                                    continue;
                                }
                                this.debug.startmonth = Integer.parseInt(trim2);
                            }
                        }
                        else {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
                        }
                    }
                }
            }
            bufferedReader.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public Debug getDebug() {
        return this.debug;
    }
    
    public void consolePrint() {
    }
    
    public static final class Seeds
    {
        int seedMain_0;
        int seedMain_1;
        int seedMain_2;
        int seedMoisture_0;
        int seedMoisture_1;
        int seedMoisture_2;
        int seedMinerals_0;
        int seedMinerals_1;
        int seedMinerals_2;
        int seedKudzu_0;
        int seedKudzu_1;
        int seedKudzu_2;
        
        public Seeds() {
            this.seedMain_0 = 16;
            this.seedMain_1 = 32;
            this.seedMain_2 = 64;
            this.seedMoisture_0 = 96;
            this.seedMoisture_1 = 128;
            this.seedMoisture_2 = 144;
            this.seedMinerals_0 = 196;
            this.seedMinerals_1 = 255;
            this.seedMinerals_2 = 0;
            this.seedKudzu_0 = 200;
            this.seedKudzu_1 = 125;
            this.seedKudzu_2 = 50;
        }
    }
    
    public static final class Time
    {
        int tickunit;
        int ticks;
        int eticks;
        int epoch;
        
        public Time() {
            this.tickunit = 144;
            this.ticks = 0;
            this.eticks = 0;
            this.epoch = 0;
        }
    }
    
    public static final class Season
    {
        int lat;
        int tempMax;
        int tempMin;
        int tempDiff;
        int seasonLag;
        float noon;
        int seedA;
        int seedB;
        int seedC;
        float jan;
        float feb;
        float mar;
        float apr;
        float may;
        float jun;
        float jul;
        float aug;
        float sep;
        float oct;
        float nov;
        float dec;
        
        public Season() {
            this.lat = 38;
            this.tempMax = 25;
            this.tempMin = 0;
            this.tempDiff = 7;
            this.seasonLag = 31;
            this.noon = 12.5f;
            this.seedA = 64;
            this.seedB = 128;
            this.seedC = 255;
            this.jan = 0.39f;
            this.feb = 0.35f;
            this.mar = 0.39f;
            this.apr = 0.4f;
            this.may = 0.35f;
            this.jun = 0.37f;
            this.jul = 0.29f;
            this.aug = 0.26f;
            this.sep = 0.23f;
            this.oct = 0.23f;
            this.nov = 0.3f;
            this.dec = 0.32f;
        }
    }
    
    public static final class Debug
    {
        boolean enabled;
        int startday;
        int startmonth;
        
        public Debug() {
            this.enabled = false;
            this.startday = 26;
            this.startmonth = 11;
        }
        
        public boolean getEnabled() {
            return this.enabled;
        }
        
        public int getStartDay() {
            return this.startday;
        }
        
        public int getStartMonth() {
            return this.startmonth;
        }
    }
}
