/**
 * Default implementation of ContainerInterface.
 */
package phic.drug;
public abstract class AbstractContainer implements ContainerInterface{
	/**
	 * Set the contents of this container so that they are identical to another
	 * container
	 */
	public abstract void set(ContainerInterface from);

	/**
	 * Return a new container with the specified volume of fluid drawn
	 * from this container
	 */
	public abstract ContainerInterface withdrawVol(double volume);

	/**
	 * Return a new container with the specified fraction of the fluid in
	 * this container
	 */
	public ContainerInterface withdrawFrac(double fraction){
		double frac=Math.max(Math.min(fraction,1),0);
		return withdrawVol(getVolume()*frac);
	}

	/**
	 * Add the whole of the the container 'from' to the current container
	 */
	public void add(ContainerInterface from){
	}

	/**
	 *	Add a specified volume of the fluid in a container 'from' into this
	 *	container.
	 */
	public void add(ContainerInterface from,double volume){
		add(from.withdrawVol(volume));
	}

	/**
	 * Return a container containing a fluid of the same volume as 'example',
	 * with as similar a composition as possible to the fluid contained in
	 * 'example', withdrawn from this container.
	 * If not enough of a certain constituent is available, then the maximum
	 * possible is removed.
	 */
	public abstract ContainerInterface withdrawVol(ContainerInterface example);

	/**
	 * Return a new container of fluid drawn from this container, of composition
			 * as similar as possible to that of 'example', and with the specified fraction
	 * of this container's volume. If not enough of a certain constituent is available,
	 * then the maximum possible quantity is withdrawn.
	 */
	public abstract ContainerInterface withdrawFrac(ContainerInterface example,
			double fraction);

	/**	Returns a container with the solids of this container. */
	public abstract ContainerInterface filterSolids();

			/**	Returns the specified amount of renal ultrafiltrate from this container */
	public abstract ContainerInterface ultraFiltrate(double volume);

	/**
			 * Transfer water between this container and 'to', so as to bring this container
			 * towards the osmolarity specified. 'frac' specifies how much water to transfer
	 * as a fraction of the amount required to attain equilibrium.
	 */
	public void osmose(ContainerInterface to,double osmolarity,double frac){
		Object lock1=this,lock2=to;
		if(hashCode()>to.hashCode()){
			lock1=to;
		}
		lock2=this;
		double transf=(osmolarity-getOsmolarity())*getVolume()/osmolarity;
		transf*=frac;
		transf=Math.max(transf,-to.getVolume()); //if I'm very concentrated, can't take up too much fluid!
		synchronized(lock1){
			synchronized(lock2){
				to.addWater(transf);
				addWater(-transf); //dilute/concentrate as req'd
			}
		}
	}

	/**	Clear contents of container. */
	public abstract void empty();

	/** Set the volume of the container, without affecting any concentrations */
	protected abstract void setVolume(double volume);

	/**	Returns the osmolarity of this container. */
	public abstract double getOsmolarity();

	/**	Returns the pH of this container. */
	public abstract double pH();
}