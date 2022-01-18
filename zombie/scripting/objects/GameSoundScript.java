// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import zombie.audio.GameSoundClip;
import java.util.Iterator;
import zombie.scripting.ScriptParser;
import zombie.audio.GameSound;

public final class GameSoundScript extends BaseScriptObject
{
    public final GameSound gameSound;
    
    public GameSoundScript() {
        this.gameSound = new GameSound();
    }
    
    public void Load(final String name, final String s) {
        this.gameSound.name = name;
        final ScriptParser.Block block = ScriptParser.parse(s).children.get(0);
        final Iterator<ScriptParser.Value> iterator = block.values.iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().string.split("=");
            final String trim = split[0].trim();
            final String trim2 = split[1].trim();
            if ("category".equals(trim)) {
                this.gameSound.category = trim2;
            }
            else if ("is3D".equals(trim)) {
                this.gameSound.is3D = Boolean.parseBoolean(trim2);
            }
            else if ("loop".equals(trim)) {
                this.gameSound.loop = Boolean.parseBoolean(trim2);
            }
            else {
                if (!"master".equals(trim)) {
                    continue;
                }
                this.gameSound.master = GameSound.MasterVolume.valueOf(trim2);
            }
        }
        for (final ScriptParser.Block block2 : block.children) {
            if ("clip".equals(block2.type)) {
                this.gameSound.clips.add(this.LoadClip(block2));
            }
        }
    }
    
    private GameSoundClip LoadClip(final ScriptParser.Block block) {
        final GameSoundClip gameSoundClip = new GameSoundClip(this.gameSound);
        final Iterator<ScriptParser.Value> iterator = block.values.iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().string.split("=");
            final String trim = split[0].trim();
            final String trim2 = split[1].trim();
            if ("distanceMax".equals(trim)) {
                gameSoundClip.distanceMax = (float)Integer.parseInt(trim2);
                final GameSoundClip gameSoundClip2 = gameSoundClip;
                gameSoundClip2.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MAX;
            }
            else if ("distanceMin".equals(trim)) {
                gameSoundClip.distanceMin = (float)Integer.parseInt(trim2);
                final GameSoundClip gameSoundClip3 = gameSoundClip;
                gameSoundClip3.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MIN;
            }
            else if ("event".equals(trim)) {
                gameSoundClip.event = trim2;
            }
            else if ("file".equals(trim)) {
                gameSoundClip.file = trim2;
            }
            else if ("pitch".equals(trim)) {
                gameSoundClip.pitch = Float.parseFloat(trim2);
            }
            else if ("volume".equals(trim)) {
                gameSoundClip.volume = Float.parseFloat(trim2);
            }
            else if ("reverbFactor".equals(trim)) {
                gameSoundClip.reverbFactor = Float.parseFloat(trim2);
            }
            else {
                if (!"reverbMaxRange".equals(trim)) {
                    continue;
                }
                gameSoundClip.reverbMaxRange = Float.parseFloat(trim2);
            }
        }
        return gameSoundClip;
    }
    
    public void reset() {
        this.gameSound.reset();
    }
}
