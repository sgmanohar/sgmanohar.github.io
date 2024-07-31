package phic.common;
/** Any class that implements HasContent will be made a 'visible class', and
 * its public members may be displayed to the user.
 * Items made visible include primitive boolean & integer types, other objects
 * implementing HasContent, methods with no parameters.
 * @see phic.common.ClassVisualiser
 */
public interface HasContent extends java.io.Serializable{
}