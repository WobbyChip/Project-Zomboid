// 
// Decompiled by Procyon v0.5.36
// 

package zombie.Lua;

import zombie.core.znet.ISteamWorkshopCallback;
import java.util.Objects;
import java.util.Arrays;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.profanity.ProfanityFilter;
import zombie.core.skinnedmodel.population.ClothingDecalGroup;
import zombie.core.skinnedmodel.population.ClothingDecals;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.erosion.ErosionData;
import zombie.network.chat.ChatType;
import java.util.regex.Matcher;
import zombie.chat.ChatManager;
import zombie.core.ThreadGroups;
import java.util.regex.Pattern;
import zombie.core.physics.Bullet;
import zombie.network.MPStatistic;
import java.util.function.Function;
import java.util.function.Predicate;
import zombie.core.input.Input;
import zombie.util.list.PZArrayUtil;
import zombie.core.physics.WorldSimulation;
import zombie.vehicles.VehiclesDB2;
import zombie.vehicles.VehicleManager;
import zombie.iso.LightingJNI;
import zombie.debug.LineDrawer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import zombie.core.znet.GameServerDetails;
import zombie.core.znet.ServerBrowser;
import zombie.util.PublicServerUtil;
import zombie.gameStates.ConnectToServerState;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamFriends;
import zombie.network.ServerWorldDatabase;
import zombie.spnetwork.SinglePlayerClient;
import zombie.core.textures.TextureID;
import java.util.Date;
import zombie.BaseSoundManager;
import zombie.iso.CellLoader;
import zombie.modding.ActiveModsFile;
import java.awt.Desktop;
import fmod.fmod.FMODManager;
import zombie.audio.BaseSoundBank;
import zombie.gameStates.GameState;
import org.lwjglx.input.Controllers;
import zombie.util.AddCoopPlayer;
import zombie.input.JoypadManager;
import org.lwjglx.input.Controller;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import zombie.characters.action.ActionGroup;
import zombie.asset.Asset;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import zombie.network.MPStatistics;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDBHelper;
import java.nio.file.DirectoryStream;
import zombie.ZombieSpawnRecorder;
import zombie.popman.ZombiePopulationManager;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import zombie.core.Rand;
import java.util.Comparator;
import java.util.Map;
import zombie.core.znet.SteamUtils;
import se.krka.kahlua.vm.LuaCallFrame;
import zombie.gameStates.IngameState;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.modding.ModUtilsJava;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.util.StringUtils;
import se.krka.kahlua.integration.annotations.LuaMethod;
import zombie.core.skinnedmodel.model.Model;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import zombie.worldMap.UIWorldMap;
import se.krka.kahlua.vm.Coroutine;
import java.lang.reflect.Method;
import zombie.chat.ServerChatMessage;
import zombie.chat.ChatBase;
import zombie.chat.ChatMessage;
import zombie.world.moddata.ModData;
import zombie.iso.IsoWaterGeometry;
import zombie.inventory.types.WeaponType;
import zombie.characterTextures.BloodClothingType;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.MapGroups;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFest;
import zombie.randomizedWorld.randomizedZoneStory.RZSMusicFestStage;
import zombie.randomizedWorld.randomizedZoneStory.RZSBaseball;
import zombie.randomizedWorld.randomizedZoneStory.RZSTrapperCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSSexyTime;
import zombie.randomizedWorld.randomizedZoneStory.RZSHunterCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSBBQParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSFishingTrip;
import zombie.randomizedWorld.randomizedZoneStory.RZSBeachParty;
import zombie.randomizedWorld.randomizedZoneStory.RZSBuryingCamp;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCampEaten;
import zombie.randomizedWorld.randomizedZoneStory.RZSForestCamp;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.randomizedWorld.randomizedVehicleStory.RVSTrailerCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSFlippedCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSChangingTire;
import zombie.randomizedWorld.randomizedVehicleStory.RVSUtilityVehicle;
import zombie.randomizedWorld.randomizedVehicleStory.RVSConstructionSite;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBurntCar;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockadeShooting;
import zombie.randomizedWorld.randomizedVehicleStory.RVSPoliceBlockade;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrashCorpse;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCrashHorde;
import zombie.randomizedWorld.randomizedVehicleStory.RVSAmbulanceCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RVSBanditRoad;
import zombie.randomizedWorld.randomizedVehicleStory.RVSCarCrash;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHockeyPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSTinFoilHat;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHouseParty;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPoliceAtHouse;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSpecificProfession;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSCorpsePsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSkeletonPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscapeWithPolice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscape;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSuicidePact;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPokerNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStudentNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStagDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHenDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSFootballNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBedroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBathroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBandPractice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombieLockedBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunslinger;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunmanInBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSDeadDrunk;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBleach;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombiesEating;
import zombie.randomizedWorld.randomizedDeadSurvivor.RandomizedDeadSurvivorBase;
import zombie.randomizedWorld.randomizedBuilding.RBKateAndBaldspot;
import zombie.randomizedWorld.randomizedBuilding.RBShopLooted;
import zombie.randomizedWorld.randomizedBuilding.RBBurntCorpse;
import zombie.randomizedWorld.randomizedBuilding.RBSafehouse;
import zombie.randomizedWorld.randomizedBuilding.RBLooted;
import zombie.randomizedWorld.randomizedBuilding.RBBar;
import zombie.randomizedWorld.randomizedBuilding.RBCafe;
import zombie.randomizedWorld.randomizedBuilding.RBPileOCrepe;
import zombie.randomizedWorld.randomizedBuilding.RBClinic;
import zombie.randomizedWorld.randomizedBuilding.RBHairSalon;
import zombie.randomizedWorld.randomizedBuilding.RBOffice;
import zombie.randomizedWorld.randomizedBuilding.RBPizzaWhirled;
import zombie.randomizedWorld.randomizedBuilding.RBSpiffo;
import zombie.randomizedWorld.randomizedBuilding.RBSchool;
import zombie.randomizedWorld.randomizedBuilding.RBStripclub;
import zombie.randomizedWorld.randomizedBuilding.RBOther;
import zombie.randomizedWorld.randomizedBuilding.RBBurnt;
import zombie.randomizedWorld.randomizedBuilding.RBBasic;
import zombie.randomizedWorld.randomizedBuilding.RBBurntFireman;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.inventory.ItemType;
import zombie.core.stash.Stash;
import zombie.core.stash.StashBuilding;
import zombie.core.stash.StashSystem;
import zombie.network.DBTicket;
import zombie.iso.areas.NonPvpZone;
import zombie.network.DBResult;
import zombie.characters.Faction;
import zombie.config.StringConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.EnumConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import zombie.network.Userlog;
import zombie.iso.objects.IsoCompost;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.iso.MultiStageBuilding;
import zombie.iso.objects.BSFurnace;
import zombie.characters.BodyDamage.Nutrition;
import zombie.AmbientStreamManager;
import zombie.BaseAmbientStreamManager;
import zombie.characters.CharacterSoundEmitter;
import zombie.characters.DummyCharacterSoundEmitter;
import zombie.WorldSoundManager;
import zombie.VirtualZombieManager;
import zombie.SystemDisabler;
import zombie.SoundManager;
import zombie.SandboxOptions;
import zombie.GameWindow;
import zombie.GameTime;
import zombie.GameSounds;
import zombie.DummySoundManager;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocation;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.characters.AttachedItems.AttachedLocation;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehicleDoor;
import zombie.vehicles.UI3DScene;
import zombie.vehicles.PathFindState2;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.EditVehicleState;
import zombie.vehicles.BaseVehicle;
import zombie.util.PZCalendar;
import zombie.util.list.PZArrayList;
import zombie.ui.TextDrawObject;
import zombie.ui.VehicleGauge;
import zombie.ui.UITextBox2;
import zombie.ui.UIServerToolbox;
import zombie.ui.UITransition;
import zombie.ui.UIFont;
import zombie.ui.UIElement;
import zombie.ui.UI3DModel;
import zombie.ui.TextManager;
import zombie.ui.SpeedControls;
import zombie.ui.RadialProgressBar;
import zombie.ui.RadialMenu;
import zombie.ui.RadarPanel;
import zombie.ui.ObjectTooltip;
import zombie.ui.NewHealthPanel;
import zombie.ui.MoodlesUI;
import zombie.ui.ModalDialog;
import zombie.ui.UIDebugConsole;
import zombie.ui.Clock;
import zombie.ui.ActionProgressBar;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.MovableRecipe;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ItemRecipe;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.GameSoundScript;
import zombie.scripting.objects.Fixing;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.radio.media.MediaData;
import zombie.radio.media.RecordedMedia;
import zombie.radio.StorySounds.DataPoint;
import zombie.radio.StorySounds.EventSound;
import zombie.radio.StorySounds.StorySoundEvent;
import zombie.radio.StorySounds.StorySound;
import zombie.radio.StorySounds.SLSoundManager;
import zombie.radio.ChannelCategory;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioLine;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.DynamicRadioChannel;
import zombie.radio.scripting.RadioScriptManager;
import zombie.radio.RadioData;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.PresetEntry;
import zombie.radio.devices.DevicePresets;
import zombie.radio.devices.DeviceData;
import zombie.radio.RadioAPI;
import zombie.popman.ZombiePopulationRenderer;
import zombie.network.ServerSettingsManager;
import zombie.network.ServerSettings;
import zombie.network.ServerOptions;
import zombie.network.Server;
import zombie.modding.ActiveMods;
import zombie.iso.IsoMarkers;
import zombie.iso.SearchMode;
import zombie.iso.WorldMarkers;
import zombie.iso.Vector3;
import zombie.iso.Vector2;
import zombie.iso.TileOverlays;
import zombie.iso.SliceY;
import zombie.iso.RoomDef;
import zombie.iso.MetaObject;
import zombie.iso.LosUtil;
import zombie.iso.IsoUtils;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoLuaMover;
import zombie.iso.IsoLot;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoDirectionSet;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoCell;
import zombie.iso.IsoCamera;
import zombie.iso.BuildingDef;
import zombie.iso.IsoChunk;
import zombie.iso.ContainerOverlays;
import zombie.iso.BrokenFences;
import zombie.iso.BentFences;
import zombie.iso.IsoPuddles;
import zombie.iso.weather.ClimateMoon;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.iso.weather.WorldFlares;
import zombie.iso.weather.ClimateHistory;
import zombie.iso.weather.ClimateForecaster;
import zombie.iso.weather.ClimateValues;
import zombie.iso.weather.ClimateColorInfo;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.iso.weather.ThunderStorm;
import zombie.iso.weather.WeatherPeriod;
import zombie.iso.weather.ClimateManager;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSprite;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.isoregion.IsoRegionsRenderer;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataCell;
import zombie.iso.areas.isoregion.IsoRegionLogType;
import zombie.iso.areas.isoregion.IsoRegionsLogger;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.inventory.RecipeManager;
import zombie.inventory.FixingManager;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.WeaponPart;
import zombie.inventory.types.Radio;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.MapItem;
import zombie.inventory.types.Literature;
import zombie.inventory.types.KeyRing;
import zombie.inventory.types.Key;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Food;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.ComboItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.AlarmClockClothing;
import zombie.inventory.types.AlarmClock;
import zombie.input.Mouse;
import zombie.globalObjects.SGlobalObjectSystem;
import zombie.globalObjects.SGlobalObjects;
import zombie.globalObjects.SGlobalObject;
import zombie.globalObjects.CGlobalObjectSystem;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.CGlobalObject;
import zombie.gameStates.MainScreenState;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.DebugGlobalObjectState;
import zombie.gameStates.DebugChunkState;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.AttachmentEditorState;
import zombie.gameStates.AnimationViewerState;
import zombie.erosion.season.ErosionSeason;
import zombie.erosion.ErosionMain;
import zombie.erosion.ErosionConfig;
import zombie.debug.BooleanDebugOption;
import zombie.debug.DebugOptions;
import zombie.core.math.PZMath;
import zombie.core.Translator;
import zombie.core.SpriteRenderer;
import zombie.core.PerformanceSettings;
import zombie.core.Language;
import zombie.core.ImmutableColor;
import zombie.core.GameVersion;
import zombie.core.Core;
import zombie.core.Colors;
import zombie.core.Color;
import zombie.core.znet.SteamWorkshopItem;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.SteamFriend;
import zombie.core.textures.Texture;
import zombie.core.textures.ColorInfo;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.properties.PropertyContainer;
import zombie.core.logger.ZLogger;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.Clipboard;
import zombie.characterTextures.BloodBodyPartType;
import zombie.network.NetworkAIParams;
import zombie.characters.HaloTextHelper;
import zombie.characters.CharacterActionAnims;
import zombie.characters.IsoZombie;
import zombie.characters.IsoSurvivor;
import zombie.characters.traits.TraitCollection;
import zombie.characters.SurvivorFactory;
import zombie.characters.SurvivorDesc;
import zombie.characters.Stats;
import zombie.characters.IsoDummyCameraCharacter;
import zombie.characters.traits.TraitFactory;
import zombie.characters.traits.ObservationFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.Moodles.Moodle;
import zombie.characters.CharacterTimedActions.LuaTimedActionNew;
import zombie.characters.CharacterTimedActions.LuaTimedAction;
import zombie.input.GameKeyboard;
import zombie.characters.BodyDamage.Fitness;
import zombie.characters.BodyDamage.Metabolics;
import zombie.characters.BodyDamage.Thermoregulator;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.ai.MapKnowledge;
import zombie.ai.GameCharacterAIBrain;
import zombie.ai.states.ZombieSittingState;
import zombie.ai.states.ZombieReanimateState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.ai.states.ZombieIdleState;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieGetDownState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ThumpState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SmashWindowState;
import zombie.ai.states.PlayerStrafeState;
import zombie.ai.states.PlayerSitOnGroundState;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.PlayerFallingState;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerExtState;
import zombie.ai.states.PlayerEmoteState;
import zombie.ai.states.PlayerAimState;
import zombie.ai.states.PlayerActionsState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.LungeState;
import zombie.ai.states.IdleState;
import zombie.ai.states.FitnessState;
import zombie.ai.states.FishingState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.FakeDeadAttackState;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.BurntToDeath;
import zombie.ai.states.AttackState;
import zombie.audio.GameSoundClip;
import zombie.audio.GameSound;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.DummySoundBank;
import se.krka.kahlua.vm.KahluaUtil;
import org.joml.Vector3f;
import org.joml.Vector2f;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODAudio;
import fmod.fmod.EmitterType;
import java.util.Vector;
import java.util.Stack;
import java.util.LinkedList;
import java.util.EnumMap;
import java.text.SimpleDateFormat;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedWriter;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import zombie.core.BoxedStaticValues;
import java.util.Calendar;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.lwjglx.input.Keyboard;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.integration.LuaReturn;
import java.io.InputStreamReader;
import zombie.iso.IsoWorld;
import zombie.characters.ZombiesZoneDefinition;
import zombie.characters.HairOutfitDefinitions;
import zombie.core.skinnedmodel.population.DefaultClothing;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Reader;
import java.io.BufferedReader;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import java.io.FileNotFoundException;
import zombie.core.IndieFileLoader;
import zombie.debug.DebugType;
import org.luaj.kahluafork.compiler.FuncState;
import zombie.iso.IsoGridSquare;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoObject;
import zombie.inventory.ItemPickerJava;
import zombie.characters.IsoPlayer;
import zombie.inventory.ItemContainer;
import zombie.debug.DebugLog;
import java.util.Iterator;
import java.net.URI;
import zombie.network.NetChecksum;
import zombie.network.GameServer;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.io.IOException;
import zombie.core.logger.ExceptionLogger;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.net.URISyntaxException;
import se.krka.kahlua.vm.LuaClosure;
import zombie.core.raknet.VoiceManager;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.ui.UIManager;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.util.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.converter.KahluaConverterManager;

public final class LuaManager
{
    public static KahluaConverterManager converterManager;
    public static J2SEPlatform platform;
    public static KahluaTable env;
    public static KahluaThread thread;
    public static KahluaThread debugthread;
    public static LuaCaller caller;
    public static LuaCaller debugcaller;
    public static Exposer exposer;
    public static ArrayList<String> loaded;
    private static final HashSet<String> loading;
    public static HashMap<String, Object> loadedReturn;
    public static boolean checksumDone;
    public static ArrayList<String> loadList;
    static ArrayList<String> paths;
    private static final HashMap<String, Object> luaFunctionMap;
    private static final HashSet<KahluaTable> s_wiping;
    
    public static void outputTable(final KahluaTable kahluaTable, final int n) {
    }
    
    private static void wipeRecurse(final KahluaTable o) {
        if (o.isEmpty()) {
            return;
        }
        if (LuaManager.s_wiping.contains(o)) {
            return;
        }
        LuaManager.s_wiping.add(o);
        final KahluaTableIterator iterator = o.iterator();
        while (iterator.advance()) {
            final KahluaTable kahluaTable = Type.tryCastTo(iterator.getValue(), KahluaTable.class);
            if (kahluaTable != null) {
                wipeRecurse(kahluaTable);
            }
        }
        LuaManager.s_wiping.remove(o);
        o.wipe();
    }
    
    public static void init() {
        LuaManager.loaded.clear();
        LuaManager.loading.clear();
        LuaManager.loadedReturn.clear();
        LuaManager.paths.clear();
        LuaManager.luaFunctionMap.clear();
        LuaManager.platform = new J2SEPlatform();
        if (LuaManager.env != null) {
            LuaManager.s_wiping.clear();
            wipeRecurse(LuaManager.env);
        }
        LuaManager.env = LuaManager.platform.newEnvironment();
        LuaManager.converterManager = new KahluaConverterManager();
        if (LuaManager.thread != null) {
            LuaManager.thread.bReset = true;
        }
        LuaManager.thread = new KahluaThread((Platform)LuaManager.platform, LuaManager.env);
        LuaManager.debugthread = new KahluaThread((Platform)LuaManager.platform, LuaManager.env);
        UIManager.defaultthread = LuaManager.thread;
        LuaManager.caller = new LuaCaller(LuaManager.converterManager);
        LuaManager.debugcaller = new LuaCaller(LuaManager.converterManager);
        if (LuaManager.exposer != null) {
            LuaManager.exposer.destroy();
        }
        LuaManager.exposer = new Exposer(LuaManager.converterManager, (Platform)LuaManager.platform, LuaManager.env);
        LuaManager.loaded = new ArrayList<String>();
        LuaManager.checksumDone = false;
        GameClient.checksum = "";
        GameClient.checksumValid = false;
        KahluaNumberConverter.install(LuaManager.converterManager);
        LuaEventManager.register((Platform)LuaManager.platform, LuaManager.env);
        LuaHookManager.register((Platform)LuaManager.platform, LuaManager.env);
        if (CoopMaster.instance != null) {
            CoopMaster.instance.register((Platform)LuaManager.platform, LuaManager.env);
        }
        if (VoiceManager.instance != null) {
            VoiceManager.instance.LuaRegister((Platform)LuaManager.platform, LuaManager.env);
        }
        final KahluaTable env = LuaManager.env;
        LuaManager.exposer.exposeAll();
        LuaManager.exposer.TypeMap.put("function", LuaClosure.class);
        LuaManager.exposer.TypeMap.put("table", KahluaTable.class);
        outputTable(LuaManager.env, 0);
    }
    
    public static void LoadDir(final String s) throws URISyntaxException {
    }
    
    public static void LoadDirBase(final String s) throws Exception {
        LoadDirBase(s, false);
    }
    
    public static void LoadDirBase(final String s, final boolean b) throws Exception {
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        final File mediaFile = ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, File.separator, s));
        if (!LuaManager.paths.contains(s2)) {
            LuaManager.paths.add(s2);
        }
        try {
            searchFolders(ZomboidFileSystem.instance.baseURI, mediaFile);
        }
        catch (IOException ex) {
            ExceptionLogger.logException(ex);
        }
        final ArrayList<String> loadList = LuaManager.loadList;
        LuaManager.loadList = new ArrayList<String>();
        final ArrayList<String> modIDs = ZomboidFileSystem.instance.getModIDs();
        for (int i = 0; i < modIDs.size(); ++i) {
            final String modDir = ZomboidFileSystem.instance.getModDir(modIDs.get(i));
            if (modDir != null) {
                final URI uri = new File(modDir).getCanonicalFile().toURI();
                final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDir, File.separator, File.separator, File.separator, s));
                try {
                    searchFolders(uri, file);
                }
                catch (IOException ex2) {
                    ExceptionLogger.logException(ex2);
                }
            }
        }
        Collections.sort((List<Comparable>)loadList);
        Collections.sort(LuaManager.loadList);
        loadList.addAll(LuaManager.loadList);
        LuaManager.loadList.clear();
        LuaManager.loadList = loadList;
        final HashSet<String> set = new HashSet<String>();
        for (final String s3 : LuaManager.loadList) {
            if (set.contains(s3)) {
                continue;
            }
            set.add(s3);
            final String absolutePath = ZomboidFileSystem.instance.getAbsolutePath(s3);
            if (absolutePath == null) {
                throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3));
            }
            if (!b) {
                RunLua(absolutePath);
            }
            if (LuaManager.checksumDone || s3.contains("SandboxVars.lua") || (!GameServer.bServer && !GameClient.bClient)) {
                continue;
            }
            NetChecksum.checksummer.addFile(s3, absolutePath);
        }
        LuaManager.loadList.clear();
    }
    
    public static void initChecksum() throws Exception {
        if (LuaManager.checksumDone) {
            return;
        }
        if (GameClient.bClient || GameServer.bServer) {
            NetChecksum.checksummer.reset(false);
        }
    }
    
    public static void finishChecksum() {
        if (GameServer.bServer) {
            GameServer.checksum = NetChecksum.checksummer.checksumToString();
            DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameServer.checksum));
        }
        else {
            if (!GameClient.bClient) {
                return;
            }
            GameClient.checksum = NetChecksum.checksummer.checksumToString();
        }
        NetChecksum.GroupOfFiles.finishChecksum();
        LuaManager.checksumDone = true;
    }
    
    public static void LoadDirBase() throws Exception {
        initChecksum();
        LoadDirBase("shared");
        LoadDirBase("client");
    }
    
    public static void searchFolders(final URI uri, final File file) throws IOException {
        if (file.isDirectory()) {
            final String[] list = file.list();
            for (int i = 0; i < list.length; ++i) {
                searchFolders(uri, new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getCanonicalFile().getAbsolutePath(), File.separator, list[i])));
            }
        }
        else if (file.getAbsolutePath().toLowerCase().endsWith(".lua")) {
            LuaManager.loadList.add(ZomboidFileSystem.instance.getRelativeFile(uri, file.getAbsolutePath()));
        }
    }
    
    public static String getLuaCacheDir() {
        final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
        final File file = new File(pathname);
        if (!file.exists()) {
            file.mkdir();
        }
        return pathname;
    }
    
    public static String getSandboxCacheDir() {
        final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
        final File file = new File(pathname);
        if (!file.exists()) {
            file.mkdir();
        }
        return pathname;
    }
    
    public static void fillContainer(final ItemContainer itemContainer, final IsoPlayer isoPlayer) {
        ItemPickerJava.fillContainer(itemContainer, isoPlayer);
    }
    
    public static void updateOverlaySprite(final IsoObject isoObject) {
        ItemPickerJava.updateOverlaySprite(isoObject);
    }
    
    public static LuaClosure getDotDelimitedClosure(final String s) {
        final String[] split = s.split("\\.");
        KahluaTable env = LuaManager.env;
        for (int i = 0; i < split.length - 1; ++i) {
            env = (KahluaTable)LuaManager.env.rawget((Object)split[i]);
        }
        return (LuaClosure)env.rawget((Object)split[split.length - 1]);
    }
    
    public static void transferItem(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem, final ItemContainer itemContainer, final ItemContainer itemContainer2) {
        LuaManager.caller.pcall(LuaManager.thread, (Object)LuaManager.env.rawget((Object)"javaTransferItems"), new Object[] { isoGameCharacter, inventoryItem, itemContainer, itemContainer2 });
    }
    
    public static void dropItem(final InventoryItem inventoryItem) {
        LuaManager.caller.pcall(LuaManager.thread, (Object)getDotDelimitedClosure("ISInventoryPaneContextMenu.dropItem"), (Object)inventoryItem);
    }
    
    public static IsoGridSquare AdjacentFreeTileFinder(final IsoGridSquare isoGridSquare, final IsoPlayer isoPlayer) {
        return (IsoGridSquare)LuaManager.caller.pcall(LuaManager.thread, (Object)((KahluaTable)LuaManager.env.rawget((Object)"AdjacentFreeTileFinder")).rawget((Object)"Find"), new Object[] { isoGridSquare, isoPlayer })[1];
    }
    
    public static Object RunLua(final String s) {
        return RunLua(s, false);
    }
    
    public static Object RunLua(final String s, final boolean b) {
        final String replace = s.replace("\\", "/");
        if (LuaManager.loading.contains(replace)) {
            DebugLog.Lua.warn("recursive require(): %s", replace);
            return null;
        }
        LuaManager.loading.add(replace);
        try {
            return RunLuaInternal(s, b);
        }
        finally {
            LuaManager.loading.remove(replace);
        }
    }
    
    private static Object RunLuaInternal(String currentfullFile, final boolean rewriteEvents) {
        currentfullFile = currentfullFile.replace("\\", "/");
        if (LuaManager.loaded.contains(currentfullFile)) {
            return LuaManager.loadedReturn.get(currentfullFile);
        }
        FuncState.currentFile = currentfullFile.substring(currentfullFile.lastIndexOf(47) + 1);
        FuncState.currentfullFile = currentfullFile;
        final String key = currentfullFile;
        currentfullFile = ZomboidFileSystem.instance.getString(currentfullFile.replace("\\", "/"));
        if (DebugLog.isEnabled(DebugType.Lua)) {
            DebugLog.Lua.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getRelativeFile(currentfullFile)));
        }
        InputStreamReader streamReader;
        try {
            streamReader = IndieFileLoader.getStreamReader(currentfullFile);
        }
        catch (FileNotFoundException ex) {
            ExceptionLogger.logException(ex);
            return null;
        }
        LuaCompiler.rewriteEvents = rewriteEvents;
        LuaClosure loadis;
        try {
            final BufferedReader bufferedReader = new BufferedReader(streamReader);
            try {
                loadis = LuaCompiler.loadis((Reader)bufferedReader, currentfullFile.substring(currentfullFile.lastIndexOf(47) + 1), LuaManager.env);
                bufferedReader.close();
            }
            catch (Throwable t) {
                try {
                    bufferedReader.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Exception ex2) {
            Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, currentfullFile), (Object)null);
            ExceptionLogger.logException(ex2);
            LuaManager.thread.debugException(ex2);
            return null;
        }
        LuaManager.luaFunctionMap.clear();
        AttachedWeaponDefinitions.instance.m_dirty = true;
        DefaultClothing.instance.m_dirty = true;
        HairOutfitDefinitions.instance.m_dirty = true;
        ZombiesZoneDefinition.bDirty = true;
        final LuaReturn protectedCall = LuaManager.caller.protectedCall(LuaManager.thread, (Object)loadis, new Object[0]);
        if (!protectedCall.isSuccess()) {
            Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, protectedCall.getErrorString(), (Object)null);
            if (protectedCall.getJavaException() != null) {
                Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, protectedCall.getJavaException().toString(), (Object)null);
            }
            Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, protectedCall.getLuaStackTrace(), (Object)null);
        }
        LuaManager.loaded.add(key);
        final Object value = (protectedCall.isSuccess() && protectedCall.size() > 0) ? protectedCall.getFirst() : null;
        if (value != null) {
            LuaManager.loadedReturn.put(key, value);
        }
        else {
            LuaManager.loadedReturn.remove(key);
        }
        LuaCompiler.rewriteEvents = false;
        return value;
    }
    
    public static Object getFunctionObject(final String s) {
        final Object value = LuaManager.luaFunctionMap.get(s);
        if (value != null) {
            return value;
        }
        KahluaTable env = LuaManager.env;
        Object value2;
        if (s.contains(".")) {
            final String[] split = s.split("\\.");
            for (int i = 0; i < split.length - 1; ++i) {
                final KahluaTable kahluaTable = Type.tryCastTo(env.rawget((Object)split[i]), KahluaTable.class);
                if (kahluaTable == null) {
                    DebugLog.General.error("no such function \"%s\"", s);
                    return null;
                }
                env = kahluaTable;
            }
            value2 = env.rawget((Object)split[split.length - 1]);
        }
        else {
            value2 = env.rawget((Object)s);
        }
        if (value2 instanceof JavaFunction || value2 instanceof LuaClosure) {
            LuaManager.luaFunctionMap.put(s, value2);
            return value2;
        }
        DebugLog.General.error("no such function \"%s\"", s);
        return null;
    }
    
    public static void Test() throws IOException {
    }
    
    public static Object get(final Object o) {
        return LuaManager.env.rawget(o);
    }
    
    public static void call(final String s, final Object o) {
        LuaManager.caller.pcall(LuaManager.thread, LuaManager.env.rawget((Object)s), o);
    }
    
    private static void exposeKeyboardKeys(final KahluaTable kahluaTable) {
        final Object rawget = kahluaTable.rawget((Object)"Keyboard");
        if (!(rawget instanceof KahluaTable)) {
            return;
        }
        final KahluaTable kahluaTable2 = (KahluaTable)rawget;
        final Field[] fields = Keyboard.class.getFields();
        try {
            for (final Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType().equals(Integer.TYPE) && field.getName().startsWith("KEY_")) {
                    if (!field.getName().endsWith("WIN")) {
                        kahluaTable2.rawset((Object)field.getName(), (Object)(double)field.getInt(null));
                    }
                }
            }
        }
        catch (Exception ex) {}
    }
    
    private static void exposeLuaCalendar() {
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"PZCalendar");
        if (kahluaTable == null) {
            return;
        }
        final Field[] fields = Calendar.class.getFields();
        try {
            for (final Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    if (field.getType().equals(Integer.TYPE)) {
                        kahluaTable.rawset((Object)field.getName(), (Object)BoxedStaticValues.toDouble(field.getInt(null)));
                    }
                }
            }
        }
        catch (Exception ex) {}
        LuaManager.env.rawset((Object)"Calendar", (Object)kahluaTable);
    }
    
    public static String getHourMinuteJava() {
        String s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Calendar.getInstance().get(12));
        if (Calendar.getInstance().get(12) < 10) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, Calendar.getInstance().get(11), s);
    }
    
    public static KahluaTable copyTable(final KahluaTable kahluaTable) {
        return copyTable(null, kahluaTable);
    }
    
    public static KahluaTable copyTable(KahluaTable table, final KahluaTable kahluaTable) {
        if (table == null) {
            table = LuaManager.platform.newTable();
        }
        else {
            table.wipe();
        }
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            return table;
        }
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            final Object key = iterator.getKey();
            final Object value = iterator.getValue();
            if (value instanceof KahluaTable) {
                table.rawset(key, (Object)copyTable(null, (KahluaTable)value));
            }
            else {
                table.rawset(key, value);
            }
        }
        return table;
    }
    
    static {
        LuaManager.converterManager = new KahluaConverterManager();
        LuaManager.platform = new J2SEPlatform();
        LuaManager.caller = new LuaCaller(LuaManager.converterManager);
        LuaManager.debugcaller = new LuaCaller(LuaManager.converterManager);
        LuaManager.loaded = new ArrayList<String>();
        loading = new HashSet<String>();
        LuaManager.loadedReturn = new HashMap<String, Object>();
        LuaManager.checksumDone = false;
        LuaManager.loadList = new ArrayList<String>();
        LuaManager.paths = new ArrayList<String>();
        luaFunctionMap = new HashMap<String, Object>();
        s_wiping = new HashSet<KahluaTable>();
    }
    
    public static final class Exposer extends LuaJavaClassExposer
    {
        private final HashSet<Class<?>> exposed;
        
        public Exposer(final KahluaConverterManager kahluaConverterManager, final Platform platform, final KahluaTable kahluaTable) {
            super(kahluaConverterManager, platform, kahluaTable);
            this.exposed = new HashSet<Class<?>>();
        }
        
        public void exposeAll() {
            this.setExposed(BufferedReader.class);
            this.setExposed(BufferedWriter.class);
            this.setExposed(DataInputStream.class);
            this.setExposed(DataOutputStream.class);
            this.setExposed(Double.class);
            this.setExposed(Long.class);
            this.setExposed(Float.class);
            this.setExposed(Integer.class);
            this.setExposed(Math.class);
            this.setExposed(Void.class);
            this.setExposed(SimpleDateFormat.class);
            this.setExposed(ArrayList.class);
            this.setExposed(EnumMap.class);
            this.setExposed(HashMap.class);
            this.setExposed(LinkedList.class);
            this.setExposed(Stack.class);
            this.setExposed(Vector.class);
            this.setExposed(Iterator.class);
            this.setExposed(EmitterType.class);
            this.setExposed(FMODAudio.class);
            this.setExposed(FMODSoundBank.class);
            this.setExposed(FMODSoundEmitter.class);
            this.setExposed(Vector2f.class);
            this.setExposed(Vector3f.class);
            this.setExposed(KahluaUtil.class);
            this.setExposed(DummySoundBank.class);
            this.setExposed(DummySoundEmitter.class);
            this.setExposed(BaseSoundEmitter.class);
            this.setExposed(GameSound.class);
            this.setExposed(GameSoundClip.class);
            this.setExposed(AttackState.class);
            this.setExposed(BurntToDeath.class);
            this.setExposed(ClimbDownSheetRopeState.class);
            this.setExposed(ClimbOverFenceState.class);
            this.setExposed(ClimbOverWallState.class);
            this.setExposed(ClimbSheetRopeState.class);
            this.setExposed(ClimbThroughWindowState.class);
            this.setExposed(CloseWindowState.class);
            this.setExposed(CrawlingZombieTurnState.class);
            this.setExposed(FakeDeadAttackState.class);
            this.setExposed(FakeDeadZombieState.class);
            this.setExposed(FishingState.class);
            this.setExposed(FitnessState.class);
            this.setExposed(IdleState.class);
            this.setExposed(LungeState.class);
            this.setExposed(OpenWindowState.class);
            this.setExposed(PathFindState.class);
            this.setExposed(PlayerActionsState.class);
            this.setExposed(PlayerAimState.class);
            this.setExposed(PlayerEmoteState.class);
            this.setExposed(PlayerExtState.class);
            this.setExposed(PlayerFallDownState.class);
            this.setExposed(PlayerFallingState.class);
            this.setExposed(PlayerGetUpState.class);
            this.setExposed(PlayerHitReactionPVPState.class);
            this.setExposed(PlayerHitReactionState.class);
            this.setExposed(PlayerKnockedDown.class);
            this.setExposed(PlayerOnGroundState.class);
            this.setExposed(PlayerSitOnGroundState.class);
            this.setExposed(PlayerStrafeState.class);
            this.setExposed(SmashWindowState.class);
            this.setExposed(StaggerBackState.class);
            this.setExposed(SwipeStatePlayer.class);
            this.setExposed(ThumpState.class);
            this.setExposed(WalkTowardState.class);
            this.setExposed(ZombieFallDownState.class);
            this.setExposed(ZombieGetDownState.class);
            this.setExposed(ZombieGetUpState.class);
            this.setExposed(ZombieIdleState.class);
            this.setExposed(ZombieOnGroundState.class);
            this.setExposed(ZombieReanimateState.class);
            this.setExposed(ZombieSittingState.class);
            this.setExposed(GameCharacterAIBrain.class);
            this.setExposed(MapKnowledge.class);
            this.setExposed(BodyPartType.class);
            this.setExposed(BodyPart.class);
            this.setExposed(BodyDamage.class);
            this.setExposed(Thermoregulator.class);
            this.setExposed(Thermoregulator.ThermalNode.class);
            this.setExposed(Metabolics.class);
            this.setExposed(Fitness.class);
            this.setExposed(GameKeyboard.class);
            this.setExposed(LuaTimedAction.class);
            this.setExposed(LuaTimedActionNew.class);
            this.setExposed(Moodle.class);
            this.setExposed(Moodles.class);
            this.setExposed(MoodleType.class);
            this.setExposed(ProfessionFactory.class);
            this.setExposed(ProfessionFactory.Profession.class);
            this.setExposed(PerkFactory.class);
            this.setExposed(PerkFactory.Perk.class);
            this.setExposed(PerkFactory.Perks.class);
            this.setExposed(ObservationFactory.class);
            this.setExposed(ObservationFactory.Observation.class);
            this.setExposed(TraitFactory.class);
            this.setExposed(TraitFactory.Trait.class);
            this.setExposed(IsoDummyCameraCharacter.class);
            this.setExposed(Stats.class);
            this.setExposed(SurvivorDesc.class);
            this.setExposed(SurvivorFactory.class);
            this.setExposed(SurvivorFactory.SurvivorType.class);
            this.setExposed(IsoGameCharacter.class);
            this.setExposed(IsoGameCharacter.PerkInfo.class);
            this.setExposed(IsoGameCharacter.XP.class);
            this.setExposed(IsoGameCharacter.CharacterTraits.class);
            this.setExposed(TraitCollection.TraitSlot.class);
            this.setExposed(TraitCollection.class);
            this.setExposed(IsoPlayer.class);
            this.setExposed(IsoSurvivor.class);
            this.setExposed(IsoZombie.class);
            this.setExposed(CharacterActionAnims.class);
            this.setExposed(HaloTextHelper.class);
            this.setExposed(HaloTextHelper.ColorRGB.class);
            this.setExposed(NetworkAIParams.class);
            this.setExposed(BloodBodyPartType.class);
            this.setExposed(Clipboard.class);
            this.setExposed(AngelCodeFont.class);
            this.setExposed(ZLogger.class);
            this.setExposed(PropertyContainer.class);
            this.setExposed(ClothingItem.class);
            this.setExposed(AnimatorDebugMonitor.class);
            this.setExposed(ColorInfo.class);
            this.setExposed(Texture.class);
            this.setExposed(SteamFriend.class);
            this.setExposed(SteamUGCDetails.class);
            this.setExposed(SteamWorkshopItem.class);
            this.setExposed(Color.class);
            this.setExposed(Colors.class);
            this.setExposed(Core.class);
            this.setExposed(GameVersion.class);
            this.setExposed(ImmutableColor.class);
            this.setExposed(Language.class);
            this.setExposed(PerformanceSettings.class);
            this.setExposed(SpriteRenderer.class);
            this.setExposed(Translator.class);
            this.setExposed(PZMath.class);
            this.setExposed(DebugLog.class);
            this.setExposed(DebugOptions.class);
            this.setExposed(BooleanDebugOption.class);
            this.setExposed(DebugType.class);
            this.setExposed(ErosionConfig.class);
            this.setExposed(ErosionConfig.Debug.class);
            this.setExposed(ErosionConfig.Season.class);
            this.setExposed(ErosionConfig.Seeds.class);
            this.setExposed(ErosionConfig.Time.class);
            this.setExposed(ErosionMain.class);
            this.setExposed(ErosionSeason.class);
            this.setExposed(AnimationViewerState.class);
            this.setExposed(AnimationViewerState.BooleanDebugOption.class);
            this.setExposed(AttachmentEditorState.class);
            this.setExposed(ChooseGameInfo.Mod.class);
            this.setExposed(DebugChunkState.class);
            this.setExposed(DebugChunkState.BooleanDebugOption.class);
            this.setExposed(DebugGlobalObjectState.class);
            this.setExposed(GameLoadingState.class);
            this.setExposed(MainScreenState.class);
            this.setExposed(CGlobalObject.class);
            this.setExposed(CGlobalObjects.class);
            this.setExposed(CGlobalObjectSystem.class);
            this.setExposed(SGlobalObject.class);
            this.setExposed(SGlobalObjects.class);
            this.setExposed(SGlobalObjectSystem.class);
            this.setExposed(Mouse.class);
            this.setExposed(AlarmClock.class);
            this.setExposed(AlarmClockClothing.class);
            this.setExposed(Clothing.class);
            this.setExposed(Clothing.ClothingPatch.class);
            this.setExposed(Clothing.ClothingPatchFabricType.class);
            this.setExposed(ComboItem.class);
            this.setExposed(Drainable.class);
            this.setExposed(DrainableComboItem.class);
            this.setExposed(Food.class);
            this.setExposed(HandWeapon.class);
            this.setExposed(InventoryContainer.class);
            this.setExposed(Key.class);
            this.setExposed(KeyRing.class);
            this.setExposed(Literature.class);
            this.setExposed(MapItem.class);
            this.setExposed(Moveable.class);
            this.setExposed(Radio.class);
            this.setExposed(WeaponPart.class);
            this.setExposed(ItemContainer.class);
            this.setExposed(ItemPickerJava.class);
            this.setExposed(InventoryItem.class);
            this.setExposed(InventoryItemFactory.class);
            this.setExposed(FixingManager.class);
            this.setExposed(RecipeManager.class);
            this.setExposed(IsoRegions.class);
            this.setExposed(IsoRegionsLogger.class);
            this.setExposed(IsoRegionsLogger.IsoRegionLog.class);
            this.setExposed(IsoRegionLogType.class);
            this.setExposed(DataCell.class);
            this.setExposed(DataChunk.class);
            this.setExposed(IsoChunkRegion.class);
            this.setExposed(IsoWorldRegion.class);
            this.setExposed(IsoRegionsRenderer.class);
            this.setExposed(IsoRegionsRenderer.BooleanDebugOption.class);
            this.setExposed(IsoBuilding.class);
            this.setExposed(IsoRoom.class);
            this.setExposed(SafeHouse.class);
            this.setExposed(BarricadeAble.class);
            this.setExposed(IsoBarbecue.class);
            this.setExposed(IsoBarricade.class);
            this.setExposed(IsoBrokenGlass.class);
            this.setExposed(IsoClothingDryer.class);
            this.setExposed(IsoClothingWasher.class);
            this.setExposed(IsoCurtain.class);
            this.setExposed(IsoCarBatteryCharger.class);
            this.setExposed(IsoDeadBody.class);
            this.setExposed(IsoDoor.class);
            this.setExposed(IsoFire.class);
            this.setExposed(IsoFireManager.class);
            this.setExposed(IsoFireplace.class);
            this.setExposed(IsoGenerator.class);
            this.setExposed(IsoJukebox.class);
            this.setExposed(IsoLightSwitch.class);
            this.setExposed(IsoMannequin.class);
            this.setExposed(IsoMolotovCocktail.class);
            this.setExposed(IsoWaveSignal.class);
            this.setExposed(IsoRadio.class);
            this.setExposed(IsoTelevision.class);
            this.setExposed(IsoStove.class);
            this.setExposed(IsoThumpable.class);
            this.setExposed(IsoTrap.class);
            this.setExposed(IsoTree.class);
            this.setExposed(IsoWheelieBin.class);
            this.setExposed(IsoWindow.class);
            this.setExposed(IsoWindowFrame.class);
            this.setExposed(IsoWorldInventoryObject.class);
            this.setExposed(IsoZombieGiblets.class);
            this.setExposed(RainManager.class);
            this.setExposed(ObjectRenderEffects.class);
            this.setExposed(HumanVisual.class);
            this.setExposed(ItemVisual.class);
            this.setExposed(ItemVisuals.class);
            this.setExposed(IsoSprite.class);
            this.setExposed(IsoSpriteInstance.class);
            this.setExposed(IsoSpriteManager.class);
            this.setExposed(IsoSpriteGrid.class);
            this.setExposed(IsoFlagType.class);
            this.setExposed(IsoObjectType.class);
            this.setExposed(ClimateManager.class);
            this.setExposed(ClimateManager.DayInfo.class);
            this.setExposed(ClimateManager.ClimateFloat.class);
            this.setExposed(ClimateManager.ClimateColor.class);
            this.setExposed(ClimateManager.ClimateBool.class);
            this.setExposed(WeatherPeriod.class);
            this.setExposed(WeatherPeriod.WeatherStage.class);
            this.setExposed(WeatherPeriod.StrLerpVal.class);
            this.setExposed(ClimateManager.AirFront.class);
            this.setExposed(ThunderStorm.class);
            this.setExposed(ThunderStorm.ThunderCloud.class);
            this.setExposed(IsoWeatherFX.class);
            this.setExposed(Temperature.class);
            this.setExposed(ClimateColorInfo.class);
            this.setExposed(ClimateValues.class);
            this.setExposed(ClimateForecaster.class);
            this.setExposed(ClimateForecaster.DayForecast.class);
            this.setExposed(ClimateForecaster.ForecastValue.class);
            this.setExposed(ClimateHistory.class);
            this.setExposed(WorldFlares.class);
            this.setExposed(WorldFlares.Flare.class);
            this.setExposed(ImprovedFog.class);
            this.setExposed(ClimateMoon.class);
            this.setExposed(IsoPuddles.class);
            this.setExposed(IsoPuddles.PuddlesFloat.class);
            this.setExposed(BentFences.class);
            this.setExposed(BrokenFences.class);
            this.setExposed(ContainerOverlays.class);
            this.setExposed(IsoChunk.class);
            this.setExposed(BuildingDef.class);
            this.setExposed(IsoCamera.class);
            this.setExposed(IsoCell.class);
            this.setExposed(IsoChunkMap.class);
            this.setExposed(IsoDirections.class);
            this.setExposed(IsoDirectionSet.class);
            this.setExposed(IsoGridSquare.class);
            this.setExposed(IsoHeatSource.class);
            this.setExposed(IsoLightSource.class);
            this.setExposed(IsoLot.class);
            this.setExposed(IsoLuaMover.class);
            this.setExposed(IsoMetaChunk.class);
            this.setExposed(IsoMetaCell.class);
            this.setExposed(IsoMetaGrid.class);
            this.setExposed(IsoMetaGrid.Trigger.class);
            this.setExposed(IsoMetaGrid.VehicleZone.class);
            this.setExposed(IsoMetaGrid.Zone.class);
            this.setExposed(IsoMovingObject.class);
            this.setExposed(IsoObject.class);
            this.setExposed(IsoObjectPicker.class);
            this.setExposed(IsoPushableObject.class);
            this.setExposed(IsoUtils.class);
            this.setExposed(IsoWorld.class);
            this.setExposed(LosUtil.class);
            this.setExposed(MetaObject.class);
            this.setExposed(RoomDef.class);
            this.setExposed(SliceY.class);
            this.setExposed(TileOverlays.class);
            this.setExposed(Vector2.class);
            this.setExposed(Vector3.class);
            this.setExposed(WorldMarkers.class);
            this.setExposed(WorldMarkers.DirectionArrow.class);
            this.setExposed(WorldMarkers.GridSquareMarker.class);
            this.setExposed(WorldMarkers.PlayerHomingPoint.class);
            this.setExposed(SearchMode.class);
            this.setExposed(SearchMode.PlayerSearchMode.class);
            this.setExposed(SearchMode.SearchModeFloat.class);
            this.setExposed(IsoMarkers.class);
            this.setExposed(IsoMarkers.IsoMarker.class);
            this.setExposed(LuaEventManager.class);
            this.setExposed(MapObjects.class);
            this.setExposed(ActiveMods.class);
            this.setExposed(Server.class);
            this.setExposed(ServerOptions.class);
            this.setExposed(ServerOptions.BooleanServerOption.class);
            this.setExposed(ServerOptions.DoubleServerOption.class);
            this.setExposed(ServerOptions.IntegerServerOption.class);
            this.setExposed(ServerOptions.StringServerOption.class);
            this.setExposed(ServerOptions.TextServerOption.class);
            this.setExposed(ServerSettings.class);
            this.setExposed(ServerSettingsManager.class);
            this.setExposed(ZombiePopulationRenderer.class);
            this.setExposed(ZombiePopulationRenderer.BooleanDebugOption.class);
            this.setExposed(RadioAPI.class);
            this.setExposed(DeviceData.class);
            this.setExposed(DevicePresets.class);
            this.setExposed(PresetEntry.class);
            this.setExposed(ZomboidRadio.class);
            this.setExposed(RadioData.class);
            this.setExposed(RadioScriptManager.class);
            this.setExposed(DynamicRadioChannel.class);
            this.setExposed(RadioChannel.class);
            this.setExposed(RadioBroadCast.class);
            this.setExposed(RadioLine.class);
            this.setExposed(RadioScript.class);
            this.setExposed(RadioScript.ExitOption.class);
            this.setExposed(ChannelCategory.class);
            this.setExposed(SLSoundManager.class);
            this.setExposed(StorySound.class);
            this.setExposed(StorySoundEvent.class);
            this.setExposed(EventSound.class);
            this.setExposed(DataPoint.class);
            this.setExposed(RecordedMedia.class);
            this.setExposed(MediaData.class);
            this.setExposed(EvolvedRecipe.class);
            this.setExposed(Fixing.class);
            this.setExposed(Fixing.Fixer.class);
            this.setExposed(Fixing.FixerSkill.class);
            this.setExposed(GameSoundScript.class);
            this.setExposed(Item.class);
            this.setExposed(Item.Type.class);
            this.setExposed(ItemRecipe.class);
            this.setExposed(ModelAttachment.class);
            this.setExposed(ModelScript.class);
            this.setExposed(MovableRecipe.class);
            this.setExposed(Recipe.class);
            this.setExposed(Recipe.RequiredSkill.class);
            this.setExposed(Recipe.Result.class);
            this.setExposed(Recipe.Source.class);
            this.setExposed(ScriptModule.class);
            this.setExposed(VehicleScript.class);
            this.setExposed(VehicleScript.Area.class);
            this.setExposed(VehicleScript.Model.class);
            this.setExposed(VehicleScript.Part.class);
            this.setExposed(VehicleScript.Passenger.class);
            this.setExposed(VehicleScript.PhysicsShape.class);
            this.setExposed(VehicleScript.Position.class);
            this.setExposed(VehicleScript.Wheel.class);
            this.setExposed(ScriptManager.class);
            this.setExposed(ActionProgressBar.class);
            this.setExposed(Clock.class);
            this.setExposed(UIDebugConsole.class);
            this.setExposed(ModalDialog.class);
            this.setExposed(MoodlesUI.class);
            this.setExposed(NewHealthPanel.class);
            this.setExposed(ObjectTooltip.class);
            this.setExposed(ObjectTooltip.Layout.class);
            this.setExposed(ObjectTooltip.LayoutItem.class);
            this.setExposed(RadarPanel.class);
            this.setExposed(RadialMenu.class);
            this.setExposed(RadialProgressBar.class);
            this.setExposed(SpeedControls.class);
            this.setExposed(TextManager.class);
            this.setExposed(UI3DModel.class);
            this.setExposed(UIElement.class);
            this.setExposed(UIFont.class);
            this.setExposed(UITransition.class);
            this.setExposed(UIManager.class);
            this.setExposed(UIServerToolbox.class);
            this.setExposed(UITextBox2.class);
            this.setExposed(VehicleGauge.class);
            this.setExposed(TextDrawObject.class);
            this.setExposed(PZArrayList.class);
            this.setExposed(PZCalendar.class);
            this.setExposed(BaseVehicle.class);
            this.setExposed(EditVehicleState.class);
            this.setExposed(PathFindBehavior2.BehaviorResult.class);
            this.setExposed(PathFindBehavior2.class);
            this.setExposed(PathFindState2.class);
            this.setExposed(UI3DScene.class);
            this.setExposed(VehicleDoor.class);
            this.setExposed(VehicleLight.class);
            this.setExposed(VehiclePart.class);
            this.setExposed(VehicleType.class);
            this.setExposed(VehicleWindow.class);
            this.setExposed(AttachedItem.class);
            this.setExposed(AttachedItems.class);
            this.setExposed(AttachedLocation.class);
            this.setExposed(AttachedLocationGroup.class);
            this.setExposed(AttachedLocations.class);
            this.setExposed(WornItems.class);
            this.setExposed(WornItem.class);
            this.setExposed(BodyLocation.class);
            this.setExposed(BodyLocationGroup.class);
            this.setExposed(BodyLocations.class);
            this.setExposed(DummySoundManager.class);
            this.setExposed(GameSounds.class);
            this.setExposed(GameTime.class);
            this.setExposed(GameWindow.class);
            this.setExposed(SandboxOptions.class);
            this.setExposed(SandboxOptions.BooleanSandboxOption.class);
            this.setExposed(SandboxOptions.DoubleSandboxOption.class);
            this.setExposed(SandboxOptions.StringSandboxOption.class);
            this.setExposed(SandboxOptions.EnumSandboxOption.class);
            this.setExposed(SandboxOptions.IntegerSandboxOption.class);
            this.setExposed(SoundManager.class);
            this.setExposed(SystemDisabler.class);
            this.setExposed(VirtualZombieManager.class);
            this.setExposed(WorldSoundManager.class);
            this.setExposed(WorldSoundManager.WorldSound.class);
            this.setExposed(DummyCharacterSoundEmitter.class);
            this.setExposed(CharacterSoundEmitter.class);
            this.setExposed(SoundManager.AmbientSoundEffect.class);
            this.setExposed(BaseAmbientStreamManager.class);
            this.setExposed(AmbientStreamManager.class);
            this.setExposed(Nutrition.class);
            this.setExposed(BSFurnace.class);
            this.setExposed(MultiStageBuilding.class);
            this.setExposed(MultiStageBuilding.Stage.class);
            this.setExposed(SleepingEvent.class);
            this.setExposed(IsoCompost.class);
            this.setExposed(Userlog.class);
            this.setExposed(Userlog.UserlogType.class);
            this.setExposed(ConfigOption.class);
            this.setExposed(BooleanConfigOption.class);
            this.setExposed(DoubleConfigOption.class);
            this.setExposed(EnumConfigOption.class);
            this.setExposed(IntegerConfigOption.class);
            this.setExposed(StringConfigOption.class);
            this.setExposed(Faction.class);
            this.setExposed(GlobalObject.LuaFileWriter.class);
            this.setExposed(Keyboard.class);
            this.setExposed(DBResult.class);
            this.setExposed(NonPvpZone.class);
            this.setExposed(DBTicket.class);
            this.setExposed(StashSystem.class);
            this.setExposed(StashBuilding.class);
            this.setExposed(Stash.class);
            this.setExposed(ItemType.class);
            this.setExposed(RandomizedWorldBase.class);
            this.setExposed(RandomizedBuildingBase.class);
            this.setExposed(RBBurntFireman.class);
            this.setExposed(RBBasic.class);
            this.setExposed(RBBurnt.class);
            this.setExposed(RBOther.class);
            this.setExposed(RBStripclub.class);
            this.setExposed(RBSchool.class);
            this.setExposed(RBSpiffo.class);
            this.setExposed(RBPizzaWhirled.class);
            this.setExposed(RBOffice.class);
            this.setExposed(RBHairSalon.class);
            this.setExposed(RBClinic.class);
            this.setExposed(RBPileOCrepe.class);
            this.setExposed(RBCafe.class);
            this.setExposed(RBBar.class);
            this.setExposed(RBLooted.class);
            this.setExposed(RBSafehouse.class);
            this.setExposed(RBBurntCorpse.class);
            this.setExposed(RBShopLooted.class);
            this.setExposed(RBKateAndBaldspot.class);
            this.setExposed(RandomizedDeadSurvivorBase.class);
            this.setExposed(RDSZombiesEating.class);
            this.setExposed(RDSBleach.class);
            this.setExposed(RDSDeadDrunk.class);
            this.setExposed(RDSGunmanInBathroom.class);
            this.setExposed(RDSGunslinger.class);
            this.setExposed(RDSZombieLockedBathroom.class);
            this.setExposed(RDSBandPractice.class);
            this.setExposed(RDSBathroomZed.class);
            this.setExposed(RDSBedroomZed.class);
            this.setExposed(RDSFootballNight.class);
            this.setExposed(RDSHenDo.class);
            this.setExposed(RDSStagDo.class);
            this.setExposed(RDSStudentNight.class);
            this.setExposed(RDSPokerNight.class);
            this.setExposed(RDSSuicidePact.class);
            this.setExposed(RDSPrisonEscape.class);
            this.setExposed(RDSPrisonEscapeWithPolice.class);
            this.setExposed(RDSSkeletonPsycho.class);
            this.setExposed(RDSCorpsePsycho.class);
            this.setExposed(RDSSpecificProfession.class);
            this.setExposed(RDSPoliceAtHouse.class);
            this.setExposed(RDSHouseParty.class);
            this.setExposed(RDSTinFoilHat.class);
            this.setExposed(RDSHockeyPsycho.class);
            this.setExposed(RandomizedVehicleStoryBase.class);
            this.setExposed(RVSCarCrash.class);
            this.setExposed(RVSBanditRoad.class);
            this.setExposed(RVSAmbulanceCrash.class);
            this.setExposed(RVSCrashHorde.class);
            this.setExposed(RVSCarCrashCorpse.class);
            this.setExposed(RVSPoliceBlockade.class);
            this.setExposed(RVSPoliceBlockadeShooting.class);
            this.setExposed(RVSBurntCar.class);
            this.setExposed(RVSConstructionSite.class);
            this.setExposed(RVSUtilityVehicle.class);
            this.setExposed(RVSChangingTire.class);
            this.setExposed(RVSFlippedCrash.class);
            this.setExposed(RVSTrailerCrash.class);
            this.setExposed(RandomizedZoneStoryBase.class);
            this.setExposed(RZSForestCamp.class);
            this.setExposed(RZSForestCampEaten.class);
            this.setExposed(RZSBuryingCamp.class);
            this.setExposed(RZSBeachParty.class);
            this.setExposed(RZSFishingTrip.class);
            this.setExposed(RZSBBQParty.class);
            this.setExposed(RZSHunterCamp.class);
            this.setExposed(RZSSexyTime.class);
            this.setExposed(RZSTrapperCamp.class);
            this.setExposed(RZSBaseball.class);
            this.setExposed(RZSMusicFestStage.class);
            this.setExposed(RZSMusicFest.class);
            this.setExposed(MapGroups.class);
            this.setExposed(BeardStyles.class);
            this.setExposed(BeardStyle.class);
            this.setExposed(HairStyles.class);
            this.setExposed(HairStyle.class);
            this.setExposed(BloodClothingType.class);
            this.setExposed(WeaponType.class);
            this.setExposed(IsoWaterGeometry.class);
            this.setExposed(ModData.class);
            this.setExposed(WorldMarkers.class);
            this.setExposed(ChatMessage.class);
            this.setExposed(ChatBase.class);
            this.setExposed(ServerChatMessage.class);
            if (Core.bDebug) {
                this.setExposed(Field.class);
                this.setExposed(Method.class);
                this.setExposed(Coroutine.class);
            }
            UIWorldMap.setExposed(this);
            if (Core.bDebug) {
                try {
                    this.exposeMethod((Class)Class.class, Class.class.getMethod("getName", (Class<?>[])new Class[0]), LuaManager.env);
                    this.exposeMethod((Class)Class.class, Class.class.getMethod("getSimpleName", (Class<?>[])new Class[0]), LuaManager.env);
                }
                catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
            }
            final Iterator<Class<?>> iterator = this.exposed.iterator();
            while (iterator.hasNext()) {
                this.exposeLikeJavaRecursively((java.lang.reflect.Type)iterator.next(), LuaManager.env);
            }
            this.exposeGlobalFunctions((Object)new GlobalObject());
            LuaManager.exposeKeyboardKeys(LuaManager.env);
            LuaManager.exposeLuaCalendar();
        }
        
        public void setExposed(final Class<?> e) {
            this.exposed.add(e);
        }
        
        public boolean shouldExpose(final Class<?> o) {
            return o != null && this.exposed.contains(o);
        }
    }
    
    public static class GlobalObject
    {
        static FileOutputStream outStream;
        static FileInputStream inStream;
        static FileReader inFileReader;
        static BufferedReader inBufferedReader;
        static long timeLastRefresh;
        private static final TimSortComparator timSortComparator;
        
        @LuaMethod(name = "loadVehicleModel", global = true)
        public static Model loadVehicleModel(final String s, final String s2, final String s3) {
            return loadZomboidModel(s, s2, s3, "vehicle", true);
        }
        
        @LuaMethod(name = "loadStaticZomboidModel", global = true)
        public static Model loadStaticZomboidModel(final String s, final String s2, final String s3) {
            return loadZomboidModel(s, s2, s3, null, true);
        }
        
        @LuaMethod(name = "loadSkinnedZomboidModel", global = true)
        public static Model loadSkinnedZomboidModel(final String s, final String s2, final String s3) {
            return loadZomboidModel(s, s2, s3, null, false);
        }
        
        @LuaMethod(name = "loadZomboidModel", global = true)
        public static Model loadZomboidModel(final String s, String substring, String substring2, String s2, final boolean bStatic) {
            try {
                if (substring.startsWith("/")) {
                    substring = substring.substring(1);
                }
                if (substring2.startsWith("/")) {
                    substring2 = substring2.substring(1);
                }
                if (StringUtils.isNullOrWhitespace(s2)) {
                    s2 = "basicEffect";
                }
                if ("vehicle".equals(s2) && !Core.getInstance().getPerfReflectionsOnLoad()) {
                    s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
                }
                final Model tryGetLoadedModel = ModelManager.instance.tryGetLoadedModel(substring, substring2, bStatic, s2, false);
                if (tryGetLoadedModel != null) {
                    return tryGetLoadedModel;
                }
                ModelManager.instance.setModelMetaData(s, substring, substring2, s2, bStatic);
                final Model.ModelAssetParams modelAssetParams = new Model.ModelAssetParams();
                modelAssetParams.bStatic = bStatic;
                modelAssetParams.meshName = substring;
                modelAssetParams.shaderName = s2;
                modelAssetParams.textureName = substring2;
                modelAssetParams.textureFlags = ModelManager.instance.getTextureFlags();
                final Model model = (Model)ModelAssetManager.instance.load(new AssetPath(s), modelAssetParams);
                if (model != null) {
                    ModelManager.instance.putLoadedModel(substring, substring2, bStatic, s2, model);
                }
                return model;
            }
            catch (Exception ex) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String;, s, substring, substring2, s2, bStatic));
                ex.printStackTrace();
                return null;
            }
        }
        
        @LuaMethod(name = "setModelMetaData", global = true)
        public static void setModelMetaData(final String s, String substring, String substring2, final String s2, final boolean b) {
            if (substring.startsWith("/")) {
                substring = substring.substring(1);
            }
            if (substring2.startsWith("/")) {
                substring2 = substring2.substring(1);
            }
            ModelManager.instance.setModelMetaData(s, substring, substring2, s2, b);
        }
        
        @LuaMethod(name = "reloadModelsMatching", global = true)
        public static void reloadModelsMatching(final String s) {
            ModelManager.instance.reloadModelsMatching(s);
        }
        
        @LuaMethod(name = "getSLSoundManager", global = true)
        public static SLSoundManager getSLSoundManager() {
            return null;
        }
        
        @LuaMethod(name = "getRadioAPI", global = true)
        public static RadioAPI getRadioAPI() {
            if (RadioAPI.hasInstance()) {
                return RadioAPI.getInstance();
            }
            return null;
        }
        
        @LuaMethod(name = "getRadioTranslators", global = true)
        public static ArrayList<String> getRadioTranslators(final Language language) {
            return RadioData.getTranslatorNames(language);
        }
        
        @LuaMethod(name = "getTranslatorCredits", global = true)
        public static ArrayList<String> getTranslatorCredits(final Language language) {
            final File file = new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, language.name())));
            try {
                final FileReader in = new FileReader(file);
                try {
                    final BufferedReader bufferedReader = new BufferedReader(in);
                    try {
                        final ArrayList<String> list = new ArrayList<String>();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (!StringUtils.isNullOrWhitespace(line)) {
                                list.add(line.trim());
                            }
                        }
                        final ArrayList<String> list2 = list;
                        bufferedReader.close();
                        in.close();
                        return list2;
                    }
                    catch (Throwable t) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                }
                catch (Throwable t2) {
                    try {
                        in.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
            }
            catch (FileNotFoundException ex2) {
                return null;
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                return null;
            }
        }
        
        @LuaMethod(name = "getBehaviourDebugPlayer", global = true)
        public static IsoGameCharacter getBehaviourDebugPlayer() {
            return null;
        }
        
        @LuaMethod(name = "setBehaviorStep", global = true)
        public static void setBehaviorStep(final boolean b) {
        }
        
        @LuaMethod(name = "getPuddlesManager", global = true)
        public static IsoPuddles getPuddlesManager() {
            return IsoPuddles.getInstance();
        }
        
        @LuaMethod(name = "setPuddles", global = true)
        public static void setPuddles(final float adminValue) {
            final IsoPuddles.PuddlesFloat puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(3);
            puddlesFloat.setEnableAdmin(true);
            puddlesFloat.setAdminValue(adminValue);
            final IsoPuddles.PuddlesFloat puddlesFloat2 = IsoPuddles.getInstance().getPuddlesFloat(1);
            puddlesFloat2.setEnableAdmin(true);
            puddlesFloat2.setAdminValue(PZMath.clamp_01(adminValue * 1.2f));
        }
        
        @LuaMethod(name = "getZomboidRadio", global = true)
        public static ZomboidRadio getZomboidRadio() {
            if (ZomboidRadio.hasInstance()) {
                return ZomboidRadio.getInstance();
            }
            return null;
        }
        
        @LuaMethod(name = "getRandomUUID", global = true)
        public static String getRandomUUID() {
            return ModUtilsJava.getRandomUUID();
        }
        
        @LuaMethod(name = "sendItemListNet", global = true)
        public static boolean sendItemListNet(final IsoPlayer isoPlayer, final ArrayList<InventoryItem> list, final IsoPlayer isoPlayer2, final String s, final String s2) {
            return ModUtilsJava.sendItemListNet(isoPlayer, list, isoPlayer2, s, s2);
        }
        
        @LuaMethod(name = "instanceof", global = true)
        public static boolean instof(final Object o, final String key) {
            if ("PZKey".equals(key)) {}
            if (o == null) {
                return false;
            }
            if (LuaManager.exposer.TypeMap.containsKey(key)) {
                return LuaManager.exposer.TypeMap.get(key).isInstance(o);
            }
            return (key.equals("LuaClosure") && o instanceof LuaClosure) || (key.equals("KahluaTableImpl") && o instanceof KahluaTableImpl);
        }
        
        @LuaMethod(name = "serverConnect", global = true)
        public static void serverConnect(final String s, final String s2, final String s3, final String s4, final String s5, final String s6) {
            Core.GameMode = "Multiplayer";
            Core.setDifficulty("Hardcore");
            if (GameClient.connection != null) {
                GameClient.connection.forceDisconnect();
            }
            GameClient.bClient = true;
            GameClient.bCoopInvite = false;
            ZomboidFileSystem.instance.cleanMultiplayerSaves();
            GameClient.instance.doConnect(s, s2, s3, s4, s5, s6);
        }
        
        @LuaMethod(name = "serverConnectCoop", global = true)
        public static void serverConnectCoop(final String s) {
            Core.GameMode = "Multiplayer";
            Core.setDifficulty("Hardcore");
            if (GameClient.connection != null) {
                GameClient.connection.forceDisconnect();
            }
            GameClient.bClient = true;
            GameClient.bCoopInvite = true;
            GameClient.instance.doConnectCoop(s);
        }
        
        @LuaMethod(name = "sendPing", global = true)
        public static void sendPing() {
            if (GameClient.bClient) {
                final ByteBufferWriter startPingPacket = GameClient.connection.startPingPacket();
                PacketTypes.doPingPacket(startPingPacket);
                startPingPacket.putLong(System.currentTimeMillis());
                GameClient.connection.endPingPacket();
            }
        }
        
        @LuaMethod(name = "forceDisconnect", global = true)
        public static void forceDisconnect() {
            if (GameClient.connection != null) {
                GameClient.connection.forceDisconnect();
            }
        }
        
        @LuaMethod(name = "backToSinglePlayer", global = true)
        public static void backToSinglePlayer() {
            if (GameClient.bClient) {
                GameClient.instance.doDisconnect("going back to single-player");
                GameClient.bClient = false;
                GlobalObject.timeLastRefresh = 0L;
            }
        }
        
        @LuaMethod(name = "isIngameState", global = true)
        public static boolean isIngameState() {
            return GameWindow.states.current == IngameState.instance;
        }
        
        @LuaMethod(name = "requestPacketCounts", global = true)
        public static void requestPacketCounts() {
            if (GameClient.bClient) {
                GameClient.instance.requestPacketCounts();
            }
        }
        
        @LuaMethod(name = "getPacketCounts", global = true)
        public static KahluaTable getPacketCounts(final int n) {
            if (GameClient.bClient) {
                return GameClient.instance.getPacketCounts(n);
            }
            return null;
        }
        
        @LuaMethod(name = "getAllItems", global = true)
        public static ArrayList<Item> getAllItems() {
            return ScriptManager.instance.getAllItems();
        }
        
        @LuaMethod(name = "scoreboardUpdate", global = true)
        public static void scoreboardUpdate() {
            GameClient.instance.scoreboardUpdate();
        }
        
        @LuaMethod(name = "save", global = true)
        public static void save(final boolean b) {
            try {
                GameWindow.save(b);
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
        
        @LuaMethod(name = "saveGame", global = true)
        public static void saveGame() {
            save(true);
        }
        
        @LuaMethod(name = "getAllRecipes", global = true)
        public static ArrayList<Recipe> getAllRecipes() {
            return new ArrayList<Recipe>(ScriptManager.instance.getAllRecipes());
        }
        
        @LuaMethod(name = "requestUserlog", global = true)
        public static void requestUserlog(final String s) {
            if (GameClient.bClient) {
                GameClient.instance.requestUserlog(s);
            }
        }
        
        @LuaMethod(name = "addUserlog", global = true)
        public static void addUserlog(final String s, final String s2, final String s3) {
            if (GameClient.bClient) {
                GameClient.instance.addUserlog(s, s2, s3);
            }
        }
        
        @LuaMethod(name = "removeUserlog", global = true)
        public static void removeUserlog(final String s, final String s2, final String s3) {
            if (GameClient.bClient) {
                GameClient.instance.removeUserlog(s, s2, s3);
            }
        }
        
        @LuaMethod(name = "tabToX", global = true)
        public static String tabToX(String s, final int n) {
            while (s.length() < n) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
            }
            return s;
        }
        
        @LuaMethod(name = "istype", global = true)
        public static boolean isType(final Object o, final String s) {
            return LuaManager.exposer.TypeMap.containsKey(s) && LuaManager.exposer.TypeMap.get(s).equals(o.getClass());
        }
        
        @LuaMethod(name = "isoToScreenX", global = true)
        public static float isoToScreenX(final int n, final float n2, final float n3, final float n4) {
            return IsoCamera.getScreenLeft(n) + (IsoUtils.XToScreen(n2, n3, n4, 0) - IsoCamera.cameras[n].getOffX()) / Core.getInstance().getZoom(n);
        }
        
        @LuaMethod(name = "isoToScreenY", global = true)
        public static float isoToScreenY(final int n, final float n2, final float n3, final float n4) {
            return IsoCamera.getScreenTop(n) + (IsoUtils.YToScreen(n2, n3, n4, 0) - IsoCamera.cameras[n].getOffY()) / Core.getInstance().getZoom(n);
        }
        
        @LuaMethod(name = "screenToIsoX", global = true)
        public static float screenToIsoX(final int n, float n2, float n3, final float n4) {
            final float zoom = Core.getInstance().getZoom(n);
            n2 -= IsoCamera.getScreenLeft(n);
            n3 -= IsoCamera.getScreenTop(n);
            return IsoCamera.cameras[n].XToIso(n2 * zoom, n3 * zoom, n4);
        }
        
        @LuaMethod(name = "screenToIsoY", global = true)
        public static float screenToIsoY(final int n, float n2, float n3, final float n4) {
            final float zoom = Core.getInstance().getZoom(n);
            n2 -= IsoCamera.getScreenLeft(n);
            n3 -= IsoCamera.getScreenTop(n);
            return IsoCamera.cameras[n].YToIso(n2 * zoom, n3 * zoom, n4);
        }
        
        @LuaMethod(name = "getAmbientStreamManager", global = true)
        public static BaseAmbientStreamManager getAmbientStreamManager() {
            return AmbientStreamManager.instance;
        }
        
        @LuaMethod(name = "getSleepingEvent", global = true)
        public static SleepingEvent getSleepingEvent() {
            return SleepingEvent.instance;
        }
        
        @LuaMethod(name = "setPlayerMovementActive", global = true)
        public static void setPlayerMovementActive(final int n, final boolean bJoypadMovementActive) {
            IsoPlayer.players[n].bJoypadMovementActive = bJoypadMovementActive;
        }
        
        @LuaMethod(name = "setActivePlayer", global = true)
        public static void setActivePlayer(final int n) {
            if (GameClient.bClient) {
                return;
            }
            IsoPlayer.setInstance(IsoPlayer.players[n]);
            IsoCamera.CamCharacter = IsoPlayer.getInstance();
        }
        
        @LuaMethod(name = "getPlayer", global = true)
        public static IsoPlayer getPlayer() {
            return IsoPlayer.getInstance();
        }
        
        @LuaMethod(name = "getNumActivePlayers", global = true)
        public static int getNumActivePlayers() {
            return IsoPlayer.numPlayers;
        }
        
        @LuaMethod(name = "playServerSound", global = true)
        public static void playServerSound(final String s, final IsoGridSquare isoGridSquare) {
            GameServer.PlayWorldSoundServer(s, false, isoGridSquare, 0.2f, 5.0f, 1.1f, true);
        }
        
        @LuaMethod(name = "getMaxActivePlayers", global = true)
        public static int getMaxActivePlayers() {
            return 4;
        }
        
        @LuaMethod(name = "getPlayerScreenLeft", global = true)
        public static int getPlayerScreenLeft(final int n) {
            return IsoCamera.getScreenLeft(n);
        }
        
        @LuaMethod(name = "getPlayerScreenTop", global = true)
        public static int getPlayerScreenTop(final int n) {
            return IsoCamera.getScreenTop(n);
        }
        
        @LuaMethod(name = "getPlayerScreenWidth", global = true)
        public static int getPlayerScreenWidth(final int n) {
            return IsoCamera.getScreenWidth(n);
        }
        
        @LuaMethod(name = "getPlayerScreenHeight", global = true)
        public static int getPlayerScreenHeight(final int n) {
            return IsoCamera.getScreenHeight(n);
        }
        
        @LuaMethod(name = "getPlayerByOnlineID", global = true)
        public static IsoPlayer getPlayerByOnlineID(final int n) {
            if (GameServer.bServer) {
                return GameServer.IDToPlayerMap.get((short)n);
            }
            if (GameClient.bClient) {
                return GameClient.IDToPlayerMap.get((short)n);
            }
            return null;
        }
        
        @LuaMethod(name = "initUISystem", global = true)
        public static void initUISystem() {
            UIManager.init();
            LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
        }
        
        @LuaMethod(name = "getPerformance", global = true)
        public static PerformanceSettings getPerformance() {
            return PerformanceSettings.instance;
        }
        
        @LuaMethod(name = "getDBSchema", global = true)
        public static void getDBSchema() {
            GameClient.instance.getDBSchema();
        }
        
        @LuaMethod(name = "getTableResult", global = true)
        public static void getTableResult(final String s, final int n) {
            GameClient.instance.getTableResult(s, n);
        }
        
        @LuaMethod(name = "getWorldSoundManager", global = true)
        public static WorldSoundManager getWorldSoundManager() {
            return WorldSoundManager.instance;
        }
        
        @LuaMethod(name = "AddWorldSound", global = true)
        public static void AddWorldSound(final IsoPlayer isoPlayer, final int n, final int n2) {
            WorldSoundManager.instance.addSound(null, (int)isoPlayer.getX(), (int)isoPlayer.getY(), (int)isoPlayer.getZ(), n, n2, false);
        }
        
        @LuaMethod(name = "AddNoiseToken", global = true)
        public static void AddNoiseToken(final IsoGridSquare isoGridSquare, final int n) {
        }
        
        @LuaMethod(name = "pauseSoundAndMusic", global = true)
        public static void pauseSoundAndMusic() {
            DebugLog.log("EXITDEBUG: pauseSoundAndMusic 1");
            SoundManager.instance.pauseSoundAndMusic();
            DebugLog.log("EXITDEBUG: pauseSoundAndMusic 2");
        }
        
        @LuaMethod(name = "resumeSoundAndMusic", global = true)
        public static void resumeSoundAndMusic() {
            SoundManager.instance.resumeSoundAndMusic();
        }
        
        @LuaMethod(name = "isDemo", global = true)
        public static boolean isDemo() {
            Core.getInstance();
            return false;
        }
        
        @LuaMethod(name = "getTimeInMillis", global = true)
        public static long getTimeInMillis() {
            return System.currentTimeMillis();
        }
        
        @LuaMethod(name = "getCurrentCoroutine", global = true)
        public static Coroutine getCurrentCoroutine() {
            return LuaManager.thread.getCurrentCoroutine();
        }
        
        @LuaMethod(name = "reloadLuaFile", global = true)
        public static void reloadLuaFile(final String o) {
            LuaManager.loaded.remove(o);
            LuaManager.RunLua(o, true);
        }
        
        @LuaMethod(name = "reloadServerLuaFile", global = true)
        public static void reloadServerLuaFile(String o) {
            if (!GameServer.bServer) {
                return;
            }
            o = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, o);
            LuaManager.loaded.remove(o);
            LuaManager.RunLua(o, true);
        }
        
        @LuaMethod(name = "getServerSpawnRegions", global = true)
        public static KahluaTable getServerSpawnRegions() {
            if (!GameClient.bClient) {
                return null;
            }
            return GameClient.instance.getServerSpawnRegions();
        }
        
        @LuaMethod(name = "getServerOptions", global = true)
        public static ServerOptions getServerOptions() {
            return ServerOptions.instance;
        }
        
        @LuaMethod(name = "getServerName", global = true)
        public static String getServerName() {
            return GameServer.ServerName;
        }
        
        @LuaMethod(name = "getSpecificPlayer", global = true)
        public static IsoPlayer getSpecificPlayer(final int n) {
            return IsoPlayer.players[n];
        }
        
        @LuaMethod(name = "getCameraOffX", global = true)
        public static float getCameraOffX() {
            return IsoCamera.getOffX();
        }
        
        @LuaMethod(name = "getLatestSave", global = true)
        public static KahluaTable getLatestSave() {
            final KahluaTable table = LuaManager.platform.newTable();
            BufferedReader bufferedReader;
            try {
                bufferedReader = new BufferedReader(new FileReader(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator))));
            }
            catch (FileNotFoundException ex) {
                return table;
            }
            try {
                int n = 1;
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    table.rawset(n, (Object)line);
                    ++n;
                }
                bufferedReader.close();
            }
            catch (Exception ex2) {
                return table;
            }
            return table;
        }
        
        @LuaMethod(name = "isCurrentExecutionPoint", global = true)
        public static boolean isCurrentExecutionPoint(final String s, final int n) {
            int n2 = LuaManager.thread.currentCoroutine.getCallframeTop() - 1;
            if (n2 < 0) {
                n2 = 0;
            }
            final LuaCallFrame callFrame = LuaManager.thread.currentCoroutine.getCallFrame(n2);
            return callFrame.closure != null && callFrame.closure.prototype.lines[callFrame.pc] == n && s.equals(callFrame.closure.prototype.filename);
        }
        
        @LuaMethod(name = "toggleBreakOnChange", global = true)
        public static void toggleBreakOnChange(final KahluaTable kahluaTable, final Object o) {
            if (Core.bDebug) {
                LuaManager.thread.toggleBreakOnChange(kahluaTable, o);
            }
        }
        
        @LuaMethod(name = "isDebugEnabled", global = true)
        public static boolean isDebugEnabled() {
            return Core.bDebug;
        }
        
        @LuaMethod(name = "toggleBreakOnRead", global = true)
        public static void toggleBreakOnRead(final KahluaTable kahluaTable, final Object o) {
            if (Core.bDebug) {
                LuaManager.thread.toggleBreakOnRead(kahluaTable, o);
            }
        }
        
        @LuaMethod(name = "toggleBreakpoint", global = true)
        public static void toggleBreakpoint(String replace, final int n) {
            replace = replace.replace("\\", "/");
            if (Core.bDebug) {
                LuaManager.thread.breakpointToggle(replace, n);
            }
        }
        
        @LuaMethod(name = "sendVisual", global = true)
        public static void sendVisual(final IsoPlayer isoPlayer) {
            if (GameClient.bClient) {
                GameClient.instance.sendVisual(isoPlayer);
            }
        }
        
        @LuaMethod(name = "sendClothing", global = true)
        public static void sendClothing(final IsoPlayer isoPlayer) {
            if (GameClient.bClient) {
                GameClient.instance.sendClothing(isoPlayer, "", null);
            }
        }
        
        @LuaMethod(name = "hasDataReadBreakpoint", global = true)
        public static boolean hasDataReadBreakpoint(final KahluaTable kahluaTable, final Object o) {
            return LuaManager.thread.hasReadDataBreakpoint(kahluaTable, o);
        }
        
        @LuaMethod(name = "hasDataBreakpoint", global = true)
        public static boolean hasDataBreakpoint(final KahluaTable kahluaTable, final Object o) {
            return LuaManager.thread.hasDataBreakpoint(kahluaTable, o);
        }
        
        @LuaMethod(name = "hasBreakpoint", global = true)
        public static boolean hasBreakpoint(final String s, final int n) {
            return LuaManager.thread.hasBreakpoint(s, n);
        }
        
        @LuaMethod(name = "getLoadedLuaCount", global = true)
        public static int getLoadedLuaCount() {
            return LuaManager.loaded.size();
        }
        
        @LuaMethod(name = "getLoadedLua", global = true)
        public static String getLoadedLua(final int index) {
            return LuaManager.loaded.get(index);
        }
        
        @LuaMethod(name = "isServer", global = true)
        public static boolean isServer() {
            return GameServer.bServer;
        }
        
        @LuaMethod(name = "isServerSoftReset", global = true)
        public static boolean isServerSoftReset() {
            return GameServer.bServer && System.getProperty("softreset") != null;
        }
        
        @LuaMethod(name = "isClient", global = true)
        public static boolean isClient() {
            return GameClient.bClient;
        }
        
        @LuaMethod(name = "canModifyPlayerStats", global = true)
        public static boolean canModifyPlayerStats() {
            return !GameClient.bClient || GameClient.canModifyPlayerStats();
        }
        
        @LuaMethod(name = "executeQuery", global = true)
        public static void executeQuery(final String s, final KahluaTable kahluaTable) {
            GameClient.instance.executeQuery(s, kahluaTable);
        }
        
        @LuaMethod(name = "canSeePlayerStats", global = true)
        public static boolean canSeePlayerStats() {
            return GameClient.canSeePlayerStats();
        }
        
        @LuaMethod(name = "getAccessLevel", global = true)
        public static String getAccessLevel() {
            return GameClient.accessLevel;
        }
        
        @LuaMethod(name = "getOnlinePlayers", global = true)
        public static ArrayList<IsoPlayer> getOnlinePlayers() {
            if (GameServer.bServer) {
                return GameServer.getPlayers();
            }
            if (GameClient.bClient) {
                return GameClient.instance.getPlayers();
            }
            return null;
        }
        
        @LuaMethod(name = "getDebug", global = true)
        public static boolean getDebug() {
            return Core.bDebug || (GameServer.bServer && GameServer.bDebug);
        }
        
        @LuaMethod(name = "getCameraOffY", global = true)
        public static float getCameraOffY() {
            return IsoCamera.getOffY();
        }
        
        @LuaMethod(name = "createRegionFile", global = true)
        public static KahluaTable createRegionFile() {
            final KahluaTable table = LuaManager.platform.newTable();
            String s = IsoWorld.instance.getMap();
            if (s.equals("DEFAULT")) {
                final MapGroups mapGroups = new MapGroups();
                mapGroups.createGroups();
                if (mapGroups.getNumberOfGroups() != 1) {
                    throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
                }
                mapGroups.setWorld(0);
                s = IsoWorld.instance.getMap();
            }
            if (!GameClient.bClient && !GameServer.bServer) {
                s = MapGroups.addMissingVanillaDirectories(s);
            }
            final String[] split = s.split(";");
            int n = 1;
            final String[] array = split;
            for (int length = array.length, i = 0; i < length; ++i) {
                final String trim = array[i].trim();
                if (!trim.isEmpty()) {
                    if (new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim))).exists()) {
                        final KahluaTable table2 = LuaManager.platform.newTable();
                        table2.rawset((Object)"name", (Object)trim);
                        table2.rawset((Object)"file", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
                        table.rawset(n, (Object)table2);
                        ++n;
                    }
                }
            }
            return table;
        }
        
        @LuaMethod(name = "getMapDirectoryTable", global = true)
        public static KahluaTable getMapDirectoryTable() {
            final KahluaTable table = LuaManager.platform.newTable();
            final String[] list = ZomboidFileSystem.instance.getMediaFile("maps").list();
            if (list == null) {
                return table;
            }
            int n = 1;
            for (int i = 0; i < list.length; ++i) {
                final String s = list[i];
                if (!s.equals("challengemaps")) {
                    table.rawset(n, (Object)s);
                    ++n;
                }
            }
            for (final String s2 : ZomboidFileSystem.instance.getModIDs()) {
                ChooseGameInfo.Mod availableModDetails = null;
                try {
                    availableModDetails = ChooseGameInfo.getAvailableModDetails(s2);
                }
                catch (Exception ex) {}
                if (availableModDetails == null) {
                    continue;
                }
                final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, availableModDetails.getDir()));
                if (!file.exists()) {
                    continue;
                }
                final String[] list2 = file.list();
                if (list2 == null) {
                    continue;
                }
                for (int j = 0; j < list2.length; ++j) {
                    final String s3 = list2[j];
                    final ChooseGameInfo.Map mapDetails = ChooseGameInfo.getMapDetails(s3);
                    if (mapDetails.getLotDirectories() != null) {
                        if (!mapDetails.getLotDirectories().isEmpty()) {
                            if (!s3.equals("challengemaps")) {
                                table.rawset(n, (Object)s3);
                                ++n;
                            }
                        }
                    }
                }
            }
            return table;
        }
        
        @LuaMethod(name = "deleteSave", global = true)
        public static void deleteSave(final String s) {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s));
            final String[] list = file.list();
            if (list == null) {
                return;
            }
            for (int i = 0; i < list.length; ++i) {
                final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s, File.separator, list[i]));
                if (file2.isDirectory()) {
                    deleteSave(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, file2.getName()));
                }
                file2.delete();
            }
            file.delete();
        }
        
        @LuaMethod(name = "sendPlayerExtraInfo", global = true)
        public static void sendPlayerExtraInfo(final IsoPlayer isoPlayer) {
            GameClient.sendPlayerExtraInfo(isoPlayer);
        }
        
        @LuaMethod(name = "getServerAddressFromArgs", global = true)
        public static String getServerAddressFromArgs() {
            if (System.getProperty("args.server.connect") != null) {
                final String property = System.getProperty("args.server.connect");
                System.clearProperty("args.server.connect");
                return property;
            }
            return null;
        }
        
        @LuaMethod(name = "getServerPasswordFromArgs", global = true)
        public static String getServerPasswordFromArgs() {
            if (System.getProperty("args.server.password") != null) {
                final String property = System.getProperty("args.server.password");
                System.clearProperty("args.server.password");
                return property;
            }
            return null;
        }
        
        @LuaMethod(name = "getServerListFile", global = true)
        public static String getServerListFile() {
            return SteamUtils.isSteamModeEnabled() ? "ServerListSteam.txt" : "ServerList.txt";
        }
        
        @LuaMethod(name = "getServerList", global = true)
        public static KahluaTable getServerList() {
            final ArrayList<Server> list = new ArrayList<Server>();
            final KahluaTable table = LuaManager.platform.newTable();
            BufferedReader bufferedReader = null;
            try {
                final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, getServerListFile()));
                if (!file.exists()) {
                    file.createNewFile();
                }
                bufferedReader = new BufferedReader(new FileReader(file));
                Server server = null;
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("name=")) {
                        server = new Server();
                        list.add(server);
                        server.setName(line.replaceFirst("name=", ""));
                    }
                    else if (line.startsWith("ip=")) {
                        server.setIp(line.replaceFirst("ip=", ""));
                    }
                    else if (line.startsWith("localip=")) {
                        server.setLocalIP(line.replaceFirst("localip=", ""));
                    }
                    else if (line.startsWith("description=")) {
                        server.setDescription(line.replaceFirst("description=", ""));
                    }
                    else if (line.startsWith("port=")) {
                        server.setPort(line.replaceFirst("port=", ""));
                    }
                    else if (line.startsWith("user=")) {
                        server.setUserName(line.replaceFirst("user=", ""));
                    }
                    else if (line.startsWith("password=")) {
                        server.setPwd(line.replaceFirst("password=", ""));
                    }
                    else {
                        if (!line.startsWith("serverpassword=")) {
                            continue;
                        }
                        server.setServerPassword(line.replaceFirst("serverpassword=", ""));
                    }
                }
                int n = 1;
                for (int i = 0; i < list.size(); ++i) {
                    table.rawset((Object)(double)n, (Object)list.get(i));
                    ++n;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                    bufferedReader.close();
                }
                catch (Exception ex2) {}
            }
            return table;
        }
        
        @LuaMethod(name = "ping", global = true)
        public static void ping(final String s, final String s2, final String s3, final String s4) {
            GameClient.askPing = true;
            serverConnect(s, s2, s3, "", s4, "");
        }
        
        @LuaMethod(name = "stopPing", global = true)
        public static void stopPing() {
            GameClient.askPing = false;
        }
        
        @LuaMethod(name = "transformIntoKahluaTable", global = true)
        public static KahluaTable transformIntoKahluaTable(final HashMap<Object, Object> hashMap) {
            final KahluaTable table = LuaManager.platform.newTable();
            for (final Map.Entry<Object, Object> entry : hashMap.entrySet()) {
                table.rawset(entry.getKey(), entry.getValue());
            }
            return table;
        }
        
        @LuaMethod(name = "getSaveDirectory", global = true)
        public static ArrayList<File> getSaveDirectory(final String s) {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator));
            if (!file.exists() && !Core.getInstance().isNoSave()) {
                file.mkdir();
            }
            final String[] list = file.list();
            if (list == null) {
                return null;
            }
            final ArrayList<File> list2 = new ArrayList<File>();
            for (int i = 0; i < list.length; ++i) {
                final File e = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, list[i]));
                if (e.isDirectory()) {
                    list2.add(e);
                }
            }
            return list2;
        }
        
        @LuaMethod(name = "getFullSaveDirectoryTable", global = true)
        public static KahluaTable getFullSaveDirectoryTable() {
            final KahluaTable table = LuaManager.platform.newTable();
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator));
            if (!file.exists()) {
                file.mkdir();
            }
            final String[] list = file.list();
            if (list == null) {
                return table;
            }
            final ArrayList<Object> list2 = new ArrayList<Object>();
            for (int i = 0; i < list.length; ++i) {
                if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, list[i])).isDirectory() && !"Multiplayer".equals(list[i])) {
                    list2.addAll(getSaveDirectory(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, list[i])));
                }
            }
            Collections.sort(list2, (Comparator<? super Object>)new Comparator<File>() {
                @Override
                public int compare(final File file, final File file2) {
                    return Long.valueOf(file2.lastModified()).compareTo(file.lastModified());
                }
            });
            int n = 1;
            for (int j = 0; j < list2.size(); ++j) {
                table.rawset((Object)(double)n, (Object)getSaveName(list2.get(j)));
                ++n;
            }
            return table;
        }
        
        public static String getSaveName(final File file) {
            final String[] split = file.getAbsolutePath().split(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, File.separator));
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, split[split.length - 2], File.separator, file.getName());
        }
        
        @LuaMethod(name = "getSaveDirectoryTable", global = true)
        public static KahluaTable getSaveDirectoryTable() {
            return LuaManager.platform.newTable();
        }
        
        public static List<String> getMods() {
            final ArrayList<String> list = new ArrayList<String>();
            ZomboidFileSystem.instance.getAllModFolders(list);
            return list;
        }
        
        @LuaMethod(name = "doChallenge", global = true)
        public static void doChallenge(final KahluaTable kahluaTable) {
            Core.GameMode = kahluaTable.rawget((Object)"gameMode").toString();
            Core.ChallengeID = kahluaTable.rawget((Object)"id").toString();
            Core.bLastStand = Core.GameMode.equals("LastStand");
            Core.getInstance().setChallenge(true);
            getWorld().setMap(kahluaTable.getString("world"));
            IsoWorld.instance.setWorld(Integer.valueOf(Rand.Next(100000000)).toString());
            getWorld().bDoChunkMapUpdate = false;
        }
        
        @LuaMethod(name = "doTutorial", global = true)
        public static void doTutorial(final KahluaTable kahluaTable) {
            Core.GameMode = "Tutorial";
            Core.bLastStand = false;
            Core.ChallengeID = null;
            Core.getInstance().setChallenge(false);
            Core.bTutorial = true;
            getWorld().setMap(kahluaTable.getString("world"));
            getWorld().bDoChunkMapUpdate = false;
        }
        
        @LuaMethod(name = "deleteAllGameModeSaves", global = true)
        public static void deleteAllGameModeSaves(final String gameMode) {
            final String gameMode2 = Core.GameMode;
            Core.GameMode = gameMode;
            final Path value = Paths.get(ZomboidFileSystem.instance.getGameModeCacheDir(), new String[0]);
            if (!Files.exists(value, new LinkOption[0])) {
                Core.GameMode = gameMode2;
                return;
            }
            try {
                Files.walkFileTree(value, new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(final Path path, final BasicFileAttributes basicFileAttributes) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult visitFile(final Path path, final BasicFileAttributes basicFileAttributes) throws IOException {
                        Files.delete(path);
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult visitFileFailed(final Path path, final IOException ex) throws IOException {
                        ex.printStackTrace();
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult postVisitDirectory(final Path path, final IOException ex) throws IOException {
                        Files.delete(path);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            Core.GameMode = gameMode2;
        }
        
        @LuaMethod(name = "sledgeDestroy", global = true)
        public static void sledgeDestroy(final IsoObject isoObject) {
            if (GameClient.bClient) {
                GameClient.destroy(isoObject);
            }
        }
        
        @LuaMethod(name = "getTickets", global = true)
        public static void getTickets(final String s) {
            if (GameClient.bClient) {
                GameClient.getTickets(s);
            }
        }
        
        @LuaMethod(name = "addTicket", global = true)
        public static void addTicket(final String s, final String s2, final int n) {
            if (GameClient.bClient) {
                GameClient.addTicket(s, s2, n);
            }
        }
        
        @LuaMethod(name = "removeTicket", global = true)
        public static void removeTicket(final int n) {
            if (GameClient.bClient) {
                GameClient.removeTicket(n);
            }
        }
        
        @LuaMethod(name = "sendFactionInvite", global = true)
        public static void sendFactionInvite(final Faction faction, final IsoPlayer isoPlayer, final String s) {
            if (GameClient.bClient) {
                GameClient.sendFactionInvite(faction, isoPlayer, s);
            }
        }
        
        @LuaMethod(name = "acceptFactionInvite", global = true)
        public static void acceptFactionInvite(final Faction faction, final String s) {
            if (GameClient.bClient) {
                GameClient.acceptFactionInvite(faction, s);
            }
        }
        
        @LuaMethod(name = "sendSafehouseInvite", global = true)
        public static void sendSafehouseInvite(final SafeHouse safeHouse, final IsoPlayer isoPlayer, final String s) {
            if (GameClient.bClient) {
                GameClient.sendSafehouseInvite(safeHouse, isoPlayer, s);
            }
        }
        
        @LuaMethod(name = "acceptSafehouseInvite", global = true)
        public static void acceptSafehouseInvite(final SafeHouse safeHouse, final String s) {
            if (GameClient.bClient) {
                GameClient.acceptSafehouseInvite(safeHouse, s);
            }
        }
        
        @LuaMethod(name = "createHordeFromTo", global = true)
        public static void createHordeFromTo(final float n, final float n2, final float n3, final float n4, final int n5) {
            ZombiePopulationManager.instance.createHordeFromTo((int)n, (int)n2, (int)n3, (int)n4, n5);
        }
        
        @LuaMethod(name = "createHordeInAreaTo", global = true)
        public static void createHordeInAreaTo(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            ZombiePopulationManager.instance.createHordeInAreaTo(n, n2, n3, n4, n5, n6, n7);
        }
        
        @LuaMethod(name = "spawnHorde", global = true)
        public static void spawnHorde(final float n, final float n2, final float n3, final float n4, final float n5, final int n6) {
            for (int i = 0; i < n6; ++i) {
                VirtualZombieManager.instance.choices.clear();
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(Rand.Next(n, n3), Rand.Next(n2, n4), n5);
                if (gridSquare != null) {
                    VirtualZombieManager.instance.choices.add(gridSquare);
                    final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(IsoDirections.Max.index())).index(), false);
                    realZombieAlways.dressInRandomOutfit();
                    ZombieSpawnRecorder.instance.record(realZombieAlways, "LuaManager.spawnHorde");
                }
            }
        }
        
        @LuaMethod(name = "createZombie", global = true)
        public static IsoZombie createZombie(final float n, final float n2, final float n3, final SurvivorDesc survivorDesc, final int n4, final IsoDirections isoDirections) {
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3));
            final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(isoDirections.index(), false);
            ZombieSpawnRecorder.instance.record(realZombieAlways, "LuaManager.createZombie");
            return realZombieAlways;
        }
        
        @LuaMethod(name = "triggerEvent", global = true)
        public static void triggerEvent(final String s) {
            LuaEventManager.triggerEvent(s);
        }
        
        @LuaMethod(name = "triggerEvent", global = true)
        public static void triggerEvent(final String s, final Object o) {
            LuaEventManager.triggerEventGarbage(s, o);
        }
        
        @LuaMethod(name = "triggerEvent", global = true)
        public static void triggerEvent(final String s, final Object o, final Object o2) {
            LuaEventManager.triggerEventGarbage(s, o, o2);
        }
        
        @LuaMethod(name = "triggerEvent", global = true)
        public static void triggerEvent(final String s, final Object o, final Object o2, final Object o3) {
            LuaEventManager.triggerEventGarbage(s, o, o2, o3);
        }
        
        @LuaMethod(name = "triggerEvent", global = true)
        public static void triggerEvent(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
            LuaEventManager.triggerEventGarbage(s, o, o2, o3, o4);
        }
        
        @LuaMethod(name = "debugLuaTable", global = true)
        public static void debugLuaTable(final Object o, final int n) {
            if (n > 1) {
                return;
            }
            if (o instanceof KahluaTable) {
                final KahluaTable kahluaTable = (KahluaTable)o;
                final KahluaTableIterator iterator = kahluaTable.iterator();
                String s = "";
                for (int i = 0; i < n; ++i) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
                }
                do {
                    final Object key = iterator.getKey();
                    final Object value = iterator.getValue();
                    if (key != null) {
                        if (value != null) {
                            DebugLog.Lua.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;, s, key, value.toString()));
                        }
                        if (!(value instanceof KahluaTable)) {
                            continue;
                        }
                        debugLuaTable(value, n + 1);
                    }
                } while (iterator.advance());
                if (kahluaTable.getMetatable() != null) {
                    debugLuaTable(kahluaTable.getMetatable(), n);
                }
            }
        }
        
        @LuaMethod(name = "debugLuaTable", global = true)
        public static void debugLuaTable(final Object o) {
            debugLuaTable(o, 0);
        }
        
        @LuaMethod(name = "sendItemsInContainer", global = true)
        public static void sendItemsInContainer(final IsoObject isoObject, final ItemContainer itemContainer) {
            GameServer.sendItemsInContainer(isoObject, (itemContainer == null) ? isoObject.getContainer() : itemContainer);
        }
        
        @LuaMethod(name = "getModDirectoryTable", global = true)
        public static KahluaTable getModDirectoryTable() {
            final KahluaTable table = LuaManager.platform.newTable();
            final List<String> mods = getMods();
            int n = 1;
            for (int i = 0; i < mods.size(); ++i) {
                table.rawset((Object)(double)n, (Object)mods.get(i));
                ++n;
            }
            return table;
        }
        
        @LuaMethod(name = "getModInfoByID", global = true)
        public static ChooseGameInfo.Mod getModInfoByID(final String s) {
            try {
                return ChooseGameInfo.getModDetails(s);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        @LuaMethod(name = "getModInfo", global = true)
        public static ChooseGameInfo.Mod getModInfo(final String s) {
            try {
                return ChooseGameInfo.readModInfo(s);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                return null;
            }
        }
        
        @LuaMethod(name = "getMapFoldersForMod", global = true)
        public static ArrayList<String> getMapFoldersForMod(final String s) {
            try {
                final ChooseGameInfo.Mod modDetails = ChooseGameInfo.getModDetails(s);
                if (modDetails == null) {
                    return null;
                }
                final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDetails.getDir(), File.separator, File.separator);
                final File file = new File(pathname);
                if (!file.exists() || !file.isDirectory()) {
                    return null;
                }
                ArrayList<String> list = null;
                final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(file.toPath());
                try {
                    for (final Path path : directoryStream) {
                        if (Files.isDirectory(path, new LinkOption[0]) && new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pathname, File.separator, path.getFileName().toString(), File.separator)).exists()) {
                            if (list == null) {
                                list = new ArrayList<String>();
                            }
                            list.add(path.getFileName().toString());
                        }
                    }
                    if (directoryStream != null) {
                        directoryStream.close();
                    }
                }
                catch (Throwable t) {
                    if (directoryStream != null) {
                        try {
                            directoryStream.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                return list;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        @LuaMethod(name = "spawnpointsExistsForMod", global = true)
        public static boolean spawnpointsExistsForMod(final String s, final String s2) {
            try {
                final ChooseGameInfo.Mod modDetails = ChooseGameInfo.getModDetails(s);
                return modDetails != null && new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDetails.getDir(), File.separator, File.separator, File.separator, s2, File.separator)).exists();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        
        @LuaMethod(name = "getFileSeparator", global = true)
        public static String getFileSeparator() {
            return File.separator;
        }
        
        @LuaMethod(name = "getScriptManager", global = true)
        public static ScriptManager getScriptManager() {
            return ScriptManager.instance;
        }
        
        @LuaMethod(name = "checkSaveFolderExists", global = true)
        public static boolean checkSaveFolderExists(final String s) {
            return new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s)).exists();
        }
        
        @LuaMethod(name = "getAbsoluteSaveFolderName", global = true)
        public static String getAbsoluteSaveFolderName(final String s) {
            return new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s)).getAbsolutePath();
        }
        
        @LuaMethod(name = "checkSaveFileExists", global = true)
        public static boolean checkSaveFileExists(final String s) {
            return new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld, File.separator, s)).exists();
        }
        
        @LuaMethod(name = "checkSavePlayerExists", global = true)
        public static boolean checkSavePlayerExists() {
            if (!GameClient.bClient) {
                return PlayerDBHelper.isPlayerAlive(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), Core.GameSaveWorld), 1);
            }
            return ClientPlayerDB.getInstance() != null && (ClientPlayerDB.getInstance().clientLoadNetworkPlayer() && ClientPlayerDB.getInstance().isAliveMainNetworkPlayer());
        }
        
        @LuaMethod(name = "fileExists", global = true)
        public static boolean fileExists(final String s) {
            return new File(ZomboidFileSystem.instance.getString(s.replace("/", File.separator).replace("\\", File.separator))).exists();
        }
        
        @LuaMethod(name = "serverFileExists", global = true)
        public static boolean serverFileExists(final String s) {
            return new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, s.replace("/", File.separator).replace("\\", File.separator))).exists();
        }
        
        @LuaMethod(name = "takeScreenshot", global = true)
        public static void takeScreenshot() {
            Core.getInstance().TakeFullScreenshot(null);
        }
        
        @LuaMethod(name = "takeScreenshot", global = true)
        public static void takeScreenshot(final String s) {
            Core.getInstance().TakeFullScreenshot(s);
        }
        
        @LuaMethod(name = "instanceItem", global = true)
        public static InventoryItem instanceItem(final Item item) {
            return InventoryItemFactory.CreateItem(item.moduleDotType);
        }
        
        @LuaMethod(name = "instanceItem", global = true)
        public static InventoryItem instanceItem(final String s) {
            return InventoryItemFactory.CreateItem(s);
        }
        
        @LuaMethod(name = "createNewScriptItem", global = true)
        public static Item createNewScriptItem(final String s, final String s2, final String displayName, final String s3, final String s4) {
            final Item value = new Item();
            value.module = ScriptManager.instance.getModule(s);
            value.module.ItemMap.put(s2, value);
            value.Icon = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s4);
            value.DisplayName = displayName;
            value.name = s2;
            value.moduleDotType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, value.module.name, s2);
            try {
                value.type = Item.Type.valueOf(s3);
            }
            catch (Exception ex) {}
            return value;
        }
        
        @LuaMethod(name = "cloneItemType", global = true)
        public static Item cloneItemType(final String key, final String s) {
            final Item findItem = ScriptManager.instance.FindItem(s);
            final Item value = new Item();
            value.module = findItem.getModule();
            value.module.ItemMap.put(key, value);
            return value;
        }
        
        @LuaMethod(name = "moduleDotType", global = true)
        public static String moduleDotType(final String s, final String s2) {
            return StringUtils.moduleDotType(s, s2);
        }
        
        @LuaMethod(name = "require", global = true)
        public static Object require(final String s) {
            String s2 = s;
            if (!s2.endsWith(".lua")) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2);
            }
            for (int i = 0; i < LuaManager.paths.size(); ++i) {
                final String absolutePath = ZomboidFileSystem.instance.getAbsolutePath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (String)LuaManager.paths.get(i), s2));
                if (absolutePath != null) {
                    return LuaManager.RunLua(ZomboidFileSystem.instance.getString(absolutePath));
                }
            }
            DebugLog.Lua.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return null;
        }
        
        @LuaMethod(name = "getRenderer", global = true)
        public static SpriteRenderer getRenderer() {
            return SpriteRenderer.instance;
        }
        
        @LuaMethod(name = "getGameTime", global = true)
        public static GameTime getGameTime() {
            return GameTime.instance;
        }
        
        @LuaMethod(name = "getStatistics", global = true)
        public static KahluaTable getStatistics() {
            return MPStatistics.getLuaStatistics();
        }
        
        @LuaMethod(name = "getPing", global = true)
        public static KahluaTable getPing() {
            return MPStatistics.getLuaPing();
        }
        
        @LuaMethod(name = "getWorld", global = true)
        public static IsoWorld getWorld() {
            return IsoWorld.instance;
        }
        
        @LuaMethod(name = "getCell", global = true)
        public static IsoCell getCell() {
            return IsoWorld.instance.getCell();
        }
        
        @LuaMethod(name = "getSandboxOptions", global = true)
        public static SandboxOptions getSandboxOptions() {
            return SandboxOptions.instance;
        }
        
        @LuaMethod(name = "getFileOutput", global = true)
        public static DataOutputStream getFileOutput(final String s) {
            if (s.contains("..")) {
                DebugLog.Lua.warn((Object)"relative paths not allowed");
                return null;
            }
            final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, s).replace("/", File.separator).replace("\\", File.separator);
            final File file = new File(replace.substring(0, replace.lastIndexOf(File.separator)).replace("\\", "/"));
            if (!file.exists()) {
                file.mkdirs();
            }
            final File file2 = new File(replace);
            try {
                GlobalObject.outStream = new FileOutputStream(file2);
            }
            catch (FileNotFoundException thrown) {
                Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
            }
            return new DataOutputStream(GlobalObject.outStream);
        }
        
        @LuaMethod(name = "getLastStandPlayersDirectory", global = true)
        public static String getLastStandPlayersDirectory() {
            return "LastStand";
        }
        
        @LuaMethod(name = "getLastStandPlayerFileNames", global = true)
        public static List<String> getLastStandPlayerFileNames() throws IOException {
            final ArrayList<String> list = new ArrayList<String>();
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, getLastStandPlayersDirectory()).replace("/", File.separator).replace("\\", File.separator));
            if (!file.exists()) {
                file.mkdir();
            }
            for (final File file2 : file.listFiles()) {
                if (!file2.isDirectory()) {
                    if (file2.getName().endsWith(".txt")) {
                        list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, getLastStandPlayersDirectory(), File.separator, file2.getName()));
                    }
                }
            }
            return list;
        }
        
        @Deprecated
        @LuaMethod(name = "getAllSavedPlayers", global = true)
        public static List<BufferedReader> getAllSavedPlayers() throws IOException {
            final ArrayList<BufferedReader> list = new ArrayList<BufferedReader>();
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, getLastStandPlayersDirectory()).replace("/", File.separator).replace("\\", File.separator));
            if (!file.exists()) {
                file.mkdir();
            }
            final File[] listFiles = file.listFiles();
            for (int length = listFiles.length, i = 0; i < length; ++i) {
                list.add(new BufferedReader(new FileReader(listFiles[i])));
            }
            return list;
        }
        
        @LuaMethod(name = "getSandboxPresets", global = true)
        public static List<String> getSandboxPresets() throws IOException {
            final ArrayList<Comparable> list = new ArrayList<Comparable>();
            final File file = new File(LuaManager.getSandboxCacheDir());
            if (!file.exists()) {
                file.mkdir();
            }
            for (final File file2 : file.listFiles()) {
                if (file2.getName().endsWith(".cfg")) {
                    list.add(file2.getName().replace(".cfg", ""));
                }
            }
            Collections.sort(list);
            return (List<String>)list;
        }
        
        @LuaMethod(name = "deleteSandboxPreset", global = true)
        public static void deleteSandboxPreset(final String s) {
            if (s.contains("..")) {
                DebugLog.Lua.warn((Object)"relative paths not allowed");
                return;
            }
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getSandboxCacheDir(), File.separator, s));
            if (file.exists()) {
                file.delete();
            }
        }
        
        @LuaMethod(name = "getFileReader", global = true)
        public static BufferedReader getFileReader(final String s, final boolean b) throws IOException {
            if (s.contains("..")) {
                DebugLog.Lua.warn((Object)"relative paths not allowed");
                return null;
            }
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, s).replace("/", File.separator).replace("\\", File.separator));
            if (!file.exists() && b) {
                file.createNewFile();
            }
            if (file.exists()) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
                return bufferedReader;
            }
            return null;
        }
        
        @LuaMethod(name = "getModFileReader", global = true)
        public static BufferedReader getModFileReader(final String s, final String pathname, final boolean b) throws IOException {
            if (pathname.isEmpty() || pathname.contains("..") || new File(pathname).isAbsolute()) {
                return null;
            }
            String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, pathname);
            if (s != null) {
                final ChooseGameInfo.Mod modDetails = ChooseGameInfo.getModDetails(s);
                if (modDetails == null) {
                    return null;
                }
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDetails.getDir(), File.separator, pathname);
            }
            final String replace = s2.replace("/", File.separator).replace("\\", File.separator);
            final File file = new File(replace);
            if (!file.exists() && b) {
                final File file2 = new File(replace.substring(0, replace.lastIndexOf(File.separator)));
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                file.createNewFile();
            }
            if (file.exists()) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
                return bufferedReader;
            }
            return null;
        }
        
        public static void refreshAnimSets(final boolean b) {
            try {
                if (b) {
                    AnimationSet.Reset();
                    final Iterator<Asset> iterator = AnimNodeAssetManager.instance.getAssetTable().values().iterator();
                    while (iterator.hasNext()) {
                        AnimNodeAssetManager.instance.reload(iterator.next());
                    }
                }
                AnimationSet.GetAnimationSet("player", true);
                AnimationSet.GetAnimationSet("player-vehicle", true);
                AnimationSet.GetAnimationSet("zombie", true);
                AnimationSet.GetAnimationSet("zombie-crawler", true);
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[i];
                    if (isoPlayer != null) {
                        isoPlayer.advancedAnimator.OnAnimDataChanged(b);
                    }
                }
                final Iterator<IsoZombie> iterator2 = IsoWorld.instance.CurrentCell.getZombieList().iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().advancedAnimator.OnAnimDataChanged(b);
                }
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        public static void reloadActionGroups() {
            try {
                ActionGroup.reloadAll();
            }
            catch (Exception ex) {}
        }
        
        @LuaMethod(name = "getModFileWriter", global = true)
        public static LuaFileWriter getModFileWriter(final String s, final String pathname, final boolean b, final boolean append) {
            if (pathname.isEmpty() || pathname.contains("..") || new File(pathname).isAbsolute()) {
                return null;
            }
            final ChooseGameInfo.Mod modDetails = ChooseGameInfo.getModDetails(s);
            if (modDetails == null) {
                return null;
            }
            final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, modDetails.getDir(), File.separator, pathname).replace("/", File.separator).replace("\\", File.separator);
            final File file = new File(replace.substring(0, replace.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            final File file2 = new File(replace);
            if (!file2.exists() && b) {
                try {
                    file2.createNewFile();
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
            }
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file2, append), StandardCharsets.UTF_8));
            }
            catch (IOException thrown2) {
                Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown2);
            }
            return new LuaFileWriter(printWriter);
        }
        
        @LuaMethod(name = "updateFire", global = true)
        public static void updateFire() {
            IsoFireManager.Update();
        }
        
        @LuaMethod(name = "deletePlayerSave", global = true)
        public static void deletePlayerSave(final String s) {
            new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, File.separator, s).replace("/", File.separator).replace("\\", File.separator)).delete();
        }
        
        @LuaMethod(name = "getControllerCount", global = true)
        public static int getControllerCount() {
            return GameWindow.GameInput.getControllerCount();
        }
        
        @LuaMethod(name = "isControllerConnected", global = true)
        public static boolean isControllerConnected(final int n) {
            return n >= 0 && n <= GameWindow.GameInput.getControllerCount() && GameWindow.GameInput.getController(n) != null;
        }
        
        @LuaMethod(name = "getControllerGUID", global = true)
        public static String getControllerGUID(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return "???";
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller != null) {
                return controller.getGUID();
            }
            return "???";
        }
        
        @LuaMethod(name = "getControllerName", global = true)
        public static String getControllerName(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return "???";
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller != null) {
                return controller.getGamepadName();
            }
            return "???";
        }
        
        @LuaMethod(name = "getControllerAxisCount", global = true)
        public static int getControllerAxisCount(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return 0;
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller == null) {
                return 0;
            }
            return controller.getAxisCount();
        }
        
        @LuaMethod(name = "getControllerAxisValue", global = true)
        public static float getControllerAxisValue(final int n, final int n2) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return 0.0f;
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller == null) {
                return 0.0f;
            }
            if (n2 < 0 || n2 >= controller.getAxisCount()) {
                return 0.0f;
            }
            return controller.getAxisValue(n2);
        }
        
        @LuaMethod(name = "getControllerDeadZone", global = true)
        public static float getControllerDeadZone(final int n, final int n2) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return 0.0f;
            }
            if (n2 < 0 || n2 >= GameWindow.GameInput.getAxisCount(n)) {
                return 0.0f;
            }
            return JoypadManager.instance.getDeadZone(n, n2);
        }
        
        @LuaMethod(name = "setControllerDeadZone", global = true)
        public static void setControllerDeadZone(final int n, final int n2, final float n3) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return;
            }
            if (n2 < 0 || n2 >= GameWindow.GameInput.getAxisCount(n)) {
                return;
            }
            JoypadManager.instance.setDeadZone(n, n2, n3);
        }
        
        @LuaMethod(name = "saveControllerSettings", global = true)
        public static void saveControllerSettings(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return;
            }
            JoypadManager.instance.saveControllerSettings(n);
        }
        
        @LuaMethod(name = "getControllerButtonCount", global = true)
        public static int getControllerButtonCount(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return 0;
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller == null) {
                return 0;
            }
            return controller.getButtonCount();
        }
        
        @LuaMethod(name = "getControllerPovX", global = true)
        public static float getControllerPovX(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return 0.0f;
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller == null) {
                return 0.0f;
            }
            return controller.getPovX();
        }
        
        @LuaMethod(name = "getControllerPovY", global = true)
        public static float getControllerPovY(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return 0.0f;
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller == null) {
                return 0.0f;
            }
            return controller.getPovY();
        }
        
        @LuaMethod(name = "reloadControllerConfigFiles", global = true)
        public static void reloadControllerConfigFiles() {
            JoypadManager.instance.reloadControllerFiles();
        }
        
        @LuaMethod(name = "isJoypadPressed", global = true)
        public static boolean isJoypadPressed(final int n, final int n2) {
            return GameWindow.GameInput.isButtonPressedD(n2, n);
        }
        
        @LuaMethod(name = "isJoypadDown", global = true)
        public static boolean isJoypadDown(final int n) {
            return JoypadManager.instance.isDownPressed(n);
        }
        
        @LuaMethod(name = "isJoypadLTPressed", global = true)
        public static boolean isJoypadLTPressed(final int n) {
            return JoypadManager.instance.isLTPressed(n);
        }
        
        @LuaMethod(name = "isJoypadRTPressed", global = true)
        public static boolean isJoypadRTPressed(final int n) {
            return JoypadManager.instance.isRTPressed(n);
        }
        
        @LuaMethod(name = "getJoypadAimingAxisX", global = true)
        public static float getJoypadAimingAxisX(final int n) {
            return JoypadManager.instance.getAimingAxisX(n);
        }
        
        @LuaMethod(name = "getJoypadAimingAxisY", global = true)
        public static float getJoypadAimingAxisY(final int n) {
            return JoypadManager.instance.getAimingAxisY(n);
        }
        
        @LuaMethod(name = "getJoypadMovementAxisX", global = true)
        public static float getJoypadMovementAxisX(final int n) {
            return JoypadManager.instance.getMovementAxisX(n);
        }
        
        @LuaMethod(name = "getJoypadMovementAxisY", global = true)
        public static float getJoypadMovementAxisY(final int n) {
            return JoypadManager.instance.getMovementAxisY(n);
        }
        
        @LuaMethod(name = "getJoypadAButton", global = true)
        public static int getJoypadAButton(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getAButton() : -1;
        }
        
        @LuaMethod(name = "getJoypadBButton", global = true)
        public static int getJoypadBButton(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getBButton() : -1;
        }
        
        @LuaMethod(name = "getJoypadXButton", global = true)
        public static int getJoypadXButton(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getXButton() : -1;
        }
        
        @LuaMethod(name = "getJoypadYButton", global = true)
        public static int getJoypadYButton(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getYButton() : -1;
        }
        
        @LuaMethod(name = "getJoypadLBumper", global = true)
        public static int getJoypadLBumper(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getLBumper() : -1;
        }
        
        @LuaMethod(name = "getJoypadRBumper", global = true)
        public static int getJoypadRBumper(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getRBumper() : -1;
        }
        
        @LuaMethod(name = "getJoypadBackButton", global = true)
        public static int getJoypadBackButton(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getBackButton() : -1;
        }
        
        @LuaMethod(name = "getJoypadStartButton", global = true)
        public static int getJoypadStartButton(final int n) {
            final JoypadManager.Joypad fromControllerID = JoypadManager.instance.getFromControllerID(n);
            return (fromControllerID != null) ? fromControllerID.getStartButton() : -1;
        }
        
        @LuaMethod(name = "wasMouseActiveMoreRecentlyThanJoypad", global = true)
        public static boolean wasMouseActiveMoreRecentlyThanJoypad() {
            if (IsoPlayer.players[0] == null) {
                return true;
            }
            final int joypadBind = IsoPlayer.players[0].getJoypadBind();
            return joypadBind == -1 || JoypadManager.instance.getLastActivity(joypadBind) < Mouse.lastActivity;
        }
        
        @LuaMethod(name = "reactivateJoypadAfterResetLua", global = true)
        public static boolean reactivateJoypadAfterResetLua() {
            if (GameWindow.ActivatedJoyPad != null) {
                LuaEventManager.triggerEvent("OnJoypadActivateUI", GameWindow.ActivatedJoyPad.getID());
                return true;
            }
            return false;
        }
        
        @LuaMethod(name = "isJoypadConnected", global = true)
        public static boolean isJoypadConnected(final int n) {
            return JoypadManager.instance.isJoypadConnected(n);
        }
        
        private static void addPlayerToWorld(final int playerIndex, final IsoPlayer instance, final boolean b) {
            if (IsoPlayer.players[playerIndex] != null) {
                IsoPlayer.players[playerIndex].getEmitter().stopAll();
                IsoPlayer.players[playerIndex].getEmitter().unregister();
                IsoPlayer.players[playerIndex].updateUsername();
                IsoPlayer.players[playerIndex].setSceneCulled(true);
                IsoPlayer.players[playerIndex] = null;
            }
            instance.PlayerIndex = playerIndex;
            if (GameClient.bClient && playerIndex != 0 && instance.serverPlayerIndex == 1) {
                instance.serverPlayerIndex = ClientPlayerDB.getInstance().getNextServerPlayerIndex();
            }
            if (playerIndex == 0) {
                instance.sqlID = 1;
            }
            if (b) {
                instance.applyTraits(IsoWorld.instance.getLuaTraits());
                instance.createKeyRing();
                final ProfessionFactory.Profession profession = ProfessionFactory.getProfession(instance.getDescriptor().getProfession());
                if (profession != null && !profession.getFreeRecipes().isEmpty()) {
                    final Iterator<String> iterator = profession.getFreeRecipes().iterator();
                    while (iterator.hasNext()) {
                        instance.getKnownRecipes().add(iterator.next());
                    }
                }
                final Iterator<String> iterator2 = IsoWorld.instance.getLuaTraits().iterator();
                while (iterator2.hasNext()) {
                    final TraitFactory.Trait trait = TraitFactory.getTrait(iterator2.next());
                    if (trait != null && !trait.getFreeRecipes().isEmpty()) {
                        final Iterator<String> iterator3 = trait.getFreeRecipes().iterator();
                        while (iterator3.hasNext()) {
                            instance.getKnownRecipes().add(iterator3.next());
                        }
                    }
                }
                instance.setDir(IsoDirections.SE);
                LuaEventManager.triggerEvent("OnNewGame", instance, instance.getCurrentSquare());
            }
            IsoPlayer.numPlayers = Math.max(IsoPlayer.numPlayers, playerIndex + 1);
            IsoWorld.instance.AddCoopPlayers.add(new AddCoopPlayer(instance));
            if (playerIndex == 0) {
                IsoPlayer.setInstance(instance);
            }
        }
        
        @LuaMethod(name = "toInt", global = true)
        public static int toInt(final double n) {
            return (int)n;
        }
        
        @LuaMethod(name = "getClientUsername", global = true)
        public static String getClientUsername() {
            return GameClient.bClient ? GameClient.username : null;
        }
        
        @LuaMethod(name = "setPlayerJoypad", global = true)
        public static void setPlayerJoypad(final int n, final int joypadBind, IsoPlayer isoPlayer, final String username) {
            if (IsoPlayer.players[n] == null || IsoPlayer.players[n].isDead()) {
                final boolean b = isoPlayer == null;
                if (isoPlayer == null) {
                    final IsoPlayer instance = IsoPlayer.getInstance();
                    final IsoWorld instance2 = IsoWorld.instance;
                    final int n2 = instance2.getLuaPosX() + 300 * instance2.getLuaSpawnCellX();
                    final int n3 = instance2.getLuaPosY() + 300 * instance2.getLuaSpawnCellY();
                    final int luaPosZ = instance2.getLuaPosZ();
                    DebugLog.Lua.debugln(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, n2, n3, luaPosZ));
                    isoPlayer = new IsoPlayer(instance2.CurrentCell, instance2.getLuaPlayerDesc(), n2, n3, luaPosZ);
                    IsoPlayer.setInstance(instance);
                    instance2.CurrentCell.getAddList().remove(isoPlayer);
                    instance2.CurrentCell.getObjectList().remove(isoPlayer);
                    isoPlayer.SaveFileName = IsoPlayer.getUniqueFileName();
                }
                if (GameClient.bClient) {
                    if (username != null) {
                        assert n != 0;
                        isoPlayer.username = username;
                        isoPlayer.getModData().rawset((Object)"username", (Object)username);
                    }
                    else {
                        assert n == 0;
                        isoPlayer.username = GameClient.username;
                    }
                }
                addPlayerToWorld(n, isoPlayer, b);
            }
            isoPlayer.JoypadBind = joypadBind;
            JoypadManager.instance.assignJoypad(joypadBind, n);
        }
        
        @LuaMethod(name = "setPlayerMouse", global = true)
        public static void setPlayerMouse(IsoPlayer isoPlayer) {
            final int n = 0;
            final boolean b = isoPlayer == null;
            if (isoPlayer == null) {
                final IsoPlayer instance = IsoPlayer.getInstance();
                final IsoWorld instance2 = IsoWorld.instance;
                final int n2 = instance2.getLuaPosX() + 300 * instance2.getLuaSpawnCellX();
                final int n3 = instance2.getLuaPosY() + 300 * instance2.getLuaSpawnCellY();
                final int luaPosZ = instance2.getLuaPosZ();
                DebugLog.Lua.debugln(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, n2, n3, luaPosZ));
                isoPlayer = new IsoPlayer(instance2.CurrentCell, instance2.getLuaPlayerDesc(), n2, n3, luaPosZ);
                IsoPlayer.setInstance(instance);
                instance2.CurrentCell.getAddList().remove(isoPlayer);
                instance2.CurrentCell.getObjectList().remove(isoPlayer);
                isoPlayer.SaveFileName = null;
            }
            if (GameClient.bClient) {
                isoPlayer.username = GameClient.username;
            }
            addPlayerToWorld(n, isoPlayer, b);
        }
        
        @LuaMethod(name = "revertToKeyboardAndMouse", global = true)
        public static void revertToKeyboardAndMouse() {
            JoypadManager.instance.revertToKeyboardAndMouse();
        }
        
        @LuaMethod(name = "isJoypadUp", global = true)
        public static boolean isJoypadUp(final int n) {
            return JoypadManager.instance.isUpPressed(n);
        }
        
        @LuaMethod(name = "isJoypadLeft", global = true)
        public static boolean isJoypadLeft(final int n) {
            return JoypadManager.instance.isLeftPressed(n);
        }
        
        @LuaMethod(name = "isJoypadRight", global = true)
        public static boolean isJoypadRight(final int n) {
            return JoypadManager.instance.isRightPressed(n);
        }
        
        @LuaMethod(name = "isJoypadLBPressed", global = true)
        public static boolean isJoypadLBPressed(final int n) {
            return JoypadManager.instance.isLBPressed(n);
        }
        
        @LuaMethod(name = "isJoypadRBPressed", global = true)
        public static boolean isJoypadRBPressed(final int n) {
            return JoypadManager.instance.isRBPressed(n);
        }
        
        @LuaMethod(name = "getButtonCount", global = true)
        public static int getButtonCount(final int n) {
            if (n < 0 || n >= GameWindow.GameInput.getControllerCount()) {
                return 0;
            }
            final Controller controller = GameWindow.GameInput.getController(n);
            if (controller == null) {
                return 0;
            }
            return controller.getButtonCount();
        }
        
        @LuaMethod(name = "setDebugToggleControllerPluggedIn", global = true)
        public static void setDebugToggleControllerPluggedIn(final int debugToggleControllerPluggedIn) {
            Controllers.setDebugToggleControllerPluggedIn(debugToggleControllerPluggedIn);
        }
        
        @LuaMethod(name = "getFileWriter", global = true)
        public static LuaFileWriter getFileWriter(final String s, final boolean b, final boolean append) {
            if (s.contains("..")) {
                DebugLog.Lua.warn((Object)"relative paths not allowed");
                return null;
            }
            final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, s).replace("/", File.separator).replace("\\", File.separator);
            final File file = new File(replace.substring(0, replace.lastIndexOf(File.separator)).replace("\\", "/"));
            if (!file.exists()) {
                file.mkdirs();
            }
            final File file2 = new File(replace);
            if (!file2.exists() && b) {
                try {
                    file2.createNewFile();
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
            }
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file2, append), StandardCharsets.UTF_8));
            }
            catch (IOException thrown2) {
                Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown2);
            }
            return new LuaFileWriter(printWriter);
        }
        
        @LuaMethod(name = "getSandboxFileWriter", global = true)
        public static LuaFileWriter getSandboxFileWriter(final String s, final boolean b, final boolean append) {
            final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getSandboxCacheDir(), File.separator, s).replace("/", File.separator).replace("\\", File.separator);
            final File file = new File(replace.substring(0, replace.lastIndexOf(File.separator)).replace("\\", "/"));
            if (!file.exists()) {
                file.mkdirs();
            }
            final File file2 = new File(replace);
            if (!file2.exists() && b) {
                try {
                    file2.createNewFile();
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
            }
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file2, append), StandardCharsets.UTF_8));
            }
            catch (IOException thrown2) {
                Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown2);
            }
            return new LuaFileWriter(printWriter);
        }
        
        @LuaMethod(name = "createStory", global = true)
        public static void createStory(final String gameMode) {
            Core.GameMode = gameMode;
            final String replace = ZomboidFileSystem.instance.getGameModeCacheDir().replace("/", File.separator).replace("\\", File.separator);
            int n = 1;
            int i = 0;
            while (i == 0) {
                if (!new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, replace, File.separator, n)).exists()) {
                    i = 1;
                }
                else {
                    ++n;
                }
            }
            Core.GameSaveWorld = "newstory";
        }
        
        @LuaMethod(name = "createWorld", global = true)
        public static void createWorld(String sanitizeWorldName) {
            if (sanitizeWorldName == null || sanitizeWorldName.isEmpty()) {
                sanitizeWorldName = "blah";
            }
            sanitizeWorldName = sanitizeWorldName(sanitizeWorldName);
            final String replace = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, sanitizeWorldName, File.separator).replace("/", File.separator).replace("\\", File.separator);
            final File file = new File(replace.substring(0, replace.lastIndexOf(File.separator)).replace("\\", "/"));
            if (!file.exists() && !Core.getInstance().isNoSave()) {
                file.mkdirs();
            }
            Core.GameSaveWorld = sanitizeWorldName;
        }
        
        @LuaMethod(name = "sanitizeWorldName", global = true)
        public static String sanitizeWorldName(final String s) {
            return s.replace(" ", "_").replace("/", "").replace("\\", "").replace("?", "").replace("*", "").replace("<", "").replace(">", "").replace(":", "").replace("|", "").trim();
        }
        
        @LuaMethod(name = "forceChangeState", global = true)
        public static void forceChangeState(final GameState gameState) {
            GameWindow.states.forceNextState(gameState);
        }
        
        @LuaMethod(name = "endFileOutput", global = true)
        public static void endFileOutput() {
            if (GlobalObject.outStream != null) {
                try {
                    GlobalObject.outStream.close();
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
            }
            GlobalObject.outStream = null;
        }
        
        @LuaMethod(name = "getFileInput", global = true)
        public static DataInputStream getFileInput(final String s) throws IOException {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getLuaCacheDir(), File.separator, s).replace("/", File.separator).replace("\\", File.separator));
            if (file.exists()) {
                try {
                    GlobalObject.inStream = new FileInputStream(file);
                }
                catch (FileNotFoundException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
                return new DataInputStream(GlobalObject.inStream);
            }
            return null;
        }
        
        @LuaMethod(name = "getGameFilesInput", global = true)
        public static DataInputStream getGameFilesInput(final String s) {
            final File file = new File(ZomboidFileSystem.instance.getString(s.replace("/", File.separator).replace("\\", File.separator)));
            if (file.exists()) {
                try {
                    GlobalObject.inStream = new FileInputStream(file);
                }
                catch (FileNotFoundException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
                return new DataInputStream(GlobalObject.inStream);
            }
            return null;
        }
        
        @LuaMethod(name = "getGameFilesTextInput", global = true)
        public static BufferedReader getGameFilesTextInput(final String fileName) {
            if (!Core.getInstance().getDebug()) {
                return null;
            }
            if (new File(fileName.replace("/", File.separator).replace("\\", File.separator)).exists()) {
                try {
                    GlobalObject.inFileReader = new FileReader(fileName);
                    return GlobalObject.inBufferedReader = new BufferedReader(GlobalObject.inFileReader);
                }
                catch (FileNotFoundException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
            }
            return null;
        }
        
        @LuaMethod(name = "endTextFileInput", global = true)
        public static void endTextFileInput() {
            if (GlobalObject.inBufferedReader != null) {
                try {
                    GlobalObject.inBufferedReader.close();
                    GlobalObject.inFileReader.close();
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
            }
            GlobalObject.inBufferedReader = null;
            GlobalObject.inFileReader = null;
        }
        
        @LuaMethod(name = "endFileInput", global = true)
        public static void endFileInput() {
            if (GlobalObject.inStream != null) {
                try {
                    GlobalObject.inStream.close();
                }
                catch (IOException thrown) {
                    Logger.getLogger(LuaManager.class.getName()).log(Level.SEVERE, null, thrown);
                }
            }
            GlobalObject.inStream = null;
        }
        
        @LuaMethod(name = "getLineNumber", global = true)
        public static int getLineNumber(final LuaCallFrame luaCallFrame) {
            if (luaCallFrame.closure == null) {
                return 0;
            }
            int pc = luaCallFrame.pc;
            if (pc < 0) {
                pc = 0;
            }
            if (pc >= luaCallFrame.closure.prototype.lines.length) {
                pc = luaCallFrame.closure.prototype.lines.length - 1;
            }
            return luaCallFrame.closure.prototype.lines[pc];
        }
        
        @LuaMethod(name = "ZombRand", global = true)
        public static double ZombRand(final double n) {
            if (n == 0.0) {
                return 0.0;
            }
            if (n < 0.0) {
                return (double)(-Rand.Next(-(long)n, Rand.randlua));
            }
            return (double)Rand.Next((long)n, Rand.randlua);
        }
        
        @LuaMethod(name = "ZombRandBetween", global = true)
        public static double ZombRandBetween(final double n, final double n2) {
            return (double)Rand.Next((long)n, (long)n2, Rand.randlua);
        }
        
        @LuaMethod(name = "ZombRand", global = true)
        public static double ZombRand(final double n, final double n2) {
            return Rand.Next((int)n, (int)n2, Rand.randlua);
        }
        
        @LuaMethod(name = "ZombRandFloat", global = true)
        public static float ZombRandFloat(final float n, final float n2) {
            return Rand.Next(n, n2, Rand.randlua);
        }
        
        @LuaMethod(name = "getShortenedFilename", global = true)
        public static String getShortenedFilename(final String s) {
            return s.substring(s.indexOf("lua/") + 4);
        }
        
        @LuaMethod(name = "isKeyDown", global = true)
        public static boolean isKeyDown(final int n) {
            return GameKeyboard.isKeyDown(n);
        }
        
        @LuaMethod(name = "wasKeyDown", global = true)
        public static boolean wasKeyDown(final int n) {
            return GameKeyboard.wasKeyDown(n);
        }
        
        @LuaMethod(name = "isKeyPressed", global = true)
        public static boolean isKeyPressed(final int n) {
            return GameKeyboard.isKeyPressed(n);
        }
        
        @LuaMethod(name = "getFMODSoundBank", global = true)
        public static BaseSoundBank getFMODSoundBank() {
            return BaseSoundBank.instance;
        }
        
        @LuaMethod(name = "isSoundPlaying", global = true)
        public static boolean isSoundPlaying(final Object o) {
            return o instanceof Double && FMODManager.instance.isPlaying(((Double)o).longValue());
        }
        
        @LuaMethod(name = "stopSound", global = true)
        public static void stopSound(final long n) {
            FMODManager.instance.stopSound(n);
        }
        
        @LuaMethod(name = "isShiftKeyDown", global = true)
        public static boolean isShiftKeyDown() {
            return GameKeyboard.isKeyDown(42) || GameKeyboard.isKeyDown(54);
        }
        
        @LuaMethod(name = "isCtrlKeyDown", global = true)
        public static boolean isCtrlKeyDown() {
            return GameKeyboard.isKeyDown(29) || GameKeyboard.isKeyDown(157);
        }
        
        @LuaMethod(name = "isAltKeyDown", global = true)
        public static boolean isAltKeyDown() {
            return GameKeyboard.isKeyDown(56) || GameKeyboard.isKeyDown(184);
        }
        
        @LuaMethod(name = "getCore", global = true)
        public static Core getCore() {
            return Core.getInstance();
        }
        
        @LuaMethod(name = "getSquare", global = true)
        public static IsoGridSquare getSquare(final double n, final double n2, final double n3) {
            return IsoCell.getInstance().getGridSquare(n, n2, n3);
        }
        
        @LuaMethod(name = "getDebugOptions", global = true)
        public static DebugOptions getDebugOptions() {
            return DebugOptions.instance;
        }
        
        @LuaMethod(name = "setShowPausedMessage", global = true)
        public static void setShowPausedMessage(final boolean showPausedMessage) {
            DebugLog.log("EXITDEBUG: setShowPausedMessage 1");
            UIManager.setShowPausedMessage(showPausedMessage);
            DebugLog.log("EXITDEBUG: setShowPausedMessage 2");
        }
        
        @LuaMethod(name = "getFilenameOfCallframe", global = true)
        public static String getFilenameOfCallframe(final LuaCallFrame luaCallFrame) {
            if (luaCallFrame.closure == null) {
                return null;
            }
            return luaCallFrame.closure.prototype.filename;
        }
        
        @LuaMethod(name = "getFilenameOfClosure", global = true)
        public static String getFilenameOfClosure(final LuaClosure luaClosure) {
            if (luaClosure == null) {
                return null;
            }
            return luaClosure.prototype.filename;
        }
        
        @LuaMethod(name = "getFirstLineOfClosure", global = true)
        public static int getFirstLineOfClosure(final LuaClosure luaClosure) {
            if (luaClosure == null) {
                return 0;
            }
            return luaClosure.prototype.lines[0];
        }
        
        @LuaMethod(name = "getLocalVarCount", global = true)
        public static int getLocalVarCount(final Coroutine coroutine) {
            final LuaCallFrame currentCallFrame = coroutine.currentCallFrame();
            if (currentCallFrame == null) {
                return 0;
            }
            return currentCallFrame.LocalVarNames.size();
        }
        
        @LuaMethod(name = "isSystemLinux", global = true)
        public static boolean isSystemLinux() {
            return !isSystemMacOS() && !isSystemWindows();
        }
        
        @LuaMethod(name = "isSystemMacOS", global = true)
        public static boolean isSystemMacOS() {
            return System.getProperty("os.name").contains("OS X");
        }
        
        @LuaMethod(name = "isSystemWindows", global = true)
        public static boolean isSystemWindows() {
            return System.getProperty("os.name").startsWith("Win");
        }
        
        @LuaMethod(name = "isModActive", global = true)
        public static boolean isModActive(final ChooseGameInfo.Mod mod) {
            String o = mod.getDir();
            if (!StringUtils.isNullOrWhitespace(mod.getId())) {
                o = mod.getId();
            }
            return ZomboidFileSystem.instance.getModIDs().contains(o);
        }
        
        @LuaMethod(name = "openUrl", global = true)
        public static void openURl(final String str) {
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(str));
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
        
        @LuaMethod(name = "isDesktopOpenSupported", global = true)
        public static boolean isDesktopOpenSupported() {
            return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN);
        }
        
        @LuaMethod(name = "showFolderInDesktop", global = true)
        public static void showFolderInDesktop(final String pathname) {
            final File file = new File(pathname);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    desktop.open(file);
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
        
        @LuaMethod(name = "getActivatedMods", global = true)
        public static ArrayList<String> getActivatedMods() {
            return ZomboidFileSystem.instance.getModIDs();
        }
        
        @LuaMethod(name = "toggleModActive", global = true)
        public static void toggleModActive(final ChooseGameInfo.Mod mod, final boolean b) {
            String s = mod.getDir();
            if (!StringUtils.isNullOrWhitespace(mod.getId())) {
                s = mod.getId();
            }
            ActiveMods.getById("default").setModActive(s, b);
        }
        
        @LuaMethod(name = "saveModsFile", global = true)
        public static void saveModsFile() {
            ZomboidFileSystem.instance.saveModsFile();
        }
        
        private static void deleteSavefileFilesMatching(final File file, final String regex) {
            final DirectoryStream.Filter<? super Path> filter = path -> path.getFileName().toString().matches(regex);
            try {
                final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(file.toPath(), filter);
                try {
                    for (final Path path2 : directoryStream) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/nio/file/Path;)Ljava/lang/String;, path2));
                        Files.deleteIfExists(path2);
                    }
                    if (directoryStream != null) {
                        directoryStream.close();
                    }
                }
                catch (Throwable t) {
                    if (directoryStream != null) {
                        try {
                            directoryStream.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        @LuaMethod(name = "manipulateSavefile", global = true)
        public static void manipulateSavefile(final String s, final String s2) {
            if (StringUtils.isNullOrWhitespace(s)) {
                return;
            }
            if (s.contains("..")) {
                return;
            }
            final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s);
            final File file = new File(pathname);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }
            switch (s2) {
                case "DeleteChunkDataXYBin": {
                    deleteSavefileFilesMatching(file, "chunkdata_[0-9]+_[0-9]+\\.bin");
                    break;
                }
                case "DeleteMapXYBin": {
                    deleteSavefileFilesMatching(file, "map_[0-9]+_[0-9]+\\.bin");
                    break;
                }
                case "DeleteMapMetaBin": {
                    deleteSavefileFilesMatching(file, "map_meta\\.bin");
                    break;
                }
                case "DeleteMapTBin": {
                    deleteSavefileFilesMatching(file, "map_t\\.bin");
                    break;
                }
                case "DeleteMapZoneBin": {
                    deleteSavefileFilesMatching(file, "map_zone\\.bin");
                    break;
                }
                case "DeletePlayersDB": {
                    deleteSavefileFilesMatching(file, "players\\.db");
                    break;
                }
                case "DeleteReanimatedBin": {
                    deleteSavefileFilesMatching(file, "reanimated\\.bin");
                    break;
                }
                case "DeleteVehiclesDB": {
                    deleteSavefileFilesMatching(file, "vehicles\\.db");
                    break;
                }
                case "DeleteZOutfitsBin": {
                    deleteSavefileFilesMatching(file, "z_outfits\\.bin");
                    break;
                }
                case "DeleteZPopVirtualBin": {
                    deleteSavefileFilesMatching(file, "zpop_virtual\\.bin");
                    break;
                }
                case "DeleteZPopXYBin": {
                    deleteSavefileFilesMatching(file, "zpop_[0-9]+_[0-9]+\\.bin");
                    break;
                }
                case "WriteModsDotTxt": {
                    new ActiveModsFile().write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pathname, File.separator), ActiveMods.getById("currentGame"));
                    break;
                }
                default: {
                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s2));
                }
            }
        }
        
        @LuaMethod(name = "getLocalVarName", global = true)
        public static String getLocalVarName(final Coroutine coroutine, final int index) {
            return coroutine.currentCallFrame().LocalVarNames.get(index);
        }
        
        @LuaMethod(name = "getLocalVarStack", global = true)
        public static int getLocalVarStack(final Coroutine coroutine, final int index) {
            final LuaCallFrame currentCallFrame = coroutine.currentCallFrame();
            return (int)currentCallFrame.LocalVarToStackMap.get(currentCallFrame.LocalVarNames.get(index));
        }
        
        @LuaMethod(name = "getCallframeTop", global = true)
        public static int getCallframeTop(final Coroutine coroutine) {
            return coroutine.getCallframeTop();
        }
        
        @LuaMethod(name = "getCoroutineTop", global = true)
        public static int getCoroutineTop(final Coroutine coroutine) {
            return coroutine.getTop();
        }
        
        @LuaMethod(name = "getCoroutineObjStack", global = true)
        public static Object getCoroutineObjStack(final Coroutine coroutine, final int n) {
            return coroutine.getObjectFromStack(n);
        }
        
        @LuaMethod(name = "getCoroutineObjStackWithBase", global = true)
        public static Object getCoroutineObjStackWithBase(final Coroutine coroutine, final int n) {
            return coroutine.getObjectFromStack(n - coroutine.currentCallFrame().localBase);
        }
        
        @LuaMethod(name = "localVarName", global = true)
        public static String localVarName(final Coroutine coroutine, final int n) {
            if (coroutine.getCallframeTop() - 1 < 0) {}
            return "";
        }
        
        @LuaMethod(name = "getCoroutineCallframeStack", global = true)
        public static LuaCallFrame getCoroutineCallframeStack(final Coroutine coroutine, final int n) {
            return coroutine.getCallFrame(n);
        }
        
        @LuaMethod(name = "createTile", global = true)
        public static void createTile(final String key, final IsoGridSquare isoGridSquare) {
            synchronized (IsoSpriteManager.instance.NamedMap) {
                final IsoSprite isoSprite = IsoSpriteManager.instance.NamedMap.get(key);
                if (isoSprite == null) {
                    return;
                }
                int x = 0;
                int y = 0;
                int z = 0;
                if (isoGridSquare != null) {
                    x = isoGridSquare.getX();
                    y = isoGridSquare.getY();
                    z = isoGridSquare.getZ();
                }
                CellLoader.DoTileObjectCreation(isoSprite, isoSprite.getType(), isoGridSquare, IsoWorld.instance.CurrentCell, x, y, z, key);
            }
        }
        
        @LuaMethod(name = "getNumClassFunctions", global = true)
        public static int getNumClassFunctions(final Object o) {
            return o.getClass().getDeclaredMethods().length;
        }
        
        @LuaMethod(name = "getClassFunction", global = true)
        public static Method getClassFunction(final Object o, final int n) {
            return o.getClass().getDeclaredMethods()[n];
        }
        
        @LuaMethod(name = "getNumClassFields", global = true)
        public static int getNumClassFields(final Object o) {
            return o.getClass().getDeclaredFields().length;
        }
        
        @LuaMethod(name = "getClassField", global = true)
        public static Field getClassField(final Object o, final int n) {
            final Field field = o.getClass().getDeclaredFields()[n];
            field.setAccessible(true);
            return field;
        }
        
        @LuaMethod(name = "getDirectionTo", global = true)
        public static IsoDirections getDirectionTo(final IsoGameCharacter isoGameCharacter, final IsoObject isoObject) {
            final Vector2 vector3;
            final Vector2 vector2 = vector3 = new Vector2(isoObject.getX(), isoObject.getY());
            vector3.x -= isoGameCharacter.x;
            final Vector2 vector4 = vector2;
            vector4.y -= isoGameCharacter.y;
            return IsoDirections.fromAngle(vector2);
        }
        
        @LuaMethod(name = "translatePointXInOverheadMapToWindow", global = true)
        public static float translatePointXInOverheadMapToWindow(final float n, final UIElement uiElement, final float n2, final float n3) {
            IngameState.draww = (float)uiElement.getWidth().intValue();
            return IngameState.translatePointX(n, n3, n2, 0.0f);
        }
        
        @LuaMethod(name = "translatePointYInOverheadMapToWindow", global = true)
        public static float translatePointYInOverheadMapToWindow(final float n, final UIElement uiElement, final float n2, final float n3) {
            IngameState.drawh = (float)uiElement.getHeight().intValue();
            return IngameState.translatePointY(n, n3, n2, 0.0f);
        }
        
        @LuaMethod(name = "translatePointXInOverheadMapToWorld", global = true)
        public static float translatePointXInOverheadMapToWorld(final float n, final UIElement uiElement, final float n2, final float n3) {
            IngameState.draww = (float)uiElement.getWidth().intValue();
            return IngameState.invTranslatePointX(n, n3, n2, 0.0f);
        }
        
        @LuaMethod(name = "translatePointYInOverheadMapToWorld", global = true)
        public static float translatePointYInOverheadMapToWorld(final float n, final UIElement uiElement, final float n2, final float n3) {
            IngameState.drawh = (float)uiElement.getHeight().intValue();
            return IngameState.invTranslatePointY(n, n3, n2, 0.0f);
        }
        
        @LuaMethod(name = "drawOverheadMap", global = true)
        public static void drawOverheadMap(final UIElement uiElement, final float n, final float n2, final float n3) {
            IngameState.renderDebugOverhead2(getCell(), 0, n, uiElement.getAbsoluteX().intValue(), uiElement.getAbsoluteY().intValue(), n2, n3, uiElement.getWidth().intValue(), uiElement.getHeight().intValue());
        }
        
        @LuaMethod(name = "assaultPlayer", global = true)
        public static void assaultPlayer() {
            assert false;
        }
        
        @LuaMethod(name = "isoRegionsRenderer", global = true)
        public static IsoRegionsRenderer isoRegionsRenderer() {
            return new IsoRegionsRenderer();
        }
        
        @LuaMethod(name = "zpopNewRenderer", global = true)
        public static ZombiePopulationRenderer zpopNewRenderer() {
            return new ZombiePopulationRenderer();
        }
        
        @LuaMethod(name = "zpopSpawnTimeToZero", global = true)
        public static void zpopSpawnTimeToZero(final int n, final int n2) {
            ZombiePopulationManager.instance.dbgSpawnTimeToZero(n, n2);
        }
        
        @LuaMethod(name = "zpopClearZombies", global = true)
        public static void zpopClearZombies(final int n, final int n2) {
            ZombiePopulationManager.instance.dbgClearZombies(n, n2);
        }
        
        @LuaMethod(name = "zpopSpawnNow", global = true)
        public static void zpopSpawnNow(final int n, final int n2) {
            ZombiePopulationManager.instance.dbgSpawnNow(n, n2);
        }
        
        @LuaMethod(name = "addVirtualZombie", global = true)
        public static void addVirtualZombie(final int n, final int n2) {
        }
        
        @LuaMethod(name = "luaDebug", global = true)
        public static void luaDebug() {
            try {
                throw new Exception("LuaDebug");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        @LuaMethod(name = "setAggroTarget", global = true)
        public static void setAggroTarget(final int n, final int n2, final int n3) {
            ZombiePopulationManager.instance.setAggroTarget(n, n2, n3);
        }
        
        @LuaMethod(name = "debugFullyStreamedIn", global = true)
        public static void debugFullyStreamedIn(final int n, final int n2) {
            IngameState.instance.debugFullyStreamedIn(n, n2);
        }
        
        @LuaMethod(name = "getClassFieldVal", global = true)
        public static Object getClassFieldVal(final Object obj, final Field field) {
            try {
                return field.get(obj);
            }
            catch (Exception ex) {
                return "<private>";
            }
        }
        
        @LuaMethod(name = "getMethodParameter", global = true)
        public static String getMethodParameter(final Method method, final int n) {
            return method.getParameterTypes()[n].getSimpleName();
        }
        
        @LuaMethod(name = "getMethodParameterCount", global = true)
        public static int getMethodParameterCount(final Method method) {
            return method.getParameterTypes().length;
        }
        
        @LuaMethod(name = "breakpoint", global = true)
        public static void breakpoint() {
        }
        
        @LuaMethod(name = "getLuaDebuggerErrorCount", global = true)
        public static int getLuaDebuggerErrorCount() {
            final KahluaThread thread = LuaManager.thread;
            return KahluaThread.m_error_count;
        }
        
        @LuaMethod(name = "getLuaDebuggerErrors", global = true)
        public static ArrayList<String> getLuaDebuggerErrors() {
            final KahluaThread thread = LuaManager.thread;
            return new ArrayList<String>(KahluaThread.m_errors_list);
        }
        
        @LuaMethod(name = "getGameSpeed", global = true)
        public static int getGameSpeed() {
            if (UIManager.getSpeedControls() != null) {
                return UIManager.getSpeedControls().getCurrentGameSpeed();
            }
            return 0;
        }
        
        @LuaMethod(name = "setGameSpeed", global = true)
        public static void setGameSpeed(final int n) {
            DebugLog.log("EXITDEBUG: setGameSpeed 1");
            if (UIManager.getSpeedControls() == null) {
                DebugLog.log("EXITDEBUG: setGameSpeed 2");
                return;
            }
            UIManager.getSpeedControls().SetCurrentGameSpeed(n);
            DebugLog.log("EXITDEBUG: setGameSpeed 3");
        }
        
        @LuaMethod(name = "isGamePaused", global = true)
        public static boolean isGamePaused() {
            return GameTime.isGamePaused();
        }
        
        @LuaMethod(name = "getMouseXScaled", global = true)
        public static int getMouseXScaled() {
            return Mouse.getX();
        }
        
        @LuaMethod(name = "getMouseYScaled", global = true)
        public static int getMouseYScaled() {
            return Mouse.getY();
        }
        
        @LuaMethod(name = "getMouseX", global = true)
        public static int getMouseX() {
            return Mouse.getXA();
        }
        
        @LuaMethod(name = "setMouseXY", global = true)
        public static void setMouseXY(final int n, final int n2) {
            Mouse.setXY(n, n2);
        }
        
        @LuaMethod(name = "isMouseButtonDown", global = true)
        public static boolean isMouseButtonDown(final int n) {
            return Mouse.isButtonDown(n);
        }
        
        @LuaMethod(name = "getMouseY", global = true)
        public static int getMouseY() {
            return Mouse.getYA();
        }
        
        @LuaMethod(name = "getSoundManager", global = true)
        public static BaseSoundManager getSoundManager() {
            return SoundManager.instance;
        }
        
        @LuaMethod(name = "getLastPlayedDate", global = true)
        public static String getLastPlayedDate(final String s) {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s));
            if (!file.exists()) {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("UI_LastPlayed"));
            }
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Translator.getText("UI_LastPlayed"), new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(file.lastModified())));
        }
        
        @LuaMethod(name = "getTextureFromSaveDir", global = true)
        public static Texture getTextureFromSaveDir(final String s, final String s2) {
            TextureID.UseFiltering = true;
            final Texture sharedTexture = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s2, File.separator, s));
            TextureID.UseFiltering = false;
            return sharedTexture;
        }
        
        @LuaMethod(name = "getSaveInfo", global = true)
        public static KahluaTable getSaveInfo(String s) {
            if (!s.contains(File.separator)) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, IsoWorld.instance.getGameMode(), File.separator, s);
            }
            final KahluaTable table = LuaManager.platform.newTable();
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s));
            if (file.exists()) {
                table.rawset((Object)"lastPlayed", (Object)new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(file.lastModified())));
                final String[] split = s.split(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, File.separator));
                table.rawset((Object)"saveName", (Object)file.getName());
                table.rawset((Object)"gameMode", (Object)split[split.length - 2]);
            }
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s, File.separator));
            if (file2.exists()) {
                try {
                    final FileInputStream in = new FileInputStream(file2);
                    try {
                        final DataInputStream dataInputStream = new DataInputStream(in);
                        try {
                            final int int1 = dataInputStream.readInt();
                            table.rawset((Object)"worldVersion", (Object)(double)int1);
                            if (int1 >= 18) {
                                try {
                                    String readString = GameWindow.ReadString(dataInputStream);
                                    if (readString.equals("DEFAULT")) {
                                        readString = "Muldraugh, KY";
                                    }
                                    table.rawset((Object)"mapName", (Object)readString);
                                }
                                catch (Exception ex3) {}
                            }
                            if (int1 >= 74) {
                                try {
                                    table.rawset((Object)"difficulty", (Object)GameWindow.ReadString(dataInputStream));
                                }
                                catch (Exception ex4) {}
                            }
                            dataInputStream.close();
                        }
                        catch (Throwable t) {
                            try {
                                dataInputStream.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                            throw t;
                        }
                        in.close();
                    }
                    catch (Throwable t2) {
                        try {
                            in.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                        throw t2;
                    }
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
            final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s, File.separator);
            final ActiveMods activeMods = new ActiveMods(s);
            if (new ActiveModsFile().read(s2, activeMods)) {
                table.rawset((Object)"activeMods", (Object)activeMods);
            }
            final String s3 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s);
            table.rawset((Object)"playerAlive", (Object)PlayerDBHelper.isPlayerAlive(s3, 1));
            final KahluaTable table2 = LuaManager.platform.newTable();
            try {
                final ArrayList<Object> players = PlayerDBHelper.getPlayers(s3);
                for (int i = 0; i < players.size(); i += 3) {
                    final Double n = players.get(i);
                    final String s4 = players.get(i + 1);
                    final Boolean b = players.get(i + 2);
                    final KahluaTable table3 = LuaManager.platform.newTable();
                    table3.rawset((Object)"sqlID", (Object)n);
                    table3.rawset((Object)"name", (Object)s4);
                    table3.rawset((Object)"isDead", (Object)b);
                    table2.rawset(i / 3 + 1, (Object)table3);
                }
            }
            catch (Exception ex2) {
                ExceptionLogger.logException(ex2);
            }
            table.rawset((Object)"players", (Object)table2);
            return table;
        }
        
        @LuaMethod(name = "setSavefilePlayer1", global = true)
        public static void setSavefilePlayer1(final String s, final String s2, final int n) {
            final String saveDirSub = ZomboidFileSystem.instance.getSaveDirSub(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, File.separator, s2));
            try {
                PlayerDBHelper.setPlayer1(saveDirSub, n);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        @LuaMethod(name = "getServerSavedWorldVersion", global = true)
        public static int getServerSavedWorldVersion(final String s) {
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, s, File.separator));
            if (file.exists()) {
                try {
                    final FileInputStream in = new FileInputStream(file);
                    try {
                        final DataInputStream dataInputStream = new DataInputStream(in);
                        try {
                            final byte byte1 = dataInputStream.readByte();
                            final byte byte2 = dataInputStream.readByte();
                            final byte byte3 = dataInputStream.readByte();
                            final byte byte4 = dataInputStream.readByte();
                            if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
                                final int int1 = dataInputStream.readInt();
                                dataInputStream.close();
                                in.close();
                                return int1;
                            }
                            final int n = 1;
                            dataInputStream.close();
                            in.close();
                            return n;
                        }
                        catch (Throwable t) {
                            try {
                                dataInputStream.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                            throw t;
                        }
                    }
                    catch (Throwable t2) {
                        try {
                            in.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                        throw t2;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return 0;
        }
        
        @LuaMethod(name = "getGameVersionInfo", global = true)
        public static KahluaTable getGameVersionInfo() {
            final KahluaTable table = LuaManager.platform.newTable();
            table.rawset((Object)"svnRevision", (Object)"");
            table.rawset((Object)"buildDate", (Object)"");
            table.rawset((Object)"buildTime", (Object)"");
            table.rawset((Object)"version", (Object)Core.getInstance().getVersionNumber());
            return table;
        }
        
        @LuaMethod(name = "getZombieInfo", global = true)
        public static KahluaTable getZombieInfo(final IsoZombie isoZombie) {
            final KahluaTable table = LuaManager.platform.newTable();
            if (isoZombie == null) {
                return table;
            }
            table.rawset((Object)"OnlineID", (Object)isoZombie.OnlineID);
            table.rawset((Object)"RealX", (Object)isoZombie.realx);
            table.rawset((Object)"RealY", (Object)isoZombie.realy);
            table.rawset((Object)"X", (Object)isoZombie.x);
            table.rawset((Object)"Y", (Object)isoZombie.y);
            table.rawset((Object)"TargetX", (Object)isoZombie.networkAI.targetX);
            table.rawset((Object)"TargetY", (Object)isoZombie.networkAI.targetY);
            table.rawset((Object)"PathLength", (Object)isoZombie.getPathFindBehavior2().getPathLength());
            table.rawset((Object)"TargetLength", (Object)Math.sqrt((isoZombie.x - isoZombie.getPathFindBehavior2().getTargetX()) * (isoZombie.x - isoZombie.getPathFindBehavior2().getTargetX()) + (isoZombie.y - isoZombie.getPathFindBehavior2().getTargetY()) * (isoZombie.y - isoZombie.getPathFindBehavior2().getTargetY())));
            table.rawset((Object)"clientActionState", (Object)isoZombie.getActionStateName());
            table.rawset((Object)"clientAnimationState", (Object)isoZombie.getAnimationStateName());
            table.rawset((Object)"finderProgress", (Object)isoZombie.getFinder().progress.name());
            table.rawset((Object)"usePathFind", (Object)Boolean.toString(isoZombie.networkAI.usePathFind));
            table.rawset((Object)"owner", (Object)isoZombie.authOwner.username);
            isoZombie.networkAI.DebugInterfaceActive = true;
            return table;
        }
        
        @LuaMethod(name = "getPlayerInfo", global = true)
        public static KahluaTable getPlayerInfo(final IsoPlayer isoPlayer) {
            final KahluaTable table = LuaManager.platform.newTable();
            if (isoPlayer == null) {
                return table;
            }
            final long l = GameTime.getServerTime() / 1000000L;
            table.rawset((Object)"OnlineID", (Object)isoPlayer.OnlineID);
            table.rawset((Object)"RealX", (Object)isoPlayer.realx);
            table.rawset((Object)"RealY", (Object)isoPlayer.realy);
            table.rawset((Object)"X", (Object)isoPlayer.x);
            table.rawset((Object)"Y", (Object)isoPlayer.y);
            table.rawset((Object)"TargetX", (Object)isoPlayer.networkAI.targetX);
            table.rawset((Object)"TargetY", (Object)isoPlayer.networkAI.targetY);
            table.rawset((Object)"TargetT", (Object)isoPlayer.networkAI.targetZ);
            table.rawset((Object)"ServerT", (Object)l);
            table.rawset((Object)"PathLength", (Object)isoPlayer.getPathFindBehavior2().getPathLength());
            table.rawset((Object)"TargetLength", (Object)Math.sqrt((isoPlayer.x - isoPlayer.getPathFindBehavior2().getTargetX()) * (isoPlayer.x - isoPlayer.getPathFindBehavior2().getTargetX()) + (isoPlayer.y - isoPlayer.getPathFindBehavior2().getTargetY()) * (isoPlayer.y - isoPlayer.getPathFindBehavior2().getTargetY())));
            table.rawset((Object)"clientActionState", (Object)isoPlayer.getActionStateName());
            table.rawset((Object)"clientAnimationState", (Object)isoPlayer.getAnimationStateName());
            table.rawset((Object)"finderProgress", (Object)isoPlayer.getFinder().progress.name());
            table.rawset((Object)"usePathFind", (Object)Boolean.toString(isoPlayer.networkAI.usePathFind));
            return table;
        }
        
        @LuaMethod(name = "getMapInfo", global = true)
        public static KahluaTable getMapInfo(String s) {
            if (s.contains(";")) {
                s = s.split(";")[0];
            }
            final ChooseGameInfo.Map mapDetails = ChooseGameInfo.getMapDetails(s);
            if (mapDetails == null) {
                return null;
            }
            final KahluaTable table = LuaManager.platform.newTable();
            table.rawset((Object)"description", (Object)mapDetails.getDescription());
            table.rawset((Object)"dir", (Object)mapDetails.getDirectory());
            final KahluaTable table2 = LuaManager.platform.newTable();
            final int n = 1;
            final Iterator<String> iterator = mapDetails.getLotDirectories().iterator();
            while (iterator.hasNext()) {
                table2.rawset((Object)(double)n, (Object)iterator.next());
            }
            table.rawset((Object)"lots", (Object)table2);
            table.rawset((Object)"thumb", (Object)mapDetails.getThumbnail());
            table.rawset((Object)"title", (Object)mapDetails.getTitle());
            return table;
        }
        
        @LuaMethod(name = "getVehicleInfo", global = true)
        public static KahluaTable getVehicleInfo(final BaseVehicle baseVehicle) {
            if (baseVehicle == null) {
                return null;
            }
            final KahluaTable table = LuaManager.platform.newTable();
            table.rawset((Object)"name", (Object)baseVehicle.getScript().getName());
            table.rawset((Object)"weight", (Object)baseVehicle.getMass());
            table.rawset((Object)"speed", (Object)baseVehicle.getMaxSpeed());
            table.rawset((Object)"frontEndDurability", (Object)Integer.toString(baseVehicle.frontEndDurability));
            table.rawset((Object)"rearEndDurability", (Object)Integer.toString(baseVehicle.rearEndDurability));
            table.rawset((Object)"currentFrontEndDurability", (Object)Integer.toString(baseVehicle.currentFrontEndDurability));
            table.rawset((Object)"currentRearEndDurability", (Object)Integer.toString(baseVehicle.currentRearEndDurability));
            table.rawset((Object)"engine_running", (Object)baseVehicle.isEngineRunning());
            table.rawset((Object)"engine_started", (Object)baseVehicle.isEngineStarted());
            table.rawset((Object)"engine_quality", (Object)baseVehicle.getEngineQuality());
            table.rawset((Object)"engine_loudness", (Object)baseVehicle.getEngineLoudness());
            table.rawset((Object)"engine_power", (Object)baseVehicle.getEnginePower());
            table.rawset((Object)"battery_isset", (Object)(baseVehicle.getBattery() != null));
            table.rawset((Object)"battery_charge", (Object)baseVehicle.getBatteryCharge());
            table.rawset((Object)"gas_amount", (Object)baseVehicle.getPartById("GasTank").getContainerContentAmount());
            table.rawset((Object)"gas_capacity", (Object)baseVehicle.getPartById("GasTank").getContainerCapacity());
            final VehiclePart partById = baseVehicle.getPartById("DoorFrontLeft");
            table.rawset((Object)"doorleft_exist", (Object)(partById != null));
            if (partById != null) {
                table.rawset((Object)"doorleft_open", (Object)partById.getDoor().isOpen());
                table.rawset((Object)"doorleft_locked", (Object)partById.getDoor().isLocked());
                table.rawset((Object)"doorleft_lockbroken", (Object)partById.getDoor().isLockBroken());
                final VehicleWindow window = partById.findWindow();
                table.rawset((Object)"windowleft_exist", (Object)(window != null));
                if (window != null) {
                    table.rawset((Object)"windowleft_open", (Object)window.isOpen());
                    table.rawset((Object)"windowleft_health", (Object)window.getHealth());
                }
            }
            final VehiclePart partById2 = baseVehicle.getPartById("DoorFrontRight");
            table.rawset((Object)"doorright_exist", (Object)(partById2 != null));
            if (partById != null) {
                table.rawset((Object)"doorright_open", (Object)partById2.getDoor().isOpen());
                table.rawset((Object)"doorright_locked", (Object)partById2.getDoor().isLocked());
                table.rawset((Object)"doorright_lockbroken", (Object)partById2.getDoor().isLockBroken());
                final VehicleWindow window2 = partById2.findWindow();
                table.rawset((Object)"windowright_exist", (Object)(window2 != null));
                if (window2 != null) {
                    table.rawset((Object)"windowright_open", (Object)window2.isOpen());
                    table.rawset((Object)"windowright_health", (Object)window2.getHealth());
                }
            }
            table.rawset((Object)"headlights_set", (Object)baseVehicle.hasHeadlights());
            table.rawset((Object)"headlights_on", (Object)baseVehicle.getHeadlightsOn());
            if (baseVehicle.getPartById("Heater") != null) {
                table.rawset((Object)"heater_isset", (Object)true);
                final Object rawget = baseVehicle.getPartById("Heater").getModData().rawget((Object)"active");
                if (rawget == null) {
                    table.rawset((Object)"heater_on", (Object)false);
                }
                else {
                    table.rawset((Object)"heater_on", (Object)(rawget == Boolean.TRUE));
                }
            }
            else {
                table.rawset((Object)"heater_isset", (Object)false);
            }
            return table;
        }
        
        @LuaMethod(name = "getLotDirectories", global = true)
        public static ArrayList<String> getLotDirectories() {
            if (IsoWorld.instance.MetaGrid != null) {
                return IsoWorld.instance.MetaGrid.getLotDirectories();
            }
            return null;
        }
        
        @LuaMethod(name = "useTextureFiltering", global = true)
        public static void useTextureFiltering(final boolean useFiltering) {
            TextureID.UseFiltering = useFiltering;
        }
        
        @LuaMethod(name = "getTexture", global = true)
        public static Texture getTexture(final String s) {
            return Texture.getSharedTexture(s);
        }
        
        @LuaMethod(name = "getTextManager", global = true)
        public static TextManager getTextManager() {
            return TextManager.instance;
        }
        
        @LuaMethod(name = "setProgressBarValue", global = true)
        public static void setProgressBarValue(final IsoPlayer isoPlayer, final int n) {
            if (isoPlayer.isLocalPlayer()) {
                UIManager.getProgressBar(isoPlayer.getPlayerNum()).setValue((float)n);
            }
        }
        
        @LuaMethod(name = "getText", global = true)
        public static String getText(final String s) {
            return Translator.getText(s);
        }
        
        @LuaMethod(name = "getText", global = true)
        public static String getText(final String s, final Object o) {
            return Translator.getText(s, o);
        }
        
        @LuaMethod(name = "getText", global = true)
        public static String getText(final String s, final Object o, final Object o2) {
            return Translator.getText(s, o, o2);
        }
        
        @LuaMethod(name = "getText", global = true)
        public static String getText(final String s, final Object o, final Object o2, final Object o3) {
            return Translator.getText(s, o, o2, o3);
        }
        
        @LuaMethod(name = "getText", global = true)
        public static String getText(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
            return Translator.getText(s, o, o2, o3, o4);
        }
        
        @LuaMethod(name = "getTextOrNull", global = true)
        public static String getTextOrNull(final String s) {
            return Translator.getTextOrNull(s);
        }
        
        @LuaMethod(name = "getTextOrNull", global = true)
        public static String getTextOrNull(final String s, final Object o) {
            return Translator.getTextOrNull(s, o);
        }
        
        @LuaMethod(name = "getTextOrNull", global = true)
        public static String getTextOrNull(final String s, final Object o, final Object o2) {
            return Translator.getTextOrNull(s, o, o2);
        }
        
        @LuaMethod(name = "getTextOrNull", global = true)
        public static String getTextOrNull(final String s, final Object o, final Object o2, final Object o3) {
            return Translator.getTextOrNull(s, o, o2, o3);
        }
        
        @LuaMethod(name = "getTextOrNull", global = true)
        public static String getTextOrNull(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
            return Translator.getTextOrNull(s, o, o2, o3, o4);
        }
        
        @LuaMethod(name = "getItemText", global = true)
        public static String getItemText(final String s) {
            return Translator.getDisplayItemName(s);
        }
        
        @LuaMethod(name = "getRadioText", global = true)
        public static String getRadioText(final String s) {
            return Translator.getRadioText(s);
        }
        
        @LuaMethod(name = "getTextMediaEN", global = true)
        public static String getTextMediaEN(final String s) {
            return Translator.getTextMediaEN(s);
        }
        
        @LuaMethod(name = "getItemNameFromFullType", global = true)
        public static String getItemNameFromFullType(final String s) {
            return Translator.getItemNameFromFullType(s);
        }
        
        @LuaMethod(name = "getRecipeDisplayName", global = true)
        public static String getRecipeDisplayName(final String s) {
            return Translator.getRecipeName(s);
        }
        
        @LuaMethod(name = "getMyDocumentFolder", global = true)
        public static String getMyDocumentFolder() {
            return Core.getMyDocumentFolder();
        }
        
        @LuaMethod(name = "getSpriteManager", global = true)
        public static IsoSpriteManager getSpriteManager(final String s) {
            return IsoSpriteManager.instance;
        }
        
        @LuaMethod(name = "getSprite", global = true)
        public static IsoSprite getSprite(final String s) {
            return IsoSpriteManager.instance.getSprite(s);
        }
        
        @LuaMethod(name = "getServerModData", global = true)
        public static void getServerModData() {
            GameClient.getCustomModData();
        }
        
        @LuaMethod(name = "isXBOXController", global = true)
        public static boolean isXBOXController() {
            for (int i = 0; i < GameWindow.GameInput.getControllerCount(); ++i) {
                final Controller controller = GameWindow.GameInput.getController(i);
                if (controller != null) {
                    if (controller.getGamepadName().contains("XBOX 360")) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @LuaMethod(name = "sendClientCommand", global = true)
        public static void sendClientCommand(final String s, final String s2, final KahluaTable kahluaTable) {
            if (GameClient.bClient && GameClient.bIngame) {
                GameClient.instance.sendClientCommand(null, s, s2, kahluaTable);
            }
            else {
                if (GameServer.bServer) {
                    throw new IllegalStateException("can't call this function on the server");
                }
                SinglePlayerClient.sendClientCommand(null, s, s2, kahluaTable);
            }
        }
        
        @LuaMethod(name = "sendClientCommand", global = true)
        public static void sendClientCommand(final IsoPlayer isoPlayer, final String s, final String s2, final KahluaTable kahluaTable) {
            if (isoPlayer == null || !isoPlayer.isLocalPlayer()) {
                return;
            }
            if (GameClient.bClient && GameClient.bIngame) {
                GameClient.instance.sendClientCommand(isoPlayer, s, s2, kahluaTable);
            }
            else {
                if (GameServer.bServer) {
                    throw new IllegalStateException("can't call this function on the server");
                }
                SinglePlayerClient.sendClientCommand(isoPlayer, s, s2, kahluaTable);
            }
        }
        
        @LuaMethod(name = "sendServerCommand", global = true)
        public static void sendServerCommand(final String s, final String s2, final KahluaTable kahluaTable) {
            if (GameServer.bServer) {
                GameServer.sendServerCommand(s, s2, kahluaTable);
            }
        }
        
        @LuaMethod(name = "sendServerCommand", global = true)
        public static void sendServerCommand(final IsoPlayer isoPlayer, final String s, final String s2, final KahluaTable kahluaTable) {
            if (GameServer.bServer) {
                GameServer.sendServerCommand(isoPlayer, s, s2, kahluaTable);
            }
        }
        
        @LuaMethod(name = "getOnlineUsername", global = true)
        public static String getOnlineUsername() {
            return IsoPlayer.getInstance().getDisplayName();
        }
        
        @LuaMethod(name = "isValidUserName", global = true)
        public static boolean isValidUserName(final String s) {
            return ServerWorldDatabase.isValidUserName(s);
        }
        
        @LuaMethod(name = "getHourMinute", global = true)
        public static String getHourMinute() {
            return LuaManager.getHourMinuteJava();
        }
        
        @LuaMethod(name = "SendCommandToServer", global = true)
        public static void SendCommandToServer(final String s) {
            GameClient.SendCommandToServer(s);
        }
        
        @LuaMethod(name = "isAdmin", global = true)
        public static boolean isAdmin() {
            return GameClient.bClient && GameClient.accessLevel.equals("admin");
        }
        
        @LuaMethod(name = "canModifyPlayerScoreboard", global = true)
        public static boolean canModifyPlayerScoreboard() {
            return GameClient.bClient && !GameClient.accessLevel.equals("");
        }
        
        @LuaMethod(name = "isAccessLevel", global = true)
        public static boolean isAccessLevel(final String anObject) {
            return GameClient.bClient && !GameClient.accessLevel.equals("") && GameClient.accessLevel.equals(anObject);
        }
        
        @LuaMethod(name = "sendBandage", global = true)
        public static void sendBandage(final int n, final int n2, final boolean b, final float n3, final boolean b2, final String s) {
            GameClient.instance.sendBandage(n, n2, b, n3, b2, s);
        }
        
        @LuaMethod(name = "sendCataplasm", global = true)
        public static void sendCataplasm(final int n, final int n2, final float n3, final float n4, final float n5) {
            GameClient.instance.sendCataplasm(n, n2, n3, n4, n5);
        }
        
        @LuaMethod(name = "sendStitch", global = true)
        public static void sendStitch(final int n, final int n2, final boolean b, final float n3) {
            GameClient.instance.sendStitch(n, n2, b, n3);
        }
        
        @LuaMethod(name = "sendWoundInfection", global = true)
        public static void sendWoundInfection(final int n, final int n2, final boolean b) {
            GameClient.instance.sendWoundInfection(n, n2, b);
        }
        
        @LuaMethod(name = "sendDisinfect", global = true)
        public static void sendDisinfect(final int n, final int n2, final float n3) {
            GameClient.instance.sendDisinfect(n, n2, n3);
        }
        
        @LuaMethod(name = "sendSplint", global = true)
        public static void sendSplint(final int n, final int n2, final boolean b, final float n3, final String s) {
            GameClient.instance.sendSplint(n, n2, b, n3, s);
        }
        
        @LuaMethod(name = "sendAdditionalPain", global = true)
        public static void sendAdditionalPain(final int n, final int n2, final float n3) {
            GameClient.instance.sendAdditionalPain(n, n2, n3);
        }
        
        @LuaMethod(name = "sendRemoveGlass", global = true)
        public static void sendRemoveGlass(final int n, final int n2) {
            GameClient.instance.sendRemoveGlass(n, n2);
        }
        
        @LuaMethod(name = "sendRemoveBullet", global = true)
        public static void sendRemoveBullet(final int n, final int n2, final int n3) {
            GameClient.instance.sendRemoveBullet(n, n2, n3);
        }
        
        @LuaMethod(name = "sendCleanBurn", global = true)
        public static void sendCleanBurn(final int n, final int n2) {
            GameClient.instance.sendCleanBurn(n, n2);
        }
        
        @LuaMethod(name = "getGameClient", global = true)
        public static GameClient getGameClient() {
            return GameClient.instance;
        }
        
        @LuaMethod(name = "sendRequestInventory", global = true)
        public static void sendRequestInventory(final IsoPlayer isoPlayer) {
            GameClient.sendRequestInventory(isoPlayer);
        }
        
        @LuaMethod(name = "InvMngGetItem", global = true)
        public static void InvMngGetItem(final long n, final String s, final IsoPlayer isoPlayer) {
            GameClient.invMngRequestItem(n, s, isoPlayer);
        }
        
        @LuaMethod(name = "InvMngRemoveItem", global = true)
        public static void InvMngRemoveItem(final long n, final IsoPlayer isoPlayer) {
            GameClient.invMngRequestRemoveItem(n, isoPlayer);
        }
        
        @LuaMethod(name = "getConnectedPlayers", global = true)
        public static ArrayList<IsoPlayer> getConnectedPlayers() {
            return GameClient.instance.getConnectedPlayers();
        }
        
        @LuaMethod(name = "getPlayerFromUsername", global = true)
        public static IsoPlayer getPlayerFromUsername(final String s) {
            return GameClient.instance.getPlayerFromUsername(s);
        }
        
        @LuaMethod(name = "isCoopHost", global = true)
        public static boolean isCoopHost() {
            return GameClient.connection != null && GameClient.connection.isCoopHost;
        }
        
        @LuaMethod(name = "setAdmin", global = true)
        public static void setAdmin() {
            if (!CoopMaster.instance.isRunning()) {
                return;
            }
            String accessLevel = "admin";
            if (GameClient.connection.accessLevel.equals("admin")) {
                accessLevel = "";
            }
            GameClient.connection.accessLevel = accessLevel;
            GameClient.accessLevel = accessLevel;
            IsoPlayer.getInstance().accessLevel = accessLevel;
            GameClient.SendCommandToServer(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, IsoPlayer.getInstance().username, accessLevel.equals("") ? "none" : accessLevel));
            if ((accessLevel.equals("") && IsoPlayer.getInstance().isInvisible()) || (accessLevel.equals("admin") && !IsoPlayer.getInstance().isInvisible())) {
                GameClient.SendCommandToServer("/invisible");
            }
        }
        
        @LuaMethod(name = "addWarningPoint", global = true)
        public static void addWarningPoint(final String s, final String s2, final int n) {
            if (GameClient.bClient) {
                GameClient.instance.addWarningPoint(s, s2, n);
            }
        }
        
        @LuaMethod(name = "toggleSafetyServer", global = true)
        public static void toggleSafetyServer(final IsoPlayer isoPlayer) {
            GameClient.toggleSafety(isoPlayer);
        }
        
        @LuaMethod(name = "disconnect", global = true)
        public static void disconnect() {
            GameClient.connection.forceDisconnect();
        }
        
        @LuaMethod(name = "writeLog", global = true)
        public static void writeLog(final String s, final String s2) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.WriteLog.doPacket(startPacket);
            startPacket.putUTF(s);
            startPacket.putUTF(s2);
            PacketTypes.PacketType.WriteLog.send(GameClient.connection);
        }
        
        @LuaMethod(name = "doKeyPress", global = true)
        public static void doKeyPress(final boolean doLuaKeyPressed) {
            GameKeyboard.doLuaKeyPressed = doLuaKeyPressed;
        }
        
        @LuaMethod(name = "getEvolvedRecipes", global = true)
        public static Stack<EvolvedRecipe> getEvolvedRecipes() {
            return ScriptManager.instance.getAllEvolvedRecipes();
        }
        
        @LuaMethod(name = "getZone", global = true)
        public static IsoMetaGrid.Zone getZone(final int n, final int n2, final int n3) {
            return IsoWorld.instance.MetaGrid.getZoneAt(n, n2, n3);
        }
        
        @LuaMethod(name = "getZones", global = true)
        public static ArrayList<IsoMetaGrid.Zone> getZones(final int n, final int n2, final int n3) {
            return IsoWorld.instance.MetaGrid.getZonesAt(n, n2, n3);
        }
        
        @LuaMethod(name = "getVehicleZoneAt", global = true)
        public static IsoMetaGrid.VehicleZone getVehicleZoneAt(final int n, final int n2, final int n3) {
            return IsoWorld.instance.MetaGrid.getVehicleZoneAt(n, n2, n3);
        }
        
        @LuaMethod(name = "replaceWith", global = true)
        public static String replaceWith(final String s, final String regex, final String replacement) {
            return s.replaceFirst(regex, replacement);
        }
        
        @LuaMethod(name = "getTimestamp", global = true)
        public static long getTimestamp() {
            return System.currentTimeMillis() / 1000L;
        }
        
        @LuaMethod(name = "getTimestampMs", global = true)
        public static long getTimestampMs() {
            return System.currentTimeMillis();
        }
        
        @LuaMethod(name = "forceSnowCheck", global = true)
        public static void forceSnowCheck() {
            ErosionMain.getInstance().snowCheck();
        }
        
        @LuaMethod(name = "getGametimeTimestamp", global = true)
        public static long getGametimeTimestamp() {
            return GameTime.instance.getCalender().getTimeInMillis() / 1000L;
        }
        
        @LuaMethod(name = "canInviteFriends", global = true)
        public static boolean canInviteFriends() {
            return GameClient.bClient && SteamUtils.isSteamModeEnabled() && (CoopMaster.instance.isRunning() || !GameClient.bCoopInvite);
        }
        
        @LuaMethod(name = "inviteFriend", global = true)
        public static void inviteFriend(final String s) {
            if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
                CoopMaster.instance.sendMessage("invite-add", s);
            }
            SteamFriends.InviteUserToGame(SteamUtils.convertStringToSteamID(s), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, GameClient.ip, GameClient.port));
        }
        
        @LuaMethod(name = "getFriendsList", global = true)
        public static KahluaTable getFriendsList() {
            final KahluaTable table = LuaManager.platform.newTable();
            if (!getSteamModeActive()) {
                return table;
            }
            final List<SteamFriend> getFriendList = SteamFriends.GetFriendList();
            int n = 1;
            for (int i = 0; i < getFriendList.size(); ++i) {
                table.rawset((Object)(double)n, (Object)getFriendList.get(i));
                ++n;
            }
            return table;
        }
        
        @LuaMethod(name = "getSteamModeActive", global = true)
        public static Boolean getSteamModeActive() {
            return SteamUtils.isSteamModeEnabled();
        }
        
        @LuaMethod(name = "isValidSteamID", global = true)
        public static boolean isValidSteamID(final String s) {
            return s != null && !s.isEmpty() && SteamUtils.isValidSteamID(s);
        }
        
        @LuaMethod(name = "getCurrentUserSteamID", global = true)
        public static String getCurrentUserSteamID() {
            if (SteamUtils.isSteamModeEnabled() && !GameServer.bServer) {
                return SteamUser.GetSteamIDString();
            }
            return null;
        }
        
        @LuaMethod(name = "getCurrentUserProfileName", global = true)
        public static String getCurrentUserProfileName() {
            if (SteamUtils.isSteamModeEnabled() && !GameServer.bServer) {
                return SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID());
            }
            return null;
        }
        
        @LuaMethod(name = "getSteamScoreboard", global = true)
        public static boolean getSteamScoreboard() {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                final String value = ServerOptions.instance.SteamScoreboard.getValue();
                return "true".equals(value) || (GameClient.accessLevel.equals("admin") && "admin".equals(value));
            }
            return false;
        }
        
        @LuaMethod(name = "isSteamOverlayEnabled", global = true)
        public static boolean isSteamOverlayEnabled() {
            return SteamUtils.isOverlayEnabled();
        }
        
        @LuaMethod(name = "activateSteamOverlayToWorkshop", global = true)
        public static void activateSteamOverlayToWorkshop() {
            if (SteamUtils.isOverlayEnabled()) {
                SteamFriends.ActivateGameOverlayToWebPage("steam://url/SteamWorkshopPage/108600");
            }
        }
        
        @LuaMethod(name = "activateSteamOverlayToWorkshopUser", global = true)
        public static void activateSteamOverlayToWorkshopUser() {
            if (SteamUtils.isOverlayEnabled()) {
                SteamFriends.ActivateGameOverlayToWebPage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, SteamUser.GetSteamIDString()));
            }
        }
        
        @LuaMethod(name = "activateSteamOverlayToWorkshopItem", global = true)
        public static void activateSteamOverlayToWorkshopItem(final String s) {
            if (SteamUtils.isOverlayEnabled() && SteamUtils.isValidSteamID(s)) {
                SteamFriends.ActivateGameOverlayToWebPage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
        }
        
        @LuaMethod(name = "activateSteamOverlayToWebPage", global = true)
        public static void activateSteamOverlayToWebPage(final String s) {
            if (SteamUtils.isOverlayEnabled()) {
                SteamFriends.ActivateGameOverlayToWebPage(s);
            }
        }
        
        @LuaMethod(name = "getSteamProfileNameFromSteamID", global = true)
        public static String getSteamProfileNameFromSteamID(final String s) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                final long convertStringToSteamID = SteamUtils.convertStringToSteamID(s);
                if (convertStringToSteamID != -1L) {
                    return SteamFriends.GetFriendPersonaName(convertStringToSteamID);
                }
            }
            return null;
        }
        
        @LuaMethod(name = "getSteamAvatarFromSteamID", global = true)
        public static Texture getSteamAvatarFromSteamID(final String s) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                final long convertStringToSteamID = SteamUtils.convertStringToSteamID(s);
                if (convertStringToSteamID != -1L) {
                    return Texture.getSteamAvatar(convertStringToSteamID);
                }
            }
            return null;
        }
        
        @LuaMethod(name = "getSteamIDFromUsername", global = true)
        public static String getSteamIDFromUsername(final String s) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                final IsoPlayer playerFromUsername = GameClient.instance.getPlayerFromUsername(s);
                if (playerFromUsername != null) {
                    return SteamUtils.convertSteamIDToString(playerFromUsername.getSteamID());
                }
            }
            return null;
        }
        
        @LuaMethod(name = "resetRegionFile", global = true)
        public static void resetRegionFile() {
            ServerOptions.getInstance().resetRegionFile();
        }
        
        @LuaMethod(name = "getSteamProfileNameFromUsername", global = true)
        public static String getSteamProfileNameFromUsername(final String s) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                final IsoPlayer playerFromUsername = GameClient.instance.getPlayerFromUsername(s);
                if (playerFromUsername != null) {
                    return SteamFriends.GetFriendPersonaName(playerFromUsername.getSteamID());
                }
            }
            return null;
        }
        
        @LuaMethod(name = "getSteamAvatarFromUsername", global = true)
        public static Texture getSteamAvatarFromUsername(final String s) {
            if (SteamUtils.isSteamModeEnabled() && GameClient.bClient) {
                final IsoPlayer playerFromUsername = GameClient.instance.getPlayerFromUsername(s);
                if (playerFromUsername != null) {
                    return Texture.getSteamAvatar(playerFromUsername.getSteamID());
                }
            }
            return null;
        }
        
        @LuaMethod(name = "getSteamWorkshopStagedItems", global = true)
        public static ArrayList<SteamWorkshopItem> getSteamWorkshopStagedItems() {
            if (SteamUtils.isSteamModeEnabled()) {
                return SteamWorkshop.instance.loadStagedItems();
            }
            return null;
        }
        
        @LuaMethod(name = "getSteamWorkshopItemIDs", global = true)
        public static ArrayList<String> getSteamWorkshopItemIDs() {
            if (!SteamUtils.isSteamModeEnabled()) {
                return null;
            }
            final ArrayList<String> list = new ArrayList<String>();
            final String[] getInstalledItemFolders = SteamWorkshop.instance.GetInstalledItemFolders();
            if (getInstalledItemFolders == null) {
                return list;
            }
            for (int i = 0; i < getInstalledItemFolders.length; ++i) {
                final String idFromItemInstallFolder = SteamWorkshop.instance.getIDFromItemInstallFolder(getInstalledItemFolders[i]);
                if (idFromItemInstallFolder != null) {
                    list.add(idFromItemInstallFolder);
                }
            }
            return list;
        }
        
        @LuaMethod(name = "getSteamWorkshopItemMods", global = true)
        public static ArrayList<ChooseGameInfo.Mod> getSteamWorkshopItemMods(final String s) {
            if (SteamUtils.isSteamModeEnabled()) {
                final long convertStringToSteamID = SteamUtils.convertStringToSteamID(s);
                if (convertStringToSteamID > 0L) {
                    return ZomboidFileSystem.instance.getWorkshopItemMods(convertStringToSteamID);
                }
            }
            return null;
        }
        
        @LuaMethod(name = "sendPlayerStatsChange", global = true)
        public static void sendPlayerStatsChange(final IsoPlayer isoPlayer) {
            if (GameClient.bClient) {
                GameClient.instance.sendChangedPlayerStats(isoPlayer);
            }
        }
        
        @LuaMethod(name = "sendPersonalColor", global = true)
        public static void sendPersonalColor(final IsoPlayer isoPlayer) {
            if (GameClient.bClient) {
                GameClient.instance.sendPersonalColor(isoPlayer);
            }
        }
        
        @LuaMethod(name = "requestTrading", global = true)
        public static void requestTrading(final IsoPlayer isoPlayer, final IsoPlayer isoPlayer2) {
            GameClient.instance.requestTrading(isoPlayer, isoPlayer2);
        }
        
        @LuaMethod(name = "acceptTrading", global = true)
        public static void acceptTrading(final IsoPlayer isoPlayer, final IsoPlayer isoPlayer2, final boolean b) {
            GameClient.instance.acceptTrading(isoPlayer, isoPlayer2, b);
        }
        
        @LuaMethod(name = "tradingUISendAddItem", global = true)
        public static void tradingUISendAddItem(final IsoPlayer isoPlayer, final IsoPlayer isoPlayer2, final InventoryItem inventoryItem) {
            GameClient.instance.tradingUISendAddItem(isoPlayer, isoPlayer2, inventoryItem);
        }
        
        @LuaMethod(name = "tradingUISendRemoveItem", global = true)
        public static void tradingUISendRemoveItem(final IsoPlayer isoPlayer, final IsoPlayer isoPlayer2, final int n) {
            GameClient.instance.tradingUISendRemoveItem(isoPlayer, isoPlayer2, n);
        }
        
        @LuaMethod(name = "tradingUISendUpdateState", global = true)
        public static void tradingUISendUpdateState(final IsoPlayer isoPlayer, final IsoPlayer isoPlayer2, final int n) {
            GameClient.instance.tradingUISendUpdateState(isoPlayer, isoPlayer2, n);
        }
        
        @LuaMethod(name = "querySteamWorkshopItemDetails", global = true)
        public static void querySteamWorkshopItemDetails(final ArrayList<String> list, final LuaClosure luaClosure, final Object o) {
            if (list == null || luaClosure == null) {
                throw new NullPointerException();
            }
            if (list.isEmpty()) {
                if (o == null) {
                    LuaManager.caller.pcall(LuaManager.thread, (Object)luaClosure, new Object[] { "Completed", new ArrayList() });
                }
                else {
                    LuaManager.caller.pcall(LuaManager.thread, (Object)luaClosure, new Object[] { o, "Completed", new ArrayList() });
                }
                return;
            }
            new ItemQuery(list, luaClosure, o);
        }
        
        @LuaMethod(name = "connectToServerStateCallback", global = true)
        public static void connectToServerStateCallback(final String s) {
            if (ConnectToServerState.instance != null) {
                ConnectToServerState.instance.FromLua(s);
            }
        }
        
        @LuaMethod(name = "getPublicServersList", global = true)
        public static KahluaTable getPublicServersList() {
            final KahluaTable table = LuaManager.platform.newTable();
            if (!SteamUtils.isSteamModeEnabled() && !PublicServerUtil.isEnabled()) {
                return table;
            }
            if (System.currentTimeMillis() - GlobalObject.timeLastRefresh < 60000L) {
                return table;
            }
            final ArrayList<Server> list = new ArrayList<Server>();
            try {
                if (getSteamModeActive()) {
                    ServerBrowser.RefreshInternetServers();
                    final List<GameServerDetails> getServerList = ServerBrowser.GetServerList();
                    for (final GameServerDetails gameServerDetails : getServerList) {
                        final Server server = new Server();
                        server.setName(gameServerDetails.name);
                        server.setDescription(gameServerDetails.gameDescription);
                        server.setSteamId(Long.toString(gameServerDetails.steamId));
                        server.setPing(Integer.toString(gameServerDetails.ping));
                        server.setPlayers(Integer.toString(gameServerDetails.numPlayers));
                        server.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
                        server.setOpen(true);
                        server.setIp(gameServerDetails.address);
                        server.setPort(Integer.toString(gameServerDetails.port));
                        server.setMods(gameServerDetails.tags);
                        server.setVersion(Core.getInstance().getVersionNumber());
                        server.setLastUpdate(1);
                        list.add(server);
                    }
                    System.out.printf("%d servers\n", getServerList.size());
                }
                else {
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PublicServerUtil.webSite)).openStream()));
                    final StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    bufferedReader.close();
                    final Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(sb.toString())));
                    parse.getDocumentElement().normalize();
                    final NodeList elementsByTagName = parse.getElementsByTagName("server");
                    for (int i = 0; i < elementsByTagName.getLength(); ++i) {
                        final Node item = elementsByTagName.item(i);
                        if (item.getNodeType() == 1) {
                            final Element element = (Element)item;
                            final Server server2 = new Server();
                            server2.setName(element.getElementsByTagName("name").item(0).getTextContent());
                            if (element.getElementsByTagName("desc").item(0) != null && !"".equals(element.getElementsByTagName("desc").item(0).getTextContent())) {
                                server2.setDescription(element.getElementsByTagName("desc").item(0).getTextContent());
                            }
                            server2.setIp(element.getElementsByTagName("ip").item(0).getTextContent());
                            server2.setPort(element.getElementsByTagName("port").item(0).getTextContent());
                            server2.setPlayers(element.getElementsByTagName("players").item(0).getTextContent());
                            server2.setMaxPlayers(element.getElementsByTagName("maxPlayers").item(0).getTextContent());
                            if (element.getElementsByTagName("version") != null && element.getElementsByTagName("version").item(0) != null) {
                                server2.setVersion(element.getElementsByTagName("version").item(0).getTextContent());
                            }
                            server2.setOpen(element.getElementsByTagName("open").item(0).getTextContent().equals("1"));
                            final Integer value = Integer.parseInt(element.getElementsByTagName("lastUpdate").item(0).getTextContent());
                            if (element.getElementsByTagName("mods").item(0) != null && !"".equals(element.getElementsByTagName("mods").item(0).getTextContent())) {
                                server2.setMods(element.getElementsByTagName("mods").item(0).getTextContent());
                            }
                            server2.setLastUpdate(new Double(Math.floor((double)((getTimestamp() - value) / 60L))).intValue());
                            final NodeList elementsByTagName2 = element.getElementsByTagName("password");
                            server2.setPasswordProtected(elementsByTagName2 != null && elementsByTagName2.getLength() != 0 && elementsByTagName2.item(0).getTextContent().equals("1"));
                            list.add(server2);
                        }
                    }
                }
                int n = 1;
                for (int j = 0; j < list.size(); ++j) {
                    table.rawset((Object)(double)n, (Object)list.get(j));
                    ++n;
                }
                GlobalObject.timeLastRefresh = Calendar.getInstance().getTimeInMillis();
                return table;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        @LuaMethod(name = "steamRequestInternetServersList", global = true)
        public static void steamRequestInternetServersList() {
            ServerBrowser.RefreshInternetServers();
        }
        
        @LuaMethod(name = "steamReleaseInternetServersRequest", global = true)
        public static void steamReleaseInternetServersRequest() {
            ServerBrowser.Release();
        }
        
        @LuaMethod(name = "steamGetInternetServersCount", global = true)
        public static int steamRequestInternetServersCount() {
            return ServerBrowser.GetServerCount();
        }
        
        @LuaMethod(name = "steamGetInternetServerDetails", global = true)
        public static Server steamGetInternetServerDetails(final int n) {
            if (!ServerBrowser.IsRefreshing()) {
                return null;
            }
            final GameServerDetails getServerDetails = ServerBrowser.GetServerDetails(n);
            if (getServerDetails == null) {
                return null;
            }
            if (getServerDetails.tags.contains("hidden") || getServerDetails.tags.contains("hosted")) {
                return null;
            }
            if (getServerDetails.tags.contains("hidden") || getServerDetails.tags.contains("hosted")) {
                return null;
            }
            final Server server = new Server();
            server.setName(getServerDetails.name);
            server.setDescription("");
            server.setSteamId(Long.toString(getServerDetails.steamId));
            server.setPing(Integer.toString(getServerDetails.ping));
            server.setPlayers(Integer.toString(getServerDetails.numPlayers));
            server.setMaxPlayers(Integer.toString(getServerDetails.maxPlayers));
            server.setOpen(true);
            server.setPublic(true);
            if (getServerDetails.tags.contains("hidden")) {
                server.setOpen(false);
                server.setPublic(false);
            }
            server.setIp(getServerDetails.address);
            server.setPort(Integer.toString(getServerDetails.port));
            server.setMods("");
            if (!getServerDetails.tags.replace("hidden", "").replace("hosted", "").replace(";", "").isEmpty()) {
                server.setMods(getServerDetails.tags.replace(";hosted", "").replace("hidden", ""));
            }
            server.setHosted(getServerDetails.tags.contains("hosted"));
            server.setVersion("");
            server.setLastUpdate(1);
            server.setPasswordProtected(getServerDetails.passwordProtected);
            return server;
        }
        
        @LuaMethod(name = "steamRequestServerRules", global = true)
        public static boolean steamRequestServerRules(final String s, final int n) {
            return ServerBrowser.RequestServerRules(s, n);
        }
        
        @LuaMethod(name = "steamRequestServerDetails", global = true)
        public static boolean steamRequestServerDetails(final String s, final int n) {
            return ServerBrowser.QueryServer(s, n);
        }
        
        @LuaMethod(name = "isPublicServerListAllowed", global = true)
        public static boolean isPublicServerListAllowed() {
            return SteamUtils.isSteamModeEnabled() || PublicServerUtil.isEnabled();
        }
        
        @LuaMethod(name = "is64bit", global = true)
        public static boolean is64bit() {
            return "64".equals(System.getProperty("sun.arch.data.model"));
        }
        
        @LuaMethod(name = "testSound", global = true)
        public static void testSound() {
            final float n = (float)Mouse.getX();
            final float n2 = (float)Mouse.getY();
            final int n3 = (int)IsoPlayer.getInstance().getZ();
            final AmbientStreamManager.Ambient e = new AmbientStreamManager.Ambient("Meta/House Alarm", (float)(int)IsoUtils.XToIso(n, n2, (float)n3), (float)(int)IsoUtils.YToIso(n, n2, (float)n3), 50.0f, 1.0f);
            e.trackMouse = true;
            ((AmbientStreamManager)AmbientStreamManager.instance).ambient.add(e);
        }
        
        @LuaMethod(name = "copyTable", global = true)
        public static KahluaTable copyTable(final KahluaTable kahluaTable) {
            return LuaManager.copyTable(kahluaTable);
        }
        
        @LuaMethod(name = "getUrlInputStream", global = true)
        public static DataInputStream getUrlInputStream(final String spec) {
            if (spec == null || (!spec.startsWith("https://") && !spec.startsWith("http://"))) {
                return null;
            }
            try {
                return new DataInputStream(new URL(spec).openStream());
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        @LuaMethod(name = "renderIsoCircle", global = true)
        public static void renderIsoCircle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9) {
            for (double n10 = 0.3490658503988659, n11 = 0.0; n11 < 6.283185307179586; n11 += n10) {
                final float n12 = n + n4 * (float)Math.cos(n11);
                final float n13 = n2 + n4 * (float)Math.sin(n11);
                final float n14 = n + n4 * (float)Math.cos(n11 + n10);
                final float n15 = n2 + n4 * (float)Math.sin(n11 + n10);
                LineDrawer.drawLine(IsoUtils.XToScreenExact(n12, n13, n3, 0), IsoUtils.YToScreenExact(n12, n13, n3, 0), IsoUtils.XToScreenExact(n14, n15, n3, 0), IsoUtils.YToScreenExact(n14, n15, n3, 0), n5, n6, n7, n8, n9);
            }
        }
        
        @LuaMethod(name = "configureLighting", global = true)
        public static void configureLighting(final float n) {
            if (LightingJNI.init) {
                LightingJNI.configure(n);
            }
        }
        
        @LuaMethod(name = "testHelicopter", global = true)
        public static void testHelicopter() {
            if (GameClient.bClient) {
                GameClient.SendCommandToServer("/chopper start");
            }
            else {
                IsoWorld.instance.helicopter.pickRandomTarget();
            }
        }
        
        @LuaMethod(name = "endHelicopter", global = true)
        public static void endHelicopter() {
            if (GameClient.bClient) {
                GameClient.SendCommandToServer("/chopper stop");
            }
            else {
                IsoWorld.instance.helicopter.deactivate();
            }
        }
        
        @LuaMethod(name = "getServerSettingsManager", global = true)
        public static ServerSettingsManager getServerSettingsManager() {
            return ServerSettingsManager.instance;
        }
        
        @LuaMethod(name = "rainConfig", global = true)
        public static void rainConfig(final String s, final int rainSpeed) {
            if ("alpha".equals(s)) {
                IsoWorld.instance.CurrentCell.setRainAlpha(rainSpeed);
            }
            if ("intensity".equals(s)) {
                IsoWorld.instance.CurrentCell.setRainIntensity(rainSpeed);
            }
            if ("speed".equals(s)) {
                IsoWorld.instance.CurrentCell.setRainSpeed(rainSpeed);
            }
            if ("reloadTextures".equals(s)) {
                IsoWorld.instance.CurrentCell.reloadRainTextures();
            }
        }
        
        @LuaMethod(name = "getVehicleById", global = true)
        public static BaseVehicle getVehicleById(final int n) {
            if (GameServer.bServer) {
                return VehicleManager.instance.getVehicleByID((short)n);
            }
            return VehicleManager.instance.getVehicleByID((short)n);
        }
        
        @LuaMethod(name = "addBloodSplat", global = true)
        public void addBloodSplat(final IsoGridSquare isoGridSquare, final int n) {
            for (int i = 0; i < n; ++i) {
                isoGridSquare.getChunk().addBloodSplat(isoGridSquare.x + Rand.Next(-0.5f, 0.5f), isoGridSquare.y + Rand.Next(-0.5f, 0.5f), (float)isoGridSquare.z, Rand.Next(8));
            }
        }
        
        @LuaMethod(name = "addCarCrash", global = true)
        public static void addCarCrash() {
            final IsoGridSquare currentSquare = IsoPlayer.getInstance().getCurrentSquare();
            if (currentSquare == null) {
                return;
            }
            final IsoChunk chunk = currentSquare.getChunk();
            if (chunk == null) {
                return;
            }
            final IsoMetaGrid.Zone zone = currentSquare.getZone();
            if (zone == null) {
                return;
            }
            if (!chunk.canAddRandomCarCrash(zone, true)) {
                return;
            }
            currentSquare.chunk.addRandomCarCrash(zone, true);
        }
        
        @LuaMethod(name = "createRandomDeadBody", global = true)
        public static IsoDeadBody createRandomDeadBody(final IsoGridSquare isoGridSquare, final int n) {
            if (isoGridSquare == null) {
                return null;
            }
            final ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)"all");
            final RandomizedBuildingBase.HumanCorpse humanCorpse = new RandomizedBuildingBase.HumanCorpse(IsoWorld.instance.getCell(), (float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z);
            humanCorpse.setDir(IsoDirections.getRandom());
            humanCorpse.setDescriptor(SurvivorFactory.CreateSurvivor());
            humanCorpse.setFemale(humanCorpse.getDescriptor().isFemale());
            humanCorpse.initWornItems("Human");
            humanCorpse.initAttachedItems("Human");
            humanCorpse.dressInNamedOutfit(humanCorpse.getRandomDefaultOutfit().m_Name);
            humanCorpse.initSpritePartsEmpty();
            humanCorpse.Dressup(humanCorpse.getDescriptor());
            for (int i = 0; i < n; ++i) {
                humanCorpse.addBlood(null, false, true, false);
            }
            final IsoDeadBody isoDeadBody = new IsoDeadBody(humanCorpse, true);
            ItemPickerJava.fillContainerType(itemPickerRoom, isoDeadBody.getContainer(), humanCorpse.isFemale() ? "inventoryfemale" : "inventorymale", null);
            return isoDeadBody;
        }
        
        @LuaMethod(name = "addZombieSitting", global = true)
        public void addZombieSitting(final int n, final int n2, final int n3) {
            final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(n, n2, n3);
            if (gridSquare == null) {
                return;
            }
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(gridSquare);
            final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
            realZombieAlways.bDressInRandomOutfit = true;
            ZombiePopulationManager.instance.sitAgainstWall(realZombieAlways, gridSquare);
        }
        
        @LuaMethod(name = "addZombiesEating", global = true)
        public void addZombiesEating(final int n, final int n2, final int n3, final int n4, final boolean skeleton) {
            final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(n, n2, n3);
            if (gridSquare == null) {
                return;
            }
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(gridSquare);
            final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
            realZombieAlways.setX((float)gridSquare.x);
            realZombieAlways.setY((float)gridSquare.y);
            realZombieAlways.setFakeDead(false);
            realZombieAlways.setHealth(0.0f);
            realZombieAlways.upKillCount = false;
            if (!skeleton) {
                realZombieAlways.dressInRandomOutfit();
                for (int i = 0; i < 10; ++i) {
                    realZombieAlways.addHole(null);
                    realZombieAlways.addBlood(null, false, true, false);
                }
                realZombieAlways.DoZombieInventory();
            }
            realZombieAlways.setSkeleton(skeleton);
            if (skeleton) {
                realZombieAlways.getHumanVisual().setSkinTextureIndex(2);
            }
            VirtualZombieManager.instance.createEatingZombies(new IsoDeadBody(realZombieAlways, true), n4);
        }
        
        @LuaMethod(name = "addZombiesInOutfitArea", global = true)
        public ArrayList<IsoZombie> addZombiesInOutfitArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final String s, final Integer n7) {
            final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
            for (int i = 0; i < n6; ++i) {
                list.addAll(addZombiesInOutfit(Rand.Next(n, n3), Rand.Next(n2, n4), n5, 1, s, n7));
            }
            return list;
        }
        
        @LuaMethod(name = "addZombiesInOutfit", global = true)
        public static ArrayList<IsoZombie> addZombiesInOutfit(final int n, final int n2, final int n3, final int n4, final String s, final Integer n5) {
            return addZombiesInOutfit(n, n2, n3, n4, s, n5, false, false, false, false, 1.0f);
        }
        
        @LuaMethod(name = "addZombiesInOutfit", global = true)
        public static ArrayList<IsoZombie> addZombiesInOutfit(final int n, final int n2, final int n3, final int n4, final String s, final Integer n5, final boolean b, final boolean fallOnFront, final boolean fakeDead, final boolean knockedDown, final float health) {
            final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
            if (IsoWorld.getZombiesDisabled()) {
                return list;
            }
            final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(n, n2, n3);
            if (gridSquare == null) {
                return list;
            }
            for (int i = 0; i < n4; ++i) {
                if (health <= 0.0f) {
                    gridSquare.getChunk().AddCorpses(n / 10, n2 / 10);
                }
                else {
                    VirtualZombieManager.instance.choices.clear();
                    VirtualZombieManager.instance.choices.add(gridSquare);
                    final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
                    if (realZombieAlways != null) {
                        if (n5 != null) {
                            realZombieAlways.setFemaleEtc(Rand.Next(100) < n5);
                        }
                        if (s != null) {
                            realZombieAlways.dressInPersistentOutfit(s);
                            realZombieAlways.bDressInRandomOutfit = false;
                        }
                        else {
                            realZombieAlways.bDressInRandomOutfit = true;
                        }
                        realZombieAlways.bLunger = true;
                        realZombieAlways.setKnockedDown(knockedDown);
                        if (b) {
                            realZombieAlways.setCrawler(true);
                            realZombieAlways.setCanWalk(false);
                            realZombieAlways.setOnFloor(true);
                            realZombieAlways.setKnockedDown(true);
                            realZombieAlways.setCrawlerType(1);
                            realZombieAlways.DoZombieStats();
                        }
                        realZombieAlways.setFakeDead(fakeDead);
                        realZombieAlways.setFallOnFront(fallOnFront);
                        realZombieAlways.setHealth(health);
                        list.add(realZombieAlways);
                    }
                }
            }
            ZombieSpawnRecorder.instance.record(list, GlobalObject.class.getSimpleName());
            return list;
        }
        
        @LuaMethod(name = "addZombiesInBuilding", global = true)
        public ArrayList<IsoZombie> addZombiesInBuilding(final BuildingDef buildingDef, final int n, final String s, RoomDef roomDef, final Integer n2) {
            final boolean b = roomDef == null;
            final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
            if (IsoWorld.getZombiesDisabled()) {
                return list;
            }
            if (roomDef == null) {
                roomDef = buildingDef.getRandomRoom(6);
            }
            int n3 = 2;
            int n4 = roomDef.area / 2;
            if (n == 0) {
                if (SandboxOptions.instance.Zombies.getValue() == 1) {
                    n4 += 4;
                }
                else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                    n4 += 3;
                }
                else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                    n4 += 2;
                }
                else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                    n4 -= 4;
                }
                if (n4 > 8) {
                    n4 = 8;
                }
                if (n4 < n3) {
                    n4 = n3 + 1;
                }
            }
            else {
                n3 = n;
                n4 = n;
            }
            for (int next = Rand.Next(n3, n4), i = 0; i < next; ++i) {
                final IsoGridSquare randomSpawnSquare = RandomizedWorldBase.getRandomSpawnSquare(roomDef);
                if (randomSpawnSquare == null) {
                    break;
                }
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(randomSpawnSquare);
                final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
                if (realZombieAlways != null) {
                    if (n2 != null) {
                        realZombieAlways.setFemaleEtc(Rand.Next(100) < n2);
                    }
                    if (s != null) {
                        realZombieAlways.dressInPersistentOutfit(s);
                        realZombieAlways.bDressInRandomOutfit = false;
                    }
                    else {
                        realZombieAlways.bDressInRandomOutfit = true;
                    }
                    list.add(realZombieAlways);
                    if (b) {
                        roomDef = buildingDef.getRandomRoom(6);
                    }
                }
            }
            ZombieSpawnRecorder.instance.record(list, this.getClass().getSimpleName());
            return list;
        }
        
        @LuaMethod(name = "addVehicleDebug", global = true)
        public static BaseVehicle addVehicleDebug(final String scriptName, IsoDirections random, final Integer n, final IsoGridSquare square) {
            if (random == null) {
                random = IsoDirections.getRandom();
            }
            final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
            if (!StringUtils.isNullOrEmpty(scriptName)) {
                e.setScriptName(scriptName);
                e.setScript();
                if (n != null) {
                    e.setSkinIndex(n);
                }
            }
            e.setDir(random);
            float n2;
            for (n2 = random.toAngle() + 3.1415927f + Rand.Next(-0.2f, 0.2f); n2 > 6.283185307179586; n2 -= (float)6.283185307179586) {}
            e.savedRot.setAngleAxis(n2, 0.0f, 1.0f, 0.0f);
            e.jniTransform.setRotation(e.savedRot);
            e.setX((float)square.x);
            e.setY((float)square.y);
            e.setZ((float)square.z);
            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
                e.setSquare(square);
                square.chunk.vehicles.add(e);
                e.chunk = square.chunk;
                e.addToWorld();
                VehiclesDB2.instance.addVehicle(e);
            }
            e.setGeneralPartCondition(1.3f, 10.0f);
            e.rust = 0.0f;
            return e;
        }
        
        @LuaMethod(name = "addVehicle", global = true)
        public static BaseVehicle addVehicle(String fullName) {
            if (!StringUtils.isNullOrWhitespace(fullName) && ScriptManager.instance.getVehicle(fullName) == null) {
                DebugLog.Lua.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, fullName));
                return null;
            }
            final ArrayList<VehicleScript> allVehicleScripts = ScriptManager.instance.getAllVehicleScripts();
            if (allVehicleScripts.isEmpty()) {
                DebugLog.Lua.warn((Object)"No vehicle scripts defined");
                return null;
            }
            WorldSimulation.instance.create();
            final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
            if (StringUtils.isNullOrWhitespace(fullName)) {
                fullName = PZArrayUtil.pickRandom(allVehicleScripts).getFullName();
            }
            e.setScriptName(fullName);
            e.setX(IsoPlayer.getInstance().getX());
            e.setY(IsoPlayer.getInstance().getY());
            e.setZ(0.0f);
            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
                e.setSquare(IsoPlayer.getInstance().getSquare());
                e.square.chunk.vehicles.add(e);
                e.chunk = e.square.chunk;
                e.addToWorld();
                VehiclesDB2.instance.addVehicle(e);
            }
            else {
                DebugLog.Lua.error((Object)"ERROR: I can not spawn the vehicle. Invalid position. Try to change position.");
            }
            return null;
        }
        
        @LuaMethod(name = "attachTrailerToPlayerVehicle", global = true)
        public static void attachTrailerToPlayerVehicle(final int n) {
            final IsoPlayer isoPlayer = IsoPlayer.players[n];
            final IsoGridSquare currentSquare = isoPlayer.getCurrentSquare();
            BaseVehicle baseVehicle = isoPlayer.getVehicle();
            if (baseVehicle == null) {
                baseVehicle = addVehicleDebug("Base.OffRoad", IsoDirections.N, 0, currentSquare);
                baseVehicle.repair();
                isoPlayer.getInventory().AddItem(baseVehicle.createVehicleKey());
            }
            final BaseVehicle addVehicleDebug = addVehicleDebug("Base.Trailer", IsoDirections.N, 0, IsoWorld.instance.CurrentCell.getGridSquare(currentSquare.x, currentSquare.y + 5, currentSquare.z));
            addVehicleDebug.repair();
            baseVehicle.addPointConstraint(addVehicleDebug, "trailer", "trailer");
        }
        
        @LuaMethod(name = "getKeyName", global = true)
        public static String getKeyName(final int n) {
            return Input.getKeyName(n);
        }
        
        @LuaMethod(name = "getKeyCode", global = true)
        public static int getKeyCode(final String s) {
            return Input.getKeyCode(s);
        }
        
        @LuaMethod(name = "addAllVehicles", global = true)
        public static void addAllVehicles() {
            addAllVehicles(vehicleScript -> !vehicleScript.getName().contains("Smashed") && !vehicleScript.getName().contains("Burnt"));
        }
        
        @LuaMethod(name = "addAllBurntVehicles", global = true)
        public static void addAllBurntVehicles() {
            addAllVehicles(vehicleScript -> vehicleScript.getName().contains("Burnt"));
        }
        
        @LuaMethod(name = "addAllSmashedVehicles", global = true)
        public static void addAllSmashedVehicles() {
            addAllVehicles(vehicleScript -> vehicleScript.getName().contains("Smashed"));
        }
        
        public static void addAllVehicles(final Predicate<VehicleScript> predicate) {
            final ArrayList<VehicleScript> allVehicleScripts = ScriptManager.instance.getAllVehicleScripts();
            Collections.sort((List<Object>)allVehicleScripts, Comparator.comparing((Function<? super Object, ? extends Comparable>)VehicleScript::getName));
            float x = (float)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5);
            float y = IsoPlayer.getInstance().getY();
            final float z = 0.0f;
            for (int i = 0; i < allVehicleScripts.size(); ++i) {
                final VehicleScript vehicleScript = allVehicleScripts.get(i);
                if (vehicleScript.getModel() != null) {
                    if (predicate.test(vehicleScript)) {
                        if (IsoWorld.instance.CurrentCell.getGridSquare(x, y, z) != null) {
                            WorldSimulation.instance.create();
                            final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                            e.setScriptName(vehicleScript.getFullName());
                            e.setX(x);
                            e.setY(y);
                            e.setZ(z);
                            if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
                                e.setSquare(IsoPlayer.getInstance().getSquare());
                                e.square.chunk.vehicles.add(e);
                                e.chunk = e.square.chunk;
                                e.addToWorld();
                                VehiclesDB2.instance.addVehicle(e);
                                IsoChunk.addFromCheckedVehicles(e);
                            }
                            else {
                                DebugLog.Lua.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, vehicleScript.getName()));
                            }
                            x += 4.0f;
                            if (x > IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles() - 5) {
                                x = (float)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles() + 5);
                                y += 8.0f;
                            }
                        }
                    }
                }
            }
        }
        
        @LuaMethod(name = "addPhysicsObject", global = true)
        public static BaseVehicle addPhysicsObject() {
            MPStatistic.getInstance().Bullet.Start();
            final int addPhysicsObject = Bullet.addPhysicsObject(getPlayer().getX(), getPlayer().getY());
            MPStatistic.getInstance().Bullet.End();
            WorldSimulation.instance.physicsObjectMap.put(addPhysicsObject, new IsoPushableObject(IsoWorld.instance.getCell(), IsoPlayer.getInstance().getCurrentSquare(), IsoSpriteManager.instance.getSprite("trashcontainers_01_16")));
            return null;
        }
        
        @LuaMethod(name = "toggleVehicleRenderToTexture", global = true)
        public static void toggleVehicleRenderToTexture() {
            BaseVehicle.RENDER_TO_TEXTURE = !BaseVehicle.RENDER_TO_TEXTURE;
        }
        
        @LuaMethod(name = "reloadSoundFiles", global = true)
        public static void reloadSoundFiles() {
            try {
                for (final String s : ZomboidFileSystem.instance.ActiveFileMap.keySet()) {
                    if (s.matches(".*/sounds_.+\\.txt")) {
                        GameSounds.ReloadFile(s);
                    }
                }
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
        
        @LuaMethod(name = "getAnimationViewerState", global = true)
        public static AnimationViewerState getAnimationViewerState() {
            return AnimationViewerState.instance;
        }
        
        @LuaMethod(name = "getAttachmentEditorState", global = true)
        public static AttachmentEditorState getAttachmentEditorState() {
            return AttachmentEditorState.instance;
        }
        
        @LuaMethod(name = "getEditVehicleState", global = true)
        public static EditVehicleState getEditVehicleState() {
            return EditVehicleState.instance;
        }
        
        @LuaMethod(name = "showAnimationViewer", global = true)
        public static void showAnimationViewer() {
            IngameState.instance.showAnimationViewer = true;
        }
        
        @LuaMethod(name = "showAttachmentEditor", global = true)
        public static void showAttachmentEditor() {
            IngameState.instance.showAttachmentEditor = true;
        }
        
        @LuaMethod(name = "showChunkDebugger", global = true)
        public static void showChunkDebugger() {
            IngameState.instance.showChunkDebugger = true;
        }
        
        @LuaMethod(name = "showGlobalObjectDebugger", global = true)
        public static void showGlobalObjectDebugger() {
            IngameState.instance.showGlobalObjectDebugger = true;
        }
        
        @LuaMethod(name = "showVehicleEditor", global = true)
        public static void showVehicleEditor(final String s) {
            IngameState.instance.showVehicleEditor = (StringUtils.isNullOrWhitespace(s) ? "" : s);
        }
        
        @LuaMethod(name = "showWorldMapEditor", global = true)
        public static void showWorldMapEditor(final String s) {
            IngameState.instance.showWorldMapEditor = (StringUtils.isNullOrWhitespace(s) ? "" : s);
        }
        
        @LuaMethod(name = "reloadVehicles", global = true)
        public static void reloadVehicles() {
            try {
                final Iterator<String> iterator = ScriptManager.instance.scriptsWithVehicleTemplates.iterator();
                while (iterator.hasNext()) {
                    ScriptManager.instance.LoadFile(iterator.next(), true);
                }
                final Iterator<String> iterator2 = ScriptManager.instance.scriptsWithVehicles.iterator();
                while (iterator2.hasNext()) {
                    ScriptManager.instance.LoadFile(iterator2.next(), true);
                }
                BaseVehicle.LoadAllVehicleTextures();
                final Iterator<BaseVehicle> iterator3 = IsoWorld.instance.CurrentCell.vehicles.iterator();
                while (iterator3.hasNext()) {
                    iterator3.next().scriptReloaded();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        @LuaMethod(name = "reloadEngineRPM", global = true)
        public static void reloadEngineRPM() {
            try {
                ScriptManager.instance.LoadFile(ZomboidFileSystem.instance.getString("media/scripts/vehicles/engine_rpm.txt"), true);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        @LuaMethod(name = "proceedPM", global = true)
        public static String proceedPM(String trim) {
            trim = trim.trim();
            final Matcher matcher = Pattern.compile("(\"[^\"]*\\s+[^\"]*\"|[^\"]\\S*)\\s(.+)").matcher(trim);
            if (matcher.matches()) {
                final String group = matcher.group(1);
                final Thread thread = new Thread(ThreadGroups.Workers, () -> ChatManager.getInstance().sendWhisperMessage(group.replaceAll("\"", ""), matcher.group(2)));
                thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
                thread.start();
                return group;
            }
            ChatManager.getInstance().addMessage("Error", getText("IGUI_Commands_Whisper"));
            return "";
        }
        
        @LuaMethod(name = "processSayMessage", global = true)
        public static void processSayMessage(String trim) {
            if (trim == null || trim.isEmpty()) {
                return;
            }
            trim = trim.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.say, trim);
        }
        
        @LuaMethod(name = "processGeneralMessage", global = true)
        public static void processGeneralMessage(String trim) {
            if (trim == null || trim.isEmpty()) {
                return;
            }
            trim = trim.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.general, trim);
        }
        
        @LuaMethod(name = "processShoutMessage", global = true)
        public static void processShoutMessage(String trim) {
            if (trim == null || trim.isEmpty()) {
                return;
            }
            trim = trim.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.shout, trim);
        }
        
        @LuaMethod(name = "proceedFactionMessage", global = true)
        public static void ProceedFactionMessage(String trim) {
            if (trim == null || trim.isEmpty()) {
                return;
            }
            trim = trim.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.faction, trim);
        }
        
        @LuaMethod(name = "processSafehouseMessage", global = true)
        public static void ProcessSafehouseMessage(String trim) {
            if (trim == null || trim.isEmpty()) {
                return;
            }
            trim = trim.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.safehouse, trim);
        }
        
        @LuaMethod(name = "processAdminChatMessage", global = true)
        public static void ProcessAdminChatMessage(String trim) {
            if (trim == null || trim.isEmpty()) {
                return;
            }
            trim = trim.trim();
            ChatManager.getInstance().sendMessageToChat(ChatType.admin, trim);
        }
        
        @LuaMethod(name = "showWrongChatTabMessage", global = true)
        public static void showWrongChatTabMessage(final int n, final int n2, final String s) {
            ChatManager.getInstance().showServerChatMessage(Translator.getText("UI_chat_wrong_tab", ChatManager.getInstance().getTabName((short)n), ChatManager.getInstance().getTabName((short)n2), s));
        }
        
        @LuaMethod(name = "focusOnTab", global = true)
        public static void focusOnTab(final Short n) {
            ChatManager.getInstance().focusOnTab(n);
        }
        
        @LuaMethod(name = "updateChatSettings", global = true)
        public static void updateChatSettings(final String s, final boolean b, final boolean b2) {
            ChatManager.getInstance().updateChatSettings(s, b, b2);
        }
        
        @LuaMethod(name = "checkPlayerCanUseChat", global = true)
        public static Boolean checkPlayerCanUseChat(String s) {
            final String trim;
            s = (trim = s.trim());
            ChatType chatType = null;
            switch (trim) {
                case "/all": {
                    chatType = ChatType.general;
                    break;
                }
                case "/a":
                case "/admin": {
                    chatType = ChatType.admin;
                    break;
                }
                case "/s":
                case "/say": {
                    chatType = ChatType.say;
                    break;
                }
                case "/y":
                case "/yell": {
                    chatType = ChatType.shout;
                    break;
                }
                case "/f":
                case "/faction": {
                    chatType = ChatType.faction;
                    break;
                }
                case "/sh":
                case "/safehouse": {
                    chatType = ChatType.safehouse;
                    break;
                }
                case "/w":
                case "/whisper": {
                    chatType = ChatType.whisper;
                    break;
                }
                case "/radio":
                case "/r": {
                    chatType = ChatType.radio;
                    break;
                }
                default: {
                    chatType = ChatType.notDefined;
                    DebugLog.Lua.warn((Object)"Chat command not found");
                    break;
                }
            }
            return ChatManager.getInstance().isPlayerCanUseChat(chatType);
        }
        
        @LuaMethod(name = "reloadVehicleTextures", global = true)
        public static void reloadVehicleTextures(final String s) {
            final VehicleScript vehicle = ScriptManager.instance.getVehicle(s);
            if (vehicle == null) {
                DebugLog.Lua.warn((Object)"no such vehicle script");
                return;
            }
            for (int i = 0; i < vehicle.getSkinCount(); ++i) {
                final VehicleScript.Skin skin = vehicle.getSkin(i);
                if (skin.texture != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.texture));
                }
                if (skin.textureRust != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureRust));
                }
                if (skin.textureMask != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureMask));
                }
                if (skin.textureLights != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureLights));
                }
                if (skin.textureDamage1Overlay != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureDamage1Overlay));
                }
                if (skin.textureDamage1Shell != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureDamage1Shell));
                }
                if (skin.textureDamage2Overlay != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureDamage2Overlay));
                }
                if (skin.textureDamage2Shell != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureDamage2Shell));
                }
                if (skin.textureShadow != null) {
                    Texture.reload(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skin.textureShadow));
                }
            }
        }
        
        @LuaMethod(name = "useStaticErosionRand", global = true)
        public static void useStaticErosionRand(final boolean staticRand) {
            ErosionData.staticRand = staticRand;
        }
        
        @LuaMethod(name = "getClimateManager", global = true)
        public static ClimateManager getClimateManager() {
            return ClimateManager.getInstance();
        }
        
        @LuaMethod(name = "getClimateMoon", global = true)
        public static ClimateMoon getClimateMoon() {
            return ClimateMoon.getInstance();
        }
        
        @LuaMethod(name = "getWorldMarkers", global = true)
        public static WorldMarkers getWorldMarkers() {
            return WorldMarkers.instance;
        }
        
        @LuaMethod(name = "getIsoMarkers", global = true)
        public static IsoMarkers getIsoMarkers() {
            return IsoMarkers.instance;
        }
        
        @LuaMethod(name = "getErosion", global = true)
        public static ErosionMain getErosion() {
            return ErosionMain.getInstance();
        }
        
        @LuaMethod(name = "getAllOutfits", global = true)
        public static ArrayList<String> getAllOutfits(final boolean b) {
            final ArrayList<String> list = (ArrayList<String>)new ArrayList<Comparable>();
            ModelManager.instance.create();
            if (OutfitManager.instance == null) {
                return list;
            }
            final Iterator<Outfit> iterator = (b ? OutfitManager.instance.m_FemaleOutfits : OutfitManager.instance.m_MaleOutfits).iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next().m_Name);
            }
            Collections.sort((List<Comparable>)list);
            return list;
        }
        
        @LuaMethod(name = "getAllVehicles", global = true)
        public static ArrayList<String> getAllVehicles() {
            return ScriptManager.instance.getAllVehicleScripts().stream().filter(vehicleScript -> !vehicleScript.getName().contains("Smashed") && !vehicleScript.getName().contains("Burnt")).map((Function<? super Object, ?>)VehicleScript::getFullName).sorted().collect((Collector<? super Object, ?, ArrayList<String>>)Collectors.toCollection((Supplier<R>)ArrayList::new));
        }
        
        @LuaMethod(name = "getAllHairStyles", global = true)
        public static ArrayList<String> getAllHairStyles(final boolean b) {
            final ArrayList<String> list = new ArrayList<String>();
            if (HairStyles.instance == null) {
                return list;
            }
            final ArrayList<HairStyle> list2 = new ArrayList<HairStyle>(b ? HairStyles.instance.m_FemaleStyles : HairStyles.instance.m_MaleStyles);
            list2.sort((hairStyle, hairStyle2) -> {
                if (hairStyle.name.isEmpty()) {
                    return -1;
                }
                else if (hairStyle2.name.isEmpty()) {
                    return 1;
                }
                else {
                    return getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hairStyle.name)).compareTo(getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hairStyle2.name)));
                }
            });
            final Iterator<HairStyle> iterator = list2.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next().name);
            }
            return list;
        }
        
        @LuaMethod(name = "getHairStylesInstance", global = true)
        public static HairStyles getHairStylesInstance() {
            return HairStyles.instance;
        }
        
        @LuaMethod(name = "getBeardStylesInstance", global = true)
        public static BeardStyles getBeardStylesInstance() {
            return BeardStyles.instance;
        }
        
        @LuaMethod(name = "getAllBeardStyles", global = true)
        public static ArrayList<String> getAllBeardStyles() {
            final ArrayList<String> list = new ArrayList<String>();
            if (BeardStyles.instance == null) {
                return list;
            }
            final ArrayList<BeardStyle> list2 = new ArrayList<BeardStyle>(BeardStyles.instance.m_Styles);
            list2.sort((beardStyle, beardStyle2) -> {
                if (beardStyle.name.isEmpty()) {
                    return -1;
                }
                else if (beardStyle2.name.isEmpty()) {
                    return 1;
                }
                else {
                    return getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, beardStyle.name)).compareTo(getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, beardStyle2.name)));
                }
            });
            final Iterator<BeardStyle> iterator = list2.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next().name);
            }
            return list;
        }
        
        @LuaMethod(name = "getAllItemsForBodyLocation", global = true)
        public static KahluaTable getAllItemsForBodyLocation(final String s) {
            final KahluaTable table = LuaManager.platform.newTable();
            if (StringUtils.isNullOrWhitespace(s)) {
                return table;
            }
            int n = 1;
            for (final Item item : ScriptManager.instance.getAllItems()) {
                if (StringUtils.isNullOrWhitespace(item.getClothingItem())) {
                    continue;
                }
                if (!s.equals(item.getBodyLocation()) && !s.equals(item.CanBeEquipped)) {
                    continue;
                }
                table.rawset(n++, (Object)item.getFullName());
            }
            return table;
        }
        
        @LuaMethod(name = "getAllDecalNamesForItem", global = true)
        public static ArrayList<String> getAllDecalNamesForItem(final InventoryItem inventoryItem) {
            final ArrayList<String> list = new ArrayList<String>();
            if (inventoryItem == null || ClothingDecals.instance == null) {
                return list;
            }
            final ClothingItem clothingItem = inventoryItem.getClothingItem();
            if (clothingItem == null) {
                return list;
            }
            final String decalGroup = clothingItem.getDecalGroup();
            if (StringUtils.isNullOrWhitespace(decalGroup)) {
                return list;
            }
            final ClothingDecalGroup findGroup = ClothingDecals.instance.FindGroup(decalGroup);
            if (findGroup == null) {
                return list;
            }
            findGroup.getDecals(list);
            return list;
        }
        
        @LuaMethod(name = "screenZoomIn", global = true)
        public void screenZoomIn() {
        }
        
        @LuaMethod(name = "screenZoomOut", global = true)
        public void screenZoomOut() {
        }
        
        @LuaMethod(name = "addSound", global = true)
        public void addSound(final IsoObject isoObject, final int n, final int n2, final int n3, final int n4, final int n5) {
            WorldSoundManager.instance.addSound(isoObject, n, n2, n3, n4, n5);
        }
        
        @LuaMethod(name = "sendAddXp", global = true)
        public void sendAddXp(final IsoPlayer isoPlayer, final PerkFactory.Perk perk, final int n, final boolean b, final boolean b2) {
            if (GameClient.bClient && isoPlayer.isExistInTheWorld()) {
                GameClient.instance.sendAddXpFromPlayerStatsUI(isoPlayer, perk, n, b, b2);
            }
        }
        
        @LuaMethod(name = "SyncXp", global = true)
        public void SyncXp(final IsoPlayer isoPlayer) {
            if (GameClient.bClient) {
                GameClient.instance.sendSyncXp(isoPlayer);
            }
        }
        
        @LuaMethod(name = "checkServerName", global = true)
        public String checkServerName(final String s) {
            final String validateString = ProfanityFilter.getInstance().validateString(s, true, true, true);
            if (!StringUtils.isNullOrEmpty(validateString)) {
                return Translator.getText("UI_BadWordCheck", validateString);
            }
            return null;
        }
        
        @LuaMethod(name = "Render3DItem", global = true)
        public void Render3DItem(final InventoryItem inventoryItem, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final float n4) {
            WorldItemModelDrawer.renderMain(inventoryItem, isoGridSquare, n, n2, n3, 0.0f, n4);
        }
        
        @LuaMethod(name = "getContainerOverlays", global = true)
        public ContainerOverlays getContainerOverlays() {
            return ContainerOverlays.instance;
        }
        
        @LuaMethod(name = "getTileOverlays", global = true)
        public TileOverlays getTileOverlays() {
            return TileOverlays.instance;
        }
        
        @LuaMethod(name = "getAverageFPS", global = true)
        public Double getAverageFSP() {
            float a = GameWindow.averageFPS;
            if (!PerformanceSettings.isUncappedFPS()) {
                a = Math.min(a, (float)PerformanceSettings.getLockFPS());
            }
            return BoxedStaticValues.toDouble(Math.floor(a));
        }
        
        @LuaMethod(name = "getServerStatistic", global = true)
        public static KahluaTable getServerStatistic() {
            return MPStatistic.getInstance().getStatisticTableForLua();
        }
        
        @LuaMethod(name = "setServerStatisticEnable", global = true)
        public static void setServerStatisticEnable(final boolean serverStatisticEnable) {
            if (GameClient.bClient) {
                GameClient.setServerStatisticEnable(serverStatisticEnable);
            }
        }
        
        @LuaMethod(name = "getServerStatisticEnable", global = true)
        public static boolean getServerStatisticEnable() {
            return GameClient.bClient && GameClient.getServerStatisticEnable();
        }
        
        @LuaMethod(name = "getSearchMode", global = true)
        public static SearchMode getSearchMode() {
            return SearchMode.getInstance();
        }
        
        @LuaMethod(name = "timSort", global = true)
        public static void timSort(final KahluaTable kahluaTable, final Object comp) {
            final KahluaTableImpl kahluaTableImpl = Type.tryCastTo(kahluaTable, KahluaTableImpl.class);
            if (kahluaTableImpl == null || kahluaTableImpl.len() < 2 || comp == null) {
                return;
            }
            GlobalObject.timSortComparator.comp = comp;
            final Object[] array = kahluaTableImpl.delegate.values().toArray();
            Arrays.sort(array, GlobalObject.timSortComparator);
            for (int i = 0; i < array.length; ++i) {
                kahluaTableImpl.rawset(i + 1, array[i]);
                array[i] = null;
            }
        }
        
        static {
            GlobalObject.inFileReader = null;
            GlobalObject.inBufferedReader = null;
            GlobalObject.timeLastRefresh = 0L;
            timSortComparator = new TimSortComparator();
        }
        
        private static final class TimSortComparator implements Comparator<Object>
        {
            Object comp;
            
            @Override
            public int compare(final Object a, final Object b) {
                if (Objects.equals(a, b)) {
                    return 0;
                }
                return (LuaManager.thread.pcallBoolean(this.comp, a, b) == Boolean.TRUE) ? -1 : 1;
            }
        }
        
        public static final class LuaFileWriter
        {
            private final PrintWriter writer;
            
            public LuaFileWriter(final PrintWriter writer) {
                this.writer = writer;
            }
            
            public void write(final String s) throws IOException {
                this.writer.write(s);
            }
            
            public void writeln(final String s) throws IOException {
                this.writer.write(s);
                this.writer.write(System.lineSeparator());
            }
            
            public void close() throws IOException {
                this.writer.close();
            }
        }
        
        private static final class ItemQuery implements ISteamWorkshopCallback
        {
            private LuaClosure functionObj;
            private Object arg1;
            private long handle;
            
            public ItemQuery(final ArrayList<String> list, final LuaClosure functionObj, final Object arg1) {
                this.functionObj = functionObj;
                this.arg1 = arg1;
                final long[] array = new long[list.size()];
                int n = 0;
                for (int i = 0; i < list.size(); ++i) {
                    final long convertStringToSteamID = SteamUtils.convertStringToSteamID(list.get(i));
                    if (convertStringToSteamID != -1L) {
                        array[n++] = convertStringToSteamID;
                    }
                }
                this.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(array, this);
                if (this.handle == 0L) {
                    SteamWorkshop.instance.RemoveCallback(this);
                    if (arg1 == null) {
                        LuaManager.caller.pcall(LuaManager.thread, (Object)functionObj, (Object)"NotCompleted");
                    }
                    else {
                        LuaManager.caller.pcall(LuaManager.thread, (Object)functionObj, new Object[] { arg1, "NotCompleted" });
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
                if (n != this.handle) {
                    return;
                }
                SteamWorkshop.instance.RemoveCallback(this);
                final ArrayList<SteamUGCDetails> list = new ArrayList<SteamUGCDetails>();
                for (int i = 0; i < n2; ++i) {
                    final SteamUGCDetails getQueryUGCResult = SteamWorkshop.instance.GetQueryUGCResult(n, i);
                    if (getQueryUGCResult != null) {
                        list.add(getQueryUGCResult);
                    }
                }
                SteamWorkshop.instance.ReleaseQueryUGCRequest(n);
                if (this.arg1 == null) {
                    LuaManager.caller.pcall(LuaManager.thread, (Object)this.functionObj, new Object[] { "Completed", list });
                }
                else {
                    LuaManager.caller.pcall(LuaManager.thread, (Object)this.functionObj, new Object[] { this.arg1, "Completed", list });
                }
            }
            
            @Override
            public void onItemQueryNotCompleted(final long n, final int n2) {
                if (n != this.handle) {
                    return;
                }
                SteamWorkshop.instance.RemoveCallback(this);
                SteamWorkshop.instance.ReleaseQueryUGCRequest(n);
                if (this.arg1 == null) {
                    LuaManager.caller.pcall(LuaManager.thread, (Object)this.functionObj, (Object)"NotCompleted");
                }
                else {
                    LuaManager.caller.pcall(LuaManager.thread, (Object)this.functionObj, new Object[] { this.arg1, "NotCompleted" });
                }
            }
        }
    }
}
