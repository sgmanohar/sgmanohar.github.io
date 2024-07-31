package medicine;

import java.util.*;
import java.awt.event.*;
/**
 * A pseudo-intelligent class whose purpose is to
 * come up with a reasonable differential diagnosis, given a set of signs
 * and symptoms.
 *
 * Highly preliminary version!
 * interface:
 * Diagnoser( observationList, actionListener )
 * start()
 * getDifferential()
 */

public class Diagnoser implements Runnable , ActionListener{

	String[] causativeClasses = { "Trauma", "Pathology", "Disease",
		"Drug", "Surgery" };
	String[] effectiveClasses = { "Sign", "Symptom" };


	Entity[] causativeEntities = new Entity[causativeClasses.length];

	Vector obs;
	ActionListener al;
	/**
	 * @param observations The list of signs and symptoms entities.
	 * @param actionListener This is called on completion of the search.
	 */
	public Diagnoser(Vector observations, ActionListener al) {
		obs=observations;
		this.al=al;

		Entity any = (Entity)obs.get(0);
		for(int i=0;i<causativeClasses.length;i++)
			causativeEntities[i] = Entities.getSpecificNamedEntity( causativeClasses[i], any);
	}
	public Thread thread = new Thread(this);
	public void start(){
		thread.start();
	}
	public void waitForCompletion() throws InterruptedException { thread.join(); }

	Vector poss = new Vector();
	class Possibility implements Comparable{
		Entity entity; double score;
		public int compareTo(Object o){
			Possibility p = (Possibility)o;
			double c = score - p.score;
			return c<0?-1:(c>0?1:0);
		}
	}

	/**
	 * Required size of the differential diagnosis list. Default is top 10.
	 */
	public int differentialSize = 10;

	/**
	 * The maximum distance, in nodes away from the starting node, that a
	 * node is allowed to be, in order to be considered as a possible cause
	 * for the starting node.
	 * The cound includes parent/child relations.
	 */
	public int maxRelevantDistance = 10;

	/**
	 * The maximum number of times each node may be crossed, in the process
	 * of calculating a relevance score for it.
	 */
	public int maxTraverseCount = 10;

	public double proximityPower = 0.25;


	public boolean allowGenerics = false;
	public boolean allowEffectless = false;
	public boolean allowNonCausative = false;

	/**
	 * Disallow items that are children of other items already in the
	 * differential diagnosis. If a child is higher on the differential,
	 * the parent replaces it.
	 */
	public boolean allowChildrenOfDiagnoses = true;

	public int branchMask = Entity.CAUSE | Entity.PARENT | Entity.CHILD;


	/**
	 * Return the array of entities in order of plausibility.
	 * Call this method after the action listener has been informed that
	 * the search has completed.
	 */
	public Vector getDifferential(){
		Possibility[] s = new Possibility[poss.size()];
		s=(Possibility[])poss.toArray(s);
		Arrays.sort(s);
		Vector best = new Vector();
		// Count back from most relevant to least relevant result
		int j = s.length - 1;
		// How many results added
		int	i=0;
		while(i < differentialSize && j > 0){
			Possibility p = s[ j-- ];
			boolean isCausative = false;
			for(int k=0;k<causativeEntities.length;k++)
				if(Entities.isChildOf(p.entity, causativeEntities[k]))
					isCausative = true;
			if(   (allowNonCausative || isCausative)
			   && (allowGenerics || p.entity.children.size() == 0)
			   && (allowEffectless || p.entity.effects.size() >0)
				){
				if(!allowChildrenOfDiagnoses){
					boolean itsOK=true;
					for(int k=0;k<best.size();k++){
						Entity comp = (Entity)best.get(k);
						if(Entities.isChildOf(p.entity, comp)) itsOK=false;
						if(Entities.isChildOf(comp,p.entity)){
							best.set(k, p.entity);
							itsOK=false;
						}
					}
					if( !itsOK ) continue; // skip and go to next possibility
				}
				best.add(p.entity);
				i++;
			}
		}
		return best;
	}

	public void run(){
		ended = 0;
		for(int i=0;i<obs.size();i++){
			Entity observation = (Entity)obs.get(i);
			RepeatingEntityCrawler ec = new RepeatingEntityCrawler(observation, this);
			ec.setBranchMask( branchMask );
			ec.setMaxTraverseCount( maxTraverseCount );
			ec.MAX_DISTANCE = maxRelevantDistance;
			ec.start();
		}
		while(ended < obs.size()){
			try{Thread.sleep(100);}catch(Exception e){e.printStackTrace();}
		}
		al.actionPerformed(new ActionEvent(this, 0, "Finished"));
	}

	int ended = 0;
	public void actionPerformed(ActionEvent e){
		int distance = e.getModifiers();
		Object o = e.getSource();
		if(!(o instanceof Entity)) {ended++;return;}
		Entity f = (Entity)o;
		if(distance>0){
			Possibility p = findPossibility(f);
			p.score += Math.pow(proximityPower, distance);
		}
	}

	/**
	 * Find the possibility corresponding to an entity, or create a new one if
	 * none already exists.
	 */
	Possibility findPossibility(Entity e){
		for(int i=0;i<poss.size();i++){
			Possibility p = (Possibility)poss.get(i);
			if(p.entity == e) return p;
		}
		Possibility p = new Possibility();
		poss.add(p);
		p.entity = e;
		p.score = 0;
		return p;
	}
}