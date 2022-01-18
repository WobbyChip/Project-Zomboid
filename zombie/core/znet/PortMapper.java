// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.znet;

import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.ServerOptions;

public class PortMapper
{
    private static String externalAddress;
    
    public static void startup() {
    }
    
    public static void shutdown() {
        _cleanup();
    }
    
    public static boolean discover() {
        _discover();
        return _igd_found();
    }
    
    public static boolean igdFound() {
        return _igd_found();
    }
    
    public static boolean addMapping(final int n, final int n2, final String s, final String s2, final int n3) {
        return addMapping(n, n2, s, s2, n3, false);
    }
    
    public static boolean addMapping(final int n, final int n2, final String s, final String s2, final int n3, final boolean b) {
        boolean b2 = _add_mapping(n, n2, s, s2, n3, b);
        if (!b2 && n3 != 0 && ServerOptions.instance.UPnPZeroLeaseTimeFallback.getValue()) {
            DebugLog.log(DebugType.Network, "Failed to add port mapping, retrying with zero lease time");
            b2 = _add_mapping(n, n2, s, s2, 0, b);
        }
        return b2;
    }
    
    public static boolean removeMapping(final int n, final String s) {
        return _remove_mapping(n, s);
    }
    
    public static void fetchMappings() {
        _fetch_mappings();
    }
    
    public static int numMappings() {
        return _num_mappings();
    }
    
    public static PortMappingEntry getMapping(final int n) {
        return _get_mapping(n);
    }
    
    public static String getGatewayInfo() {
        return _get_gateway_info();
    }
    
    public static synchronized String getExternalAddress(final boolean b) {
        if (b || PortMapper.externalAddress == null) {
            PortMapper.externalAddress = _get_external_address();
        }
        return PortMapper.externalAddress;
    }
    
    public static String getExternalAddress() {
        return getExternalAddress(false);
    }
    
    private static native void _discover();
    
    private static native void _cleanup();
    
    private static native boolean _igd_found();
    
    private static native boolean _add_mapping(final int p0, final int p1, final String p2, final String p3, final int p4, final boolean p5);
    
    private static native boolean _remove_mapping(final int p0, final String p1);
    
    private static native void _fetch_mappings();
    
    private static native int _num_mappings();
    
    private static native PortMappingEntry _get_mapping(final int p0);
    
    private static native String _get_gateway_info();
    
    private static native String _get_external_address();
    
    static {
        PortMapper.externalAddress = null;
    }
}
