package phic.modifiable;
import phic.gui.VisibleVariable;

/**
 * Pair of minimum and maximum values.
 */
public class Range{
	public Range(){}

	public Range(double minimum,double maximum){
          set(minimum, maximum);
        }

	public double minimum=0;

	public double maximum=1;

        public void set(double minimum, double maximum){
          this.maximum=Math.max(maximum, minimum);
          this.minimum=Math.min(minimum,maximum);
        }
	/** The span of the range, i.e. maximum - minimum */
	public final double getSpan(){
		return maximum-minimum;
	}

	/** Return a value from this range scaled to a new range */
	public double convertValueTo(Range newRange,double value){
		return(value-minimum)*newRange.getSpan()/getSpan()+newRange.minimum;
	}

        /** expand the range to include the given value */
        public void include(double a){
          maximum = Math.max(a,maximum);
          minimum = Math.min(a,minimum);
        }
        /** expand the range to include both the given values */
        public void include(double a, double b){
          maximum = Math.max(a,Math.max(b, maximum));
          minimum = Math.min(b,Math.min(a, minimum));
        }
        /** expand the range to include the given range */
        public void include(Range r){
          if(r==null) return;
          maximum = Math.max(r.maximum,Math.max(r.minimum, maximum));
          minimum = Math.min(r.minimum,Math.min(r.maximum, minimum));
        }

	/**
	 * The values between which scaling takes place.
	 * (0 is implicitly included.)
	 */
	protected static final double[] decades=new double[]{0.1,0.15,0.2,0.25,0.3,
			0.35,0.4,0.45,0.5,0.6,0.7,0.8,0.9,1.0},triads=new double[]{0.2,0.5,1.0};

	/** The possible values of zoomRange */
	public static final int ZOOM_IN=0,ZOOM_NORMAL=1,ZOOM_OUT=2,
			ZOOM_VARIABLE_RANGE=3;

	/** A list of the possible values of zoomRange */
	public static final int[] ZOOM_RANGES={ZOOM_IN,ZOOM_NORMAL,ZOOM_OUT};

	/**
	 * Create a range based on a single value, zoomed to the specified level
	 * around the value.
	 */
	public static Range findRange(double value,int zoomRange){
		Range newRange=new Range();
		// negativity dealt with afterwards
		boolean negative=value<0;
		value=Math.abs(value);
		//cannot scale a zero value!
		if(value==0){
			value=0.001;
			// the power of ten of the value
			// I.E. 10 ^ ( Int(log (value)) + 1 )
		}
		double decade=Math.pow(10,1+(int)Math.floor(Math.log(value)/Math.log(10)));
		int i;
		switch(zoomRange){
		case ZOOM_OUT:
			newRange.minimum=0;
			i=findFirstIndexAbove(value/decade,triads);
			newRange.maximum=decade*triads[i];
			break;
		case ZOOM_IN:
			i=findFirstIndexAbove(value/decade,decades);
			if(i==0){
				newRange.minimum=decades[i]*decade;
				newRange.maximum=decades[i+1]*decade;
			} else{
				newRange.minimum=decades[i-1]*decade;
				newRange.maximum=decades[i]*decade;
			}
			break;
		case ZOOM_NORMAL: case ZOOM_VARIABLE_RANGE:
			i=findFirstIndexAbove(value/decade,triads);
			if(i==0){
				newRange.minimum=triads[i]*decade;
				newRange.maximum=triads[i+1]*decade;
			} else{
				newRange.minimum=triads[i-1]*decade;
				newRange.maximum=triads[i]*decade;
			}
			break;

		default:
			throw new IllegalArgumentException("Zoom range must"
		+"be in the range 0-2");
		}
		if(newRange.maximum==newRange.minimum){
      newRange.maximum+=1.0;
			//throw new RuntimeException("Zoom range is not working");
		} //@todo Do something to correct it here?
		return newRange;
	}

	/**
	 * Create a range based on the variable, zoomed to the specified level
	 * around the value. If the zoomRange is ZOOM_VARIABLE_RANGE, the
	 * variable's range is used. Otherwise, the variable's current value is
	 * used.
	 */
	public static Range findRange(VisibleVariable var,int zoomRange){
    Range newRange=new Range();
		switch(zoomRange){
		case ZOOM_VARIABLE_RANGE:
			newRange.minimum=var.minimum;
			newRange.maximum=var.maximum;
			return newRange;
    case ZOOM_OUT:
       Range r1,r2,r3;
      try{
        r1=findRange(var.node.doubleGetVal(),ZOOM_IN);
        r2=findRange(var.minimum,ZOOM_IN);
        r3=findRange(var.maximum,ZOOM_IN);
      }catch (RuntimeException e) { return findRange(var, ZOOM_VARIABLE_RANGE); }
      newRange.maximum=Math.max(Math.max(r1.maximum,r2.maximum),r3.maximum);
      newRange.minimum=Math.min(Math.min(r1.minimum,r2.minimum),r3.minimum);
      return newRange;
    case ZOOM_IN:
      return findRange(var.node.doubleGetVal(),ZOOM_IN);
		default:
		return findRange(var.node.doubleGetVal(),zoomRange);
		}
	}

        /**
         * Return a range which is centred around the variable's initial value.
         */
        public static Range findRangeSymmetrical(VisibleVariable var, int zoom){
          Range r=findRange(var,zoom);
          double i=var.initial;
          double rm = Math.max(i-r.minimum, r.maximum-i);
          r.maximum = i+rm;
          r.minimum=i-rm;
          return r;
        }

	/** Find the first index in the list that is above value. */
	protected static final int findFirstIndexAbove(double value,double[] list){
		for(int i=0;i<list.length;i++){
			if(list[i]>value){
				return i;
			}
		}
		return list.length-1;
	}
        /** Return a string representation of the range */
	public String toString(){
		return "[ "+minimum+", "+maximum+" ]";
	}
        /** Returns whether two ranges are identical, i.e. the maxima and
         * minima are identical  */
	public boolean equals(Range r){
		return minimum==r.minimum&&maximum==r.maximum;
	}
        /** Returns true if the range contains the value */
        public boolean contains(double value){
          return value>=minimum && value<=maximum;
        }
}
