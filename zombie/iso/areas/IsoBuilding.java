// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas;

import zombie.characters.IsoGameCharacter;
import java.util.Collection;
import zombie.iso.objects.IsoDoor;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.iso.RoomDef;
import zombie.iso.LotHeader;
import zombie.inventory.ItemType;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import java.util.Iterator;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.characters.SurvivorDesc;
import java.util.Stack;
import zombie.iso.IsoCell;
import zombie.core.Rand;
import zombie.iso.IsoLightSource;
import zombie.iso.BuildingDef;
import zombie.iso.objects.IsoWindow;
import zombie.inventory.ItemContainer;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.Rectangle;

public final class IsoBuilding extends IsoArea
{
    public Rectangle bounds;
    public final Vector<IsoRoomExit> Exits;
    public boolean IsResidence;
    public final ArrayList<ItemContainer> container;
    public final Vector<IsoRoom> Rooms;
    public final Vector<IsoWindow> Windows;
    public int ID;
    public static int IDMax;
    public int safety;
    public int transparentWalls;
    private boolean isToxic;
    public static float PoorBuildingScore;
    public static float GoodBuildingScore;
    public int scoreUpdate;
    public BuildingDef def;
    public boolean bSeenInside;
    public ArrayList<IsoLightSource> lights;
    static ArrayList<IsoRoom> tempo;
    static ArrayList<ItemContainer> tempContainer;
    static ArrayList<String> RandomContainerChoices;
    static ArrayList<IsoWindow> windowchoices;
    
    public int getRoomsNumber() {
        return this.Rooms.size();
    }
    
    public IsoBuilding() {
        this.Exits = new Vector<IsoRoomExit>();
        this.IsResidence = true;
        this.container = new ArrayList<ItemContainer>();
        this.Rooms = new Vector<IsoRoom>();
        this.Windows = new Vector<IsoWindow>();
        this.ID = 0;
        this.safety = 0;
        this.transparentWalls = 0;
        this.isToxic = false;
        this.scoreUpdate = -1;
        this.bSeenInside = false;
        this.lights = new ArrayList<IsoLightSource>();
        this.ID = IsoBuilding.IDMax++;
        this.scoreUpdate = -120 + Rand.Next(120);
    }
    
    public int getID() {
        return this.ID;
    }
    
    public void TriggerAlarm() {
    }
    
    public IsoBuilding(final IsoCell isoCell) {
        this.Exits = new Vector<IsoRoomExit>();
        this.IsResidence = true;
        this.container = new ArrayList<ItemContainer>();
        this.Rooms = new Vector<IsoRoom>();
        this.Windows = new Vector<IsoWindow>();
        this.ID = 0;
        this.safety = 0;
        this.transparentWalls = 0;
        this.isToxic = false;
        this.scoreUpdate = -1;
        this.bSeenInside = false;
        this.lights = new ArrayList<IsoLightSource>();
        this.ID = IsoBuilding.IDMax++;
        this.scoreUpdate = -120 + Rand.Next(120);
    }
    
    public boolean ContainsAllItems(final Stack<String> stack) {
        return false;
    }
    
    public float ScoreBuildingPersonSpecific(final SurvivorDesc survivorDesc, final boolean b) {
        float n = 0.0f + this.Rooms.size() * 5 + this.Exits.size() * 15 - this.transparentWalls * 10;
        for (int i = 0; i < this.container.size(); ++i) {
            n += this.container.get(i).Items.size() * 3;
        }
        if (!IsoWorld.instance.CurrentCell.getBuildingScores().containsKey(this.ID)) {
            final BuildingScore value = new BuildingScore(this);
            value.building = this;
            IsoWorld.instance.CurrentCell.getBuildingScores().put(this.ID, value);
            this.ScoreBuildingGeneral(value);
        }
        final BuildingScore buildingScore = IsoWorld.instance.CurrentCell.getBuildingScores().get(this.ID);
        float n2 = n + (buildingScore.defense + buildingScore.food + buildingScore.size + buildingScore.weapons + buildingScore.wood) * 10.0f;
        int x = -10000;
        int y = -10000;
        if (!this.Exits.isEmpty()) {
            final IsoRoomExit isoRoomExit = this.Exits.get(0);
            x = isoRoomExit.x;
            y = isoRoomExit.y;
        }
        final float distanceManhatten = IsoUtils.DistanceManhatten(survivorDesc.getInstance().getX(), survivorDesc.getInstance().getY(), (float)x, (float)y);
        if (distanceManhatten > 0.0f) {
            if (b) {
                n2 *= distanceManhatten * 0.5f;
            }
            else {
                n2 /= distanceManhatten * 0.5f;
            }
        }
        return n2;
    }
    
    public BuildingDef getDef() {
        return this.def;
    }
    
    public void update() {
        if (this.Exits.isEmpty()) {
            return;
        }
        final int n = 0;
        int n2 = 0;
        for (int i = 0; i < this.Rooms.size(); ++i) {
            final IsoRoom isoRoom = this.Rooms.get(i);
            if (isoRoom.layer == 0) {
                for (int j = 0; j < isoRoom.TileList.size(); ++j) {
                    ++n2;
                    final IsoGridSquare isoGridSquare = isoRoom.TileList.get(j);
                }
            }
        }
        if (n2 == 0) {
            ++n2;
        }
        final int safety = (int)(n / (float)n2);
        --this.scoreUpdate;
        if (this.scoreUpdate <= 0) {
            this.scoreUpdate += 120;
            BuildingScore buildingScore;
            if (IsoWorld.instance.CurrentCell.getBuildingScores().containsKey(this.ID)) {
                buildingScore = IsoWorld.instance.CurrentCell.getBuildingScores().get(this.ID);
            }
            else {
                buildingScore = new BuildingScore(this);
                buildingScore.building = this;
            }
            final BuildingScore scoreBuildingGeneral;
            final BuildingScore value = scoreBuildingGeneral = this.ScoreBuildingGeneral(buildingScore);
            scoreBuildingGeneral.defense += safety * 10;
            this.safety = safety;
            IsoWorld.instance.CurrentCell.getBuildingScores().put(this.ID, value);
        }
    }
    
    public void AddRoom(final IsoRoom e) {
        this.Rooms.add(e);
        if (this.bounds == null) {
            this.bounds = (Rectangle)e.bounds.clone();
        }
        if (e != null && e.bounds != null) {
            this.bounds.add(e.bounds);
        }
    }
    
    public void CalculateExits() {
        for (final IsoRoom isoRoom : this.Rooms) {
            for (final IsoRoomExit e : isoRoom.Exits) {
                if (e.To.From == null && isoRoom.layer == 0) {
                    this.Exits.add(e);
                }
            }
        }
    }
    
    public void CalculateWindows() {
        for (final IsoRoom isoRoom : this.Rooms) {
            for (final IsoGridSquare isoGridSquare : isoRoom.TileList) {
                final IsoGridSquare gridSquare = isoGridSquare.getCell().getGridSquare(isoGridSquare.getX(), isoGridSquare.getY() + 1, isoGridSquare.getZ());
                final IsoGridSquare gridSquare2 = isoGridSquare.getCell().getGridSquare(isoGridSquare.getX() + 1, isoGridSquare.getY(), isoGridSquare.getZ());
                if (isoGridSquare.getProperties().Is(IsoFlagType.collideN) && isoGridSquare.getProperties().Is(IsoFlagType.transparentN)) {
                    final IsoRoom isoRoom2 = isoRoom;
                    ++isoRoom2.transparentWalls;
                    ++this.transparentWalls;
                }
                if (isoGridSquare.getProperties().Is(IsoFlagType.collideW) && isoGridSquare.getProperties().Is(IsoFlagType.transparentW)) {
                    final IsoRoom isoRoom3 = isoRoom;
                    ++isoRoom3.transparentWalls;
                    ++this.transparentWalls;
                }
                if (gridSquare != null) {
                    boolean b = gridSquare.getRoom() != null;
                    if (gridSquare.getRoom() != null && gridSquare.getRoom().building != isoRoom.building) {
                        b = false;
                    }
                    if (gridSquare.getProperties().Is(IsoFlagType.collideN) && gridSquare.getProperties().Is(IsoFlagType.transparentN) && !b) {
                        final IsoRoom isoRoom4 = isoRoom;
                        ++isoRoom4.transparentWalls;
                        ++this.transparentWalls;
                    }
                }
                if (gridSquare2 != null) {
                    boolean b2 = gridSquare2.getRoom() != null;
                    if (gridSquare2.getRoom() != null && gridSquare2.getRoom().building != isoRoom.building) {
                        b2 = false;
                    }
                    if (gridSquare2.getProperties().Is(IsoFlagType.collideW) && gridSquare2.getProperties().Is(IsoFlagType.transparentW) && !b2) {
                        final IsoRoom isoRoom5 = isoRoom;
                        ++isoRoom5.transparentWalls;
                        ++this.transparentWalls;
                    }
                }
                for (int i = 0; i < isoGridSquare.getSpecialObjects().size(); ++i) {
                    final IsoObject isoObject = isoGridSquare.getSpecialObjects().get(i);
                    if (isoObject instanceof IsoWindow) {
                        this.Windows.add((IsoWindow)isoObject);
                    }
                }
                if (gridSquare != null) {
                    for (int j = 0; j < gridSquare.getSpecialObjects().size(); ++j) {
                        final IsoObject isoObject2 = gridSquare.getSpecialObjects().get(j);
                        if (isoObject2 instanceof IsoWindow) {
                            this.Windows.add((IsoWindow)isoObject2);
                        }
                    }
                }
                if (gridSquare2 != null) {
                    for (int k = 0; k < gridSquare2.getSpecialObjects().size(); ++k) {
                        final IsoObject isoObject3 = gridSquare2.getSpecialObjects().get(k);
                        if (isoObject3 instanceof IsoWindow) {
                            this.Windows.add((IsoWindow)isoObject3);
                        }
                    }
                }
            }
        }
    }
    
    public void FillContainers() {
        for (final IsoRoom isoRoom : this.Rooms) {
            if (isoRoom.RoomDef != null && isoRoom.RoomDef.contains("tutorial")) {}
            if (!isoRoom.TileList.isEmpty()) {
                final IsoGridSquare isoGridSquare = isoRoom.TileList.get(0);
                if (isoGridSquare.getX() < 74 && isoGridSquare.getY() < 32) {}
            }
            if (isoRoom.RoomDef.contains("shop")) {
                this.IsResidence = false;
            }
            for (final IsoGridSquare e : isoRoom.TileList) {
                for (int i = 0; i < e.getObjects().size(); ++i) {
                    final IsoObject e2 = e.getObjects().get(i);
                    if (e2.hasWater()) {
                        isoRoom.getWaterSources().add(e2);
                    }
                    if (e2.container != null) {
                        this.container.add(e2.container);
                        isoRoom.Containers.add(e2.container);
                    }
                }
                if (e.getProperties().Is(IsoFlagType.bed)) {
                    isoRoom.Beds.add(e);
                }
            }
        }
    }
    
    public ItemContainer getContainerWith(final ItemType itemType) {
        final Iterator<IsoRoom> iterator = this.Rooms.iterator();
        while (iterator.hasNext()) {
            for (final ItemContainer itemContainer : iterator.next().Containers) {
                if (itemContainer.HasType(itemType)) {
                    return itemContainer;
                }
            }
        }
        return null;
    }
    
    public IsoRoom getRandomRoom() {
        if (this.Rooms.size() == 0) {
            return null;
        }
        return this.Rooms.get(Rand.Next(this.Rooms.size()));
    }
    
    private BuildingScore ScoreBuildingGeneral(final BuildingScore buildingScore) {
        buildingScore.food = 0.0f;
        buildingScore.defense = 0.0f;
        buildingScore.weapons = 0.0f;
        buildingScore.wood = 0.0f;
        buildingScore.building = this;
        buildingScore.size = 0;
        buildingScore.defense += (this.Exits.size() - 1) * 140;
        buildingScore.defense -= this.transparentWalls * 40;
        buildingScore.size = this.Rooms.size() * 10;
        buildingScore.size += this.container.size() * 10;
        return buildingScore;
    }
    
    public IsoGridSquare getFreeTile() {
        IsoGridSquare freeTile;
        do {
            freeTile = this.Rooms.get(Rand.Next(this.Rooms.size())).getFreeTile();
        } while (freeTile == null);
        return freeTile;
    }
    
    public boolean hasWater() {
        final Iterator<IsoRoom> iterator = this.Rooms.iterator();
        while (iterator != null && iterator.hasNext()) {
            final IsoRoom isoRoom = iterator.next();
            if (!isoRoom.WaterSources.isEmpty()) {
                IsoObject isoObject = null;
                for (int i = 0; i < isoRoom.WaterSources.size(); ++i) {
                    if (isoRoom.WaterSources.get(i).hasWater()) {
                        isoObject = isoRoom.WaterSources.get(i);
                        break;
                    }
                }
                if (isoObject != null) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public void CreateFrom(final BuildingDef buildingDef, final LotHeader lotHeader) {
        for (int i = 0; i < buildingDef.rooms.size(); ++i) {
            final IsoRoom room = lotHeader.getRoom(buildingDef.rooms.get(i).ID);
            room.building = this;
            this.Rooms.add(room);
        }
    }
    
    public void setAllExplored(final boolean explored) {
        this.def.bAlarmed = false;
        for (int i = 0; i < this.Rooms.size(); ++i) {
            final IsoRoom isoRoom = this.Rooms.get(i);
            isoRoom.def.setExplored(explored);
            for (int j = isoRoom.def.getX(); j <= isoRoom.def.getX2(); ++j) {
                for (int k = isoRoom.def.getY(); k <= isoRoom.def.getY2(); ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, k, isoRoom.def.level);
                    if (gridSquare != null) {
                        gridSquare.setHourSeenToCurrent();
                    }
                }
            }
        }
    }
    
    public boolean isAllExplored() {
        for (int i = 0; i < this.Rooms.size(); ++i) {
            if (!this.Rooms.get(i).def.bExplored) {
                return false;
            }
        }
        return true;
    }
    
    public void addWindow(final IsoWindow e, final boolean b, final IsoGridSquare isoGridSquare, final IsoBuilding isoBuilding) {
        this.Windows.add(e);
        IsoGridSquare square;
        if (b) {
            square = e.square;
        }
        else {
            square = isoGridSquare;
        }
        if (square == null) {
            return;
        }
        if (square.getRoom() != null) {
            return;
        }
        final IsoLightSource isoLightSource = new IsoLightSource(square.getX(), square.getY(), square.getZ(), RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f, RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f, RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f, 7, isoBuilding);
        this.lights.add(isoLightSource);
        IsoWorld.instance.CurrentCell.getLamppostPositions().add(isoLightSource);
    }
    
    public void addWindow(final IsoWindow isoWindow, final boolean b) {
        this.addWindow(isoWindow, b, isoWindow.square, null);
    }
    
    public void addDoor(final IsoDoor isoDoor, final boolean b, final IsoGridSquare isoGridSquare, final IsoBuilding isoBuilding) {
        IsoGridSquare square;
        if (b) {
            square = isoDoor.square;
        }
        else {
            square = isoGridSquare;
        }
        if (square == null) {
            return;
        }
        if (square.getRoom() != null) {
            return;
        }
        final IsoLightSource isoLightSource = new IsoLightSource(square.getX(), square.getY(), square.getZ(), RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f, RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f, RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7f, 7, isoBuilding);
        this.lights.add(isoLightSource);
        IsoWorld.instance.CurrentCell.getLamppostPositions().add(isoLightSource);
    }
    
    public void addDoor(final IsoDoor isoDoor, final boolean b) {
        this.addDoor(isoDoor, b, isoDoor.square, null);
    }
    
    public boolean isResidential() {
        return this.containsRoom("bedroom");
    }
    
    public boolean containsRoom(final String s) {
        for (int i = 0; i < this.Rooms.size(); ++i) {
            if (s.equals(this.Rooms.get(i).getName())) {
                return true;
            }
        }
        return false;
    }
    
    public IsoRoom getRandomRoom(final String s) {
        IsoBuilding.tempo.clear();
        for (int i = 0; i < this.Rooms.size(); ++i) {
            if (s.equals(this.Rooms.get(i).getName())) {
                IsoBuilding.tempo.add(this.Rooms.get(i));
            }
        }
        if (IsoBuilding.tempo.isEmpty()) {
            return null;
        }
        return IsoBuilding.tempo.get(Rand.Next(IsoBuilding.tempo.size()));
    }
    
    public ItemContainer getRandomContainer(final String s) {
        IsoBuilding.RandomContainerChoices.clear();
        String[] split = null;
        if (s != null) {
            split = s.split(",");
        }
        if (split != null) {
            for (int i = 0; i < split.length; ++i) {
                IsoBuilding.RandomContainerChoices.add(split[i]);
            }
        }
        IsoBuilding.tempContainer.clear();
        for (int j = 0; j < this.Rooms.size(); ++j) {
            final IsoRoom isoRoom = this.Rooms.get(j);
            for (int k = 0; k < isoRoom.Containers.size(); ++k) {
                final ItemContainer e = isoRoom.Containers.get(k);
                if (s == null || IsoBuilding.RandomContainerChoices.contains(e.getType())) {
                    IsoBuilding.tempContainer.add(e);
                }
            }
        }
        if (IsoBuilding.tempContainer.isEmpty()) {
            return null;
        }
        return IsoBuilding.tempContainer.get(Rand.Next(IsoBuilding.tempContainer.size()));
    }
    
    public IsoWindow getRandomFirstFloorWindow() {
        IsoBuilding.windowchoices.clear();
        IsoBuilding.windowchoices.addAll(this.Windows);
        for (int i = 0; i < IsoBuilding.windowchoices.size(); ++i) {
            if (IsoBuilding.windowchoices.get(i).getZ() > 0.0f) {
                IsoBuilding.windowchoices.remove(i);
            }
        }
        if (!IsoBuilding.windowchoices.isEmpty()) {
            return IsoBuilding.windowchoices.get(Rand.Next(IsoBuilding.windowchoices.size()));
        }
        return null;
    }
    
    public boolean isToxic() {
        return this.isToxic;
    }
    
    public void setToxic(final boolean isToxic) {
        this.isToxic = isToxic;
    }
    
    public void forceAwake() {
        for (int i = this.def.getX(); i <= this.def.getX2(); ++i) {
            for (int j = this.def.getY(); j <= this.def.getY2(); ++j) {
                for (int k = 0; k <= 4; ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getMovingObjects().size(); ++l) {
                            if (gridSquare.getMovingObjects().get(l) instanceof IsoGameCharacter) {
                                ((IsoGameCharacter)gridSquare.getMovingObjects().get(l)).forceAwake();
                            }
                        }
                    }
                }
            }
        }
    }
    
    static {
        IsoBuilding.IDMax = 0;
        IsoBuilding.PoorBuildingScore = 10.0f;
        IsoBuilding.GoodBuildingScore = 100.0f;
        IsoBuilding.tempo = new ArrayList<IsoRoom>();
        IsoBuilding.tempContainer = new ArrayList<ItemContainer>();
        IsoBuilding.RandomContainerChoices = new ArrayList<String>();
        IsoBuilding.windowchoices = new ArrayList<IsoWindow>();
    }
}
