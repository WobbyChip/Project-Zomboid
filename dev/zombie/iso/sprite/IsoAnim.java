// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import zombie.network.ServerGUI;
import zombie.network.GameServer;
import zombie.core.textures.Texture;
import java.util.ArrayList;
import java.util.HashMap;

public final class IsoAnim
{
    public static final HashMap<String, IsoAnim> GlobalAnimMap;
    public short FinishUnloopedOnFrame;
    public short FrameDelay;
    public short LastFrame;
    public final ArrayList<IsoDirectionFrame> Frames;
    public String name;
    boolean looped;
    public int ID;
    private static final ThreadLocal<StringBuilder> tlsStrBuf;
    public IsoDirectionFrame[] FramesArray;
    
    public IsoAnim() {
        this.FinishUnloopedOnFrame = 0;
        this.FrameDelay = 0;
        this.LastFrame = 0;
        this.Frames = new ArrayList<IsoDirectionFrame>(8);
        this.looped = true;
        this.ID = 0;
        this.FramesArray = new IsoDirectionFrame[0];
    }
    
    public static void DisposeAll() {
        IsoAnim.GlobalAnimMap.clear();
    }
    
    void LoadExtraFrame(final String s, final String name, final int value) {
        this.name = name;
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        final Integer n = new Integer(value);
        this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n.toString()))));
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesReverseAltName(final String s, final String s2, final String name, final int n) {
        this.name = name;
        final StringBuilder sb = IsoAnim.tlsStrBuf.get();
        sb.setLength(0);
        sb.append(s);
        sb.append("_%_");
        sb.append(s2);
        sb.append("_^");
        final int lastIndex = sb.lastIndexOf("^");
        final int n2 = sb.indexOf("_%_") + 1;
        sb.setCharAt(n2, '9');
        sb.setCharAt(lastIndex, '0');
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            for (int i = 0; i < n; ++i) {
                this.Frames.add(new IsoDirectionFrame(null));
            }
            this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
            this.FramesArray = this.Frames.toArray(this.FramesArray);
        }
        final Texture sharedTexture = Texture.getSharedTexture(sb.toString());
        if (sharedTexture == null) {
            return;
        }
        for (int j = 0; j < n; ++j) {
            if (j == 10) {
                sb.setLength(0);
                sb.append(s);
                sb.append("_1_");
                sb.append(s2);
                sb.append("_10");
            }
            final Integer value = j;
            final String string = value.toString();
            IsoDirectionFrame element;
            if (sharedTexture != null) {
                sb.setCharAt(n2, '9');
                for (int k = 0; k < string.length(); ++k) {
                    sb.setCharAt(lastIndex + k, string.charAt(k));
                }
                final String string2 = sb.toString();
                sb.setCharAt(n2, '6');
                final String string3 = sb.toString();
                sb.setCharAt(n2, '3');
                final String string4 = sb.toString();
                sb.setCharAt(n2, '2');
                final String string5 = sb.toString();
                sb.setCharAt(n2, '1');
                final String string6 = sb.toString();
                sb.setCharAt(n2, '4');
                final String string7 = sb.toString();
                sb.setCharAt(n2, '7');
                final String string8 = sb.toString();
                sb.setCharAt(n2, '8');
                element = new IsoDirectionFrame(Texture.getSharedTexture(string2), Texture.getSharedTexture(string3), Texture.getSharedTexture(string4), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7), Texture.getSharedTexture(string8), Texture.getSharedTexture(sb.toString()));
            }
            else {
                sb.setCharAt(n2, '8');
                try {
                    sb.setCharAt(lastIndex, value.toString().charAt(0));
                }
                catch (Exception ex) {
                    this.LoadFramesReverseAltName(s, s2, name, n);
                }
                final String string9 = sb.toString();
                sb.setCharAt(n2, '9');
                final String string10 = sb.toString();
                sb.setCharAt(n2, '6');
                final String string11 = sb.toString();
                sb.setCharAt(n2, '3');
                final String string12 = sb.toString();
                sb.setCharAt(n2, '2');
                element = new IsoDirectionFrame(Texture.getSharedTexture(string9), Texture.getSharedTexture(string10), Texture.getSharedTexture(string11), Texture.getSharedTexture(string12), Texture.getSharedTexture(sb.toString()));
            }
            this.Frames.add(0, element);
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFrames(final String s, final String str, final int n) {
        this.name = str;
        final StringBuilder sb = IsoAnim.tlsStrBuf.get();
        sb.setLength(0);
        sb.append(s);
        sb.append("_%_");
        sb.append(str);
        sb.append("_^");
        int n2 = sb.indexOf("_%_") + 1;
        int n3 = sb.lastIndexOf("^");
        sb.setCharAt(n2, '9');
        sb.setCharAt(n3, '0');
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            for (int i = 0; i < n; ++i) {
                this.Frames.add(new IsoDirectionFrame(null));
            }
            this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        }
        final Texture sharedTexture = Texture.getSharedTexture(sb.toString());
        if (sharedTexture == null) {
            return;
        }
        for (int j = 0; j < n; ++j) {
            if (j % 10 == 0 && j > 0) {
                sb.setLength(0);
                sb.append(s);
                sb.append("_%_");
                sb.append(str);
                sb.append("_^_");
                n2 = sb.indexOf("_%_") + 1;
                n3 = sb.lastIndexOf("^");
            }
            final Integer value = j;
            final String string = value.toString();
            IsoDirectionFrame e;
            if (sharedTexture != null) {
                sb.setCharAt(n2, '9');
                for (int k = 0; k < string.length(); ++k) {
                    sb.setCharAt(n3 + k, string.charAt(k));
                }
                final String string2 = sb.toString();
                sb.setCharAt(n2, '6');
                final String string3 = sb.toString();
                sb.setCharAt(n2, '3');
                final String string4 = sb.toString();
                sb.setCharAt(n2, '2');
                final String string5 = sb.toString();
                sb.setCharAt(n2, '1');
                final String string6 = sb.toString();
                sb.setCharAt(n2, '4');
                final String string7 = sb.toString();
                sb.setCharAt(n2, '7');
                final String string8 = sb.toString();
                sb.setCharAt(n2, '8');
                e = new IsoDirectionFrame(Texture.getSharedTexture(string2), Texture.getSharedTexture(string3), Texture.getSharedTexture(string4), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7), Texture.getSharedTexture(string8), Texture.getSharedTexture(sb.toString()));
            }
            else {
                try {
                    sb.setCharAt(n2, '8');
                }
                catch (Exception ex) {
                    this.LoadFrames(s, str, n);
                }
                for (int l = 0; l < string.length(); ++l) {
                    try {
                        sb.setCharAt(n3 + l, value.toString().charAt(l));
                    }
                    catch (Exception ex2) {
                        this.LoadFrames(s, str, n);
                    }
                }
                final String string9 = sb.toString();
                sb.setCharAt(n2, '9');
                final String string10 = sb.toString();
                sb.setCharAt(n2, '6');
                final String string11 = sb.toString();
                sb.setCharAt(n2, '3');
                final String string12 = sb.toString();
                sb.setCharAt(n2, '2');
                e = new IsoDirectionFrame(Texture.getSharedTexture(string9), Texture.getSharedTexture(string10), Texture.getSharedTexture(string11), Texture.getSharedTexture(string12), Texture.getSharedTexture(sb.toString()));
            }
            this.Frames.add(e);
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesUseOtherFrame(final String s, final String s2, final String name, final String s3, final int value, final String s4) {
        this.name = name;
        final String s5 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s2);
        final String s6 = "_";
        String s7 = "";
        if (s4 != null) {
            s7 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4);
        }
        for (int i = 0; i < 1; ++i) {
            final Integer n = new Integer(value);
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s5, s6, n.toString(), s7)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s5, s6, n.toString(), s7)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s5, s6, n.toString(), s7)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s5, s6, n.toString(), s7)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s5, s6, n.toString(), s7))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesBits(final String s, final String s2, final String name, final int n) {
        this.name = name;
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2);
        final String s4 = "_";
        for (int i = 0; i < n; ++i) {
            final Integer n2 = new Integer(i);
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString()))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesBits(final String s, final String name, final int n) {
        this.name = name;
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, name);
        final String s3 = "_";
        for (int i = 0; i < n; ++i) {
            final Integer n2 = new Integer(i);
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString()))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesBitRepeatFrame(final String s, final String name, final int value) {
        this.name = name;
        final String s2 = "_";
        final String s3 = "";
        final Integer n = new Integer(value);
        this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2, n.toString(), s3)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2, n.toString(), s3)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2, n.toString(), s3)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2, n.toString(), s3)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2, n.toString(), s3))));
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesBitRepeatFrame(final String s, final String s2, final String name, final int value, final String s3) {
        this.name = name;
        final String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2);
        final String s5 = "_";
        String s6 = "";
        if (s3 != null) {
            s6 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3);
        }
        final Integer n = new Integer(value);
        this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n.toString(), s6))));
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesBits(final String s, final String s2, final String name, final int n, final String s3) {
        this.name = name;
        final String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, s2);
        final String s5 = "_";
        String s6 = "";
        if (s3 != null) {
            s6 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3);
        }
        for (int i = 0; i < n; ++i) {
            final Integer n2 = new Integer(i);
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n2.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n2.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n2.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n2.toString(), s6)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s4, s5, n2.toString(), s6))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesPcx(final String s, final String name, final int n) {
        this.name = name;
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        for (int i = 0; i < n; ++i) {
            final Integer n2 = new Integer(i);
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString())), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, n2.toString()))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    void Dispose() {
        for (int i = 0; i < this.Frames.size(); ++i) {
            this.Frames.get(i).SetAllDirections(null);
        }
    }
    
    Texture LoadFrameExplicit(final String s) {
        final Texture sharedTexture = Texture.getSharedTexture(s);
        this.Frames.add(new IsoDirectionFrame(sharedTexture));
        this.FramesArray = this.Frames.toArray(this.FramesArray);
        return sharedTexture;
    }
    
    void LoadFramesNoDir(final String s, final String name, final int n) {
        this.name = name;
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        for (int i = 0; i < n; ++i) {
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, s3, new Integer(i).toString()))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    void LoadFramesNoDirPage(final String s, final String name, final int n) {
        this.name = name;
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        for (int i = 0; i < n; ++i) {
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2, new Integer(i).toString()))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    void LoadFramesNoDirPageDirect(final String s, final String name, final int n) {
        this.name = name;
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        for (int i = 0; i < n; ++i) {
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2, new Integer(i).toString()))));
        }
        this.FramesArray = this.Frames.toArray(this.FramesArray);
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
    }
    
    void LoadFramesNoDirPage(final String s) {
        this.name = "default";
        for (int i = 0; i < 1; ++i) {
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(s)));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    public void LoadFramesPageSimple(final String s, final String s2, final String s3, final String s4) {
        this.name = "default";
        for (int i = 0; i < 1; ++i) {
            final Integer n = new Integer(i);
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(s), Texture.getSharedTexture(s2), Texture.getSharedTexture(s3), Texture.getSharedTexture(s4)));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    void LoadFramesNoDirPalette(final String s, final String name, final int n, final String s2) {
        this.name = name;
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        final String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        for (int i = 0; i < n; ++i) {
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, new Integer(i).toString()), s2)));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    void LoadFramesPalette(final String s, final String name, final int n, final String s2) {
        this.name = name;
        final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        final String s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
        for (int i = 0; i < n; ++i) {
            final Integer n2 = new Integer(i);
            this.Frames.add(new IsoDirectionFrame(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString(), s2)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString(), s2)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString(), s2)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString(), s2)), Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s3, s4, n2.toString(), s2))));
        }
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    void DupeFrame() {
        for (int i = 0; i < 8; ++i) {
            final IsoDirectionFrame e = new IsoDirectionFrame();
            e.directions[i] = this.Frames.get(0).directions[i];
            e.bDoFlip = this.Frames.get(0).bDoFlip;
            this.Frames.add(e);
        }
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
    
    static {
        GlobalAnimMap = new HashMap<String, IsoAnim>();
        tlsStrBuf = new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                return new StringBuilder();
            }
        };
    }
}
