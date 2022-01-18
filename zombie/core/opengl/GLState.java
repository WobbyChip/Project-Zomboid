// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.SpriteRenderer;
import zombie.util.Type;

public final class GLState
{
    public static final CAlphaFunc AlphaFunc;
    public static final CAlphaTest AlphaTest;
    public static final CBlendFunc BlendFunc;
    public static final CBlendFuncSeparate BlendFuncSeparate;
    public static final CColorMask ColorMask;
    public static final CStencilFunc StencilFunc;
    public static final CStencilMask StencilMask;
    public static final CStencilOp StencilOp;
    public static final CStencilTest StencilTest;
    
    public static void startFrame() {
        GLState.AlphaFunc.setDirty();
        GLState.AlphaTest.setDirty();
        GLState.BlendFunc.setDirty();
        GLState.BlendFuncSeparate.setDirty();
        GLState.ColorMask.setDirty();
        GLState.StencilFunc.setDirty();
        GLState.StencilMask.setDirty();
        GLState.StencilOp.setDirty();
        GLState.StencilTest.setDirty();
    }
    
    static {
        AlphaFunc = new CAlphaFunc();
        AlphaTest = new CAlphaTest();
        BlendFunc = new CBlendFunc();
        BlendFuncSeparate = new CBlendFuncSeparate();
        ColorMask = new CColorMask();
        StencilFunc = new CStencilFunc();
        StencilMask = new CStencilMask();
        StencilOp = new CStencilOp();
        StencilTest = new CStencilTest();
    }
    
    public static class CBooleanValue implements IOpenGLState.Value
    {
        public static final CBooleanValue TRUE;
        public static final CBooleanValue FALSE;
        boolean value;
        
        CBooleanValue(final boolean value) {
            this.value = value;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof CBooleanValue && ((CBooleanValue)o).value == this.value;
        }
        
        @Override
        public IOpenGLState.Value set(final IOpenGLState.Value value) {
            this.value = ((CBooleanValue)value).value;
            return this;
        }
        
        static {
            TRUE = new CBooleanValue(true);
            FALSE = new CBooleanValue(false);
        }
    }
    
    public static final class C4BooleansValue implements IOpenGLState.Value
    {
        boolean a;
        boolean b;
        boolean c;
        boolean d;
        
        public C4BooleansValue set(final boolean a, final boolean b, final boolean c, final boolean d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            final C4BooleansValue c4BooleansValue = Type.tryCastTo(o, C4BooleansValue.class);
            return c4BooleansValue != null && c4BooleansValue.a == this.a && c4BooleansValue.b == this.b && c4BooleansValue.c == this.c;
        }
        
        @Override
        public IOpenGLState.Value set(final IOpenGLState.Value value) {
            final C4BooleansValue c4BooleansValue = (C4BooleansValue)value;
            this.a = c4BooleansValue.a;
            this.b = c4BooleansValue.b;
            this.c = c4BooleansValue.c;
            this.d = c4BooleansValue.d;
            return this;
        }
    }
    
    public static class CIntValue implements IOpenGLState.Value
    {
        int value;
        
        public CIntValue set(final int value) {
            this.value = value;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof CIntValue && ((CIntValue)o).value == this.value;
        }
        
        @Override
        public IOpenGLState.Value set(final IOpenGLState.Value value) {
            this.value = ((CIntValue)value).value;
            return this;
        }
    }
    
    public static final class C2IntsValue implements IOpenGLState.Value
    {
        int a;
        int b;
        
        public C2IntsValue set(final int a, final int b) {
            this.a = a;
            this.b = b;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            final C2IntsValue c2IntsValue = Type.tryCastTo(o, C2IntsValue.class);
            return c2IntsValue != null && c2IntsValue.a == this.a && c2IntsValue.b == this.b;
        }
        
        @Override
        public IOpenGLState.Value set(final IOpenGLState.Value value) {
            final C2IntsValue c2IntsValue = (C2IntsValue)value;
            this.a = c2IntsValue.a;
            this.b = c2IntsValue.b;
            return this;
        }
    }
    
    public static final class C3IntsValue implements IOpenGLState.Value
    {
        int a;
        int b;
        int c;
        
        public C3IntsValue set(final int a, final int b, final int c) {
            this.a = a;
            this.b = b;
            this.c = c;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            final C3IntsValue c3IntsValue = Type.tryCastTo(o, C3IntsValue.class);
            return c3IntsValue != null && c3IntsValue.a == this.a && c3IntsValue.b == this.b && c3IntsValue.c == this.c;
        }
        
        @Override
        public IOpenGLState.Value set(final IOpenGLState.Value value) {
            final C3IntsValue c3IntsValue = (C3IntsValue)value;
            this.a = c3IntsValue.a;
            this.b = c3IntsValue.b;
            this.c = c3IntsValue.c;
            return this;
        }
    }
    
    public static final class C4IntsValue implements IOpenGLState.Value
    {
        int a;
        int b;
        int c;
        int d;
        
        public C4IntsValue set(final int a, final int b, final int c, final int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            final C4IntsValue c4IntsValue = Type.tryCastTo(o, C4IntsValue.class);
            return c4IntsValue != null && c4IntsValue.a == this.a && c4IntsValue.b == this.b && c4IntsValue.c == this.c && c4IntsValue.d == this.d;
        }
        
        @Override
        public IOpenGLState.Value set(final IOpenGLState.Value value) {
            final C4IntsValue c4IntsValue = (C4IntsValue)value;
            this.a = c4IntsValue.a;
            this.b = c4IntsValue.b;
            this.c = c4IntsValue.c;
            this.d = c4IntsValue.d;
            return this;
        }
    }
    
    public static final class CIntFloatValue implements IOpenGLState.Value
    {
        int a;
        float b;
        
        public CIntFloatValue set(final int a, final float b) {
            this.a = a;
            this.b = b;
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            final CIntFloatValue cIntFloatValue = Type.tryCastTo(o, CIntFloatValue.class);
            return cIntFloatValue != null && cIntFloatValue.a == this.a && cIntFloatValue.b == this.b;
        }
        
        @Override
        public IOpenGLState.Value set(final IOpenGLState.Value value) {
            final CIntFloatValue cIntFloatValue = (CIntFloatValue)value;
            this.a = cIntFloatValue.a;
            this.b = cIntFloatValue.b;
            return this;
        }
    }
    
    public abstract static class BaseBoolean extends IOpenGLState<CBooleanValue>
    {
        @Override
        CBooleanValue defaultValue() {
            return new CBooleanValue(true);
        }
    }
    
    public abstract static class Base4Booleans extends IOpenGLState<C4BooleansValue>
    {
        @Override
        C4BooleansValue defaultValue() {
            return new C4BooleansValue();
        }
    }
    
    public abstract static class BaseIntFloat extends IOpenGLState<CIntFloatValue>
    {
        @Override
        CIntFloatValue defaultValue() {
            return new CIntFloatValue();
        }
    }
    
    public abstract static class BaseInt extends IOpenGLState<CIntValue>
    {
        @Override
        CIntValue defaultValue() {
            return new CIntValue();
        }
    }
    
    public abstract static class Base2Ints extends IOpenGLState<C2IntsValue>
    {
        @Override
        C2IntsValue defaultValue() {
            return new C2IntsValue();
        }
    }
    
    public abstract static class Base3Ints extends IOpenGLState<C3IntsValue>
    {
        @Override
        C3IntsValue defaultValue() {
            return new C3IntsValue();
        }
    }
    
    public abstract static class Base4Ints extends IOpenGLState<C4IntsValue>
    {
        @Override
        C4IntsValue defaultValue() {
            return new C4IntsValue();
        }
    }
    
    public static final class CAlphaFunc extends BaseIntFloat
    {
        @Override
        void Set(final CIntFloatValue cIntFloatValue) {
            SpriteRenderer.instance.glAlphaFunc(cIntFloatValue.a, cIntFloatValue.b);
        }
    }
    
    public static final class CAlphaTest extends BaseBoolean
    {
        @Override
        void Set(final CBooleanValue cBooleanValue) {
            if (cBooleanValue.value) {
                SpriteRenderer.instance.glEnable(3008);
            }
            else {
                SpriteRenderer.instance.glDisable(3008);
            }
        }
    }
    
    public static final class CBlendFunc extends Base2Ints
    {
        @Override
        void Set(final C2IntsValue c2IntsValue) {
            SpriteRenderer.instance.glBlendFunc(c2IntsValue.a, c2IntsValue.b);
        }
    }
    
    public static final class CBlendFuncSeparate extends Base4Ints
    {
        @Override
        void Set(final C4IntsValue c4IntsValue) {
            SpriteRenderer.instance.glBlendFuncSeparate(c4IntsValue.a, c4IntsValue.b, c4IntsValue.c, c4IntsValue.d);
        }
    }
    
    public static final class CColorMask extends Base4Booleans
    {
        @Override
        void Set(final C4BooleansValue c4BooleansValue) {
            SpriteRenderer.instance.glColorMask(c4BooleansValue.a ? 1 : 0, c4BooleansValue.b ? 1 : 0, c4BooleansValue.c ? 1 : 0, c4BooleansValue.d ? 1 : 0);
        }
    }
    
    public static final class CStencilFunc extends Base3Ints
    {
        @Override
        void Set(final C3IntsValue c3IntsValue) {
            SpriteRenderer.instance.glStencilFunc(c3IntsValue.a, c3IntsValue.b, c3IntsValue.c);
        }
    }
    
    public static final class CStencilMask extends BaseInt
    {
        @Override
        void Set(final CIntValue cIntValue) {
            SpriteRenderer.instance.glStencilMask(cIntValue.value);
        }
    }
    
    public static final class CStencilOp extends Base3Ints
    {
        @Override
        void Set(final C3IntsValue c3IntsValue) {
            SpriteRenderer.instance.glStencilOp(c3IntsValue.a, c3IntsValue.b, c3IntsValue.c);
        }
    }
    
    public static final class CStencilTest extends BaseBoolean
    {
        @Override
        void Set(final CBooleanValue cBooleanValue) {
            if (cBooleanValue.value) {
                SpriteRenderer.instance.glEnable(2960);
            }
            else {
                SpriteRenderer.instance.glDisable(2960);
            }
        }
    }
}
