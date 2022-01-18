// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import java.util.Iterator;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.BaseScriptObject;

public class VehicleEngineRPM extends BaseScriptObject
{
    public static final int MAX_GEARS = 8;
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    private String m_name;
    public final EngineRPMData[] m_rpmData;
    
    public VehicleEngineRPM() {
        this.m_rpmData = new EngineRPMData[8];
    }
    
    public String getName() {
        return this.m_name;
    }
    
    public void Load(final String name, final String s) throws RuntimeException {
        this.m_name = name;
        int tryParseInt = -1;
        final ScriptParser.Block block = ScriptParser.parse(s).children.get(0);
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("VERSION".equals(trim)) {
                tryParseInt = PZMath.tryParseInt(trim2, -1);
                if (tryParseInt < 0 || tryParseInt > 1) {
                    throw new RuntimeException(String.format("unknown vehicleEngineRPM VERSION \"%s\"", trim2));
                }
                continue;
            }
        }
        if (tryParseInt == -1) {
            throw new RuntimeException(String.format("unknown vehicleEngineRPM VERSION \"%s\"", block.type));
        }
        int n = 0;
        for (final ScriptParser.Block block2 : block.children) {
            if (!"data".equals(block2.type)) {
                throw new RuntimeException(String.format("unknown block vehicleEngineRPM.%s", block2.type));
            }
            if (n == 8) {
                throw new RuntimeException(String.format("too many vehicleEngineRPM.data blocks, max is %d", 8));
            }
            this.LoadData(block2, this.m_rpmData[n] = new EngineRPMData());
            ++n;
        }
    }
    
    private void LoadData(final ScriptParser.Block block, final EngineRPMData engineRPMData) {
        for (final ScriptParser.Value value : block.values) {
            final String trim = value.getKey().trim();
            final String trim2 = value.getValue().trim();
            if ("afterGearChange".equals(trim)) {
                engineRPMData.afterGearChange = PZMath.tryParseFloat(trim2, 0.0f);
            }
            else {
                if (!"gearChange".equals(trim)) {
                    throw new RuntimeException(String.format("unknown value vehicleEngineRPM.data.%s", value.string));
                }
                engineRPMData.gearChange = PZMath.tryParseFloat(trim2, 0.0f);
            }
        }
        for (final ScriptParser.Block block2 : block.children) {
            if ("xxx".equals(block2.type)) {
                continue;
            }
            throw new RuntimeException(String.format("unknown block vehicleEngineRPM.data.%s", block2.type));
        }
    }
    
    public void reset() {
        for (int i = 0; i < this.m_rpmData.length; ++i) {
            if (this.m_rpmData[i] != null) {
                this.m_rpmData[i].reset();
            }
        }
    }
}
