// 
// Decompiled by Procyon v0.5.36
// 

package zombie.Lua;

import se.krka.kahlua.converter.JavaToLuaConverter;
import se.krka.kahlua.converter.LuaToJavaConverter;
import se.krka.kahlua.converter.KahluaConverterManager;

public final class KahluaNumberConverter
{
    private KahluaNumberConverter() {
    }
    
    public static void install(final KahluaConverterManager kahluaConverterManager) {
        kahluaConverterManager.addLuaConverter((LuaToJavaConverter)new LuaToJavaConverter<Double, Long>() {
            public Long fromLuaToJava(final Double n, final Class<Long> clazz) {
                return n.longValue();
            }
            
            public Class<Long> getJavaType() {
                return Long.class;
            }
            
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter((LuaToJavaConverter)new LuaToJavaConverter<Double, Integer>() {
            public Integer fromLuaToJava(final Double n, final Class<Integer> clazz) {
                return n.intValue();
            }
            
            public Class<Integer> getJavaType() {
                return Integer.class;
            }
            
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter((LuaToJavaConverter)new LuaToJavaConverter<Double, Float>() {
            public Float fromLuaToJava(final Double n, final Class<Float> clazz) {
                return new Float(n.floatValue());
            }
            
            public Class<Float> getJavaType() {
                return Float.class;
            }
            
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter((LuaToJavaConverter)new LuaToJavaConverter<Double, Byte>() {
            public Byte fromLuaToJava(final Double n, final Class<Byte> clazz) {
                return n.byteValue();
            }
            
            public Class<Byte> getJavaType() {
                return Byte.class;
            }
            
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter((LuaToJavaConverter)new LuaToJavaConverter<Double, Character>() {
            public Character fromLuaToJava(final Double n, final Class<Character> clazz) {
                return (char)n.intValue();
            }
            
            public Class<Character> getJavaType() {
                return Character.class;
            }
            
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter((LuaToJavaConverter)new LuaToJavaConverter<Double, Short>() {
            public Short fromLuaToJava(final Double n, final Class<Short> clazz) {
                return n.shortValue();
            }
            
            public Class<Short> getJavaType() {
                return Short.class;
            }
            
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Double.class));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Float.class));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Integer.class));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Long.class));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Short.class));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Byte.class));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Character.class));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Double.TYPE));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Float.TYPE));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Integer.TYPE));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Long.TYPE));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Short.TYPE));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Byte.TYPE));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new NumberToLuaConverter((Class<Number>)Character.TYPE));
        kahluaConverterManager.addJavaConverter((JavaToLuaConverter)new JavaToLuaConverter<Boolean>() {
            public Object fromJavaToLua(final Boolean b) {
                return b;
            }
            
            public Class<Boolean> getJavaType() {
                return Boolean.class;
            }
        });
    }
    
    private static final class NumberToLuaConverter<T extends Number> implements JavaToLuaConverter<T>
    {
        private final Class<T> clazz;
        
        public NumberToLuaConverter(final Class<T> clazz) {
            this.clazz = clazz;
        }
        
        public Object fromJavaToLua(final T t) {
            if (t instanceof Double) {
                return t;
            }
            return DoubleCache.valueOf(t.doubleValue());
        }
        
        public Class<T> getJavaType() {
            return this.clazz;
        }
    }
    
    private static final class DoubleCache
    {
        static final int low = -128;
        static final int high = 10000;
        static final Double[] cache;
        
        public static Double valueOf(final double value) {
            if (value == (int)value && value >= -128.0 && value <= 10000.0) {
                return DoubleCache.cache[(int)(value + 128.0)];
            }
            return new Double(value);
        }
        
        static {
            cache = new Double[10129];
            int n = -128;
            for (int i = 0; i < DoubleCache.cache.length; ++i) {
                DoubleCache.cache[i] = new Double(n++);
            }
        }
    }
}
