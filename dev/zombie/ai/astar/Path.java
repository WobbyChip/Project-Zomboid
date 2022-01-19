// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.astar;

import java.util.Stack;
import java.util.ArrayList;

public class Path
{
    private ArrayList<Step> steps;
    public float cost;
    public static Stack<Step> stepstore;
    static Step containsStep;
    
    public Path() {
        this.steps = new ArrayList<Step>();
        this.cost = 0.0f;
    }
    
    public float costPerStep() {
        if (this.steps.isEmpty()) {
            return this.cost;
        }
        return this.cost / this.steps.size();
    }
    
    public void appendStep(final int x, final int y, final int z) {
        final Step e = new Step();
        e.x = x;
        e.y = y;
        e.z = z;
        this.steps.add(e);
    }
    
    public boolean contains(final int x, final int y, final int z) {
        Path.containsStep.x = x;
        Path.containsStep.y = y;
        Path.containsStep.z = z;
        return this.steps.contains(Path.containsStep);
    }
    
    public int getLength() {
        return this.steps.size();
    }
    
    public Step getStep(final int index) {
        return this.steps.get(index);
    }
    
    public int getX(final int n) {
        return this.getStep(n).x;
    }
    
    public int getY(final int n) {
        return this.getStep(n).y;
    }
    
    public int getZ(final int n) {
        return this.getStep(n).z;
    }
    
    public static Step createStep() {
        if (Path.stepstore.isEmpty()) {
            for (int i = 0; i < 200; ++i) {
                Path.stepstore.push(new Step());
            }
        }
        return Path.stepstore.push(Path.containsStep);
    }
    
    public void prependStep(final int x, final int y, final int z) {
        final Step element = new Step();
        element.x = x;
        element.y = y;
        element.z = z;
        this.steps.add(0, element);
    }
    
    static {
        Path.stepstore = new Stack<Step>();
        Path.containsStep = new Step();
    }
    
    public static class Step
    {
        public int x;
        public int y;
        public int z;
        
        public Step(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public Step() {
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof Step) {
                final Step step = (Step)o;
                return step.x == this.x && step.y == this.y && step.z == this.z;
            }
            return false;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public int getZ() {
            return this.z;
        }
        
        @Override
        public int hashCode() {
            return this.x * this.y * this.z;
        }
    }
}
