package org.firstinspires.ftc.lib.replay;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Replay {
    String name() default "";
}
