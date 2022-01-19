// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import org.joml.Vector2d;

public final class MapProjection
{
    public static final double EARTH_RADIUS_METERS = 6378137.0;
    public static final double EARTH_HALF_CIRCUMFERENCE_METERS = 2.0037508342789244E7;
    public static final double EARTH_CIRCUMFERENCE_METERS = 4.007501668557849E7;
    public static final double MAX_LATITUDE_DEGREES = 85.05112878;
    private static final double LOG_2;
    
    static ProjectedMeters lngLatToProjectedMeters(final LngLat lngLat) {
        final ProjectedMeters projectedMeters = new ProjectedMeters();
        projectedMeters.x = lngLat.longitude * 2.0037508342789244E7 / 180.0;
        projectedMeters.y = Math.log(Math.tan(0.7853981633974483 + lngLat.latitude * 3.141592653589793 / 360.0)) * 6378137.0;
        return projectedMeters;
    }
    
    static double metersPerTileAtZoom(final int n) {
        return 4.007501668557849E7 / (1 << n);
    }
    
    static double metersPerPixelAtZoom(final double n, final double n2) {
        return 4.007501668557849E7 / (exp2(n) * n2);
    }
    
    static double zoomAtMetersPerPixel(final double n, final double n2) {
        return log2(4.007501668557849E7 / (n * n2));
    }
    
    static BoundingBox mapLngLatBounds() {
        return new BoundingBox(new Vector2d(-180.0, -85.05112878), new Vector2d(180.0, 85.05112878));
    }
    
    static BoundingBox mapProjectedMetersBounds() {
        final BoundingBox mapLngLatBounds = mapLngLatBounds();
        return new BoundingBox(lngLatToProjectedMeters(new LngLat(mapLngLatBounds.min.x, mapLngLatBounds.min.y)), lngLatToProjectedMeters(new LngLat(mapLngLatBounds.max.x, mapLngLatBounds.max.y)));
    }
    
    public static double exp2(final double b) {
        return Math.pow(2.0, b);
    }
    
    public static double log2(final double a) {
        return Math.log(a) / MapProjection.LOG_2;
    }
    
    static {
        LOG_2 = Math.log(2.0);
    }
    
    public static final class BoundingBox
    {
        Vector2d min;
        Vector2d max;
        
        public BoundingBox(final Vector2d min, final Vector2d max) {
            this.min = min;
            this.max = max;
        }
    }
    
    public static final class LngLat
    {
        double longitude;
        double latitude;
        
        public LngLat(final double longitude, final double latitude) {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
    
    public static final class ProjectedMeters extends Vector2d
    {
    }
}
