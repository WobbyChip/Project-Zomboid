// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoGridSquare;
import java.util.Iterator;
import zombie.debug.LineDrawer;
import zombie.core.math.PZMath;
import zombie.iso.IsoWorld;
import zombie.util.Type;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import zombie.popman.ObjectPool;
import zombie.iso.Vector2;

public class VehicleStorySpawner
{
    private static final VehicleStorySpawner instance;
    private static final Vector2 s_vector2_1;
    private static final Vector2 s_vector2_2;
    private static final ObjectPool<Element> s_elementPool;
    private static final int[] s_AABB;
    public final ArrayList<Element> m_elements;
    public final HashMap<String, Object> m_storyParams;
    
    public VehicleStorySpawner() {
        this.m_elements = new ArrayList<Element>();
        this.m_storyParams = new HashMap<String, Object>();
    }
    
    public static VehicleStorySpawner getInstance() {
        return VehicleStorySpawner.instance;
    }
    
    public void clear() {
        VehicleStorySpawner.s_elementPool.release(this.m_elements);
        this.m_elements.clear();
        this.m_storyParams.clear();
    }
    
    public Element addElement(final String s, final float n, final float n2, final float n3, final float n4, final float n5) {
        final Element init = VehicleStorySpawner.s_elementPool.alloc().init(s, n, n2, n3, n4, n5);
        this.m_elements.add(init);
        return init;
    }
    
    public void setParameter(final String key, final boolean b) {
        this.m_storyParams.put(key, b ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setParameter(final String key, final float f) {
        this.m_storyParams.put(key, f);
    }
    
    public void setParameter(final String key, final int i) {
        this.m_storyParams.put(key, i);
    }
    
    public void setParameter(final String key, final Object value) {
        this.m_storyParams.put(key, value);
    }
    
    public boolean getParameterBoolean(final String s) {
        return this.getParameter(s, Boolean.class);
    }
    
    public float getParameterFloat(final String s) {
        return this.getParameter(s, Float.class);
    }
    
    public int getParameterInteger(final String s) {
        return this.getParameter(s, Integer.class);
    }
    
    public String getParameterString(final String s) {
        return this.getParameter(s, String.class);
    }
    
    public <E> E getParameter(final String key, final Class<E> clazz) {
        return Type.tryCastTo(this.m_storyParams.get(key), clazz);
    }
    
    public void spawn(final float n, final float n2, final float z, final float n3, final IElementSpawner elementSpawner) {
        for (int i = 0; i < this.m_elements.size(); ++i) {
            final Element element = this.m_elements.get(i);
            final Vector2 setLengthAndDirection = VehicleStorySpawner.s_vector2_1.setLengthAndDirection(element.direction, 1.0f);
            setLengthAndDirection.add(element.position);
            this.rotate(n, n2, setLengthAndDirection, n3);
            this.rotate(n, n2, element.position, n3);
            element.direction = Vector2.getDirection(setLengthAndDirection.x - element.position.x, setLengthAndDirection.y - element.position.y);
            element.z = z;
            element.square = IsoWorld.instance.CurrentCell.getGridSquare(element.position.x, element.position.y, z);
            elementSpawner.spawn(this, element);
        }
    }
    
    public Vector2 rotate(final float n, final float n2, final Vector2 vector2, final float n3) {
        final float x = vector2.x;
        final float y = vector2.y;
        vector2.x = n + (float)(x * Math.cos(n3) - y * Math.sin(n3));
        vector2.y = n2 + (float)(x * Math.sin(n3) + y * Math.cos(n3));
        return vector2;
    }
    
    public void getAABB(final float n, final float n2, final float n3, final float n4, final float n5, final int[] array) {
        final Vector2 setLengthAndDirection = VehicleStorySpawner.s_vector2_1.setLengthAndDirection(n5, 1.0f);
        final Vector2 set = VehicleStorySpawner.s_vector2_2.set(setLengthAndDirection);
        set.tangent();
        final Vector2 vector2 = setLengthAndDirection;
        vector2.x *= n4 / 2.0f;
        final Vector2 vector3 = setLengthAndDirection;
        vector3.y *= n4 / 2.0f;
        final Vector2 vector4 = set;
        vector4.x *= n3 / 2.0f;
        final Vector2 vector5 = set;
        vector5.y *= n3 / 2.0f;
        final float n6 = n + setLengthAndDirection.x;
        final float n7 = n2 + setLengthAndDirection.y;
        final float n8 = n - setLengthAndDirection.x;
        final float n9 = n2 - setLengthAndDirection.y;
        final float n10 = n6 - set.x;
        final float n11 = n7 - set.y;
        final float n12 = n6 + set.x;
        final float n13 = n7 + set.y;
        final float n14 = n8 - set.x;
        final float n15 = n9 - set.y;
        final float n16 = n8 + set.x;
        final float n17 = n9 + set.y;
        final float min = PZMath.min(n10, PZMath.min(n12, PZMath.min(n14, n16)));
        final float max = PZMath.max(n10, PZMath.max(n12, PZMath.max(n14, n16)));
        final float min2 = PZMath.min(n11, PZMath.min(n13, PZMath.min(n15, n17)));
        final float max2 = PZMath.max(n11, PZMath.max(n13, PZMath.max(n15, n17)));
        array[0] = (int)PZMath.floor(min);
        array[1] = (int)PZMath.floor(min2);
        array[2] = (int)PZMath.ceil(max);
        array[3] = (int)PZMath.ceil(max2);
    }
    
    public void render(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        LineDrawer.DrawIsoRectRotated(n, n2, n3, n4, n5, n6, 0.0f, 0.0f, 1.0f, 1.0f);
        final float n7 = 1.0f;
        final float n8 = 1.0f;
        final float n9 = 1.0f;
        final float n10 = 1.0f;
        float min = Float.MAX_VALUE;
        float min2 = Float.MAX_VALUE;
        float max = -3.4028235E38f;
        float max2 = -3.4028235E38f;
        for (final Element element : this.m_elements) {
            final Vector2 setLengthAndDirection = VehicleStorySpawner.s_vector2_1.setLengthAndDirection(element.direction, 1.0f);
            LineDrawer.DrawIsoLine(element.position.x, element.position.y, n3, element.position.x + setLengthAndDirection.x, element.position.y + setLengthAndDirection.y, n3, n7, n8, n9, n10, 1);
            LineDrawer.DrawIsoRectRotated(element.position.x, element.position.y, n3, element.width, element.height, element.direction, n7, n8, n9, n10);
            this.getAABB(element.position.x, element.position.y, element.width, element.height, element.direction, VehicleStorySpawner.s_AABB);
            min = PZMath.min(min, (float)VehicleStorySpawner.s_AABB[0]);
            min2 = PZMath.min(min2, (float)VehicleStorySpawner.s_AABB[1]);
            max = PZMath.max(max, (float)VehicleStorySpawner.s_AABB[2]);
            max2 = PZMath.max(max2, (float)VehicleStorySpawner.s_AABB[3]);
        }
    }
    
    static {
        instance = new VehicleStorySpawner();
        s_vector2_1 = new Vector2();
        s_vector2_2 = new Vector2();
        s_elementPool = new ObjectPool<Element>(Element::new);
        s_AABB = new int[4];
    }
    
    public static final class Element
    {
        String id;
        final Vector2 position;
        float direction;
        float width;
        float height;
        float z;
        IsoGridSquare square;
        
        public Element() {
            this.position = new Vector2();
        }
        
        Element init(final String id, final float n, final float n2, final float direction, final float width, final float height) {
            this.id = id;
            this.position.set(n, n2);
            this.direction = direction;
            this.width = width;
            this.height = height;
            this.z = 0.0f;
            this.square = null;
            return this;
        }
    }
    
    public interface IElementSpawner
    {
        void spawn(final VehicleStorySpawner p0, final Element p1);
    }
}
