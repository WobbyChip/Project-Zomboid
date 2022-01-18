// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Network extends OptionGroup
{
    public final Client Client;
    public final Server Server;
    public final PublicServerUtil PublicServerUtil;
    
    public Network() {
        super("Network");
        this.Client = new Client(this.Group);
        this.Server = new Server(this.Group);
        this.PublicServerUtil = new PublicServerUtil(this.Group);
    }
    
    public final class Client extends OptionGroup
    {
        public final BooleanDebugOption MainLoop;
        public final BooleanDebugOption UpdateZombiesFromPacket;
        public final BooleanDebugOption SyncIsoObject;
        
        public Client(final IDebugOptionGroup debugOptionGroup) {
            super(debugOptionGroup, "Client");
            this.MainLoop = OptionGroup.newDebugOnlyOption(this.Group, "MainLoop", true);
            this.UpdateZombiesFromPacket = OptionGroup.newDebugOnlyOption(this.Group, "UpdateZombiesFromPacket", true);
            this.SyncIsoObject = OptionGroup.newDebugOnlyOption(this.Group, "SyncIsoObject", true);
        }
    }
    
    public final class Server extends OptionGroup
    {
        public final BooleanDebugOption SyncIsoObject;
        
        public Server(final IDebugOptionGroup debugOptionGroup) {
            super(debugOptionGroup, "Server");
            this.SyncIsoObject = OptionGroup.newDebugOnlyOption(this.Group, "SyncIsoObject", true);
        }
    }
    
    public final class PublicServerUtil extends OptionGroup
    {
        public final BooleanDebugOption Enabled;
        
        public PublicServerUtil(final IDebugOptionGroup debugOptionGroup) {
            super(debugOptionGroup, "PublicServerUtil");
            this.Enabled = OptionGroup.newDebugOnlyOption(this.Group, "Enabled", true);
        }
    }
}
