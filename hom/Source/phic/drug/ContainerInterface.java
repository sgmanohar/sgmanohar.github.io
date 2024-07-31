/**
 * Interface declaring container functions.
 * Not currently implemented! May be used later for cleaner design.
 */
package phic.drug;
public interface ContainerInterface extends Cloneable,java.io.Serializable{
	/**
	 * Set the contents of this container so that they are identical to another
	 * container
	 */
	public void set(ContainerInterface from);

	/** Get the volume of the container */
	abstract double getVolume();

	/** Add or remove water from the container */
	abstract void addWater(double volume);

	/**
	 * Return a new container with the specified volume of fluid drawn
	 * from this container
	 */
	public ContainerInterface withdrawVol(double volume);

	/**
	 * Return a new container with the specified fraction of the fluid in
	 * this container
	 */
	public ContainerInterface withdrawFrac(double fraction);

	/**
	 * Add the whole of the the container 'from' to the current container
	 */
	public void add(ContainerInterface from);

	/**
	 *	Add a specified volume of the fluid in a container 'from' into this
	 *	container.
	 */
	public void add(ContainerInterface from,double volume);

	/**
	 * Return a container containing a fluid of the same volume as 'example',
	 * with as similar a composition as possible to the fluid contained in
	 * 'example', withdrawn from this container.
	 * If not enough of a certain constituent is available, then the maximum
	 * possible is removed.
	 */
	public ContainerInterface withdrawVol(ContainerInterface example);

	/**
	 * Return a new container of fluid drawn from this container, of composition
			 * as similar as possible to that of 'example', and with the specified fraction
	 * of this container's volume. If not enough of a certain constituent is available,
	 * then the maximum possible quantity is withdrawn.
	 */
	ContainerInterface withdrawFrac(ContainerInterface example,double fraction);

	/**	Returns a container with the solids of this container. */

	ContainerInterface filterSolids();

			/**	Returns the specified amount of renal ultrafiltrate from this container */

			ContainerInterface ultraFiltrate(double volume);

	/**	Returns the osmolarity of this container. */

	double getOsmolarity();

	/**	Transfer water between this container and 'to', so as to bring this container
			 * towards the osmolarity specified. 'frac' specifies how much water to transfer
	 * as a fraction of the amount required to attain equilibrium.
	 */
	void osmose(ContainerInterface to,double osmolarity,double frac);

	/**	Returns the pH of this container. */
	double pH();

	/**	Clear contents of container. */
	void empty();
}