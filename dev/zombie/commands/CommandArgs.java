// 
// Decompiled by Procyon v0.5.36
// 

package zombie.commands;

import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AltCommandArgs.class)
public @interface CommandArgs {
    public static final String DEFAULT_OPTIONAL_ARGUMENT = "no value";
    
    String[] required() default {};
    
    String optional() default "no value";
    
    String argName() default "NO_ARGUMENT_NAME";
    
    boolean varArgs() default false;
}
