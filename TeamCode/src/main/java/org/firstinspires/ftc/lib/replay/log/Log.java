package org.firstinspires.ftc.lib.replay.log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String name() default "";
    String link() default "";
}
