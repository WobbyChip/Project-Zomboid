// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.core.profiling.PerformanceProfileProbe;
import java.util.List;
import zombie.characters.ZombieVocalsManager;
import zombie.characters.ZombieThumpManager;
import zombie.characters.ZombieFootstepManager;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.IsoPushableObject;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoMovingObject;
import java.util.Stack;
import java.util.ArrayList;
import zombie.core.collision.Polygon;
import zombie.iso.Vector2;

public final class CollisionManager
{
    static Vector2 temp;
    static Vector2 axis;
    static Polygon polygonA;
    static Polygon polygonB;
    float minA;
    float minB;
    float maxA;
    float maxB;
    PolygonCollisionResult result;
    public ArrayList<Contact> ContactMap;
    Long[] longArray;
    Stack<Contact> contacts;
    public static final CollisionManager instance;
    
    public CollisionManager() {
        this.minA = 0.0f;
        this.minB = 0.0f;
        this.maxA = 0.0f;
        this.maxB = 0.0f;
        this.result = new PolygonCollisionResult();
        this.ContactMap = new ArrayList<Contact>();
        this.longArray = new Long[1000];
        this.contacts = new Stack<Contact>();
    }
    
    private void ProjectPolygonA(final Vector2 vector2, final Polygon polygon) {
        final float dot = vector2.dot(polygon.points.get(0));
        this.minA = dot;
        this.maxA = dot;
        for (int i = 0; i < polygon.points.size(); ++i) {
            final float dot2 = polygon.points.get(i).dot(vector2);
            if (dot2 < this.minA) {
                this.minA = dot2;
            }
            else if (dot2 > this.maxA) {
                this.maxA = dot2;
            }
        }
    }
    
    private void ProjectPolygonB(final Vector2 vector2, final Polygon polygon) {
        final float dot = vector2.dot(polygon.points.get(0));
        this.minB = dot;
        this.maxB = dot;
        for (int i = 0; i < polygon.points.size(); ++i) {
            final float dot2 = polygon.points.get(i).dot(vector2);
            if (dot2 < this.minB) {
                this.minB = dot2;
            }
            else if (dot2 > this.maxB) {
                this.maxB = dot2;
            }
        }
    }
    
    public PolygonCollisionResult PolygonCollision(final Vector2 vector2) {
        this.result.Intersect = true;
        this.result.WillIntersect = true;
        this.result.MinimumTranslationVector.x = 0.0f;
        this.result.MinimumTranslationVector.y = 0.0f;
        final int size = CollisionManager.polygonA.edges.size();
        final int size2 = CollisionManager.polygonB.edges.size();
        float n = Float.POSITIVE_INFINITY;
        final Vector2 vector3 = new Vector2();
        for (int i = 0; i < size + size2; ++i) {
            Vector2 vector4;
            if (i < size) {
                vector4 = CollisionManager.polygonA.edges.get(i);
            }
            else {
                vector4 = CollisionManager.polygonB.edges.get(i - size);
            }
            CollisionManager.axis.x = -vector4.y;
            CollisionManager.axis.y = vector4.x;
            CollisionManager.axis.normalize();
            this.minA = 0.0f;
            this.minB = 0.0f;
            this.maxA = 0.0f;
            this.maxB = 0.0f;
            this.ProjectPolygonA(CollisionManager.axis, CollisionManager.polygonA);
            this.ProjectPolygonB(CollisionManager.axis, CollisionManager.polygonB);
            if (this.IntervalDistance(this.minA, this.maxA, this.minB, this.maxB) > 0.0f) {
                this.result.Intersect = false;
            }
            final float dot = CollisionManager.axis.dot(vector2);
            if (dot < 0.0f) {
                this.minA += dot;
            }
            else {
                this.maxA += dot;
            }
            final float intervalDistance = this.IntervalDistance(this.minA, this.maxA, this.minB, this.maxB);
            if (intervalDistance > 0.0f) {
                this.result.WillIntersect = false;
            }
            if (!this.result.Intersect && !this.result.WillIntersect) {
                break;
            }
            final float abs = Math.abs(intervalDistance);
            if (abs < n) {
                n = abs;
                vector3.x = CollisionManager.axis.x;
                vector3.y = CollisionManager.axis.y;
                CollisionManager.temp.x = CollisionManager.polygonA.Center().x - CollisionManager.polygonB.Center().x;
                CollisionManager.temp.y = CollisionManager.polygonA.Center().y - CollisionManager.polygonB.Center().y;
                if (CollisionManager.temp.dot(vector3) < 0.0f) {
                    vector3.x = -vector3.x;
                    vector3.y = -vector3.y;
                }
            }
        }
        if (this.result.WillIntersect) {
            this.result.MinimumTranslationVector.x = vector3.x * n;
            this.result.MinimumTranslationVector.y = vector3.y * n;
        }
        return this.result;
    }
    
    public float IntervalDistance(final float n, final float n2, final float n3, final float n4) {
        if (n < n3) {
            return n3 - n2;
        }
        return n - n4;
    }
    
    public void initUpdate() {
        if (this.longArray[0] == null) {
            for (int i = 0; i < this.longArray.length; ++i) {
                this.longArray[i] = new Long(0L);
            }
        }
        for (int j = 0; j < this.ContactMap.size(); ++j) {
            this.ContactMap.get(j).a = null;
            this.ContactMap.get(j).b = null;
            this.contacts.push(this.ContactMap.get(j));
        }
        this.ContactMap.clear();
    }
    
    public void AddContact(final IsoMovingObject isoMovingObject, final IsoMovingObject isoMovingObject2) {
        if ((isoMovingObject instanceof IsoSurvivor || isoMovingObject2 instanceof IsoSurvivor) && (isoMovingObject instanceof IsoPushableObject || isoMovingObject2 instanceof IsoPushableObject)) {
            return;
        }
        if (isoMovingObject.getID() < isoMovingObject2.getID()) {
            this.ContactMap.add(this.contact(isoMovingObject, isoMovingObject2));
        }
    }
    
    Contact contact(final IsoMovingObject a, final IsoMovingObject b) {
        if (this.contacts.isEmpty()) {
            for (int i = 0; i < 50; ++i) {
                this.contacts.push(new Contact(null, null));
            }
        }
        final Contact contact = this.contacts.pop();
        contact.a = a;
        contact.b = b;
        return contact;
    }
    
    public void ResolveContacts() {
        s_performance.profile_ResolveContacts.invokeAndMeasure(this, CollisionManager::resolveContactsInternal);
    }
    
    private void resolveContactsInternal() {
        final Vector2 vel = l_ResolveContacts.vel;
        final Vector2 vel2 = l_ResolveContacts.vel2;
        final List<IsoPushableObject> pushables = l_ResolveContacts.pushables;
        final ArrayList<IsoPushableObject> pushableObjectList = IsoWorld.instance.CurrentCell.getPushableObjectList();
        for (int size = pushableObjectList.size(), i = 0; i < size; ++i) {
            final IsoPushableObject isoPushableObject = pushableObjectList.get(i);
            if (isoPushableObject.getImpulsex() != 0.0f || isoPushableObject.getImpulsey() != 0.0f) {
                if (isoPushableObject.connectList != null) {
                    pushables.add(isoPushableObject);
                }
                else {
                    isoPushableObject.setNx(isoPushableObject.getNx() + isoPushableObject.getImpulsex());
                    isoPushableObject.setNy(isoPushableObject.getNy() + isoPushableObject.getImpulsey());
                    isoPushableObject.setImpulsex(isoPushableObject.getNx() - isoPushableObject.getX());
                    isoPushableObject.setImpulsey(isoPushableObject.getNy() - isoPushableObject.getY());
                    isoPushableObject.setNx(isoPushableObject.getX());
                    isoPushableObject.setNy(isoPushableObject.getY());
                }
            }
        }
        for (int size2 = pushables.size(), j = 0; j < size2; ++j) {
            final IsoPushableObject isoPushableObject2 = pushables.get(j);
            float n = 0.0f;
            float n2 = 0.0f;
            for (int k = 0; k < isoPushableObject2.connectList.size(); ++k) {
                n += isoPushableObject2.connectList.get(k).getImpulsex();
                n2 += isoPushableObject2.connectList.get(k).getImpulsey();
            }
            final float impulsex = n / isoPushableObject2.connectList.size();
            final float impulsey = n2 / isoPushableObject2.connectList.size();
            for (int l = 0; l < isoPushableObject2.connectList.size(); ++l) {
                isoPushableObject2.connectList.get(l).setImpulsex(impulsex);
                isoPushableObject2.connectList.get(l).setImpulsey(impulsey);
                final int index = pushables.indexOf(isoPushableObject2.connectList.get(l));
                pushables.remove(isoPushableObject2.connectList.get(l));
                if (index <= j) {
                    --j;
                }
            }
            if (j < 0) {
                j = 0;
            }
        }
        pushables.clear();
        for (int size3 = this.ContactMap.size(), index2 = 0; index2 < size3; ++index2) {
            final Contact contact = this.ContactMap.get(index2);
            if (Math.abs(contact.a.getZ() - contact.b.getZ()) <= 0.3f) {
                vel.x = contact.a.getNx() - contact.a.getX();
                vel.y = contact.a.getNy() - contact.a.getY();
                vel2.x = contact.b.getNx() - contact.b.getX();
                vel2.y = contact.b.getNy() - contact.b.getY();
                if (vel.x != 0.0f || vel.y != 0.0f || vel2.x != 0.0f || vel2.y != 0.0f || contact.a.getImpulsex() != 0.0f || contact.a.getImpulsey() != 0.0f || contact.b.getImpulsex() != 0.0f || contact.b.getImpulsey() != 0.0f) {
                    final float n3 = contact.a.getX() - contact.a.getWidth();
                    final float n4 = contact.a.getX() + contact.a.getWidth();
                    final float n5 = contact.a.getY() - contact.a.getWidth();
                    final float n6 = contact.a.getY() + contact.a.getWidth();
                    final float n7 = contact.b.getX() - contact.b.getWidth();
                    final float n8 = contact.b.getX() + contact.b.getWidth();
                    final float n9 = contact.b.getY() - contact.b.getWidth();
                    final float n10 = contact.b.getY() + contact.b.getWidth();
                    CollisionManager.polygonA.Set(n3, n5, n4, n6);
                    CollisionManager.polygonB.Set(n7, n9, n8, n10);
                    final PolygonCollisionResult polygonCollision = this.PolygonCollision(vel);
                    if (polygonCollision.WillIntersect) {
                        contact.a.collideWith(contact.b);
                        contact.b.collideWith(contact.a);
                        final float n11 = 1.0f - contact.a.getWeight(polygonCollision.MinimumTranslationVector.x, polygonCollision.MinimumTranslationVector.y) / (contact.a.getWeight(polygonCollision.MinimumTranslationVector.x, polygonCollision.MinimumTranslationVector.y) + contact.b.getWeight(polygonCollision.MinimumTranslationVector.x, polygonCollision.MinimumTranslationVector.y));
                        if (contact.a instanceof IsoPushableObject && contact.b instanceof IsoSurvivor) {
                            ((IsoSurvivor)contact.b).bCollidedWithPushable = true;
                            ((IsoSurvivor)contact.b).collidePushable = (IsoPushableObject)contact.a;
                        }
                        else if (contact.b instanceof IsoPushableObject && contact.a instanceof IsoSurvivor) {
                            ((IsoSurvivor)contact.a).bCollidedWithPushable = true;
                            ((IsoSurvivor)contact.a).collidePushable = (IsoPushableObject)contact.b;
                        }
                        if (contact.a instanceof IsoPushableObject) {
                            final ArrayList<IsoPushableObject> connectList = ((IsoPushableObject)contact.a).connectList;
                            if (connectList != null) {
                                for (int size4 = connectList.size(), index3 = 0; index3 < size4; ++index3) {
                                    final IsoPushableObject isoPushableObject3 = connectList.get(index3);
                                    isoPushableObject3.setImpulsex(isoPushableObject3.getImpulsex() + polygonCollision.MinimumTranslationVector.x * n11);
                                    isoPushableObject3.setImpulsey(isoPushableObject3.getImpulsey() + polygonCollision.MinimumTranslationVector.y * n11);
                                }
                            }
                        }
                        else {
                            contact.a.setImpulsex(contact.a.getImpulsex() + polygonCollision.MinimumTranslationVector.x * n11);
                            contact.a.setImpulsey(contact.a.getImpulsey() + polygonCollision.MinimumTranslationVector.y * n11);
                        }
                        if (contact.b instanceof IsoPushableObject) {
                            final ArrayList<IsoPushableObject> connectList2 = ((IsoPushableObject)contact.b).connectList;
                            if (connectList2 != null) {
                                for (int size5 = connectList2.size(), index4 = 0; index4 < size5; ++index4) {
                                    final IsoPushableObject isoPushableObject4 = connectList2.get(index4);
                                    isoPushableObject4.setImpulsex(isoPushableObject4.getImpulsex() - polygonCollision.MinimumTranslationVector.x * (1.0f - n11));
                                    isoPushableObject4.setImpulsey(isoPushableObject4.getImpulsey() - polygonCollision.MinimumTranslationVector.y * (1.0f - n11));
                                }
                            }
                        }
                        else {
                            contact.b.setImpulsex(contact.b.getImpulsex() - polygonCollision.MinimumTranslationVector.x * (1.0f - n11));
                            contact.b.setImpulsey(contact.b.getImpulsey() - polygonCollision.MinimumTranslationVector.y * (1.0f - n11));
                        }
                    }
                }
            }
        }
        IsoWorld.instance.CurrentCell.getObjectList().size();
        MovingObjectUpdateScheduler.instance.postupdate();
        IsoMovingObject.treeSoundMgr.update();
        ZombieFootstepManager.instance.update();
        ZombieThumpManager.instance.update();
        ZombieVocalsManager.instance.update();
    }
    
    static {
        CollisionManager.temp = new Vector2();
        CollisionManager.axis = new Vector2();
        CollisionManager.polygonA = new Polygon();
        CollisionManager.polygonB = new Polygon();
        instance = new CollisionManager();
    }
    
    public class PolygonCollisionResult
    {
        public boolean WillIntersect;
        public boolean Intersect;
        public Vector2 MinimumTranslationVector;
        
        public PolygonCollisionResult() {
            this.MinimumTranslationVector = new Vector2();
        }
    }
    
    public class Contact
    {
        public IsoMovingObject a;
        public IsoMovingObject b;
        
        public Contact(final IsoMovingObject a, final IsoMovingObject b) {
            this.a = a;
            this.b = b;
        }
    }
    
    private static class l_ResolveContacts
    {
        static final Vector2 vel;
        static final Vector2 vel2;
        static final List<IsoPushableObject> pushables;
        static IsoMovingObject[] objectListInvoking;
        
        static {
            vel = new Vector2();
            vel2 = new Vector2();
            pushables = new ArrayList<IsoPushableObject>();
            l_ResolveContacts.objectListInvoking = new IsoMovingObject[1024];
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe profile_ResolveContacts;
        static final PerformanceProfileProbe profile_MovingObjectPostUpdate;
        
        static {
            profile_ResolveContacts = new PerformanceProfileProbe("CollisionManager.ResolveContacts");
            profile_MovingObjectPostUpdate = new PerformanceProfileProbe("IsoMovingObject.postupdate");
        }
    }
}
