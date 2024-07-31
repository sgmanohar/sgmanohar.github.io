package phic.common;
import java.io.PrintStream;
import java.util.Vector;
import phic.Current;
import phic.drug.DrugContainer;

/**
 * Container
 * Each container has a vector of constituents.
 * Currently each container contains bicarb, H, glucose, urea,
 * prot, K, Na, solids.
 * 1/7/3 added creatinine
 *
 * Locking order is low -> high hashCode. The quantities obtain locks on this container;
 * so quantities should not be retrieved from containers when other containers are locked.
 *
 * Conventions: all units are litres, moles, grams.
 */
public class Container implements Cloneable,HasContent{
	/** Creates the set of quantities, and adds them to the vector qs. */
	public Container(){
		qs.add(bicarb);
		qs.add(H);
		qs.add(glucose);
		qs.add(urea);
		qs.add(prot);
		qs.add(K);
		qs.add(Na);
		qs.add(solids);
		qs.add(creat);
                serial++;
	}
        static int serial=0;
	//quantities
	/**
	 * The vector of quantites. use substance(int) to access.
	 * Each quantity is the concentration in the entire volume of this container.
	 */
	public Vector qs=new Vector();

	/**
	 * Publically accessible definitions of basic quantities in a container
			 * Each quantity is assigned this container as its parent. This is so that the
	 * volume can be used for converting concentrations to quantities.
	 * Quantites are now also VDoubles.
	 */
	public Quantity bicarb=new Quantity(this),H=new Quantity(this),
			glucose=new Quantity(this),urea=new Quantity(this),prot=new Quantity(this),
			K=new Quantity(this),Na=new Quantity(this),creat=new Quantity(this);

	/**
	 * Solids - another quantity, currently the 'concentration' is represented as
	 * a percentage of the total container volume, i.e. 100 is plain solids.
	 * @todo Change this to a fraction, where 1 is maximum.
	 */
	public Quantity solids=new Quantity(this);

	/**
	 * Method to retrieve a quantity by its index
	 * Used in loops to perform tasks to each constituent.
	 */
	public synchronized Quantity substance(int i){
		return(Quantity)qs.elementAt(i);
	}

	/**
	 * The volume of the container, as a VDouble variable, that simply
	 * accesses the protected double variable vol. Synchronized on the container
	 * object.
	 */
	public VDouble volume=new VDouble(){
		{
			unit=UnitConstants.LITRES;
		}

		public void set(double v){
			synchronized(Container.this){
				vol=v;
			}
		}

		public double get(){
			synchronized(Container.this){
				return vol;
			}
		}
	};

	/**
	 * The actual value of the volume - should only be accessed within Containers
	 */
	protected double vol;

	//container functions
	/**
	 * Primitive:
	 * Copy contents of 'from' exactly into current container. Locks 2 containers at once.
	 * Warning: This method does not conserve quantities (it causes
	 * duplication of substances in 'from' and destroys the contents of this
	 * container!)
	 * @deprecated Do not use this method. Use empty() followed by add().
	 */
	public void set(Container from){
		Object lock1=this,lock2=from;
		if(hashCode()>from.hashCode()){
			lock1=from;
		}
		lock2=this;
	//	synchronized(lock1){
	//		synchronized(lock2){
				vol=from.vol;
				for(int i=0;i<from.qs.size();i++){
					substance(i).set(from.substance(i).get());
				}
	//		}
	//	}
	}

	/**
	 * Primitive:
	 * 	Return a new container with the specified quantity of fluid drawn
	 *	from this container. Volume must be non-negative.
	 */
	public  Container withdrawVol(double volume){
		/* //alternative implementation with clone
		 Container c;
		 try{c=(Container)clone();}catch(CloneNotSupportedException e){e.printStackTrace();return null;}
		 double v=Math.min(volume,this.volume);
		 c.volume=v;
		 this.volume-=v;
		 */
		Container c=new Container();
                withdrawVol_overwrite(volume, c);
		return c;
	}

        /** primitive:
         *   this withdraws a volume from the container, and overwrites
         *   the contents of the container 'into' with this fluid.
         */
        public void withdrawVol_overwrite(double volume, Container into){
          into.set(this);
          if(volume<0){
                  Current.body.error("cannot withdraw negative amount, "+volume);
          }
          volume=Math.min(volume,this.vol);
          into.vol=volume; //setup new fluid volume
          for(int i=0;i<qs.size();i++){ //copy concentrations
                  into.substance(i).setC(substance(i).getC());
          }
          this.vol-=volume; //deplete this fluid volume
        }

	/**
	 * 	Return a new container with the specified fraction of the fluid in
	 *	this container. Calls withdrawVol( volume ).
	 *      This delegation is valid for DrugContainers.
	 */
	public  Container withdrawFrac(double fraction){
		double frac=Math.max(Math.min(fraction,1),0);
		return withdrawVol(vol*frac);
	}

        /**
         * Transfer a fraction of the fluid in this container to the new
         * container 'into', whose contents are overwritten
         */
        public void withdrawFrac_overwrite(double fraction, Container into){
          double frac=Math.max(Math.min(fraction,1),0);
          withdrawVol_overwrite(vol*frac, into);
        }

	/**
	 * Primitive:
	 * Add all of a container 'from' into this container.
	 * Locks 2 containers at once.
         * Note that if this container has fewer quantities than the other
         * container, an IllegalArgumentException will be thrown....
	 */
	public void add(Container from){
          Object lock1 = this, lock2 = from;
          if (hashCode() > from.hashCode()) {
            lock1 = from;
          }
          lock2 = this;
          //	synchronized(lock1){
          //		synchronized(lock2){
          //it is ok to use increaseVolume since the H+ lost is replaced from the other container
          increaseVolume(from.vol);
          int n = from.qs.size();
          if (qs.size() < n)throw new IllegalArgumentException(
              "This "+getClass()+" and that "+from.getClass()+" have different quantities.");
          for (int i = 0; i < n; i++) {
            substance(i).addQ(from.substance(i).getQ());
            from.substance(i).setQ(0); //replace with setC for speed?
          }
          from.vol = 0;
          //		}
          //	}
	}
        /**
         * Add discarding any quantities that are not implemented in one of the
         * containers
         */
        public void addAndDiscardExtras(Container from){
          increaseVolume(from.vol);
          int n=compatibleQSize(from);
          for(int i=0;i<n;i++){
                  substance(i).addQ(from.substance(i).getQ());
                  from.substance(i).setQ(0); //replace with setC for speed?
          }
          from.vol=0;
        }

	/**
	 * Add a specified volume of the fluid in 'from' into this
	 *	container; the composition of the tranfer is always of the composition
	 * 'from', even when the volume is negative. I.e., negative volumes mean
	 * removing fluid of type 'from' from 'this' into 'from'.
	 *
	 * If there is not enough of a certain constituent in the source container,
	 * the maximum amount of this constituent will be transferred.
	 *
	 * Locks 2 containers at once.
	 * Is faster than calling add(from.withdrawVol(volume));
	 * Used by addWater with negative amounts.
	 * @param volume This may be in the range (-this.volume, from.volume).
	 */
	public  void add(Container from,double volume){
		//lock both containers
		if(volume<0&&this.solids.getC()>Math.abs(volume)){
			throw new RuntimeException("Withdrawal of "+Math.abs(volume)+"L from "
					+this.vol+"L, "+this.solids.getQ()+" of which is solids.");
		}
		Object lock1=this,lock2=from;
		if(hashCode()>from.hashCode()){
			lock1=from;
		}
		lock2=this;
	//	synchronized(lock1){
	//		synchronized(lock2){
				volume=Math.min(volume,from.vol); //cannot add more than present in source container.
				double p=volume/from.vol; //proportion of source container. always <= 1
				for(int i=0;i<qs.size();i++){
					double t=p*from.substance(i).getQ();
					// If removing substance, is there enough in this container?
					if(p<0){
						t=Math.max(t,-substance(i).getQ());
						// move substance
					}
					this.substance(i).setC((substance(i).getQ()+t)/(this.vol+volume));
					from.substance(i).setC((from.substance(i).getQ()-t)/(from.vol-volume));
				}
				// update new volumes
				from.vol-=volume;
				this.vol+=volume;
	//		}
	//	}
	}

	/**
	 * Return a container containing a fluid of the same volume as 'example',
	 * with as similar a composition as possible to the fluid contained in
	 * 'example', withdrawn from this container.
	 *
	 * @param example The container whose contents should be used as an example
	 *       of what to remove from this container.
	 *	If not enough of a certain constituent is available, then the volume
	 *	removed is less such that the composition of the extract is as required.
	 *
	 * Calls withdrawVol(example, volume).
	 */
//	public synchronized Container withdrawVol(Container example){
//		return withdrawFracII(example,example.vol/vol);
		/* //alternative implementation that is self standing, but like the current version
			//try to remove a fluid like 'example'
			//if not enought substances, removes maximum possible
		 double maxVolume = Math.min(example.vol, vol);
			//calculate the maximum possible to withdraw
		 for(int i=0;i<qs.size();i++){
			double exampleC = example.substance(i).getC();
			double exampleQ =  exampleC * maxVolume;
			double availableQ = this.substance(i).getC();
			if( exampleQ > availableQ ){
			maxVolume = Math.min( maxVolume, availableQ / exampleC );
			}
		 }
		 Container extract = this.withdrawVol( maxVolume );
		 return extract;
		 */
//	}

        /**
         * this calculates how much of the example it is possible to withdraw,
         * then puts this fluid in 'into', overwriting its contents.
         * The composition of the extract in 'into' is always the same as the
         * example.
         */
        public void withdrawVolExample_overwrite(Container example, Container into){
          //try to remove a fluid like 'example'
          //if not enought substances, decrease maxVolume
          double fraction = example.vol/vol;
          double maxVolume= example.volume.get();
          into.set(example);
          //calculate the maximum possible to withdraw
          for(int i=0;i<qs.size();i++){
                  double exampleC=example.substance(i).getC();
                  double exampleQ=exampleC*maxVolume;
                  double availableQ=this.substance(i).getQ();
                  if(exampleQ>availableQ){ // if cannot withdraw that much, relax the constraint
                          maxVolume=Math.min(maxVolume,availableQ/exampleC);
                  }
          }
          // withdraw maxVolume of example from this container
          into.vol=maxVolume;
          if(maxVolume<=0) return;
          for(int i=0;i<qs.size();i++){
            double transferQ=into.substance(i).getQ();
            substance(i).addQ(-transferQ);
          }
          increaseVolume(-maxVolume);
        }

	/**
	 * Alternative implementation of 'withdraw by example' that will try and
	 * withdraw as much as possible, if there is not enough of a particular
	 * constituent. Thus the concentrations in the extract may not be exactly
	 * those in the example.
	 * @param example The container whose contents should be used as an example
	 * of what to remove from this container.
	 *
	 * @todo This method is inefficient: most of the time, should be able to
	 * replicate the concentrations and change the volumes.
	 * @todo This method will eventually replace WithdrawFrac and WithdrawFracII
	 */
	private Container withdrawAllPossible(Container example,double withdrawVolume){
		Container n=new Container();
		n.vol=withdrawVolume;
		if(withdrawVolume!=0){
			for(int i=0;i<qs.size();i++){
				//this tells us how much to try and take out
				double exampleQ=example.substance(i).getC()*withdrawVolume;
				double availableQ=this.substance(i).getQ();
				double transferQ=Math.min(exampleQ,availableQ);
				n.substance(i).setQ(transferQ);
				substance(i).addQ(-transferQ);
			}
			//then reconcentrate the source container
			//this is inefficient though... normally the source concentration is unchanged.
			increaseVolume(-withdrawVolume);
		}
		return n;
	}

        /**
         *  Remove as much as possible of this container that has the same
         * same composition as example.
         * If enough of a substance is available, its concentration in the
         * withdrawn fluid is the same as the example concentration.
         */
        private void withdrawAllPossible_overwrite(Container example, double withdrawVolume, Container into){
          double tx=Math.min(this.vol, withdrawVolume);
          into.vol=tx;
          if(tx==0)return;
          for(int i=0;i<qs.size();i++){
            double exampleQ = example.substance(i).getC()*tx;
            double availableQ = this.substance(i).getQ();
            double transferQ=Math.min(exampleQ,availableQ);
            into.substance(i).setQ(transferQ);
            this.substance(i).addQ(-transferQ);
          }
          increaseVolume(-tx);
        }


	/**
	 * Return a new container of fluid drawn from this container, of composition
	 * identical to that of 'example', and with the specified fraction
	 * of this container's volume. If not enough of a certain constituent is available,
			 * then a smaller volume is removed, so that the composition of the extract is
	 * as required.
	 *
	 * Calls withdrawVol( volume ).
			 * @param example The container whose concentrations represent the composition
	 * of the fluid to be withdrawn.
			 * @param fraction How much of the maximum volume to withdraw. The maximum volume
	 * is either the volume of this container, or the maximum volume that could
			 * be withdrawn that would resemble the example. Note: If the example contains
	 * a constituent not present in this container, nothing will be withdrawn!
	 * @return A container with a fluid of the same composition as the example,
	 * removed from this container.
	 *
	 * @todo This is a strange unphysiological function. Replace it.
	 * @deprecated Use withdrawFracII as a test
	 */
/*
	public  Container withdrawFrac(Container example,double fraction){
		//percentage is percentage of maximum volume that can be removed.
		//try to remove a fluid like 'example'
		//if not enought substances, decrease maxVolume
		double maxVolume=vol;
		//calculate the maximum possible to withdraw
		for(int i=0;i<qs.size();i++){
			double exampleC=example.substance(i).getC();
			double exampleQ=exampleC*maxVolume;
			double availableQ=this.substance(i).getQ();
			if(exampleQ>availableQ){ // if cannot withdraw that much, relax the constraint
				maxVolume=Math.min(maxVolume,availableQ/exampleC);
			}
		}
		Container extract=this.withdrawAllPossible(example,maxVolume*fraction);
		return extract;
	}
*/

	/**
	 * Does almost the same. Coded directly from original Phic
	 * Only concentrations of example are relevant.
	 * @todo remove this.
	 */
	public Container withdrawFracII(Container example,double fraction){
		double maxVolume=vol; //used to include *AHct
		for(int i=0;i<qs.size();i++){
			double availableQ=this.substance(i).getQ();
			double requiredC=example.substance(i).getC();
			if(requiredC!=0){
				maxVolume=Math.min(maxVolume,availableQ/requiredC);
			}
		}
		Container extract=this.withdrawAllPossible(example,maxVolume*fraction);
		return extract;
	}

        /**
         * Removes a fraction of this container's volume, but constituents are
         * removed according to the example container's concentrations (where
         * possible).
         * This version overwrites the values in 'into'
         */
        public void withdrawFracII_overwrite(Container example, double fraction, Container into){
          double maxVolume=vol; //used to include *AHct
          for(int i=0;i<qs.size();i++){
                  double availableQ=this.substance(i).getQ();
                  double requiredC=example.substance(i).getC();
                  if(requiredC!=0){
                          maxVolume=Math.min(maxVolume,availableQ/requiredC);
                  }
          }
          withdrawAllPossible_overwrite(example,maxVolume*fraction, into);
        }


	/**
	 * Returns a container containing the solids of this container.
	 *
	 * Unlike water, solids do not contain H+, so the pH (as corrected for
	 * haematocrit) is unchanged.
	 */
	public  Container filterSolids(){
		Container to=new Container();
			double solidvolume=solids.getQ();
			to.increaseVolume(solidvolume);
			increaseVolume(-solidvolume);
//			to.volume+=solidvolume;
//			volume-=solidvolume;
			to.solids.set(1);
			solids.set(0);
		//solids.moveTo(to.solids);
		return to;
	}

        /** Transfer the solids from this container into another given container c */
        public void filterSolidsInto(Container c){
          double solidVol=solids.getQ();
          double oldsol=c.solids.getQ();
          c.increaseVolume(solidVol);
          c.solids.setQ(oldsol+solidVol);
          increaseVolume(-solidVol);
          solids.set(0);
        }

	/** Example of the protein to remove during ultrafiltration.
	 *  This fluid must not be altered.
   * This is initialised in ultraFiltrate() */
	private static Container PROTEIN_1M=null;

	/**
	 * Returns renal ultrafiltrate from this container.
	 * Calls filterSolids() to remove solids, and then removes all prot.
	 * @param volume The volume of fluid from this container that is to be
	 * ultrafiltered.
	 * @return A container with the ultrafiltrate.
	 * Note: the returned container will have a lower volume than specified if
	 * the container contains solids.
	 */
	public  Container ultraFiltrate(double volume){
          if(PROTEIN_1M==null)PROTEIN_1M=Fluids.get("Large",1);
		//the parameter is the volume before filtration
		Container to=withdrawVol(volume); //withdraw some
//		synchronized(to){ //this is threadsafe since 'to is locally created
			add(to.filterSolids()); //put solids back
			//to.prot.moveTo(prot);			//put protein back: extract 1M protein
			add(to.withdrawFracII(PROTEIN_1M,1.0));
//		}
		return to;
	}
        /**
         * Remove some fluid, without removing solids or protein.
         */
        public void ultraFiltrate_overwrite(double volume, Container into){
//          double oldSolidQ=this.solids.getQ();
          withdrawVol_overwrite(volume, into);
          //add(into.filterSolids());
          into.filterSolidsInto(this);
/*
          double solidvolume=into.solids.getQ();
          into.increaseVolume(-solidvolume);
          into.solids.set(0);
          double oldVol=this.volume.get();
          increaseVolume(solidvolume);
          solids.set(oldSolidQ/this.volume.get());
*/
          into.prot.moveTo(prot);
        }

	//OSMOSIS
	/**
	 * This method dilutes all the components of the container and increases the
			 * volume by the specified amount. This method is not recommended except in the
	 * context of adding containers together, since it does not preserve pH
	 * (the hydrogen ion concentration is also adjusted).
	 * @todo Was protected. Should be a protected method ?
	 */
	public  void increaseVolume(double volume){
		volume=Math.max(volume,-this.vol);
		double cfrac;
		if(volume+this.vol<=0){
			cfrac=0; //if removing all fluid, set fraction to zero
		} else{
			cfrac=this.vol/(this.vol+volume);
		}
		for(int i=0;i<qs.size();i++){
			substance(i).multiplyC(cfrac);
		}
		this.vol+=volume;
	}

	/**
	 * This adds a quantity of water to the container, or removes some.
	 * It is similar to the protected method increaseVolume(), except that in
	 * addition to diluting the fluid, the appropriate quantity of H+ is added.
	 * Calls add(Container, volume) for negatve values, and add(Container) for
	 * positive.
	 * The water definition is taken from @link phic.Fluids Fluids.
	 */
	public  void addWater(double volume){
		volume=Math.max(volume,-this.vol); //can't remove too much...
		if(volume>0){
                  WATER.vol=volume;
                  add(WATER);
		} else if(volume<0){
                  WATER.vol=1;
                  add(WATER,volume);
		}
	}
        /**
         * This is an example of pure water. Should never be modified.
         * I need a read-only container!
         */
        protected static Container WATER = Fluids.get("Water", 1.0);

        /**
         * Transfer water between this container and 'to', so as to bring this container
         *	towards the osmolarity specified. 'frac' specifies how much water to transfer
         *	as a fraction of the amount required to attain equilibrium.
         * This method locks 2 containers at once.
         *
         * @param to The container to receive or donate the osmosed water
         * @param osmolarity The target osmolarity of this container
	 * @param frac A value of 1 indicates complete equilibrium
	 */
	public  void osmose(Container to,double osmolarity,double frac){
		// add/remove water until in osmotic equilibrium
		//Object lock1=this,lock2=to;
		//if(hashCode()>to.hashCode()){
		//	lock1=to;
		//}
		//lock2=this;
		double transf=(osmolarity-getOsmolarity())*vol/osmolarity;
		transf*=frac;
		transf=Math.max(transf,-to.vol); //if I'm very concentrated, can't take up too much fluid!
		//synchronized(lock1){
		//	synchronized(lock2){
				//to.addWater(transf);
        //addWater(-transf);
				//H+ left behind, cannot cross membranes
				to.increaseVolume(transf);
        increaseVolume(-transf); //dilute/concentrate as req'd
		//	}
		//}
	}

	/**
	 * Calculates osmolarity of the container.
	 * Approximation assuming [Cl] + [HCO3] = [Na] + [K],
	 * takes into account protein., urea, glucose and H+ (1 Osm/M).
	 */
	protected double getOsmolarity(){
		return H.getC()+glucose.getC()+urea.getC()+prot.getC()+2*Na.getC()+2*K.getC();
	}

	/**	Returns the osmolarity of this container. Shortcut to the protected
	 *  method getOsmolarity(). */
	public VDouble Osm=new VDoubleReadOnly(){
		{
			unit=UnitConstants.OSMOLES;
                }

		public double get(){
			return getOsmolarity();
		}
	};

	/**
	 * Returns the pH of this container. This is calculated excluding the
	 * solid volume.
	 * @todo this method could be final
	 */
	protected synchronized double calculatepH(){
		double fconc=H.getC()/(1-solids.getC());
		if(fconc<=0){
			return 0;
		}
		return-Math.log(fconc)/log10;
	}

	/** Convenience constant for log(10) */
	protected static final double log10=Math.log(10);

	/**
	 * Variable representing the pH of a container. It excludes the solid
	 * volume of the container.
	 * Currently get() chains to Container.pH(), and set alters Container.H
	 * directly.
	 */
	public VDouble pH=new VDouble(){
		public double get(){
			return calculatepH();
		}

		public void set(double value){
			H.setC((1-solids.getC())*Math.pow(10,-value));
		}
	};

        public VDoubleReadOnly Cl= new VDoubleReadOnly(){
          public double get(){
            return calculateChloride();
          }
        };
        protected double calculateChloride(){
          return Na.getC()+K.getC()-bicarb.getC();
        }

	// Fick's law
	/**
	 * Diffuse a quantity of substance according to Fick's law. Specify the
	 * substance quantities in two containers as q1 and q2. The respective concentrations
	 * should be given in conc1 and conc2, and mu is the coefficient of
	 * diffusion.
	 * @todo Will be made static? ...
	 */
	public synchronized void diffuse(double mu,double conc1,double conc2,
			Quantity q1,Quantity q2){
		double R=(q1.getQ()-q2.getQ())*mu/(conc1+conc2);
		q1.moveTo(q2,conc2*R);
	}

	/**
	 * Create a new container with composition identical to this container,
	 * but with the specified volume. Uses clone() to create a copy of the
	 * container (the concentrations should thus be preserved), and then
	 * resets the volume to the given value.
	 *
	 * Note: This method creates new substances!! e.g. If this is a
	 * DrugContainer, new drug will be obtained without using the Pharmacy.
	 * @todo this does not actually clone quantities!
	 * @deprecated This method does not function correctly since the
	 * clone is shallow - i.e. quantities in the new container are linked to
	 * those in the old container! Therefore this method invalidates the old container.
	 */
	public Container createMore(double volume){
          try{
            Container c=(Container)this.clone();
            c.vol=volume;
            c.qs = new Vector();
            for(int i=0;i<qs.size();i++){
              Quantity q=(Quantity)substance(i).clone();
              q.parent=c;
              c.qs.add( q );
            }
            return c;
          } catch(CloneNotSupportedException e){
            e.printStackTrace();
            return null;
          }
        }

        /**
	 * Clear contents of container. Volume and concentrations are set to zero.
	 */
	public synchronized void empty(){
		for(int i=0;i<qs.size();i++){
			substance(i).setC(0);
		}
		vol=0;
		ABC=0;
		H2CO3.set(0);
	}

	// New acid base routine
	// should be used instead of the old implementation in Blood.
	/**
	 * Dissociation constant for H2CO3 &lt=&gt HCO3- + H+
	 */
	protected static final double BICARB_DISSOC_K=795E-9;

	/**
	 * Concentration of unionized Carbonic acid H2CO3.
	 */
	public Quantity H2CO3=new Quantity(this);

	/**
	 * Acid-base correction (concentration increase of bicarb).
	 * When ABC is positive, H+ and bicarb are being produced from H2CO3.
	 * The value is the concentration increase of HCO3- per minute.
	 *
	 * @todo 22/1/03 -> protected since it's not obviously a user variable
	 */
	public double ABC;

	/**
	 * New method to perform default acid-base buffering.
	 * Moved here from Blood.
	 *
			 * Calculate amount of transfer between venous CO2 and plasma ions (hydrogen and
	 * bicarbonate) due to small change in one of these quantites.
	 *    <BR>	    K.[H2CO3] - [H].[HCO3]
	 *    <BR>	    ----------------------
	 *    <BR>	       [H] + [HCO3] - K
	 *
	 * @todo Implement carbonic anhydrase inhibitors
	 */
	public synchronized void doBicarbonateBuffering(){
		//double carbonicAcid = 0.0301 * venous.PCO2.get();
		//in old 'acid-base' called from 'lung-prep'
		//check c rhsc HCO3- or PCO2*0.0301 ?
		double cH=H.getC(),cBic=bicarb.getC(),cH2CO3=H2CO3.get();
		ABC=(BICARB_DISSOC_K*cH2CO3-cH*cBic)/(cBic+cH+BICARB_DISSOC_K);
		double quantity=ABC /* * elapsedSeconds/60 */;
		H.addC(quantity);
		bicarb.addC(quantity);
		// add CO2 from bicarbonate -- is this 22.4 l/mol at 0K? check coefficient c rhsc
		H2CO3.addC(-quantity*22.4);
	}

	// DEBUGGING
	/**
	 * Dump a report of the container's contents to the specified PrintStream.
	 * @todo A debug method
	 */
	public void report(PrintStream ps){ //debug
		ps.print("volume\t");
		for(int i=0;i<quantityname.length;i++){
			ps.print(quantityname[i]+"\t");
		}
		ps.print('\n');
		ps.print(vol+'\t');
		for(int i=0;i<qs.size();i++){
			ps.print(substance(i).getC()+"\t");
		}
		ps.print('\n');
	}
        /** Return the content of this container in the format
         * {glucose=4 mM, Na=144 mM, ...}
         */

        public String toString(){
          StringBuffer s = new StringBuffer("{");
          for(int i=0;i<quantityname.length;i++){
            if(i>0)s.append(", ");
            s.append(quantityname[i]);
            s.append("=" + substance(i).formatValue(true,false));
          }
          s.append("}");
          return s.toString();
        }

	/**
	 * True if any value in this container is Not-a-Number
	 * @todo A debug method
	 */
	public boolean hasError(){
		if(Double.isNaN(vol)){
			return true;
		}
		for(int i=0;i<qs.size();i++){
			if(Double.isNaN(((Quantity)qs.get(i)).getC())){
				return true;
			}
		}
		return false; //may be false sense of security!
	}

	/**
	 * The names of the quantities in the qs vector.
	 * Used by report method.
	 */
	public static String[] quantityname=new String[]{"bicarb","H","glucose",
			"urea","prot","K","Na","solids", "creat"
	};
  protected int compatibleQSize(Container from) {
    int n1 = qs.size(), n2 = from.qs.size();
    int n = Math.min(n1, n2);
    return n;
  }

  /**
	 * A class to represent the non-solid part of this container.
	 * This helps with blood plasma.
	 * @todo Not yet implemented.
	 */
	public class NonSolid{
		Container outerC;

		public NonSolid(){
			outerC=Container.this;
		}

		public Quantity bicarb=new LQuantity(Container.this);

		/** Setting the volume of the fluid component only */
		VDouble volume=new VDouble(){
			public double get(){
				return outerC.volume.get()-outerC.solids.getQ();
			}

			public void set(double volume){
				outerC.volume.set(volume+outerC.solids.getQ());
			}
		};
		/** Class to handle quantities within this container*/

		private class LQuantity extends Quantity{
			public LQuantity(Container parent){
				super(parent);
			}

			public void set(double value){
			}

			public double get(){
				return 0;
			}
		}
	}
}
