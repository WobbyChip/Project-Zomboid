// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.core.Core;
import java.util.Map;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.IsoGameCharacter;
import java.util.HashMap;

public class ActionPacket implements INetworkPacket
{
    private short id;
    private boolean operation;
    private float reloadSpeed;
    private boolean override;
    private String primary;
    private String secondary;
    private final HashMap<String, String> variables;
    private IsoGameCharacter character;
    
    public ActionPacket() {
        this.variables = new HashMap<String, String>();
    }
    
    public void set(final boolean operation, final BaseAction baseAction) {
        this.character = baseAction.chr;
        this.id = baseAction.chr.getOnlineID();
        this.operation = operation;
        this.reloadSpeed = baseAction.chr.getVariableFloat("ReloadSpeed", 1.0f);
        this.override = baseAction.overrideHandModels;
        this.primary = ((baseAction.getPrimaryHandItem() == null) ? baseAction.getPrimaryHandMdl() : baseAction.getPrimaryHandItem().getStaticModel());
        this.secondary = ((baseAction.getSecondaryHandItem() == null) ? baseAction.getSecondaryHandMdl() : baseAction.getSecondaryHandItem().getStaticModel());
        for (final String key : baseAction.animVariables) {
            this.variables.put(key, baseAction.chr.getVariableString(key));
        }
        if (this.variables.containsValue("DetachItem") || this.variables.containsValue("AttachItem")) {
            this.variables.put("AttachAnim", baseAction.chr.getVariableString("AttachAnim"));
        }
        if (this.variables.containsValue("Loot")) {
            this.variables.put("LootPosition", baseAction.chr.getVariableString("LootPosition"));
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.id = byteBuffer.getShort();
        this.operation = (byteBuffer.get() != 0);
        this.reloadSpeed = byteBuffer.getFloat();
        this.override = (byteBuffer.get() != 0);
        this.primary = GameWindow.ReadString(byteBuffer);
        this.secondary = GameWindow.ReadString(byteBuffer);
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            this.variables.put(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
        }
        if (GameServer.bServer) {
            this.character = GameServer.IDToPlayerMap.get(this.id);
        }
        else if (GameClient.bClient) {
            this.character = GameClient.IDToPlayerMap.get(this.id);
        }
        else {
            this.character = null;
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putBoolean(this.operation);
        byteBufferWriter.putFloat(this.reloadSpeed);
        byteBufferWriter.putBoolean(this.override);
        byteBufferWriter.putUTF(this.primary);
        byteBufferWriter.putUTF(this.secondary);
        byteBufferWriter.putInt(this.variables.size());
        for (final Map.Entry<String, String> entry : this.variables.entrySet()) {
            byteBufferWriter.putUTF(entry.getKey());
            byteBufferWriter.putUTF(entry.getValue());
        }
    }
    
    @Override
    public boolean isConsistent() {
        final boolean b = this.character instanceof IsoPlayer;
        if (!b && Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, "[Action] is not consistent");
        }
        return b;
    }
    
    @Override
    public String getDescription() {
        final StringBuilder append = new StringBuilder("[ ").append("character=").append(this.id);
        if (this.isConsistent()) {
            append.append(" \"").append(((IsoPlayer)this.character).getUsername()).append("\"");
        }
        append.append(" | ").append("operation=").append(this.operation ? "start" : "stop").append(" | ").append("variables=").append(this.variables.size()).append(" | ");
        for (final Map.Entry<String, String> entry : this.variables.entrySet()) {
            append.append(entry.getKey()).append("=").append(entry.getValue()).append(" | ");
        }
        append.append("override=").append(this.override).append(" ").append("primary=\"").append((this.primary == null) ? "" : this.primary).append("\" ").append("secondary=\"").append((this.secondary == null) ? "" : this.secondary).append("\" ]");
        return append.toString();
    }
    
    public boolean isRelevant(final UdpConnection udpConnection) {
        return this.isConsistent() && udpConnection.RelevantTo(this.character.getX(), this.character.getY());
    }
    
    public void process() {
        if (this.isConsistent()) {
            if (this.operation) {
                final BaseAction action = new BaseAction(this.character);
                final BaseAction baseAction;
                this.variables.forEach((s, s2) -> {
                    if ("true".equals(s2) || "false".equals(s2)) {
                        baseAction.setAnimVariable(s, Boolean.parseBoolean(s2));
                    }
                    else {
                        baseAction.setAnimVariable(s, s2);
                    }
                    return;
                });
                if ("Reload".equals(this.variables.get("PerformingAction"))) {
                    this.character.setVariable("ReloadSpeed", this.reloadSpeed);
                }
                this.character.setVariable("IsPerformingAnAction", true);
                this.character.getNetworkCharacterAI().setAction(action);
                this.character.getNetworkCharacterAI().setOverride(this.override, this.primary, this.secondary);
                this.character.getNetworkCharacterAI().startAction();
            }
            else if (this.character.getNetworkCharacterAI().getAction() != null) {
                this.character.getNetworkCharacterAI().stopAction();
            }
        }
        else {
            DebugLog.Multiplayer.warn(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, this.id));
        }
    }
}
