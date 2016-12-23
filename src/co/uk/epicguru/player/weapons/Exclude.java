package co.uk.epicguru.player.weapons;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Indicates that this field should be excluded from reflection in classes that use it in Day100.
 * @author James Billy
 */
public @interface Exclude {

}
