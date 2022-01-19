// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.iso.LosUtil;
import java.util.Collection;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoPlayer;
import java.util.ArrayList;

public class ServerLOS
{
    public static ServerLOS instance;
    private LOSThread thread;
    private ArrayList<PlayerData> playersMain;
    private ArrayList<PlayerData> playersLOS;
    private boolean bMapLoading;
    private boolean bSuspended;
    boolean bWasSuspended;
    
    public ServerLOS() {
        this.playersMain = new ArrayList<PlayerData>();
        this.playersLOS = new ArrayList<PlayerData>();
        this.bMapLoading = false;
        this.bSuspended = false;
    }
    
    private void noise(final String s) {
    }
    
    public static void init() {
        (ServerLOS.instance = new ServerLOS()).start();
    }
    
    public void start() {
        (this.thread = new LOSThread()).setName("LOS");
        this.thread.setDaemon(true);
        this.thread.start();
    }
    
    public void addPlayer(final IsoPlayer isoPlayer) {
        synchronized (this.playersMain) {
            if (this.findData(isoPlayer) != null) {
                return;
            }
            this.playersMain.add(new PlayerData(isoPlayer));
            synchronized (this.thread.notifier) {
                this.thread.notifier.notify();
            }
        }
    }
    
    public void removePlayer(final IsoPlayer isoPlayer) {
        synchronized (this.playersMain) {
            this.playersMain.remove(this.findData(isoPlayer));
            synchronized (this.thread.notifier) {
                this.thread.notifier.notify();
            }
        }
    }
    
    public boolean isCouldSee(final IsoPlayer isoPlayer, final IsoGridSquare isoGridSquare) {
        final PlayerData data = this.findData(isoPlayer);
        if (data != null) {
            final int n = isoGridSquare.x - data.px + 50;
            final int n2 = isoGridSquare.y - data.py + 50;
            if (n >= 0 && n < 100 && n2 >= 0 && n2 < 100) {
                return data.visible[n][n2][isoGridSquare.z];
            }
        }
        return false;
    }
    
    public void doServerZombieLOS(final IsoPlayer isoPlayer) {
        if (!ServerMap.instance.bUpdateLOSThisFrame) {
            return;
        }
        final PlayerData data = this.findData(isoPlayer);
        if (data == null) {
            return;
        }
        if (data.status == UpdateStatus.NeverDone) {
            data.status = UpdateStatus.ReadyInMain;
        }
        if (data.status == UpdateStatus.ReadyInMain) {
            data.status = UpdateStatus.WaitingInLOS;
            this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoPlayer.OnlineID));
            synchronized (this.thread.notifier) {
                this.thread.notifier.notify();
            }
        }
    }
    
    public void updateLOS(final IsoPlayer isoPlayer) {
        final PlayerData data = this.findData(isoPlayer);
        if (data == null) {
            return;
        }
        if (data.status == UpdateStatus.ReadyInLOS || data.status == UpdateStatus.ReadyInMain) {
            if (data.status == UpdateStatus.ReadyInLOS) {
                this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoPlayer.OnlineID));
            }
            data.status = UpdateStatus.BusyInMain;
            isoPlayer.updateLOS();
            data.status = UpdateStatus.ReadyInMain;
            synchronized (this.thread.notifier) {
                this.thread.notifier.notify();
            }
        }
    }
    
    private PlayerData findData(final IsoPlayer isoPlayer) {
        for (int i = 0; i < this.playersMain.size(); ++i) {
            if (this.playersMain.get(i).player == isoPlayer) {
                return this.playersMain.get(i);
            }
        }
        return null;
    }
    
    public void suspend() {
        this.bMapLoading = true;
        this.bWasSuspended = this.bSuspended;
        while (!this.bSuspended) {
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException ex) {}
        }
        if (!this.bWasSuspended) {
            this.noise("suspend **********");
        }
    }
    
    public void resume() {
        this.bMapLoading = false;
        synchronized (this.thread.notifier) {
            this.thread.notifier.notify();
        }
        if (!this.bWasSuspended) {
            this.noise("resume **********");
        }
    }
    
    enum UpdateStatus
    {
        NeverDone, 
        WaitingInLOS, 
        BusyInLOS, 
        ReadyInLOS, 
        BusyInMain, 
        ReadyInMain;
        
        private static /* synthetic */ UpdateStatus[] $values() {
            return new UpdateStatus[] { UpdateStatus.NeverDone, UpdateStatus.WaitingInLOS, UpdateStatus.BusyInLOS, UpdateStatus.ReadyInLOS, UpdateStatus.BusyInMain, UpdateStatus.ReadyInMain };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static final class ServerLighting implements IsoGridSquare.ILighting
    {
        private static final byte LOS_SEEN = 1;
        private static final byte LOS_COULD_SEE = 2;
        private static final byte LOS_CAN_SEE = 4;
        private static ColorInfo lightInfo;
        private byte los;
        
        @Override
        public int lightverts(final int n) {
            return 0;
        }
        
        @Override
        public float lampostTotalR() {
            return 0.0f;
        }
        
        @Override
        public float lampostTotalG() {
            return 0.0f;
        }
        
        @Override
        public float lampostTotalB() {
            return 0.0f;
        }
        
        @Override
        public boolean bSeen() {
            return (this.los & 0x1) != 0x0;
        }
        
        @Override
        public boolean bCanSee() {
            return (this.los & 0x4) != 0x0;
        }
        
        @Override
        public boolean bCouldSee() {
            return (this.los & 0x2) != 0x0;
        }
        
        @Override
        public float darkMulti() {
            return 0.0f;
        }
        
        @Override
        public float targetDarkMulti() {
            return 0.0f;
        }
        
        @Override
        public ColorInfo lightInfo() {
            ServerLighting.lightInfo.r = 1.0f;
            ServerLighting.lightInfo.g = 1.0f;
            ServerLighting.lightInfo.b = 1.0f;
            return ServerLighting.lightInfo;
        }
        
        @Override
        public void lightverts(final int n, final int n2) {
        }
        
        @Override
        public void lampostTotalR(final float n) {
        }
        
        @Override
        public void lampostTotalG(final float n) {
        }
        
        @Override
        public void lampostTotalB(final float n) {
        }
        
        @Override
        public void bSeen(final boolean b) {
            if (b) {
                this.los |= 0x1;
            }
            else {
                this.los &= 0xFFFFFFFE;
            }
        }
        
        @Override
        public void bCanSee(final boolean b) {
            if (b) {
                this.los |= 0x4;
            }
            else {
                this.los &= 0xFFFFFFFB;
            }
        }
        
        @Override
        public void bCouldSee(final boolean b) {
            if (b) {
                this.los |= 0x2;
            }
            else {
                this.los &= 0xFFFFFFFD;
            }
        }
        
        @Override
        public void darkMulti(final float n) {
        }
        
        @Override
        public void targetDarkMulti(final float n) {
        }
        
        @Override
        public int resultLightCount() {
            return 0;
        }
        
        @Override
        public IsoGridSquare.ResultLight getResultLight(final int n) {
            return null;
        }
        
        @Override
        public void reset() {
            this.los = 0;
        }
        
        static {
            ServerLighting.lightInfo = new ColorInfo();
        }
    }
    
    private class LOSThread extends Thread
    {
        public Object notifier;
        
        private LOSThread() {
            this.notifier = new Object();
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        this.runInner();
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
                break;
            }
        }
        
        private void runInner() {
            MPStatistic.getInstance().ServerLOS.Start();
            synchronized (ServerLOS.this.playersMain) {
                ServerLOS.this.playersLOS.clear();
                ServerLOS.this.playersLOS.addAll(ServerLOS.this.playersMain);
            }
            for (int i = 0; i < ServerLOS.this.playersLOS.size(); ++i) {
                final PlayerData playerData = ServerLOS.this.playersLOS.get(i);
                if (playerData.status == UpdateStatus.WaitingInLOS) {
                    playerData.status = UpdateStatus.BusyInLOS;
                    ServerLOS.this.noise(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, playerData.player.OnlineID));
                    this.calcLOS(playerData);
                    playerData.status = UpdateStatus.ReadyInLOS;
                }
                if (ServerLOS.this.bMapLoading) {
                    break;
                }
            }
            MPStatistic.getInstance().ServerLOS.End();
            while (this.shouldWait()) {
                ServerLOS.this.bSuspended = true;
                synchronized (this.notifier) {
                    try {
                        this.notifier.wait();
                    }
                    catch (InterruptedException ex) {}
                }
            }
            ServerLOS.this.bSuspended = false;
        }
        
        private void calcLOS(final PlayerData playerData) {
            boolean b = false;
            if (playerData.px == (int)playerData.player.getX() && playerData.py == (int)playerData.player.getY() && playerData.pz == (int)playerData.player.getZ()) {
                b = true;
            }
            playerData.px = (int)playerData.player.getX();
            playerData.py = (int)playerData.player.getY();
            playerData.pz = (int)playerData.player.getZ();
            playerData.player.initLightInfo2();
            if (b) {
                return;
            }
            final int n = 0;
            for (int i = 0; i < LosUtil.XSIZE; ++i) {
                for (int j = 0; j < LosUtil.YSIZE; ++j) {
                    for (int k = 0; k < LosUtil.ZSIZE; ++k) {
                        LosUtil.cachedresults[i][j][k][n] = 0;
                    }
                }
            }
            try {
                IsoPlayer.players[n] = playerData.player;
                final int px = playerData.px;
                final int py = playerData.py;
                for (int l = -50; l < 50; ++l) {
                    for (int n2 = -50; n2 < 50; ++n2) {
                        for (int n3 = 0; n3 < 8; ++n3) {
                            final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(l + px, n2 + py, n3);
                            if (gridSquare != null) {
                                gridSquare.CalcVisibility(n);
                                playerData.visible[l + 50][n2 + 50][n3] = gridSquare.isCouldSee(n);
                                gridSquare.checkRoomSeen(n);
                            }
                        }
                    }
                }
            }
            finally {
                IsoPlayer.players[n] = null;
            }
        }
        
        private boolean shouldWait() {
            if (ServerLOS.this.bMapLoading) {
                return true;
            }
            for (int i = 0; i < ServerLOS.this.playersLOS.size(); ++i) {
                if (ServerLOS.this.playersLOS.get(i).status == UpdateStatus.WaitingInLOS) {
                    return false;
                }
            }
            synchronized (ServerLOS.this.playersMain) {
                if (ServerLOS.this.playersLOS.size() != ServerLOS.this.playersMain.size()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private class PlayerData
    {
        public IsoPlayer player;
        public UpdateStatus status;
        public int px;
        public int py;
        public int pz;
        public boolean[][][] visible;
        
        public PlayerData(final IsoPlayer player) {
            this.status = UpdateStatus.NeverDone;
            this.visible = new boolean[100][100][8];
            this.player = player;
        }
    }
}
