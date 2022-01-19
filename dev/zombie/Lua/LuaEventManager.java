// 
// Decompiled by Procyon v0.5.36
// 

package zombie.Lua;

import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Prototype;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.KahluaTable;
import java.util.Arrays;
import zombie.debug.DebugLog;
import java.util.HashMap;
import se.krka.kahlua.vm.LuaClosure;
import java.util.ArrayList;
import se.krka.kahlua.vm.JavaFunction;

public final class LuaEventManager implements JavaFunction
{
    public static final ArrayList<LuaClosure> OnTickCallbacks;
    static Object[][] a1;
    static Object[][] a2;
    static Object[][] a3;
    static Object[][] a4;
    static Object[][] a5;
    static Object[][] a6;
    static int a1index;
    static int a2index;
    static int a3index;
    static int a4index;
    static int a5index;
    static int a6index;
    private static final ArrayList<Event> EventList;
    private static final HashMap<String, Event> EventMap;
    
    private static Event checkEvent(final String key) {
        Event addEvent = LuaEventManager.EventMap.get(key);
        if (addEvent == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key));
            addEvent = AddEvent(key);
        }
        if (addEvent.callbacks.isEmpty()) {
            return null;
        }
        return addEvent;
    }
    
    public static void triggerEvent(final String s) {
        synchronized (LuaEventManager.EventMap) {
            final Event checkEvent = checkEvent(s);
            if (checkEvent == null) {
                return;
            }
            checkEvent.trigger(LuaManager.env, LuaManager.caller, null);
        }
    }
    
    public static void triggerEvent(final String s, final Object o) {
        synchronized (LuaEventManager.EventMap) {
            final Event checkEvent = checkEvent(s);
            if (checkEvent == null) {
                return;
            }
            if (LuaEventManager.a1index == LuaEventManager.a1.length) {
                LuaEventManager.a1 = Arrays.copyOf(LuaEventManager.a1, LuaEventManager.a1.length * 2);
                for (int i = LuaEventManager.a1index; i < LuaEventManager.a1.length; ++i) {
                    LuaEventManager.a1[i] = new Object[1];
                }
            }
            final Object[] array = LuaEventManager.a1[LuaEventManager.a1index];
            array[0] = o;
            ++LuaEventManager.a1index;
            try {
                checkEvent.trigger(LuaManager.env, LuaManager.caller, array);
            }
            finally {
                --LuaEventManager.a1index;
                array[0] = null;
            }
        }
    }
    
    public static void triggerEventGarbage(final String s, final Object o) {
        triggerEvent(s, o);
    }
    
    public static void triggerEventUnique(final String s, final Object o) {
        triggerEvent(s, o);
    }
    
    public static void triggerEvent(final String s, final Object o, final Object o2) {
        synchronized (LuaEventManager.EventMap) {
            final Event checkEvent = checkEvent(s);
            if (checkEvent == null) {
                return;
            }
            if (LuaEventManager.a2index == LuaEventManager.a2.length) {
                LuaEventManager.a2 = Arrays.copyOf(LuaEventManager.a2, LuaEventManager.a2.length * 2);
                for (int i = LuaEventManager.a2index; i < LuaEventManager.a2.length; ++i) {
                    LuaEventManager.a2[i] = new Object[2];
                }
            }
            final Object[] array = LuaEventManager.a2[LuaEventManager.a2index];
            array[0] = o;
            array[1] = o2;
            ++LuaEventManager.a2index;
            try {
                checkEvent.trigger(LuaManager.env, LuaManager.caller, array);
            }
            finally {
                --LuaEventManager.a2index;
                array[1] = (array[0] = null);
            }
        }
    }
    
    public static void triggerEventGarbage(final String s, final Object o, final Object o2) {
        triggerEvent(s, o, o2);
    }
    
    public static void triggerEvent(final String s, final Object o, final Object o2, final Object o3) {
        synchronized (LuaEventManager.EventMap) {
            final Event checkEvent = checkEvent(s);
            if (checkEvent == null) {
                return;
            }
            if (LuaEventManager.a3index == LuaEventManager.a3.length) {
                LuaEventManager.a3 = Arrays.copyOf(LuaEventManager.a3, LuaEventManager.a3.length * 2);
                for (int i = LuaEventManager.a3index; i < LuaEventManager.a3.length; ++i) {
                    LuaEventManager.a3[i] = new Object[3];
                }
            }
            final Object[] array = LuaEventManager.a3[LuaEventManager.a3index];
            array[0] = o;
            array[1] = o2;
            array[2] = o3;
            ++LuaEventManager.a3index;
            try {
                checkEvent.trigger(LuaManager.env, LuaManager.caller, array);
            }
            finally {
                --LuaEventManager.a3index;
                array[0] = null;
                array[2] = (array[1] = null);
            }
        }
    }
    
    public static void triggerEventGarbage(final String s, final Object o, final Object o2, final Object o3) {
        triggerEvent(s, o, o2, o3);
    }
    
    public static void triggerEvent(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        synchronized (LuaEventManager.EventMap) {
            final Event checkEvent = checkEvent(s);
            if (checkEvent == null) {
                return;
            }
            if (LuaEventManager.a4index == LuaEventManager.a4.length) {
                LuaEventManager.a4 = Arrays.copyOf(LuaEventManager.a4, LuaEventManager.a4.length * 2);
                for (int i = LuaEventManager.a4index; i < LuaEventManager.a4.length; ++i) {
                    LuaEventManager.a4[i] = new Object[4];
                }
            }
            final Object[] array = LuaEventManager.a4[LuaEventManager.a4index];
            array[0] = o;
            array[1] = o2;
            array[2] = o3;
            array[3] = o4;
            ++LuaEventManager.a4index;
            try {
                checkEvent.trigger(LuaManager.env, LuaManager.caller, array);
            }
            finally {
                --LuaEventManager.a4index;
                array[1] = (array[0] = null);
                array[3] = (array[2] = null);
            }
        }
    }
    
    public static void triggerEventGarbage(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        triggerEvent(s, o, o2, o3, o4);
    }
    
    public static void triggerEvent(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        synchronized (LuaEventManager.EventMap) {
            final Event checkEvent = checkEvent(s);
            if (checkEvent == null) {
                return;
            }
            if (LuaEventManager.a5index == LuaEventManager.a5.length) {
                LuaEventManager.a5 = Arrays.copyOf(LuaEventManager.a5, LuaEventManager.a5.length * 2);
                for (int i = LuaEventManager.a5index; i < LuaEventManager.a5.length; ++i) {
                    LuaEventManager.a5[i] = new Object[5];
                }
            }
            final Object[] array = LuaEventManager.a5[LuaEventManager.a5index];
            array[0] = o;
            array[1] = o2;
            array[2] = o3;
            array[3] = o4;
            array[4] = o5;
            ++LuaEventManager.a5index;
            try {
                checkEvent.trigger(LuaManager.env, LuaManager.caller, array);
            }
            finally {
                --LuaEventManager.a5index;
                array[0] = null;
                array[2] = (array[1] = null);
                array[4] = (array[3] = null);
            }
        }
    }
    
    public static void triggerEvent(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        synchronized (LuaEventManager.EventMap) {
            final Event checkEvent = checkEvent(s);
            if (checkEvent == null) {
                return;
            }
            if (LuaEventManager.a6index == LuaEventManager.a6.length) {
                LuaEventManager.a6 = Arrays.copyOf(LuaEventManager.a6, LuaEventManager.a6.length * 2);
                for (int i = LuaEventManager.a6index; i < LuaEventManager.a6.length; ++i) {
                    LuaEventManager.a6[i] = new Object[6];
                }
            }
            final Object[] array = LuaEventManager.a6[LuaEventManager.a6index];
            array[0] = o;
            array[1] = o2;
            array[2] = o3;
            array[3] = o4;
            array[4] = o5;
            array[5] = o6;
            ++LuaEventManager.a6index;
            try {
                checkEvent.trigger(LuaManager.env, LuaManager.caller, array);
            }
            finally {
                --LuaEventManager.a6index;
                array[1] = (array[0] = null);
                array[3] = (array[2] = null);
                array[5] = (array[4] = null);
            }
        }
    }
    
    public static Event AddEvent(final String s) {
        final Event event = LuaEventManager.EventMap.get(s);
        if (event != null) {
            return event;
        }
        final Event event2 = new Event(s, LuaEventManager.EventList.size());
        LuaEventManager.EventList.add(event2);
        LuaEventManager.EventMap.put(s, event2);
        final Object rawget = LuaManager.env.rawget((Object)"Events");
        if (rawget instanceof KahluaTable) {
            event2.register((Platform)LuaManager.platform, (KahluaTable)rawget);
        }
        else {
            DebugLog.log("ERROR: 'Events' table not found or not a table");
        }
        return event2;
    }
    
    private static void AddEvents() {
        AddEvent("OnGameBoot");
        AddEvent("OnPreGameStart");
        AddEvent("OnTick");
        AddEvent("OnTickEvenPaused");
        AddEvent("OnRenderUpdate");
        AddEvent("OnFETick");
        AddEvent("OnGameStart");
        AddEvent("OnPreUIDraw");
        AddEvent("OnPostUIDraw");
        AddEvent("OnCharacterCollide");
        AddEvent("OnKeyStartPressed");
        AddEvent("OnKeyPressed");
        AddEvent("OnObjectCollide");
        AddEvent("OnNPCSurvivorUpdate");
        AddEvent("OnPlayerUpdate");
        AddEvent("OnZombieUpdate");
        AddEvent("OnTriggerNPCEvent");
        AddEvent("OnMultiTriggerNPCEvent");
        AddEvent("OnLoadMapZones");
        AddEvent("OnAddBuilding");
        AddEvent("OnCreateLivingCharacter");
        AddEvent("OnChallengeQuery");
        AddEvent("OnFillInventoryObjectContextMenu");
        AddEvent("OnPreFillInventoryObjectContextMenu");
        AddEvent("OnFillWorldObjectContextMenu");
        AddEvent("OnPreFillWorldObjectContextMenu");
        AddEvent("OnRefreshInventoryWindowContainers");
        AddEvent("OnGamepadConnect");
        AddEvent("OnGamepadDisconnect");
        AddEvent("OnJoypadActivate");
        AddEvent("OnJoypadActivateUI");
        AddEvent("OnJoypadBeforeDeactivate");
        AddEvent("OnJoypadDeactivate");
        AddEvent("OnJoypadBeforeReactivate");
        AddEvent("OnJoypadReactivate");
        AddEvent("OnJoypadRenderUI");
        AddEvent("OnMakeItem");
        AddEvent("OnWeaponHitCharacter");
        AddEvent("OnWeaponSwing");
        AddEvent("OnWeaponHitTree");
        AddEvent("OnWeaponHitXp");
        AddEvent("OnWeaponSwingHitPoint");
        AddEvent("OnPlayerAttackFinished");
        AddEvent("OnLoginState");
        AddEvent("OnLoginStateSuccess");
        AddEvent("OnCharacterCreateStats");
        AddEvent("OnLoadSoundBanks");
        AddEvent("OnObjectLeftMouseButtonDown");
        AddEvent("OnObjectLeftMouseButtonUp");
        AddEvent("OnObjectRightMouseButtonDown");
        AddEvent("OnObjectRightMouseButtonUp");
        AddEvent("OnDoTileBuilding");
        AddEvent("OnDoTileBuilding2");
        AddEvent("OnDoTileBuilding3");
        AddEvent("OnConnectFailed");
        AddEvent("OnConnected");
        AddEvent("OnDisconnect");
        AddEvent("OnConnectionStateChanged");
        AddEvent("OnScoreboardUpdate");
        AddEvent("OnMouseMove");
        AddEvent("OnMouseDown");
        AddEvent("OnMouseUp");
        AddEvent("OnRightMouseDown");
        AddEvent("OnRightMouseUp");
        AddEvent("OnNewSurvivorGroup");
        AddEvent("OnPlayerSetSafehouse");
        AddEvent("OnLoad");
        AddEvent("AddXP");
        AddEvent("LevelPerk");
        AddEvent("OnSave");
        AddEvent("OnMainMenuEnter");
        AddEvent("OnPreMapLoad");
        AddEvent("OnPostFloorSquareDraw");
        AddEvent("OnPostFloorLayerDraw");
        AddEvent("OnPostTilesSquareDraw");
        AddEvent("OnPostTileDraw");
        AddEvent("OnPostWallSquareDraw");
        AddEvent("OnPostCharactersSquareDraw");
        AddEvent("OnCreateUI");
        AddEvent("OnMapLoadCreateIsoObject");
        AddEvent("OnCreateSurvivor");
        AddEvent("OnCreatePlayer");
        AddEvent("OnPlayerDeath");
        AddEvent("OnZombieDead");
        AddEvent("OnCharacterDeath");
        AddEvent("OnCharacterMeet");
        AddEvent("OnSpawnRegionsLoaded");
        AddEvent("OnPostMapLoad");
        AddEvent("OnAIStateExecute");
        AddEvent("OnAIStateEnter");
        AddEvent("OnAIStateExit");
        AddEvent("OnAIStateChange");
        AddEvent("OnPlayerMove");
        AddEvent("OnInitWorld");
        AddEvent("OnNewGame");
        AddEvent("OnIsoThumpableLoad");
        AddEvent("OnIsoThumpableSave");
        AddEvent("ReuseGridsquare");
        AddEvent("LoadGridsquare");
        AddEvent("EveryOneMinute");
        AddEvent("EveryTenMinutes");
        AddEvent("EveryDays");
        AddEvent("EveryHours");
        AddEvent("OnDusk");
        AddEvent("OnDawn");
        AddEvent("OnEquipPrimary");
        AddEvent("OnEquipSecondary");
        AddEvent("OnClothingUpdated");
        AddEvent("OnWeatherPeriodStart");
        AddEvent("OnWeatherPeriodStage");
        AddEvent("OnWeatherPeriodComplete");
        AddEvent("OnWeatherPeriodStop");
        AddEvent("OnRainStart");
        AddEvent("OnRainStop");
        AddEvent("OnAmbientSound");
        AddEvent("OnWorldSound");
        AddEvent("OnResetLua");
        AddEvent("OnModsModified");
        AddEvent("OnSeeNewRoom");
        AddEvent("OnNewFire");
        AddEvent("OnFillContainer");
        AddEvent("OnChangeWeather");
        AddEvent("OnRenderTick");
        AddEvent("OnDestroyIsoThumpable");
        AddEvent("OnPostSave");
        AddEvent("OnResolutionChange");
        AddEvent("OnWaterAmountChange");
        AddEvent("OnClientCommand");
        AddEvent("OnServerCommand");
        AddEvent("OnContainerUpdate");
        AddEvent("OnObjectAdded");
        AddEvent("OnObjectAboutToBeRemoved");
        AddEvent("onLoadModDataFromServer");
        AddEvent("OnGameTimeLoaded");
        AddEvent("OnCGlobalObjectSystemInit");
        AddEvent("OnSGlobalObjectSystemInit");
        AddEvent("OnWorldMessage");
        AddEvent("OnKeyKeepPressed");
        AddEvent("SendCustomModData");
        AddEvent("ServerPinged");
        AddEvent("OnServerStarted");
        AddEvent("OnLoadedTileDefinitions");
        AddEvent("OnPostRender");
        AddEvent("DoSpecialTooltip");
        AddEvent("OnCoopJoinFailed");
        AddEvent("OnServerWorkshopItems");
        AddEvent("OnVehicleDamageTexture");
        AddEvent("OnCustomUIKey");
        AddEvent("OnCustomUIKeyPressed");
        AddEvent("OnCustomUIKeyReleased");
        AddEvent("OnDeviceText");
        AddEvent("OnRadioInteraction");
        AddEvent("OnLoadRadioScripts");
        AddEvent("OnAcceptInvite");
        AddEvent("OnCoopServerMessage");
        AddEvent("OnReceiveUserlog");
        AddEvent("OnAdminMessage");
        AddEvent("OnGetDBSchema");
        AddEvent("OnGetTableResult");
        AddEvent("ReceiveFactionInvite");
        AddEvent("AcceptedFactionInvite");
        AddEvent("ReceiveSafehouseInvite");
        AddEvent("AcceptedSafehouseInvite");
        AddEvent("ViewTickets");
        AddEvent("SyncFaction");
        AddEvent("OnReceiveItemListNet");
        AddEvent("OnMiniScoreboardUpdate");
        AddEvent("OnSafehousesChanged");
        AddEvent("RequestTrade");
        AddEvent("AcceptedTrade");
        AddEvent("TradingUIAddItem");
        AddEvent("TradingUIRemoveItem");
        AddEvent("TradingUIUpdateState");
        AddEvent("OnGridBurnt");
        AddEvent("OnPreDistributionMerge");
        AddEvent("OnDistributionMerge");
        AddEvent("OnPostDistributionMerge");
        AddEvent("MngInvReceiveItems");
        AddEvent("OnTileRemoved");
        AddEvent("OnServerStartSaving");
        AddEvent("OnServerFinishSaving");
        AddEvent("OnMechanicActionDone");
        AddEvent("OnClimateTick");
        AddEvent("OnThunderEvent");
        AddEvent("OnEnterVehicle");
        AddEvent("OnSteamGameJoin");
        AddEvent("OnTabAdded");
        AddEvent("OnSetDefaultTab");
        AddEvent("OnTabRemoved");
        AddEvent("OnAddMessage");
        AddEvent("SwitchChatStream");
        AddEvent("OnChatWindowInit");
        AddEvent("OnInitSeasons");
        AddEvent("OnClimateTickDebug");
        AddEvent("OnInitModdedWeatherStage");
        AddEvent("OnUpdateModdedWeatherStage");
        AddEvent("OnClimateManagerInit");
        AddEvent("OnPressReloadButton");
        AddEvent("OnPressRackButton");
        AddEvent("OnHitZombie");
        AddEvent("OnBeingHitByZombie");
        AddEvent("OnServerStatisticReceived");
        AddEvent("OnDynamicMovableRecipe");
        AddEvent("OnInitGlobalModData");
        AddEvent("OnReceiveGlobalModData");
        AddEvent("OnInitRecordedMedia");
        AddEvent("onUpdateIcon");
        AddEvent("preAddForageDefs");
        AddEvent("preAddZoneDefs");
        AddEvent("preAddCatDefs");
        AddEvent("preAddItemDefs");
        AddEvent("onAddForageDefs");
        AddEvent("onFillSearchIconContextMenu");
        AddEvent("onItemFall");
    }
    
    public static void clear() {
    }
    
    public static void register(final Platform platform, final KahluaTable kahluaTable) {
        kahluaTable.rawset((Object)"Events", (Object)platform.newTable());
        AddEvents();
    }
    
    public static void reroute(final Prototype prototype, final LuaClosure element) {
        for (int i = 0; i < LuaEventManager.EventList.size(); ++i) {
            final Event event = LuaEventManager.EventList.get(i);
            for (int j = 0; j < event.callbacks.size(); ++j) {
                final LuaClosure luaClosure = event.callbacks.get(j);
                if (luaClosure.prototype.filename.equals(prototype.filename) && luaClosure.prototype.name.equals(prototype.name)) {
                    event.callbacks.set(j, element);
                }
            }
        }
    }
    
    public static void Reset() {
        for (int i = 0; i < LuaEventManager.EventList.size(); ++i) {
            LuaEventManager.EventList.get(i).callbacks.clear();
        }
        LuaEventManager.EventList.clear();
        LuaEventManager.EventMap.clear();
    }
    
    public static void ResetCallbacks() {
        for (int i = 0; i < LuaEventManager.EventList.size(); ++i) {
            LuaEventManager.EventList.get(i).callbacks.clear();
        }
    }
    
    public int call(final LuaCallFrame luaCallFrame, final int n) {
        return 0;
    }
    
    private int OnTick(final LuaCallFrame luaCallFrame, final int n) {
        return 0;
    }
    
    static {
        OnTickCallbacks = new ArrayList<LuaClosure>();
        LuaEventManager.a1 = new Object[1][1];
        LuaEventManager.a2 = new Object[1][2];
        LuaEventManager.a3 = new Object[1][3];
        LuaEventManager.a4 = new Object[1][4];
        LuaEventManager.a5 = new Object[1][5];
        LuaEventManager.a6 = new Object[1][6];
        LuaEventManager.a1index = 0;
        LuaEventManager.a2index = 0;
        LuaEventManager.a3index = 0;
        LuaEventManager.a4index = 0;
        LuaEventManager.a5index = 0;
        LuaEventManager.a6index = 0;
        EventList = new ArrayList<Event>();
        EventMap = new HashMap<String, Event>();
    }
}
