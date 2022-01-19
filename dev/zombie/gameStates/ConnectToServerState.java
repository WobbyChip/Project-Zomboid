// 
// Decompiled by Procyon v0.5.36
// 

package zombie.gameStates;

import zombie.savefile.ClientPlayerDB;
import zombie.world.WorldDictionary;
import zombie.globalObjects.CGlobalObjects;
import zombie.erosion.ErosionConfig;
import java.io.IOException;
import zombie.core.logger.ExceptionLogger;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.network.ServerOptions;
import zombie.Lua.LuaManager;
import zombie.iso.IsoChunkMap;
import java.util.Collection;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.ISteamWorkshopCallback;
import zombie.core.znet.SteamWorkshopItem;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamUtils;
import zombie.core.Translator;
import zombie.network.CoopMaster;
import zombie.SystemDisabler;
import zombie.core.SpriteRenderer;
import zombie.Lua.LuaEventManager;
import zombie.network.GameClient;
import zombie.core.Core;
import zombie.GameWindow;
import zombie.debug.DebugLog;
import java.util.ArrayList;
import java.nio.ByteBuffer;

public final class ConnectToServerState extends GameState
{
    public static ConnectToServerState instance;
    private ByteBuffer connectionDetails;
    private State state;
    private ArrayList<WorkshopItem> workshopItems;
    private ArrayList<WorkshopItem> confirmItems;
    private ItemQuery query;
    
    private static void noise(final String s) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
    }
    
    public ConnectToServerState(final ByteBuffer src) {
        this.workshopItems = new ArrayList<WorkshopItem>();
        this.confirmItems = new ArrayList<WorkshopItem>();
        (this.connectionDetails = ByteBuffer.allocate(src.capacity())).put(src);
        this.connectionDetails.rewind();
    }
    
    @Override
    public void enter() {
        ConnectToServerState.instance = this;
        this.state = State.Start;
    }
    
    @Override
    public GameStateMachine.StateAction update() {
        switch (this.state) {
            case Start: {
                this.Start();
                break;
            }
            case TestTCP: {
                this.TestTCP();
                break;
            }
            case WorkshopInit: {
                this.WorkshopInit();
                break;
            }
            case WorkshopConfirm: {
                this.WorkshopConfirm();
                break;
            }
            case WorkshopQuery: {
                this.WorkshopQuery();
                break;
            }
            case ServerWorkshopItemScreen: {
                this.ServerWorkshopItemScreen();
                break;
            }
            case WorkshopUpdate: {
                this.WorkshopUpdate();
                break;
            }
            case CheckMods: {
                this.CheckMods();
                break;
            }
            case Finish: {
                this.Finish();
                break;
            }
            case Exit: {
                return GameStateMachine.StateAction.Continue;
            }
        }
        return GameStateMachine.StateAction.Remain;
    }
    
    private void Start() {
        noise("Start");
        final ByteBuffer connectionDetails = this.connectionDetails;
        if (connectionDetails.get() == 1) {
            Core.GameSaveWorld = invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, connectionDetails.getLong(), GameWindow.ReadStringUTF(connectionDetails));
        }
        GameClient.instance.ID = connectionDetails.get();
        connectionDetails.getInt();
        this.state = State.TestTCP;
    }
    
    private void TestTCP() {
        noise("TestTCP");
        final ByteBuffer connectionDetails = this.connectionDetails;
        if (connectionDetails.get() == 1) {
            LuaEventManager.triggerEvent("OnConnectionStateChanged", "TestTCP");
            if (SpriteRenderer.instance != null) {
                GameWindow.render();
            }
            if (Core.bDebug) {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException ex) {}
            }
        }
        GameClient.accessLevel = GameWindow.ReadStringUTF(connectionDetails);
        if (!SystemDisabler.getAllowDebugConnections() && Core.bDebug && !SystemDisabler.getOverrideServerConnectDebugCheck() && !GameClient.accessLevel.equals("admin") && !CoopMaster.instance.isRunning()) {
            LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_DebugNotAllowed"));
            GameClient.connection.forceDisconnect();
            this.state = State.Exit;
            return;
        }
        GameClient.GameMap = GameWindow.ReadStringUTF(connectionDetails);
        if (GameClient.GameMap.contains(";")) {
            Core.GameMap = GameClient.GameMap.split(";")[0].trim();
        }
        else {
            Core.GameMap = GameClient.GameMap.trim();
        }
        if (SteamUtils.isSteamModeEnabled()) {
            this.state = State.WorkshopInit;
        }
        else {
            this.state = State.CheckMods;
        }
    }
    
    private void WorkshopInit() {
        final ByteBuffer connectionDetails = this.connectionDetails;
        for (short short1 = connectionDetails.getShort(), n = 0; n < short1; ++n) {
            this.workshopItems.add(new WorkshopItem(connectionDetails.getLong(), connectionDetails.getLong()));
        }
        this.state = State.WorkshopConfirm;
    }
    
    private void WorkshopConfirm() {
        this.confirmItems.clear();
        for (int i = 0; i < this.workshopItems.size(); ++i) {
            final WorkshopItem e = this.workshopItems.get(i);
            final long getItemState = SteamWorkshop.instance.GetItemState(e.ID);
            noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, SteamWorkshopItem.ItemState.toString(getItemState), e.ID));
            if (getItemState != (SteamWorkshopItem.ItemState.Subscribed.getValue() | SteamWorkshopItem.ItemState.Installed.getValue())) {
                this.confirmItems.add(e);
            }
        }
        if (this.confirmItems.isEmpty()) {
            this.state = State.WorkshopUpdate;
        }
        else {
            final long[] array = new long[this.workshopItems.size()];
            final ArrayList<String> list = new ArrayList<String>();
            for (int j = 0; j < this.workshopItems.size(); ++j) {
                final WorkshopItem workshopItem = this.workshopItems.get(j);
                array[j] = workshopItem.ID;
                list.add(SteamUtils.convertSteamIDToString(workshopItem.ID));
            }
            this.query = new ItemQuery();
            this.query.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(array, this.query);
            if (this.query.handle != 0L) {
                LuaEventManager.triggerEvent("OnServerWorkshopItems", "Required", list);
                this.state = State.WorkshopQuery;
            }
            else {
                this.query = null;
                LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_CreateQueryUGCDetailsRequest"));
                GameClient.connection.forceDisconnect();
                this.state = State.Exit;
            }
        }
    }
    
    private void WorkshopQuery() {
        if (this.query.isCompleted()) {
            final ArrayList<SteamUGCDetails> details = this.query.details;
            this.query = null;
            this.state = State.ServerWorkshopItemScreen;
            LuaEventManager.triggerEvent("OnServerWorkshopItems", "Details", details);
            return;
        }
        if (this.query.isNotCompleted()) {
            this.query = null;
            this.state = State.ServerWorkshopItemScreen;
            LuaEventManager.triggerEvent("OnServerWorkshopItems", "Error", "ItemQueryNotCompleted");
        }
    }
    
    private void ServerWorkshopItemScreen() {
    }
    
    private void WorkshopUpdate() {
        for (int i = 0; i < this.workshopItems.size(); ++i) {
            final WorkshopItem workshopItem = this.workshopItems.get(i);
            workshopItem.update();
            if (workshopItem.state == WorkshopItemState.Fail) {
                this.state = State.ServerWorkshopItemScreen;
                LuaEventManager.triggerEvent("OnServerWorkshopItems", "Error", workshopItem.ID, workshopItem.error);
                return;
            }
            if (workshopItem.state != WorkshopItemState.Ready) {
                return;
            }
        }
        ZomboidFileSystem.instance.resetModFolders();
        LuaEventManager.triggerEvent("OnServerWorkshopItems", "Success");
        this.state = State.CheckMods;
    }
    
    private void CheckMods() {
        final ByteBuffer connectionDetails = this.connectionDetails;
        final ArrayList<String> c = new ArrayList<String>();
        final HashMap<String, ChooseGameInfo.Mod> hashMap = new HashMap<String, ChooseGameInfo.Mod>();
        for (int int1 = connectionDetails.getInt(), i = 0; i < int1; ++i) {
            final ChooseGameInfo.Mod mod = new ChooseGameInfo.Mod(GameWindow.ReadStringUTF(connectionDetails));
            mod.setUrl(GameWindow.ReadStringUTF(connectionDetails));
            mod.setName(GameWindow.ReadStringUTF(connectionDetails));
            c.add(mod.getDir());
            hashMap.put(mod.getDir(), mod);
        }
        GameClient.instance.ServerMods.clear();
        GameClient.instance.ServerMods.addAll(c);
        c.clear();
        final String loadModsAux = ZomboidFileSystem.instance.loadModsAux(GameClient.instance.ServerMods, c);
        if (loadModsAux != null) {
            String text = Translator.getText("UI_OnConnectFailed_ModRequired", loadModsAux);
            if (hashMap.get(loadModsAux) != null && !"".equals(hashMap.get(loadModsAux).getUrl())) {
                text = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, text, hashMap.get(loadModsAux).getUrl());
            }
            LuaEventManager.triggerEvent("OnConnectFailed", text);
            GameClient.connection.forceDisconnect();
            this.state = State.Exit;
            return;
        }
        this.state = State.Finish;
    }
    
    private void Finish() {
        final ByteBuffer connectionDetails = this.connectionDetails;
        LuaEventManager.triggerEvent("OnConnectionStateChanged", "Connected");
        IsoChunkMap.MPWorldXA = connectionDetails.getInt();
        IsoChunkMap.MPWorldYA = connectionDetails.getInt();
        IsoChunkMap.MPWorldZA = connectionDetails.getInt();
        GameClient.username = GameClient.username.trim();
        Core.GameMode = "Multiplayer";
        LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
        GameClient.instance.bConnected = true;
        for (int int1 = connectionDetails.getInt(), i = 0; i < int1; ++i) {
            ServerOptions.instance.putOption(GameWindow.ReadString(connectionDetails), GameWindow.ReadString(connectionDetails));
        }
        try {
            Core.getInstance().ResetLua("client", "ConnectedToServer");
            Core.GameMode = "Multiplayer";
            GameClient.connection.ip = GameClient.ip;
            SandboxOptions.instance.load(connectionDetails);
            SandboxOptions.instance.applySettings();
            SandboxOptions.instance.toLua();
            GameTime.getInstance().load(connectionDetails);
            GameTime.getInstance().save();
        }
        catch (IOException ex) {
            ExceptionLogger.logException(ex);
        }
        (GameClient.instance.erosionConfig = new ErosionConfig()).load(connectionDetails);
        try {
            CGlobalObjects.loadInitialState(connectionDetails);
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
        }
        GameClient.instance.setResetID(connectionDetails.getInt());
        Core.getInstance().setPoisonousBerry(GameWindow.ReadString(connectionDetails));
        GameClient.poisonousBerry = Core.getInstance().getPoisonousBerry();
        Core.getInstance().setPoisonousMushroom(GameWindow.ReadString(connectionDetails));
        GameClient.poisonousMushroom = Core.getInstance().getPoisonousMushroom();
        GameClient.connection.isCoopHost = (connectionDetails.get() == 1);
        try {
            WorldDictionary.loadDataFromServer(connectionDetails);
        }
        catch (Exception ex2) {
            ExceptionLogger.logException(ex2);
            LuaEventManager.triggerEvent("OnConnectFailed", "WorldDictionary error");
            GameClient.connection.forceDisconnect();
            this.state = State.Exit;
        }
        ClientPlayerDB.setAllow(true);
        LuaEventManager.triggerEvent("OnConnected");
        this.state = State.Exit;
    }
    
    public void FromLua(final String s) {
        if (this.state != State.ServerWorkshopItemScreen) {
            throw new IllegalStateException("state != ServerWorkshopItemScreen");
        }
        if ("install".equals(s)) {
            this.state = State.WorkshopUpdate;
            return;
        }
        if ("disconnect".equals(s)) {
            LuaEventManager.triggerEvent("OnConnectFailed", "ServerWorkshopItemsCancelled");
            GameClient.connection.forceDisconnect();
            this.state = State.Exit;
        }
    }
    
    @Override
    public void exit() {
        ConnectToServerState.instance = null;
    }
    
    private enum State
    {
        Start, 
        TestTCP, 
        WorkshopInit, 
        WorkshopConfirm, 
        WorkshopQuery, 
        ServerWorkshopItemScreen, 
        WorkshopUpdate, 
        CheckMods, 
        Finish, 
        Exit;
        
        private static /* synthetic */ State[] $values() {
            return new State[] { State.Start, State.TestTCP, State.WorkshopInit, State.WorkshopConfirm, State.WorkshopQuery, State.ServerWorkshopItemScreen, State.WorkshopUpdate, State.CheckMods, State.Finish, State.Exit };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private class ItemQuery implements ISteamWorkshopCallback
    {
        long handle;
        ArrayList<SteamUGCDetails> details;
        boolean bCompleted;
        boolean bNotCompleted;
        
        public boolean isCompleted() {
            return this.bCompleted;
        }
        
        public boolean isNotCompleted() {
            return this.bNotCompleted;
        }
        
        @Override
        public void onItemCreated(final long n, final boolean b) {
        }
        
        @Override
        public void onItemNotCreated(final int n) {
        }
        
        @Override
        public void onItemUpdated(final boolean b) {
        }
        
        @Override
        public void onItemNotUpdated(final int n) {
        }
        
        @Override
        public void onItemSubscribed(final long n) {
        }
        
        @Override
        public void onItemNotSubscribed(final long n, final int n2) {
        }
        
        @Override
        public void onItemDownloaded(final long n) {
        }
        
        @Override
        public void onItemNotDownloaded(final long n, final int n2) {
        }
        
        @Override
        public void onItemQueryCompleted(final long n, final int n2) {
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
            if (n != this.handle) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            final ArrayList<SteamUGCDetails> details = new ArrayList<SteamUGCDetails>();
            for (int i = 0; i < n2; ++i) {
                final SteamUGCDetails getQueryUGCResult = SteamWorkshop.instance.GetQueryUGCResult(n, i);
                if (getQueryUGCResult != null) {
                    details.add(getQueryUGCResult);
                }
            }
            this.details = details;
            SteamWorkshop.instance.ReleaseQueryUGCRequest(n);
            this.bCompleted = true;
        }
        
        @Override
        public void onItemQueryNotCompleted(final long n, final int n2) {
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
            if (n != this.handle) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            SteamWorkshop.instance.ReleaseQueryUGCRequest(n);
            this.bNotCompleted = true;
        }
    }
    
    private enum WorkshopItemState
    {
        CheckItemState, 
        SubscribePending, 
        DownloadPending, 
        Ready, 
        Fail;
        
        private static /* synthetic */ WorkshopItemState[] $values() {
            return new WorkshopItemState[] { WorkshopItemState.CheckItemState, WorkshopItemState.SubscribePending, WorkshopItemState.DownloadPending, WorkshopItemState.Ready, WorkshopItemState.Fail };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private class WorkshopItem implements ISteamWorkshopCallback
    {
        long ID;
        long serverTimeStamp;
        WorkshopItemState state;
        boolean subscribed;
        long downloadStartTime;
        long downloadQueryTime;
        String error;
        
        WorkshopItem(final long id, final long serverTimeStamp) {
            this.state = WorkshopItemState.CheckItemState;
            this.ID = id;
            this.serverTimeStamp = serverTimeStamp;
        }
        
        void update() {
            switch (this.state) {
                case CheckItemState: {
                    this.CheckItemState();
                    break;
                }
                case SubscribePending: {
                    this.SubscribePending();
                    break;
                }
                case DownloadPending: {
                    this.DownloadPending();
                    break;
                }
            }
        }
        
        void setState(final WorkshopItemState state) {
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(Lzombie/gameStates/ConnectToServerState$WorkshopItemState;Lzombie/gameStates/ConnectToServerState$WorkshopItemState;J)Ljava/lang/String;, this.state, state, this.ID));
            this.state = state;
        }
        
        void CheckItemState() {
            final long getItemState = SteamWorkshop.instance.GetItemState(this.ID);
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, SteamWorkshopItem.ItemState.toString(getItemState), this.ID));
            if (!SteamWorkshopItem.ItemState.Subscribed.and(getItemState)) {
                if (SteamWorkshop.instance.SubscribeItem(this.ID, this)) {
                    this.setState(WorkshopItemState.SubscribePending);
                    return;
                }
                this.error = "SubscribeItemFalse";
                this.setState(WorkshopItemState.Fail);
            }
            else if (SteamWorkshopItem.ItemState.NeedsUpdate.and(getItemState)) {
                if (SteamWorkshop.instance.DownloadItem(this.ID, true, this)) {
                    this.setState(WorkshopItemState.DownloadPending);
                    this.downloadStartTime = System.currentTimeMillis();
                    return;
                }
                this.error = "DownloadItemFalse";
                this.setState(WorkshopItemState.Fail);
            }
            else {
                if (!SteamWorkshopItem.ItemState.Installed.and(getItemState)) {
                    this.error = "UnknownItemState";
                    this.setState(WorkshopItemState.Fail);
                    return;
                }
                final long getItemInstallTimeStamp = SteamWorkshop.instance.GetItemInstallTimeStamp(this.ID);
                if (getItemInstallTimeStamp == 0L) {
                    this.error = "GetItemInstallTimeStamp";
                    this.setState(WorkshopItemState.Fail);
                    return;
                }
                if (getItemInstallTimeStamp != this.serverTimeStamp) {
                    this.error = "VersionMismatch";
                    this.setState(WorkshopItemState.Fail);
                    return;
                }
                this.setState(WorkshopItemState.Ready);
            }
        }
        
        void SubscribePending() {
            if (this.subscribed && SteamWorkshopItem.ItemState.Subscribed.and(SteamWorkshop.instance.GetItemState(this.ID))) {
                this.setState(WorkshopItemState.CheckItemState);
            }
        }
        
        void DownloadPending() {
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.downloadQueryTime + 100L > currentTimeMillis) {
                return;
            }
            this.downloadQueryTime = currentTimeMillis;
            if (SteamWorkshopItem.ItemState.NeedsUpdate.and(SteamWorkshop.instance.GetItemState(this.ID))) {
                final long[] array = new long[2];
                if (SteamWorkshop.instance.GetItemDownloadInfo(this.ID, array)) {
                    ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(JJJ)Ljava/lang/String;, array[0], array[1], this.ID));
                    LuaEventManager.triggerEvent("OnServerWorkshopItems", "Progress", SteamUtils.convertSteamIDToString(this.ID), array[0], Math.max(array[1], 1L));
                }
            }
        }
        
        @Override
        public void onItemCreated(final long n, final boolean b) {
        }
        
        @Override
        public void onItemNotCreated(final int n) {
        }
        
        @Override
        public void onItemUpdated(final boolean b) {
        }
        
        @Override
        public void onItemNotUpdated(final int n) {
        }
        
        @Override
        public void onItemSubscribed(final long n) {
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
            if (n != this.ID) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            this.subscribed = true;
        }
        
        @Override
        public void onItemNotSubscribed(final long n, final int n2) {
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
            if (n != this.ID) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            this.error = "ItemNotSubscribed";
            this.setState(WorkshopItemState.Fail);
        }
        
        @Override
        public void onItemDownloaded(final long n) {
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, n, System.currentTimeMillis() - this.downloadStartTime));
            if (n != this.ID) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            this.setState(WorkshopItemState.CheckItemState);
        }
        
        @Override
        public void onItemNotDownloaded(final long n, final int n2) {
            ConnectToServerState.noise(invokedynamic(makeConcatWithConstants:(JI)Ljava/lang/String;, n, n2));
            if (n != this.ID) {
                return;
            }
            SteamWorkshop.instance.RemoveCallback(this);
            this.error = "ItemNotDownloaded";
            this.setState(WorkshopItemState.Fail);
        }
        
        @Override
        public void onItemQueryCompleted(final long n, final int n2) {
        }
        
        @Override
        public void onItemQueryNotCompleted(final long n, final int n2) {
        }
    }
}
