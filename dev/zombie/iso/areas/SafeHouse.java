// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas;

import zombie.iso.BuildingDef;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.core.Translator;
import zombie.network.chat.ChatServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.network.ServerOptions;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.Lua.LuaEventManager;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import java.util.Calendar;
import java.util.ArrayList;

public class SafeHouse
{
    private int x;
    private int y;
    private int w;
    private int h;
    private static int diffError;
    private String owner;
    private ArrayList<String> players;
    private long lastVisited;
    private String title;
    private int playerConnected;
    private int openTimer;
    private final String id;
    public ArrayList<String> playersRespawn;
    private static final ArrayList<SafeHouse> safehouseList;
    
    public static void init() {
        SafeHouse.safehouseList.clear();
    }
    
    public static SafeHouse addSafeHouse(final int n, final int n2, final int n3, final int n4, final String owner, final boolean b) {
        final SafeHouse e = new SafeHouse(n, n2, n3, n4, owner);
        e.setOwner(owner);
        e.setLastVisited(Calendar.getInstance().getTimeInMillis());
        e.addPlayer(owner);
        SafeHouse.safehouseList.add(e);
        if (GameServer.bServer) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IIIILjava/lang/String;)Ljava/lang/String;, n, n2, n3, n4, owner));
        }
        if (GameClient.bClient && !b) {
            GameClient.sendSafehouse(e, false);
        }
        updateSafehousePlayersConnected();
        if (GameClient.bClient) {
            LuaEventManager.triggerEvent("OnSafehousesChanged");
        }
        return e;
    }
    
    public static SafeHouse addSafeHouse(final IsoGridSquare isoGridSquare, final IsoPlayer isoPlayer) {
        final String canBeSafehouse = canBeSafehouse(isoGridSquare, isoPlayer);
        if (canBeSafehouse != null && !"".equals(canBeSafehouse)) {
            return null;
        }
        return addSafeHouse(isoGridSquare.getBuilding().def.getX() - SafeHouse.diffError, isoGridSquare.getBuilding().def.getY() - SafeHouse.diffError, isoGridSquare.getBuilding().def.getW() + SafeHouse.diffError * 2, isoGridSquare.getBuilding().def.getH() + SafeHouse.diffError * 2, isoPlayer.getUsername(), false);
    }
    
    public static SafeHouse hasSafehouse(final String s) {
        for (int i = 0; i < SafeHouse.safehouseList.size(); ++i) {
            final SafeHouse safeHouse = SafeHouse.safehouseList.get(i);
            if (safeHouse.getPlayers().contains(s) || safeHouse.getOwner().equals(s)) {
                return safeHouse;
            }
        }
        return null;
    }
    
    public static SafeHouse hasSafehouse(final IsoPlayer isoPlayer) {
        return hasSafehouse(isoPlayer.getUsername());
    }
    
    public static void updateSafehousePlayersConnected() {
        for (int i = 0; i < SafeHouse.safehouseList.size(); ++i) {
            final SafeHouse safeHouse = SafeHouse.safehouseList.get(i);
            safeHouse.setPlayerConnected(0);
            for (final IsoPlayer isoPlayer : GameClient.IDToPlayerMap.values()) {
                if (safeHouse.getPlayers().contains(isoPlayer.getUsername()) || safeHouse.getOwner().equals(isoPlayer.getUsername())) {
                    safeHouse.setPlayerConnected(safeHouse.getPlayerConnected() + 1);
                }
            }
        }
    }
    
    public static SafeHouse getSafeHouse(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.getBuilding() != null && isoGridSquare.getBuilding().def != null) {
            return isSafeHouse(isoGridSquare, null, false);
        }
        return null;
    }
    
    public static SafeHouse getSafeHouse(final int n, final int n2, final int n3, final int n4) {
        for (int i = 0; i < SafeHouse.safehouseList.size(); ++i) {
            final SafeHouse safeHouse = SafeHouse.safehouseList.get(i);
            if (n == safeHouse.getX() && n3 == safeHouse.getW() && n2 == safeHouse.getY() && n4 == safeHouse.getH()) {
                return safeHouse;
            }
        }
        return null;
    }
    
    public static SafeHouse isSafeHouse(final IsoGridSquare isoGridSquare, final String s, final boolean b) {
        if (isoGridSquare == null) {
            return null;
        }
        if (GameClient.bClient) {
            final IsoPlayer playerFromUsername = GameClient.instance.getPlayerFromUsername(s);
            if (playerFromUsername != null && !playerFromUsername.accessLevel.equals("")) {
                return null;
            }
        }
        SafeHouse safeHouse = null;
        boolean b2 = false;
        for (int i = 0; i < SafeHouse.safehouseList.size(); ++i) {
            safeHouse = SafeHouse.safehouseList.get(i);
            if (isoGridSquare.getX() >= safeHouse.getX() && isoGridSquare.getX() < safeHouse.getX2() && isoGridSquare.getY() >= safeHouse.getY() && isoGridSquare.getY() < safeHouse.getY2()) {
                b2 = true;
                break;
            }
        }
        if (b2 && b && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue() && (safeHouse.getPlayerConnected() > 0 || safeHouse.getOpenTimer() > 0)) {
            return null;
        }
        if (b2 && ((s != null && safeHouse != null && !safeHouse.getPlayers().contains(s) && !safeHouse.getOwner().equals(s)) || s == null)) {
            return safeHouse;
        }
        return null;
    }
    
    public static void clearSafehouseList() {
        SafeHouse.safehouseList.clear();
    }
    
    public boolean playerAllowed(final IsoPlayer isoPlayer) {
        return this.players.contains(isoPlayer.getUsername()) || this.owner.equals(isoPlayer.getUsername()) || !isoPlayer.accessLevel.equals("");
    }
    
    public boolean playerAllowed(final String s) {
        return this.players.contains(s) || this.owner.equals(s);
    }
    
    public void addPlayer(final String s) {
        if (!this.players.contains(s)) {
            this.players.add(s);
            if (GameClient.bClient) {
                GameClient.sendSafehouse(this, false);
            }
            updateSafehousePlayersConnected();
        }
    }
    
    public void removePlayer(final String o) {
        if (this.players.contains(o)) {
            this.players.remove(o);
            this.playersRespawn.remove(o);
            if (GameClient.bClient) {
                GameClient.sendSafehouse(this, false);
            }
        }
    }
    
    public void syncSafehouse() {
        if (GameClient.bClient) {
            GameClient.sendSafehouse(this, false);
        }
    }
    
    public void removeSafeHouse(final IsoPlayer isoPlayer) {
        this.removeSafeHouse(isoPlayer, false);
    }
    
    public void removeSafeHouse(final IsoPlayer isoPlayer, final boolean b) {
        if (isoPlayer != null && !isoPlayer.getUsername().equals(this.getOwner()) && (isoPlayer.accessLevel.equals("admin") || isoPlayer.accessLevel.equals("moderator")) && !b) {
            return;
        }
        if (GameClient.bClient) {
            GameClient.sendSafehouse(this, true);
        }
        if (GameServer.bServer) {
            GameServer.sendSafehouse(this, true, null);
        }
        getSafehouseList().remove(this);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(IIIILjava/lang/String;)Ljava/lang/String;, this.x, this.y, this.w, this.h, this.getOwner()));
        if (GameClient.bClient) {
            LuaEventManager.triggerEvent("OnSafehousesChanged");
        }
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.putInt(this.getX());
        byteBuffer.putInt(this.getY());
        byteBuffer.putInt(this.getW());
        byteBuffer.putInt(this.getH());
        GameWindow.WriteString(byteBuffer, this.getOwner());
        byteBuffer.putInt(this.getPlayers().size());
        final Iterator<String> iterator = this.getPlayers().iterator();
        while (iterator.hasNext()) {
            GameWindow.WriteString(byteBuffer, iterator.next());
        }
        byteBuffer.putLong(this.getLastVisited());
        GameWindow.WriteString(byteBuffer, this.getTitle());
        byteBuffer.putInt(this.playersRespawn.size());
        for (int i = 0; i < this.playersRespawn.size(); ++i) {
            GameWindow.WriteString(byteBuffer, this.playersRespawn.get(i));
        }
    }
    
    public static SafeHouse load(final ByteBuffer byteBuffer, final int n) {
        final SafeHouse e = new SafeHouse(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt(), GameWindow.ReadString(byteBuffer));
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            e.addPlayer(GameWindow.ReadString(byteBuffer));
        }
        e.setLastVisited(byteBuffer.getLong());
        if (n >= 101) {
            e.setTitle(GameWindow.ReadString(byteBuffer));
        }
        if (ChatServer.isInited()) {
            ChatServer.getInstance().createSafehouseChat(e.getId());
        }
        SafeHouse.safehouseList.add(e);
        if (n >= 177) {
            for (int int2 = byteBuffer.getInt(), j = 0; j < int2; ++j) {
                e.playersRespawn.add(GameWindow.ReadString(byteBuffer));
            }
        }
        return e;
    }
    
    public static String canBeSafehouse(final IsoGridSquare isoGridSquare, final IsoPlayer isoPlayer) {
        if (!GameClient.bClient && !GameServer.bServer) {
            return null;
        }
        if (!ServerOptions.instance.PlayerSafehouse.getValue() && !ServerOptions.instance.AdminSafehouse.getValue()) {
            return null;
        }
        if (ServerOptions.instance.PlayerSafehouse.getValue() && hasSafehouse(isoPlayer) != null) {
            return Translator.getText("IGUI_Safehouse_AlreadyHaveSafehouse");
        }
        int value = ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue();
        if (!ServerOptions.instance.PlayerSafehouse.getValue() && ServerOptions.instance.AdminSafehouse.getValue() && GameClient.bClient) {
            if (!isoPlayer.accessLevel.equals("admin") && !isoPlayer.accessLevel.equals("moderator")) {
                return null;
            }
            value = 0;
        }
        if (value > 0 && isoPlayer.getHoursSurvived() < value * 24) {
            return Translator.getText("IGUI_Safehouse_DaysSurvivedToClaim", value);
        }
        if (GameClient.bClient) {
            final KahluaTableIterator iterator = GameClient.instance.getServerSpawnRegions().iterator();
            while (iterator.advance()) {
                final KahluaTableIterator iterator2 = ((KahluaTableImpl)((KahluaTable)iterator.getValue()).rawget((Object)"points")).iterator();
                while (iterator2.advance()) {
                    final KahluaTableIterator iterator3 = ((KahluaTable)iterator2.getValue()).iterator();
                    while (iterator3.advance()) {
                        final KahluaTable kahluaTable = (KahluaTable)iterator3.getValue();
                        final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare((double)kahluaTable.rawget((Object)"posX") + (double)kahluaTable.rawget((Object)"worldX") * 300.0, (double)kahluaTable.rawget((Object)"posY") + (double)kahluaTable.rawget((Object)"worldY") * 300.0, 0.0);
                        if (gridSquare != null && gridSquare.getBuilding() != null && gridSquare.getBuilding().getDef() != null) {
                            final BuildingDef def = gridSquare.getBuilding().getDef();
                            if (isoGridSquare.getX() >= def.getX() && isoGridSquare.getX() < def.getX2() && isoGridSquare.getY() >= def.getY() && isoGridSquare.getY() < def.getY2()) {
                                return Translator.getText("IGUI_Safehouse_IsSpawnPoint");
                            }
                            continue;
                        }
                    }
                }
            }
        }
        boolean b = true;
        boolean b2 = false;
        boolean b3 = false;
        final BuildingDef def2 = isoGridSquare.getBuilding().getDef();
        if (isoGridSquare.getBuilding().Rooms != null) {
            for (final IsoRoom isoRoom : isoGridSquare.getBuilding().Rooms) {
                if (isoRoom.getName().equals("kitchen")) {}
                if (isoRoom.getName().equals("bedroom") || isoRoom.getName().equals("livingroom")) {
                    b3 = true;
                }
                if (isoRoom.getName().equals("bathroom")) {
                    continue;
                }
            }
        }
        for (int i = def2.getX() - SafeHouse.diffError; i < def2.getX2() + SafeHouse.diffError; ++i) {
            for (int j = def2.getY() - SafeHouse.diffError; j < def2.getY2() + SafeHouse.diffError; ++j) {
                final IsoGridSquare gridSquare2 = isoGridSquare.getCell().getGridSquare(i, j, 0);
                if (gridSquare2 != null) {
                    for (int k = 0; k < gridSquare2.getMovingObjects().size(); ++k) {
                        final IsoMovingObject isoMovingObject = gridSquare2.getMovingObjects().get(k);
                        if (isoMovingObject != isoPlayer) {
                            b = false;
                            break;
                        }
                        if (!isoMovingObject.getSquare().Is(IsoFlagType.exterior)) {
                            b2 = true;
                        }
                    }
                }
            }
            if (!b) {
                break;
            }
        }
        if (!b || !b2) {
            return Translator.getText("IGUI_Safehouse_SomeoneInside");
        }
        if (!b3) {
            return Translator.getText("IGUI_Safehouse_NotHouse");
        }
        return "";
    }
    
    public void kickOutOfSafehouse(final IsoPlayer isoPlayer) {
        if (isoPlayer.getAccessLevel().equals("None")) {
            GameClient.sendTeleport(isoPlayer, (float)(this.x - 1), (float)(this.y - 1), 0.0f);
        }
    }
    
    public SafeHouse alreadyHaveSafehouse(final String s) {
        if (ServerOptions.instance.PlayerSafehouse.getValue()) {
            return hasSafehouse(s);
        }
        return null;
    }
    
    public SafeHouse alreadyHaveSafehouse(final IsoPlayer isoPlayer) {
        if (ServerOptions.instance.PlayerSafehouse.getValue()) {
            return hasSafehouse(isoPlayer);
        }
        return null;
    }
    
    public static boolean allowSafeHouse(final IsoPlayer isoPlayer) {
        boolean b = false;
        if ((GameClient.bClient || GameServer.bServer) && (ServerOptions.instance.PlayerSafehouse.getValue() || ServerOptions.instance.AdminSafehouse.getValue())) {
            if (ServerOptions.instance.PlayerSafehouse.getValue()) {
                b = (hasSafehouse(isoPlayer) == null);
            }
            if (b && ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue() > 0 && isoPlayer.getHoursSurvived() / 24.0 < ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue()) {
                b = false;
            }
            if (ServerOptions.instance.AdminSafehouse.getValue() && GameClient.bClient) {
                b = (isoPlayer.accessLevel.equals("admin") || isoPlayer.accessLevel.equals("moderator"));
            }
        }
        return b;
    }
    
    public void updateSafehouse(final IsoPlayer isoPlayer) {
        if (isoPlayer != null && (this.getPlayers().contains(isoPlayer.getUsername()) || this.getOwner().equals(isoPlayer.getUsername()))) {
            this.setLastVisited(Calendar.getInstance().getTimeInMillis());
        }
        else if (ServerOptions.instance.SafeHouseRemovalTime.getValue() > 0 && Calendar.getInstance().getTimeInMillis() - this.getLastVisited() > 3600000 * ServerOptions.instance.SafeHouseRemovalTime.getValue()) {
            boolean b = false;
            for (int i = this.getX(); i < this.getX2(); ++i) {
                for (int j = this.getY(); j < this.getY2(); ++j) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(i, j, 0);
                    if (gridSquare != null) {
                        for (int k = 0; k < gridSquare.getMovingObjects().size(); ++k) {
                            final IsoMovingObject isoMovingObject = gridSquare.getMovingObjects().get(k);
                            if (isoMovingObject instanceof IsoPlayer && (this.getPlayers().contains(((IsoPlayer)isoMovingObject).getUsername()) || this.getOwner().equals(((IsoPlayer)isoMovingObject).getUsername()))) {
                                b = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (b) {
                this.setLastVisited(Calendar.getInstance().getTimeInMillis());
                return;
            }
            this.removeSafeHouse(isoPlayer, true);
        }
    }
    
    public SafeHouse(final int x, final int y, final int w, final int h, final String s) {
        this.x = 0;
        this.y = 0;
        this.w = 0;
        this.h = 0;
        this.owner = null;
        this.players = new ArrayList<String>();
        this.lastVisited = 0L;
        this.title = "Safehouse";
        this.playerConnected = 0;
        this.openTimer = 0;
        this.playersRespawn = new ArrayList<String>();
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.players.add(s);
        this.owner = s;
        this.id = invokedynamic(makeConcatWithConstants:(IIJ)Ljava/lang/String;, x, y, Calendar.getInstance().getTimeInMillis());
    }
    
    public String getId() {
        return this.id;
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getW() {
        return this.w;
    }
    
    public void setW(final int w) {
        this.w = w;
    }
    
    public int getH() {
        return this.h;
    }
    
    public void setH(final int h) {
        this.h = h;
    }
    
    public int getX2() {
        return this.x + this.w;
    }
    
    public int getY2() {
        return this.y + this.h;
    }
    
    public ArrayList<String> getPlayers() {
        return this.players;
    }
    
    public void setPlayers(final ArrayList<String> players) {
        this.players = players;
    }
    
    public static ArrayList<SafeHouse> getSafehouseList() {
        return SafeHouse.safehouseList;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(final String o) {
        this.owner = o;
        if (this.players.contains(o)) {
            this.players.remove(o);
        }
    }
    
    public boolean isOwner(final IsoPlayer isoPlayer) {
        return this.getOwner().equals(isoPlayer.getUsername());
    }
    
    public long getLastVisited() {
        return this.lastVisited;
    }
    
    public void setLastVisited(final long lastVisited) {
        this.lastVisited = lastVisited;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public int getPlayerConnected() {
        return this.playerConnected;
    }
    
    public void setPlayerConnected(final int playerConnected) {
        this.playerConnected = playerConnected;
    }
    
    public int getOpenTimer() {
        return this.openTimer;
    }
    
    public void setOpenTimer(final int openTimer) {
        this.openTimer = openTimer;
    }
    
    public void setRespawnInSafehouse(final boolean b, final String s) {
        if (b) {
            this.playersRespawn.add(s);
        }
        else {
            this.playersRespawn.remove(s);
        }
        if (GameClient.bClient) {
            GameClient.sendSafehouse(this, false);
        }
    }
    
    public boolean isRespawnInSafehouse(final String o) {
        return this.playersRespawn.contains(o);
    }
    
    static {
        SafeHouse.diffError = 2;
        safehouseList = new ArrayList<SafeHouse>();
    }
}
