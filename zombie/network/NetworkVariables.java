// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

public class NetworkVariables
{
    public enum PredictionTypes
    {
        None, 
        Moving, 
        Static, 
        Thump, 
        Climb, 
        Lunge, 
        LungeHalf, 
        Walk, 
        WalkHalf, 
        PathFind;
        
        public static PredictionTypes fromByte(final byte b) {
            for (final PredictionTypes predictionTypes : values()) {
                if (predictionTypes.ordinal() == b) {
                    return predictionTypes;
                }
            }
            return PredictionTypes.None;
        }
        
        private static /* synthetic */ PredictionTypes[] $values() {
            return new PredictionTypes[] { PredictionTypes.None, PredictionTypes.Moving, PredictionTypes.Static, PredictionTypes.Thump, PredictionTypes.Climb, PredictionTypes.Lunge, PredictionTypes.LungeHalf, PredictionTypes.Walk, PredictionTypes.WalkHalf, PredictionTypes.PathFind };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public enum WalkType
    {
        WT1("1"), 
        WT2("2"), 
        WT3("3"), 
        WT4("4"), 
        WT5("5"), 
        WTSprint1("sprint1"), 
        WTSprint2("sprint2"), 
        WTSprint3("sprint3"), 
        WTSprint4("sprint4"), 
        WTSprint5("sprint5"), 
        WTSlow1("slow1"), 
        WTSlow2("slow2"), 
        WTSlow3("slow3");
        
        private final String walkType;
        
        private WalkType(final String walkType) {
            this.walkType = walkType;
        }
        
        @Override
        public String toString() {
            return this.walkType;
        }
        
        public static WalkType fromString(final String anotherString) {
            for (final WalkType walkType : values()) {
                if (walkType.walkType.equalsIgnoreCase(anotherString)) {
                    return walkType;
                }
            }
            return WalkType.WT1;
        }
        
        public static WalkType fromByte(final byte b) {
            for (final WalkType walkType : values()) {
                if (walkType.ordinal() == b) {
                    return walkType;
                }
            }
            return WalkType.WT1;
        }
        
        private static /* synthetic */ WalkType[] $values() {
            return new WalkType[] { WalkType.WT1, WalkType.WT2, WalkType.WT3, WalkType.WT4, WalkType.WT5, WalkType.WTSprint1, WalkType.WTSprint2, WalkType.WTSprint3, WalkType.WTSprint4, WalkType.WTSprint5, WalkType.WTSlow1, WalkType.WTSlow2, WalkType.WTSlow3 };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public enum ThumpType
    {
        TTNone(""), 
        TTDoor("Door"), 
        TTClaw("DoorClaw"), 
        TTBang("DoorBang");
        
        private final String thumpType;
        
        private ThumpType(final String thumpType) {
            this.thumpType = thumpType;
        }
        
        @Override
        public String toString() {
            return this.thumpType;
        }
        
        public static ThumpType fromString(final String anotherString) {
            for (final ThumpType thumpType : values()) {
                if (thumpType.thumpType.equalsIgnoreCase(anotherString)) {
                    return thumpType;
                }
            }
            return ThumpType.TTNone;
        }
        
        public static ThumpType fromByte(final Byte b) {
            for (final ThumpType thumpType : values()) {
                if (thumpType.ordinal() == b) {
                    return thumpType;
                }
            }
            return ThumpType.TTNone;
        }
        
        private static /* synthetic */ ThumpType[] $values() {
            return new ThumpType[] { ThumpType.TTNone, ThumpType.TTDoor, ThumpType.TTClaw, ThumpType.TTBang };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public enum ZombieState
    {
        Attack("attack"), 
        AttackNetwork("attack-network"), 
        AttackVehicle("attackvehicle"), 
        AttackVehicleNetwork("attackvehicle-network"), 
        Bumped("bumped"), 
        ClimbFence("climbfence"), 
        ClimbWindow("climbwindow"), 
        EatBody("eatbody"), 
        FaceTarget("face-target"), 
        FakeDead("fakedead"), 
        FakeDeadAttack("fakedead-attack"), 
        FakeDeadAttackNetwork("fakedead-attack-network"), 
        FallDown("falldown"), 
        Falling("falling"), 
        GetDown("getdown"), 
        Getup("getup"), 
        HitReaction("hitreaction"), 
        HitReactionHit("hitreaction-hit"), 
        HitWhileStaggered("hitwhilestaggered"), 
        Idle("idle"), 
        Lunge("lunge"), 
        LungeNetwork("lunge-network"), 
        OnGround("onground"), 
        PathFind("pathfind"), 
        Sitting("sitting"), 
        StaggerBack("staggerback"), 
        Thump("thump"), 
        TurnAlerted("turnalerted"), 
        WalkToward("walktoward"), 
        WalkTowardNetwork("walktoward-network"), 
        FakeZombieStay("fakezombie-stay"), 
        FakeZombieNormal("fakezombie-normal"), 
        FakeZombieAttack("fakezombie-attack");
        
        private final String zombieState;
        
        private ZombieState(final String zombieState) {
            this.zombieState = zombieState;
        }
        
        @Override
        public String toString() {
            return this.zombieState;
        }
        
        public static ZombieState fromString(final String anotherString) {
            for (final ZombieState zombieState : values()) {
                if (zombieState.zombieState.equalsIgnoreCase(anotherString)) {
                    return zombieState;
                }
            }
            return ZombieState.Idle;
        }
        
        public static ZombieState fromByte(final Byte b) {
            for (final ZombieState zombieState : values()) {
                if (zombieState.ordinal() == b) {
                    return zombieState;
                }
            }
            return ZombieState.Idle;
        }
        
        private static /* synthetic */ ZombieState[] $values() {
            return new ZombieState[] { ZombieState.Attack, ZombieState.AttackNetwork, ZombieState.AttackVehicle, ZombieState.AttackVehicleNetwork, ZombieState.Bumped, ZombieState.ClimbFence, ZombieState.ClimbWindow, ZombieState.EatBody, ZombieState.FaceTarget, ZombieState.FakeDead, ZombieState.FakeDeadAttack, ZombieState.FakeDeadAttackNetwork, ZombieState.FallDown, ZombieState.Falling, ZombieState.GetDown, ZombieState.Getup, ZombieState.HitReaction, ZombieState.HitReactionHit, ZombieState.HitWhileStaggered, ZombieState.Idle, ZombieState.Lunge, ZombieState.LungeNetwork, ZombieState.OnGround, ZombieState.PathFind, ZombieState.Sitting, ZombieState.StaggerBack, ZombieState.Thump, ZombieState.TurnAlerted, ZombieState.WalkToward, ZombieState.WalkTowardNetwork, ZombieState.FakeZombieStay, ZombieState.FakeZombieNormal, ZombieState.FakeZombieAttack };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
