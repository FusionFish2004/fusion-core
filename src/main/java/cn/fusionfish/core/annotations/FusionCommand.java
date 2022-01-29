package cn.fusionfish.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author JeremyHu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FusionCommand {
    String perm() default "";
    String description() default "";
    String usage() default "";
    String label() default "";
    String[] aliases() default {};
    boolean adminCommand() default false;
    boolean playerCommand() default false;
    String parent() default "";
}
