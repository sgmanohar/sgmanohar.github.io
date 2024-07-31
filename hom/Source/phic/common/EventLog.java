package phic.common;
import java.io.Serializable;
import java.util.Vector;
import phic.Body;

/**
 * Keep track of fluid events etc.; stores as objects of type Event,
 * in a vector. Currently only fluid events are supported.
 */
public class EventLog implements Serializable{
	/**
	 * Event type objects: these are tags that are used to reference and
	 * classify events: each event is associated with one.
	 */
	public static String FLUID_EVENT="Fluid";

	Body body;

	public EventLog(Body body){
		this.body=body;
	}

	/** The list of all events */
	public Vector events=new Vector();

	/**
	 * The maximum amount of time for which the log retains events.
	 * in milliseconds. Default 1 week
	 */
	long logDuration=1000*60*60*24*7;

	/**
	 * To document an event, call this method.
	 * The eventType must be one of the static tag objects above.
	 * The name is the actual event itself, and its parameter is specific to the
	 * event type. For fluid events, it should be a Double.
	 */
	public void document(Object eventType,String name,Object parameter){
		Event e=new Event();
		e.type=eventType;
		e.parameter=parameter;
		e.time=body.getClock().getTime();
		e.name=name;
		events.add(e);
		//expire the last element in the log if necessary
		Event e0=(Event)events.get(0);
		if(e0.time<e.time-logDuration){
			events.remove(0);
		}
	}

	/** Format of event data */
	public class Event implements Serializable{
		public Object type,parameter;

		public long time;

		public String name;

		public String toString(){
			return body.getClock().getTimeString(Clock.DATETIME)+": "+body.getName()
					+type.toString()+parameter.toString();
		}
	}


	/**
	 * Filter returning events only of the given type.
	 * @param type the event-type object tag from the static list above.
	 * This method searches the events stored in this log, and returns only those
	 * that have the requested event type.
	 */
	public Vector getEventsOfType(Object type){
		Vector v=new Vector();
		for(int i=0;i<events.size();i++){
			Event e=(Event)events.get(i);
			if(e.type==type){
				v.add(e);
			}
		}
		return v;
	}
}