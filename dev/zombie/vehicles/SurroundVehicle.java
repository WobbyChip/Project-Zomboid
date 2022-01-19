// 
// Decompiled by Procyon v0.5.36
// 

package zombie.vehicles;

import zombie.iso.IsoMovingObject;
import zombie.VirtualZombieManager;
import zombie.util.Type;
import zombie.characters.IsoGameCharacter;
import org.joml.Vector2f;
import zombie.iso.IsoUtils;
import zombie.characters.IsoZombie;
import zombie.scripting.objects.VehicleScript;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import org.joml.Vector3f;
import zombie.popman.ObjectPool;

public final class SurroundVehicle
{
    private static final ObjectPool<Position> s_positionPool;
    private static final Vector3f s_tempVector3f;
    private final BaseVehicle m_vehicle;
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public float x3;
    public float y3;
    public float x4;
    public float y4;
    private float x1p;
    private float y1p;
    private float x2p;
    private float y2p;
    private float x3p;
    private float y3p;
    private float x4p;
    private float y4p;
    private boolean m_bMoved;
    private final ArrayList<Position> m_positions;
    private long m_updateMS;
    
    public SurroundVehicle(final BaseVehicle baseVehicle) {
        this.m_bMoved = false;
        this.m_positions = new ArrayList<Position>();
        this.m_updateMS = 0L;
        Objects.requireNonNull(baseVehicle);
        this.m_vehicle = baseVehicle;
    }
    
    private void calcPositionsLocal() {
        SurroundVehicle.s_positionPool.release(this.m_positions);
        this.m_positions.clear();
        final VehicleScript script = this.m_vehicle.getScript();
        if (script == null) {
            return;
        }
        final Vector3f extents = script.getExtents();
        final Vector3f centerOfMassOffset = script.getCenterOfMassOffset();
        final float x = extents.x;
        final float z = extents.z;
        final float n = BaseVehicle.PLUS_RADIUS + 0.005f;
        final float n2 = centerOfMassOffset.x - x / 2.0f - n;
        final float n3 = centerOfMassOffset.z - z / 2.0f - n;
        final float n4 = centerOfMassOffset.x + x / 2.0f + n;
        final float n5 = centerOfMassOffset.z + z / 2.0f + n;
        this.addPositions(n2, centerOfMassOffset.z - z / 2.0f, n2, centerOfMassOffset.z + z / 2.0f, PositionSide.Right);
        this.addPositions(n4, centerOfMassOffset.z - z / 2.0f, n4, centerOfMassOffset.z + z / 2.0f, PositionSide.Left);
        this.addPositions(n2, n3, n4, n3, PositionSide.Rear);
        this.addPositions(n2, n5, n4, n5, PositionSide.Front);
    }
    
    private void addPositions(final float n, final float n2, final float n3, final float n4, final PositionSide positionSide) {
        final Vector3f passengerLocalPos = this.m_vehicle.getPassengerLocalPos(0, SurroundVehicle.s_tempVector3f);
        if (passengerLocalPos == null) {
            return;
        }
        final float n5 = 0.3f;
        if (positionSide == PositionSide.Left || positionSide == PositionSide.Right) {
            float z;
            float n6;
            for (n6 = (z = passengerLocalPos.z); z >= n2 + n5; z -= n5 * 2.0f) {
                this.addPosition(n, z, positionSide);
            }
            for (float n7 = n6 + n5 * 2.0f; n7 < n4 - n5; n7 += n5 * 2.0f) {
                this.addPosition(n, n7, positionSide);
            }
        }
        else {
            float n9;
            float n8;
            for (n8 = (n9 = 0.0f); n9 >= n + n5; n9 -= n5 * 2.0f) {
                this.addPosition(n9, n2, positionSide);
            }
            for (float n10 = n8 + n5 * 2.0f; n10 < n3 - n5; n10 += n5 * 2.0f) {
                this.addPosition(n10, n2, positionSide);
            }
        }
    }
    
    private Position addPosition(final float n, final float n2, final PositionSide side) {
        final Position e = SurroundVehicle.s_positionPool.alloc();
        e.posLocal.set(n, n2);
        e.side = side;
        this.m_positions.add(e);
        return e;
    }
    
    private void calcPositionsWorld() {
        for (int i = 0; i < this.m_positions.size(); ++i) {
            final Position position = this.m_positions.get(i);
            this.m_vehicle.getWorldPos(position.posLocal.x, 0.0f, position.posLocal.y, position.posWorld);
            switch (position.side) {
                case Front:
                case Rear: {
                    this.m_vehicle.getWorldPos(position.posLocal.x, 0.0f, 0.0f, position.posAxis);
                    break;
                }
                case Left:
                case Right: {
                    this.m_vehicle.getWorldPos(0.0f, 0.0f, position.posLocal.y, position.posAxis);
                    break;
                }
            }
        }
        final PolygonalMap2.VehiclePoly poly = this.m_vehicle.getPoly();
        this.x1p = poly.x1;
        this.x2p = poly.x2;
        this.x3p = poly.x3;
        this.x4p = poly.x4;
        this.y1p = poly.y1;
        this.y2p = poly.y2;
        this.y3p = poly.y3;
        this.y4p = poly.y4;
    }
    
    private Position getClosestPositionFor(final IsoZombie isoZombie) {
        if (isoZombie == null || isoZombie.getTarget() == null) {
            return null;
        }
        float n = Float.MAX_VALUE;
        Position position = null;
        for (int i = 0; i < this.m_positions.size(); ++i) {
            final Position position2 = this.m_positions.get(i);
            if (!position2.bBlocked) {
                final float distanceToSquared = IsoUtils.DistanceToSquared(isoZombie.x, isoZombie.y, position2.posWorld.x, position2.posWorld.y);
                if (!position2.isOccupied() || IsoUtils.DistanceToSquared(position2.zombie.x, position2.zombie.y, position2.posWorld.x, position2.posWorld.y) >= distanceToSquared) {
                    final float distanceToSquared2 = IsoUtils.DistanceToSquared(isoZombie.getTarget().x, isoZombie.getTarget().y, position2.posWorld.x, position2.posWorld.y);
                    if (distanceToSquared2 < n) {
                        n = distanceToSquared2;
                        position = position2;
                    }
                }
            }
        }
        return position;
    }
    
    public Vector2f getPositionForZombie(final IsoZombie zombie, final Vector2f vector2f) {
        if ((zombie.isOnFloor() && !zombie.isCanWalk()) || (int)zombie.getZ() != (int)this.m_vehicle.getZ()) {
            return vector2f.set(this.m_vehicle.x, this.m_vehicle.y);
        }
        if (IsoUtils.DistanceToSquared(zombie.x, zombie.y, this.m_vehicle.x, this.m_vehicle.y) > 100.0f) {
            return vector2f.set(this.m_vehicle.x, this.m_vehicle.y);
        }
        if (this.checkPosition()) {
            this.m_bMoved = true;
        }
        for (int i = 0; i < this.m_positions.size(); ++i) {
            final Position position = this.m_positions.get(i);
            if (position.bBlocked) {
                position.zombie = null;
            }
            if (position.zombie == zombie) {
                return vector2f.set(position.posWorld.x, position.posWorld.y);
            }
        }
        final Position closestPosition = this.getClosestPositionFor(zombie);
        if (closestPosition == null) {
            return null;
        }
        closestPosition.zombie = zombie;
        closestPosition.targetX = zombie.getTarget().x;
        closestPosition.targetY = zombie.getTarget().y;
        return vector2f.set(closestPosition.posWorld.x, closestPosition.posWorld.y);
    }
    
    private boolean checkPosition() {
        if (this.m_vehicle.getScript() == null) {
            return false;
        }
        if (this.m_positions.isEmpty()) {
            this.calcPositionsLocal();
            this.x1 = -1.0f;
        }
        final PolygonalMap2.VehiclePoly poly = this.m_vehicle.getPoly();
        if (this.x1 != poly.x1 || this.x2 != poly.x2 || this.x3 != poly.x3 || this.x4 != poly.x4 || this.y1 != poly.y1 || this.y2 != poly.y2 || this.y3 != poly.y3 || this.y4 != poly.y4) {
            this.x1 = poly.x1;
            this.x2 = poly.x2;
            this.x3 = poly.x3;
            this.x4 = poly.x4;
            this.y1 = poly.y1;
            this.y2 = poly.y2;
            this.y3 = poly.y3;
            this.y4 = poly.y4;
            this.calcPositionsWorld();
            return true;
        }
        return false;
    }
    
    private boolean movedSincePositionsWereCalculated() {
        final PolygonalMap2.VehiclePoly poly = this.m_vehicle.getPoly();
        return this.x1p != poly.x1 || this.x2p != poly.x2 || this.x3p != poly.x3 || this.x4p != poly.x4 || this.y1p != poly.y1 || this.y2p != poly.y2 || this.y3p != poly.y3 || this.y4p != poly.y4;
    }
    
    private boolean hasOccupiedPositions() {
        for (int i = 0; i < this.m_positions.size(); ++i) {
            if (this.m_positions.get(i).zombie != null) {
                return true;
            }
        }
        return false;
    }
    
    public void update() {
        if (this.hasOccupiedPositions() && this.checkPosition()) {
            this.m_bMoved = true;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.m_updateMS < 1000L) {
            return;
        }
        this.m_updateMS = currentTimeMillis;
        if (this.m_bMoved) {
            this.m_bMoved = false;
            for (int i = 0; i < this.m_positions.size(); ++i) {
                this.m_positions.get(i).zombie = null;
            }
        }
        final boolean movedSincePositionsWereCalculated = this.movedSincePositionsWereCalculated();
        for (int j = 0; j < this.m_positions.size(); ++j) {
            final Position position = this.m_positions.get(j);
            if (!movedSincePositionsWereCalculated) {
                position.checkBlocked(this.m_vehicle);
            }
            if (position.zombie != null) {
                if (IsoUtils.DistanceToSquared(position.zombie.x, position.zombie.y, this.m_vehicle.x, this.m_vehicle.y) > 100.0f) {
                    position.zombie = null;
                }
                else {
                    final IsoGameCharacter isoGameCharacter = Type.tryCastTo(position.zombie.getTarget(), IsoGameCharacter.class);
                    if (position.zombie.isDead() || VirtualZombieManager.instance.isReused(position.zombie) || position.zombie.isOnFloor() || isoGameCharacter == null || this.m_vehicle.getSeat(isoGameCharacter) == -1) {
                        position.zombie = null;
                    }
                    else if (IsoUtils.DistanceToSquared(position.targetX, position.targetY, isoGameCharacter.x, isoGameCharacter.y) > 0.1f) {
                        position.zombie = null;
                    }
                }
            }
        }
    }
    
    public void render() {
        if (!this.hasOccupiedPositions()) {
            return;
        }
        for (int i = 0; i < this.m_positions.size(); ++i) {
            final Position position = this.m_positions.get(i);
            final Vector3f posWorld = position.posWorld;
            float n = 1.0f;
            float n2 = 1.0f;
            float n3 = 1.0f;
            if (position.isOccupied()) {
                n3 = (n = 0.0f);
            }
            else if (position.bBlocked) {
                n3 = (n2 = 0.0f);
            }
            this.m_vehicle.getController().drawCircle(posWorld.x, posWorld.y, 0.3f, n, n2, n3, 1.0f);
        }
    }
    
    public void reset() {
        SurroundVehicle.s_positionPool.release(this.m_positions);
        this.m_positions.clear();
    }
    
    static {
        s_positionPool = new ObjectPool<Position>(Position::new);
        s_tempVector3f = new Vector3f();
    }
    
    private enum PositionSide
    {
        Front, 
        Rear, 
        Left, 
        Right;
        
        private static /* synthetic */ PositionSide[] $values() {
            return new PositionSide[] { PositionSide.Front, PositionSide.Rear, PositionSide.Left, PositionSide.Right };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static final class Position
    {
        final Vector2f posLocal;
        final Vector3f posWorld;
        final Vector3f posAxis;
        PositionSide side;
        IsoZombie zombie;
        float targetX;
        float targetY;
        boolean bBlocked;
        
        private Position() {
            this.posLocal = new Vector2f();
            this.posWorld = new Vector3f();
            this.posAxis = new Vector3f();
        }
        
        boolean isOccupied() {
            return this.zombie != null;
        }
        
        void checkBlocked(final BaseVehicle baseVehicle) {
            if (!(this.bBlocked = PolygonalMap2.instance.lineClearCollide(this.posWorld.x, this.posWorld.y, this.posAxis.x, this.posAxis.y, (int)baseVehicle.z, baseVehicle))) {
                this.bBlocked = !PolygonalMap2.instance.canStandAt(this.posWorld.x, this.posWorld.y, (int)baseVehicle.z, baseVehicle, false, false);
            }
        }
    }
}
