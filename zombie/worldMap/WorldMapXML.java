// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import java.util.Map;
import zombie.core.math.PZMath;
import zombie.util.Lambda;
import zombie.debug.DebugLog;
import zombie.util.PZXmlParserException;
import org.w3c.dom.Element;
import zombie.util.PZXmlUtil;
import java.util.ArrayList;
import zombie.util.SharedStrings;

public final class WorldMapXML
{
    private final SharedStrings m_sharedStrings;
    private final WorldMapPoint m_point;
    private final WorldMapProperties m_properties;
    private final ArrayList<WorldMapProperties> m_sharedProperties;
    
    public WorldMapXML() {
        this.m_sharedStrings = new SharedStrings();
        this.m_point = new WorldMapPoint();
        this.m_properties = new WorldMapProperties();
        this.m_sharedProperties = new ArrayList<WorldMapProperties>();
    }
    
    public boolean read(final String s, final WorldMapData worldMapData) throws PZXmlParserException {
        final Element xml = PZXmlUtil.parseXml(s);
        if (xml.getNodeName().equals("world")) {
            this.parseWorld(xml, worldMapData);
            return true;
        }
        return false;
    }
    
    private void parseWorld(final Element element, final WorldMapData worldMapData) {
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapData, (element2, worldMapData2) -> {
            if (!element2.getNodeName().equals("cell")) {
                DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, element2.getNodeName()));
            }
            else {
                worldMapData2.m_cells.add(this.parseCell(element2));
            }
        });
    }
    
    private WorldMapCell parseCell(final Element element) {
        final WorldMapCell worldMapCell2 = new WorldMapCell();
        worldMapCell2.m_x = PZMath.tryParseInt(element.getAttribute("x"), 0);
        worldMapCell2.m_y = PZMath.tryParseInt(element.getAttribute("y"), 0);
        final WorldMapCell worldMapCell3;
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapCell2, (element2, worldMapCell) -> {
            try {
                if ("feature".equalsIgnoreCase(element2.getNodeName())) {
                    worldMapCell.m_features.add(this.parseFeature(worldMapCell3, element2));
                }
            }
            catch (Exception ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, element2.getNodeName()));
                DebugLog.General.error(ex);
            }
            return;
        });
        return worldMapCell2;
    }
    
    private WorldMapFeature parseFeature(final WorldMapCell worldMapCell, final Element element2) {
        final WorldMapFeature worldMapFeature2 = new WorldMapFeature(worldMapCell);
        final String s;
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element2, worldMapFeature2, (element, worldMapFeature) -> {
            try {
                element.getNodeName();
                if ("geometry".equalsIgnoreCase(s)) {
                    worldMapFeature.m_geometries.add(this.parseGeometry(element));
                }
                if ("properties".equalsIgnoreCase(s)) {
                    this.parseFeatureProperties(element, worldMapFeature);
                }
            }
            catch (Exception ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, element.getNodeName()));
                DebugLog.General.error(ex);
            }
            return;
        });
        return worldMapFeature2;
    }
    
    private void parseFeatureProperties(final Element element, final WorldMapFeature worldMapFeature) {
        this.m_properties.clear();
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapFeature, (element2, p1) -> {
            try {
                if ("property".equalsIgnoreCase(element2.getNodeName())) {
                    this.m_properties.put(this.m_sharedStrings.get(element2.getAttribute("name")), this.m_sharedStrings.get(element2.getAttribute("value")));
                }
            }
            catch (Exception ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, element2.getNodeName()));
                DebugLog.General.error(ex);
            }
            return;
        });
        worldMapFeature.m_properties = this.getOrCreateProperties(this.m_properties);
    }
    
    private WorldMapProperties getOrCreateProperties(final WorldMapProperties worldMapProperties) {
        for (int i = 0; i < this.m_sharedProperties.size(); ++i) {
            if (this.m_sharedProperties.get(i).equals(worldMapProperties)) {
                return this.m_sharedProperties.get(i);
            }
        }
        final WorldMapProperties e = new WorldMapProperties();
        e.putAll(worldMapProperties);
        this.m_sharedProperties.add(e);
        return e;
    }
    
    private WorldMapGeometry parseGeometry(final Element element) {
        final WorldMapGeometry worldMapGeometry2 = new WorldMapGeometry();
        worldMapGeometry2.m_type = WorldMapGeometry.Type.valueOf(element.getAttribute("type"));
        WorldMapPoints e;
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapGeometry2, (element2, worldMapGeometry) -> {
            try {
                if ("coordinates".equalsIgnoreCase(element2.getNodeName())) {
                    e = new WorldMapPoints();
                    this.parseGeometryCoordinates(element2, e);
                    worldMapGeometry.m_points.add(e);
                }
            }
            catch (Exception ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, element2.getNodeName()));
                DebugLog.General.error(ex);
            }
            return;
        });
        worldMapGeometry2.calculateBounds();
        return worldMapGeometry2;
    }
    
    private void parseGeometryCoordinates(final Element element, final WorldMapPoints worldMapPoints) {
        final WorldMapPoint worldMapPoint;
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, worldMapPoints, (element2, worldMapPoints2) -> {
            try {
                if ("point".equalsIgnoreCase(element2.getNodeName())) {
                    this.parsePoint(element2, this.m_point);
                    worldMapPoints2.add(worldMapPoint.x);
                    worldMapPoints2.add(worldMapPoint.y);
                }
            }
            catch (Exception ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, element2.getNodeName()));
                DebugLog.General.error(ex);
            }
        });
    }
    
    private WorldMapPoint parsePoint(final Element element, final WorldMapPoint worldMapPoint) {
        worldMapPoint.x = PZMath.tryParseInt(element.getAttribute("x"), 0);
        worldMapPoint.y = PZMath.tryParseInt(element.getAttribute("y"), 0);
        return worldMapPoint;
    }
}
